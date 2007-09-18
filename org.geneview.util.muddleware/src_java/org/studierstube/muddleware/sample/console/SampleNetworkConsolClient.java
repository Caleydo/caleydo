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

import org.studierstube.net.thread.INetworkHanlderClientManager;
import org.studierstube.net.thread.NetworkHandlerClientManager;

/**
 * Sample application using multi-threaded IO-handler.
 * 
 * @author Michael Kalkusch
 * 
 * @see org.studierstube.muddleware.sample.console.SampleNetworkConsolCallbackObject
 *
 */
public class SampleNetworkConsolClient  {

	/**
	 * Multithreaded IO-handler
	 */
	protected INetworkHanlderClientManager io_handler;
	
	/**
	 * 
	 */
	public SampleNetworkConsolClient() {
		io_handler = new NetworkHandlerClientManager();
	}

	private void watchdog_Example(INetworkHanlderClientManager io_handler) {
		
		/**
		 * Create two objects that are triggerd from the watchdog via the interface 
		 * org.studierstube.net.thread.IOperationCallback#callbackOperation(org.studierstube.net.protocol.muddleware.IOperation, org.studierstube.net.protocol.muddleware.IOperation)
		 */
		SampleNetworkConsolCallbackObject A = new SampleNetworkConsolCallbackObject();
		SampleNetworkConsolCallbackObject B = new SampleNetworkConsolCallbackObject();
		
		/**
		 * Create a new message
		 */
		IMessage sendMsg3 = new Message();
		IOperation op3a = new Operation( OperationEnum.OP_REGISTER_WATCHDOG );
		op3a.setXPath( "/StbAM/Users/User/");
		op3a.setClientData(42);
		
		sendMsg3.addOperation( op3a );
		sendMsg3.setId( 9 );
		
		io_handler.sendMessage(sendMsg3, A);
		
		/**
		 * Create a new message
		 */
		
		IOperation op4a = new Operation( OperationEnum.OP_REGISTER_WATCHDOG );
		op3a.setXPath( "/StbAM/Users/User/");
		op3a.setClientData(71);
		
		IMessage sendMsg4 = createMessageWithOperation(15, op4a);
		
		/**
		 *  Class B will receive the callback.
		 *  
		 *  @see org.studierstube.net.thread.IOperationCallback#callbackOperation(org.studierstube.net.protocol.muddleware.IOperation, org.studierstube.net.protocol.muddleware.IOperation)
		 */
		io_handler.sendMessage(sendMsg4, B);
	
	}
	
	/**
	 * Create a new Message and add op as first Operation to the Message.
	 * 
	 * @param messageId id for new message
	 * @param op operation to be added; must not be null
	 * @return new message with operation op
	 */
	private IMessage createMessageWithOperation(final int messageId, IOperation op) {
		
		IMessage message = new Message();
		message.addOperation( op );
		message.setId(messageId);
		
		return message;
	}
	
	public void go() {
		
		io_handler.startManager();
		
		/** 
		 * Switch io-handler state from instantly sending messages (is default)
		 * to creating a queue of messages and send them via 
		 * submitAllMessages()
		 * 
		 * @see org.studierstube.net.thread.INetworkMessageClientManager#submitAllMessages()
		 */
		io_handler.setTriggerSendInstantly(false);
		
		IOperation op1 = new Operation( OperationEnum.OP_ELEMENT_EXISTS );
		op1.setXPath( "/StbAM/cerberus/Application/");
		IOperation op2 = new Operation( OperationEnum.OP_ELEMENT_EXISTS );
		op2.setXPath( "/StbAM/workspace/Application/");
		
		IMessage sendMsg1 = createMessageWithOperation(5, op1);
		IMessage sendMsg2 = createMessageWithOperation(7, op2);
		
		io_handler.sendMessage(sendMsg1);
		io_handler.sendMessage(sendMsg2);
		io_handler.submitAllMessages();
		
		/**
		 * Reset IO handler to immediate sending
		 */
		io_handler.setTriggerSendInstantly(true);
		
		
		watchdog_Example(io_handler);
		
		/**
		 * 
		 */
		IOperation op4 = new Operation( OperationEnum.OP_GET_ATTRIBUTE );
		op4.setClientData(42);
		op4.setXPath( "/StbAM/cerberus/workspace/Application/");
		
		IMessage sendMsg4 = this.createMessageWithOperation(9, op4);
		
		int iSend_n_Messages = 100;
		
		for ( int i =0; i < iSend_n_Messages; i++ ) {
			io_handler.sendMessage(sendMsg4);
		}
		
	}
	
	/**
	 * Stop IO handler.
	 */
	public void stop() {
		
		io_handler.stopManager();
	}
	
	/**
	 * Run consol sampel client.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		SampleNetworkConsolClient runner = new SampleNetworkConsolClient();
		
		runner.go();
		runner.stop();
	}

}
