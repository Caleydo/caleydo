package org.caleydo.view.datawindows;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class DataWindowsMouseWheelListener extends MouseAdapter implements
		MouseWheelListener {
	private GLHyperbolic hyperbolic;
	private float wheelFactor;
	private int numberOfScrollsToFullScreen;
	private int numberOfScrollsUntilFullScreen;

	public DataWindowsMouseWheelListener(GLHyperbolic master) {
		hyperbolic = master;
		wheelFactor = 0.2f;
		this.numberOfScrollsToFullScreen = 5;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {

		hyperbolic.diskZoomIntensity = hyperbolic.diskZoomIntensity
				+ event.getWheelRotation() * wheelFactor;
		hyperbolic.disk.zoomTree(hyperbolic.diskZoomIntensity);

		if (hyperbolic.diskZoomIntensity < 1) {
			this.numberOfScrollsUntilFullScreen = this.numberOfScrollsToFullScreen;
			if (hyperbolic.displayFullView = true) {
				hyperbolic.displayFullView = false;
			}
		}

		if (hyperbolic.diskZoomIntensity < -1) {
			hyperbolic.diskZoomIntensity = -1;
			if (hyperbolic.displayFullView = true) {
				hyperbolic.displayFullView = false;
			}
		}
		if (hyperbolic.diskZoomIntensity > 1) {
			hyperbolic.diskZoomIntensity = 1;
			this.numberOfScrollsUntilFullScreen--;

		}
		if (this.numberOfScrollsUntilFullScreen == 0) {
			hyperbolic.displayFullView = true;
		}

	}
}