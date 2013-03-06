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
package org.caleydo.vis.rank.ui;

import static org.caleydo.vis.rank.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.rank.ui.RenderStyle.LABEL_HEIGHT;

import java.awt.Color;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.rank.ui.column.ColumnHeaderUI;

/**
 * simple visualization of the pool of hidden columns
 *
 * @author Samuel Gratzl
 *
 */
public class ColumnPoolUI extends GLElementContainer {
	private final RankTableModel table;

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case RankTableModel.PROP_POOL:
				onColumsChanged((IndexedPropertyChangeEvent) evt);
				break;
			default:
				break;
			}
		}
	};

	private int dropPickingId = -1;
	private final IPickingListener dropListener = new IPickingListener() {
		@Override
		public void pick(Pick pick) {
			onDropPick(pick);
		}
	};
	private boolean armed;

	public ColumnPoolUI(RankTableModel table) {
		this.table = table;
		table.addPropertyChangeListener(RankTableModel.PROP_POOL, listener);
		setLayout(new GLFlowLayout(true, 5, new GLPadding(5)));
		setSize(-1, LABEL_HEIGHT + HIST_HEIGHT);
		for (ARankColumnModel hidden : table.getPool()) {
			add(wrap(hidden));
		}
		this.add(new GLElement()); // spacer
		this.add(new PaperBasket(table).setSize(LABEL_HEIGHT + HIST_HEIGHT - 10, -1));
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		dropPickingId = context.registerPickingListener(dropListener);
	}

	protected void onDropPick(Pick pick) {
		if (!pick.isAnyDragging() || !context.getMouseLayer().hasDraggable(IHideableColumnMixin.class))
			return;
		Pair<GLElement, IHideableColumnMixin> pair = context.getMouseLayer().getFirstDraggable(
					IHideableColumnMixin.class);
		if (pair.getSecond().isHidden())
			return;
		switch(pick.getPickingMode()) {
		case MOUSE_OVER:
			if (!pair.getSecond().isHideAble())
				return;
			this.armed = true;
			context.getMouseLayer().setDropable(IHideableColumnMixin.class, true);
			repaint();
			break;
		case MOUSE_OUT:
			if (this.armed) {
				context.getMouseLayer().setDropable(IHideableColumnMixin.class, true);
				this.armed = false;
				repaint();
			}
			break;
		case MOUSE_RELEASED:
			if (armed) {
				context.getMouseLayer().removeDraggable(pair.getFirst());
				pair.getSecond().hide();
				context.setCursor(-1);
				armed = false;
				repaint();
			}
			break;
		default:
			break;
		}
	}

	private GLElement wrap(ARankColumnModel hidden) {
		return new ColumnHeaderUI(hidden, false, true).setSize(100, -1);
	}

	@Override
	protected void takeDown() {
		context.unregisterPickingListener(dropPickingId);
		dropPickingId = -1;
		table.removePropertyChangeListener(RankTableModel.PROP_POOL, listener);
		super.takeDown();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		g.color(Color.DARK_GRAY).drawRoundedRect(0, 0, w, h, 10);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		GLElement paperBasket = get(size() - 1);
		g.pushName(dropPickingId);
		g.fillRect(0, 0, paperBasket.getLocation().x() - 2, h);
		g.popName();
		super.renderPickImpl(g, w, h);
	}

	protected void onColumsChanged(IndexedPropertyChangeEvent evt) {
		if (evt.getOldValue() == null) { // new
			ARankColumnModel new_ = (ARankColumnModel) evt.getNewValue();
			this.add(size() - 2, wrap(new_)); // at the back
		} else if (evt.getNewValue() == null) { // remove
			for (GLElement g : this) {
				if (Objects.equals(g.getLayoutDataAs(ARankColumnModel.class, null), evt.getOldValue())) {
					remove(g);
					break;
				}
			}
		}
	}

	private static class PaperBasket extends PickableGLElement {
		private boolean armed = false;
		private final RankTableModel table;

		public PaperBasket(RankTableModel table) {
			this.table = table;
		}

		@Override
		protected void onMouseOver(Pick pick) {
			if (!pick.isAnyDragging() || !context.getMouseLayer().hasDraggable(IHideableColumnMixin.class))
				return;
			Pair<GLElement, IHideableColumnMixin> pair = context.getMouseLayer().getFirstDraggable(
					IHideableColumnMixin.class);
			if (!pair.getSecond().isDestroyAble())
				return;
			this.armed = true;
			context.getMouseLayer().setDropable(IHideableColumnMixin.class, true);
			repaint();
		}

		@Override
		protected void onMouseOut(Pick pick) {
			if (armed) {
				context.getMouseLayer().setDropable(IHideableColumnMixin.class, true);
				armed = false;
				repaint();
			}
		}

		@Override
		protected void onMouseReleased(Pick pick) {
			if (armed) {
				Pair<GLElement, ARankColumnModel> draggable = context.getMouseLayer().getFirstDraggable(
						ARankColumnModel.class);
				context.getMouseLayer().removeDraggable(draggable.getFirst());
				table.destroy(draggable.getSecond());
				context.setCursor(-1);
				armed = false;
				repaint();
			}
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.fillImage(RenderStyle.ICON_TRASH, 5, 5, w - 10, h - 10);
			if (armed) {
				g.color(Color.BLACK).drawRoundedRect(0, 0, w, h, 10);
			}
			super.renderImpl(g, w, h);
		}
	}
}
