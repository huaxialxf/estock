package com.lxf.stock.backtest.daydata.domain;

public class InvestRecord {
	private StockDayInfo dayInfo;
	private String stockCode;
	private float inPrice = 0;
	private float outPrice = 0;
	private String inDate = null;//买入日期
	private String outDate = null;//出售日期
	private int holdDays = 0;// 持仓天数
	public StockDayInfo getDayInfo() {
		return dayInfo;
	}
	public void setDayInfo(StockDayInfo dayInfo) {
		this.dayInfo = dayInfo;
	}
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	public float getInPrice() {
		return inPrice;
	}
	public void setInPrice(float inPrice) {
		this.inPrice = inPrice;
	}
	public float getOutPrice() {
		return outPrice;
	}
	public void setOutPrice(float outPrice) {
		this.outPrice = outPrice;
	}
	public String getInDate() {
		return inDate;
	}
	public void setInDate(String inDate) {
		this.inDate = inDate;
	}
	public String getOutDate() {
		return outDate;
	}
	public void setOutDate(String outDate) {
		this.outDate = outDate;
	}
	public int getHoldDays() {
		return holdDays;
	}
	public void setHoldDays(int holdDays) {
		this.holdDays = holdDays;
	}

	
}
