package cerberus.manager;

import org.eclipse.swt.widgets.Shell;

import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.Widget;

public interface ISWTGUIManager extends GeneralManager
{
	public Widget createWidget(final ManagerObjectType useWidgetType);
	
	/**
	 * Initialize Window.
	 *
	 */
	public void createApplicationWindow();
}
