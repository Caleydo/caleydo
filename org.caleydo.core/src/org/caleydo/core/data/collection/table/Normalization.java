package org.caleydo.core.data.collection.table;

import java.util.HashMap;
import org.caleydo.core.data.collection.dimension.AColumn;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.dimension.NumericalColumn;

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

	void normalizeUsingFoldChange() {
		for (AColumn dimension : table.hashColumns.values()) {
			if (!dimension.containsDataRepresentation(DataRepresentation.FOLD_CHANGE_RAW))
				calculateFoldChange();
			break;
		}

		for (AColumn dimension : table.hashColumns.values()) {
			if (dimension instanceof NumericalColumn) {
				NumericalColumn nDimension = (NumericalColumn) dimension;
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
			new HashMap<Integer, float[]>(table.hashColumns.size());

		for (Integer dimensionKey : table.hashColumns.keySet()) {
			foldChangePerDimension.put(dimensionKey, new float[metaData.depth()]);
		}

		for (int contentCount = 0; contentCount < metaData.depth(); contentCount++) {
			float minValue = Float.MAX_VALUE;
			// find out which is the smalles raw value
			for (Integer dimensionKey : table.hashColumns.keySet()) {
				NumericalColumn nDimension = (NumericalColumn) table.hashColumns.get(dimensionKey);
				float rawValue = nDimension.getFloat(DataRepresentation.RAW, contentCount);
				if (rawValue < minValue)
					minValue = rawValue;
			}
			// set the fold changes
			for (Integer dimensionKey : table.hashColumns.keySet()) {
				NumericalColumn nDimension = (NumericalColumn) table.hashColumns.get(dimensionKey);
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
		for (Integer dimensionKey : table.hashColumns.keySet()) {
			NumericalColumn nDimension = (NumericalColumn) table.hashColumns.get(dimensionKey);
			if (!nDimension.containsDataRepresentation(DataRepresentation.FOLD_CHANGE_RAW))
				nDimension.setNewRepresentation(DataRepresentation.FOLD_CHANGE_RAW,
					foldChangePerDimension.get(dimensionKey));
			else
				throw new UnsupportedOperationException("Tried to normalize globally on a set wich"
					+ "contains nominal dimensions, currently not supported!");
		}
	}
}
