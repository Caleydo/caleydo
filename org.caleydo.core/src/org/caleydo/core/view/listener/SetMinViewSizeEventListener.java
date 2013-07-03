/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.MinSizeAppliedEvent;
import org.caleydo.core.event.view.SetMinViewSizeEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.MinimumSizeComposite;
import org.caleydo.core.view.opengl.canvas.AGLView;

public class SetMinViewSizeEventListener
	extends AEventListener<MinimumSizeComposite> {

	private AGLView view;

	@Override
	public void handleEvent(AEvent event) {
		if (handler.isDisposed()) {
			GeneralManager.get().getEventPublisher().removeListener(this);
			return;
		}

		if (event instanceof SetMinViewSizeEvent) {
			SetMinViewSizeEvent setMinViewSizeEvent = (SetMinViewSizeEvent) event;
			if (setMinViewSizeEvent.getView() == view) {
				handler.setMinSize(setMinViewSizeEvent.getMinWidth(), setMinViewSizeEvent.getMinHeight());
				MinSizeAppliedEvent e = new MinSizeAppliedEvent();
				e.setView(view);
				e.setSender(this);
				GeneralManager.get().getEventPublisher().triggerEvent(e);
			}
		}
	}

	public AGLView getView() {
		return view;
	}

	public void setView(AGLView view) {
		this.view = view;
	}

}
