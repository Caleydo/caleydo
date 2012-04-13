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
package org.caleydo.testing.applications.gui.swt.jgraph.kegg;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Kgml test SAX parser
 * 
 * @author Marc Streit
 */
public class KgmlSaxHandler
	extends DefaultHandler
{
	private PathwayGraphBuilder pathwayGraphBuilder;

	/**
	 * Constructor.
	 * 
	 * @param pathwayGraphBuilder
	 */
	public KgmlSaxHandler(PathwayGraphBuilder pathwayGraphBuilder)
	{
		this.pathwayGraphBuilder = pathwayGraphBuilder;
	}

	public void startDocument() throws SAXException
	{
		System.out.println("Start parsing the document.");
	}

	@Override
	public void endDocument() throws SAXException
	{
		System.out.println("End parsing the document.");
	}

	@Override
	public void startElement(String namespaceURI, String sSimpleName, String sQualifiedName,
			Attributes attributes) throws SAXException
	{
		String sElementName = sSimpleName;

		if ("".equals(sElementName))
		{
			sElementName = sQualifiedName; // namespaceAware = false
		}

		// System.out.println("Element name: " +sElementName);

		if (sElementName.equals("graphics"))
		{
			if (attributes != null)
			{
				String sName = "";
				int iHeight = 0;
				int iWidth = 0;
				int iXPosition = 0;
				int iYPosition = 0;

				for (int iAttributeIndex = 0; iAttributeIndex < attributes.getLength(); iAttributeIndex++)
				{
					String sAttributeName = attributes.getLocalName(iAttributeIndex);

					if ("".equals(sAttributeName))
					{
						sAttributeName = attributes.getQName(iAttributeIndex);
					}

					if (sAttributeName.equals("name"))
						sName = attributes.getValue(iAttributeIndex);
					else if (sAttributeName.equals("height"))
						iHeight = new Integer(attributes.getValue(iAttributeIndex));
					else if (sAttributeName.equals("width"))
						iWidth = new Integer(attributes.getValue(iAttributeIndex));
					else if (sAttributeName.equals("x"))
						iXPosition = new Integer(attributes.getValue(iAttributeIndex));
					else if (sAttributeName.equals("y"))
						iYPosition = new Integer(attributes.getValue(iAttributeIndex));

					// System.out.println("Attribute name: " +sAttributeName);
					// System.out.println("Attribute value: "
					// +attributes.getValue(iAttributeIndex));
				}

				pathwayGraphBuilder.createCell(sName, iHeight, iWidth, iXPosition, iYPosition);
			}
		}
	}

	@Override
	public void endElement(String namespaceURI, String sSimpleName, String sQualifiedName)
			throws SAXException
	{
		// emit("</"+sName+">");
	}
}
