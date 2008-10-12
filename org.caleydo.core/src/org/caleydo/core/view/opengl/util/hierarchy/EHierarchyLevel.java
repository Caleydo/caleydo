package org.caleydo.core.view.opengl.util.hierarchy;

/**
 * Enum represents type of RemoteHierarchyLevel
 * 
 * @author Marc Streit
 */
public enum EHierarchyLevel
{
	UNDER_INTERACTION(1, "Under Interaction Lavel"),
	STACK(4, "Stack Level"),
	POOL(18, "Pool Level"),
	MEMO(5, "Memo Level"),
	TRANSITION(1, "Transition Level"),
	SPAWN(1, "Spawn Level");

	private int iCapacity;

	private String sName;
	
	/**
	 * Constructor.
	 * 
	 * @param iCapacity
	 */
	private EHierarchyLevel(final int iCapacity,
			String sName)
	{
		this.sName = sName;
		this.iCapacity = iCapacity;
	}

	public int getCapacity()
	{
		return iCapacity;
	}
	
	public String getName()
	{
		return sName;
	}
}
