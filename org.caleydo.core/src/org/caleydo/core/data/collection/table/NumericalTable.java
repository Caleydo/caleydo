/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.collection.table;

import javax.naming.OperationNotSupportedException;

import org.caleydo.core.data.collection.column.AColumn;
import org.caleydo.core.data.collection.column.NumericalColumn;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.util.Math.MathHelper;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Extension of {@link Table} to add functionality specific to homogeneous numerical tables, such as a joint
 * normalization, a joint maximum, etc.
 *
 * @author Alexander Lex
 */
public class NumericalTable extends Table {

	public class Transformation extends Table.Transformation {
		public static final String LOG2 = "Log2";
		public static final String LOG10 = "Log10";
	}

	private boolean artificialMin = false;
	private double min = Double.POSITIVE_INFINITY;

	private boolean artificialMax = false;
	private double max = Double.NEGATIVE_INFINITY;

	/** same as {@link DataSetDescription#getDataCenter()} */
	private Double dataCenter = null;

	/**
	 * @param dataDomain
	 */
	public NumericalTable(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
	}

	/**
	 * Calculates a raw value based on min and max from a normalized value.
	 *
	 * @param normalized
	 *            a value between 0 and 1
	 * @return a value between min and max
	 */
	public double getRawForNormalized(String dataTransformation, double normalized) {

		if (dataTransformation.equals(Table.Transformation.NONE)) {
			return getMin() + normalized * (getMax() - getMin());
		} else if (dataTransformation.equals(Transformation.LOG2)) {
			double logMin = MathHelper.log(getMin(), 2);
			double logMax = MathHelper.log(getMax(), 2);
			double spread = logMax - logMin;

			double logRaw = logMin + normalized * spread;
			double raw = Math.pow(2, logRaw);
			return raw;
			// normalized = Math.pow(2, normalized);
		} else if (dataTransformation.equals(Transformation.LOG10)) {
			return Math.pow(10, normalized);
		} else {
			throw new IllegalStateException("Unknown transformation" + dataTransformation);
		}

	}

	/**
	 * Calculates a normalized value based on min and max.
	 *
	 * @param raw
	 *            the raw value
	 * @return a value between 0 and 1
	 */
	public double getNormalizedForRaw(String dataTransformation, double raw) {
		double result;

		if (dataTransformation == org.caleydo.core.data.collection.table.Table.Transformation.NONE) {
			result = raw;
		} else if (dataTransformation == Transformation.LOG2) {
			result = Math.log(raw) / Math.log(2);
		} else if (dataTransformation == Transformation.LOG10) {
			result = Math.log10(raw);
		} else {
			throw new IllegalStateException("Conversion raw to normalized not implemented for data rep"
					+ dataTransformation);
		}

		result = (result - getMin()) / (getMax() - getMin());

		return result;
	}

	/**
	 * @param dataCenter
	 *            setter, see {@link #dataCenter}
	 */
	public void setDataCenter(Double dataCenter) {
		this.dataCenter = dataCenter;
	}

	/**
	 * @return the dataCenter, see {@link #dataCenter}
	 */
	public Double getDataCenter() {
		return dataCenter;
	}

	/**
	 * Get the minimum value in the table.
	 *
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return the absolute minimum value in the set
	 */
	public double getMin() {
		if (min == Double.POSITIVE_INFINITY) {
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
		if (max == Double.NEGATIVE_INFINITY) {
			calculateGlobalExtrema();
		}
		return max;
	}

	/**
	 * Set an artificial minimum for the datatable. All elements smaller than that are clipped to this value in the
	 * representation. This only affects the normalization, does not alter the raw data
	 */
	void setMin(double dMin) {
		artificialMin = true;
		this.min = dMin;
	}

	/**
	 * Set an artificial maximum for the Table. All elements smaller than that are clipped to this value in the
	 * representation. This only affects the normalization, does not alter the raw data
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
	 * @return The absolute minimum value in the set in the specified data representation.
	 */
	// public double getMinAs(String dataRepresentation) {
	// if (min == Double.MAX_VALUE) {
	// calculateGlobalExtrema();
	// }
	//
	// double result = getRawFromExternalDataRep(min);
	//
	// return getDataRepFromRaw(result, dataRepresentation);
	// }

	/**
	 * Gets the maximum value in the set in the specified data representation.
	 *
	 * @param dataRepresentation
	 *            Data representation the maximum value shall be returned in.
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return The absolute maximum value in the set in the specified data representation.
	 */
	// public double getMaxAs(EDataTransformation dataRepresentation) {
	// if (max == Double.MIN_VALUE) {
	// calculateGlobalExtrema();
	// }
	// if (dataRepresentation == dataTransformation)
	// return max;
	// double result = getRawFromExternalDataRep(max);
	//
	// return getDataRepFromRaw(result, dataRepresentation);
	// }

	/**
	 * Converts a raw value to the specified data representation.
	 *
	 * @param raw
	 *            Raw value that shall be converted
	 * @param dataRepresentation
	 *            Data representation the raw value shall be converted to.
	 * @return Value in the specified data representation converted from the raw value.
	 */
	// private double getDataRepFromRaw(double raw, String dataRepresentation) {
	// switch (dataRepresentation) {
	// case org.caleydo.core.data.collection.table.Table.Transformation.NONE:
	// return raw;
	// case Transformation.LOG2:
	// return Math.log(raw) / Math.log(2);
	// case Transformation.LOG10:
	// return Math.log10(raw);
	// default:
	// throw new IllegalStateException("Conversion to data rep not implemented for data rep" + dataRepresentation);
	// }
	// }
	//
	// public double getRawForTransfomed(String dataTransformation, double raw)
	// {
	// getRawForNormalized(dataTransfomation, normalized)
	//
	// }

	/**
	 * Converts the specified value into raw using the current external data representation.
	 *
	 * @param dNumber
	 *            Value in the current external data representation.
	 * @return Raw value converted from the specified value.
	 */
	// private double getRawFromExternalDataRep(double dNumber) {
	// switch (dataTransformation) {
	// case NONE:
	// return dNumber;
	// case LOG2:
	// return Math.pow(2, dNumber);
	// case LOG10:
	// return Math.pow(10, dNumber);
	// default:
	// throw new IllegalStateException("Conversion to raw not implemented for data rep" + dataTransformation);
	// }
	// }

	private void calculateGlobalExtrema() {
		double temp = 1.0;

		if (!(columns.get(0) instanceof NumericalColumn<?, ?>)) {
			throw new UnsupportedOperationException("Minimum or maximum can be calculated only on numeric data");
		}

		for (AColumn<?, ?> column : columns) {
			NumericalColumn<?, ?> nColumn = (NumericalColumn<?, ?>) column;
			temp = new Double(nColumn.getMin());
			if (!artificialMin && temp < min) {
				min = temp;
			}
			temp = nColumn.getMax();
			if (!artificialMax && temp > max) {
				max = temp;
			}
		}
		if (dataCenter == null) {
			// in case we have data with both, positive and negative values
			// we assume it to be centered.
			if (min < 0 && max > 0)
				dataCenter = 0d;
		}
		if (dataCenter != null) {
			if (min > dataCenter || max < dataCenter) {
				dataCenter = null;

				Logger.log(new Status(
						IStatus.WARNING,
						this.toString(),
						"DataCentered was set to "
								+ dataCenter
								+ ", but min ("
								+ min
								+ ") is larger or max ("
								+ max
								+ ") is smaller than data center. This means that the dataCenter was inccoretly set. Setting dataCenter to null!"));
				return;
			}

			double lowerDelta = Math.abs(min - dataCenter);
			double upperDelta = Math.abs(max - dataCenter);
			double maxDelta;
			maxDelta = (lowerDelta > upperDelta) ? lowerDelta : upperDelta;
			max = dataCenter + maxDelta;
			min = dataCenter - maxDelta;

		}

	}

	// /**
	// * Calculates log10 on all dimensions in the table. Take care that the set contains only numerical dimensions,
	// since
	// * nominal dimensions will cause a runtime exception. If you have mixed data you have to call log10 on all the
	// * dimensions that support it manually.
	// */
	// void log10() {
	// for (AColumn<?, ?> dimension : columns) {
	// if (dimension instanceof NumericalColumn) {
	// NumericalColumn<?, ?> nDimension = (NumericalColumn<?, ?>) dimension;
	// nDimension.log10();
	// } else
	// throw new UnsupportedOperationException(
	// "Tried to calcualte log values on a set wich contains nominal dimensions. This is not possible!");
	// }
	// }
	//
	// /**
	// * Calculates log2 on all dimensions in the table. Take care that the set contains only numerical dimensions,
	// since
	// * nominal dimensions will cause a runtime exception. If you have mixed data you have to call log2 on all the
	// * dimensions that support it manually.
	// */
	// void log2() {
	// for (AColumn<?, ?> dimension : columns) {
	// if (dimension instanceof NumericalColumn) {
	// NumericalColumn<?, ?> nDimension = (NumericalColumn<?, ?>) dimension;
	// nDimension.log2();
	// } else
	// throw new UnsupportedOperationException("Tried to calcualte log values on a set wich has"
	// + "contains nominal dimensions. This is not possible!");
	// }
	// }

	/**
	 * Normalize all columns in the tablet, based on values of all dimensions. For a numerical dimension, this would
	 * mean, that global minima and maxima are retrieved instead of local ones (as is done with normalize()) Operates
	 * with the raw data as basis by default, however when a logarithmized representation is in the dimension this is
	 * used. Make sure that all dimensions are logarithmized.
	 */
	@Override
	protected void normalize() {

		for (AColumn<?, ?> column : columns) {
			if (column instanceof NumericalColumn) {
				NumericalColumn<?, ?> nColumn = (NumericalColumn<?, ?>) column;
				nColumn.setExternalMin(getMin());
				nColumn.setExternalMax(getMax());
				nColumn.normalize();

			} else
				throw new IllegalStateException("Non numerical dimension in numerical table: " + column);
		}
	}

	// /**
	// * Switch the representation of the data. When this is called the data in normalized is replaced with data
	// * calculated from the mode specified.
	// *
	// * @param dataTransformation
	// * Determines how the data is visualized. For options see {@link EDataTransformation}.
	// */
	// void setDataTransformation(EDataTransformation dataTransformation) {
	// if (dataTransformation == this.dataTransformation)
	// return;
	//
	// this.dataTransformation = dataTransformation;
	//
	// for (AColumn<?, ?> dimension : columns) {
	// if (dimension instanceof NumericalColumn) {
	// ((NumericalColumn<?, ?>) dimension).setDataTransformation(dataTransformation);
	// }
	// }
	//
	// switch (dataTransformation) {
	// case NONE:
	// break;
	// case LOG10:
	// log10();
	// break;
	// case LOG2:
	// log2();
	// break;
	// }
	// }

	@Override
	public boolean isDataHomogeneous() {
		return true;
	}

}
