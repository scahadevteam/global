package com.gbli.schedule.objects;

import java.util.HashMap;
import java.util.logging.Logger;

import com.gbli.connectors.GDatabase;
import com.gbli.context.ContextManager;

/**
 * @author David
 *
 */
public class TeamGameInfo extends SchedObject {

	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private int TotalGames = 0;
	private int HomeGames = 0;
	private int AwayGames = 0;
	private int ByeGames = 0;
	private int ExGames = 0;
	
	/**
	 * @return the exGames
	 */
	public int getExGames() {
		return ExGames;
	}
	/**
	 * @param exGames the exGames to set
	 */
	public void setExGames(int exGames) {
		ExGames = exGames;
	}
	private HashMap<String,String> hmBOD = new HashMap<String, String>();

	/**
	 * @return the hmBOD
	 */
	public HashMap<String, String> getHmBOD() {
		return hmBOD;
	}
	/**
	 * @param hmBOD the hmBOD to set
	 */
	public void setHmBOD(HashMap<String, String> hmBOD) {
		this.hmBOD = hmBOD;
	}
	public TeamGameInfo (Team _tm) {
		this.setParent(_tm);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * refreshInfo.. This gives us the latest information about a team that we need to track from the database
	 * 
	 * @param _db
	 */
	public void refreshInfo(GDatabase _db, Season _se) {
		
		_db.refresh(this, _se);
		_db.cleanup();
		
	}
	@Override
	public String Dump() {
		// TODO Auto-generated method stub
		return toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TeamGameInfo [TotalGames=" + TotalGames + ", HomeGames="
				+ HomeGames + ", AwayGames=" + AwayGames + ", ByeGames="
				+ ByeGames + ", ExGames=" + ExGames + ", hmBOD=" + hmBOD + "]";
	}
	/**
	 * @return the homeGames
	 */
	public int getHomeGames() {
		return HomeGames;
	}

	/**
	 * @param homeGames the homeGames to set
	 */
	public void setHomeGames(int homeGames) {
		HomeGames = homeGames;
	}

	/**
	 * @return the awayGames
	 */
	public int getAwayGames() {
		return AwayGames;
	}

	/**
	 * @param awayGames the awayGames to set
	 */
	public void setAwayGames(int awayGames) {
		AwayGames = awayGames;
	}

	/**
	 * @return the byeGames
	 */
	public int getByeGames() {
		return ByeGames;
	}

	/**
	 * @param byeGames the byeGames to set
	 */
	public void setByeGames(int byeGames) {
		ByeGames = byeGames;
	}
	/**
	 * @return the totalGames
	 */
	public int getTotalGames() {
		return TotalGames;
	}
	/**
	 * @param totalGames the totalGames to set
	 */
	public void setTotalGames(int totalGames) {
		TotalGames = totalGames;
	}

}
