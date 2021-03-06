package com.yayiabc.http.mvc.dao;

import java.util.List;
import java.util.Set;

import com.yayiabc.common.utils.Page;
import com.yayiabc.http.mvc.pojo.jpa.ItemInfo;
import org.apache.ibatis.annotations.Param;

import com.yayiabc.http.mvc.pojo.jpa.VidManage;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoManageDao {

			
		
			int insertVid(VidManage vidManage);

			int updateVid(VidManage VidManage);
	
			int  deleteVid(@Param("viId")Integer viId);



	List<VidManage> showVid(@Param("rule")Integer rule,@Param("videoCategory") Integer videoCategory,@Param("keyWord")String keyWord,@Param("page")Page page,@Param("userId")String userId);

    VidManage detail(@Param("viId")Integer viId,@Param("userId") String userId);

    List<VidManage> queryMyCollect(@Param("idList")List<String> idList,@Param("currentNumber") Integer currentNumber,@Param("numberPerPage") Integer numberPerPage);

    ItemInfo videoItem(String viId);

	String getVideoRoute(Integer viId);

    ItemInfo getVideoItem(String videoRout);


    void playVideo(Integer viId);

    int getVideoTotalNum(@Param("videoCategory")Integer videoCategory,@Param("keyWord") String keyWord);

    void addVideoZanNum(Integer typeId);

    void delVideoZanNum(Integer typeId);
}
