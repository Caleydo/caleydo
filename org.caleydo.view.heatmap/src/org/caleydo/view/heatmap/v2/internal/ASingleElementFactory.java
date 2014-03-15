/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc;
import org.caleydo.core.view.opengl.layout2.manage.GLElementDimensionDesc.DescBuilder;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory2;
import org.caleydo.view.heatmap.v2.ASingleElement;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ASingleElementFactory implements IGLElementFactory2 {

	/**
	 * @param context
	 * @return
	 */
	protected boolean hasTablePerspective(GLElementFactoryContext context) {
		TablePerspective d = context.getData();
		if (d == null)
			return false;
		if (!DataSupportDefinitions.homogenousColumns.apply(d))
			return false;
		return d.getNrDimensions() == 1 || d.getNrRecords() == 1;
	}

	@Override
	public GLElementDimensionDesc getDesc(final EDimension dim, GLElement elem) {
		final ASingleElement l = (ASingleElement) elem;
		DescBuilder b;
		if (l.getDimension() == dim) {
			b = GLElementDimensionDesc.newCountDependent(1).locateUsing(new GLLocation.ALocator() {
				@Override
				public GLLocation apply(int dataIndex) {
					return l.getLocation(dim, dataIndex);
				}

				@Override
				public Set<Integer> unapply(GLLocation location) {
					return l.forLocation(dim, location);
				}
			});
		} else {
			b = fixDesc();
		}
		return b.build();
	}

	/**
	 *
	 * @param elem
	 * @param context
	 */
	static void setSelectionStrategies(ASingleElement elem, GLElementFactoryContext context) {
		elem.setSelectionHoverStrategy(BarPlotElementFactory.toSelectionStrategy(SelectionType.MOUSE_OVER, context,
				elem.getSelectionHoverStrategy()));
		elem.setSelectionSelectedStrategy(BarPlotElementFactory.toSelectionStrategy(SelectionType.SELECTION, context,
				elem.getSelectionSelectedStrategy()));
	}

	/**
	 * @return
	 */
	protected abstract DescBuilder fixDesc();

	@Override
	public GLElement createParameters(GLElement elem) {
		return null;
	}
}

