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

package com.jmex.game.state;

import com.jme.scene.Node;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;

/**
 * @author Irrisor
 */
public abstract class CameraGameStateDefaultCamera extends BasicGameState {
    public CameraGameStateDefaultCamera( String name ) {
        super( name );

        initZBuffer();

        // Update geometric and rendering information for the rootNode.
        rootNode.updateGeometricState(0.0f, true);
        rootNode.updateRenderState();
    }

    public Node getRootNode()
    {
        return rootNode;
    }

    /**
	 * Creates a ZBuffer to display pixels closer to the camera above
	 * farther ones.
	 */
	protected void initZBuffer() {
		ZBufferState buf = DisplaySystem.getDisplaySystem().
			getRenderer().createZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		rootNode.setRenderState(buf);
	}

    /**
	 * Overwritten to appropriately call switchTo() or switchFrom().
	 *
	 * @see GameState#setActive(boolean)
	 */
	public void setActive(boolean active) {
		if (active) onActivate();
		else onDeactivate();
		super.setActive(active);
	}

    /**
	 * Calls stateUpdate(float), then updates the geometric state of the
	 * rootNode.
	 *
	 * @param tpf The elapsed time since last frame.
	 * @see GameState#update(float)
	 * @see CameraGameState#stateUpdate(float)
	 */
	public final void update(float tpf) {
		stateUpdate(tpf);
		super.update(tpf);
	}

    /**
	 * Calls stateRender(float), then renders the rootNode.
	 *
	 * @param tpf The elapsed time since last frame.
	 * @see GameState#render(float)
	 * @see CameraGameState#stateRender(float)
	 */
	public final void render(float tpf) {
		stateRender(tpf);
		super.render(tpf);
	}

    /**
	 * This is where derived classes are supposed to put their game logic.
	 * Gets called between the input.update and
	 * rootNode.updateGeometricState calls.
	 *
	 * <p>
	 * Much like the structure of <code>SimpleGame</code>.
	 * </p>
	 *
	 * @param tpf The time since the last frame.
	 */
	protected void stateUpdate(float tpf) {
	}

    /**
	 * This is where derived classes are supposed to put their render logic.
	 * Gets called before the rootNode gets rendered.
	 *
	 * <p>
	 * Much like the structure of <code>SimpleGame</code>.
	 * </p>
	 *
	 * @param tpf The time since the last frame.
	 */
	protected void stateRender(float tpf) {
	}

    /**
	 * Points the renderers camera to the one contained by this state. Derived
	 * classes can put special actions they want to perform when activated here.
	 */
	protected abstract void onActivate();

    /**
	 * Derived classes can put special actions they want to perform when
	 * deactivated here.
	 */
	protected void onDeactivate() {
	}
}
