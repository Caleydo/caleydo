package org.caleydo.core.view.opengl.layout2;

import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.util.spline.TesselationRenderer;
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
	private final ITextRenderer text;

	private final TextureManager textures;

	private final IResourceLocator loader;

	private TesselationRenderer tesselationRenderer = null; // lazy

	private GLU glu = null; // lazy

	private final DisplayListPool pool = new DisplayListPool();

	private final TimeDelta timeDelta = new TimeDelta();

	public GLContextLocal(ITextRenderer text, TextureManager textures, IResourceLocator loader) {
		this.text = text;
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
		return text;
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