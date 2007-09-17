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

/**
 * Extend NetworkHandlerClientManager to run in a thread.
 * 
 * @see org.studierstube.muddleware.sample.console.SampleNetworkConsolThreadedClient
 * 
 * @author Micahel Kalkusch
 *
 */
public class NetworkHandlerClientRunable 
extends NetworkHandlerClientManager 
implements Runnable {

	/**
	 * 
	 */
	public NetworkHandlerClientRunable() {
		super();
	}

	/**
	 * Start thread and INetworkMessageClientManager
	 * @see  org.studierstube.net.thread.INetworkHanlderClientManager#startManager()
	 */
	public void run() {
		super.startManager();
	}

}
