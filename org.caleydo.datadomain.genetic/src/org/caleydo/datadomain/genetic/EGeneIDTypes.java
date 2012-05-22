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
package org.caleydo.datadomain.genetic;

import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;

/**
 * List of names of {@link IDType}s that belong to the gene {@link IDCategory}.
 * These values should not be used as enums, but with the {@link #name()}
 * accessor for its string equivalent.
 * 
 * @author Alexander Lex
 * 
 */
public enum EGeneIDTypes {
	/** The gene ID Category */
	GENE,

	DAVID(EColumnType.INT),
	GENE_SYMBOL(EColumnType.STRING),
	GENE_NAME(EColumnType.STRING),
	BIOCARTA_GENE_ID(EColumnType.STRING),
	REFSEQ_MRNA(EColumnType.STRING),
	ENSEMBL_GENE_ID(EColumnType.STRING),
	ENTREZ_GENE_ID(EColumnType.INT),
	PATHWAY_VERTEX(EColumnType.INT),
	PATHWAY(EColumnType.INT);

	private EColumnType columnType;

	/**
	 * 
	 */
	private EGeneIDTypes() {
	}

	private EGeneIDTypes(EColumnType columnType) {
		this.columnType = columnType;
	}

	/**
	 * @return the columnType, see {@link #columnType}
	 */
	public EColumnType getColumnType() {
		return columnType;
	}
}
