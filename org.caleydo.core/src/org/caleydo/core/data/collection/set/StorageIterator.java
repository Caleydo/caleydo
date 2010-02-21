package org.caleydo.core.data.collection.set;

import java.util.HashMap;
import java.util.ListIterator;

import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.VAIterator;

/**
 * Implementation of the iterator for sets considering a virtual array.
 * 
 * @author Alexander Lex
 */
public class StorageIterator
	implements ListIterator<IStorage> {
	private VAIterator vaIterator;
	private HashMap<Integer, IStorage> alStorages;

	/**
	 * Constructor
	 * 
	 * @param set
	 * @param virtualArray
	 */
	public StorageIterator(HashMap<Integer, IStorage> alStorages, IVirtualArray virtualArray) {
		this.vaIterator = virtualArray.iterator();
		this.alStorages = alStorages;
	}

	@Override
	public void add(IStorage storage) {
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
	public IStorage next() {
		return alStorages.get(vaIterator.next());
	}

	@Override
	public int nextIndex() {
		return vaIterator.nextIndex();
	}

	@Override
	public IStorage previous() {
		return alStorages.get(vaIterator.previous());
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
	public void set(IStorage storage) {
		vaIterator.set(storage.getID());
	}

}
