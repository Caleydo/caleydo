package org.caleydo.core.view.opengl.canvas.storagebased;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.radial.event.IClusterNodeEventReceiver;

public abstract class AGLDendrogram
	extends AStorageBasedView
	implements IClusterNodeEventReceiver {

	protected AGLDendrogram(GLCaleydoCanvas glCanvas, String label, IViewFrustum viewFrustum) {
		super(glCanvas, label, viewFrustum);

	}

}
