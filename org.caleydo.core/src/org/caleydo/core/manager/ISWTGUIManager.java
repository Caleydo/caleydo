package org.caleydo.core.manager;

import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.view.swt.ISWTWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public interface ISWTGUIManager 
extends IManager
{	
	public ISWTWidget createWidget(
			final ManagerObjectType useWidgetType, 
			int iUniqueParentWindowId, 
			int iWidth, 
			int iHeight);
	
	public ISWTWidget createWidget(
			final ManagerObjectType useWidgetType, 
			final Composite externalParentComposite,
			int iWidth,
			int iHeight);
	
	/**
	 * Method cretes an unique window ID and calls createWindow(iUniqueId)
	 * with the default layout (ROW VERTICAL).
	 * 
	 * @return Newly created shell.
	 */
	public Shell createWindow();
	
	/**
	 * Method takes a window ID and creates a shell using this ID.
	 * Also the layout is set here.
	 * 
	 * @return Newly created shell.
	 */
	public Shell createWindow(
			int iUniqueId, 
			String sLabel, 
			String sLayoutAttributes);
	
	/**
	 * Searches for the parent window and 
	 * creates a new composite in that window.
	 */
	public void createComposite(
			int iUniqueId, 
			int iUniqueParentContainerId, 
			String sLayoutAttributes);

	public void runApplication();
	
	public void createLoadingProgressBar();
	
	/**
	 * Sets the percentage of the progress bar during the loading progress 
	 * of the application.
	 * 
	 * @param iPercentage
	 * @return True when the assignment was successful.
	 */
	public boolean setLoadingProgressBarPercentage(int iPercentage);
	
	/**
	 * Returns the current percentage of the loading progress bar of the
	 * application.
	 * 
	 * @return Progress bar percentage.
	 */
	public int getLoadingProgressBarPercentage();
	
	/**
	 * Show or hide the "global" progress bar.
	 * 
	 * @param state TRUE enables the progress bar and shows it; FALSE hides the progressbar.
	 */
	public void setProgressbarVisible( final boolean state);
	
}
