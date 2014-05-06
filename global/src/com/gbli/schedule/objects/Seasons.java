/**
 * 
 */
package com.gbli.schedule.objects;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.gbli.connectors.GDatabase;
import com.gbli.context.ContextManager;

/**
 * @author David
 *
 */
public class Seasons extends SchedObjectCollection {

	/**
	 *  All the private static information here.
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
		
	public Seasons (boolean _init){
		
		//
		// ok.. first we have to truncate the rrtemplate table.
		// 
		GDatabase db = (GDatabase) ContextManager.getDatabase("GDatabase");
		LOGGER.info("Seasons: Starting Generating Season Collection.");
		db.getSeasonCollection(this, _init);
		db.free();
		LOGGER.info("Seasons: Done Generating Season Collection.");
		
	}
	
	public String Dump() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getKey());
		for (int i=0;i<this.getChildCount();i++) {
			sb.append("<br>\n" + this.getChildAtIndex(i).Dump());
		}
		return sb.toString();
	}
	
	/**
	 *  
	 * OK.. this is the meat of it.
	 * 
	 * We loop until we are done scheduling everything..
	 * 
	 */
	public void scheduleSeasons() {
		
		
		boolean keepgoing = true;
		
		// Step through the seasons..
		
		for (int i = 0;i < this.getChildCount();i++) {
			Season se = (Season)this.getChildAtIndex(i);
		
			if (se.getIsLocked() == 1) {
				LOGGER.info("SCHEDULING: SEASON IS LOCKED.. " + se);
				continue;
			}
			se.schedule(false);
		}
		for (int i = 0;i < this.getChildCount();i++) {
			Season se = (Season)this.getChildAtIndex(i);
			if (se.getIsLocked() == 1) {
				LOGGER.info("SCHEDULING: SEASON IS LOCKED.. " + se);
				continue;
			}
			se.schedule(true);
		}
		
		
			//
			// iok.. lets check overall games - exhibition games for each team in each season..
			/// we will loop on one season until a good matchup pops out..
			//
		while (keepgoing) {

			keepgoing = false;

		
			GDatabase db = (GDatabase) ContextManager.getDatabase("GDatabase"); 
			for (int i = 0;i < this.getChildCount();i++) {
				Season se = (Season)this.getChildAtIndex(i);
				if (se.getIsLocked() == 1) {
					LOGGER.info("SCHEDULING: SEASON IS LOCKED.. " + se);
					continue;
				}
				Participants parts = se.getParticipants();
				for (int y = 0;y < parts.getChildCount();y++) {
					Participant p = (Participant)parts.getChildAtIndex(y);
					Team tm = p.getTeam();
					tm.getTeamGameInfo().refreshInfo(db, se);
					LOGGER.info("Team Info:" + tm.getSName() + ": homec=" + tm.getTeamGameInfo().getHomeGames() + ", away=" + 
					tm.getTeamGameInfo().getAwayGames() + " , exh=" + tm.getTeamGameInfo().getExGames());
					int iagmax = ((tm.getTotalGames() / 2) + 2 + ((tm.getTotalGames() % 2 != 0 ? 1 : 0)));
					if (tm.isExhibitionTeam()) {
						// dont care
						// we are now dealing with all non exhibition teams
					
					} else if ((tm.getTotalGames() - tm.getTeamGameInfo().getExGames()) < 7) { 
//					} else if ((tm.getTotalGames()) < 3) { 
						LOGGER.info("Team Info:" + tm.getSName() + "not enought games.. try again...");
						db.resetGames(se);
						keepgoing = true;
						break;
					} else if (tm.getTeamGameInfo().getAwayGames() == 0  ) {
						LOGGER.info("Team Info:" + tm.getSName() + "no away games...");
						db.resetGames(se);
						keepgoing = true;
						break;
					} else if (tm.getTeamGameInfo().getAwayGames() > 5  ) {
						LOGGER.info("Team Info:" + tm.getSName() + "too many away games...");
						db.resetGames(se);
						keepgoing = true;
						break;
					}
				}	
				
				if (keepgoing) {
					
					se.schedule(false);
					se.schedule(true);

				}
					
			}
			db.free();
		}

		}
	}
