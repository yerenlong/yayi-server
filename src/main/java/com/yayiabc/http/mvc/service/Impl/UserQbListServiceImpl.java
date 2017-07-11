package com.yayiabc.http.mvc.service.Impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yayiabc.common.utils.Page;
import com.yayiabc.common.enums.ErrorCodeEnum;
import com.yayiabc.common.utils.DataWrapper;
import com.yayiabc.http.mvc.dao.UserDao;
import com.yayiabc.http.mvc.dao.UserMyQbDao;
import com.yayiabc.http.mvc.dao.UserQbListDao;
import com.yayiabc.http.mvc.pojo.jpa.QbRecord;
import com.yayiabc.http.mvc.service.UserQbListService;

@Service
public class UserQbListServiceImpl implements UserQbListService {

	@Autowired
	UserQbListDao userQbListDao;
	@Autowired
	UserDao userDao;
	@Autowired
	UserMyQbDao userMyQbDao;

	@Override
	public DataWrapper<List<QbRecord>> list(String phone, String startDate,
			String endDate, Integer currentPage, Integer numberPerPage) {
		DataWrapper<List<QbRecord>> dataWrapper = new DataWrapper<List<QbRecord>>();
		Page page = new Page();
		page.setNumberPerPage(numberPerPage);
	    page.setCurrentPage(currentPage);
		int totalNumber = userQbListDao.getCount(phone, startDate, endDate);
		dataWrapper.setPage(page, totalNumber);
		List<QbRecord> list = userQbListDao.list(phone, startDate, endDate,
				page);
			dataWrapper.setData(list);
		return dataWrapper;
	}

	@Override
	public DataWrapper<Void> update(Integer qbBalance, String phone) {
		DataWrapper<Void> dataWrapper = new DataWrapper<Void>();
		String userId = userDao.getUserId(phone);		
		int i = userQbListDao.update(qbBalance, phone);
		if (i > 0) {
			QbRecord qbRecord=new QbRecord();
			qbRecord.setUserId(userId);
			Integer newQb = userQbListDao.queryQb(phone);
			Integer oldQb=userQbListDao.queryQbBalances(userId);
			if(newQb > oldQb){
				qbRecord.setQbRget(newQb-oldQb);
			}else if(newQb < oldQb){
				qbRecord.setQbRout(newQb-oldQb);
			}
			qbRecord.setQbBalances(qbBalance);
			qbRecord.setRemark("管理员修改乾币余额");
			int sign=userMyQbDao.add(qbRecord);
			if(sign > 0){
				dataWrapper.setErrorCode(ErrorCodeEnum.No_Error);
			}
		} else {
			dataWrapper.setErrorCode(ErrorCodeEnum.Error);
		}
		return dataWrapper;
	}

	@Override
	public DataWrapper<Map<String, Integer>> queryQb(String userPhone) {
		DataWrapper<Map<String, Integer>> dataWrapper = new DataWrapper<Map<String, Integer>>();
		String userId = userDao.getUserId(userPhone);
		if (userId == null) {
			dataWrapper.setErrorCode(ErrorCodeEnum.Username_NOT_Exist);
		} else {
			Integer qbBalance = userQbListDao.queryQb(userPhone);
			Map<String, Integer> map = new HashMap<String, Integer>();
			if (qbBalance == null) {
				map.put("qbBalance", 0);
			} else {
				map.put("qbBalance", qbBalance);
			}
			dataWrapper.setData(map);
		}
		return dataWrapper;
	}

}
