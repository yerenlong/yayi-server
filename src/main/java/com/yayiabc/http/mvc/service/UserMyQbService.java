package com.yayiabc.http.mvc.service;

import java.util.List;
import com.yayiabc.common.utils.DataWrapper;
import com.yayiabc.http.mvc.pojo.jpa.QbRecord;

public interface UserMyQbService {
	DataWrapper<QbRecord> add(QbRecord qbRecord, String phone, String token);

	DataWrapper<List<QbRecord>> query(String phone, Integer type,
			Integer currentPage, Integer numberPerPage, String token);
}
