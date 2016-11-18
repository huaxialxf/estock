package com.lxf.stock.backtest.daydata.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.lxf.stock.backtest.daydata.domain.InvestRecord;
import com.lxf.stock.backtest.daydata.frame.IReportCreater;

public class RreportCreaterImpl implements IReportCreater {

	public boolean _createReport(List<InvestRecord> listRecord) {
		int nSuccess = 0;
		int nFinish = 0;
		float profit = 0;
		for (InvestRecord investRecord : listRecord) {
			if ("".equals(investRecord.getOutDate()) || investRecord.getOutDate() == null) {
				continue;
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
		System.out.println("成功率:" + 100.0f * nSuccess / nFinish + "%");
		System.out.println("收益率:" + (100 * profit) + "%");
		return profit > 0;
	}

	private Map<String, List<InvestRecord>> getListRecordMap(List<InvestRecord> listRecord) {
		Map<String, List<InvestRecord>> map = new LinkedHashMap<String, List<InvestRecord>>();
		for (InvestRecord investRecord : listRecord) {
			String key = investRecord.getInDate().substring(0, "2010".length());
			List<InvestRecord> list = map.get(key);
			if (list == null) {
				list = new ArrayList<InvestRecord>();
				map.put(key, list);
			}
			list.add(investRecord);
		}
		return map;
	}

	public void createReport(List<InvestRecord> listRecord) {
		
		
		boolean isRise = _createReport(listRecord);
		if (!isRise) {
			System.out.println("报表 :所有 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>亏损\n");
		} else {
			System.out.println("报表 :所有 \n");
		}

		Map<String, List<InvestRecord>> map = getListRecordMap(listRecord);
		List<String> listKey = new ArrayList<String>();
		for (String key : map.keySet()) {
			listKey.add(key);
		}
		Collections.sort(listKey);
		int nFall = 0;
		for (String key : listKey) {
			isRise = _createReport(map.get(key));
			if (!isRise) {
				nFall++;
				System.out.println(
						"报表 :" + key + " >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>亏损\n");
			} else {
				System.out.println("报表 :" + key + "\n");
			}
		}
		System.out.println("亏损次数:" + nFall + "              周期亏损率:" + 100.0f * nFall / map.size() + "%");
		
		for (InvestRecord investRecord : listRecord) {
			if(investRecord.getOutPrice()<investRecord.getInPrice() && investRecord.getInDate().startsWith("2016")){
				System.out.println(investRecord.getStockCode() +" == " +investRecord.getInDate() +"=="+investRecord.getOutDate());
			}
		}
	}

}
