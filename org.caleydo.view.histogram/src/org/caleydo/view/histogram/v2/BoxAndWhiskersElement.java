/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TableDoubleLists;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;

import com.google.common.base.Supplier;

/**
 * renders an box and whiskers plot for numerical data domains
 *
 * @author Samuel Gratzl
 */
public class BoxAndWhiskersElement extends ABoxAndWhiskersElement implements
		TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback {
	@DeepScan
	protected final TablePerspectiveSelectionMixin selections;

	public BoxAndWhiskersElement(TablePerspective tablePerspective) {
		this(tablePerspective, EDetailLevel.HIGH, EDimension.RECORD, false);
	}

	public BoxAndWhiskersElement(TablePerspective tablePerspective, EDetailLevel detailLevel, EDimension direction,
			boolean showOutlier) {
		super(detailLevel, direction, showOutlier);
		this.selections = new TablePerspectiveSelectionMixin(tablePerspective, this);

		onVAUpdate(tablePerspective);
	}

	@Override
	public String getLabel() {
		return getTablePerspective().getLabel();
	}

	@Override
	protected Color getColor() {
		return getDataDomain().getColor();
	}

	public final TablePerspective getTablePerspective() {
		return selections.getTablePerspective();
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		if (clazz.isAssignableFrom(Vec2f.class))
			return clazz.cast(getMinSize());
		if (clazz.isInstance(getTablePerspective()))
			return clazz.cast(getTablePerspective());
		if (clazz.isInstance(getDataDomain()))
			return clazz.cast(getDataDomain());
		return super.getLayoutDataAs(clazz, default_);
	}

	public final ATableBasedDataDomain getDataDomain() {
		return getTablePerspective().getDataDomain();
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		repaintAll();
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		double min;
		double max;

		Table table = tablePerspective.getDataDomain().getTable();
		if (table instanceof NumericalTable) {
			max = ((NumericalTable) table).getMax();
			min = ((NumericalTable) table).getMin();
		} else if (tablePerspective.getParentTablePerspective() != null) {
			DoubleStatistics r = DoubleStatistics.of(TableDoubleLists.asRawList(tablePerspective
					.getParentTablePerspective()));
			min = r.getMin();
			max = r.getMax();
		} else {
			min = Double.NaN;
			max = Double.NaN;
		}

		IDoubleList t = TableDoubleLists.asRawList(tablePerspective);
		setData(t, min, max);
	}
}
