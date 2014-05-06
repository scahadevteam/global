package com.gbli.schedule.objects;

import com.gbli.connectors.GDatabase;
import com.gbli.context.ContextManager;

public class SeasonWeeks extends SchedObjectCollection {

	public SeasonWeeks(Season _se) {
		
		//
		// Who owns this
		//
		setParent(_se);
		
		GDatabase db = (GDatabase)ContextManager.getDatabase("GDatabase");
		db.getSeasonWeeksCollection(_se, this);
		db.free();
		
	}
	@Override
	public String Dump() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n===================================================");
		sb.append("\nSeason Weeks: count:" + (getChildCount()-1));
		sb.append("\n===================================================");
		for (int i=0;i<getChildCount();i++){
			sb.append("\n" + getChildAtIndex(i).Dump());
		}
		sb.append("\n===================================================");
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
