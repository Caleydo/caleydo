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

package com.jme.scene;

import java.io.IOException;
import java.io.Serializable;

import com.jme.renderer.RenderContext;
import com.jme.renderer.Renderer;
import com.jme.scene.state.RenderState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/** PassNodeState Creator: rikard.herlitz, 2007-maj-10 */
public class PassNodeState implements Savable, Serializable {

	private static final long serialVersionUID = -2190443394368566111L;

	/** if false, pass will not be updated or rendered. */
    protected boolean enabled = true;

    /**
     * offset params to use to differentiate multiple passes of the same scene
     * in the zbuffer.
     */
    protected float zFactor;
    protected float zOffset;

    /**
     * RenderStates registered with this pass - if a given state is not null it
     * overrides the corresponding state set during rendering.
     */
    protected RenderState[] passStates = new RenderState[RenderState.StateType.values().length];

    /**
     * a place to internally save previous states setup before rendering this
     * pass
     */
    protected RenderState[] savedStates = new RenderState[RenderState.StateType.values().length];

    /**
     * Applies all currently set renderstates and z offset parameters to the
     * supplied context
     *
     * @param r
     * @param context
     */
    public void applyPassNodeState(Renderer r, RenderContext<?> context) {
        applyPassStates(context);
        r.setPolygonOffset(zFactor, zOffset);
    }

    /**
     * Resets currently set renderstates and z offset parameters on the supplied
     * context
     *
     * @param r
     * @param context
     */
    public void resetPassNodeStates(Renderer r, RenderContext<?> context) {
        r.clearPolygonOffset();
        resetOldStates(context);
    }

    /**
     * Enforce a particular state. In other words, the given state will override
     * any state of the same type set on a scene object. Remember to clear the
     * state when done enforcing. Very useful for multipass techniques where
     * multiple sets of states need to be applied to a scenegraph drawn multiple
     * times.
     *
     * @param state state to enforce
     */
    public void setPassState(RenderState state) {
    	
        passStates[state.getStateType().ordinal()] = state;
    }

	/**
	 * @param renderStateType
	 *            the type to query
	 * @return the state enforced for a give state type, or null if none.
	 * @deprecated As of 2.0, use {@link #getPassState(com.jme.scene.state.RenderState.StateType)} instead.
	 */
    public RenderState getPassState(int renderStateType) {
    	return passStates[renderStateType];
    }

	/**
	 * Returns the {@link RenderState} of the given type.
	 * 
	 * @param type
	 *            the type to query
	 * @return the {@link RenderState} enforced for a given state type, or null if none.
	 */
    public RenderState getPassState(RenderState.StateType type) {
    	
    	return passStates[type.ordinal()];
    }

    /**
     * Clears an enforced render state index by setting it to null. This allows
     * object specific states to be used.
     *
     * @param renderStateType The type of RenderState to clear enforcement on.
	 * @deprecated As of 2.0, use {@link #clearPassState(com.jme.scene.state.RenderState.StateType)} instead.
     */
    public void clearPassState(int renderStateType) {
    	
        passStates[renderStateType] = null;
    }

    /**
     * Clears an enforced render state by setting it to null. This allows
     * object specific states to be used.
     *
     * @param type The type of {@link RenderState} to clear enforcement on.
     */
    public void clearPassState(RenderState.StateType type) {
    	
        passStates[type.ordinal()] = null;
    }

    /**
     * sets all enforced states to null.
     *
     * @see RenderContext#clearEnforcedState(int)
     */
    public void clearPassStates() {
        for (int i = 0; i < passStates.length; i++) {
            passStates[i] = null;
        }
    }

    /**
     * Applies all currently set renderstates to the supplied context
     *
     * @param context
     */
    protected void applyPassStates(RenderContext<?> context) {
        for (int x = RenderState.StateType.values().length; --x >= 0;) {
            if (passStates[x] != null) {
                savedStates[x] = context.enforcedStateList[x];
                context.enforcedStateList[x] = passStates[x];
            }
        }
    }

    /**
     * Resets all renderstates on the supplied context
     *
     * @param context
     */
    protected void resetOldStates(RenderContext<?> context) {
        for (int x = RenderState.StateType.values().length; --x >= 0;) {
            if (passStates[x] != null) {
                context.enforcedStateList[x] = savedStates[x];
            }
        }
    }

    /** @return Returns the enabled. */
    public boolean isEnabled() {
        return enabled;
    }

    /** @param enabled The enabled to set. */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** @return Returns the zFactor. */
    public float getZFactor() {
        return zFactor;
    }

    /**
     * Sets the polygon offset param - factor - for this Pass.
     *
     * @param factor The zFactor to set.
     */
    public void setZFactor(float factor) {
        zFactor = factor;
    }

    /** @return Returns the zOffset. */
    public float getZOffset() {
        return zOffset;
    }

    /**
     * Sets the polygon offset param - offset - for this Pass.
     *
     * @param offset The zOffset to set.
     */
    public void setZOffset(float offset) {
        zOffset = offset;
	}

    public Class<?> getClassTag() {
        return this.getClass();
    }

	public void write(JMEExporter e) throws IOException {
		OutputCapsule oc = e.getCapsule(this);
		oc.write(enabled, "enabled", true);
		oc.write(zFactor, "zFactor", 0);
		oc.write(zOffset, "zOffset", 0);
		oc.write(passStates, "passStates", null);
		oc.write(savedStates, "savedStates", null);
	}

	public void read(JMEImporter e) throws IOException {
		InputCapsule ic = e.getCapsule(this);
		enabled = ic.readBoolean("enabled", true);
		zFactor = ic.readFloat("zFactor", 0);
		zOffset = ic.readFloat("zOffset", 0);
		Savable[] temp = ic.readSavableArray("passStates", null);
		// TODO: Perhaps this should be redone to use the state type to place it
		// in the right spot in the array?
		passStates = new RenderState[temp.length];
		for (int i = 0; i < temp.length; i++) {
			passStates[i] = (RenderState) temp[i];
		}
		temp = ic.readSavableArray("savedStates", null);
		savedStates = new RenderState[temp.length];
		for (int i = 0; i < temp.length; i++) {
			savedStates[i] = (RenderState) temp[i];
		}
	}
}
