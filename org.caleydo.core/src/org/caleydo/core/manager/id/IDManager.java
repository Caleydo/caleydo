package org.caleydo.core.manager.id;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

/**
 * Returns a system-wide unique id for the different managed units
 * 
 * @author Alexander Lex
 * @author Marc Streit
 * 
 */
public class IDManager
{
	private HashMap<Integer, Integer> hashExternalToInternalID;
	private HashMap<Integer, Integer> hashInternalToExternalID;

	private EnumMap<EManagedObjectType, Integer> hashObjectTypeToCounter;

	/**
	 * Constructor.
	 */
	public IDManager()
	{
		hashExternalToInternalID = new HashMap<Integer, Integer>();
		hashInternalToExternalID = new HashMap<Integer, Integer>();

		hashObjectTypeToCounter = new EnumMap<EManagedObjectType, Integer>(
				EManagedObjectType.class);
	}

	/**
	 * Creates a new ID based on the object type. The type prefix defined in
	 * EManagedObjectType is reflected in the last two digits of the number
	 * 
	 * @param type the type of object
	 * @return the id
	 */
	public int createID(EManagedObjectType type)
	{
		Integer iCount = hashObjectTypeToCounter.get(type);

		if (iCount == null)
			iCount = new Integer(0);

		hashObjectTypeToCounter.put(type, ++iCount);
		return calculateID(type, iCount);
	}

	/**
	 * When dealing with external IDs for example specified in an xml file, this
	 * method creates a mapping between those two, which can be retrieved by
	 * calling getInternalFromExternalID(int iExternalID)
	 */
	public void mapInternalToExternalID(int iInternalID, int iExternalID)
	{
		hashExternalToInternalID.put(iExternalID, iInternalID);
		hashInternalToExternalID.put(iInternalID, iExternalID);
	}

	public ArrayList<Integer> convertExternalToInternalIDs(ArrayList<Integer> iAlExternalIDs)
	{
		ArrayList<Integer> iAlInternalIDs = new ArrayList<Integer>(iAlExternalIDs.size());

		for (Integer iExternalID : iAlExternalIDs)
		{
			iAlInternalIDs.add(getInternalFromExternalID(iExternalID));
		}

		return iAlInternalIDs;
	}

	/**
	 * Returns an internal id which is mapped to an external id, when such a
	 * mapping exists.
	 * 
	 * @param iExternalID the external id
	 * @return the internal id
	 * @throws IllegalArgumentException if now mapping exists
	 */
	public int getInternalFromExternalID(int iExternalID)

	{
		if (!hashExternalToInternalID.containsKey(iExternalID))
		{
			throw new IllegalArgumentException("Given external ID " + iExternalID
					+ " does not map to any internal ID.");
		}

		return hashExternalToInternalID.get(iExternalID);
	}

	public int getExternalFromInternalID(int iInternalID)
	{
		if (!hashInternalToExternalID.containsKey(iInternalID))
		{
			throw new IllegalArgumentException("Given internal ID " + iInternalID
					+ " does not map to an external ID.");
		}

		return hashInternalToExternalID.get(iInternalID);
	}

	/**
	 * Calculates the ID, based on type and a counter
	 * 
	 * @param type the type
	 * @param iCount the counter
	 * @return the ID
	 */
	private int calculateID(EManagedObjectType type, int iCount)
	{
		if (iCount > 99999)
			throw new IllegalStateException(
					"ID Overflow. Number of IDs is limited to 99,999 per type");
		return (iCount * 100 + type.getIdPrefix());
	}
}
