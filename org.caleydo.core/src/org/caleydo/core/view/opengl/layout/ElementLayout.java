package org.caleydo.core.view.opengl.layout;

import java.awt.Container;
import java.util.ArrayList;

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

	protected LayoutRenderer renderer;
	protected ArrayList<LayoutRenderer> backgroundRenderers;
	protected ArrayList<LayoutRenderer> foregroundRenderers;

	/** specifies how much this element is translated in x relative to it's parent layout */
	protected float translateX = 0;
	/** specifies how much this element is translated in y relative to it's parent layout */
	protected float translateY = 0;

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

	/** The uniqueID of the managing class. Used for notifications on collisions via event. */
	protected int managingClassID = -1;
	/** An id to identify the layout. */
	protected int layoutID = -1;

	/**
	 * The currently available width for the layout. Used if only this sub-part of the layout is updated via
	 * {@link #updateSubLayout()}
	 */
	protected float totalWidth = 0;
	/**
	 * The currently available height for the layout. Used if only this sub-part of the layout is updated via
	 * {@link #updateSubLayout()}
	 */
	protected float totalHeight = 0;

	protected boolean debug = false;

	public ElementLayout() {
		renderer = new LayoutRenderer();
		layoutName = "";
	}

	public ElementLayout(String layoutName) {
		renderer = new LayoutRenderer();
		this.layoutName = layoutName;
	}

	/**
	 * Set ids for the layout, which are used for events in case an element doesn't fit into a
	 * {@link Container}
	 * 
	 * @param managingClassID
	 *            the id of the class to be called-back by this layout
	 * @param an
	 *            arbitrary id to identify the layout
	 */
	public void setIDs(int managingClassID, int layoutID) {
		this.managingClassID = managingClassID;
		this.layoutID = layoutID;
	}

	/**
	 * Set a flag specifying whether a frame, showing the extend of this layout should be drawn. This is a
	 * debug option.
	 * 
	 * @param debug
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Get the renderer for this layout
	 * 
	 * @return
	 */
	public LayoutRenderer getRenderer() {
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
		resetX();
		this.absoluteSizeX = absoluteSizeX;
	}

	/**
	 * Set the absolute size in GL coordinates of the element in y direction.
	 * 
	 * @param absoluteSizeX
	 */
	public void setAbsoluteSizeY(float absoluteSizeY) {
		resetY();
		this.absoluteSizeY = absoluteSizeY;
	}

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
		resetX();
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
		resetY();
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
		resetX();
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
		resetY();
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
	 * Instruct the element to grab the remaining space in the x direction. If multiple elements are defined
	 * to grab in one direction, the space is shared evenly.
	 * 
	 * @param grabX
	 *            true if this element should grab remaining space, false if not
	 */
	public void setGrabX(boolean grabX) {
		resetX();
		this.grabX = grabX;
	}

	/**
	 * Instruct the element to grab the remaining space in the y direction. If multiple elements are defined
	 * to grab in one direction, the space is shared evenly.
	 * 
	 * @param grabY
	 *            true if this element should grab remaining space, false if not
	 */
	public void setGrabY(boolean grabY) {
		resetY();
		this.grabY = grabY;
	}

	/**
	 * Set the color for the debug frame.
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	public void setFrameColor(float red, float green, float blue, float alpha) {
		frameColor = new float[] { red, green, blue, alpha };
	}

	/**
	 * Update the layout recursively for all elements below this layout. Requires a previous call to
	 * {@link LayoutManager#updateLayout()}. The values calculated through the LayoutManager in this step are
	 * used for this layout. The sub-layouts are calculated from scratch.
	 */
	public void updateSubLayout() {
		calculateScales(totalWidth, totalHeight);
		updateSpacings();
	}

	/**
	 * Returns true if the height of this element has been set statically (either via
	 * {@link #setAbsoluteSizeY(float)} or {@link #setPixelSizeY(int)})
	 * 
	 * @return true if this is a static layout in y, else false
	 */
	public boolean isHeightStatic() {
		if (!Float.isNaN(absoluteSizeY))
			return true;
		if (pixelSizeY != Integer.MIN_VALUE)
			return true;

		return false;
	}

	/**
	 * Returns true if the width of this element has been set statically (either via
	 * {@link #setAbsoluteSizeX(float)} or {@link #setPixelSizeX(int)})
	 * 
	 * @return true if this is a static layout in x, else false
	 */
	public boolean isWidthStatic() {
		if (!Float.isNaN(absoluteSizeX))
			return true;
		if (pixelSizeX != Integer.MIN_VALUE)
			return true;

		return false;
	}

	/**
	 * Get the value specifying how much this element has to be transformed in X relative to its parent
	 * layout.
	 */
	public float getTranslateX() {
		return translateX;
	}

	/**
	 * Get the value specifying how much this element has to be transformed in Y relative to its parent
	 * layout.
	 */
	public float getTranslateY() {
		return translateY;
	}

	public void setRenderer(LayoutRenderer renderer) {
		this.renderer = renderer;
	}

	public void addBackgroundRenderer(LayoutRenderer renderer) {
		if (backgroundRenderers == null)
			backgroundRenderers = new ArrayList<LayoutRenderer>(3);
		backgroundRenderers.add(renderer);
	}

	public void addForeGroundRenderer(LayoutRenderer renderer) {
		if (foregroundRenderers == null)
			foregroundRenderers = new ArrayList<LayoutRenderer>(3);
		foregroundRenderers.add(renderer);
	}

	// ---------------------------- END OF PUBLIC INTERFACE -----------------------------------

	private void resetX() {
		absoluteSizeX = Float.NaN;
		ratioSizeX = 1;
		pixelSizeX = Integer.MIN_VALUE;
	}

	private void resetY() {
		absoluteSizeY = Float.NaN;
		ratioSizeY = 1;
		pixelSizeY = Integer.MIN_VALUE;
	}

	void render(GL2 gl) {

		gl.glTranslatef(getTranslateX(), getTranslateY(), 0);

		if (debug) {
			float yPositionDebugText = 0;

			float[] color;
			if (frameColor == null)
				color = new float[] { 0, 0.5f, 0.5f, 1 };
			else {
				color = frameColor;
			}
			gl.glColor4fv(color, 0);
			if (this instanceof LayoutContainer) {

				gl.glLineWidth(6);
				yPositionDebugText = getSizeScaledY() / 2;
			}
			else {
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
			for (LayoutRenderer backgroundRenderer : backgroundRenderers) {
				backgroundRenderer.render(gl);
			}
		}
		renderer.render(gl);
		if (foregroundRenderers != null) {
			for (LayoutRenderer foregroundRenderer : foregroundRenderers) {
				foregroundRenderer.render(gl);
			}
		}
		gl.glTranslatef(-getTranslateX(), -getTranslateY(), 0);
	}

	void calculateScales(float totalWidth, float totalHeight) {
		this.totalWidth = totalWidth;
		this.totalHeight = totalHeight;

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

	void setTranslateX(float translateX) {
		this.translateX = translateX;
	}

	void setTranslateY(float translateY) {
		this.translateY = translateY;
	}

	protected void updateSpacings() {
		// LayoutRenderer renderer = ((RenderableLayoutElement) layout).getRenderer();
		if (renderer == null)
			return;
		renderer.setLimits(getSizeScaledX(), getSizeScaledY());
		renderer.updateSpacing(this);
		if (backgroundRenderers != null) {
			for (LayoutRenderer renderer : backgroundRenderers) {
				renderer.setLimits(getSizeScaledX(), getSizeScaledY());
			}
		}
		if (foregroundRenderers != null)

		{
			for (LayoutRenderer renderer : foregroundRenderers) {
				renderer.setLimits(getSizeScaledX(), getSizeScaledY());
			}
		}

	}

	/**
	 * Get the unscalable height part of this layout
	 * 
	 * @return
	 */
	float getUnscalableElementHeight() {
		if (grabY)
			return 0;
		else if (pixelSizeY != Integer.MIN_VALUE)
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
		if (grabX)
			return 0;
		else if (pixelSizeX != Integer.MIN_VALUE)
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
