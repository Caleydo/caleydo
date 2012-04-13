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

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Pathway parser test.
 * 
 * @author Marc Streit
 */
public class PathwaysTest
{

	public static void main(String[] args) throws Exception
	{
		PathwayGraphBuilder pathwayGraphBuilder = new PathwayGraphBuilder();
		KgmlSaxHandler kgmlParser = new KgmlSaxHandler(pathwayGraphBuilder);

		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try
		{
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new File("data/XML/pathways/map00271.xml"), kgmlParser);

		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}

		pathwayGraphBuilder.showPathwayGraph();
		// System.exit(0);
	}
}
