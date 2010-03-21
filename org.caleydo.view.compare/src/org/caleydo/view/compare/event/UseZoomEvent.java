package org.caleydo.view.compare.event;

import org.caleydo.core.manager.event.AEvent;

/**
 * @author Alexander Lex
 *
 */
public class UseZoomEvent extends AEvent {

	private boolean useZoom;

	public UseZoomEvent() {
	}

	public UseZoomEvent(boolean useZoom) {
		this.useZoom = useZoom;
	}

	public void setUseSorting(boolean useZoom) {
		this.useZoom = useZoom;
	}

	public boolean isUseZoom() {
		return useZoom;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
