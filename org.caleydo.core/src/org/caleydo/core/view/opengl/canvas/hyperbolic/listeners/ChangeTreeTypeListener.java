package org.caleydo.core.view.opengl.canvas.hyperbolic.listeners;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.hyperbolic.ChangeTreeTypeEvent;
import org.caleydo.core.view.opengl.canvas.hyperbolic.GLHyperbolic;
import org.caleydo.core.view.opengl.canvas.radial.GLRadialHierarchy;

public class ChangeTreeTypeListener 
extends AEventListener<GLHyperbolic>{

	@Override
	public void handleEvent(AEvent event){
		if(event instanceof ChangeTreeTypeEvent)
			handler.changeTreeType();
	}
}
