package org.geneview.core.application.mapping;

import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceLocator;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceSoap;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchResult;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.GeneCommentaryType;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.xml.rpc.ServiceException;

import keggapi.KEGGLocator;
import keggapi.KEGGPortType;

import org.geneview.core.manager.IGeneralManager;

/**
 * Class generates a file that contains a mapping 
 * from GeneIDs to Enzyme Code.
 * 
 * @author Marc Streit
 *
 */
public class MappingBuilder {
	
	protected static String strDelimiter = 
		IGeneralManager.sDelimiter_Parser_DataType;
	
	protected PrintWriter writer_ACCESSION_NUMBER_2_ENZYME_ID;
	
	protected PrintWriter writer_ACCESSION_NUMBER_2_GENE_ID;
	
	protected PrintWriter writer_ENZYME_CODE_2_ENZYME_ID;
	
	protected HashMap<String, Integer> hashMapEnzymeCode2EnzymeID;
	
	protected int iIncrementedEnzymeID = 0;
	
	protected int iCurrentEnzymeID = 0;
	
	public MappingBuilder() throws IOException {
		
		writer_ACCESSION_NUMBER_2_ENZYME_ID = 
			new PrintWriter("data/mapping/accession_number_2_enzyme_id.map");
		
		writer_ACCESSION_NUMBER_2_GENE_ID =
			new PrintWriter("data/mapping/accession_number_2_gene_id.map");
		
		writer_ENZYME_CODE_2_ENZYME_ID =
			new PrintWriter("data/mapping/enzyme_code_2_enzyme_id.map");
		
		hashMapEnzymeCode2EnzymeID = new HashMap<String, Integer>();
	}
	
	protected void fillMappingFile() 
	throws ServiceException, IOException {
		
		String strGeneID = "";
		String strAccessionNumber = ""; 
		String[] strArEnzymeQueryResult = null;
		String[] strArHomeSapiensGenes = null;
		int iNumberOfGenes;
		
		GeneCommentaryType[] tmpGeneCommentaries = null;
		GeneCommentaryType[] geneCommentaries = null;
		GeneCommentaryType[] lastGeneCommentaries = null;
		
		//KEGG connection initialization
		KEGGLocator locator = new KEGGLocator();
		KEGGPortType serv = locator.getKEGGPort();
		
		//Entrez connection initialization
        EUtilsServiceLocator service = new EUtilsServiceLocator();
        EUtilsServiceSoap utils = service.geteUtilsServiceSoap();

        EFetchRequest parameters = new EFetchRequest();
        parameters.setDb("gene");
        parameters.setRettype("xml");
        EFetchResult entrezEFetchResult = null;
		
		iNumberOfGenes = serv.get_number_of_genes_by_organism("hsa");
		
		System.out.println("Number of genes in KEGG for hsa (homo sapiens): " 
				+iNumberOfGenes);
		
		strArHomeSapiensGenes = serv.get_genes_by_organism("hsa", 1, iNumberOfGenes);
		
		for (int iGeneIndex = 0; iGeneIndex < strArHomeSapiensGenes.length; 
			iGeneIndex++)
		{
//			// Refresh connections every 100 genes
//			if (iGeneIndex%100 == 0)
//			{
//				serv = null;
//				utils = null;
//				serv = locator.getKEGGPort();
//				utils = service.geteUtilsServiceSoap();
//			}
			
			strGeneID = strArHomeSapiensGenes[iGeneIndex];
			
			System.out.println("Getting enzymes for gene: "+strGeneID);
			strArEnzymeQueryResult = serv.get_enzymes_by_gene(strGeneID);
			
			// Remove the "hsa:" prefix of the geneID before writing it to the file
			strGeneID = strGeneID.substring(4);
			
            // Call NCBI EFetch utility
            parameters.setId(strGeneID);
            entrezEFetchResult = utils.run_eFetch(parameters);
            
            geneCommentaries = 
            	entrezEFetchResult.getEntrezgeneSet().getEntrezgene(0).
            		getEntrezgene_comments().getGeneCommentary();
            
            for (int iGeneCommentaryIndex = 0; iGeneCommentaryIndex < geneCommentaries.length;
            	iGeneCommentaryIndex++)
            {
            	if (geneCommentaries[iGeneCommentaryIndex].getGeneCommentary_heading() != null && 
            		geneCommentaries[iGeneCommentaryIndex].getGeneCommentary_heading().equals("NCBI Reference Sequences (RefSeq)"))
            	{      	
                	tmpGeneCommentaries = geneCommentaries[iGeneCommentaryIndex].
                		getGeneCommentary_comment().getGeneCommentary();
                	
                    for (int iTmpGeneCommentaryIndex = 0; iTmpGeneCommentaryIndex < tmpGeneCommentaries.length;
                		iTmpGeneCommentaryIndex++)
                    {
                    	if (tmpGeneCommentaries[iTmpGeneCommentaryIndex].getGeneCommentary_heading() != null && 
                    			tmpGeneCommentaries[iTmpGeneCommentaryIndex].getGeneCommentary_heading().
                    				equals("RefSeqs maintained independently of Annotated Genomes"))
                    	{
                    		lastGeneCommentaries = 
                    			tmpGeneCommentaries[iTmpGeneCommentaryIndex].getGeneCommentary_products().getGeneCommentary();
                    	
                            for (int iLastGeneCommentaryIndex = 0; iLastGeneCommentaryIndex < lastGeneCommentaries.length;
                    			iLastGeneCommentaryIndex++)
                            {
                            	if (lastGeneCommentaries[iLastGeneCommentaryIndex].getGeneCommentary_heading() != null && 
                        			lastGeneCommentaries[iLastGeneCommentaryIndex].getGeneCommentary_heading().
                        				equals("mRNA Sequence"))
                            	{
                            		strAccessionNumber = 
                            			lastGeneCommentaries[iLastGeneCommentaryIndex].getGeneCommentary_accession();
                            		
                            		break;
                            	}
                            }
                            
                            break;
                    	}
                	
                    }
                	
	                // Remove "NM_" from accesion number string
	                strAccessionNumber = strAccessionNumber.substring(3);
                    
                    // Writing accession number 2 geneID mapping
                	System.out.println("Writing: " 
                		+strAccessionNumber + strDelimiter + strGeneID);	
                	writer_ACCESSION_NUMBER_2_GENE_ID.println(strAccessionNumber 
    						+strDelimiter + strGeneID);
                	writer_ACCESSION_NUMBER_2_GENE_ID.flush();
    	            
    	            // Writing accession number 2 enzyme code mapping
    				if (strArEnzymeQueryResult.length == 0)
    				{
    					System.out.println("Writing: " +strAccessionNumber + strDelimiter);
    					writer_ACCESSION_NUMBER_2_ENZYME_ID.println(strAccessionNumber + strDelimiter);	
    					writer_ACCESSION_NUMBER_2_ENZYME_ID.flush();
    				}
    				
    				String sTmpEnzymeCode;
    				
    				for (int iResultIndex = 0; iResultIndex < strArEnzymeQueryResult.length; 
    					iResultIndex++)
    				{	
    					sTmpEnzymeCode = strArEnzymeQueryResult[iResultIndex];
    					
    					if (!hashMapEnzymeCode2EnzymeID.containsKey(sTmpEnzymeCode))
    					{
    						iIncrementedEnzymeID++;
    						
    						hashMapEnzymeCode2EnzymeID.put(
    							sTmpEnzymeCode, iIncrementedEnzymeID);
    						
    						writer_ENZYME_CODE_2_ENZYME_ID.println(sTmpEnzymeCode 
    								+strDelimiter + iIncrementedEnzymeID);
    						writer_ENZYME_CODE_2_ENZYME_ID.flush();
    						
    						System.out.println("Writing: " +sTmpEnzymeCode 
    								+strDelimiter + iIncrementedEnzymeID);
    						
    						iCurrentEnzymeID = iIncrementedEnzymeID;
    					}
    					else
    					{
    						iCurrentEnzymeID = 
    							hashMapEnzymeCode2EnzymeID.get(sTmpEnzymeCode);
    					}
    					
    					System.out.println("Writing: " +strAccessionNumber 
    							+strDelimiter + iCurrentEnzymeID);
    					
    					writer_ACCESSION_NUMBER_2_ENZYME_ID.println(strAccessionNumber 
    							+strDelimiter + iCurrentEnzymeID);	
    					
    					writer_ACCESSION_NUMBER_2_ENZYME_ID.flush();
    				}
    	            
                	break;
                }
            }
		}
		
		// Close output streams (only then the data is written).
		writer_ACCESSION_NUMBER_2_ENZYME_ID.close();
		writer_ACCESSION_NUMBER_2_GENE_ID.close();
		
//		System.out.println("Finished creating mapping files.");
	}
	
    public static void main(String[] args) {
    	
    	try {
    		
        	MappingBuilder geneID2EnzymeMappingBuilder = 
        		new MappingBuilder();
        	
        	geneID2EnzymeMappingBuilder.fillMappingFile();
    		
		} catch (Exception e)
		{
			e.printStackTrace();
		}    	
    }
}