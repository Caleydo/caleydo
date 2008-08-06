package org.caleydo.core.data.collection.ccontainer;

import java.util.ListIterator;
import org.caleydo.core.data.selection.IVirtualArray;

/**
 * Iterator for containers of the type ATypedCContainer. The iterator can handle
 * virtual arrays (optionally), thereby masking the real structure in the
 * storage
 * 
 * @author Alexander Lex
 * 
 * @param <T> the type
 */
public class ContainerIterator<T>
	extends AContainerIterator
	implements ListIterator<T>
{
	private ATypedCContainer<T> container;
	private ListIterator<T> containerIterator;

	/**
	 * Constructor for iterator. Provide a ListIterator from the ArrayList of
	 * the Container
	 * 
	 * @param container the container itself
	 * @param containerIterator a ListIterator from the ArrayList
	 */
	public ContainerIterator(ATypedCContainer<T> container, ListIterator<T> containerIterator)
	{
		this.container = container;
		this.containerIterator = containerIterator;
	}

	/**
	 * Constructor for iterator when operating with virtual arrays.
	 * 
	 * @param container the container on which is iterated
	 * @param virtualArray the virtual array
	 */
	public ContainerIterator(ATypedCContainer<T> container, IVirtualArray virtualArray)
	{
		this.container = container;
		this.virtualArray = virtualArray;
		this.vaIterator = virtualArray.iterator();
	}

	/**
	 * Operation not supported
	 */
	@Override
	public void add(T element)
	{
		throw new UnsupportedOperationException("Adding is not supported on storages");
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#hasPrevious()
	 */
	@Override
	public boolean hasPrevious()
	{
		if (vaIterator != null)
			return vaIterator.hasPrevious();
		else
			return containerIterator.hasPrevious();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#next()
	 */
	@Override
	public T next()
	{
		if (vaIterator != null)
			return container.get(vaIterator.next());
		else
			return containerIterator.next();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#nextIndex()
	 */
	@Override
	public int nextIndex()
	{
		if (vaIterator != null)
			return vaIterator.nextIndex();
		else
			return containerIterator.nextIndex();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#previous()
	 */
	@Override
	public T previous()
	{
		if (vaIterator != null)
			return container.get(vaIterator.previous());
		else
			return containerIterator.previous();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#previousIndex()
	 */
	@Override
	public int previousIndex()
	{
		if (vaIterator != null)
			return vaIterator.previousIndex();
		else
			return containerIterator.previousIndex();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#set(java.lang.Object)
	 */
	@Override
	public void set(T e)
	{
		// we could allow that here (technically) but we probably don't want to
		throw new UnsupportedOperationException("Modification is not supported on storages");
	}

}
