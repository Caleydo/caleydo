package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import gleem.linalg.Vec3f;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;


import com.sun.opengl.util.j2d.TextRenderer;

public class TextRenderingNode
	extends ADrawAbleNode {
	
	TextRenderer textRenderer = null;
	
	float scaling = 0.01f;

	//TextRenderer text = null;
	public TextRenderingNode(String sNodeName, int iNodeID) {
		super(sNodeName, iNodeID, null);
		textRenderer = new TextRenderer(new Font(HyperbolicRenderStyle.LABEL_FONT_NAME, HyperbolicRenderStyle.LABEL_FONT_STYLE, HyperbolicRenderStyle.LABEL_FONT_SIZE));
		Rectangle2D rect = textRenderer.getBounds(this.getNodeName());
		fHeight = (float) rect.getHeight() * scaling;
		fWidth = (float) rect.getWidth() * scaling;
	}
	
	protected List<Vec3f> getConnectionPointsSpecialNode() {
		ArrayList<Vec3f> alPoints = new ArrayList<Vec3f>();
		int iSegPerLine = HyperbolicRenderStyle.DA_OBJ_NUM_CONTACT_POINTS / 4;
		//float fSideL = Math.min(fWidth, fHeight) / 2f;
		// first add corners
		alPoints.add(new Vec3f(fXCoord, fYCoord, fZCoord));
		alPoints.add(new Vec3f(fXCoord + fWidth, fYCoord, fZCoord));
		alPoints.add(new Vec3f(fXCoord + fWidth, fYCoord + fHeight, fZCoord));
		alPoints.add(new Vec3f(fXCoord, fYCoord + fHeight, fZCoord));
		// up, down
		for (int i = 1; i < iSegPerLine; i++) {
			alPoints.add(new Vec3f(fXCoord + fWidth / iSegPerLine * i, fYCoord + fHeight, fZCoord));
			alPoints.add(new Vec3f(fXCoord + fWidth / iSegPerLine * i, fYCoord, fZCoord));
		}
		// left, right
		for (int i = 1; i < iSegPerLine; i++) {
			alPoints.add(new Vec3f(fXCoord + fWidth, fYCoord +  fHeight / iSegPerLine * i, fZCoord));
			alPoints.add(new Vec3f(fXCoord , fYCoord +  fHeight / iSegPerLine * i, fZCoord));
		}
		return alPoints;
	}

	protected void drawSpecialNode(GL gl) {
		//Vec3f coords = this.getProjectedCoordinates();
		textRenderer.setColor(0, 0, 0, 1);
		
		gl.glColor4f(0.2f, 0.2f, 0.2f, 0.2f);
		gl.glBegin(gl.GL_POLYGON);
		gl.glVertex3f(fXCoord, fYCoord, fZCoord);
		gl.glVertex3f(fXCoord + fWidth, fYCoord, fZCoord);
		gl.glVertex3f(fXCoord + fWidth, fYCoord + fHeight, fZCoord);
		gl.glVertex3f(fXCoord, fYCoord + fHeight, fZCoord);
		gl.glEnd();
		
		textRenderer.begin3DRendering();
		textRenderer.draw3D(this.getNodeName(), fXCoord, fYCoord, fZCoord, scaling);
		textRenderer.end3DRendering();
	}

}
