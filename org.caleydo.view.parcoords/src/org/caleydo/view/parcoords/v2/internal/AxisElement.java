/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2.internal;

import static org.caleydo.view.parcoords.PCRenderStyle.AXIS_MARKER_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.NUMBER_AXIS_MARKERS;
import static org.caleydo.view.parcoords.PCRenderStyle.Y_AXIS_COLOR;
import static org.caleydo.view.parcoords.PCRenderStyle.Y_AXIS_LINE_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.Y_AXIS_SELECTED_LINE_WIDTH;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.parcoords.PCRenderStyle;
import org.caleydo.view.parcoords.v2.ParallelCoordinateElement;

/**
 * @author Samuel Gratzl
 *
 */
public class AxisElement extends GLElementContainer implements IPickingListener, IGLLayout2 {
	private final int id;
	private final String label;
	private boolean hovered = false;
	private final GLButton hideNaN;
	private final GLButton addGate;
	private GLButton moveAxis;

	public AxisElement(int id, String label) {
		this.id = id;
		this.label = label;
		setLayout(this);

		this.add(new AxisLine().onPick(this));
		this.hideNaN = new GLButton();
		this.add(hideNaN.setCallback(new ISelectionCallback() {
			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				onHideNan();
			}
		}).setRenderer(GLRenderers.fillImage(PCRenderStyle.NAN)));

		this.addGate = new GLButton();
		this.add(addGate.setCallback(new ISelectionCallback() {
			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				onAddGate();
			}
		}).setRenderer(GLRenderers.fillImage(PCRenderStyle.ADD_GATE)));

		this.moveAxis = new GLButton();
		this.add(moveAxis.setCallback(new ISelectionCallback() {
			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				onMoveAxis();
			}
		}).setRenderer(GLRenderers.fillImage(PCRenderStyle.SMALL_DROP)));

		// if (hovered) {
		//
		// // the mouse over drops
		// // texture is 63x62
		//
		// buttonWidht = pixelGLConverter.getGLWidthForPixelWidth(31);
		// buttonHeight = pixelGLConverter.getGLHeightForPixelHeight(63);
		//
		// lowerLeftCorner.set(xOrigin - buttonWidht, fYDropOrigin - buttonHeight, AXIS_Z + 0.005f);
		// lowerRightCorner.set(xOrigin + buttonWidht, fYDropOrigin - buttonHeight, AXIS_Z + 0.005f);
		// upperRightCorner.set(xOrigin + buttonWidht, fYDropOrigin, AXIS_Z + 0.005f);
		// upperLeftCorner.set(xOrigin - buttonWidht, fYDropOrigin, AXIS_Z + 0.005f);
		//
		// if (changeDropOnAxisNumber == count) {
		// // tempTexture = textureManager.getIconTexture(gl,
		// // dropTexture);
		// textureManager.renderTexture(gl, dropTexture, lowerLeftCorner, lowerRightCorner, upperRightCorner,
		// upperLeftCorner, Color.WHITE);
		//
		// if (!bWasAxisMoved) {
		// dropTexture = PCRenderStyle.DROP_NORMAL;
		// }
		// } else {
		// textureManager.renderTexture(gl, PCRenderStyle.DROP_NORMAL, lowerLeftCorner, lowerRightCorner,
		// upperRightCorner, upperLeftCorner, Color.WHITE);
		// }
		//
		// // picking for the sub-parts of the drop texture
		//
		// // center drop has width of 30 starts at position 16
		// buttonWidht = pixelGLConverter.getGLWidthForPixelWidth(15);
		// buttonHeight = pixelGLConverter.getGLHeightForPixelHeight(63);
		//
		// pickingID = pickingManager.getPickingID(uniqueID, EPickingType.MOVE_AXIS.name(), count);
		// gl.glColor4f(0, 0, 0, 0f);
		// gl.glPushName(pickingID);
		// gl.glBegin(GL.GL_TRIANGLES);
		// gl.glVertex3f(xOrigin, fYDropOrigin, AXIS_Z + 0.01f);
		// gl.glVertex3f(xOrigin + buttonWidht, fYDropOrigin - buttonHeight, AXIS_Z + 0.01f);
		// gl.glVertex3f(xOrigin - buttonWidht, fYDropOrigin - buttonHeight, AXIS_Z + 0.01f);
		// gl.glEnd();
		// gl.glPopName();
		//
		// // left drop
		// buttonHeight = pixelGLConverter.getGLHeightForPixelHeight(63 - 14);
		// float buttonOuterBorder = pixelGLConverter.getGLWidthForPixelWidth(31);
		// buttonWidht = pixelGLConverter.getGLWidthForPixelWidth(16);
		//
		// float buttonOuterHight = pixelGLConverter.getGLHeightForPixelHeight(16);
		//
		// pickingID = pickingManager.getPickingID(uniqueID, EPickingType.DUPLICATE_AXIS.name(), count);
		// // gl.glColor4f(0, 1, 0, 0.5f);
		// gl.glPushName(pickingID);
		// gl.glBegin(GL2.GL_POLYGON);
		// gl.glVertex3f(xOrigin, fYDropOrigin, AXIS_Z + 0.01f);
		// gl.glVertex3f(xOrigin - buttonOuterBorder, fYDropOrigin - buttonHeight + buttonOuterHight, AXIS_Z + 0.01f);
		// gl.glVertex3f(xOrigin - buttonOuterBorder, fYDropOrigin - buttonHeight, AXIS_Z + 0.01f);
		// gl.glVertex3f(xOrigin - buttonOuterBorder + buttonWidht, fYDropOrigin - buttonHeight, AXIS_Z + 0.01f);
		// gl.glEnd();
		// gl.glPopName();
		//
		// pickingID = pickingManager.getPickingID(uniqueID, EPickingType.REMOVE_AXIS.name(), count);
		// // gl.glColor4f(0, 0, 1, 0.5f);
		// gl.glPushName(pickingID);
		// gl.glBegin(GL2.GL_POLYGON);
		// gl.glVertex3f(xOrigin, fYDropOrigin, AXIS_Z + 0.01f);
		// gl.glVertex3f(xOrigin + buttonOuterBorder, fYDropOrigin - buttonHeight + buttonOuterHight, AXIS_Z + 0.01f);
		// gl.glVertex3f(xOrigin + buttonOuterBorder, fYDropOrigin - buttonHeight, AXIS_Z + 0.01f);
		// gl.glVertex3f(xOrigin + buttonOuterBorder - buttonWidht, fYDropOrigin - buttonHeight, AXIS_Z + 0.01f);
		// gl.glEnd();
		// gl.glPopName();
		//
		// }
	}

	/**
	 *
	 */
	protected void onMoveAxis() {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 */
	protected void onAddGate() {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 */
	protected void onHideNan() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs) {
		final IGLLayoutElement axis = children.get(0);
		final IGLLayoutElement nan = children.get(1);
		final IGLLayoutElement add = children.get(2);
		final IGLLayoutElement move = children.get(3);

		axis.setBounds(0, 0, w, h);
		nan.setBounds(-6, h + 3, 12, 12);
		add.setBounds(-8, -32, 16, 32);
		if (hovered)
			move.hide();
		else
			move.setBounds(-8, h + 16, 16, 32);

		return false;
	}

	@Override
	public void pick(Pick pick) {
		// pick axis line
		TablePerspectiveSelectionMixin mixin = findSelectionManager();
		SelectionManager manager = mixin.getDimensionSelectionManager();
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			manager.addToType(SelectionType.MOUSE_OVER, id);
			hovered = true;
			relayout();
			break;
		case MOUSE_OUT:
			manager.removeFromType(SelectionType.MOUSE_OVER, id);
			hovered = false;
			relayout();
			break;
		case CLICKED:
			if (!((IMouseEvent) pick).isCtrlDown())
				manager.clearSelection(SelectionType.SELECTION);
			manager.addToType(SelectionType.SELECTION, id);
			break;
		default:
			return;
		}
		repaint();
		mixin.fireDimensionSelectionDelta();

	}
	/**
	 * @return the id, see {@link #id}
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return
	 */
	public float getX() {
		return getLocation().x();
	}

	private TablePerspectiveSelectionMixin findSelectionManager() {
		ParallelCoordinateElement p = findParent(ParallelCoordinateElement.class);
		assert p != null;
		return p.getSelections();
	}

	private class AxisLine extends GLElement {
		/**
		 *
		 */
		public AxisLine() {
			setPicker(null);
			setVisibility(EVisibility.PICKABLE);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL2.GL_LINE_BIT);
			SelectionManager manager = findSelectionManager().getDimensionSelectionManager();
			SelectionType highestSelectionType = manager.getHighestSelectionType(id);
			if (highestSelectionType == SelectionType.SELECTION || highestSelectionType == SelectionType.MOUSE_OVER) {
				g.color(highestSelectionType.getColor());
				g.lineWidth(Y_AXIS_SELECTED_LINE_WIDTH);
				g.gl.glEnable(GL2.GL_LINE_STIPPLE);
				g.gl.glLineStipple(2, (short) 0xAAAA);
			} else {
				g.color(Y_AXIS_COLOR).lineWidth(Y_AXIS_LINE_WIDTH);
			}
			g.drawLine(0, 0, 0, h);

			g.gl.glPopAttrib();

			// top marker
			g.drawLine(-AXIS_MARKER_WIDTH, 0, +AXIS_MARKER_WIDTH, 0);

			g.gl.glDisable(GL2.GL_LINE_STIPPLE);

			renderMarkers(g, w, h);
			g.drawText(label, -100, -45, 200, 10, VAlign.CENTER);
			super.renderImpl(g, w, h);
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			g.lineWidth(3).drawLine(0, 0, 0, h).lineWidth(1);
			g.drawLine(-AXIS_MARKER_WIDTH, 0, +AXIS_MARKER_WIDTH, 0);
			renderMarkers(g, w, h);
			super.renderPickImpl(g, w, h);
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		g.gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
		super.renderImpl(g, w, h);
		g.gl.glPopAttrib();
	}



	/**
	 * @param g
	 * @param w
	 * @param h
	 */
	private void renderMarkers(GLGraphics g, float w, float h) {
		// markers on axis
		g.color(Y_AXIS_COLOR);
		float delta = h / (NUMBER_AXIS_MARKERS + 1);
		for (int i = 1; i < NUMBER_AXIS_MARKERS; ++i) {
			float at = delta * i;
			// if (count == 0) {
			// if (dataDomain.getTable() instanceof NumericalTable) {
			// float fNumber = (float) ((NumericalTable) dataDomain.getTable()).getRawForNormalized(
			// dataTransformation, currentHeight / renderStyle.getAxisHeight());
			//
			// float width = pixelGLConverter.getGLWidthForPixelWidth(40);
			// float height = pixelGLConverter.getGLHeightForPixelHeight(12);
			//
			// float xOrigin = xPosition - width - axisMarkerWidth;
			//
			// float yOrigin = currentHeight - height / 2;
			//
			// textRenderer.renderTextInBounds(gl, Formatter.formatNumber(fNumber), xOrigin, yOrigin,
			// PCRenderStyle.TEXT_ON_LABEL_Z, width, height);
			//
			// } else {
			// // TODO: dimension based access
			// }
			// }
			g.drawLine(-AXIS_MARKER_WIDTH, at, +AXIS_MARKER_WIDTH, at);
		}
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		g.lineWidth(3).drawLine(0, 0, 0, h).lineWidth(1);
		super.renderPickImpl(g, w, h);
	}
}
