package cerberus.view.gui;

import java.util.Vector;

/**
 * Interface for the view representations.
 * 
 * @author Marc Streit
 */
public interface IView
{
	/**
	 * Initialization of the view.
	 */
	public void initView();

	/**
	 * Method is responsible for filling the composite with content.
	 */
	public void drawView();

	public void retrieveNewGUIContainer();

	public void retrieveExistingGUIContainer();
	
	public void setAttributes(Vector <String> attributes );
	
	/**
	 * Sets the unique ID of the parent container.
	 * Normally it is already set in the constructor.
	 * Use this method only if you want to change the parent during runtime.
	 */
	public void setParentContainerId(int iParentContainerId);
}