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
	 * Calculates log10 on all storages in the set. Take care that the set contains only numerical storages,
	 * since nominal storages will cause a runtime exception. If you have mixed data you have to call log10 on
	 * all the storages that support it manually.
	 */
	void log10() {
		for (ADimension storage : table.hashDimensions.values()) {
			if (storage instanceof NumericalDimension) {
				NumericalDimension nStorage = (NumericalDimension) storage;
				nStorage.log10();
			}
			else
				throw new UnsupportedOperationException(
					"Tried to calcualte log values on a set wich contains nominal storages. This is not possible!");
		}
	}

	/**
	 * Calculates log2 on all storages in the set. Take care that the set contains only numerical storages,
	 * since nominal storages will cause a runtime exception. If you have mixed data you have to call log2 on
	 * all the storages that support it manually.
	 */
	void log2() {

		for (ADimension storage : table.hashDimensions.values()) {
			if (storage instanceof NumericalDimension) {
				NumericalDimension nStorage = (NumericalDimension) storage;
				nStorage.log2();
			}
			else
				throw new UnsupportedOperationException("Tried to calcualte log values on a set wich has"
					+ "contains nominal storages. This is not possible!");
		}
	}

	/**
	 * Normalize all storages in the set, based solely on the values within each storage. Operates with the
	 * raw data as basis by default, however when a logarithmized representation is in the storage this is
	 * used.
	 */
	void normalizeLocally() {
		table.isSetHomogeneous = false;
		for (ADimension storage : table.hashDimensions.values()) {
			storage.normalize();
		}
	}

	/**
	 * Normalize all storages in the set, based on values of all storages. For a numerical storage, this would
	 * mean, that global minima and maxima are retrieved instead of local ones (as is done with normalize())
	 * Operates with the raw data as basis by default, however when a logarithmized representation is in the
	 * storage this is used. Make sure that all storages are logarithmized.
	 */
	void normalizeGlobally() {

		table.isSetHomogeneous = true;
		for (ADimension storage : table.hashDimensions.values()) {
			if (storage instanceof NumericalDimension) {
				NumericalDimension nStorage = (NumericalDimension) storage;
				nStorage.normalizeWithExternalExtrema(metaData.getMin(), metaData.getMax());
			}
			else
				throw new UnsupportedOperationException("Tried to normalize globally on a set wich"
					+ "contains nominal storages, currently not supported!");
		}
	}

	void normalizeUsingFoldChange() {
		for (ADimension storage : table.hashDimensions.values()) {
			if (!storage.containsDataRepresentation(DataRepresentation.FOLD_CHANGE_RAW))
				calculateFoldChange();
			break;
		}

		for (ADimension storage : table.hashDimensions.values()) {
			if (storage instanceof NumericalDimension) {
				NumericalDimension nStorage = (NumericalDimension) storage;
				nStorage.normalizeWithExternalExtrema(DataRepresentation.FOLD_CHANGE_RAW,
					DataRepresentation.FOLD_CHANGE_NORMALIZED, 1, 10);
			}
			else
				throw new UnsupportedOperationException("Tried to normalize globally on a set wich"
					+ "contains nominal storages, currently not supported!");
		}

	}

	private void calculateFoldChange() {
		HashMap<Integer, float[]> foldChangePerStorage =
			new HashMap<Integer, float[]>(table.hashDimensions.size());

		for (Integer storageKey : table.hashDimensions.keySet()) {
			foldChangePerStorage.put(storageKey, new float[metaData.depth()]);
		}

		for (int contentCount = 0; contentCount < metaData.depth(); contentCount++) {
			float minValue = Float.MAX_VALUE;
			// find out which is the smalles raw value
			for (Integer storageKey : table.hashDimensions.keySet()) {
				NumericalDimension nStorage = (NumericalDimension) table.hashDimensions.get(storageKey);
				float rawValue = nStorage.getFloat(DataRepresentation.RAW, contentCount);
				if (rawValue < minValue)
					minValue = rawValue;
			}
			// set the fold changes
			for (Integer storageKey : table.hashDimensions.keySet()) {
				NumericalDimension nStorage = (NumericalDimension) table.hashDimensions.get(storageKey);
				float rawValue = nStorage.getFloat(DataRepresentation.RAW, contentCount);
				float[] foldChanges = foldChangePerStorage.get(storageKey);
				if (minValue == 0)
					foldChanges[contentCount] = Float.POSITIVE_INFINITY;
				else {
					foldChanges[contentCount] = rawValue / minValue;
					if (foldChanges[contentCount] < 1)
						System.out.println("problem");
				}
			}
		}

		// set the float[] to the storages
		for (Integer storageKey : table.hashDimensions.keySet()) {
			NumericalDimension nStorage = (NumericalDimension) table.hashDimensions.get(storageKey);
			if (!nStorage.containsDataRepresentation(DataRepresentation.FOLD_CHANGE_RAW))
				nStorage.setNewRepresentation(DataRepresentation.FOLD_CHANGE_RAW,
					foldChangePerStorage.get(storageKey));
			else
				throw new UnsupportedOperationException("Tried to normalize globally on a set wich"
					+ "contains nominal storages, currently not supported!");
		}
	}
}
