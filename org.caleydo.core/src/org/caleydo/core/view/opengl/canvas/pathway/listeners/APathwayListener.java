package org.caleydo.core.view.opengl.canvas.pathway.listeners;

import org.caleydo.core.manager.event.IEventListener;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;

/**
 * abstract base class for listeners related to pathway views
 * @author Werner Puff
 *
 */
public abstract class APathwayListener
	implements IEventListener {

	GLPathway glPathway = null;
	
	public GLPathway getGLPathway() {
		return glPathway;
	}

	public void setGLPathway(GLPathway glPathway) {
		this.glPathway = glPathway;
	}

}
