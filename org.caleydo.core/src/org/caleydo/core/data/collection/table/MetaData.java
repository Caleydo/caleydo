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

import javax.naming.OperationNotSupportedException;

import org.caleydo.core.data.collection.EDataTransformation;
import org.caleydo.core.data.collection.dimension.AColumn;
import org.caleydo.core.data.collection.dimension.NominalColumn;
import org.caleydo.core.data.collection.dimension.NumericalColumn;
import org.caleydo.core.io.DataSetDescription;

/**
 * This class encapsulates all metadata related operations for DataTables.
 * Examples are size, depth and histograms.
 * 
 * @author Alexander Lex
 */
public class MetaData {

	private DataTable table;
	protected int nrColumns = 0;
	protected int depth = 0;

	private boolean artificialMin = false;
	double min = Double.MAX_VALUE;

	private boolean artificialMax = false;
	double max = Double.MIN_VALUE;

	/** same as {@link DataSetDescription#isDataCenteredAtZero()} */
	private boolean isDataCenteredAtZero = false;

	public MetaData(DataTable table) {
		this.table = table;
	}

	/**
	 * Get the number of dimensions in a set
	 * 
	 * @return
	 */
	public int size() {
		if (table.isColumnDimension)
			return getNrColumns();
		else
			return getNrRows();

	}

	/**
	 * Get the depth of the set, which is the number of records, the length of
	 * the dimensions
	 * 
	 * @return the number of elements in the dimensions contained in the list
	 */
	public int depth() {
		if (table.isColumnDimension)
			return getNrRows();
		else
			return getNrColumns();
	}

	/** Get the number of columns in the table */
	int getNrColumns() {
		return table.hashColumns.size();
	}

	/** Get the number of rows in the table */
	int getNrRows() {
		return table.hashColumns.values().iterator().next().size();
	}

	/**
	 * @param isDataCenteredAtZero
	 *            setter, see {@link #isDataCenteredAtZero}
	 */
	public void setDataCenteredAtZero(boolean isDataCenteredAtZero) {
		this.isDataCenteredAtZero = isDataCenteredAtZero;
	}

	/**
	 * Get the minimum value in the table.
	 * 
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return the absolute minimum value in the set
	 */
	public double getMin() {
		if (min == Double.MAX_VALUE) {
			calculateGlobalExtrema();
		}
		return min;
	}

	/**
	 * Get the maximum value in the table.
	 * 
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return the absolute minimum value in the set
	 */
	public double getMax() {
		if (max == Double.MIN_VALUE) {
			calculateGlobalExtrema();
		}
		return max;
	}

	/**
	 * Set an artificial minimum for the datatable. All elements smaller than
	 * that are clipped to this value in the representation. This only affects
	 * the normalization, does not alter the raw data
	 */
	void setMin(double dMin) {
		artificialMin = true;
		this.min = dMin;
	}

	/**
	 * Set an artificial maximum for the DataTable. All elements smaller than
	 * that are clipped to this value in the representation. This only affects
	 * the normalization, does not alter the raw data
	 */
	void setMax(double dMax) {
		artificialMax = true;
		this.max = dMax;
	}

	/**
	 * Gets the minimum value in the set in the specified data representation.
	 * 
	 * @param dataRepresentation
	 *            Data representation the minimum value shall be returned in.
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return The absolute minimum value in the set in the specified data
	 *         representation.
	 */
	public double getMinAs(EDataTransformation dataRepresentation) {
		if (min == Double.MAX_VALUE) {
			calculateGlobalExtrema();
		}
		if (dataRepresentation == table.externalDataTrans)
			return min;
		double result = getRawFromExternalDataRep(min);

		return getDataRepFromRaw(result, dataRepresentation);
	}

	/**
	 * Gets the maximum value in the set in the specified data representation.
	 * 
	 * @param dataRepresentation
	 *            Data representation the maximum value shall be returned in.
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return The absolute maximum value in the set in the specified data
	 *         representation.
	 */
	public double getMaxAs(EDataTransformation dataRepresentation) {
		if (max == Double.MIN_VALUE) {
			calculateGlobalExtrema();
		}
		if (dataRepresentation == table.externalDataTrans)
			return max;
		double result = getRawFromExternalDataRep(max);

		return getDataRepFromRaw(result, dataRepresentation);
	}

	/**
	 * Converts a raw value to the specified data representation.
	 * 
	 * @param dRaw
	 *            Raw value that shall be converted
	 * @param dataRepresentation
	 *            Data representation the raw value shall be converted to.
	 * @return Value in the specified data representation converted from the raw
	 *         value.
	 */
	private double getDataRepFromRaw(double dRaw, EDataTransformation dataRepresentation) {
		switch (dataRepresentation) {
		case NONE:
			return dRaw;
		case LOG2:
			return Math.log(dRaw) / Math.log(2);
		case LOG10:
			return Math.log10(dRaw);
		default:
			throw new IllegalStateException(
					"Conversion to data rep not implemented for data rep"
							+ dataRepresentation);
		}
	}

	/**
	 * Converts the specified value into raw using the current external data
	 * representation.
	 * 
	 * @param dNumber
	 *            Value in the current external data representation.
	 * @return Raw value converted from the specified value.
	 */
	private double getRawFromExternalDataRep(double dNumber) {
		switch (table.externalDataTrans) {
		case NONE:
			return dNumber;
		case LOG2:
			return Math.pow(2, dNumber);
		case LOG10:
			return Math.pow(10, dNumber);
		default:
			throw new IllegalStateException(
					"Conversion to raw not implemented for data rep"
							+ table.externalDataTrans);
		}
	}

	private void calculateGlobalExtrema() {
		double temp = 1.0;

		if (table.tableType.equals(DataTableDataType.NUMERIC)) {
			for (AColumn column : table.hashColumns.values()) {
				NumericalColumn nColumn = (NumericalColumn) column;
				temp = nColumn.getMin();
				if (!artificialMin && temp < min) {
					min = temp;
				}
				temp = nColumn.getMax();
				if (!artificialMax && temp > max) {
					max = temp;
				}
			}
			if (isDataCenteredAtZero) {
				if (min > 0 || max < 0)
					return;
//					throw new IllegalStateException(
//							"Flag isDataCenteredAtZero was set, but min is larger than 0: "
//									+ min + " or max is smaller than 0: " + max);
				double absMin = Math.abs(min);
				if (absMin > max) {
					max = absMin;
				} else if (absMin < max) {
					min = max * -1;
				}

			}

		} else if (table.hashColumns.get(0) instanceof NominalColumn<?>) {
			throw new UnsupportedOperationException(
					"No minimum or maximum can be calculated " + "on nominal data");
		}
	}

}
