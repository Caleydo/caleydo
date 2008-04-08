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

package org.studierstube.net.thread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
//import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.IOperation;
import org.studierstube.net.protocol.muddleware.OperationEnum;
import org.studierstube.net.thread.stream.InputStreamThread;
import org.studierstube.net.thread.stream.OutputStreamThread;
import org.studierstube.net.thread.IMessageCallback;
import org.studierstube.util.LogInterface;
import org.studierstube.util.StudierstubeException;
import org.studierstube.util.SystemLogger;

/**
 * @author Michael Kalkusch
 *
 */
public class NetworkHandlerClientManager 
implements INetworkHanlderClientManager {

	/**
	 * If this is set to FALSE vector-object will create out-of-memory sooner or later,
	 * because no sent messages are discharged!
	 */
	private final boolean removeSentMessagesOnReceive = true;
	
	protected LogInterface logger;
		
	protected String nServerName = "localhost";
	
	private int nServerPort = 20000;

	private Socket client;
	
	protected InputStreamThread inputStreamThread;
	
	protected OutputStreamThread outputStreamThread;
	
	protected boolean bManagerIsRunning = false;
	
	protected HashMap <Integer,IMessage> vectorSentMsg;	
	protected boolean autoIncrementSendMessages = true;
	protected int sendMessageCounter=0;
	
	//private Collection vectorReceiveMsg;
	
	private Collection <IMessage> vectorBufferingSendMsg;
	

	private HashMap <Integer,IMessageCallback>  hashCallbackMessageId;
	
	private HashMap <Integer,Boolean> hashCallbackMessageId_Watchdogs;
	
	private boolean bTriggerSendInstantly = true;
	
	private Lock sendBufferLock;
	
	protected Lock sentListLock;
	
	protected Lock receiveListLock;
	
	private Lock sentWatchdogCallbacks;
	
	protected IMessageCallback refMuddlewareCallback;
	
	public NetworkHandlerClientManager() {
		inputStreamThread  = new InputStreamThread(this);
		outputStreamThread = new OutputStreamThread(this);
		
		vectorSentMsg = new HashMap <Integer,IMessage> ();	//<Interger,IMessage>	
		//vectorReceiveMsg = new Vector ();		
		vectorBufferingSendMsg = new Vector <IMessage> ();

		
		hashCallbackMessageId = new HashMap <Integer,IMessageCallback> (); //< (Integer) IMessage.getId(),IOperationCallback>(); 
		hashCallbackMessageId_Watchdogs = new HashMap <Integer,Boolean> (); // <Integer, boolean >
		
		sendBufferLock = new ReentrantLock();
		sentListLock = new ReentrantLock();
		receiveListLock = new ReentrantLock();
		
		logger = new SystemLogger();
	}
	
	/**
	 * Overload this methode for subclasses.
	 * 
	 * @param sendMsg message to be sent.
	 */
	protected synchronized void sendMessageNow( IMessage sendMsg ) {
		sendMessageNow(outputStreamThread, sendMsg, null);
	}
	
	/**
	 * Overload this methode for subclasses.
	 * 
	 * @param sendMsg message to be sent.
	 */
	protected synchronized void sendMessageNow( OutputStreamThread outStrm, IMessage sendMsg, IMessageCallback callbackObject ) {
		Integer messageId = Integer.valueOf(sendMsg.getId());
		if (sendMsg.getId() == -1) {
			if (autoIncrementSendMessages) {
				sendMsg.setId(sendMessageCounter);
				sendMessageCounter++;
			}
		}
		if ( this.hashCallbackMessageId.containsKey(messageId)) {
			System.out.println("WARNING: Callback is already registered! Overwrite existing callback!");
		}
		
		hashCallbackMessageId.put(messageId, callbackObject);
		
		/* search for watchdogs.. */
		Iterator <IOperation> iter = sendMsg.getOperationIterator();
		
		while ( iter.hasNext() ) {
			//IOperation bufferOp = (IOperation) iter.next();
			
			if ( ((IOperation) iter.next()).getOperation() == OperationEnum.OP_REGISTER_WATCHDOG ) {
				this.hashCallbackMessageId_Watchdogs.put(messageId, new Boolean(true));
				break;
			}
		}
		
		if ( outStrm.sentMessage(sendMsg) ) {
			
			if ( ! hashCallbackMessageId_Watchdogs.containsKey( Integer.valueOf(sendMsg.getId())) ) {
				
				/**
				 * add to list of sent messages if no watchdog is registered
				 */
				try {
					/**
					 * block list local
					 */
					sentListLock.lock();
					vectorSentMsg.put(Integer.valueOf(sendMsg.getId()),sendMsg);
				}
				finally {
					sentListLock.unlock();
				}
			
			} //if ( ! hashCallbackMessageId_Watchdogs.containsKey( Integer.valueOf(sendMsg.getId())) ) {
			
		} //if ( outputStreamThread.sentMessage(sendMsg) ) {
	}
	
	/**
	 * Set a target object for callbackDefaultOnReceive(IMessage)
	 * @see org.studierstube.net.thread.INetworkHanlderClientManager#setDefaultCallback(org.studierstube.net.thread.IMuddlewareCallback)
	 */
	public final void setDefaultCallback(IMessageCallback callbackTarget) {
		this.refMuddlewareCallback = callbackTarget;
	}

	/**
	 * Forward to object set via setDefaultCallback(IMuddlewareCallback)
	 * 
	 * @see org.studierstube.net.thread.IMuddlewareCallback#callbackDefaultOnReceive(org.studierstube.net.protocol.muddleware.IMessage)
	 */
	public final void callbackDefaultOnReceive(IMessage received) {
		refMuddlewareCallback.callbackDefaultOnReceive(received);		
	}
	
	/**
	 * 
	 * @see org.studierstube.net.thread.INetworkHanlderClientManager#startManager()
	 * @see org.studierstube.net.thread.INetworkHanlderClientManager#stopManager()
	 * 
	 * @return TREU if Manger is running
	 */
	public synchronized final boolean isManagerRunning() {
		return bManagerIsRunning;
	}


	/* (non-Javadoc)
	 * @see org.studierstube.net.thread.INetworkMessageClientManager#isTriggerSendInstantly()
	 */
	public synchronized final boolean isTriggerSendInstantly() {
		return bTriggerSendInstantly;
	}

	/* (non-Javadoc)
	 * @see org.studierstube.net.thread.INetworkMessageClientManager#setTriggerSendInstantly(boolean)
	 */
	public synchronized final void setTriggerSendInstantly(
			boolean triggerSendInstantly) {
		bTriggerSendInstantly = triggerSendInstantly;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	/* (non-Javadoc)
	 * @see org.studierstube.net.thread.INetworkMessageClientManager#startManager()
	 */
	public void startManager() {
		try {
			
			InetAddress serverAddress = 
				InetAddress.getByName(nServerName);
			
			client = new Socket( serverAddress , nServerPort );
			bManagerIsRunning = true;
			
			logger.log(this, "start server at [" + nServerName + ":" + nServerPort+ "]" );
			
			inputStreamThread.startStream(client);			
			outputStreamThread.startStream(client);
			
		} catch ( UnknownHostException uhe ) {
			
		} catch ( IOException ioe ) {
			
		} 
//		catch ( InterruptedException ire) {
//			
//		}

	}

	/* (non-Javadoc)
	 * @see org.studierstube.net.thread.INetworkMessageClientManager#stopManager()
	 */
	public void stopManager() {
		bManagerIsRunning = false;
		
		outputStreamThread.stopStream();
		inputStreamThread.stopStream();
		
		this.vectorSentMsg.clear();
		this.hashCallbackMessageId_Watchdogs.clear();
		this.hashCallbackMessageId.clear();
	}
	
	/**
	 * @see org.studierstube.net.thread.IMessageCallback#callbackMessageOnReceive(org.studierstube.net.protocol.muddleware.IMessage)
	 */
	public void callbackMessageOnReceive( IMessage message) {
		try {
			receiveListLock.lock();
					
			IMessageCallback callbacktarget = callbackMessageProcedure(message);
			
			if ( callbacktarget != null ) {
				callbacktarget.callbackMessageOnReceive(message);
			} else {
			//	logger.log(this, "ERROR: no callback defined for IMessage: " + message.toString());
			}
			
		//	logger.log(this,"Callback triggered by input stream..." + message.toString());	
				
		} finally {
			receiveListLock.unlock();
		}
		
		/**
		 * End of Note: this code is identical with the one in callbackMessageOnReceive(IMessage)
		 */
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
	
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.thread.INetworkMessageClientManager#submitAllMessages()
	 */
	public synchronized void submitAllMessages() {
		try {
			sendBufferLock.lock();
		
			if ( ! vectorBufferingSendMsg.isEmpty() ) {
				Iterator <IMessage> iter = vectorBufferingSendMsg.iterator();
				
				while ( iter.hasNext() ) {
					sendMessageNow((IMessage) iter.next());
				}
			}
		}
		finally {
			sendBufferLock.unlock();
		}
	}

	
	public void sendMessage(IMessage sendMsg, IMessageCallback callbackObject) {
		
		Integer messageId = Integer.valueOf(sendMsg.getId());
		
		if ( this.hashCallbackMessageId.containsKey(messageId)) {
			System.out.println("WARNING: Callback is already registered! Overwrite existing callback!");
		}
		
		hashCallbackMessageId.put(messageId, callbackObject);
		
		/* search for watchdogs.. */
		Iterator <IOperation> iter = sendMsg.getOperationIterator();
		
		while ( iter.hasNext() ) {
			//IOperation bufferOp = (IOperation) iter.next();
			
			if ( (iter.next()).getOperation() == OperationEnum.OP_REGISTER_WATCHDOG ) {
				this.hashCallbackMessageId_Watchdogs.put(messageId, new Boolean(true));
				break;
			}
		}
		
		sendMessage(sendMsg);
		
	}


	/**
	 * Overloadd this methode in sub-classes.
	 * 
	 * @see org.studierstube.net.thread.IMuddlewareCallback#lostConnectionToServer()
	 */
	public synchronized void lostConnectionToServer() {
		this.bManagerIsRunning = false;
		
	}

	/**
	 * (Overloadd this methode in sub-classes.
	 * 
	 * @see org.studierstube.net.thread.IMuddlewareCallback#reconnectionToServerEstablished()
	 */
	public synchronized void reconnectionToServerEstablished() {
		this.bManagerIsRunning = true;
		
		if ( ! client.isConnected()) {
			try {
				client.close();
			} catch (IOException e) {
				bManagerIsRunning = false;
				throw new StudierstubeException("error while closing socket to muddleware server. " + e.toString());
				//e.printStackTrace();
			}
			
			InetSocketAddress socketAddress = 
				new InetSocketAddress(nServerName, nServerPort);
			
			try {
				
				client.connect(socketAddress );
			} catch (IOException e) {
				bManagerIsRunning= false;
				throw new StudierstubeException("error while connecting to muddleware server" + 
						socketAddress.toString() + 
						" " + e.toString());				
				//e.printStackTrace();
			}
		}
		
		if ( ! outputStreamThread.isConnected() ) {
			outputStreamThread.startStream(client);
		}
		if ( ! inputStreamThread.isConnected() ) {
			inputStreamThread.startStream(client);
		}
		
	}

	public void callbackMessageOnReceive(IMessage send, IMessage received) {
	//	System.out.println("Callback triggered by input stream..." + received.toString());
		
		if ( send != null ) {
			logger.log(this, "WARNING: callbackMessageOnReceive(send,received) called with send!= null! send: " + 
					send.toString() );
		}
		
		try {
			receiveListLock.lock();
			
			IMessage sendLookup = vectorSentMsg.get( Integer.valueOf(received.getId()));			
			IMessageCallback callbacktarget = callbackMessageProcedure(received);
			
			if ( callbacktarget != null ) {
				callbacktarget.callbackMessageOnReceive(sendLookup, received);
			} else {
				//logger.log(this, "ERROR: no callback defined for IMessage: " + received.toString());
			}
			
			//TODO: notify any registered callback objects for message "received"
			//TODO: look send message according to received message.
			//TODO: look if received message is a watchdog. otherwise remove is from hasMap.
			
		//	logger.log(this,"Callback triggered by input stream..." + received.toString());	

				
		} finally {
			receiveListLock.unlock();
		}
		
		/**
		 * End of Note: this code is identical with the one in callbackMessageOnReceive(IMessage)
		 */
	
	}

	
	protected IMessageCallback callbackMessageProcedure(IMessage received) {
		
		IMessageCallback messageCallback = null;
		
		//logger.log(this,"Callback triggered by input stream..." + received.toString());	
		
		/**
		 * Note: this code is identical with the one in callbackMessageOnReceive(IMessage)
		 */
		try {
			receiveListLock.lock();
			
			//vectorReceiveMsg.add(message);
			
			Integer messageId = Integer.valueOf(received.getId());
			//System.out.println("msg ["+ messageId+"]");
			messageCallback = hashCallbackMessageId.get(messageId);

			if ( messageCallback != null ) {
				
				/** 
				 * test if first operation is OperationEnum.OP_UNREGISTER_WATCHDOG ...
				 */
				if ( received.getNumOperations() > 0 ) {
					IOperation op = received.getOperation(0);
					
					if ( op.getOperation() == OperationEnum.OP_UNREGISTER_WATCHDOG ) {
						
						try {
							sentWatchdogCallbacks.lock();
							hashCallbackMessageId_Watchdogs.remove(messageId);
							
							logger.log(this,"Unregister watchdog id=" + messageId.toString());
						}
						finally {
							sentWatchdogCallbacks.unlock();
						}
						
					} //if ( op.getOperation() == OperationEnum.OP_UNREGISTER_WATCHDOG ) {
				} //if ( message.getNumOperations() > 0 ) {
				
			} else {  //if ( messageCallback != null ) {..}else{
				/* no watchdog registered... */
				
				if ( removeSentMessagesOnReceive ) {
					this.vectorSentMsg.remove(received);
				}
				
				messageCallback = refMuddlewareCallback;
				
			} // if ( messageCallback != null ) {..}else {..}
			
	//		logger.log(this, "IN: (callback) " + received.toString() );
			
		} finally {
			receiveListLock.unlock();
		}
		
		/**
		 * This object shall receive the callback triggered by the IMessage receive
		 */
		return messageCallback;
	}

	/**
	 * @see org.studierstube.net.thread.INetworkHanlderClientManager#isObjectRegisteredForCallback(java.lang.Object)
	 */
	public boolean isObjectRegisteredForCallback(Object test) {
		if ( hashCallbackMessageId.containsValue(test) ) {
			return true;
		}
		
		return hashCallbackMessageId_Watchdogs.containsValue(test);
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
