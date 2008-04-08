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

import org.studierstube.util.LogInterface;
import org.studierstube.util.SystemLogger;

/**
 * Base class handling logger.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AConnectionSocketTester {

	/**
	 * Referecen to logger
	 */
	protected LogInterface refLogInterface;
	
	/**
	 * Constructor
	 * 
	 * @param setLogInterface assing logger; if null a default logger is created
	 */
	protected AConnectionSocketTester(final LogInterface setLogInterface) {
		if ( setLogInterface!= null ) {
			this.refLogInterface = setLogInterface;
		}
		else {
			refLogInterface = new SystemLogger();
		}
	}

	/**
	 * Log messages; forward to internal logger.
	 * 
	 * @see org.studierstube.net.protocol.ErrorMessageHandler#logMsg(java.lang.Object, Stringt)
	 */
	public final void logMsg(Object cl, String msg) {
		refLogInterface.log(cl, msg);		
	}


}