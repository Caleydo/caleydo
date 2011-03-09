package org.caleydo.view.visbricks.brick;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class FuelBarRenderer extends LayoutRenderer {

	private static final float SIDE_SPACING_PORTION = 0.2f;

	private GLBrick brick;

	public FuelBarRenderer(GLBrick brick) {
		this.brick = brick;
	}

	@Override
	public void render(GL2 gl) {

		ISet set = brick.getSet();
		ContentVirtualArray contentVA = brick.getContentVA();

		if (set == null || contentVA == null)
			return;

		ContentVirtualArray setContentVA = set.getContentData(Set.CONTENT)
				.getContentVA();

		if (setContentVA == null)
			return;

		int totalNumElements = setContentVA.size();

		int currentNumElements = contentVA.size();
		
		float sideSpacing = y * SIDE_SPACING_PORTION;
		float fuelBarWidth = (float) x - 2 * sideSpacing;
		float fuelWidth = (float) fuelBarWidth / totalNumElements * currentNumElements;
		

		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		
		gl.glColor3f(0.2f, 0.2f, 0.2f);
		gl.glVertex3f(sideSpacing, sideSpacing, 0);
		gl.glVertex3f(fuelBarWidth, sideSpacing, 0);
		gl.glColor3f(0.4f, 0.4f, 0.4f);
		gl.glVertex3f(fuelBarWidth, y - sideSpacing, 0);
		gl.glVertex3f(sideSpacing, y - sideSpacing, 0);

		gl.glColor3f(0, 0.3f, 0);
		gl.glVertex3f(sideSpacing, sideSpacing, 0);
		gl.glVertex3f(fuelWidth, sideSpacing, 0);
		gl.glColor3f(0, 1f, 0);
		gl.glVertex3f(fuelWidth, y - sideSpacing, 0);
		gl.glVertex3f(sideSpacing, y - sideSpacing, 0);
		gl.glEnd();
	}

}
