package org.caleydo.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Convert SET <K> to SET <V> and Collection <K> to Collection <V> were K,V are
 * elementOf(Integer, String)
 * 
 * @see org.caleydo.core.manager.specialized.genome.id.GenomeIdMapInt2Int#getValuesInteger()
 * @author Michael Kalkusch
 */
public final class ConversionStringInteger
{

	/**
	 * Convert a Collection <String> into a Collection <Integer>.
	 * 
	 * @param input
	 *            Collection <String> to be converted
	 * @return same collection as input as Integer
	 */
	public static final Set<Integer> convertSet_String2Integer(Set<String> input)
	{

		if (input.isEmpty())
		{
			return new HashSet<Integer>();
		}

		try
		{
			Iterator<String> iter = input.iterator();
			HashSet<Integer> result = new HashSet<Integer>(input.size());

			while (iter.hasNext())
			{
				result.add(Integer.parseInt(iter.next()));
			}

			return result;
		}
		catch (NumberFormatException nfe)
		{
			throw new CaleydoRuntimeException("Can not convert String to Integer; "
					+ nfe.toString());
		}

	}

	/**
	 * Convert a Collection <Integer> into a Collection <String>.
	 * 
	 * @param input
	 *            Collection <Integer> to be converted
	 * @return collection as input as String
	 */
	public static final Set<String> convertSet_Integer2String(Set<Integer> input)
	{

		if (input.isEmpty())
		{
			return new HashSet<String>();
		}

		Iterator<Integer> iter = input.iterator();
		HashSet<String> result = new HashSet<String>(input.size());

		while (iter.hasNext())
		{
			result.add(iter.next().toString());
		}

		return result;
	}

	/**
	 * Convert a Collection <String> into a Collection <Integer>.
	 * 
	 * @param input
	 *            Collection <String> to be converted
	 * @return same collection as input as Integer
	 */
	public static final Collection<Integer> convertCollection_String2Integer(
			Collection<String> input)
	{

		if (input.isEmpty())
		{
			return new ArrayList<Integer>();
		}

		try
		{
			Iterator<String> iter = input.iterator();
			ArrayList<Integer> result = new ArrayList<Integer>(input.size());

			while (iter.hasNext())
			{
				result.add(Integer.parseInt(iter.next()));
			}

			return result;
		}
		catch (NumberFormatException nfe)
		{
			throw new CaleydoRuntimeException("Can not convert String to Integer; "
					+ nfe.toString());
		}
	}

	/**
	 * Convert a Collection <Integer> into a Collection <String>.
	 * 
	 * @param input
	 *            Collection <Integer> to be converted
	 * @return collection as input as String
	 */
	public static final Collection<String> convertCollection_Integer2String(
			Collection<Integer> input)
	{

		if (input.isEmpty())
		{
			return new ArrayList<String>();
		}

		Iterator<Integer> iter = input.iterator();
		ArrayList<String> result = new ArrayList<String>(input.size());

		while (iter.hasNext())
		{
			result.add(iter.next().toString());
		}

		return result;
	}

}
