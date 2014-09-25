/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.ResetAllViewsEvent;
import org.caleydo.core.event.view.ResetViewEvent;

/**
 * Events that signals that all view that are of the type IResettableView should be resetted
 * 
 * @author Alexander Lex
 */
public class ResetViewListener
	extends AEventListener<IResettableView> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ResetViewEvent)
			handler.resetView();

		if (event instanceof ResetAllViewsEvent) {
			System.out.println("WWWWWWWWWWWWWWWWWAAAAAAAAAAAAAAA");
		}
	}

}
