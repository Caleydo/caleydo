/**
 * 
 */
package org.studierstube.net.thread;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;

import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.IOperation;
import org.studierstube.net.protocol.muddleware.OperationEnum;

/**
 * @author java
 *
 */
public class MuddlewareNetworkOperationsHander 
extends NetworkHandlerClientManager
implements IOperationSendMsg {

	private HashMap <Integer,IOperationCallback> hashCallbackOperation;
	
	private HashMap <Integer,IOperation> hashSentOperation;
	
	private Lock sentOperationHashLock;
	
	/**
	 * 
	 */
	public MuddlewareNetworkOperationsHander() {
		super();
				
		hashCallbackOperation = new HashMap <Integer,IOperationCallback> (); //<Integer,IOperationCallback>(); 
		
		hashSentOperation = new HashMap <Integer,IOperation> (); //<Integer,Operation>
	}

	/*
	 * (non-Javadoc)
	 * @see org.studierstube.net.thread.IOperationSendMsg#sendMessage(org.studierstube.net.protocol.muddleware.IMessage, org.studierstube.net.thread.IOperationCallback)
	 */
	public void sendMessage(IMessage sendMsg, IOperationCallback callbackObject) {
		
		Iterator <IOperation> iter = sendMsg.getOperationIterator();
		
		while ( iter.hasNext() ) {
			IOperation bufferOp = (IOperation) iter.next();
			
			if ( bufferOp.getOperation() == OperationEnum.OP_REGISTER_WATCHDOG ) {
				sendOperation(bufferOp, callbackObject);
			}
		}
		
		sendMessage(sendMsg);
		
	}
	
	public void sendOperation(IOperation sendOp, IOperationCallback callbackObject) {

		Integer bufferClientKey = new Integer(sendOp.getClientData());
		
		
		
		try {
			sentOperationHashLock.lock();
			
			if ( hashCallbackOperation.containsKey(bufferClientKey) ) {
				this.logger.log(this, "overwrite callback for operation=[" + bufferClientKey.toString() + "]");
			}
			
			hashCallbackOperation.put(bufferClientKey, callbackObject);
			
		} finally {
			sentOperationHashLock.unlock();
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
				vectorSentMsg.put(Integer.valueOf(sendMsg.getId()),sendMsg);
				
				try {
					sentOperationHashLock.lock();
					
					Iterator <IOperation> iter = sendMsg.getOperationIterator();	
					
					while ( iter.hasNext() ) {
						IOperation bufferOp = iter.next();					
						Integer bufferClientId = new Integer(bufferOp.getClientData());
						hashSentOperation.put(bufferClientId, bufferOp);
					}
				} finally {
					sentOperationHashLock.unlock();
				}
			}
			finally {
				sentListLock.unlock();
			}
		}
	}
	
	public boolean isObjectRegisteredForCallback(Object test) {
		if ( super.isObjectRegisteredForCallback(test) ) {
			return true;
		}
		
		return this.hashCallbackOperation.containsValue(test);
	}

}
