package cerberus.manager;

import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.ViewInter;

public interface ViewManager extends GeneralManager 
{
	public ViewInter createView( final ManagerObjectType useViewType );
}