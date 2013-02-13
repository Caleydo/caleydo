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
package org.caleydo.view.tourguide.v2.r.ui;

import gleem.linalg.Vec2f;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLPadding;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.v2.r.model.ScoreColumn;
import org.caleydo.view.tourguide.v2.r.model.ScoreTable;
import org.eclipse.swt.SWT;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreHeaderItemUI extends GLElement {
	private final ScoreColumn column;
	private int selectedRow = -1;

	private boolean hovered;

	private int dragPickingID = -1;
	private boolean dragHovered = false;

	private int cursorMinPickingID = -1, cursorMaxPickingID = -1;
	private int mainAreaPickingID = -1;
	private int labelAreaPickingID = -1;
	private boolean cursorMinHovered = false, cursorMaxHovered = true;
	private ScoreColumnDragInfo dragged;

	public ScoreHeaderItemUI(ScoreColumn column) {
		this.column = column;
		this.column.addPropertyChangeListener(ScoreColumn.PROP_SELECTED_ROW, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				onSelectRow((int) evt.getNewValue());
			}
		});
		this.column.addPropertyChangeListener(ScoreColumn.PROP_SELECTION, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				repaintAll();
			}
		});
		this.selectedRow = column.getSelectedRow();
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		dragPickingID = context.registerPickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onPickDrag(pick);
			}
		});
		cursorMinPickingID = context.registerPickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onCursorPick(true, pick);
			}
		});
		cursorMaxPickingID = context.registerPickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onCursorPick(false, pick);
			}
		});
		mainAreaPickingID = context.registerPickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onMainAreaPick(pick);
			}
		});
		labelAreaPickingID = context.registerPickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onLabelAreaPick(pick);
			}
		});
	}

	protected void onCursorPick(boolean isMin, Pick pick) {
		switch (pick.getPickingMode()) {
		case CLICKED:
			pick.setDoDragging(true);
			context.getMouseLayer().addDraggable(ScoreDragInfo.SELECTION_DRAG_INFO);
			break;
		case MOUSE_OVER:
			context.setCursor(SWT.CURSOR_HAND);
			if (isMin)
				cursorMinHovered = true;
			else
				cursorMaxHovered = true;
			repaintAll();
			break;
		case MOUSE_OUT:
			if (!pick.isDoDragging()) {
				if (isMin)
					cursorMinHovered = false;
				else
					cursorMaxHovered = false;
				repaintAll();
			}
			break;
		case DRAGGED:
			if (!pick.isDoDragging())
				return;
			int dx = pick.getDx();
			if (dx != 0) {
				float delta = dx / this.getSize().x();
				if (isMin) {
					column.setNormalizedSelectionMin(column.getNormalizedSelectionMin() + delta);
				} else {
					column.setNormalizedSelectionMax(column.getNormalizedSelectionMax() + delta);
				}
				repaintAll();
			}
			break;
		case MOUSE_RELEASED:
			if (pick.isDoDragging()) {
				context.setCursor(-1);
				if (isMin)
					cursorMinHovered = false;
				else
					cursorMaxHovered = false;
				context.getMouseLayer().removeDraggable(ScoreDragInfo.SELECTION_DRAG_INFO);
				repaintAll();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * @param pick
	 */
	protected void onPickDrag(Pick pick) {
		switch (pick.getPickingMode()) {
		case CLICKED:
			pick.setDoDragging(true);
			context.getMouseLayer().addDraggable(ScoreDragInfo.WEIGHT_DRAG_INFO);
			break;
		case MOUSE_OVER:
			context.setCursor(SWT.CURSOR_HAND);
			dragHovered = true;
			repaintAll();
			break;
		case MOUSE_OUT:
			if (!pick.isDoDragging()) {
				context.setCursor(-1);
				dragHovered = false;
				repaintAll();
			}
			break;
		case DRAGGED:
			if (!pick.isDoDragging())
				return;
			int dx = pick.getDx();
			if (dx != 0) { // update weight
				column.setWeight(column.getWeight() + dx);
			}
			break;
		case MOUSE_RELEASED:
			if (pick.isDoDragging()) {
				context.setCursor(-1);
				dragHovered = false;
				context.getMouseLayer().removeDraggable(ScoreDragInfo.WEIGHT_DRAG_INFO);
				repaintAll();
			}
			break;
		default:
			break;
		}
	}

	protected void onMainAreaPick(Pick pick) {
		switch (pick.getPickingMode()) {
		case CLICKED:
			if (pick.isDoDragging() || context.getMouseLayer().hasDraggables())
				return;
			Vec2f p = toRelative(pick.getPickedPoint());
			float at = p.x() / this.getSize().x();
			float min = column.getNormalizedSelectionMin();
			float max = column.getNormalizedSelectionMax();
			if (at < min)
				column.setNormalizedSelectionMin(at);
			else if (at > max)
				column.setNormalizedSelectionMax(at);
			else if ((at - min) < (max - min) * .5f)
				column.setNormalizedSelectionMin(at);
			else
				column.setNormalizedSelectionMax(at);
			break;
		case MOUSE_OVER:
			if (!context.getMouseLayer().hasDraggables())
				this.hovered = true;
			repaint();
			repaintPick();
			break;
		case MOUSE_OUT:
			this.hovered = false;
			repaint();
			repaintPick();
			break;
		default:
			break;
		}
	}

	protected void onLabelAreaPick(Pick pick) {
		IMouseLayer l = context.getMouseLayer();
		switch (pick.getPickingMode()) {
		case CLICKED:
			if (l.hasDraggables())
				return;
			GLElement elem = new DraggedScoreHeaderItem();
			elem.setSize(getSize().x(), getSize().y());
			pick.setDoDragging(true);
			this.dragged = new ScoreColumnDragInfo(this.column);
			l.addDraggable(elem, dragged);
			break;
		case MOUSE_OVER:
			if (!l.hasDraggables())
				context.setCursor(SWT.CURSOR_HAND);
			break;
		case MOUSE_OUT:
			if (!pick.isDoDragging())
				context.setCursor(-1);
			break;
		case MOUSE_RELEASED:
			if (this.dragged != null) {
				if (!l.isDropable(this.dragged))
					l.removeDraggable(this.dragged);
				this.dragged = null;
				context.setCursor(-1);
				return;
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void takeDown() {
		context.unregisterPickingListener(dragPickingID);
		context.unregisterPickingListener(cursorMinPickingID);
		context.unregisterPickingListener(cursorMaxPickingID);
		context.unregisterPickingListener(mainAreaPickingID);
		context.unregisterPickingListener(labelAreaPickingID);
		super.takeDown();
	}

	/**
	 * @param newValue
	 */
	protected void onSelectRow(int selectedRow) {
		if (this.selectedRow == selectedRow)
			return;
		this.selectedRow = selectedRow;
		repaint();
	}

	class DraggedScoreHeaderItem extends GLElement {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			ScoreHeaderItemUI.this.renderImpl(g, w, h);
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);

		g.drawText(column.getLabel(), 0, 0, w, h * 0.3f - 2, VAlign.CENTER);

		g.save();
		g.move(0, h * 0.3f);
		renderHeaderBar(g, w, h * 0.7f);
		g.restore();
	}

	private void renderHeaderBar(GLGraphics g, float w, float h) {
		// background
		g.color(column.getBackgroundColor()).fillRect(0, 0, w, h);
		// hist
		int bins = Math.round(w);
		int selectedBin = selectedRow < 0 ? -1 : column.getHistBin(bins, selectedRow);
		RenderUtils.renderHist(g, column.getHist(bins), w, h, selectedBin, column.getColor(),
				column.getSelectionColor());
		// selection
		renderSelection(g, column.getNormalizedSelectionMin(), column.getNormalizedSelectionMax(), w, h);

		if (hovered || dragHovered) {
			g.incZ();
			if (!dragHovered)
				g.fillImage("resources/icons/drag.png", w, 0, 8, h);
			else
				g.fillImage("resources/icons/drag.png", w - 3, 0, 14, h);
			g.decZ();
		}
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);

		g.pushName(labelAreaPickingID);
		g.fillRect(0, 0, w, h * 0.3f);
		g.popName();

		g.save();
		g.move(0, h * 0.3f);
		h = h * 0.7f;

		g.pushName(mainAreaPickingID);
		g.fillRect(0, 0, w, h);
		g.popName();

		g.incZ();
		if (hovered || dragHovered) {
			g.pushName(dragPickingID);
			if (!dragHovered)
				g.fillRect(w - 2, 0, 8, h);
			else
				g.fillRect(w - 5, 0, 8 + 10, h);
			g.popName();
		}

		float from = column.getNormalizedSelectionMin();
		if (from > 0) {
			g.pushName(cursorMinPickingID);
			if (cursorMinHovered)
				g.fillRect(from * w - 8, 0, 16, h);
			else
				g.fillRect(from * w, 0, 1, h);
			g.popName();
		}
		float to = column.getNormalizedSelectionMax();
		if (to < 1) {
			g.pushName(cursorMaxPickingID);
			if (cursorMaxHovered)
				g.fillRect(to * w - 8, 0, 16, h);
			else
				g.fillRect(to * w, 0, 1, h);
			g.popName();
		}

		g.decZ();
		g.restore();
	}

	private void renderSelection(GLGraphics g, float from, float to, float w, float h) {
		assert from < to;
		if (from > 0 && from < 1) {
			g.color(0, 0, 0, 0.25f).fillRect(0, 0, from * w, h);
			if (cursorMinHovered)
				g.color(Color.BLACK).fillRect(from * w - 2, 0, 4, h);
			else
				g.color(Color.BLACK).fillRect(from * w, 0, 1, h);
		}
		if (to > 0 && to < 1) {
			g.color(0, 0, 0, 0.25f).fillRect(to * w, 0, (1 - to) * w, h);
			if (cursorMaxHovered)
				g.color(Color.BLACK).fillRect(to * w - 2, 0, 4, h);
			else
				g.color(Color.BLACK).fillRect(to * w, 0, 1, h);
		}

	}

	public static void main(String[] args) {
		ScoreTable t = ScoreTable.demo();
		GLSandBox.main(args, new ScoreHeaderItemUI(t.getColumn(0)), new GLPadding(100));
	}
}