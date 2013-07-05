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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

public class MinimumSizeComposite
	extends ScrolledComposite
	implements IListenerOwner {

	SetMinViewSizeEventListener setMinSizeEventListener;

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

	@Override
	public void dispose() {

		unregisterEventListeners();
		super.dispose();
	}
}
