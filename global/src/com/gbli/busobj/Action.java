/**
 * 
 */
package com.gbli.busobj;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

/**
 * @author dbigelow
 *
 */
public class Action extends GbliBusObject implements Comparable<Action> {
	

	/**
	 *  All the private static information here.
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//
	// Static Constants
	//
	public static final String GROUP = "GROUP";
	public static final String VERB = "VERB";

	private int pid;
	private String Type;
	private String Tag;
	private String Desc;
	private String jspTarget;
	
	
	//
	// This holds the action list that this actions may contain
	private Hashtable<Integer,Action> m_hActions = new Hashtable<Integer,Action>();

	/**
	 * 
	 * @param _iType
	 * @param _id
	 * @param _strTag
	 * @param _strDesc
	 */
	public Action (int _id, int _pid, String _sType, String _sTag,  String _sDesc, String _sJspTarget) {
		
		super.setID(_id);
		super.setKey(_id + ":" +  _sTag);
		

		//
		// Set the properties of this object here
		//
		this.setPID(_pid);
		this.setType(_sType);
		this.setTag(_sTag);
		this.setDesc(_sDesc);
		this.setJspTarget(_sJspTarget);
		
	}
	
	/**
	 * This represents The fully qualified String Value
	 */
	public String toString() {
		return super.getID() + ":" + this.getType() + ":" + this.getTag() + ":" + this.getJspTarget() + ":" + this.getDesc();
	}
	
	/**
	 * Adds an subordinate action to this parent action
	 * @param _act
	 */
	public void putChildAction(Action _act) {
		this.m_hActions.put(Integer.valueOf(_act.getID()),_act);
	}

	public Action getChildAction(int _iActKey) {
		return this.m_hActions.get(Integer.valueOf(_iActKey));
	}
	
	public int getChildActionSize() {
		return this.m_hActions.size();
	}
	
	public Action[] getChildActionsAsArray() {	
		return this.m_hActions.values().toArray(new Action[this.m_hActions.size()]);
	}

	public int compareTo(Action _act) {
		return this.getDesc().compareTo(_act.getDesc());
	}	
	
	/**
	 * @return the jspTarget
	 */
	public String getJspTarget() {
		return jspTarget;
	}

	/**
	 * @param jspTarget the jspTarget to set
	 */
	public void setJspTarget(String jspTarget) {
		this.jspTarget = jspTarget;
	}
	
	//
	// Member Variable Section
	//
	
	/**
	 * @return the type
	 */
	public String getType() {
		return Type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		Type = type;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return Tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		Tag = tag;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return Desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		Desc = desc;
	}
	
	public void setPID(int _i) {
		this.pid = _i;
	}
	
	public int getPID() {
		return this.pid;
	}
}
