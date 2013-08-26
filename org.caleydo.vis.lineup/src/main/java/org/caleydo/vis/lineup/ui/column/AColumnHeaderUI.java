/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.column;

import static org.caleydo.core.view.opengl.layout2.layout.GLLayouts.defaultValue;
import static org.caleydo.vis.lineup.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.lineup.ui.RenderStyle.LABEL_HEIGHT;
import gleem.linalg.Vec2f;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Objects;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions;
import org.caleydo.core.view.opengl.layout2.animation.Transitions;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.AdvancedPick;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.text.ETextStyle;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.caleydo.vis.lineup.config.IRankTableConfig;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.config.IRankTableUIConfig.EButtonBarPositionMode;
import org.caleydo.vis.lineup.internal.event.OrderByMeEvent;
import org.caleydo.vis.lineup.internal.ui.ButtonBar;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.DoubleRankColumnModel;
import org.caleydo.vis.lineup.model.IRankColumnParent;
import org.caleydo.vis.lineup.model.RankRankColumnModel;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.mixin.IAnnotatedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ICompressColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IExplodeableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IScriptedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ISearchableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ISnapshotableColumnMixin;
import org.caleydo.vis.lineup.ui.IColumnRenderInfo;
import org.caleydo.vis.lineup.ui.RenderStyle;
import org.caleydo.vis.lineup.ui.column.StackedColumnHeaderUI.AlignmentDragInfo;
import org.eclipse.swt.SWT;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class AColumnHeaderUI extends AnimatedGLElementContainer implements IGLLayout, IColumnRenderInfo, ILabeled {
	private final static int HIST = 0;
	private final static int DRAG_WEIGHT = 1;
	private final static int BUTTONS = 2;
	private final static int UNCOLLAPSE = 3;


	protected final IRankTableUIConfig config;
	private boolean isHovered;
	private boolean armDropColum;
	private String armDropHint;

	private boolean isCollapsed;

	private boolean isDragging;
	protected boolean headerHovered;

	protected final ARankColumnModel model;
	private PropertyChangeListener filterChangedListener;
	private final PropertyChangeListener collapsedChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onCollapsedChanged(evt.getNewValue() == Boolean.TRUE);
		}
	};
	private int dragPickingId = -1;

	private boolean hasTitle;
	private boolean isWeightDragging;

	private boolean wasColumnDragging = false;


	public AColumnHeaderUI(final ARankColumnModel model, IRankTableUIConfig config, boolean hasTitle, boolean hasHist) {
		this.model = model;
		model.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, collapsedChanged);
		this.config = config;
		this.hasTitle = hasTitle;

		setLayout(this);
		setLayoutData(model);
		setPicker(null);
		if (config.isInteractive())
			this.setVisibility(EVisibility.PICKABLE);
		this.onPick(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onMainPick(pick);
			}
		});
		if (hasHist) {
			this.add(model.createSummary(config.isInteractive()).setLayoutData(0), 0);
		} else {
			this.add(new GLElement().setVisibility(EVisibility.HIDDEN));
		}
		if (config.isInteractive()) {
			this.add(new DragElement().setLayoutData(MoveTransitions.GROW_LINEAR), 0);
			final GLElement buttons = createButtons().setLayoutData(
					new MoveTransitions.MoveTransitionBase(Transitions.NO, Transitions.LINEAR, Transitions.NO,
							Transitions.LINEAR));
			this.add(buttons, 0);

			this.isCollapsed = (model instanceof ICollapseableColumnMixin) ? ((ICollapseableColumnMixin) model)
					.isCollapsed() : false;

			GLButton b = new GLButton();
			b.setzDelta(0.5f);
			b.setRenderer(GLRenderers.fillImage(RenderStyle.ICON_COLLAPSE));
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					((ICollapseableColumnMixin) model).setCollapsed(false);
				}
			});
			b.setTooltip("Toggle collapse / expand of this column");
			b.setLayoutData(0);
			this.add(b, 0);

			this.isCollapsed = !isCollapsed;
			onCollapsedChanged(!isCollapsed); // force a change
		}

	}

	protected final int firstColumn() {
		return config.isInteractive() ? 4 : 1;
	}

	private static boolean isSmallHeader(float h) {
		return h < (LABEL_HEIGHT + HIST_HEIGHT * 0.5f);
	}

	/**
	 * @param hasTitle
	 *            setter, see {@link hasTitle}
	 */
	public void setHasTitle(boolean hasTitle) {
		if (this.hasTitle == hasTitle)
			return;
		this.hasTitle = hasTitle;
		relayout();
	}

	@Override
	public boolean isCollapsed() {
		return ((model instanceof ICollapseableColumnMixin) && ((ICollapseableColumnMixin) model).isCollapsed());
	}

	@Override
	public VAlign getAlignment() {
		return VAlign.LEFT;
	}

	@Override
	public boolean hasFreeSpace() {
		return false;
	}

	@Override
	public Color getBarOutlineColor() {
		return config.getBarOutlineColor();
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		onPick(context.getSWTLayer().createTooltip(this));
		dragPickingId = context.registerPickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onDragPick(pick);
			}
		});
	}

	@Override
	public String getLabel() {
		String ann = ((model instanceof IAnnotatedColumnMixin) ? ((IAnnotatedColumnMixin) model).getDescription()
				: null);
		ann = ann == null ? "" : ann.trim();
		if (ann.trim().isEmpty())
			return model.getTitle();
		return model.getTitle() + "\n" + ann;
	}

	protected void onCollapsedChanged(boolean isCollapsed) {
		if (this.isCollapsed == isCollapsed)
			return;
		this.isCollapsed = isCollapsed;
		if (!config.isInteractive())
			return;
		final GLElement buttons = this.get(BUTTONS);
		GLButton collapseButton = findCollapseButton((ButtonBar) buttons);
		if (collapseButton != null)
			collapseButton.setSelected(isCollapsed);
		if (isCollapsed) {
			this.get(DRAG_WEIGHT).setVisibility(EVisibility.HIDDEN);
			buttons.setVisibility(EVisibility.HIDDEN);
			this.get(UNCOLLAPSE).setVisibility(EVisibility.PICKABLE);
		} else {
			this.get(DRAG_WEIGHT).setVisibility(config.canChangeWeights() ? EVisibility.PICKABLE : EVisibility.HIDDEN);
			buttons.setVisibility(EVisibility.VISIBLE);
			this.get(UNCOLLAPSE).setVisibility(EVisibility.HIDDEN);
		}
		repaintAll();
	}

	private GLButton findCollapseButton(ButtonBar buttons) {
		for (GLButton b : Iterables.filter(buttons, GLButton.class)) {
			if (b.getTooltip().startsWith("Toggle Collapse"))
				return b;
		}
		return null;
	}

	@Override
	protected void takeDown() {
		context.unregisterPickingListener(dragPickingId);
		dragPickingId = -1;
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
	public void layout(int deltaTimeMs) {
		super.layout(deltaTimeMs);

		boolean r = isDraggingAColumn();
		if (r != wasColumnDragging) {
			repaintPick();
			wasColumnDragging = r;
		}
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (config.isMoveAble() || config.isInteractive()) {
			g.incZ().incZ();
			g.pushName(dragPickingId);
			// RoundedRectRenderer.render(g, 0, 0, w, h, RenderStyle.HEADER_ROUNDED_RADIUS, 0,
			// RoundedRectRenderer.FLAG_FILL | RoundedRectRenderer.FLAG_TOP);
			if (isDraggingAColumn()) {
				float wi = RenderStyle.SEPARATOR_PICK_WIDTH - RenderStyle.COLUMN_SPACE;
				g.color(Color.BLUE).fillRect(wi * 0.5f, 0, w - wi, h).color(Color.BLACK);
			} else
				g.fillRect(0, 0, w, h);
			g.popName();

			boolean showButtonBar = isHovered && !isWeightDragging;
			if (showButtonBar && config.getButtonBarPosition() == EButtonBarPositionMode.ABOVE_LABEL) {
				g.fillRect(0, -RenderStyle.BUTTON_WIDTH, w, RenderStyle.BUTTON_WIDTH);
			} else if (showButtonBar && config.getButtonBarPosition() == EButtonBarPositionMode.BELOW_HIST) {
				g.fillRect(0, h, w, RenderStyle.BUTTON_WIDTH);
			}

			g.decZ().decZ();


		}

		super.renderPickImpl(g, w, h);
	}

	private boolean isDraggingAColumn() {
		IMouseLayer m = context.getMouseLayer();
		if (m.hasDraggable(ARankColumnModel.class))
			return true;
		if (getParent() instanceof StackedColumnHeaderUI) {
			return m.hasDraggable(AlignmentDragInfo.class);
		}
		return false;
	}

	protected void renderBackground(GLGraphics g, float w, float h) {
		config.renderHeaderBackground(g, w, h, LABEL_HEIGHT, model);
		renderOrderGlyph(g, w, h);

		if (isCollapsed)
			return;
		boolean small = isSmallHeader(h);

		if (config.isFastFiltering() && model instanceof IFilterColumnMixin && !(model instanceof DoubleRankColumnModel)
				&& ((IFilterColumnMixin) model).isFiltered()) {
			// draw a filter icon
			g.fillImage(RenderStyle.ICON_FILTER, w - 13, 1, 12, 12);
		}

		if (hasTitle && !(armDropColum && small)) {
			g.move(2, 2);
			model.getHeaderRenderer().render(g, w - 6, LABEL_HEIGHT - 7, this);
			g.move(-2, -2);
		}
		if (headerHovered) {
			g.drawRect(0, 0, w, h);
		}
		if (this.armDropColum) {
			g.incZ(0.6f);
			if (small) {
				// render as title hint
				g.drawText(armDropHint, 2, 2, w - 6, LABEL_HEIGHT - 6, VAlign.CENTER, ETextStyle.BOLD);
			} else {
				float hi = Math.min(h - 4, 18);
				g.drawText(armDropHint, 2, 2 + (h - hi) * .5f, w - 4, hi, VAlign.CENTER, ETextStyle.BOLD);
			}
			g.incZ(-0.6f);
		}
	}

	protected void renderOrderGlyph(GLGraphics g, float w, float h) {
		boolean o = model instanceof IRankableColumnMixin && model.getMyRanker().getOrderBy() == model;
		config.renderIsOrderByGlyph(g, w, h, o);
	}

	protected ButtonBar createButtons() {
		ButtonBar buttons = new ButtonBar();
		// buttons.setRenderer(GLRenderers.drawRoundedRect(Color.BLACK));
		buttons.setzDelta(.5f);

		// FIXME hack
		if (model instanceof IFilterColumnMixin && !(model instanceof DoubleRankColumnModel)) {
			final IFilterColumnMixin m = (IFilterColumnMixin) model;
			final GLButton b = new GLButton();
			b.setSelected(m.isFiltered());
			final ISelectionCallback callback = new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.editFilter(get(HIST), context);
				}
			};
			b.setCallback(callback);
			filterChangedListener = new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					b.setCallback(null);
					b.setSelected(m.isFiltered());
					b.setCallback(callback);
					repaint();
				}
			};
			model.addPropertyChangeListener(IFilterColumnMixin.PROP_FILTER, filterChangedListener);
			buttons.addButton(b, "Edit the filter of this column", RenderStyle.ICON_FILTER_DISABLED,
					RenderStyle.ICON_FILTER);
		}
		if (model instanceof RankRankColumnModel && !isFirst(model)) {
			final RankRankColumnModel m = (RankRankColumnModel) model;
			GLButton b = new GLButton(GLButton.EButtonMode.CHECKBOX);
			b.setSelected(m.isShowRankDelta());
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.setShowRankDelta(selected);
				}
			});
			buttons.addButton(b, "Show rank delta", RenderStyle.ICON_SHOW_RANK_DELTA, RenderStyle.ICON_HIDE_RANK_DELTA);

			b = new GLButton(GLButton.EButtonMode.BUTTON);
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.createDeltaColumn();
				}
			});
			buttons.addButton(b, "Add Rank Delta Column", RenderStyle.ICON_ADD_RANK_DELTA,
					RenderStyle.ICON_ADD_RANK_DELTA);
		}
		if (model instanceof ISearchableColumnMixin) {
			final ISearchableColumnMixin m = (ISearchableColumnMixin) model;
			GLButton b = new GLButton();
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.openSearchDialog(get(HIST), context);
				}
			});
			buttons.addButton(b, "Search for an item", RenderStyle.ICON_FIND, RenderStyle.ICON_FIND);
		}
		if (model instanceof IScriptedColumnMixin) {
			final IScriptedColumnMixin m = (IScriptedColumnMixin) model;
			GLButton b = new GLButton();
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.editCode(get(HIST), context);
				}
			});
			buttons.addButton(b, "Edit combination code", RenderStyle.ICON_EDIT_CODE, RenderStyle.ICON_EDIT_CODE);
		}
		if (model instanceof IMappedColumnMixin) {
			final IMappedColumnMixin m = (IMappedColumnMixin) model;
			GLButton b = new GLButton();
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.editMapping(get(HIST), context, config);
				}
			});
			buttons.addButton(b, "Edit the mapping of this column", RenderStyle.ICON_MAPPING, RenderStyle.ICON_MAPPING);
		}
		if (model instanceof IExplodeableColumnMixin) {
			final IExplodeableColumnMixin m = (IExplodeableColumnMixin) model;
			GLButton b = new GLButton();
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.explode();
				}
			});
			buttons.addButton(b, "Split this combined column in individual ones", RenderStyle.ICON_EXPLODE,
					RenderStyle.ICON_EXPLODE);
		}
		if (model instanceof IAnnotatedColumnMixin) {
			final IAnnotatedColumnMixin m = (IAnnotatedColumnMixin) model;
			GLButton b = new GLButton();
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.editAnnotation(get(HIST));
				}
			});
			buttons.addButton(b, "Edit label", RenderStyle.ICON_EDIT_ANNOTATION, RenderStyle.ICON_EDIT_ANNOTATION);
		}
		if (model instanceof ISnapshotableColumnMixin) {
			final ISnapshotableColumnMixin m = (ISnapshotableColumnMixin) model;
			final GLButton b = new GLButton();
			final ISelectionCallback callback = new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.takeSnapshot();
				}
			};
			b.setCallback(callback);
			buttons.addButton(b, "Take a snapshot of the current state", RenderStyle.ICON_FREEZE,
					RenderStyle.ICON_FREEZE);
		}
		buttons.addSpacer();

		if (model instanceof ICollapseableColumnMixin) {
			final ICollapseableColumnMixin m = (ICollapseableColumnMixin) model;
			if (m.isCollapseAble()) {
				GLButton b = new GLButton(EButtonMode.CHECKBOX);
				b.setSelected(m.isCollapsed());
				b.setCallback(new ISelectionCallback() {
					@Override
					public void onSelectionChanged(GLButton button, boolean selected) {
						m.setCollapsed(selected);
					}
				});
				buttons.addButton(b, "Toggle collapse / expand of this column", RenderStyle.ICON_UNCOLLAPSE,
						RenderStyle.ICON_COLLAPSE);
			}
		}
		if (model instanceof ICompressColumnMixin) {
			final ICompressColumnMixin m = (ICompressColumnMixin) model;

			GLButton b = new GLButton(EButtonMode.CHECKBOX);
			b.setSelected(m.isCompressed());
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					m.setCompressed(selected);
				}
			});
			buttons.addButton(b, "Toggle compress / unpack of this column", RenderStyle.ICON_COMPRESS,
					RenderStyle.ICON_UNCOMPRESS);
		}
		if (model instanceof IHideableColumnMixin) {
			final IHideableColumnMixin m = (IHideableColumnMixin) model;
			GLButton b = new GLButton();
			b.setCallback(new ISelectionCallback() {
				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					if (m.isHideAble())
						m.hide();
				}
			});
			buttons.addButton(b, "Remove this column", RenderStyle.ICON_HIDE, RenderStyle.ICON_HIDE);
		}
		return buttons;
	}

	/**
	 * @param model2
	 * @return
	 */
	private static boolean isFirst(ARankColumnModel m) {
		return m.getParent().indexOf(m) == 0;
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {

		IGLLayoutElement hist = children.get(HIST);
		final boolean smallHeader = isSmallHeader(h);
		if (smallHeader)
			hist.hide();
		else
			hist.setBounds(1, hasTitle ? LABEL_HEIGHT : 0, w - 2, h - (hasTitle ? LABEL_HEIGHT : 0));

		if (config.isInteractive()) {
			IGLLayoutElement weight = children.get(DRAG_WEIGHT);
			weight.setBounds(w, hasTitle && !smallHeader ? LABEL_HEIGHT : 0,
					(isHovered && config.canChangeWeights()) ? 8 : 0, h - (hasTitle && !smallHeader ? LABEL_HEIGHT : 0));

			{
				IGLLayoutElement buttons = children.get(BUTTONS);
				float minWidth = (buttons.asElement() instanceof ButtonBar) ? ((ButtonBar) buttons.asElement())
						.getMinWidth() : 0;
				// HACK for testing different button positions
				boolean showButtonBar = isHovered && !isWeightDragging;
				float yb = 0;
				switch (config.getButtonBarPosition()) {
				case AT_THE_BOTTOM:
					yb = isHovered ? (h - 2 - RenderStyle.BUTTON_WIDTH) : h;
					break;
				case OVER_LABEL: // at the top
					yb = 0;
					break;
				case UNDER_LABEL: // under the label
					yb = LABEL_HEIGHT;
					break;
				case ABOVE_LABEL: // above the label
					yb = showButtonBar ? -RenderStyle.BUTTON_WIDTH : 0;
					break;
				case BELOW_HIST: // below the histogram
					yb = isHovered ? h : h;
					break;
				}

				float hb = showButtonBar ? RenderStyle.BUTTON_WIDTH : 0;
				if ((w - 4) < minWidth) {
					float missing = minWidth - (w - 4);
					buttons.setBounds(-missing * 0.5f, yb, minWidth, hb);
				} else {
					buttons.setBounds(2, yb, w - 4, hb);
				}
			}
			{
				IGLLayoutElement uncollapse = children.get(UNCOLLAPSE);
				float yb = 0;
				switch (config.getButtonBarPosition()) {
				case AT_THE_BOTTOM:
					yb = isHovered ? (h - 2 - RenderStyle.BUTTON_WIDTH) : h;
					break;
				case OVER_LABEL: // at the top
					yb = 0;
					break;
				case UNDER_LABEL: // under the label
					yb = LABEL_HEIGHT;
					break;
				case ABOVE_LABEL: // above the label
					yb = isHovered ? -RenderStyle.BUTTON_WIDTH : 0;
					break;
				case BELOW_HIST: // below the histogram
					yb = isHovered ? h : h;
					break;
				}
				uncollapse.setBounds((w - RenderStyle.BUTTON_WIDTH) * .5f, yb, RenderStyle.BUTTON_WIDTH,
						isHovered ? RenderStyle.BUTTON_WIDTH : 0);
			}

			for (IGLLayoutElement r : children.subList(firstColumn(), children.size()))
				r.setBounds(defaultValue(r.getSetX(), 0), defaultValue(r.getSetY(), h),
						defaultValue(r.getSetWidth(), w), defaultValue(r.getSetHeight(), HIST_HEIGHT));
		}

	}

	/**
	 * @param pick
	 */
	protected void onDragPick(Pick pick) {
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
			context.getSWTLayer().setCursor(SWT.CURSOR_HAND);
			relayout();
			break;
		case MOUSE_OUT:
			if (this.headerHovered) {
				this.headerHovered = false;
				context.getSWTLayer().setCursor(-1);
				relayout();
			}
			break;
		default:
			break;
		}
	}

	protected void onMainPick(Pick pick) {
		if (context == null)
			return;
		IMouseLayer m = context.getMouseLayer();
		final IRankTableConfig tableConfig = model.getTable().getConfig();
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			if (config.isMoveAble() && !pick.isDoDragging() && m.hasDraggable(ARankColumnModel.class)) {
				Pair<GLElement, ARankColumnModel> pair = m.getFirstDraggable(ARankColumnModel.class);
				int mode = tableConfig.getCombineMode(model, pick);
				if (model.isCombineAble(pair.getSecond(), RenderStyle.isCloneDragging(pick), mode)) {
					m.setDropable(ARankColumnModel.class, true);
					this.armDropColum = true;
					armDropHint = tableConfig.getCombineStringHint(model, pair.getSecond(), mode);
					repaint();
				}
			} else if (!pick.isAnyDragging()) {
				this.isHovered = true;
				this.relayout();
			}
			break;
		case DRAGGED:
			if (this.armDropColum) {
				Pair<GLElement, ARankColumnModel> pair = m.getFirstDraggable(ARankColumnModel.class);
				int mode = tableConfig.getCombineMode(model, pick);
				String hint = tableConfig.getCombineStringHint(model, pair.getSecond(), mode);
				if (!Objects.equals(hint, armDropHint)) {
					this.armDropHint = hint;
					repaint();
				}
			}
			break;
		case MOUSE_OUT:
			if (armDropColum) {
				this.armDropColum = false;
				m.setDropable(ARankColumnModel.class, false);
				repaint();
			}
			if (this.isHovered) {
				this.isHovered = false;
				this.relayout();
			}
			break;
		case MOUSE_RELEASED:
			if (this.armDropColum) {
				Pair<GLElement, ARankColumnModel> info = m.getFirstDraggable(ARankColumnModel.class);
				if (info != null)
					m.removeDraggable(info.getFirst());
				m.setDropable(ARankColumnModel.class, false);
				context.getSWTLayer().setCursor(-1);
				if (info != null)
					model.combine(info.getSecond(), RenderStyle.isCloneDragging(pick),
							tableConfig.getCombineMode(model, pick));
			}
			break;
		case DOUBLE_CLICKED:
			if (model instanceof IRankableColumnMixin)
				((IRankableColumnMixin) model).orderByMe();
			break;
		case RIGHT_CLICKED:
			showContextMenu();
			break;
		default:
			break;
		}
	}

	private void showContextMenu() {
		ButtonBar bb = (ButtonBar) get(BUTTONS);
		IResourceLocator l = ResourceLocators.chain(ResourceLocators.classLoader(this.getClass().getClassLoader()),
				ResourceLocators.DATA_CLASSLOADER, ResourceLocators.FILE);
		List<AContextMenuItem> items = bb.asContextMenu(l);
		showContextMenu(items);
	}

	protected void showContextMenu(List<AContextMenuItem> items) {
		if (model instanceof IRankableColumnMixin) {
			if (getParent() instanceof StackedColumnHeaderUI) { // FIXME hack
				items.add(0, new GenericContextMenuItem("Align by this attribute", new OrderByMeEvent().to(this)));
				items.add(1, new GenericContextMenuItem("Order by this attribute", new OrderByMeEvent(true).to(this)));
			} else {
				items.add(0, new GenericContextMenuItem("Order by this attribute", new OrderByMeEvent().to(this)));
			}

		}
		context.getSWTLayer().showContextMenu(items);
	}

	@ListenTo(sendToMe = true)
	private void onOrderByMe(OrderByMeEvent event) {
		if (event.isCloneAndAddNext()) {
			assert getParent() instanceof StackedColumnHeaderUI;
			IRankColumnParent parent = model.getParent();
			RankTableModel table = parent.getTable();
			int index = table.getColumns().indexOf(parent);
			ARankColumnModel clone = model.clone();
			table.add(index + 1, clone);
			((IRankableColumnMixin) clone).orderByMe();
		} else {
			((IRankableColumnMixin) model).orderByMe();
		}
	}

	protected void onChangeWeight(float dx, boolean takeFromRight) {
		if (dx == 0)
			return;
		// float delta = (dx / getSize().x())*;
		model.setWidth(Math.max(model.getWidth() + dx, 0));
	}

	/**
	 * drop drag column again
	 *
	 * @param pick
	 */
	private void onDropColumn(Pick pick) {
		IMouseLayer l = context.getMouseLayer();
		if (this.isDragging) {
			if (!l.isDropable(this.model)) {
				l.removeDraggable(this.model);
			}
			this.isDragging = false;
			context.getSWTLayer().setCursor(-1);
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
			if (AColumnHeaderUI.this.getParent() != null)
				renderBackground(g, w, h);
			if (get(HIST).getParent() != null)
				get(HIST).render(g);
		}
	}

	class DragElement extends PickableGLElement {
		private boolean hovered = false;

		public DragElement() {
			setRenderer(GLRenderers.fillImage(RenderStyle.ICON_DRAG));
			setTooltip("Drag this element to change the weight of this column");
			setzDelta(.5f);
		}

		@Override
		protected void onMouseOver(Pick pick) {
			if (pick.isAnyDragging())
				return;
			this.hovered = true;
			context.getSWTLayer().setCursor(SWT.CURSOR_HAND);
			repaintAll();
		}

		@Override
		protected void onMouseOut(Pick pick) {
			if (this.hovered)
				context.getSWTLayer().setCursor(-1);
			this.hovered = false;
			repaintAll();
		}


		@Override
		protected void onClicked(Pick pick) {
			if (pick.isAnyDragging())
				return;
			pick.setDoDragging(true);
			isWeightDragging = true;
			relayoutParent();
		}

		@Override
		protected void onDragged(Pick pick) {
			if (pick.isDoDragging())
				onChangeWeight(pick.getDx(),
						(pick instanceof AdvancedPick ? ((AdvancedPick) pick).isCtrlDown() : false));
		}

		@Override
		protected void onMouseReleased(Pick pick) {
			if (!pick.isDoDragging())
				return;
			isWeightDragging = false;
			relayoutParent();
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
