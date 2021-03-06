package com.yayiabc.http.mvc.dao;

import com.yayiabc.http.mvc.pojo.jpa.Benefit;
import com.yayiabc.http.mvc.pojo.jpa.BenefitDetail;
import com.yayiabc.http.mvc.pojo.jpa.ExcelEntry;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BenefitChangeDao {

	void addBenefit(Benefit benefit);

//	void addBenefitDetail(@Param("benefitId")Integer benefitId,@Param("benefitCode") String benefitCode);

	
	
	BenefitDetail getBenefitByBenefitCode(@Param("benefitCode")String benefitCode);

	Benefit getBenefitByBenefitId(@Param("benefitId")Integer benefitId);

	String getUserIdByToken(@Param("token")String token);

	String getPhoneByUserId(@Param("userId")String userId);

	void updateState(@Param("benefitCodeId")Integer benefitCodeId,@Param("phone") String phone);

	void updateBenefitValueNum(@Param("benefitId")Integer benefitId);

	Integer getTotalNum(@Param("benefitName")String benefitName,@Param("enable") Integer enable);

	List<Benefit> getBenefitList(@Param("benefitName")String benefitName,@Param("enable") Integer enable,@Param("currentNumber")Integer currentNumber,@Param("numberPerPage")Integer numberPerPage);

	List<BenefitDetail> getBenefitDetailListByBenefitId(@Param("benefitId")Integer benefitId,
			@Param("currentNumber")Integer currentNumber, @Param("numberPerPage")Integer numberPerPage);

	List<ExcelEntry> getExcelEntryList(@Param("benefitId")Integer benefitId);

	int getBenefitCodeCount(@Param("benefitCode")String benefitCode);

	List<String> queryValidateCode(@Param("benefitId")Integer benefitId);

	void deleteBenefitDetailByBenefitId(@Param("benefitId")Integer benefitId);

	void deleteBenefitByBenefitId(@Param("benefitId")Integer benefitId);


	void addBenefitDetail(Map<String, Object> map);
}
