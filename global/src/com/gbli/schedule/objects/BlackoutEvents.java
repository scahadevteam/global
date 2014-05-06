package com.gbli.schedule.objects;

import com.gbli.connectors.GDatabase;
import com.gbli.context.ContextManager;

public class BlackoutEvents extends SchedObjectCollection {

	public BlackoutEvents(Club _cl) {
		
		setParent(_cl);
		
	}
	@Override
	public String Dump() {
		return "";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
