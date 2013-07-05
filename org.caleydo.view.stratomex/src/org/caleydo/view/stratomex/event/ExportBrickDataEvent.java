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
package org.caleydo.view.stratomex.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Event for triggering export of data of a certain brick.
 *
 * @author Christian Partl
 *
 */
public class ExportBrickDataEvent extends AEvent {

	private GLBrick brick;

	private boolean exportIdentifiersOnly;

	public ExportBrickDataEvent(GLBrick brick, boolean exportIdentifiersOnly) {
		this.brick = brick;
		this.exportIdentifiersOnly = exportIdentifiersOnly;
	}

	@Override
	public boolean checkIntegrity() {
		return brick != null;
	}

	/**
	 * @return the brick
	 */
	public GLBrick getBrick() {
		return brick;
	}

	/**
	 * @param brick
	 *            the brick to set
	 */
	public void setBrick(GLBrick brick) {
		this.brick = brick;
	}

	/**
	 * @param exportIdentifiersOnly
	 *            the exportIdentifiersOnly to set
	 */
	public void setExportIdentifiersOnly(boolean exportIdentifiersOnly) {
		this.exportIdentifiersOnly = exportIdentifiersOnly;
	}

	/**
	 * @return the exportIdentifiersOnly
	 */
	public boolean isExportIdentifiersOnly() {
		return exportIdentifiersOnly;
	}

}
