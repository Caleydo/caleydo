package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class ComparisonGroupOverviewRenderer extends LayoutRenderer {

	private final static int SPACING_PIXELS = 2;
	private final static int MIN_COMP_GROUP_WIDTH_PIXELS = 10;

	private IDataGraphNode node;
	private AGLView view;
	private Map<ADimensionGroupData, Pair<Point2D, Point2D>> dimensionGroupPositions;

	public ComparisonGroupOverviewRenderer(IDataGraphNode node, AGLView view) {

		this.node = node;
		this.view = view;
		dimensionGroupPositions = new HashMap<ADimensionGroupData, Pair<Point2D, Point2D>>();
	}

	@Override
	public void render(GL2 gl) {

		PixelGLConverter pixelGLConverter = view.getParentGLCanvas()
				.getPixelGLConverter();

		gl.glBegin(GL2.GL_QUADS);

		float currentPosX = 0;
		float step = pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS
				+ MIN_COMP_GROUP_WIDTH_PIXELS);

		Set<ADimensionGroupData> dimensionGroupData = node.getDimensionGroups();
		dimensionGroupPositions.clear();

		for (ADimensionGroupData data : dimensionGroupData) {
			float width = pixelGLConverter
					.getGLWidthForPixelWidth(MIN_COMP_GROUP_WIDTH_PIXELS);

			gl.glVertex3f(currentPosX, 0, 0);
			gl.glVertex3f(currentPosX + width, 0, 0);
			gl.glVertex3f(currentPosX + width, y, 0);
			gl.glVertex3f(currentPosX, y, 0);

			Point2D position1 = new Point2D.Float(currentPosX, 0);
			Point2D position2 = new Point2D.Float(currentPosX + width, 0);
			dimensionGroupPositions.put(data, new Pair<Point2D, Point2D>(
					position1, position2));

			currentPosX += step;
		}

		gl.glEnd();
	}

	@Override
	public int getMinWidthPixels() {
		return Math
				.max(200,
						(node.getDimensionGroups().size() * MIN_COMP_GROUP_WIDTH_PIXELS)
								+ ((node.getDimensionGroups().size() - 1) * SPACING_PIXELS));
	}

	public Pair<Point2D, Point2D> getAnchorPointsOfDimensionGroup(
			ADimensionGroupData dimensionGroupData) {
		return dimensionGroupPositions.get(dimensionGroupData);
	}

}
