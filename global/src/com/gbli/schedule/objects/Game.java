/**
 * 
 */
package com.gbli.schedule.objects;

import java.util.logging.Logger;

import com.gbli.context.ContextManager;

/**
 * @author David
 *
 */
public class Game extends SchedObject {
	
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	int idSeason = 0;
	int idTeamHome = 0;
	int idTeamAway = 0;
	int Participant1 = 0;  // relative rank
	int Participant2 = 0;  // relative rank
	int isBye = 0;
	int isExhibition = 0;
	int idSeasonWeek = 0;
	int idSlot = 0;
	int idOfficialSet = 0;
	int isLocked = 0;
	int idParticipant1 = 0;
	int idParticipant2 = 0;

	String DivSName = null;

	Participant Part1 = null;
	Participant Part2 = null;
	
	Team Team1 = null;
	Team Team2 = null;
	
	Team HomeTeam = null;
	Team AwayTeam = null;
	
	
	public Game () {
		
	}

	public Game (int _id) {
		super.setID(_id);
	}
	
	public Game (int _id, String _strToken, Season _se) {

		super.setID(_id);
		this.setParent(_se);
		//
		// Lets pull the participants and teams out of the season.. for the given game
		// if not done yet.
		// for now.. lets just load them up..
		//
		
	}

	/**
	 * Lets get the participants and find the teams associated with them
	 */
	public final void init() {
		Season se = (Season)getParent();
		
		Participant p1 = se.getParticipantAtID(this.getIdParticipant1());
		Participant p2 = se.getParticipantAtID(this.getIdParticipant2());
		
		if (p1 == null) {
			LOGGER.info("NOT PART 1 FOUND..." + this.Dump());
		} else {
			this.setPart1(p1);
			this.setTeam1(p1.getTeam());
		}
		
		if (p2 == null) {
			LOGGER.info(this.getParticipant2()+"" + " --- NOT PART2 FOUND..." + this.Dump());
		} else {
			this.setPart2(p2);
			this.setTeam2(p2.getTeam());
		}
				
	}
		

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Game [idSeason=" + idSeason + ", idTeamHome=" + idTeamHome
				+ ", idTeamAway=" + idTeamAway + ", Participant1="
				+ Participant1 + ", Participant2=" + Participant2 + ", isBye="
				+ isBye + ", idSeasonWeek=" + idSeasonWeek + ", idSlot="
				+ idSlot + ", idOfficialSet=" + idOfficialSet + ", isLocked="
				+ isLocked + ", DivSName=" + DivSName + ", Part1=" + Part1
				+ ", Part2=" + Part2 + ", Team1=" + Team1 + ", Team2=" + Team2
				+ ", HomeTeam=" + HomeTeam + ", AwayTeam=" + AwayTeam + ", ID="
				+ ID + "]";
	}

	/* (non-Javadoc)
	 * @see com.gbli.schedule.objects.SchedObject#Dump()
	 */
	@Override
	public String Dump() {
		// TODO Auto-generated method stub
		return toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the idSeason
	 */
	public int getIdSeason() {
		return idSeason;
	}

	/**
	 * @param idSeason the idSeason to set
	 */
	public void setIdSeason(int idSeason) {
		this.idSeason = idSeason;
	}

	/**
	 * @return the idTeamHome
	 */
	public int getIdTeamHome() {
		return idTeamHome;
	}

	/**
	 * @param idTeamHome the idTeamHome to set
	 */
	public void setIdTeamHome(int idTeamHome) {
		this.idTeamHome = idTeamHome;
	}

	/**
	 * @return the idTeamAway
	 */
	public int getIdTeamAway() {
		return idTeamAway;
	}

	/**
	 * @param idTeamAway the idTeamAway to set
	 */
	public void setIdTeamAway(int idTeamAway) {
		this.idTeamAway = idTeamAway;
	}

	/**
	 * @return the participant1
	 */
	public int getParticipant1() {
		return Participant1;
	}

	/**
	 * @param participant1 the participant1 to set
	 */
	public void setParticipant1(int participant1) {
		Participant1 = participant1;
	}

	/**
	 * @return the participant2
	 */
	public int getParticipant2() {
		return Participant2;
	}

	/**
	 * @param participant2 the participant2 to set
	 */
	public void setParticipant2(int participant2) {
		Participant2 = participant2;
	}

	/**
	 * @return the isBye
	 */
	public int getIsBye() {
		return isBye;
	}

	/**
	 * @param isBye the isBye to set
	 */
	public void setIsBye(int isBye) {
		this.isBye = isBye;
	}

	/**
	 * @return the isExhibition
	 */
	public int getIsExhibition() {
		return isExhibition;
	}

	/**
	 * @param isExhibition the isExhibition to set
	 */
	public void setIsExhibition(int isExhibition) {
		this.isExhibition = isExhibition;
	}

	/**
	 * @return the idSeasonWeek
	 */
	public int getIdSeasonWeek() {
		return idSeasonWeek;
	}

	/**
	 * @param idSeasonWeek the idSeasonWeek to set
	 */
	public void setIdSeasonWeek(int idSeasonWeek) {
		this.idSeasonWeek = idSeasonWeek;
	}

	/**
	 * @return the idSlot
	 */
	public int getIdSlot() {
		return idSlot;
	}

	/**
	 * @param idSlot the idSlot to set
	 */
	public void setIdSlot(int idSlot) {
		this.idSlot = idSlot;
	}

	/**
	 * @return the idOfficialSet
	 */
	public int getIdOfficialSet() {
		return idOfficialSet;
	}

	/**
	 * @param idOfficialSet the idOfficialSet to set
	 */
	public void setIdOfficialSet(int idOfficialSet) {
		this.idOfficialSet = idOfficialSet;
	}

	/**
	 * @return the isLocked
	 */
	public int getIsLocked() {
		return isLocked;
	}

	/**
	 * @param isLocked the isLocked to set
	 */
	public void setIsLocked(int isLocked) {
		this.isLocked = isLocked;
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
	 * @return the team1
	 */
	public Team getTeam1() {
		return Team1;
	}

	/**
	 * @param team1 the team1 to set
	 */
	public void setTeam1(Team team1) {
		Team1 = team1;
	}

	/**
	 * @return the team2
	 */
	public Team getTeam2() {
		return Team2;
	}

	/**
	 * @param team2 the team2 to set
	 */
	public void setTeam2(Team team2) {
		Team2 = team2;
	}

	/**
	 * @return the homeTeam
	 */
	public Team getHomeTeam() {
		return HomeTeam;
	}

	/**
	 * @param homeTeam the homeTeam to set
	 */
	public void setHomeTeam(Team homeTeam) {
		HomeTeam = homeTeam;
	}

	/**
	 * @return the awayTeam
	 */
	public Team getAwayTeam() {
		return AwayTeam;
	}

	/**
	 * @param awayTeam the awayTeam to set
	 */
	public void setAwayTeam(Team awayTeam) {
		AwayTeam = awayTeam;
	}

	/**
	 * @return the part1
	 */
	public Participant getPart1() {
		return Part1;
	}

	/**
	 * @param part1 the part1 to set
	 */
	public void setPart1(Participant part1) {
		Part1 = part1;
	}

	/**
	 * @return the part2
	 */
	public Participant getPart2() {
		return Part2;
	}

	/**
	 * @param part2 the part2 to set
	 */
	public void setPart2(Participant part2) {
		Part2 = part2;
	}

	/**
	 * @return the idParticipant1
	 */
	public int getIdParticipant1() {
		return idParticipant1;
	}

	/**
	 * @param idParticipant1 the idParticipant1 to set
	 */
	public void setIdParticipant1(int idParticipant1) {
		this.idParticipant1 = idParticipant1;
	}

	/**
	 * @return the idParticipant2
	 */
	public int getIdParticipant2() {
		return idParticipant2;
	}

	/**
	 * @param idParticipant2 the idParticipant2 to set
	 */
	public void setIdParticipant2(int idParticipant2) {
		this.idParticipant2 = idParticipant2;
	}

}
