package org.caleydo.view.matchmaker.renderer;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

public interface ICompareConnectionRenderer {

	public void init(GL gl);

	public void display(GL gl);

	public void render(GL gl, ArrayList<Vec3f> points);
}
