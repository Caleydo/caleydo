package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import gleem.linalg.Vec3f;

public class TransformationTest {
	
	float fFirstX;
	float fFirstY;
	float fSecondX;
	float fSecondY;
	
	
	public TransformationTest(float fFirstX, float fFirstY,
		float fSecondX, float fSecondY){
		
		this.fFirstX = fFirstX;
		this.fFirstY = fFirstY;
		this.fSecondX = fSecondX;
		this.fSecondY = fSecondY;
		
	}
	
	public void generateNewView(){
		
		Vec3f firstPoint = new Vec3f(fFirstX,fFirstY,0);
		Vec3f secondPoint = new Vec3f(fSecondX,fSecondY,0);
		
		//TODO: Set Line using this 2 Points;
		//Line == new Y Plane;
		//generate new X Plane by Normal Vector
	}

}
