package cerberus.manager.gui;

import cerberus.manager.GeneralManager;
import cerberus.manager.SWTGUIManager;
import cerberus.manager.base.AbstractManagerImpl;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.Widget;

public class SWTGUIManagerSimple 
extends AbstractManagerImpl
implements SWTGUIManager
{

	public SWTGUIManagerSimple( GeneralManager setGeneralManager) 
	{	
		super( setGeneralManager );
		
		assert setGeneralManager != null : "Constructor with null-pointer to singelton";
		
		refGeneralManager.getSingelton().setSWTGUIManager( this );
			
	}

	public Widget createWdiget()
	{	
//		if (useWidgetType.getGroupType() != ManagerType.VIEW)
//		{
//			throw new CerberusRuntimeException(
//					"try to create object with wrong type "
//							+ useViewType.name());
//		}
//
//		final int iNewId = this.createNewId(useViewType);
//
//		switch (useViewType)
//		{
//		case VIEW:
//
//		case PATHWAY_VIEW:
//			return new PathwayViewRep(iNewId);
//
//		default:
//			throw new CerberusRuntimeException(
//					"StorageManagerSimple.createView() failed due to unhandled type ["
//							+ useViewType.toString() + "]");
//		}
		return null;
	}
	
	public boolean hasItem(int iItemId) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object getItem(int iItemId) {
		// TODO Auto-generated method stub
		return null;
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ManagerObjectType getManagerType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean registerItem(Object registerItem, int iItemId, ManagerObjectType type) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type) {
		// TODO Auto-generated method stub
		return false;
	}

	public int createNewId(ManagerObjectType setNewBaseType) {
		// TODO Auto-generated method stub
		return 0;
	}
}
