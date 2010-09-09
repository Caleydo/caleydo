package org.caleydo.core.view;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.serialize.ASerializedView;

/**
 * Interface for the view representations.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IView
	extends IUniqueObject {

	/**
	 * Initializes the view after setting all required parameters.
	 */
	public void initialize();

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
