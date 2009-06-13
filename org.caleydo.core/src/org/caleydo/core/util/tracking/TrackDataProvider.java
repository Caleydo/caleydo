package org.caleydo.core.util.tracking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class TrackDataProvider {

	public static final ETrackerMode eTrackerMode = ETrackerMode.SIMULATED_BY_MOUSE_MOVEMENT;
	
	public static float POSITON_SMOOTH_RANGE = 20;
	public static float DEPTH_SMOOTH_RANGE = 30;

	// public static String IP_TRACKER = "192.168.1.91";
	public static String IP_TRACKER = "169.254.55.198";
	// public static String IP_LOCAL = "169.254.7.200";

	private Queue<float[]> eyePosInputQueue = new LinkedList<float[]>();
	private Queue<float[]> headPosInputQueue = new LinkedList<float[]>();
	private Queue<Float> diameterInputQueue = new LinkedList<Float>();

	/**
	 * Determines status of eye tracker
	 */
	private boolean bIsTrackMode = false;

	private TrackDataListenerThread trackDataListener;

	public enum ETrackerMode {
		SIMULATED_BY_MOUSE_MOVEMENT,
		RED, // monitor based
		HED
		// head tracker based
	};
	
	public void startTracking() {

		if (bIsTrackMode)
			return;

		bIsTrackMode = true;

		float[] point = new float[2];
		point[0] = 0;
		point[1] = 0;

		// Initialize eye position input queue
		for (int i = 0; i < POSITON_SMOOTH_RANGE; i++) {
			eyePosInputQueue.add(point);
		}

		// Initialize head position input queue
		for (int i = 0; i < POSITON_SMOOTH_RANGE; i++) {
			headPosInputQueue.add(point);
		}

		// Initialize IR depth input queue
		for (int i = 0; i < DEPTH_SMOOTH_RANGE; i++) {
			diameterInputQueue.add(new Float(0f));
		}

		try {
			DatagramSocket senderSocket;
			senderSocket = new DatagramSocket(4444);

			InetAddress smiPCAddress = InetAddress.getByName(IP_TRACKER);

			String command = "";
			
			if (eTrackerMode == ETrackerMode.RED || eTrackerMode == ETrackerMode.SIMULATED_BY_MOUSE_MOVEMENT) 
				command = "ET_STR \nET_FRM \"%SX %SY %DX %DY\" \n";
			else if (eTrackerMode == ETrackerMode.HED)
				command = "ET_STR \nET_FRM \"%HX %HY %HZ\" \n";
			
			byte[] sendData = new byte[1024];
			sendData = command.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, smiPCAddress, 4444);;
			senderSocket.send(sendPacket);
			senderSocket.close();
		}
		catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		trackDataListener =
			new TrackDataListenerThread(eyePosInputQueue, headPosInputQueue, diameterInputQueue);
		trackDataListener.start();
	}

	public void stopTracking() {

		if (!bIsTrackMode)
			return;

		bIsTrackMode = false;
		trackDataListener.stopThread();
	}

	public float[] getEyeTrackData() {

		float[] fArSmoothedPoint = new float[] { 0f, 0f };
		float[] fArTmpPoint;

		for (int i = 0; i < POSITON_SMOOTH_RANGE; i++) {
			if (eyePosInputQueue.size() < POSITON_SMOOTH_RANGE) {
				break;
			}

			fArTmpPoint = ((LinkedList<float[]>) eyePosInputQueue).get(i);
			fArSmoothedPoint[0] += fArTmpPoint[0];
			fArSmoothedPoint[1] += fArTmpPoint[1];
		}

		fArSmoothedPoint[0] /= POSITON_SMOOTH_RANGE;
		fArSmoothedPoint[1] /= POSITON_SMOOTH_RANGE;

		System.out.println("Eye position: " + fArSmoothedPoint[0] + " / " + fArSmoothedPoint[1]);

		return fArSmoothedPoint;
	}

	public float getDepth() {

		float fSmoothedDepth = 0f;
		float fTmpDepth = 0;

		for (int i = 0; i < DEPTH_SMOOTH_RANGE; i++) {
			if (diameterInputQueue.size() < DEPTH_SMOOTH_RANGE) {
				break;
			}

			fTmpDepth = ((LinkedList<Float>) diameterInputQueue).get(i);
			fSmoothedDepth += fTmpDepth;
		}

		fSmoothedDepth /= DEPTH_SMOOTH_RANGE;

		System.out.println("Depth: " + fSmoothedDepth);

		return fSmoothedDepth;
	}

	public float[] getHeadTrackData() {

		float[] fArSmoothedPoint = new float[] { 0f, 0f, 0f };
		float[] fArTmpPoint;

		for (int i = 0; i < POSITON_SMOOTH_RANGE; i++) {
			if (headPosInputQueue.size() < POSITON_SMOOTH_RANGE) {
				break;
			}

			fArTmpPoint = ((LinkedList<float[]>) headPosInputQueue).get(i);
			fArSmoothedPoint[0] += fArTmpPoint[0];
			fArSmoothedPoint[1] += fArTmpPoint[1];
			fArSmoothedPoint[2] += fArTmpPoint[2];
		}

		fArSmoothedPoint[0] /= POSITON_SMOOTH_RANGE;
		fArSmoothedPoint[1] /= POSITON_SMOOTH_RANGE;
		fArSmoothedPoint[2] /= POSITON_SMOOTH_RANGE;

		System.out.println("Head position: " + fArSmoothedPoint[0] + " / " + fArSmoothedPoint[1] + " / "
			+ fArSmoothedPoint[2]);

		return fArSmoothedPoint;
	}

	public boolean isTrackModeActive() {
		return bIsTrackMode;
	}
}
