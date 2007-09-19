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

package org.studierstube.net.thread;

import org.studierstube.net.protocol.muddleware.IMessage;
//import org.studierstube.net.thread.INetworkHanlderClientManager;
/**
 * 
 * @author Michael Kalkusch
 *
 */
public interface IMessageCallback extends IMuddlewareCallback {

	/**
	 * Receive callback from IO-Thread. 
	 * org.studierstube.net.thread.INetworkHanlderClientManager stores send Message (outstream) with same Id 
	 * as received Message (instream) and returns both via this callback.
	 * 
	 * @param send Message send that created a message from the server with same id
	 * @param received Message triggering the callback
	 * 
	 * @see org.studierstube.net.thread.INetworkHanlderClientManager
	 */
	public void callbackMessageOnReceive( IMessage send, IMessage received);
	
	/**
	 * Callback containing only received message. 
	 * Send message has to be stored by client.
	 * 
	 * @param received
	 */
	public void callbackMessageOnReceive(IMessage received);
	
}
