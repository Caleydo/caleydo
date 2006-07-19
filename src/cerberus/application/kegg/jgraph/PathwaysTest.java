/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 *  creation date: 03-07-2006
 *  
 */
package cerberus.application.kegg.jgraph;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cerberus.application.kegg.jgraph.KgmlSaxHandler;
import cerberus.application.kegg.jgraph.PathwayGraphBuilder;

public class PathwaysTest {
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
            saxParser.parse( 
            		new File("../data/pathways/map00271.xml"), kgmlParser);

        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        pathwayGraphBuilder.showPathwayGraph();
        //System.exit(0);
	}
}
