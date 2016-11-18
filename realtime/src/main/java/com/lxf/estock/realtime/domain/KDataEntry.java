package com.lxf.estock.realtime.domain;

public class KDataEntry {

	private String date; // 日期
	private float lastClosePrice;// 昨收价
	private float openPrice;// 开盘价
	private float closePrice;// 收盘价
	private float hightPrice;// 最高价
	private float lowPrice;// 最低价
	private float transactionCount;//成交笔数
	private float totalVolume;//成交总量
	private float totalAmount;//总成交金额
	
	public String status; //状态
	
	
	
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public float getLastClosePrice() {
		return lastClosePrice;
	}

	public void setLastClosePrice(float lastClosePrice) {
		this.lastClosePrice = lastClosePrice;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public float getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(float openPrice) {
		this.openPrice = openPrice;
	}

	public float getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(float closePrice) {
		this.closePrice = closePrice;
	}

	public float getHightPrice() {
		return hightPrice;
	}

	public void setHightPrice(float hightPrice) {
		this.hightPrice = hightPrice;
	}

	public float getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(float lowPrice) {
		this.lowPrice = lowPrice;
	}

	public float getTransactionCount() {
		return transactionCount;
	}

	public void setTransactionCount(float transactionCount) {
		this.transactionCount = transactionCount;
	}

	public float getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(float totalVolume) {
		this.totalVolume = totalVolume;
	}

	public float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String toCsv() {

		StringBuffer buffer = new StringBuffer();
		buffer.append(date + ",");
		buffer.append(lastClosePrice + ",");
		buffer.append(openPrice + ",");
		buffer.append(closePrice + ",");
		buffer.append(hightPrice + ",");
		buffer.append(lowPrice + ",");
		buffer.append(transactionCount + ",");
		buffer.append(totalVolume + ",");
		buffer.append(totalAmount);
		return buffer.toString();
	}

}
