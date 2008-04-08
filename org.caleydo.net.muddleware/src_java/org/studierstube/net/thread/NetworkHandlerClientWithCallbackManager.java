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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;

import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.IOperation;
import org.studierstube.net.protocol.muddleware.OperationEnum;
import org.studierstube.net.thread.stream.InputStreamThread;
import org.studierstube.net.thread.stream.OutputStreamThread;
import org.studierstube.util.StudierstubeException;

/**
 * This class adds another InputStreamThread to the NetworkHandlerClientManager to handle ASYNC_WATCHDOG callbacks.
 * Please note that the <b>same</b> callback function will be called, no matter wether a ASYNC_WATCHDOG or a SYNC_WATCHDOG 
 * message is received.
 * 
 * @author Thomas Psik
 *
 */
public class NetworkHandlerClientWithCallbackManager extends
		NetworkHandlerClientManager {
	private int nServerCallbackPort = 20001;
	private Socket clientCallback = null;
	
	protected InputStreamThread inputStreamCallbackThread;
	protected OutputStreamThread outputStreamCallbackThread;
	protected int muddlewareClientID = -1;
	protected HashMap <Integer, IMessageCallback> hashAsyncWatchdogCallbacks;


	public NetworkHandlerClientWithCallbackManager() {
		super();
		inputStreamCallbackThread  = new InputStreamThread(this);
		outputStreamCallbackThread = new OutputStreamThread(this);
		hashAsyncWatchdogCallbacks = new HashMap<Integer ,IMessageCallback> (); 
		
	}
	
	protected void setClientID(int i) {
		muddlewareClientID = i;
	}
	
	@Override
	public void startManager() {
		super.startManager();
		try {

			InetAddress serverAddress = InetAddress.getByName(nServerName);

			clientCallback = new Socket(serverAddress, nServerCallbackPort);

			logger.log(this, "start callback listener for server ["
					+ nServerName + ":" + nServerCallbackPort + "]");

			inputStreamCallbackThread.startStream(clientCallback);
			outputStreamCallbackThread.startStream(clientCallback);
			
		} catch (UnknownHostException uhe) {

		} catch (IOException ioe) {
		}
	}

	@Override
	public void stopManager() {
		super.stopManager();
		inputStreamCallbackThread.stopStream();
		outputStreamCallbackThread.stopStream();
	}
	
	@Override
	public synchronized void reconnectionToServerEstablished() {
		super.reconnectionToServerEstablished();
		
		if ( ! clientCallback.isConnected()) {
			try {
				clientCallback.close();
			} catch (IOException e) {
				bManagerIsRunning = false;
				throw new StudierstubeException("error while closing socket to muddleware server. " + e.toString());
				//e.printStackTrace();
			}
			
			InetSocketAddress socketAddress = 
				new InetSocketAddress(nServerName, nServerCallbackPort);
			
			try {
				
				clientCallback.connect(socketAddress );
			} catch (IOException e) {
				bManagerIsRunning= false;
				throw new StudierstubeException("error while connecting to muddleware server" + 
						socketAddress.toString() + 
						" " + e.toString());				
			}
		}
		if ( ! outputStreamCallbackThread.isConnected() ) {
			outputStreamCallbackThread.startStream(clientCallback);
		}
	
		if ( ! inputStreamCallbackThread.isConnected() ) {
			inputStreamCallbackThread.startStream(clientCallback);
		}
		
	}
	
	public void setCallbackForWatchdogOp(int clientData, IMessageCallback callbackObject) {
		synchronized (hashAsyncWatchdogCallbacks) {
			Integer cd = Integer.valueOf(clientData);
			if (hashAsyncWatchdogCallbacks.get(cd) != null) {
				logger.log(this, "Warning: replacing watchdog for clientData ["+clientData+"]");
			}
			if (callbackObject == null) {
				hashAsyncWatchdogCallbacks.remove(cd);
			} else {
				hashAsyncWatchdogCallbacks.put(cd, callbackObject);
			}
		}
	}
	
	@Override
	protected IMessageCallback callbackMessageProcedure(IMessage received) {
		synchronized (received) {
			synchronized (hashAsyncWatchdogCallbacks) {
			Iterator <IOperation> iter = received.getOperationIterator();
			while (iter.hasNext()) {
				IOperation op = (IOperation)iter.next();
				if (op.getOperation() == OperationEnum.OP_WATCHDOG)
				{	
					IMessageCallback msgCall =  (IMessageCallback)hashAsyncWatchdogCallbacks.get(Integer.valueOf(op.getClientData()));
					if (msgCall != null) {
						msgCall.callbackMessageOnReceive(received);
					}
				}
			}
			}
		}
		return super.callbackMessageProcedure(received);
	}

	

}
