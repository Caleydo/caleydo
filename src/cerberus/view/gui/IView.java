package cerberus.view.gui;

import cerberus.data.IUniqueObject;
import cerberus.data.collection.SetType;
import cerberus.view.gui.ViewType;

//import cerberus.command.ICommand;
//import cerberus.data.collection.ISet;

/**
 * Interface for the view representations.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public interface IView 
extends IUniqueObject {
	
	/**
	 * Initialization of the view.
	 * All initialization sets must be accomplished in theis methode.
	 */
	public void initView();

	/**
	 * Method is responsible for filling the composite with content.
	 */
	public void drawView();

	/**
	 * Method takes uses the parent container ID to retrieve the 
	 * GUI widget by calling the createWidget method from
	 * the SWT GUI Manager.
	 * Method is implemented in the subclasses because only there
	 * the type of the needed widget is available.
	 * 
	 * @deprecated use initView()
	 */
	public void retrieveGUIContainer();
	
	/**
	 * Sets the unique ID of the parent container.
	 * Normally it is already set in the constructor.
	 * Use this method only if you want to change the parent during runtime.
	 */
	public void setParentContainerId(int iParentContainerId);
	
	/**
	 * Add a new Set to the view via its SetId
	 * 
	 * @param iDataSetId array of SetId's to be added
	 */
	public void addSetId( int [] iSet);
	
	/**
	 * Remove a Set from the view via its SetId
	 * 
	 * @param iDataSetId array of SetId's to be added
	 */
	public void removeSetId( int [] iSet);
	
	/**
	 * Removes all Set of a given type from the View.
	 *  
	 * @param setType define which type of Set shall be removed
	 */
	public void removeAllSetIdByType( SetType setType );
	
	/**
	 * Get all the SetId's assigned to this View.
	 * 
	 * @return array of all SetId's
	 */
	public int[] getAllSetId();
	
	/**
	 * Ask if a cairtain Set is used by this view.
	 * 
	 * @param iSetId define a Set vai its Id
	 * @return TRUE if teh cset is used by the view
	 */
	public boolean hasSetId( int iSetId);
	
	/**
	 * Method return the label of the view.
	 * @return View name
	 */
	public String getLabel();
	
	
	/**
	 * Get the ViewType of this ViewRep.
	 * 
	 * @return 
	 */
	public ViewType getViewType();

}