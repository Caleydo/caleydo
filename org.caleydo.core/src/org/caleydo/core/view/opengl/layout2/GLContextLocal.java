/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.util.spline.TesselationRenderer;
import org.caleydo.core.view.opengl.util.text.CompositeTextRenderer;
import org.caleydo.core.view.opengl.util.text.ETextStyle;
import org.caleydo.core.view.opengl.util.text.ITextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;

/**
 * container for objects that are {@link GLContext} specific but just once per context
 *
 * @author Samuel Gratzl
 *
 */
public class GLContextLocal {
	private final ITextRenderer text_plain;
	private final ITextRenderer text_bold;
	private final ITextRenderer text_italic;

	private final TextureManager textures;

	private final IResourceLocator loader;

	private TesselationRenderer tesselationRenderer = null; // lazy

	private GLU glu = null; // lazy

	private final DisplayListPool pool = new DisplayListPool();

	private final TimeDelta timeDelta = new TimeDelta();

	public GLContextLocal(TextureManager textures, IResourceLocator loader, IGLCanvas canvas) {
		this(createText(ETextStyle.PLAIN, canvas), createText(ETextStyle.BOLD, canvas), createText(ETextStyle.ITALIC,
				canvas), textures, loader);
	}

	private static ITextRenderer createText(ETextStyle style, IGLCanvas canvas) {
		return new CompositeTextRenderer(canvas.toRawPixelFunction(), style, 8, 16, 24, 40);
	}

	public GLContextLocal(ITextRenderer text, TextureManager textures, IResourceLocator loader) {
		this(text, text, text, textures, loader);
	}

	public GLContextLocal(ITextRenderer text_plain, ITextRenderer text_bold, ITextRenderer text_italic,
			TextureManager textures, IResourceLocator loader) {
		this.text_plain = text_plain;
		this.text_bold = text_bold;
		this.text_italic = text_italic;
		this.textures = textures;
		this.loader = loader;
	}

	/**
	 * @return the tesselationRenderer, see {@link #tesselationRenderer}
	 */
	public TesselationRenderer getTesselationRenderer() {
		if (this.tesselationRenderer == null)
			this.tesselationRenderer = new TesselationRenderer();
		return tesselationRenderer;
	}

	/**
	 * @return the text, see {@link #text}
	 */
	public ITextRenderer getText() {
		return text_plain;
	}

	/**
	 * @return the text_bold, see {@link #text_bold}
	 */
	public ITextRenderer getText_bold() {
		return text_bold;
	}

	/**
	 * @return the text_italic, see {@link #text_italic}
	 */
	public ITextRenderer getText_italic() {
		return text_italic;
	}

	/**
	 * @return the loader, see {@link #loader}
	 */
	public IResourceLocator getLoader() {
		return loader;
	}

	/**
	 * @return the textures, see {@link #textures}
	 */
	public TextureManager getTextures() {
		return textures;
	}

	/**
	 * @return the pool, see {@link #pool}
	 */
	public DisplayListPool getPool() {
		return pool;
	}

	/**
	 * @return the timeDelta, see {@link #timeDelta}
	 */
	public TimeDelta getTimeDelta() {
		return timeDelta;
	}

	public int getDeltaTimeMs() {
		return timeDelta.getDeltaTimeMs();
	}

	/**
	 * @return the glu, see {@link #glu}
	 */
	public GLU getGlu() {
		if (this.glu == null)
			this.glu = new GLU();
		return glu;
	}

	/**
	 *
	 */
	void destroy(GL2 gl) {
		timeDelta.stop();
		pool.deleteAll(gl);

		if (this.glu != null) {
			this.glu.destroy();
			this.glu = null;
		}
		if (this.tesselationRenderer != null) {
			this.tesselationRenderer.destroy();
			this.tesselationRenderer = null;
		}
	}
}
