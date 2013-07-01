/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
