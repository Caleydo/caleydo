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

package org.studierstube.muddleware.sample.console;

import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.IOperation;
import org.studierstube.net.protocol.muddleware.Message;
import org.studierstube.net.protocol.muddleware.Operation;
import org.studierstube.net.protocol.muddleware.OperationEnum;
import org.studierstube.net.thread.NetworkHandlerClientRunable;

/**
 * Sample application using multi-threaded IO-handler.
 * 
 * @author Michael Kalkusch
 * 
 * @see org.studierstube.muddleware.sample.console.SampleNetworkConsolCallbackObject
 *
 */
public class SampleNetworkConsolThreadedClient  {

	/**
	 * Multithreaded IO-handler
	 */
	NetworkHandlerClientRunable io_handler;
	
	Thread t;
	
	/**
	 * 
	 */
	public SampleNetworkConsolThreadedClient() {
		io_handler = new NetworkHandlerClientRunable();
	}

	public void go() {
		t = new Thread(io_handler);
		t.setName("Server IO");
		t.run();
		
		io_handler.startManager();
		
		IOperation op = new Operation( OperationEnum.OP_ELEMENT_EXISTS );
		op.setXPath( "/StbAM/Users/User/");
		
		IMessage sendMsg = new Message();
		sendMsg.addOperation( op );
				

		
		
//		server.setBTriggerSendInstantly(false);
		
		sendMsg.setId( 5 );
		io_handler.sendMessage(sendMsg);
		
//		IMessage sendMsg2 = new Message();
//		sendMsg2.addOperation( op );
//		sendMsg2.setId( 7 );
//		
//		server.sendMessage(sendMsg2);
//		server.submitAllMessages();
//		
//		server.setBTriggerSendInstantly(true);
		
		SampleNetworkConsolCallbackObject A = new SampleNetworkConsolCallbackObject();
//		TestGetCallback B = new TestGetCallback();
		
		
		IMessage sendMsg3 = new Message();
		IOperation op3a = new Operation( OperationEnum.OP_REGISTER_WATCHDOG );
		//IOperation op3b = new Operation( OperationEnum.OP_GET_ATTRIBUTE );
		op3a.setXPath( "/StbAM/Users/User/");
		op3a.setClientData(42);
		//op3b.setXPath( "/StbAM/Users/User/");
		
		sendMsg3.addOperation( op3a );
		//sendMsg3.addOperation( op3b );
		sendMsg3.setId( 9 );
		io_handler.sendMessage(sendMsg3, A);
		
		
		IMessage sendMsg4 = new Message();
		IOperation op4 = new Operation( OperationEnum.OP_GET_ATTRIBUTE );
		op4.setClientData(42);
		op4.setXPath( "/StbAM/Users/User/");
		sendMsg4.addOperation( op4 );
		sendMsg4.setId( 9 );
		
		while ( true ) {
			io_handler.sendMessage(sendMsg4);
		}
		
	}
	
	public void stop() {
		
		io_handler.stopManager();
		//t.interrupt();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SampleNetworkConsolThreadedClient runner = new SampleNetworkConsolThreadedClient();
		
		runner.go();
		
		//runner.stop();
	}

}
