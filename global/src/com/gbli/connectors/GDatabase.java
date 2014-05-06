/**
 * 
 */
package com.gbli.connectors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;
import com.gbli.schedule.objects.Club;
import com.gbli.schedule.objects.Clubs;
import com.gbli.schedule.objects.Game;
import com.gbli.schedule.objects.Games;
import com.gbli.schedule.objects.Participant;
import com.gbli.schedule.objects.Participants;
import com.gbli.schedule.objects.SeasonWeek;
import com.gbli.schedule.objects.SeasonWeeks;
import com.gbli.schedule.objects.Season;
import com.gbli.schedule.objects.Seasons;
import com.gbli.schedule.objects.Slot;
import com.gbli.schedule.objects.Team;
import com.gbli.schedule.objects.TeamGameInfo;

import java.sql.PreparedStatement;

import org.w3c.dom.ls.LSException;

/**
 * @author dbigelow
 * 
 */
public class GDatabase extends Database {

	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	public GDatabase(int _iId, String _sDriver, String _sURL, String _sUser, String _sPwd) {
		super(_iId, _sDriver, _sURL, _sUser, _sPwd);
	}

	public GDatabase(String _sDriver, String _sURL, String _sUser, String _sPwd) {
		super(_sDriver, _sURL, _sUser, _sPwd);
	}

	/**
	 * This will check to see if the user has the proper credentials and will
	 * set up the result set for interrogation if true
	 * 
	 * @param _strUser
	 * @param _strPass
	 * @return
	 */
	public final boolean verify(String _sUser, String _sPass) {

		Vector<String> v = new Vector<String>();
		v.add(_sUser);
		v.add(_sPass);

		return super.getData("", v);

	}

	/**
	 * This will check to see if the user has the proper credentials and will
	 * set up the result set for interrogation if true
	 * 
	 * @param _strUser
	 * @param _strPass
	 * @return
	 */
	public final boolean getActionData(int _ipid) {

		Vector<Integer> v = new Vector<Integer>();
		v.add(Integer.valueOf(_ipid));

		return getData("", v);

	}

	/**
	 * This truncates the rrtemplate database...
	 */
	public final boolean truncateRRData() {

		String sTruncate = "TRUNCATE TABLE GBLI.RRTEMPLATE;";
		try {
			Statement stmt = createStatement();
			stmt.executeUpdate(sTruncate);
			commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("DB ERROR IN truncateRRData..");
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * This truncates the games table...
	 */
	public final boolean truncateGame() {

		String sTruncate = "TRUNCATE TABLE GBLI.GAME;";
		try {
			Statement stmt = createStatement();
			stmt.executeUpdate(sTruncate);
			commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("DB ERROR IN truncateGame..");
			e.printStackTrace();
			return false;
		}

	}
	/**
	 * This truncates the rrtemplate database... Very inefficiant right now...
	 * 
	 */
	public final boolean insertRRTemplateRow(int _iSetSize, int _iteamA,
			int _iteamB) {

		String sInsert = "INSERT INTO GBLI.RRTEMPLATE (SETSIZE, TEAMA, TEAMB) "
				+ " VALUES (" + _iSetSize + ", " + _iteamA + ", " + _iteamB
				+ ");";
		try {
			Statement stmt = createStatement();
			stmt.executeUpdate(sInsert);
			commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("DB ERROR IN insertRRTemplateRow..");
			e.printStackTrace();
			return false;
		}
	}

	public final boolean getObjectBody(Season _se) {
		
		LOGGER.info("Grabbing Season Boday for Season " + _se.getName());

		String strSQL = "SELECT  sname, name, tagseasonweeks,"
				+ " divsname, " + " gamecount, " + " teamcount, " + " byeteamcount, "
				+ " gamelength," + " playonce, " + " startdate, "
				+ " enddate, maxbyecount, islocked, mingamecount " + " from gbli.season " + " where idseason = ? and isactive = 1";

		Vector<Integer> v = new Vector<Integer>();
		v.add(Integer.valueOf(_se.getID()));
		
		if (this.getData(strSQL,v)) {
			ResultSet rs = super.getResultSet();
			try {
				while (rs.next()) {
					int i = 1;
					_se.setSName(rs.getString(i++));
					_se.setName(rs.getString(i++));
					_se.setSeasonWeeksTag(rs.getString(i++));
					_se.setDivSName(rs.getString(i++));
					_se.setGameCount(rs.getInt(i++));
					_se.setTeamCount(rs.getInt(i++));
					_se.setByeTeamCount(rs.getInt(i++));
					_se.setGameLenth(rs.getString(i++));
					_se.setPlayOnce(rs.getInt(i++));
					_se.setStartDate(rs.getString(i++));
					_se.setEndDate(rs.getString(i++));
					_se.setMaxByeCount(rs.getInt(i++));
					_se.setIsLocked(rs.getInt(i++));
					_se.setMinGameCount(rs.getInt(i++));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;

	}
	
	/**
	 * This gets all active seasons and puts them in the passes Season Object
	 * 
	 * @param _sc
	 * @return
	 */
	public final boolean getSeasonCollection(Seasons _sc,boolean _init) {

		String strSQL = "SELECT " + " idseason, " + " sname, " + " name, " + " tagseasonweeks, "
				+ " divsname, " + " gamecount, " + " teamcount, " + " byeteamcount, " 
				+ " gamelength," + " playonce, " + " startdate, "
				+ " enddate , maxbyecount, islocked, mingamecount " + " from gbli.season " + " where isactive = 1 order by rank";

		if (this.getData(strSQL)) {
			ResultSet rs = super.getResultSet();
			try {
				while (rs.next()) {
					int i = 1;
					int id = rs.getInt(i++);
					Season sn = new Season(id);
					_sc.addChild(sn);
					sn.setSName(rs.getString(i++));
					sn.setName(rs.getString(i++));
					sn.setSeasonWeeksTag(rs.getString(i++));
					sn.setDivSName(rs.getString(i++));
					sn.setGameCount(rs.getInt(i++));
					sn.setTeamCount(rs.getInt(i++));
					sn.setByeTeamCount(rs.getInt(i++));
					sn.setGameLenth(rs.getString(i++));
					sn.setPlayOnce(rs.getInt(i++));
					sn.setStartDate(rs.getString(i++));
					sn.setEndDate(rs.getString(i++));
					sn.setMaxByeCount(rs.getInt(i++));
					sn.setIsLocked(rs.getInt(i++));
					sn.setMinGameCount(rs.getInt(i++));
					if (_init) {;
						sn.init();  // Fill out the guts of it..
					}
					//
					// ok.. a season has Participants, SeasonWeeks, and Games
					//  lets load them all..
					sn.setParticipants(new Participants(sn));
					sn.setSeasonWeeks(new SeasonWeeks(sn));

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		}
		return true;

	}

	/**
	 * This gets all active seasons and puts them in the passes Season Object
	 * 
	 * @param _sc
	 * @return
	 */
	public final boolean getClubCollection(Clubs _cs, boolean _init) {

		LOGGER.info("getGlubCollection Started Searching for all Clubs...");
		String strSQL = "SELECT " + " idclub, " + " sname, " + " name, " + " teamcount, "
				+ " slotsperweek from gbli.club " + " where isactive = 1";

		if (this.getData(strSQL)) {
			ResultSet rs = super.getResultSet();
			try {
				while (rs.next()) {
					int i = 1;
					int id = rs.getInt(i++);
					Club cl = new Club(id);
					_cs.addChild(cl);
					cl.setSName(rs.getString(i++));
					cl.setName(rs.getString(i++));
					cl.setTeamCount(rs.getInt(i++));
					cl.setSlotsPerWeek(rs.getInt(i++));
					if (_init) {
						cl.init();  // Fill out the guts of it..
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		}
		this.cleanup();
		LOGGER.info("getGlubCollection Done searching for all Clubs...");

		return true;

	}
	
	
	
	public final boolean getParticipantCollection(Season _se, Participants _part) {

		String strSQL = 
		"SELECT " + 
		" p.idparticipant, " + 
		" p.rank, " + 
		" p.idteam, " +
		" t.sname, " + 
		" t.name, " +
		" t.isbyeteam, " + 
		" t.isexhibitionteam, " +
		" t.idClub " +
		" from gbli.participant p " + 
		" join gbli.team t on " + 
		"    t.idteam = p.idteam " + 
		" where p.isactive = 1 and p.idseason = ? order by 2";

		Vector<Integer> v = new Vector<Integer>();
		//
		// we want to get the ID Value of the Season
		v.add(Integer.valueOf(_se.getID()));

		if (getData(strSQL, v)) {
			ResultSet rs = super.getResultSet();
			try {
				
				while (rs.next()) {
					int i = 1;
					int id = rs.getInt(i++);
					int rank = rs.getInt(i++);
					int idteam = rs.getInt(i++);
					String sSname = rs.getString(i++);
					String sName = rs.getString(i++);
					int isBye = rs.getInt(i++);
					int isExhibition = rs.getInt(i++);
					int idClub = rs.getInt(i++);
					Team tm =  new Team(idteam, sSname, sName, isBye, isExhibition, idClub);
					Participant pa = new Participant(id, rank, _se, tm);
					_part.addChild(pa);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		}
		return true;

	}
	
	/**
	 * getTeamSeasonMissmatches -
	 * 
	 * We want to remove any participants that are pointing to a team that no longer exists..
	 * 
	 * @param _se
	 * @return
	 */
	public final boolean getTeamSeasonMissmatches(Season _se) {
		
		//
		// Should be triggers at some point in the database...
		//
		String strChk = "delete from gbli.participant where idparticipant in " +
				"(select idparticipant from gbli.participant where idseason = ? and idteam not in (select idteam" +
				" from gbli.team where seasontags like ?))";
		
		String strChk2 = "delete from gbli.games where participant1 in " +
				"(select idparticipant from gbli.participant where idseason = ? and idteam not in (select idteam" +
				" from gbli.team where seasontags like ?))";

		String strChk3 = "delete from gbli.games where participant2 in " +
				"(select idparticipant from gbli.participant where idseason = ? and idteam not in (select idteam" +
				" from gbli.team where seasontags like ?))";
		
		String strSQL = "select 'A', t.idteam from gbli.team t " +
				"where " +  
				"    t.seasontags like ? " + 
				"and t.idteam not in " +
				"(select p.idteam from gbli.participant p where p.idseason = ?) " +
				"union all " +
				" select 'D', p.idteam from gbli.participant p " + 
				" where  p.idseason = ? and p.idteam not in (select t.idteam " +
				" from gbli.team t where seasontags like ?) ";

		/*
		try {
			LOGGER.info("getTeamSeasonMissmatches:  Looking for orphan participants for " + _se.getSName());
			PreparedStatement ps = this.getConnection().prepareStatement(strChk);
			ps.setInt(1, _se.getID());
			ps.setString(2,("%:" + _se.getSName() + ":%"));
			ps.executeUpdate();
			LOGGER.info("getTeamSeasonMissmatches:  Looking for orphaned games " + _se.getSName());
			ps = this.getConnection().prepareStatement(strChk2);
			ps.setInt(1, _se.getID());
			ps.setString(2,("%:" + _se.getSName() + ":%"));
			ps.executeUpdate();
			ps = this.getConnection().prepareStatement(strChk3);
			ps.setInt(1, _se.getID());
			ps.setString(2,("%:" + _se.getSName() + ":%"));
			ps.executeUpdate();
			this.cleanup();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		*/
		//
		// ok.. lets pick up the adds and deletes
		//
		Vector<Object> v = new Vector<Object>();
		v.add("%:" + _se.getSName() + ":%");
		v.add(Integer.valueOf(_se.getID()));
		v.add(Integer.valueOf(_se.getID()));
		v.add("%:" + _se.getSName() + ":%");

		if (getData(strSQL, v)) {
			return true;
		}
		return false;
		
	}
	
	public final boolean getTeamSeeding(Season _se) {
		
		String strSQL = "select " + 
				" p.idparticipant, " + 
				" p.rank  " +
				" from gbli.participant p " +
				" where " +
				"    p.idseason = ? " + 
				" order by p.rank";

		Vector<Object> v = new Vector<Object>();
		v.add(Integer.valueOf(_se.getID()));

		if (getData(strSQL, v)) {
			return true;
		}
		return false;
		
	}
	
	/**
	 * updates the ranking of a given participant... This is part of the clean up and prep section of the software
	 * When we change an existing ranking.. we need to restate any games that are going on 
	 * 
	 * @param _ikey
	 * @param _irank
	 * @return
	 */
	public final boolean updatePartipantRanking(Season _se, int _ikey, int _ioldrank, int _inewrank) {
		
		LOGGER.info("updateParticipantRanking:" + _ikey + ":" + _inewrank);
		

		String strSQL = "update gbli.participant " + 
				" set rank = ?, lastupdate = now() " + 
				" where " +
				"  idparticipant = ?"; 
		String strSQL1 = "update gbli.game " +
				" set participant1 = ? where participant1 = ? and idseason = ?";

		String strSQL2  = "update gbli.game " +
				" set participant2 = ? where participant2 = ? and idseason = ?";

		try {
			PreparedStatement ps = this.getConnection().prepareStatement(strSQL);
			ps.setInt(1, _inewrank);
			ps.setInt(2, _ikey);
			ps.executeUpdate();
			this.cleanup();
			LOGGER.info("updateParticipantRanking:UPDATE COMPLETE FOR:" + _ikey + ":changed old rank (" + _ioldrank + ") to new rank (" + _inewrank + ")");
			ps = this.getConnection().prepareStatement(strSQL1);
			ps.setInt(1, _inewrank);
			ps.setInt(2, _ioldrank);
			ps.setInt(3, _se.getID());
			ps.executeUpdate();
			this.cleanup();
			ps = this.getConnection().prepareStatement(strSQL2);
			ps.setInt(1, _inewrank);
			ps.setInt(2, _ioldrank);
			ps.setInt(3, _se.getID());
			ps.executeUpdate();
			this.cleanup();
			LOGGER.info("updateParticipantRanking:Restate COMPLETE FOR:" + _ikey + ":changed participant ids from (" + _ioldrank + ") to new (" + _inewrank + ")");
			
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}
	
	public final boolean getSeasonWeeksCollection(Season _se, SeasonWeeks _sew) {

		String strSQL = 
		" SELECT " + 
		" sw.idseasonweeks, " + 
		" sw.week, " + 
		" sw.sname, " + 
		" sw.name, " +
		" sw.fromdate, " +
		" sw.todate, " +
		" (select coalesce(count(b.idteam),0) " +
		"     from gbli.blackoutteam b" +
		"     join gbli.team t on t.idteam = b.idteam" +
		"     join gbli.season s on " +
		"        t.seasontags like concat('%:',s.sname,':%') " + 
		"     and s.idseason = ?  " +
		"   where b.dategone between sw.fromdate and sw.todate) as sortme " +
		" from gbli.seasonweeks sw " +
		" where sw.tagseasonweeks = ?  and sw.isactive = 1 order by sortme desc, sw.fromdate";

		Vector v = new Vector<Object>();
		v.add(Integer.valueOf(_se.getID()));
		v.add(_se.getSeasonWeeksTag());

		if (getData(strSQL, v)) {
			ResultSet rs = super.getResultSet();
			try {
				while (rs.next()) {
					int id = rs.getInt(1);
					int week = rs.getInt(2);
					String sSname = rs.getString(3);
					String sName = rs.getString(4);
					String sFromDate = rs.getString(5);
					String sToDate= rs.getString(6);
					SeasonWeek sw =  new SeasonWeek(id, _se);
					sw.setSName(sSname);
					sw.setName(sName);
					sw.setWeek(week);
					sw.setFromDate(sFromDate);
					sw.setToDate(sToDate);
					_sew.addChild(sw);
					sw.setParent(_se);
					LOGGER.finest("getSeasonWeeks: found seasonweek for:" + _se.getSName() + ":" + sw.Dump());
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		}
		return true;

	}

	/**
	 * delParticipant - We want to remove a team from a season
	 * we:
	 *   1) Remove the record from the participant table
	 *   2) Remove any games for that season associated with that team
	 * @param _se
	 * @param _idteam
	 * @return
	 */
	public boolean delParticipant(Season _se, int _idteam) {
		
		LOGGER.info("delParticipant:" + _idteam + ":" + _se.getSName());

		String strSQL = "delete from gbli.participant " + 
				" " + 
				" where " +
				"  idteam = ? and idseason = ?"; 
		
		String strSQL2 = "delete from gbli.game " +
				" where " + 
				" idseason = ? " + 
				" and ( "  +
				" participant1 in (select rank from gbli.participant p where p.idseason = ? and p.idteam = ?) or " +
				" participant2 in (select rank from gbli.participant p where p.idseason = ? and p.idteam = ?) " +
				" ) ";					
		try {
			
			//
			// delete games.. then the participant.
			//
			PreparedStatement ps = this.getConnection().prepareStatement(strSQL2);
			ps.setInt(1, _se.getID());
			ps.setInt(2, _se.getID());
			ps.setInt(3, _idteam);
			ps.setInt(4, _se.getID());
			ps.setInt(5, _idteam);
			ps.executeUpdate();
			LOGGER.info("removeGamesForDeleted Paricipating:DEL COMPLETE FOR:" + + _se.getID() + ":" + _se.toString());
			ps = this.getConnection().prepareStatement(strSQL);
			ps.setInt(1, _idteam);
			ps.setInt(2, _se.getID());
			ps.executeUpdate();
			LOGGER.info("deleteParticipantFromSeason:DEL COMPLETE FOR:" + + _idteam + ":" + _se.toString());
			this.cleanup();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		return true;
		
	}
	
	public boolean addlParticipant(Season _se, int _idteam) {
		
		LOGGER.info("addParticipant:" + _idteam + ":" + _se.toString());
		String strSQL = "insert into gbli.participant (idteam,rank,idseason) VALUES(?,(coalesce((select max(p.rank) from gbli.participant p " +
		                                   " where p.idseason = ?),0)+1),?)";

		try {
			int i = 1;
			PreparedStatement ps = this.getConnection().prepareStatement(strSQL);
			ps.setInt(i++, _idteam);
			ps.setInt(i++, _se.getID());
			ps.setInt(i++, _se.getID());
			ps.executeUpdate();
			LOGGER.info("addParticipant: Insert COMPLETE FOR:" + + _idteam + ":" + _se.toString());
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
	
	public boolean getGames(Games _games, Season _se) {
		// TODO Auto-generated method stub
		String strSQL = 
				"SELECT " + 
				" g.idgame, " +
				" g.idteamhome, " + 
				" g.idteamaway, " + 
				" g.participant1, " +
				" p1.idparticipant, " +
				" g.participant2, " +
				" p2.idparticipant, " +
				" g.divsname, " + 
				" g.isbye, " +
				" g.isexhibition," +
				" g.idseasonweek, " +
				" g.idslot, " +
				" g.idofficialset, " +
				" g.islocked " +
				" from gbli.game g " + 
				" join gbli.participant p1 on " +
				"     p1.rank = g.participant1 " + 
				" and p1.idseason = g.idseason " +
				" join gbli.participant p2 on " +
				"     p2.rank = g.participant2 " + 
				" and p2.idseason = g.idseason " +
				" where g.idseason = ? and g.isactive = 1 order by 2";

				Vector<Integer> v = new Vector<Integer>();
				v.add(Integer.valueOf(_se.getID()));

				if (getData(strSQL, v)) {
					ResultSet rs = super.getResultSet();
					try {
						while (rs.next()) {
							int i = 1;
							int id = rs.getInt(i++);
							int idteamhome = rs.getInt(i++);
							int idteamaway = rs.getInt(i++);
							int participant1 = rs.getInt(i++);
							int idparticipant1 = rs.getInt(i++);
							int participant2 = rs.getInt(i++);
							int idparticipant2 = rs.getInt(i++);
							String sDivName = rs.getString(i++);
							int isBye = rs.getInt(i++);
							int isExhibition = rs.getInt(i++);
							int idseasonweek = rs.getInt(i++);
							int idslot = rs.getInt(i++);
							int idofficialset = rs.getInt(i++);
							int islocked = rs.getInt(i++);
							Game gm = new Game(id,null,_se);
							gm.setIdTeamHome(idteamhome);
							gm.setIdTeamAway(idteamaway);
							gm.setParticipant1(participant1);
							gm.setParticipant2(participant2);
							gm.setDivSName(sDivName);
							gm.setIsBye(isBye);
							gm.setIsExhibition(isExhibition);
							gm.setIdSeasonWeek(idseasonweek);
							gm.setIdSlot(idslot);
							gm.setIdOfficialSet(idofficialset);
							gm.setIsLocked(islocked);
							gm.setIdParticipant1(idparticipant1);
							gm.setIdParticipant2(idparticipant2);
							
							_games.addChild(gm);
							//
							// now lets get the game linked up and initialized
							//
							gm.init();
							
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}

				}
				return true;

			}		
		
	
	/**
	 * This will make sure the number of participants are reflected correctly in the Season record in the DB
	 * 
	 * @param _se
	 * @return
	 */
	public void updateSeasonTeamCount(Season _se) {
		
		LOGGER.info("updateSeasonTeamCount: for season " + _se.getName());
		String strSQL = "update gbli.season set teamcount = (select count(p.idparticipant) from gbli.participant p where p.idseason = ?) where idseason = ?";
		String strSQL1 = "update gbli.season set byeteamcount = (select count(p.idparticipant) from gbli.participant p join gbli.team t on t.idteam = p.idteam and t.isbyeteam = 1 where p.idseason = ?) where idseason = ?";
		
		try {
			int i = 1;
			PreparedStatement ps = this.getConnection().prepareStatement(strSQL);
			ps.setInt(i++, _se.getID());
			ps.setInt(i++, _se.getID());
			ps.executeUpdate();
			LOGGER.fine("updateSeasonTeamCount: updated teamcount for season " + _se.getName());
			//
			// Now lets go after byteam count
			//
			i=1;
			ps = this.getConnection().prepareStatement(strSQL1);
			ps.setInt(i++, _se.getID());
			ps.setInt(i++, _se.getID());
			ps.executeUpdate();
			LOGGER.fine("updateSeasonTeamCount: updated BYE team count for season " + _se.getName());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOGGER.info("updateSeasonTeamCount: Finished for season " + _se.getName());

	}
	
	/**
	 * genGames
	 * 
	 * This will take a look at the season.. how many teams.. and how many games they have to play.. and generate 
	 * enough games to cover the season through iteration...
	 * 
	 * if the iteration of games are smaller than the number of teams based upon the round robin matrix
	 * then we generate another iteration.. this makes sure we have enough games to pull from to piece the matchups 
	 * together.
	 * 
	 * We always want more games to choose from then possible games for the weekend.
	 * @param _se
	 */
	public void genGames(Season _se) {
		
		LOGGER.info("genGames: Starting to Gen games for season "  + _se.getName());
		String sSQL2 = "DELETE FROM GBLI.GAME where idseason = ? and iteration = ?";
		// This is the statement that for the given season.. we want call this for each iteration in 
		// the main loop.
		String sSql1 = "INSERT INTO `gbli`.`game` " +
				" (`idgame`, " +
				" `idteamhome`, " +
				" `idteamaway`, " +
				" `participant1`, " +
				" `participant2`, " +
				" `divsname`, " +
				" `idseason`, " +
				" `isbye`, " +
				" `isexhibition`," +
				" `idseasonweeks`, " +
				" `idslot`, " +
				" `idofficialset`, " +
				" `islocked`, " +
				" `iteration`) " +
				" select " +
				" 0, " +  
				" 0," + 
				" 0, " +  
				" rt.teama, " +  
				" rt.teamb, " +  
				" s.divsname, " +
				" s.idseason, " +
				" case when t1.isbyeteam = 1 or t2.isbyeteam = 1 then 1 else 0 end, " +
				" case when t1.isexhibitionteam = 1 or t2.isexhibitionteam = 1 then 1 else 0 end, " +
				" 0," +
				" 0," +
				" 0," +
				" 0," +
				" ? " +
				" from gbli.season s " +
				" JOIN gbli.rrtemplate rt on " +
				" rt.setsize = s.teamcount " +
				" join gbli.participant p1 on " +
				" p1.idseason = s.idseason and " +
				" p1.rank = rt.teama " +
				" join gbli.participant p2 on " +
				" p2.idseason = s.idseason and " +
				" p2.rank = rt.teamb " +
				" join gbli.team t1 on " +
				" t1.idteam = p1.idteam " +
				" join gbli.team t2 on " +
				" t2.idteam = p2.idteam " +
				" where s.idseason = ? " +
				" and (rt.teama,rt.teamb) not in " + 
				" ( " + 
				"   select g.participant1, g.participant2 from gbli.game g where g.idseason = s.idseason and g.iteration = ? " +
				"  union " +
				"   select g.participant2, g.participant1 from gbli.game g where g.idseason = s.idseason and g.iteration = ?  " +
				"  )";
		
			// We will always have at least one iteration.
			int iCount = 0;
			int iGames = _se.getGameCount();
			int iGamesPerIteration = _se.getTeamCount() - 1 ;
			
			LOGGER.info("genGames: Check for " + _se.getName() + ". iCount=" + iCount + ": iGames=" + _se.getGameCount() + ": Team Count-1=" + (_se.getTeamCount() - 1));
			
			//
			// Lets calculate the number of iterations now..
			//
			while (iGames > (iGamesPerIteration*iCount)) {
				iCount++;
				// Generate the games for the count here..
				try {
					int i = 1;
					PreparedStatement ps = this.getConnection().prepareStatement(sSql1);
					ps.setInt(i++, iCount);
					ps.setInt(i++, _se.getID());
					ps.setInt(i++, iCount);
					ps.setInt(i++, iCount);
					ps.executeUpdate();
					LOGGER.info("genGames: Inserted missing games for iteration :" + iCount + ": for seaon "  + _se.getName());
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
			LOGGER.info("genGames: Inserted missing games finished for season " + _se.getName());
						
		}
	
	/**
	 * Here we simply return all the Starting Weekends of all the seasons we have to look at
	 * 
	 * @param _cl
	 * @return
	 */
	public ArrayList<String> getWeekendsForAllSeasons() {
		ArrayList<String> dates = new ArrayList<String>();
		String sSQL1 = "select distinct sw.fromdate from gbli.seasonweeks sw where isActive = 1 order by 1";
		if (getData(sSQL1)) {
			ResultSet rs = super.getResultSet();
			try {
				int index = 0;
				while (rs.next()) {
					int i = 1;
					dates.add(rs.getString(i++));
				}
				this.cleanup();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return dates;
	}
	
	public ResultSet getSlotTeamplate(Club _cl, String _sDate) {
		String sSQL1 = "select " +
			"sw.fromdate,  " +
			"sw.todate,  " +
			"s.gamelength, " +
			"round((count(t.idclub)/2))  " + 
			"from gbli.seasonweeks sw  " +
			"join gbli.season s on  " +
			"s.tagseasonweeks = sw.tagseasonweeks  " +
			"join gbli.team t on " +
			"t.seasontags like concat('%:',s.sname,':%') " +
			"join gbli.club c on " +
			"c.idclub = t.idclub " +
			"and c.idclub = ? " +
			"where ? between sw.fromdate and sw.todate  " +
			"group by t.idclub, c.sname, sw.fromdate, sw.todate, s.gamelength, t.isbyeteam ";

		Vector<Object> v = new Vector<Object>();
		v.add(Integer.valueOf(_cl.getID()));
		v.add(_sDate);

		if (getData(sSQL1, v)) {
			return super.getResultSet();
		}
		
		return null;
	}
	
	/**
	 * Here we check presence for a slot with the given pattern.. if not present.. we create.
	 * 
	 * @param _cl
	 * @param _rank
	 * @param _sFromDate
	 * @param _sToDate
	 * @param _sGameLen
	 */
	public void synchSlot(Club _cl,int _rank,String _sFromDate,String _sToDate,String _sGameLen) {
		
		String sSQL1 = "Select count(idslot) from gbli.slot where idclub = ? and rank = ? and fromdate = ? and todate = ? and gamelength = ?";
		String sSQL2 = "insert into gbli.slot (idclub, rank, fromdate, todate, gamelength) values(?,?,?,?,?)";
		
		Vector<Object> v = new Vector<Object>();
		v.add(Integer.valueOf(_cl.getID()));
		v.add(Integer.valueOf(_rank));
		v.add(_sFromDate.trim());
		v.add(_sToDate.trim());
		v.add(_sGameLen.trim());
		int icount = 0;
		if (getData(sSQL1, v)) {
			ResultSet rs = super.getResultSet();
			try {
				while (rs.next()) {
					icount = rs.getInt(1);
					LOGGER.info("synchSlot: # of slots found is (" + icount + ") for :" + _cl.getSName() + " on rank=" + _rank + ". fd=" + _sFromDate + ". tdate=" + _sToDate + ". gl=" + _sGameLen);
							
				}
				this.cleanup();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//
		// if there are not records .. lets insert the slot
		//
		if (icount == 0) {
			try {
				int i = 1;
				PreparedStatement ps = this.getConnection().prepareStatement(sSQL2);
				ps.setInt(i++, _cl.getID());
				ps.setInt(i++, _rank);
				ps.setString(i++, _sFromDate);
				ps.setString(i++, _sToDate);
				ps.setString(i++, _sGameLen);
				ps.executeUpdate();
				LOGGER.info("synchSlot: Inserted new slot for :" + _cl.getSName() + _rank + ":"  + _cl.getSName() +":" + _sFromDate + ":" + _sToDate + ":" + _sGameLen);
					
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.cleanup();
		}
	}
	
	/**
	 * For a given club.. this remove any slots that exist prior to the earliest start date of the club
	 *
	 * @param _cl
	 * @param _sDate
	 */
	public void removeEarlySlots (Club _cl, String _sFromDate) {
		
		String sSQL1 = "delete from gbli.slot where idclub = ? and fromdate < ?";

		try {
			int i = 1;
			PreparedStatement ps = this.getConnection().prepareStatement(sSQL1);
			ps.setInt(i++, _cl.getID());
			ps.setString(i++, _sFromDate);
			ps.executeUpdate();
			LOGGER.info("removeEarlySlots: Cleaned up early slots for :" + _cl.getSName() + ":" + _sFromDate);
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.cleanup();
	
	}
	
	/**
	 * For a given club.. this remove any slots that exist prior to the earliest start date of the club
	 *
	 * @param _cl
	 * @param _sDate
	 */
	public void removeLateSlots (Club _cl, String _sFromDate) {
		
		String sSQL1 = "delete from gbli.slot where idclub = ? and fromdate > ?";

		try {
			int i = 1;
			PreparedStatement ps = this.getConnection().prepareStatement(sSQL1);
			ps.setInt(i++, _cl.getID());
			ps.setString(i++, _sFromDate);
			ps.executeUpdate();
			LOGGER.info("removeLateSlots: Cleaned up late slots for :" + _cl.getSName() + ":" + _sFromDate);
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.cleanup();

	
	}
	
	/**
	 * synchClubSlots  
	 * 
	 * This will take all the club slots given and assign them to the generic slots created by the software
	 * It assigned them to open slots only.
	 * 
	 *  We need to check to make sure we do not have any clubslots assigned to two diffent genslots
	 *  
	 *  we then have to try to find "best fit" club slots to the remaining open slots in the system
	 *  
	 *  Finally.. we have to remove any excess generated slots that do not have a club slot assigned to it.
	 *  
	 * @param _cl
	 */
	public void synchClubSlots(Club _cl) {

		String sSQL1 = " update gbli.slot sl set idclubslot =  " +
				" coalesce(( " +
				" select cs.idclubslot from gbli.clubslot cs  " +
				" where  " +
				"     cs.idclub = sl.idclub  " +
				" and cs.rank = sl.rank  " +
				" and cs.gamelength = sl.gamelength  " +
				" and cs.isalternate = 0 " +
				" and cs.actdate between sl.fromdate and sl.todate),0) " +
				" where (sl.idclubslot = 0) and sl.idclub = ?";

		try {
			int i = 1;
			PreparedStatement ps = this.getConnection().prepareStatement(sSQL1);
			ps.setInt(i++, _cl.getID());
			LOGGER.info("synchClub=" + _cl.getID());
			ps.executeUpdate();
			LOGGER.info("synchClubSlots: Merged slots to club slots for club :" + _cl.getSName());
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		this.cleanup();
	}
	
	/** 
	 * fillMisfitSlots - This will simply loop through all the unassigned generated slots and fill them in with unused 
	 * club slots..
	 * @param _cl
	 */
	public void fillMisfitSlots (Club _cl, String _sGameLength) {
		
		String sSQLCount = "select count(sl.idslot) from gbli.slot sl where (sl.idclubslot = 0) and sl.idclub = ? and " +
		" 0 < coalesce((select count(cs.idclubslot) from gbli.clubslot cs where cs.idclub = sl.idclub),0)";
		String sSQLAMark = " update gbli.clubslot set isalternate = 1 where idclubslot = ?";
		
		//
		// alternate only candidate
		//
		String sSQL1 = "update gbli.slot sl " +
				" set sl.idclubslot =  " +
				" coalesce( " +
				" (select id from  " +
				" (select min(cs.idclubslot) as id from gbli.clubslot cs  " +
				"   where  " +
				"       cs.idclub = ? and cs.gamelength = ?" +
				"   and cs.isalternate = 1 " +
				"   and cs.idclubslot not in  " +
				" 	(select sl2.idclubslot from gbli.slot sl2  " +
				"      where sl2.idclub = cs.idclub and sl2.gamelength = cs.gamelength)  " +
				" ) as tmptable),0) " +
				"  " +
				" where  " +
				" sl.idslot = (select slotid from  " +
				" (SELECT min(sl3.idSlot) as slotid FROM gbli.slot sl3  " +
				" WHERE sl3.IDCLUB = ? and sl3.gamelength = ? and (sl3.idclubslot = 0 or sl3.idclubslot is null)) as tmptable2) " +
				" and sl.idclub = ? and sl.gamelength = ? " ;
		
		//
		// Now use any slot to fill a vacant one
		// mark it as used as an alternate
		//
		String sSQL2 = "update gbli.slot sl " +
				" set sl.idclubslot =  " +
				" coalesce( " +
				" (select id from  " +
				" (select min(cs.idclubslot) as id from gbli.clubslot cs  " +
				"   where  " +
				"       cs.idclub = ? and cs.gamelength = ?" +
				"   and cs.idclubslot not in  " +
				" 	(select sl2.idclubslot from gbli.slot sl2  " +
				"      where sl2.idclub = cs.idclub and sl2.gamelength = cs.gamelength)  " +
				" ) as tmptable),0) " +
				"  " +
				" where  " +
				" sl.idslot = (select slotid from  " +
				" (SELECT min(sl3.idSlot) as slotid FROM gbli.slot sl3  " +
				" WHERE sl3.IDCLUB = ? and sl3.gamelength = ? and (sl3.idclubslot = 0 or sl3.idclubslot is null)) as tmptable2) " +
				" and sl.idclub = ? and sl.gamelength = ? " ;
		
		Vector<Object> v = new Vector<Object>();
		v.add(Integer.valueOf(_cl.getID()));
		if (getData(sSQLCount, v)) {
			try {
				getResultSet().next();
				int iCount = getResultSet().getInt(1);
				for	 (int iv = 1;iv<=iCount;iv++) {
					PreparedStatement ps = this.getConnection().prepareStatement(sSQL1);
					int i = 1;
					ps.setInt(i++, _cl.getID());
					ps.setString(i++, _sGameLength);
					ps.setInt(i++, _cl.getID());
					ps.setString(i++, _sGameLength);
					ps.setInt(i++, _cl.getID());
					ps.setString(i++, _sGameLength);
					ps.executeUpdate();
					LOGGER.info("fillMisfitSlots: alternate slot pass:" + iv + " of " + iCount + " for Club " +  _cl.getSName() + " for gamelength:" + _sGameLength);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.cleanup();
		}

		//
		// now lets go after any slot.. and mark the club slot as used by an alternate 
		//
		v = new Vector<Object>();
		v.add(Integer.valueOf(_cl.getID()));
		if (getData(sSQLCount, v)) {
			try {
				getResultSet().next();
				int iCount = getResultSet().getInt(1);
				for	 (int iv = 1;iv<=iCount;iv++) {
					PreparedStatement ps = this.getConnection().prepareStatement(sSQL2);
					int i = 1;
					ps.setInt(i++, _cl.getID());
					ps.setString(i++, _sGameLength);
					ps.setInt(i++, _cl.getID());
					ps.setString(i++, _sGameLength);
					ps.setInt(i++, _cl.getID());
					ps.setString(i++, _sGameLength);
					ps.executeUpdate();
					LOGGER.info("fillMisfitSlots: any slot pass:" + iv + " of " + iCount + " for Club " +  _cl.getSName() + " for gamelength:" + _sGameLength);
					this.cleanup();
					
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.cleanup();
		}

	}
	
	/** 
	 * createBonusSlots - This will simply look for any extra slots that were given.   For each one found we:
	 * Create a Slot for it with the appropriate from, to date
	 * make the ranking 99
	 * tie the current club slot you are on the the newly generated slot.
	 * We will need to find a seasonweek the fits between the actdate.. any seasonweek will do for the given club
	 * @param _cl
	 */
	public void createBonusSlots (Club _cl) {
		
		String sSQLBonusSlot = " insert into gbli.slot " + 
				" (idclub,gamelength, fromdate, todate, rank, idclubslot)" + 
				" " + 
				" (select  distinct cl.idclub, cs.gamelength, sw.fromdate, sw.todate,99, cs.idclubslot" + 
				" from gbli.club cl " +   
				" join gbli.clubslot cs on  " +
				"     cs.idclub = cl.idclub and  " +
				" 	  cs.idclubslot not in (select sl.idclubslot from gbli.slot sl where sl.idclub = cl.idclub) " +
				" join gbli.seasonweeks sw on " +
				"     cs.actdate between sw.fromdate and sw.todate " +
				" join gbli.season se on " +
				"     se.tagseasonweeks = sw.tagseasonweeks " +
				" join gbli.team t on  " +
				"     t.seasontags like concat('%:',se.sname,':%')   " +
				" where cl.idclub = ?)";
			try {
				PreparedStatement ps = this.getConnection().prepareStatement(sSQLBonusSlot);
				int i = 1;
				ps.setInt(i++, _cl.getID());
				int icount = ps.executeUpdate();
				LOGGER.info("fillMisfitSlots: createBonusSlots created:" + icount + " new slots for Club " +  _cl.getSName());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.cleanup();
		
	}
	
	/**
	 * revoveExcessSlots - this removes any uneeded planned slots that are about the slot count and not assigned to any 
	 * club slot...
	 * @param _cl
	 * @param _sFromDate
	 * @param _sToDate
	 * @param _sGameLen
	 * @param _iSlotCount
	 */
	public void removeExcessSlots(Club _cl, String _sFromDate, String _sToDate, String _sGameLen, int _iSlotCount) {
		String sSQL1 = " delete from gbli.slot where " + 
				" idclub = ? and rank > ? and fromdate = ? and todate = ? and gamelength = ? and idclubslot = 0 ";
			try {
				PreparedStatement ps = this.getConnection().prepareStatement(sSQL1);
				int i = 1;
				ps.setInt(i++, _cl.getID());
				ps.setInt(i++, _iSlotCount);
				ps.setString(i++, _sFromDate);
				ps.setString(i++, _sToDate);
				ps.setString(i++, _sGameLen);
				int icount = ps.executeUpdate();
				LOGGER.info("removeExcessSlots: removed:" + icount + " extra generated slots for Club " +  _cl.getSName());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.cleanup();
		
	}
	
	public ArrayList<Integer> getAvailableParticipants(SeasonWeek _sw) {
		
		ArrayList<Integer> keys = new ArrayList<Integer>();
		String sSQL1 = "select p.idparticipant," +
				" case " +
				"  when " +
				"   ((select coalesce(count(bot.dategone),0) from gbli.blackoutteam bot where bot.dategone between ? and ? and bot.idteam in " +
				"        (select p3.idteam from gbli.participant p3 where p3.idseason = se.idseason and p3.idparticipant = p.idparticipant)) > 0) then -1 " +
				"  else " +
				"    floor(rand() * 100) " +
				" end " +
				"  from gbli.seasonweeks sw " + 
				" join gbli.season se on " +
			    "   sw.tagseasonweeks = se.tagseasonweeks " +
				" join gbli.participant p on " +
				"	se.idseason = p.idseason  " +
				" join gbli.team tm on " +
				"    tm.idteam = p.idteam " +
				" left join gbli.game ga on " +
				" 	(tm.idteam = ga.idteamhome or tm.idteam = ga.idteamaway) " +
				" and ga.idslot <> 0 and ga.idseasonweeks = sw.idseasonweeks " +
				" where sw.idseasonweeks = ? and se.idseason = ? and ga.idgame is null order by 2,1";
		
		Vector v = new Vector<Object>();
		v.add(_sw.getFromDate());
		v.add(_sw.getToDate());
		LOGGER.info("getAvailableParticipants:P1:idseasonweeks:" + _sw.getID());
		v.add(Integer.valueOf(_sw.getID()));
		LOGGER.info("getAvailableParticipants:P2:idseason:" + _sw.getParent().getID());
		v.add(Integer.valueOf(_sw.getParent().getID()));
		if (getData(sSQL1, v)) {
			try {
				while (getResultSet().next()) {
				  keys.add(Integer.valueOf(getResultSet().getInt(1)));	
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		this.cleanup();
		LOGGER.fine("getAvailableParticipants:for seasonweek:all available participants:" + keys);
		return keys;
		
	}
	
	public ArrayList<Integer> getAvailableMatchups(Participant _pa, Season _se, SeasonWeek _sw, boolean _sq) {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		String sSQL1 = 
				" select matchup," +
				" case " +
				"  when 1 = ? then " +
				" ((select coalesce(count(g2.idgame),0) from gbli.game g2 where g2.idteamhome = ? and idseason = ?) + " +
					"(select coalesce(count(g2.idgame),0) from gbli.game g2 where g2.idteamaway = ? and idseason = ?))" +
//				"  coalesce((select sum(tmp2.cnt) from " +
//				"    (select count(gm.idteamhome) as cnt, p.idparticipant as part " +
//				"     from gbli.game gm " +
//				"      join gbli.participant p on " +
//				"      p.idteam = gm.idteamhome " +
//				"      and p.idseason = gm.idseason " +
//				"     where " +
//				"          gm.isbye = 0 " +
//				"       and gm.idslot <> 0  and gm.isactive = 1 " +
//				"       and gm.idseason = ?  group by p.idparticipant " +
//				"    union all " +
//				"     select count(gm.idteamaway) as cnt, p.idparticipant as part " +
//				"      from gbli.game gm " +
//				"       join gbli.participant p on " +
//				"           p.idteam = gm.idteamaway " +
//				"       and p.idseason = gm.idseason " +
//				"       where gm.isbye = 0 " +
//				"       and gm.idslot <> 0  and gm.isactive = 1" +
//				"        and gm.idseason = ?  group by p.idparticipant" +
//				"    ) as tmp2 where part = tmp.matchup group by part),0) " +
				"  when " +
				"   ((select coalesce(count(bot.dategone),0) from gbli.blackoutteam bot where bot.dategone between ? and ? and bot.idteam in " +
				"        (select p3.idteam from gbli.participant p3 where p3.idseason = ? and p3.idparticipant = tmp.matchup)) > 0) then -1 " +
				"  else " +
				"    floor(rand() * 100) " +
				" end " +
				" from (select " +
				" case when ga.participant1 = ?  then (select p.idparticipant from gbli.participant p " +
				"                          where p.idseason = ga.idseason and p.rank = ga.participant2) " +
				" when ga.participant2 = ?  then (select p.idparticipant from gbli.participant p  " +
				"                          where p.idseason = ga.idseason and p.rank = ga.participant1)  " +
				" 	end as matchup " +
				" from gbli.game ga " +
				" where  " +
				" ga.idslot = 0 and (ga.participant1 = ? or ga.participant2 = ?)  " +
				" and ga.idseason = ? and ga.isactive = 1 " +
				" and ga.iteration = (select coalesce(min(gm2.iteration)) from gbli.game gm2 " +
				" where gm2.idseason = ? and gm2.isactive = 1 and gm2.idslot = 0 and (participant1 = ? or participant2 = ?))) as tmp" +
				" where matchup in " +
				" (select p.idparticipant from gbli.seasonweeks sw " + 
				" join gbli.season se on " +
			    "   sw.tagseasonweeks = se.tagseasonweeks " +
				" join gbli.participant p on " +
				"	se.idseason = p.idseason  " +
				" join gbli.team tm on " +
				"    tm.idteam = p.idteam " +
				" left join gbli.game ga on " +
				" 	(tm.idteam = ga.idteamhome or tm.idteam = ga.idteamaway) " +
				" and ga.idslot <> 0 and ga.isactive = 1 and ga.idseasonweeks = sw.idseasonweeks " +
				" where sw.idseasonweeks = ? and se.idseason = ? and ga.idgame is null) or 1 = ? order by 2";
	
		LOGGER.info("pRank:"+ _pa.getRank() + ":idseason:" + _se.getID() + ":squeeze:" + _sq);
		Vector v = new Vector<Object>();
		v.add(Integer.valueOf((_sq ? 1: 0)));
//		v.add(Integer.valueOf(_se.getID()));
//		v.add(Integer.valueOf(_se.getID()));
		v.add(Integer.valueOf(_pa.getTeam().getID()));
		v.add(Integer.valueOf(_se.getID()));
		v.add(Integer.valueOf(_pa.getTeam().getID()));
		v.add(Integer.valueOf(_se.getID()));
		v.add(_sw.getFromDate());
		v.add(_sw.getToDate());
		v.add(Integer.valueOf(_se.getID()));
		v.add(Integer.valueOf(_pa.getRank()));
		v.add(Integer.valueOf(_pa.getRank()));
		v.add(Integer.valueOf(_pa.getRank()));
		v.add(Integer.valueOf(_pa.getRank()));
		v.add(Integer.valueOf(_se.getID()));
		v.add(Integer.valueOf(_se.getID()));
		v.add(Integer.valueOf(_pa.getRank()));
		v.add(Integer.valueOf(_pa.getRank()));
		v.add(Integer.valueOf(_sw.getID()));
		v.add(Integer.valueOf(_se.getID()));
		v.add(Integer.valueOf((_sq ? 1: 0)));

		if (getData(sSQL1, v)) {
			try {
				while (getResultSet().next()) {
				  keys.add(Integer.valueOf(getResultSet().getInt(1)));	
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			System.exit(0);
			}
		}
		this.cleanup();
		LOGGER.fine("getAvailableMatchups:all available matchups:" + keys);
		return keys;

	}
	
	public ArrayList<Integer> getAvailableSlotIDs(Club _cl, Team _tm, SeasonWeek _sw) {
		LOGGER.info("getAvailableSlotIDs:" + _tm.getSName() + ":" + _sw.getID());
		ArrayList<Integer> keys = new ArrayList<Integer>();
		String sSQL1 = "SELECT sl.idslot FROM GBLI.Slot sl " +
				" join gbli.seasonweeks sw on " +
				"	sw.idseasonweeks = ? " +
				" join gbli.team t on " +
				"	   t.idteam = ? " +
				"  join gbli.season se on " +
                "      t.seasontags like concat(':%',se.sname,':%') " +
                "  and se.tagseasonweeks = sw.tagseasonweeks " +
                //
                // make sure the slot is not used
                //
                " join gbli.clubslot cs on " +
				"	    cs.idclubslot = sl.idclubslot " +
                " left join gbli.game ga on " +
                "    ga.idslot = sl.idslot " +
				"	WHERE  " +
				"	    sl.idclub = ?  " +
				"	and cs.actdate between sw.fromdate and sw.todate " +
				"   and cs.actdate between se.startdate and se.enddate " +  // Added to help define scope of boundaries
				"	and sl.gamelength = se.gamelength " +
				"   and ga.idgame is null" + 
				"	and 1 = (case  " +
				"	when t.teamtag = cs.teamtag then 1 " +
				"	when cs.teamtag = t.teamtag and cs.teamtag <> 'NONE' then 1 " +
				"	else 0 end) " +
				"	order by actdate, starttime ";
		
		// if its a bye team.. return slot -1.. they always have slots..
		if (_tm.getIsByeTeam() == 1) {
			keys.add(Integer.valueOf(-1));	
			LOGGER.info("getAvailableSlotIDs: BYE TEAM.. always give slot -1 " + _tm.getSName() + ":" + _sw.getID() + "[" + keys + "]");
			return keys;
		}
		
		Vector v = new Vector<Object>();
//		LOGGER.fine("getAvailableSlotIDs:P1:idseasonseeks:" + _sw.getID());
		v.add(Integer.valueOf(_sw.getID()));
//		LOGGER.fine("getAvailableSlotIDs:P2:idteam:" + _tm.getID());
		v.add(Integer.valueOf(_tm.getID()));
//		LOGGER.fine("getAvailableSlotIDs:P3:idclub:" + _cl.getID());
		v.add(Integer.valueOf(_cl.getID()));
		if (getData(sSQL1, v)) {
			try {
				while (getResultSet().next()) {
				  keys.add(Integer.valueOf(getResultSet().getInt(1)));	
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}

		}
		this.cleanup();
		LOGGER.info("getAvailableSlotIDs: found  the following slots for " + _tm.getSName() + ":" + _sw.getID() + "[" + keys + "]");
		if (keys.size() == 0) {
			LOGGER.info("getAvailableSlotIDs: *** WARNING *** NO AVAILABLE SLOTS for  " + _tm + ":" + _sw.toString());
		}
		return keys;

	}
	
	/**
	 *  refresh - TeamGameInfo .. updates all the pertinant information you want to track for a team
	 *  from the database
	 * @param _tgi
	 */
	public void refresh(TeamGameInfo _tgi, Season _se) {
		String strSQL =
		" select sum(TOTALCOUNT), sum(HOMECOUNT) as homecount, sum(AWAYCOUNT) as awaycount, sum(BYECOUNT) as byecount from " +
				" (select 0 as TOTALCOUNT, count(ga.idgame) AS HOMECOUNT, 0 AS AWAYCOUNT, 0 AS BYECOUNT from gbli.game ga " +
				" where " +
				" ga.idteamhome = ? and ga.idslot > 0 and isbye = 0 " +
				" and ga.idseason = ? " +
				" union all " +
				" select 0 as TOTALCOUNT, 0 as HOMECOUNT, count(ga.idgame) AS AWAYCOUNT, 0 AS BYECOUNT from gbli.game ga " +
				" where " +
				" ga.idteamaway = ? and ga.idslot > 0 and isbye = 0 " +
				" and ga.idseason = ? " +
				" union all " +
				" select 0 as TOTALCOUNT, 0 as HOMECOUNT, 0 as AWAYCOUNT, count(ga.idgame) AS ExCount from gbli.game ga " +
				" where " +
				" (ga.idteamaway = ? or ga.idteamhome = ?) and isexhibition = 1 " +
				" and ga.idseason = ? " +
				" union all " +
				" select count(ga.idgame), 0 as HOMECOUNT, 0 as AWAYCOUNT, 0 AS BYECOUNT from gbli.game ga " +
				" where " +
				" (ga.idteamaway = ? or ga.idteamhome = ?) and ga.idslot > 0 " +
				" and ga.idseason = ? " +
				" ) as tmp1 ";
		
		String strSQL2 =
				" select dategone from gbli.blackoutteam where idteam = ? ";
			
		Team tm = (Team)_tgi.getParent();
		Vector v = new Vector<Object>();
		v.add(Integer.valueOf(tm.getID()));
		v.add(Integer.valueOf(_se.getID()));
		v.add(Integer.valueOf(tm.getID()));
		v.add(Integer.valueOf(_se.getID()));
		v.add(Integer.valueOf(tm.getID()));
		v.add(Integer.valueOf(tm.getID()));
		v.add(Integer.valueOf(_se.getID()));
		v.add(Integer.valueOf(tm.getID()));
		v.add(Integer.valueOf(tm.getID()));
		v.add(Integer.valueOf(_se.getID()));
		if (getData(strSQL, v)) {
			try {
				while (getResultSet().next()) {
					_tgi.setTotalGames(getResultSet().getInt(1));
					_tgi.setHomeGames(getResultSet().getInt(2));
					_tgi.setAwayGames(getResultSet().getInt(3));
					_tgi.setExGames(getResultSet().getInt(4));
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.info(e.getMessage());
			}
		}
		
		v = new Vector<Object>();
		v.add(Integer.valueOf(tm.getID()));
		if (getData(strSQL2, v)) {
			try {
				while (getResultSet().next()) {
					_tgi.getHmBOD().put(getResultSet().getString(1),"");
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.info(e.getMessage());
			}
		}
		
		this.cleanup();
	}

	/**
	 * This bonds a game to a slot for a given participant paring
	 * @param _se
	 * @param _sw
	 * @param _tmMain
	 * @param _tmMatch
	 * @param _iSlotID
	 */
	public void scheduleGame(Season _se, SeasonWeek _sw, Participant _pMain, Participant _pMatch, Integer _iSlotID, boolean _bumpon) {

		String sSQLC = "select idgame from gbli.game where idslot = ?";
		
		String sSQL1  = "update gbli.game " +
						" set idteamhome = ?, " +
						" idteamaway = ?, " +
						" idseasonweeks = ?," +
						" idslot = ?, " + 
						" islocked = ? " +
						" where " +
						" idseason = ? and " +
						" ((participant1 = ? and participant2 = ?) or (participant2 = ? and participant1 = ?))" + 
						" and idslot = 0 and " +
						" iteration = " +
						"	(select coalesce(min(iter),1) " +
						" 		from ( " +
									"select gm2.iteration as iter " +
									" from gbli.game gm2 " +
									" where " +
									" gm2.idseason = ? and" +
									" gm2.idslot = 0 and ((participant1 = ? and participant2 = ?) or (participant2 = ? and participant1 = ?)) ) as tmp)";

			if (_iSlotID > 0) {
				Vector v = new Vector<Object>();
				v.add(Integer.valueOf(_iSlotID));
				if (getData(sSQLC, v)) {
					try {
						while (getResultSet().next()) {
							LOGGER.info("ATTEMPTING TO USE A FILLED SLOT!!" + _iSlotID);
							System.exit(0);
						}
					
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LOGGER.info(e.getMessage());
					}
				}
			}
		try {
			PreparedStatement ps = this.getConnection().prepareStatement(sSQL1);
			int i = 1;
			LOGGER.info("P" + i + ":idhometeam:" + _pMain.getTeam().getID());
			ps.setInt(i++, _pMain.getTeam().getID());
			LOGGER.info("P" + i + ":idawayteam:" + _pMatch.getTeam().getID());
			ps.setInt(i++, _pMatch.getTeam().getID());
			LOGGER.info("P" + i + ":idseasonweeks:" + _sw.getID());
			ps.setInt(i++, _sw.getID());
			LOGGER.info("P" + i + ":idslot:" + _iSlotID.intValue());
			ps.setInt(i++, _iSlotID.intValue());
			LOGGER.info("P" + i + ":isbumpsticky:" + _bumpon);
			ps.setInt(i++, (_bumpon ? 1: 0));
			LOGGER.info("P" + i + ":idseason:" + _se.getID());
			ps.setInt(i++, _se.getID());
			LOGGER.info("P" + i + ":participant1:" + _pMain.getRank());
			ps.setInt(i++, _pMain.getRank());
			LOGGER.info("P" + i + ":participant2:" + _pMatch.getRank());
			ps.setInt(i++, _pMatch.getRank());
			LOGGER.info("P" + i + ":participant2:" + _pMain.getRank());
			ps.setInt(i++, _pMain.getRank());
			LOGGER.info("P" + i + ":participant1:" + _pMatch.getRank());
			ps.setInt(i++, _pMatch.getRank());
			LOGGER.info("P" + i + ":idseason:" + _se.getID());
			ps.setInt(i++, _se.getID());
			LOGGER.info("P" + i + ":participant1:" + _pMain.getRank());
			ps.setInt(i++, _pMain.getRank());
			LOGGER.info("P" + i + ":participant2:" + _pMatch.getRank());
			ps.setInt(i++, _pMatch.getRank());
			LOGGER.info("P" + i + ":participant2:" + _pMain.getRank());
			ps.setInt(i++, _pMain.getRank());
			LOGGER.info("P" + i + ":participant1:" + _pMatch.getRank());
			ps.setInt(i++, _pMatch.getRank());
			ps.executeUpdate();
			LOGGER.info("scheduleGame: game scheduled between:" + _pMain + " and " + _pMatch + " for sw:" + _sw.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		this.cleanup();
		
			
	}
	
	/**
	 * This backs out a seasonweek for a schedule
	 * 
	 * @param _se
	 * @param _sw
	 */
	public void backoutSchedule(Season _se, SeasonWeek _sw, boolean _hardbump) {
		
		String sSQL1 = "update gbli.game  set idslot = 0, idseasonweeks = 0, idteamhome = 0, idteamaway = 0, islocked = 0 where " +
				" idseason = ? and idseasonweeks = ? " + (_hardbump ? "" : " and islocked = 0");

		try {
			PreparedStatement ps = this.getConnection().prepareStatement(sSQL1);
			int i = 1;
			ps.setInt(i++, _se.getID());
			ps.setInt(i++, _sw.getID());
			ps.executeUpdate();
			LOGGER.info("backoutSchedule: " + (_hardbump ? " HARD BUMP " : "") + "removed schedule for " + _se.getSName() + " and seasonweek " + _sw);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		this.cleanup();
				
	}
	
	/**
	 * order by 
	 * @param _se
	 * @param _part
	 */
	public void getAllAvailableSlots(Season _se, Participant _part) {

		_part.resetSlotsAvail();
		
		String sSQL1 = "select  sl.idslot, cs.actdate, cs.starttime from participant p  " +
			" join gbli.team t on " +
			"     t.idteam = p.idteam " +
			" join gbli.club c on " +
			"     c.idclub = t.idclub " +
			" join gbli.season s on  " +
			"     s.idseason = ? " +
			" join gbli.seasonweeks sw on  " +
			"     sw.tagseasonweeks = s.tagseasonweeks " +
			" join gbli.clubslot cs on " +
			"     cs.idclub = t.idclub " +
			"     and cs.actdate between s.startdate and s.enddate " +
			"   and cs.actdate between sw.fromdate and sw.todate " +
			" join gbli.slot sl on " +
			"     sl.idclubslot = cs.idclubslot " +
			"	and sl.gamelength = s.gamelength " +
			" left join gbli.game g on " +
			"     g.idslot = sl.idslot " +
			" where p.idparticipant = ? " +
			"     and g.idgame is null " +
			" and 1 = (case  when t.teamtag = cs.teamtag then 1  " +
			"  				  when cs.teamtag = t.teamtag and cs.teamtag <> 'NONE' then 1 " +
			"				  else 0 end) ";
		Vector v = new Vector<Object>();
		v.add(Integer.valueOf(_se.getID()));
		v.add(Integer.valueOf(_part.getID()));
		if (getData(sSQL1, v)) {
			try {

				while (getResultSet().next()) {
					Slot sl = new Slot(getResultSet().getInt(1),getResultSet().getString(2),getResultSet().getString(3),0);
					_part.getSlotsAvail().add(sl);
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.info(e.getMessage());
			}
		}
		
		this.cleanup();
		LOGGER.info("getAllAvailableSlots for (" + _part.getTeam().getSName() + ") are:" + _part.getSlotsAvail());

		
		
	}
	
	
	/**
	 * order by 
	 * @param _se
	 * @param _part
	 */
	public void getAllUsedSlots(Season _se, Participant _part) {
		
		_part.resetSlotsPlayed();

		String sSQL1 = "select sl.idslot, cs.actdate, cs.starttime, (case when gm.idteamhome = p.idteam then gm.idteamaway else gm.idteamhome end) " +
	     " from gbli.game gm "+
	     " join gbli.participant p on "+
	     " ( p.idteam = gm.idteamhome or p.idteam = gm.idteamaway) "+
	     " and p.idseason = gm.idseason "+
	     " join gbli.season s on "+
	     " s.idseason = gm.idseason "+
	     " join gbli.slot sl on "+
	     " gm.idslot = sl.idslot "+
	     " join gbli.clubslot cs on "+ 
	     " cs.idclubslot = sl.idclubslot " +
	     " and p.idseason = gm.idseason " +
	     " where " +
	     "   gm.isbye = 0 " +
	     "  and gm.idslot <> 0 " +
	     " and gm.idseason = ?  " +
	     " and p.idparticipant = ? ";
		Vector v = new Vector<Object>();
		v.add(Integer.valueOf(_se.getID()));
		v.add(Integer.valueOf(_part.getID()));
		if (getData(sSQL1, v)) {
			try {

				while (getResultSet().next()) {
					Slot sl = new Slot(getResultSet().getInt(1),getResultSet().getString(2),getResultSet().getString(3),
							getResultSet().getInt(4));
					_part.getSlotsPlaying().add(sl);
					//LOGGER.info("SLOT:Participant: " + _part + "..Playing on=" + sl);
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.info(e.getMessage());
			}
		}
		
		this.cleanup();

	}
	
	public void getSlotsForMatchup(ArrayList<Integer> _slotList, Participant _part) {
		
		String sSlotIDs = "";
		_part.resetSlotsMatchup();

		for (Integer Slotid : _slotList) {
			sSlotIDs = sSlotIDs + Slotid.toString() + ",";
		}
		if (sSlotIDs.isEmpty()) {
			LOGGER.info("getSlotsForMatchup ... is empty.. nothing to parse");
			return;
		}

		sSlotIDs = sSlotIDs.substring(0,sSlotIDs.length()-1);
		LOGGER.info("getSlotsForMatchup, sSlotIDs=" + sSlotIDs);
		
		String sSQL1 = "select sl.idslot, cs.actdate, cs.starttime  " +
				"from gbli.slot sl " + 
	     " join gbli.clubslot cs on "+ 
	     " cs.idclubslot = sl.idclubslot " +
	     " where  sl.idslot in (:SLOTS) ";
		
		sSQL1 = sSQL1.replace(":SLOTS", sSlotIDs);
		
		if (getData(sSQL1)) {
			try {
				while (getResultSet().next()) {
					Slot sl = new Slot(getResultSet().getInt(1),getResultSet().getString(2),getResultSet().getString(3),
							0);
					_part.getSlotsMatchup().add(sl);
					//LOGGER.info("SLOT:getSlotsForMatchup: " + _part + "..Playing on=" + sl);
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.info(e.getMessage());
			}
		}
		LOGGER.info("getSlotsForMatchup:" + _part.getSlotsMatchup());
		this.cleanup();

	}
	
	
	public boolean checkclubblock (Participant _p1, Participant _p2, Season _se) {

		boolean bok = true;
		String sSQL1 = "Select 1 from gbli.clubblock cb " +
		" join gbli.team t1 on " +
		"    t1.idteam = ? " +
		" join gbli.team t2 on " +
		"    t2.idteam = ? " +
		"where ((cb.idclub1 = t1.idclub and cb.idclub2 = t2.idclub) or (cb.idclub1 = t2.idclub and cb.idclub2 = t1.idclub)) and cb.idseason = ?";
		
		Vector v = new Vector<Object>();
		v.add(Integer.valueOf(_p1.getTeam().getID()));
		v.add(Integer.valueOf(_p2.getTeam().getID()));
		v.add(Integer.valueOf(_se.getID()));
		if (getData(sSQL1, v)) {
			try {

				while (getResultSet().next()) {
					bok = false;
					LOGGER.info("CB:Participant: " + _p1 + " cannot play " + _p2);
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.info(e.getMessage());
			}
		}
		cleanup();
		return bok;
		
	}
	
	public boolean checkHomeOnly (Participant _p1, String _strTargetDate) {

		boolean bok = false;
		String sSQL1 = "Select dategone from gbli.team tm1 " +
				" join gbli.club cl on " +
				"    cl.idclub = tm1.idclub " +
		" join gbli.blackoutclub boc on " +
		"    cl.idclub  = boc.idclub " +
		" and boc.notravel = 1 " +
		"  where tm1.idteam = ? ";
		Vector v = new Vector<Object>();
		v.add(Integer.valueOf(_p1.getTeam().getID()));
		if (getData(sSQL1, v)) {
			try {
				while (getResultSet().next()) {
					if (getResultSet().getString(1).equals(_strTargetDate))
					bok = true;
					LOGGER.info("CB:Participant: " + _p1 + " cannot have an away game ");
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.info(e.getMessage());
			}
		}
		cleanup();
		return bok;
		
	}
	
	public boolean checkClubOffDay (Participant _p1, String _strTargetDate) {

		boolean bok = false;
		String sSQL1 = "Select dategone from gbli.team tm1 " +
				" join gbli.club cl on " +
				"    cl.idclub = tm1.idclub " +
		" join gbli.blackoutclub boc on " +
		"    cl.idclub  = boc.idclub " +
		" and boc.isclubgone = 1 " +
		"  where tm1.idteam = ? ";
		Vector v = new Vector<Object>();
		v.add(Integer.valueOf(_p1.getTeam().getID()));
		LOGGER.info("CHMO: vector=" + v);
		if (getData(sSQL1, v)) {
			try {
				while (getResultSet().next()) {
					if (getResultSet().getString(1).equals(_strTargetDate))
					bok = true;
					LOGGER.info("COG:Participant: " + _p1 + " cannot play on this day ");
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.info(e.getMessage());
			}
		}
		cleanup();
		return bok;
		
	}
	
	public  String getSlotDate (Integer _iSlotID) {

		String sreturn = "1980-01-01";
		String sSQL1 = "Select cs.actdate from gbli.clubslot cs " +
					" join gbli.slot sl on " +
					"   sl.idslot = ? " +
					" where " +
					"    cs.idclubslot = sl.idclubslot ";
		
		Vector v = new Vector<Object>();
		v.add(_iSlotID);
		if (getData(sSQL1, v)) {
			try {
				while (getResultSet().next()) {
					sreturn = getResultSet().getString(1);
					LOGGER.info("getSlotDate: " + sreturn);
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.info(e.getMessage());
			}
		}
		cleanup();
		return sreturn;
		
	}

	public boolean checkMaxExhibitionStatus (Participant _p1, Participant _p2, Season _se) {

		boolean bok = true;
		
		if (!_p1.getTeam().isExhibitionTeam() && !_p2.getTeam().isExhibitionTeam()) {
			LOGGER.info("xccheck:Participant: " + _p1 + " BOTH ARE NOT EXHIBITE.. CAN PLAY..." + _p2);
			return true;
		}

		if (_p1.getTeam().isExhibitionTeam() && _p2.getTeam().isExhibitionTeam()) {
			LOGGER.info("xccheck:Participant: " + _p1 + " BOTH ARE  EXHIBITE.. CAN PLAY..." + _p2);
			return true;
		}
		
		//
		// if they have already played once.. then we are done
		//
   		String sSQL1 = "select coalesce(count(idgame),0) from gbli.game gm " +
   				"where ((idteamhome = ? and idteamaway = ?) or (idteamaway = ? and idteamhome = ?)) and idseason = ?";

		Vector v = new Vector<Object>();
		v.add(Integer.valueOf(_p1.getTeam().getID()));
		v.add(Integer.valueOf(_p2.getTeam().getID()));
		v.add(Integer.valueOf(_p1.getTeam().getID()));
		v.add(Integer.valueOf(_p2.getTeam().getID()));
		v.add(Integer.valueOf(_se.getID()));
		LOGGER.info("excheck: vector=" + v);
		if (getData(sSQL1, v)) {
			try {

				while (getResultSet().next()) {
					if (getResultSet().getInt(1) == _se.getPlayOnce()) { 
						bok = false;
						LOGGER.info("xccheck:Participant: " + _p1 + " cannot play " + _p2);
					}
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.info(e.getMessage());
			}
		}
		
		return bok;
		
	}
	
	public  Participant calcHomeParticipant(Season _se, Participant _pMain, Participant  _pMatch, ArrayList<Integer> _aMain, ArrayList<Integer> _aMatch) {
	
		int iMainHitPoints = 0;
		int iMatchHitPoints = 0;
		//
		//
		// distance hit
		//
		Club clMain = (Club)ContextManager.c_clubs.getChildAtID(_pMain.getTeam().getIdClub());
		Club clMatch = (Club)ContextManager.c_clubs.getChildAtID(_pMatch.getTeam().getIdClub());
		
		if (clMain.getSName().equals("DRAGONS") 
//				|| clMain.getSName().equals("BLAZE")
				) {
			iMainHitPoints = 1;
		}
		if (clMatch.getSName().equals("DRAGONS") 
//				| clMatch.getSName().equals("BLAZE")
				) {
			iMatchHitPoints = 1;
		}
		if (_pMain.getTeam().isExhibitionTeam()) { 
			iMainHitPoints = 0;
		}
		if (_pMatch.getTeam().isExhibitionTeam()) { 
			iMatchHitPoints = 0;
		}

		//
		// lets get see who wins on matchup homes..
		//
		int ihomeadv = 0;
		String sSQL1 = "select coalesce((select count(idgame)from gbli.game gm where idteamhome = ? and idteamaway = ? and idseason = ?),0) - " +
		          " coalesce((select count(idgame)from gbli.game gm where idteamaway = ? and idteamhome = ? and idseason = ?),0)";

		
		Vector v = new Vector<Object>();
		v.add(Integer.valueOf(_pMain.getTeam().getID()));
		v.add(Integer.valueOf(_pMatch.getTeam().getID()));
		v.add(Integer.valueOf(_se.getID()));
		v.add(Integer.valueOf(_pMain.getTeam().getID()));
		v.add(Integer.valueOf(_pMatch.getTeam().getID()));
		v.add(Integer.valueOf(_se.getID()));
		if (getData(sSQL1, v)) {
			try {
				while (getResultSet().next()) {
					ihomeadv = getResultSet().getInt(1);
					LOGGER.info("homeAtvantage:is:" + ihomeadv  + " to Main " + _pMain);
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.info(e.getMessage());
			}
		}
		
		int iMain = _pMain.getTeam().getTeamGameInfo().getHomeGames();
		int iMatch = _pMatch.getTeam().getTeamGameInfo().getHomeGames();
		
		if (_pMain.getTeam().getID() == 76 && iMain > 7 && !_aMatch.isEmpty()) {
			return _pMatch;
		}
		if (_pMatch.getTeam().getID() == 76 && iMatch > 7 && !_aMain.isEmpty()) {
			return _pMain;
		}
		
		if (_pMain.getTeam().isByeTeam()) {
			return _pMain;
		} else if (_pMatch.getTeam().isByeTeam()) {
			return _pMatch;
		} else if (_aMain.isEmpty() && !_aMatch.isEmpty()) {
			return _pMatch;
		} else 	if (!_aMain.isEmpty() && _aMatch.isEmpty()) {
			return _pMain;
		} else if (_aMain.size() < 2 && (iMain + iMainHitPoints) < (iMatch  + iMatchHitPoints)) {
			return _pMain;
		} else if (_aMatch.size() < 2 && (iMatch + iMatchHitPoints) < (iMain + iMainHitPoints)) {
			return _pMatch;
		} else if (ihomeadv - iMainHitPoints > 0 && !_aMatch.isEmpty()) {
			return _pMatch;
		} else if (ihomeadv + iMainHitPoints < 0 && !_aMain.isEmpty()) {
			return _pMain;
		} else 	if (iMain + iMainHitPoints < iMatch + iMatchHitPoints && !_aMain.isEmpty()) {
			return _pMain;
		} else if (iMatch + iMatchHitPoints < iMain + iMainHitPoints && !_aMatch.isEmpty()) {
			return _pMatch;
		} else {
			return _pMain;
		}
	
		
	}
	
	public void resetGames(Season _se) {
		LOGGER.info("resetGames:" + _se.getName());

		String strSQL =  
				" update gbli.game " +
				" set idteamhome = 0, " +
				" idteamaway = 0, " +
				" idseasonweeks = 0," +
				" idslot = (case when idslot = -3 then -3 else 0 end)," +
				" islocked = 0 " +
				" where " +  
				" idseason = ?" ;
		
			try {
				PreparedStatement ps = this.getConnection().prepareStatement(strSQL);
				ps.setInt(1, _se.getID());
				ps.executeUpdate();
				this.cleanup();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void lockSeason (Season _se) {
		LOGGER.info("Lock Seasopm:" + _se.getName());

		String strSQL =  
				" update gbli.season " +
				" islocked = 1 " +
				" where " +  
				" idseason = ?" ;
		
			try {
				PreparedStatement ps = this.getConnection().prepareStatement(strSQL);
				ps.setInt(1, _se.getID());
				ps.executeUpdate();
				this.cleanup();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}


