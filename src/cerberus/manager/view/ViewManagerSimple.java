package cerberus.manager.view;

import cerberus.manager.GeneralManager;
import cerberus.manager.ViewManager;
import cerberus.manager.base.AbstractManagerImpl;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.ViewInter;
import cerberus.view.gui.swt.data.explorer.DataExplorerViewRep;
import cerberus.view.gui.swt.data.DataTableViewRep;
import cerberus.view.gui.swt.data.set.SetTableViewRep;
import cerberus.view.gui.swt.pathway.jgraph.PathwayViewRep;
import cerberus.view.gui.swt.gears.jogl.GearsViewRep;
import cerberus.view.gui.swt.scatterplot.jogl.Scatterplot2DViewRep;
import cerberus.view.gui.swt.heatmap.jogl.Heatmap2DViewRep;
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
		final int iUniqueId = this.createNewId(useViewType);
		
		return createView(useViewType, iUniqueId);
	}

	public ViewInter createView(ManagerObjectType useViewType, int iUniqueId)
	{
		if (useViewType.getGroupType() != ManagerType.VIEW)
		{
			throw new CerberusRuntimeException(
					"try to create object with wrong type "
							+ useViewType.name());
		}

		switch (useViewType)
		{
		case VIEW:

		case VIEW_PATHWAY:
			return new PathwayViewRep(iUniqueId, this.refGeneralManager);
		case VIEW_SWT_DATA_EXPLORER:
			return new DataExplorerViewRep(iUniqueId, this.refGeneralManager);	
		case VIEW_TEST_TABLE:
			return new TestTableViewRep(iUniqueId, this.refGeneralManager);
		case VIEW_SWT_DATA_TABLE:
			return new DataTableViewRep(this.refGeneralManager);		
		case VIEW_SWT_SET_TABLE:
			return new SetTableViewRep(this.refGeneralManager);		
		case VIEW_SWT_GEARS:
			return new GearsViewRep(iUniqueId, this.refGeneralManager);
		case VIEW_SWT_HEATMAP2D:
			return new Heatmap2DViewRep(iUniqueId, this.refGeneralManager);
		case VIEW_SWT_SCATTERPLOT2D:
			return new Scatterplot2DViewRep(iUniqueId, this.refGeneralManager);
		case VIEW_SWT_SCATTERPLOT3D:
			return new Scatterplot2DViewRep(iUniqueId, this.refGeneralManager);			

		default:
			throw new CerberusRuntimeException(
					"StorageManagerSimple.createView() failed due to unhandled type ["
							+ useViewType.toString() + "]");
		}
	}
}
