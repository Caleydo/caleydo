/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.swt;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.ViewManager;
import org.eclipse.swt.widgets.Display;

public abstract class ASWTView
	extends AView
	implements IListenerOwner {

	/**
	 * Constructor.
	 * @param viewType TODO
	 * @param viewName TODO
	 */
	public ASWTView(String viewType, String viewName) {
		super(viewType, viewName);
		ViewManager.get().addView(this);
	}

	public abstract void draw();

	@Override
	public synchronized void queueEvent(final AEventListener<? extends IListenerOwner> listener,
		final AEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				listener.handleEvent(event);
			}
		});
	}

	@Override
	public void registerEventListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterEventListeners() {
		// TODO Auto-generated method stub

	}
}
