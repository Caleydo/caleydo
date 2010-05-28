package org.caleydo.core.manager.data;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IManager;

/**
 * Manages all ISets.
 * 
 * @author Alexander Lex
 */
public interface ISetManager
	extends IManager<ISet> {

	public ISet createSet();

}
