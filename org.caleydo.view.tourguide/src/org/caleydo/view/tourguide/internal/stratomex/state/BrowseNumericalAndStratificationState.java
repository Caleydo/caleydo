/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.tourguide.internal.stratomex.state;



import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.view.stratomex.tourguide.event.UpdateNumericalPreviewEvent;
import org.caleydo.view.tourguide.api.state.BrowseOtherState;
import org.caleydo.view.tourguide.api.state.IReactions;
import org.caleydo.view.tourguide.api.state.ISelectStratificationState;
import org.caleydo.view.tourguide.api.state.PreviewRenderer;

/**
 * @author Samuel Gratzl
 *
 */
public class BrowseNumericalAndStratificationState extends BrowseOtherState implements ISelectStratificationState {

	private TablePerspective numerical;

	public BrowseNumericalAndStratificationState() {
		super("Select a numerical value in the Tour Guide and select a strafication to refer to.");
	}

	@Override
	public void onEnter() {

		super.onEnter();
	}

	@Override
	public void onUpdate(UpdateNumericalPreviewEvent event, IReactions adapter) {
		numerical = event.getTablePerspective();
		if (underlying == null) {
			updatePreview(adapter);
		}
		super.onUpdate(event, adapter);
	}

	/**
	 * @param adapter
	 *
	 */
	private void updatePreview(IReactions adapter) {
		AGLView view = adapter.getGLView();
		MultiFormRenderer preview = adapter.createPreview(numerical);
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
