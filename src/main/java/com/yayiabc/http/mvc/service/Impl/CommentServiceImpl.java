package com.yayiabc.http.mvc.service.Impl;



import com.qiniu.util.Json;
import com.yayiabc.common.utils.DataWrapper;
import com.yayiabc.common.utils.Page;
import com.yayiabc.common.utils.ScoreUtil;
import com.yayiabc.http.mvc.dao.UtilsDao;
import com.yayiabc.http.mvc.pojo.jpa.Comment;

import com.yayiabc.http.mvc.pojo.jpa.SubComment;
import com.yayiabc.http.mvc.pojo.jpa.User;
import com.yayiabc.http.mvc.service.CommentService;
import com.yayiabc.http.mvc.service.RedisService;
import com.yayiabc.http.mvc.service.ZanService;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private UtilsDao utilsDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ZanService zanService;


    @Override
    public DataWrapper<Object> addCom(String token, String type, Integer beCommentedId, Comment comment,Integer parentId) {
        DataWrapper<Object> dataWrapper = new DataWrapper<Object>();
        //判断是父评论还是子评论
//        boolean flag=checkComment(parentId);//true父评论 false 子评论
        String str="";//父评论
        if(parentId!=null){
            str=":"+parentId;//子评论
        }
        //通过token来获取用户的信息
        User user = utilsDao.getUserByToken(token);
        comment.setUserId(user.getUserId());
        comment.setUserName(user.getTrueName());
        comment.setUserPic(user.getUserPic());
        //牙医圈的评论拿出来做
        if(type.equals("牙医圈")){
            return addMomentCom(token,type,beCommentedId,comment,parentId,dataWrapper,str);
        }
        //生成评论自增主键的STRING类型
        long commentId = redisService.STRINGS.incrBy("自增序列"+type + beCommentedId+str , 1);
        //初始化或者自增一个点赞计数的SET
        redisService.SORTSET.zadd("点赞计数列表"+type+":"+beCommentedId+str,0,commentId+"");
        comment.setCommentId(commentId);
        comment.setCommentTime(new Date());
        //生成评论的key
        String key = type + "评论" + beCommentedId+str;
        JSONObject jsonObject;
        if("".equals(str)){//父评论
            //初始化生成评论的评论数序列,如果不存在创建，如果存在加一
            redisService.SORTSET.zincrby(type+"评论数",1,beCommentedId+"");
            //将对象存储进List
            jsonObject = JSONObject.fromObject(comment);
            dataWrapper.setData(comment);
        }else{
            //获取父评论的评论人和评论id
            String replyUserId="";
            String replyUserName="";
            List<String> jsonList=redisService.LISTS.lrange(type + "评论" + beCommentedId,0,-1);
            for (String strCom:jsonList
                 ) {
                JSONObject jstr = JSONObject.fromObject(strCom);
                Comment comment1=(Comment)JSONObject.toBean(jstr,Comment.class);
                if((int)comment1.getCommentId()==parentId.intValue()){
                    replyUserId=comment1.getUserId();
                    replyUserName=comment1.getUserName();
                    break;
                }
            }
            //填充子评论对象里面的数据
            SubComment subComment=new SubComment();
            subComment.setCommentId(comment.getCommentId());
            subComment.setUserId(comment.getUserId());
            subComment.setUserName(comment.getUserName());
            subComment.setCommentContent(comment.getCommentContent());
            subComment.setReplyUserId(replyUserId);
            subComment.setReplyUserName(replyUserName);
            subComment.setCommentTime(new Date());
            subComment.setUserPic(comment.getUserPic());
            jsonObject = JSONObject.fromObject(subComment);
            dataWrapper.setData(subComment);
        }
        String json = jsonObject.toString();
        redisService.LISTS.rpush(key, json);
        return dataWrapper;
    }

    //牙医圈的评论,只有一级评论
    private DataWrapper<Object> addMomentCom(String token, String type, Integer beCommentedId, Comment comment, Integer parentId,DataWrapper<Object> dataWrapper,String str) {
        //生成评论的自增主键序列
        long commentId = redisService.STRINGS.incrBy("自增序列"+type + beCommentedId, 1);
        SubComment subComment=new SubComment();
        subComment.setCommentId(commentId);
        subComment.setCommentTime(new Date());
        subComment.setCommentContent(comment.getCommentContent());
        subComment.setUserId(comment.getUserId());
        subComment.setUserName(comment.getUserName());
        if(!"".equals(str)){//子评论
            //找出父评论的userid和username
            //得到评论列表
           List<String> comListStr= redisService.LISTS.lrange(type+"评论"+ beCommentedId,0,-1);
            for (String comStr:comListStr
                 ) {
                JSONObject jsonObject= JSONObject.fromObject(comStr);
                SubComment subComment6=(SubComment) JSONObject.toBean(jsonObject,SubComment.class);
                if((int)subComment6.getCommentId()==parentId.intValue()){
                    subComment.setReplyUserId(subComment6.getUserId());
                    subComment.setReplyUserName(subComment6.getUserName());
                    break;
                }
            }
        }
        String subComJsonStr= JSONObject.fromObject(subComment).toString();
        redisService.LISTS.rpush(type+"评论"+ beCommentedId,subComJsonStr);
        dataWrapper.setData(subComment);
        return dataWrapper;
    }

    @Override
    public List<Comment> queryCom(String type, Integer beCommentedId,Integer currentPage,Integer numberPerPage) {
        Set<String> idSet=redisService.SORTSET.zrevrange("点赞计数列表"+type+":"+beCommentedId,(currentPage-1)*numberPerPage,currentPage*numberPerPage);
        System.out.println("点赞计数列表"+idSet);
        List<Comment> commentModelList = new ArrayList<Comment>();
        String key = type + "评论" + beCommentedId;
        List<String> jsonList = redisService.LISTS.lrange(key, 0, -1);
        System.out.println("jsonList"+jsonList);
        List<Comment> comments = new ArrayList<Comment>();
        for (String id : idSet
                ) {
            for (String json : jsonList
                    ) {
                JSONObject jsonObject = JSONObject.fromObject(json);
                Comment commentModel = (Comment) JSONObject.toBean(jsonObject, Comment.class);
                if((commentModel.getCommentId()+"").equals(id)){
                    //填充点赞数
                    int zan=zanService.getZanNumber(type,beCommentedId,(int)commentModel.getCommentId(),null);
                    commentModel.setZan(zan);
                    List<SubComment> subCommentList=querySubCom(type,beCommentedId,commentModel.getCommentId(),currentPage,numberPerPage);
                    System.out.println(subCommentList);
                    commentModel.setSubCommentList(subCommentList);
                    comments.add(commentModel);
                }
            }
        }
        return comments;
    }


    @Override
    public List<SubComment> querySubCom(String type,Integer beCommentedId,long preCommentId,Integer currentPage,Integer numberPerPage) {
        Set<String> idSet=redisService.SORTSET.zrevrange("点赞计数列表"+type+":"+beCommentedId+":"+preCommentId,(currentPage-1)*numberPerPage,currentPage*numberPerPage);
        System.out.println("子评论idset"+idSet);
        String key = type + "评论" + beCommentedId+":"+preCommentId;
        List<String> jsonList = redisService.LISTS.lrange(key, 0, -1);
        System.out.println("子评论jsonList"+jsonList);
        List<SubComment> subCommentList = new ArrayList<SubComment>();
        for (String id:idSet
             ) {
            for (String json:jsonList
                 ) {
                JSONObject jsonObject=JSONObject.fromObject(json);
                SubComment subComment=(SubComment)JSONObject.toBean(jsonObject,SubComment.class);
                if((subComment.getCommentId()+"").equals(id)){
                    //填充赞数
                    int zan=zanService.getZanNumber(type,beCommentedId,(int)preCommentId,(int)subComment.getCommentId());
                    subComment.setZan(zan);
                    subCommentList.add(subComment);
                    break;
                }
            }
        }
        return subCommentList;
    }

    @Override
    public DataWrapper<Void> delete(String type, String beCommentedId, Integer parentId, Integer presentId) {
        DataWrapper<Void> dataWrapper=new DataWrapper<Void>();
        if("牙医圈".equals(type)){
            return deleteYayiCom(type,beCommentedId,parentId,dataWrapper);
        }
        //判断是父评论还是子评论
        String str="";//父评论
        if(presentId!=null){
            str=":"+presentId;//子评论
        }
        if("".equals(str)){//父评论
            deleteCom(type,beCommentedId,parentId);
        }else{//子评论
            deleteSubCom(type,beCommentedId,parentId,presentId);
        }
        return dataWrapper;
    }

    private DataWrapper<Void> deleteYayiCom(String type, String beCommentedId, Integer parentId,DataWrapper<Void> dataWrapper) {
        List<String> jsonList=redisService.LISTS.lrange(type+"评论"+beCommentedId,0,-1);
        for (int i=0;i<jsonList.size();i++) {
            JSONObject jsonObject=JSONObject.fromObject(jsonList.get(i));
            SubComment subComment=(SubComment) JSONObject.toBean(jsonObject,SubComment.class);
            if(parentId.intValue()==subComment.getCommentId()){
                redisService.LISTS.lrem(type+"评论"+beCommentedId,0,jsonList.get(i));
            }
        }
        return dataWrapper;
    }

    public void deleteSubCom(String type, String beCommentedId, Integer parentId, Integer presentId){
        List<String> jsonList=redisService.LISTS.lrange(type+beCommentedId+":"+parentId,0,-1);
        for (int i=0;i<jsonList.size();i++){
            JSONObject jsonObject=JSONObject.fromObject(jsonList.get(i));
            SubComment subComment=(SubComment) JSONObject.toBean(jsonObject,SubComment.class);
            if(presentId.intValue()==subComment.getCommentId()){
                redisService.LISTS.lrem(type+"评论"+beCommentedId+":"+parentId,0,jsonList.get(i));
            }
        }
    }

    public void deleteCom(String type, String beCommentedId, Integer parentId){
        //删除父评论
        List<String> jsonComList=redisService.LISTS.lrange(type+"评论"+beCommentedId,0,-1);
        for(int i=0;i<jsonComList.size();i++){
            JSONObject jsonObject=JSONObject.fromObject(jsonComList.get(i));
            Comment comment=(Comment) JSONObject.toBean(jsonObject,Comment.class);
            if(parentId.intValue()==comment.getCommentId()){
                System.out.println("进来了");
                redisService.LISTS.lrem(type+"评论"+beCommentedId,0,jsonComList.get(i));
            }
        }
        //删除子评论
        redisService.getJedis().del(type+"评论"+beCommentedId+":"+parentId);
    }


}

