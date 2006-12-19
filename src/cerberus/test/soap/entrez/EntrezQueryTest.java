package cerberus.test.soap.entrez;

import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceLocator;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceSoap;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.efetch.EFetchResult;
import gov.nih.nlm.ncbi.www.soap.eutils.esummary.ESummaryResult;
import gov.nih.nlm.ncbi.www.soap.eutils.esummary.ESummaryRequest;

public class EntrezQueryTest {
    public static void main(String[] args) throws Exception 
    {
        // fetch article from pubmed and displays its abstract
        try
        {
        	System.out.println("Start Entrez Query Process...");
        	
            EUtilsServiceLocator service = new EUtilsServiceLocator();
            EUtilsServiceSoap utils = service.geteUtilsServiceSoap();
            ESummaryRequest parameters = new ESummaryRequest();
            parameters.setDb("gene");
            parameters.setId("65250");
            ESummaryResult res = utils.run_eSummary(parameters);
            
            // results output
            for(int i=0; i<res.getDocSum().length; i++)
            {
                System.out.println("ID: "+res.getDocSum()[i].getId());
                for(int k=0; k<res.getDocSum()[i].getItem().length; k++)
                {
                	System.out.print(res.getDocSum()[i].getItem()[k].getName() +": ");
                    System.out.println(res.getDocSum()[i].getItem()[k].get_any()[0].getValue());
                }
            }
            
//            // call NCBI EFetch utility
//            EFetchRequest parameters = new EFetchRequest();
//            parameters.setDb("nucleotid");
//            parameters.setId("65250");
////            parameters.setRettype("xml");
//            EFetchResult res = utils.run_eFetch(parameters);
//            // results output
//            for(int i=0; i<res.getGBSet().getGBSeq().length; i++)
//            {
//            	System.out.println("BLA: " +res.getGBSet().getGBSeq()[i].getGBSeq_accessionVersion());
////                System.out.println("ID: "+res.getPubmedArticleSet().getPubmedArticle()[i].getMedlineCitation().getPMID());
////                System.out.println("Abstract: "+res.getPubmedArticleSet().getPubmedArticle()[i].getMedlineCitation().getArticle().get_abstract().getAbstractText());
////                System.out.println("--------------------------\n");
//            }
        }
        catch(Exception e) { System.out.println(e.toString()); }
    }
}