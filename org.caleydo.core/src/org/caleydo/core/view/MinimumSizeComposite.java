/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view;

import java.awt.Rectangle;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.view.SetMinViewSizeEvent;
import org.caleydo.core.event.view.ViewScrollEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.listener.SetMinViewSizeEventListener;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

public class MinimumSizeComposite extends ScrolledComposite implements IListenerOwner {

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
				} else {
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

		SelectionAdapter scrollBarListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				triggerScrollEvent(getOrigin().x, getOrigin().y);
			}
		};
		Listener l = new Listener() {

			@Override
			public void handleEvent(Event event) {
				triggerScrollEvent(getOrigin().x, getOrigin().y);

			}
		};

		// getHorizontalBar().addListener(SWT.Modify, l);
		// getVerticalBar().addListener(SWT.Modify, l);

		getHorizontalBar().addSelectionListener(scrollBarListener);
		getVerticalBar().addSelectionListener(scrollBarListener);
	}

	@Override
	public void setOrigin(int x, int y) {
		super.setOrigin(x, y);
		triggerScrollEvent(x, y);
	}

	public void setView(AGLView view) {
		setMinSizeEventListener.setView(view);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		triggerScrollEvent(getOrigin().x, getOrigin().y);
	}

	private void triggerScrollEvent(int x, int y) {
		org.eclipse.swt.graphics.Rectangle r = getClientArea();
		ViewScrollEvent event = new ViewScrollEvent(x, y,r.width, r.height);
		event.to(setMinSizeEventListener.getView());
		EventPublisher.trigger(event);
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
		GeneralManager.get().getEventPublisher().addListener(SetMinViewSizeEvent.class, setMinSizeEventListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (setMinSizeEventListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(setMinSizeEventListener);
			setMinSizeEventListener = null;
		}
	}
}
