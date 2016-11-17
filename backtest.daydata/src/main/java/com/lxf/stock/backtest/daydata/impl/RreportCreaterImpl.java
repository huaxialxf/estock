package com.lxf.stock.backtest.daydata.impl;

import java.util.List;

import com.lxf.stock.backtest.daydata.domain.InvestRecord;
import com.lxf.stock.backtest.daydata.frame.IReportCreater;

public class RreportCreaterImpl implements IReportCreater {

	public void createReport(List<InvestRecord> listRecord) {
		int nSuccess = 0;
		int nFinish = 0;
		float profit = 0;
		for (InvestRecord investRecord : listRecord) {
			if ("".equals(investRecord.getOutDate()) || investRecord.getOutDate() == null) {

			}
			nFinish++;
			if (investRecord.getOutPrice() > investRecord.getInPrice()) {
				nSuccess++;
			}
			profit = profit + (investRecord.getOutPrice() - investRecord.getInPrice()) / investRecord.getInPrice();
		}
		System.out.println("投资次数:" + listRecord.size());
		System.out.println("未完成:" + (listRecord.size() - nFinish));
		System.out.println("成功次数:" + nSuccess);
		System.out.println("成功率:" + 100 * nSuccess / nFinish + "%");
		System.out.println("收益率:" + (100 * profit) + "%");

	}

}
