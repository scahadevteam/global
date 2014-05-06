/**
 * 
 */
package com.gbli.schedule.objects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

/**
 * @author David
 *
 */
public class Participant extends SchedObject {
	

	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private Team Team = null;
	private Vector<Slot> SlotsAvail = new Vector<Slot>();
	private Vector<Slot> SlotsPlaying = new Vector<Slot>();
	private Vector<Slot> SlotsMatchup = new Vector<Slot>();
	/**
	 * @return the slotsPlaying
	 */
	public Vector<Slot> getSlotsPlaying() {
		return SlotsPlaying;
	}

	/**
	 * @param slotsPlaying the slotsPlaying to set
	 */
	public void setSlotsPlaying(Vector<Slot> slotsPlaying) {
		SlotsPlaying = slotsPlaying;
	}

	/**
	 * @return the SlotsAvail
	 */
	public Vector<Slot> getSlotsAvail() {
		return SlotsAvail;
	}

	/**
	 * @param SlotsAvail the SlotsAvail to set
	 */
	public void setSlotsAvail(Vector<Slot> vSqSlots) {
		this.SlotsAvail = vSqSlots;
	}
	
	public void resetSlotsAvail() {
		SlotsAvail = new Vector<Slot>();
	}

	public void resetSlotsPlayed() {
		SlotsPlaying = new Vector<Slot>();
	}

	
	private int Rank = 0;
	
	public Participant (int _id, int _irank, Season _se, Team _team) {
		this.setID(_id);
		this.setRank(_irank);
		this.setParent(_se);
		this.setTeam(_team);
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Participant [Team=" + Team + ", Rank=" + Rank + ", ID=" + ID + "]";
	}

	/* (non-Javadoc)
	 * @see com.gbli.schedule.objects.SchedObject#Dump()
	 */
	@Override
	public String Dump() {
		// TODO Auto-generated method stub
		return this.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the team
	 */
	public Team getTeam() {
		return Team;
	}

	/**
	 * @param team the team to set
	 */
	public void setTeam(Team team) {
		Team = team;
	}

	/**
	 * @return the rank
	 */
	public int getRank() {
		return Rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		Rank = rank;
	}
	
	/**
	 * Rules checker to make sure this game can be played...
	 * Given we are running in squeeze mode
	 * @param _sl
	 * @param _pAgainst
	 * @return
	 */
	public boolean canIPlay(Season _se, Slot _sl, Participant _pAgainst) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

		try {
			 Date dateAvail = dateFormat.parse(_sl.actDate + " " + _sl.StartTime); 
			 if (this.getSlotsPlaying().isEmpty()) {
			   	LOGGER.info("Team Not playing... so anyslot will do SLOT:" + _sl);
		    	return true;
			 }
		 //   LOGGER.info("I (" + this.getTeam().getSName() + ") am  playing a team :" + _pAgainst.getTeam().getSName());
		//	LOGGER.info("I (" + this.getTeam().getSName() + ") am looking at  date:"  + _sl.actDate + " " + _sl.StartTime );
			 for (Slot slot: this.getSlotsPlaying()) {
				Date datePlaying = dateFormat.parse(slot.actDate + " " + slot.StartTime); 
			    long diff = dateAvail.getTime() - datePlaying.getTime();
			    long hours = Math.abs(diff / (1000*60*60));
		//	    LOGGER.info("I (" + this.getTeam().getSName() + ") have a game on date:"  + slot.actDate + " " + slot.StartTime );
		//		LOGGER.info("I (" + this.getTeam().getSName() + ") was told it was dif:" + hours);
				
//				if (_pAgainst.getTeam().getID() ==  slot.idTeamAgainst && hours < 24 * (7)) {
				if (_pAgainst.getTeam().getID() ==  slot.idTeamAgainst && hours < 24 * (_se.getTeamCount() * 2)) {
			    	LOGGER.info("I cannot play...not enough time between matchups.. needs to be more than:" +(24 * (_se.getTeamCount() * 1.25)));
			    	return false;
			    }
			    
			    if (hours < 8 ) {
			    	LOGGER.info("I cannot play...not enough time between matchups..  already have a game within 8 hours of start time");
			    	return false;
			    }
			    
			    //
			    // lets make sure its not in the middle of a club or team out of town
			    //
			    
			    for (String bodate: _pAgainst.getTeam().getTeamGameInfo().getHmBOD().keySet()) {
			    	Date datebo = dateFormat.parse(bodate + " 00:00:00");
			        long bodiff = dateAvail.getTime() - datebo.getTime();
			    	long bodhours = Math.abs(bodiff / (1000*60*60));
					    if (bodhours < 72) {
		//			   	LOGGER.info("SLOT CONFLICT:  team out of town.." + _sl );
					   	return false;
					   }
			    }
			    
			}
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
    	LOGGER.info("I (" + this.getTeam().getSName() + ") can play on this slot:" + _sl);
		
		return true;
	}

	public void resetSlotsMatchup() {
		SlotsMatchup = new Vector<Slot>();
	}

	public Vector<Slot> getSlotsMatchup() {
		// TODO Auto-generated method stub
		return SlotsMatchup;
	}

}
