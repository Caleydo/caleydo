package org.caleydo.view.datawindows;

public class eyeCoordinateListEntry {
	private int[] coordinate;
	private double time;

	public eyeCoordinateListEntry(int[] coordinate, double time) {
		this.coordinate = new int[2];

		this.coordinate[0] = coordinate[0];
		this.coordinate[1] = coordinate[1];
		this.time = time;
	}

	public double getTime() {
		return time;
	}

	public int[] getcoordinate() {
		return coordinate;
	}

}
