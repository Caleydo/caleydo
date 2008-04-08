/* ========================================================================
 * Copyright (C) 2006-2007  Graz University of Technology
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
 
 package org.studierstube.util;


/**
 * Logger that prints messages to System.out
 * 
 * @author Michael Kalkusch
 */
public class SystemLogger implements LogInterface {
	
	/**
	 * flag to switch between log levels
	 */
	protected boolean bIsVerboseEnabled = true;

	/**
	 * Constructor
	 *
	 */
	public SystemLogger() {
		
	}
	
	/**
	 * Creates a String from the calling object and the message.
	 * 
	 * @see eu.ist.Artelier.Components.util.LogInterface#logAlways(java.lang.Object, Stringt)
	 */
	protected final String logAlwaysString(Object cl, String msg) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(cl.getClass().getSimpleName());
		buffer.append("]");
		buffer.append(msg);
		buffer.append("\n");
		
		return buffer.toString();
	}
	
	/**
	 * @see eu.ist.Artelier.Components.util.LogInterface#getVerbose()
	 */
	public final boolean getVerbose() {
		return bIsVerboseEnabled;
	}

	/**
	 * @see eu.ist.Artelier.Components.util.LogInterface#setVerbose(boolean)
	 */
	public final void setVerbose(boolean flag) {
		this.bIsVerboseEnabled = flag;
	}

	/**
	 * @see eu.ist.Artelier.Components.util.LogInterface#log(java.lang.Object, Stringt)
	 */
	public final void log(Object cl, String msg) {
		if (getVerbose())
			logAlways(cl, msg);
	}
	
	/**
	 * @see eu.ist.Artelier.Components.util.LogInterface#setLogInterface(eu.ist.Artelier.Components.util.LogInterface)
	 */
	public void setLogInterface(LogInterface logInterf) {
		throw new RuntimeException("Can not setLogInterface("+
				logInterf.getClass().getSimpleName()+") on "+
				this.getClass().getSimpleName());
	}

	/**
	 * @see eu.ist.Artelier.Components.util.LogInterface#logAlways(java.lang.Object, Stringt)
	 */
	public void logAlways(Object cl, String msg) {		
		System.out.println(logAlwaysString(cl,msg));
	}
	

}
