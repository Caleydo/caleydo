package org.caleydo.core.data.collection;

import org.caleydo.core.manager.usecase.EDataDomain;

/**
 * Defines different types of sets.
 * 
 * @author Michael Kalkusch
 * @author Alexander Lex
 */
// TODO this is obsolete, replace with EDataDomain
public enum ESetType {
	GENE_EXPRESSION_DATA(EDataDomain.GENETIC_DATA),
	CLINICAL_DATA(EDataDomain.CLINICAL_DATA),
	UNSPECIFIED(EDataDomain.UNSPECIFIED);

	private EDataDomain dataDomain;

	private ESetType(EDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public EDataDomain getDataDomain() {
		return dataDomain;
	}
}
