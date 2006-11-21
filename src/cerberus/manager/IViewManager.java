package cerberus.manager;

import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.data.explorer.DataExplorerViewRep;

public interface IViewManager 
extends IGeneralManager {
	
	public IView createView(final ManagerObjectType useViewType, 
			int iViewId, int iParentContainerId, String sLabel);
	
	public void destroyOnExit();

	public void addDataExplorerViewRep(
			DataExplorerViewRep refDataExplorerView);
}