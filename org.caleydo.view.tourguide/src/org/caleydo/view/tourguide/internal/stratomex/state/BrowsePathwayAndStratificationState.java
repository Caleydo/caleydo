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

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.text.TextUtils;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.stratomex.tourguide.event.UpdatePathwayPreviewEvent;
import org.caleydo.view.tourguide.api.state.BrowsePathwayState;
import org.caleydo.view.tourguide.api.state.ISelectReaction;
import org.caleydo.view.tourguide.api.state.ISelectStratificationState;
import org.caleydo.view.tourguide.api.state.PreviewRenderer;

/**
 * @author Samuel Gratzl
 *
 */
public class BrowsePathwayAndStratificationState extends BrowsePathwayState implements ISelectStratificationState {

	private PathwayGraph pathway;

	public BrowsePathwayAndStratificationState() {
		super("Select a pathway in the Tour Guide and select a strafication to refer to.");
	}

	@Override
	public void onEnter() {

		super.onEnter();
	}

	@Override
	public void onUpdate(UpdatePathwayPreviewEvent event, ISelectReaction adapter) {
		pathway = event.getPathway();
		if (underlying == null) {
			ALayoutRenderer preview = new MyLabelRenderer(pathway.getTitle(), adapter.getGLView());
			adapter.replaceTemplate(new PreviewRenderer(preview, adapter.getGLView(),
					"Select a stratification to refer to"));
		} else {
			show(adapter);
		}
	}

	private void show(ISelectReaction adapter) {
		if (underlying == null || pathway == null)
			return;
		adapter.replacePathwayTemplate(underlying, pathway);
	}

	@Override
	public boolean apply(TablePerspective tablePerspective) {
		return true;
	}

	@Override
	public void select(TablePerspective tablePerspective, ISelectReaction reactions) {
		setUnderlying(tablePerspective.getRecordPerspective());
		show(reactions);
	}

	@Override
	public boolean isAutoSelect() {
		return true;
	}

	private static class MyLabelRenderer extends ALayoutRenderer {
		private final String label;
		private final AGLView view;

		public MyLabelRenderer(String label, AGLView view) {
			this.label = label;
			this.view = view;
		}

		@Override
		protected void renderContent(GL2 gl) {
			float hi = view.getPixelGLConverter().getGLHeightForPixelHeight(16);
			CaleydoTextRenderer textRenderer = view.getTextRenderer();
			List<String> lines = TextUtils.wrap(textRenderer, label, x, hi);
			float yi = (y * 0.5f - hi * 0.5f * lines.size());
			for (String line : lines) {
				float wi = textRenderer.getTextWidth(line, hi);
				textRenderer.renderTextInBounds(gl, line, (x * 0.5f - wi * 0.5f), yi, 0, wi, hi);
				yi += hi;
			}
		}

		@Override
		protected boolean permitsWrappingDisplayLists() {
			return true;
		}
	}
}
