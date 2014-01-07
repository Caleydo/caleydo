/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui;

import gleem.linalg.Vec2f;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.util.PickingPool;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;
import org.caleydo.datadomain.pathway.IVertexRepSelectionListener;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.view.pathway.v2.internal.GLPathwayView;

import com.google.common.io.CharStreams;
import com.jogamp.opengl.util.glsl.ShaderUtil;

/**
 * @author Christian
 *
 */
public class PathwayTextureRepresentation extends APathwayElementRepresentation {

	protected PathwayGraph pathway;

	protected boolean isShaderInitialized = false;
	protected int shaderProgramTextOverlay;

	protected Vec2f renderSize = new Vec2f();
	protected Vec2f origin = new Vec2f();
	protected Vec2f scaling = new Vec2f();

	protected PickingPool pool;

	protected List<IVertexRepSelectionListener> vertexListeners = new ArrayList<>();

	protected float minWidth = -1;
	protected float minHeight = -1;

	// protected List<VertexRepBasedContextMenuItem> contextMenuItems = new ArrayList<>();

	public PathwayTextureRepresentation() {
	}

	public PathwayTextureRepresentation(PathwayGraph pathway) {
		this.pathway = pathway;
	}

	@Override
	protected void init(IGLElementContext context) {
		setVisibility(EVisibility.PICKABLE);

		IPickingListener pickingListener = PickingListenerComposite.concat(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onVertexPick(pick);
			}
		}, context.getSWTLayer().createTooltip(new IPickingLabelProvider() {
			@Override
			public String getLabel(Pick pick) {
				PathwayVertexRep vertexRep = PathwayItemManager.get().getPathwayVertexRep(pick.getObjectID());
				return vertexRep.getLabel();
			}
		}));

		pool = new PickingPool(context, pickingListener);

		super.init(context);
	}

	@Override
	protected void takeDown() {
		pool.clear();
		pool = null;
		super.takeDown();
	}

	private void onVertexPick(Pick pick) {
		PathwayVertexRep vertexRep = PathwayItemManager.get().getPathwayVertexRep(pick.getObjectID());
		for (IVertexRepSelectionListener listener : vertexListeners) {
			listener.onSelect(vertexRep, pick);
		}

		// if (pick.getPickingMode() == PickingMode.RIGHT_CLICKED) {
		// ContextMenuCreator creator = new ContextMenuCreator();
		// for (VertexRepBasedContextMenuItem item : contextMenuItems) {
		// item.setVertexRep(vertexRep);
		// creator.add(item);
		// }
		// context.getSWTLayer().showContextMenu(creator);
		// }
	}

	private void initShaders(GL2 gl) throws IOException {
		isShaderInitialized = true;
		shaderProgramTextOverlay = -1;
		if (!ShaderUtil.isShaderCompilerAvailable(gl)) {
			GLPathwayView.log.error("no shader available no intelligent texture manipulation");
			return;
		}
		int vs = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER);
		String vsrc = CharStreams.toString(new InputStreamReader(this.getClass().getResourceAsStream(
				"../../vsTextOverlay.glsl")));
		gl.glShaderSource(vs, 1, new String[] { vsrc }, (int[]) null, 0);
		gl.glCompileShader(vs);

		ByteArrayOutputStream slog = new ByteArrayOutputStream();
		PrintStream slog_print = new PrintStream(slog);

		if (!ShaderUtil.isShaderStatusValid(gl, vs, GL2ES2.GL_COMPILE_STATUS, slog_print)) {
			gl.glDeleteShader(vs);
			GLPathwayView.log.error("can't compile vertex shader: " + slog.toString());
			return;
		} else {
			GLPathwayView.log.debug("compiling vertex shader warnings: " + ShaderUtil.getShaderInfoLog(gl, vs));
		}

		String fsrc = CharStreams.toString(new InputStreamReader(this.getClass().getResourceAsStream(
				"../../fsTextOverlay.glsl")));
		int fs = gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fs, 1, new String[] { fsrc }, (int[]) null, 0);
		gl.glCompileShader(fs);
		if (!ShaderUtil.isShaderStatusValid(gl, vs, GL2ES2.GL_COMPILE_STATUS, slog_print)) {
			gl.glDeleteShader(vs);
			gl.glDeleteShader(fs);
			GLPathwayView.log.error("can't compile fragment shader: " + slog.toString());
			return;
		} else {
			GLPathwayView.log.debug("compiling fragment shader warnings: " + ShaderUtil.getShaderInfoLog(gl, fs));
		}

		shaderProgramTextOverlay = gl.glCreateProgram();
		gl.glAttachShader(shaderProgramTextOverlay, vs);
		gl.glAttachShader(shaderProgramTextOverlay, fs);
		gl.glLinkProgram(shaderProgramTextOverlay);
		gl.glValidateProgram(shaderProgramTextOverlay);
		if (!ShaderUtil.isProgramLinkStatusValid(gl, shaderProgramTextOverlay, slog_print)) {
			gl.glDeleteShader(vs);
			gl.glDeleteShader(fs);
			gl.glDeleteProgram(shaderProgramTextOverlay);
			shaderProgramTextOverlay = -1;
			GLPathwayView.log.error("can't link program: " + slog.toString());
			return;
		} else {
			GLPathwayView.log.debug("linking program warnings: "
					+ ShaderUtil.getProgramInfoLog(gl, shaderProgramTextOverlay));
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {

		if (pathway == null)
			return;

		if (!isShaderInitialized) {
			try {
				initShaders(g.gl);
			} catch (IOException e) {
				GLPathwayView.log.error("Error while reading shader file");
			}
		}

		calculateTransforms(w, h);

		if (pathway.getType() == EPathwayDatabaseType.WIKIPATHWAYS) {
			renderBackground(g);
		}

		GL2 gl = g.gl;
		if (shaderProgramTextOverlay > 0) {
			gl.glUseProgram(shaderProgramTextOverlay);
			// texture
			gl.glUniform1i(gl.glGetUniformLocation(shaderProgramTextOverlay, "pathwayTex"), 0);
			// which type
			gl.glUniform1i(gl.glGetUniformLocation(shaderProgramTextOverlay, "mode"), this.pathway.getType().ordinal());
		}

		g.fillImage(pathway.getImage().getPath(), origin.x(), origin.y(), renderSize.x(), renderSize.y());

		if (shaderProgramTextOverlay > 0)
			gl.glUseProgram(0);

		// g.color(0f, 0f, 0f, 0f);
		// for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
		// g.fillRect(getVertexRepBounds(vertexRep));
		// }

	}

	private void renderBackground(GLGraphics g) {
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
			g.fillRect(getVertexRepBounds(vertex));
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

		g.color(1, 1, 1, 1).fillRect(origin.x(), origin.y(), renderSize.x(), renderSize.y());
		g.color(0, 0, 0, 1).drawRect(origin.x(), origin.y(), renderSize.x(), renderSize.y());

		gl.glPopAttrib();
		gl.glPopMatrix();

		gl.glDisable(GL.GL_STENCIL_TEST);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
			g.pushName(pool.get(vertexRep.getID()));
			g.fillRect(getVertexRepBounds(vertexRep));
			g.popName();
		}

	}

	private void calculateTransforms(float w, float h) {

		float pathwayWidth = pathway.getWidth();
		float pathwayHeight = pathway.getHeight();

		float pathwayAspectRatio = pathwayWidth / pathwayHeight;
		float viewFrustumAspectRatio = w / h;

		if (pathwayWidth <= w && pathwayHeight <= h) {
			scaling.set(1f, 1f);
			renderSize.setX(pathwayWidth);
			renderSize.setY(pathwayHeight);
		} else {
			if (viewFrustumAspectRatio > pathwayAspectRatio) {
				renderSize.setX((h / pathwayHeight) * pathwayWidth);
				renderSize.setY(h);
			} else {
				renderSize.setX(w);
				renderSize.setY((w / pathwayWidth) * pathwayHeight);
			}
			scaling.set(renderSize.x() / pathwayWidth, renderSize.y() / pathwayHeight);
		}
		origin.set((w - renderSize.x()) / 2.0f, (h - renderSize.y()) / 2.0f);
		// Center pathway in x direction
		// if (pathwayWidth < w) {
		// vecTranslation.setX((w - pathwayWidth) / 2.0f);
		// }
		//
		// // Center pathway in y direction
		// if (pathwayHeight < h) {
		// vecTranslation.setY((h - pathwayHeight) / 2.0f);
		// }
	}

	@Override
	public PathwayGraph getPathway() {
		return pathway;
	}

	@Override
	public List<PathwayGraph> getPathways() {
		if (pathway == null)
			return new ArrayList<>();
		return Arrays.asList(pathway);
	}

	@Override
	public Rect getVertexRepBounds(PathwayVertexRep vertexRep) {
		if (pathway == null || !pathway.containsVertex(vertexRep))
			return null;
		int coordsX = vertexRep.getCoords().get(0).getFirst();
		int coordsY = vertexRep.getCoords().get(0).getSecond();

		float x = origin.x() + (scaling.x() * coordsX);
		float y = origin.y() + (scaling.y() * coordsY);

		float width = scaling.x() * vertexRep.getWidth();
		float height = scaling.y() * vertexRep.getHeight();

		return new Rect(x, y, width, height);
	}

	@Override
	public List<Rect> getVertexRepsBounds(PathwayVertexRep vertexRep) {
		Rect bounds = getVertexRepBounds(vertexRep);
		if (bounds == null)
			return new ArrayList<>();
		return Arrays.asList(bounds);
	}

	// @Override
	// public void addVertexRepBasedContextMenuItem(VertexRepBasedContextMenuItem item) {
	// if (item != null)
	// contextMenuItems.add(item);
	//
	// }

	@Override
	public void addVertexRepSelectionListener(IVertexRepSelectionListener listener) {
		vertexListeners.add(listener);
	}

	@Override
	public Rect getPathwayBounds() {
		if (pathway == null)
			return null;
		return new Rect(origin.x(), origin.y(), renderSize.x(), renderSize.y());
	}

	@Override
	public float getMinWidth() {
		if (minWidth > 0)
			return minWidth;
		if (pathway == null)
			return 120;
		return pathway.getWidth() * 0.8f;
	}

	@Override
	public float getMinHeight() {
		if (minHeight > 0)
			return minHeight;
		if (pathway == null)
			return 120;
		return pathway.getHeight() * 0.8f;
	}

}
