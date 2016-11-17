package com.lxf.stock.backtest.daydata.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxf.stock.backtest.daydata.domain.InvestRecord;
import com.lxf.stock.backtest.daydata.domain.StockDayInfo;
import com.lxf.stock.backtest.daydata.domain.StockInfo;
import com.lxf.stock.backtest.daydata.frame.IBackTester;
import com.lxf.stock.backtest.daydata.frame.IDataProvider;
import com.lxf.stock.backtest.daydata.frame.IReportCreater;
import com.lxf.stock.backtest.daydata.frame.IStockPolicy;

public class BackTesterImpl implements IBackTester {
	private static Logger logger = LoggerFactory.getLogger(BackTesterImpl.class);
	private IDataProvider dataProvider;
	private IStockPolicy iStockPolicy;
	private IReportCreater reportCreater;

	private List<InvestRecord> listRecord = new ArrayList<InvestRecord>();

	public void start() {
		try {
			List<String> listCode = dataProvider.getStockList();
			for (String stockCode : listCode) {
				StockInfo stockInfo = dataProvider.getStockInfo(stockCode);
				List<StockDayInfo> listDayInfo = stockInfo.getListData();
				int index = 0;
				for (StockDayInfo stockDayInfo : listDayInfo) {
					index++;
					List<StockDayInfo> historyDayInfoList = listDayInfo.subList(0, index);
					if (stockDayInfo.getVolume() < 100) {
						continue;// 成交量太少，不能买入
					}
					boolean canBuy = iStockPolicy.buy(stockCode, stockDayInfo.getDate(), historyDayInfoList);
					if (canBuy) {
						InvestRecord investRecord = new InvestRecord();
						investRecord.setDayInfo(stockDayInfo);
						investRecord.setInDate(stockDayInfo.getDate());
						investRecord.setInPrice(stockDayInfo.getClosePrice());
						investRecord.setStockCode(stockCode);
						iStockPolicy.sale(stockCode, investRecord, stockInfo);
						String outDate = investRecord.getOutDate();
						if (outDate != null && !"".equals(outDate)) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							try {
								Date dOutDate = sdf.parse(outDate);
								Date dInDate = sdf.parse(investRecord.getInDate());
								if (!dInDate.before(dOutDate)) {
									logger.error(
											"不合理的交易 dInDate >= dOutDate  stockCode = {} , inDate = {} , outDate = {}",
											stockCode, investRecord.getInDate(), investRecord.getOutDate());
									throw new RuntimeException("策略不合理");
								}
							} catch (ParseException e) {
								logger.error("stockCode = {} , inDate = {} , outDate = {}", stockCode,
										investRecord.getInDate(), investRecord.getOutDate());
							}
						}
						listRecord.add(investRecord);
					}
				}
				reportCreater.createReport(listRecord);
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

	public IStockPolicy getiStockPolicy() {
		return iStockPolicy;
	}

	public void setiStockPolicy(IStockPolicy iStockPolicy) {
		this.iStockPolicy = iStockPolicy;
	}

	public IReportCreater getReportCreater() {
		return reportCreater;
	}

	public void setReportCreater(IReportCreater reportCreater) {
		this.reportCreater = reportCreater;
	}

	public IDataProvider getDataProvider() {
		return dataProvider;
	}

	public void setDataProvider(IDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

}
