package org.caleydo.core.data.collection.table;

import java.util.HashMap;

import org.caleydo.core.data.collection.dimension.ADimension;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.dimension.NumericalDimension;

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
	 * Calculates log10 on all dimensions in the table. Take care that the set contains only numerical dimensions,
	 * since nominal dimensions will cause a runtime exception. If you have mixed data you have to call log10 on
	 * all the dimensions that support it manually.
	 */
	void log10() {
		for (ADimension dimension : table.hashDimensions.values()) {
			if (dimension instanceof NumericalDimension) {
				NumericalDimension nDimension = (NumericalDimension) dimension;
				nDimension.log10();
			}
			else
				throw new UnsupportedOperationException(
					"Tried to calcualte log values on a set wich contains nominal dimensions. This is not possible!");
		}
	}

	/**
	 * Calculates log2 on all dimensions in the table. Take care that the set contains only numerical dimensions,
	 * since nominal dimensions will cause a runtime exception. If you have mixed data you have to call log2 on
	 * all the dimensions that support it manually.
	 */
	void log2() {

		for (ADimension dimension : table.hashDimensions.values()) {
			if (dimension instanceof NumericalDimension) {
				NumericalDimension nDimension = (NumericalDimension) dimension;
				nDimension.log2();
			}
			else
				throw new UnsupportedOperationException("Tried to calcualte log values on a set wich has"
					+ "contains nominal dimensions. This is not possible!");
		}
	}

	/**
	 * Normalize all dimensions in the set, based solely on the values within each dimension. Operates with the
	 * raw data as basis by default, however when a logarithmized representation is in the dimension this is
	 * used.
	 */
	void normalizeLocally() {
		table.isSetHomogeneous = false;
		for (ADimension dimension : table.hashDimensions.values()) {
			dimension.normalize();
		}
	}

	/**
	 * Normalize all dimensions in the set, based on values of all dimensions. For a numerical dimension, this would
	 * mean, that global minima and maxima are retrieved instead of local ones (as is done with normalize())
	 * Operates with the raw data as basis by default, however when a logarithmized representation is in the
	 * dimension this is used. Make sure that all dimensions are logarithmized.
	 */
	void normalizeGlobally() {

		table.isSetHomogeneous = true;
		for (ADimension dimension : table.hashDimensions.values()) {
			if (dimension instanceof NumericalDimension) {
				NumericalDimension nDimension = (NumericalDimension) dimension;
				nDimension.normalizeWithExternalExtrema(metaData.getMin(), metaData.getMax());
			}
			else
				throw new UnsupportedOperationException("Tried to normalize globally on a set wich"
					+ "contains nominal dimensions, currently not supported!");
		}
	}

	void normalizeUsingFoldChange() {
		for (ADimension dimension : table.hashDimensions.values()) {
			if (!dimension.containsDataRepresentation(DataRepresentation.FOLD_CHANGE_RAW))
				calculateFoldChange();
			break;
		}

		for (ADimension dimension : table.hashDimensions.values()) {
			if (dimension instanceof NumericalDimension) {
				NumericalDimension nDimension = (NumericalDimension) dimension;
				nDimension.normalizeWithExternalExtrema(DataRepresentation.FOLD_CHANGE_RAW,
					DataRepresentation.FOLD_CHANGE_NORMALIZED, 1, 10);
			}
			else
				throw new UnsupportedOperationException("Tried to normalize globally on a set wich"
					+ "contains nominal dimensions, currently not supported!");
		}

	}

	private void calculateFoldChange() {
		HashMap<Integer, float[]> foldChangePerDimension =
			new HashMap<Integer, float[]>(table.hashDimensions.size());

		for (Integer dimensionKey : table.hashDimensions.keySet()) {
			foldChangePerDimension.put(dimensionKey, new float[metaData.depth()]);
		}

		for (int contentCount = 0; contentCount < metaData.depth(); contentCount++) {
			float minValue = Float.MAX_VALUE;
			// find out which is the smalles raw value
			for (Integer dimensionKey : table.hashDimensions.keySet()) {
				NumericalDimension nDimension = (NumericalDimension) table.hashDimensions.get(dimensionKey);
				float rawValue = nDimension.getFloat(DataRepresentation.RAW, contentCount);
				if (rawValue < minValue)
					minValue = rawValue;
			}
			// set the fold changes
			for (Integer dimensionKey : table.hashDimensions.keySet()) {
				NumericalDimension nDimension = (NumericalDimension) table.hashDimensions.get(dimensionKey);
				float rawValue = nDimension.getFloat(DataRepresentation.RAW, contentCount);
				float[] foldChanges = foldChangePerDimension.get(dimensionKey);
				if (minValue == 0)
					foldChanges[contentCount] = Float.POSITIVE_INFINITY;
				else {
					foldChanges[contentCount] = rawValue / minValue;
					if (foldChanges[contentCount] < 1)
						System.out.println("problem");
				}
			}
		}

		// set the float[] to the dimensions
		for (Integer dimensionKey : table.hashDimensions.keySet()) {
			NumericalDimension nDimension = (NumericalDimension) table.hashDimensions.get(dimensionKey);
			if (!nDimension.containsDataRepresentation(DataRepresentation.FOLD_CHANGE_RAW))
				nDimension.setNewRepresentation(DataRepresentation.FOLD_CHANGE_RAW,
					foldChangePerDimension.get(dimensionKey));
			else
				throw new UnsupportedOperationException("Tried to normalize globally on a set wich"
					+ "contains nominal dimensions, currently not supported!");
		}
	}
}
