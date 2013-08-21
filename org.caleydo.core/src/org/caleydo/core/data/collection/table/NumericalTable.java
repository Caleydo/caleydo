/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection.table;

import javax.naming.OperationNotSupportedException;

import org.caleydo.core.data.collection.column.AColumn;
import org.caleydo.core.data.collection.column.NumericalColumn;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.color.mapping.ColorMapper;
import org.caleydo.core.util.color.mapping.ColorMarkerPoint;
import org.caleydo.core.util.function.FloatStatistics;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.math.MathHelper;
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

	private final NumericalProperties numericalProperties;

	private FloatStatistics datasetStatistics;

	/**
	 * @param dataDomain
	 */
	public NumericalTable(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
		numericalProperties = dataSetDescription.getDataDescription().getNumericalProperties();
	}

	/**
	 * Calculates a raw value based on min and max from a normalized value.
	 *
	 * @param normalized
	 *            a value between 0 and 1
	 * @return a value between min and max
	 */
	public double getRawForNormalized(String dataTransformation, double normalized) {

		if (dataTransformation.equals(Table.Transformation.LINEAR)) {
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

		if (dataTransformation == org.caleydo.core.data.collection.table.Table.Transformation.LINEAR) {
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

		datasetStatistics = computeTableStats();

		if (numericalProperties != null
				&& NumericalProperties.ZSCORE_ROWS.equals(numericalProperties.getzScoreNormalization())) {
			FloatStatistics.Builder postStats = FloatStatistics.builder();
			for (int rowCount = 0; rowCount < getNrRows(); rowCount++) {
				FloatStatistics stats = computeRowStats(rowCount);
				for (AColumn<?, ?> column : columns) {
					@SuppressWarnings("unchecked")
					NumericalColumn<?, Float> nColumn = (NumericalColumn<?, Float>) column;
					float zScore = ((nColumn.getRaw(rowCount)) - stats.getMean()) / stats.getSd();

					nColumn.setRaw(rowCount, zScore);
					postStats.add(zScore);
				}
			}
			datasetStatistics = postStats.build();
		} else if (numericalProperties != null
				&& NumericalProperties.ZSCORE_COLUMNS.equals(numericalProperties.getzScoreNormalization())) {
			FloatStatistics.Builder postStats = FloatStatistics.builder();
			for (AColumn<?, ?> column : columns) {
				@SuppressWarnings("unchecked")
				NumericalColumn<?, Float> nColumn = (NumericalColumn<?, Float>) column;
				FloatStatistics stats = computeColumnStats(nColumn);

				for (int rowCount = 0; rowCount < getNrRows(); rowCount++) {
					float zScore = ((nColumn.getRaw(rowCount)) - stats.getMean()) / stats.getSd();
					nColumn.setRaw(rowCount, zScore);
					postStats.add(zScore);
				}
			}
			datasetStatistics = postStats.build();
		}

		if (numericalProperties != null && numericalProperties.getClipToStdDevFactor() != null) {
			float nrDevs = numericalProperties.getClipToStdDevFactor();
			setMax(datasetStatistics.getMean() + nrDevs * datasetStatistics.getSd());
			setMin(datasetStatistics.getMean() - nrDevs * datasetStatistics.getSd());
			if (dataCenter == null) {
				// in case we have data with both, positive and negative values
				// we assume it to be centered.
				if (min < 0 && max > 0)
					dataCenter = 0d;
			}
		}

		for (AColumn<?, ?> column : columns) {
			NumericalColumn<?, ?> nColumn = (NumericalColumn<?, ?>) column;
			nColumn.setExternalMin(getMin());
			nColumn.setExternalMax(getMax());
			nColumn.normalize();
			// FIXME - need to re-think this
			nColumn.log2();
			nColumn.log10();

		}

		// setting the color marker points to 3 std devs
		ColorMapper mapper = getColorMapper();
		ColorMarkerPoint point = mapper.getMarkerPoints().get(0);
		float raw = datasetStatistics.getMean() - 3 * datasetStatistics.getSd();
		float normalized = (float) getNormalizedForRaw(defaultDataTransformation, raw);
		if (normalized < 0)
			normalized = 0;
		point.setRightSpread(normalized);

		point = mapper.getMarkerPoints().get(mapper.getMarkerPoints().size() - 1);
		raw = datasetStatistics.getMean() + 3 * datasetStatistics.getSd();
		normalized = (float) getNormalizedForRaw(defaultDataTransformation, raw);
		float distance = 1 - normalized;
		if (distance < 0)
			distance = 0;
		point.setLeftSpread(distance);
		mapper.update();
		// mapper.update();
	}

	/**
	 * @return the datasetStatistics, see {@link #datasetStatistics}
	 */
	public FloatStatistics getDatasetStatistics() {
		return datasetStatistics;
	}


	private FloatStatistics computeColumnStats(NumericalColumn<?, Float> nColumn) {
		FloatStatistics.Builder stats = FloatStatistics.builder();
		for (int rowCount = 0; rowCount < getNrRows(); rowCount++) {
			stats.add(nColumn.getRaw(rowCount));
		}
		return stats.build();
	}

	private FloatStatistics computeTableStats() {
		FloatStatistics.Builder stats = FloatStatistics.builder();
		for (AColumn<?, ?> column : columns) {
			NumericalColumn<?, ?> nColumn = (NumericalColumn<?, ?>) column;
			for (int rowCount = 0; rowCount < getNrRows(); rowCount++) {
				stats.add((Float) nColumn.getRaw(rowCount));
			}
		}
		return stats.build();
	}

	private FloatStatistics computeRowStats(int row) {
		FloatStatistics.Builder stats = FloatStatistics.builder();
		for (AColumn<?, ?> column : columns) {
			NumericalColumn<?, ?> nColumn = (NumericalColumn<?, ?>) column;
			stats.add((Float) nColumn.getRaw(row));
		}
		return stats.build();
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
