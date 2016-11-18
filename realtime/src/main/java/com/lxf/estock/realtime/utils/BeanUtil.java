package com.lxf.estock.realtime.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class BeanUtil {
	private ApplicationContext context = null;
	private static BeanUtil one;

	private BeanUtil() {
		File fileSpring = new File("spring");
		if (fileSpring.exists()) {
			File[] files = fileSpring.listFiles();

			context = new FileSystemXmlApplicationContext();
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();
				if (fileName.startsWith("applicationContext") && fileName.endsWith(".xml")) {
					list.add("spring/" + files[i].getName());
				}
			}
			if (list.size() > 0) {
				context = new FileSystemXmlApplicationContext(list.toArray(new String[0]));
			}
		}
	}

	public static BeanUtil getOne() {
		if (one == null) {
			one = new BeanUtil();
		}
		return one;
	}

	public static Object getBean(String name) {
		return getOne().context.getBean(name);
	}
}
