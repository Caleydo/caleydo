/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui.pool;

import static org.caleydo.vis.lineup.ui.RenderStyle.LABEL_HEIGHT;
import gleem.linalg.Vec2f;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Objects;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.dnd.ADropGLTarget;
import org.caleydo.core.view.opengl.layout2.dnd.IDnDItem;
import org.caleydo.core.view.opengl.layout2.dnd.IDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.IDropGLTarget;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.internal.view.GLTourGuideView;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.ColumnDragInfo;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.mixin.IHideableColumnMixin;

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

	private IDropGLTarget dropTarget = new ADropGLTarget() {

		@Override
		public void onDrop(IDnDItem input) {
			ARankColumnModel model = ((ColumnDragInfo) input.getInfo()).getModel();
			model.hide();
			armed = false;
			repaint();
		}

		@Override
		public boolean canSWTDrop(IDnDItem input) {
			IDragInfo info = input.getInfo();
			if (!(info instanceof ColumnDragInfo))
				return false;
			ARankColumnModel model = ((ColumnDragInfo) info).getModel();
			if (!(model instanceof IHideableColumnMixin) || model.isHidden() || !model.isHideAble())
				return false;
			armed = true;
			repaint();
			return true;
		}
	};

	public ScorePoolUI(RankTableModel table, IRankTableUIConfig config, GLTourGuideView view) {
		this.table = table;
		table.addPropertyChangeListener(RankTableModel.PROP_POOL, listener);
		setLayout(this);
		setLayoutData(new Vec2f(110, (LABEL_HEIGHT + 8) * 3));

		this.add(new PaperBasket(table));

		this.add(new SerialFactoryPoolElem(table));
		this.add(new SeparatorFactoryPoolElem(table));

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
		switch(pick.getPickingMode()) {
		case MOUSE_OVER:
			context.getMouseLayer().addDropTarget(dropTarget);
			break;
		case MOUSE_OUT:
			context.getMouseLayer().removeDropTarget(dropTarget);
			if (this.armed) {
				this.armed = false;
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
}
