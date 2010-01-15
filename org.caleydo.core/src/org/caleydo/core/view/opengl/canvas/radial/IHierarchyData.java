package org.caleydo.core.view.opengl.canvas.radial;

/**
 * Basic interface for hierarchical data that shall be visualized in the radial hierarchy view.
 * 
 * @author Christian Partl
 */
public interface IHierarchyData<E extends IHierarchyData<E>>
	extends Comparable<E> {

	/**
	 * @return ID of the hierarchical data object.
	 */
	public int getID();

	/**
	 * @return Size of the hierarchical data object.
	 */
	public float getSize();

	/**
	 * @return Text describing the hierarchical data object.
	 */
	public String getLabel();

	/**
	 * @return Value that shall be used when comparing hierarchical data object.
	 */
	public int getComparableValue();
}
