package com.lxf.stock.backtest.daydata.impl;

import java.util.List;

import com.lxf.stock.backtest.daydata.domain.InvestRecord;
import com.lxf.stock.backtest.daydata.domain.StockDayInfo;
import com.lxf.stock.backtest.daydata.domain.StockInfo;
import com.lxf.stock.backtest.daydata.frame.IStockPolicy;

/**
 * 联系下跌的侧率
 * 
 * @author LuXinFeng
 *
 */
public class StockPolicyFall implements IStockPolicy {
	private int nDayFall = 7; // 连续跌几天
	private int minSize = 15; // 最少交易天数
	private float fundRate = 0.0f;// minSize天内 流入流出资金比 ,这个好像没什么用

	private int maxHoldDay = 3; // 最多持有天数
	private float riseRate = 0.04f; // 预期收益率
	private float fallRate = 0.02f; // 止损率

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

		int nFall = 0;// 连续跌的天数
		for (int i = 0; i < historyDayInfoList.size(); i++) {
			int index = historyDayInfoList.size() - i - 1;
			StockDayInfo curDayInfo = historyDayInfoList.get(index);
			if (curDayInfo.getOpenPrice() > curDayInfo.getClosePrice()) { // 定义为跌
				nFall++;
			} else {
				break;
			}
		}
		if (nFall != nDayFall) {
			return false;
		}

		float fFallFund = 0; // 跌的金额
		float fRiseFund = 0; // 涨的金额
		for (int i = 0; i < minSize; i++) {
			int index = historyDayInfoList.size() - i - 1;
			StockDayInfo curDayInfo = historyDayInfoList.get(index);
			if (curDayInfo.getOpenPrice() > curDayInfo.getClosePrice()) { // 定义为跌
				fFallFund = fFallFund
						+ (curDayInfo.getClosePrice() - curDayInfo.getOpenPrice()) * curDayInfo.getVolume();
			} else {
				fRiseFund = fRiseFund
						- (curDayInfo.getClosePrice() - curDayInfo.getOpenPrice()) * curDayInfo.getVolume();
			}
		}
		if (fFallFund / fRiseFund < fundRate) {
			return false;
		}

		return true;
	}

	public void sale(String stockCode, InvestRecord investRecord, StockInfo info) {
		if("000662".equals(stockCode) && "2014-05-20".equals(investRecord.getInDate())){
			System.out.println(">>>");
		}
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
