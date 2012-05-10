/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.collection;

import java.util.ArrayList;
import java.util.Collection;
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
	implements Map<KeyType, ValueType> {
	HashMap<KeyType, Set<ValueType>> internalMap;

	public MultiHashMap() {
		internalMap = new HashMap<KeyType, Set<ValueType>>();
	}

	public boolean remove(KeyType key, ValueType value) {
		Set<ValueType> tmpSet = internalMap.get(key);

		if (tmpSet == null)
			return false;

		return tmpSet.remove(value);
	}

	/**
	 * Behaves similar to a classical hashMap but does not replace the key, it adds it instead.
	 * 
	 * @return always null
	 */
	@Override
	public ValueType put(KeyType key, ValueType value) {
		Set<ValueType> tmpSet = internalMap.get(key);

		if (tmpSet == null) {
			tmpSet = new HashSet<ValueType>();
		}

		tmpSet.add(value);
		internalMap.put(key, tmpSet);
		return null;
	}

	@Override
	public void clear() {
		internalMap.clear();
	}

	@Override
	public boolean containsKey(Object arg0) {
		return internalMap.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object arg0) {
		for (Collection<ValueType> tmpCollection : internalMap.values()) {
			if (tmpCollection.contains(arg0))
				return true;
		}
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<KeyType, ValueType>> entrySet() {
		throw new UnsupportedOperationException("MultiHashMap does not support entrySet()");
	}

	@Override
	public ValueType get(Object arg0) {
		throw new UnsupportedOperationException("MultiHashMap does not support get(), use getAll()");
	}

	public Set<ValueType> getAll(Object arg0) {
		return internalMap.get(arg0);
	}

	@Override
	public boolean isEmpty() {
		return internalMap.isEmpty();
	}

	@Override
	public Set<KeyType> keySet() {
		return internalMap.keySet();
	}

	@Override
	public void putAll(Map<? extends KeyType, ? extends ValueType> arg0) {
		for (KeyType key : arg0.keySet()) {
			put(key, arg0.get(key));
		}
	}

	/**
	 * Removes all occurrences of the object and returns the first of the removed objects. Implemented for
	 * compatibility, use of {@link #removeAllOccurences(Object)} is recommended instead
	 */
	@Override
	public ValueType remove(Object arg0) {
		Set<ValueType> tempSet = internalMap.remove(arg0);
		if (tempSet == null)
			return null;
		else
			return tempSet.iterator().next();
	}

	public Set<ValueType> removeAllOccurences(Object arg0) {
		return internalMap.remove(arg0);
	}

	@Override
	public int size() {
		int iSize = 0;
		for (KeyType key : internalMap.keySet()) {
			iSize += internalMap.get(key).size();
		}

		return iSize;
	}

	@Override
	public Collection<ValueType> values() {
		Collection<ValueType> values = new ArrayList<ValueType>();

		for (KeyType key : internalMap.keySet()) {
			Set<ValueType> tempSet = internalMap.get(key);

			values.addAll(tempSet);
		}
		return values;
	}
}
