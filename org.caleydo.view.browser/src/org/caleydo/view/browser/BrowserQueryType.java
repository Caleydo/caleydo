/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.browser;

import java.util.ArrayList;

/**
 * This type is passed to browser to know how to handle the incoming string. Example: For an search on PubMed just an
 * element ID is sent to the browser. Because of the EBrowserQueryType the browser knows that the ID must be attached to
 * the PubMed search string which is stored as member of the type (sQueryStringPrefix).
 *
 * @author Marc Streit
 */
@SuppressWarnings("serial")
public enum BrowserQueryType {
	PROCESS_STRING_WITHOUT_CHANGE("", "", null), PubMed(
			"PubMed",
			"http://www.ncbi.nlm.nih.gov/sites/entrez?db=pubmed&cmd=search&term=",
			new ArrayList<String>() {
				{
					add("GENE_SYMBOL");
				}
			}), EntrezGene(
			"Entrez",
			"http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&cmd=search&term=",
			new ArrayList<String>() {
				{
					add("ENTREZ_GENE_ID");
					add("GENE_SYMBOL");
					add("REFSEQ_MRNA");
				}
			}), KEGG_HomoSapiens(
			"KEGG (homo sapiens)",
			"http://www.genome.jp/dbget-bin/www_bget?hsa+",
			new ArrayList<String>() {
				{
					add("ENTREZ_GENE_ID");
					add("GENE_SYMBOL");
				}
			}), KEGG_MusMusculus(
			"KEGG (mus musculus)",
			"http://www.genome.jp/dbget-bin/www_bget?mmu+",
			new ArrayList<String>() {
				{
					add("ENTREZ_GENE_ID");
					add("GENE_SYMBOL");
				}
			}), GeneCards("GeneCards", "http://www.genecards.org/cgi-bin/carddisp.pl?gene=", new ArrayList<String>() {
		{
			add("GENE_SYMBOL");
		}
	});
	// Ensembl_HomoSapiens(
	// "Ensembl (homo sapiens)",
	// "http://www.ensembl.org/Homo_sapiens/Search/Results?species=Homo_sapiens;idx=;q=",
	// new ArrayList<String>() {
	// {
	// add("GENE_SYMBOL");
	// add("REFSEQ_MRNA");
	// }
	// }), Ensembl_MusMusculus(
	// "Ensembl (mus musculus)",
	// "http://www.ensembl.org/Mus_musculus/Search/Results?species=Mus_musculus;idx=;q=",
	// new ArrayList<String>() {
	// {
	// add("GENE_SYMBOL");
	// add("REFSEQ_MRNA");
	// }
	// }),

	private String sTitle;

	private String queryPrefix;

	private ArrayList<String> queryIDTypes;

	/**
	 * Constructor.
	 */
	BrowserQueryType(String sTitle, String sQueryStringPrefix, ArrayList<String> queryIDTypes) {
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
