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
 * The StencilState RenderState allows the user to set the attributes of the
 * stencil buffer of the renderer. The Stenciling is similar to Z-Buffering in
 * that it allows enabling and disabling drawing on a per pixel basis. You can
 * use the stencil plane to mask out portions of the rendering to create special
 * effects, such as outlining or planar shadows. Our stencil state supports
 * setting operations for front and back facing polygons separately. If your
 * card does not support setting faces independantly, the front face values will
 * be used for both sides.
 * 
 * @author Mark Powell
 * @author Joshua Slack - two sided, wrap, enums, etc.
 * @version $Id: StencilState.java 4336 2009-05-03 20:57:01Z christoph.luder $
 */
public abstract class StencilState extends RenderState {

    public enum StencilFunction {
        /** A stencil function that never passes. */
        Never,
        /** A stencil function that passes if (ref & mask) < (stencil & mask). */
        LessThan,
        /** A stencil function that passes if (ref & max) <= (stencil & mask). */
        LessThanOrEqualTo,
        /** A stencil function that passes if (ref & max) > (stencil & mask). */
        GreaterThan,
        /** A stencil function that passes if (ref & max) >= (stencil & mask). */
        GreaterThanOrEqualTo,
        /** A stencil function that passes if (ref & max) == (stencil & mask). */
        EqualTo,
        /** A stencil function that passes if (ref & max) != (stencil & mask). */
        NotEqualTo,
        /** A stencil function that always passes. (Default) */
        Always;
    }

    public enum StencilOperation {
        /** A stencil function result that keeps the current value. */
        Keep,
        /** A stencil function result that sets the stencil buffer value to 0. */
        Zero,
        /**
         * A stencil function result that sets the stencil buffer value to ref,
         * as specified by stencil function.
         */
        Replace,
        /**
         * A stencil function result that increments the current stencil buffer
         * value.
         */
        Increment,
        /**
         * A stencil function result that decrements the current stencil buffer
         * value.
         */
        Decrement,
        /**
         * A stencil function result that increments the current stencil buffer
         * value and wraps around to the lowest stencil value if it reaches the
         * max. (if the renderer does not support stencil wrap, we'll fall back
         * to Increment)
         */
        IncrementWrap,
        /**
         * A stencil function result that decrements the current stencil buffer
         * and wraps around to the highest stencil value if it reaches the min.
         * value. (if the renderer does not support stencil wrap, we'll fall
         * back to Decrement)
         */
        DecrementWrap,
        /**
         * A stencil function result that bitwise inverts the current stencil
         * buffer value.
         */
        Invert;
    }

    protected static boolean twoSidedSupport = false;
    protected static boolean stencilWrapSupport = false;

    private boolean useTwoSided = false;
    
    private StencilFunction stencilFunctionFront = StencilFunction.Always;
    private int stencilReferenceFront = 0;
    private int stencilFuncMaskFront = ~0;
    private int stencilWriteMaskFront = ~0;
    private StencilOperation stencilOpFailFront = StencilOperation.Keep;
    private StencilOperation stencilOpZFailFront = StencilOperation.Keep;
    private StencilOperation stencilOpZPassFront = StencilOperation.Keep;

    private StencilFunction stencilFunctionBack = StencilFunction.Always;
    private int stencilReferenceBack = 0;
    private int stencilFuncMaskBack = ~0;
    private int stencilWriteMaskBack = ~0;
    private StencilOperation stencilOpFailBack = StencilOperation.Keep;
    private StencilOperation stencilOpZFailBack = StencilOperation.Keep;
    private StencilOperation stencilOpZPassBack = StencilOperation.Keep;

    /**
     * @return true if we can handle doing separate operations for front and
     *         back facing polys in a single pass.
     */
    public static boolean supportsTwoSided() {
        return twoSidedSupport;
    }

    /**
     * @return true if we can handle wrapping increment/decrement operations.
     */
    public static boolean supportsStencilWrap() {
        return stencilWrapSupport;
    }

    /**
     * Returns RS_STENCIL
     * 
     * @see com.jme.scene.state.RenderState#getType()
     * @deprecated As of 2.0, use {@link RenderState#getStateType()} instead.
     */
    public int getType() {
        return RS_STENCIL;
    }

    /**
     * <code>getStateType</code> returns the type {@link RenderState.StateType#Stencil}
     * 
     * @return {@link RenderState.StateType#Stencil}
     * @see com.jme.scene.state.RenderState#getStateType()
     */
    public StateType getStateType() {
    	
        return StateType.Stencil;
    }

    /**
     * Sets the function that defines if a stencil test passes or not for both faces.
     * 
     * @param function
     *            The new stencil function for both faces.
     * @throws IllegalArgumentException
     *             if function is null
     */
    public void setStencilFunction(StencilFunction function) {
        setStencilFunctionFront(function);
        setStencilFunctionBack(function);
    }

    /**
     * Sets the stencil reference to be used during the stencil function for both faces.
     * 
     * @param reference
     *            The new stencil reference for both faces.
     */
    public void setStencilReference(int reference) {
        setStencilReferenceFront(reference);
        setStencilReferenceBack(reference);
    }

    /**
     * Convienence method for setting both types of stencil masks at once for both faces.
     * 
     * @param mask
     *            The new stencil write and func mask for both faces.
     */
    public void setStencilMask(int mask) {
        setStencilMaskFront(mask);
        setStencilMaskBack(mask);
    }

    /**
     * Controls which stencil bitplanes are written for both faces.
     * 
     * @param mask
     *            The new stencil write mask for both faces.
     */
    public void setStencilWriteMask(int mask) {
        setStencilWriteMaskFront(mask);
        setStencilWriteMaskBack(mask);
    }

    /**
     * Sets the stencil mask to be used during stencil functions for both faces.
     * 
     * @param mask
     *            The new stencil function mask for both faces.
     */
    public void setStencilFuncMask(int mask) {
        setStencilFuncMaskFront(mask);
        setStencilFuncMaskBack(mask);
    }

    /**
     * Specifies the aciton to take when the stencil test fails for both faces.
     * 
     * @param operation
     *            The new stencil operation for both faces.
     * @throws IllegalArgumentException
     *             if operation is null
     */
    public void setStencilOpFail(StencilOperation operation) {
        setStencilOpFailFront(operation);
        setStencilOpFailBack(operation);
    }

    /**
     * Specifies stencil action when the stencil test passes, but the depth test
     * fails for both faces.
     * 
     * @param operation
     *            The Z test operation to set for both faces.
     * @throws IllegalArgumentException
     *             if operation is null
     */
    public void setStencilOpZFail(StencilOperation operation) {
        setStencilOpZFailFront(operation);
        setStencilOpZFailBack(operation);
    }

    /**
     * Specifies stencil action when both the stencil test and the depth test
     * pass, or when the stencil test passes and either there is no depth buffer
     * or depth testing is not enabled.
     * 
     * @param operation
     *            The new Z test pass operation to set for both faces.
     * @throws IllegalArgumentException
     *             if operation is null
     */
    public void setStencilOpZPass(StencilOperation operation) {
        setStencilOpZPassFront(operation);
        setStencilOpZPassBack(operation);
    }

    /**
     * Sets the function that defines if a stencil test passes or not for front faces.
     * 
     * @param function
     *            The new stencil function for front faces.
     * @throws IllegalArgumentException
     *             if function is null
     */
    public void setStencilFunctionFront(StencilFunction function) {
        if (function == null) {
            throw new IllegalArgumentException("function can not be null.");
        }
        this.stencilFunctionFront = function;
        setNeedsRefresh(true);
    }

    /**
     * @return The current stencil function for front faces. Default is StencilFunction.Always
     */
    public StencilFunction getStencilFunctionFront() {
        return stencilFunctionFront;
    }

    /**
     * Sets the stencil reference to be used during the stencil function for front faces.
     * 
     * @param reference
     *            The new stencil reference for front faces.
     */
    public void setStencilReferenceFront(int reference) {
        this.stencilReferenceFront = reference;
        setNeedsRefresh(true);
    }

    /**
     * @return The current stencil reference for front faces. Default is 0
     */
    public int getStencilReferenceFront() {
        return stencilReferenceFront;
    }

    /**
     * Convienence method for setting both types of stencil masks at once for front faces.
     * 
     * @param mask
     *            The new stencil write and func mask for front faces.
     */
    public void setStencilMaskFront(int mask) {
        setStencilWriteMaskFront(mask);
        setStencilFuncMaskFront(mask);
    }

    /**
     * Controls which stencil bitplanes are written for front faces.
     * 
     * @param mask
     *            The new stencil write mask for front faces.
     */
    public void setStencilWriteMaskFront(int mask) {
        this.stencilWriteMaskFront = mask;
        setNeedsRefresh(true);
    }

    /**
     * @return The current stencil write mask for front faces. Default is all 1's (~0)
     */
    public int getStencilWriteMaskFront() {
        return stencilWriteMaskFront;
    }

    /**
     * Sets the stencil mask to be used during stencil functions for front faces.
     * 
     * @param mask
     *            The new stencil function mask for front faces.
     */
    public void setStencilFuncMaskFront(int mask) {
        this.stencilFuncMaskFront = mask;
        setNeedsRefresh(true);
    }

    /**
     * @return The current stencil function mask for front faces. Default is all 1's (~0)
     */
    public int getStencilFuncMaskFront() {
        return stencilFuncMaskFront;
    }

    /**
     * Specifies the aciton to take when the stencil test fails for front faces.
     * 
     * @param operation
     *            The new stencil operation for front faces.
     * @throws IllegalArgumentException
     *             if operation is null
     */
    public void setStencilOpFailFront(StencilOperation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("operation can not be null.");
        }
        this.stencilOpFailFront = operation;
        setNeedsRefresh(true);
    }

    /**
     * @return The current stencil operation for front faces. Default is StencilOperation.Keep
     */
    public StencilOperation getStencilOpFailFront() {
        return stencilOpFailFront;
    }

    /**
     * Specifies stencil action when the stencil test passes, but the depth test
     * fails for front faces.
     * 
     * @param operation
     *            The Z test operation to set for front faces.
     * @throws IllegalArgumentException
     *             if operation is null
     */
    public void setStencilOpZFailFront(StencilOperation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("operation can not be null.");
        }
        this.stencilOpZFailFront = operation;
        setNeedsRefresh(true);
    }

    /**
     * @return The current Z op fail function for front faces. Default is StencilOperation.Keep
     */
    public StencilOperation getStencilOpZFailFront() {
        return stencilOpZFailFront;
    }

    /**
     * Specifies stencil action when both the stencil test and the depth test
     * pass, or when the stencil test passes and either there is no depth buffer
     * or depth testing is not enabled.
     * 
     * @param operation
     *            The new Z test pass operation to set for front faces.
     * @throws IllegalArgumentException
     *             if operation is null
     */
    public void setStencilOpZPassFront(StencilOperation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("operation can not be null.");
        }
        this.stencilOpZPassFront = operation;
        setNeedsRefresh(true);
    }

    /**
     * @return The current Z op pass function for front faces. Default is StencilOperation.Keep
     */
    public StencilOperation getStencilOpZPassFront() {
        return stencilOpZPassFront;
    }

    /**
     * Sets the function that defines if a stencil test passes or not for back faces.
     * 
     * @param function
     *            The new stencil function for back faces.
     * @throws IllegalArgumentException
     *             if function is null
     */
    public void setStencilFunctionBack(StencilFunction function) {
        if (function == null) {
            throw new IllegalArgumentException("function can not be null.");
        }
        this.stencilFunctionBack = function;
        setNeedsRefresh(true);
    }

    /**
     * @return The current stencil function for back faces. Default is StencilFunction.Always
     */
    public StencilFunction getStencilFunctionBack() {
        return stencilFunctionBack;
    }

    /**
     * Sets the stencil reference to be used during the stencil function for back faces.
     * 
     * @param reference
     *            The new stencil reference for back faces.
     */
    public void setStencilReferenceBack(int reference) {
        this.stencilReferenceBack = reference;
        setNeedsRefresh(true);
    }

    /**
     * @return The current stencil reference for back faces. Default is 0
     */
    public int getStencilReferenceBack() {
        return stencilReferenceBack;
    }

    /**
     * Convienence method for setting both types of stencil masks at once for back faces.
     * 
     * @param mask
     *            The new stencil write and func mask for back faces.
     */
    public void setStencilMaskBack(int mask) {
        setStencilWriteMaskBack(mask);
        setStencilFuncMaskBack(mask);
    }

    /**
     * Controls which stencil bitplanes are written for back faces.
     * 
     * @param mask
     *            The new stencil write mask for back faces.
     */
    public void setStencilWriteMaskBack(int mask) {
        this.stencilWriteMaskBack = mask;
        setNeedsRefresh(true);
    }

    /**
     * @return The current stencil write mask for back faces. Default is all 1's (~0)
     */
    public int getStencilWriteMaskBack() {
        return stencilWriteMaskBack;
    }

    /**
     * Sets the stencil mask to be used during stencil functions for back faces.
     * 
     * @param mask
     *            The new stencil function mask for back faces.
     */
    public void setStencilFuncMaskBack(int mask) {
        this.stencilFuncMaskBack = mask;
        setNeedsRefresh(true);
    }

    /**
     * @return The current stencil function mask for back faces. Default is all 1's (~0)
     */
    public int getStencilFuncMaskBack() {
        return stencilFuncMaskBack;
    }

    /**
     * Specifies the aciton to take when the stencil test fails for back faces.
     * 
     * @param operation
     *            The new stencil operation for back faces.
     * @throws IllegalArgumentException
     *             if operation is null
     */
    public void setStencilOpFailBack(StencilOperation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("operation can not be null.");
        }
        this.stencilOpFailBack = operation;
        setNeedsRefresh(true);
    }

    /**
     * @return The current stencil operation for back faces. Default is StencilOperation.Keep
     */
    public StencilOperation getStencilOpFailBack() {
        return stencilOpFailBack;
    }

    /**
     * Specifies stencil action when the stencil test passes, but the depth test
     * fails.
     * 
     * @param operation
     *            The Z test operation to set for back faces.
     * @throws IllegalArgumentException
     *             if operation is null
     */
    public void setStencilOpZFailBack(StencilOperation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("operation can not be null.");
        }
        this.stencilOpZFailBack = operation;
        setNeedsRefresh(true);
    }

    /**
     * @return The current Z op fail function for back faces. Default is StencilOperation.Keep
     */
    public StencilOperation getStencilOpZFailBack() {
        return stencilOpZFailBack;
    }

    /**
     * Specifies stencil action when both the stencil test and the depth test
     * pass, or when the stencil test passes and either there is no depth buffer
     * or depth testing is not enabled.
     * 
     * @param operation
     *            The new Z test pass operation to set for back faces.
     * @throws IllegalArgumentException
     *             if operation is null
     */
    public void setStencilOpZPassBack(StencilOperation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("operation can not be null.");
        }
        this.stencilOpZPassBack = operation;
        setNeedsRefresh(true);
    }

    /**
     * @return The current Z op pass function for back faces. Default is StencilOperation.Keep
     */
    public StencilOperation getStencilOpZPassBack() {
        return stencilOpZPassBack;
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(useTwoSided, "useTwoSided", false);
        capsule.write(stencilFunctionFront, "stencilFuncFront", StencilFunction.Always);
        capsule.write(stencilReferenceFront, "stencilRefFront", 0);
        capsule.write(stencilWriteMaskFront, "stencilWriteMaskFront", ~0);
        capsule.write(stencilFuncMaskFront, "stencilFuncMaskFront", ~0);
        capsule.write(stencilOpFailFront, "stencilOpFailFront", StencilOperation.Keep);
        capsule.write(stencilOpZFailFront, "stencilOpZFailFront", StencilOperation.Keep);
        capsule.write(stencilOpZPassFront, "stencilOpZPassFront", StencilOperation.Keep);

        capsule.write(stencilFunctionBack, "stencilFuncBack", StencilFunction.Always);
        capsule.write(stencilReferenceBack, "stencilRefBack", 0);
        capsule.write(stencilWriteMaskBack, "stencilWriteMaskBack", ~0);
        capsule.write(stencilFuncMaskBack, "stencilFuncMaskBack", ~0);
        capsule.write(stencilOpFailBack, "stencilOpFailBack", StencilOperation.Keep);
        capsule.write(stencilOpZFailBack, "stencilOpZFailBack", StencilOperation.Keep);
        capsule.write(stencilOpZPassBack, "stencilOpZPassBack", StencilOperation.Keep);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        useTwoSided = capsule.readBoolean("useTwoSided", false);
        stencilFunctionFront = capsule.readEnum("stencilFuncFront", StencilFunction.class, StencilFunction.Always);
        stencilReferenceFront = capsule.readInt("stencilRefFront", 0);
        stencilWriteMaskFront = capsule.readInt("stencilWriteMaskFront", ~0);
        stencilFuncMaskFront = capsule.readInt("stencilFuncMaskFront", ~0);
        stencilOpFailFront = capsule.readEnum("stencilOpFailFront", StencilOperation.class, StencilOperation.Keep);
        stencilOpZFailFront = capsule.readEnum("stencilOpZFailFront", StencilOperation.class, StencilOperation.Keep);
        stencilOpZPassFront = capsule.readEnum("stencilOpZPassFront", StencilOperation.class, StencilOperation.Keep);

        stencilFunctionBack = capsule.readEnum("stencilFuncBack", StencilFunction.class, StencilFunction.Always);
        stencilReferenceBack = capsule.readInt("stencilRefBack", 0);
        stencilWriteMaskBack = capsule.readInt("stencilWriteMaskBack", ~0);
        stencilFuncMaskBack = capsule.readInt("stencilFuncMaskBack", ~0);
        stencilOpFailBack = capsule.readEnum("stencilOpFailBack", StencilOperation.class, StencilOperation.Keep);
        stencilOpZFailBack = capsule.readEnum("stencilOpZFailBack", StencilOperation.class, StencilOperation.Keep);
        stencilOpZPassBack = capsule.readEnum("stencilOpZPassBack", StencilOperation.class, StencilOperation.Keep);
    }

    public Class<?> getClassTag() {
        return StencilState.class;
    }

    public boolean isUseTwoSided() {
        return useTwoSided;
    }

    public void setUseTwoSided(boolean useTwoSided) {
        this.useTwoSided = useTwoSided;
    }
}
