package cerberus.manager;

import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.IView;

public interface IViewManager extends IGeneralManager 
{
//	public IView createView(final ManagerObjectType useViewType);
	
	public IView createView(final ManagerObjectType useViewType, 
			int iViewId, int iParentContainerId, String sLabel);
	
}