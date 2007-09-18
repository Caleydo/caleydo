package org.studierstube.net.thread;

import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.thread.IMessageCallback;
//import org.studierstube.net.thread.IOperationSendMsg;
import org.studierstube.net.thread.IMuddlewareCallback;

public interface INetworkHanlderClientManager 
extends IMessageCallback {

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void startManager();

	public void stopManager();
	
	public boolean isManagerRunning();

	public void submitAllMessages();

	public boolean isTriggerSendInstantly();

	public void setTriggerSendInstantly(boolean triggerSendInstantly);
	
	/**
	 * Sends a Message. Default callback will be called for the answer.
	 * 
	 * @param sendMsg
	 * 
	 * @see org.studierstube.net.thread.INetworkHanlderClientManager#setDefaultCallback(IMuddlewareCallback)
	 */
	public void sendMessage(IMessage sendMsg);
	
	public void sendMessage(IMessage sendMsg, IMessageCallback callbackObject);
	
	/**
	 * Set default target for sendMessage(IMEssage)
	 * 
	 * @param callbackTarget
	 * 
	 * @see org.studierstube.net.thread.INetworkHanlderClientManager#sendMessage(IMessage)
	 */
	public void setDefaultCallback(IMessageCallback callbackTarget);
	
	/**
	 * Test if object is registered for any callback.
	 * 
	 * @param test object to be tested
	 * @return TRUE if test is registered for any callbacks
	 */
	public boolean isObjectRegisteredForCallback(Object test);
	
	

}