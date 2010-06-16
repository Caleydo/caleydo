package org.caleydo.view.datawindows;

import org.caleydo.core.util.tracking.TrackDataProvider;

public class eyeTracking {
	private int[] rawEyeTrackerPosition;
	private TrackDataProvider tracker;
	private float[] receivedEyeData;
	private float[] eyeTrackerOffset;
	private int[] fixedCoordinate;
	private boolean simulation;
	private float timeToFixCoordinate;
	private int radiusOfFixedCoordinate;
	private String ipTracker;
	public int debugMode;

	@SuppressWarnings("static-access")
	public eyeTracking(boolean simulation, String ipTracker) {
		setEyeTrackerOffset(new float[2]);
		debugMode = 0;
		// the coordinate, choosen by the eye
		fixedCoordinate = new int[2];
		timeToFixCoordinate = 0.5f;
		radiusOfFixedCoordinate = 20;
		this.ipTracker = ipTracker;
		this.simulation = simulation;
		rawEyeTrackerPosition = new int[2];
		this.rawEyeTrackerPosition[0] = 0;
		this.rawEyeTrackerPosition[1] = 0;

	}

	public void startTracking() {

		tracker = new TrackDataProvider();
		tracker.IP_TRACKER = ipTracker;
		tracker.startTracking();

	}

	public void receiveData() {
		if (simulation == false) {
			receivedEyeData = tracker.getEyeTrackData();
			if (debugMode == 1) {
				System.out.println("Eye Tracker Data received: "
						+ receivedEyeData[0] + " / " + receivedEyeData[1]);
			}
			rawEyeTrackerPosition[0]=(int)receivedEyeData[0];
			rawEyeTrackerPosition[1]=(int)receivedEyeData[1];

		}
	}

	public void cutWindowOffset(int x, int y) {
		rawEyeTrackerPosition[0] = rawEyeTrackerPosition[0] -  x;
		rawEyeTrackerPosition[1] = rawEyeTrackerPosition[1] -  y;
	}

	// public void calculateGLCoordinates(float canvasWidth, float canvasHeight,
	// int pixelWidth, int pixelHeight) {
	// if (simulation == false) {
	// glCoordinate[0] = receivedEyeData[0] * (canvasWidth / pixelWidth);
	// glCoordinate[1] = receivedEyeData[1] * (canvasHeight / pixelHeight);
	// }
	// }

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

		this.fixedCoordinate[0] = this.rawEyeTrackerPosition[0];
		this.fixedCoordinate[1] = this.rawEyeTrackerPosition[1];

	}

	public int[] getFixedCoordinate() {
		return fixedCoordinate;
	}

	public void resetFixedCoordinate() {
		this.fixedCoordinate[0] = 0;
		this.fixedCoordinate[1] = 0;
	}

}
