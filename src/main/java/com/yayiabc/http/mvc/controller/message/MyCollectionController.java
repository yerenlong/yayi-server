package com.yayiabc.http.mvc.controller.message;

import com.yayiabc.common.annotation.UserTokenValidate;
import com.yayiabc.common.utils.DataWrapper;
import com.yayiabc.http.mvc.dao.UtilsDao;
import com.yayiabc.http.mvc.service.MyCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 个人中心我的收藏 1.病例，2.视频 3.商品 4.问答 5.资料库
 */
@Controller
@RequestMapping("api/collect")
public class MyCollectionController {

    //默认显示什么(病例)1.病例，2.视频 3.商品 4.问答 5.资料库
    private static final String DEFAULT_TYPE="商品";

    @Autowired
    private MyCollectionService myCollectionService;
    @Autowired
    private UtilsDao utilsDao;
    @RequestMapping("queryList")
    @ResponseBody
    @UserTokenValidate
    public DataWrapper<Object> queryList(
            @RequestHeader(value="token",required =false) String token,
            @RequestParam(value="userId",required = false) String userId,
            @RequestParam(value="type",required = false) String type,
            @RequestParam(value="category",required = false)Integer category,
            @RequestParam(value="currentPage",required = false,defaultValue = "1") Integer currentPage,
            @RequestParam(value="numberPerPage",required = false,defaultValue = "10")Integer numberPerPage
    ){  
           userId=utilsDao.getUserID(token);

        return myCollectionService.queryList(type,currentPage,numberPerPage,userId,category);
    }
}
