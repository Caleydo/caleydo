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

package org.studierstube.net.thread.net;



import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import java.net.Socket;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
//import java.util.Iterator;
import java.util.Vector;


import org.studierstube.net.AbstractClientByteStreamHandler;
import org.studierstube.net.protocol.ErrorMessageHandler;
import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.IOperation;
import org.studierstube.net.protocol.muddleware.Message;
import org.studierstube.net.protocol.muddleware.Operation;
import org.studierstube.net.protocol.muddleware.OperationEnum;
//import org.studierstube.net.protocol.muddleware.OperationEnum;
import org.studierstube.util.LogInterface;



/**
 * Muddleware byte reader and writer. 
 * Reveice messages from Muddleware server and send messages to it.
 * 
 * Note: JAVA only
 * 
 * @author Michael Kalkusch
 *
 */
public class ClientByteStreamHandler extends AbstractClientByteStreamHandler
 implements ErrorMessageHandler, Runnable  {
	
	
//	private String sServerName = "localhost";
//	
//	private int iServerPort = 20000;
	
	/**
	 * @see org.studierstube.net.protocol.muddleware.ClientByteStreamHandler#setAutoReconnectOnSend(boolean)
	 */
	private boolean bAutoReconnectOnSend = false;
	
	/**
	 * A series of messages can be recored to replay them later on.
	 * If this flas is true all messages are recored and paced in a Vector.
	 */
	private boolean bRecordSendAction = false;
		
	/**
	 * @see org.studierstube.net.protocol.muddleware.ClientByteStreamHandler#setAutoIncrementId(boolean)
	 */
	private boolean bAutoIncrementId = true;
	
	/**
	 * Current id used by autoIncrement. This is reset to "1" is connection is closed.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.ClientByteStreamHandler#setAutoIncrementId(boolean)
	 */
	protected int iAutoIncrementId = 1;
	
	private int iMessageInfoStyle = IMessage.MESSAGE_STYLE_BRIEF;

	
	/* ------------------------
	 *    SERVER CONNECT
	 * ------------------------
	 */
	
	//java1.3>>
	protected Vector vecReceiveBuffer;
	protected Vector vecSendBuffer;
	
	//java1.3<<
	
	//java1.5>>
	//protected Vector <Message> vecReceiveBuffer;	
	//protected Vector <Message> vecSendBuffer;	
	//protected Vector <String> vecLogMessageBuffer;
	//java1.5<<
	
	
	/* ------------------------
	 *    MESSAGE
	 * ------------------------
	 */

	
	/* ------------------------
	 *    LOGGER
	 * ------------------------
	 */

	
	
	/**
	 * 
	 */
	public ClientByteStreamHandler( final LogInterface logger,
			final String nServerName,
			final int nServerPort ) {
		
		super( logger, nServerName, nServerPort );
		
		initDatastructures();			
	}
	
	/**
	 * 
	 */
	public ClientByteStreamHandler( final LogInterface logger) {
		
		super( logger, "localhost", 20000 );
		
		this.refLogger = logger;
		
		initDatastructures();			
	}
 
	private void initDatastructures() {
		
		//java1.3>>
		vecReceiveBuffer = new Vector (40);
		vecSendBuffer = new Vector (40);
		//java1.3<<
		//java1.5>>
		//vecReceiveBuffer = new Vector <Message> (40);
		//vecSendBuffer = new Vector <Message> (40);
		//vecLogMessageBuffer = new Vector <String> (10000);
		//java1.5<<
		
	}

	
	protected final void addMessageReceived( IMessage add ) {
		
		if ( bRecordSendAction ) {
			
			vecReceiveBuffer.addElement( add );
		}
	}
	
	protected final void addMessageSent( IMessage add ) {
		
		if ( bRecordSendAction ) {
			
			vecSendBuffer.addElement( add );
		}
	}
	
	protected final IMessage sendReceiveMessageTest() {
		
		IOperation op = new Operation( OperationEnum.OP_ELEMENT_EXISTS );
		op.setXPath( "/StbAM/Users/User/");
		
		IMessage sendMsg = new Message();
		sendMsg.addOperation( op );
				
		return sendReceiveMessage( sendMsg );
	}
	

	/**
	 * @see org.studierstube.net.AbstractClientByteStreamHandler#removeAllMessages()
	 */
	public final synchronized void removeAllMessages() {
		vecSendBuffer.clear();
		vecReceiveBuffer.clear();
		
		//TODO: to make thread safe use explicit locking!
	}
	
	/**
	 * Toggle state of autoincrement.
	 * Used by GUI.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.ClientByteStreamHandler#setAutoIncrementId(boolean)
	 * @see org.studierstube.net.protocol.muddleware.ClientByteStreamHandler#isAutoIncrementIdEnabled()
	 */
	public final void toggleAutoIncrementId() {
		if ( bAutoIncrementId ) {
			setAutoIncrementId( false );
			return;
		}
		setAutoIncrementId( true );
	}
	
	
	/**
	 * Enabled or disables AutoIncremnetId for messages. 
	 * If TRUE the Id is a unique increasing number, that is reset to "1" if the connection is closed.
	 * 
	 * @param bEnableAutoIncrement TRUE for enabled auto increment Id
	 */
	public final void setAutoIncrementId( boolean bEnableAutoIncrement ) {		
		bAutoIncrementId = bEnableAutoIncrement;
	}
	
	/**
	 * Get state of autoincrement Id.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.ClientByteStreamHandler#setAutoIncrementId(boolean)
	 * @see org.studierstube.net.protocol.muddleware.ClientByteStreamHandler#toggleAutoIncrementId()
	 * 
	 * @return TRUE if autoIncrement ID is enalbed
	 */
	public final boolean isAutoIncrementIdEnabled() {
		return bAutoIncrementId;
	}
	
	/**
	 * If AutoReconnectOnSend is enable (TRUE) each message opens
	 * a connection to the server, if the connection was not established prior to sending.
	 * 
	 * @param bEnableAutoReconnectOnSend TRUE to enable this feature.
	 */
	public final void setAutoReconnectOnSend( boolean bEnableAutoReconnectOnSend ) {		
		bAutoReconnectOnSend = bEnableAutoReconnectOnSend;
	}
	
	/**
	 * Get state of AutoReconnectOnSend.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.ClientByteStreamHandler#setAutoReconnectOnSend(boolean)
	 * 
	 * @return TRUE if feature is enabled
	 */
	public final boolean isAutoReconnectOnSendEnabled() {
		return bAutoReconnectOnSend;
	}
	
	
//	/**
//	 * @see org.studierstube.net.AbstractClientByteStreamHandler#connect()
//	 */
//	public boolean connect() {
//		return super.connect();
//	}
	
//	public boolean isConnected() {
//		return super.isConnected();
//	}
	
	public IMessage sendReceiveMessage( IMessage sendMsg ) {
		
		int id = 3;
		
		if ( bAutoIncrementId ) {
			id = iAutoIncrementId++;
			sendMsg.setId( id );
		}
		
		
		IMessage receiveMsg = new Message();
		
		logMsg(this, "SEND: " + sendMsg.toString(iMessageInfoStyle) );
		
		byte [] sendByteArray = sendMsg.createMessageByteArray();
		
		try {
			if ( outStream == null ) {
				if ( bAutoReconnectOnSend ) {
					connect();
				} else {
					return null;
				}
			}
			outStream.write( sendByteArray );
			
			addMessageSent( sendMsg );
			
			receiveMsg.parseByteArrayFromInStream( inStream, this );
						
			if ( receiveMsg != null ) {
				logMsg(this, "RECEIVE:" + receiveMsg.toString(iMessageInfoStyle) );
				
				addMessageReceived( receiveMsg );
				
				return receiveMsg;
			} else {
				return null;
			}
			
			
		} catch (IOException ioe) {
			logMsg(this, "ERROR while send/receive message: " + ioe.toString());			
			return null;
		} catch ( NullPointerException npe) {
			logMsg(this, "SEND: connection not availble. Please reconnect!");
			return null;
		}
		
	}


	
	public void getConnectionData() {
		
		if ( ! connectToServer("localhost",20000) ) {
			
			logMsg(this, "Shut down!");
			return;
		}
		
		try {			
			IMessage sendMsg = new Message( OperationEnum.OP_REQUEST_CLIENTID );
			
			sendMsg.setId( 14 );
			
			byte [] sendByteArray = sendMsg.createMessageByteArray();
			
			logMsg(this, "P: SEND: " + sendMsg.toString(iMessageInfoStyle));
			
			outStream.write( sendByteArray );			

			
			IMessage receiveMsg = new Message();
			
			if ( receiveMsg.parseByteArrayFromInStream( inStream, this ) ) {
				logMsg(this, "P: RECEIVE: " + receiveMsg.toString(iMessageInfoStyle));
			} else {
				logMsg(this, "P: RECEIVE: FAILED!");
			}
			
			
		} catch ( IOException ioe ) {
			logMsg(this,  "ERROR: " + ioe.toString());
			
		} finally {
			//disconnectFromServer();
		}
		
		/* reset Id ..*/
		iAutoIncrementId = 1;
		
		disconnectFromServer();
		
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}
}
