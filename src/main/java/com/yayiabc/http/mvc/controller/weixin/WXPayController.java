package com.yayiabc.http.mvc.controller.weixin;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yayiabc.common.utils.DataWrapper;
import com.yayiabc.common.utils.GetQCode;
import com.yayiabc.common.weixin.WXPay;
import com.yayiabc.common.weixin.WXPayConfigImpl;
import com.yayiabc.common.weixin.WXPayUtil;
import com.yayiabc.http.mvc.dao.AliPayDao;
import com.yayiabc.http.mvc.service.AliPayService;

@Controller
@RequestMapping("api/weixin")
public class WXPayController {
	
	@Autowired
	private AliPayService aliPayService;
	
	@Autowired
	private AliPayDao aliPayDao;
	
	/**
	 * 统一下单接口
	 * @return
	 */
	@RequestMapping("unifiedOrderReturnUrl")
	@ResponseBody
	public void unifiedOrderReturnUrl(String orderId,HttpServletResponse response){
		System.out.println("开始处理回调");
		HashMap<String, String> hashMap=aliPayService.queryY(orderId);
		String total_fee=hashMap.get("WIDtotal_fee");//0.01
		Double total=Double.parseDouble(total_fee);
		Integer totalFee=(int)(total*100);
		String body=hashMap.get("WIDsubject");
		try {
			WXPay wxPay = new WXPay(WXPayConfigImpl.getInstance(), "http://47.93.48.111:8080/api/weixin/getReturnUrl");
			Map<String,String> reqData =new HashMap<String,String>();
			if(body!=null&&!"".equals(body)){
				reqData.put("body",body);//必传
			}else{
				reqData.put("body", "商品名称");
			}
			reqData.put("out_trade_no",orderId);
			reqData.put("fee_type", "CNY");
			if(totalFee!=null&&!"".equals(totalFee)){
				reqData.put("total_fee",totalFee+"");//必传,总金额,接口中单位为分,对账单中的单位为元,必须为整数,可以通过参数传进来
			}else {
				reqData.put("total_fee","1");
			}
			reqData.put("spbill_create_ip","47.93.48.111");//终端ip,必传,APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
//			reqData.put("notify_url","http://47.93.48.111:8080/api/item/getItemId");//通知地址,接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数
			reqData.put("trade_type","NATIVE");//必传,现场扫码付
			reqData.put("product_id",System.currentTimeMillis()+"");//扫码支付时此参数必传,可以通过参数传进来,trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义。
			System.out.println(reqData);
			Map<String,String> respMap=wxPay.unifiedOrder(reqData);
			System.out.println(respMap);
			String urlCode=(String)respMap.get("code_url");
			GetQCode.getqCode(urlCode, response);
		} catch (Exception e) {
			String msg="服务器错误";
			e.printStackTrace();
		}
	}
	
	/**
	 * 接收微信系统发送的回掉的请求，并展示给用户
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("getReturnUrl")
	@ResponseBody
	public void getReturnUrl(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		WXPay wxPay =new WXPay(WXPayConfigImpl.getInstance());
		System.out.println("helloworld");
		//读取参数
		InputStream inputStream;
		StringBuffer sb=new StringBuffer();
		inputStream=request.getInputStream();
		String s;
		BufferedReader in=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
		while((s=in.readLine())!=null){
			sb.append(s);
		}
		in.close();
		inputStream.close();
		
		//解析XML成MAP
		Map<String, String> map =new HashMap<String,String>();
		map=WXPayUtil.xmlToMap(sb.toString());
		System.out.println(map);
		
		//过滤空,设置treeMap
		SortedMap<Object, Object> packageParam=new TreeMap<Object, Object>();
		Iterator it=map.keySet().iterator();
		while(it.hasNext()){
			String parameter=(String)it.next();
			String parameterValue=map.get(parameter);
			String v="";
			if(null!=parameterValue){
				v=parameterValue.trim();
			}
			packageParam.put(parameter, v);
		}
		
		//账号信息
		String out_trade_no =(String)packageParam.get("out_trade_no");
		System.out.println(out_trade_no);
		//判断签名是否正确
		if(wxPay.isPayResultNotifySignatureValid(map)){
			//处理业务开始
			String resXml="";
			if("SUCCESS".equals((String)packageParam.get("result_code"))){
				//判断返回结果中的金额是否和数据库中查出来的订单金额一致
				String orderId=(String)packageParam.get("out_trade_no");
				HashMap<String, String> hashMap=aliPayService.queryY(orderId);
				String total_fee=hashMap.get("WIDtotal_fee");//0.01
				Double total=Double.parseDouble(total_fee);
				Integer totalFee=(int)(total*100);
				if(totalFee==(int)packageParam.get("total_fee")){
					//这里是支付成功
					System.out.println("支付成功");
					//改变订单状态
					aliPayDao.updateStateAndPayTime(orderId);
					resXml = "<xml>" + "<return_code><" +
							"![CDATA[SUCCESS]]></return_code>"+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
				}else{
					resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"+ "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
				}
			}else{
				resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"+ "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
			}
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());  
			out.write(resXml.getBytes());  
			out.flush();  
			out.close();
		}else{
			System.out.println("签名验证失败");
		}
	}
	
	@RequestMapping("checkOrderState")
	@ResponseBody
	public DataWrapper<Void> checkOrderState(String out_trade_no){
		DataWrapper<Void> dataWrapper =new DataWrapper<Void>();
		Integer num=aliPayDao.querySatetIsTwo(out_trade_no);
		dataWrapper.setNum(num);
		return dataWrapper;
	}
}
