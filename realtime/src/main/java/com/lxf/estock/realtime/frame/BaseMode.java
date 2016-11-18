package com.lxf.estock.realtime.frame;

import java.util.List;
import java.util.Map;

public abstract class BaseMode implements IDataChangeListener {
	protected List<IDataChangeListener> listerners;

	public List<IDataChangeListener> getListerners() {
		return listerners;
	}

	public void setListerners(List<IDataChangeListener> listerners) {
		this.listerners = listerners;
	}
	
	
}
