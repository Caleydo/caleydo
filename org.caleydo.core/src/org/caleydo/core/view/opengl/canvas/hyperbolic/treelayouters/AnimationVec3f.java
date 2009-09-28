package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import gleem.linalg.Vec3f;



public class AnimationVec3f{
	
	//float stepWidth = 0;
	int steps = 0;
	int numsteps = 0;
	Vec3f start = null;
	Vec3f end = null;
	Vec3f step = null;
	
	public AnimationVec3f(Vec3f start, Vec3f end, float stepSize) {
		step = end.minus(start);
		this.numsteps = (int) (step.length() / stepSize);
		step.normalize();
		step.scale(stepSize);
		this.start = start;
		this.end = end;
	}
	
	public boolean nextStep(){
		start.add(step);
		return (++steps) > numsteps;
	}
	
	public Vec3f getCurrentPos(){
		return start;
	}
	
	public Vec3f getFinalPos(){
		return end;
	}
	
}
