/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.datawindows;

import java.util.ArrayList;
import org.caleydo.core.util.system.Time;

public class EyeTracking {
	private int[] rawEyeTrackerPosition;
	private float[] receivedEyeData;
	private float[] eyeTrackerOffset;
	private int[] fixedCoordinate;
	// private boolean simulation;
	private double timeToFixCoordinate;
	private float radiusOfFixedCoordinate;
	// private String ipTracker;
	public int debugMode;
	private ArrayList<EyeCoordinateListEntry> coordinateList;
	private Time time;
	private double totalTime;
	private double pauseTime;
	public double eyeTrackerPauseStatus;

	public EyeTracking(boolean simulation, String ipTracker) {
		setEyeTrackerOffset(new float[2]);
		debugMode = 0;
		fixedCoordinate = new int[2];
		timeToFixCoordinate = 0.2f;
		radiusOfFixedCoordinate = 40;
		// this.ipTracker = ipTracker;
		// this.simulation = simulation;
		rawEyeTrackerPosition = new int[2];
		this.rawEyeTrackerPosition[0] = 0;
		this.rawEyeTrackerPosition[1] = 0;
		time = new Time();
		((Time) time).rebase();
		time.update();
		coordinateList = new ArrayList<EyeCoordinateListEntry>();
		totalTime = 0;
		pauseTime = 1;
		this.eyeTrackerPauseStatus = 0;
	}

	@SuppressWarnings("static-access")
	public void startTracking() {

	}

	public void receiveData() {

	}

	public void cutWindowOffset(int x, int y) {
		rawEyeTrackerPosition[0] = rawEyeTrackerPosition[0] - x;
		rawEyeTrackerPosition[1] = rawEyeTrackerPosition[1] - y;
	}

	public void setRawEyeTrackerPosition(int[] rawEyeTrackerPosition) {
		this.rawEyeTrackerPosition = rawEyeTrackerPosition;
	}

	public int[] getRawEyeTrackerPosition() {
		return rawEyeTrackerPosition;
	}

	public void setEyeTrackerOffset(float[] eyeTrackerOffset) {
		this.eyeTrackerOffset = eyeTrackerOffset;
	}

	public float[] getEyeTrackerOffset() {
		return eyeTrackerOffset;
	}

	public void checkForFixedCoordinate() {
		// if the user focuses a point on the screen, the coordinate of the
		// point is fixed
		// while the user is looking around, the fixed coordinate is null

		// there is no need to handle overflow
		double deltaTime = time.deltaT();
		totalTime = totalTime + deltaTime;
		// System.out.println("pause status: "+eyeTrackerPauseStatus);
		if (this.eyeTrackerPauseStatus > 0) {

			this.eyeTrackerPauseStatus -= deltaTime;
		} else {

			this.eyeTrackerPauseStatus = 0;
		}

		time.update();
		this.coordinateList.add(new EyeCoordinateListEntry(rawEyeTrackerPosition,
				totalTime));
		int size = coordinateList.size();

		double dx;
		double dy;
		EyeCoordinateListEntry actualEntry;
		double length;
		for (int i = 0; i < size; i++) {
			actualEntry = coordinateList.get(i);
			if ((totalTime - actualEntry.getTime()) > this.timeToFixCoordinate) {
				coordinateList.remove(i);
				i--;
				size = coordinateList.size();
				if (size == 0) {
					return;
				}
			} else {
				dx = (actualEntry.getcoordinate()[0] - rawEyeTrackerPosition[0]);
				dy = (actualEntry.getcoordinate()[1] - rawEyeTrackerPosition[1]);
				length = (float) Math.sqrt(dx * dx + dy * dy);
				if (length > this.radiusOfFixedCoordinate) {
					return;
				}
			}
		}

		if (this.eyeTrackerPauseStatus == 0) {

			this.fixedCoordinate[0] = this.rawEyeTrackerPosition[0];
			this.fixedCoordinate[1] = this.rawEyeTrackerPosition[1];
		}

	}

	public int[] getFixedCoordinate() {
		return fixedCoordinate;
	}

	public void resetFixedCoordinate() {
		this.fixedCoordinate[0] = 0;
		this.fixedCoordinate[1] = 0;
	}

	public void pauseEyeTracker() {
		this.eyeTrackerPauseStatus = this.pauseTime;
	}

}
