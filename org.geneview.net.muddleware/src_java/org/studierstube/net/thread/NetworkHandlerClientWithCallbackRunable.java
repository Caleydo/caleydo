/* ========================================================================
 * Copyright (C) 2004-2005  Vienna University of Technology
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

package org.studierstube.net.thread;

import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.IOperation;
import org.studierstube.net.protocol.muddleware.Message;
import org.studierstube.net.protocol.muddleware.Operation;
import org.studierstube.net.protocol.muddleware.OperationEnum;

/**
 * Extend NetworkHandlerClientWithCallbackManager to run in a thread.
 * 
 * @see org.studierstube.muddleware.sample.console.SampleNetworkConsolThreadedClient
 * 
 * @author Thomas Psik
 *
 */
public class NetworkHandlerClientWithCallbackRunable extends
		NetworkHandlerClientWithCallbackManager 
		implements Runnable{

	/**
	 * 
	 */
	public NetworkHandlerClientWithCallbackRunable() {
		super();
	}

	/**
	 * Start thread and INetworkMessageClientManager
	 * @see  org.studierstube.net.thread.INetworkHandlerClientManager#startManager()
	 */
	public void run() {
		super.startManager();
	}

	public void registerCallbackChannel( ) {
		
		IMessage sendMsg = new Message(  );
		IOperation op = new Operation(OperationEnum.OP_REQUEST_CLIENTID);
		op.setClientData(72);
		sendMsg.setId( 1 );
		sendMsg.addOperation(op);
		sendMessageNow(outputStreamThread, sendMsg, this);
	}
	
	public void callbackMessageOnReceive(IMessage received) {
		//System.out.println("****************** ++++" + received.getId());
		if ((received.getId() == 1) && (received.getNumOperations() == 1)
				&& (received.getOperation(0).getClientData() == 72)) {
			// callback on OP_REQUEST_CLIENTID
			//System.out.println(">>>>>>>>>> clientID received");
			if (received.getNumOperations() == 1) {
				System.out.println(">>>>>>>>>> clientID received sending OP_REGISTER_CALLBACK"
						+ received.getOperation(0).getXPath());
				setClientID(Integer.parseInt(received.getOperation(0)
						.getXPath()));
				IMessage regCallbackMsg = new Message();
				IOperation op = new Operation(
						OperationEnum.OP_REGISTER_CALLBACK);
				op.addNodeString("" + muddlewareClientID);
				regCallbackMsg.addOperation(op);
				regCallbackMsg.setId(2);
				sendMessageNow(outputStreamCallbackThread, regCallbackMsg, this);
			}
		} else
			super.callbackMessageOnReceive(received);
	}

}
