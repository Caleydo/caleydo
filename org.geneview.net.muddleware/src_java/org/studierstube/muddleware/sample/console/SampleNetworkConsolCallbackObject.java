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
import org.studierstube.net.thread.IMessageCallback;

/**
 * Part of sample application. 
 * This object receives the callback from org.studierstube.muddleware.consol.sample.SampleThreadedNetworkClient
 * 
 * @author Michael Kalkusch
 *
 * @see org.studierstube.net.thread.IOperationCallback
 * @see org.studierstube.muddleware.sample.console.SampleNetworkConsolClient
 */
public class SampleNetworkConsolCallbackObject implements IMessageCallback {

	/**
	 * 
	 */
	public SampleNetworkConsolCallbackObject() {
		
	}

	/* (non-Javadoc)
	 * @see org.studierstube.net.thread.IOperationCallback#callbackOperation(org.studierstube.net.protocol.muddleware.IOperation, org.studierstube.net.protocol.muddleware.IOperation)
	 */
	public void callbackOperationOnReceive(IOperation sent, IOperation received) {
		System.out.println("TestGetCallback: trigger Callback SEND=[" + 
				sent.toString() + "]  RECEIVE=[" +
				received.toString() + "]");
	}

	public void callbackMessageOnReceive(IMessage send, IMessage received) {
		System.out.println("TestGetCallback: trigger Callback SEND=[" + 
				send.toString() + "]  RECEIVE=[" +
				received.toString() + "]");
		
	}

	public void callbackMessageOnReceive(IMessage received) {
		System.out.println("TestGetCallback: trigger Callback SEND=[ -- ]  RECEIVE=[" +
				received.toString() + "]");
		
	}

	public void callbackDefaultOnReceive(IMessage received) {
		System.out.println("TestGetCallback: trigger default Callback SEND=[ -- ]  RECEIVE=[" +
				received.toString() + "]");
		
	}

	public void lostConnectionToServer() {
		System.out.println("lost connection to server!");
		
	}

	public void reconnectionToServerEstablished() {
		System.out.println("reconnection to server established!");
		
	}

}
