package com.gbli.schedule.objects;

import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class Slot extends SchedObject {

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Slot [idTeamAgainst=" + idTeamAgainst + ", actDate=" + actDate
				+ ", StartTime=" + StartTime + ", ID=" + getID() + "]";
	}

	
	int idTeamAgainst  =  0;
	String actDate = null;
	String StartTime = null;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	public Slot (int _id, String _actDate, String _StartTime, int _idT) {
		super.setID(_id);
		actDate = _actDate;
		StartTime = _StartTime;
		idTeamAgainst = _idT;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}



	@Override
	public String Dump() {
		// TODO Auto-generated method stub
		return null;
	}

}
