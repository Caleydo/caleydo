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
import java.io.InputStream;
import java.net.Socket;
//import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.Message;
import org.studierstube.net.protocol.ErrorMessageHandler;
import org.studierstube.net.thread.IMessageCallback;
import org.studierstube.util.StudierstubeException;


/**
 * @author Michael Kalkusch
 *
 */
public class InputStreamThread extends AStreamThread implements ErrorMessageHandler {

	
	/**
	 * Input stream
	 */
	protected InputStream inStream = null;
	
	private Lock receiveMessageLock;
	
//	private Condition notifyOnIncomingMessage;
	
	public InputStreamThread( IMessageCallback callbackTarget) {
		
		super(callbackTarget, null);
		
		receiveMessageLock = new ReentrantLock();
//		notifyOnIncomingMessage = receiveMessageLock.newCondition();

	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		logger.log( this, "connect input stream..("+client.getInetAddress().toString()+":"+client.getPort());
		
		try {
			reconnect();
			
			while (bParseData) {
				IMessage receiveMsg = new Message();
				
				try {
					
					/**
					 * blocking while no input data is received...
					 */
					receiveMsg.parseByteArrayFromInStream( inStream, this );
					
					receiveMessageLock.lockInterruptibly();
					
					//notifyOnIncomingMessage.signal();
					
					callbackTarget.callbackMessageOnReceive(receiveMsg);
					
				} catch (StudierstubeException se) {
					this.logger.log(this, "ERROR while read input stream: " + se.toString() + " (StudierstubeException)");
					this.callbackTarget.lostConnectionToServer();
					
				} catch (InterruptedException e) {
					this.logger.log(this, "ERROR while read input stream: " + e.toString() + " (InterruptedException)");
					this.callbackTarget.lostConnectionToServer();
					//e.printStackTrace();
				} finally {
					receiveMessageLock.unlock();
				}
				
				
			//	logger.log(this, "IN: " + receiveMsg.toString());
			}
			
		} catch ( IOException ioe ) {
			logger.log(this, "..input-stream [FAILED]" );	
			inStream = null;
		}	

		logger.log(this, "exit thread.run() ..input-stream" );	
	}


	
	public synchronized void startStream(Socket client) {
		this.setClient(client);
		bParseData = true;
		
		if ( runner == null ) {
			/* calling startStream the first time */
			runner = new Thread(this);
		}
		
		if ( !runner.isAlive()) {
			/* start thread, if it is not running yet */
			runner.setName("network-input tream:" + client.getPort() + " :"+client.getLocalPort());
			runner.start();
		} else {
			throw new StudierstubeException("try to start thread in stream, that is already running!");	
		} //if ( !runner.isAlive()) {..} else {..}
	}
	
	public synchronized void stopStream() {
		
		bParseData = false;
		
		if ( runner == null ) {
			throw new StudierstubeException("try to stop thread in stream, that was not started!");
		}
		
		runner.interrupt();
		
		try {
			receiveMessageLock.lock();
//			notifyOnIncomingMessage.signal();
		} finally {
			receiveMessageLock.unlock();
		}
		
		if ( inStream != null ) {
			try {
				inStream.close();
				inStream = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

//	public final Condition getIncomingNotificationCondition() {
//		return this.notifyOnIncomingMessage;
//	}
	
	public final Lock getIncomingNotificationLock() {
		return this.receiveMessageLock;
	}
	
	/**
	 * @see org.studierstube.net.protocol.ErrorMessageHandler#logMsg(java.lang.Object, Stringt)
	 */
	public final void logMsg(Object cl, String msg) {
		logger.log(cl, msg);	
	}

	public final boolean isConnected() {
		if ( isConnected_Client() ) {
			return true;
		}
		
		if ((inStream != null)) {
			return true;
		}
		
		return false;
	}

	public void reconnect() throws IOException {
		try {
			inStream = client.getInputStream();
			logger.log(this, ".. input-stream connect .." );
		} catch ( IOException ioe ) {
			logger.log(this, "..input-stream connect [FAILED]" );	
			inStream = null;
			throw new IOException( "reconnect() failed; "+ ioe.toString());
		}	
	}
}
