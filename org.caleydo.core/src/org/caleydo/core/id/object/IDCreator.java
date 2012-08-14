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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;


/**
 * Returns a system-wide unique id for the different managed units
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class IDCreator {
	private HashMap<Integer, Integer> hashExternalToInternalID;
	private HashMap<Integer, Integer> hashInternalToExternalID;

	private EnumMap<ManagedObjectType, Integer> hashObjectTypeToCounter;

	/**
	 * Constructor.
	 */
	public IDCreator() {
		hashExternalToInternalID = new HashMap<Integer, Integer>();
		hashInternalToExternalID = new HashMap<Integer, Integer>();

		hashObjectTypeToCounter = new EnumMap<ManagedObjectType, Integer>(ManagedObjectType.class);
	}

	/**
	 * Creates a new ID based on the object type. The type prefix defined in EManagedObjectType is reflected
	 * in the last two digits of the number
	 * 
	 * @param type
	 *            the type of object
	 * @return the id
	 */
	public int createID(ManagedObjectType type) {
		Integer iCount = hashObjectTypeToCounter.get(type);

		if (iCount == null) {
			iCount = new Integer(0);
		}

		hashObjectTypeToCounter.put(type, ++iCount);
		return calculateID(type, iCount);
	}

	/**
	 * When dealing with external IDs for example specified in an xml file, this method creates a mapping
	 * between those two, which can be retrieved by calling getInternalFromExternalID(int externalID)
	 */
	public void mapInternalToExternalID(int internalID, int externalID) {
		hashExternalToInternalID.put(externalID, internalID);
		hashInternalToExternalID.put(internalID, externalID);
	}

	/**
	 * Converts IDs used in xml files to those used internally.
	 * 
	 * @param iAlExternalIDs
	 * @return
	 */
	public ArrayList<Integer> convertExternalToInternalIDs(ArrayList<Integer> iAlExternalIDs) {
		ArrayList<Integer> iAlInternalIDs = new ArrayList<Integer>(iAlExternalIDs.size());

		for (Integer externalID : iAlExternalIDs) {
			iAlInternalIDs.add(getInternalFromExternalID(externalID));
		}

		return iAlInternalIDs;
	}

	/**
	 * Returns an internal id which is mapped to an external id, when such a mapping exists.
	 * 
	 * @param externalID
	 *            the external id
	 * @return the internal id
	 * @throws IllegalArgumentException
	 *             if now mapping exists
	 */
	public int getInternalFromExternalID(int externalID)

	{
		if (!hashExternalToInternalID.containsKey(externalID))
			throw new IllegalArgumentException("Given external ID " + externalID
				+ " does not map to any internal ID.");

		return hashExternalToInternalID.get(externalID);
	}

	public int getExternalFromInternalID(int internalID) {
		if (!hashInternalToExternalID.containsKey(internalID))
			throw new IllegalArgumentException("Given internal ID " + internalID
				+ " does not map to an external ID.");

		return hashInternalToExternalID.get(internalID);
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
		if (iCount > 99999)
			throw new IllegalStateException("ID Overflow for type " + type
				+ ". Number of IDs is limited to 99,999 per type");
		return iCount * 100 + type.getIdPrefix();
	}
}
