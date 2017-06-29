package com.yayiabc.http.mvc.service;

import java.util.Date;
import java.util.List;

import com.yayiabc.common.utils.DataWrapper;
import com.yayiabc.http.mvc.pojo.jpa.ItemDetail;
import com.yayiabc.http.mvc.pojo.jpa.ItemInfo;
import com.yayiabc.http.mvc.pojo.jpa.ItemProperty;
import com.yayiabc.http.mvc.pojo.jpa.ItemValue;

public interface ItemInfoManageService {

	DataWrapper<List<ItemInfo>> itemInfoList(String itemId, String itemName,
			String itemClassify, String itemBrandName, Integer state);

	DataWrapper<Void> up(String itemId);

	DataWrapper<Void> down(String itemId);

	DataWrapper<Void> delete(String itemId);

	DataWrapper<Void> update();


	DataWrapper<Void> getItemSku(String itemId);

	DataWrapper<Void> insert(String itemId, String itemName,
			String oneClassify, String itemLevels, String twoClassify,
			String threeClassify, String itemPica, String itemPicb,
			String itemPicc, String itemPicd, String itemPice, String video,
			String itemDesc, String itemUse, String itemRange,
			String registerId, String storeItemId, Integer apparatusType,
			String unit, String producePompany, Date registerDate,
			String itemPacking, String itemPparam, String itemBrandName,
			List<ItemValue> itemValueList);



	

}
