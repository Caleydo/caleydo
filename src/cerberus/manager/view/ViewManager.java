package cerberus.manager.view;

import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.data.explorer.DataExplorerViewRep;
import cerberus.view.gui.swt.data.DataTableViewRep;
import cerberus.view.gui.swt.pathway.jgraph.PathwayViewRep;
import cerberus.view.gui.swt.progressbar.ProgressBarViewRep;
import cerberus.view.gui.swt.gears.jogl.GearsViewRep;
import cerberus.view.gui.swt.heatmap.jogl.SwtJogHistogram2DViewRep;
import cerberus.view.gui.swt.scatterplot.jogl.Scatterplot2DViewRep;
import cerberus.view.gui.swt.slider.SliderViewRep;
import cerberus.view.gui.swt.heatmap.jogl.Heatmap2DViewRep;
import cerberus.view.gui.swt.test.TestTableViewRep;

public class ViewManager 
extends AAbstractManager
implements IViewManager
{

	public ViewManager(IGeneralManager setGeneralManager)
	{
		super(setGeneralManager,
				IGeneralManager.iUniqueId_TypeOffset_GUI_AWT );

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

//	/**
//	 * Method creates a new ID and 
//	 * calls createView(ManagerObjectType useViewType, int iUniqueId).
//	 */
//	public IView createView(final ManagerObjectType useViewType)
//	{
//		final int iUniqueId = this.createNewId(useViewType);
//		
//		return createView(useViewType, iUniqueId);
//	}

	/**
	 * Method creates a new view representation according to the 
	 * type parameter.
	 */
	public IView createView(ManagerObjectType useViewType, int iViewId,
			int iParentContainerId, String sLabel)
	{
		if (useViewType.getGroupType() != ManagerType.VIEW)
		{
			throw new CerberusRuntimeException(
					"try to create object with wrong type "
							+ useViewType.name());
		}

		//final int iNewId = this.createNewId(useViewType);

		switch (useViewType)
		{
		case VIEW:

		case VIEW_SWT_PATHWAY:
			return new PathwayViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_DATA_EXPLORER:
			return new DataExplorerViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_PROGRESS_BAR:
			return new ProgressBarViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_TEST_TABLE:
			return new TestTableViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);	
		case VIEW_SWT_GEARS:
			return new GearsViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_HEATMAP2D:
		case VIEW_SWT_HISTOGRAM2D:
			return new SwtJogHistogram2DViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
			//return new Heatmap2DViewRep(iNewId, this.refGeneralManager);
		case VIEW_SWT_SCATTERPLOT2D:
			return new Scatterplot2DViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_SCATTERPLOT3D:
			return new Scatterplot2DViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_SLIDER:
			return new SliderViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);			

		default:
			throw new CerberusRuntimeException(
					"StorageManagerSimple.createView() failed due to unhandled type ["
							+ useViewType.toString() + "]");
		}
	}
}
