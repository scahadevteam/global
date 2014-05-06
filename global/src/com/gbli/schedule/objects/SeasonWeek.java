package com.gbli.schedule.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.gbli.connectors.GDatabase;
import com.gbli.context.ContextManager;

public class SeasonWeek extends SchedObject {

	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	//
	// used to help scheduling objects..
	//
	Participants pParc = new Participants();
	Participants pBump = new Participants();

	int iBC = 0;
	public HashMap<String, String> hmPattern = new HashMap<String, String>(); 
	
	ArrayList<Integer> AvauilToPlayKeys = new ArrayList<Integer>();
	ArrayList<Integer> MatchUpKeys = new ArrayList<Integer>();

	String SName = null;
	String Name = null;
	int Week = 0;
	String FromDate = null;
	String ToDate = null;
	
	boolean ScheduleComplete = false;
	
	public SeasonWeek(int _id, Season _se) {
		
		super.setID(_id);
		super.setParent(_se);
		
		
	}

	@Override
	public String Dump() {
		// TODO Auto-generated method stub
		return toString();
	}

	public Participants getBumpList() {
		return pBump;
	}
	
	public int getBumpCount () {
		return iBC;
	}
	public Participants getProcessList() {
		return pParc;
	}
	
	public void resetBumpList () {
		LOGGER.info("Reseting Bump List..");
		pBump = new Participants();
	}

	public void resetProcessList() {
		LOGGER.info("Reseting Processed List..");
		pParc = new Participants();
	}
	
	public Participant getBumpListPartAtID(int _i) {
		return (Participant)pBump.getChildAtID(_i);
	}

	public Participant getProcessListPartAtID(int _i) {
		return (Participant)pParc.getChildAtID(_i);
	}

	public void addBumpListPart(Participant _p) {
		pBump.addChild(_p);
	}

	public void addPrcoessListPart(Participant _p) {
		pParc.addChild(_p);
	}

	public boolean isBumpOn () {
		return pBump.getChildCount() > 0;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the sName
	 */
	public String getSName() {
		return SName;
	}
	/**
	 * @param sName the sName to set
	 */
	public void setSName(String sName) {
		SName = sName;
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
	 * @return the week
	 */
	public int getWeek() {
		return Week;
	}
	/**
	 * @param week the week to set
	 */
	public void setWeek(int _week) {
		Week = _week;
	}
	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return FromDate;
	}
	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		FromDate = fromDate;
	}
	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return ToDate;
	}
	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(String toDate) {
		ToDate = toDate;
	}
	/**
	 * @return the avauilToPlayKeys
	 */
	public ArrayList<Integer> getAvailToPlayKeys() {
		return AvauilToPlayKeys;
	}

	/**
	 * @param avauilToPlayKeys the avauilToPlayKeys to set
	 */
	public void setAvailToPlayKeys(ArrayList<Integer> availToPlayKeys) {
		AvauilToPlayKeys = availToPlayKeys;
	}

	/**
	 * @return the matchUpKeys
	 */
	public ArrayList<Integer> getMatchUpKeys() {
		return MatchUpKeys;
	}

	/**
	 * @param matchUpKeys the matchUpKeys to set
	 */
	public void setMatchUpKeys(ArrayList<Integer> matchUpKeys) {
		MatchUpKeys = matchUpKeys;
	}
	
	/**
	 * @return the scheduleComplete
	 */
	public boolean isScheduleComplete() {
		return ScheduleComplete;
	}

	/**
	 * @param scheduleComplete the scheduleComplete to set
	 */
	public void setScheduleComplete(boolean scheduleComplete) {
		ScheduleComplete = scheduleComplete;
	}

	public boolean isAlreadyScheduled(Participant _p) {
		return pParc.getChildAtID(_p.getID()) != null;
	}

	public void setAvailableToPlay(GDatabase _db) {
		setAvailToPlayKeys(_db.getAvailableParticipants(this));
		if (isBumpOn()) {
			LOGGER.info("** PICKED UP A BUMP FOR *** new bump list is adding to the fron the the key list for immediate processing:" + pBump.Dump());
			// DWB AvauilToPlayKeys.add(0,Integer.valueOf(pBump.getChildAtIndex(0).getID()));
		}
	}
	
	public void setAvailableMatchups(GDatabase _db, Participant _p, Season _se) {
		setMatchUpKeys(_db.getAvailableMatchups(_p,_se, this, false));
	}

	public void setAvailableMatchupsSqueeze(GDatabase _db, Participant _p, Season _se) {
		setMatchUpKeys(_db.getAvailableMatchups(_p,_se, this, true));
		weedOutMaxGamers(_db);
	}

	
	public boolean noMatchups() {
		return this.getMatchUpKeys().isEmpty();
	}
	
	public boolean alreadyProcessed(Participant _p) {
		return pParc.getChildAtID(_p.getID()) != null;
	}
	
	public void backoutSchedule(GDatabase _db, Season _se, Participant _p) {
		this.iBC++;
		addBumpListPart(_p);
		// if bump list grew.. then we have a case where we need to put this at the top top.. blow everything away
		//
		if (pBump.getChildCount() > 1) {
			LOGGER.info("  Initiate Hard Bump Reset...");
			_db.backoutSchedule(_se, this, true);
		} else {
			LOGGER.info("  Initiate Soft Bump Reset...");
			_db.backoutSchedule(_se, this, false);
		}
		
		//
		// we are not done
		//
		this.resetProcessList();
		this.setScheduleComplete(false);
	}

	public  Participant calcHomeParticipant(Participant _pMain, Participant  _pMatch, ArrayList<Integer> _aMain, ArrayList<Integer> _aMatch) {
		
		int iMain = _pMain.getTeam().getTeamGameInfo().getHomeGames();
		int iMatch = _pMatch.getTeam().getTeamGameInfo().getHomeGames();
		
		if (_pMain.getTeam().isByeTeam()) {
			return _pMain;
		} else if (_pMatch.getTeam().isByeTeam()) {
			return _pMatch;
		} else if (_aMain.isEmpty() && !_aMatch.isEmpty()) {
			return _pMatch;
		} else 	if (!_aMain.isEmpty() && _aMatch.isEmpty()) {
			return _pMain;
		} else if (_aMain.size() < 2 && iMain < iMatch + 1) {
			return _pMain;
		} else if (_aMatch.size() < 2 && iMatch < iMain + 1) {
			return _pMatch;
		} else 	if (iMain <= iMatch && !_aMain.isEmpty()) {
			return _pMain;
		} else if (iMatch <= iMain && !_aMatch.isEmpty()) {
			return _pMatch;
		}
		return null;
		
	}
	
	public void backoutSchedule(GDatabase _db, Season _se) {

		// if bump list grew.. then we have a case where we need to put this at the top top.. blow everything away
		//
		if (pBump.getChildCount() > 1) {
			LOGGER.info("  Initiate Hard Bump Reset...");
			_db.backoutSchedule(_se, this, true);
		} else {
			LOGGER.info("  Initiate Soft Bump Reset...");
			_db.backoutSchedule(_se, this, true);
		}
		this.resetProcessList();
		this.setScheduleComplete(false);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SeasonWeek [SName=" + SName + ", Name=" + Name + ", Week="
				+ Week + ", FromDate=" + FromDate + ", ToDate=" + ToDate
				+ ", ID=" + ID + "]";
	}
	
	public void weedOutMaxGamers(GDatabase _db) {
		ArrayList<Integer> am = new ArrayList<Integer>();
		for (Integer match : getMatchUpKeys()) {
			Participant pMatch = ((Season)this.getParent()).getParticipantAtID(match.intValue());
			pMatch.getTeam().getTeamGameInfo().refreshInfo(_db,(Season)this.getParent());
			if (pMatch.getTeam().getTotalGames() > ((Season)this.getParent()).getGameCount()) {
				am.add(match);
				LOGGER.info("schedule:Removing team from matchup.. too many games:" + pMatch);
			}
			if (pMatch.getTeam().isByeTeam()) {
				am.add(match);
				LOGGER.info("schedule:Removing bye team from matchup.. we dont  break for a bye" + pMatch);
			}
		}
		
		for (Integer match : am) {
			getMatchUpKeys().remove(match);
		}

	}

}
