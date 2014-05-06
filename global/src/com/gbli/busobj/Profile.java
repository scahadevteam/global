/**
 * 
 */
package com.gbli.busobj;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.gbli.connectors.GDatabase;
import com.gbli.context.ContextManager;


/**
 * This Presents the user in our business system.  It contains all the keys and permissions that this user 
 * has access to throughout the system
 * @author dbigelow
 *
 */
public class Profile extends GbliBusObject {
	
	//
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	

	//
	// Member Variables
	//
	private String m_sUser = null;
	private String m_sPass = null;
	private String m_sNickName = null;
	private ActionList m_al = null;
	
	private Profile (int _id, String _sNN, String _sUser, String _sPass) {
		
		super.setID(_id);
		m_sNickName = _sNN;
		m_sUser = _sUser;
		m_sPass = _sPass;
		
		m_al = new ActionList(this);
	}
	
	
	/**
	 * verify - Gathers the profile information from the target system
	 * If it returns false.. authentication failed.
	 * 
	 * @return boolean
	 */
	//
	public static final Profile verify(String _sUser, String _sPass) {

		//
		// get an iSiteDatabase Connection..
		//
		GDatabase db = (GDatabase)ContextManager.getDatabase(GDatabase.class.getSimpleName());

		ResultSet rs = null;
		boolean bgood = false;
		
		//
		// If this comes back true.. we have a good result set to play with and fill out the profile
		//
		int id  = -1;
		String sNickName = null;
		try {
			if (db.verify(_sUser, _sPass)) {
				rs = db.getResultSet();
				if (rs.next()) {
					id = rs.getInt(1);
					sNickName = rs.getString(2);
					bgood = true;
				}
			}
		} catch (SQLException ex) {
				ex.printStackTrace();
		} finally {
			db.free();
		}
		
		if (bgood) {
			
			return new Profile (id, sNickName, _sUser, _sPass);
			
		} 
		return null;
				
	}

	/**
	 * 
	 * @return
	 */
	public final String getNickName() {
		return this.m_sNickName;
	}
	
	/**
	 * gets the actionlist associated with the given Profile.
	 * THe user can peruse this structure to find out what actions 
	 * this particular Profile has access to
	 * @return
	 */
	public final ActionList getActionList() {
		return this.m_al;
	}
	
	public String toString() {
		return this.getID() + ":" + this.getNickName() + ":" + this.m_sUser;
	}
	
}
