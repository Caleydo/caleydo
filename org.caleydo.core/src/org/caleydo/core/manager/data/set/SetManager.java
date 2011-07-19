package org.caleydo.core.manager.data.set;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.manager.AManager;

/**
 * Manage the sets TODO: register and store sets
 * 
 * @author Michael Kalkusch
 * @author Alexander Lex
 */
public class SetManager
	extends AManager<DataTable> {

	private static SetManager instance = null;

	private SetManager() {
	}

	public static SetManager getInstance() {
		if (instance == null)
			instance = new SetManager();

		return instance;
	}

	public DataTable createSet() {
		// DataTable set = new Set();
		// return set;
		throw new IllegalStateException("No Longer used");
	}
}
