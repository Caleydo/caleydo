package cerberus.manager;

import org.eclipse.swt.widgets.Shell;

import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.swt.ISWTWidget;

public interface ISWTGUIManager extends IGeneralManager
{	
	public ISWTWidget createWidget(
			final ManagerObjectType uswWidgetType);
	
	public ISWTWidget createWidget(
			final ManagerObjectType useWidgetType, int iUniqueParentWindowId, int iWidth, int iHeight);
	
	public Shell createWindow();
	
	public Shell createWindow(int iUniqueId, String sLabel, String sLayoutAttributes);
	
	public void createComposite(
			int iUniqueId, int iUniqueParentContainerId, String sLayoutAttributes);
}
