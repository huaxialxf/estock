package com.lxf.estock.realtime.frame;

public interface IDataChangeListener {
	public void delta(String type,Object Message);
}
