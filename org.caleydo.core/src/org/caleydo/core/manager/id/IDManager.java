package org.caleydo.core.manager.id;

import java.util.HashMap;
import java.util.Random;
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
	
	/**
	 * Constructor.
	 */
	public IDManager() 
	{
		hashExternalToInternalID = new HashMap<Integer, Integer>();
	}
	
	public int createID(EManagedObjectType type)
	{
		return new Random().nextInt();
	}
	
//	public int createIDFromExternalID(EManagedObjectType type, int iExternalID) 
//	{
//		int iInternalID = createID(type);
//		hashExternalToInternalID.put(iExternalID, iInternalID);
//		
//		return iInternalID;
//	}
	
	public void mapInternalToExternalID(int iInternalID, int iExternalID)
	{
		hashExternalToInternalID.put(iExternalID, iInternalID);
	}
	
	public int getInternalFromExternalID(int iExternalID) 
	{
		if (!hashExternalToInternalID.containsKey(iExternalID))
		{
			throw new CaleydoRuntimeException("Given external ID does not map to any internal ID.", 
					CaleydoRuntimeExceptionType.ID);
		}
		
		return hashExternalToInternalID.get(iExternalID);
	}
}
