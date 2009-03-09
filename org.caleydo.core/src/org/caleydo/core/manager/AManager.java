package org.caleydo.core.manager;

import java.util.Collection;
import java.util.HashMap;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.general.GeneralManager;

/**
 * Base class for manager classes.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class AManager<T extends IUniqueObject>
	implements IManager<T> {
	protected IGeneralManager generalManager;

	protected HashMap<Integer, T> hashItems;

	/**
	 * Constructor.
	 */
	protected AManager() {
		generalManager = GeneralManager.get();

		hashItems = new HashMap<Integer, T>();
	}

	@Override
	public T getItem(int iItemID) {
		if (!hasItem(iItemID)) {
			throw new IllegalArgumentException("Requested item with ID " + iItemID + " does not exist!");
		}

		return hashItems.get(iItemID);
	}

	@Override
	public boolean hasItem(int iItemID) {
		return hashItems.containsKey(iItemID);
	}

	@Override
	public void registerItem(final T item) {
		hashItems.put(item.getID(), item);
	}

	@Override
	public int size() {
		return hashItems.size();
	}

	@Override
	public void unregisterItem(int iItemID) {
		hashItems.remove(iItemID);
	}

	@Override
	public Collection<T> getAllItems() {
		return hashItems.values();
	}

}
