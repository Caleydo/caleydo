package org.caleydo.core.view;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.serialize.ASerializedView;

/**
 * Interface for the view representations.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 * @author Alexander Lex
 */
public interface IView
	extends IUniqueObject{
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
	 * Set the label of the view
	 * 
	 * @param label
	 *            the label
	 */
	void setLabel(String label);

	/**
	 * Set the data domain which determines the behavior of the view. Attention: The data domain need not be changed
	 * at runtime.
	 * 
	 * @param dataDomain
	 */
	public void setDataDomain(IDataDomain dataDomain);

	/**
	 * Get the data domain the view is operating on
	 * 
	 * @return
	 */
	public IDataDomain getDataDomain();

	
	/**
	 * Retrieves a serializeable representation of the view
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
