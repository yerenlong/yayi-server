package com.yayiabc.http.mvc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yayiabc.common.utils.Page;
import com.yayiabc.http.mvc.pojo.model.ItemStatistics;
@Repository
public interface ItemStatisticsDao {
	List<ItemStatistics> query(@Param("itemName")String itemName,@Param("itemId")String itemId,@Param("itemSKU")String itemSKU,@Param("itemBrandName")String itemBrandName,@Param("page")Page page);

	int queryRefund(@Param("itemId")String itemId);
	
	int getCount(@Param("itemName")String itemName,@Param("itemId")String itemId,@Param("itemSKU")String itemSKU,@Param("itemBrandName")String itemBrandName);
}
