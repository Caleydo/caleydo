/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.column;

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
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions.IMoveTransition;
import org.caleydo.core.view.opengl.layout2.animation.Transitions;
import org.caleydo.core.view.opengl.layout2.layout.GLLayoutDatas;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.config.RankTableUIConfigs;
import org.caleydo.vis.lineup.model.ACompositeRankColumnModel;
import org.caleydo.vis.lineup.model.ARankColumnModel;

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
		for (GLElement col : Lists.newArrayList(this.iterator()).subList(firstColumn(), size())) {
			takeDown(col.getLayoutDataAs(ARankColumnModel.class, null));
		}
		super.takeDown();
	}

	@SuppressWarnings("unchecked")
	protected void onChildrenChanged(IndexedPropertyChangeEvent evt) {
		int index = evt.getIndex();
		final int firstColumn = firstColumn();
		if (evt.getOldValue() instanceof Integer) { // moved
			int movedFrom = (Integer) evt.getOldValue();
			add(index + firstColumn, get(movedFrom + firstColumn));
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
				add(index++ + firstColumn, new_);
		} else if (evt.getNewValue() == null) { // removed
			takeDown(get(index + firstColumn).getLayoutDataAs(ARankColumnModel.class, null));
			remove(index + firstColumn);
		} else { // replaced
			takeDown(get(index + firstColumn).getLayoutDataAs(ARankColumnModel.class, null));
			set(index + firstColumn, wrap((ARankColumnModel) evt.getNewValue()));
		}
	}

	/**
	 * @param c
	 * @return
	 */
	private GLElement wrap(ARankColumnModel c) {
		init(c);
		GLElement elem = ColumnUIs.createHeader(c, RankTableUIConfigs.nonInteractive(config), false);
		elem.setLayoutData(GLLayoutDatas.combine(c, move));
		return elem;
	}

	private boolean isDownAlignment() {
		boolean down = true;
		IGLElementParent p = getParent();
		do {
			if (p instanceof VerticalColumnHeaderUI)
				down = !down;
			p = p.getParent();
		} while (p != null);
		return down;
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		super.doLayout(children, w, h);
		List<? extends IGLLayoutElement> childs = children.subList(firstColumn(), children.size());

		if (headerHovered)
			childrenHovered = true;
		boolean showChildren = childrenHovered && !this.isCollapsed();
		if (isDownAlignment()) {
			if (showChildren) {
				float y = h;
				for (IGLLayoutElement child : childs) {
					child.setBounds(0, y, w, h);
					y += h;
				}
			} else {
				for (IGLLayoutElement child : childs)
					child.setBounds(0, h, w, 0);
			}
		} else {
			if (showChildren) {
				float x = w;
				for (IGLLayoutElement child : childs) {
					child.setBounds(x, 0, w, h);
					x += w;
				}
			} else {
				for (IGLLayoutElement child : childs)
					child.setBounds(w, 0, 0, h);
			}
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
