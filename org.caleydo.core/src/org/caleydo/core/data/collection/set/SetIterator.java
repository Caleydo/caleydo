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
 * 
 */
public class SetIterator
	implements ListIterator<IStorage>
{
	private VAIterator vaIterator;
	private ArrayList<IStorage> alStorages;

	/**
	 * Constructor
	 * 
	 * @param set
	 * @param virtualArray
	 */
	public SetIterator(ArrayList<IStorage> alStorages, IVirtualArray virtualArray)
	{
		this.vaIterator = virtualArray.iterator();
		this.alStorages = alStorages;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#add(java.lang.Object)
	 */
	@Override
	public void add(IStorage storage)
	{
		vaIterator.add(alStorages.indexOf(storage));
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#hasNext()
	 */
	@Override
	public boolean hasNext()
	{
		return vaIterator.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#hasPrevious()
	 */
	@Override
	public boolean hasPrevious()
	{
		return vaIterator.hasPrevious();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#next()
	 */
	@Override
	public IStorage next()
	{
		return alStorages.get(vaIterator.next());
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#nextIndex()
	 */
	@Override
	public int nextIndex()
	{
		return vaIterator.nextIndex();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#previous()
	 */
	@Override
	public IStorage previous()
	{
		return alStorages.get(vaIterator.previous());
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#previousIndex()
	 */
	@Override
	public int previousIndex()
	{
		return vaIterator.previousIndex();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#remove()
	 */
	@Override
	public void remove()
	{
		vaIterator.remove();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#set(java.lang.Object)
	 */
	@Override
	public void set(IStorage storage)
	{
		vaIterator.set(alStorages.indexOf(storage));
	}

}
