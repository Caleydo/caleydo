package org.caleydo.view.heatmap.heatmap.renderer;

import java.awt.Font;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

public class DetailToolBar extends ARenderer {

	GLHeatMap heatMap;
	PickingManager pickingManager;

	private TextureManager iconManager;
	private TextRenderer textRender = new CaleydoTextRenderer(new Font("Arial",
			Font.PLAIN, 24), false);

	public DetailToolBar(GLHeatMap heatMap) {
		this.heatMap = heatMap;
		iconManager = new TextureManager();

	}

	@Override
	public void render(GL gl) {
		pickingManager = heatMap.getPickingManager();
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.4f);

		gl.glBegin(GL.GL_POLYGON);
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

		if (heatMap.getContentSelectionManager().getNumberOfElements(
				GLHeatMap.SELECTION_HIDDEN) > 0) {

			tempTexture = iconManager.getIconTexture(gl,
					EIconTextures.COMPARER_SHOW_HIDDEN);

			tempTexture.enable();
			tempTexture.bind();
			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glColor4f(1, 1, 1, 1);
			gl.glTranslatef(sideSpacing, spacing, 0);
			gl.glPushName(pickingManager.getPickingID(heatMap.getID(),
					EPickingType.HEAT_MAP_HIDE_HIDDEN_ELEMENTS, 1));
			gl.glBegin(GL.GL_POLYGON);
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

			tempTexture.disable();
			gl.glTranslatef(-sideSpacing, -spacing, 0);
		}
		float secondButtonOffset = 2 * sideSpacing + buttonSize;

		int nrTotal = heatMap.getContentVA().size();
		int nrVisible = nrTotal - heatMap.getContentSelectionManager().getNumberOfElements(GLHeatMap.SELECTION_HIDDEN);

		String content;
		if(nrVisible == nrTotal)
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
		// gl.glBegin(GL.GL_POLYGON);
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
}
