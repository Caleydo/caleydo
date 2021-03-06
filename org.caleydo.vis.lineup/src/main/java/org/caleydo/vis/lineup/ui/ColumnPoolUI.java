/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui;

import static org.caleydo.vis.lineup.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.lineup.ui.RenderStyle.LABEL_HEIGHT;

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
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.dnd.ADropGLTarget;
import org.caleydo.core.view.opengl.layout2.dnd.EDnDType;
import org.caleydo.core.view.opengl.layout2.dnd.IDnDItem;
import org.caleydo.core.view.opengl.layout2.dnd.IDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.IDropGLTarget;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.config.RankTableUIConfigs;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.ColumnDragInfo;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.lineup.ui.column.ColumnHeaderUI;

/**
 * simple visualization of the pool of hidden columns
 *
 * @author Samuel Gratzl
 *
 */
public class ColumnPoolUI extends GLElementContainer implements IGLLayout {
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
	private boolean isSmallHeader;

	private final IRankTableUIConfig config;

	private IDropGLTarget dropTarget = new ADropGLTarget() {

		@Override
		public boolean canSWTDrop(IDnDItem input) {
			IDragInfo info = input.getInfo();
			if (!(info instanceof ColumnDragInfo))
				return false;
			ARankColumnModel model = ((ColumnDragInfo) info).getModel();
			if (!(model instanceof IHideableColumnMixin) || model.isHidden())
				return false;
			armed = true;
			repaint();
			return true;
		}

		@Override
		public void onDrop(IDnDItem info) {
			ARankColumnModel model = ((ColumnDragInfo) info.getInfo()).getModel();
			model.hide();
			armed = false;
			repaint();
		}
	};

	public ColumnPoolUI(RankTableModel table, IRankTableUIConfig config) {
		this.table = table;
		this.config = RankTableUIConfigs.nonInteractive(config);
		this.isSmallHeader = config.isSmallHeaderByDefault();
		table.addPropertyChangeListener(RankTableModel.PROP_POOL, listener);
		setLayout(this);
		setSize(-1, LABEL_HEIGHT + HIST_HEIGHT);
		this.add(new PaperBasket(table).setSize(LABEL_HEIGHT + HIST_HEIGHT, -1));

		{
			GLButton toggleSmallHeader = new GLButton(GLButton.EButtonMode.CHECKBOX);
			toggleSmallHeader.setSelected(isSmallHeader);
			toggleSmallHeader.setCallback(new GLButton.ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					setSmallHeader(selected);
				}
			});
			toggleSmallHeader.setTooltip("Toggle Small / Thick Headers");
			toggleSmallHeader.setRenderer(GLRenderers.fillImage(RenderStyle.ICON_SMALL_HEADER_ON));
			toggleSmallHeader.setSelectedRenderer(GLRenderers.fillImage(RenderStyle.ICON_SMALL_HEADER_OFF));

			this.add(toggleSmallHeader);
		}

		for (ARankColumnModel hidden : table.getPool()) {
			add(wrap(hidden));
		}
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		dropPickingId = context.registerPickingListener(dropListener);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement paperBasket = children.get(0);
		paperBasket.setBounds(w - paperBasket.getSetWidth() - 5, 5, paperBasket.getSetWidth(), h - 10);

		IGLLayoutElement toggleExpand = children.get(1);
		toggleExpand.setBounds(w - paperBasket.getSetWidth() - 5 - 16, 5, 16, 16);

		children = children.subList(2, children.size());
		float x = 5;
		float y = 5;
		w -= paperBasket.getSetWidth() - 5;
		h -= 10;
		if (!isSmallHeader) {
			float wi = Math.min(100, (w - children.size() * 5 - 5) / children.size());
			for (IGLLayoutElement child : children) {
				child.setBounds(x, y, wi, h);
				x += wi + 5;
			}
		} else {
			int rows = (int) (h / LABEL_HEIGHT);
			int perrow = (int) Math.ceil((float) children.size() / rows);
			float wi = Math.min(100, (w - perrow * 5 - 5) / perrow);
			if (wi == 100) {
				for (IGLLayoutElement child : children) {
					if (x + 100 < wi) {
						x = 5;
						y += LABEL_HEIGHT;
					}
					child.setBounds(x, y, wi, LABEL_HEIGHT);
					x += wi + 5;
				}
			} else {
				int i = 0;
				for (IGLLayoutElement child : children) {
					i++;
					if (i % perrow == 0) {
						x = 5;
						y += LABEL_HEIGHT;
					}
					child.setBounds(x, y, wi, LABEL_HEIGHT);
					x += wi + 5;
				}
			}
		}
	}

	/**
	 * @return the isSmallHeader, see {@link #isSmallHeader}
	 */
	public boolean isSmallHeader() {
		return isSmallHeader;
	}

	/**
	 * @param isSmallHeader
	 *            setter, see {@link isSmallHeader}
	 */
	public void setSmallHeader(boolean isSmallHeader) {
		if (this.isSmallHeader == isSmallHeader)
			return;
		this.isSmallHeader = isSmallHeader;
		relayout();
	}

	protected void onDropPick(Pick pick) {
		switch(pick.getPickingMode()) {
		case MOUSE_OVER:
			this.armed = true;
			context.getMouseLayer().addDropTarget(dropTarget);
			repaint();
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

	private GLElement wrap(ARankColumnModel hidden) {
		return new ColumnHeaderUI(hidden, config);
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

	private static class PaperBasket extends PickableGLElement implements IDropGLTarget {
		private boolean armed = false;
		private final RankTableModel table;

		public PaperBasket(RankTableModel table) {
			this.table = table;
		}

		@Override
		public boolean canSWTDrop(IDnDItem input) {
			if (!(input.getInfo() instanceof ColumnDragInfo))
				return false;
			ColumnDragInfo info = (ColumnDragInfo)input.getInfo();
			if (!(info.getModel() instanceof IHideableColumnMixin))
				return false;
			IHideableColumnMixin model = (IHideableColumnMixin) info.getModel();
			if (!model.isDestroyAble())
				return false;
			this.armed = true;
			repaint();
			return true;
		}

		@Override
		public void onItemChanged(IDnDItem input) {

		}

		@Override
		public void onDrop(IDnDItem info) {
			table.removeFromPool(((ColumnDragInfo) info.getInfo()).getModel());
			armed = false;
			repaint();
		}

		@Override
		public EDnDType defaultSWTDnDType(IDnDItem item) {
			return EDnDType.MOVE;
		}

		@Override
		public void onDropLeave() {
		}

		@Override
		protected void takeDown() {
			context.getMouseLayer().removeDropTarget(this);
			super.takeDown();
		}

		@Override
		protected void onMouseOver(Pick pick) {
			context.getMouseLayer().addDropTarget(this);
		}

		@Override
		protected void onMouseOut(Pick pick) {
			context.getMouseLayer().removeDropTarget(this);
			if (armed) {
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
