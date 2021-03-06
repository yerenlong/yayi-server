package com.yayiabc.http.mvc.controller.message;

import com.yayiabc.common.utils.DataWrapper;
import com.yayiabc.http.mvc.pojo.jpa.MessageNumber;
import com.yayiabc.http.mvc.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 个人中心我的消息相关接口
 */
@Controller
@RequestMapping("api/message")
public class MessageController {
    @Autowired
    private MessageService messageService;


    //获取评论消息，问答消息的总个数
    @RequestMapping("getNumber")
    @ResponseBody
    public DataWrapper<MessageNumber> getNumber(
            @RequestHeader(value="token",required = false) String token,
            @RequestParam(value="type",required = false) String type
    ){
        return messageService.getNumber(token,type);
    }

    //获取系统消息 1评论消息 2.问答TODO
    @RequestMapping("getDetail")
    @ResponseBody
    public DataWrapper<Object> getDetail(
            @RequestHeader(value="token",required = false) String token,
            @RequestParam(value="type",required = true) String type,
            @RequestParam(value="numberPerPage",required = false,defaultValue = "10") Integer numberPerPage
    ) {
            return messageService.getDetail(token,type,numberPerPage);
    }

}
