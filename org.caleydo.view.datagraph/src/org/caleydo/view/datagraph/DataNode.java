package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.util.BorderedAreaRenderer;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.view.visbricks.brick.data.IDimensionGroupData;

public class DataNode implements IDataGraphNode {

	private ForceDirectedGraphLayout graphLayout;
	private IDataDomain dataDomain;
	private AGLView view;
	private PixelGLConverter pixelGLConverter;
	private LayoutManager layoutManager;

	public DataNode(ForceDirectedGraphLayout graphLayout, AGLView view) {
		this.graphLayout = graphLayout;
		this.view = view;
		this.pixelGLConverter = view.getParentGLCanvas().getPixelGLConverter();
		layoutManager = new LayoutManager(new ViewFrustum());
		LayoutTemplate layoutTemplate = new LayoutTemplate();
		Column baseColumn = new Column();
		baseColumn.setRenderer(new BorderedAreaRenderer());
		baseColumn.setPixelGLConverter(pixelGLConverter);
		baseColumn.setPixelSizeX(50);
		baseColumn.setPixelSizeY(50);
		layoutTemplate.setBaseElementLayout(baseColumn);
		layoutManager.setTemplate(layoutTemplate);
	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX,
			float mouseCoordinateY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX,
			float mouseCoordinateY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX,
			float mouseCoordinateY) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IDimensionGroupData> getDimensionGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void render(GL2 gl) {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		float spacingWidth = pixelGLConverter.getGLWidthForPixelWidth(50);
		float spacingHeight = pixelGLConverter.getGLHeightForPixelHeight(50);
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
//		GLHelperFunctions.drawPointAt(gl, x, y, 0);

	}
}
