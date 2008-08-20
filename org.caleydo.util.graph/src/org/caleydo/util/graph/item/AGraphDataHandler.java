package org.caleydo.util.graph.item;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

/**
 * Abstract class implementing IGraphDataHandler interface.
 * 
 * @see org.caleydo.util.graph.item.GraphItemDataSequenzer
 * @author Michael Kalkusch
 */
public abstract class AGraphDataHandler
	implements IGraphDataHandler, Serializable
{
	private static final long serialVersionUID = 1L;

	private static final int iInitialSize = 3;

	protected final HashMap<Integer, Object> key_2_data;

	/**
	 * 
	 */
	protected AGraphDataHandler()
	{
		this(iInitialSize);
	}

	/**
	 * @param initialSize define initial size and number of expected objects.
	 */
	protected AGraphDataHandler(int initialSize)
	{
		key_2_data = new HashMap<Integer, Object>(initialSize);
	}

	@Override
	public final boolean containsData(int key)
	{
		return key_2_data.containsKey(new Integer(key));
	}

	@Override
	public final boolean containsDataObject(Object data)
	{
		return key_2_data.containsValue(data);
	}

	@Override
	public final Collection<Object> getAllData()
	{
		return key_2_data.values();
	}

	@Override
	public final Object getData(int key)
	{
		return key_2_data.get(new Integer(key));
	}

	@Override
	public final void removeAllData()
	{
		key_2_data.clear();
	}

	@Override
	public final Object removeData(int key)
	{
		return key_2_data.remove(new Integer(key));
	}

	@Override
	public final boolean setData(int key, Object data)
	{
		return (key_2_data.put(new Integer(key), data) == null) ? false : true;
	}

}
