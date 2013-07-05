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
