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
package org.caleydo.core.view.opengl.layout.util;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

/**
 * Abstract base class that can hold multiple pickingIDs to be pushed when rendering.
 *
 * @author Christian
 */
public abstract class APickableLayoutRenderer
	extends LayoutRenderer {

	protected AGLView view;
	protected List<Pair<String, Integer>> pickingIDs = new ArrayList<Pair<String, Integer>>();

	public APickableLayoutRenderer() {

	}

	public APickableLayoutRenderer(AGLView view, String pickingType, int id) {
		pickingIDs.add(new Pair<String, Integer>(pickingType, id));
		this.view = view;
	}

	public APickableLayoutRenderer(AGLView view, List<Pair<String, Integer>> pickintIDs) {
		if (pickintIDs != null) {
			this.pickingIDs = pickintIDs;
		}
		this.view = view;
	}

	protected void pushNames(GL2 gl) {
		if (view == null)
			return;
		for (Pair<String, Integer> pickingIDPair : pickingIDs) {
			int pickingID =
				view.getPickingManager().getPickingID(view.getID(), pickingIDPair.getFirst(),
					pickingIDPair.getSecond());

			gl.glPushName(pickingID);
		}
	}

	protected void popNames(GL2 gl) {
		for (int i = 0; i < pickingIDs.size(); i++) {
			gl.glPopName();
		}
	}

	public List<Pair<String, Integer>> getPickingIDs() {
		return pickingIDs;
	}

	public void setPickingIDs(List<Pair<String, Integer>> pickingIDs) {
		this.pickingIDs = pickingIDs;
	}

	public void addPickingIDs(List<Pair<String, Integer>> pickingIDs) {
		if (pickingIDs != null) {
			this.pickingIDs.addAll(pickingIDs);
		}
	}

	public APickableLayoutRenderer addPickingID(String pickingType, int id) {
		pickingIDs.add(new Pair<String, Integer>(pickingType, id));
		return this;
	}

	/**
	 * @param view setter, see {@link #view}
	 */
	public void setView(AGLView view) {
		this.view = view;
	}
}
