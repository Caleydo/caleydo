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
package org.caleydo.core.data.collection.table;

import org.caleydo.core.data.collection.column.AColumn;
import org.caleydo.core.data.collection.column.NumericalColumn;

/**
 * This class handles everything related to the normalization of DataTables
 * 
 * @author Alexander Lex
 */
public class Normalization {

	private DataTable table;
	private MetaData metaData;

	public Normalization(DataTable table) {
		this.table = table;
		this.metaData = table.getMetaData();
	}

	/**
	 * Calculates log10 on all dimensions in the table. Take care that the set contains only numerical
	 * dimensions, since nominal dimensions will cause a runtime exception. If you have mixed data you have to
	 * call log10 on all the dimensions that support it manually.
	 */
	void log10() {
		for (AColumn dimension : table.hashColumns.values()) {
			if (dimension instanceof NumericalColumn) {
				NumericalColumn nDimension = (NumericalColumn) dimension;
				nDimension.log10();
			}
			else
				throw new UnsupportedOperationException(
					"Tried to calcualte log values on a set wich contains nominal dimensions. This is not possible!");
		}
	}

	/**
	 * Calculates log2 on all dimensions in the table. Take care that the set contains only numerical
	 * dimensions, since nominal dimensions will cause a runtime exception. If you have mixed data you have to
	 * call log2 on all the dimensions that support it manually.
	 */
	void log2() {

		for (AColumn dimension : table.hashColumns.values()) {
			if (dimension instanceof NumericalColumn) {
				NumericalColumn nDimension = (NumericalColumn) dimension;
				nDimension.log2();
			}
			else
				throw new UnsupportedOperationException("Tried to calcualte log values on a set wich has"
					+ "contains nominal dimensions. This is not possible!");
		}
	}

	/**
	 * Normalize all dimensions in the set, based solely on the values within each dimension. Operates with
	 * the raw data as basis by default, however when a logarithmized representation is in the dimension this
	 * is used.
	 */
	void normalizeLocally() {
		table.isTableHomogeneous = false;
		for (AColumn dimension : table.hashColumns.values()) {
			dimension.normalize();
		}
	}

	/**
	 * Normalize all dimensions in the set, based on values of all dimensions. For a numerical dimension, this
	 * would mean, that global minima and maxima are retrieved instead of local ones (as is done with
	 * normalize()) Operates with the raw data as basis by default, however when a logarithmized
	 * representation is in the dimension this is used. Make sure that all dimensions are logarithmized.
	 */
	void normalizeGlobally() {

		table.isTableHomogeneous = true;
		for (AColumn dimension : table.hashColumns.values()) {
			if (dimension instanceof NumericalColumn) {
				NumericalColumn nDimension = (NumericalColumn) dimension;
				nDimension.normalizeWithExternalExtrema(metaData.getMin(), metaData.getMax());
			}
			else
				throw new UnsupportedOperationException("Tried to normalize globally on a set wich"
					+ "contains nominal dimensions, currently not supported!");
		}
	}


}
