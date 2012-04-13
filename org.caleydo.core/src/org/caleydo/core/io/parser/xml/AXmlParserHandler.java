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

import org.caleydo.core.manager.GeneralManager;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AXmlParserHandler
	extends DefaultHandler
	implements IXmlParserHandler {

	private boolean bDestroyHandlerAfterClosingTag = false;

	protected final GeneralManager generalManager;
	protected final XmlParserManager xmlParserManager;

	protected String sOpeningTag = "";

	/**
	 * Constructor.
	 */
	protected AXmlParserHandler() {
		this.generalManager = GeneralManager.get();
		this.xmlParserManager = generalManager.getXmlParserManager();
	}

	public final void setXmlActivationTag(final String tag) {
		if (tag.length() < 2)
			throw new IllegalStateException("setXmlActivationTag() tag must be at least one char!");

		this.sOpeningTag = tag;
	}

	@Override
	public final String getXmlActivationTag() {

		return sOpeningTag;
	}

	@Override
	public final boolean isHandlerDestoryedAfterClosingTag() {
		if (bDestroyHandlerAfterClosingTag)
			return true;

		return false;
	}

	@Override
	public final void setHandlerDestoryedAfterClosingTag(final boolean setHandlerDestoryedAfterClosingTag) {

		this.bDestroyHandlerAfterClosingTag = setHandlerDestoryedAfterClosingTag;
	}

	/**
	 * Sends init message to logger.
	 * 
	 * @see org.caleydo.core.io.parser.xml.IXmlParserHandler#initHandler()
	 */
	@Override
	public void initHandler() {

		// generalManager.logMsg(
		// this.getClass().getSimpleName() +
		// ": initHandler", LoggerType.VERBOSE_EXTRA );
	}

	/**
	 * Sends init message to logger.
	 * 
	 * @see org.caleydo.core.io.parser.xml.IXmlParserHandler#destroyHandler()
	 */
	@Override
	public void destroyHandler() {

		// generalManager.logMsg(
		// this.getClass().getSimpleName() +
		// ": destroyHandler",
		// LoggerType.VERBOSE );
	}
}
