package org.caleydo.core.util.tracking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Queue;
import java.util.StringTokenizer;

class TrackDataListenerThread
	extends Thread {

	private DatagramSocket serverSocket;
	private byte[] receiveData;

	private Queue<float[]> posInputQueue;
	
	private boolean bKeepRunning = true;

	public TrackDataListenerThread(Queue<float[]> posInputQueue) {

		this.posInputQueue = posInputQueue;
	}

	public void stopThread() {
		bKeepRunning = false;
	}
	
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

//				System.out.println("Waiting for datagram packet");

				serverSocket.receive(receivePacket);
				String sentence = new String(receivePacket.getData());

				if (!sentence.contains("ET_SPL"))
					continue;
				
				StringTokenizer tokenizer = new StringTokenizer(sentence, " ");

				tokenizer.nextToken();
				tokenizer.nextToken();
				int x = new Integer(tokenizer.nextToken());

				 tokenizer.nextToken();
				 int y = new Integer(tokenizer.nextToken());

//				 String tmp = tokenizer.nextToken();
//				 tmp = tmp.substring(0, tmp.indexOf('\n'));
//				 int y = new Integer(tmp);

				
				if (x == 0 || y == 0) 
					continue;
				

				posInputQueue.add(new float[] { x, y });

				if (posInputQueue.size() > TrackDataProvider.SMOOTH_RANGE) {
					posInputQueue.remove();
				}

				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				System.out.println("From: " + IPAddress + ":" + port);
				System.out.println("Message: " + sentence);
				System.out.println("X/Y: " + x + "/" + y);
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
