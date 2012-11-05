/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.io.parser.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;

/**
 * Interface for all XML Handler's registered to the org.caleydo.core.manager.XmlParserManager
 * 
 * @see org.caleydo.core.io.parser.xml.XmlParserManager
 * @author Michael Kalkusch
 */
public interface IXmlParserHandler
	extends ContentHandler, EntityResolver {
	
	/**
	 * Initialization of handler. Called once by Manager before using the handler.
	 * 
	 * @see org.caleydo.core.io.parser.xml.XmlParserManager#registerAndInitSaxHandler(IXmlParserHandler)
	 */
	public void initHandler();

	/**
	 * Cleanup called by Manager after Handler is not used any more.
	 */
	public void destroyHandler();

	/**
	 * Get the XmlActivationTag, which makes this Handler the current XMLHandler, that receives all events
	 * from the org.caleydo.core.manager.XmlParserManager. XmlActivationTag is set via the Constructor.
	 * 
	 * @return tag that enables this Handler inside the org.caleydo.core.manager.XmlParserManager
	 */
	public String getXmlActivationTag();
}
