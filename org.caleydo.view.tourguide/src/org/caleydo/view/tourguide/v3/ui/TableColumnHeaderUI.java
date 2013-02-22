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
package org.caleydo.view.tourguide.v3.ui;

import static org.caleydo.core.view.opengl.layout2.layout.GLLayouts.defaultValue;
import gleem.linalg.Vec2f;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.animation.Durations;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions;
import org.caleydo.core.view.opengl.layout2.animation.Transitions;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;
import org.caleydo.view.tourguide.v3.model.ARankColumnModel;
import org.caleydo.view.tourguide.v3.model.mixin.ICollapseableColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IExplodeableColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IFilterColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IHideableColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IMappedColumnMixin;
import org.eclipse.swt.SWT;

/**
 * @author Samuel Gratzl
 *
 */
public class TableColumnHeaderUI extends AnimatedGLElementContainer implements IGLLayout {
	private final static int HIST = 0;
	private final static int DRAG_WEIGHT = 1;
	private final static int BUTTONS = 2;
	private final static int UNCOLLAPSE = 3;

	private final boolean interactive;
	private boolean canDrag;
	private boolean armDropColum;

	private boolean isCollapsed;

	private boolean isDragging;
	private final ARankColumnModel model;
	private PropertyChangeListener filterChangedListener;
	private final PropertyChangeListener collapsedChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onCollapsedChanged(evt.getNewValue() == Boolean.TRUE);
		}
	};
	private final PickingListenerComposite headerPick = new PickingListenerComposite(2);
	private int headerPickingId = -1;
	private boolean headerHovered;


	public TableColumnHeaderUI(final ARankColumnModel model, boolean interactive) {
		this.model = model;
		model.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, collapsedChanged);
		this.interactive = interactive;
		setLayout(this);
		setLayoutData(model);
		if (interactive)
			this.setVisibility(EVisibility.PICKABLE);
		this.onPick(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onMainPick(pick);
			}
		});
		this.add(model.createSummary(interactive).setLayoutData(Durations.NO), 0);
		if (interactive) {
			this.add(new DragElement().setLayoutData(MoveTransitions.GROW_LINEAR), 0);
			this.add(
					createButtons().setLayoutData(
							new MoveTransitions.MoveTransitionBase(Transitions.NO, Transitions.NO, Transitions.NO,
									Transitions.LINEAR)), 0);

			this.isCollapsed = (model instanceof ICollapseableColumnMixin) ? ((ICollapseableColumnMixin) model)
					.isCollapsed() : false;

			GLButton b = new GLButton();
			b.setzDelta(0.5f);
			b.setRenderer(GLRenderers.fillImage("resources/icons/view/tourguide/bullet_toggle_plus.png"));
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					((ICollapseableColumnMixin) model).setCollapsed(false);
				}
			});
			b.setTooltip("Toggle Collapse / Expand of this column");
			b.setLayoutData(Durations.NO);
			this.add(b, 0);

			this.isCollapsed = !isCollapsed;
			onCollapsedChanged(!isCollapsed); // force a change
		}

	}


	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		headerPick.add(context.createTooltip(new ILabelProvider() {
			@Override
			public String getProviderName() {
				return null;
			}

			@Override
			public String getLabel() {
				return model.getHeaderRenderer().toString();
			}
		}));
		headerPick.add(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onLabelPick(pick);
			}
		});
		headerPickingId = context.registerPickingListener(headerPick);
	}


	protected void onCollapsedChanged(boolean isCollapsed) {
		if (this.isCollapsed == isCollapsed)
			return;
		this.isCollapsed = isCollapsed;
		if (!interactive)
			return;
		if (isCollapsed) {
			this.get(DRAG_WEIGHT).setVisibility(EVisibility.HIDDEN);
			this.get(BUTTONS).setVisibility(EVisibility.HIDDEN);
			this.get(UNCOLLAPSE).setVisibility(EVisibility.PICKABLE);
		} else {
			this.get(DRAG_WEIGHT).setVisibility(EVisibility.PICKABLE);
			this.get(BUTTONS).setVisibility(EVisibility.VISIBLE);
			this.get(UNCOLLAPSE).setVisibility(EVisibility.HIDDEN);
		}
		repaintAll();
	}

	@Override
	protected void takeDown() {
		context.unregisterPickingListener(headerPickingId);
		headerPickingId = -1;
		model.removePropertyChangeListener(IFilterColumnMixin.PROP_FILTER, filterChangedListener);
		model.removePropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, collapsedChanged);
		super.takeDown();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		renderBackground(g, w, h);
		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (interactive) {
			g.incZ();
			g.pushName(headerPickingId);
			g.fillRect(0, 0, w, 20);
			g.popName();
			g.decZ();
		}
		super.renderPickImpl(g, w, h);
	}

	protected void renderBackground(GLGraphics g, float w, float h) {
		g.color(model.getBgColor()).renderRoundedRect(true, 0, 0, w, h, 5, 2, true, true, false, false);
		if (isCollapsed)
			return;
		g.move(2, 2);
		model.getHeaderRenderer().render(g, w - 4, 20 - 6, this);
		g.move(-2, -2);
		if (headerHovered) {
			g.color(Color.BLACK).renderRoundedRect(false, 0, 0, w, h, 5, 2, true, true, false, false);
		}
		if (this.armDropColum) {
			g.incZ(0.3f);
			g.drawText("+", 2, 2, w - 4, h - 4, VAlign.CENTER);
			g.decZ();
		}
	}

	/**
	 * @param pick
	 */
	protected void onLabelPick(Pick pick) {
		switch (pick.getPickingMode()) {
		case CLICKED:
			if (pick.isAnyDragging())
				return;
			pick.setDoDragging(true);
			onDragColumn(pick);
			break;
		case MOUSE_RELEASED:
			if (pick.isDoDragging())
				onDropColumn(pick);
			break;
		case MOUSE_OVER:
			if (pick.isAnyDragging())
				return;
			this.headerHovered = true;
			context.setCursor(SWT.CURSOR_HAND);
			repaint();
			break;
		case MOUSE_OUT:
			if (this.headerHovered) {
				this.headerHovered = false;
				context.setCursor(-1);
				repaint();
			}
			break;
		default:
			break;
		}
	}

	private GLElement createButtons() {
		GLElementContainer buttons = new GLElementContainer(GLLayouts.flowHorizontal(2));
		buttons.setzDelta(.5f);

		final int button_width = 12;
		if (model instanceof IFilterColumnMixin) {
			final IFilterColumnMixin m = (IFilterColumnMixin) model;
			final GLButton b = new GLButton();
			b.setSize(button_width, -1);
			b.setRenderer(GLRenderers.fillImage("resources/icons/view/tourguide/filter_disabled.png"));
			b.setSelectedRenderer(GLRenderers.fillImage("resources/icons/view/tourguide/filter.png"));
			b.setSelected(m.isFiltered());
			b.setTooltip("Edit the filter of this column");
			final ISelectionCallback callback = new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.editFilter(get(HIST));
				}
			};
			b.setCallback(callback);
			filterChangedListener = new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					b.setCallback(null);
					b.setSelected(m.isFiltered());
					b.setCallback(callback);
				}
			};
			model.addPropertyChangeListener(IFilterColumnMixin.PROP_FILTER, filterChangedListener);
			buttons.add(b);
		}
		if (model instanceof IMappedColumnMixin) {
			final IMappedColumnMixin m = (IMappedColumnMixin) model;
			GLButton b = new GLButton();
			b.setSize(button_width, -1);
			b.setRenderer(GLRenderers.fillImage("resources/icons/view/tourguide/pencil_ruler.png"));
			b.setTooltip("Edit the mapping of this column");
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.editMapping(get(HIST));
				}
			});
			buttons.add(b);
		}
		if (model instanceof IExplodeableColumnMixin) {
			final IExplodeableColumnMixin m = (IExplodeableColumnMixin) model;
			GLButton b = new GLButton();
			b.setSize(button_width, -1);
			b.setRenderer(GLRenderers.fillImage("resources/icons/view/tourguide/dynamite.png"));
			b.setTooltip("Split this combined column in individual ones");
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.explode();
				}
			});
			buttons.add(b);
		}
		buttons.add(new GLElement()); // spacer

		if (model instanceof ICollapseableColumnMixin) {
			final ICollapseableColumnMixin m = (ICollapseableColumnMixin) model;
			if (m.isCollapseAble()) {
				GLButton b = new GLButton();
				b.setSize(button_width, -1);
				b.setRenderer(GLRenderers.fillImage("resources/icons/view/tourguide/bullet_toggle_minus.png"));
				b.setCallback(new ISelectionCallback() {
					@Override
					public void onSelectionChanged(GLButton button, boolean selected) {
						m.setCollapsed(true);
					}
				});
				b.setTooltip("Toggle Collapse / Expand of this column");
				buttons.add(b);
			}
		}
		if (model instanceof IHideableColumnMixin) {
			final IHideableColumnMixin m = (IHideableColumnMixin) model;
			GLButton b = new GLButton();
			b.setSize(button_width, -1);
			b.setRenderer(GLRenderers.fillImage("resources/icons/view/tourguide/delete.png"));
			b.setTooltip("Removes this column");
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					if (m.isHideAble())
						m.hide();
				}
			});
			buttons.add(b);
		}
		return buttons;
	}


	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement hist = children.get(HIST);
		hist.setBounds(1, 20, w - 2, h - 20);

		if (interactive) {
			IGLLayoutElement weight = children.get(DRAG_WEIGHT);
			weight.setBounds(w, 20, canDrag ? 8 : 0, h - 20);

			IGLLayoutElement buttons = children.get(BUTTONS);
			buttons.setBounds(2, 2, w - 4, canDrag ? 12 : 0);

			IGLLayoutElement uncollapse = children.get(UNCOLLAPSE);
			uncollapse.setBounds((w - 12) * .5f, 2, 12, canDrag ? 12 : 0);

			for (IGLLayoutElement r : children.subList(UNCOLLAPSE + 1, children.size()))
				r.setBounds(defaultValue(r.getSetX(), 0), defaultValue(r.getSetY(), h),
						defaultValue(r.getSetWidth(), w), defaultValue(r.getSetHeight(), 40));
		}

	}


	protected void onMainPick(Pick pick) {
		IMouseLayer m = context.getMouseLayer();
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			if (!pick.isDoDragging() && m.hasDraggable(ARankColumnModel.class)) {
				Pair<GLElement, ARankColumnModel> pair = m.getFirstDraggable(ARankColumnModel.class);
				if (model.isCombineAble(pair.getSecond())) {
					m.setDropable(ARankColumnModel.class, true);
					this.armDropColum = true;
					repaint();
				}
			} else if (!pick.isAnyDragging()) {
				this.canDrag = true;
				this.relayout();
			}
			break;
		case MOUSE_OUT:
			if (armDropColum) {
				this.armDropColum = false;
				m.setDropable(ARankColumnModel.class, false);
				repaint();
			}
			if (this.canDrag) {
				this.canDrag = false;
				this.relayout();
			}
			break;
		case MOUSE_RELEASED:
			if (this.armDropColum) {
				Pair<GLElement, ARankColumnModel> info = m.getFirstDraggable(ARankColumnModel.class);
				m.removeDraggable(info.getFirst());
				context.setCursor(-1);
				if (info != null)
					model.combine(info.getSecond());
			}
			break;
		default:
			break;
		}
	}

	protected void onChangeWeight(int dx) {
		if (dx == 0)
			return;
		// float delta = (dx / getSize().x())*;
		model.addWeight(dx);
	}

	/**
	 * drop drag column again
	 *
	 * @param pick
	 */
	private void onDropColumn(Pick pick) {
		IMouseLayer l = context.getMouseLayer();
		if (this.isDragging) {
			if (!l.isDropable(this.model))
				l.removeDraggable(this.model);
			this.isDragging = false;
			context.setCursor(-1);
			return;
		}
	}

	private void onDragColumn(Pick pick) {
		IMouseLayer l = context.getMouseLayer();
		GLElement elem = new DraggedScoreHeaderItem();
		elem.setSize(getSize().x(), getSize().y());
		Vec2f loc = toRelative(pick.getPickedPoint());
		elem.setLocation(-loc.x(), -loc.y());
		isDragging = true;
		l.addDraggable(elem, this.model);
	}

	class DraggedScoreHeaderItem extends GLElement {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			renderBackground(g, w, h);
			if (get(HIST).getParent() != null)
				get(HIST).render(g);
		}
	}

	class DragElement extends PickableGLElement {
		private boolean hovered = false;
		public DragElement() {
			setRenderer(GLRenderers.fillImage("resources/icons/drag.png"));
			setTooltip("Drag this element to change the weight of this column");
			setzDelta(.5f);
		}

		@Override
		protected void onMouseOver(Pick pick) {
			if (pick.isAnyDragging())
				return;
			this.hovered = true;
			context.setCursor(SWT.CURSOR_HAND);
			repaintAll();
		}

		@Override
		protected void onMouseOut(Pick pick) {
			if (this.hovered)
				context.setCursor(-1);
			this.hovered = false;
			repaintAll();
		}


		@Override
		protected void onClicked(Pick pick) {
			if (pick.isAnyDragging())
				return;
			pick.setDoDragging(true);
		}

		@Override
		protected void onDragged(Pick pick) {
			if (pick.isDoDragging())
				onChangeWeight(pick.getDx());
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			super.renderPickImpl(g, w, h);
			if (hovered) {
				g.fillRect(0, 0, w * 2, h);
			}
		}
	}
}
