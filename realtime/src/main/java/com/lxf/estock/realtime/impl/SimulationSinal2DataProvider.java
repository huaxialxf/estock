package com.lxf.estock.realtime.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxf.estock.realtime.frame.BaseSinal2DataProvider;
import com.lxf.estock.realtime.utils.FileHandler;

public class SimulationSinal2DataProvider extends BaseSinal2DataProvider {
	private static Logger logger = LoggerFactory.getLogger(SimulationSinal2DataProvider.class);

	static int n = 0;

	@Override
	public void run() throws IOException {
		logger.info("启动 SimulationSinal2DataProvider");

		new FileHandler() {
			@Override
			public String doHandlerLine(String line) {
				SimulationSinal2DataProvider.super.delta(line);
//				n++;
//				if (n == 100) {
//					n = 0;
//					try {
//						Thread.sleep(1);
//					} catch (InterruptedException e) {
//					}
//				}
				return null;
			}
		}.doHandle("data", null);
		logger.info("结束 SimulationSinal2DataProvider");
	}

}
