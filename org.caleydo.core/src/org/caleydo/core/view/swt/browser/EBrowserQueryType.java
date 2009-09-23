package org.caleydo.core.view.swt.browser;

import org.caleydo.core.data.mapping.EMappingType;

/**
 * This type is passed to browser to know how to handle the incoming string. Example: For an search on PubMed
 * just an element ID is sent to the browser. Because of the EBrowserQueryType the browser knows that the ID
 * must be attached to the PubMed search string which is stored as member of the type (sQueryStringPrefix).
 * 
 * @author Marc Streit
 */
public enum EBrowserQueryType {
	PROCESS_STRING_WITHOUT_CHANGE("", "", null),
	PubMed(
		"PubMed",
		"http://www.ncbi.nlm.nih.gov/sites/entrez?db=pubmed&cmd=search&term=",
		EMappingType.DAVID_2_GENE_SYMBOL),
	EntrezGene(
		"Entrez",
		"http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&cmd=search&term=",
		EMappingType.DAVID_2_ENTREZ_GENE_ID),
	KEGG_HomoSapiens(
		"KEGG (homo sapiens)",
		"http://www.genome.jp/dbget-bin/www_bget?hsa+",
		EMappingType.DAVID_2_ENTREZ_GENE_ID),
	KEGG_MusMusculus(
		"KEGG (mus musculus)",
		"http://www.genome.jp/dbget-bin/www_bget?mmu+",
		EMappingType.DAVID_2_ENTREZ_GENE_ID),
	GeneCards(
		"GeneCards",
		"http://www.genecards.org/cgi-bin/carddisp.pl?gene=",
		EMappingType.DAVID_2_GENE_SYMBOL),
	Ensembl_HomoSapiens(
		"Ensembl (homo sapiens)",
		"http://www.ensembl.org/Homo_sapiens/Search/Summary?species=Homo_sapiens;idx=;q=",
		EMappingType.DAVID_2_GENE_SYMBOL),
	Ensembl_MusMusculus(
		"Ensembl (mus musculus)",
		"http://www.ensembl.org/Mus_musculus/Search/Summary?species=Mus_musculus;idx=;q=",
		EMappingType.DAVID_2_GENE_SYMBOL),
	BioCarta_HomoSapiens(
		"BioCarta (homo sapiens)",
		"http://cgap.nci.nih.gov/Genes/GeneInfo?ORG=Hs&BCID=",
		EMappingType.DAVID_2_BIOCARTA_GENE_ID),
	BioCarta_MusMusculus(
		"BioCarta (mus musculus)",
		"http://cgap.nci.nih.gov/Genes/GeneInfo?ORG=Mm&BCID=",
		EMappingType.DAVID_2_BIOCARTA_GENE_ID);

	private String sTitle;

	private String sQueryStringPrefix;

	private EMappingType mappingType;

	/**
	 * Constructor.
	 */
	EBrowserQueryType(String sTitle, String sQueryStringPrefix, EMappingType mappingType) {
		this.sQueryStringPrefix = sQueryStringPrefix;
		this.mappingType = mappingType;
		this.sTitle = sTitle;
	}

	public String getBrowserQueryStringPrefix() {
		return sQueryStringPrefix;
	}

	public EMappingType getMappingType() {
		return mappingType;
	}

	public String getTitle() {
		return sTitle;
	}

	@Override
	public String toString() {

		return getTitle();
	}
}
