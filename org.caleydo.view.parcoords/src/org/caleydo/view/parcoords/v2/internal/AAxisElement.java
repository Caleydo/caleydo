/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2.internal;

import static org.caleydo.view.parcoords.PCRenderStyle.AXIS_MARKER_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.Y_AXIS_COLOR;
import static org.caleydo.view.parcoords.PCRenderStyle.Y_AXIS_LINE_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.Y_AXIS_SELECTED_LINE_WIDTH;
import gleem.linalg.Vec2f;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.IDoublePredicate;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
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
public abstract class AAxisElement extends GLElementContainer implements IPickingListener, IGLLayout2, IDoublePredicate {
	protected final int id;
	protected final String label;
	private final GLButton hideNaN;
	private final MultiButton moveAxis;

	public AAxisElement(int id, String label) {
		this.id = id;
		this.label = label;
		setLayout(this);

		this.add(new AxisLine().onPick(this));
		this.hideNaN = new GLButton(EButtonMode.CHECKBOX);
		this.add(hideNaN.setCallback(new ISelectionCallback() {
			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				getParent().repaint();
			}
		}).setRenderer(GLRenderers.fillImage(PCRenderStyle.NAN)));

		this.moveAxis = new MultiButton();
		this.add(this.moveAxis);
	}

	@Override
	public boolean apply(double in) {
		if (hideNaN.isSelected() && Double.isNaN(in))
			return false;
		return true;
	}

	@Override
	public final boolean apply(Double input) {
		return apply(input.doubleValue());
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs) {
		final IGLLayoutElement axis = children.get(0);
		final IGLLayoutElement nan = children.get(1);
		final IGLLayoutElement move = children.get(2);

		axis.setBounds(0, 0, w, h);
		nan.setBounds(-6, h + 3, 12, 12);
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
			relayout();
			break;
		case MOUSE_OUT:
			manager.removeFromType(SelectionType.MOUSE_OVER, id);
			relayout();
			break;
		case CLICKED:
			if (ParallelCoordinateElement.isBrushClick(pick))
				return;
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
	public final int getId() {
		return id;
	}

	/**
	 * @return
	 */
	public final float getX() {
		return getLocation().x();
	}

	private TablePerspectiveSelectionMixin findSelectionManager() {
		ParallelCoordinateElement p = findParent();
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

	protected final ParallelCoordinateElement findParent() {
		return findParent(ParallelCoordinateElement.class);
	}
	/**
	 *
	 */
	void remove() {
		findParent().remove(this);
	}

	/**
	 * @param dx
	 * @param dy
	 */
	void move(float dx, float dy) {
		findParent().move(this, dx);
	}

	/**
	 *
	 */
	void resetAxis() {
		ParallelCoordinateElement p = findParent();
		p.resetAxesSpacing();
	}

	/**
	 *
	 */
	void duplicate() {
		ParallelCoordinateElement p = findParent();
		p.add(createClone());
		p.resetAxesSpacing();
	}


	/**
	 * @return
	 */
	protected abstract AAxisElement createClone();

	/**
	 * @param g
	 * @param w
	 * @param h
	 */
	protected abstract void renderMarkers(GLGraphics g, float w, float h);

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		g.lineWidth(3).drawLine(0, 0, 0, h).lineWidth(1);
		super.renderPickImpl(g, w, h);
	}

	private final class MultiButton extends PickableGLElement implements IPickingListener {
		private static final int DUPLICATE = 1;
		private static final int REMOVE = 2;
		private static final int MOVE = 3;
		private int duplicatePickingId;
		private int removePickingId;
		private int movePickingId;

		private int hoveredSub = 0;
		private boolean hovered = false;
		private int armed = 0;

		@Override
		protected void init(IGLElementContext context) {
			duplicatePickingId = context.registerPickingListener(this, DUPLICATE);
			removePickingId = context.registerPickingListener(this, REMOVE);
			movePickingId = context.registerPickingListener(this, MOVE);
			super.init(context);
		}

		@Override
		protected void onMouseOver(Pick pick) {
			if (pick.isAnyDragging())
				return;
			hovered = true;
			repaintAll();
		}

		@Override
		protected void onMouseOut(Pick pick) {
			if (!hovered)
				return;
			hovered = false;
			repaintAll();
		}

		@Override
		public void pick(Pick pick) {
			switch (pick.getObjectID()) {
			case DUPLICATE:
				onDuplicatePicked(pick);
				break;
			case MOVE:
				onMovePicked(pick);
				break;
			case REMOVE:
				onRemovePicked(pick);
				break;
			default:
				break;
			}
		}

		/**
		 * @param pick
		 */
		private void onRemovePicked(Pick pick) {
			if (pick.isAnyDragging())
				return;
			switch (pick.getPickingMode()) {
			case MOUSE_OVER:
				hoveredSub = REMOVE;
				repaint();
				break;
			case MOUSE_OUT:
				hoveredSub = 0;
				repaint();
				break;
			case CLICKED:
				armed = REMOVE;
				repaint();
				break;
			case MOUSE_RELEASED:
				if (armed == REMOVE) {
					remove();
				}
				armed = 0;
				break;
			default:
				break;
			}
		}

		/**
		 * @param pick
		 */
		private void onMovePicked(Pick pick) {
			switch (pick.getPickingMode()) {
			case MOUSE_OVER:
				if (pick.isAnyDragging())
					return;
				hoveredSub = MOVE;
				repaint();
				break;
			case MOUSE_OUT:
				hoveredSub = 0;
				repaint();
				break;
			case CLICKED:
				if (pick.isAnyDragging())
					return;
				pick.setDoDragging(true);
				armed = MOVE;
				repaint();
				break;
			case DOUBLE_CLICKED:
				resetAxis();
				break;
			case DRAGGED:
				if (armed != MOVE)
					return;
				move(pick.getDx(), pick.getDy());
				break;
			case MOUSE_RELEASED:
				armed = 0;
				repaint();
				break;
			default:
				break;
			}
		}

		/**
		 * @param pick
		 */
		private void onDuplicatePicked(Pick pick) {
			if (pick.isAnyDragging())
				return;
			switch (pick.getPickingMode()) {
			case MOUSE_OVER:
				hoveredSub = DUPLICATE;
				repaint();
				break;
			case MOUSE_OUT:
				hoveredSub = 0;
				repaint();
				break;
			case CLICKED:
				armed = DUPLICATE;
				repaint();
				break;
			case MOUSE_RELEASED:
				if (armed == DUPLICATE) {
					duplicate();
				}
				armed = 0;
				break;
			default:
				break;
			}
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			String image = hovered ? PCRenderStyle.DROP_NORMAL : PCRenderStyle.SMALL_DROP;
			switch (hoveredSub) {
			case DUPLICATE:
				image = PCRenderStyle.DROP_DUPLICATE;
				break;
			case REMOVE:
				image = PCRenderStyle.DROP_DELETE;
				break;
			case MOVE:
				image = PCRenderStyle.DROP_MOVE;
				break;
			}
			if (hovered)
				g.fillImage(image, -32 + 8, 0, 64, 63);
			else
				g.fillImage(image, 0, 0, w, h);
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			super.renderPickImpl(g, w, h);
			final float s = w * 0.5f;
			if (hovered && getVisibility() == EVisibility.PICKABLE) {
				g.incZ();
				g.pushName(duplicatePickingId);
				g.color(Color.RED).fillPolygon(new Vec2f(s, 0), new Vec2f(s + -31, 49 - 16), new Vec2f(s + -31, 49),
						new Vec2f(s + -31 + 16, 49));
				g.popName();
				g.pushName(movePickingId);
				g.color(Color.BLUE).fillPolygon(new Vec2f(s, 0), new Vec2f(s + -15, 63), new Vec2f(s + 15, 63));
				g.popName();
				g.pushName(removePickingId);
				g.color(Color.GREEN).fillPolygon(new Vec2f(s, 0), new Vec2f(s + 31, 49 - 16), new Vec2f(s + 31, 49),
						new Vec2f(s + 31 - 16, 49));
				g.popName();
				g.color(Color.BLACK);
				g.decZ();
			}
		}
	}

}
