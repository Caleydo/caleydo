package org.caleydo.core.view.swt.browser;


public enum EBrowserType
{
	GENERAL(""),
	PUBMED("http://www.ncbi.nlm.nih.gov/sites/entrez?term=");
	
	private String sQueryStringPrefix;
	
	EBrowserType( String sQueryStringPrefix ) {
		
		this.sQueryStringPrefix = sQueryStringPrefix;
	}
	
	public String getBrowserQueryStringPrefix() {
		
		return sQueryStringPrefix;
	}
}
