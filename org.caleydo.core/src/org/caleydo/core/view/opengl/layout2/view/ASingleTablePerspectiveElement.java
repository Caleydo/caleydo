/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.view;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ASingleTablePerspectiveElement extends PickableGLElement implements
		TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback, IHasMinSize {
	@DeepScan
	protected final TablePerspectiveSelectionMixin selections;

	public ASingleTablePerspectiveElement(TablePerspective tablePerspective) {
		this.selections = new TablePerspectiveSelectionMixin(tablePerspective, this);
	}

	public final TablePerspective getTablePerspective() {
		return selections.getTablePerspective();
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

	public final ATableBasedDataDomain getDataDomain() {
		return getTablePerspective().getDataDomain();
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		repaintAll();
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		repaintAll();
	}
}
