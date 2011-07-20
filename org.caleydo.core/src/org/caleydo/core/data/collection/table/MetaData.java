package org.caleydo.core.data.collection.table;

import javax.naming.OperationNotSupportedException;

import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.storage.AStorage;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;

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
	private double dMin = Double.MAX_VALUE;

	private boolean bArtificialMax = false;
	private double dMax = Double.MIN_VALUE;

	public MetaData(DataTable table) {
		this.table = table;
	}

	/**
	 * Get the number of storages in a set
	 * 
	 * @return
	 */
	public int size() {
		return table.hashStorages.size();
	}

	/**
	 * Get the depth of the set, which is the length of the storages (i.e. the number of content elements)
	 * 
	 * @return the number of elements in the storages contained in the list
	 */
	public int depth() {
		if (depth == 0) {
			for (AStorage storage : table.hashStorages.values()) {
				if (depth == 0)
					depth = storage.size();
				else {
					if (depth != storage.size())
						throw new IllegalArgumentException("All storages in a set must be of the same length");
				}

			}
		}
		return depth;
	}

	/**
	 * Returns a histogram of the values of all storages in the set (not considering VAs). The number of the
	 * bins is sqrt(numberOfElements). This only works for homogeneous sets, if used on other sets an
	 * exception is thrown.
	 * 
	 * @return the Histogram of the values in the set
	 * @throws UnsupportedOperationException
	 *             when used on non-homogeneous sets
	 */
	public Histogram getHistogram() {
		if (!table.isSetHomogeneous) {
			throw new UnsupportedOperationException(
				"Tried to calcualte a set-wide histogram on a not homogeneous set. This makes no sense. Use storage based histograms instead!");
		}
		Histogram histogram = new Histogram();

		boolean bIsFirstLoop = true;
		for (AStorage storage : table.hashStorages.values()) {
			NumericalStorage nStorage = (NumericalStorage) storage;
			Histogram storageHistogram = nStorage.getHistogram();

			if (bIsFirstLoop) {
				bIsFirstLoop = false;
				for (int iCount = 0; iCount < storageHistogram.size(); iCount++) {
					histogram.add(0);
				}
			}
			int iCount = 0;
			for (Integer histoValue : histogram) {
				histoValue += storageHistogram.get(iCount);
				histogram.set(iCount++, histoValue);
			}
		}

		return histogram;
	}

	/**
	 * Returns a histogram of the values of all storages in the set considering the VA of the default content
	 * data. The number of the bins is sqrt(VA size). This only works for homogeneous sets, if used on other
	 * sets an exception is thrown.
	 * 
	 * @return the Histogram of the values in the set
	 * @throws UnsupportedOperationException
	 *             when used on non-homogeneous sets
	 */
	public Histogram getBaseHistogram() {
		if (!table.isSetHomogeneous) {
			throw new UnsupportedOperationException(
				"Tried to calcualte a set-wide histogram on a not homogeneous set. This makes no sense. Use storage based histograms instead!");
		}
		Histogram histogram = new Histogram();

		boolean bIsFirstLoop = true;
		for (AStorage storage : table.hashStorages.values()) {
			NumericalStorage nStorage = (NumericalStorage) storage;
			Histogram storageHistogram = nStorage.getHistogram(table.defaultContentData.getContentVA());

			if (bIsFirstLoop) {
				bIsFirstLoop = false;
				for (int iCount = 0; iCount < storageHistogram.size(); iCount++) {
					histogram.add(0);
				}
			}
			int iCount = 0;
			for (Integer histoValue : histogram) {
				histoValue += storageHistogram.get(iCount);
				histogram.set(iCount++, histoValue);
			}
		}

		return histogram;
	}

	/**
	 * Returns a histogram of the values of all storages in the set considering the specified VA. The number
	 * of the bins is sqrt(VA size). This only works for homogeneous sets, if used on other sets an exception
	 * is thrown.
	 * 
	 * @return the Histogram of the values in the set
	 * @throws UnsupportedOperationException
	 *             when used on non-homogeneous sets
	 */
	public Histogram getHistogram(ContentVirtualArray contentVA) {
		// FIXME put that back
		// if (!isSetHomogeneous) {
		// throw new UnsupportedOperationException(
		// "Tried to calcualte a set-wide histogram on a not homogeneous set. This makes no sense. Use storage based histograms instead!");
		// }
		Histogram histogram = new Histogram();

		boolean bIsFirstLoop = true;
		for (AStorage storage : table.hashStorages.values()) {
			NumericalStorage nStorage = (NumericalStorage) storage;
			Histogram storageHistogram = nStorage.getHistogram(contentVA);

			if (bIsFirstLoop) {
				bIsFirstLoop = false;
				for (int iCount = 0; iCount < storageHistogram.size(); iCount++) {
					histogram.add(0);
				}
			}
			int iCount = 0;
			for (Integer histoValue : histogram) {
				histoValue += storageHistogram.get(iCount);
				histogram.set(iCount++, histoValue);
			}
		}

		return histogram;
	}

	/**
	 * Get the minimum value in the set.
	 * 
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return the absolute minimum value in the set
	 */
	public double getMin() {
		if (dMin == Double.MAX_VALUE) {
			calculateGlobalExtrema();
		}
		return dMin;
	}

	/**
	 * Get the maximum value in the set.
	 * 
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return the absolute minimum value in the set
	 */
	public double getMax() {
		if (dMax == Double.MIN_VALUE) {
			calculateGlobalExtrema();
		}
		return dMax;
	}

	/**
	 * Set an artificial minimum for the dataset. All elements smaller than that are clipped to this value in
	 * the representation. This only affects the normalization, does not alter the raw data
	 */
	void setMin(double dMin) {
		bArtificialMin = true;
		this.dMin = dMin;
	}

	/**
	 * Set an artificial maximum for the dataset. All elements smaller than that are clipped to this value in
	 * the representation. This only affects the normalization, does not alter the raw data
	 */
	void setMax(double dMax) {
		bArtificialMax = true;
		this.dMax = dMax;
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
	public double getMinAs(EExternalDataRepresentation dataRepresentation) {
		if (dMin == Double.MAX_VALUE) {
			calculateGlobalExtrema();
		}
		if (dataRepresentation == table.externalDataRep)
			return dMin;
		double result = getRawFromExternalDataRep(dMin);

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
	public double getMaxAs(EExternalDataRepresentation dataRepresentation) {
		if (dMax == Double.MIN_VALUE) {
			calculateGlobalExtrema();
		}
		if (dataRepresentation == table.externalDataRep)
			return dMax;
		double result = getRawFromExternalDataRep(dMax);

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
	private double getDataRepFromRaw(double dRaw, EExternalDataRepresentation dataRepresentation) {
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

		if (table.dataTableType.equals(EDataTableDataType.NUMERIC)) {
			for (AStorage storage : table.hashStorages.values()) {
				NumericalStorage nStorage = (NumericalStorage) storage;
				dTemp = nStorage.getMin();
				if (!bArtificialMin && dTemp < dMin) {
					dMin = dTemp;
				}
				dTemp = nStorage.getMax();
				if (!bArtificialMax && dTemp > dMax) {
					dMax = dTemp;
				}
			}
		}
		else if (table.hashStorages.get(0) instanceof NominalStorage<?>)
			throw new UnsupportedOperationException("No minimum or maximum can be calculated "
				+ "on nominal data");
	}

}
