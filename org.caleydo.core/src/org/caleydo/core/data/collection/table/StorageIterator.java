package org.caleydo.core.data.collection.table;

import java.util.HashMap;
import java.util.ListIterator;

import org.caleydo.core.data.collection.storage.AStorage;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.VAIterator;

/**
 * Implementation of the iterator for sets considering a virtual array.
 * 
 * @author Alexander Lex
 */
public class StorageIterator
	implements ListIterator<AStorage> {
	private VAIterator vaIterator;
	private HashMap<Integer, AStorage> storages;

	/**
	 * Constructor
	 * 
	 * @param set
	 * @param virtualArray
	 */
	public StorageIterator(HashMap<Integer, AStorage> storages, StorageVirtualArray virtualArray) {
		this.vaIterator = virtualArray.iterator();
		this.storages = storages;
	}

	@Override
	public void add(AStorage storage) {
		vaIterator.add(storage.getID());
	}

	@Override
	public boolean hasNext() {
		return vaIterator.hasNext();
	}

	@Override
	public boolean hasPrevious() {
		return vaIterator.hasPrevious();
	}

	@Override
	public AStorage next() {
		return storages.get(vaIterator.next());
	}

	@Override
	public int nextIndex() {
		return vaIterator.nextIndex();
	}

	@Override
	public AStorage previous() {
		return storages.get(vaIterator.previous());
	}

	@Override
	public int previousIndex() {
		return vaIterator.previousIndex();
	}

	@Override
	public void remove() {
		vaIterator.remove();
	}

	@Override
	public void set(AStorage storage) {
		vaIterator.set(storage.getID());
	}

}
