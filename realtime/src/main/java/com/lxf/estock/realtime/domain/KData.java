package com.lxf.estock.realtime.domain;

import java.util.ArrayList;
import java.util.List;

public class KData {
	private KDataEntry currentEntry;
	private List<KDataEntry> listEntry = new ArrayList<KDataEntry>();

	public KDataEntry getCurrentEntry() {
		return currentEntry;
	}

	public void setCurrentEntry(KDataEntry currentEntry) {
		this.currentEntry = currentEntry;
	}

	public List<KDataEntry> getListEntry() {
		return listEntry;
	}

	public void setListEntry(List<KDataEntry> listEntry) {
		this.listEntry = listEntry;
	}

}
