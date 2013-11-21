/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.util.PickingPool;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.parcoords.Activator;
import org.caleydo.view.parcoords.PCRenderStyle;
import org.caleydo.view.parcoords.v2.internal.AxisElement;
import org.caleydo.view.parcoords.v2.internal.Brush;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class ParallelCoordinateElement extends GLElementContainer implements IGLLayout2,
		TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback, IHasMinSize, IPickingListener {

	private static final float NAN_VALUE = 0;
	/**
	 * sort by the stored layout data defining the ordering in percentage
	 */
	private static final Comparator<GLElement> BY_PERCENTAGE = new Comparator<GLElement>() {
		@Override
		public int compare(GLElement o1, GLElement o2) {
			float p1 = o1.getLayoutDataAs(Float.class, 0.0f);
			float p2 = o2.getLayoutDataAs(Float.class, 0.0f);
			return Float.compare(p1, p2);
		}
	};
	@DeepScan
	protected final TablePerspectiveSelectionMixin selections;
	private int numberOfRandomElements;
	private final EDetailLevel detailLevel;

	// in percent
	private final GLPadding padding = new GLPadding(0.03f, 0.10f, 0.03f, 0.15f);

	private PickingPool pool;
	private Brush brush;

	/**
	 *
	 */
	public ParallelCoordinateElement(TablePerspective tablePerspective, EDetailLevel detailLevel) {
		this.detailLevel = detailLevel;
		this.selections = new TablePerspectiveSelectionMixin(tablePerspective, this);
		setLayout(this);
		this.numberOfRandomElements = fromLevel(detailLevel);

		createAxes(tablePerspective.getDimensionPerspective().getVirtualArray());
		setVisibility(EVisibility.PICKABLE);
		onPick(this);
	}

	/**
	 * @param virtualArray
	 * @return
	 */
	private void createAxes(VirtualArray va) {
		IDType idtype = va.getIdType();
		IIDTypeMapper<Integer, String> id2name = IDMappingManagerRegistry.get().getIDMappingManager(idtype)
				.getIDTypeMapper(idtype, idtype.getIDCategory().getHumanReadableIDType());

		for (Integer id : va) {
			Set<String> names = id2name == null ? null : id2name.apply(id);
			AxisElement axis = new AxisElement(id, names == null || names.isEmpty() ? id.toString() : StringUtils.join(names,
					", "));
			this.add(axis);
		}
		resetAxesSpacing();
	}

	@Override
	protected void init(IGLElementContext context) {
		pool = new PickingPool(context, new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onLinePick(pick);
			}
		});
		super.init(context);
	}

	@Override
	protected void takeDown() {
		pool.clear();
		pool = null;
		super.takeDown();
	}

	@Override
	public void pick(Pick pick) {
		switch(pick.getPickingMode()) {
		case CLICKED:
			if (!isBrushClick(pick)) // start brushing
				return;
			this.brush = new Brush(pick.getPickedPoint());
			pick.setDoDragging(true);
			repaint();
			break;
		case DRAGGED:

		case MOUSE_RELEASED:
			if (brush != null) {
				brush = null;
				repaint();
			}
		}
	}

	public static boolean isBrushClick(Pick pick) {
		return ((IMouseEvent) pick).isAltDown();
	}
	/**
	 * @param pick
	 */
	protected void onLinePick(Pick pick) {
		SelectionManager record = selections.getRecordSelectionManager();
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			record.addToType(SelectionType.MOUSE_OVER, pick.getObjectID());
			break;
		case MOUSE_OUT:
			record.removeFromType(SelectionType.MOUSE_OVER, pick.getObjectID());
			break;
		case CLICKED:
			if (isBrushClick(pick))
				return;
			if (!((IMouseEvent) pick).isCtrlDown())
				record.clearSelection(SelectionType.SELECTION);
			record.addToType(SelectionType.SELECTION, pick.getObjectID());
			break;
		default:
			return;
		}
		selections.fireRecordSelectionDelta();
		repaint();
	}

	/**
	 * @param level
	 * @return
	 */
	private int fromLevel(EDetailLevel level) {
		switch (level) {
		case LOW:
			return 50;
		case MEDIUM:
			return 100;
		case HIGH:
			return 100; // FIXME MyPreferences.getNumRandomSamplePoint();
		default:
			return 20;
		}
	}

	public void resetAxesSpacing() {
		final int total = this.size();
		float p = 0;
		final float delta = 1.f / (total - 1); // percent
		for (GLElement elem : this) {
			elem.setLayoutData(p);
			p += delta;
		}
		relayout();
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs) {
		//uniformly distribute the axis
		final float scale = (w - padding.hor() * w);
		final float x = padding.left * w;
		final float y = h * padding.top;
		final float hi = axisHeight(h);
		for (IGLLayoutElement child : children) {
			float p = child.getLayoutDataAs(Float.class, 0.0f);
			child.setBounds(x + p * scale, y, 1, hi);
		}
		return false;
	}

	public void axisMoved() {
		sortBy(BY_PERCENTAGE);
	}

	private float axisHeight(float h) {
		return h - h * padding.vert();
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		if (clazz.isAssignableFrom(Vec2f.class))
			return clazz.cast(getMinSize());
		if (clazz.isInstance(getTablePerspective()))
			return clazz.cast(getTablePerspective());
		if (clazz.isInstance(getDataDomain()))
			return clazz.cast(getDataDomain());
		return super.getLayoutDataAs(clazz, default_);
	}

	public final TablePerspective getTablePerspective() {
		return selections.getTablePerspective();
	}

	public final ATableBasedDataDomain getDataDomain() {
		return getTablePerspective().getDataDomain();
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		repaint();
		repaintChildren();
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		repaint();
		repaintChildren();
	}

	@Override
	public Vec2f getMinSize() {
		final int dims = getTablePerspective().getDimensionPerspective().getVirtualArray().size();
		switch (detailLevel) {
		case HIGH:
			return new Vec2f(dims * 10, 400);
		case MEDIUM:
			return new Vec2f(dims * 5, 200);
		default:
			return new Vec2f(dims * 5, 50);
		}
	}

	private static float getAngle(final Vec3f vecOne, final Vec3f vecTwo) {
		Vec3f vecNewOne = vecOne.copy();
		Vec3f vecNewTwo = vecTwo.copy();

		vecNewOne.normalize();
		vecNewTwo.normalize();
		float fTmp = vecNewOne.dot(vecNewTwo);
		return (float) Math.acos(fTmp);
	}

	private void renderPolylines(GLGraphics g, float h) {
		final TablePerspective t = getTablePerspective();
		final VirtualArray va = t.getRecordPerspective().getVirtualArray();
		final int displayEveryNthPolyline = Math.max(va.size() / numberOfRandomElements, 1);
		final SelectionManager record = selections.getRecordSelectionManager();

		if (va.size() == 0)
			return;

		// this loop executes once per polyline
		g.save();
		g.move(0, padding.top * h).gl.glScalef(1, axisHeight(h), 1);
		if (g.isPickingPass()) {
			g.lineWidth(2);
		}
		float alpha = (float) (6 / Math.sqrt(va.size() / displayEveryNthPolyline));
		for (int i = 0; i < va.size(); i += displayEveryNthPolyline) {
			int recordID = va.get(i);
			List<Vec2f> points = asPoints(recordID);
			if (points == null || points.isEmpty()) //skip empty
				continue;
			if (g.isPickingPass()) {
				g.pushName(pool.get(recordID));
				g.drawPath(points, false);
				g.popName();
			} else {
				SelectionType type = record.getHighestSelectionType(recordID);
				if (type != null) {
					Color color = type.getColor();
					g.color(color.r, color.g, color.b, alpha);
					g.lineWidth(type.getLineWidth());
				} else {
					g.color(0, 0, 0, alpha);
					g.lineWidth(1);
				}
				g.drawPath(points, false);
			}
		}

		g.lineWidth(1);
		g.restore();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());

		renderBackground(g, w, h);
		super.renderImpl(g, w, h);
		renderPolylines(g, h);
		if (brush != null)
			brush.render(g);

		g.popResourceLocator();
	}

	/**
	 * @param g
	 * @param w
	 * @param h
	 */
	private void renderBackground(GLGraphics g, float w, float h) {
		renderXAxis(g, w, h);

	}

	/**
	 * @param g
	 * @param w
	 * @param h
	 */
	private void renderXAxis(GLGraphics g, float w, float h) {
		g.color(PCRenderStyle.X_AXIS_COLOR).lineWidth(PCRenderStyle.X_AXIS_LINE_WIDTH);
		g.drawLine(0, h, w, h);
		g.lineWidth(0);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
		g.incZ();
		renderPolylines(g, h);
		g.decZ();
	}

	private List<Vec2f> asPoints(Integer recordID) {
		final Table table = getTablePerspective().getDataDomain().getTable();
		List<Vec2f> points = new ArrayList<>(this.size());
		for (AxisElement axis : Iterables.filter(this, AxisElement.class)) {
			float raw = table.getNormalizedValue(axis.getId(), recordID);
			if (!axis.apply(raw))
				return Collections.emptyList();
			if (Float.isNaN(raw)) {
				raw = NAN_VALUE;
			}
			points.add(new Vec2f(axis.getX(), raw));
		}
		return points;
	}

	/**
	 * @return the selections, see {@link #selections}
	 */
	public TablePerspectiveSelectionMixin getSelections() {
		return selections;
	}

	/**
	 * @param axisElement
	 * @param dx
	 */
	public void move(AxisElement axisElement, float dx) {
		float total = getSize().x();
		float shift = dx / total;
		Float oldShift = axisElement.getLayoutDataAs(Float.class, 0.f);
		axisElement.setLayoutData(oldShift + shift);
		axisMoved();
	}
}
