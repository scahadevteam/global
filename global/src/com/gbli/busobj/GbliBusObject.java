package com.gbli.busobj;

import java.io.Serializable;

public class GbliBusObject implements Serializable {
	
	private Profile m_pro = null;
	//
	//  This represents the Key for the object
	//
	String m_sKey = null;
	int ID = -1;
	
	/**
	 * @return the m_ID
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @param m_ID the m_ID to set
	 */
	public void setID(int _iID) {
		this.ID = _iID;
	}

	/**
	 * This represents the unique Identifier of this object to the whole system
	 * set it carefully...
	 * @param _sKey
	 */
	public void setKey(String _sKey) {
		m_sKey = _sKey;
	}

	/**
	 * Retrieves the unique identifier of this object to the whole system
	 * @return
	 */
	public String getKey() {
		return m_sKey;
	}
	
	public void setProfile (Profile _pro) {
		m_pro = _pro;
	}

	public Profile getProfile () {
		return m_pro;
	}

}
