package com.lxf.stock.backtest.daydata.impl;

import java.util.List;

import com.lxf.stock.backtest.daydata.domain.InvestRecord;
import com.lxf.stock.backtest.daydata.domain.StockDayInfo;
import com.lxf.stock.backtest.daydata.domain.StockInfo;
import com.lxf.stock.backtest.daydata.frame.IStockPolicy;

/**
 * 聚宝盆策略
 * 
 * @author LuXinFeng
 *
 */
public class StockPolicyJbp implements IStockPolicy {
	private int nDayFall = 7; // 连续跌几天
	private int minSize = 30; // 最少交易天数
	private float fundRate = 0.0f;// minSize天内 流入流出资金比 ,这个好像没什么用

	private int maxHoldDay = 20; // 最多持有天数
	private float riseRate = 0.08f; // 预期收益率
	private float fallRate = 0.05f; // 止损率

	/**
	 * 连续跌7天，买入
	 * 
	 */
	public boolean buy(String stockCode, String curDay, List<StockDayInfo> historyDayInfoList) {
		if (historyDayInfoList.size() <= minSize) {// 交易天数太少，可能是新股
			return false;
		}
		StockDayInfo dayInfo = historyDayInfoList.get(historyDayInfoList.size() - 1);
		if (!curDay.equals(dayInfo.getDate())) {
			throw new RuntimeException("买入环境不正常:{" + stockCode + "|" + curDay + "|" + dayInfo.getDate() + "}");
		}
		float totalPrice = 0;
		float totalVolume = 0;
		float maxPrice = 0;
		float minPrice = 10000;
		float maxVolume = 0;
		float minVolume = 10000;
		float preVolume = 0;

		for (int i = 1; i < minSize; i++) {
			int index = historyDayInfoList.size() - i - 1;
			StockDayInfo curDayInfo = historyDayInfoList.get(index);
			totalPrice = totalPrice + curDayInfo.getClosePrice();
			totalVolume = totalVolume + curDayInfo.getVolume();
			maxPrice = Math.max(curDayInfo.getHightPrice(), maxPrice);
			minPrice = Math.min(curDayInfo.getLowPrice(), minPrice);
			maxVolume = Math.max(curDayInfo.getVolume(), maxVolume);
			minPrice = Math.min(curDayInfo.getVolume(), minVolume);
			if (i == 1)
				preVolume = curDayInfo.getVolume();
		}
		float avePrice = totalPrice / (minSize - 1);
		float aveVolume = totalVolume / (minSize - 1);

		if (minPrice < avePrice * 0.97 || maxPrice > avePrice * 1.03) {
			return false;
		}

//		if (minVolume < aveVolume * (1 - 0.6) || maxVolume > aveVolume * (1 + 1.5)) {
//			return false;
//		}
		// dayInfo.getVolume() > aveVolume * 3 &&

		// && dayInfo.getClosePrice() > avePrice * 1.03
		if (dayInfo.getVolume() > 2 * preVolume && dayInfo.getHightPrice() < dayInfo.getLowPrice() * 1.04  & dayInfo.getVolume() > aveVolume * 2 
				&& dayInfo.getClosePrice() > dayInfo.getOpenPrice() * 1.01) {
			if ("2016-10-18".equals(dayInfo.getDate()) && stockCode.endsWith("000999")) {
				System.out.println(">>");
			}
			return true;
		}
		return false;

	}

	public void sale(String stockCode, InvestRecord investRecord, StockInfo info) {
		int index = info.getListData().indexOf(investRecord.getDayInfo());
		for (int i = 0; i < maxHoldDay; i++) {
			if (index + i + 1 >= info.getListData().size()) {
				return;// 投资未结束
			}
			StockDayInfo dayInfo = info.getListData().get(index + i + 1);
			float inPrice = investRecord.getInPrice();

			// 假定先出现最低价格
			if ((inPrice - dayInfo.getLowPrice()) / inPrice >= fallRate) { // 止损收益
				investRecord.setHoldDays(i + 1);
				investRecord.setOutDate(dayInfo.getDate());
				investRecord.setOutPrice(inPrice * (1 - fallRate));
				return;
			}

			if ((dayInfo.getHightPrice() - inPrice) / inPrice >= riseRate) { // 达到预期收益
				investRecord.setHoldDays(i + 1);
				investRecord.setOutDate(dayInfo.getDate());
				investRecord.setOutPrice(inPrice * (1 + riseRate));
				return;
			}

		}

		// 时间太长，收盘价结束
		StockDayInfo dayInfo = info.getListData().get(index + maxHoldDay);
		investRecord.setHoldDays(maxHoldDay);
		investRecord.setOutDate(dayInfo.getDate());
		investRecord.setOutPrice(dayInfo.getClosePrice());
	}

}
