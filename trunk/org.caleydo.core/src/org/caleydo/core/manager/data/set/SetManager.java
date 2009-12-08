package org.caleydo.core.manager.data.set;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
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

	@Override
	public ISet createSet(final ESetType type) {
		ISet set = new Set();
		set.setSetType(type);

		return set;
	}
}
