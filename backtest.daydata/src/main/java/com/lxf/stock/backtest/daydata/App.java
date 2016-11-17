package com.lxf.stock.backtest.daydata;

import com.lxf.stock.backtest.daydata.frame.IBackTester;
import com.lxf.stock.utils.BeanUtil;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		IBackTester backTester = (IBackTester) BeanUtil.getBean("BackTester");
		backTester.start();
	}
}
