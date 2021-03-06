package com.yayiabc.http.mvc.service.Impl;


import com.yayiabc.http.mvc.dao.CornDao;
import com.yayiabc.http.mvc.service.CornService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;





@Service
public class CornServiceImpl implements CornService{
	
	@Autowired
	private CornDao cornDao;

	
	

	@Override
	public void addBalance() {
		cornDao.addBalance();
		/*List<String> saleIdList=cornDao.getSaleIdList();
		for (String saleId : saleIdList) {
			Double totalHaoCaiMoney=cornDao.getTotalHaoCaiMoneyBySaleId(saleId);
			if(totalHaoCaiMoney==null){
				totalHaoCaiMoney=0.0;
			}
			Double totalGongJuMoney=cornDao.getTotalGongJuMoneyBySaleId(saleId);
			if(totalGongJuMoney==null){
				totalGongJuMoney=0.0;
			}
			Double totalHaoCaiRefund=cornDao.getTotalHaoCaiRefundBySaleId(saleId);
			if(totalHaoCaiRefund==null){
				totalHaoCaiRefund=0.0;
			}
			Double totalGongJuRefund=cornDao.getTotalGongJuRefundBySaleId(saleId);
			if(totalGongJuRefund==null){
				totalGongJuRefund=0.0;
			}
			Double getMoneyByHaoCai=0.0;
			if(totalHaoCaiMoney<1000){
				getMoneyByHaoCai=(totalHaoCaiMoney-totalHaoCaiRefund)*0.05;
			}else if(totalHaoCaiMoney<2000){
				getMoneyByHaoCai=(totalHaoCaiMoney-totalHaoCaiRefund)*0.08;
			}else if(totalHaoCaiMoney<3000){
				getMoneyByHaoCai=(totalHaoCaiMoney-totalHaoCaiRefund)*0.12;
			}else{
				getMoneyByHaoCai=(totalHaoCaiMoney-totalHaoCaiRefund)*0.16;
			}
			Double getMoneyByGongJu=0.0;
			if(totalGongJuMoney<10000){
				getMoneyByGongJu=(totalGongJuMoney-totalGongJuRefund)*0.08;
			}else {
				getMoneyByGongJu=(totalGongJuMoney-totalGongJuRefund)*0.15;
			}
			Double balanceIn=getMoneyByHaoCai+getMoneyByGongJu;
			
			if(balanceIn!=0){
				Double balance=cornDao.getLatestBalanceBySaleId(saleId);
				if(balance==null){
					balance=0.00;
				}
				balance+=0.00;
				DecimalFormat df = new DecimalFormat("#.00");
				balanceIn=Double.parseDouble(df.format(balanceIn));
				balance+=balanceIn;
				Balance balanceMonthCash=new Balance();
				balanceMonthCash.setSaleId(saleId);
				balanceMonthCash.setHaocaiMoney(Double.parseDouble(df.format(totalHaoCaiMoney)));
				balanceMonthCash.setGongjuMoney(Double.parseDouble(df.format(totalGongJuMoney)));
				balanceMonthCash.setHaocaiRefund(Double.parseDouble(df.format(totalHaoCaiRefund)));
				balanceMonthCash.setGongjuRefund(Double.parseDouble(df.format(totalGongJuRefund)));
				balanceMonthCash.setBalanceIn(balanceIn);
				balanceMonthCash.setBalance(balance);
				balanceMonthCash.setDescribey("进账每月结算");
				balanceMonthCash.setCreated(new Date());
				cornDao.addBalancePerMonth(balanceMonthCash);
				
			}
		}*/
		
		
	}
}
