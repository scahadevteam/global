package com.gbli.schedule.objects;

import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class Team extends SchedObject {
	
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private String Name = null;
	private String SName = null;
	private Club clb = null;
	private int idClub = 0;
	private int isByeTeam = 0;
	private int isExhibitionTeam = 0;
	private TeamGameInfo TeamGameInfo = null;
	
	
	public Team(int _id, String _sSname, String _sName, int _isByeTeam, int _isExhibitionTeam, int _idClub) {
	
		this.setID(_id);
		this.setName(_sName);
		this.setSName(_sSname);
		this.setIsByeTeam(_isByeTeam);
		this.setIsExhibitionTeam(_isExhibitionTeam);
		this.setIdClub(_idClub);
		
	}

	@Override
	public String Dump() {
		// TODO Auto-generated method stub
		return this.toString();
	}
	

	/**
	 * @return the Name
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @param Name the Name to set
	 */
	public void setName(String sName) {
		Name = sName;
	}

	/**
	 * @return the sSname
	 */
	public String getSName() {
		return SName;
	}

	/**
	 * @param sSname the sSname to set
	 */
	public void setSName(String sSname) {
		this.SName = sSname;
	}

	/**
	 * @return the isByeTeam
	 */
	public int getIsByeTeam() {
		return isByeTeam;
	}

	/**
	 * @param isByeTeam the isByeTeam to set
	 */
	public void setIsByeTeam(int isByeTeam) {
		this.isByeTeam = isByeTeam;
	}

	/**
	 * @return the isExhibitionTeam
	 */
	public int getIsExhibitionTeam() {
		return isExhibitionTeam;
	}

	/**
	 * @param isExhibitionTeam the isExhibitionTeam to set
	 */
	public void setIsExhibitionTeam(int isExhibitionTeam) {
		this.isExhibitionTeam = isExhibitionTeam;
	}

	/**
	 * @return the teamGameInfo
	 */
	public TeamGameInfo getTeamGameInfo() {
		if (TeamGameInfo == null) {
			this.setTeamGameInfo(new TeamGameInfo(this));
		}
		return TeamGameInfo;
	}

	public int getTotalGames() {
		return this.getTeamGameInfo().getAwayGames() + this.getTeamGameInfo().getHomeGames();
	}
	
	/**
	 * @param teamGameInfo the teamGameInfo to set
	 */
	public void setTeamGameInfo(TeamGameInfo teamGameInfo) {
		TeamGameInfo = teamGameInfo;
	}

	/**
	 * @return the clb
	 */
	public Club getClb() {
		return clb;
	}

	/**
	 * @param clb the clb to set
	 */
	public void setClb(Club clb) {
		this.clb = clb;
	}

	/**
	 * @return the idClub
	 */
	public int getIdClub() {
		return idClub;
	}

	/**
	 * @param idClub the idClub to set
	 */
	public void setIdClub(int idClub) {
		this.idClub = idClub;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Returns true if the tean is out of town this weekend..
	 * 
	 * @param _sw
	 * @return
	 */
	public boolean isOutOfTown(SeasonWeek _sw) {
		
		for (String date : getTeamGameInfo().getHmBOD().keySet()) {
			if (date.compareTo(_sw.getFromDate().trim()) >= 0 && date.compareTo(_sw.getToDate().trim()) <= 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isByeTeam() {
		return this.getIsByeTeam() == 1;
	}

	public boolean isExhibitionTeam() {
		return this.getIsExhibitionTeam() == 1;
	}
	
	public boolean gameCapCheck(Season _se, Team _op) {

		int imytot = this.getTeamGameInfo().getHomeGames() + this.getTeamGameInfo().getAwayGames();
		int ioptot = _op.getTeamGameInfo().getHomeGames() + _op.getTeamGameInfo().getAwayGames();
		int igamecap = _se.getGameCount();
		
		LOGGER.info("gamecapcheck:mytot=" + imytot + ", ioptot=" + ioptot + ", igamecap =" + igamecap + ", teamcount=" + _se.getTeamCount());
		//
		// if we have an odd number of teams.
		//
		// then if my opponent is at the game cap .. but I am not one away.. then we have to play..
		//
		
		if (this.getID() == 76 && imytot == 17) {
			return false;
		}
		if (_op.getID() == 76 && ioptot == 17) {
			return false;
		}
		if (_se.getTeamCount() % 2 != 0) {
			if (imytot == igamecap + 1) {
				LOGGER.info("gamecap:  I (" + getSName() +  ") ODD I at my limit..");
				return false;
				
			}
			if (imytot == igamecap && ioptot > igamecap) {
				LOGGER.info("gamecap:  I (" + getSName() +  ") ODD am at my game cap.. my opponent is over it..");
				return false;
			}
			if (imytot == igamecap && ioptot == igamecap) {
				LOGGER.info("gamecap:  I (" + getSName() +  ") ODD am at game cap.. and my opponent is as well");
				return false;
			}
			return true;
		}
		// if either are at game cap here.. return 	
		if (_se.getTeamCount() % 2 == 0) {
			if (imytot == igamecap) {
				LOGGER.info("gamecap:  I (" + getSName() +  ") EVEN - I AM AT MY GAME CAP...");
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Team [Name=" + Name + ", SName=" + SName + ", isByeTeam="
				+ isByeTeam + ", isExhibitionTeam=" + isExhibitionTeam + "]";
	}

}
