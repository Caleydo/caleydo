package org.caleydo.core.view;

import java.util.ArrayList;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.view.serialize.ASerializedView;

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
	 * Set the label of the view
	 * 
	 * @param label
	 *            the label
	 */
	void setLabel(String label);

	/**
	 * Add a set to a view by passing the set itself
	 * 
	 * @param set
	 *            the set
	 */
	public void addSet(ISet set);

	/**
	 * Add a set to a view by passing the ID of the set
	 * 
	 * @param iSetID
	 *            the id of the set
	 */
	public void addSet(int iSetID);

	/**
	 * Add a list of sets to the view
	 * 
	 * @param alSets
	 *            the list of sets
	 */
	public void addSets(ArrayList<ISet> alSets);

	/**
	 * Remove all sets that have the specified set type
	 * 
	 * @param setType
	 *            the type of the set
	 */
	public void removeSets(ESetType setType);

	/**
	 * Remove all sets from the view
	 */
	public void clearSets();

	/**
	 * Retreives a serializeable representation of the view
	 * @return serialized representation of the view 
	 */
	public ASerializedView getSerializableRepresentation();
	
}
