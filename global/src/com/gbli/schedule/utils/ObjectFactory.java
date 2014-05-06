/**
 * 
 */
package com.gbli.schedule.utils;

import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.GDatabase;
import com.gbli.context.ContextManager;
import com.gbli.schedule.objects.Season;
import com.gbli.schedule.objects.Seasons;

/**
 * @author David
 * 
 * This nifty routine simply recreates the round robin table in the given database
 * to provide game matchups for each set of scenarios (teams in a division)
 *
 */
public class ObjectFactory {

	private final static Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	/**
	 * genTemplate - 
	 * @param _iSize - the number of teams in the set
	 */
	public static void genTemplate(int _iSetSize, GDatabase _db) {
		
		for (int i= 1;i<=_iSetSize-1;i++) {
			for (int y = i+1;y<=_iSetSize;y++) {
				LOGGER.info("Team Pairing: Team-" + i + " vs " + y);
				_db.insertRRTemplateRow(_iSetSize,i,y);
			}
		}
		
	}
	
	public static void genTemplateSet(int _iRange) {
		
		//
		// ok.. first we have to truncate the rrtemplate table.
		// 
		GDatabase db = (GDatabase) ContextManager.getDatabase("GDatabase");
		LOGGER.info("Attempting to reset RR Template In Database...");
		if (db.truncateRRData()) {
			for (int i=1;i<=_iRange;i++) {
				ObjectFactory.genTemplate(i,db);
			}
		}
		db.free();
		LOGGER.info("done resetting RR Template In Database...");

	}
	
public static void syncTeamsToSeason(Season _se) {
		
		
		//
		// ok..lets remove teams from a season that do not belong
	    // and add teams to a season that should be there.
		// 
		// if everything is copesetic.. then we are ok..
	    // 
	    // if we have to make adjustments here.. all the games have to 
	    // be reset
		GDatabase db = (GDatabase) ContextManager.getDatabase("GDatabase");
		LOGGER.info("Attempting to reset RR Template In Database...");
		
		
		if (db.getTeamSeasonMissmatches(_se)) {
			try {
				while (db.getResultSet().next()) {
					LOGGER.info(db.getResultSet().getString(1));
					LOGGER.info(db.getResultSet().getInt(2)+ "");
				}
				db.getResultSet().close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		db.free();
	
	}
		

}
