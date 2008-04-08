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

package org.studierstube.muddleware.console.tester;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;


import org.studierstube.net.protocol.ErrorMessageHandler;
import org.studierstube.net.protocol.muddleware.Message;
//import org.studierstube.net.protocol.muddleware.Operation;
import org.studierstube.net.protocol.muddleware.Header;
import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.OperationEnum;



/**
 * Tester emulating Muddleware clinet. 
 * Revice messages from Muddleware server and sends one Message to the Muddleware server.
 * 
 * Note: JAVA only
 * 
 * @author Michael Kalkusch
 *
 */
public class ClientConnectionSocketTester 
extends AConnectionSocketTester 
implements ErrorMessageHandler {

	//private final static int iMaxSizeMessage = 10000;
	
	private Socket client;
	
	private OutputStream outStream = null;
	
	private InputStream inStream = null;
	
	/**
	 * 
	 */
	public ClientConnectionSocketTester() {
		super(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ClientConnectionSocketTester tester = new ClientConnectionSocketTester();
		tester.getConnectionData();
	}
	

	private boolean connectToServer( final String sServerName, final int iServerPort ) {
		
		InetAddress serverAddress = null;
		
		try {
			serverAddress = InetAddress.getByName(sServerName);
		} catch ( UnknownHostException uhe ) {
			
			try {
				serverAddress = InetAddress.getLocalHost();
			} catch ( UnknownHostException uhe2 ) {
				System.exit(-1);
			}
		}
		
		try {
			client = new Socket( serverAddress , iServerPort );
			
			System.out.print( "connect.." );
			
			try {
				inStream = client.getInputStream();
				System.out.print( ".. input-stream .." );
			} catch ( IOException ioe ) {
				logMsg(this, "..input-stream [FAILED]");	
				inStream = null;
			}	
			
			try {
				outStream = client.getOutputStream();
				logMsg(this, ".. output-stream ..");	
				
			} catch ( IOException ioe ) {
				logMsg(this, "..output-stream [FAILED]");	
				outStream = null;
			}
			
			logMsg(this, "..[DONE]");
			return true;
			
		} catch ( IOException ioe ) {
			logMsg(this, "..[FAILED]");	
			return false;
		}
	}
	
	private void disconnectFromServer() {
		
		try {
			logMsg(this, "disconnect...");
			
			if ( inStream != null ) {
				inStream.close();
			} else {
				
			}
			
			if ( outStream != null ) {
				outStream.close();	
			} else {
				
			}
			
			client.close();
			
			logMsg(this, "..[DONE]");
			
		} catch ( IOException ioe ) {
			
			logMsg(this, "..[FAILED]");
		}
		
	}
	
	public void getConnectionData_explicit() {
		
		if ( ! connectToServer("localhost",20000) ) {
			
			System.out.println("Shut down!");
			return;
		}
		
		try {			
			IMessage sendMsg = new Message( OperationEnum.OP_REQUEST_CLIENTID );
			
			sendMsg.setId( 14 );
			
//			Operation op = new Operation();
//			
//			op.setOperation( Operation.OP_REQUEST_CLIENTID );
//			
//			sendMsg.addOperation( op );
			
			logMsg(this, "PRE: " + sendMsg.toString());
			
			byte [] sendByteArray = sendMsg.createMessageByteArray();
			
			logMsg(this, "POST: " + sendMsg.toString() + "\n  ==> SEND..");
			
			outStream.write( sendByteArray );
			
			logMsg(this, "OUT: " + sendMsg.toString());

			byte[] inStreamHeaderByteArray = new byte[Header.SIZE_OF_HEADER];
			
			inStream.read(inStreamHeaderByteArray);
			
			IMessage receiveMsg = new Message();
			
			if ( receiveMsg.parseHeaderOnly(inStreamHeaderByteArray) ) {
				byte[] inStreamWithoutHeaderByteArray = new byte[ receiveMsg.getRestSize() ];
				
				inStream.read(inStreamWithoutHeaderByteArray);
				
				if ( receiveMsg.parseByteArray(inStreamWithoutHeaderByteArray) ) {
					
					logMsg(this, "\nMessage --> " + receiveMsg.toString());
					
				} else {
					logMsg(this, "ERROR while content of message! ");
				}
				
			} else {
				logMsg(this, "ERROR while reading header! ");
			}
			
		} catch ( IOException ioe ) {
			logMsg(this, "ERROR: " + ioe.toString());
			
		} finally {
			//disconnectFromServer();
		}
		
		disconnectFromServer();
		
	}
	
	public void getConnectionData() {
		
		if ( ! connectToServer("localhost",20000) ) {
			
			System.out.println("Shut down!");
			return;
		}
		
		try {			
			IMessage sendMsg = new Message( OperationEnum.OP_REQUEST_CLIENTID );
			
			sendMsg.setId( 14 );
			
			logMsg(this, "PRE: " + sendMsg.toString());
			
			byte [] sendByteArray = sendMsg.createMessageByteArray();
			
			logMsg(this, "POST: " + sendMsg.toString() + "\n  ==> SEND..");
			
			outStream.write( sendByteArray );			

			
			IMessage receiveMsg = new Message();
			
			if ( receiveMsg.parseByteArrayFromInStream( inStream, this ) ) {
				logMsg(this,"\n  DONE! ");
			} else {
				logMsg(this,"\n  FAILED! " );
			}
			
			
		} catch ( IOException ioe ) {
			logMsg(this, "ERROR: " + ioe.toString());
			
		} finally {
			//disconnectFromServer();
		}
		
		disconnectFromServer();
	}

}
