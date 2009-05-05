package org.caleydo.core.view.opengl.canvas.storagebased;

import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.manager.event.view.TriggerPropagationCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.PropagationEvent;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.storagebased.listener.PropagationListener;
import org.caleydo.core.view.opengl.canvas.storagebased.listener.TriggerPropagationCommandListener;

public class GLPropagationHeatMap
	extends GLHeatMap {

	protected PropagationListener propagationListener = null;
	protected TriggerPropagationCommandListener triggerPropagationCommandListener = null;
	
	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public GLPropagationHeatMap(GLCaleydoCanvas glCanvas, String label, IViewFrustum viewFrustum) {
		super(glCanvas, label, viewFrustum);

		this.listModeEnabled = true;
		bUseDetailLevel = false;
		setDisplayListDirty();
	}

	@Override
	public void handleVirtualArrayUpdate(IVirtualArrayDelta delta, String info) {

	}

	public void handlePropagation(IVirtualArrayDelta delta) {
		super.handleVirtualArrayUpdate(delta, null);
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		
		propagationListener = new PropagationListener();
		propagationListener.setHandler(this);
		eventPublisher.addListener(PropagationEvent.class, propagationListener);
		
		triggerPropagationCommandListener = new TriggerPropagationCommandListener();
		triggerPropagationCommandListener.setHandler(this);
		eventPublisher.addListener(TriggerPropagationCommandEvent.class, triggerPropagationCommandListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(propagationListener);
			propagationListener = null;
		}
		if (triggerPropagationCommandListener != null) {
			eventPublisher.removeListener(triggerPropagationCommandListener);
			triggerPropagationCommandListener = null;
		}

		super.unregisterEventListeners();
	}
}
