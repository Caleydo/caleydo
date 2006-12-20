package cerberus.application.mapping;

import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceLocator;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceSoap;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchResult;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.GeneCommentaryType;

import java.io.IOException;
import java.io.PrintStream;

import javax.xml.rpc.ServiceException;

import keggapi.KEGGLocator;
import keggapi.KEGGPortType;

/**
 * Class generated a file that contains a mapping 
 * from GeneIDs to Enzyme Code.
 * 
 * @author Marc Streit
 *
 */
public class MappingBuilder {
	
	protected static String strDelimiter = ";";
	
	protected PrintStream mappingGeneID2EnzymeOutputStream;
	
	protected PrintStream mappingGeneID2AccessionOutputStream;
	
	public MappingBuilder() throws IOException {
		
		mappingGeneID2EnzymeOutputStream = 
			new PrintStream("data/mapping/geneID2EnzymeCode.map");
		
		mappingGeneID2AccessionOutputStream =
			new PrintStream("data/mapping/geneID2AccessionNumber.map");
	}
	
	protected void fillMappingFile() 
	throws ServiceException, IOException {
		
		String strGeneID = "";
		String strAccessionNumber; 
		String[] strArEnzymeQueryResult = null;
		String[] strArHomeSapiensGenes = null;
		int iNumberOfGenes;
		
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
			strGeneID = strArHomeSapiensGenes[iGeneIndex];
			
			System.out.println("Getting enzymes for gene: "+strGeneID);
			strArEnzymeQueryResult = serv.get_enzymes_by_gene(strGeneID);
			
			// Remove the "hsa:" prefix of the geneID before writing it to the file
			strGeneID = strGeneID.substring(4);
			
			if (strArEnzymeQueryResult.length == 0)
			{
				System.out.println("Writing: " +strGeneID + strDelimiter);
				mappingGeneID2EnzymeOutputStream.println(strGeneID + strDelimiter);								
			}
			
			for (int iResultIndex = 0; iResultIndex < strArEnzymeQueryResult.length; 
				iResultIndex++)
			{	
				System.out.println("Write: " +strGeneID 
						+strDelimiter + strArEnzymeQueryResult[iResultIndex]);
				
				mappingGeneID2EnzymeOutputStream.println(strGeneID 
						+strDelimiter + strArEnzymeQueryResult[iResultIndex]);				
			}
			
            // Call NCBI EFetch utility
            parameters.setId(strGeneID);
            entrezEFetchResult = utils.run_eFetch(parameters);
            
            GeneCommentaryType[] geneCommentaries = 
            	entrezEFetchResult.getEntrezgeneSet().getEntrezgene(0).
            		getEntrezgene_comments().getGeneCommentary();
            
            for (int iGeneCommentaryIndex = 0; iGeneCommentaryIndex < geneCommentaries.length;
            	iGeneCommentaryIndex++)
            {
            	if (geneCommentaries[iGeneCommentaryIndex].getGeneCommentary_heading() != null && 
            		geneCommentaries[iGeneCommentaryIndex].getGeneCommentary_heading().equals("NCBI Reference Sequences (RefSeq)"))
            	{      	
                	strAccessionNumber = geneCommentaries[iGeneCommentaryIndex].
                		getGeneCommentary_comment().getGeneCommentary(0).
                			getGeneCommentary_products().getGeneCommentary(0).getGeneCommentary_accession();
                	
                	System.out.println("Writing: " 
                		+strGeneID + strDelimiter + strAccessionNumber);
                	
    				mappingGeneID2AccessionOutputStream.println(strGeneID 
    						+strDelimiter + strAccessionNumber);
              	
                	break;
                }
            }
		}
		
		// Close output streams (only then the data is written).
		mappingGeneID2EnzymeOutputStream.close();
		mappingGeneID2AccessionOutputStream.close();
		
		System.out.println("Finished creating mapping files.");
	}
	
    public static void main(String[] args) 
    throws Exception {
    	
    	MappingBuilder geneID2EnzymeMappingBuilder = 
    		new MappingBuilder();
    	
    	geneID2EnzymeMappingBuilder.fillMappingFile();
    }
}