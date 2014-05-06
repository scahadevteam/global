package com.gbli.schedule.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.gbli.connectors.GDatabase;
import com.gbli.context.ContextManager;

/**
 * Season
 * 
 * This represents a given season... 
 * 
 * @author David
 *
 */
public class Club extends SchedObject {

	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	

	private Teams tms = null;
	//private Slots sl = null;
	
	private String Name = null;
	private String SName = null;
	private int TeamCount = 0;
	private int IsByeClub = 0;
	private int SlotsPerWeek = 0;
	
	/**
	 * A Club has:
	 * 	Teams
	 * 	Slots
	 * 	Venues
	 * @param _id
	 * @param _sToken
	 */
	public Club (int _id) {
		this.setID(_id);
	}
	
	/**
	 * This will initialize the body of the Club and fill out all the teams in that club 
	 * We need 
	 * Teams
	 * Slots
	 * 
	 */
	public final boolean init() {
		
		//
		// quality check starts here.
		//
		
		//
		// Lets make sure there are no gaps in the numbering of the Participants 
		// in the season.
		getClubBody();
		syncSlotsToClub();
		
		getTeams();
		return true;
				
		
	}
	
	/**
	 * This little guy will rip through all the seasons that a club participates in and will generate all the slots that
	 * are missing.  There are generic slots that do not care about blackout weekends of clubs, etc.  The slots that get 
	 * generated from here can be played any time.. at any venue.  The scheduling engine does this for them.
	 * 
	 */
	private void syncSlotsToClub() {
	
		LOGGER.info("syncSlotsToClub: Reviewing slot requirements for Club:" + this.getSName());
		GDatabase db = (GDatabase) ContextManager.getDatabase("GDatabase");
		GDatabase db2 = (GDatabase) ContextManager.getDatabase("GDatabase");
		//
		// lets get the information for the outside loop now..
		//
		ArrayList<String> dates = db.getWeekendsForAllSeasons();
		
		//
		// Lets remove too early and too late
		//
		String sEarliestDate = dates.get(0);
		String sLatestDate = dates.get(dates.size() -1);
		db2.removeEarlySlots(this, sEarliestDate);
		db2.removeLateSlots(this, sLatestDate);
		db2.cleanup();

		for (String date : dates) {
			
			 // first .. lets remove all the slots that are there prior to any scheduling
			 // 
			 ResultSet rs = db.getSlotTeamplate(this, date);
			 if (rs != null) {
				 try {
					while (rs.next()) {
						int i=1;
						String sFromDate = rs.getString(i++);
						String sToDate = rs.getString(i++);
						String sGameLen = rs.getString(i++);
						int iSlotCount = rs.getInt(i++);
						LOGGER.info("syncSlotsToClub: Slot req for club " + this.getSName() + " are sc=" + iSlotCount + ". fdate=" + sFromDate + ". todate=" + sToDate + ". gl=" + sGameLen);
						
						//
						// Ok.. here we do the following:
						// 
						
						//  later.. we need to eliminate any extra slots that are "unused".. if any
						//  if we have extra slots that are used.. we have to renumber them so they are sequential.
						//     this is important so we do not have any gaps..
						//  this will happen if teams get deleted and we need less slots for that particular weekend.
						// 
						for (int c = 1;c<=iSlotCount;c++) {
							db2.synchSlot(this,c,sFromDate,sToDate,sGameLen);
						}
						db2.cleanup();
						//
						// ok.. for the given week.. we want to remove any slots that exist that are above and beyond
						// andy count
						db2.removeExcessSlots(this,sFromDate, sToDate, sGameLen,iSlotCount);
						db2.cleanup();
						
					}
					
				} catch (SQLException e) {
				
					e.printStackTrace();
				} finally {
					db.cleanup();
				}
			 }
			 
		
		}
		db.free();
		 //
		// ok.. now we have to look to see if we can take our generic slots and see if we can assign any club slots to
		// give them real meaning
		//
		// The club could have recently added club slots to make up for their short commings
		// 
		db.synchClubSlots(this);  // Match them up based upon date and unassigned
		db.fillMisfitSlots(this, "1.5"); // take any extra club provided slots and force them into an open gen slot
		db.fillMisfitSlots(this, "1.25"); // take any extra club provided slots and force them into an open gen slot
		db.createBonusSlots(this); // Generate bonus slots
		db2.free();
		LOGGER.info("syncSlotsToClub: Done reviewing slot requirements for Club:" + this.getSName());
		
	}

	/**
	 * This gets all the teams for a given Club
	 */
	private void getTeams() {

		GDatabase db = (GDatabase) ContextManager.getDatabase("GDatabase");
		db.free();
		
	}
	
	
	/**
	 * Once we quality check everything about a Club.. we can then go get the attributes
	 * 
	 */
	private void getClubBody() {

		GDatabase db = (GDatabase) ContextManager.getDatabase("GDatabase");
		db.free();
		//
		// Report out the detail..
		//
		LOGGER.info(this.toString());

	}
	

	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
	}



	/**
	 * @return the isByeClub
	 */
	public int getIsByeClub() {
		return IsByeClub;
	}

	/**
	 * @param isByeClub the isByeClub to set
	 */
	public void setIsByeClub(int isByeClub) {
		IsByeClub = isByeClub;
	}

	/**
	 * @return the Name
	 */
	public String getSName() {
		return SName;
	}



	/**
	 * @param Name the Name to set
	 */
	public void setSName(String sName) {
		SName = sName;
	}



	/**
	 * @return the slotsPerWeek
	 */
	public int getSlotsPerWeek() {
		return SlotsPerWeek;
	}

	/**
	 * @param slotsPerWeek the slotsPerWeek to set
	 */
	public void setSlotsPerWeek(int slotsPerWeek) {
		SlotsPerWeek = slotsPerWeek;
	}

	/**
	 * @return the teamCount
	 */
	public int getTeamCount() {
		return TeamCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Club [Name=" + Name + ", SName=" + SName + ", TeamCount="
				+ TeamCount + ", SlotsPerWeek=" + SlotsPerWeek + "]";
	}

	/**
	 * @param teamCount the teamCount to set
	 */
	public void setTeamCount(int teamCount) {
		TeamCount = teamCount;
	}

	
	public String Dump() {
		StringBuffer sb = new StringBuffer();
		sb.append(toString());
		return sb.toString();
	}

}
