package cerberus.manager;

import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.swt.widget.ASWTWidget;

public interface ISWTGUIManager extends GeneralManager
{
	public ASWTWidget createWidget(final ManagerObjectType useWidgetType);
	
	/**
	 * Initialize Window.
	 *
	 */
	public void createApplicationWindow();
}
