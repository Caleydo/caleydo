package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.BorderedAreaRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.layout.util.LineSeparatorRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;

public class ViewNode extends ADraggableDataGraphNode implements IDropArea {

	private final static int SPACING_PIXELS = 4;
	private final static int CAPTION_HEIGHT_PIXELS = 16;
	private final static int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
	private final static int OVERVIEW_COMP_GROUP_HEIGHT_PIXELS = 32;

	private LayoutManager layoutManager;
	private ComparisonGroupOverviewRenderer compGroupOverviewRenderer;
	private AGLView representedView;
	private Set<IDataDomain> dataDomains;

	public ViewNode(ForceDirectedGraphLayout graphLayout, GLDataGraph view,
			DragAndDropController dragAndDropController, int id,
			AGLView representedView) {
		super(graphLayout, view, dragAndDropController, id);

		this.representedView = representedView;

		// TODO: this is not nice
		if (representedView instanceof GLVisBricks) {
			view.addSingleIDPickingListener(new APickingListener() {

				@Override
				public void dragged(Pick pick) {
					DragAndDropController dragAndDropController = ViewNode.this.dragAndDropController;
					if (dragAndDropController.isDragging()
							&& dragAndDropController.getDraggingMode().equals(
									"DimensionGroupDrag")) {
						dragAndDropController.setDropArea(ViewNode.this);
					}
				}
			}, PickingType.DATA_GRAPH_NODE.name(), id);
		}

		setupLayout();
	}

	private void setupLayout() {
		layoutManager = new LayoutManager(new ViewFrustum());
		LayoutTemplate layoutTemplate = new LayoutTemplate();

		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);

		baseRow.setRenderer(new BorderedAreaRenderer(view,
				PickingType.DATA_GRAPH_NODE, id));

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		Column baseColumn = new Column();
		baseColumn.setPixelGLConverter(pixelGLConverter);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		ElementLayout captionLayout = new ElementLayout("caption");
		captionLayout.setPixelGLConverter(pixelGLConverter);
		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setRatioSizeX(1);
		captionLayout.setRenderer(new LabelRenderer(view, representedView
				.getViewType(), PickingType.DATA_GRAPH_NODE, id));

		ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");
		lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
		lineSeparatorLayout.setPixelSizeY(LINE_SEPARATOR_HEIGHT_PIXELS);
		lineSeparatorLayout.setRatioSizeX(1);
		lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(false));

		ElementLayout compGroupLayout = new ElementLayout("compGroupOverview");
		compGroupOverviewRenderer = new ComparisonGroupOverviewRenderer(this,
				view, dragAndDropController, getDimensionGroups());
		compGroupLayout.setPixelGLConverter(pixelGLConverter);
		compGroupLayout.setPixelSizeY(OVERVIEW_COMP_GROUP_HEIGHT_PIXELS);
		// compGroupLayout.setPixelSizeX(compGroupOverviewRenderer.getMinWidthPixels());
		compGroupLayout.setRenderer(compGroupOverviewRenderer);

		ElementLayout spacingLayoutY = new ElementLayout("spacingY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setRatioSizeX(0);

		baseColumn.append(spacingLayoutY);
		baseColumn.append(compGroupLayout);
		baseColumn.append(lineSeparatorLayout);
		baseColumn.append(captionLayout);
		baseColumn.append(spacingLayoutY);
		layoutTemplate.setBaseElementLayout(baseRow);
		layoutManager.setTemplate(layoutTemplate);
	}

	@Override
	public List<ADimensionGroupData> getDimensionGroups() {
		List<ADimensionGroupData> groups = representedView.getDimensionGroups();
		if (groups == null) {
			groups = new ArrayList<ADimensionGroupData>();
		}
		return groups;
	}

	@Override
	public void render(GL2 gl) {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		float spacingWidth = pixelGLConverter
				.getGLWidthForPixelWidth(getWidthPixels());
		float spacingHeight = pixelGLConverter
				.getGLHeightForPixelHeight(getHeightPixels());
		gl.glPushMatrix();
		gl.glTranslatef(x - spacingWidth / 2.0f, y - spacingHeight / 2.0f, 0.1f);

		// layoutManager.setViewFrustum(new ViewFrustum(
		// ECameraProjectionMode.ORTHOGRAPHIC, x - spacingWidth, x
		// + spacingWidth, y - spacingHeight, y + spacingHeight,
		// -1, 20));
		layoutManager.setViewFrustum(new ViewFrustum(
				CameraProjectionMode.ORTHOGRAPHIC, 0, spacingWidth, 0,
				spacingHeight, -1, 20));

		layoutManager.render(gl);
		gl.glPopMatrix();
	}

	@Override
	public int getHeightPixels() {
		return 2 * SPACING_PIXELS + CAPTION_HEIGHT_PIXELS
				+ LINE_SEPARATOR_HEIGHT_PIXELS
				+ OVERVIEW_COMP_GROUP_HEIGHT_PIXELS;
	}

	@Override
	public int getWidthPixels() {
		return 2 * SPACING_PIXELS
				+ compGroupOverviewRenderer.getMinWidthPixels();
	}

	@Override
	public Pair<Point2D, Point2D> getTopDimensionGroupAnchorPoints(
			ADimensionGroupData dimensionGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<Point2D, Point2D> getBottomDimensionGroupAnchorPoints(
			ADimensionGroupData dimensionGroup) {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		float width = pixelGLConverter
				.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter
				.getGLHeightForPixelHeight(getHeightPixels());
		float spacingX = pixelGLConverter
				.getGLWidthForPixelWidth(SPACING_PIXELS);
		float spacingY = pixelGLConverter
				.getGLHeightForPixelHeight(SPACING_PIXELS);

		Pair<Point2D, Point2D> anchorPoints = compGroupOverviewRenderer
				.getAnchorPointsOfDimensionGroup(dimensionGroup);

		Point2D first = (Point2D)anchorPoints.getFirst().clone();
		Point2D second = (Point2D)anchorPoints.getSecond().clone();
		
		first.setLocation(
				anchorPoints.getFirst().getX() + x - width / 2.0f + spacingX,
				anchorPoints.getFirst().getY() + y - height / 2.0f + spacingY);
		second.setLocation(
				anchorPoints.getSecond().getX() + x - width / 2.0f + spacingX,
				anchorPoints.getSecond().getY() + y - height / 2.0f + spacingY);
		

		return new Pair<Point2D, Point2D>(first, second);
	}

	@Override
	public Pair<Point2D, Point2D> getTopAnchorPoints() {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		float width = pixelGLConverter
				.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter
				.getGLHeightForPixelHeight(getHeightPixels());

		Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();

		anchorPoints.setFirst(new Point2D.Float(x - width / 2.0f, y + height
				/ 2.0f));
		anchorPoints.setSecond(new Point2D.Float(x + width / 2.0f, y + height
				/ 2.0f));

		return anchorPoints;
	}

	@Override
	public Pair<Point2D, Point2D> getBottomAnchorPoints() {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		float width = pixelGLConverter
				.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter
				.getGLHeightForPixelHeight(getHeightPixels());

		Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();

		anchorPoints.setFirst(new Point2D.Float(x - width / 2.0f, y - height
				/ 2.0f));
		anchorPoints.setSecond(new Point2D.Float(x + width / 2.0f, y - height
				/ 2.0f));

		return anchorPoints;
	}

	public void setDataDomains(Set<IDataDomain> dataDomains) {
		this.dataDomains = dataDomains;
	}

	public Set<IDataDomain> getDataDomains() {
		return dataDomains;
	}

	@Override
	public Pair<Point2D, Point2D> getLeftAnchorPoints() {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		float width = pixelGLConverter
				.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter
				.getGLHeightForPixelHeight(getHeightPixels());

		Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();

		anchorPoints.setFirst(new Point2D.Float(x - width / 2.0f, y + height
				/ 2.0f));
		anchorPoints.setSecond(new Point2D.Float(x - width / 2.0f, y - height
				/ 2.0f));

		return anchorPoints;
	}

	@Override
	public Pair<Point2D, Point2D> getRightAnchorPoints() {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		float width = pixelGLConverter
				.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter
				.getGLHeightForPixelHeight(getHeightPixels());

		Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();

		anchorPoints.setFirst(new Point2D.Float(x + width / 2.0f, y + height
				/ 2.0f));
		anchorPoints.setSecond(new Point2D.Float(x + width / 2.0f, y - height
				/ 2.0f));

		return anchorPoints;
	}

	@Override
	public Point2D getPosition() {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		return new Point2D.Float(x, y);
	}

	@Override
	public float getHeight() {
		return pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());
	}

	@Override
	public float getWidth() {
		return pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
	}

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
			if (draggable instanceof ComparisonGroupRepresentation) {
				ComparisonGroupRepresentation comparisonGroupRepresentation = (ComparisonGroupRepresentation) draggable;
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
		compGroupOverviewRenderer.setDimensionGroups(getDimensionGroups());
	}

}
