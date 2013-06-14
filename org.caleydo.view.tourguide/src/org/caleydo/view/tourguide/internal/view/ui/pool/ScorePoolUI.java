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
package org.caleydo.view.tourguide.internal.view.ui.pool;

import static org.caleydo.vis.rank.ui.RenderStyle.LABEL_HEIGHT;
import gleem.linalg.Vec2f;

import org.caleydo.core.util.color.Color;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Objects;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.view.GLTourGuideView;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;

import com.google.common.collect.Iterables;

/**
 * simple visualization of the pool of hidden columns
 *
 * @author Samuel Gratzl
 *
 */
public class ScorePoolUI extends GLElementContainer implements IGLLayout {
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

	public ScorePoolUI(RankTableModel table, IRankTableUIConfig config, GLTourGuideView view) {
		this.table = table;
		table.addPropertyChangeListener(RankTableModel.PROP_POOL, listener);
		setLayout(this);
		setLayoutData(new Vec2f(110, (LABEL_HEIGHT + 8) * 3));

		this.add(new PaperBasket(table));

		this.add(new SerialFactoryPoolElem(table));

		// final EDataDomainQueryMode mode = view.getMode();
		// for(Map.Entry<String,IScoreFactory> factory : ScoreFactories.getFactories().entrySet()) {
		// if (!factory.getValue().supports(mode))
		// continue;
		// this.add(new ScoreFactoryPoolElem(factory.getKey(), factory.getValue(), view));
		// }

		for (ARankColumnModel hidden : table.getPool()) {
			add(wrap(hidden));
		}
	}

	/**
	 * @param hidden
	 * @return
	 */
	private GLElement wrap(ARankColumnModel hidden) {
		return new ColumnPoolElem(hidden);
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		dropPickingId = context.registerPickingListener(dropListener);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement paperBasket = children.get(0);
		children = children.subList(1, children.size());

		float x = 5;
		float y = 5;
		w -= 5;
		h -= 5;
		if (w > 120) {
			for (IGLLayoutElement child : children) {
				if ((x + 100) > w) {
					x = 5;
					y += LABEL_HEIGHT + 5;
				}
				child.setBounds(x, y, 100, LABEL_HEIGHT);
				x += 100 + 5;
			}
			paperBasket.setBounds(w - 40, h - 16, 40, 16);
		} else {
			// linear
			for (IGLLayoutElement child : children) {
				child.setBounds(x, y, 100, LABEL_HEIGHT);
				y += LABEL_HEIGHT + 2;
			}
			paperBasket.setBounds(x, h - 16, w - x, 16);
		}

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
				context.getSWTLayer().setCursor(-1);
				armed = false;
				repaint();
			}
			break;
		default:
			break;
		}
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
		g.color(Color.DARK_GRAY).drawRoundedRect(0, 0, w - 1, h - 1, 10);
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
			this.add(wrap(new_)); // at the back
		} else if (evt.getNewValue() == null) { // remove
			for (GLElement g : this) {
				if (Objects.equals(g.getLayoutDataAs(ARankColumnModel.class, null), evt.getOldValue())) {
					remove(g);
					break;
				}
			}
		}
	}

	public void updateMode(EDataDomainQueryMode mode) {
		for (ScoreFactoryPoolElem elem : Iterables.filter(this, ScoreFactoryPoolElem.class))
			elem.setMode(mode);
	}
}
