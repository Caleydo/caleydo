package org.caleydo.core.view.opengl.util.hierarchy;

/**
 * Enum represents type of RemoteHierarchyLevel
 * 
 * @author Marc Streit
 */
public enum EHierarchyLevel
{
	UNDER_INTERACTION(1), STACK(4), POOL(100), MEMO(5), TRANSITION(1), SPAWN(1);

	private int iCapacity;

	/**
	 * Constructor.
	 * 
	 * @param iCapacity
	 */
	private EHierarchyLevel(final int iCapacity)
	{

		this.iCapacity = iCapacity;
	}

	public int getCapacity()
	{

		return iCapacity;
	}
}
