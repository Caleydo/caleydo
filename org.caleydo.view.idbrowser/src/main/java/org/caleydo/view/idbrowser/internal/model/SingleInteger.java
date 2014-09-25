/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.model;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Set;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.IntegerRankColumnModel;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class SingleInteger extends IntegerRankColumnModel {
	/**
	 *
	 */
	public SingleInteger(final TablePerspective t) {
		super(GLRenderers.drawText(t.getLabel(), VAlign.CENTER), new Function<IRow, Integer>() {
			private final Table data = t.getDataDomain().getTable();
			private final Integer dimId = t.getDimensionPerspective().getVirtualArray().get(0);
			private final IDType idType = t.getDataDomain().getRecordIDType();
			@Override
			public Integer apply(IRow input) {
				assert input instanceof IIDRow;
				Set<Object> r = ((IIDRow) input).get(idType);
				if (r == null || r.isEmpty())
					return null;
				Object ri = r.iterator().next();
				if (!(ri instanceof Integer))
					return null;
				Integer id = (Integer) ri;
				ri = data.getRaw(dimId, id);
				if (ri instanceof Integer)
					return (Integer) ri;
				return null;
			}
		}, t.getDataDomain().getColor(), t.getDataDomain().getColor().brighter().brighter(), NumberFormat
				.getInstance(Locale.ENGLISH));
	}
}