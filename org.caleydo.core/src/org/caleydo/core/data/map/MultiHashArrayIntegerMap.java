/**
 * 
 */
package org.caleydo.core.data.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Kalkusch
 * @see org.caleydo.core.data.map.MultiHashArrayStringMap
 * @see org.caleydo.core.manager.specialized.genome.IGenomeIdMap
 */
public class MultiHashArrayIntegerMap
	extends HashMap<Integer, ArrayList<Integer>>
	implements GenericMultiMap<Integer>
{

	static final long serialVersionUID = 80806678;

	final int iDefaultLengthInternalArrayList = 3;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public MultiHashArrayIntegerMap(int arg0, float arg1)
	{

		super(arg0, arg1);

	}

	/**
	 * @param arg0
	 */
	public MultiHashArrayIntegerMap(int arg0)
	{

		super(arg0);

	}

	/**
	 * 
	 */
	public MultiHashArrayIntegerMap()
	{

		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public MultiHashArrayIntegerMap(Map<Integer, ArrayList<Integer>> arg0)
	{

		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.commons.collections.MultiMap#remove(java.lang.Object,
	 * java.lang.Object)
	 */
	public Object remove(Object key, Object item)
	{

		ArrayList<Integer> bufferArrayList = super.get(key);

		if (bufferArrayList == null)
			return null;

		bufferArrayList.remove(item);
		return null;
	}

	public void putAll(Map mapToPut)
	{

		super.putAll((MultiIntegerMap) mapToPut);
	}

	public Object put(Integer key, Integer value)
	{

		// NOTE:: put might be called during deserialization !!!!!!
		// so we must provide a hook to handle this case
		// This means that we cannot make MultiMaps of ArrayLists !!!

		ArrayList<Integer> bufferArrayList = super.get(key);

		if (bufferArrayList == null)
		{
			/**
			 * new value!
			 */
			bufferArrayList = new ArrayList<Integer>(iDefaultLengthInternalArrayList);
		}

		bufferArrayList.add(value);
		return super.put(key, bufferArrayList);
	}

	/**
	 * Checkes if value is already registerd to the key.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Object putChecked(Integer key, Integer value)
	{

		// NOTE:: put might be called during deserialization !!!!!!
		// so we must provide a hook to handle this case
		// This means that we cannot make MultiMaps of ArrayLists !!!

		ArrayList<Integer> bufferArrayList = super.get(key);

		if (bufferArrayList == null)
		{
			/**
			 * new value!
			 */
			bufferArrayList = new ArrayList<Integer>(iDefaultLengthInternalArrayList);
			bufferArrayList.add(value);
			return (super.put(key, bufferArrayList));
		}

		if (!bufferArrayList.contains(value))
		{
			bufferArrayList.add(value);
			return super.put(key, bufferArrayList);
		}
		return null;
	}

	public boolean containsValue(Object value)
	{

		Set<Entry<Integer, ArrayList<Integer>>> pairs = super.entrySet();

		if (pairs == null)
			return false;

		Iterator<Entry<Integer, ArrayList<Integer>>> pairsIterator = pairs.iterator();

		while (pairsIterator.hasNext())
		{
			Map.Entry<Integer, ArrayList<Integer>> keyValuePair = pairsIterator.next();

			ArrayList<Integer> list = keyValuePair.getValue();

			if (list.contains(value))
				return true;
		}
		return false;
	}

	public void clear()
	{

		Set<Entry<Integer, ArrayList<Integer>>> pairs = super.entrySet();

		Iterator<Entry<Integer, ArrayList<Integer>>> pairsIterator = pairs.iterator();

		while (pairsIterator.hasNext())
		{
			Map.Entry<Integer, ArrayList<Integer>> keyValuePair = pairsIterator.next();

			ArrayList<Integer> list = keyValuePair.getValue();
			list.clear();
		}

		super.clear();
	}

	public Collection<ArrayList<Integer>> values()
	{

		assert false : "values() in not implemented yet!";

		return null;
	}

	public static ArrayList<Integer> mergeRemoveCopies(ArrayList<Integer> first,
			final ArrayList<Integer> second)
	{

		first.ensureCapacity(first.size() + second.size());
		Iterator<Integer> iter = second.iterator();

		Integer sBuffer;
		while (iter.hasNext())
		{
			if (!first.contains(sBuffer = iter.next()))
			{
				first.add(sBuffer);
			}
		}

		return first;
	}

}
