package cerberus.manager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.swt.ISWTWidget;

public interface ISWTGUIManager extends IGeneralManager
{	
	public ISWTWidget createWidget(
			final ManagerObjectType uswWidgetType);
	
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
	
	public Shell createWindow();
	
	public Shell createWindow(
			int iUniqueId, 
			String sLabel, 
			String sLayoutAttributes);
	
	public void createComposite(
			int iUniqueId, 
			int iUniqueParentContainerId, 
			String sLayoutAttributes);

	public void runApplication();
	
	public void createLoadingProgressBar();
	
	/**
	 * Sets the percentag of the progress bar during the loading progress 
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
}
