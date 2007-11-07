package org.geneview.core.view.swt.browser;

/**
 * This type is passed to browser to know how to handle the incoming string.
 * Example: For an search on PubMed just an element ID
 * is sent to the browser. Because of the EBrowserQueryType the browser
 * knows that the ID must be attached to the PubMed search string 
 * which is stored as member of the type (sQueryStringPrefix).
 * 
 * @author Marc Streit
 *
 */
public enum EBrowserQueryType
{	
	PROCESS_STRING_WITHOUT_CHANGE (""),
	PUBMED_PREFIX ("http://www.ncbi.nlm.nih.gov/sites/entrez?term=");

	private String sQueryStringPrefix;
	
	EBrowserQueryType( String sQueryStringPrefix ) {
		
		this.sQueryStringPrefix = sQueryStringPrefix;
	}
	
	public String getBrowserQueryStringPrefix() {
		
		return sQueryStringPrefix;
	}
}
