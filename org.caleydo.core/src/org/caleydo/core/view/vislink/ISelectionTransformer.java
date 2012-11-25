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
package org.caleydo.core.view.vislink;

import java.util.HashMap;

import javax.media.opengl.GL2;

import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.id.IDType;

/**
 * Interface for utility objects to view's that help to transform and project points. The transformation and
 * projection itself are usually view specific. So each view that provides selections should have its own
 * implementation.
 * 
 * @author Werner Puff
 */
public interface ISelectionTransformer
	extends IListenerOwner {

	/**
	 * Projects all selection points related to this {@link ISelectionTransformer}'s view from the source-map
	 * into 2D selection points in the target-map. The resulting points have coordinates within the view
	 * canvas coordinate system.
	 * 
	 * @param gl
	 *            gl-object to do the transformation, it must be in the related view's state.
	 * @param deskoXID
	 *            deskotheque id (network name) of the caleydo application that displays the views the source
	 *            selection points are related to
	 * @param source
	 *            contains the selection points in the view's coordinate system
	 * @param target
	 *            is filled with connection points in the view's canvas coordinate system.
	 */
	public void project(GL2 gl, String deskoXID, HashMap<IDType, ConnectionMap> source,
		HashMap<IDType, CanvasConnectionMap> target);

	/**
	 * Transforms all selection points related to this {@link ISelectionTransformer}'s view from the
	 * source-map into the view's coordinate system. This is necessary to get the selection points in
	 * remote-rendered view's coordinate system into the remote-rendering view's coordinate system.
	 */
	public boolean transform(HashMap<IDType, ConnectionMap> source, HashMap<IDType, ConnectionMap> target);

	/**
	 * Handling method for connection-point updates
	 */
	public void handleNewConnections();

	/**
	 * Releases all obtained resources
	 */
	public void destroy();

}
