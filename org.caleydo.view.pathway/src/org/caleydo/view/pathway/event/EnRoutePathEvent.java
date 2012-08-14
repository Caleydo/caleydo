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
/**
 * 
 */
package org.caleydo.view.pathway.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.PathwayPath;

/**
 * Event that specifies a linearizable pathway path.
 * 
 * @author Christian Partl
 * 
 */
public class EnRoutePathEvent extends AEvent {

	/**
	 * Path object that specifies a path.
	 */
	private PathwayPath path;

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param path
	 *            setter, see {@link #path}
	 */
	public void setPath(PathwayPath path) {
		this.path = path;
	}

	/**
	 * @return the path, see {@link #path}
	 */
	public PathwayPath getPath() {
		return path;
	}
}
