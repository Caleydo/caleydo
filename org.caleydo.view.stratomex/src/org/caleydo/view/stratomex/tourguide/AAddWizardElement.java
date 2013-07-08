/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.tourguide;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.stratomex.tourguide.event.UpdateNumericalPreviewEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdatePathwayPreviewEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdateStratificationPreviewEvent;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AAddWizardElement extends ALayoutRenderer {
	public static final String PICKING_TYPE = "templateWizard";

	protected final IStratomexAdapter adapter;

	public AAddWizardElement(IStratomexAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void prepare() {
		super.prepare();
	}

	public abstract void onPick(Pick pick);

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

	public abstract void onUpdate(UpdateStratificationPreviewEvent event);

	public abstract void onUpdate(UpdatePathwayPreviewEvent event);

	public abstract void onUpdate(UpdateNumericalPreviewEvent event);

	public abstract boolean onSelected(TablePerspective tablePerspective);

	public abstract boolean onSelected(TablePerspective tablePerspective, Group recordGroup);

	public abstract boolean canGoBack();

	public abstract void goBack();

	public void done(boolean confirmed) {
		// TODO Auto-generated method stub
	}
}

