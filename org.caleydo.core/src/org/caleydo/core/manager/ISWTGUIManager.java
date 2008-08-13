package org.caleydo.core.manager;

import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.ISWTWidget;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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

	public void setProgressBarPercentage(int iPercentage);
	public void setProgressBarPercentageFromExternalThread(int iPercentage);
	public void setProgressBarText(String sText);
	public void setProgressBarTextFromExternalThread(final String sText);
	public void setProgressBarVisible(final boolean state);
	public void setExternalProgressBarAndLabel(ProgressBar progressBar, Label progressLabel);

	public void setExternalRCPStatusLineMessage(final String sMessage);
	public void setExternalRCPStatusLine(IStatusLineManager statusLine, Display display);
}
