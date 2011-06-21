package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.picking.APickingListener;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.BorderedAreaRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.layout.util.LineSeparatorRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.visbricks.brick.data.IDimensionGroupData;

public class DataNode implements IDataGraphNode {

	private final static int SPACING_PIXELS = 4;
	private final static int CAPTION_HEIGHT_PIXELS = 16;
	private final static int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
	private final static int OVERVIEW_COMP_GROUP_HEIGHT_PIXELS = 32;

	private ForceDirectedGraphLayout graphLayout;
	private IDataDomain dataDomain;
	private GLDataGraph view;
	private PixelGLConverter pixelGLConverter;
	private LayoutManager layoutManager;
	private int id;

	private float prevDraggingMouseX;
	private float prevDraggingMouseY;

	private ComparisonGroupOverviewRenderer compGroupOverviewRenderer;

	public DataNode(ForceDirectedGraphLayout graphLayout, GLDataGraph view,
			final DragAndDropController dragAndDropController, int id) {
		this.graphLayout = graphLayout;
		this.view = view;
		this.pixelGLConverter = view.getParentGLCanvas().getPixelGLConverter();
		this.id = id;

		setupLayout();

		createPickingListener(dragAndDropController);
	}

	private void createPickingListener(
			final DragAndDropController dragAndDropController) {
		view.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				dragAndDropController.clearDraggables();
				dragAndDropController.setDraggingStartPosition(pick
						.getPickedPoint());
				dragAndDropController.addDraggable(DataNode.this);
			}

			@Override
			public void mouseOver(Pick pick) {
			}

			@Override
			public void dragged(Pick pick) {
				if (!dragAndDropController.isDragging()) {
					dragAndDropController.startDragging();
				}
			}

		}, EPickingType.DATA_GRAPH_NODE, id);
	}

	private void setupLayout() {
		layoutManager = new LayoutManager(new ViewFrustum());
		LayoutTemplate layoutTemplate = new LayoutTemplate();

		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);

		baseRow.setRenderer(new BorderedAreaRenderer(view,
				EPickingType.DATA_GRAPH_NODE, id));

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
		captionLayout.setRenderer(new LabelRenderer(view, "Data Set X",
				EPickingType.DATA_GRAPH_NODE, id));

		ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");
		lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
		lineSeparatorLayout.setPixelSizeY(LINE_SEPARATOR_HEIGHT_PIXELS);
		lineSeparatorLayout.setRatioSizeX(1);
		lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(false));

		ElementLayout compGroupLayout = new ElementLayout("compGroupOverview");
		compGroupOverviewRenderer = new ComparisonGroupOverviewRenderer(
				getDimensionGroups(), view);
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
	public void setDraggingStartPoint(float mouseCoordinateX,
			float mouseCoordinateY) {
		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;

	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX,
			float mouseCoordinateY) {
		if ((prevDraggingMouseX >= mouseCoordinateX - 0.01 && prevDraggingMouseX <= mouseCoordinateX + 0.01)
				&& (prevDraggingMouseY >= mouseCoordinateY - 0.01 && prevDraggingMouseY <= mouseCoordinateY + 0.01))
			return;

		float mouseDeltaX = prevDraggingMouseX - mouseCoordinateX;
		float mouseDeltaY = prevDraggingMouseY - mouseCoordinateY;
		int mouseDeltaXPixels = pixelGLConverter
				.getPixelWidthForGLWidth(mouseDeltaX);
		int mouseDeltaYPixels = pixelGLConverter
				.getPixelHeightForGLHeight(mouseDeltaY);

		Point2D position = graphLayout.getNodePosition(this, true);

		position.setLocation(position.getX() - mouseDeltaXPixels,
				position.getY() - mouseDeltaYPixels);

		graphLayout.setNodePosition(this, position);

		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;
		
		view.setDisplayListDirty();
		view.setApplyAutomaticLayout(false);

	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX,
			float mouseCoordinateY) {
//		view.setApplyAutomaticLayout(true);
	}

	@Override
	public List<IDimensionGroupData> getDimensionGroups() {
		// TODO Implement correctly
		List<IDimensionGroupData> groups = new ArrayList<IDimensionGroupData>();
		groups.add(null);
		groups.add(null);
		groups.add(null);
		groups.add(null);
		groups.add(null);
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
		gl.glTranslatef(x - spacingWidth / 2.0f, y - spacingHeight / 2.0f, 0);

		// layoutManager.setViewFrustum(new ViewFrustum(
		// ECameraProjectionMode.ORTHOGRAPHIC, x - spacingWidth, x
		// + spacingWidth, y - spacingHeight, y + spacingHeight,
		// -1, 20));
		layoutManager.setViewFrustum(new ViewFrustum(
				ECameraProjectionMode.ORTHOGRAPHIC, 0, spacingWidth, 0,
				spacingHeight, -1, 20));

		layoutManager.render(gl);
		gl.glPopMatrix();
		// GLHelperFunctions.drawPointAt(gl, x, y, 0);

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
}
