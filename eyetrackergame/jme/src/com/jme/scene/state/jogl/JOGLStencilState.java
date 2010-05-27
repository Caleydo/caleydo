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

package com.jme.scene.state.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.jme.renderer.RenderContext;
import com.jme.renderer.jogl.JOGLContextCapabilities;
import com.jme.renderer.jogl.JOGLRenderer;
import com.jme.scene.state.StencilState;
import com.jme.scene.state.jogl.records.StencilStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>JOGLStencilState</code>
 *
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @author Steve Vaughan - JOGL port
 * @version $Id: JOGLStencilState.java 4458 2009-07-02 08:39:10Z julien.gouesse $
 */
public class JOGLStencilState extends StencilState {
    
    private static final long serialVersionUID = 2L;

    
    public JOGLStencilState() {
        this( ( ( JOGLRenderer ) DisplaySystem.getDisplaySystem().
        getRenderer()).getContextCapabilities() );
    }
    
    public JOGLStencilState(JOGLContextCapabilities caps) {
        twoSidedSupport = caps.GL_EXT_stencil_two_side;
        stencilWrapSupport = caps.GL_EXT_stencil_wrap;
    }

    @Override
    public void apply() {
        final GL gl = GLU.getCurrentGL();

        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
        StencilStateRecord record = (StencilStateRecord) context.getStateRecord(StateType.Stencil);
        context.currentStates[StateType.Stencil.ordinal()] = this;

        setEnabled(isEnabled(), twoSidedSupport ? isUseTwoSided() : false,
                record);
        if (isEnabled()) {
            if (isUseTwoSided() && twoSidedSupport) {
                gl.glActiveStencilFaceEXT(GL.GL_BACK);
                applyMask(getStencilWriteMaskBack(), record, 2);
                applyFunc(getGLStencilFunction(getStencilFunctionBack()),
                        getStencilReferenceBack(), getStencilFuncMaskBack(),
                        record, 2);
                applyOp(getGLStencilOp(getStencilOpFailBack()),
                        getGLStencilOp(getStencilOpZFailBack()),
                        getGLStencilOp(getStencilOpZPassBack()), record, 2);

                gl.glActiveStencilFaceEXT(GL.GL_FRONT);
                applyMask(getStencilWriteMaskFront(), record, 1);
                applyFunc(getGLStencilFunction(getStencilFunctionFront()),
                        getStencilReferenceFront(), getStencilFuncMaskFront(),
                        record, 1);
                applyOp(getGLStencilOp(getStencilOpFailFront()),
                        getGLStencilOp(getStencilOpZFailFront()),
                        getGLStencilOp(getStencilOpZPassFront()), record, 1);
            } else {
                applyMask(getStencilWriteMaskFront(), record, 0);
                applyFunc(getGLStencilFunction(getStencilFunctionFront()),
                        getStencilReferenceFront(), getStencilFuncMaskFront(),
                        record, 0);
                applyOp(getGLStencilOp(getStencilOpFailFront()),
                        getGLStencilOp(getStencilOpZFailFront()),
                        getGLStencilOp(getStencilOpZPassFront()), record, 0);
            }
        }

        if (!record.isValid())
            record.validate();
    }

    private static int getGLStencilFunction(StencilFunction function) {
        switch (function) {
            case Always:
                return GL.GL_ALWAYS;
            case Never:
                return GL.GL_NEVER;
            case EqualTo:
                return GL.GL_EQUAL;
            case NotEqualTo:
                return GL.GL_NOTEQUAL;
            case GreaterThan:
                return GL.GL_GREATER;
            case GreaterThanOrEqualTo:
                return GL.GL_GEQUAL;
            case LessThan:
                return GL.GL_LESS;
            case LessThanOrEqualTo:
                return GL.GL_LEQUAL;
        }
        throw new IllegalArgumentException("unknown function: " + function);
    }

    private static int getGLStencilOp(StencilOperation operation) {
        switch (operation) {
            case Keep:
                return GL.GL_KEEP;
            case DecrementWrap:
                if (stencilWrapSupport)
                    return GL.GL_DECR_WRAP_EXT;
                // FALLS THROUGH
            case Decrement:
                return GL.GL_DECR;
            case IncrementWrap:
                if (stencilWrapSupport)
                    return GL.GL_INCR_WRAP_EXT;
                // FALLS THROUGH
            case Increment:
                return GL.GL_INCR;
            case Invert:
                return GL.GL_INVERT;
            case Replace:
                return GL.GL_REPLACE;
            case Zero:
                return GL.GL_ZERO;
        }
        throw new IllegalArgumentException("unknown operation: " + operation);
    }

    private void setEnabled(boolean enable, boolean twoSided,
            StencilStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        if (record.isValid()) {
            if (enable && !record.enabled) {
                gl.glEnable(GL.GL_STENCIL_TEST);
            } else if (!enable && record.enabled) {
                gl.glDisable(GL.GL_STENCIL_TEST);
            }
        } else {
            if (enable) {
                gl.glEnable(GL.GL_STENCIL_TEST);
            } else {
                gl.glDisable(GL.GL_STENCIL_TEST);
            }
        }

        setTwoSidedEnabled(enable ? twoSided : false, record);
        record.enabled = enable;
    }

    private void setTwoSidedEnabled(boolean enable, StencilStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        if (twoSidedSupport) {
            if (record.isValid()) {
                if (enable && !record.useTwoSided) {
                    gl.glEnable(GL.GL_STENCIL_TEST_TWO_SIDE_EXT);
                } else if (!enable && record.useTwoSided) {
                    gl.glDisable(GL.GL_STENCIL_TEST_TWO_SIDE_EXT);
                }
            } else {
                if (enable) {
                    gl.glEnable(GL.GL_STENCIL_TEST_TWO_SIDE_EXT);
                } else {
                    gl.glDisable(GL.GL_STENCIL_TEST_TWO_SIDE_EXT);
                }
            }
        }
        record.useTwoSided = enable;
    }

    private void applyMask(int writeMask, StencilStateRecord record, int face) {
final GL gl = GLU.getCurrentGL();

//        if (!record.isValid() || writeMask != record.writeMask[face]) {
            gl.glStencilMask(writeMask);
//            record.writeMask[face] = writeMask;
//        }
    }

    private void applyFunc(int glfunc, int stencilRef, int funcMask,
            StencilStateRecord record, int face) {
final GL gl = GLU.getCurrentGL();

//        if (!record.isValid() || glfunc != record.func[face] || stencilRef != record.ref[face]
//                || funcMask != record.funcMask[face]) {
            gl.glStencilFunc(glfunc, stencilRef, funcMask);
//            record.func[face] = glfunc;
//            record.ref[face] = stencilRef;
//            record.funcMask[face] = funcMask;
//        }
    }

    private void applyOp(int fail, int zfail, int zpass,
            StencilStateRecord record, int face) {
final GL gl = GLU.getCurrentGL();

//        if (!record.isValid() || fail != record.fail[face] || zfail != record.zfail[face]
//                || zpass != record.zpass[face]) {
            gl.glStencilOp(fail, zfail, zpass);
//            record.fail[face] = fail;
//            record.zfail[face] = zfail;
//            record.zpass[face] = zpass;
//        }
    }

    @Override
    public StencilStateRecord createStateRecord() {
        return new StencilStateRecord();
    }
}
