package com.yayiabc.http.mvc.pojo.jpa;

public class Invoice {
	private Integer InvoiceId;
	private String companyName;
	private Integer taxpayerNum;
	private String registeredAddress;
	private Integer registeredPhone;
	private String opneBank;
	private String bankNumber;
	private String stickNanme;
	private String stickPhone;
	private String stickaddress;
	private String orderId;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Integer getInvoiceId() {
		return InvoiceId;
	}
	public void setInvoiceId(Integer invoiceId) {
		InvoiceId = invoiceId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public Integer getTaxpayerNum() {
		return taxpayerNum;
	}
	public void setTaxpayerNum(Integer taxpayerNum) {
		this.taxpayerNum = taxpayerNum;
	}
	public String getRegisteredAddress() {
		return registeredAddress;
	}
	public void setRegisteredAddress(String registeredAddress) {
		this.registeredAddress = registeredAddress;
	}
	public Integer getRegisteredPhone() {
		return registeredPhone;
	}
	public void setRegisteredPhone(Integer registeredPhone) {
		this.registeredPhone = registeredPhone;
	}
	public String getOpneBank() {
		return opneBank;
	}
	public void setOpneBank(String opneBank) {
		this.opneBank = opneBank;
	}
	public String getBankNumber() {
		return bankNumber;
	}
	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}
	public String getStickNanme() {
		return stickNanme;
	}
	public void setStickNanme(String stickNanme) {
		this.stickNanme = stickNanme;
	}
	public String getStickPhone() {
		return stickPhone;
	}
	public void setStickPhone(String stickPhone) {
		this.stickPhone = stickPhone;
	}
	public String getStickaddress() {
		return stickaddress;
	}
	public void setStickaddress(String stickaddress) {
		this.stickaddress = stickaddress;
	}

}