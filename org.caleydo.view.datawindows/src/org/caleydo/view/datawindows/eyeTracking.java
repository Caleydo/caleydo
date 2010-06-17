package org.caleydo.view.datawindows;

import java.util.ArrayList;

import org.caleydo.core.util.system.SystemTime;
import org.caleydo.core.util.system.Time;
import org.caleydo.core.util.tracking.TrackDataProvider;

public class eyeTracking {
	private int[] rawEyeTrackerPosition;
	private TrackDataProvider tracker;
	private float[] receivedEyeData;
	private float[] eyeTrackerOffset;
	private int[] fixedCoordinate;
	private boolean simulation;
	private double timeToFixCoordinate;
	private float radiusOfFixedCoordinate;
	private String ipTracker;
	public int debugMode;
	private ArrayList<eyeCoordinateListEntry> coordinateList;
	private Time time;
	private double totalTime;
	private double pauseTime;
	private double eyeTrackerPauseStatus;

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
		time = new SystemTime();
		((SystemTime) time).rebase();
		time.update();
		coordinateList = new ArrayList<eyeCoordinateListEntry>();
		totalTime = 0;
		pauseTime = 1;
		this.eyeTrackerPauseStatus=0;
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
			rawEyeTrackerPosition[0] = (int) receivedEyeData[0];
			rawEyeTrackerPosition[1] = (int) receivedEyeData[1];

		}
	}

	public void cutWindowOffset(int x, int y) {
		rawEyeTrackerPosition[0] = rawEyeTrackerPosition[0] - x;
		rawEyeTrackerPosition[1] = rawEyeTrackerPosition[1] - y;
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

		// there is no need to handle overflow
		double deltaTime = time.deltaT();
		totalTime = totalTime + deltaTime;
		if (this.eyeTrackerPauseStatus > 0) {
			this.eyeTrackerPauseStatus -= deltaTime;
			System.out.println("pause beendet in:" + this.eyeTrackerPauseStatus);
		} else {
			this.eyeTrackerPauseStatus = 0;
		}
		// System.out.println("total time:"+this.totalTime);
		time.update();

		this.coordinateList.add(new eyeCoordinateListEntry(
				rawEyeTrackerPosition, totalTime));

		int size = coordinateList.size();

		double dy;
		double dx;
		eyeCoordinateListEntry actualEntry;
		double length;
		for (int i = 0; i < size; i++) {
			actualEntry = coordinateList.get(i);
			// System.out.println("time:"+actualEntry.getTime()+
			// "actualtime"+totalTime);
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
				// System.out.println("entry: " +i+" aktuell"+
				// actualEntry.getcoordinate()[0] );
			//	System.out.println("dx:" + dx + " dy:" + dy + " distance: "
			//			+ length);
				if (length > this.radiusOfFixedCoordinate) {
					return;
				}
			}
		}
		System.out.println("Point fixed!!!: " + rawEyeTrackerPosition[0] + " "
				+ rawEyeTrackerPosition[1]);
		if(this.eyeTrackerPauseStatus==0){
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
