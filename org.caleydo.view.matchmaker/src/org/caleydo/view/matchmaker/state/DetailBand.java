package org.caleydo.view.matchmaker.state;

import java.util.ArrayList;

public class DetailBand {

	private int bandID;

	private ArrayList<Integer> recordIDs;

	public DetailBand(int bandID) {
		this.bandID = bandID;
	}

	public void setContentIDs(ArrayList<Integer> recordIDs) {
		this.recordIDs = recordIDs;
	}

	public ArrayList<Integer> getContentIDs() {
		return recordIDs;
	}

	public int getBandID() {
		return bandID;
	}
}
