/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.picking;

import static org.caleydo.core.view.opengl.picking.PickingUtils.convertToPickingMode;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.util.IntegerPool;
import org.caleydo.core.view.opengl.canvas.AGLView;

import com.google.common.collect.Iterables;

/**
 * simpler version of {@link PickingManager} for a view specific setup
 *
 * registration: {@link #addPickingListener(Object, int, IPickingListener)} or
 * {@link #addTypePickingListener(Object, IPickingListener)} or {@link #register(Object)} using annotation
 *
 * querying: {@link #getPickingID(Object, int)}
 *
 * executing: {@link #doPicking(GL2, AGLView)}
 *
 * @author Samuel Gratzl
 *
 */
public class PickingManager2 {
	/**
	 * counter variable for picking ids
	 */
	private IntegerPool pool = new IntegerPool();

	/**
	 * indicator, whether any element was hovered in the last run, used for optimization
	 */
	private boolean anyHovered = false;

	private Map<Integer, SpacePickingManager> spacePickingManagers = new HashMap<>();



	/**
	 * returns a space specific picking manager, e.g. one per view instance
	 *
	 * @param spaceId
	 * @return
	 */
	public SpacePickingManager get(int spaceId) {
		SpacePickingManager p = spacePickingManagers.get(spaceId);
		if (p == null) {
			p = new SpacePickingManager(pool);
			spacePickingManagers.put(spaceId, p);
		}
		return p;
	}

	/**
	 * see {@link #doPicking(GL2, PickingMode, Point, IRenderable)} but simpler version for a {@link AGLView}
	 *
	 * @param gl
	 * @param view
	 */
	public void doPicking(final GL2 gl, final AGLView view) {
		PickingMode mode = convertToPickingMode(view.getGLMouseListener());
		Point pickPoint = view.getGLMouseListener().getPickedPoint();

		doPicking(gl, mode, pickPoint, new Runnable() {
			@Override
			public void run() {
				view.display(gl);
			}
		});
	}

	/**
	 * performs picking on the given view and calls all the registered picking listeners
	 *
	 * @param gl
	 *            the gl context to use
	 * @param mode
	 *            the picking mode, if null only the last mouseOut will be executed
	 * @param mousePos
	 *            the position of the mouse
	 * @param toRender
	 *            the element to render
	 */
	public void doPicking(GL2 gl, PickingMode mode, Point mousePos, Runnable toRender) {
		anyHovered = PickingUtils.doPicking(mode, mousePos, gl, toRender, anyHovered,
				Iterables.concat(this.spacePickingManagers.values()));
	}
}
