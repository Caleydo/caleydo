/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import gleem.linalg.Vec2f;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.heatmap.v2.ISpacingStrategy.ISpacingLayout;
import org.caleydo.view.heatmap.v2.internal.DimensionRenderer;
import org.caleydo.view.heatmap.v2.internal.IHeatMapDataProvider;
import org.caleydo.view.heatmap.v2.internal.IHeatMapDataProvider.IDataChangedCallback;
import org.caleydo.view.heatmap.v2.internal.IHeatMapRenderer;
import org.caleydo.view.heatmap.v2.internal.IndexedId;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * a generic heat map implemenation
 *
 * @author Samuel Gratzl
 *
 */
public class HeatMapElementBase extends PickableGLElement implements IHasMinSize, IDataChangedCallback,
		IPickingLabelProvider {
	private static final int TEXT_OFFSET = 5;
	/**
	 * maximum pixel size of a text
	 */
	private static final int MAX_TEXT_HEIGHT = 12;

	private final static int TEXT_WIDTH = 80; // [px]

	protected final DimensionRenderer record;
	protected final DimensionRenderer dimension;

	private int textWidth = TEXT_WIDTH;

	private boolean renderGroupHints = false;
	@DeepScan
	private final IHeatMapRenderer renderer;
	@DeepScan
	private final IHeatMapDataProvider data;

	private ESelectionStrategy selectionHoverStrategy = ESelectionStrategy.OUTLINE;
	private ESelectionStrategy selectionSelectedStrategy = ESelectionStrategy.OUTLINE;

	public HeatMapElementBase(IHeatMapDataProvider data, IHeatMapRenderer renderer, EDetailLevel detailLevel) {
		this.data = data;
		this.data.setCallback(this);
		this.renderer = renderer;
		detailLevel = Objects.firstNonNull(detailLevel, EDetailLevel.LOW);

		this.dimension = new DimensionRenderer(data, EDimension.DIMENSION);
		this.record = new DimensionRenderer(data, EDimension.RECORD);

		switch (detailLevel) {
		case HIGH:
		case MEDIUM:
			setVisibility(EVisibility.PICKABLE); //pickable + no textures
			break;
		default:
			setVisibility(EVisibility.VISIBLE); // not pickable + textures
			break;
		}
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		if (clazz.isAssignableFrom(Vec2f.class))
			return clazz.cast(getMinSize());
		T r;
		if ((r = GLLayouts.resolveLayoutDatas(clazz, Suppliers.<T> ofInstance(null), renderer, data)) != null)
			return r;
		return super.getLayoutDataAs(clazz, default_);
	}

	@Override
	public void onDataUpdate() {
		updateRenderer();
		repaintAll();
	}

	@Override
	public void onSelectionUpdate() {
		repaint();
	}

	private DimensionRenderer get(EDimension dim) {
		return dim.select(dimension, record);
	}

	/**
	 * @return the recommended min size of this heatmap
	 */
	@Override
	public final Vec2f getMinSize() {
		Vec2f v = getMinSizeImpl();
		if (record.getLabel().show())
			v.setX(v.x() + textWidth);
		if (dimension.getLabel().show())
			v.setY(v.y() + textWidth);
		return v;
	}

	protected Vec2f getMinSizeImpl() {
		float w = dimension.minSize();
		float h = record.minSize();
		return new Vec2f(w, h);
	}

	/**
	 * @param selectionStrategy
	 *            setter, see {@link selectionStrategy}
	 */
	public void setSelectionHoverStrategy(ESelectionStrategy selectionStrategy) {
		if (this.selectionHoverStrategy == selectionStrategy)
			return;
		this.selectionHoverStrategy = selectionStrategy;
		repaint();
	}

	/**
	 * @return the selectionStrategy, see {@link #selectionStrategy}
	 */
	public ESelectionStrategy getSelectionHoverStrategy() {
		return selectionHoverStrategy;
	}

	/**
	 * @param selectionStrategy
	 *            setter, see {@link selectionStrategy}
	 */
	public void setSelectionSelectedStrategy(ESelectionStrategy selectionStrategy) {
		if (this.selectionSelectedStrategy == selectionStrategy)
			return;
		this.selectionSelectedStrategy = selectionStrategy;
		repaint();
	}

	/**
	 * @return the selectionStrategy, see {@link #selectionStrategy}
	 */
	public ESelectionStrategy getSelectionSelectedStrategy() {
		return selectionSelectedStrategy;
	}

	/**
	 *
	 * @return whether in both dimension it is a unfirm rendering
	 */
	protected final boolean isUniform() {
		return record.isUniformSpacing() && dimension.isUniformSpacing();

	}
	/**
	 * @param textWidth
	 *            setter, see {@link textWidth}
	 */
	public void setTextWidth(int textWidth) {
		if (textWidth == this.textWidth)
			return;
		this.textWidth = textWidth;
		relayout();
	}

	/**
	 * @return the textWidth, see {@link #textWidth}
	 */
	public int getTextWidth() {
		return textWidth;
	}

	/**
	 * @param showDimensionLabels
	 *            setter, see {@link showDimensionLabels}
	 */
	public final void setLabel(EDimension dim, EShowLabels value) {
		DimensionRenderer r = get(dim);
		if (r.getLabel() == value)
			return;
		r.setLabel(value);
		relayout();
		relayoutParent();
	}

	public final EShowLabels getLabel(EDimension dim) {
		return get(dim).getLabel();
	}

	protected List<Integer> getData(EDimension dim) {
		return get(dim).getData();
	}

	public final ISpacingStrategy getSpacingStrategy(EDimension dim) {
		return get(dim).getSpacingStrategy();
	}

	/**
	 * @param recordSpacingStrategy
	 *            setter, see {@link recordSpacingStrategy}
	 */
	public void setSpacingStrategy(EDimension dim, ISpacingStrategy spacingStrategy) {
		DimensionRenderer r = get(dim);
		if (r.getSpacingStrategy() == spacingStrategy)
			return;
		r.setSpacingStrategy(spacingStrategy);
		relayout();
	}

	@Override
	protected void layoutImpl(int deltaTimeMs) {
		Vec2f size = getSize().copy();
		if (record.getLabel().show()) {
			size.setX(size.x() - textWidth);
		}
		if (dimension.getLabel().show()) {
			size.setY(size.y() - textWidth);
		}
		// compute the layout
		dimension.updateSpacing(size.x());
		record.updateSpacing(size.y());
	}

	public final GLLocation getLocation(EDimension dim, int index) {
		return get(dim).getLocation(index, textWidth);
	}

	public final Set<Integer> forLocation(EDimension dim, GLLocation location) {
		return get(dim).forLocation(location, textWidth);
	}

	public final int getIndex(EDimension dim, float position) {
		return get(dim).getIndex(position, textWidth);
	}

	@Override
	protected final void renderImpl(GLGraphics g, float w, float h) {
		g.save();
		switch (record.getLabel()) {
		case LEFT:
			w -= textWidth;
			g.move(textWidth, 0);
			break;
		case RIGHT:
			w -= textWidth;
			break;
		default:
			break;
		}
		switch (dimension.getLabel()) {
		case LEFT:
			h -= textWidth;
			g.move(0, textWidth);
			break;
		case RIGHT:
			h -= textWidth;
			break;
		default:
			break;
		}

		if (record.getLabel().show()) {
			final List<Integer> data = record.getData();
			final ISpacingLayout spacing = record.getSpacing();
			final EShowLabels label = record.getLabel();

			for (int i = 0; i < data.size(); ++i) {
				Integer recordID = data.get(i);
				float y = spacing.getPosition(i);
				float fieldHeight = spacing.getSize(i);
				float textHeight = Math.min(fieldHeight, MAX_TEXT_HEIGHT);
				String text = getLabel(EDimension.RECORD, recordID);
				if (label == EShowLabels.LEFT)
					g.drawText(text, -textWidth, y + (fieldHeight - textHeight) * 0.5f, textWidth - TEXT_OFFSET,
							textHeight, VAlign.RIGHT);
				else
					g.drawText(text, w + TEXT_OFFSET, y + (fieldHeight - textHeight) * 0.5f, textWidth - TEXT_OFFSET,
							textHeight, VAlign.LEFT);
			}
		}

		if (dimension.getLabel().show()) {
			final List<Integer> data = dimension.getData();
			final ISpacingLayout spacing = dimension.getSpacing();
			final EShowLabels label = dimension.getLabel();

			g.save();
			g.gl.glRotatef(-90, 0, 0, 1);
			for (int i = 0; i < data.size(); ++i) {
				Integer dimensionID = data.get(i);
				String l = getLabel(EDimension.DIMENSION, dimensionID);
				float x = spacing.getPosition(i);
				float fieldWidth = spacing.getSize(i);
				float textHeight = Math.min(fieldWidth, MAX_TEXT_HEIGHT);
				if (textHeight < 5)
					continue;
				if (label == EShowLabels.LEFT)
					g.drawText(l, TEXT_OFFSET, x + (fieldWidth - textHeight) * 0.5f, textWidth - TEXT_OFFSET,
							textHeight, VAlign.LEFT);
				else
					g.drawText(l, -h - textWidth, x + (fieldWidth - textHeight) * 0.5f, textWidth - TEXT_OFFSET,
							textHeight, VAlign.RIGHT);
			}
			g.restore();
		}

		renderAddons(g, w, h);

		renderer.render(g, w, h, record.getSpacing(), dimension.getSpacing());

		g.incZ();
		renderSelection(selectionSelectedStrategy, SelectionType.SELECTION, g, w, h);
		renderSelection(selectionHoverStrategy, SelectionType.MOUSE_OVER, g, w, h);

		g.lineWidth(1);
		g.decZ();

		g.restore();
	}

	private void renderSelection(ESelectionStrategy strategy, SelectionType type, GLGraphics g, float w, float h) {
		boolean showOutline = shouldShowOutline(w, h, dimension.getData().size(), record.getData().size());
		if (strategy == ESelectionStrategy.AUTO_BLUR_OUTLINE)
			strategy = showOutline ? ESelectionStrategy.OUTLINE : ESelectionStrategy.BLUR;
		if (strategy == ESelectionStrategy.AUTO_FILL_OUTLINE)
			strategy = showOutline ? ESelectionStrategy.OUTLINE : ESelectionStrategy.FILL;

		switch (strategy) {
		case OUTLINE:
			record.renderSelectionRects(g, type, w, h, false);
			dimension.renderSelectionRects(g, type, w, h, false);
			break;
		case BLUR:
			renderBlurSelection(g, type, w, h);
			break;
		case FILL:
			record.renderSelectionRects(g, type, w, h, true);
			dimension.renderSelectionRects(g, type, w, h, true);
			break;
		default:
			break; // shouldn't happen
		}
	}

	/**
	 * determine whether in automatic mode the outlines or something different should be shown
	 *
	 * @return
	 */
	static boolean shouldShowOutline(float w, float h, int cols, int rows) {
		float wi = w / cols;
		float hi = h / rows;
		return wi > 5 && hi > 5;
	}

	/**
	 * @param g
	 * @param type
	 * @param w
	 * @param h
	 */
	private void renderBlurSelection(GLGraphics g, SelectionType type, float w, float h) {
		List<Vec2f> dims = dimension.getNotSelectedRanges(type, w, h);
		List<Vec2f> recs = record.getNotSelectedRanges(type, w, h);
		if (dims == null && recs == null) // nothing selected or hoverded
			return;
		dims = dims == null ? Collections.singletonList(new Vec2f(0, w)) : dims;
		recs = recs == null ? Collections.singletonList(new Vec2f(0, h)) : recs;
		g.color(1, 1, 1, 0.5f);
		for (Vec2f dim : dims)
			for (Vec2f rec : recs)
				g.fillRect(dim.x(), rec.x(), dim.y(), rec.y());
	}

	/**
	 * @param g
	 * @param w
	 * @param h
	 */
	private void renderAddons(GLGraphics g, float w, float h) {

		List<Group> dims = data.getGroups(EDimension.DIMENSION);
		List<Group> recs = data.getGroups(EDimension.RECORD);
		if (renderGroupHints) {
			g.color(Color.BLACK).lineWidth(2);
			g.incZ();
			renderGroupHints(g, recs, true, record.getSpacing(), w);
			renderGroupHints(g, dims, false, dimension.getSpacing(), h);
			g.decZ();
			g.lineWidth(1);
		}
	}

	private String getLabel(EDimension dim, Integer id) {
		return data.getLabel(dim, id);
	}

	private void renderGroupHints(GLGraphics g, Collection<Group> groupStarts, boolean isRecord,
			ISpacingLayout spacing,
			float total) {
		// indicate the grouping borders by shading
		if (groupStarts == null || groupStarts.size() <= 1)
			return;

		for (Group group : groupStarts) {
			if (group.getSize() <= 0)
				continue;
			int start = group.getStartIndex();
			if (start == 0) // no left border
				continue;
			float y = spacing.getPosition(start);
			if (isRecord)
				g.drawLine(0, y, total, y);
			else
				g.drawLine(y, 0, y, total);
		}
	}

	@Override
	protected final void onClicked(Pick pick) {
		Pair<IndexedId, IndexedId> ids = toDimensionRecordIds(pick);
		boolean repaint = false;
		boolean isCTRLDown = ((IMouseEvent) pick).isCtrlDown();

		for (EDimension dim : EDimension.values()) {
			IndexedId id = dim.select(ids);
			if (id.getId() != null) {
				get(dim).select(SelectionType.SELECTION, !isCTRLDown, id);
				repaint = true;
			}
		}
		if (repaint)
			repaint();
	}

	@Override
	public String getLabel(Pick pick) {
		Pair<IndexedId, IndexedId> ids = toDimensionRecordIds(pick);
		if (ids == null || ids.getFirst() == null || ids.getSecond() == null)
			return "";
		return this.data.getLabel(ids.getSecond().getId(), ids.getFirst().getId());
	}

	@Override
	protected void onDragDetected(Pick pick) {
		if (!pick.isAnyDragging())
			pick.setDoDragging(true);
	}

	@Override
	protected final void onMouseMoved(Pick pick) {
		Pair<IndexedId, IndexedId> ids = toDimensionRecordIds(pick);
		boolean repaint = false;
		for (EDimension dim : EDimension.values()) {
			IndexedId id = dim.select(ids);
			DimensionRenderer r = get(dim);
			if (!Objects.equal(id, r.getHoveredID())) {
				r.select(SelectionType.MOUSE_OVER, true, id);
				repaint = true;
			}
		}
		if (repaint)
			repaint();
	}

	@Override
	protected final void onMouseReleased(Pick pick) {
		if (!pick.isDoDragging())
			return;
	}

	@Override
	protected final void onDragged(Pick pick) {
		if (!pick.isDoDragging())
			return;
		Pair<IndexedId, IndexedId> ids = toDimensionRecordIds(pick);

		boolean repaint = false;
		for (EDimension dim : EDimension.values()) {
			IndexedId id = dim.select(ids);
			DimensionRenderer r = get(dim);
			if (id.getId() != null && !Objects.equal(id, r.getHoveredID())) {
				r.drag(id);
				repaint = true;
			}
		}
		if (repaint)
			repaint();
	}

	@Override
	protected final void onMouseOut(Pick pick) {
		// clear all hovered elements
		dimension.clear(SelectionType.MOUSE_OVER);
		record.clear(SelectionType.MOUSE_OVER);
		repaint();
	}

	/**
	 * computes out of the given pick the corresponding dimension and record picking ids
	 *
	 * @param pick
	 * @return
	 */
	private Pair<IndexedId, IndexedId> toDimensionRecordIds(Pick pick) {
		Vec2f point = toRelative(pick.getPickedPoint());
		IndexedId d = dimension.getIndexedId(point.x(), textWidth);
		IndexedId r = record.getIndexedId(point.y(), textWidth);
		return Pair.make(d, r);
	}

	/**
	 * @param b
	 */
	public void setRenderGroupHints(boolean renderGroupHints) {
		if (this.renderGroupHints == renderGroupHints)
			return;
		this.renderGroupHints = renderGroupHints;
		repaint();
	}

	@ListenTo
	private void onColorMappingUpdate(UpdateColorMappingEvent event) {
		updateRenderer();
		repaint();
	}

	@Override
	protected void takeDown() {
		renderer.takeDown();
		super.takeDown();
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		onPick(context.getSWTLayer().createTooltip(this));
		updateRenderer();
	}

	/**
	 *
	 */
	protected final void updateRenderer() {
		if (context != null)
			renderer.update(context, getData(EDimension.DIMENSION), getData(EDimension.RECORD), this);
	}

}
