/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.view.stratomex.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.event.RenameEvent;

/**
 * Listener for renaming a brick
 * 
 * @author Alexander Lex
 * 
 */
public class RenameListener extends AEventListener<GLBrick> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RenameEvent) {
			RenameEvent renameEvent = (RenameEvent) event;
			handler.rename(renameEvent.getID());
		}

	}

}
