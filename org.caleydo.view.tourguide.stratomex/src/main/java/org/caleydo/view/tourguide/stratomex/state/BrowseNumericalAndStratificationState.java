/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.state;



import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.view.tourguide.api.state.BrowseOtherState;
import org.caleydo.view.tourguide.api.state.IReactions;
import org.caleydo.view.tourguide.api.state.ISelectStratificationState;
import org.caleydo.view.tourguide.api.state.PreviewRenderer;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapter;

/**
 * @author Samuel Gratzl
 *
 */
public class BrowseNumericalAndStratificationState extends BrowseOtherState implements ISelectStratificationState {

	private TablePerspective numerical;

	public BrowseNumericalAndStratificationState(ITourGuideAdapter adapter) {
		super(adapter, "Select a numerical value in the LineUp and select a strafication to refer to.");
	}

	@Override
	public void onEnter() {

		super.onEnter();
	}

	@Override
	public void onUpdateOther(TablePerspective tablePerspective, IReactions adapter) {
		numerical = tablePerspective;
		if (underlying == null) {
			updatePreview(adapter);
		}
		super.onUpdateOther(tablePerspective, adapter);
	}

	/**
	 * @param adapter
	 *
	 */
	private void updatePreview(IReactions adapter) {
		AGLView view = adapter.getGLView();
		ALayoutRenderer preview = adapter.createPreview(numerical);
		adapter.replaceTemplate(new PreviewRenderer(preview, view, "Select a stratification to refer to"));
	}

	@Override
	public boolean apply(TablePerspective tablePerspective) {
		return true;
	}

	@Override
	public void select(TablePerspective tablePerspective, IReactions reactions) {
		setUnderlying(tablePerspective.getRecordPerspective());
		if (numerical != null)
			show(numerical, reactions);
	}

	@Override
	public boolean isAutoSelect() {
		return true;
	}
}
