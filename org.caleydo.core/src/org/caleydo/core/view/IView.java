package org.caleydo.core.view;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.serialize.ASerializedView;

/**
 * Interface for the view representations.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 * @author Alexander Lex
 */
public interface IView
	extends IUniqueObject {
	/**
	 * Sets the unique ID of the parent container. Normally it is already set in the constructor. Use this
	 * method only if you want to change the parent during runtime.
	 */
	public void setParentContainerId(int iParentContainerId);

	/**
	 * Method return the label of the view.
	 * 
	 * @return View name
	 */
	public String getLabel();

	/**
	 * Initializes the view after setting all required parameters.
	 */
	public void initialize();

	/**
	 * Set the label of the view
	 * 
	 * @param label
	 *            the label
	 */
	void setLabel(String label);

	/**
	 * Retrieves a serializable representation of the view
	 * 
	 * @return serialized representation of the view
	 */
	public ASerializedView getSerializableRepresentation();

	/**
	 * Initializes the view with the values from the given {@link ASerializedView}.
	 * 
	 * @param serializedView
	 *            serialized representation of the view.
	 */
	public void initFromSerializableRepresentation(ASerializedView serializedView);

	public String getViewType();
}
