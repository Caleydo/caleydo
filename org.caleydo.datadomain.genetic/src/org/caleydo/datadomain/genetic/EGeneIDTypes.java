/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.genetic;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;

/**
 * List of names of {@link IDType}s that belong to the gene {@link IDCategory}. These values should not be used as
 * enums, but with the {@link #name()} method to retreive its string representation.
 *
 * @author Alexander Lex
 *
 */
public enum EGeneIDTypes {
	/** The gene ID Category */
	GENE,

	DAVID(EDataType.INTEGER),
	GENE_SYMBOL(EDataType.STRING),
	GENE_NAME(EDataType.STRING),
	REFSEQ_MRNA(EDataType.STRING),
	ENSEMBL_GENE_ID(EDataType.STRING),
	ENTREZ_GENE_ID(EDataType.INTEGER),

	// INTERACTION_ID(EDataType.INTEGER),
	// COMPOUND_ID(EDataType.STRING),
	// FINGERPRINT_ID(EDataType.INTEGER),

	PATHWAY(EDataType.INTEGER),
	PATHWAY_VERTEX(EDataType.INTEGER),
	PATHWAY_VERTEX_REP(EDataType.INTEGER);

	private EDataType dataType;

	/**
	 *
	 */
	private EGeneIDTypes() {
	}

	private EGeneIDTypes(EDataType dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the dataType, see {@link #dataType}
	 */
	public EDataType getDataType() {
		return dataType;
	}
}
