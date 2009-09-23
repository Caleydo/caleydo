package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

public interface ITreeProjection
	extends Comparable<ITreeProjection> {

	public Vec3f projectCoordinates(Vec3f fvCoords);

	public int getID();

	public void drawCanvas(GL gl);

	void updateFrustumInfos(float fHeight, float fWidth, float fDepth, float[] fViewSpaceX,
		float fViewSpaceXAbs, float[] fViewSpaceY, float fViewSpaceYAbs);

}
