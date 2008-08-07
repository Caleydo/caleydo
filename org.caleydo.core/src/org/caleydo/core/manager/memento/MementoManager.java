package org.caleydo.core.manager.memento;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import org.caleydo.core.command.memento.IMemento;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IMementoManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Simple IMemento Manager, that stores all IMemento's in a Vector.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class MementoManager
	extends AManager
	implements IMementoManager
{

	private int iVecMementoStorageSize;

	protected Vector<IMemento> vecMemento;

	protected Hashtable<Integer, Integer> hashMementoId2Index;

	protected final int iInitSizeMementoVector = 40;

	/**
	 * Constructor.
	 */
	public MementoManager()
	{
		vecMemento = new Vector<IMemento>(iInitSizeMementoVector);
		hashMementoId2Index = new Hashtable<Integer, Integer>(iInitSizeMementoVector * 2);
		iVecMementoStorageSize = 0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IMementoManager#pushMemento(org.caleydo.core.command.memento.IMemento)
	 */
	public final int pushMemento(final IMemento addMemento)
	{
		//TODO: review when implementing ID management
		final int iUniqueId = -1;//createId(EManagerObjectType.MEMENTO);

		try
		{
			vecMemento.add(addMemento);
			iVecMementoStorageSize = vecMemento.size();
			hashMementoId2Index.put(iUniqueId, iVecMementoStorageSize - 1);
			return iUniqueId;

		}
		catch (Exception e)
		{
			throw new CaleydoRuntimeException("setMemento(IMemento) failed. " + e.toString(),
					CaleydoRuntimeExceptionType.MEMENTO);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see prometheus.command.memento.manager.MementoManager#getMemento(int)
	 */
	public IMemento getMemento(int iMementoId)
	{

		final int iVectorIndex = hashMementoId2Index.get(iMementoId);

		try
		{
			return vecMemento.get(iVectorIndex);
		}
		catch (ArrayIndexOutOfBoundsException ae)
		{
			assert false : "getMemento(int) failed due to wrong iMementoId. " + ae.toString();
			return null;
		}
	}

	public IMemento pullMemento(final int iMementoId)
	{

		final int iIndex = hashMementoId2Index.get(iMementoId);

		try
		{
			IMemento pullMemento = vecMemento.get(iIndex);
			vecMemento.removeElementAt(iIndex);
			return pullMemento;
		}
		catch (ArrayIndexOutOfBoundsException ae)
		{
			assert false : "getMemento(int) failed due to wrong iMementoId. " + ae.toString();
			return null;
		}
	}

	public boolean pullMemento(IMemento pullMemento)
	{

		return vecMemento.contains(pullMemento);
	}

	/*
	 * (non-Javadoc)
	 * @see prometheus.command.memento.manager.MementoManager#isMementoId(int)
	 */
	public boolean isMementoId(int iMementoId)
	{

		return this.hashMementoId2Index.contains(iMementoId);
	}

	/*
	 * (non-Javadoc)
	 * @see prometheus.data.manager.GeneralManagerInterface#size()
	 */
	public final int size()
	{

		return vecMemento.size();
	}

	public String toString()
	{

		final String newLine = "\n";
		String result = "[";

		// Show content of Vector ...

		int iCounter = 0;
		Iterator<IMemento> iter = vecMemento.iterator();

		if (!iter.hasNext())
		{
			result += "Vector: is empty" + newLine;
		}

		while (iter.hasNext())
		{
			IMemento buffer = iter.next();

			if (buffer != null)
			{
				result += " #" + iCounter + ": " + buffer.toString() + newLine;
			}
			else
			{
				result += " #" + iCounter + ": null" + newLine;
			}

			iCounter++;
		}

		result += "]" + newLine;

		return result;
	}

	/**
	 * Not used.
	 */
	public void optimize()
	{

	}

	/*
	 * (non-Javadoc)
	 * @see prometheus.data.manager.MementoManager#clearAllMementos()
	 */
	public void clearAllMementos()
	{

		vecMemento.clear();
		hashMementoId2Index.clear();
		iVecMementoStorageSize = vecMemento.size();
	}

	/**
	 * Mementos are internal structures and can not be search for.
	 * 
	 * @see org.caleydo.core.manager.IGeneralManager#hasItem(int)
	 */
	public final boolean hasItem(final int iItemId)
	{

		return false;
	}

	public final Object getItem(final int iItemId)
	{

		return null;
	}

	/**
	 * Writes all current stored mementos to the ObjectOutputStream.
	 * 
	 * @return true on success
	 */
	public boolean writeToOutputStream(ObjectOutputStream outStream)
	{

		// TODO: code this
		return true;
	}

	/**
	 * Reads stored mementos from ObjectInputStream.
	 * 
	 * @return true on success
	 */
	public boolean readFromInputStream(ObjectInputStream inStream)
	{

		// TODO: code this

		return true;
	}

}
