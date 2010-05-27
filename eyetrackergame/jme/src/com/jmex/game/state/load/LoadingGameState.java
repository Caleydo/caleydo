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
package com.jmex.game.state.load;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jmex.font2d.Font2D;
import com.jmex.font2d.Text2D;
import com.jmex.game.state.GameState;
import com.jmex.scene.TimedLifeController;

/**
 * @author Matthew D. Hicks
 */
public class LoadingGameState extends GameState implements Loader {
	protected Node rootNode;
	private Text2D statusText;
	private Quad progressBar;
	private Text2D percentageText;
	protected ColorRGBA color;
	protected BlendState alphaState;

	private int steps;
	private int current;
	private boolean removeOnComplete;

	public LoadingGameState() {
		this(100, true);
	}
	
	public LoadingGameState(int steps) {
		this(steps, true);
	}

	public LoadingGameState(int steps, boolean removeOnComplete) {
		this.steps = steps;
		current = 0;
		this.removeOnComplete = removeOnComplete;
		init();
	}

	protected void init() {
		color = new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f);

		alphaState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
		alphaState.setBlendEnabled(true);
		alphaState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		alphaState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
		alphaState.setTestEnabled(true);
		alphaState.setTestFunction(BlendState.TestFunction.GreaterThan);
		alphaState.setEnabled(true);

		rootNode = new Node();
		rootNode.setCullHint(Spatial.CullHint.Never);
		rootNode.setLightCombineMode(Spatial.LightCombineMode.Off);

		Font2D font = new Font2D();
		ZBufferState zbs = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
		zbs.setFunction(ZBufferState.TestFunction.Always);

		statusText = font.createText("Loading...", 10.0f, 0);
		statusText.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		statusText.setRenderState(zbs);
		statusText.setTextColor(color);
		statusText.updateRenderState();
		rootNode.attachChild(statusText);

		progressBar = new Quad("ProgressBar", 100.0f, 15.0f);
		progressBar.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		progressBar.setColorBuffer(null);
		progressBar.setDefaultColor(color);
		progressBar.setRenderState(alphaState);
		progressBar.updateRenderState();
		rootNode.attachChild(progressBar);

		percentageText = font.createText("", 10.0f, 0);
		percentageText.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		percentageText.setRenderState(zbs);
		percentageText.setTextColor(color);
		percentageText.updateRenderState();
		rootNode.attachChild(percentageText);
	}

	public void update(float tpf) {
		rootNode.updateGeometricState(tpf, true);
	}

	public void render(float tpf) {
		DisplaySystem.getDisplaySystem().getRenderer().draw(rootNode);
	}

	public void cleanup() {
	}

	public void setProgress(float progress) {
		int percentage = (int) (progress * 100);
		if (percentageText != null) {
			statusText.updateRenderState();
			statusText.updateModelBound();
			statusText.setLocalTranslation(new Vector3f((DisplaySystem.getDisplaySystem().getWidth() / 2)
							- (statusText.getWidth() / 2), (DisplaySystem.getDisplaySystem().getHeight() / 2)
							- (statusText.getHeight() / 2) + 20.0f, 0.0f));

			progressBar.setLocalScale(new Vector3f(progress, 1.0f, 1.0f));
			float xPosition = (DisplaySystem.getDisplaySystem().getWidth() / 2.0f) - 50.0f + (percentage / 2.0f);
			progressBar.setLocalTranslation(new Vector3f(xPosition, DisplaySystem.getDisplaySystem().getHeight() / 2,
							0.0f));

			percentageText.setText(percentage + "%");
			percentageText.updateRenderState();
			percentageText.updateModelBound();
			percentageText.setLocalTranslation(new Vector3f((DisplaySystem.getDisplaySystem().getWidth() / 2)
							- (percentageText.getWidth() / 2), (DisplaySystem.getDisplaySystem().getHeight() / 2)
							- (percentageText.getHeight() / 2) - 20.0f, 0.0f));
		}
		if (percentage == 100) {
			LoaderFadeOut fader = new LoaderFadeOut(2.0f, this, removeOnComplete);
			rootNode.addController(fader);
			fader.setActive(true);
		}
	}

	public void setProgress(float progress, String activity) {
		if (statusText != null) {
			statusText.setText(activity);
			setProgress(progress);
		}
	}

	protected void setAlpha(float alpha) {
		color.a = alpha;
	}

	public float increment() {
		return increment(1);
	}
	

	public float increment(int steps) {
		current += steps;
		float percent = (float)current / (float)this.steps;
		setProgress(percent);
		return percent;
	}
	

	public float increment(String activity) {
		float percent = increment();
		setProgress(percent, activity);
		return percent;
	}
	

	public float increment(int steps, String activity) {
		float percent = increment(steps);
		setProgress(percent, activity);
		return percent;
	}
}

class LoaderFadeOut extends TimedLifeController {
	private static final long serialVersionUID = 1L;

	private LoadingGameState loading;
	private boolean removeOnComplete;
	
	public LoaderFadeOut(float lifeInSeconds, LoadingGameState loading) {
		this(lifeInSeconds, loading, true);
	}

	public LoaderFadeOut(float lifeInSeconds, LoadingGameState loading, boolean removeOnComplete) {
		super(lifeInSeconds);
		this.removeOnComplete = removeOnComplete;
		this.loading = loading;
	}

	public void updatePercentage(float percentComplete) {
		loading.setAlpha(1.0f - percentComplete);
		if (percentComplete == 1.0f) {
			loading.setActive(false);
			if(removeOnComplete) {
				loading.getParent().detachChild(loading);
			}
		}
	}
}
