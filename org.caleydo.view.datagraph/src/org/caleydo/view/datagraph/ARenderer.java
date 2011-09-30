package org.caleydo.view.datagraph;

import javax.media.opengl.GL2;

public abstract class ARenderer {
	
	protected float x;
	protected float y;
	
	public void setLimits(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getY() {
		return y;
	}
	
	public abstract void render(GL2 gl);

}
