package com.gbli.schedule.objects;

import java.util.HashMap;
import java.util.Vector;

public abstract class SchedObjectCollection extends SchedObject {

	 
	 Vector<SchedObject> vct = new Vector<SchedObject>();
	 HashMap<String, SchedObject> hm = new HashMap<String, SchedObject>();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void addChild(SchedObject _so) {
		vct.addElement(_so);
		hm.put(_so.getKey(),_so);
	}
	
	public void remove(SchedObject _so) {
		vct.remove(_so);
		hm.remove(_so.getKey());
	}
	public int getChildCount() {
		return vct.size();
	}
	
	public SchedObject getChildAtIndex(int _i) {
		return vct.get(_i);
	}

	public SchedObject getChildAtID(int _i) {
		return hm.get(_i + "");
	}
}
