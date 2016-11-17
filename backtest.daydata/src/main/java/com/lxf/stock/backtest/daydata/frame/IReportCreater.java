package com.lxf.stock.backtest.daydata.frame;

import java.util.List;

import com.lxf.stock.backtest.daydata.domain.InvestRecord;

public interface IReportCreater {
	public void createReport(List<InvestRecord> listRecord);
}
