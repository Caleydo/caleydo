package org.caleydo.core.util.tracking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Queue;
import java.util.StringTokenizer;

public class TrackDataListenerThread
	extends Thread {

	private DatagramSocket serverSocket;
	private byte[] receiveData;

	private Queue<float[]> eyePosInputQueue;
	private Queue<float[]> headPosInputQueue;
	private Queue<Float> diameterInputQueue;

	private boolean bKeepRunning = true;

	public TrackDataListenerThread(Queue<float[]> eyePosInputQueue, Queue<float[]> headPosInputQueue,
		Queue<Float> diameterInputQueue) {

		this.eyePosInputQueue = eyePosInputQueue;
		this.headPosInputQueue = headPosInputQueue;
		this.diameterInputQueue = diameterInputQueue;
	}

	public void stopThread() {
		bKeepRunning = false;
	}

	@Override
	public void run() {

		try {
			serverSocket = new DatagramSocket(5555);
		}
		catch (SocketException ex) {
			System.out.println("UDP Port 5555 is occupied.");
		}

		try {

			while (bKeepRunning) {
				receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				// System.out.println("Waiting for datagram packet");

				serverSocket.receive(receivePacket);
				String sentence = new String(receivePacket.getData());

				if (!sentence.contains("ET_SPL"))
					continue;

				// System.out.println(sentence);

				StringTokenizer tokenizer = new StringTokenizer(sentence, " ");

				tokenizer.nextToken();

				int x = new Integer(tokenizer.nextToken());
				int y = 0;
				int diameter = 0;

				if (TrackDataProvider.eTrackerMode == TrackDataProvider.ETrackerMode.RED) {

					tokenizer.nextToken();
					y = new Integer(tokenizer.nextToken());
					tokenizer.nextToken();
					diameter = new Integer(tokenizer.nextToken());
				}
				else if (TrackDataProvider.eTrackerMode == TrackDataProvider.ETrackerMode.SIMULATED_BY_MOUSE_MOVEMENT) {

					y = new Integer(tokenizer.nextToken());
					// String tmp = tokenizer.nextToken();
					// tmp = tmp.substring(0, tmp.indexOf('\n'));
					// y = new Integer(tmp);
				}
				else if (TrackDataProvider.eTrackerMode == TrackDataProvider.ETrackerMode.HED) {

					throw new IllegalStateException("Not implemented yet.");
				}

				if (x == 0 || y == 0)
					continue;

				if (TrackDataProvider.eTrackerMode == TrackDataProvider.ETrackerMode.RED
					|| TrackDataProvider.eTrackerMode == TrackDataProvider.ETrackerMode.SIMULATED_BY_MOUSE_MOVEMENT) {

					eyePosInputQueue.add(new float[] { x, y });
					if (eyePosInputQueue.size() > TrackDataProvider.POSITON_SMOOTH_RANGE) {
						eyePosInputQueue.remove();
					}

					diameterInputQueue.add((float) diameter);
					if (diameterInputQueue.size() > TrackDataProvider.DEPTH_SMOOTH_RANGE) {
						diameterInputQueue.remove();
					}
				}
				else if (TrackDataProvider.eTrackerMode == TrackDataProvider.ETrackerMode.HED) {

					headPosInputQueue.add(new float[] { x, y });
					if (headPosInputQueue.size() > TrackDataProvider.POSITON_SMOOTH_RANGE) {
						headPosInputQueue.remove();
					}
				}

				// System.out.println("From: " + IPAddress + ":" + port);
				// System.out.println("Message: " + sentence);
				// System.out.println("X/Y: " + x + "/" + y);
				// System.out.println("Diameter: " +diameter);
			}

			serverSocket.disconnect();
			serverSocket.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

	}
}
