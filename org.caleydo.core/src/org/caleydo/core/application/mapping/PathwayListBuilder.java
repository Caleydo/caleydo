package org.caleydo.core.application.mapping;

//import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceLocator;
//import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceSoap;
//import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchRequest;
//import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchResult;
//import gov.nih.nlm.ncbi.www.soap.eutils.efetch.GeneCommentaryType;

import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.rpc.ServiceException;

import org.caleydo.core.manager.IGeneralManager;

import keggapi.Definition;
import keggapi.KEGGLocator;
import keggapi.KEGGPortType;

/**
 * Class generates a file that contains a 
 * list of pathways for homo sapiens.
 * 
 * @author Marc Streit
 *
 */
public class PathwayListBuilder {
	
	protected static String strDelimiter = 
		IGeneralManager.sDelimiter_Parser_DataType;
	
	protected PrintWriter pathwayListWriter;
	
	public PathwayListBuilder() throws IOException {
		
		pathwayListWriter = 
			new PrintWriter("data/mapping/list_of_pathways.map");
	}
	
	protected void fillFile() 
	throws ServiceException, IOException {
		
		//KEGG connection initialization
		KEGGLocator locator = new KEGGLocator();
		KEGGPortType serv = locator.getKEGGPort();
				
		System.out.println("Starting writing pathway list...");
		
		Definition[] arPathways = serv.list_pathways("hsa");

		for (int iPathwayIndex = 0; iPathwayIndex < arPathways.length; iPathwayIndex++)
		{
			System.out.println(arPathways[iPathwayIndex].getEntry_id()
					+ strDelimiter
					+ arPathways[iPathwayIndex].getDefinition());
			
			pathwayListWriter.println(arPathways[iPathwayIndex].getEntry_id()
					+ strDelimiter
					+ arPathways[iPathwayIndex].getDefinition());
			
			pathwayListWriter.flush();	
		}
		
		// Close output streams (only then the data is written).
		pathwayListWriter.close();
		
		System.out.println("...finished.");
	}
	
    public static void main(String[] args) {
    	
    	try {
    		
        	PathwayListBuilder refPathwayListBuilder = 
        		new PathwayListBuilder();
        	
        	refPathwayListBuilder.fillFile();
    		
		} catch (Exception e)
		{
			e.printStackTrace();
		}    	
    }
}
