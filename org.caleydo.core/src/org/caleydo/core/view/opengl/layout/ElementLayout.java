package org.caleydo.core.view.opengl.layout;

import java.util.ArrayList;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.MinSizeTextRenderer;

/**
 * Size parameters for a single element. There are four ways to specify the dimensions of an element. It can
 * be specified by
 * <ul>
 * <li>specifying a ratio - where 1 takes up the whole space granted by the parent (see
 * {@link #setRatioSizeX(float)} and {@link #setRatioSizeY(float)}</li>
 * <li>specifying an absolute value in gl coordinate space (see {@link #setAbsoluteSizeY(float)} and
 * {@link #setAbsoluteSizeY(float)}</li>
 * <li>specifying an absolute value in pixel space (see {@link #setPixelSizeX(int)} and
 * {@link #setPixelSizeY(int)}. Notice that using pixel sizes requires the {@link PixelGLConverter} for this
 * layout to be set (see {@link #setPixelGLConverter(PixelGLConverter)}.</li>
 * <li>setting the element to grab the remaining available space in the container (see
 * {@link #setGrabX(boolean)} and {@link #grabY(boolean)}</li>
 * </ul>
 * <p>
 * This can be done independently for X and Y
 * </p>
 * <p>
 * If no size is specified, a ratio value of 1 is assumed.
 * </p>
 * <p>
 * The values set are then converted to the coordinates actually used for rendering, which can be retrieved
 * using {@link #getSizeScaledX()} and {@link #getSizeScaledY()}
 * </p>
 * 
 * @author Alexander Lex
 */
public class ElementLayout {

	protected ARenderer renderer;
	protected ArrayList<ARenderer> backgroundRenderers;
	protected ArrayList<ARenderer> foregroundRenderers;

	protected float transformX = 0;
	protected float transformY = 0;
	// float transformScaledX = 0;
	// float transformScaledY = 0;

	/** use the remaining space in X, invalidates absoluteSizeX */
	protected boolean grabX = false;
	/** use the remaining space in Y */
	protected boolean grabY = false;

	protected float absoluteSizeX = Float.NaN;
	protected float absoluteSizeY = Float.NaN;

	protected float ratioSizeX = 1;
	protected float ratioSizeY = 1;

	protected int pixelSizeX = Integer.MIN_VALUE;
	protected int pixelSizeY = Integer.MIN_VALUE;

	protected float sizeScaledX = 0;
	protected float sizeScaledY = 0;

	protected PixelGLConverter pixelGLConverter;

	protected String layoutName;

	protected float[] frameColor = null;

	private MinSizeTextRenderer textRenderer;

	public ElementLayout() {
		renderer = new ARenderer();
		layoutName = "";
	}

	public ElementLayout(String layoutName) {
		renderer = new ARenderer();
		this.layoutName = layoutName;
	}

	public ARenderer getRenderer() {
		return renderer;
	}

	public void setPixelGLConverter(PixelGLConverter pixelGLConverter) {
		this.pixelGLConverter = pixelGLConverter;
	}

	/**
	 * Set the absolute size in GL coordinates of the element in x direction.
	 * 
	 * @param absoluteSizeX
	 */
	public void setAbsoluteSizeX(float absoluteSizeX) {
		this.absoluteSizeX = absoluteSizeX;
	}

	// public float getAbsoluteSizeX() {
	// return absoluteSizeX;
	// }

	/**
	 * Set the absolute size in GL coordinates of the element in y direction.
	 * 
	 * @param absoluteSizeX
	 */
	public void setAbsoluteSizeY(float absoluteSizeY) {
		this.absoluteSizeY = absoluteSizeY;
	}

	//
	// public float getAbsoluteSizeY() {
	// return absoluteSizeY;
	// }

	/**
	 * Set a ratio size in x direction. The ration indicates how much of the containing element this element
	 * occupies. The size has to be normalized between 0 and 1, where 1 is the whole space available for the
	 * rendered elements
	 * 
	 * @param ratioSizeX
	 *            the size of the element in relation to other elements in the same container on a scale of 0
	 *            to 1
	 */
	public void setRatioSizeX(float ratioSizeX) {
		if (ratioSizeX > 1 || ratioSizeX < 0)
			throw new IllegalArgumentException("Ratio sizes must be between 0 and 1");
		this.ratioSizeX = ratioSizeX;
	}

	/**
	 * Set a ratio size in y direction. The ration indicates how much of the containing element this element
	 * occupies. The size has to be normalized between 0 and 1, where 1 is the whole space available for the
	 * rendered elements
	 * 
	 * @param ratioSizeY
	 *            the size of the element in relation to other elements in the same container on a scale of 0
	 *            to 1
	 */
	public void setRatioSizeY(float ratioSizeY) {
		if (ratioSizeY > 1 || ratioSizeY < 0)
			throw new IllegalArgumentException("Ratio sizes must be between 0 and 1");
		this.ratioSizeY = ratioSizeY;
	}

	/**
	 * Set the size of the element in x direction in pixels. As a consequence, the size remains static even if
	 * the window size changes. This requires the PixelGLConverte to be set (see
	 * {@link #setPixelGLConverter(PixelGLConverter)}).
	 * 
	 * @param pixelSizeX
	 */
	public void setPixelSizeX(int pixelSizeX) {
		if (pixelGLConverter == null)
			throw new IllegalStateException("Tried to set a pixel size, but no pixelGLConverter is set.");
		this.pixelSizeX = pixelSizeX;
	}

	/**
	 * Set the size of the element in y direction in pixels. As a consequence, the size remains static even if
	 * the window size changes. This requires the PixelGLConverte to be set (see
	 * {@link #setPixelGLConverter(PixelGLConverter)}).
	 * 
	 * @param pixelSizeY
	 */
	public void setPixelSizeY(int pixelSizeY) {
		if (pixelGLConverter == null)
			throw new IllegalStateException("Tried to set a pixel size, but no pixelGLConverter is set.");
		this.pixelSizeY = pixelSizeY;
	}

	/**
	 * Get the scaled size of X. This is the absolute size actually used for rendering. It is calculated from
	 * the size set via one of the set methods.
	 * 
	 * @return
	 */
	public float getSizeScaledX() {
		return sizeScaledX;
	}

	/**
	 * Get the scaled size of Z. This is the absolute size actually used for rendering. It is calculated from
	 * the size set via one of the set methods.
	 * 
	 * @return
	 */
	public float getSizeScaledY() {
		return sizeScaledY;
	}

	/**
	 * Instruct the element to grab the remaining space in the x direction.
	 */
	public void grabX() {
		this.grabX = true;
	}

	/**
	 * Instruct the element to grab the remaining space in the y direction
	 */
	public void grabY() {
		this.grabY = true;
	}

	public void setFrameColor(float red, float green, float blue, float alpha) {
		frameColor = new float[] { red, green, blue, alpha };
	}

	// ---------------------------- END OF PUBLIC INTERFACE -----------------------------------

	@SuppressWarnings("unused")
	void render(GL2 gl) {
		if ((this instanceof LayoutContainer && TemplateRenderer.DEBUG_CONTAINERS)
			|| (!(this instanceof LayoutContainer) && TemplateRenderer.DEBUG_ELEMENTS)) {

			// if (!this.layoutName.equals("mainColumn"))
			// return;

			float yPositionDebugText = 0;
			Random rand = new Random();

			float[] color;
			if (frameColor == null)
				color = new float[] { rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1 };
			else {
				color = frameColor;
			}
			if (this instanceof LayoutContainer) {
				gl.glColor4fv(color, 0);
				gl.glLineWidth(6);
				yPositionDebugText = getSizeScaledY() / 2;
			}
			else {
				gl.glColor4fv(color, 0);
				gl.glLineWidth(2);
			}
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(0, 0, 0.2f);
			gl.glVertex3f(getSizeScaledX(), 0, 0.2f);
			gl.glVertex3f(getSizeScaledX(), getSizeScaledY(), 0.2f);
			gl.glVertex3f(0, getSizeScaledY(), 0.2f);
			gl.glEnd();

			if (textRenderer == null) {
				textRenderer = new MinSizeTextRenderer();
			}

			textRenderer.setColor(color[0], color[1], color[2], color[3]);
			textRenderer.renderText(gl, layoutName, 0, yPositionDebugText, 0.4f);

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

	void calculateScales(float totalWidth, float totalHeight) {
		if (pixelSizeX != Integer.MIN_VALUE)
			sizeScaledX = pixelGLConverter.getGLWidthForPixelWidth(pixelSizeX);

		else if (!Float.isNaN(absoluteSizeX))
			sizeScaledX = absoluteSizeX;
		else
			sizeScaledX = ratioSizeX * totalWidth;

		if (pixelSizeY != Integer.MIN_VALUE)
			sizeScaledY = pixelGLConverter.getGLHeightForPixelHeight(pixelSizeY);
		else if (!Float.isNaN(absoluteSizeY))
			sizeScaledY = absoluteSizeY;
		else
			sizeScaledY = ratioSizeY * totalHeight;
	}

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

	protected void updateSpacings(Template template) {
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
	float getUnscalableElementHeight() {
		if (pixelSizeY != Integer.MIN_VALUE)
			return pixelGLConverter.getGLHeightForPixelHeight(pixelSizeY);
		else if (!Float.isNaN(absoluteSizeY))
			return absoluteSizeY;
		else
			return 0;

	}

	/**
	 * Get the unscalable height part of this layout
	 * 
	 * @return
	 */
	float getUnscalableElementWidth() {
		if (pixelSizeX != Integer.MIN_VALUE)
			return pixelGLConverter.getGLWidthForPixelWidth(pixelSizeX);
		else if (!Float.isNaN(absoluteSizeX))
			return absoluteSizeX;
		else
			return 0;
	}

	@Override
	public String toString() {
		if (!layoutName.isEmpty())
			return layoutName;
		return super.toString();
	}

}
