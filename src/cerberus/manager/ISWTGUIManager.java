package cerberus.manager;

import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.swt.ISWTWidget;

public interface ISWTGUIManager extends GeneralManager
{
	public ISWTWidget createWidget(final ManagerObjectType useWidgetType);
	
	/**
	 * Initialize Window.
	 *
	 */
	public void createApplicationWindow();
}
