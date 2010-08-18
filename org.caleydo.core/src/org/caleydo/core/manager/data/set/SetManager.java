package org.caleydo.core.manager.data.set;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.data.ISetManager;

/**
 * Manage the sets TODO: register and store sets
 * 
 * @author Michael Kalkusch
 * @author Alexander Lex
 */
public class SetManager
	extends AManager<ISet>
	implements ISetManager {

	private static SetManager instance = null;

	private SetManager() {
	}

	public static SetManager getInstance() {
		if (instance == null)
			instance = new SetManager();

		return instance;
	}

	@Override
	public ISet createSet() {
//		ISet set = new Set();
//		return set;
		throw new IllegalStateException("No Longer used");
	}
}
