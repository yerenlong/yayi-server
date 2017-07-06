package com.yayiabc.http.mvc.controller.user;

import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yayiabc.common.utils.DataWrapper;
import com.yayiabc.http.mvc.pojo.jpa.Ordera;
import com.yayiabc.http.mvc.service.CommentManageService;

@Controller
@RequestMapping("api/commentManage")
public class CommentManageController {
      @Autowired 
      private CommentManageService commentManage;
     
      @RequestMapping("show")
      @ResponseBody
      //显示评论
     public DataWrapper<List<Ordera>> show(
    		 @RequestParam(value="orderId",required=false) String orderid,
    		 @RequestParam(value="phone",required=false) String phone,
    		 @RequestParam(value="recoveryState",required=false) String recoveryState,
    		 @RequestParam(value="currentPage",required=false) Integer currentPage,
    		 @RequestParam(value="numberPerpage",required=false) Integer numberPerpage
    		 ){
		return commentManage.commentM(orderid,recoveryState,phone,currentPage,numberPerpage);
      }
      //回复评论
      @RequestMapping("reply")
      @ResponseBody
      public DataWrapper<Void> reply(
    		  @RequestParam(value="orderId",required=true) String orderId,
    		  @RequestParam(value="itemId",required=true) String itemId,
    		  @RequestParam(value="data",required=true) String data
     		 ){
 		return commentManage.reply(orderId,itemId,data);
       }
}


