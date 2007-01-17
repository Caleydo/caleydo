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
/**
 * 
 */
package org.studierstube.net.protocol.muddleware;



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

import javax.swing.JTextArea;

import org.studierstube.net.AbstractClientByteStreamHandler;
import org.studierstube.net.protocol.ErrorMessageHandler;
import org.studierstube.net.protocol.muddleware.Message;
import org.studierstube.net.protocol.muddleware.Operation;
//import org.studierstube.net.protocol.muddleware.OperationEnum;



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
 implements ErrorMessageHandler  {
	
	
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
	
	private int iMessageInfoStyle = Message.MESSAGE_STYLE_BRIEF;
	
	/**
	 * Output text area for debug messages. 
	 * This is set via the constructor only.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.ClientByteStreamHandler#ClientByteStreamHandler(JTextArea)
	 */
	private JTextArea jta_logMessageOutput = null;
	
	/* ------------------------
	 *    SERVER CONNECT
	 * ------------------------
	 */

	
	protected Vector <Message> vecReceiveBuffer;
	
	protected Vector <Message> vecSendBuffer;
	
	protected Vector <String> vecLogMessageBuffer;
	
	
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
	public ClientByteStreamHandler( JTextArea jTextArea_logMessage ) {
		
		super( "localhost", 20000 );
		
		initDatastructures();
		
		jta_logMessageOutput = jTextArea_logMessage;
	}
 
	private void initDatastructures() {
		
		vecReceiveBuffer = new Vector <Message> (40);
		
		vecSendBuffer = new Vector <Message> (40);
		
		vecLogMessageBuffer = new Vector <String> (10000);						
	}
	
	/**
	 * @see org.studierstube.net.AbstractClientByteStreamHandler#connect()
	 */
	public boolean connect() {
		return super.connect();
	}
	
	private void addMessage( Message add, boolean bSentNotReceived ) {
		
		if ( bRecordSendAction ) {
			
			if ( bSentNotReceived ) {
				vecSendBuffer.addElement( add );
			} else {
				vecReceiveBuffer.addElement( add );
			}
		}
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
	
	
	protected Message sendReceiveMessageTest() {
		
		Operation op = new Operation( Operation.OP_ELEMENT_EXISTS );
		op.setXPath( "/StbAM/Users/User/");
		
		Message sendMsg = new Message();
		sendMsg.addOperation( op );
				
		return sendReceiveMessage( sendMsg );
	}
	
//	public boolean isConnected() {
//		return super.isConnected();
//	}
	
	public Message sendReceiveMessage( Message sendMsg ) {
		
		int id = 3;
		
		if ( bAutoIncrementId ) {
			id = iAutoIncrementId++;
			sendMsg.setId( id );
		}
		
		
		Message receiveMsg = new Message();
		
		logMsg( "SEND: " + sendMsg.toString(iMessageInfoStyle), false );
		
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
			
			addMessage( sendMsg, true );
			
			receiveMsg.parseByteArrayFromInStream( inStream, this );
						
			if ( receiveMsg != null ) {
				logMsg( "RECEIVE:" + receiveMsg.toString(iMessageInfoStyle), true );
				
				addMessage( receiveMsg, false );
				
				return receiveMsg;
			} else {
				return null;
			}
			
			
		} catch (IOException ioe) {
			logMsg( "ERROR while send/receive message: " + ioe.toString(), true );			
			return null;
		} catch ( NullPointerException npe) {
			logMsg( "SEND: connection not availble. Please reconnect!", true );
			return null;
		}
		
	}
	
	
	

	
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//			
//		ClientByteStreamHandler tester = new ClientByteStreamHandler();
//		
//		//tester.getConnectionData();
//	}

	
	public void logMsg( String text, boolean newLine ) {
		
		String output;
		
		if ( newLine ) {
			output = text + "\n";
		} else {
			output = text;
		}
		
		vecLogMessageBuffer.addElement( output );
		System.out.print( output );
		
		if ( jta_logMessageOutput != null ) {
			jta_logMessageOutput.append( output );
		}
	}

	
	public void getConnectionData() {
		
		if ( ! connectToServer("localhost",20000) ) {
			
			System.out.println("Shut down!");
			return;
		}
		
		try {			
			Message sendMsg = new Message( Operation.OP_REQUEST_CLIENTID );
			
			sendMsg.setId( 14 );
			
			byte [] sendByteArray = sendMsg.createMessageByteArray();
			
			logMsg( "P: SEND: " + sendMsg.toString(iMessageInfoStyle) , false );
			
			outStream.write( sendByteArray );			

			
			Message receiveMsg = new Message();
			
			if ( receiveMsg.parseByteArrayFromInStream( inStream, this ) ) {
				logMsg("P: RECEIVE: " + receiveMsg.toString(iMessageInfoStyle),true );
			} else {
				logMsg("\n  FAILED! " ,true );
			}
			
			
		} catch ( IOException ioe ) {
			logMsg( "ERROR: " + ioe.toString() , true );
			
		} finally {
			//disconnectFromServer();
		}
		
		/* reset Id ..*/
		iAutoIncrementId = 1;
		
		disconnectFromServer();
		
	}
	
	
	

}
