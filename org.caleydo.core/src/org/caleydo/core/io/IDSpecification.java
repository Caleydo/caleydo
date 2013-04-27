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
/**
 *
 */
package org.caleydo.core.io;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IDTypeInitializer;

/**
 * <p>
 * This class specifies an ID category and an ID type used for resolving
 * relationships in Caleydo. These IDs are typically specified in the headers of
 * the data matrices files.
 * </p>
 * <p>
 * Multi-dataset relationships and mappings are based on <b>definied
 * relationships between id types</b> (see {@link #idType} and {@link IDType}).
 * All ID types that can be <b>resolved to each other belong to the same id
 * category</b> (see {@link IDSpecification#idCategory} and {@link IDCategory}
 * ).That means, if two datasets containing shared IDs from two different files
 * are loaded, they can be mapped if the respective type is identical.
 * Alternatively, they can also be mapped if they do not have the same ID type
 * but the same ID category. This, however, requires that a mapping between the
 * ID types of the category was specified. An example for a mapping based on ID
 * categories are the genetic ID types defined in caleydo. As all genetic ID
 * types share the same ID category and a mapping between the ID types is
 * available, all genetic ID types can be mapped to each other.
 * </p>
 * <p>
 * Caleydo provides special types for gene identifiers using the ID category
 * "GENE". That means that if the rows or columns of the data matrix contain
 * gene identifiers, this needs to be specified using the
 * {@link #isColumnDataTypeGene} resp. the {@link #isRowTypeGene} members.
 * Additionally, the string for {@link #idType} can not be arbitrarily chosen,
 * but must be one of those listed below. Also, the ID category is automatically
 * considered to be "GENE" if this flag is true.
 * </p>
 * <p>
 * Caleydo uses the DAVID Bioinformatics Resources (see
 * http://david.abcc.ncifcrf.gov/) for ID Mapping. The supported ID Strings for
 * the respective types are the following:
 * </p>
 * <ul>
 * <li><code>DAVID</code></li>
 * <li><code>GENE_NAME</code></li>
 * <li><code>GENE_SYMBOL</code></li>
 * <li><code>ENSEMBL_GENE_ID</code></li>
 * <li><code>ENTREZ_GENE_ID</code></li>
 * <li><code>REFSEQ_MRNA</code></li>
 * </ul>
 *
 * <p>
 * This IDSpecification also provides the ability to transform ID Types using
 * regular expressions. For details see the parent {@link IDTypeParsingRules}
 * </p>
 * <p>
 * The defaults are initializes in {@link IDTypeInitializer}
 * </p>
 *
 * @author Alexander Lex
 *
 */
public class IDSpecification {

	/**
	 * <p>
	 * The category of an ID on which resolution of idTypes is based. If this is
	 * set, the {@link #idType} is considered a member of this idCategory.
	 * </p>
	 * <p>
	 * This is optional. If it is not set it is assumed that the id type is not
	 * part of a complex multi-ID type mapping and an idCategory with a name
	 * identical to the ID type is created.
	 * </p>
	 * <p>
	 * The value of this member is ignored if {@link #isIDTypeGene} is true, in
	 * which case the ID category is automatically "GENE".
	 * </p>
	 */
	private String idCategory;

	/**
	 * <p>
	 * The name of the data type of the dimensions. For example, if the
	 * dimensions contain samples this should be <i>sample</i>.
	 * </p>
	 * <p>
	 * Based on this the ID mapping is created. The ID mapping assumes that in
	 * the line above the first record, labels identifying the dimensions are
	 * available.
	 * </p>
	 * <p>
	 * This means, that if you have two datasets that are cross-referenced (i.e.
	 * use the same type of IDs for their entries) the string specified here
	 * <b>must be identical</b> for both datasets. For example, if you have two
	 * datasets with samples as dimensions, you must in both cases use the
	 * string <i>sample</i> so that they can be resolved.
	 * </p>
	 * <p>
	 * The {@link IDType} and the denominations are created based on this.
	 * </p>
	 * <p>
	 * This is mandatory.
	 * </p>
	 */
	private String idType;

	/**
	 * <p>
	 * Flag determining whether the ID Type is for genes. If so, this must be
	 * specified. Defaults to false.
	 * </p>
	 * <p>
	 * If this is true the {@link #idType} needs to be one of the types
	 * explained in the class documentation.
	 * </p>
	 */
	private boolean isIDTypeGene = false;

	/** Advanced parsing rules for IDs. Defaults to null */
	private IDTypeParsingRules idTypeParsingRules = null;

	/**
	 *
	 */
	public IDSpecification() {
	}

	public IDSpecification(String idCategory, String idType) {
		this.idCategory = idCategory;
		this.idType = idType;
	}

	public static IDSpecification createGene() {
		IDSpecification geneIDSpecification = new IDSpecification();
		geneIDSpecification.setIDTypeGene(true);
		geneIDSpecification.setIdType("GENE_SYMBOL");
		return geneIDSpecification;
	}

	/**
	 * @param idCategory
	 *            setter, see {@link #idCategory}
	 */
	public void setIdCategory(String idCategory) {
		this.idCategory = idCategory;
	}

	/**
	 * @return the idCategory, see {@link #idCategory}
	 */
	public String getIdCategory() {
		return idCategory;
	}

	/**
	 * @param idType
	 *            setter, see {@link #idType}
	 */
	public void setIdType(String idType) {
		this.idType = idType;
	}

	/**
	 * @return the idType, see {@link #idType}
	 */
	public String getIdType() {
		return idType;
	}

	/**
	 * Setter for both, {@link #idCategory} and {@link #idType}
	 *
	 * @param idCategory
	 * @param idType
	 */
	public void setIDSpecification(String idCategory, String idType) {
		this.idCategory = idCategory;
		this.idType = idType;
	}

	/**
	 * @param isIDTypeGene
	 *            setter, see {@link #isIDTypeGene}
	 */
	public void setIDTypeGene(boolean isIDTypeGene) {
		this.isIDTypeGene = isIDTypeGene;
		if (isIDTypeGene)
			this.idCategory = "GENE";
	}

	/**
	 * @return the isIDTypeGene, see {@link #isIDTypeGene}
	 */
	public boolean isIDTypeGene() {
		return isIDTypeGene;
	}

	/**
	 * @param idTypeParsingRules
	 *            setter, see {@link #idTypeParsingRules}
	 */
	public void setIdTypeParsingRules(IDTypeParsingRules idTypeParsingRules) {
		this.idTypeParsingRules = idTypeParsingRules;
	}

	/**
	 * @return the idTypeParsingRules, see {@link #idTypeParsingRules}
	 */
	public IDTypeParsingRules getIdTypeParsingRules() {
		return idTypeParsingRules;
	}

}
