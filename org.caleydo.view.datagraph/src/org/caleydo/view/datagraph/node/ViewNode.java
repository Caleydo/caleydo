package org.caleydo.view.datagraph.node;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.BorderedAreaRenderer;
import org.caleydo.core.view.opengl.layout.util.TextureRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.view.datagraph.ForceDirectedGraphLayout;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.ViewNodeBackGroundRenderer;
import org.caleydo.view.datagraph.contextmenu.OpenViewItem;
import org.caleydo.view.datagraph.datacontainer.ADataContainerRenderer;
import org.caleydo.view.datagraph.datacontainer.DimensionGroupRenderer;
import org.caleydo.view.datagraph.datacontainer.DataContainerListRenderer;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ViewNode extends ADefaultTemplateNode implements IDropArea {

	private DataContainerListRenderer overviewDataContainerRenderer;
	private AGLView representedView;
	private Set<IDataDomain> dataDomains;
	private List<ADimensionGroupData> dimensionGroups;
	private String viewName;
	private String iconPath;

	public ViewNode(ForceDirectedGraphLayout graphLayout, GLDataGraph view,
			DragAndDropController dragAndDropController, Integer id,
			AGLView representedView) {
		super(graphLayout, view, dragAndDropController, id);

		// dimensionGroups = new ArrayList<ADimensionGroupData>();
		// dimensionGroups.add(new FakeDimensionGroupData(0));
		// dimensionGroups.add(new FakeDimensionGroupData(1));
		// dimensionGroups.add(new FakeDimensionGroupData(2));
		// dimensionGroups.add(new FakeDimensionGroupData(5));
		// dimensionGroups.add(new FakeDimensionGroupData(4));

		this.representedView = representedView;

		registerPickingListeners();
		setRepresentedViewInfo();
		setupLayout();
	}

	private void registerPickingListeners() {
		// TODO: this is not nice

		view.addSingleIDPickingListener(new APickingListener() {

			@Override
			public void rightClicked(Pick pick) {
				view.getContextMenuCreator().addContextMenuItem(
						new OpenViewItem(representedView));
			}

			@Override
			public void doubleClicked(Pick pick) {
				view.openView(representedView);
			}

			@Override
			public void dragged(Pick pick) {
				if (representedView instanceof GLVisBricks) {
					DragAndDropController dragAndDropController = ViewNode.this.dragAndDropController;
					if (dragAndDropController.isDragging()
							&& dragAndDropController.getDraggingMode().equals(
									"DimensionGroupDrag")) {
						dragAndDropController.setDropArea(ViewNode.this);
					}
				}
			}
		}, PickingType.DATA_GRAPH_NODE.name(), id);

	}

	private void setRepresentedViewInfo() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry
				.getExtensionPoint("org.eclipse.ui.views");
		IExtension[] extensions = point.getExtensions();
		String viewID = representedView.getViewType();
		viewName = viewID;
		iconPath = null;
		boolean viewNameObtained = false;

		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension
					.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getAttribute("id").equals(viewID)) {
					viewName = element.getAttribute("name");
					iconPath = element.getAttribute("icon");
					viewNameObtained = true;
					break;

				}
			}
			if (viewNameObtained) {
				break;
			}
		}

		if (iconPath.equals("")) {
			iconPath = null;
		}
		if (iconPath != null) {
			ClassLoader classLoader = representedView.getClass()
					.getClassLoader();
			URL url = classLoader.getResource(iconPath);
			try {
				url = FileLocator.resolve(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
			iconPath = new File(url.getFile()).getAbsolutePath();
		}
	}

	@Override
	protected ElementLayout setupLayout() {
		Row baseRow = createDefaultBaseRow(BorderedAreaRenderer.DEFAULT_COLOR,
				id);

		ElementLayout spacingLayoutX = createDefaultSpacingX();

		baseColumn = new Column();
		baseColumn.setPixelGLConverter(pixelGLConverter);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		Row titleRow = new Row("titleRow");
		titleRow.setYDynamic(true);

		if (iconPath != null) {
			ElementLayout iconLayout = new ElementLayout("icon");
			iconLayout.setPixelGLConverter(pixelGLConverter);
			iconLayout.setPixelSizeX(CAPTION_HEIGHT_PIXELS);
			iconLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
			iconLayout.setRenderer(new TextureRenderer(iconPath, view
					.getTextureManager(), true));
			titleRow.append(iconLayout);
			titleRow.append(spacingLayoutX);
		}

		ElementLayout captionLayout = createDefaultCaptionLayout(viewName, id);

		titleRow.append(captionLayout);

		ElementLayout lineSeparatorLayout = createDefaultLineSeparatorLayout();

		Row bodyRow = new Row("bodyRow");
		bodyRow.addBackgroundRenderer(new ViewNodeBackGroundRenderer(
				new float[] { 1, 1, 1, 1 }, iconPath, view.getTextureManager(),
				true));

		bodyColumn = new Column("bodyColumn");

		ElementLayout compGroupLayout = new ElementLayout("compGroupOverview");
		overviewDataContainerRenderer = new DataContainerListRenderer(this,
				view, dragAndDropController, getDimensionGroups());
		compGroupLayout.setRatioSizeY(1);
		compGroupLayout.setRenderer(overviewDataContainerRenderer);

		ElementLayout spacingLayoutY = createDefaultSpacingY();

		bodyColumn.append(compGroupLayout);
		bodyColumn.append(spacingLayoutY);

		bodyRow.append(bodyColumn);

		baseColumn.append(spacingLayoutY);
		baseColumn.append(bodyRow);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(lineSeparatorLayout);
		baseColumn.append(titleRow);
		baseColumn.append(spacingLayoutY);
		
		setUpsideDown(isUpsideDown);

		return baseRow;
	}

	@Override
	public List<ADimensionGroupData> getDimensionGroups() {
		List<ADimensionGroupData> groups = representedView.getDimensionGroups();
		if (groups == null) {
			groups = new ArrayList<ADimensionGroupData>();
		}

		// List<ADimensionGroupData> groups = new
		// ArrayList<ADimensionGroupData>();
		// FakeDimensionGroupData data = new FakeDimensionGroupData(0);
		// data.setDimensionPerspectiveID("ColumnPerspec2");
		// data.setRecordPerspectiveID("Row1");
		// groups.add(data);
		//
		// data = new FakeDimensionGroupData(1);
		// data.setDimensionPerspectiveID("ColumnPerspec2");
		// data.setRecordPerspectiveID("AnotherRow");
		// groups.add(data);
		//
		// data = new FakeDimensionGroupData(2);
		// data.setDimensionPerspectiveID("ColumnPerspec2");
		// data.setRecordPerspectiveID("YetAnotherRow");
		// groups.add(data);
		//
		// data = new FakeDimensionGroupData(3);
		// data.setDimensionPerspectiveID("ColumnPerspec2");
		// data.setRecordPerspectiveID("RowPerspec2");
		// groups.add(data);
		//
		// data = new FakeDimensionGroupData(4);
		// data.setDimensionPerspectiveID("AnotherColumn2");
		// data.setRecordPerspectiveID("Row1");
		// groups.add(data);
		//
		// data = new FakeDimensionGroupData(5);
		// data.setDimensionPerspectiveID("YetAnotherColumn2");
		// data.setRecordPerspectiveID("YetAnotherRow");
		// groups.add(data);

		return groups;
	}

	// @Override
	// public void render(GL2 gl) {
	// Point2D position = graphLayout.getNodePosition(this, true);
	// float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
	// .getX());
	// float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
	// .getY());
	// float spacingWidth = pixelGLConverter
	// .getGLWidthForPixelWidth(getWidthPixels());
	// float spacingHeight = pixelGLConverter
	// .getGLHeightForPixelHeight(getHeightPixels());
	// gl.glPushMatrix();
	// gl.glTranslatef(x - spacingWidth / 2.0f, y - spacingHeight / 2.0f, 0f);
	//
	// // layoutManager.setViewFrustum(new ViewFrustum(
	// // ECameraProjectionMode.ORTHOGRAPHIC, x - spacingWidth, x
	// // + spacingWidth, y - spacingHeight, y + spacingHeight,
	// // -1, 20));
	// layoutManager.setViewFrustum(new ViewFrustum(
	// CameraProjectionMode.ORTHOGRAPHIC, 0, spacingWidth, 0,
	// spacingHeight, -1, 20));
	//
	// layoutManager.render(gl);
	// gl.glPopMatrix();
	// }
	//
	// @Override
	// public int getHeightPixels() {
	// return 4 * SPACING_PIXELS + CAPTION_HEIGHT_PIXELS
	// + LINE_SEPARATOR_HEIGHT_PIXELS
	// + Math.max(MIN_OVERVIEW_COMP_GROUP_HEIGHT_PIXELS,
	// overviewDataContainerRenderer.getMinHeightPixels());
	// }
	//
	// @Override
	// public int getWidthPixels() {
	// return 2 * SPACING_PIXELS
	// + Math.max(200, overviewDataContainerRenderer.getMinWidthPixels());
	// }
	//
	//
	// @Override
	// public Pair<Point2D, Point2D> getBottomDimensionGroupAnchorPoints(
	// ADimensionGroupData dimensionGroup) {
	// Point2D position = graphLayout.getNodePosition(this, true);
	// float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
	// .getX());
	// float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
	// .getY());
	// float width = pixelGLConverter
	// .getGLWidthForPixelWidth(getWidthPixels());
	// float height = pixelGLConverter
	// .getGLHeightForPixelHeight(getHeightPixels());
	// float spacingX = pixelGLConverter
	// .getGLWidthForPixelWidth(SPACING_PIXELS);
	// float spacingY = pixelGLConverter
	// .getGLHeightForPixelHeight(SPACING_PIXELS);
	//
	// Pair<Point2D, Point2D> anchorPoints = overviewDataContainerRenderer
	// .getAnchorPointsOfDimensionGroup(dimensionGroup);
	//
	// Point2D first = (Point2D) anchorPoints.getFirst().clone();
	// Point2D second = (Point2D) anchorPoints.getSecond().clone();
	//
	// first.setLocation(anchorPoints.getFirst().getX() + x - width / 2.0f
	// + spacingX, anchorPoints.getFirst().getY() + y - height / 2.0f
	// + spacingY);
	// second.setLocation(anchorPoints.getSecond().getX() + x - width / 2.0f
	// + spacingX, anchorPoints.getSecond().getY() + y - height / 2.0f
	// + spacingY);
	//
	// return new Pair<Point2D, Point2D>(first, second);
	// }
	//
	// @Override
	// public Pair<Point2D, Point2D> getTopAnchorPoints() {
	// Point2D position = graphLayout.getNodePosition(this, true);
	// float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
	// .getX());
	// float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
	// .getY());
	// float width = pixelGLConverter
	// .getGLWidthForPixelWidth(getWidthPixels());
	// float height = pixelGLConverter
	// .getGLHeightForPixelHeight(getHeightPixels());
	//
	// Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();
	//
	// anchorPoints.setFirst(new Point2D.Float(x - width / 2.0f, y + height
	// / 2.0f));
	// anchorPoints.setSecond(new Point2D.Float(x + width / 2.0f, y + height
	// / 2.0f));
	//
	// return anchorPoints;
	// }
	//
	// @Override
	// public Pair<Point2D, Point2D> getBottomAnchorPoints() {
	// Point2D position = graphLayout.getNodePosition(this, true);
	// float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
	// .getX());
	// float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
	// .getY());
	// float width = pixelGLConverter
	// .getGLWidthForPixelWidth(getWidthPixels());
	// float height = pixelGLConverter
	// .getGLHeightForPixelHeight(getHeightPixels());
	//
	// Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();
	//
	// anchorPoints.setFirst(new Point2D.Float(x - width / 2.0f, y - height
	// / 2.0f));
	// anchorPoints.setSecond(new Point2D.Float(x + width / 2.0f, y - height
	// / 2.0f));
	//
	// return anchorPoints;
	// }

	public void setDataDomains(Set<IDataDomain> dataDomains) {
		this.dataDomains = dataDomains;
	}

	public Set<IDataDomain> getDataDomains() {
		return dataDomains;
	}

	// @Override
	// public Pair<Point2D, Point2D> getLeftAnchorPoints() {
	// Point2D position = graphLayout.getNodePosition(this, true);
	// float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
	// .getX());
	// float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
	// .getY());
	// float width = pixelGLConverter
	// .getGLWidthForPixelWidth(getWidthPixels());
	// float height = pixelGLConverter
	// .getGLHeightForPixelHeight(getHeightPixels());
	//
	// Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();
	//
	// anchorPoints.setFirst(new Point2D.Float(x - width / 2.0f, y + height
	// / 2.0f));
	// anchorPoints.setSecond(new Point2D.Float(x - width / 2.0f, y - height
	// / 2.0f));
	//
	// return anchorPoints;
	// }
	//
	// @Override
	// public Pair<Point2D, Point2D> getRightAnchorPoints() {
	// Point2D position = graphLayout.getNodePosition(this, true);
	// float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
	// .getX());
	// float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
	// .getY());
	// float width = pixelGLConverter
	// .getGLWidthForPixelWidth(getWidthPixels());
	// float height = pixelGLConverter
	// .getGLHeightForPixelHeight(getHeightPixels());
	//
	// Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();
	//
	// anchorPoints.setFirst(new Point2D.Float(x + width / 2.0f, y + height
	// / 2.0f));
	// anchorPoints.setSecond(new Point2D.Float(x + width / 2.0f, y - height
	// / 2.0f));
	//
	// return anchorPoints;
	// }
	//
	// @Override
	// public Point2D getPosition() {
	// Point2D position = graphLayout.getNodePosition(this, true);
	// float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
	// .getX());
	// float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
	// .getY());
	// return new Point2D.Float(x, y);
	// }
	//
	// @Override
	// public float getHeight() {
	// return pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());
	// }
	//
	// @Override
	// public float getWidth() {
	// return pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
	// }

	public AGLView getRepresentedView() {
		return representedView;
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY,
			DragAndDropController dragAndDropController) {
		ArrayList<ADimensionGroupData> dimensionGroupData = new ArrayList<ADimensionGroupData>();
		for (IDraggable draggable : draggables) {
			if (draggable instanceof DimensionGroupRenderer) {
				DimensionGroupRenderer comparisonGroupRepresentation = (DimensionGroupRenderer) draggable;
				dimensionGroupData.add(comparisonGroupRepresentation
						.getDimensionGroupData());
			}
		}

		if (!dimensionGroupData.isEmpty()) {
			AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent();
			event.setDimensionGroupData(dimensionGroupData);
			event.setSender(this);
			GeneralManager.get().getEventPublisher().triggerEvent(event);
		}

		dragAndDropController.clearDraggables();

	}

	@Override
	public void update() {
		overviewDataContainerRenderer.setDimensionGroups(getDimensionGroups());
	}

	@Override
	protected ADataContainerRenderer getDataContainerRenderer() {
		return overviewDataContainerRenderer;
	}

	@Override
	public void destroy() {
		super.destroy();
		overviewDataContainerRenderer.destroy();
		view.removeSingleIDPickingListeners(PickingType.DATA_GRAPH_NODE.name(),
				id);
	}

}
