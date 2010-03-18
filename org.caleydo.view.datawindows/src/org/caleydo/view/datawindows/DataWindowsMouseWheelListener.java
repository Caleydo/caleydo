package org.caleydo.view.datawindows;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class DataWindowsMouseWheelListener extends MouseAdapter implements
		MouseWheelListener, MouseMotionListener {
	private GLHyperbolic hyperbolic;
	private double wheelFactor;

	public DataWindowsMouseWheelListener(GLHyperbolic master) {
		hyperbolic = master;
		wheelFactor = 0.2;
	}

	public void mouseWheelMoved(MouseWheelEvent event) {

		System.out.println("scroll:" + event.getWheelRotation());

		// if (event.isControlDown() || event.isAltDown()) {
		System.out.println("mouseWheel");
		hyperbolic.diskZoomIntensity = hyperbolic.diskZoomIntensity
				+ event.getWheelRotation() * wheelFactor;
		hyperbolic.disk.zoomTree(hyperbolic.diskZoomIntensity);
		hyperbolic.disk.centeredNodeSize = hyperbolic.disk
				.findOptimalCenterNodeSize(hyperbolic.disk.getCenteredNode(),
						10);

		if (hyperbolic.diskZoomIntensity < -1) {
			hyperbolic.diskZoomIntensity = -1;
		}
		if (hyperbolic.diskZoomIntensity > 1) {
			hyperbolic.diskZoomIntensity = 1;
		}

		// }

	}
}