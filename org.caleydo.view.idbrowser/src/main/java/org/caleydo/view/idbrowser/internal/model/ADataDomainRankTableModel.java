/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.model;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.lineup.model.ARankColumnModel;

import com.jogamp.common.util.IntObjectHashMap;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ADataDomainRankTableModel extends ARankColumnModel {
	protected final ATableBasedDataDomain d;
	protected final EDimension dim;

	protected final IntObjectHashMap cache = new IntObjectHashMap();

	protected final VirtualArray others;
	/**
	 * @param d
	 * @param dim
	 */
	public ADataDomainRankTableModel(ATableBasedDataDomain d, EDimension dim) {
		super(Color.GRAY, new Color(0.95f, .95f, .95f));
		this.d = d;
		this.dim = dim;
		setHeaderRenderer(GLRenderers.drawText(d.getLabel(), VAlign.CENTER));

		final Table table = d.getTable();
		this.others = dim.opposite()
				.select(table.getDefaultDimensionPerspective(false), table.getDefaultRecordPerspective(false))
				.getVirtualArray();
	}

	public ADataDomainRankTableModel(TablePerspective t, EDimension dim) {
		super(Color.GRAY, new Color(0.95f, .95f, .95f));
		this.d = t.getDataDomain();
		this.dim = dim;
		setHeaderRenderer(GLRenderers.drawText(d.getLabel(), VAlign.CENTER));

		this.others = dim.opposite().select(t.getDimensionPerspective(), t.getRecordPerspective()).getVirtualArray();
	}

	/**
	 * @param distributionRankTableModel
	 */
	public ADataDomainRankTableModel(ADataDomainRankTableModel clone) {
		super(clone);
		this.d = clone.d;
		this.dim = clone.dim;
		this.others = clone.others;
		this.cache.putAll(clone.cache);
		setHeaderRenderer(GLRenderers.drawText(d.getLabel(), VAlign.CENTER));
	}

	public final IDType getIDType() {
		return dim.select(d.getDimensionIDType(), d.getRecordIDType());
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new GLElement();
	}
}
