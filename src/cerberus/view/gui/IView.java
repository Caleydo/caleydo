package cerberus.view.gui;

import java.util.Vector;

import cerberus.xml.parser.parameter.IParameterHandler;

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

	/**
	 * Method takes uses the parent container ID to retrieve the 
	 * GUI widget by calling the createWidget method from
	 * the SWT GUI Manager.
	 * Method is implemented in the subclasses because only there
	 * the type of the needed widget is available.
	 */
	public void retrieveGUIContainer();
	
	public void readInAttributes(IParameterHandler refParameterHandler );
	
	/**
	 * @deprecated use setAttributes(IParameterHandler) instead
	 * 
	 * @param attributes
	 */
	public void setAttributes(Vector <String> attributes );
	
	/**
	 * Sets the unique ID of the parent container.
	 * Normally it is already set in the constructor.
	 * Use this method only if you want to change the parent during runtime.
	 */
	public void setParentContainerId(int iParentContainerId);
}