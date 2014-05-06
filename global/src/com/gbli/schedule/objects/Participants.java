package com.gbli.schedule.objects;

import com.gbli.connectors.GDatabase;
import com.gbli.context.ContextManager;


/**
 * Participants - Holds all the participants of a given season
 * @author David
 *
 */
public class Participants extends SchedObjectCollection {
	
	public Participants (Season _se) {
		this.setParent(_se);
		GDatabase db = (GDatabase)ContextManager.getDatabase("GDatabase");
		db.getParticipantCollection(_se, this);
		db.free();
		
	}

	/**
	 * Holds a list of simple participants
	*/
	public Participants() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String Dump() {
		StringBuffer sb = new StringBuffer();
		sb.append("<br>===================================================");
		sb.append("<br>Participants for the season: count:" + (getChildCount()-1));
		sb.append("<br>===================================================");
		for (int i=0;i<getChildCount();i++){
			sb.append("<br>" + getChildAtIndex(i).toString());
		}
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
