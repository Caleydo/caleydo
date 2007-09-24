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

import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
//import java.util.concurrent.locks.Condition;

import org.studierstube.net.protocol.ErrorMessageHandler;
import org.studierstube.net.protocol.muddleware.IMessage;
//import org.studierstube.net.protocol.muddleware.IOperation;
//import org.studierstube.net.protocol.muddleware.Message;
//import org.studierstube.net.protocol.muddleware.Operation;
//import org.studierstube.net.protocol.muddleware.OperationEnum;
import org.studierstube.net.thread.IMessageCallback;
import org.studierstube.util.StudierstubeException;


/**
 * @author Michael Kalkusch
 *
 */
public class OutputStreamThread 
extends AStreamThread 
implements ErrorMessageHandler {
	
	/**
	 * Input stream
	 */
	protected OutputStream outStream = null;
	
	private Lock readLock;
	
//	private Condition readCondition;
	
//	private final long lTimeWaitingNanoSecondes = 1000000;
	
	private ArrayList <IMessage> alBufferSendMessage;
	
	public OutputStreamThread(IMessageCallback callbackTarget) {
		super(callbackTarget,null);
		
		readLock = new ReentrantLock();
//		readCondition = readLock.newCondition();
		
		alBufferSendMessage = new ArrayList <IMessage> ();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		logger.log( this, "connect inputstream..("+client.getInetAddress().toString()+":"+client.getPort());
		
		try {
			outStream = client.getOutputStream();
			logger.log(this, ".. output-stream .." );
			
//			IOperation op = new Operation( OperationEnum.OP_ELEMENT_EXISTS );
//			op.setXPath( "/StbAM/Users/User/");
//			
//			IMessage sendMsg = new Message();
//			sendMsg.addOperation( op );
//					
//			outStream.write( sendMsg.createMessageByteArray() );
			
			while (bParseData) {
				
				try {
					readLock.lock();
					
//					readCondition.awaitNanos(lTimeWaitingNanoSecondes);		
					
					if ( ! alBufferSendMessage.isEmpty() ) {
						Iterator <IMessage> iter = alBufferSendMessage.iterator();
						
						try {
	//						//FAST VERSION, no debug output!
	//						while (iter.hasNext()) {
	//							outStream.write( ((IMessage) iter.next() 
	//									).createMessageByteArray());
	//						}
							
							//SLOW version with debug output
							while (iter.hasNext()) {
								IMessage buffer = (IMessage) iter.next();
								outStream.write(buffer.createMessageByteArray());
								
							//	logger.log(this, "OUT: " + buffer.toString());
							}
							
							alBufferSendMessage.clear();
							
						} catch (StudierstubeException stbe) {
							logger.log(this, "ERROR while write outstream: " + stbe.toString());
						} catch (SocketException se) {
							logger.log(this, "ERROR while write outstream: GENERAL! " + se.toString());
							callbackTarget.lostConnectionToServer();
						}
						// reduce 100% CPU usage in this thread !
						
					}  //if ( ! alBufferSendMessage.isEmpty() ) {
					
//					readCondition.signal();
//				}
//				catch (InterruptedException ie) {
//					bParseData = false;
				}
				finally {
					readLock.unlock();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		
				
			}
			
//			while (bParseData) {
//				IMessage receiveMsg = new Message(IMessage.);
//				receiveMsg.parseByteArrayFromInStream( outStream, this );
//			}
			
		} catch ( IOException ioe ) {
			logger.log(this, "..output-stream [FAILED]" );	
			outStream = null;
		}	

		logger.log(this, "exit thread.run() ..output-stream" );	
	}
	
	public synchronized void startStream(Socket client) {
		this.setClient(client);
		bParseData = true;
		
		runner = new Thread(this);
		runner.setName("network-output tream:" + client.getPort() + " :"+client.getLocalPort());
		runner.start();
	}
	
	public synchronized void stopStream() {
		
		bParseData = false;
		
		runner.interrupt();
		
		if ( outStream != null ) {
			try {
				outStream.close();
				outStream = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean sentMessage( IMessage sendMsg ) {
		try {
			readLock.lock();
			
//			readCondition.awaitNanos(lTimeWaitingNanoSecondes);			
			alBufferSendMessage.add(sendMsg);			
//			readCondition.signal();
		}
//		catch (InterruptedException ie) {
//			return false;
//		}
		catch (StudierstubeException se) {
			logger.log(this, "ERROR: " + se.toString() );
		}
		finally {
			readLock.unlock();
		}
		
		return true;
	}
	
	/**
	 * @see org.studierstube.net.protocol.ErrorMessageHandler#logMsg(java.lang.Object, Stringt)
	 */
	public final void logMsg(Object cl, String msg) {
		logger.log(cl, msg);	
	}
	
//	public final Condition getOutgoingNotificationCondition() {
//		return this.readCondition;
//	}
	
	public final Lock getOutgoingNotificationLock() {
		return this.readLock;
	}
	
	public final boolean isConnected() {
		if ( isConnected_Client() ) {
			return true;
		}
		
		if ((outStream != null)) {
			return true;
		}
		
		return false;
	}
	public void reconnect() throws IOException {
		try {
			outStream = client.getOutputStream();
			logger.log(this, ".. output-stream connect .." );
		} catch ( IOException ioe ) {
			logger.log(this, "..output-stream connect [FAILED]" );	
			outStream = null;
			throw new IOException( "reconnect() failed; "+ ioe.toString());
		}	
	}
}
