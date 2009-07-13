package org.caleydo.core.manager;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.usecase.EUseCaseMode;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

/**
 * Use cases are the unique points of coordinations for views and its data. Genetic data is one example -
 * another is a more generic one where Caleydo can load arbitrary tabular data but without any special
 * features of genetic analysis.
 * 
 * @author Marc Streit
 */
public interface IUseCase {

	/**
	 * Returns whether the application can load and work with non further specified data (general use case) or
	 * if a more specialized use case (e.g. gene expression) is active.
	 */
	public EUseCaseMode getUseCaseMode();

	/**
	 * Returns the set which is currently loaded and used inside the views for this use case.
	 * 
	 * @return a data set
	 */
	public ISet getSet();

	/**
	 * Sets the set which is currently loaded and used inside the views for this use case.
	 * 
	 * @param set
	 *            The new set which replaced the currenlty loaded one.
	 */
	public void setSet(ISet set);

	/**
	 * Add a view that is part of the use case.
	 * 
	 * @param view
	 */
	public void addView(IView view);

	/**
	 * Remove a view that is part of the use case.
	 * 
	 * @param view
	 */
	public void removeView(IView view);

	/**
	 * Update the data set in the view of this use case.
	 */
	public void updateSetInViews();

	/**
	 * Returns the content label. E.g. gene for genome use case, entity for generic use case
	 * 
	 * @param bUpperCase
	 *            TRUE makes the label upper case
	 * @param bPlural
	 *            TRUE label = plural, FALSE label = singular
	 * @return label valid for the specific use case
	 */
	public String getContentLabel(boolean bUpperCase, boolean bPlural);

	/**
	 * Returns the ID of the virtual array associated with a particular type specified via the parameter for
	 * the set associated with the use case.
	 * 
	 * @param vaType
	 *            the type of VA requested
	 * @return the ID of the VirtualArray
	 */
	public IVirtualArray getVA(EVAType vaType);

	/**
	 * Initiates clustering based on the parameters passed. Sends out an event to all affected views upon
	 * positive completion to replace their VA.
	 * 
	 * @param clusterState
	 */
	public void startClustering(ClusterState clusterState);
	
	/**
	 * Resets the context VA to it's initial state
	 */
	public void resetContextVA();
}
