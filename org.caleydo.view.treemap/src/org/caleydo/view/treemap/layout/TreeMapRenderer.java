/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.layout;

import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * Class to render the treemap via jogl.
 *
 * @author Michael Lafer
 *
 */

public class TreeMapRenderer {

	public static final float SELECTION_LINE_WIDTH = 6.0f;

	ViewFrustum viewFrustum;
	PickingManager pickingManager;
	int viewID;

	int treemapList, highlightList;

	SelectionManager selectionManager;

	CaleydoTextRenderer textRenderer;

	private boolean bDrawNodeFrame = false;
	private Color frameColor = Color.WHITE;

	private boolean bDrawLabel = true;

	public void initRenderer(ViewFrustum viewFrustum, PickingManager pickingManager, int viewID, SelectionManager selectionManager,
			CaleydoTextRenderer textRenderer) {

		if (textRenderer == null)
			throw new IllegalArgumentException("Text ALayoutRenderer may never be null");
		this.pickingManager = pickingManager;

		this.viewFrustum = viewFrustum;
		this.viewID = viewID;
		this.selectionManager = selectionManager;
		this.textRenderer = textRenderer;

	}

	/**
	 * Generates new Cache for treemap.
	 *
	 * @param gl
	 */
	public void initCache(GL2 gl) {
		treemapList = gl.glGenLists(1);
		highlightList = gl.glGenLists(1);
	}

	/**
	 * Sets a frame for each node.
	 *
	 * @param flag
	 *            true when frames should be drawn
	 * @param color
	 *            color of the frame
	 */
	public void setNodeFrame(boolean flag, Color color) {
		bDrawNodeFrame = flag;
		frameColor = color;
	}

	/**
	 * Switch label on/off
	 *
	 * @param flag
	 *            true when labels should be drawn.
	 */
	public void setDrawLabel(boolean flag) {
		bDrawLabel = flag;
	}

	/**
	 * Renders only the highlighting to cache (not display).
	 *
	 * @param gl
	 * @param tree
	 *            Treemap model.
	 * @param selection
	 *            Selectionmanager
	 */
	public void paintHighlighting(GL2 gl, Tree<ATreeMapNode> tree, SelectionManager selection) {
		gl.glNewList(highlightList, GL2.GL_COMPILE);

		// for (int id : selection.getElements(SelectionType.MOUSE_OVER)) {
		// ATreeMapNode node = tree.getNodeByNumber(id);
		// if (node != null)
		// paintRectangle(gl, node.getMinX(), node.getMinY(), node.getMaxX(),
		// node.getMaxY(),
		// SelectionType.MOUSE_OVER.getColor(),SELECTION_LINE_WIDTH);
		// }
		//
		// for (int id : selection.getElements(SelectionType.SELECTION)) {
		// ATreeMapNode node = tree.getNodeByNumber(id);
		// if (node != null)
		// paintRectangle(gl, node.getMinX(), node.getMinY(), node.getMaxX(),
		// node.getMaxY(),
		// SelectionType.SELECTION.getColor(),SELECTION_LINE_WIDTH);
		// }

		for (SelectionType type : selection.getSelectionTypes()) {
			if (type != SelectionType.NORMAL)
				for (int id : selection.getElements(type)) {
					ATreeMapNode node;
					// TODO remove work around for tree bug when fixed
					if (tree.getRoot().getID() == id)
						node = tree.getRoot();
					else
						node = tree.getNodeByNumber(id);
					if (node != null)
						paintRectangle(gl, node.getMinX(), node.getMinY(), node.getMaxX(), node.getMaxY(), type.getColor(), type.getLineWidth());

				}
		}

		gl.glEndList();
	}

	/**
	 * Draws treemap data and highlighting from cache.
	 *
	 * @param gl
	 */
	public void renderTreeMapFromCache(GL2 gl) {
		gl.glCallList(treemapList);
		gl.glCallList(highlightList);
	}

	/**
	 * Renders only treemap data to cache.
	 *
	 * @param gl
	 * @param tree
	 *            Treemap model
	 */
	public void renderTreeMap(GL2 gl, ATreeMapNode tree) {
		gl.glNewList(treemapList, GL2.GL_COMPILE);
		renderHelp(gl, tree);
		gl.glEndList();

	}

	private void renderHelp(GL2 gl, ATreeMapNode root) {
		List<ATreeMapNode> children = root.getChildren();
		if (children == null || children.size() == 0) {
			gl.glPushName(pickingManager.getPickingID(viewID, PickingType.TREEMAP_ELEMENT_SELECTED, root.getID()));

			fillRectangle(gl, root.getMinX(), root.getMinY(), root.getMaxX(), root.getMaxY(), root.getColorAttribute());
			gl.glPopName();

			if (bDrawLabel && bDrawNodeFrame)
				displayText(gl, root.getMinX(), root.getMinY(), root.getMaxX(), root.getMaxY(), root.getLabel());

		} else {
			for (ATreeMapNode node : children) {
				renderHelp(gl, node);
			}
		}
	}

	private void paintRectangle(GL2 gl, float x, float y, float xmax, float ymax, Color color, float lineWdith) {
		gl.glLineWidth(lineWdith);

		gl.glBegin(GL.GL_LINE_LOOP);

		gl.glColor3fv(color.getRGB(), 0);

		x = viewFrustum.getWidth() * x + viewFrustum.getLeft();
		y = viewFrustum.getHeight() * y + viewFrustum.getBottom();
		xmax = viewFrustum.getWidth() * xmax + viewFrustum.getLeft();
		ymax = viewFrustum.getHeight() * ymax + viewFrustum.getBottom();

		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(x, ymax, 0);
		gl.glVertex3f(xmax, ymax, 0);
		gl.glVertex3f(xmax, y, 0);

		gl.glEnd();
	}

	private void fillRectangle(GL2 gl, float x, float y, float xmax, float ymax, float[] color) {
		gl.glBegin(GL2.GL_QUADS);

		gl.glColor3f(color[0], color[1], color[2]);

		x = viewFrustum.getWidth() * x + viewFrustum.getLeft();
		y = viewFrustum.getHeight() * y + viewFrustum.getBottom();
		xmax = viewFrustum.getWidth() * xmax + viewFrustum.getLeft();
		ymax = viewFrustum.getHeight() * ymax + viewFrustum.getBottom();

		// x = viewFrustum.getWidth() * x;
		// y = viewFrustum.getHeight() * y;
		// xmax = viewFrustum.getWidth() * xmax;
		// ymax = viewFrustum.getHeight() * ymax;

		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(x, ymax, 0);
		gl.glVertex3f(xmax, ymax, 0);
		gl.glVertex3f(xmax, y, 0);

		gl.glEnd();

		if (bDrawNodeFrame) {
			gl.glColor3fv(frameColor.getRGB(), 0);
			gl.glLineWidth(2);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(x, y, 0);
			gl.glVertex3f(x, ymax, 0);
			gl.glVertex3f(xmax, ymax, 0);
			gl.glVertex3f(xmax, y, 0);
			gl.glEnd();
		}
	}

	private void displayText(GL2 gl, float x, float y, float xmax, float ymax, String text) {
		float minScaling = 1.0f;
		float maxScaling = 3.0f;
		float border = 0.03f;

		float scaling = 1.0f;

		Rectangle2D bbox = textRenderer.getScaledBounds(gl, text, GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR * scaling, 20);

		float width = (float) (bbox.getWidth() / viewFrustum.getWidth()) + border;
		float height = (float) (bbox.getHeight() / viewFrustum.getHeight()) + border;

		scaling = Math.min((xmax - x) / width, (ymax - y) / height);

		if (scaling < minScaling)
			return;
		scaling = Math.min(scaling, maxScaling);

		textRenderer.renderText(gl, text, x * viewFrustum.getWidth() + 0.03f + viewFrustum.getLeft(),
				y * viewFrustum.getHeight() + 0.03f + viewFrustum.getBottom(), 0, GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR * scaling, 20);
	}

	public void destroy(GL2 gl) {
		gl.glDeleteLists(treemapList, 1);
		gl.glDeleteLists(highlightList, 1);
	}

}
