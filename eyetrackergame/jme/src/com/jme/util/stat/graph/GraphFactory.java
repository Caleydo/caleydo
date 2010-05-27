/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.util.stat.graph;

import java.nio.FloatBuffer;

import com.jme.image.Texture2D;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;
import com.jme.system.DisplaySystem;
import com.jme.util.stat.StatCollector;

/**
 * Factory class useful for setting up various types of graphs.
 * 
 * @author Joshua Slack
 */
public class GraphFactory {

	/**
	 * Makes a new line grapher and sets up a quad to display it.
	 * 
	 * @param width
	 *            the width in pixels of the graph
	 * @param height
	 *            the height in pixels of the graph
	 * @param quad
	 *            the quad on whose surface we'll display our graph.
	 * @return the new LineGrapher
	 */
	public static LineGrapher makeLineGraph(int width, int height, Quad quad) {
		LineGrapher grapher = new LineGrapher(width, height);
		grapher.setThreshold(1);
		StatCollector.addStatListener(grapher);
		Texture2D graphTex = setupGraphTexture(grapher);

		float dW = (float) width / grapher.texRenderer.getWidth();
		float dH = (float) height / grapher.texRenderer.getHeight();

		setupGraphQuad(quad, graphTex, dW, dH);

		return grapher;
	}

	/**
	 * Makes a new area grapher and sets up a quad to display it.
	 * 
	 * @param width
	 *            the width in pixels of the graph
	 * @param height
	 *            the height in pixels of the graph
	 * @param quad
	 *            the quad on whose surface we'll display our graph.
	 * @return the new TimedAreaGrapher
	 */
	public static TimedAreaGrapher makeTimedGraph(int width, int height,
			Quad quad) {
		TimedAreaGrapher grapher = new TimedAreaGrapher(width, height);
		grapher.setThreshold(1);
		StatCollector.addStatListener(grapher);
		Texture2D graphTex = setupGraphTexture(grapher);
		float dW = (float) width / grapher.texRenderer.getWidth();
		float dH = (float) height / grapher.texRenderer.getHeight();

		setupGraphQuad(quad, graphTex, dW, dH);

		return grapher;
	}

	/**
	 * Makes a new label grapher and sets up a quad to display it.
	 * 
	 * @param width
	 *            the width in pixels of the graph
	 * @param height
	 *            the height in pixels of the graph
	 * @param quad
	 *            the quad on whose surface we'll display our graph.
	 * @return the new TabledLabelGrapher
	 */
	public static TabledLabelGrapher makeTabledLabelGraph(int width,
			int height, Quad quad) {
		TabledLabelGrapher grapher = new TabledLabelGrapher(width, height);
		grapher.setThreshold(1);
		StatCollector.addStatListener(grapher);
		Texture2D graphTex = setupGraphTexture(grapher);
		float dW = (float) width / grapher.texRenderer.getWidth();
		float dH = (float) height / grapher.texRenderer.getHeight();

		setupGraphQuad(quad, graphTex, dW, dH);

		return grapher;
	}

	/**
	 * Creates and sets up a texture to be used as the texture for a given
	 * grapher. Also applies appropriate texture filter modes.
	 * (NearestNeighborNoMipMaps and Bilinear)
	 * 
	 * @param grapher
	 *            the grapher to associate the texture with
	 * @return the texture
	 */
	private static Texture2D setupGraphTexture(AbstractStatGrapher grapher) {
		Texture2D graphTex = new Texture2D();
		graphTex
				.setMinificationFilter(MinificationFilter.NearestNeighborNoMipMaps);
		graphTex.setMagnificationFilter(MagnificationFilter.Bilinear);
		grapher.setTexture(graphTex);
		return graphTex;
	}

	/**
	 * Sets up a Quad to be used as the display surface for a grapher. Puts it
	 * in the ortho mode, sets up UVs, and sets up a TextureState and an alpha
	 * transparency BlendState.
	 * 
	 * @param quad
	 *            the Quad to use
	 * @param graphTexture
	 *            the texture to use
	 * @param maxU
	 *            the maximum value along the U axis to use in the texture for
	 *            UVs
	 * @param maxV
	 *            the maximum value along the V axis to use in the texture for
	 *            UVs
	 */
	private static void setupGraphQuad(Quad quad, Texture2D graphTexture,
			float maxU, float maxV) {
		quad.setTextureCombineMode(Spatial.TextureCombineMode.Replace);
		quad.setLightCombineMode(Spatial.LightCombineMode.Off);
		quad.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		quad.setZOrder(-1);

		FloatBuffer tbuf = quad.getTextureCoords(0).coords;
		tbuf.clear();
		tbuf.put(0).put(maxV);
		tbuf.put(0).put(0);
		tbuf.put(maxU).put(0);
		tbuf.put(maxU).put(maxV);
		tbuf.rewind();

		TextureState texState = DisplaySystem.getDisplaySystem().getRenderer()
				.createTextureState();
		texState.setTexture(graphTexture);
		quad.setRenderState(texState);

		BlendState blend = DisplaySystem.getDisplaySystem().getRenderer()
				.createBlendState();
		blend.setBlendEnabled(true);
		blend.setSourceFunction(SourceFunction.SourceAlpha);
		blend.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
		quad.setRenderState(blend);
	}
}
