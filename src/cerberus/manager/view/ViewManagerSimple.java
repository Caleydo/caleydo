package cerberus.manager.view;

import cerberus.manager.GeneralManager;
import cerberus.manager.ViewManager;
import cerberus.manager.base.AbstractManagerImpl;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.ViewInter;
import cerberus.view.gui.swt.data.explorer.DataExplorerViewRep;
import cerberus.view.gui.swt.data.selection.SelectionTableViewRep;
import cerberus.view.gui.swt.data.set.SetTableViewRep;
import cerberus.view.gui.swt.data.storage.StorageTableViewRep;
import cerberus.view.gui.swt.gears.jogl.GearsViewRep;
import cerberus.view.gui.swt.pathway.jgraph.PathwayViewRep;
import cerberus.view.gui.swt.test.TestTableViewRep;

public class ViewManagerSimple extends AbstractManagerImpl implements
		ViewManager
{

	public ViewManagerSimple(GeneralManager setGeneralManager)
	{
		super(setGeneralManager,
				GeneralManager.iUniqueId_TypeOffset_GuiAWT );

		assert setGeneralManager != null : "Constructor with null-pointer to singelton";

		refGeneralManager.getSingelton().setViewManager(this);

	}

	public boolean hasItem(int iItemId)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Object getItem(int iItemId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int size()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public ManagerObjectType getManagerType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean registerItem(Object registerItem, int iItemId,
			ManagerObjectType type)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public ViewInter createView(final ManagerObjectType useViewType)
	{
		if (useViewType.getGroupType() != ManagerType.VIEW)
		{
			throw new CerberusRuntimeException(
					"try to create object with wrong type "
							+ useViewType.name());
		}

		final int iNewId = this.createNewId(useViewType);

		switch (useViewType)
		{
		case VIEW:

		case VIEW_PATHWAY:
			return new PathwayViewRep(iNewId, this.refGeneralManager);
		case VIEW_DATA_EXPLORER:
			return new DataExplorerViewRep(iNewId, this.refGeneralManager);	
		case VIEW_TEST_TABLE:
			return new TestTableViewRep(iNewId, this.refGeneralManager);
		case VIEW_SET_TABLE:
			return new SetTableViewRep(iNewId, this.refGeneralManager);
		case VIEW_STORAGE_TABLE:
			return new StorageTableViewRep(iNewId, this.refGeneralManager);
		case VIEW_SELECTION_TABLE:
			return new SelectionTableViewRep(iNewId, this.refGeneralManager);		
		case VIEW_GEARS:
			return new GearsViewRep(iNewId, this.refGeneralManager);

		default:
			throw new CerberusRuntimeException(
					"StorageManagerSimple.createView() failed due to unhandled type ["
							+ useViewType.toString() + "]");
		}
	}
}
