package org.caleydo.core.data.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Multi hash map implementation using generics.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class MultiHashMap<KeyType, ValueType>
	extends HashMap<KeyType, Set<ValueType>>
	implements Map<KeyType, Set<ValueType>>
{
	public boolean remove(KeyType key, ValueType value)
	{
		Set<ValueType> hashTmp = super.get(key);

		if (hashTmp == null)
			return false;

		return hashTmp.remove(value);
	}

	public void put(KeyType key, ValueType value)
	{
		Set<ValueType> hashTmp = super.get(key);

		if (hashTmp == null)
		{
			hashTmp = new HashSet<ValueType>();
		}

		hashTmp.add(value);
		super.put(key, hashTmp);
	}
}
