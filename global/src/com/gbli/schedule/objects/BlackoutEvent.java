package com.gbli.schedule.objects;

public class BlackoutEvent extends SchedObject {
	

	String dateGone = null;
	boolean isClubGone = false;
	boolean isIceGone = false;
	boolean isNoTravel = false;
	
	public BlackoutEvent(Club _cl, int _id, String _dateGone, int _isClubGone, int _isIceGone, int _notravel) {
		
		setID(_id);
		setParent(_cl);
		//
		// lets set it all up
		// 
		this.setDateGone(_dateGone);
		this.setClubGone((_isClubGone == 1 ? true : false));
		this.setIceGone((_isIceGone == 1 ? true : false));
		this.setNoTravel((_notravel == 1 ? true : false));
	}
	@Override
	public String Dump() {
		// TODO Auto-generated method stub
		return toString();
	}

	/**
	 * @return the dateGone
	 */
	public String getDateGone() {
		return dateGone;
	}
	/**
	 * @param dateGone the dateGone to set
	 */
	public void setDateGone(String dateGone) {
		this.dateGone = dateGone;
	}
	/**
	 * @return the isClubGone
	 */
	public boolean isClubGone() {
		return isClubGone;
	}
	/**
	 * @param isClubGone the isClubGone to set
	 */
	public void setClubGone(boolean isClubGone) {
		this.isClubGone = isClubGone;
	}
	/**
	 * @return the isIceGone
	 */
	public boolean isIceGone() {
		return isIceGone;
	}
	/**
	 * @param isIceGone the isIceGone to set
	 */
	public void setIceGone(boolean isIceGone) {
		this.isIceGone = isIceGone;
	}
	/**
	 * @return the isNoTravel
	 */
	public boolean isNoTravel() {
		return isNoTravel;
	}
	/**
	 * @param isNoTravel the isNoTravel to set
	 */
	public void setNoTravel(boolean isNoTravel) {
		this.isNoTravel = isNoTravel;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}
