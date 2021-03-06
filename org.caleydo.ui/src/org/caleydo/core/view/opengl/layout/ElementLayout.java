/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.id.IDCreator;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.util.Zoomer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * An ElementLayout holds the size, and the position of an element in a layout. The position is specified implicitly
 * through the nesting in parent {@link ALayoutContainer}s. The size can be specified in four ways:
 * <ol>
 * <li>specifying a ratio - where 1 takes up the whole space granted by the parent (see {@link #setRatioSizeX(float)}
 * and {@link #setRatioSizeY(float)}</li>
 * <li>specifying an absolute value in gl coordinate space (see {@link #setAbsoluteSizeY(float)} and
 * {@link #setAbsoluteSizeY(float)}</li>
 * <li>specifying an absolute value in pixel space (see {@link #setPixelSizeX(int)} and {@link #setPixelSizeY(int)}.
 * Notice that using pixel sizes requires the {@link PixelGLConverter} for this layout to be set (see
 * {@link #setPixelGLConverter(PixelGLConverter)}.</li>
 * <li>setting the element to grab the remaining available space in the container (see {@link #setGrabX(boolean)} and
 * {@link #grabY(boolean)}</li>
 * <li>Setting dynamic size units, where the non-static size is equally distributed among all size units using
 * {@link #setDynamicSizeUnitsX(int)} and {@link #setDynamicSizeUnitsY(int)}</li>
 * </ol>
 * <p>
 * This can be done independently for X and Y
 * </p>
 * <p>
 * If no size is specified, a ratio value of 1 is assumed.
 * </p>
 * <p>
 * The values set are then converted to the coordinates actually used for rendering, which can be retrieved using
 * {@link #getSizeScaledX()} and {@link #getSizeScaledY()}
 * </p>
 * <p>
 * An ElementLayout also holds the {@link ALayoutRenderer}s which define its appearance.
 * </p>
 *
 * @author Alexander Lex
 */
public class ElementLayout implements Comparable<ElementLayout> {

	private Integer id;

	/**
	 * The manager for this layout, this element is, or is a sub-element of {@link LayoutManager#baseElementLayout}
	 */
	protected LayoutManager layoutManager;

	private ALayoutRenderer renderer;
	private List<ALayoutRenderer> backgroundRenderers = new ArrayList<>(1);
	private List<ALayoutRenderer> foregroundRenderers = new ArrayList<>(1);

	/** specifies how much this element is translated in x absolutely */
	float translateX = 0;
	/** specifies how much this element is translated in y absolutely */
	float translateY = 0;

	/** The width in actual OpenGL coordinates */
	float sizeScaledX = 0;
	/** The height in actual OpenGL coordinates */
	float sizeScaledY = 0;

	// sizes at the various scales

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

	protected int dynamicSizeUnitsX = Integer.MIN_VALUE;
	protected int dynamicSizeUnitsY = Integer.MIN_VALUE;

	private String layoutName = "";

	protected float[] frameColor = null;

	/**
	 * Flag determining whether the element layout is hidden or not. If it is hidden, it is not rendered and the size is
	 * not taken into account for the element and all possible sub-elements.
	 */
	protected boolean isHidden = false;

	private CaleydoTextRenderer textRenderer;
	/**
	 * The uniqueID of the managing class. Used for notifications on collisions via event.
	 */
	protected int managingClassID = -1;
	/** An id to identify the layout. */
	protected int externalID = -1;

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

	/** Flag for rendering in debug mode, which shows a frame around the renderer */
	protected boolean debug = false;

	protected Zoomer zoomer;

	/**
	 * Determines the point in time this element layout is rendered if its {@link ALayoutContainer} uses priority
	 * rendering. the higher the better
	 */
	protected int renderingPriority = 0;

	/** Handler for transitions */
	private TransitionSpecification transitionSpecification;

	boolean isTransitionActive = false;

	ETransitionDirection appearanceDirection = ETransitionDirection.VERTICAL;
	ETransitionDirection removalDirection = ETransitionDirection.VERTICAL;

	public ElementLayout() {
		this("");
	}

	public ElementLayout(String layoutName) {
		id = IDCreator.createVMUniqueID(ElementLayout.class);
		this.layoutName = layoutName == null ? "" : layoutName;
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public Integer getID() {
		return id;
	}

	@Override
	public int compareTo(ElementLayout o) {
		return o.renderingPriority - this.renderingPriority;
	}

	/**
	 * @param layoutManager
	 *            setter, see {@link #layoutManager}
	 */
	void setLayoutManager(LayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}

	/**
	 * @return the layoutManager, see {@link #layoutManager}
	 */
	public LayoutManager getLayoutManager() {
		return layoutManager;
	}

	/**
	 * Destroys the element layout, associated {@link ALayoutRenderer}s.
	 *
	 * @param gl
	 */
	public void destroy(GL2 gl) {
		if (zoomer != null) {
			zoomer.destroy();
			zoomer = null;
		}
		if (renderer != null)
			renderer.destroy(gl);
		renderer = null;

		if (foregroundRenderers != null) {
			for (ALayoutRenderer foregroundRenderer : foregroundRenderers) {
				foregroundRenderer.destroy(gl);
			}
			foregroundRenderers.clear();
		}

		if (backgroundRenderers != null) {
			for (ALayoutRenderer foregroundRenderer : backgroundRenderers) {
				foregroundRenderer.destroy(gl);
			}
			backgroundRenderers.clear();
		}

	}

	/**
	 * Set ids for the layout, which are used for events in case an element doesn't fit into a {@link Container}
	 *
	 * @param managingClassID
	 *            the id of the class to be called-back by this layout
	 * @param an
	 *            arbitrary id to identify the layout
	 */
	public void setIDs(int managingClassID, int layoutID) {
		this.managingClassID = managingClassID;
		this.externalID = layoutID;
	}

	/**
	 * Set a flag specifying whether a frame, showing the extend of this layout should be drawn. This is a debug option.
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
	public ALayoutRenderer getRenderer() {
		return renderer;
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
	 * Set a ratio size in x direction. The ration indicates how much of the containing element this element occupies.
	 * The size has to be normalized between 0 and 1, where 1 is the whole space available for the rendered elements
	 *
	 * @param ratioSizeX
	 *            the size of the element in relation to other elements in the same container on a scale of 0 to 1
	 */
	public void setRatioSizeX(float ratioSizeX) {
		if (ratioSizeX > 1 || ratioSizeX < 0)
			throw new IllegalArgumentException("Ratio sizes must be between 0 and 1, but was: " + ratioSizeX);
		resetX();
		this.ratioSizeX = ratioSizeX;
	}

	/**
	 * Set a ratio size in y direction. The ration indicates how much of the containing element this element occupies.
	 * The size has to be normalized between 0 and 1, where 1 is the whole space available for the rendered elements
	 *
	 * @param ratioSizeY
	 *            the size of the element in relation to other elements in the same container on a scale of 0 to 1
	 */
	public void setRatioSizeY(float ratioSizeY) {
		if (ratioSizeY > 1 || ratioSizeY < 0)
			throw new IllegalArgumentException("Ratio sizes must be between 0 and 1, but was: " + ratioSizeY);
		resetY();
		this.ratioSizeY = ratioSizeY;
	}

	/**
	 * Set the size of the element in x direction in pixels. As a consequence, the size remains static even if the
	 * window size changes. This requires the PixelGLConverte to be set (see
	 * {@link #setPixelGLConverter(PixelGLConverter)}).
	 *
	 * @param pixelSizeX
	 */
	public void setPixelSizeX(int pixelSizeX) {
		resetX();
		this.pixelSizeX = pixelSizeX;
	}

	/**
	 * Set the size of the element in y direction in pixels. As a consequence, the size remains static even if the
	 * window size changes. This requires the PixelGLConverte to be set (see
	 * {@link #setPixelGLConverter(PixelGLConverter)}).
	 *
	 * @param pixelSizeY
	 */
	public void setPixelSizeY(int pixelSizeY) {
		resetY();
		this.pixelSizeY = pixelSizeY;
	}

	/**
	 * @param dynamicSizeUnitsX
	 *            setter, see {@link #dynamicSizeUnitsX}
	 */
	public void setDynamicSizeUnitsX(int dynamicSizeUnitsX) {
		this.dynamicSizeUnitsX = dynamicSizeUnitsX;
	}

	/**
	 * @param dynamicSizeUnitsY
	 *            setter, see {@link #dynamicSizeUnitsY}
	 */
	public void setDynamicSizeUnitsY(int dynamicSizeUnitsY) {
		this.dynamicSizeUnitsY = dynamicSizeUnitsY;
	}

	/**
	 * Get the scaled size of X. This is the absolute size actually used for rendering. It is calculated from the size
	 * set via one of the set* methods.
	 *
	 * @return
	 */
	public float getSizeScaledX() {
		if (isHidden)
			return 0;
		return sizeScaledX;
	}

	/**
	 * Get the scaled size of Y. This is the absolute size actually used for rendering. It is calculated from the size
	 * set via one of the set* methods.
	 *
	 * @return
	 */
	public float getSizeScaledY() {
		if (isHidden)
			return 0;
		return sizeScaledY;
	}

	/**
	 * Instruct the element to grab the remaining space in the x direction. If multiple elements are defined to grab in
	 * one direction, the space is shared evenly.
	 *
	 * @param grabX
	 *            true if this element should grab remaining space, false if not
	 */
	public void setGrabX(boolean grabX) {
		resetX();
		this.grabX = grabX;
	}

	/**
	 * Instruct the element to grab the remaining space in the y direction. If multiple elements are defined to grab in
	 * one direction, the space is shared evenly.
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
	 * {@link LayoutManager#updateLayout()}. The values calculated through the LayoutManager in this step are used for
	 * this layout. The sub-layouts are calculated from scratch.
	 */
	public void updateSubLayout() {
		if (isHidden)
			return;
		calculateScales(totalWidth, totalHeight, null, null);
		updateSpacings();
	}

	/**
	 * Returns true if the height of this element has been set statically (either via {@link #setAbsoluteSizeY(float)}
	 * or {@link #setPixelSizeY(int)})
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
	 * Returns true if the width of this element has been set statically (either via {@link #setAbsoluteSizeX(float)} or
	 * {@link #setPixelSizeX(int)})
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
	 * Get the value specifying how much this element has to be transformed in X absolutely
	 */
	public float getTranslateX() {
		return translateX;
	}

	/**
	 * Get the value specifying how much this element has to be transformed in Y absolutely
	 */
	public float getTranslateY() {
		return translateY;
	}

	public void setRenderer(ALayoutRenderer renderer) {
		this.renderer = renderer;
		if (this.renderer != null) {
			this.renderer.setElementLayout(this);
		}
	}

	public void addBackgroundRenderer(ALayoutRenderer renderer) {
		backgroundRenderers.add(renderer);
		renderer.setElementLayout(this);
	}

	public void removeBackgroundRenderer(ALayoutRenderer renderer) {
		backgroundRenderers.remove(renderer);
	}

	public List<ALayoutRenderer> getBackgroundRenderer() {
		return Collections.unmodifiableList(backgroundRenderers);
	}

	public void addForeGroundRenderer(ALayoutRenderer renderer) {
		foregroundRenderers.add(renderer);
		renderer.setElementLayout(this);
	}

	public List<ALayoutRenderer> getForegroundRenderer() {
		return Collections.unmodifiableList(foregroundRenderers);
	}

	public void clearBackgroundRenderers() {
		backgroundRenderers.clear();
	}

	public void clearBackgroundRenderers(Class<? extends ALayoutRenderer> ofType, GL2 gl) {
		for (Iterator<ALayoutRenderer> it = backgroundRenderers.iterator(); it.hasNext();) {
			ALayoutRenderer next = it.next();
			if (ofType.isInstance(next)) {
				if (gl != null)
					next.destroy(gl);
				it.remove();
			}
		}
	}

	public void clearForegroundRenderers() {
		foregroundRenderers.clear();
	}

	public void clearForegroundRenderers(Class<? extends ALayoutRenderer> ofType, GL2 gl) {
		for (Iterator<ALayoutRenderer> it = foregroundRenderers.iterator(); it.hasNext();) {
			ALayoutRenderer next = it.next();
			if (ofType.isInstance(next)) {
				if (gl != null)
					next.destroy(gl);
				it.remove();
			}
		}
	}

	/**
	 * Resets the sizes provided to the defaults
	 */
	public void reset() {
		resetX();
		resetY();
	}

	public void setZoomer(Zoomer zoomer) {
		if (this.zoomer != null)
			this.zoomer.destroy();
		this.zoomer = zoomer;

	}

	/**
	 * Sets the rendering priority and therefore determines the point in time this element layout is rendered if its
	 * {@link ALayoutContainer} uses priority rendering.
	 *
	 * @param renderingPriority
	 *            sets the priority of the rendering, where higher values have a higher priority
	 */
	public void setRenderingPriority(int renderingPriority) {
		this.renderingPriority = renderingPriority;
	}

	/**
	 * @param isHidden
	 *            setter, see {@link #isHidden}
	 */
	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	/**
	 * @return the isHidden, see {@link #isHidden}
	 */
	public boolean isHidden() {
		return isHidden;
	}

	/**
	 * @return Width of the ElementLayout in Pixels. Note, that the returned value is only valid, if the pixel size has
	 *         been set explicitly.
	 */
	public int getPixelSizeX() {
		return pixelSizeX;
	}

	/**
	 * @return Height of the ElementLayout in Pixels. Note, that the returned value is only valid, if the pixel size has
	 *         been set explicitly.
	 */
	public int getPixelSizeY() {
		return pixelSizeY;
	}

	/**
	 * @return GL-Width of the ElementLayout. Note, that the returned value is only valid, if the absolute size has been
	 *         set explicitly.
	 */
	public float getAbsoluteSizeX() {
		return absoluteSizeX;
	}

	/**
	 * @return GL-Height of the ElementLayout. Note, that the returned value is only valid, if the absolute size has
	 *         been set explicitly.
	 */
	public float getAbsoluteSizeY() {
		return absoluteSizeY;
	}

	@Override
	public String toString() {
		if (!layoutName.isEmpty())
			return layoutName;
		return super.toString();
	}

	// ---------------------------- END OF PUBLIC INTERFACE
	// -----------------------------------

	private void resetX() {
		absoluteSizeX = Float.NaN;
		ratioSizeX = 1;
		pixelSizeX = Integer.MIN_VALUE;
		dynamicSizeUnitsX = Integer.MIN_VALUE;
	}

	private void resetY() {
		absoluteSizeY = Float.NaN;
		ratioSizeY = 1;
		pixelSizeY = Integer.MIN_VALUE;
		dynamicSizeUnitsY = Integer.MIN_VALUE;
	}

	void render(GL2 gl) {
		if (isTransitionActive && transitionSpecification != null) {
			isTransitionActive = transitionSpecification.nextStep();
		}

		if (isHidden)
			return;
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
			if (this instanceof ALayoutContainer) {

				gl.glLineWidth(6);
				yPositionDebugText = getSizeScaledY() / 2;
			} else {
				gl.glLineWidth(2);
			}

			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(0, 0, 0.2f);
			gl.glVertex3f(getSizeScaledX(), 0, 0.2f);
			gl.glVertex3f(getSizeScaledX(), getSizeScaledY(), 0.2f);
			gl.glVertex3f(0, getSizeScaledY(), 0.2f);
			gl.glEnd();

			// FIXME: dirty. assign text renderer once via setter
			if (textRenderer == null) {
				textRenderer = new CaleydoTextRenderer(24);
			}

			textRenderer.setColor(color[0], color[1], color[2], color[3]);
			textRenderer.renderText(gl, layoutName, 0, yPositionDebugText, 0.4f, 0.005f, 3);

		}

		if (zoomer != null)
			zoomer.beginZoom(gl);

		for (ALayoutRenderer backgroundRenderer : backgroundRenderers) {
			backgroundRenderer.render(gl);
		}
		if (renderer != null)
			renderer.render(gl);
		for (ALayoutRenderer foregroundRenderer : foregroundRenderers) {
			foregroundRenderer.render(gl);
		}
		if (zoomer != null)
			zoomer.endZoom(gl);

		gl.glTranslatef(-getTranslateX(), -getTranslateY(), 0);
	}

	void calculateScales(float totalWidth, float totalHeight, Integer numberOfDynamicSizeUnitsX,
			Integer numberOfDynamicSizeUnitsY) {
		if (isHidden)
			return;
		this.totalWidth = totalWidth;
		this.totalHeight = totalHeight;

		if (pixelSizeX != Integer.MIN_VALUE)
			sizeScaledX = layoutManager.getPixelGLConverter().getGLWidthForPixelWidth(pixelSizeX);
		else if (!Float.isNaN(absoluteSizeX))
			sizeScaledX = absoluteSizeX;
		else if (numberOfDynamicSizeUnitsX > 0 && dynamicSizeUnitsX >= 0)
			sizeScaledX = (float) dynamicSizeUnitsX / numberOfDynamicSizeUnitsX * totalWidth;
		else
			sizeScaledX = ratioSizeX * totalWidth;

		if (pixelSizeY != Integer.MIN_VALUE)
			sizeScaledY = layoutManager.getPixelGLConverter().getGLHeightForPixelHeight(pixelSizeY);
		else if (!Float.isNaN(absoluteSizeY))
			sizeScaledY = absoluteSizeY;
		else if (numberOfDynamicSizeUnitsY > 0 && dynamicSizeUnitsY >= 0)
			sizeScaledY = (float) dynamicSizeUnitsY / numberOfDynamicSizeUnitsY * totalHeight;
		else
			sizeScaledY = ratioSizeY * totalHeight;
	}

	void setTranslateX(float translateX) {
		this.translateX = translateX;
	}

	void setTranslateY(float translateY) {
		this.translateY = translateY;
	}

	/**
	 * Sets the display lists of all renderers of this layout dirty.
	 */
	void setRenderingDirty() {
		if (isHidden)
			return;

		if (foregroundRenderers != null) {
			for (ALayoutRenderer renderer : foregroundRenderers) {
				renderer.setDisplayListDirty(true);
			}
		}
		if (backgroundRenderers != null) {
			for (ALayoutRenderer renderer : backgroundRenderers) {
				renderer.setDisplayListDirty(true);
			}
		}
		if (renderer != null) {
			renderer.setDisplayListDirty(true);
		}

	}

	protected void updateSpacings() {
		if (isHidden)
			return;
		// ALayoutRenderer renderer = ((RenderableLayoutElement)
		// layout).getRenderer();

		if (foregroundRenderers != null) {
			for (ALayoutRenderer renderer : foregroundRenderers) {
				renderer.setElementLayout(this);
				renderer.setLimits(getSizeScaledX(), getSizeScaledY());
			}
		}
		if (backgroundRenderers != null) {
			for (ALayoutRenderer renderer : backgroundRenderers) {
				renderer.setElementLayout(this);
				renderer.setLimits(getSizeScaledX(), getSizeScaledY());
			}
		}
		if (renderer != null) {
			renderer.setElementLayout(this);
			renderer.setLimits(getSizeScaledX(), getSizeScaledY());
		}
		if (zoomer != null)
			zoomer.setLimits(getSizeScaledX(), getSizeScaledY());

		if (renderer != null)
			renderer.updateSpacing();
	}

	/**
	 * Get the unscalable height part of this layout
	 *
	 * @return
	 */
	float getUnscalableElementHeight() {
		if (isHidden)
			return 0;
		if (grabY)
			return 0;
		else if (pixelSizeY != Integer.MIN_VALUE)
			return layoutManager.getPixelGLConverter().getGLHeightForPixelHeight(pixelSizeY);
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
		if (isHidden)
			return 0;
		if (grabX)
			return 0;
		else if (pixelSizeX != Integer.MIN_VALUE)
			return layoutManager.getPixelGLConverter().getGLWidthForPixelWidth(pixelSizeX);
		else if (!Float.isNaN(absoluteSizeX))
			return absoluteSizeX;
		else
			return 0;
	}

	/**
	 * @return the dynamicSizeUnitsX, see {@link #dynamicSizeUnitsX}
	 */
	int getDynamicSizeUnitsX() {
		if (dynamicSizeUnitsX > 0)
			return dynamicSizeUnitsX;
		else
			return 0;
	}

	/**
	 * @return the dynamicSizeUnitsY, see {@link #dynamicSizeUnitsY}
	 */
	int getDynamicSizeUnitsY() {
		if (dynamicSizeUnitsY > 0)
			return dynamicSizeUnitsY;
		else
			return 0;
	}

	protected void recordPreTransitionState() {
		transitionSpecification = new TransitionSpecification(this, translateX, translateY, sizeScaledX, sizeScaledY);
		layoutManager.registerTransitionSpecification(transitionSpecification);

	}

	protected void recordPostTranslationState() {
		if (transitionSpecification == null) {
			// we are a new element!
			transitionSpecification = new TransitionSpecification(this, translateX, translateY, sizeScaledX,
					sizeScaledY);
			transitionSpecification.setToAppear(appearanceDirection);
		} else {
			// here we remove ourselves from the transition manager so that only those that were deleted remain
			layoutManager.unRegisterTransitionSpecification(transitionSpecification);
		}
		transitionSpecification.setPostTransitionValues(translateX, translateY, sizeScaledX, sizeScaledY);
		isTransitionActive = true;

	}

}
