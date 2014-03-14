/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2;

import gleem.linalg.Vec2f;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.perspective.table.TableDoubleLists;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.MultiSelectionManagerMixin;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.ArrayDoubleList;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.util.function.IInvertableDoubleFunction;
import org.caleydo.core.util.function.MappedDoubleList;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation.ILocator;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * @author Samuel Gratzl
 *
 */
public class SingleAxisElement extends GLElement implements MultiSelectionManagerMixin.ISelectionMixinCallback,
		ILocator, IPickingLabelProvider, IPickingListener {
	private final static float ITEM_AXIS_WIDTH = 0.3f;
	@DeepScan
	private final MultiSelectionManagerMixin selections;
	private final EDimension dim;
	private final List<Integer> data;
	private final IDoubleList list;
	private final IInvertableDoubleFunction normalize;

	private double nanReplacement = Double.NaN;

	private Function<Double, String> toMarker;
	private int markers;

	private float selectStart = Float.NaN;
	private float selectEndPrev = Float.NaN;
	private float selectEnd = Float.NaN;

	private boolean invertOrder = false;
	private final boolean renderOutsideBounds;

	public SingleAxisElement(TablePerspective t, boolean renderOutsideBounds) {
		this(EDimension.get(t.getNrDimensions() == 1), t, EDimension.get(t.getNrDimensions() == 1)
				.select(t.getRecordPerspective(), t.getDimensionPerspective()).getVirtualArray(), renderOutsideBounds);
	}

	public SingleAxisElement(EDimension dim, TablePerspective t, VirtualArray va, boolean renderOutsideBounds) {
		this(dim, TableDoubleLists.asNormalizedList(t), 0, 1, va.getIDs(), va.getIdType(), renderOutsideBounds);
	}

	public SingleAxisElement(EDimension dim, List<Integer> data, IDType idType, Function<Integer, Double> f,
			double min, double max, boolean renderOutsideBounds) {
		this(dim, new MappedDoubleList<Integer>(data, f), min, max, data, idType, renderOutsideBounds);
	}

	public SingleAxisElement(EDimension dim, IDoubleList data, double min, double max, boolean renderOutsideBounds) {
		this(dim, data, min, max, null, null, renderOutsideBounds);
	}

	// either String[] markers
	// or Function<Double,String>
	// or nothing

	public SingleAxisElement(EDimension dim, IDoubleList data, double min, double max, List<Integer> ids,
			IDType idType, boolean renderOutsideBounds) {
		this.dim = dim;
		this.data = ids;
		this.list = data;
		this.renderOutsideBounds = renderOutsideBounds;
		if (Double.isNaN(min) || Double.isNaN(max)) {
			DoubleStatistics stats = DoubleStatistics.of(list);
			if (Double.isNaN(min))
				min = stats.getMin();
			if (Double.isNaN(max))
				max = stats.getMax();
		}
		this.normalize = DoubleFunctions.normalize(min, max);
		if (ids != null && idType != null) {
			selections = new MultiSelectionManagerMixin(this);
			selections.add(new SelectionManager(idType));
		} else
			selections = null;

		setVisibility(EVisibility.PICKABLE);
		onPick(this);
	}

	/**
	 * @return the invertOrder, see {@link #invertOrder}
	 */
	public boolean isInvertOrder() {
		return invertOrder;
	}

	/**
	 * @param invertOrder
	 *            setter, see {@link invertOrder}
	 */
	public void setInvertOrder(boolean invertOrder) {
		if (this.invertOrder == invertOrder)
			return;
		this.invertOrder = invertOrder;
		repaintAll();
	}

	@Override
	protected void init(IGLElementContext context) {
		onPick(context.getSWTLayer().createTooltip(this));
		super.init(context);
	}

	@Override
	public String getLabel(Pick pick) {
		Vec2f xy = toRelative(pick.getPickedPoint());
		float max = dim.select(getSize());
		float ratio = dim.select(xy) / max;
		if (invertOrder)
			ratio = 1 - ratio;
		String marker = null;
		if (markers > 0) {
			float delta = 1.f / (markers - 1);
			for (int i = 0; i < markers; ++i) {
				float vi = delta * i;
				if (Math.abs(ratio - vi) < 0.05) {
					marker = toMarker.apply(Double.valueOf(vi));
					break;
				}
			}
		} else if (toMarker != null && markers <= 0)
			marker = toMarker.apply(Double.valueOf(ratio));
		else
			marker = Formatter.formatNumber(normalize.unapply(ratio));
		return marker;
	}

	@Override
	public void pick(Pick pick) {
		if (this.data == null || this.selections == null)
			return;
		SelectionManager manager = selections.get(0);
		Vec2f xy = toRelative(pick.getPickedPoint());
		float v = dim.select(xy) / dim.select(getSize());
		if (invertOrder)
			v = 1 - v;
		boolean ctrlDown = ((IMouseEvent) pick).isCtrlDown();
		switch(pick.getPickingMode()) {
		case DRAG_DETECTED:
			pick.setDoDragging(true);
			selectStart = selectEndPrev = v;
			if (!ctrlDown) {
				manager.clearSelection(SelectionType.SELECTION);
				selections.fireSelectionDelta(manager);
				repaint();
			}
			break;
		case DRAGGED:
			if (pick.isDoDragging()) {
				selectEnd = v;
				updateSelections(manager, ctrlDown);
			}
			break;
		case MOUSE_RELEASED:
			if (pick.isDoDragging()) {
				selectEnd = v;
				updateSelections(manager, ctrlDown);
				selectStart = selectEnd = selectEndPrev = Float.NaN;
			}
			break;
		default:
			break;
		}
	}

	private void updateSelections(SelectionManager manager, boolean ctrlDown) {
		if (!ctrlDown) {
			manager.clearSelection(SelectionType.SELECTION);
			update(manager, selectStart, selectEnd, false);
		} else {
			update(manager, selectEnd, selectEndPrev, true);
		}
		selections.fireSelectionDelta(manager);
		selectEndPrev = selectEnd;
		repaint();
	}

	private void update(SelectionManager manager, float a, float b, boolean toggle) {
		double min = normalize.unapply(Math.min(a, b));
		double max = normalize.unapply(Math.max(a, b));
		int n = list.size();
		for (int i = 0; i < n; ++i) {
			double v = list.getPrimitive(i);
			if (v >= min && v <= max) {
				final Integer id = data.get(i);
				boolean add = !toggle || !manager.checkStatus(SelectionType.SELECTION, id);
				if (add)
					manager.addToType(SelectionType.SELECTION, id);
				else
					manager.removeFromType(SelectionType.SELECTION, id);
			}
		}
	}

	private boolean isRenderMarkers() {
		return toMarker != null;
	}

	/**
	 * @param nanReplacement
	 *            setter, see {@link nanReplacement}
	 */
	public void setNanReplacement(double nanReplacement) {
		if (this.nanReplacement == nanReplacement)
			return;
		this.nanReplacement = nanReplacement;
		repaint();
	}

	public void setMarker(String... markers) {
		this.toMarker = toMarkerFunction(markers);
		this.markers = markers.length;
		repaint();
	}

	public void setNumberMarkers() {
		this.setMarker(toMarkerFunction(this.normalize));
	}

	public void setMarker(Function<Double, String> toMarker) {
		this.toMarker = toMarker;
		this.markers = -1;
		repaint();
	}

	/**
	 * @return the nanReplacement, see {@link #nanReplacement}
	 */
	public double getNanReplacement() {
		return nanReplacement;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		final SelectionManager manager = selections == null || selections.isEmpty() ? null : selections.get(0);
		final int n = list.size();

		g.color(Color.BLACK);
		if (dim.isHorizontal()) {
			g.drawLine(0, h * 0.5f, w, h * 0.5f);
		} else {
			g.drawLine(w * 0.5f, 0, w * 0.5f, h);
		}

		float o = markerOffset(w, h);
		for (int i = 0; i < n; ++i) {
			double v = normalize.apply(list.getPrimitive(i));
			if (Double.isNaN(v))
				v = nanReplacement;
			if (Double.isNaN(v))
				continue;
			SelectionType t = data == null || manager == null ? null : manager.getHighestSelectionType(data.get(i));
			if (t == null) {
				g.color(0, 0, 0, 0.5f);
			} else {
				final Color c = t.getColor();
				g.color(c.r, c.g, c.b, 0.5f);
			}
			if (invertOrder)
				v = 1 - v;
			if (dim.isHorizontal()) {
				g.drawLine(w * (float) v, o, w * (float) v, h - o);
			} else {
				g.drawLine(o, h * (float) v, w - o, h * (float) v);
			}
		}

		if (isRenderMarkers()) {
			renderMarkers(g, w, h);
		}

		if (selectStart != Float.NaN && selectEnd != Float.NaN) {
			renderSelection(g, w, h);
		}

		super.renderImpl(g, w, h);
	}

	private float markerOffset(float w, float h) {
		final float total = dim.opposite().select(w, h);
		if (renderOutsideBounds)
			return -1;
		return total * (0.5f - ITEM_AXIS_WIDTH / 2f);
	}

	private void renderSelection(GLGraphics g, float w, float h) {
		float min = Math.min(selectStart, selectEnd);
		float max = Math.max(selectStart, selectEnd);
		if (invertOrder) {
			min = 1 - min;
			max = 1 - max;
		}
		final Color c = SelectionType.SELECTION.getColor();
		g.color(c.r, c.g, c.b, 0.5f);
		if (dim.isHorizontal())
			g.fillRect(min * w, 0, (max - min) * w, h);
		else
			g.fillRect(0, min * h, w, (max - min) * h);
	}

	private void renderMarkers(GLGraphics g, float w, float h) {
		final float max = dim.select(w, h);
		int nMarkers = determineMarkerCount(max);
		float delta = max / (nMarkers - 1);
		float other = dim.select(h, w);
		float o = Math.max(5, 15 - other * 0.5f);
		g.lineWidth(2);
		for (int i = 0; i < nMarkers; ++i) {
			float v = i * delta;
			if (invertOrder)
				v = max - v;
			if (dim.isHorizontal()) {
				g.drawLine(v, -o, v, h + o);
			} else {
				g.drawLine(-o, v, w + o, v);
			}
		}
		g.lineWidth(1);
		renderMarkerLabels(g, w, h, delta, nMarkers);
	}

	private void renderMarkerLabels(GLGraphics g, float w, float h, float delta, int nMarkers) {
		int every = Math.round((float) Math.ceil(dim.select(30, 10) / delta));
		float shift = dim.select(h, w) * (0.5f + ITEM_AXIS_WIDTH / 2);
		final float hi = dim.isHorizontal() ? Math.min(shift, 10) : Math.min(delta * every, 10);

		double pdelta = 1. / (nMarkers - 1);
		for (int i = 0; i < (nMarkers - 1); i += every) {
			float v = i * delta;
			String li = toMarker.apply(pdelta * (invertOrder ? nMarkers - 1 - i : i));
			if (li == null)
				continue;
			if (dim.isHorizontal()) {
				g.drawText(li, v + 2, shift, delta * every, hi);
			} else {
				g.drawText(li, shift + 2, v + 1, 100, hi);
			}
		}
		{
			String li = toMarker.apply(invertOrder ? 0. : 1.);
			if (li != null) {
				if (dim.isHorizontal()) {
					g.drawText(li, w - delta * every - 2, shift, delta * every, hi, VAlign.RIGHT);
				} else {
					g.drawText(li, shift + 2, h - hi - 2, 100, hi);
				}
			}
		}
	}

	private int determineMarkerCount(float size) {
		if (this.markers >= 2) // externally specified
			return this.markers;
		if (size < 50)
			return 2;
		if (size < 100)
			return 3;
		if (size < 250)
			return 5;
		return 7;
	}

	/**
	 * @param dim
	 * @return
	 */
	public GLElementDimensionDesc getDesc(EDimension dim) {
		if (this.dim != dim)
			return GLElementDimensionDesc.newFix(renderOutsideBounds ? 5 : 20).inRange(5, 60).build();
		return GLElementDimensionDesc.newFix(200).minimum(50).locateUsing(this).build();
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		repaint();
	}

	@Override
	public GLLocation apply(int dataIndex, boolean topLeft) {
		final Vec2f size = getSize();
		float total = dim.select(size);
		double n = normalize.apply(list.getPrimitive(dataIndex));
		if (invertOrder)
			n = 1 - n;
		return new GLLocation(n * total - 0.5f, 1, markerOffset(size.x(), size.y()));
	}

	@Override
	public Set<Integer> unapply(GLLocation location) {
		float total = dim.select(getSize());
		double min = location.getOffset() / total;
		double max = location.getOffset2() / total;
		if (invertOrder) {
			min = 1 - min;
			max = 1 - max;
		}
		double from = normalize.unapply(min);
		double to = normalize.unapply(max);

		Set<Integer> r = new HashSet<>();
		for (int i = 0; i < list.size(); ++i) {
			double v = list.getPrimitive(i);
			if (from <= v && v <= to)
				r.add(i);
		}
		return ImmutableSet.copyOf(r);
	}

	@Override
	public GLLocation apply(Integer input, Boolean topLeft) {
		return GLLocation.applyPrimitive(this, input, topLeft);
	}

	private static Function<Double, String> toMarkerFunction(final String... markers) {
		return new Function<Double, String>() {
			@Override
			public String apply(Double input) {
				double d = input.doubleValue();
				int i = (int) (d * (markers.length - 1));
				i = Math.max(0, Math.min(markers.length - 1, i));
				return markers[i];
			}
		};
	}

	private static Function<Double, String> toMarkerFunction(final IInvertableDoubleFunction normalize) {
		return new Function<Double, String>() {
			@Override
			public String apply(Double input) {
				double raw = normalize.unapply(input.doubleValue());
				return Formatter.formatNumber(raw);
			}
		};
	}

	public static void main(String[] args) {
		Random r = new Random();
		double[] arr = new double[100];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = r.nextDouble();
		}

		final SingleAxisElement elem = new SingleAxisElement(EDimension.RECORD, new ArrayDoubleList(arr), 0, 1, null,
				null, false);
		elem.setInvertOrder(true);
		elem.setMarker("A", "B", "C");
		GLSandBox.main(args,
				elem);
	}
}
