/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout.util;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

/**
 * Abstract base class that can hold multiple pickingIDs to be pushed when rendering.
 *
 * @author Christian
 */
public abstract class APickableLayoutRenderer
	extends ALayoutRenderer {

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
