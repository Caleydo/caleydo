package org.caleydo.core.view.swt.browser;

import org.caleydo.core.data.mapping.EMappingType;

/**
 * This type is passed to browser to know how to handle the incoming string.
 * Example: For an search on PubMed just an element ID is sent to the browser.
 * Because of the EBrowserQueryType the browser knows that the ID must be
 * attached to the PubMed search string which is stored as member of the type
 * (sQueryStringPrefix).
 * 
 * @author Marc Streit
 */
public enum EBrowserQueryType
{
	PROCESS_STRING_WITHOUT_CHANGE("", "", null),
	PubMed("PubMed", "http://www.ncbi.nlm.nih.gov/sites/entrez?db=pubmed&cmd=search&term=", EMappingType.DAVID_2_ENTREZ_GENE_ID),
	EntrezGene("Entrez", "http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&cmd=search&term=", EMappingType.DAVID_2_ENTREZ_GENE_ID),
	KEGG("KEGG", "http://www.genome.jp/dbget-bin/www_bget?hsa+", EMappingType.DAVID_2_ENTREZ_GENE_ID),
	BioCarta("BioCarta", "http://cgap.nci.nih.gov/Genes/GeneInfo?ORG=Hs&BCID=", EMappingType.DAVID_2_BIOCARTA_GENE_ID);

	private String sTitle;
	
	private String sQueryStringPrefix;

	private EMappingType mappingType;
	
	/**
	 * Constructor.
	 */
	EBrowserQueryType(String sTitle,
			String sQueryStringPrefix, EMappingType mappingType)
	{
		this.sQueryStringPrefix = sQueryStringPrefix;
		this.mappingType = mappingType;
		this.sTitle = sTitle;
	}

	public String getBrowserQueryStringPrefix()
	{
		return sQueryStringPrefix;
	}
	
	public EMappingType getMappingType()
	{
		return mappingType;
	}
	
	public String getTitle()
	{
		return sTitle;
	}
}
