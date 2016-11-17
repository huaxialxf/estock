package com.lxf.stock.backtest.daydata.domain;

import java.util.ArrayList;
import java.util.List;

public class StockInfo {
	
	//列表中的股票信息，按时间升序排列
	private List<StockDayInfo> listData = new ArrayList<StockDayInfo>();
	private String stockCode;

	public List<StockDayInfo> getListData() {
		return listData;
	}

	public void setListData(List<StockDayInfo> listData) {
		this.listData = listData;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

}
