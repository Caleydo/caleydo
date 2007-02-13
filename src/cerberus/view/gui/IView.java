package cerberus.view.gui;

import cerberus.data.IUniqueObject;
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
	 */
	public void retrieveGUIContainer();
	
	/**
	 * Sets the unique ID of the parent container.
	 * Normally it is already set in the constructor.
	 * Use this method only if you want to change the parent during runtime.
	 */
	public void setParentContainerId(int iParentContainerId);
	

	
	/**
	 * Returns the ID of the SET that is the base data of that view.
	 * If no SET is stored 0 is returned.
	 *
	 */
	public int getDataSetId();
	

	public void setDataSetId(int iDataSetId);
	
	
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