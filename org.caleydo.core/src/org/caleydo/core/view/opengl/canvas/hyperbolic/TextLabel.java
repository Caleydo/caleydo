package org.caleydo.core.view.opengl.canvas.hyperbolic;

import gleem.linalg.Vec3f;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

public class TextLabel
	extends TextRenderer {

	Rectangle2D rectCanvas = null;
	Rectangle2D rectText = null;
	private String text = null;
	private float fScalingX = 1;
	private float fScalingY = 1;
	private float fZCoord = 0.0f;
	private float[] fTextColor = HyperbolicRenderStyle.LABEL_TEXT_COLOR;
	private float[] fCanvasColor = HyperbolicRenderStyle.LABEL_CANVAS_COLOR;
	//private Vec3f fCanvasPosition;
	private Vec3f fTextPosition;

	/**
	 * Constructor.
	 * 
	 * @param font
	 */
	public TextLabel(Font font) {
		super(font);
	}

	/**
	 * Constructor.
	 * 
	 * @param font
	 * @param mipmap
	 */
	public TextLabel(Font font, boolean mipmap) {
		super(font, mipmap);
	}

	/**
	 * Constructor.
	 * 
	 * @param font
	 * @param antialiased
	 * @param useFractionalMetrics
	 */
	public TextLabel(Font font, boolean antialiased, boolean useFractionalMetrics) {
		super(font, antialiased, useFractionalMetrics);
	}

	/**
	 * Constructor.
	 * 
	 * @param font
	 * @param antialiased
	 * @param useFractionalMetrics
	 * @param renderDelegate
	 */
	public TextLabel(Font font, boolean antialiased, boolean useFractionalMetrics,
		TextRenderer.RenderDelegate renderDelegate) {
		super(font, antialiased, useFractionalMetrics, renderDelegate);
	}

	/**
	 * Constructor.
	 * 
	 * @param font
	 * @param antialiased
	 * @param useFractionalMetrics
	 * @param renderDelegate
	 * @param mipmap
	 */
	public TextLabel(Font font, boolean antialiased, boolean useFractionalMetrics,
		TextRenderer.RenderDelegate renderDelegate, boolean mipmap) {
		super(font, antialiased, useFractionalMetrics, renderDelegate, mipmap);
	}

	public void setText(String text) {
		this.text = text;
	}

	public void place(float fXCoord, float fYCoord, float fZCoord, float fHeight, float fWidth) {
		if (text == null)
			return;
		this.fZCoord = fZCoord;
		rectCanvas = getBounds(text);
		fScalingX = (float) (fWidth / rectCanvas.getWidth());
		fScalingY = (float) (fHeight / rectCanvas.getHeight());
		float min = Math.min(fScalingX, fScalingY);

		fTextPosition = new Vec3f(fXCoord + fWidth / 2.0f - (float)rectCanvas.getWidth() * min / 2.0f,
			fYCoord + (float)rectCanvas.getHeight() * min / 5.0f, fZCoord + 0.1f);
		
		rectCanvas.setRect(fXCoord, fYCoord, Math.abs(rectCanvas.getWidth()) * fScalingX,
			rectCanvas.getHeight() * fScalingY);
		//float fOffX = (float) rectCanvas.getWidth() * 0.02f;
		//float fOffY = (float) rectCanvas.getHeight() * 0.02f;
		//fTextPosition = new Vec3f((float)(rectCanvas.getX() + fOffX), (float)(rectCanvas.getY()+fOffY), fZCoord+0.01f);
		
		// Text should fit into box, with 2% space
		// rectCanvas.setRect(fXCoord, fYCoord, rectCanvas.getWidth(), rectCanvas.getHeight());
		//		
		//		

		// rectText = new Rectangle();
		// rectText.setRect((float)(rectCanvas.getX() + fOffX),(float) (rectCanvas.getY() + fOffY),
		// (float)(rectCanvas.getWidth() - fOffX),
		// (float)(rectCanvas.getHeight() - fOffY));
	}

	public void draw3d(GL gl, boolean bDrawCanvas) {
		if (text == null || rectCanvas == null || fTextPosition == null)
			return;
		if (bDrawCanvas) {
			gl.glColor4fv(fCanvasColor, 0);
			gl.glBegin(gl.GL_POLYGON);
			gl.glVertex3f((float) rectCanvas.getX(), (float) rectCanvas.getY(), fZCoord);
			gl.glVertex3f((float) (rectCanvas.getX() + rectCanvas.getWidth()), (float) rectCanvas.getY(),
				fZCoord);
			gl.glVertex3f((float) (rectCanvas.getX() + rectCanvas.getWidth()),
				(float) (rectCanvas.getY() + rectCanvas.getHeight()), fZCoord);
			gl.glVertex3f((float) rectCanvas.getX(), (float) (rectCanvas.getY() + rectCanvas.getHeight()),
				fZCoord);
			gl.glEnd();
		}
		super.setColor(fTextColor[0], fTextColor[1], fTextColor[2], fTextColor[3]);
		begin3DRendering();
		super.draw3D(text, fTextPosition.x(), fTextPosition.y(), fTextPosition.z(), Math.min(
			fScalingX, fScalingY));
		end3DRendering();
	}

	public void setColor(float[] fTextColor, float[] fCanvasColor) {
		setTextColor(fTextColor);
		setCanvasColor(fCanvasColor);
	}

	public void setTextColor(float[] fColor) {
		if (fColor.length != 4)
			throw new IllegalArgumentException("TextLabel: Color Array must be lenght of 4");
		this.fTextColor = fColor;
	}

	public void setCanvasColor(float[] fColor) {
		if (fColor.length != 4)
			throw new IllegalArgumentException("TextLabel: Color Array must be lenght of 4");
		this.fCanvasColor = fColor;
	}

}
