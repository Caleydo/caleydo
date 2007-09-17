package org.studierstube.net.thread;

import org.studierstube.net.protocol.muddleware.IMessage;

/**
 * Handel callbacks created from Muddleware for either IMessages or IOperations.
 * 
 * @author Michael Kalkusch
 *
 * @see org.studierstube.net.thread.INetworkHanlderClientManager trigers callback
 */
public interface IMuddlewareCallback {

	/**
	 * Notify class, that connection to server is not available any more.
	 *
	 */
	public void lostConnectionToServer();
	
	/**
	 * Notify class, that connection to serever was established.
	 * Note: client starts in state "connected"; thus this method is only called after connection was lost lostConnectionToServer()
	 * 
	 */
	public void reconnectionToServerEstablished();
	
	/**
	 * Default callback is called every time sendMessage(IMessage) receives a reply.
	 * 
	 * @param received received IMessage
	 * 
	 * @see org.studierstube.net.thread.INetworkHanlderClientManager#sendMessage(IMessage)
	 * @see org.studierstube.net.thread.INetworkHanlderClientManager#setDefaultCallback(IMuddlewareCallback)
	 */
	public void callbackDefaultOnReceive(IMessage received);
}
