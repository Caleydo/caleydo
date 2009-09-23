package org.caleydo.rcp.view.opengl;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.SetMinViewSizeEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;

public class SetMinViewSizeEventListener
	extends AEventListener<MinimumSizeComposite> {

	private AGLEventListener view;

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
			}
		}
	}

	public AGLEventListener getView() {
		return view;
	}

	public void setView(AGLEventListener view) {
		this.view = view;
	}

}
