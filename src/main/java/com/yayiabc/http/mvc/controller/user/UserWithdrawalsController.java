package com.yayiabc.http.mvc.controller.user;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yayiabc.common.enums.ErrorCodeEnum;
import com.yayiabc.common.sessionManager.VerifyCodeManager;
import com.yayiabc.common.utils.DataWrapper;
import com.yayiabc.http.mvc.pojo.jpa.UserWith;
import com.yayiabc.http.mvc.pojo.model.UserWithExtend;
import com.yayiabc.http.mvc.service.UserWithdrawalsService;

@Controller
@RequestMapping("api/userWith")
public class UserWithdrawalsController {
   @Autowired
   private UserWithdrawalsService userWithdrawalsService;
   /**
    * 提现列表的显示(后台)
    */
   @ResponseBody
   @RequestMapping("show")
   DataWrapper<Object> show(
		   @RequestHeader(value="adminToken",required=true) String adminToken,
		   @RequestParam(value="nameOrPhone",required=false) String nameOrPhone,
		   @RequestParam(value="sign",required=false) String sign,  //申请中   1   成功 为  2
		   @RequestParam(value="currentPage",required=false,defaultValue="1")Integer currentPage,
		   @RequestParam(value="numberPerpage",required=false,defaultValue="10")Integer numberPerpage
		   ){
	   HashMap<String, Object> hm=new HashMap<String,Object>();
	   hm.put("nameOrPhone", nameOrPhone);
	   hm.put("numberPerpage", numberPerpage);
	   hm.put("currentPage", currentPage);
	   hm.put("sign", sign);
	  return  userWithdrawalsService.show(hm);
   }
   /**
    * 
    * @param userWith
    * @param token
    * @return
    * 提交提现申请
    */
   @ResponseBody
   @RequestMapping("submit")
   DataWrapper<Object> submit(
		   @ModelAttribute UserWith  userWith,
		   @RequestHeader(value="token",required=true) String token,
		   @RequestParam(value="vCode",required=false) String vCode
		   ){
	  return  userWithdrawalsService.submit(userWith,token,vCode);
   }
   /**
    * 确定申请
    * (后台)
    */
   @ResponseBody
   @RequestMapping("YesOrNo")    // 1 提现申请中   2 是提现成功  3 提现拒绝
   DataWrapper<Object> yesOrNo(
		   @RequestHeader(value="adminToken",required=true) String adminToken,
		   @RequestParam(value="withId",required=true) String withId,
		   @RequestParam(value="sign",required=true) Integer sign
		   ){
	   //是否需要验证adminToken的安全
	  return  userWithdrawalsService.yesOrNo(withId,sign);
   }
   
  /**
   * 设置提现类型
   * @param token
   * @param accountHolder
   * @param cardNumber
   * @return
   */
   @ResponseBody
   @RequestMapping("setUpWitType")
   DataWrapper<Object> setUpWitType(
		   @RequestHeader(value="token",required=true) String token,
		   @RequestParam(value="witType",required=true) String witType,
		   @RequestParam(value="accountHolder",required=true) String accountHolder,
		   @RequestParam(value="oBank",required=false,defaultValue="非银行") String oBank,
		   @RequestParam(value="cardNumber",required=true) String cardNumber
		   ){
	  return  userWithdrawalsService.setUpWitType(token,witType,accountHolder,oBank,cardNumber);
   }
   
   @ResponseBody
   @RequestMapping("witSetUpShow")
   DataWrapper<Object> witSetUpShow(
		   @RequestHeader(value="token",required=true) String token
		   ){
	  return  userWithdrawalsService.witSetUpShow(token);
   }
   //显示当前用户的钱币余额
   @ResponseBody
   @RequestMapping("showUserQbNum")
   DataWrapper<Object> showUserQbNum(
		   @RequestHeader(value="token",required=true) String token
		   ){
	  return  userWithdrawalsService.showUserQbNum(token);
   }
   
 //为下单服务的钱币情况
   @ResponseBody
   @RequestMapping("showUserQbNumPo")
   DataWrapper<Object> showUserQbNumPo(
		   @RequestHeader(value="token",required=true) String token,
		   @RequestParam(value="sumItemsPrice",required=true) String sumItemsPrice
		   ){
	  return  userWithdrawalsService.showUserQbNumPo(token,sumItemsPrice);
   }
   //获取单用户最近的一次提现记录
   @ResponseBody
   @RequestMapping("latelyWithRecord")
   DataWrapper<Object> latelyWithRecord(
		   @RequestHeader(value="token",required=true) String token
		   ){
	  return  userWithdrawalsService.latelyWithRecord(token);
   }
   /**
    * 首页 提现成功小喇叭通知
    */
   @ResponseBody
   @RequestMapping("withHornPrompt")
   DataWrapper<List<UserWithExtend>> withHornPrompt(
		   ){
	  return  userWithdrawalsService.withHornPrompt();
   }
   
}
