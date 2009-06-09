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

	public static float SMOOTH_RANGE = 20;

	public static String IP_TRACKER = "192.168.1.91";
	// public static String IP_TRACKER = "169.254.55.198";
	// public static String IP_LOCAL = "169.254.7.200";

	private Queue<float[]> posInputQueue = new LinkedList<float[]>();

	/**
	 * Determines status of eye tracker
	 */
	private boolean bIsTrackMode = false;

	private TrackDataListenerThread trackDataListener;

	// /**
	// * Constructor.
	// */
	// public TrackDataProvider() {
	//
	// }

	public void startTracking() {

		if (bIsTrackMode)
			return;
		
		bIsTrackMode = true;

		float[] point = new float[2];
		point[0] = 0;
		point[1] = 0;

		// Initialize IR position input queue
		for (int i = 0; i < SMOOTH_RANGE; i++) {
			posInputQueue.add(point);
		}

		try {
			DatagramSocket senderSocket;
			senderSocket = new DatagramSocket(4444);

			InetAddress smiPCAddress = InetAddress.getByName(IP_TRACKER);

			String command = "ET_STR \n";// ET_FRM \"%SX,%SY\" \n";
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

		// TODO: stop this thread in a stopTracking() method
		trackDataListener = new TrackDataListenerThread(posInputQueue);
		trackDataListener.start();
	}

	public void stopTracking() {

		if (!bIsTrackMode)
			return;
		
		bIsTrackMode = false;
		trackDataListener.stopThread();
	}

	public float[] get2DTrackData() {

		float[] fArSmoothedPoint = new float[] { 0f, 0f };
		float[] fArTmpPoint;

		for (int i = 0; i < SMOOTH_RANGE; i++) {
			if (posInputQueue.size() < SMOOTH_RANGE) {
				break;
			}

			fArTmpPoint = ((LinkedList<float[]>) posInputQueue).get(i);
			fArSmoothedPoint[0] += fArTmpPoint[0];
			fArSmoothedPoint[1] += fArTmpPoint[1];
		}

		fArSmoothedPoint[0] /= SMOOTH_RANGE;
		fArSmoothedPoint[1] /= SMOOTH_RANGE;

		// System.out.println("Focus position: " +fArSmoothedPoint[0] + " / " + fArSmoothedPoint[1]);

		return fArSmoothedPoint;
	}

	// public float[] get3DTrackData() {
	//
	// return new float[] { 0, 0, 0 };
	// }

	public boolean isTrackModeActive() {
		return bIsTrackMode;
	}
}
