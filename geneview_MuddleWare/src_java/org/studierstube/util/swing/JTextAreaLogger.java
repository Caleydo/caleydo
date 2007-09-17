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

package org.studierstube.util.swing;

import javax.swing.JTextArea;

//import eu.ist.Artelier.Components.util.LogInterface;
import org.studierstube.util.LogInterface;
import org.studierstube.util.StudierstubeException;

/**
 * Swing logger print messages to JTextArea
 * 
 * @author Michael Kalkusch
 *
 */
public class JTextAreaLogger 
extends AbstractJLogger 
implements LogInterface {

	/**
	 * Output text area for debug messages. 
	 * This is set via the constructor or via the setter method.
	 */
	protected JTextArea refJTextArea;
	
	/**
	 * 
	 */
	public JTextAreaLogger( JTextArea setJTextArea ) {
		this.refJTextArea = setJTextArea;
	}

	/**
	 * @return the refJTextArea
	 */
	protected final JTextArea getJTextArea() {
		return refJTextArea;
	}

	/**
	 * @param refJTextArea the refJTextArea to set
	 */
	public final void setJTextArea(JTextArea refJTextArea) {
		this.refJTextArea = refJTextArea;
	}

	/**
	 * @see eu.ist.Artelier.Components.util.LogInterface#logAlways(java.lang.Object, Stringt)
	 */
	public void logAlways(Object cl, String msg) {

		String outputMessage = logAlwaysString(cl, msg);
		
		if ( refJTextArea != null ) {
			refJTextArea.append(outputMessage);
		} else {
			throw new StudierstubeException("JTextArea has not been assigned!");
		}
		
		if ( bEnableSystemOut ) {
			System.out.println(outputMessage);
		}
	}

}
