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

import org.xml.sax.Attributes;

/**
 * Parser for recursive parsing of external files
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class OpenExternalXmlFileSaxHandler
	extends AXmlParserHandler
	implements IXmlParserHandler {

	public static final String sXML_attribute_target = "target";

	/**
	 * Constructor.
	 */
	public OpenExternalXmlFileSaxHandler() {
		super();

		setXmlActivationTag("read-xml-file");
	}

	public void reset() {

	}

	/**
	 * startElement() for pareser callbacks
	 * 
	 * @param uri
	 *            URI @see org.xml.sax.helpers.DefaultHandler
	 * @param localName
	 *            lacalName @see org.xml.sax.helpers.DefaultHandler
	 * @param qName
	 *            tag to parse for @see org.xml.sax.helpers.DefaultHandler
	 * @param attributes
	 *            attributes bound to qName
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {

		if (qName.equalsIgnoreCase(this.openingTag)) {
			String sTargetFileName = "";

			sTargetFileName = attributes.getValue(sXML_attribute_target);

			if (sTargetFileName == null)
				throw new IllegalArgumentException("no XML-file specified!");

			/**
			 * Recursion...
			 */
			xmlParserManager.parseXmlFileByName(sTargetFileName);

		}
		else {
			xmlParserManager.startElementSearch4Tag(uri, localName, qName, attributes);
		}
	}

	/**
	 * endElement for pareser callbacks
	 * 
	 * @param uri
	 *            URI @see org.xml.sax.helpers.DefaultHandler
	 * @param localName
	 *            lacalName @see org.xml.sax.helpers.DefaultHandler
	 * @param qName
	 *            tag to parse for @see org.xml.sax.helpers.DefaultHandler
	 */
	@Override
	public void endElement(String uri, String localName, String qName) {

		if (qName.equals(openingTag)) {
			xmlParserManager.sectionFinishedByHandler(this);
		}
	}
}
