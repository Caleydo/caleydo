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

package com.jme.renderer;

import com.jme.scene.state.RenderState;
import com.jme.scene.state.StateRecord;

/**
 * Represents the state of an individual context in OpenGL.
 * 
 * @author Joshua Slack
 * @version $Id: RenderContext.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class RenderContext<ContextHolder> {

    /** List of states that override any set states on a spatial if not null. */
    public RenderState[] enforcedStateList = new RenderState[RenderState.StateType.values().length];

    /** RenderStates a Spatial contains during rendering. */
    public RenderState[] currentStates = new RenderState[RenderState.StateType.values().length];

    private StateRecord[] stateRecords = new StateRecord[RenderState.StateType.values().length];
    private StateRecord lineRecord = null;
    private StateRecord rendererRecord = null;
    
    private ContextHolder contextHolder = null;
    
    public RenderContext(ContextHolder key) {
        contextHolder = key;
    }
    
    public void setupRecords(Renderer r) {
    	
    	for(RenderState.StateType type : RenderState.StateType.values()) {
    		stateRecords[type.ordinal()] = r.createState(type).createStateRecord();
    	}
        lineRecord = r.createLineRecord();
        rendererRecord = r.createRendererRecord();
    }
    
    public void invalidateStates() {
        for (int i = 0; i < stateRecords.length; i++) {
            stateRecords[i].invalidate();
        }
        lineRecord.invalidate();
        rendererRecord.invalidate();
        
        clearCurrentStates();
    }
    
    /**
     * @deprecated As of 2.0, use {@link #getStateRecord(com.jme.scene.state.RenderState.StateType)} instead.
     */
    public StateRecord getStateRecord(int state) {
    	
        return stateRecords[state];
    }
    
    /**
     * Returns the {@link StateRecord} of the given {@link RenderState.StateType}.
     * 
     * @param type {@link RenderState.StateType}
     * @return {@link StateRecord}
     */
    public StateRecord getStateRecord(RenderState.StateType type) {
    	
        return stateRecords[type.ordinal()];
    }

    public StateRecord getLineRecord() {
        return lineRecord;
    }

    public StateRecord getRendererRecord() {
        return rendererRecord;
    }

    /**
     * Enforce a particular state. In other words, the given state will override
     * any state of the same type set on a scene object. Remember to clear the
     * state when done enforcing. Very useful for multipass techniques where
     * multiple sets of states need to be applied to a scenegraph drawn multiple
     * times.
     * 
     * @param state
     *            state to enforce
     */
    public void enforceState(RenderState state) {
        enforcedStateList[state.getStateType().ordinal()] = state;
    }

    /**
     * Clears an enforced render state index by setting it to null. This allows
     * object specific states to be used.
     * 
     * @param renderStateType
     *            The type of RenderState to clear enforcement on.
     * @deprecated As of 2.0, use {@link #clearEnforcedState(com.jme.scene.state.RenderState.StateType)} instead.
     */
    public void clearEnforcedState(int renderStateType) {
        if (enforcedStateList != null) {
            enforcedStateList[renderStateType] = null;
        }
    }

    /**
     * Clears an enforced render state by setting it to null. This allows
     * object specific states to be used.
     * 
     * @param type
     *            The type of {@link RenderState} to clear enforcement on.
     */
    public void clearEnforcedState(RenderState.StateType type) {
        if (enforcedStateList != null) {
            enforcedStateList[type.ordinal()] = null;
        }
    }

    /**
     * sets all enforced states to null.
     * 
     * @see com.jme.scene.Spatial#clearEnforcedState(int)
     */
    public void clearEnforcedStates() {
        for (int i = 0; i < enforcedStateList.length; i++)
            enforcedStateList[i] = null;
    }

    /**
     * sets all current states to null, and therefore forces the use of the
     * default states.
     *
     */
    public void clearCurrentStates() {
        for (int i = 0; i < currentStates.length; i++)
            currentStates[i] = null;
    }

    /**
     * clears the specified state. The state is referenced by it's int value,
     * and therefore should be called via RenderState's constant list. For
     * example, RenderState.RS_ALPHA.
     *
     * @param state
     *            the state to clear.
     * @deprecated As of 2.0, use {@link #clearCurrentState(com.jme.scene.state.RenderState.StateType)} instead.
     */
    public void clearCurrentState(int state) {
        currentStates[state] = null;
    }

    /**
     * Clears the specified state. The state is referenced by it's {@link RenderState.StateType} value.
     *
     * @param state
     *            the state to clear.
     */
    public void clearCurrentState(RenderState.StateType type) {
        currentStates[type.ordinal()] = null;
    }

    /**
     * @deprecated As of 2.0, use {@link #getCurrentState(com.jme.scene.state.RenderState.StateType)} instead.
     */
    public RenderState getCurrentState(int state) {
        return currentStates[state];
    }

    /**
     * Returns the specified {@link RenderState}.
     * 
     * @param type {@link RenderState.StateType}
     * @return {@link RenderState}
     */
    public RenderState getCurrentState(RenderState.StateType type) {
        return currentStates[type.ordinal()];
    }

    public ContextHolder getContextHolder() {
        return contextHolder;
    }

    public void setContextHolder(ContextHolder contextHolder) {
        this.contextHolder = contextHolder;
    }
}
