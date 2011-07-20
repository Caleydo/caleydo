package org.caleydo.core.data.collection.table;

import org.caleydo.core.data.collection.storage.AStorage;
import org.caleydo.core.data.collection.storage.NumericalStorage;

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
		for (AStorage storage : table.hashStorages.values()) {
			if (storage instanceof NumericalStorage) {
				NumericalStorage nStorage = (NumericalStorage) storage;
				nStorage.log10();
			}
			else
				throw new UnsupportedOperationException("Tried to calcualte log values on a set wich has"
					+ "contains nominal storages. This is not possible!");
		}
	}

	/**
	 * Calculates log2 on all storages in the set. Take care that the set contains only numerical storages,
	 * since nominal storages will cause a runtime exception. If you have mixed data you have to call log2 on
	 * all the storages that support it manually.
	 */
	void log2() {

		for (AStorage storage : table.hashStorages.values()) {
			if (storage instanceof NumericalStorage) {
				NumericalStorage nStorage = (NumericalStorage) storage;
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
		for (AStorage storage : table.hashStorages.values()) {
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
		for (AStorage storage : table.hashStorages.values()) {
			if (storage instanceof NumericalStorage) {
				NumericalStorage nStorage = (NumericalStorage) storage;
				nStorage.normalizeWithExternalExtrema(metaData.getMin(), metaData.getMax());
			}
			else
				throw new UnsupportedOperationException("Tried to normalize globally on a set wich"
					+ "contains nominal storages, currently not supported!");
		}
	}

}
