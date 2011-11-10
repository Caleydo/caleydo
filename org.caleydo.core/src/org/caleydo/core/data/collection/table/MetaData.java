package org.caleydo.core.data.collection.table;

import javax.naming.OperationNotSupportedException;

import org.caleydo.core.data.collection.ExternalDataRepresentation;
import org.caleydo.core.data.collection.dimension.AColumn;
import org.caleydo.core.data.collection.dimension.NominalColumn;
import org.caleydo.core.data.collection.dimension.NumericalColumn;

/**
 * This class encapsulates all metadata related operations for DataTables. Examples are size, depth and
 * histograms.
 * 
 * @author Alexander Lex
 */
public class MetaData {

	private DataTable table;
	protected int depth = 0;

	private boolean bArtificialMin = false;
	double min = Double.MAX_VALUE;

	private boolean bArtificialMax = false;
	double max = Double.MIN_VALUE;

	public MetaData(DataTable table) {
		this.table = table;
	}

	/**
	 * Get the number of dimensions in a set
	 * 
	 * @return
	 */
	public int size() {
		return table.hashColumns.size();
	}

	/**
	 * Get the depth of the set, which is the length of the dimensions (i.e. the number of content elements)
	 * 
	 * @return the number of elements in the dimensions contained in the list
	 */
	public int depth() {
		if (depth == 0) {
			for (AColumn dimension : table.hashColumns.values()) {
				if (depth == 0)
					depth = dimension.size();
				else {
					if (depth != dimension.size())
						throw new IllegalArgumentException(
							"All dimensions in a set must be of the same length");
				}

			}
		}
		return depth;
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
	 * Set an artificial minimum for the datatable. All elements smaller than that are clipped to this value
	 * in the representation. This only affects the normalization, does not alter the raw data
	 */
	void setMin(double dMin) {
		bArtificialMin = true;
		this.min = dMin;
	}

	/**
	 * Set an artificial maximum for the datatable. All elements smaller than that are clipped to this value
	 * in the representation. This only affects the normalization, does not alter the raw data
	 */
	void setMax(double dMax) {
		bArtificialMax = true;
		this.max = dMax;
	}

	/**
	 * Gets the minimum value in the set in the specified data representation.
	 * 
	 * @param dataRepresentation
	 *            Data representation the minimum value shall be returned in.
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return The absolute minimum value in the set in the specified data representation.
	 */
	public double getMinAs(ExternalDataRepresentation dataRepresentation) {
		if (min == Double.MAX_VALUE) {
			calculateGlobalExtrema();
		}
		if (dataRepresentation == table.externalDataRep)
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
	 * @return The absolute maximum value in the set in the specified data representation.
	 */
	public double getMaxAs(ExternalDataRepresentation dataRepresentation) {
		if (max == Double.MIN_VALUE) {
			calculateGlobalExtrema();
		}
		if (dataRepresentation == table.externalDataRep)
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
	 * @return Value in the specified data representation converted from the raw value.
	 */
	private double getDataRepFromRaw(double dRaw, ExternalDataRepresentation dataRepresentation) {
		switch (dataRepresentation) {
			case NORMAL:
				return dRaw;
			case LOG2:
				return Math.log(dRaw) / Math.log(2);
			case LOG10:
				return Math.log10(dRaw);
			default:
				throw new IllegalStateException("Conversion to data rep not implemented for data rep"
					+ dataRepresentation);
		}
	}

	/**
	 * Converts the specified value into raw using the current external data representation.
	 * 
	 * @param dNumber
	 *            Value in the current external data representation.
	 * @return Raw value converted from the specified value.
	 */
	private double getRawFromExternalDataRep(double dNumber) {
		switch (table.externalDataRep) {
			case NORMAL:
				return dNumber;
			case LOG2:
				return Math.pow(2, dNumber);
			case LOG10:
				return Math.pow(10, dNumber);
			default:
				throw new IllegalStateException("Conversion to raw not implemented for data rep"
					+ table.externalDataRep);
		}
	}

	private void calculateGlobalExtrema() {
		double dTemp = 1.0;

		if (table.tableType.equals(DataTableDataType.NUMERIC)) {
			for (AColumn dimension : table.hashColumns.values()) {
				NumericalColumn nDimension = (NumericalColumn) dimension;
				dTemp = nDimension.getMin();
				if (!bArtificialMin && dTemp < min) {
					min = dTemp;
				}
				dTemp = nDimension.getMax();
				if (!bArtificialMax && dTemp > max) {
					max = dTemp;
				}
			}
		}
		else if (table.hashColumns.get(0) instanceof NominalColumn<?>)
			throw new UnsupportedOperationException("No minimum or maximum can be calculated "
				+ "on nominal data");
	}

}
