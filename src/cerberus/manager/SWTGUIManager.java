package cerberus.manager;

import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.Widget;

public interface SWTGUIManager extends GeneralManager
{
	public Widget createWidget(final ManagerObjectType useWidgetType);
	
	/**
	 * Initialize Window.
	 *
	 */
	public void createApplicationWindow();
}
