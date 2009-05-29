package org.caleydo.core.manager;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.usecase.EUseCaseMode;
import org.caleydo.core.view.IView;

/**
 * Use cases are the unique points of coordinations for views and its data.
 * Genetic data is one example - another is a more generic one where Caleydo can load 
 * arbitrary tabular data but without any special features of genetic analysis.
 * 
 * @author Marc Streit
 *
 */
public interface IUseCase {

	/**
	 * Returns whether the application can load and work with non further specified data
	 * (general use case) or if a more specialized use case (e.g. gene expression) is active. 
	 */
	public EUseCaseMode getUseCaseMode();
	
	/**
	 * Returns the set which is currently loaded and used inside the views for this use case.
	 * @return a data set
	 */
	public ISet getSet();
	
	/**
	 * Sets the set which is currently loaded and used inside the views for this use case.
	 * @param set The new set which replaced the currenlty loaded one.
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
	 * Returns the content label.
	 * E.g. gene for genome use case, entity for generic use case
	 * 
	 * @param bUpperCase TRUE makes the label upper case
	 * @param bPlural TRUE label = plural, FALSE label = singular
	 * @return label valid for the specific use case
	 */
	public String getContentLabel(boolean bUpperCase, boolean bPlural);
}
