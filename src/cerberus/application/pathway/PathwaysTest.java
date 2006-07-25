/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 *  creation date: 03-07-2006
 *  
 */
package cerberus.application.pathway;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cerberus.xml.parser.kgml.KgmlSaxHandler;
import cerberus.view.gui.swing.jgraph.PathwayGraphBuilder;


public class PathwaysTest {
	public static void main(String[] args) throws Exception 
	{
	  	KgmlSaxHandler kgmlParser = new KgmlSaxHandler();
	  	
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try 
        {
            // Parse the input
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse( 
            		new File("data/XML/pathways/map00271.xml"), kgmlParser);

        } catch (Throwable t) {
            t.printStackTrace();
        }

	  	PathwayGraphBuilder pathwayGraphBuilder = new PathwayGraphBuilder();
	  	pathwayGraphBuilder.setUpPathwayGraph();
        pathwayGraphBuilder.showPathwayGraph();
        //System.exit(0);
	}
}
