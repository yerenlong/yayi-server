package com.yayiabc.http.mvc.service;

import java.util.List;
import java.util.Map;

import com.yayiabc.common.utils.DataWrapper;
import com.yayiabc.http.mvc.pojo.jpa.SaleInfo;
import com.yayiabc.http.mvc.pojo.model.UserAllInfo;

public interface UserManageListService {
	DataWrapper<List<Map<String,String>>> userlist(String phone,String trueName,String companyName,Integer isBindSale,Integer type,String saleName,String salePhone,Integer currentPage,Integer numberPerPage);

	DataWrapper<List<SaleInfo>> salelist(String salePhone,String saleName,Integer currentPage,Integer numberPerPage);
	
	DataWrapper<Void> bind(String salePhone,String userPhone);
	
	DataWrapper<Void> disBind(String salePhone,String userPhone);
	
	DataWrapper<UserAllInfo> detail(String phone);
}
