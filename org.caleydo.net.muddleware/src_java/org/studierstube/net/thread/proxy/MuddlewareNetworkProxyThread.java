/* ========================================================================
 * Copyright (C) 2004-2005  Graz University of Technology
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this framework; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For further information please contact Dieter Schmalstieg under
 * <schmalstieg@icg.tu-graz.ac.at> or write to Dieter Schmalstieg,
 * Graz University of Technology, Institut für Maschinelles Sehen und Darstellen,
 * Inffeldgasse 16a, 8010 Graz, Austria.
 * ========================================================================
 * PROJECT: Muddleware
 * ======================================================================== */

package org.studierstube.net.thread.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.thread.IMessageCallback;
import org.studierstube.net.thread.stream.InputStreamThread;
import org.studierstube.net.thread.stream.OutputStreamThread;
import org.studierstube.util.LogInterface;
import org.studierstube.util.SystemLogger;

/**
 * @author Michael Kalkusch
 *
 */
public class MuddlewareNetworkProxyThread implements Runnable, IMessageCallback {

	private LogInterface logger;
	
	private String nServerName = "localhost";
	
	private int nServerPort = 20000;

	private Socket client;
	
	private InputStreamThread inputStreamThread;
	
	private OutputStreamThread outputStreamThread;
	
	private boolean bRunServer = false;
	
	private Collection <IMessage> vectorSentMsg;
	
	private Collection <IMessage> vectorReceiveMsg;
	
	private Collection <IMessage> vectorBufferingSendMsg;
	
	private boolean bTriggerSendInstantly = true;
	
	private Lock sendBufferLock;
	private Lock sentListLock;
	private Lock receiveListLock;
	
	public MuddlewareNetworkProxyThread() {
		inputStreamThread  = new InputStreamThread(this);
		outputStreamThread = new OutputStreamThread(this);
		
		vectorSentMsg 			= new Vector <IMessage> ();		
		vectorReceiveMsg 		= new Vector <IMessage> ();		
		vectorBufferingSendMsg 	= new Vector <IMessage> ();
		
		sendBufferLock = new ReentrantLock();
		sentListLock = new ReentrantLock();
		receiveListLock = new ReentrantLock();
		
		logger = new SystemLogger();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			
			InetAddress serverAddress = 
				InetAddress.getByName(nServerName);
			
			client = new Socket( serverAddress , nServerPort );
			bRunServer = true;
			
			logger.log(this, "start server at [" + nServerName + ":" + nServerPort+ "]" );
			
			inputStreamThread.startStream(client);
			
			outputStreamThread.startStream(client);
			
//			try {
//				while (bRunServer) {
//					
//					try {
//						outputStreamThread.getOutgoingNotificationLock().lock();
//						
//						submitAllMessages();
//						
//					} finally {
//						outputStreamThread.getOutgoingNotificationLock().unlock();
//					}
//					
////					inputStreamThread.getIncomingNotificationLock().lock();	
////					
////					inputStreamThread.getIncomingNotificationCondition().await();
////					submitAllMessages();
////					inputStreamThread.getIncomingNotificationLock().unlock();
//				}
////			} catch (InterruptedException e) {
////				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			
//			
////			inputStreamThread.stopStream();
			
		} catch ( UnknownHostException uhe ) {
			
		} catch ( IOException ioe ) {
			
		} 
//		catch ( InterruptedException ire) {
//			
//		}

	}

	public void stopServer() {
		bRunServer = false;
		
		outputStreamThread.stopStream();
		inputStreamThread.stopStream();
	}
	
	public void callbackMessageOnReceive( IMessage message) {
		System.out.println("Callback triggered by input stream..." + message.toString());	
		try {
			receiveListLock.lock();
			
			vectorReceiveMsg.add(message);
			
			logger.log(this, "IN: (callback) " + message.toString() );
			
		} finally {
			receiveListLock.unlock();
		}
	}
	
	public synchronized void sendMessage( IMessage sendMsg ) {
		if ( bTriggerSendInstantly ) {
			sendMessageNow(sendMsg);
		} else {
			try {
				sendBufferLock.lock();
				vectorBufferingSendMsg.add(sendMsg);
			}
			finally {
				sendBufferLock.unlock();
			}
		}
	}
	
	protected synchronized void sendMessageNow( IMessage sendMsg ) {
		
		if ( outputStreamThread.sentMessage(sendMsg) ) {
			/**
			 * add to list od sent messages
			 */
			try {
				/**
				 * block list local
				 */
				sentListLock.lock();
				vectorSentMsg.add(sendMsg);
			}
			finally {
				sentListLock.unlock();
			}
		}
	}
	
	public synchronized void submitAllMessages() {
		try {
			sendBufferLock.lock();
		
			if ( ! vectorBufferingSendMsg.isEmpty() ) {
				Iterator <IMessage> iter = vectorBufferingSendMsg.iterator();
				
				while ( iter.hasNext() ) {
					outputStreamThread.sentMessage((IMessage) iter.next());
				}
			}
		}
		finally {
			sendBufferLock.unlock();
		}
	}

	public synchronized final boolean isTriggerSendInstantly() {
		return bTriggerSendInstantly;
	}

	public synchronized final void setTriggerSendInstantly(
			boolean triggerSendInstantly) {
		bTriggerSendInstantly = triggerSendInstantly;
	}

	public synchronized final boolean isThreadRunning() {
		return bRunServer;
	}
	
	
	public void lostConnectionToServer() {
		// TODO Auto-generated method stub
		
	}

	public void reconnectionToServerEstablished() {
		// TODO Auto-generated method stub
		
	}

	public void callbackMessageOnReceive(IMessage send, IMessage received) {
		// TODO Auto-generated method stub
		
	}

	public void callbackDefaultOnReceive(IMessage received) {
		// TODO Auto-generated method stub
		
	}
	
//	public synchronized void readAllMessages() {
//		try {
//			receiveListLock.lock();
//		
//			this.inputStreamThread.
//			if ( ! vectorBufferingSendMsg.isEmpty() ) {
//				Iterator iter = vectorBufferingSendMsg.iterator();
//				
//				while ( iter.hasNext() ) {
//					outputStreamThread.sentMessage((IMessage) iter.next());
//				}
//			}
//		}
//		finally {
//			receiveListLock.unlock();
//		}
//	}

}
