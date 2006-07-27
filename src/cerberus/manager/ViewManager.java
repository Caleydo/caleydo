package cerberus.manager;

import cerberus.manager.type.ManagerObjectType;


public interface ViewManager extends GeneralManager 
{
	public void createView( final ManagerObjectType useViewType );
}