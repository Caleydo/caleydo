/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.ViewScrollEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * @author Christian Partl
 *
 */
public class ViewScrollEventListener extends AEventListener<AGLView> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ViewScrollEvent) {
			ViewScrollEvent e = (ViewScrollEvent) event;
			if (e.getReceiver() == handler) {
				handler.onScrolled(e);
			}
		}
	}

}
