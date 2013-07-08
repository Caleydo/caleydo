/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.stratomex.state;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.stratomex.tourguide.event.UpdatePathwayPreviewEvent;
import org.caleydo.view.tourguide.api.state.BrowsePathwayState;
import org.caleydo.view.tourguide.api.state.IReactions;
import org.caleydo.view.tourguide.api.state.ISelectStratificationState;
import org.caleydo.view.tourguide.api.state.PreviewRenderer;
import org.caleydo.view.tourguide.api.util.PathwayOracle;

/**
 * @author Samuel Gratzl
 *
 */
public class BrowsePathwayAndStratificationState extends BrowsePathwayState implements ISelectStratificationState {

	private PathwayGraph pathway;

	public BrowsePathwayAndStratificationState() {
		super("Select a pathway in the LineUp and select a strafication to refer to.");
	}

	@Override
	public void onEnter() {

		super.onEnter();
	}

	@Override
	public void onUpdate(UpdatePathwayPreviewEvent event, IReactions adapter) {
		pathway = event.getPathway();
		if (underlying == null) {
			ALayoutRenderer preview = adapter.createPreview(pathway);
			adapter.replaceTemplate(new PreviewRenderer(preview, adapter.getGLView(),
					"Select a stratification to refer to"));
		} else {
			show(adapter);
		}
	}

	private void show(IReactions adapter) {
		if (underlying == null || pathway == null)
			return;
		adapter.replacePathwayTemplate(underlying, pathway, false);
	}

	@Override
	public boolean apply(TablePerspective tablePerspective) {
		return PathwayOracle.canBeUnderlying(tablePerspective);
	}

	@Override
	public void select(TablePerspective tablePerspective, IReactions reactions) {
		setUnderlying(tablePerspective.getRecordPerspective());
		show(reactions);
	}

	@Override
	public boolean isAutoSelect() {
		return true;
	}
}
