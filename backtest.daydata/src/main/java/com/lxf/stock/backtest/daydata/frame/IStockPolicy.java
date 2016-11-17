package com.lxf.stock.backtest.daydata.frame;

import java.util.List;

import com.lxf.stock.backtest.daydata.domain.InvestRecord;
import com.lxf.stock.backtest.daydata.domain.StockDayInfo;
import com.lxf.stock.backtest.daydata.domain.StockInfo;

public interface IStockPolicy {
	public boolean buy(String stockCode, String curDay, List<StockDayInfo> historyDayInfoList);

	public void sale(String stockCode, InvestRecord investRecord, StockInfo info);

}
