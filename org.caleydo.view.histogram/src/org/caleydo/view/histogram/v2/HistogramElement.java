/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import java.util.List;

import org.caleydo.core.data.collection.CategoricalHistogram;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.RedrawViewEvent;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.ColorMapper;
import org.caleydo.core.util.color.mapping.ColorMarkerPoint;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.util.PickingPool;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.histogram.HistogramRenderStyle;

/**
 * Rendering the histogram.
 *
 * @author Samuel Gratzl
 */

public class HistogramElement extends ASingleTablePerspectiveElement {

	private static Color SPREAD_LINE_COLOR = new Color(0.5f, 0.5f, 0.5f);


	private PickingPool leftSpreadPickingIds;
	private PickingPool rightSpreadPickingIds;

	private final EDetailLevel detailLevel;

	private int hoveredSpread = -1;
	private boolean hoveredLeft = false;

	private boolean showColorMapper;
	private boolean renderBackground = false;

	public HistogramElement(TablePerspective tablePerspective) {
		this(tablePerspective, EDetailLevel.HIGH);
	}

	public HistogramElement(TablePerspective tablePerspective, EDetailLevel detailLevel) {
		super(tablePerspective);
		this.detailLevel = detailLevel;
		setPicker(null);
		setVisibility(detailLevel.ordinal() > EDetailLevel.MEDIUM.ordinal() ? EVisibility.PICKABLE
				: EVisibility.VISIBLE);
		this.showColorMapper = detailLevel.ordinal() > EDetailLevel.LOW.ordinal();
	}

	/**
	 * @param showColorMapper
	 *            setter, see {@link showColorMapper}
	 */
	public void setShowColorMapper(boolean showColorMapper) {
		this.showColorMapper = showColorMapper;
	}

	/**
	 * @return the showColorMapper, see {@link #showColorMapper}
	 */
	public boolean isShowColorMapper() {
		return showColorMapper;
	}

	/**
	 * @param renderBackground
	 *            setter, see {@link renderBackground}
	 */
	public void setRenderBackground(boolean renderBackground) {
		this.renderBackground = renderBackground;
	}

	/**
	 * @return the renderBackground, see {@link #renderBackground}
	 */
	public boolean isRenderBackground() {
		return renderBackground;
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
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (renderBackground)
			g.color(Color.WHITE).fillRect(0, 0, w, h);

		float padding = getPadding();
		ATableBasedDataDomain dataDomain = getDataDomain();
		g.save();
		g.move(padding, padding);
		Histogram hist = getTablePerspective().getContainerStatistics().getHistogram();
		renderHist(g, hist, w - padding * 2, h - padding * 2,
				dataDomain.getTable().getColorMapper());
		if (hist instanceof CategoricalHistogram)
			showColorMapper = false;
		g.restore();


		if (showColorMapper)
			renderColorMapper(g, w, h, dataDomain.getTable().getColorMapper());
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (showColorMapper && getVisibility() == EVisibility.PICKABLE)
			renderColorMapperPick(g, w, h, getDataDomain().getTable().getColorMapper());
		super.renderPickImpl(g, w, h);
	}

	public static void renderHist(GLGraphics g, Histogram hist, float w, float h, ColorMapper mapper) {

		final float factor = h / hist.getLargestValue();
		final float delta = w / hist.size();
		final float colorDelta = 1.f / (hist.size() - 1);

		final float lineWidth = Math.min(delta - 1, 25);
		final float lineWidthHalf = lineWidth * 0.5f;
		float x = delta / 2;

		CategoricalHistogram colored_hist = null;
		if (hist instanceof CategoricalHistogram) {
			colored_hist = (CategoricalHistogram) hist;
		}
		g.lineWidth(0.3f);

		g.save().move(0, h - 1);
		g.color(Color.DARK_GRAY).drawLine(0, 0, w, 0);
		g.color(Color.GRAY);
		g.save().move(0, h - 1);
		for (int i = 0; i < hist.size(); ++i) {
			if (colored_hist != null) {
				g.color(colored_hist.getColor(i));
			} else if (mapper != null) {
				g.color(mapper.getColor(i * colorDelta));
			}
			float v = -hist.get(i) * factor;

			if (v <= -1) {
				g.fillRect(x - lineWidthHalf, 0, lineWidth, v);
				if (RenderStyle.COLOR_BORDER != null)
					g.color(RenderStyle.COLOR_BORDER).drawRect(x - lineWidthHalf, 0, lineWidth, v);
			}
			x += delta;
		}
		g.restore();
		g.lineWidth(1);
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
			Color color = markerPoint.getColor();
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
				if (hoveredLeft && hoveredSpread == i) {
					g.lineWidth(3.f);
				}
				g.drawLine(from,0,from,h);
				if (hoveredLeft && hoveredSpread == i) {
					g.lineWidth(1);
				}
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
				if (!hoveredLeft && hoveredSpread == i) {
					g.lineWidth(3.f);
				}
				g.drawLine(to, 0, to, h);
				if (!hoveredLeft && hoveredSpread == i) {
					g.lineWidth(1);
				}
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
		case MOUSE_OVER:
			hoveredSpread = pick.getObjectID();
			hoveredLeft = isLeftSpread;
			repaint();
			break;
		case MOUSE_OUT:
			hoveredSpread = -1;
			hoveredLeft = isLeftSpread;
			repaint();
			break;
		case CLICKED:
			pick.setDoDragging(true);
			break;
		case DRAGGED:
			List<ColorMarkerPoint> markers = dataDomain.getTable().getColorMapper().getMarkerPoints();
			final int selected = pick.getObjectID();
			ColorMarkerPoint point = markers.get(selected);
			final float dv = pick.getDx() / (getSize().x() - getPadding() * 2);
			if (dv == 0)
				break;
			//clamp values in the neighbor range
			float v = dv;
			if (isLeftSpread) {
				v = -v + point.getLeftSpread();
				if (v < 0.01f)
					v = 0.01f;
				if (selected > 0) {
					ColorMarkerPoint prev = markers.get(selected-1);
					float maxv = (prev.getMappingValue() + prev.getRightSpread() + 0.01f);
					v = Math.min(v, point.getMappingValue() - maxv);
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
	public final Vec2f getMinSize() {
		switch (detailLevel) {
		case HIGH:
			return new Vec2f(300, 300);
		case MEDIUM:
			return new Vec2f(100, 100);
		default:
			return new Vec2f(40, 80);
		}
	}
}
