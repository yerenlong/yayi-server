package com.yayiabc.http.mvc.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yayiabc.common.enums.ErrorCodeEnum;
import com.yayiabc.common.utils.DataWrapper;
import com.yayiabc.common.utils.Page;
import com.yayiabc.http.mvc.dao.UserDao;
import com.yayiabc.http.mvc.dao.UserMyQbDao;
import com.yayiabc.http.mvc.dao.UtilsDao;
import com.yayiabc.http.mvc.pojo.jpa.QbRecord;
import com.yayiabc.http.mvc.service.UserMyQbService;

@Service
public class UserMyQbServiceImpl implements UserMyQbService {

	@Autowired
	UserMyQbDao userMyQbDao;
	@Autowired
	UserDao userDao;
	@Autowired
	UtilsDao utilsDao;

	@Override
	public DataWrapper<QbRecord> add(QbRecord qbRecord, String phone,
			String token) {
		DataWrapper<QbRecord> dataWrapper = new DataWrapper<QbRecord>();
		String userId = userDao.getUserId(phone);
		if (userId == null || userId.equals(utilsDao.getUserID(token)) == false) {
			dataWrapper.setErrorCode(ErrorCodeEnum.Error);
			dataWrapper.setMsg("token错误");
		} else {
			qbRecord.setUserId(userId);
			int id = userMyQbDao.add(qbRecord);
			if (id > 0) {
				dataWrapper.setErrorCode(ErrorCodeEnum.No_Error);
				dataWrapper.setData(qbRecord);
			} else {
				dataWrapper.setErrorCode(ErrorCodeEnum.Error);
			}
		}
		return dataWrapper;
	}

	@Override
	public DataWrapper<List<QbRecord>> query(String phone, Integer type,
			Integer currentPage, Integer numberPerPage, String token) {
		DataWrapper<List<QbRecord>> dataWrapper = new DataWrapper<List<QbRecord>>();
		String userId = userDao.getUserId(phone);
		if (userId == null || userId.equals(utilsDao.getUserID(token)) == false) {
			dataWrapper.setErrorCode(ErrorCodeEnum.Error);
			dataWrapper.setMsg("token错误");
		} else {
			Page page = new Page();
			page.setNumberPerPage(numberPerPage);
			page.setCurrentPage(currentPage);
			int totalNumber = userMyQbDao.getCount(userId);
			dataWrapper.setPage(page, totalNumber);
			List<QbRecord> list = userMyQbDao.query(page, userId, type);
			dataWrapper.setData(list);
		}
		return dataWrapper;
	}

}
