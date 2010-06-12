package org.caleydo.view.datawindows;

import org.caleydo.core.util.tracking.TrackDataProvider;

public class eyeTracking {
	private int[] rawEyeTrackerPosition;
	private TrackDataProvider tracker;
	private float[] receivedEyeData;
	private float[] eyeTrackerOffset;
	public float[] glCoordinate;
	private boolean simulation;

	public int debugMode;

	@SuppressWarnings("static-access")
	public eyeTracking(boolean simulation, String ipTracker) {
		tracker = new TrackDataProvider();
		tracker.IP_TRACKER = ipTracker;
		tracker.startTracking();

		setEyeTrackerOffset(new float[2]);
		debugMode = 0;
		glCoordinate = new float[2];
		this.simulation = simulation;
	}

	public void receiveData() {
		if (simulation == false) {
			receivedEyeData = tracker.getEyeTrackData();
			if (debugMode == 1) {
				System.out.println("Eye Tracker Data received: "
						+ receivedEyeData[0] + " / " + receivedEyeData[1]);
			}
		}
	}

	public void cutWindowOffset(int x, int y) {
		if (simulation == false) {
			receivedEyeData[0] = receivedEyeData[0] - (float) x;
			receivedEyeData[1] = receivedEyeData[1] - (float) y;

			if (debugMode == 1) {
				System.out.println("Eye position korrigiert: "
						+ receivedEyeData[0] + " / " + receivedEyeData[1]);
			}
		}
	}

	public void calculateGLCoordinates(float canvasWidth, float canvasHeight,
			int pixelWidth, int pixelHeight) {
		if (simulation == false) {
			glCoordinate[0] = receivedEyeData[0] * (canvasWidth / pixelWidth);
			glCoordinate[1] = receivedEyeData[1] * (canvasHeight / pixelHeight);
		}
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

}
