package org.caleydo.core.view.opengl.canvas.hyperbolic.listeners;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.hyperbolic.SwitchClusterTypeEvent;
import org.caleydo.core.view.opengl.canvas.hyperbolic.GLHyperbolic;

public class SwitchClusterTypeListener extends AEventListener<GLHyperbolic>{

	@Override
	public void handleEvent(AEvent event){
		if(event instanceof SwitchClusterTypeEvent)
			handler.switchClusterType();
	}

}
