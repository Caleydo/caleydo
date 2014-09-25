/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.imageviewer.internal;

import org.caleydo.core.internal.cmd.AOpenViewHandler;

/**
 * simple command handler for opening this view
 *
 * @author Thomas Geymayer
 *
 */
public class OpenViewHandler extends AOpenViewHandler {
	public OpenViewHandler() {
		super(ImageViewerView.VIEW_TYPE, MULTIPLE);
	}
}
