package com.lxf.stock.backtest.daydata.frame;

import java.io.IOException;
import java.util.List;

import com.lxf.stock.backtest.daydata.domain.StockInfo;

public interface IDataProvider {
	public List<String> getStockList() throws IOException;
	public StockInfo getStockInfo(String stockCode) throws IOException;

}
