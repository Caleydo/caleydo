/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.idbrowser.internal;

import org.caleydo.core.gui.command.AOpenViewHandler;

/**
 * simple command handler for opening this view
 * 
 * @author Samuel Gratzl
 * 
 */
public class OpenViewHandler extends AOpenViewHandler {
	public OpenViewHandler() {
		super(IDBrowserView.VIEW_TYPE, SINGLE);
	}
}
