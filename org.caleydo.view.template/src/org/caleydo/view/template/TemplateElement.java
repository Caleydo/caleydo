/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.template;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * the root element of this view holding a {@link TablePerspective}
 * 
 * @author Samuel Gratzl
 * 
 */
public class TemplateElement extends GLElement implements TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback {

	private final TablePerspective tablePerspective;

	@DeepScan
	private final TablePerspectiveSelectionMixin selection;

	public TemplateElement(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
		this.selection = new TablePerspectiveSelectionMixin(tablePerspective, this);
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		repaintAll();
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		relayout();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());
		super.renderImpl(g, w, h);
		g.popResourceLocator();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());
		super.renderPickImpl(g, w, h);
		g.popResourceLocator();
	}

}
