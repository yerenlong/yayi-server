package com.yayiabc.http.mvc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yayiabc.http.mvc.pojo.jpa.Cart;
import com.yayiabc.http.mvc.pojo.jpa.FreeShipping;
import com.yayiabc.http.mvc.pojo.jpa.OrderItem;
import com.yayiabc.http.mvc.pojo.jpa.Ordera;
import com.yayiabc.http.mvc.pojo.jpa.PostFee;
import com.yayiabc.http.mvc.pojo.jpa.Receiver;


public interface PlaceOrderDao {
	//购物车
	List<Cart> buyNows(@Param("userId")String userId,@Param("itemSKUs")String[] itemSKUs);
	
     
	//查询订货地址
	Receiver  queryReceiver(Integer receiverId);
   //查询包邮表数据
	List<FreeShipping> queryPostFree();
    //查询自定义运费表的数据
	List<PostFee> query();
     //用户钱币剩余
	int  ded(@Param("userId")String userId);
    //把商品同步到订单商品表
	int synchronization(@Param("cart")OrderItem cart,@Param("orderId")String orderId);
     //更新单个商品到订单商品表
	int synchronizationOne(@Param("orderItem")OrderItem orderItem);

	  //订单数据保存到订单表
	int  saveMessage(
			Ordera order
			/*@Param("orderId")String orderId, @Param("inHead")String inHead, @Param("registerNum")String registerNum,
			@Param("orderMessage")String orderMessage,@Param("phone") String phone*/);
  
	//伪清空购物车
	int  emptyCart(@Param("userId")String userId);
	
    //创建订单  并插入订单数据
	int  createOrder(@Param("orderId")String orderId,@Param("userId")String userId,
			@Param("order")Ordera order
			);	  
	
	 
	//用户不用默认  使用其他收货地址时
	public Receiver  upateAddress(@Param("receiverId")Integer receiverId);

     //查看钱币赠送百分比
	Integer queryQbPercentage(@Param("itemSKU")String itemSKU);

}
