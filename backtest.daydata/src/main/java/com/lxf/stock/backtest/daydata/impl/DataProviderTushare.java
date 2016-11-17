package com.lxf.stock.backtest.daydata.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lxf.stock.backtest.daydata.domain.StockDayInfo;
import com.lxf.stock.backtest.daydata.domain.StockInfo;
import com.lxf.stock.backtest.daydata.frame.IDataProvider;

public class DataProviderTushare implements IDataProvider {
	private String fileStockList;
	private String fileStockInfo;

	public String getFileStockList() {
		return fileStockList;
	}

	public void setFileStockList(String fileStockList) {
		this.fileStockList = fileStockList;
	}

	public String getFileStockInfo() {
		return fileStockInfo;
	}

	public void setFileStockInfo(String fileStockInfo) {
		this.fileStockInfo = fileStockInfo;
	}

	List<String> listCode = new ArrayList<String>();

	public synchronized List<String> _getStockList() throws IOException {
		if (listCode.size() == 0) {
			BufferedReader reader = null;
			String line;
			try {
				reader = new BufferedReader(new FileReader(fileStockList));
				line = reader.readLine();
				while ((line = reader.readLine()) != null) {
					String arr[] = line.split(",");
					String code = arr[0];
					listCode.add(code);
				}
				reader.close();
			} catch (IOException e) {
				throw e;
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
				}
			}
		}
		return listCode;
	}

	private Map<String, StockInfo> map = new HashMap<String, StockInfo>();

	public synchronized void loadData() throws IOException {
		if (map.size() == 0) {
			File file = new File(fileStockInfo);
			File[] files = file.listFiles();
			for (File subFile : files) {
				if (subFile.getName().endsWith(".csv")) {
					String stockCode = subFile.getName().substring(0, subFile.getName().length() - ".csv".length());
					StockInfo stockInfo = parseStockFile(stockCode, subFile);
					map.put(stockCode, stockInfo);
					listCode.add(stockCode);
				}
			}
		}
	}

	private StockInfo parseStockFile(String stockCode, File stockFile) throws IOException {
		StockInfo stockInfo = new StockInfo();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(stockFile));
			String line = reader.readLine();
			line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				StockDayInfo dayInfo = parseStockDayInfo(line);
				stockInfo.getListData().add(0,dayInfo);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}

		return stockInfo;

	}

	private StockDayInfo parseStockDayInfo(String line) {
		StockDayInfo dayInfo = new StockDayInfo();
		String[] arr = line.split(",");
		dayInfo.setDate(arr[0]);
		dayInfo.setOpenPrice(Float.parseFloat(arr[1]));// 开盘价
		dayInfo.setHightPrice(Float.parseFloat(arr[2]));// 最高价
		dayInfo.setClosePrice(Float.parseFloat(arr[3]));// 收盘价
		dayInfo.setLowPrice(Float.parseFloat(arr[4]));// 最低价
		dayInfo.setVolume(Float.parseFloat(arr[5]));// 成交量
		return dayInfo;
	}

	public StockInfo getStockInfo(String stockCode) throws IOException {
		loadData();
		return map.get(stockCode);
	}

	public List<String> getStockList() throws IOException {
		loadData();
		return listCode;
	}

	public static void main(String[] args) throws IOException {
		DataProviderTushare dataProvider = new DataProviderTushare();
		dataProvider.setFileStockInfo("data/test/2016-11-13");
		List<String> listCode = dataProvider.getStockList();
		StockInfo info = dataProvider.getStockInfo("000001");
		System.out.println(listCode);
	}

}
