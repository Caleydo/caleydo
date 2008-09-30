package org.caleydo.core.data.selection;

import java.util.HashMap;

/**
 * Enum that lists the possible selection types
 * 
 * @author Alexander Lex
 * 
 */
public enum ESelectionType
{
	NORMAL(0),
	SELECTION(1),
	MOUSE_OVER(2),
	ADD(99),
	DESELECTED(-2),
	REMOVE(-1);

	private int iType;
	private static HashMap<Integer, ESelectionType> hashValuesToEnum;
	private static boolean bIsFirstTime = true;

	// private static ArrayList<String> alSelectionType;

	/**
	 * Constructor
	 */
	ESelectionType(int iType)
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

	public static ESelectionType valueOf(int iRep)
	{
		if (bIsFirstTime)
		{
			init();
			bIsFirstTime = false;
		}
		return hashValuesToEnum.get(iRep);
	}

	private static void init()
	{
		hashValuesToEnum = new HashMap<Integer, ESelectionType>();
		for (ESelectionType selectionType : ESelectionType.values())
		{
			hashValuesToEnum.put(selectionType.intRep(), selectionType);
		}
	}
}
