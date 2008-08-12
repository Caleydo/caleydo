package org.caleydo.core.manager;

import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.ISWTWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

public interface ISWTGUIManager
{

	public ISWTWidget createWidget(final EManagedObjectType useWidgetType,
			int iUniqueParentWindowId);

	public ISWTWidget createWidget(final EManagedObjectType useWidgetType,
			final Composite externalParentComposite);

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

	public void setLoadingProgressBarPercentage(int iPercentage);
	
	public void setLoadingProgressBarPercentageFromExternalThread(int iPercentage);
	
	public void setLoadingProgressBarText(String sText);
	
	public void setLoadingProgressBarTextFromExternalThread(final String sText);

	/**
	 * Show or hide the "global" progress bar.
	 * 
	 * @param state TRUE enables the progress bar and shows it; FALSE hides the
	 *            progressbar.
	 */
	public void setProgressbarVisible(final boolean state);
	
	public void setExternalProgressBarAndLabel(ProgressBar progressBar, Label progressLabel);

}
