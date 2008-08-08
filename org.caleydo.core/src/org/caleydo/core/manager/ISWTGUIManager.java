package org.caleydo.core.manager;

import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.ISWTWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public interface ISWTGUIManager
{

	public ISWTWidget createWidget(final EManagedObjectType useWidgetType,
			int iUniqueParentWindowId, int iWidth, int iHeight);

	public ISWTWidget createWidget(final EManagedObjectType useWidgetType,
			final Composite externalParentComposite, int iWidth, int iHeight);

	/**
	 * Method creates a shell with a given label and layout.
	 * 
	 * @return Unique shell ID
	 */
	public int createWindow(String sLabel, String sLayoutAttributes);

	/**
	 * Searches for the parent window and creates a new composite in that
	 * window.
	 */
	public void createComposite(int iUniqueId, int iUniqueParentContainerId,
			String sLayoutAttributes);

	public void runApplication();

	public void createLoadingProgressBar();

	/**
	 * Sets the percentage of the progress bar during the loading progress of
	 * the application.
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
	 * @param state TRUE enables the progress bar and shows it; FALSE hides the
	 *            progressbar.
	 */
	public void setProgressbarVisible(final boolean state);

}
