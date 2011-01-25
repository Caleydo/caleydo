package org.caleydo.core.view.opengl.layout;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * Size parameters for a single element
 * 
 * @author Alexander Lex
 */
public class ElementLayout {

	ARenderer renderer;
	ArrayList<ARenderer> backgroundRenderers;
	ArrayList<ARenderer> foregroundRenderers;

	boolean scaleX = true;
	boolean scaleY = true;

	float transformX = 0;
	float transformY = 0;
	// float transformScaledX = 0;
	// float transformScaledY = 0;

	/** use the remaining space in X, invalidates sizeX */
	boolean grabX = false;
	/** use the remaining space in Y */
	boolean grabY = false;
	float sizeX = 0;
	float sizeY = 0;

	float sizeScaledX = 0;
	float sizeScaledY = 0;

	public ElementLayout() {
		renderer = new ARenderer();
	}

	public ARenderer getRenderer() {
		return renderer;
	}

	@SuppressWarnings("unused")
	public void render(GL2 gl) {
		if ((this instanceof LayoutContainer && TemplateRenderer.DEBUG_CONTAINERS)
			|| (!(this instanceof LayoutContainer) && TemplateRenderer.DEBUG_ELEMENTS)) {
			if (this instanceof LayoutContainer) {
				gl.glColor3f(1, 1, 0);
				gl.glLineWidth(4);
			}
			else {
				gl.glColor3f(0, 0, 1);
				gl.glLineWidth(2);
			}
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(0, 0, 0.2f);
			gl.glVertex3f(getSizeScaledX(), 0, 0.2f);
			gl.glVertex3f(getSizeScaledX(), getSizeScaledY(), 0.2f);
			gl.glVertex3f(0, getSizeScaledY(), 0.2f);
			gl.glEnd();
		}
		if (backgroundRenderers != null) {
			for (ARenderer backgroundRenderer : backgroundRenderers) {
				backgroundRenderer.render(gl);
			}
		}
		renderer.render(gl);
		if (foregroundRenderers != null) {
			for (ARenderer foregroundRenderer : foregroundRenderers) {
				foregroundRenderer.render(gl);
			}
		}
	}

	/**
	 * Set the x size of the element. The size has to be normalized between 0 and 1, where 1 is the whole
	 * space available for the rendered elements
	 * 
	 * @param sizeX
	 */
	public void setSizeX(float sizeX) {
		this.sizeX = sizeX;

	}

	public float getSizeX() {
		return sizeX;
	}

	/**
	 * Set the y size of the element. The size has to be normalized between 0 and 1, where 1 is the whole
	 * space available for the rendered elements
	 * 
	 * @param sizeY
	 */
	public void setSizeY(float sizeY) {
		this.sizeY = sizeY;
	}

	public float getSizeY() {
		return sizeY;
	}

	/**
	 * Get the scaled size of X (i.e. not normalized to 0-1)
	 * 
	 * @return
	 */
	public float getSizeScaledX() {
		return sizeScaledX;
	}

	/**
	 * Get the scaled size of Y (i.e. not normalized to 0-1)
	 * 
	 * @return
	 */
	public float getSizeScaledY() {
		return sizeScaledY;
	}

	/**
	 * Instruct the element to grab the remaining space in the x direction
	 */
	public void setGrabX(boolean grabX) {
		this.grabX = grabX;
	}

	/**
	 * Instruct the element to grab the remaining space in the y direction
	 */
	public void setGrabY(boolean grabY) {
		this.grabY = grabY;
	}

	/**
	 * Set whether the values set should be scaled according to the available window, or whether they should
	 * be of static size. Default is true.
	 * 
	 * @param scaleX
	 */
	public void setScaleX(boolean scaleX) {
		this.scaleX = scaleX;
	}

	/**
	 * Set whether the values set should be scaled according to the available window, or whether they should
	 * be of static size. Default is true.
	 * 
	 * @param scaleY
	 */

	public void setScaleY(boolean scaleY) {
		this.scaleY = scaleY;
	}

	void calculateScales(float totalWidth, float totalHeight) {
		if (scaleX) {
			sizeScaledX = sizeX * totalWidth;

			// transformScaledX = transformX * totalWidth;
			// transformScaledY = transformY * totalHeight;
		}
		else {
			sizeScaledX = sizeX;

			// transformScaledX = transformX * totalWidth;
			// transformScaledY = transformY * totalHeight;

		}
		if (scaleY)
			sizeScaledY = sizeY * totalHeight;
		else
			sizeScaledY = sizeY;
	}

	// void setTransforms(float transformX, float transform)

	void setTransformX(float transformX) {
		this.transformX = transformX;
	}

	void setTransformY(float transformY) {
		this.transformY = transformY;
	}

	public float getTransformX() {
		return transformX;

	}

	public float getTransformY() {
		return transformY;

	}

	protected void updateSpacings(ATemplate template) {
		// ARenderer renderer = ((RenderableLayoutElement) layout).getRenderer();
		if (renderer == null)
			return;
		renderer.setLimits(getSizeScaledX(), getSizeScaledY());
		renderer.updateSpacing(template, this);
		if (backgroundRenderers != null) {
			for (ARenderer renderer : backgroundRenderers) {
				renderer.setLimits(getSizeScaledX(), getSizeScaledY());
			}
		}
		if (foregroundRenderers != null)

		{
			for (ARenderer renderer : foregroundRenderers) {
				renderer.setLimits(getSizeScaledX(), getSizeScaledY());
			}
		}

	}

	public void setRenderer(ARenderer renderer) {
		this.renderer = renderer;
	}

	public void addBackgroundRenderer(ARenderer renderer) {
		if (backgroundRenderers == null)
			backgroundRenderers = new ArrayList<ARenderer>(3);
		backgroundRenderers.add(renderer);
	}

	public void addForeGroundRenderer(ARenderer renderer) {
		if (foregroundRenderers == null)
			foregroundRenderers = new ArrayList<ARenderer>(3);
		foregroundRenderers.add(renderer);
	}

	/**
	 * Get the unscalable height part of this layout
	 * 
	 * @return
	 */
	public float getUnscalableElementHeight() {

		if (scaleY)
			return 0;
		else
			return sizeY;

	}

	/**
	 * Get the unscalable height part of this layout
	 * 
	 * @return
	 */
	public float getUnscalableElementWidth() {

		if (scaleX)
			return 0;
		else
			return sizeX;
	}
}
