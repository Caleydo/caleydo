/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.kaplanmeier.v2;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.selection.MultiSelectionManagerMixin;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.ADoubleFunction;
import org.caleydo.core.util.function.ArrayDoubleList;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.util.function.IInvertableDoubleFunction;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

/**
 * kaplan meier plot implementation as a {@link GLElement}
 *
 * @author Samuel Gratzl
 *
 */
public class ListKaplanMeierElement extends AKaplanMeierElement implements
		MultiSelectionManagerMixin.ISelectionMixinCallback {

	private final List<Integer> ids;
	private final IDoubleList raw;
	private final IInvertableDoubleFunction normalize;

	private final List<Vec2f> curve;
	private final IDoubleList data;

	private final Axis xAxis;
	private final Axis yAxis;

	private Color color = Color.GRAY;
	private boolean isFillCurve = false;

	@DeepScan
	private MultiSelectionManagerMixin selections;

	/**
	 * @param idType
	 * @param ids
	 * @param tablePerspective
	 */
	public ListKaplanMeierElement(IDoubleList data, List<Integer> ids, IDType idType, EDetailLevel detailLevel) {
		super(detailLevel);
		this.xAxis = new Axis("Time", 6, (float) DoubleStatistics.of(data).getMax());
		this.yAxis = new Axis("Percentage", 6, 100);

		this.raw = data;
		DoubleStatistics stats = DoubleStatistics.of(data);
		// normalize
		this.normalize = DoubleFunctions.normalize(stats.getMin(), stats.getMax());
		this.data = convert(Preconditions.checkNotNull(data));
		this.curve = createCurve(this.data);

		this.ids = ids;
		if (idType != null) {
			selections = new MultiSelectionManagerMixin(this);
			selections.add(new SelectionManager(idType));
		} else
			selections = null;
	}

	/**
	 * @param isFillCurve
	 *            setter, see {@link isFillCurve}
	 */
	public void setFillCurve(boolean isFillCurve) {
		if (this.isFillCurve == isFillCurve)
			return;
		this.isFillCurve = isFillCurve;
		repaint();
	}

	/**
	 * @param xAxis setter, see {@link xAxis}
	 */
	public void setXAxis(String xAxis) {
		if (this.xAxis.label.equals(xAxis))
			return;
		this.xAxis.label = xAxis;
		repaint();
	}

	public String getXAxis() {
		return xAxis.label;
	}

	public String getYAxis() {
		return yAxis.label;
	}

	/**
	 * @return the color, see {@link #color}
	 */
	public Color getColor() {
		return color;
	}

	public void setXMaxValue(float value) {
		if (this.xAxis.max == value)
			return;
		this.xAxis.max = value;
		repaint();
	}

	public float getXMaxValue() {
		return xAxis.max;
	}

	public void setYAxis(String yAxis) {
		if (this.yAxis.label.equals(yAxis))
			return;
		this.yAxis.label = yAxis;
		repaint();
	}

	/**
	 * @param color setter, see {@link color}
	 */
	public void setColor(Color color) {
		if (this.color.equals(color))
			return;
		this.color = color;
		repaint();
	}

	@Override
	public List<GLLocation> getLocations(EDimension dim, Iterable<Integer> dataIndizes) {
		List<GLLocation> r = new ArrayList<>();
		for (Integer dataIndex : dataIndizes) {
			GLLocation g = forLocation(dim, dataIndex);
			r.add(g);
		}
		return r;
	}

	GLLocation forLocation(EDimension dim, Integer dataIndex) {
		Vec2f wh = getSize();
		float factor = dim.select(wh.x() - padding.hor(), wh.y() - padding.vert());
		double v = normalize.apply(raw.getPrimitive(dataIndex));
		v = Double.isNaN(v) ? 1 : v;
		Pair<Vec2f, Vec2f> loc = getLocation(curve, v);

		float offset = dim.select(loc.getFirst()) * factor;
		float size = dim.select(loc.getSecond()) * factor - offset;
		GLLocation g = new GLLocation(offset + dim.select(padding.left, padding.top), size);
		return g;
	}

	@Override
	public Set<Integer> forLocation(EDimension dim, GLLocation location) {
		Vec2f wh = getSize();
		float factor = dim.select(wh.x() - padding.hor(), wh.y() - padding.vert());
		double from = normalize.unapply((location.getOffset() - dim.select(padding.left, padding.top)) / factor);
		double to = normalize.unapply(location.getSize() / factor);

		List<Integer> r = new ArrayList<>();
		for(int i = 0; i < raw.size(); ++i) {
			double v = raw.getPrimitive(i);
			v = Double.isNaN(v) ? 1 : v;
			if (from <= v && v <= to)
				r.add(i);
		}
		return ImmutableSet.copyOf(r);
	}

	/**
	 * @param checkNotNull
	 * @return
	 */
	private IDoubleList convert(IDoubleList data) {
		data = data.map(normalize);
		// remove nan
		data = data.map(new ADoubleFunction() {
			@Override
			public double apply(double v) {
				return Double.isNaN(v) ? 1 : v;
			}
		});
		// sort
		double[] vs = data.toPrimitiveArray();
		Arrays.sort(vs);

		return new ArrayDoubleList(vs);
	}

	@Override
	protected void renderCurve(GLGraphics g, float w, float h) {
		drawCurve(g, color, w, h, isFillCurve, null, null, curve);

		if (g.isPickingPass() || ids == null || selections == null)
			return;

		SelectionManager manager = selections.get(0);
		renderSelection(manager.getElements(SelectionType.SELECTION), SelectionType.SELECTION, g, w, h);
		renderSelection(manager.getElements(SelectionType.MOUSE_OVER), SelectionType.MOUSE_OVER, g, w, h);
	}

	private void renderSelection(Set<Integer> elements, SelectionType selectionType, GLGraphics g, float w, float h) {
		if (elements.isEmpty())
			return;

		final NavigableSet<Float> s = new TreeSet<>();
		for (int i = 0; i < ids.size(); ++i) {
			Integer id = ids.get(i);
			if (!elements.contains(id)) // continue
				continue;
			Double v = normalize.apply(raw.getPrimitive(i));
			if (v.isNaN())
				v = 1.0;
			s.add(v.floatValue());
		}
		if (s.isEmpty())
			return;
		Collection<List<Vec2f>> curves = getSubCurves(curve, s);
		for(List<Vec2f> subcurve : curves)
			drawCurve(g, selectionType.getColor(), w, h, isFillCurve, selectionType, null, subcurve);
	}

	@Override
	protected Axis getAxis(EDimension dim) {
		return dim.select(xAxis, yAxis);
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		repaint();
	}

	public static void main(String[] args) {
		Random r = new Random();
		double[] d = new double[100];
		for (int i = 0; i < d.length; ++i)
			d[i] = r.nextDouble() * 100;
		IDoubleList data = new ArrayDoubleList(d);
		GLSandBox.main(args, new ListKaplanMeierElement(data, null, null, EDetailLevel.HIGH));
	}
}
