package org.caleydo.view.datagraph.datacontainer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class DataContainerListRenderer extends ADataContainerRenderer {

	private final static int SPACING_PIXELS = 4;
	private final static int MIN_COMP_GROUP_WIDTH_PIXELS = 16;
	private final static int MAX_TEXT_WIDTH_PIXELS = 80;
	private final static int TEXT_HEIGHT_PIXELS = 13;
	private final static int SIDE_SPACING_PIXELS = 20;

	private List<DimensionGroupRenderer> dimensionGroupRenderers;

	public DataContainerListRenderer(IDataGraphNode node, GLDataGraph view,
			DragAndDropController dragAndDropController,
			List<DataContainer> dataContainers) {
		super(node, view, dragAndDropController);

		dimensionGroupRenderers = new ArrayList<DimensionGroupRenderer>();
		setDataContainers(dataContainers);
		registerPickingListeners();
	}

	@Override
	public void createPickingListeners() {
//		view.addTypePickingListener(new APickingListener() {
//
//			@Override
//			public void clicked(Pick pick) {
//				DimensionGroupRenderer draggedComparisonGroupRepresentation = null;
//				int dimensionGroupID = pick.getID();
//
//				for (DimensionGroupRenderer comparisonGroupRepresentation : dimensionGroupRenderers) {
//					if (comparisonGroupRepresentation.getDataContainer()
//							.getID() == dimensionGroupID) {
//						draggedComparisonGroupRepresentation = comparisonGroupRepresentation;
//						break;
//					}
//				}
//				if (draggedComparisonGroupRepresentation == null)
//					return;
//
//				draggedComparisonGroupRepresentation
//						.setSelectionType(SelectionType.SELECTION);
//				Point point = pick.getPickedPoint();
//				dragAndDropController.clearDraggables();
//				dragAndDropController.setDraggingStartPosition(new Point(
//						point.x, point.y));
//				dragAndDropController
//						.addDraggable(draggedComparisonGroupRepresentation);
//				view.setDisplayListDirty();
//
//			}
//
//			@Override
//			public void mouseOver(Pick pick) {
//			}
//
//			@Override
//			public void dragged(Pick pick) {
//				if (!dragAndDropController.isDragging()) {
//					dragAndDropController.startDragging("DimensionGroupDrag");
//				}
//			}
//
//			@Override
//			public void rightClicked(Pick pick) {
//
//				int dimensionGroupID = pick.getID();
//				DataContainer dataContainer = null;
//
//				for (DimensionGroupRenderer dimensionGroupRenderer : dimensionGroupRenderers) {
//					if (dimensionGroupRenderer.getDataContainer().getID() == dimensionGroupID) {
//						dataContainer = dimensionGroupRenderer
//								.getDataContainer();
//						break;
//					}
//				}
//				if (dataContainer == null)
//					return;
//
//				IExtensionRegistry registry = Platform.getExtensionRegistry();
//
//				List<Pair<String, String>> viewTypes = new ArrayList<Pair<String, String>>();
//
//				IConfigurationElement[] viewElements = registry
//						.getConfigurationElementsFor("org.eclipse.ui.views");
//
//				IConfigurationElement[] categoryElements = registry
//						.getConfigurationElementsFor("org.caleydo.view.ViewCategory");
//
//				for (IConfigurationElement element : viewElements) {
//					try {
//						String bundleID = element.getAttribute("id");
//						if (bundleID.startsWith("org.caleydo.view.")) {
//
//							for (IConfigurationElement category : categoryElements) {
//
//								if (category.getAttribute("viewID").equals(
//										bundleID)
//										&& new Boolean(category
//												.getAttribute("isDataView"))) {
//
//									int indexOfLastDot = -1;
//									for (int i = 0; i < 4; i++) {
//										indexOfLastDot = bundleID.indexOf('.',
//												indexOfLastDot + 1);
//									}
//
//									bundleID = (indexOfLastDot == -1) ? (bundleID)
//											: (bundleID.substring(0,
//													indexOfLastDot));
//
//									Bundle bundle = Platform
//											.getBundle(bundleID);
//									if (bundle != null) {
//										bundle.start();
//										viewTypes.add(new Pair<String, String>(
//												element.getAttribute("name"),
//												element.getAttribute("id")));
//									}
//								}
//							}
//						}
//					} catch (BundleException e) {
//						e.printStackTrace();
//					}
//				}
//
//				Set<String> validViewIDs = DataDomainManager
//						.get()
//						.getAssociationManager()
//						.getViewTypesForDataDomain(
//								dataContainer.getDataDomain()
//										.getDataDomainType());
//
//				List<Pair<String, String>> finalViewTypes = new ArrayList<Pair<String, String>>();
//
//				for (String viewID : validViewIDs) {
//					for (Pair<String, String> viewType : viewTypes) {
//						if (viewID.equals(viewType.getSecond())) {
//							finalViewTypes.add(viewType);
//						}
//					}
//				}
//
//				Collections.sort(finalViewTypes);
//
//				List<CreateViewItem> createViewItems = new ArrayList<CreateViewItem>();
//
//				for (Pair<String, String> viewType : viewTypes) {
//					createViewItems.add(new CreateViewItem(viewType.getFirst(),
//							viewType.getSecond(),
//							dataContainer.getDataDomain(), dataContainer));
//				}
//
//				if (createViewItems.size() > 0) {
//					view.getContextMenuCreator().addContextMenuItem(
//							new ShowDataContainerInViewsItem(createViewItems));
//				}
//
//				Set<ViewNode> viewNodes = view.getViewNodes();
//
//				if (viewNodes != null) {
//					for (ViewNode node : viewNodes) {
//						if (node.getRepresentedView() instanceof GLVisBricks) {
//							view.getContextMenuCreator().addContextMenuItem(
//									new AddGroupToVisBricksItem(
//											(GLVisBricks) node
//													.getRepresentedView(),
//											dataContainer));
//						}
//					}
//				}
//			}
//
//		}, DIMENSION_GROUP_PICKING_TYPE + node.getID());
		
		view.addTypePickingListener(new DataContainerPickingListener(view,
				dragAndDropController, this), DIMENSION_GROUP_PICKING_TYPE + node.getID());
	}

	@Override
	public void setDataContainers(List<DataContainer> dataContainers) {
		dimensionGroupRenderers.clear();
		for (DataContainer dataContainer : dataContainers) {
			float[] color = dataContainer.getDataDomain().getColor().getRGBA();

			DimensionGroupRenderer dimensionGroupRenderer = new DimensionGroupRenderer(
					dataContainer, view, dragAndDropController, node, color);
			dimensionGroupRenderer.setTextHeightPixels(TEXT_HEIGHT_PIXELS);
			dimensionGroupRenderer.setUpsideDown(isUpsideDown);
			dimensionGroupRenderers.add(dimensionGroupRenderer);
		}
	}

	@Override
	public void render(GL2 gl) {

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
		// CaleydoTextRenderer textRenderer = view.getTextRenderer();
		// float dimensionGroupWidth = (x -
		// pixelGLConverter.getGLWidthForPixelWidth(2
		// * SIDE_SPACING_PIXELS + (node.getDataContainers().size() - 1)
		// * SPACING_PIXELS))
		// / (float) node.getDataContainers().size();

		float dimensionGroupWidth = pixelGLConverter
				.getGLWidthForPixelWidth(MIN_COMP_GROUP_WIDTH_PIXELS);

		// float currentPosX =
		// pixelGLConverter.getGLWidthForPixelWidth(SIDE_SPACING_PIXELS);
		float currentPosX = (x / 2.0f)
				- pixelGLConverter
						.getGLWidthForPixelWidth(getDimensionGroupsWidthPixels()
								/ 2 - SIDE_SPACING_PIXELS);
		float step = pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS)
				+ dimensionGroupWidth;

		bottomDimensionGroupPositions.clear();
		topDimensionGroupPositions.clear();

		for (DimensionGroupRenderer dimensionGroupRenderer : dimensionGroupRenderers) {
			// float currentDimGroupWidth = pixelGLConverter
			// .getGLWidthForPixelWidth(MIN_COMP_GROUP_WIDTH_PIXELS);

			int pickingID = view.getPickingManager().getPickingID(view.getID(),
					DIMENSION_GROUP_PICKING_TYPE + node.getID(),
					dimensionGroupRenderer.getDataContainer().getID());

			gl.glPushName(pickingID);
			if (pickingIDsToBePushed != null) {
				for (Pair<String, Integer> pickingPair : pickingIDsToBePushed) {
					gl.glPushName(view.getPickingManager().getPickingID(
							view.getID(), pickingPair.getFirst(),
							pickingPair.getSecond()));
				}
			}

			dimensionGroupRenderer.setLimits(dimensionGroupWidth, y);
			gl.glPushMatrix();
			gl.glTranslatef(currentPosX, 0, 0);

			dimensionGroupRenderer.render(gl);
			gl.glPopMatrix();

			gl.glPopName();
			if (pickingIDsToBePushed != null) {
				for (int i = 0; i < pickingIDsToBePushed.size(); i++) {
					gl.glPopName();
				}
			}

			Point2D bottomPosition1 = new Point2D.Float(currentPosX, 0);
			Point2D bottomPosition2 = new Point2D.Float(currentPosX
					+ dimensionGroupWidth, 0);
			Point2D topPosition1 = new Point2D.Float(currentPosX, y);
			Point2D topPosition2 = new Point2D.Float(currentPosX
					+ dimensionGroupWidth, y);
			bottomDimensionGroupPositions.put(dimensionGroupRenderer
					.getDataContainer().getID(), new Pair<Point2D, Point2D>(
					bottomPosition1, bottomPosition2));
			topDimensionGroupPositions.put(dimensionGroupRenderer
					.getDataContainer().getID(), new Pair<Point2D, Point2D>(
					topPosition1, topPosition2));

			currentPosX += step;
		}

	}

	@Override
	public int getMinWidthPixels() {
		return getDimensionGroupsWidthPixels();
	}

	@Override
	public int getMinHeightPixels() {
		return getMaxDimensionGroupLabelHeight();
	}

	private int getDimensionGroupsWidthPixels() {
		return (node.getDataContainers().size() * MIN_COMP_GROUP_WIDTH_PIXELS)
				+ ((node.getDataContainers().size() - 1) * SPACING_PIXELS) + 2
				* SIDE_SPACING_PIXELS;
	}

	private int getMaxDimensionGroupLabelHeight() {

		CaleydoTextRenderer textRenderer = view.getTextRenderer();
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		float maxTextWidth = Float.MIN_VALUE;

		for (DataContainer dataContainer : node.getDataContainers()) {
			float textWidth = textRenderer.getRequiredTextWidthWithMax(
					dataContainer.getLabel(), pixelGLConverter
							.getGLHeightForPixelHeight(TEXT_HEIGHT_PIXELS),
					pixelGLConverter
							.getGLWidthForPixelWidth(MAX_TEXT_WIDTH_PIXELS));
			if (textWidth > maxTextWidth)
				maxTextWidth = textWidth;
		}

		return pixelGLConverter.getPixelHeightForGLHeight(maxTextWidth);

	}

	@Override
	public void setUpsideDown(boolean isUpsideDown) {
		this.isUpsideDown = isUpsideDown;

		for (DimensionGroupRenderer renderer : dimensionGroupRenderers) {
			renderer.setUpsideDown(isUpsideDown);
		}

	}

	@Override
	public void removePickingListeners() {
		view.removeAllTypePickingListeners(DIMENSION_GROUP_PICKING_TYPE
				+ node.getID());

	}

	@Override
	protected Collection<DimensionGroupRenderer> getDimensionGroupRenderers() {
		return dimensionGroupRenderers;
	}

}
