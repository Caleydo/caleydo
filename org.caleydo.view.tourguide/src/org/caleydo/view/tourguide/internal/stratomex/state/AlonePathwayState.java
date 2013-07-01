/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.stratomex.state;

import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.stratomex.tourguide.event.UpdatePathwayPreviewEvent;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.state.ABrowseState;
import org.caleydo.view.tourguide.api.state.IReactions;
import org.caleydo.view.tourguide.api.state.PreviewRenderer;

/**
 * a stupid state that show the pathway but can't never be finished
 *
 * @author Samuel Gratzl
 *
 */
public class AlonePathwayState extends ABrowseState {

	public AlonePathwayState() {
		super(EDataDomainQueryMode.PATHWAYS, "Select a pathway in the LineUp.");
	}

	@Override
	public void onUpdate(UpdatePathwayPreviewEvent event, IReactions adapter) {
		PathwayGraph pathway = event.getPathway();
		ALayoutRenderer preview = adapter.createPreview(pathway);
		adapter.replaceTemplate(new PreviewRenderer(preview, adapter.getGLView(),
				"A Pathway can't stand by its own, you first have to add a stratification"));
	}
}
