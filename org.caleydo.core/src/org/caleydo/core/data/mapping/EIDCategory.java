package org.caleydo.core.data.mapping;

import org.caleydo.core.manager.datadomain.EDataDomain;

/**
 * <p>
 * General categorization of the different ID Types. When two IDs are in the same category a mapping between
 * them should be (at least theoretically) possible. An exception is other, where no assumption about
 * equivalency is valid.
 * </p>
 * <p>
 * Every ID type in {@link EIDType} has to specify its category
 * </p>
 * 
 * @author Alexander Lex
 */
public enum EIDCategory {
	GENE("Genes", EDataDomain.GENETIC_DATA),
	EXPERIMENT("Experiments", EDataDomain.CLINICAL_DATA),
	PATHWAY("Pathways", EDataDomain.PATHWAY_DATA),
	OTHER("Unspecified", EDataDomain.UNSPECIFIED);

	private String name;
	private EDataDomain dataDomain;

	private EIDCategory(String name, EDataDomain dataDomain) {
		this.name = name;
		this.dataDomain = dataDomain;
	}

	public String getName() {
		return name;
	}

	public EDataDomain getDataDomain() {
		return dataDomain;
	}
}
