package com.lxf.estock.realtime.frame;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class BaseSinal2DataProvider {

	public static final String MESSAGE_TYPE_QUOTATION = "quotation";
	public static final String MESSAGE_TYPE_TRANSACTION = "transaction";
	public static final String MESSAGE_TYPE_ORDERS = "orders";
	public static final String MESSAGE_TYPE_INFO = "info";
	public static final String MESSAGE_TYPE_OTHER = "other";

	protected Map<String, List<IDataChangeListener>> mapListerner;

	public Map<String, List<IDataChangeListener>> getMapListerner() {
		return mapListerner;
	}

	public void setMapListerner(Map<String, List<IDataChangeListener>> mapListerner) {
		this.mapListerner = mapListerner;
	}

	public abstract void run() throws IOException;

	public String getType(String message) {
		// 2cn_sz000002=万 科Ａ
		int index = message.indexOf("=");
		String header = message.substring(0, index);
		if (header.startsWith("2cn_")) {
			if (header.length() == 12) {
				return MESSAGE_TYPE_QUOTATION;
			} else if (header.endsWith("_0") || header.endsWith("_1")) {
				return MESSAGE_TYPE_TRANSACTION;
			} else if (header.endsWith("_orders")) {
				return MESSAGE_TYPE_ORDERS;
			} else if (header.endsWith("_i")) {
				return MESSAGE_TYPE_INFO;
			} else {
				throw new RuntimeException("接收到未知报文:" + message);
			}
		}
		return MESSAGE_TYPE_OTHER;

	}

	public void delta(String message) {
		String type = getType(message);
		List<IDataChangeListener> changeListeners = mapListerner.get(type);
		if(changeListeners==null){
			return ;
		}
		for (IDataChangeListener iDataChangeListener : changeListeners) {
			iDataChangeListener.delta(type, message);
		}
	}
}
