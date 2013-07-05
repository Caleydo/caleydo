/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.id.object;

import java.util.EnumMap;
import java.util.Map;


/**
 * Returns a system-wide unique id for the different managed units
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public class IDCreator {
	private final Map<ManagedObjectType, Integer> hashObjectTypeToCounter = new EnumMap<ManagedObjectType, Integer>(
			ManagedObjectType.class);

	/**
	 * Creates a new ID based on the object type. The type prefix defined in EManagedObjectType is reflected
	 * in the last two digits of the number
	 *
	 * @param type
	 *            the type of object
	 * @return the id
	 */
	public synchronized int createID(ManagedObjectType type) {
		Integer iCount = hashObjectTypeToCounter.get(type);

		if (iCount == null) {
			iCount = new Integer(0);
		}

		hashObjectTypeToCounter.put(type, ++iCount);
		return calculateID(type, iCount);
	}

	/**
	 * Calculates the ID, based on type and a counter
	 *
	 * @param type
	 *            the type
	 * @param iCount
	 *            the counter
	 * @return the ID
	 */
	private int calculateID(ManagedObjectType type, int iCount) {
		// INT_MAX = 2147483647 -> 21474836 still valid numbers
		if (iCount > (Integer.MAX_VALUE / 100) - 1)
			throw new IllegalStateException("ID Overflow for type " + type
					+ ". Number of IDs is limited to 99,999 per type");
		return iCount * 100 + type.getIdPrefix();
	}
}
