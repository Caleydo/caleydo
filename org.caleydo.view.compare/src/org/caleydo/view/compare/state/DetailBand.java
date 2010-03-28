package org.caleydo.view.compare.state;

import java.util.ArrayList;

public class DetailBand {

	private int bandID; 
	
	private ArrayList<Integer> contentIDs;
	
	public DetailBand(int bandID) {
		this.bandID = bandID;
	}
	
	public void setContentIDs(ArrayList<Integer> contentIDs) {
		this.contentIDs = contentIDs;
	}
	
	public ArrayList<Integer> getContentIDs() {
		return contentIDs;
	}
	
	public int getBandID() {
		return bandID;
	}
}
