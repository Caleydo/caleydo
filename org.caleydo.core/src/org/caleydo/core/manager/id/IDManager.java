package org.caleydo.core.manager.id;

import java.util.EnumMap;
import java.util.HashMap;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

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
	private EnumMap<EManagedObjectType, Integer> hashObjectTypeToCounter;

	/**
	 * Constructor.
	 */
	public IDManager()
	{
		hashExternalToInternalID = new HashMap<Integer, Integer>();
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
		hashObjectTypeToCounter.put(type, iCount++);
		return calculateID(type, iCount);

	}

	// public int createIDFromExternalID(EManagedObjectType type, int
	// iExternalID)
	// {
	// int iInternalID = createID(type);
	// hashExternalToInternalID.put(iExternalID, iInternalID);
	//		
	// return iInternalID;
	// }

	/**
	 * When dealing with external IDs for example specified in an xml file, this
	 * method creates a mapping between those two, which can be retrieved by
	 * calling getInternalFromExternalID(int iExternalID)
	 */
	public void mapInternalToExternalID(int iInternalID, int iExternalID)
	{
		hashExternalToInternalID.put(iExternalID, iInternalID);
	}

	/**
	 * Returns an internal id which is mapped to an external id, when such a mapping exists.
	 * 
	 * @param iExternalID the external id
	 * @return the internal id
	 * @throws CaleydoRuntimeException if now mapping exists
	 */
	public int getInternalFromExternalID(int iExternalID)
	{
		if (!hashExternalToInternalID.containsKey(iExternalID))
		{
			throw new CaleydoRuntimeException(
					"Given external ID does not map to any internal ID.",
					CaleydoRuntimeExceptionType.ID);
		}

		return hashExternalToInternalID.get(iExternalID);
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
			throw new CaleydoRuntimeException(
					"ID Overflow. Number of IDs is limited to 99,999 per type",
					CaleydoRuntimeExceptionType.MANAGER);
		return (iCount * 100 + type.getIdPrefix());
	}
}
