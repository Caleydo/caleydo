/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.heatmap.renderer;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

public class DetailToolBar extends ALayoutRenderer {

	GLHeatMap heatMap;
	PickingManager pickingManager;

	private TextureManager iconManager;
	private CaleydoTextRenderer textRender;

	public DetailToolBar(GLHeatMap heatMap) {
		this.heatMap = heatMap;
		iconManager = new TextureManager();
		textRender = heatMap.getTextRenderer();
	}

	@Override
	public void renderContent(GL2 gl) {
		pickingManager = heatMap.getPickingManager();
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.4f);

		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();

		float spacing = y / 10;
		float buttonSize = y - 2 * spacing;

		// ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		//
		// NURBSCurve curve = new NURBSCurve(inputPoints, 30);
		// ArrayList<Vec3f> points = curve.getCurvePoints();
		//
		// // Band border
		// gl.glLineWidth(2);
		// gl.glColor4f(0, 0, 0, 0.6f);
		// gl.glBegin(GL.GL_LINE_STRIP);
		// for (int i = 0; i < points.size(); i++)
		// gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0f);
		// gl.glEnd();
		//
		// compareConnectionRenderer.render(gl, outputPoints);

		float sideSpacing = 2 * spacing;

		float buttonZ = 0.001f;

		Texture tempTexture;

		if (heatMap.getRecordSelectionManager().getNumberOfElements(GLHeatMap.SELECTION_HIDDEN) > 0) {

			tempTexture = iconManager.getIconTexture(EIconTextures.COMPARER_SHOW_HIDDEN);

			tempTexture.enable(gl);
			tempTexture.bind(gl);
			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glColor4f(1, 1, 1, 1);
			gl.glTranslatef(sideSpacing, spacing, 0);
			gl.glPushName(pickingManager.getPickingID(heatMap.getID(), PickingType.HEAT_MAP_HIDE_HIDDEN_ELEMENTS, 1));
			gl.glBegin(GL2.GL_POLYGON);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(0, 0, buttonZ);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(buttonSize, 0, buttonZ);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(buttonSize, buttonSize, buttonZ);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(0, buttonSize, buttonZ);
			gl.glEnd();
			gl.glPopName();

			tempTexture.disable(gl);
			gl.glTranslatef(-sideSpacing, -spacing, 0);
		}
		float secondButtonOffset = 2 * sideSpacing + buttonSize;

		int nrTotal = heatMap.getTablePerspective().getRecordPerspective().getVirtualArray().size();
		int nrVisible = nrTotal - heatMap.getRecordSelectionManager().getNumberOfElements(GLHeatMap.SELECTION_HIDDEN);

		String content;
		if (nrVisible == nrTotal)
			content = nrTotal + " elements";
		else
			content = nrVisible + "/" + nrTotal + " elements";

		gl.glColor3f(1, 1, 1);
		gl.glTranslatef(secondButtonOffset, spacing * 2, 0);
		textRender.begin3DRendering();
		textRender.draw3D(content, 0f, 0f, 0f, 0.0035f);
		textRender.end3DRendering();
		// tempTexture = iconManager.getIconTexture(gl,
		// EIconTextures.COMPARER_SHOW_CAPTIONS);
		//
		// tempTexture.enable();
		// tempTexture.bind();
		// TextureCoords texCoords = tempTexture.getImageTexCoords();
		//
		// gl.glColor4f(1, 1, 1, 1);
		// gl.glPushName(pickingManager.getPickingID(heatMap.getID(),
		// EPickingType.HEAT_MAP_SHOW_CAPTIONS, 1));
		// gl.glBegin(GL2.GL_POLYGON);
		// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		// gl.glVertex3f(0, 0, buttonZ);
		// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		// gl.glVertex3f(buttonSize, 0, buttonZ);
		// gl.glTexCoord2f(texCoords.right(), texCoords.top());
		// gl.glVertex3f(buttonSize, buttonSize, buttonZ);
		// gl.glTexCoord2f(texCoords.left(), texCoords.top());
		// gl.glVertex3f(0, buttonSize, buttonZ);
		// gl.glEnd();
		// gl.glPopName();
		//
		// tempTexture.disable();

		gl.glTranslatef(-secondButtonOffset, -spacing * 2, 0);

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}
}
