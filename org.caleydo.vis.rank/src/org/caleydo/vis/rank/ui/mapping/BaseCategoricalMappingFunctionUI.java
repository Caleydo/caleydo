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
package org.caleydo.vis.rank.ui.mapping;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.model.CategoricalRankRankColumnModel.CategoryInfo;
import org.caleydo.vis.rank.model.mapping.BaseCategoricalMappingFunction;
import org.caleydo.vis.rank.model.mapping.ICategoricalMappingFunction;

/**
 * FIXME not implemented
 *
 * @author Samuel Gratzl
 *
 */
public class BaseCategoricalMappingFunctionUI<T> extends GLElementContainer implements IGLLayout {
	private static final float GAP = 10;
	private static final float PADDING = 5;

	private final BaseCategoricalMappingFunction<T> model;
	private final Map<T, Integer> data;
	private final Map<T, CategoryInfo> metaData;
	private final Color backgroundColor;
	private final ICallback<? super ICategoricalMappingFunction<?>> callback;

	public BaseCategoricalMappingFunctionUI(BaseCategoricalMappingFunction<T> model, Map<T, Integer> data,
			Map<T, CategoryInfo> metaData, Color bgColor, ICallback<? super ICategoricalMappingFunction<?>> callback) {
		this.model = model;
		this.data = data;
		this.metaData = metaData;
		this.backgroundColor = bgColor;
		this.callback = callback;

		setLayout(this);
		setSize(PADDING * 2 + 200 + GAP * 2, PADDING * 2 + 200 + GAP * 2);

		// this.add(new RawHistogramElement());
		// this.add(new NormalizedHistogramElement());
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		// TODO Auto-generated method stub

	}

	// /**
	// * a mapping point
	// *
	// * @author Samuel Gratzl
	// *
	// */
	// private class Point extends PickableGLElement {
	// private boolean hovered;
	// private boolean pseudo;
	// private float from;
	// private float to;
	//
	// public Point(float from, float to, boolean pseudo) {
	// this.from = from;
	// this.to = to;
	// this.pseudo = pseudo;
	// }
	//
	// public void set(float from, float to) {
	// this.from = from;
	// this.to = to;
	// }
	//
	// @Override
	// protected void init(IGLElementContext context) {
	// super.init(context);
	// }
	//
	// @Override
	// protected void renderImpl(GLGraphics g, float w, float h) {
	// Color color = this.hovered ? Color.RED : Color.BLACK;
	// if (!pseudo)
	// g.fillImage(g.getTexture(RenderStyle.ICON_CIRCLE), -5, -5, 10, 10, color);
	// else {
	// g.gl.glEnable(GL2.GL_LINE_STIPPLE);
	// g.gl.glLineStipple(2, (short) 0xAAAA);
	// g.lineWidth(2);
	// g.color(color).drawCircle(0, 0, 5);
	// g.gl.glDisable(GL2.GL_LINE_STIPPLE);
	// g.lineWidth(1);
	// }
	// // g.fillCircle(0, 0, 5);
	// }
	//
	// @Override
	// protected void renderPickImpl(GLGraphics g, float w, float h) {
	// g.incZ().fillCircle(0, 0, 10, 8).decZ();
	// }
	//
	// @Override
	// protected void onClicked(Pick pick) {
	// pick.setDoDragging(true);
	// }
	//
	// @Override
	// protected void onRightClicked(Pick pick) {
	// onRemovePoint(this);
	// }
	//
	// @Override
	// protected void onDragged(Pick pick) {
	// if (!pick.isDoDragging())
	// return;
	// if (pick.getDx() != 0 || pick.getDy() != 0)
	// this.pseudo = false;
	// drag(this, pick.getDx(), pick.getDy());
	// this.repaintAll();
	// }
	//
	// @Override
	// protected void onMouseOver(Pick pick) {
	// this.hovered = true;
	// this.repaint();
	// getCanvas().repaint();
	// }
	//
	// @Override
	// protected void onMouseReleased(Pick pick) {
	// if (pick.isDoDragging()) {
	// this.hovered = false;
	// this.repaint();
	// getCanvas().repaint();
	// fireCallback();
	// }
	// }
	//
	// @Override
	// protected void onMouseOut(Pick pick) {
	// if (!pick.isDoDragging()) {
	// this.hovered = false;
	// this.repaint();
	// getCanvas().repaint();
	// fireCallback();
	// }
	// }
	// }
	//
	// class RawHistogramElement extends GLElement {
	// private IFloatList data;
	//
	// private RawHistogramElement(IFloatList raw) {
	// this.data = raw;
	// }
	//
	// @Override
	// protected void renderImpl(GLGraphics g, float w, float h) {
	// super.renderImpl(g, w, h);
	// g.drawText(Formatter.formatNumber(model.getActMin()), -40, h - 14, 38, 12, VAlign.RIGHT);
	// g.drawText(Formatter.formatNumber(model.getActMax()), w + 2, h - 14, 38, 12, VAlign.LEFT);
	// g.color(backgroundColor).fillRect(0, 0, w, h);
	// RenderUtils.renderHist(g, DataUtils.getHist(binsForWidth(w), data), w, h, -1, color, Color.BLACK);
	// g.color(color).drawRect(0, 0, w, h);
	//
	// float[] m = model.getMappedMin();
	// if (m[0] > model.getActMin()) {
	// float from = normalizeRaw(m[0]);
	// g.color(0, 0, 0, 0.25f).fillRect(0, 0, from * w, h);
	// }
	// m = model.getMappedMax();
	// if (m[0] < model.getActMax()) {
	// float to = normalizeRaw(m[0]);
	// g.color(0, 0, 0, 0.25f).fillRect(to * w, 0, (1 - to) * w, h);
	// }
	// }
	// }
	//
	// class NormalizedHistogramElement extends GLElement {
	// @Override
	// protected void renderImpl(GLGraphics g, float w, float h) {
	// super.renderImpl(g, w, h);
	// g.drawText(Formatter.formatNumber(1), 0, -14, 38, 12, VAlign.LEFT);
	// g.drawText(Formatter.formatNumber(0), 0, h + 2, 38, 12, VAlign.LEFT);
	// g.color(backgroundColor).fillRect(0, 0, w, h);
	// g.color(color).drawRect(0, 0, w, h);
	// g.save();
	// g.gl.glRotatef(-90, 0, 0, 1);
	// g.move(-h, 0);
	// RenderUtils.renderHist(g, computeHist(h), h, w, -1, color, Color.BLACK);
	//
	// float m = model.getMinTo();
	// if (m > 0) {
	// g.color(0, 0, 0, 0.25f).fillRect(0, 0, m * h, w);
	// }
	// m = model.getMaxTo();
	// if (m < 1) {
	// g.color(0, 0, 0, 0.25f).fillRect(m * h, 0, (1 - m) * h, w);
	// }
	//
	// g.restore();
	// }
	// }
}
