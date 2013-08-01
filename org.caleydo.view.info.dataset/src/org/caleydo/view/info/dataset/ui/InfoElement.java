/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.info.dataset.ui;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.util.text.ETextStyle;

/**
 * simple element showing statistics of the current {@link TablePerspective}
 *
 * @author Samuel Gratzl
 *
 */
public class InfoElement extends GLElement implements
		TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback, IHasMinSize {
	@DeepScan
	protected final TablePerspectiveSelectionMixin mixin;
	private final EDetailLevel detailLevel;

	public InfoElement(TablePerspective tablePerspective) {
		this(tablePerspective, EDetailLevel.HIGH);
	}

	public InfoElement(TablePerspective tablePerspective, EDetailLevel detailLevel) {
		this.mixin = new TablePerspectiveSelectionMixin(tablePerspective, this);
		this.detailLevel = detailLevel;
	}

	public TablePerspective getTablePerspective() {
		return mixin.getTablePerspective();
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		repaint();
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		repaint();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		TablePerspective tablePerspective = getTablePerspective();
		ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();

		final int nrRecords = dataDomain.getTable().depth();
		final int nrDimensions = dataDomain.getTable().size();

		String recordName = dataDomain.getRecordDenomination(true, true) + ":";
		String rCount = String.format("%d (%.2f%%)", tablePerspective.getNrRecords(), tablePerspective.getNrRecords()
				* 100f / nrRecords);
		if (tablePerspective.getRecordPerspective().getUnmappedElements() > 0) {
			rCount += String.format(" - %d unmapped", tablePerspective.getRecordPerspective().getUnmappedElements());
		}

		String dimensionName = dataDomain.getDimensionDenomination(true, true) + ":";
		String cCount = String.format("%d (%.2f%%)", tablePerspective.getNrDimensions(),
				tablePerspective.getNrDimensions() * 100f / nrDimensions);
		if (tablePerspective.getDimensionPerspective().getUnmappedElements() > 1) {
			rCount += String.format(" - %d unmapped", tablePerspective.getDimensionPerspective().getUnmappedElements());
		}

		h -= 10;
		float textHeight = Math.min(15, h / 2 - 1);
		float lineSpace = textHeight + 1;
		float neededHeight = textHeight * 2 + 2;
		float yi = (h- neededHeight)*0.5f + 3;
		float l = g.text.getTextWidth(recordName.length() > dimensionName.length() ? recordName : dimensionName,
				textHeight);

		g.drawText(recordName, 5, yi,Math.min(l,w-10),textHeight, VAlign.LEFT, ETextStyle.PLAIN);
		g.drawText(dimensionName, 5, yi+lineSpace,Math.min(l,w-10),textHeight, VAlign.LEFT, ETextStyle.PLAIN);

		l+=10;
		if (l < w-5) {
			g.drawText(rCount, l, yi,w-l,textHeight, VAlign.LEFT, ETextStyle.BOLD);
			g.drawText(cCount, l, yi+lineSpace,w-l,textHeight, VAlign.LEFT, ETextStyle.BOLD);
		}

		// TODO add more stats
		// TablePerspectiveStatistics statistics = getTablePerspective().getContainerStatistics();

		// TODO add more info about the number of current selected items,... depending on detail level

		super.renderImpl(g, w, h);
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		if (clazz.isInstance(mixin.getTablePerspective()))
			return clazz.cast(mixin.getTablePerspective());
		if (clazz.isInstance(getDataDomain()))
			return clazz.cast(getDataDomain());
		if (Vec2f.class.isAssignableFrom(clazz))
			return clazz.cast(getMinSize());
		return super.getLayoutDataAs(clazz, default_);
	}

	private ATableBasedDataDomain getDataDomain() {
		return getTablePerspective().getDataDomain();
	}

	@Override
	public final Vec2f getMinSize() {
		return new Vec2f(220, 40);
	}
}
