package com.lxf.estock.realtime;

import java.io.IOException;

import com.lxf.estock.realtime.frame.BaseSinal2DataProvider;
import com.lxf.estock.realtime.utils.BeanUtil;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws IOException {
		BaseSinal2DataProvider sinal2DataProvider = (BaseSinal2DataProvider) BeanUtil.getBean("Sinal2DataProvider");
		sinal2DataProvider.run();
	}
}
