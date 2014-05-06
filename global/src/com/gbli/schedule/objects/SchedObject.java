package com.gbli.schedule.objects;

public abstract class SchedObject {
	
	int ID = 0;
	SchedObject parent = null;

	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getKey() {
		return ID + "";
	}
	
	/**
	 * Sets the parent of the object.
	 * @param _so
	 */
	public final void setParent(SchedObject _so) {
		parent = _so;
	}
	
	public final SchedObject getParent() {
		return parent;
	}
	
	public abstract String Dump();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
}
