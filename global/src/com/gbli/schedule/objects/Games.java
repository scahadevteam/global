package com.gbli.schedule.objects;

import com.gbli.connectors.GDatabase;
import com.gbli.context.ContextManager;

public class Games extends SchedObjectCollection {

	
	
	public Games (Season _se) {
		
		//
		// Who owns this
		//
		super.setParent(_se);
		
		GDatabase db = (GDatabase)ContextManager.getDatabase("GDatabase");
		db.getGames(this, _se);
		db.free();
	}

	@Override
	public String Dump() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n===================================================");
		sb.append("\nGame count:" + (getChildCount()-1));
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
