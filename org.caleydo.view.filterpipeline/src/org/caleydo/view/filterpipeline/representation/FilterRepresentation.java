/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
/**
 *
 */
package org.caleydo.view.filterpipeline.representation;

import gleem.linalg.Vec2f;

import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.filterpipeline.FilterItem;
import org.caleydo.view.filterpipeline.renderstyle.FilterPipelineRenderStyle;

/**
 * Represents a filter which can be rendered and dragged around
 *
 * @author Thomas Geymayer
 * @uathor Marc Streit
 *
 */
public class FilterRepresentation implements IDraggable, IRenderable, IDropArea {
	protected FilterPipelineRenderStyle renderStyle;
	protected PickingManager pickingManager;
	protected int viewId;

	protected static final float Z_POS_BODY = 0.1f;
	protected static final float Z_POS_BORDER = 0.15f;
	protected static final float Z_POS_MARK = 0.6f;
	protected static final float Z_POS_TEXT = 0.7f;
	protected static final float Z_POS_DRAG = 0.8f;
	protected static final float Z_POS_DRAG_OVER = 0.9f;

	protected FilterItem filter;
	protected int iPickingID = -1;
	protected SelectionType selectionType = SelectionType.NORMAL;

	protected Vec2f vPos = new Vec2f();
	protected Vec2f vSize = new Vec2f();
	protected Vec2f vDragStart = null;

	protected int mouseOverItem = -1;

	float heightLeft = 0;
	float heightRight = 0;
	float uncertaintyHeightRight = 0;

	public FilterRepresentation(FilterPipelineRenderStyle renderStyle,
			PickingManager pickingManager, int viewId) {
		this.renderStyle = renderStyle;
		this.pickingManager = pickingManager;
		this.viewId = viewId;
	}

	public void setFilter(FilterItem filter) {
		this.filter = filter;
		this.iPickingID = filter.getPickingID();
	}

	public FilterItem getFilter() {
		return filter;
	}

	public void setPosition(Vec2f filterPosition) {
		vPos = filterPosition.copy();
	}

	public Vec2f getPosition() {
		return vPos;
	}

	public void setSize(Vec2f filterSize) {
		vSize = filterSize.copy();
	}

	public Vec2f getSize() {
		return vSize;
	}

	public float getHeightLeft() {
		return vSize.y() * (filter.getInput().size() / 100.f);
	}

	public float getHeightRight() {
		return vSize.y() * (filter.getOutput().size() / 100.f);
	}

	public float getUncertaintyHeightRight() {

		if (filter.getUncertaintyOutput() == null)
			return 0;

		return heightRight
				- heightRight
				* ((float) filter.getUncertaintyOutput().size() / filter.getOutput()
						.size());
	}

	@Override
	public void render(GL2 gl, CaleydoTextRenderer textRenderer) {
		heightLeft = getHeightLeft();
		heightRight = getHeightRight();
		uncertaintyHeightRight = getUncertaintyHeightRight();

		gl.glPushName(iPickingID);
		renderBasicShape(gl, textRenderer,
				renderStyle.getUncertaintyColor(filter.getId() + 1));
		gl.glPopName();

		// render selection/mouseover if needed
		if (selectionType != SelectionType.NORMAL) {
			gl.glLineWidth((selectionType == SelectionType.SELECTION) ? SelectionType.SELECTION
					.getLineWidth() : SelectionType.MOUSE_OVER.getLineWidth());

			renderShape(
					gl,
					GL.GL_LINE_LOOP,
					(selectionType == SelectionType.SELECTION) ? SelectionType.SELECTION
							.getColor() : SelectionType.MOUSE_OVER.getColor(), Z_POS_MARK);
		}

		// label
		textRenderer.renderText(gl, filter.getLabel(), vPos.x() + 0.05f,
				vPos.y() + 0.05f, Z_POS_TEXT, 0.004f, 20);

		renderOutputBand(gl, new float[] { vPos.x() + vSize.x(), vPos.y(), Z_POS_BODY },
				new float[] { vPos.x() + vSize.x(), vPos.y() + heightRight, Z_POS_BODY },
				new float[] { vPos.x() + vSize.x() + 0.058f * vSize.x(),
						vPos.y() + heightRight, Z_POS_BODY },
				new float[] { vPos.x() + vSize.x() + 0.058f * vSize.x(), vPos.y(),
						Z_POS_BODY }, new float[] { 0.8f, 0.8f, 0.8f, 0.5f },
				new float[] { 0f, 0f, 0f, 1f });
	}

	protected void renderShape(GL2 gl, int renderMode, final Vec2f pos, float width,
			float heightLeft, float heightRight, float offsetLeft, float offsetRight,
			float[] color, float z) {
		gl.glBegin(renderMode);
		{
			gl.glColor4fv(color, 0);

			gl.glVertex3f(pos.x(), pos.y() + offsetLeft, z);
			gl.glVertex3f(pos.x(), pos.y() + offsetLeft + heightLeft, z);
			gl.glVertex3f(pos.x() + width, pos.y() + offsetRight + heightRight, z);
			gl.glVertex3f(pos.x() + width, pos.y() + offsetRight, z);
		}
		gl.glEnd();
	}

	protected void renderShape(GL2 gl, int renderMode, final Vec2f pos, float width,
			float heightLeft, float heightRight, float offsetRight, float[] color, float z) {
		renderShape(gl, renderMode, pos, width, heightLeft, heightRight, 0, 0, color, z);
	}

	protected void renderShape(GL2 gl, int renderMode, final Vec2f pos, float width,
			float heightLeft, float heightRight, float[] color, float z) {
		renderShape(gl, renderMode, pos, width, heightLeft, heightRight, 0, color, z);
	}

	protected void renderShape(GL2 gl, int renderMode, float[] color, float z) {
		renderShape(gl, renderMode, vPos, vSize.x(), heightLeft, heightRight, color, z);
	}

	/**
	 * Render the basic filter (background and border)
	 *
	 * @param gl
	 * @param color
	 */
	protected void renderBasicShape(GL2 gl, CaleydoTextRenderer textRenderer,
			float[] color) {

		gl.glLineWidth(1);
		// Render outline of filter
		renderShape(gl, GL.GL_LINE_LOOP, renderStyle.FILTER_BORDER_COLOR, Z_POS_BORDER);

		// Render uncertainty line
		if (filter.getUncertaintyOutput() == null)
			renderShape(gl, GL2.GL_QUADS, color, Z_POS_BODY);
		else {

			// Render uncertain area
			renderShape(gl, GL2.GL_QUADS, vPos, vSize.x(), 0, uncertaintyHeightRight
					- heightRight, heightLeft, heightRight, color, Z_POS_BODY);

			// Render certain area
			renderShape(gl, GL2.GL_QUADS, vPos, vSize.x(), heightLeft,
					uncertaintyHeightRight, 0, 0, renderStyle.FILTER_COLOR_UNCERTAINTY,
					Z_POS_BODY);

			// Render delimiter line between certain and uncertain area
			renderShape(gl, GL.GL_LINE_LOOP, vPos, vSize.x(), 0, 0, heightLeft,
					uncertaintyHeightRight, renderStyle.FILTER_BORDER_COLOR, Z_POS_BORDER);
		}

		// currently not filtered elements
		textRenderer.renderText(gl, "" + filter.getOutput().size(), vPos.x() + vSize.x()
				- 0.03f, vPos.y() + heightRight + 0.05f, Z_POS_TEXT, 0.004f, 20);

		// label
		textRenderer.renderText(gl, ""
				+ (filter.getOutput().size() - filter.getInput().size())
		/* + " (-"+filter.getSizeVADelta()+")" */, vPos.x() + 0.2f, vPos.y() - 0.1f,
				Z_POS_TEXT, 0.004f, 20);
	}

	/**
	 * Render a band with a transparent body, bounded by lines at top and bottom
	 *
	 * @param gl
	 * @param bottomLeft
	 * @param topLeft
	 * @param topRight
	 * @param bottomRight
	 * @param bodyColor
	 * @param borderColor
	 */
	protected void renderOutputBand(GL2 gl, float[] bottomLeft, float[] topLeft,
			float[] topRight, float[] bottomRight, float[] bodyColor, float[] borderColor) {
		gl.glColor4fv(bodyColor, 0);
		gl.glBegin(GL2.GL_QUADS);
		{
			gl.glVertex3fv(topLeft, 0);
			gl.glVertex3fv(topRight, 0);
			gl.glVertex3fv(bottomRight, 0);
			gl.glVertex3fv(bottomLeft, 0);
		}
		gl.glEnd();

		gl.glColor4fv(borderColor, 0);
		gl.glLineWidth(1);
		gl.glBegin(GL.GL_LINES);
		{
			gl.glVertex3fv(topLeft, 0);
			gl.glVertex3fv(topRight, 0);

			gl.glVertex3fv(bottomRight, 0);
			gl.glVertex3fv(bottomLeft, 0);
		}
		gl.glEnd();
	}

	/**
	 * Updates the selection state by gathering the selection state from the
	 * given {@link SelectionManager}
	 *
	 * @param selectionManager
	 */
	public void updateSelections(SelectionManager selectionManager) {
		if (selectionManager.checkStatus(SelectionType.SELECTION, filter.getId()))
			selectionType = SelectionType.SELECTION;
		else if (selectionManager.checkStatus(SelectionType.MOUSE_OVER, filter.getId()))
			selectionType = SelectionType.MOUSE_OVER;
		else
			selectionType = SelectionType.NORMAL;
	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX, float mouseCoordinateY) {
		vDragStart = new Vec2f(mouseCoordinateX, mouseCoordinateY);
	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {
		Vec2f tempPos = vPos.copy();
		vPos.add(new Vec2f(mouseCoordinateX - vDragStart.x(), mouseCoordinateY
				- vDragStart.y()));

		renderShape(gl, GL2.GL_POLYGON, renderStyle.getFilterColorDrag(filter.getId()),
				Z_POS_DRAG);

		vPos = tempPos;
	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY) {
		gl.glLineWidth(5);
		renderShape(gl, GL.GL_LINE_LOOP, renderStyle.DRAG_OVER_COLOR, Z_POS_DRAG_OVER);
	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController) {
		// TODO Auto-generated method stub
	}

	public void handleIconMouseOver(int externalID) {
		mouseOverItem = externalID;
	}

	public void handleClearMouseOver() {
		mouseOverItem = -1;
	}

	@Override
	public void handleDropAreaReplaced()
	{
		// TODO Auto-generated method stub

	}
}
