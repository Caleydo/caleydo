package org.caleydo.core.data.collection.set;

import java.util.ArrayList;
import java.util.ListIterator;

import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.VAIterator;

/**
 * Implementation of the iterator for sets considering a virtual array.
 * 
 * @author Alexander Lex
 */
public class SetIterator
	implements ListIterator<IStorage> {
	private VAIterator vaIterator;
	private ArrayList<IStorage> alStorages;

	/**
	 * Constructor
	 * 
	 * @param set
	 * @param virtualArray
	 */
	public SetIterator(ArrayList<IStorage> alStorages, IVirtualArray virtualArray) {
		this.vaIterator = virtualArray.iterator();
		this.alStorages = alStorages;
	}

	@Override
	public void add(IStorage storage) {
		vaIterator.add(alStorages.indexOf(storage));
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
		vaIterator.set(alStorages.indexOf(storage));
	}

}
