package org.caleydo.view.datagraph.datacontainer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.datadomain.pathway.data.PathwayDataContainer;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.contextmenu.AddGroupToVisBricksItem;
import org.caleydo.view.datagraph.contextmenu.CreateViewItem;
import org.caleydo.view.datagraph.contextmenu.ShowDataContainerInViewsItem;
import org.caleydo.view.datagraph.node.ViewNode;
import org.caleydo.view.visbricks.GLVisBricks;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class DataContainerPickingListener
	extends APickingListener
{

	private GLDataGraph view;
	private DragAndDropController dragAndDropController;
	private ADataContainerRenderer dataContainerRenderer;

	public DataContainerPickingListener(GLDataGraph view,
			DragAndDropController dragAndDropController,
			ADataContainerRenderer dataContainerRenderer)
	{
		this.view = view;
		this.dragAndDropController = dragAndDropController;
		this.dataContainerRenderer = dataContainerRenderer;
	}

	private DimensionGroupRenderer getDimensionGroupRenderer(int id)
	{

		Collection<DimensionGroupRenderer> dimensionGroupRenderers = dataContainerRenderer
				.getDimensionGroupRenderers();

		for (DimensionGroupRenderer dimensionGroupRenderer : dimensionGroupRenderers)
		{
			if (dimensionGroupRenderer.getDataContainer().getID() == id)
			{
				return dimensionGroupRenderer;
			}
		}
		return null;
	}

	@Override
	public void clicked(Pick pick)
	{
		int dimensionGroupID = pick.getID();

		DimensionGroupRenderer draggedComparisonGroupRenderer = getDimensionGroupRenderer(dimensionGroupID);
		if (draggedComparisonGroupRenderer == null)
			return;
		//
		// draggedComparisonGroupRenderer
		// .setSelectionType(SelectionType.SELECTION);
		Point point = pick.getPickedPoint();
		dragAndDropController.clearDraggables();
		dragAndDropController.setDraggingStartPosition(new Point(point.x, point.y));
		dragAndDropController.addDraggable(draggedComparisonGroupRenderer);
		dragAndDropController.setDraggingMode("DimensionGroupDrag");
		view.setDisplayListDirty();

	}

	@Override
	public void mouseOver(Pick pick)
	{
		DimensionGroupRenderer dimensionGroupRenderer = getDimensionGroupRenderer(pick.getID());
		if (dimensionGroupRenderer == null)
			return;

		dimensionGroupRenderer.setColor(dimensionGroupRenderer.getBorderColor());
		view.setDisplayListDirty();
	}

	@Override
	public void mouseOut(Pick pick)
	{
		DimensionGroupRenderer dimensionGroupRenderer = getDimensionGroupRenderer(pick.getID());
		if (dimensionGroupRenderer == null)
			return;

		DataContainer dataContainer = dimensionGroupRenderer.getDataContainer();

		float[] color = dataContainer.getDataDomain().getColor().getRGBA();

		if (dataContainer instanceof PathwayDataContainer)
		{
			color = ((PathwayDataContainer) dataContainer).getPathwayDataDomain().getColor()
					.getRGBA();
		}

		dimensionGroupRenderer.setColor(color);
		view.setDisplayListDirty();
	}

	@Override
	public void dragged(Pick pick)
	{
		String draggingMode = dragAndDropController.getDraggingMode();

		if (!dragAndDropController.isDragging() && dragAndDropController.hasDraggables()
				&& draggingMode != null && draggingMode.equals("DimensionGroupDrag"))
		{
			dragAndDropController.startDragging();
		}
	}

	@Override
	public void rightClicked(Pick pick)
	{

		int dimensionGroupID = pick.getID();
		DimensionGroupRenderer dimensionGroupRenderer = getDimensionGroupRenderer(dimensionGroupID);
		if (dimensionGroupRenderer == null)
			return;
		DataContainer dataContainer = dimensionGroupRenderer.getDataContainer();

		// for (DimensionGroupRenderer dimensionGroupRenderer :
		// dimensionGroupRenderers) {
		// if (dimensionGroupRenderer.getDataContainer().getID() ==
		// dimensionGroupID) {
		// dataContainer = dimensionGroupRenderer.getDataContainer();
		// break;
		// }
		// }
		// if (dataContainer == null)
		// return;

		IExtensionRegistry registry = Platform.getExtensionRegistry();

		List<Pair<String, String>> viewTypes = new ArrayList<Pair<String, String>>();

		IConfigurationElement[] viewElements = registry
				.getConfigurationElementsFor("org.eclipse.ui.views");

		IConfigurationElement[] categoryElements = registry
				.getConfigurationElementsFor("org.caleydo.view.ViewCategory");

		for (IConfigurationElement element : viewElements)
		{
			try
			{
				String bundleID = element.getAttribute("id");
				if (bundleID.startsWith("org.caleydo.view."))
				{

					for (IConfigurationElement category : categoryElements)
					{

						if (category.getAttribute("viewID").equals(bundleID)
								&& new Boolean(category.getAttribute("isDataView")))
						{

							int indexOfLastDot = -1;
							for (int i = 0; i < 4; i++)
							{
								indexOfLastDot = bundleID.indexOf('.', indexOfLastDot + 1);
							}

							bundleID = (indexOfLastDot == -1) ? (bundleID) : (bundleID
									.substring(0, indexOfLastDot));

							Bundle bundle = Platform.getBundle(bundleID);
							if (bundle != null)
							{
								bundle.start();
								viewTypes.add(new Pair<String, String>(element
										.getAttribute("name"), element.getAttribute("id")));
							}
						}
					}
				}
			}
			catch (BundleException e)
			{
				e.printStackTrace();
			}
		}

		Set<String> validViewIDs = DataDomainManager.get().getAssociationManager()
				.getViewTypesForDataDomain(dataContainer.getDataDomain().getDataDomainType());

		List<Pair<String, String>> finalViewTypes = new ArrayList<Pair<String, String>>();

		for (String viewID : validViewIDs)
		{
			for (Pair<String, String> viewType : viewTypes)
			{
				if (viewID.equals(viewType.getSecond()))
				{
					finalViewTypes.add(viewType);
				}
			}
		}

		Collections.sort(finalViewTypes);

		List<CreateViewItem> createViewItems = new ArrayList<CreateViewItem>();

		for (Pair<String, String> viewType : viewTypes)
		{
			createViewItems.add(new CreateViewItem(viewType.getFirst(), viewType.getSecond(),
					dataContainer.getDataDomain(), dataContainer));
		}

		if (createViewItems.size() > 0)
		{
			view.getContextMenuCreator().addContextMenuItem(
					new ShowDataContainerInViewsItem(createViewItems));
		}

		Set<ViewNode> viewNodes = view.getViewNodes();

		if (viewNodes != null)
		{
			for (ViewNode node : viewNodes)
			{
				if (node.getRepresentedView() instanceof GLVisBricks)
				{
					view.getContextMenuCreator().addContextMenuItem(
							new AddGroupToVisBricksItem((GLVisBricks) node
									.getRepresentedView(), dataContainer));
				}
			}
		}
	}

}
