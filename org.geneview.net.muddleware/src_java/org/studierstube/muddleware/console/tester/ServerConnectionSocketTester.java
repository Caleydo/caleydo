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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.studierstube.net.protocol.ErrorMessageHandler;
import org.studierstube.net.protocol.muddleware.Message;
//import org.studierstube.net.protocol.muddleware.Operation;
//import org.studierstube.net.protocol.muddleware.Footer;
import org.studierstube.net.protocol.muddleware.Header;
import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.OperationEnum;


/**
 * Tester emulating Muddleware server. Revice Muddleware-messages from Muddleware client.
 * 
 * Note: JAVA only
 * 
 * @author Michael Kalkusch
 *
 */
public class ServerConnectionSocketTester
extends AConnectionSocketTester 
implements ErrorMessageHandler {
	
	private boolean bParseAsMessage = true;
	
	protected int iServerPort = 20000;
	
	/**
	 * 
	 */
	public ServerConnectionSocketTester() {
		super(null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ServerConnectionSocketTester tester = new ServerConnectionSocketTester();
		
		tester.actAsByteServer();
		
		//tester.actAsServer();
	}
	
//	public void actAsServer() {
//	
//		try {
//			ServerSocket server = new ServerSocket(20000);
//			
//			Socket srvConnection = server.accept();
//			
//			System.out.println( "connect from " + srvConnection.getInetAddress().getHostName() +
//					" IP=" + srvConnection.getInetAddress().toString() );
//			
//			InputStream in = srvConnection.getInputStream();
//			
//			if ( bParseAsMessage ) {
//				Message inMsg = new Message();
//				
//				if ( inMsg.parseByteArrayFromInStream( in, this ) ) {
//					logMsg(this,  "IN: " + inMsg.toString() , true );
//					
//					
//				} else {
//					logMsg(this,  "Could not parse message from input stream! Skip message..");
//				}
//				
//			} 
//			else 
//			{
//				System.out.println( "key=" + Integer.toString( in.read() ) );
//				System.out.println( "id=" + Integer.toString( in.read() ) );
//				System.out.println( "num=" + Integer.toString( in.read() ) );
//				System.out.println( "rest=" + Integer.toString( in.read() ) );
//				
//				byte[] array = new byte[500];
//				
//				System.out.println( "rest=" + Integer.toString( in.read( array ) ) );
//			}
//			
//			
////			
////			ObjectInputStream objIn = new ObjectInputStream( in );
////			
////			System.out.println( "key=" + Integer.toString( objIn.readUnsignedShort() ) );
////			System.out.println( "id=" + Integer.toString( objIn.readUnsignedShort() ) );
////			System.out.println( "num=" + Integer.toString( objIn.readUnsignedShort() ) );
////			System.out.println( "rest=" + Integer.toString( objIn.readUnsignedShort() ) );
////			
//			in.close();
//			srvConnection.close();
//			
//		} catch (IOException ioe) {
//			
//		}
//		
//	}

	public void actAsByteServer() {

		Socket srvConnection = null;
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(iServerPort);
		} catch (IOException ioe) {
			System.err.println("server could not start. server-port=" +
					iServerPort + " is in use"
					+ ioe.toString());
		}
		
		try {
			logMsg(this, "start server to server-port=" + server.getLocalPort());

			boolean bRun = true;

			while (bRun) {

				srvConnection = server.accept();

				InputStream in = null;
				OutputStream out = null;

				if ( bParseAsMessage ) {
									
					IMessage inMsg = new Message();
					IMessage outMsg = new Message( OperationEnum.OP_REQUEST_CLIENTID );
	
					logMsg(this, "connect to server-port="
							+ server.getLocalPort() + " from ["
							+ srvConnection.getInetAddress().getHostName()
							+ "] IP=" + srvConnection.getInetAddress().toString()
							+ " :" + srvConnection.getPort());
	
					in = srvConnection.getInputStream();
					out = srvConnection.getOutputStream();
	
					BufferedInputStream bufferedIn = new BufferedInputStream(in);
	
					byte[] headerData = new byte[16];
	
					try {
						bufferedIn.read(headerData, 0, Header.SIZE_OF_HEADER);
	
						if (!inMsg.parseHeaderOnly(headerData)) {
							logMsg(this, "Parse error while reading header");
							return;
						}
	
						int iRestSize = inMsg.getHeader().getRestSize();
	
						byte[] dataArray = new byte[iRestSize];
	
						bufferedIn.read(dataArray, 0, iRestSize);
	
						if (!inMsg.parseByteArray(dataArray)) {
							logMsg(this, "Parse while reading message block of package "
											+ inMsg.getHeader().toString());
							// return;
						}
	
						logMsg(this, "DONE!\n" + inMsg.toString());
	
						outMsg.setId( inMsg.getId() );
						
						outMsg.createMessageByte( out );
					
						logMsg(this, "OUT: " + inMsg.toString());
						
					} catch (IOException ioe) {
						System.err.println("Error while reciving data from client: "
										+ ioe.toString());
					}
	
					in.close();
				}

			} // end: while

			srvConnection.close();

		} catch (IOException ioe) {
			System.err.println("Error while reciving data from client: "
					+ ioe.toString());
		}
	}
}
