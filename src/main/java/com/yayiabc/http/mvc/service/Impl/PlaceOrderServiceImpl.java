package com.yayiabc.http.mvc.service.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yayiabc.common.cahce.CacheUtils;
import com.yayiabc.common.enums.ErrorCodeEnum;
import com.yayiabc.common.exceptionHandler.OrderException;
import com.yayiabc.common.utils.BeanUtil;
import com.yayiabc.common.utils.DataWrapper;
import com.yayiabc.common.utils.OrderIdUtils;
import com.yayiabc.common.utils.PayAfterOrderUtil;
import com.yayiabc.common.utils.RedisClient;
import com.yayiabc.http.mvc.dao.PlaceOrderDao;
import com.yayiabc.http.mvc.dao.UserWithdrawalsDao;
import com.yayiabc.http.mvc.dao.UtilsDao;
import com.yayiabc.http.mvc.pojo.jpa.FreeShipping;
import com.yayiabc.http.mvc.pojo.jpa.Invoice;
import com.yayiabc.http.mvc.pojo.jpa.OrderItem;
import com.yayiabc.http.mvc.pojo.jpa.Ordera;
import com.yayiabc.http.mvc.pojo.jpa.PostFee;
import com.yayiabc.http.mvc.pojo.jpa.Receiver;
import com.yayiabc.http.mvc.pojo.jpa.User;
import com.yayiabc.http.mvc.pojo.model.ExpireOrder;
import com.yayiabc.http.mvc.pojo.model.FinalList;
import com.yayiabc.http.mvc.service.PlaceOrderService;

import net.sf.json.JSONArray;
import redis.clients.jedis.Jedis;

@Service
public class PlaceOrderServiceImpl implements PlaceOrderService{
	@Autowired
	private PlaceOrderDao placeOrderDao;
	@Autowired
	private UtilsDao utilsDao;

	public Integer getFreight(Receiver receiver,Double sumPrice,Integer itemSum){
		String Province =receiver.getProvince();
		//查询包邮表数据
		List<FreeShipping> list=placeOrderDao.queryPostFree();
		if(!list.isEmpty())
		{
			for(int i=0;i<list.size();i++){

				String[] citys=list.get(i).getPostCity().split(",");
				int money=list.get(i).getFreeShippingMoney();
				if(Province==null){
					String city=receiver.getCity();
					for(int x=0;x<citys.length;x++){
						if(city.equals(citys[x])){
							if(sumPrice>money||sumPrice==money){
								if(list.get(i).getState()==1){
									return 0;
								}
							}
						}
					}
				}else{
					for(int y=0;y<citys.length;y++){
						if(Province.equals(citys[y])){
							if(sumPrice>money||sumPrice==money){
								if(list.get(i).getState()==1){
									return 0;
								}
							}
						}
					}
				}
			}
		}
		//查询自定义运费表数据
		List<PostFee> lists=placeOrderDao.query();
		if(!lists.isEmpty()){
			for(int i=0;i<lists.size();i++){
				String[] citys=lists.get(i).getPostCity().split(",");
				if(Province==null){
					String cityString=receiver.getCity();
					for(int x=0;x<citys.length;x++){
						if(cityString.equals(citys[x])){
							return (lists.get(i).getFirstMoney()+(itemSum-1)*lists.get(i).getAddMoney());
						}
					}
				}else{
					for(int x=0;x<citys.length;x++){
						if(Province.equals(citys[x])){
							return  (lists.get(i).getFirstMoney()+(itemSum-1)*lists.get(i).getAddMoney());
						}
					}
				}
			}
		}
		return 0;
	}
	@Autowired
	private UserWithdrawalsDao userWithdrawalsServiceDao;
	
	//钱币抵扣
	@Override
	public DataWrapper<Integer> ded(String token, int num,String sumItemsPrice/*,Integer  receiverId*/) {
		DataWrapper<Integer> dataWrapper=new DataWrapper<Integer>();
		String userId=utilsDao.getUserID(token);
		User user=userWithdrawalsServiceDao.showUserQbNum(userId);
		String useMaxQbNum=null;
		Jedis jedis=RedisClient.getInstance().getJedis();
		if(Double.parseDouble(sumItemsPrice)>=120){
			useMaxQbNum=user.getaQb()+user.getcQb()+user.getQbBalance()+user.getQbNotwtih()+"";
			dataWrapper.setMsg("2");
		}else{
			jedis.select(11);
			if(jedis.exists(userId)){
				System.out.println("不是首单");
				useMaxQbNum=user.getaQb()+user.getcQb()+user.getQbBalance()+user.getQbNotwtih()+"";
				if(num>Double.parseDouble(sumItemsPrice)){
					dataWrapper.setMsg("余额不足");
					return dataWrapper;
				}
			}else{
				System.out.println("是首单");
				useMaxQbNum=user.getaQb()+user.getcQb()+user.getQbBalance()+"";
				if(num>Double.parseDouble(sumItemsPrice)){
					dataWrapper.setMsg("余额不足");
					return dataWrapper;
				}
			}
		}
		dataWrapper.setData(Integer.parseInt(useMaxQbNum));
		return dataWrapper;
	}
	//更改收货地址
	@Override
    public DataWrapper<HashMap<String, Object>>  upateAddress(Integer receiverId, Double sumPrice, Integer itemSum){
		DataWrapper<HashMap<String, Object>> dataWrapper=new DataWrapper<HashMap<String, Object>>();

		Receiver receiver=placeOrderDao.queryReceiver(receiverId);

		double yunfei=getFreight(receiver, sumPrice, itemSum);
		HashMap<String, Object> hashMap=new HashMap<String,Object>();
		hashMap.put("postFee", yunfei);
		hashMap.put("Receiver", receiver);
		dataWrapper.setData(hashMap);
		return  dataWrapper;
	}

	//清空购物车
	@Override
	public DataWrapper<Void> emptyCart(String token) {
		// TODO Auto-generated method stub
		int state=placeOrderDao.emptyCart(utilsDao.getUserID(token));
		return util(state);
	}
	private DataWrapper<Void> util(int state){
		DataWrapper<Void> dataWrapper=new DataWrapper<Void>();
		if(state>0){
			dataWrapper.setMsg("操作成功");
		}else{
			dataWrapper.setMsg("操作失败");
		}
		return dataWrapper;
	}

	//1234
	@Transactional
	@Override
	public DataWrapper<HashMap<String, Object>> generaOrder(String token, List<OrderItem> orderItemList, Ordera order,
			Invoice  invoice
			) {
		DataWrapper<HashMap<String, Object>> dataWrapper=new DataWrapper<HashMap<String,Object>>();

		if(order.getQbDed()==null){
			order.setQbDed(0);
		}
		
		//记录工具设备类的商品 个数
		int TooldevicesSumCount=0;
		// TODO Auto-generated method stub
		//  get userId

		HashMap<String, Object> hashMap=new HashMap<String, Object>();
		String userId=null;
		try {
			userId=utilsDao.getUserID(token);
			
			//obtain orderId 
			String orderId=OrderIdUtils.createOrderId(userId);
			//将订单信息保存在订单里 你如是否需要发表  留言等。。2[=。 
			if(order != null){
				if(order.getQbDed()==null){
					order.setQbDed(0);
				}if(order.getInvoiceHand()==null){
					order.setInvoiceHand("该用户不需要发票");
				}if(order.getIsRegister()==null){
					order.setIsRegister(0);
				}if(order.getBuyerMessage()==null){
					order.setBuyerMessage("该单暂时未被评价呢");
					order.setBuyerRate(0);
				}
			} else {
				//set datawrapper
				throw new OrderException(ErrorCodeEnum.QBDED_Error);
			}

			//本单赠送钱币
			double giveQbNum=0;
			//道邦总价
			double daoBnagSumPrice=0;

			//除道邦之外的耗材类总价格 
			double SuppliesSumPrice=0;
			//除道邦之外的工具设备类总价格
			double TooldevicesSumPrice=0;	

			//全部的耗材类总价格
			double AllSuppliesSumPrice=0;
			//全部的工具设备类总价格
			double AllTooldevicesSumPrice=0;	


			Double sumPrice=0.0;
			int itemSum=orderItemList.size();//商品总数量
			List<FinalList> finalList=placeOrderDao.queryFinalList(orderItemList);

			//填充orderItemList
			orderItemList=goodOrderItemList(orderItemList,orderId,finalList);

			//校验 商品是否在售卖状态与校检库存是否正确 与更改库存
			boolean flag=changeStockNum(orderItemList,finalList);
			if(!flag){
				dataWrapper.setMsg("库存不足");
				return dataWrapper;
			}
			RedisClient rc=RedisClient.getInstance();
			Jedis jedis=rc.getJedis();


			//计算改单商品金额
			HashMap<String, Object> priceMap=partItemPrices(orderItemList,AllSuppliesSumPrice,AllTooldevicesSumPrice);
			sumPrice=(Double) priceMap.get("sumPrice");
			AllSuppliesSumPrice=(Double) priceMap.get("AllSuppliesSumPrice");
			AllTooldevicesSumPrice=(Double) priceMap.get("AllTooldevicesSumPrice");
           
			
		/*	DataWrapper<Integer> data=ded(token,order.getQbDed(),sumPrice+"");
			if(data!=null){
				int qbBalance=data.getData();
				if(qbBalance<order.getQbDed()){
					throw new OrderException(ErrorCodeEnum.QBDED_Error);
				}
			}*/
			 
			//检查是否为首单  true 是  ，false 不是
             if(sumPrice<120){
            	 Integer qbDedNum=order.getQbDed();
            	 boolean flg=inspectIsFirstOrder(userId,jedis,orderId,sumPrice);
            	if(flg){
            		 if(qbDedNum!=0){
 						//检查  用户此时余额 除去注册赠送的60钱币   是否够支付该订单
                     boolean f=cheackUserQb(userId,qbDedNum);
                     if(!f){
                  	   dataWrapper.setMsg("首单订单金额必须大于120元（不包括运费），才可使用注册赠送的钱币");
                  	   return dataWrapper;
                     }
 					}
            	}else{
                 	//不是首单可以使注册赠送的钱币
    				 System.out.println(6666666);
    				 jedis.hset("isFirstOrder", orderId, "1");
                 }
             }
			
			/*Integer qbDedNum=order.getQbDed();
			boolean flg=inspectIsFirstOrder(userId,jedis,orderId,sumPrice);
			if(flg){
				System.out.println(sumPrice+"sumPricesumPricesumPricesumPrice");
				if(sumPrice<120){
					//首单 商品总价（不包括运费小于120） 不能 使用注册赠送的钱币
					if(qbDedNum!=0||qbDedNum!=null){
						//检查  用户此时余额 除去注册赠送的60钱币   是否够支付该订单
                       boolean f=cheackUserQb(userId,qbDedNum);
                       if(!f){
                    	   dataWrapper.setMsg("首单订单金额必须大于120元（不包括运费），才可使用注册赠送的钱币");
                    	   return dataWrapper;
                       }
					}
					//System.out.println("00000000000");
					//jedis.hset("isFirstOrder", orderId, "0");
				}
			 }else{
				 //不是首单可以使注册赠送的钱币
				 System.out.println(6666666);
				 jedis.hset("isFirstOrder", orderId, "1");
			 }*/
			//创建订单并保存订单数据
			placeOrderDao.createOrder(orderId,userId,order);


			//将商品 同步到 订单商品表里--------双休优化  批量 插入到order_item表中
			int a=placeOrderDao.batchSynchronization(orderItemList);
			//清空购物车 双休优化
			int q=placeOrderDao.cleanCartList(userId,orderItemList);
			if(a<=0){
				throw new OrderException(ErrorCodeEnum.ORDER_ERROR); 
			}
			//计算本单赠送钱币数
			giveQbNum=calculQbNum(daoBnagSumPrice,SuppliesSumPrice,TooldevicesSumCount,TooldevicesSumPrice,orderItemList);


			//该单计算运费
			Receiver receiver=placeOrderDao.queryReceiver(order.getReceiverId());
			
			Integer postFee=getFreight(receiver,sumPrice,itemSum);
			//是否需要发票
			if("1".equals(order.getInvoiceHand())){
				invoice.setOrderId(orderId);
				invoice.setUserId(userId);
				int  sign=placeOrderDao.saveInvoiced(invoice);
				if(sign<=0){
					throw new OrderException(ErrorCodeEnum.ORDER_ERROR); 
				}
			}
			//订单实际价格的计算
			Double actualPay=sumPrice+postFee-order.getQbDed();
			//放入订单表
			//placeOrderDao.insertActualPay(orderId,String.valueOf(actualPay));
			hashMap.put("OrderId",orderId);
			hashMap.put("sumPrice",String.valueOf(sumPrice));
			hashMap.put("giveQbNum", (int)giveQbNum);
			hashMap.put("itemSum", itemSum);
			hashMap.put("postFee", postFee);
			hashMap.put("actualPay", actualPay);
			/*hashMap.put("AllSuppliesSumPrice", AllSuppliesSumPrice);
			hashMap.put("AllTooldevicesSumPrice", AllTooldevicesSumPrice);*/
			hashMap.put("SuppliesSumPrice", SuppliesSumPrice);
			hashMap.put("TooldevicesSumPrice", TooldevicesSumPrice);
			hashMap.put("daoBnagSumPrice", daoBnagSumPrice);

			//本单赠送钱币数保存到数据库
			if(
					placeOrderDao.saveGiveQbNum(String.valueOf((int)giveQbNum),String.valueOf(postFee),
							String.valueOf(sumPrice)
							,orderId,String.valueOf(AllSuppliesSumPrice),String.valueOf(AllTooldevicesSumPrice)
							,String.valueOf(actualPay)
							)<=0
					)
			{
				throw new OrderException(ErrorCodeEnum.ORDER_ERROR); 
			}
			
			/*
			 * 放入redis
			 */
			List<ExpireOrder> expireOrderList=new ArrayList<ExpireOrder>();
			for (OrderItem oredrItem : orderItemList) {
				ExpireOrder expireOrder=new ExpireOrder();
				expireOrder.setOrderId(orderId);
				expireOrder.setItemId(oredrItem.getItemId());
				expireOrder.setItemSKU(oredrItem.getItemSKU());
				expireOrder.setItemNum(oredrItem.getNum());
				expireOrderList.add(expireOrder);
			}
			
			JSONArray json = JSONArray.fromObject(expireOrderList); 
			//System.out.println("json.toString() "+json.toString());
			
			jedis.select(1);
			jedis.set("expireOrder"+orderId, json.toString());  
			jedis.expire("expireOrder"+orderId,60*30);
			System.out.println("一分钟后关闭订单");
			//保存主要数据
            jedis.hset("expireOrder1", "expireOrder"+orderId, json.toString());
			jedis.close();
			
			/**
			 * 这里减少钱币
			 */
			order.setOrderId(orderId);
			order.setUserId(userId);
			System.out.println(order);
			PayAfterOrderUtil payAfterOrderUtil= BeanUtil.getBean("PayAfterOrderUtil");
			//判断  钱币抵扣是否等于0
			 if(order.getQbDed()!=0){
				 System.out.println("减呀");
				boolean boo= payAfterOrderUtil.createOrderDedQbMed(order);
				if(!boo){
					throw new RuntimeException();
				}
			 }
			//判断客服是否全额乾币支付
			if(actualPay==0){
				

				if(!payAfterOrderUtil.universal(orderId,"3")){
					throw new RuntimeException();
					//throw new OrderException(ErrorCodeEnum.ORDER_ERROR); 
				 }
			}
			dataWrapper.setData(hashMap);
			return dataWrapper;
		} catch (Exception e){
			throw new RuntimeException(e); 
		}
	}
	
	//检查  用户此时余额 除去注册赠送的60钱币   是否够支付该订单
	private boolean cheackUserQb(String userId, Integer qbDedNum) {
		// TODO Auto-generated method stub
		int userQbExceptReg=placeOrderDao.getUserQbExceptReg(userId);
		if(userQbExceptReg>=qbDedNum){
			return true;
		}
		return false;
	}
	//分别计算该单下的分类价格AllSuppliesSumPrice
	private HashMap<String, Object> partItemPrices(List<OrderItem> orderItemList, double AllSuppliesSumPrice, double AllTooldevicesSumPrice){
		HashMap<String, Object> hm=new HashMap<String,Object>();
		if(!orderItemList.isEmpty()) {
            for (int i = 0; i < orderItemList.size(); i++) {
                //这里计算除道邦之外的商品分类价格  耗材类  工具设备类
                if ("耗材类".equals(orderItemList.get(i).getItemType())) {
                    AllSuppliesSumPrice += orderItemList.get(i).getNum() * orderItemList.get(i).getPrice();
                } else if ("工具设备类".equals(orderItemList.get(i).getItemType())) {
                    AllTooldevicesSumPrice += orderItemList.get(i).getNum() * orderItemList.get(i).getPrice();
                }
            }
        }
		hm.put("AllSuppliesSumPrice", keepTwo(AllSuppliesSumPrice));
		hm.put("AllTooldevicesSumPrice", keepTwo(AllTooldevicesSumPrice));
		hm.put("sumPrice",keepTwo(AllSuppliesSumPrice+AllTooldevicesSumPrice));
		return hm;
	}
	//下单更改订单里的商品库存
	@Override
	public boolean changeStockNum(List<OrderItem> orderItemList,List<FinalList> finalList){

		if(orderItemList.size()!=finalList.size()){
			System.out.println("orderItemList.size()与 finalList.size()不同！");
			return false;
		}else{
			for(int i=0;i<orderItemList.size();i++){
				//判断当前商品是否在售卖状态
				if(finalList.get(i).getCanUse()==1){
					//判断库存
					if(finalList.get(i).getStockNum()<orderItemList.get(i).getNum()){
						System.out.println("库存不足");
						return false;
					}
				}else{
					return false;
				}
			}
			int state=placeOrderDao.updateInventNums(orderItemList);
			//乐观锁 解决一致性与并发性的矛盾
			if(state==0){
				System.out.println("乐观锁,false");
				return false;
			}
		}
		return true;
	}
	private double calculQbNum(double daoBnagSumPrice,double SuppliesSumPrice,double TooldevicesSumCount,double TooldevicesSumPrice, List<OrderItem> orderItemList){
		
		double giveQbNum=0.0;
		for(int i=0;i<orderItemList.size();i++){
			if("上海道邦".equals(orderItemList.get(i).getItemBrandName())){
				daoBnagSumPrice+=orderItemList.get(i).getNum()*orderItemList.get(i).getPrice();
			}else{
				//这里计算除道邦之外的商品分类价格  耗材类  工具设备类
				if("耗材类".equals(orderItemList.get(i).getItemType())){
					SuppliesSumPrice+=orderItemList.get(i).getNum()*orderItemList.get(i).getPrice();
				}else if("工具设备类".equals(orderItemList.get(i).getItemType())){
					TooldevicesSumCount+=orderItemList.get(i).getNum();
					TooldevicesSumPrice+=orderItemList.get(i).getNum()*orderItemList.get(i).getPrice();
				}
			}
		}
		//首先道邦品牌
		if(daoBnagSumPrice>0&&daoBnagSumPrice<300){
			giveQbNum=giveQbNum+daoBnagSumPrice*0.03;
		}else if(daoBnagSumPrice>=300&&daoBnagSumPrice<600){
			giveQbNum=giveQbNum+daoBnagSumPrice*0.05;
		}else if(daoBnagSumPrice>=600&&daoBnagSumPrice<1200){
			giveQbNum=giveQbNum+daoBnagSumPrice*0.08;
		}else if(daoBnagSumPrice>=1200&&daoBnagSumPrice<2500){
			giveQbNum=giveQbNum+daoBnagSumPrice*0.12;
		}else if(daoBnagSumPrice>=2500){
			giveQbNum=giveQbNum+daoBnagSumPrice*0.15;
		}
		//其他品牌 耗材类
		if(SuppliesSumPrice>0&&SuppliesSumPrice<500){
			giveQbNum=giveQbNum+SuppliesSumPrice*0.03;
		}else if(SuppliesSumPrice>=500&&SuppliesSumPrice<1000){
			giveQbNum=giveQbNum+SuppliesSumPrice*0.05;
		}else if(SuppliesSumPrice>=1000&&SuppliesSumPrice<3000){
			giveQbNum=giveQbNum+SuppliesSumPrice*0.08;
		}else if(SuppliesSumPrice>=3000){
			giveQbNum=giveQbNum+SuppliesSumPrice*0.12;
		}
		//其他品牌 工具设配类
		if(TooldevicesSumCount==1){
			giveQbNum=giveQbNum+TooldevicesSumPrice*0.05;
		}else if(TooldevicesSumCount>=2){
			giveQbNum=giveQbNum+TooldevicesSumPrice*0.10;
		}
		return giveQbNum;
		/*double giveQbNum=0;
		System.out.println(finalList);
		System.out.println(orderItemList);
	    for(int i=0;i<finalList.size();i++){
	    	giveQbNum+=finalList.get(i).getItemQb()*orderItemList.get(i).getNum();
	    }
	    System.out.println("赠送乾币数:"+giveQbNum);
		return giveQbNum;*/
	}
	//查询上次下订单时填写的发票信息
	@Override
	public DataWrapper<Invoice> queryLastInvoice(String token) {
		// TODO Auto-generated method stub
		String userId=utilsDao.getUserID(token);
		DataWrapper<Invoice> dataWrapper=new DataWrapper<Invoice>();
		Invoice in=placeOrderDao.queryLastInvoice(userId);
		if(in!=null){
			dataWrapper.setData(in);
		}
		return dataWrapper;
	}
	//填充orderItemList
	private List<OrderItem> goodOrderItemList(List<OrderItem> orderItemList,String orderId,List<FinalList> finalList){

		for(int i=0;i<orderItemList.size();i++){
			for(int x=0;x<finalList.size();x++){
				if(orderItemList.get(i).getItemSKU().equals(finalList.get(x).getItemSKU())){
					orderItemList.get(i).setItemBrandName(finalList.get(x).getItemBrandName());
					orderItemList.get(i).setItemName(finalList.get(x).getItemName());
					orderItemList.get(i).setPicPath(finalList.get(x).getPicPath());
					orderItemList.get(i).setItemPropertyNamea(finalList.get(x).getItemPropertyInfo());
					orderItemList.get(i).setItemPropertyNameb(finalList.get(x).getItemPropertyTwoValue());
					orderItemList.get(i).setItemPropertyNamec(finalList.get(x).getItemPropertyThreeValue());
					orderItemList.get(i).setPrice(finalList.get(x).getItemSkuPrice());
					orderItemList.get(i).setOrderId(orderId);
					orderItemList.get(i).setItemType(finalList.get(x).getItemType());
					orderItemList.get(i).setVersion(finalList.get(x).getVersion());
				}
			}
		}
		return orderItemList;
	}
	//double保留最后两位小数
	private Double keepTwo(Double ouble){
		return (double) Math.round(ouble);
	}
	/**
	 * 检查是否未首单
	 * @param userId
	 * @return
	 */
	private boolean inspectIsFirstOrder(String userId,Jedis jedis,String orderId,Double sumPrice){
		
	jedis.select(11);
	if(jedis.exists(userId)){
		//不是首单
		System.out.println("不是首单，提交订单进行时。");
		return false;
	}else{
		//是首单
		System.out.println("是首单，提交订单进行时。且总额小于120");
		//jedis.set(userId, "");
		jedis.hset("isFirstOrders", orderId,"Y");
		return true;
	}
		/*int sign=placeOrderDao.inspectIsFirstOrder(userId);
		
		if(sign==0){
			return true;
		}else{
		return false;
		}*/
	}
}
