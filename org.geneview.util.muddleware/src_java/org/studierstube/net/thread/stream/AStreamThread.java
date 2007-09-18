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

package org.studierstube.net.thread.stream;

import java.io.IOException;
import java.net.Socket;

import org.studierstube.net.thread.IMessageCallback;
import org.studierstube.util.LogInterface;
//import org.studierstube.util.StudierstubeException;
import org.studierstube.util.SystemLogger;


/**
 * Abstract class for InputStreamThread and OutputStreamThread
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AStreamThread implements Runnable {


	protected Thread runner =null;
	
	/**
	 * Reference to logger
	 */
	protected LogInterface logger;
	
	/**
	 * Reference to object triggered by callback
	 */
	protected IMessageCallback callbackTarget;

	/**
	 * Socket for client.
	 */
	protected Socket client = null;
	
	/**
	 * Define if connection to server should be established after disconnection
	 */
	protected boolean bAutoReconnect = true;

	protected boolean bParseData;
	
	/**
	 * Constructor.
	 * 
	 * 
	 * @param callbackTarget target object to receive callback
	 * @param setLogger assign logger, but keep in mind, that this is a thread. If null is used a defautl logger is created
	 */
	protected AStreamThread(final IMessageCallback callbackTarget, 
			final LogInterface setLogger) {
		this.callbackTarget = callbackTarget;
		
		if ( setLogger != null ) {
			this.logger = setLogger;
		}
		else {
			this.logger = new SystemLogger();
		}
	}
	
	/**
	 * Get current logger.
	 * 
	 * @return current logger
	 */
	protected synchronized final LogInterface getLogger() {
		return logger;
	}

	/**
	 * Set current logger.
	 * 
	 * @param logger set curretn logger
	 */
	protected synchronized final void setLogger(LogInterface logger) {
		this.logger = logger;
	}

	/**
	 * Get object receiving callback
	 * 
	 * @see org.studierstube.net.thread.IMessageCallback#callbackMessageOnReceive(org.studierstube.net.protocol.muddleware.IMessage)
	 * 
	 * @return object registered to receive callback
	 */
	protected synchronized final IMessageCallback getCallbackTarget() {
		return callbackTarget;
	}

	/**
	 * Set object receiving callback
	 * 
	 * @see org.studierstube.net.thread.IMessageCallback#callbackMessageOnReceive(org.studierstube.net.protocol.muddleware.IMessage)
	 * 
	 * @param callbackTarget set object receiving callback
	 */
	protected synchronized final void setCallbackTarget(
			IMessageCallback callbackTarget) {
		this.callbackTarget = callbackTarget;
	}

	protected synchronized final Socket getClient() {
		return client;
	}

	protected synchronized final void setClient(Socket client) {
		this.client = client;
	}
	
	public synchronized final boolean isAutoReconnectEnabled() {
		return bAutoReconnect;
	}

	public synchronized final void setAutoReconnect(boolean autoReconnect) {
		bAutoReconnect = autoReconnect;
	}
		
	/**
	 * Expose connection state.
	 * 
	 * @return TRUE if connected to IO stream
	 *
	 */
	public abstract boolean isConnected();
	
	protected final boolean isConnected_Client() {
		if ( client == null ) {
			return false;
		}
		return client.isConnected();
	}
	/**
	 * Open InputStream at client
	 *
	 */
	public abstract void reconnect() throws IOException;
	
	public abstract void startStream(Socket client);
	
	public abstract void stopStream();

	
}