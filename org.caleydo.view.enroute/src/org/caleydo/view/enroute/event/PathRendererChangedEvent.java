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
package org.caleydo.view.enroute.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.enroute.path.APathwayPathRenderer;

/**
 * Event that is triggered when a {@link APathwayPathRenderer} changes, i.e., its path changes, branches are uncollapsed
 * etc.
 *
 * @author Christian Partl
 *
 */
public class PathRendererChangedEvent extends AEvent {

	/**
	 * The {@link APathwayPathRenderer} that changed.
	 */
	private APathwayPathRenderer pathRenderer;

	public PathRendererChangedEvent(APathwayPathRenderer pathRenderer) {
		this.pathRenderer = pathRenderer;
	}

	@Override
	public boolean checkIntegrity() {
		return pathRenderer != null;
	}

	/**
	 * @param pathRenderer
	 *            setter, see {@link pathRenderer}
	 */
	public void setPathRenderer(APathwayPathRenderer pathRenderer) {
		this.pathRenderer = pathRenderer;
	}

	/**
	 * @return the pathRenderer, see {@link #pathRenderer}
	 */
	public APathwayPathRenderer getPathRenderer() {
		return pathRenderer;
	}

}
