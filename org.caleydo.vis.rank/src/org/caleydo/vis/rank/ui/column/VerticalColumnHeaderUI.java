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
package org.caleydo.vis.rank.ui.column;

import static org.caleydo.core.view.opengl.layout2.animation.Transitions.LINEAR;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions.IMoveTransition;
import org.caleydo.core.view.opengl.layout2.animation.Transitions;
import org.caleydo.core.view.opengl.layout2.layout.CompositeGLLayoutData;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.config.RankTableUIConfigs;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class VerticalColumnHeaderUI extends AColumnHeaderUI {
	private static final IMoveTransition move = new MoveTransitions.MoveTransitionBase(Transitions.NO, LINEAR,
			Transitions.NO, Transitions.NO);
	protected final PropertyChangeListener childrenChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onChildrenChanged((IndexedPropertyChangeEvent) evt);
		}
	};

	private int childrenPickingId;
	private boolean childrenHovered;

	public VerticalColumnHeaderUI(ACompositeRankColumnModel model, IRankTableUIConfig config) {
		super(model, config, true, true);
		model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		for (ARankColumnModel m : model) {
			this.add(wrap(m));
		}
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		childrenPickingId = context.registerPickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onChildPick(pick);
			}
		});
	}

	/**
	 * @param pick
	 */
	protected void onChildPick(Pick pick) {
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			childrenHovered = true;
			relayout();
			break;
		case MOUSE_OUT:
			childrenHovered = false;
			relayout();
			break;
		default:
			break;
		}
	}

	@Override
	protected void takeDown() {
		context.unregisterPickingListener(childrenPickingId);
		model.removePropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		for (GLElement col : Lists.newArrayList(this.iterator()).subList(FIRST_CUSTOM, size())) {
			takeDown(col.getLayoutDataAs(ARankColumnModel.class, null));
		}
		super.takeDown();
	}

	@SuppressWarnings("unchecked")
	protected void onChildrenChanged(IndexedPropertyChangeEvent evt) {
		int index = evt.getIndex();
		if (evt.getOldValue() instanceof Integer) { // moved
			int movedFrom = (Integer) evt.getOldValue();
			add(index + FIRST_CUSTOM, get(movedFrom + FIRST_CUSTOM));
		} else if (evt.getOldValue() == null) { // new
			Collection<GLElement> news = null;
			if (evt.getNewValue() instanceof ARankColumnModel) {
				news = Collections.singleton(wrap((ARankColumnModel) evt.getNewValue()));
			} else {
				news = new ArrayList<>();
				for (ARankColumnModel c : (Collection<ARankColumnModel>) evt.getNewValue())
					news.add(wrap(c));
			}
			for (GLElement new_ : news)
				add(index++ + FIRST_CUSTOM, new_);
		} else if (evt.getNewValue() == null) { // removed
			takeDown(get(index + FIRST_CUSTOM).getLayoutDataAs(ARankColumnModel.class, null));
			remove(index + FIRST_CUSTOM);
		} else { // replaced
			takeDown(get(index + FIRST_CUSTOM).getLayoutDataAs(ARankColumnModel.class, null));
			set(index + FIRST_CUSTOM, wrap((ARankColumnModel) evt.getNewValue()));
		}
	}

	/**
	 * @param c
	 * @return
	 */
	private GLElement wrap(ARankColumnModel c) {
		init(c);
		GLElement elem = ColumnUIs.createHeader(c, RankTableUIConfigs.nonInteractive(config), false);
		elem.setLayoutData(CompositeGLLayoutData.combine(c, move));
		return elem;
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		super.doLayout(children, w, h);
		List<? extends IGLLayoutElement> childs = children.subList(FIRST_CUSTOM, children.size());

		if (headerHovered)
			childrenHovered = true;
		if (childrenHovered && !this.isCollapsed()) {
			float y = h;
			for (IGLLayoutElement child : childs) {
				child.setBounds(0, y, w, h);
				y += h;
			}
		} else {
			for (IGLLayoutElement child : childs)
				child.setBounds(0, h, w, 0);
		}
	}

	private void init(ARankColumnModel col) {
		if (col == null)
			return;
	}

	private void takeDown(ARankColumnModel col) {
		if (col == null)
			return;
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		boolean doRender = headerHovered || childrenHovered;
		if (doRender)
			g.pushName(childrenPickingId);
		super.renderPickImpl(g, w, h);
		if (doRender)
			g.popName();
	}
}
