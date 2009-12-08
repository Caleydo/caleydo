package org.caleydo.core.manager.usecase;

/**
 * This mode determines whether the user can load and work with gene expression data or otherwise if an not
 * further specified data set is loaded. In the case of the unspecified data set some specialized gene
 * expression features are not available.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public enum EDataDomain {
	GENETIC_DATA("GENETIC_DATA"),
	CLINICAL_DATA("CLINICAL_DATA"),
	PATHWAY_DATA("PATHWAY_DATA"),
	TISSUE_DATA("TISSUE_DATA"),
	UNSPECIFIED("UNSPECIFIED");

	String dataDomain;

	/**
	 * We need this string rep here to compare to the string we get from the xml.
	 * 
	 * @param dataDomain
	 */
	private EDataDomain(String dataDomain) {
		this.dataDomain = dataDomain;
	}
}
