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
package org.caleydo.vis.rank.ui.detail;

import static org.caleydo.vis.rank.ui.RenderStyle.binsForWidth;

import java.awt.Color;

import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.model.AMultiRankColumnModel;
import org.caleydo.vis.rank.model.DataUtils;
import org.caleydo.vis.rank.model.SimpleHistogram;
import org.caleydo.vis.rank.ui.RenderUtils;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreFilter extends PickableGLElement implements IPickingListener {
	protected final AMultiRankColumnModel model;
	private final IFloatList data;

	private int minPickingId, maxPickingId;
	private boolean minHovered = false;
	private boolean maxHovered = false;

	private float min;
	private float max;

	private SimpleHistogram cache = null;
	private final GLElement summary;

	public ScoreFilter(AMultiRankColumnModel model, IFloatList data, GLElement summary) {
		this.model = model;
		this.data = data;
		this.summary = summary;

	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		minPickingId = context.registerPickingListener(this, 0);
		maxPickingId = context.registerPickingListener(this, 1);
		this.min = model.getFilterMin();
		this.max = model.getFilterMax();
	}

	@Override
	protected void takeDown() {
		context.unregisterPickingListener(minPickingId);
		context.unregisterPickingListener(maxPickingId);
		super.takeDown();
	}

	@Override
	public void pick(Pick pick) {
		if (pick.isAnyDragging() && !pick.isDoDragging())
			return;
		boolean isMin = pick.getObjectID() == 0;
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			if (isMin)
				minHovered = true;
			else
				maxHovered = true;
			repaint();
			break;
		case CLICKED:
			pick.setDoDragging(true);
			break;
		case DRAGGED:
			float d = pick.getDx() / getSize().x();
			if (isMin)
				min += d;
			else
				max += d;
			repaintAll();
			break;
		case MOUSE_RELEASED:
			if (pick.isDoDragging()) {
				model.setFilter(min, max);
				summary.repaint();
			}
			break;
		case MOUSE_OUT:
			if (isMin)
				minHovered = false;
			else
				maxHovered = false;
			repaint();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onClicked(Pick pick) {
		if (pick.isAnyDragging())
			return;
		float v = toRelative(pick.getPickedPoint()).x() / getSize().x();
		if (v < min || ((v - min) < (max - v)))
			min = v;
		else
			max = v;
		model.setFilter(min, max);
		summary.repaint();
		repaintAll();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(model.getBgColor()).fillRect(0, 0, w, h);
		int bins = binsForWidth(w);
		if (cache == null || cache.size() != bins)
			cache = DataUtils.getHist(bins, data);
		RenderUtils.renderHist(g, cache, w, h, -1, model.getColor(), model.getColor().darker());

		if (min > 0) {
			g.color(0, 0, 0, 0.25f).fillRect(0, 0, min * w, h);
			if (minHovered)
				g.color(Color.BLACK).fillRect(Math.max(0, min * w - 2), 0, 4, h);
		}
		if (max < 1) {
			g.color(0, 0, 0, 0.25f).fillRect(max * w, 0, (1 - max) * w, h);
			if (maxHovered)
				g.color(Color.BLACK).fillRect(Math.min(w - 4, max * w - 2), 0, 4, h);
		}
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
		g.incZ();
		if (min > 0) {
			g.pushName(minPickingId);
			g.fillRect(Math.max(0, min * w - 2), 0, 4, h);
			g.popName();
		}
		if (max < 1) {
			g.pushName(maxPickingId);
			g.fillRect(Math.min(w - 4, max * w - 2), 0, 4, h);
			g.popName();
		}
		g.decZ();
	}
}