package org.caleydo.core.view.opengl.canvas.pathway;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.IEventListener;

public class EnableTexturesListener
	implements IEventListener {

	GLPathway glPathway = null;
	
	@Override
	public void handleEvent(AEvent event) {
		glPathway.enablePathwayTextures(true);
	}

	public GLPathway getGLPathway() {
		return glPathway;
	}

	public void setGLPathway(GLPathway glPathway) {
		this.glPathway = glPathway;
	}

}
