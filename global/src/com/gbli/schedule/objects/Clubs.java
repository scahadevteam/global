package com.gbli.schedule.objects;

import java.util.logging.Logger;

import com.gbli.connectors.GDatabase;
import com.gbli.context.ContextManager;

/**
 * Clubs
 * 
 * This holds all the active clubs we have in the system.
 * Its counterpart is Seasons...
 * 
 * Each Club will hold teams.. and ice slots
 * 
 * 
 * @author David
 *
 *
 */
public class Clubs extends SchedObjectCollection {

	/**
	 *  All the private static information here.
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
		
	public Clubs(boolean _init){
		
		//
		// ok.. first we have to truncate the rrtemplate table.
		// 
		GDatabase db = (GDatabase) ContextManager.getDatabase("GDatabase");
		LOGGER.info("Generating Club Collection...");
		db.getClubCollection(this, _init);
		db.free();
		LOGGER.info("Done Generating Season Collection...");
		
	}
	
	public String Dump() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getKey());
		for (int i=0;i<this.getChildCount();i++) {
			sb.append("\n" + this.getChildAtIndex(i).Dump());
		}
		return sb.toString();
	}
}
