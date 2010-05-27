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

package com.jme.scene.state;

import java.io.IOException;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>ZBufferState</code> maintains how the use of the depth buffer is to
 * occur. Depth buffer comparisons are used to evaluate what incoming fragment
 * will be used. This buffer is based on z depth, or distance between the pixel
 * source and the eye.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: ZBufferState.java 4336 2009-05-03 20:57:01Z christoph.luder $
 */
public abstract class ZBufferState extends RenderState {

    public enum TestFunction {
        /**
         * Depth comparison never passes.
         */
        Never,
        /**
         * Depth comparison always passes.
         */
        Always,
        /**
         * Passes if the incoming value is the same as the stored value.
         */
        EqualTo,
        /**
         * Passes if the incoming value is not equal to the stored value.
         */
        NotEqualTo,
        /**
         * Passes if the incoming value is less than the stored value.
         */
        LessThan,
        /**
         * Passes if the incoming value is less than or equal to the stored
         * value.
         */
        LessThanOrEqualTo,
        /**
         * Passes if the incoming value is greater than the stored value.
         */
        GreaterThan,
        /**
         * Passes if the incoming value is greater than or equal to the stored
         * value.
         */
        GreaterThanOrEqualTo;

    }

    /** Depth function. */
    protected TestFunction function = TestFunction.LessThan;
    /** Depth mask is writable or not. */
    protected boolean writable = true;

    /**
     * Constructor instantiates a new <code>ZBufferState</code> object. The
     * initial values are TestFunction.LessThan and depth writing on.
     */
    public ZBufferState() {
    }

    /**
     * <code>getFunction</code> returns the current depth function.
     * 
     * @return the depth function currently used.
     */
    public TestFunction getFunction() {
        return function;
    }

    /**
     * <code>setFunction</code> sets the depth function.
     * 
     * @param function
     *            the depth function.
     * @throws IllegalArgumentException
     *             if function is null
     */
    public void setFunction(TestFunction function) {
        if (function == null) {
            throw new IllegalArgumentException("function can not be null.");
        }
        this.function = function;
        setNeedsRefresh(true);
    }

    /**
     * <code>isWritable</code> returns if the depth mask is writable or not.
     * 
     * @return true if the depth mask is writable, false otherwise.
     */
    public boolean isWritable() {
        return writable;
    }

    /**
     * <code>setWritable</code> sets the depth mask writable or not.
     * 
     * @param writable
     *            true to turn on depth writing, false otherwise.
     */
    public void setWritable(boolean writable) {
        this.writable = writable;
        setNeedsRefresh(true);
    }

    /**
     * <code>getType</code> returns the type of renderstate this is.
     * (RS_ZBUFFER).
     * 
     * @see com.jme.scene.state.RenderState#getType()
     * @deprecated As of 2.0, use {@link RenderState#getStateType()} instead.
     */
    public int getType() {
        return RS_ZBUFFER;
    }

    /**
     * <code>getStateType</code> returns the type {@link RenderState.StateType#ZBuffer}
     * 
     * @return {@link RenderState.StateType#ZBuffer}
     * @see com.jme.scene.state.RenderState#getStateType()
     */
    public StateType getStateType() {
    	
        return StateType.ZBuffer;
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(function, "function", TestFunction.LessThan);
        capsule.write(writable, "writable", true);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        function = capsule.readEnum("function", TestFunction.class, TestFunction.LessThan);
        writable = capsule.readBoolean("writable", true);
    }

    public Class<?> getClassTag() {
        return ZBufferState.class;
    }
}
