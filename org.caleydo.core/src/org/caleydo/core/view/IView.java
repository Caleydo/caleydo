package org.caleydo.core.view;

import org.caleydo.core.data.IUniqueObject;

/**
 * Interface for the view representations.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public interface IView
	extends IUniqueObject
{
	/**
	 * Sets the unique ID of the parent container. Normally it is already set in
	 * the constructor. Use this method only if you want to change the parent
	 * during runtime.
	 */
	public void setParentContainerId(int iParentContainerId);

	/**
	 * Method return the label of the view.
	 * 
	 * @return View name
	 */
	public String getLabel();

	/**
	 * Set the label of the view
	 * 
	 * @param label the label
	 */
	void setLabel(String label);
}
