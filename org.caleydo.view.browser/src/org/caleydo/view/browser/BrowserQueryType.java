package org.caleydo.view.browser;

import java.util.ArrayList;

/**
 * This type is passed to browser to know how to handle the incoming string.
 * Example: For an search on PubMed just an element ID is sent to the browser.
 * Because of the EBrowserQueryType the browser knows that the ID must be
 * attached to the PubMed search string which is stored as member of the type
 * (sQueryStringPrefix).
 * 
 * @author Marc Streit
 */
@SuppressWarnings("serial")
public enum BrowserQueryType {
	PROCESS_STRING_WITHOUT_CHANGE("", "", null), PubMed("PubMed",
			"http://www.ncbi.nlm.nih.gov/sites/entrez?db=pubmed&cmd=search&term=",
			new ArrayList<String>() {
				{
					add("GENE_SYMBOL");
				}
			}), EntrezGene("Entrez",
			"http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&cmd=search&term=",
			new ArrayList<String>() {
				{
					add("ENTREZ_GENE_ID");
					add("GENE_SYMBOL");
					add("REFSEQ_MRNA");
				}
			}), KEGG_HomoSapiens("KEGG (homo sapiens)",
			"http://www.genome.jp/dbget-bin/www_bget?hsa+", new ArrayList<String>() {
				{
					add("ENTREZ_GENE_ID");
					add("GENE_SYMBOL");
				}
			}), KEGG_MusMusculus("KEGG (mus musculus)",
			"http://www.genome.jp/dbget-bin/www_bget?mmu+", new ArrayList<String>() {
				{
					add("ENTREZ_GENE_ID");
					add("GENE_SYMBOL");
				}
			}), GeneCards("GeneCards",
			"http://www.genecards.org/cgi-bin/carddisp.pl?gene=",
			new ArrayList<String>() {
				{
					add("GENE_SYMBOL");
				}
			}), 
//			Ensembl_HomoSapiens(
//			"Ensembl (homo sapiens)",
//			"http://www.ensembl.org/Homo_sapiens/Search/Results?species=Homo_sapiens;idx=;q=",
//			new ArrayList<String>() {
//				{
//					add("GENE_SYMBOL");
//					add("REFSEQ_MRNA");
//				}
//			}), Ensembl_MusMusculus(
//			"Ensembl (mus musculus)",
//			"http://www.ensembl.org/Mus_musculus/Search/Results?species=Mus_musculus;idx=;q=",
//			new ArrayList<String>() {
//				{
//					add("GENE_SYMBOL");
//					add("REFSEQ_MRNA");
//				}
//			}), 
			BioCarta_HomoSapiens("BioCarta (homo sapiens)",
			"http://cgap.nci.nih.gov/Genes/GeneInfo?ORG=Hs&BCID=",
			new ArrayList<String>() {
				{
					add("BIOCARTA_GENE_ID");
				}
			}), BioCarta_MusMusculus("BioCarta (mus musculus)",
			"http://cgap.nci.nih.gov/Genes/GeneInfo?ORG=Mm&BCID=",
			new ArrayList<String>() {
				{
					add("BIOCARTA_GENE_ID");
					add("GENE_SYMBOL");
				}
			});

	private String sTitle;

	private String queryPrefix;

	private ArrayList<String> queryIDTypes;

	/**
	 * Constructor.
	 */
	BrowserQueryType(String sTitle, String sQueryStringPrefix,
			ArrayList<String> queryIDTypes) {
		this.queryPrefix = sQueryStringPrefix;
		this.queryIDTypes = queryIDTypes;
		this.sTitle = sTitle;
	}

	public String getBrowserQueryStringPrefix() {
		return queryPrefix;
	}

	public ArrayList<String> getQueryIDTypes() {
		return queryIDTypes;
	}

	public String getTitle() {
		return sTitle;
	}
}
