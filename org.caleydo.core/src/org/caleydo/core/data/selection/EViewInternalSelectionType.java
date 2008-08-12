package org.caleydo.core.data.selection;

import java.util.HashMap;

/**
 * Enum that lists the possible selection types
 * 
 * @author Alexander Lex
 * 
 */
public enum EViewInternalSelectionType
{
	NORMAL(0),
	SELECTION(1),
	MOUSE_OVER(2),
	DESELECTED(-2),
	REMOVE(-1);

	private int iType;
	private static HashMap<Integer, EViewInternalSelectionType> hashValuesToEnum;
	private static boolean bIsFirstTime = true;

	// private static ArrayList<String> alSelectionType;

	/**
	 * Constructor
	 */
	EViewInternalSelectionType(int iType)
	{
		this.iType = iType;
		// hashValuesToEnum =

	}

	/**
	 * Returns an integer representation of the element
	 * 
	 * @return the integer representation
	 */
	public int intRep()
	{
		return iType;
	}


	public static EViewInternalSelectionType valueOf(int iRep)
	{
		if(bIsFirstTime)
		{
			init();
			bIsFirstTime = false;
		}
		return hashValuesToEnum.get(iRep);
	}
	
	private static void init()
	{
		hashValuesToEnum = new HashMap<Integer, EViewInternalSelectionType>();
		for (EViewInternalSelectionType selectionType : EViewInternalSelectionType.values())
		{
			hashValuesToEnum.put(selectionType.intRep(), selectionType);
		}
	}
}
