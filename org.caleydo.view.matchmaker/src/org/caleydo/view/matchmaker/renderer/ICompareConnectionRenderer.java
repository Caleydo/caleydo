package org.caleydo.view.matchmaker.renderer;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL2;

public interface ICompareConnectionRenderer {

	public void init(GL2 gl);

	public void display(GL2 gl);

	public void render(GL2 gl, ArrayList<Vec3f> points);
}
