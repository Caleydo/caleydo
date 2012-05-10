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

import javax.xml.bind.annotation.XmlElement;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;

/**
 * <p>
 * This class specifies an id type used for resolving relationships in Caleydo.
 * They are typically used in headers of matrix files.
 * </p>
 * <p>
 * Multi-dataset relationships and mappings are based on the same definition of
 * the id type. That means, if two datasets containing shared IDs from two
 * different files are loaded, they can be mapped if the respective type is
 * identical.
 * </p>
 * <p>
 * This class also provides the ability to transform ID Types using regular
 * expressions by defining substrings and replacements.
 * </p>
 * <p>
 * Caleydo provides special types for gene identifiers, i.e. if the rows or
 * columns contain gene identifiers, this needs to be specified using the
 * {@link #isColumnDataTypeGene} resp. the {@link #isRowTypeGene} members.
 * Additionally, the string for {@link #columnType} resp. {@link #rowType} can
 * not be arbitrarily chosen. Caleydo uses the DAVID Bioinformatics Resources
 * (see http://david.abcc.ncifcrf.gov/) for ID Mapping. The supported ID Strings
 * for the respective types are the following:
 * </p>
 * <ul>
 * <li><code>DAVID</code></li>
 * <li><code>GENE_NAME</code></li>
 * <li><code>GENE_SYMBOL</code></li>
 * <li><code>ENSEMBL_GENE_ID</code></li>
 * <li><code>ENTREZ_GENE_ID</code></li>
 * <li><code>REFSEQ_MRNA</code></li>
 * <li><code>BIOCARTA_GENE_ID</code></li>
 * </ul>
 * 
 * @author Alexander Lex
 * 
 */
public class IDSpecification {

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
	 * The {@link IDCategory}, {@link IDType} and the denominations are created
	 * based on this.
	 * </p>
	 * <p>
	 * This is optional
	 * </p>
	 * This is only necessary if the {@link #dataDomainType} is not
	 * {@link GeneticDataDomain#DATA_DOMAIN_TYPE}
	 * <p>
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

	/**
	 * <p>
	 * Regular expression specifying a string that is to be replaced with
	 * {@link #replacementString} before storing and resolving the id.
	 * {@link String#replaceAll(String, String)} is used to achieve this.
	 * </p>
	 * <p>
	 * This is executed <b>before</b> a possible operation based on
	 * {@link #subStringExpression}.
	 * </p>
	 * <p>
	 * See http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
	 * for syntax.
	 * </p>
	 * <p>
	 * Defaults to null.
	 * </p>
	 */
	@XmlElement
	private String replacingExpression = null;

	/**
	 * <p>
	 * The string which replaces the string matched by the regular expression in
	 * {@link #replacingExpression}.
	 * </p>
	 * <p>
	 * Defaults to null.
	 * </p>
	 */
	@XmlElement
	private String replacementString = null;

	/**
	 * <p>
	 * Regular expression specifying a string that is used to split the input
	 * string. The {@link String#split(String)} operation is used to execute
	 * this operation. The contract for the resulting String[] is that the first
	 * not-empty string in the array is the desired substring. So if, for
	 * example a leading string "abc-" is to be removed from a string "abc-001",
	 * the expression must match to "abc-" so that the split operation results
	 * in ["","001"]. Using, for example, "\\-" as expression wuld result in
	 * ["abc","001"] and "abc" would be used as the result.
	 * </p>
	 * <p>
	 * Trailing strings can be removed in this manner.
	 * </p>
	 * <p>
	 * This is executed <b>after</b> a possible operation based on
	 * {@link #replacingExpression}.
	 * </p>
	 * <p>
	 * See http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
	 * for syntax.
	 * </p>
	 * <p>
	 * Defaults to null.
	 * </p>
	 */
	private String subStringExpression = null;

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
	 * @param isIDTypeGene
	 *            setter, see {@link #isIDTypeGene}
	 */
	public void setIDTypeGene(boolean isIDTypeGene) {
		this.isIDTypeGene = isIDTypeGene;
	}

	/**
	 * @return the isIDTypeGene, see {@link #isIDTypeGene}
	 */
	public boolean isIDTypeGene() {
		return isIDTypeGene;
	}

	/**
	 * Set a replacement expression. Sets {@link #replacingExpression} and
	 * {@link #replacementString}
	 */
	public void setReplacementExpression(String replacingExpression,
			String replacementString) {
		this.replacingExpression = replacingExpression;
		this.replacementString = replacementString;
	}

	/**
	 * @return the replacingExpression, see {@link #replacingExpression}
	 */
	public String getReplacingExpression() {
		return replacingExpression;
	}

	/**
	 * @return the replacementString, see {@link #replacementString}
	 */
	public String getReplacementString() {
		return replacementString;
	}

	/**
	 * @param subStringExpression
	 *            setter, see {@link #subStringExpression}
	 */
	public void setSubStringExpression(String subStringExpression) {
		this.subStringExpression = subStringExpression;
	}

	/**
	 * @return the subStringExpression, see {@link #subStringExpression}
	 */
	public String getSubStringExpression() {
		return subStringExpression;
	}

}
