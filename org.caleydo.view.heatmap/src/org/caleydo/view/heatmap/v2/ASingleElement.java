/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import gleem.linalg.Vec2f;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
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
public abstract class ASingleElement extends PickableGLElement implements IHasMinSize, IDataChangedCallback,
		IPickingLabelProvider {
	protected final DimensionRenderer renderer;
	@DeepScan
	protected final IHeatMapDataProvider data;
	private final EDimension dim;

	private ESelectionStrategy selectionHoverStrategy = ESelectionStrategy.OUTLINE;
	private ESelectionStrategy selectionSelectedStrategy = ESelectionStrategy.OUTLINE;
	private Color frameColor = null;

	public ASingleElement(IHeatMapDataProvider data, EDetailLevel detailLevel, EDimension dim) {
		this.data = data;
		this.dim = dim;
		this.data.setCallback(this);
		detailLevel = Objects.firstNonNull(detailLevel, EDetailLevel.LOW);

		this.renderer = new DimensionRenderer(data, dim);

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

	/**
	 * @param showFrame
	 *            setter, see {@link showFrame}
	 */
	public void setFrameColor(Color frameColor) {
		if (Objects.equal(this.frameColor, frameColor))
			return;
		this.frameColor = frameColor;
		repaint();
	}

	/**
	 * @return the frameColor, see {@link #frameColor}
	 */
	public Color getFrameColor() {
		return frameColor;
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

	/**
	 * @return the dim, see {@link #dim}
	 */
	public EDimension getDimension() {
		return dim;
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

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		onPick(context.getSWTLayer().createTooltip(this));
	}

	@Override
	public void onDataUpdate() {
		repaintAll();
	}

	@Override
	public void onSelectionUpdate() {
		repaint();
	}

	/**
	 * @return the recommended min size of this heatmap
	 */
	@Override
	public final Vec2f getMinSize() {
		float v = renderer.minSize();
		float o = 20;
		float w = dim.select(v, o);
		float h = dim.select(o, v);
		return new Vec2f(w, h);
	}

	/**
	 *
	 * @return whether in both dimension it is a unfirm rendering
	 */
	protected final boolean isUniform() {
		return renderer.isUniformSpacing();

	}

	protected List<Integer> getData() {
		return renderer.getData();
	}

	public final ISpacingStrategy getSpacingStrategy() {
		return renderer.getSpacingStrategy();
	}

	/**
	 * @param recordSpacingStrategy
	 *            setter, see {@link recordSpacingStrategy}
	 */
	public void setSpacingStrategy(ISpacingStrategy spacingStrategy) {
		if (renderer.getSpacingStrategy() == spacingStrategy)
			return;
		renderer.setSpacingStrategy(spacingStrategy);
		relayout();
	}

	@Override
	protected void layoutImpl(int deltaTimeMs) {
		// compute the layout
		renderer.updateSpacing(dim.select(getSize()));
	}

	public final GLLocation getLocation(EDimension dim, int index) {
		if (dim == this.dim)
			return renderer.getLocation(index, 0);
		return GLLocation.UNKNOWN;
	}

	public final Set<Integer> forLocation(EDimension dim, GLLocation location) {
		if (dim == this.dim)
			return renderer.forLocation(location, 0);
		return GLLocation.UNKNOWN_IDS;
	}

	public final int getIndex(EDimension dim, float position) {
		if (dim == this.dim)
			return renderer.getIndex(position, 0);
		return -1;
	}

	@Override
	protected final void renderImpl(GLGraphics g, float w, float h) {
		g.save();

		render(g, w, h, renderer.getSpacing());

		g.incZ();
		{ // as selection rects
			renderSelection(selectionSelectedStrategy, SelectionType.SELECTION, g, w, h);
			renderSelection(selectionHoverStrategy, SelectionType.MOUSE_OVER, g, w, h);
		}
		g.lineWidth(1);
		g.decZ();

		if (frameColor != null)
			g.color(frameColor).drawRect(0, 0, w, h);

		g.restore();
	}

	private void renderSelection(ESelectionStrategy strategy, SelectionType type, GLGraphics g, float w, float h) {
		int size = renderer.getData().size();
		boolean showOutline = HeatMapElementBase.shouldShowOutline(w, h, dim.select(size, 1), dim.select(1, size));
		if (strategy == ESelectionStrategy.AUTO_BLUR_OUTLINE)
			strategy = showOutline ? ESelectionStrategy.OUTLINE : ESelectionStrategy.BLUR;
		if (strategy == ESelectionStrategy.AUTO_FILL_OUTLINE)
			strategy = showOutline ? ESelectionStrategy.OUTLINE : ESelectionStrategy.FILL;

		switch (strategy) {
		case OUTLINE:
			renderer.renderSelectionRects(g, type, w, h, false);
			break;
		case BLUR:
			renderBlurSelection(g, type, w, h);
			break;
		case FILL:
			renderer.renderSelectionRects(g, type, w, h, true);
			break;
		default:
			break; // shouldn't happen
		}
	}

	private void renderBlurSelection(GLGraphics g, SelectionType type, float w, float h) {
		List<Vec2f> dims = renderer.getNotSelectedRanges(type, w, h);
		if (dims == null) // nothing selected or hoverded
			return;
		dims = dims == null ? Collections.singletonList(new Vec2f(0, w)) : dims;
		g.color(1, 1, 1, 0.5f);
		if (dim.isHorizontal()) {
			for (Vec2f dim : dims)
				g.fillRect(dim.x(), 0, dim.y(), h);
		} else {
			for (Vec2f dim : dims)
				g.fillRect(0, dim.x(), w, dim.y());
		}
	}

	protected abstract void render(GLGraphics g, float w, float h, ISpacingLayout spacing);

	@Override
	protected final void onClicked(Pick pick) {
		IndexedId id = toId(pick);
		boolean repaint = false;
		boolean isCTRLDown = ((IMouseEvent) pick).isCtrlDown();

		if (id.getId() != null) {
			renderer.select(SelectionType.SELECTION, !isCTRLDown, id);
			repaint = true;
		}
		if (repaint)
			repaint();
	}

	@Override
	protected void onDragDetected(Pick pick) {
		if (!pick.isAnyDragging())
			pick.setDoDragging(true);
	}

	@Override
	protected final void onMouseMoved(Pick pick) {
		IndexedId id = toId(pick);
		boolean repaint = false;
		if (!Objects.equal(id, renderer.getHoveredID())) {
			renderer.select(SelectionType.MOUSE_OVER, true, id);
			repaint = true;
		}
		if (repaint)
			repaint();
	}

	@Override
	public String getLabel(Pick pick) {
		IndexedId id = toId(pick);
		if (id == null)
			return "";
		if (dim.isRecord())
			return this.data.getLabel(id.getId(), -1);
		else
			return this.data.getLabel(-1, id.getId());
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
		IndexedId id = toId(pick);

		boolean repaint = false;
		if (id.getId() != null && !Objects.equal(id, renderer.getHoveredID())) {
			renderer.drag(id);
			repaint = true;
		}
		if (repaint)
			repaint();
	}

	@Override
	protected final void onMouseOut(Pick pick) {
		// clear all hovered elements
		renderer.clear(SelectionType.MOUSE_OVER);
		repaint();
	}

	/**
	 * computes out of the given pick the corresponding dimension and record picking ids
	 *
	 * @param pick
	 * @return
	 */
	private IndexedId toId(Pick pick) {
		Vec2f point = toRelative(pick.getPickedPoint());
		return renderer.getIndexedId(dim.select(point), 0);
	}

	@ListenTo
	private void onColorMappingUpdate(UpdateColorMappingEvent event) {
		repaint();
	}

}
