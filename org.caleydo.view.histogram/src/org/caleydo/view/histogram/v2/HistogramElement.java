/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.RedrawViewEvent;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.ColorMapper;
import org.caleydo.core.util.color.mapping.ColorMarkerPoint;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.util.PickingPool;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.histogram.HistogramRenderStyle;

/**
 * Rendering the histogram.
 *
 * @author Samuel Gratzl
 */
public class HistogramElement extends GLElement implements
		TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback {
	private static float[] SPREAD_LINE_COLOR = { 0.5f, 0.5f, 0.5f };

	@DeepScan
	protected final TablePerspectiveSelectionMixin mixin;

	private PickingPool leftSpreadPickingIds;
	private PickingPool rightSpreadPickingIds;

	private final EDetailLevel detailLevel;

	public HistogramElement(TablePerspective tablePerspective) {
		this(tablePerspective, EDetailLevel.HIGH);
	}

	public HistogramElement(TablePerspective tablePerspective, EDetailLevel detailLevel) {
		this.mixin = new TablePerspectiveSelectionMixin(tablePerspective, this);
		this.detailLevel = detailLevel;
		setPicker(null);
		setVisibility(detailLevel.ordinal() > EDetailLevel.LOW.ordinal() ? EVisibility.PICKABLE : EVisibility.VISIBLE);
	}

	@Override
	protected boolean hasPickAbles() {
		return super.hasPickAbles() || detailLevel.ordinal() > EDetailLevel.LOW.ordinal();
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		leftSpreadPickingIds = new PickingPool(context, new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onLinePicked(pick, getDataDomain(), true);
			}
		});
		rightSpreadPickingIds = new PickingPool(context, new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onLinePicked(pick, getDataDomain(), false);
			}
		});
		int points = getDataDomain().getTable().getColorMapper().getMarkerPoints().size();
		leftSpreadPickingIds.ensure(0, points);
		rightSpreadPickingIds.ensure(0, points);
	}

	@Override
	protected void takeDown() {
		leftSpreadPickingIds.clear();
		leftSpreadPickingIds = null;
		rightSpreadPickingIds.clear();
		rightSpreadPickingIds = null;
		super.takeDown();
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		repaintAll();
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		repaintAll();
	}

	private TablePerspective getTablePerspective() {
		return mixin.getTablePerspective();
	}

	private ATableBasedDataDomain getDataDomain() {
		return getTablePerspective().getDataDomain();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		float padding = getPadding();
		ATableBasedDataDomain dataDomain = getDataDomain();
		g.save();
		g.move(padding, padding);
		renderHist(g, getTablePerspective().getContainerStatistics().getHistogram(), w - padding * 2, h - padding * 2,
				dataDomain.getTable().getColorMapper());
		g.restore();

		if (detailLevel.ordinal() > EDetailLevel.LOW.ordinal())
			renderColorMapper(g, w, h, dataDomain.getTable().getColorMapper());
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (detailLevel.ordinal() > EDetailLevel.LOW.ordinal() && getVisibility() == EVisibility.PICKABLE)
			renderColorMapperPick(g, w, h, getDataDomain().getTable().getColorMapper());
		super.renderPickImpl(g, w, h);
	}

	public static void renderHist(GLGraphics g, Histogram hist, float w, float h, ColorMapper mapper) {

		float factor = h / hist.getLargestValue();
		float delta = w / hist.size();
		float colorDelta = 1.f / (hist.size() - 1);
		g.gl.glPushAttrib(GL2.GL_LINE_BIT);

		final float lineWidth = Math.min(delta - 1, 25);
		final float lineWidthHalf = lineWidth * 0.5f;
		if (lineWidth < 10)
			g.lineWidth(lineWidth);
		float x = delta / 2;

		g.color(Color.GRAY);
		for (int i = 0; i < hist.size(); ++i) {
			if (mapper != null) {
				g.color(mapper.getColor(i * colorDelta));
			}
			float v = -hist.get(i) * factor;

			if (v <= -1) {
				if (lineWidth < 10)
					g.drawLine(x, 0, x, v);
				else
					g.fillRect(x - lineWidthHalf, 0, lineWidth, v);
			}
			x += delta;
		}
		g.gl.glPopAttrib();
	}

	private float getPadding() {
		float padding;
		if (detailLevel == EDetailLevel.LOW || detailLevel == EDetailLevel.MEDIUM) {
			padding = HistogramRenderStyle.SIDE_SPACING_DETAIL_LOW;
		} else {
			padding = HistogramRenderStyle.SIDE_SPACING;
		}
		return padding;
	}


	@ListenTo
	private void onColorMappingUpdate(UpdateColorMappingEvent event) {
		repaint();
	}
	/**
	 * Render the color bars for selecting the color mapping
	 *
	 * @param gl
	 */
	private void renderColorMapper(GLGraphics g, float w, float h, ColorMapper mapper) {
		final float padding = getPadding();
		w = w - 2 * padding;
		h = h - 2 * padding;
		g.save();
		g.move(padding,padding);

		List<ColorMarkerPoint> markerPoints = mapper.getMarkerPoints();

		for(int i = 0; i < markerPoints.size(); ++i) {
			ColorMarkerPoint markerPoint = markerPoints.get(i);
			// the left polygon between the central line and the spread
			org.caleydo.core.util.color.Color color = markerPoint.getColor();
			g.color(color.r,color.g,color.b,0.3f);

			final float v = markerPoint.getMappingValue();

			if (markerPoint.hasLeftSpread()) {
				float spread = markerPoint.getLeftSpread();

				float from = (v - spread) * w;
				float to = v * w;

				g.fillRect(from,0,to-from, h);

				// the left spread line
				g.color(SPREAD_LINE_COLOR);
				g.incZ();
				g.drawLine(from,0,from,h);
				g.decZ();
			}

			if (markerPoint.hasRightSpread()) {
				float spread = markerPoint.getRightSpread();

				float from = v * w;
				float to = (v + spread) * w;

				g.fillRect(from, 0, to - from, h);

				// the right spread line
				g.color(SPREAD_LINE_COLOR);
				g.incZ();
				g.drawLine(to, 0, to, h);
				g.decZ();
			}
		}

		g.restore();

	}

	/**
	 * Render the color bars for selecting the color mapping
	 *
	 * @param gl
	 */
	private void renderColorMapperPick(GLGraphics g, float w, float h, ColorMapper mapper) {
		final float padding = getPadding();
		w = w - 2 * padding;
		h = h - 2 * padding;
		g.save();
		g.move(padding, padding);

		List<ColorMarkerPoint> markerPoints = mapper.getMarkerPoints();
		g.incZ();

		for (int i = 0; i < markerPoints.size(); ++i) {
			ColorMarkerPoint markerPoint = markerPoints.get(i);
			final float v = markerPoint.getMappingValue();

			if (markerPoint.hasLeftSpread()) {
				float spread = markerPoint.getLeftSpread();
				float from = (v - spread) * w;
				g.pushName(leftSpreadPickingIds.get(i));
				g.drawLine(from, 0, from, h);
				g.popName();
			}

			if (markerPoint.hasRightSpread()) {
				float spread = markerPoint.getRightSpread();
				float to = (v + spread) * w;
				g.pushName(rightSpreadPickingIds.get(i));
				g.drawLine(to, 0, to, h);
				g.popName();
			}
		}
		g.decZ();

		g.restore();

	}

	private void onLinePicked(Pick pick, ATableBasedDataDomain dataDomain, boolean isLeftSpread) {
		switch (pick.getPickingMode()) {
		case MOUSE_RELEASED:
			EventPublisher.trigger(new UpdateColorMappingEvent().from(this));
			break;
		case CLICKED:
			pick.setDoDragging(true);
			break;
		case DRAGGED:
			List<ColorMarkerPoint> markers = dataDomain.getTable().getColorMapper().getMarkerPoints();
			final int selected = pick.getObjectID();
			ColorMarkerPoint point = markers.get(selected);
			final float dv = pick.getDx() / (getSize().x() - getPadding() * 2);
			//clamp values in the neighbor range
			float v = dv;
			if (isLeftSpread) {
				v += point.getLeftSpread();
				if (v < 0.01f)
					v = 0.01f;
				if (selected > 0) {
					ColorMarkerPoint prev = markers.get(selected-1);
					v = Math.max(v, point.getMappingValue()-(prev.getMappingValue()+prev.getRightSpread()+0.01f));
				}
				if (v != point.getLeftSpread()) {
					point.setLeftSpread(v);
					updateMapping(dataDomain);
				}
			} else {
				v += point.getRightSpread();
				if (v < 0.01f)
					v = 0.01f;
				if (selected < markers.size()-1) {
					ColorMarkerPoint next = markers.get(selected+1);
					v = Math.min(v, -point.getMappingValue() + (next.getMappingValue()-next.getLeftSpread()-0.01f));
				}
				if (v != point.getRightSpread()) {
					point.setRightSpread(v);
					updateMapping(dataDomain);
				}
			}
			break;
		default:
			break;
		}
	}

	private void updateMapping(ATableBasedDataDomain dataDomain) {
		dataDomain.getTable().getColorMapper().update();
		RedrawViewEvent event = new RedrawViewEvent();
		event.setSender(this);
		event.setEventSpace(dataDomain.getDataDomainID());
		EventPublisher.trigger(event);
		repaintAll();
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		if (Vec2f.class.isAssignableFrom(clazz))
			return clazz.cast(getMinSize());
		return super.getLayoutDataAs(clazz, default_);
	}

	public final Vec2f getMinSize() {
		switch (detailLevel) {
		case HIGH:
			return new Vec2f(300, 300);
		case MEDIUM:
			return new Vec2f(100, 100);
		case LOW:
			return new Vec2f(40, 40);
		default:
			return new Vec2f(40, 40);
		}
	}
}
