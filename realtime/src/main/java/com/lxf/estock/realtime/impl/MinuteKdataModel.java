package com.lxf.estock.realtime.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxf.estock.realtime.domain.KData;
import com.lxf.estock.realtime.domain.KDataEntry;
import com.lxf.estock.realtime.frame.BaseMode;
import com.lxf.estock.realtime.frame.IDataChangeListener;

public class MinuteKdataModel extends BaseMode {
	private static Logger logger = LoggerFactory.getLogger(MinuteKdataModel.class);

	private Map<String, KData> mapKData = new HashMap<String, KData>();
	private Queue<String> queue = new LinkedBlockingQueue<String>();

	public void delta(String type, Object message) {
		if (!queue.offer((String) message)) {
			System.err.println("入队列失败:" + message);
		}
	}

	public void start() {
		Thread t = new Thread() {
			public void run() {
				_start();
			};
		};
		t.setName("MinuteKdataModel");
		t.start();
		Thread monitor = new Thread() {
			public void run() {
				while (true) {
					logger.info("queue size:" + queue.size());
					try {
						Thread.sleep(1000 * 5);
					} catch (InterruptedException e) {
					}
				}
			};
		};
		monitor.setName("MinuteKdataModel_Monitor");
		monitor.start();
		Thread outFile = new Thread() {
			public void run() {
				Scanner scanner = new Scanner(System.in);
				System.err.println("回车导入KData:");
				String line = scanner.nextLine();
				for (String stockCode : mapKData.keySet()) {
					KData data = mapKData.get(stockCode);
					try {
						FileOutputStream fos = new FileOutputStream(new File("kdata/" + stockCode + ".txt"));

						fos.write("data,lastClosePrice,openPrice,closePrice,hightPrice,lowPrice,volume\n".getBytes());
						for (KDataEntry dataEntry : data.getListEntry()) {
							fos.write(dataEntry.toCsv().getBytes());
							fos.write("\n".getBytes());
						}
						fos.write("cur======".getBytes());
						fos.write(data.getCurrentEntry().toCsv().getBytes());
						fos.write("\n".getBytes());
						fos.close();

					} catch (Exception e) {
						e.printStackTrace();
					}
					System.err.println("导出KData:" + stockCode);
				}
			};
		};
		outFile.setName("MinuteKdataModel_outFile");
		outFile.start();
	}

	public void _start() {
		logger.debug("启动 MinuteKdataModel");
		while (true) {
			String info = queue.poll();
			if (info != null) {
				String symbol = info.substring(7, 12);
				KData kData = mapKData.get(symbol);
				KDataEntry dataEntry = parseKDataEntry(info.substring(13));
				if (!"PZ".equals(dataEntry.getStatus())) {
					continue;
				}
				String minute = dataEntry.getDate().substring(0, 5);
				if (kData == null) {// 第一笔记录
					kData = new KData();
					KDataEntry currentEntry = new KDataEntry();
					kData.setCurrentEntry(currentEntry);
					currentEntry.setDate(minute);
					currentEntry.setLastClosePrice(dataEntry.getLastClosePrice());
					currentEntry.setOpenPrice(dataEntry.getOpenPrice());
					currentEntry.setHightPrice(dataEntry.getHightPrice());
					currentEntry.setLowPrice(dataEntry.getLowPrice());
					currentEntry.setClosePrice(dataEntry.getClosePrice());// 现价
					currentEntry.setTransactionCount(dataEntry.getTransactionCount());// 笔数
					currentEntry.setTotalVolume(dataEntry.getTotalVolume());// 数量
					currentEntry.setTotalAmount(dataEntry.getTotalAmount());// 金额
					mapKData.put(symbol, kData);
					notifyModifyEntry(dataEntry);
					continue;
				}
				KDataEntry currentEntry = kData.getCurrentEntry();

				if (currentEntry.getDate().equals(minute)) {// 当前分钟
					// currentEntry.setDate(minute);
					// currentEntry.setLastClosePrice(dataEntry.getLastClosePrice());
					// currentEntry.setOpenPrice(dataEntry.getOpenPrice());
					currentEntry.setHightPrice(Math.max(currentEntry.getHightPrice(), dataEntry.getHightPrice()));
					currentEntry.setLowPrice(Math.min(currentEntry.getLowPrice(), dataEntry.getLowPrice()));
					currentEntry.setClosePrice(dataEntry.getClosePrice());// 现价

					currentEntry
							.setTransactionCount(currentEntry.getTransactionCount() + dataEntry.getTransactionCount());// 笔数
					currentEntry.setTotalVolume(currentEntry.getTotalVolume() + dataEntry.getTotalVolume());// 数量
					currentEntry.setTotalAmount(currentEntry.getTotalAmount() + dataEntry.getTotalAmount());// 金额

					notifyModifyEntry(dataEntry);
				} else {// 新的一分钟来了
					addNewEntryForKData(kData);
					notifyAddEntry(currentEntry);
					// 下面逻辑跟第一条是一样的。
					currentEntry.setDate(minute);
					currentEntry.setLastClosePrice(dataEntry.getLastClosePrice());
					currentEntry.setOpenPrice(dataEntry.getOpenPrice());
					currentEntry.setHightPrice(dataEntry.getHightPrice());

					currentEntry.setLowPrice(dataEntry.getLowPrice());
					currentEntry.setClosePrice(dataEntry.getClosePrice());// 现价

					currentEntry.setTransactionCount(dataEntry.getTransactionCount());// 笔数
					currentEntry.setTotalVolume(dataEntry.getTotalVolume());// 数量
					currentEntry.setTotalAmount(dataEntry.getTotalAmount());// 金额
					notifyModifyEntry(dataEntry);
				}
			}
			// try {
			// Thread.sleep(1);
			// } catch (InterruptedException e) {
			// }
		}
	}

	public void notifyModifyEntry(KDataEntry dataEntry) {
		for (IDataChangeListener iDataChangeListener : listerners) {
			iDataChangeListener.delta("modifyEntry", dataEntry);
		}
	}

	public void notifyAddEntry(KDataEntry dataEntry) {
		for (IDataChangeListener iDataChangeListener : listerners) {
			iDataChangeListener.delta("addEntry", dataEntry);
		}
	}

	public KDataEntry parseKDataEntry(String line) {
		String[] arr = line.split(",");
		KDataEntry dataEntry = new KDataEntry();
		dataEntry.setDate(arr[1]);
		dataEntry.setLastClosePrice(Float.parseFloat(arr[3]));
		dataEntry.setOpenPrice(Float.parseFloat(arr[4]));
		dataEntry.setHightPrice(Float.parseFloat(arr[5]));
		dataEntry.setLowPrice(Float.parseFloat(arr[6]));
		dataEntry.setClosePrice(Float.parseFloat(arr[7]));// 现价
		dataEntry.setStatus(arr[8]);
		dataEntry.setTransactionCount(Float.parseFloat(arr[9]));//
		dataEntry.setTotalVolume(Float.parseFloat(arr[10]));//
		dataEntry.setTotalAmount(Float.parseFloat(arr[11]));//
		return dataEntry;
	}

	public void addNewEntryForKData(KData kData) {
		KDataEntry newDataEntry = new KDataEntry();
		newDataEntry.setDate(kData.getCurrentEntry().getDate());
		newDataEntry.setLastClosePrice(kData.getCurrentEntry().getLastClosePrice());
		newDataEntry.setOpenPrice(kData.getCurrentEntry().getOpenPrice());
		newDataEntry.setHightPrice(kData.getCurrentEntry().getHightPrice());
		newDataEntry.setLowPrice(kData.getCurrentEntry().getLowPrice());
		newDataEntry.setClosePrice(kData.getCurrentEntry().getClosePrice());// 现价

		newDataEntry.setTransactionCount(kData.getCurrentEntry().getTransactionCount());// 笔数
		newDataEntry.setTotalVolume(kData.getCurrentEntry().getTotalVolume());// 数量
		newDataEntry.setTotalAmount(kData.getCurrentEntry().getTotalAmount());// 金额
		kData.getListEntry().add(newDataEntry);

	}
}
