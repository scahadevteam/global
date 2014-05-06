package com.gbli.schedule.objects;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.GDatabase;
import com.gbli.context.ContextManager;
import com.gbli.schedule.utils.ObjectFactory;

/**
 * Season
 * 
 * This represents a given season... 
 * 
 * @author David
 *
 */
public class Season extends SchedObject {

	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private Participants parts = null;
	private SeasonWeeks sw = null;
	private Games gms = null;
	
	private String DivSName = null;
	private String Name = null;
	private String SName = null;
	private String SeasonWeeksTag = null;
	private int GameCount = 0;
	private int TeamCount = 0;
	private int ByeTeamCount = 0;
	private int IsLocked = 0;
	/**
	 * @param isLocked the isLocked to set
	 */
	public void setIsLocked(int isLocked) {
		IsLocked = isLocked;
	}

	/**
	 * @return the isLocked
	 */
	public int getIsLocked() {
		return IsLocked;
	}

	private String GameLenth = null;
	private int PlayOnce = 0;
	private int MaxByeCount = 99;
	private int MinGameCount = 99;
	/**
	 * @return the minGameCount
	 */
	public int getMinGameCount() {
		return MinGameCount;
	}

	/**
	 * @param minGameCount the minGameCount to set
	 */
	public void setMinGameCount(int minGameCount) {
		MinGameCount = minGameCount;
	}

	private String StartDate = null;
	private String EndDate = null;
	
	//
	//  Here Each Season Has a collection of Weekends
	// 
	//  Each Season Has a collection of Participants
	//     Inside of each Participate in a Team
	// 	   Inside of each Team is a Club..
	//
	public Season (int _id) {
		this.setID(_id);
	}
	
	/**
	 * This will initialize the body of the Schedule and pull in 
	 * Participants
	 * SeasonWeeks
	 * and Games
	 * 
	 * it will then check to see if everything is ready to go for the season.
	 * it will check to make sure the games are created 
	 * that all the teams that say they want to be in the season are
	 * participants.  
	 * 
	 * and so on
	 */
	public final boolean init() {
		//
		// quality check starts here.
		//

		//
		// Lets make sure there are no gaps in the numbering of the Participants 
		// in the season.
		
		syncTeamsToSeason();
		syncTeamSeeding();


		// here we get all the information loaded once we get all the teams synced properly
		getSeasonBody();
		
		//
		// now lets gen any needed games..
		genGames();

		//gms = new Games(this);
		
		
		return true;
				
		
	}
	
	/**
	 * Once we quality check everything about a season.. we can then go get the attributes
	 * 
	 */
	private void getSeasonBody() {

		GDatabase db = (GDatabase) ContextManager.getDatabase("GDatabase");
		db.getObjectBody(this);
		db.free();
		

	}
	
	/**
	 * genGames
	 * 
	 * Here we want to make sure that once we clean up all the teams and rank them properly.. Let ensure we have all the games
	 * accounted for.
	 * 
	 */
	private void genGames() {
		GDatabase db = (GDatabase) ContextManager.getDatabase("GDatabase");
		db.genGames(this);
		db.free();
		
	}

	/**
	 * This will make sure all the teams are seeded in sequential order
	 * And if Not.. it will attempt to fix it.
	 * If there is an even number then we have two bye teams
	 * If there is an odd number.. we will have one bye team
	 * 
	 * Because of the key structuring of the database.. 
	 * we will not have two teams ranked the same for a given season.
	 * and we will not have the same team is the season twice..
	 * 
	 * But we can have a gap in the rankings.. this makes sure we do not have that
	 * 
	 * 
	 * @return
	 */
	private boolean syncTeamSeeding() {
		
		LOGGER.info("sychTeamSeeding: Reviewing rankings to ensure they are sequential for season " + this.getName());

		GDatabase db = (GDatabase) ContextManager.getDatabase("GDatabase");
		boolean bwork = true;
		while (bwork) {
			
			//
			// the last ranking (seeded) team in this season..
			//
			int itargetrank = 0;
			int ikey = 0;
			//
			// lets assume everything is ok
			//
			bwork = false;
			if (db.getTeamSeeding(this)) {
				int irank = 1;
				int ioldrank = 0;
				try {
					while (db.getResultSet().next()) {
						ioldrank = db.getResultSet().getInt(2);
						if (irank != ioldrank && itargetrank == 0 ) {
							itargetrank = irank;
							LOGGER.info("syncTeamSeeding: Found a ranking gap in season " + getName() + ". " + db.getResultSet().getInt(1) + ":" + db.getResultSet().getInt(2) + ": should be:" + irank);
							bwork = true;
						}
						ikey = db.getResultSet().getInt(1);
						irank++;
						
					}
					db.cleanup();
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//
				// now lets put them back
				//
				if (bwork) {
					// call db function to update the rankings, and restate the games associated with the season and the 
					// ranking
					db.updatePartipantRanking(this, ikey, ioldrank, itargetrank);
					db.cleanup();
				}
			}
		}
		db.free();
		
		// now.. lets update the team count in the season table.. and update it here as well...
		// This is not the object oriented way to do things.. but we are quality checking the data 
		// we just want it right...
		//
		db.updateSeasonTeamCount(this);

		return true;
	}
	
	private boolean syncTeamsToSeason() {
			
			
			//
			// ok..lets remove teams from a season that do not belong
		    // and add teams to a season that should be there.
			// 
			// if everything is copesetic.. then we are ok..
		    // 
		    // if we have to make adjustments here.. all the games have to 
		    // be reset
			LOGGER.info("Searching for new teams and defunct teams for season " + this.getSName());
			GDatabase db = (GDatabase) ContextManager.getDatabase("GDatabase");
			
			
			if (db.getTeamSeasonMissmatches(this)) {
				try {
					
					//
					// If anything comes back here..
					// then we have to add listed teams under 'A'
					// remove listed teams under 'D'
					// re sequence the teams to make sure we do not have any holes..
					// make sure the team count and game counts on the season are correct based upon 
					// the teams..
					// then reset the games.. (SQL for that i believe);
					//
					// All saved in test files.
					//

					GDatabase dbdel = (GDatabase) ContextManager.getDatabase("GDatabase");
					while (db.getResultSet().next()) {
						String sCode = db.getResultSet().getString(1);
						int id = db.getResultSet().getInt(2);
						if (sCode.equals("D")) {
							LOGGER.info("Found a defunct team.. cleaning up for season " + this.getSName());
							dbdel.delParticipant(this, id);
							dbdel.cleanup();
						} else if (sCode.equals("A")) {
							LOGGER.info("Found a New Team.. adding it to the participants " + this.getSName());
							dbdel.addlParticipant(this, id);
							dbdel.cleanup();						}
					}
					dbdel.free();
					db.getResultSet().close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			db.free();
			
			return true;
	}
	
	/**
	 * @return the divSName
	 */
	public String getDivSName() {
		return DivSName;
	}



	/**
	 * @param divSName the divSName to set
	 */
	public void setDivSName(String divSName) {
		DivSName = divSName;
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
	 * @return the gameCount
	 */
	public int getGameCount() {
		return GameCount;
	}



	/**
	 * @param gameCount the gameCount to set
	 */
	public void setGameCount(int gameCount) {
		GameCount = gameCount;
	}



	/**
	 * @return the teamCount
	 */
	public int getTeamCount() {
		return TeamCount;
	}



	/**
	 * @param teamCount the teamCount to set
	 */
	public void setTeamCount(int teamCount) {
		TeamCount = teamCount;
	}



	/**
	 * @return the gameLenth
	 */
	public String getGameLenth() {
		return GameLenth;
	}



	/**
	 * @param gameLenth the gameLenth to set
	 */
	public void setGameLenth(String gameLenth) {
		GameLenth = gameLenth;
	}



	/**
	 * @return the playOnce
	 */
	public int getPlayOnce() {
		return PlayOnce;
	}



	/**
	 * @param playOnce the playOnce to set
	 */
	public void setPlayOnce(int playOnce) {
		PlayOnce = playOnce;
	}



	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return StartDate;
	}



	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		StartDate = startDate;
	}



	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return EndDate;
	}



	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		EndDate = endDate;
	}
	
	

	/**
	 * @return the seasonWeeksTag
	 */
	public String getSeasonWeeksTag() {
		return SeasonWeeksTag;
	}

	/**
	 * @param seasonWeeksTag the seasonWeeksTag to set
	 */
	public void setSeasonWeeksTag(String seasonWeeksTag) {
		SeasonWeeksTag = seasonWeeksTag;
	}

	/**
	 * @return the byeTeamCount
	 */
	public int getByeTeamCount() {
		return ByeTeamCount;
	}

	/**
	 * @param byeTeamCount the byeTeamCount to set
	 */
	public void setByeTeamCount(int byeTeamCount) {
		ByeTeamCount = byeTeamCount;
	}

	/**
	 * @return the Participants
	 */
	public Participants getParticipants() {
		return parts;
	}

	/**
	 * @param pl the pl to set
	 */
	public void setParticipants(Participants pl) {
		this.parts = pl;
	}

	//
	// This puts a participant into the Season
	public void addParticipant(Participant _par) {
		parts.addChild(_par);
	}
	
	public Participant getParticipantAtID(int _key) {
		return (Participant)parts.getChildAtID(_key);
	}
	
	public Participant getParticipantAtIndex(int _key) {
		return (Participant)parts.getChildAtIndex(_key);
	}
	
	/**
	 * @return the Participants
	 */
	public SeasonWeeks getSeasonWeeks() {
		return sw;
	}

	/**
	 * @param pl the pl to set
	 */
	public void setSeasonWeeks(SeasonWeeks _sw) {
		this.sw = _sw;
	}

	//
	// This puts a participant into the Season
	public void addSeasonWeek(SeasonWeek _sw) {
		sw.addChild(_sw);
	}
	
	public SeasonWeek getSeasonWeekAtID(int _key) {
		return (SeasonWeek)sw.getChildAtID(_key);
	}
	
	public SeasonWeek getSeasonWeekAtIndex(int _key) {
		return (SeasonWeek)sw.getChildAtIndex(_key);
	}

	/**
	 * @return the gms
	 */
	public Games getGms() {
		return gms;
	}


	/**
	 * @param gms the gms to set
	 */
	public void setGms(Games gms) {
		this.gms = gms;
	}


	/**
	 * @return the maxByeCount
	 */
	public int getMaxByeCount() {
		return MaxByeCount;
	}

	/**
	 * @param maxByeCount the maxByeCount to set
	 */
	public void setMaxByeCount(int maxByeCount) {
		MaxByeCount = maxByeCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Season {" + this.getKey() + "}" + "[DivSName=" + DivSName + ", Name=" + Name + ", SName="
				
				+ SName + ", GameCount=" + GameCount + ", TeamCount="
				+ TeamCount + ", GameLenth=" + GameLenth + ", PlayOnce="
				+ PlayOnce + ", StartDate=" + StartDate + ", EndDate="
				+ EndDate + "]";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	
	public void schedule(boolean _bsqueeze) {

		LOGGER.info((_bsqueeze ? "SQUEEZE MODE " : " STANDARD MODE "));
		
		LOGGER.info("Scheduling Season " + this.getName());

		// Lets get the connections we need
		GDatabase db = (GDatabase) ContextManager.getDatabase("GDatabase"); 

		boolean bhome = false;
		boolean foundmatchup = false;
		
		// Define all the main object for the match team
		Participant pMain = null;
		Team tmMain = null;
		Club clMain = null;
		// Define all the match objects for the match team
		Participant pMatch = null;
		Team tmMatch = null;
		Club clMatch = null;
		boolean squeezeit = false;
		
		ArrayList<Integer> slMatchIDs = new ArrayList<Integer>();
		ArrayList<Integer> slMainIDs = new ArrayList<Integer>();
		
		// for each season.. step through the season weeks
		SeasonWeeks swks = this.getSeasonWeeks();
		Participants parts = this.getParticipants();
		
		LOGGER.info("Scheduling Season has " + swks.getChildCount() + " season weeks.");
		LOGGER.info("Scheduling Season has " + parts.getChildCount() + " participants.");
		
		int ibacktrack = 0;
		int iblowupcount = 0;
		for (int y=0;y < swks.getChildCount();y++) {
			
			SeasonWeek sw = (SeasonWeek)swks.getChildAtIndex(y);
			LOGGER.info(":Looking at SeasonWeek:" + sw.getName());
			
			// for a given season week.. who is available to play..
			// This needs to be fixed..
			sw.setScheduleComplete(false);
			
			// lets reset the process list for this week
			sw.resetProcessList();
			sw.resetBumpList();

			int iloopcount = 0;
			while (!sw.isScheduleComplete()) {
				iloopcount++;
				if (iblowupcount > 2) {
					LOGGER.info("BLOWUPTIME NO MORE.. lets squeeze take care of this...");
					ibacktrack = y;
					iblowupcount = 0;
					break;
				}
				if (iloopcount > 5) {
					iloopcount=0;
					iblowupcount++;
					LOGGER.info("BLOWUPTIME  need to backup the seasonweeks... and restart  starting over...");
					if (y >= swks.getChildCount() - 2) {
						LOGGER.info("BLOWUPTIME NOPE.. lets exit");
						break;
					}

					for (int i = y;i>=0 + ibacktrack;i--) {
						SeasonWeek swb = (SeasonWeek)swks.getChildAtIndex(i);
						swb.backoutSchedule(db, this);
					}
					y=-1 + ibacktrack;
					break;
				}
			
				sw.setScheduleComplete(true);
				sw.setAvailableToPlay(db);
				
				//
				// lets loop through all the teams that have to play 
				//
				for (Integer key : sw.getAvailToPlayKeys()) {

					pMain = (Participant)parts.getChildAtID(key.intValue());
					tmMain = pMain.getTeam();
					clMain = (Club)ContextManager.c_clubs.getChildAtID(tmMain.getIdClub());
					LOGGER.info("schedule:Refreshing Main Team:" + pMain.getTeam());
					tmMain.getTeamGameInfo().refreshInfo(db,this);

					if (sw.isAlreadyScheduled(pMain)) {
						LOGGER.info("schedule:alreadyscheduled a game for team:" + pMain.getTeam().getName() + ". Skipping..");
						continue;
					} 

					if (tmMain.isOutOfTown(sw)) {
						LOGGER.info("schedule:  Main Team is hout of town this week: " + pMain.getTeam().getName() + ". Skipping..");
						continue;
					} 
						
					//
					// if we are squeezing.. we need to get all available matchups.. even if they have games currently scheduled.
					//
					sw.setAvailableMatchups(db,pMain, this);
					LOGGER.info("schedule:Matchups are:" +sw.getMatchUpKeys());
											
					if (sw.noMatchups()) {
						if (this.getTeamCount() % 2 != 0 && (_bsqueeze || iloopcount == 4)) {
							LOGGER.info("ISTS SQUEESE TIME");
							sw.setAvailableMatchupsSqueeze(db,pMain, this);
							squeezeit = true;
						} else if (sw.getBumpCount() > 5 && _bsqueeze) {
							sw.setAvailableMatchupsSqueeze(db,pMain, this);
							squeezeit = true;
						} else if (this.getTeamCount() % 2 == 0 ) {
							if (iloopcount <= 4) {
								sw.backoutSchedule(db, this, pMain);
								sw.setScheduleComplete(false);
								break;
							} else if (iloopcount == 4) {
								sw.setAvailableMatchupsSqueeze(db,pMain, this);
								squeezeit = true;
							}
							
						}  
					}
						
					//
					// if the team is out of town.. then mark the available ice as -2
					//
					
					LOGGER.info("Scheduling:Main Team Assumes a Home position: " + tmMain);
					
					bhome = true;
					
					if (tmMain.isOutOfTown(sw)) {
						slMainIDs.clear();
						slMainIDs.add(Integer.valueOf(-2));
						LOGGER.info("Scheduling:" + " out of town detection.. ice is -2 for " + tmMain.toString());
					} else if (tmMain.isByeTeam()){
						slMainIDs.clear();
						slMainIDs.add(Integer.valueOf(-1));
						LOGGER.info("Scheduling:" + " Bye Detection for " + tmMain.toString());
					} else {
						slMainIDs = db.getAvailableSlotIDs(clMain,tmMain, sw);
					}

					if (slMainIDs.size() == 0) {
						LOGGER.info("Scheduling:" + tmMain.getSName() + " for " + sw.getFromDate() + " wants home game.. but does not have ice.  Switching to away mode.");
						bhome = false;
					}
					
					//
					// now lets iterate through the potential matchups
					//
					
					while (!sw.getMatchUpKeys().isEmpty()) { 

						pMatch = (Participant)parts.getChildAtID(sw.getMatchUpKeys().get(0));
						slMatchIDs.clear();
						tmMatch = pMatch.getTeam();
						clMatch = (Club)ContextManager.c_clubs.getChildAtID(tmMatch.getIdClub());
						tmMatch.getTeamGameInfo().refreshInfo(db,this);
						
						LOGGER.info("Scheduler: Match Team .. pulled from the rubble:" + tmMatch);
						
//						if (tmMatch.hasEnoughGames(this) && tmMain.hasEnoughGames(this)) {
//							LOGGER.info("schedule:  Main Team and match team has enough games: " + pMain + " " + pMatch + ". Skipping..");
//							sw.getMatchUpKeys().remove(0);
//							continue;
//						}

					//	if (!tmMatch.gameCapCheck(this,tmMain) || !tmMain.gameCapCheck(this,tmMatch)) {
					//		LOGGER.info("schedule:  Match Team enough games: " + pMain + " " + pMatch + ". Skipping..");
					//		sw.getMatchUpKeys().remove(0);
					//		continue;
					//	}

						if (!db.checkclubblock(pMatch, pMain, this)) {
							LOGGER.info("Block alert" + pMatch + ". Skipping..");
							sw.getMatchUpKeys().remove(0);
							continue;
						}
						
						//
						// Out of Town Checker
						//
						if (tmMain.isByeTeam() && tmMatch.isOutOfTown(sw)) {
							LOGGER.info("schedule:Main is bye.. match is out of town.. let it be a bye");
						} else if (tmMain.isOutOfTown(sw) && tmMatch.isByeTeam()) {
							LOGGER.info("schedule:Main is out o town.. match is bye.. let it be a bye");
						} else if (tmMatch.isOutOfTown(sw)) {
							LOGGER.info("schedule:Match Team is out of town..." + pMatch + ". Skipping..");
							sw.getMatchUpKeys().remove(0);
							continue;
						}
						
						//
						// only disregard the Match Team already being processed if squeeze is on
						// we need the double up

						// if we had to squeeze.. then lets punch a hole in the schedule
						// and try to make it happen
						if (squeezeit) {
							foundmatchup = false;
							LOGGER.info("Putting on the squeeezeee A");
							db.getAllAvailableSlots(this,pMatch);
							db.getAllAvailableSlots(this,pMain);
							db.getAllUsedSlots(this,  pMatch);
							db.getAllUsedSlots(this,  pMain);
							for (Slot myslot : pMain.getSlotsAvail()) {
								if (!db.checkHomeOnly(pMatch,myslot.actDate)) {
									if (pMain.canIPlay(this, myslot, pMatch) && pMatch.canIPlay(this, myslot, pMain)) {
										bhome = true;
										slMainIDs.clear();
										slMainIDs.add(Integer.valueOf(myslot.getID()));
										foundmatchup = true;
										break;
									}
								}	
							}
							for (Slot myslot : pMatch.getSlotsAvail()) {
								if (!db.checkHomeOnly(pMain,myslot.actDate)) {
									if (pMain.canIPlay(this, myslot, pMatch) && pMatch.canIPlay(this, myslot, pMain)) {
										bhome = false;
										slMatchIDs.clear();
										slMatchIDs.add(Integer.valueOf(myslot.getID()));
										foundmatchup = true;
										break;
									}
								}
							}
							if (!foundmatchup) {
								LOGGER.info("Did not find any squeeze ice.. moving on");
								sw.getMatchUpKeys().remove(0);
								continue;
							}
						} else {
							slMatchIDs= db.getAvailableSlotIDs(clMatch,tmMatch, sw);
						}
					
						if (tmMatch.isOutOfTown(sw)) {
							slMatchIDs.clear();
							slMatchIDs.add(Integer.valueOf(-2));
						} else if (tmMatch.isByeTeam()) {
							slMatchIDs.clear();
							slMatchIDs.add(Integer.valueOf(-1));
						}

					
						if (tmMain.isByeTeam() && !tmMatch.isByeTeam() && tmMatch.getTeamGameInfo().getByeGames() >= this.getMaxByeCount()) {
							LOGGER.info("*** WARNING **** schedule:Match already reached maximum byes for season:" + pMatch + ". Skipping..");
							sw.getMatchUpKeys().remove(0);
							continue;
						} else if (!tmMain.isByeTeam() && tmMatch.isByeTeam() && tmMain.getTeamGameInfo().getByeGames() >= this.getMaxByeCount()) {
							LOGGER.info("*** WARNING **** schedule:Main already reached maximum byes for season:" + pMatch + ". Skipping..");
							sw.getMatchUpKeys().remove(0);
							continue;
						} else if (slMatchIDs.isEmpty() && slMainIDs.isEmpty()) {
							if (_bsqueeze){
								squeezeit = true;
								LOGGER.info("Putting on the squeeezeee");
								db.getAllAvailableSlots(this,pMatch);
								db.getAllAvailableSlots(this,pMain);
								db.getAllUsedSlots(this,  pMatch);
								db.getAllUsedSlots(this,  pMain);
								for (Slot myslot : pMain.getSlotsAvail()) {
									if (!db.checkHomeOnly(pMatch,myslot.actDate)) {
										if (pMain.canIPlay(this, myslot, pMatch) && pMatch.canIPlay(this, myslot, pMain)) {
											bhome = true;
											slMainIDs.clear();
											slMainIDs.add(Integer.valueOf(myslot.getID()));
											foundmatchup = true;
											break;
										}
									}	
								}
								for (Slot myslot : pMatch.getSlotsAvail()) {
									if (!db.checkHomeOnly(pMain,myslot.actDate)) {
										if (pMain.canIPlay(this, myslot, pMatch) && pMatch.canIPlay(this, myslot, pMain)) {
											bhome = false;
											slMatchIDs.clear();
											slMatchIDs.add(Integer.valueOf(myslot.getID()));
											break;
										}
									}
								}

								if (slMatchIDs.isEmpty() && slMainIDs.isEmpty()) {
									LOGGER.info("*** WARNING **** Niether Team Has Ice **** skipping...");
									sw.getMatchUpKeys().remove(0);
									continue;
								}  
							} else {
								LOGGER.info("*** WARNING **** Niether Team Has Ice **** skipping...");
								sw.getMatchUpKeys().remove(0);
								continue;
							}
						}

						while (!slMatchIDs.isEmpty()) {
							if (db.checkHomeOnly(pMain, db.getSlotDate(slMatchIDs.get(0)))) {
								slMatchIDs.remove(0);
								LOGGER.info("1 Main has home only on this day clear the Match Slot IDs");
							} else {
								break;
							}
						}						
						while (!slMainIDs.isEmpty()) {
							if (db.checkHomeOnly(pMatch, db.getSlotDate(slMainIDs.get(0)))) {
								slMainIDs.remove(0);
								LOGGER.info("1 Match has to be a home game.. so we must clear the Main Slot IDs");
							} else {
								break;
							}
						}
						

						while (!slMainIDs.isEmpty()) {
							if (db.checkClubOffDay(pMain, db.getSlotDate(slMainIDs.get(0)))) {
								slMainIDs.remove(0);
								LOGGER.info("1 Main is gone.. so we must clear  Slot IDs");
							} else {
								break;
							}
						}

						while (!slMatchIDs.isEmpty()) {
							if (db.checkClubOffDay(pMain, db.getSlotDate(slMatchIDs.get(0)))) {
								slMatchIDs.remove(0);
								LOGGER.info("2 Main is gone.. so we must clear  Slot IDs");
							} else {
								break;
							}
						}

						while (!slMatchIDs.isEmpty()) {
							if (db.checkClubOffDay(pMatch, db.getSlotDate(slMatchIDs.get(0)))) {
								slMatchIDs.remove(0);
								LOGGER.info("3 Match is gone.. so we must clear  Slot IDs");
							} else {
								break;
							}
						}
						
						while (!slMainIDs.isEmpty()) {
							if (db.checkClubOffDay(pMatch, db.getSlotDate(slMainIDs.get(0)))) {
								slMainIDs.remove(0);
								LOGGER.info("4 Match is gone.. so we must clear  Slot IDs");
							} else { 
								break;
							}
						}
						
						//
						// we need to double check the slots because a squeeze could have moved a slot forward into a non squeeze week
						//
						db.getAllUsedSlots(this,  pMatch);
						db.getSlotsForMatchup(slMatchIDs,pMatch);
						db.getAllUsedSlots(this,  pMain);
						db.getSlotsForMatchup(slMainIDs,pMain);
						if (!tmMain.isByeTeam()) {
							slMainIDs.clear();
							for (Slot myslot : pMain.getSlotsMatchup()) {
								if (pMain.canIPlay(this, myslot, pMatch) && pMatch.canIPlay(this, myslot, pMain)) {
									slMainIDs.add(Integer.valueOf(myslot.getID()));
								}
							}	
						}
						if (!tmMatch.isByeTeam()) {
							slMatchIDs.clear();
							for (Slot myslot : pMatch.getSlotsMatchup()) {
								if (pMain.canIPlay(this, myslot, pMatch) && pMatch.canIPlay(this, myslot, pMain)) {
									slMatchIDs.add(Integer.valueOf(myslot.getID()));
								}
							}	
						}						
						LOGGER.info("Scheduler:MAIN Slots Scrubbed are:" + slMainIDs);
						LOGGER.info("Scheduler:Match Slots Scrubbed are:" + slMatchIDs);
						
						if (!tmMatch.gameCapCheck(this,tmMain) || !tmMain.gameCapCheck(this,tmMatch)) {
							LOGGER.info("schedule:  Match Team enough games: " + pMain + " " + pMatch + ". Skipping..");
							sw.getMatchUpKeys().remove(0);
							continue;
						}
						if (slMatchIDs.isEmpty() && slMainIDs.isEmpty()) {
							LOGGER.info("*** WARNING Level 2**** Niether Team Has Ice **** skipping...");
							sw.getMatchUpKeys().remove(0);
							continue;
						} else 

						if (squeezeit) {
							LOGGER.info("schedule:Closing the squeeze now...");
							squeezeit = false;
						} else if (tmMain.isByeTeam()) {
							LOGGER.info("schedule:bye game on the Main side letting it go through as is...");
						} else if (tmMatch.isByeTeam()) {
							LOGGER.info("schedule:bye game on the matchup side.. flipping to away...");
						} else if (tmMatch.isOutOfTown(sw) && tmMain.isOutOfTown(sw)) {
							LOGGER.info(" Both Teams are out of town.. schedule them now.:" + pMatch +":" + pMain);
						}


						// schedule the game .. see who gets home..
						//
						Participant pHome = db.calcHomeParticipant(this, pMain, pMatch, slMainIDs, slMatchIDs);
						
						if (pHome.equals(pMain)) {
							LOGGER.info("Main Slots: " + slMainIDs);
								db.scheduleGame(this, sw, pMain, pMatch, slMainIDs.get(0),sw.isBumpOn());
						} else if (pHome.equals(pMatch)) {
							LOGGER.info("Match Slots: " + slMatchIDs);
							db.scheduleGame(this, sw, pMatch, pMain,  slMatchIDs.get(0),sw.isBumpOn());
						} else {
							LOGGER.info("SNAFU on scheduling game..");
						}
						//
						// lets place them in the processed list
						// reset the bump list
						// and clear the matchupkeys.
						sw.addPrcoessListPart(pMain);
						sw.addPrcoessListPart(pMatch);
						//sw.resetBumpList();
						sw.getMatchUpKeys().clear();
						
					}
				}
			}
			//if (y==7) break;
		}
		
		db.free();

	}
	public String Dump() {
		StringBuffer sb = new StringBuffer();
		sb.append(toString());
		sb.append(parts.Dump());
		sb.append(sw.Dump());
		//sb.append(gms.Dump());
		return sb.toString();
	}


}
