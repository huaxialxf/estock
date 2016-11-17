package com.lxf.stock.backtest.daydata.domain;

public class StockDayInfo {
	private String date; // 日期
	private float openPrice;// 开盘价
	private float closePrice;// 收盘价
	private float hightPrice;// 最高价
	private float lowPrice;// 最低价
	private float volume;// 成交量

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

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

}
