/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.manager;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;

import org.apache.commons.lang.BooleanUtils;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.ExtensionUtils.IExtensionLoader;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.GLContextLocal;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.util.GLGraphicsUtils;
import org.caleydo.datadomain.pathway.IPathwayLoader;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Class holds all supported pathway database types.
 *
 * @author Marc Streit
 */
public class EPathwayDatabaseType implements ILabeled, Comparable<EPathwayDatabaseType> {
	private static final Logger log = Logger.create(EPathwayDatabaseType.class);

	private final static Collection<EPathwayDatabaseType> types = ExtensionUtils.loadExtensions(
			"org.caleydo.data.pathway.Database", new IExtensionLoader<EPathwayDatabaseType>() {
				@Override
				public EPathwayDatabaseType load(IConfigurationElement elem) throws CoreException {
					return new EPathwayDatabaseType(elem);
				}

			});
	/**
	 * @return
	 */
	public static Collection<EPathwayDatabaseType> values() {
		return types;
	}
	/**
	 * @param string
	 * @return
	 */
	public static EPathwayDatabaseType valueOf(String name) {
		for(EPathwayDatabaseType type : types) {
			if (type.name.equals(name))
				return type;
		}
		return null;
	}

	private final String name;
	private final IConfigurationElement elem;

	/**
	 * one shared shader per type per context
	 */
	private final GLContextLocal<Integer> shaderProgramTextOverlay = new GLContextLocal<>(new SafeCallable<Integer>() {
				@Override
		public Integer call() {
			GL2 gl = GLContext.getCurrentGL().getGL2();
			URL vertex = getVertexShader();
			URL fragment = getFragmentShader();
			if (vertex == null || fragment == null) {
				return -1;
			}
			int program = -1;
			try {
				program = GLGraphicsUtils.loadShader(gl, vertex.openStream(), fragment.openStream());
			} catch (IOException e) {
				log.error("can't load shader for " + getName(), e);
			}
			return program;
		}

	});

	/**
	 * Constructor.
	 *
	 * @param name
	 * @param url
	 */
	private EPathwayDatabaseType(IConfigurationElement elem) {
		this.name = elem.getAttribute("name");
		this.elem = elem;
	}

	@Override
	public int compareTo(EPathwayDatabaseType o) {
		return Objects.compare(name, o.name, String.CASE_INSENSITIVE_ORDER);
	}

	public String getName() {
		return name;
	}

	@Override
	public String getLabel() {
		return name;
	}

	public String getURL() {
		return elem.getAttribute("url");
	}
	/**
	 * @return
	 */
	public URL getIcon() {
		return ExtensionUtils.getResource(elem, "icon");
	}

	URL getFragmentShader() {
		return ExtensionUtils.getResource(getRenderer(), "fragmentShader");
	}

	URL getVertexShader() {
		return ExtensionUtils.getResource(getRenderer(), "vertexShader");
	}
	/**
	 * @return
	 */
	public boolean doRenderBackground() {
		return BooleanUtils.toBoolean(getRenderer().getAttribute("renderBackground"));
	}
	private IConfigurationElement getRenderer() {
		return elem.getChildren("renderer")[0];
	}

	/**
	 *
	 */
	public void load() {
		Object l;
		try {
			l = elem.createExecutableExtension("loader");
			if (l instanceof IPathwayLoader) {
				((IPathwayLoader) l).parse(this);
			}
		} catch (CoreException e) {
			log.error("can't create pathway loader " + name, e);
		}
	}

	/**
	 * @param g
	 * @param pathway
	 * @param rect
	 */
	public void render(GLGraphics g, PathwayGraph pathway) {
		assert pathway.getType() == this;
		if (doRenderBackground()) {
			renderBackground(pathway, g);
		}

		GL2 gl = g.gl;
		Integer program = shaderProgramTextOverlay.get();
		if (program != null && program > 0) {
			gl.glUseProgram(program);
			// texture
			gl.glUniform1i(gl.glGetUniformLocation(program, "pathwayTex"), 0);
		}

		g.fillImage(pathway.getImage().getPath(), 0, 0, pathway.getWidth(), pathway.getHeight());

		if (program != null && program > 0)
			gl.glUseProgram(0);

	}

	private void renderBackground(PathwayGraph pathway, GLGraphics g) {
		GL2 gl = g.gl;

		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glColorMask(false, false, false, false);
		gl.glDepthMask(false);
		gl.glStencilFunc(GL.GL_NEVER, 1, 0xFF);
		gl.glStencilOp(GL.GL_REPLACE, GL.GL_KEEP, GL.GL_KEEP); // draw 1s on test fail (always)

		// draw stencil pattern
		gl.glStencilMask(0xFF);
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT); // needs mask=0xFF

		for (PathwayVertexRep vertex : pathway.vertexSet()) {
			int x = vertex.getCoords().get(0).getFirst();
			int y = vertex.getCoords().get(0).getSecond();

			float w = vertex.getWidth();
			float h = vertex.getHeight();
			g.fillRect(x, y, w, h);
		}

		gl.glColorMask(true, true, true, true);
		gl.glDepthMask(true);
		gl.glStencilMask(0x00);
		// draw where stencil's value is 0
		gl.glStencilFunc(GL.GL_EQUAL, 0, 0xFF);

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl.glPushMatrix();
		gl.glPushAttrib(GL2.GL_LINE_BIT);

		g.color(1, 1, 1, 1).fillRect(0, 0, pathway.getWidth(), pathway.getHeight());
		g.color(0, 0, 0, 1).drawRect(0, 0, pathway.getWidth(), pathway.getHeight());

		gl.glPopAttrib();
		gl.glPopMatrix();

		gl.glDisable(GL.GL_STENCIL_TEST);
	}

	/**
	 * @return
	 */
	public int getShader() {
		Integer p = shaderProgramTextOverlay.get();
		return p == null ? -1 : p.intValue();
	}
}
