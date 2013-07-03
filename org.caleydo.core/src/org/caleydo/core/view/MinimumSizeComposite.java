/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.view.SetMinViewSizeEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.listener.SetMinViewSizeEventListener;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

public class MinimumSizeComposite
	extends ScrolledComposite
	implements IListenerOwner {

	SetMinViewSizeEventListener setMinSizeEventListener;

	// TODO evaluate whether we can remove this
	@Deprecated
	public MinimumSizeComposite(Composite parent, int style) {
		super(parent, style);
		registerEventListeners();
		addListener(SWT.MouseWheel, new Listener() {
			@Override
			public void handleEvent(Event event) {

				Point origin = getOrigin();
				if (event.count < 0) {
					origin.y = Math.min(origin.y + 40, origin.y + getSize().y);
				}
				else {
					origin.y = Math.max(origin.y - 40, 0);
				}
				setOrigin(origin);
			}
		});
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				unregisterEventListeners();
			}
		});

	}

	public void setView(AGLView view) {
		setMinSizeEventListener.setView(view);
	}

	@Override
	public void queueEvent(final AEventListener<? extends IListenerOwner> listener, final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				listener.handleEvent(event);
			}
		});
	}

	@Override
	public void registerEventListeners() {
		setMinSizeEventListener = new SetMinViewSizeEventListener();
		setMinSizeEventListener.setHandler(this);
		GeneralManager.get().getEventPublisher()
			.addListener(SetMinViewSizeEvent.class, setMinSizeEventListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (setMinSizeEventListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(setMinSizeEventListener);
			setMinSizeEventListener = null;
		}
	}
}
