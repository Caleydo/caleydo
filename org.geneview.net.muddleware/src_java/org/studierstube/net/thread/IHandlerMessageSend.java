package org.studierstube.net.thread;

import org.studierstube.net.protocol.muddleware.IMessage;

public interface IHandlerMessageSend {

	/**
	 * Register callback specialized to IMessage
	 * 
	 * @param sendMsg message with id that should trigger the callback on receiving a message with the same id
	 * @param callbackObject object called
	 */
	public void sendMessage(IMessage sendMsg, IMessageCallback callbackObject);
	
}
