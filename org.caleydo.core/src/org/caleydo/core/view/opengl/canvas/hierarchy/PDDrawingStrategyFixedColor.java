package org.caleydo.core.view.opengl.canvas.hierarchy;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class PDDrawingStrategyFixedColor
	extends PDDrawingStrategy {
	
	private float fFillColorR;
	private float fFillColorG;
	private float fFillColorB;
	private float fFillAlpha;
	
	private float fBorderColorR;
	private float fBorderColorG;
	private float fBorderColorB;
	private float fBorderAlpha;
	
	public PDDrawingStrategyFixedColor() {
		fFillColorR = 0.0f;
		fFillColorG = 0.0f;
		fFillColorB = 0.0f;
		fFillAlpha = 1.0f;
		fBorderColorR = 0.0f;
		fBorderColorG = 0.0f;
		fBorderColorB = 0.0f;
		fBorderAlpha = 1.0f;
	}
	

	@Override
	public void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw) {
		
		if(pdDiscToDraw == null)
			return;
		
		float fRadius = pdDiscToDraw.getCurrentWidth();
		
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		
		gl.glColor4f(fFillColorR, fFillColorG, fFillColorB, fFillAlpha);
		GLPrimitives.renderCircle(gl, glu, fRadius,	iNumSlicesPerFullDisc);
		
		gl.glColor4f(fBorderColorR, fBorderColorG, fBorderColorB, fBorderAlpha);
		GLPrimitives.renderCircleBorder(gl, glu, fRadius, iNumSlicesPerFullDisc, 2);
		
		gl.glPopAttrib();

	}

	@Override
	public void drawPartialDisc(GL gl, GLU glu, PartialDisc pdDiscToDraw) {
		
		if(pdDiscToDraw == null)
			return;
		
		float fStartAngle = pdDiscToDraw.getCurrentStartAngle();
		float fAngle = pdDiscToDraw.getCurrentAngle();
		float fInnerRadius = pdDiscToDraw.getCurrentInnerRadius();
		float fWidth = pdDiscToDraw.getCurrentWidth();
		
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		
		gl.glColor4f(fFillColorR, fFillColorG, fFillColorB, fFillAlpha);
		GLPrimitives.renderPartialDisc(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle, fAngle,
			iNumSlicesPerFullDisc);
		
		gl.glColor4f(fBorderColorR, fBorderColorG, fBorderColorB, fBorderAlpha);
		GLPrimitives.renderPartialDiscBorder(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle, fAngle,
			iNumSlicesPerFullDisc, 2);
		
		gl.glPopAttrib();

	}
	
	public void setFillColor(float fColorR, float fColorG, float fColorB, float fAlpha) {
		fFillColorR = fColorR;
		fFillColorG = fColorG;
		fFillColorB = fColorB;
		fFillAlpha = fAlpha;
	}
	
	public void setBorderColor(float fColorR, float fColorG, float fColorB, float fAlpha) {
		fBorderColorR = fColorR;
		fBorderColorG = fColorG;
		fBorderColorB = fColorB;
		fBorderAlpha = fAlpha;
	}

}
