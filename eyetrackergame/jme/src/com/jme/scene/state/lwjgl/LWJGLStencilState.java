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

package com.jme.scene.state.lwjgl;

import org.lwjgl.opengl.EXTStencilTwoSide;
import org.lwjgl.opengl.EXTStencilWrap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.jme.renderer.RenderContext;
import com.jme.scene.state.StencilState;
import com.jme.scene.state.lwjgl.records.StencilStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>LWJGLStencilState</code>
 * 
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @version $Id: LWJGLStencilState.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class LWJGLStencilState extends StencilState {
    private static final long serialVersionUID = 2L;

    public LWJGLStencilState() {
        twoSidedSupport = GLContext.getCapabilities().GL_EXT_stencil_two_side;
        stencilWrapSupport = GLContext.getCapabilities().GL_EXT_stencil_wrap;
    }

    @Override
    public void apply() {
        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
        StencilStateRecord record = (StencilStateRecord) context.getStateRecord(StateType.Stencil);
        context.currentStates[StateType.Stencil.ordinal()] = this;

        setEnabled(isEnabled(), twoSidedSupport ? isUseTwoSided() : false,
                record);
        if (isEnabled()) {
            if (isUseTwoSided() && twoSidedSupport) {
                EXTStencilTwoSide.glActiveStencilFaceEXT(GL11.GL_BACK);
                applyMask(getStencilWriteMaskBack(), record, 2);
                applyFunc(getGLStencilFunction(getStencilFunctionBack()),
                        getStencilReferenceBack(), getStencilFuncMaskBack(),
                        record, 2);
                applyOp(getGLStencilOp(getStencilOpFailBack()),
                        getGLStencilOp(getStencilOpZFailBack()),
                        getGLStencilOp(getStencilOpZPassBack()), record, 2);

                EXTStencilTwoSide.glActiveStencilFaceEXT(GL11.GL_FRONT);
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
                return GL11.GL_ALWAYS;
            case Never:
                return GL11.GL_NEVER;
            case EqualTo:
                return GL11.GL_EQUAL;
            case NotEqualTo:
                return GL11.GL_NOTEQUAL;
            case GreaterThan:
                return GL11.GL_GREATER;
            case GreaterThanOrEqualTo:
                return GL11.GL_GEQUAL;
            case LessThan:
                return GL11.GL_LESS;
            case LessThanOrEqualTo:
                return GL11.GL_LEQUAL;
        }
        throw new IllegalArgumentException("unknown function: " + function);
    }

    private static int getGLStencilOp(StencilOperation operation) {
        switch (operation) {
            case Keep:
                return GL11.GL_KEEP;
            case DecrementWrap:
                if (stencilWrapSupport)
                    return EXTStencilWrap.GL_DECR_WRAP_EXT;
                // FALLS THROUGH
            case Decrement:
                return GL11.GL_DECR;
            case IncrementWrap:
                if (stencilWrapSupport)
                    return EXTStencilWrap.GL_INCR_WRAP_EXT;
                // FALLS THROUGH
            case Increment:
                return GL11.GL_INCR;
            case Invert:
                return GL11.GL_INVERT;
            case Replace:
                return GL11.GL_REPLACE;
            case Zero:
                return GL11.GL_ZERO;
        }
        throw new IllegalArgumentException("unknown operation: " + operation);
    }

    private void setEnabled(boolean enable, boolean twoSided,
            StencilStateRecord record) {
        if (record.isValid()) {
            if (enable && !record.enabled) {
                GL11.glEnable(GL11.GL_STENCIL_TEST);
            } else if (!enable && record.enabled) {
                GL11.glDisable(GL11.GL_STENCIL_TEST);
            }
        } else {
            if (enable) {
                GL11.glEnable(GL11.GL_STENCIL_TEST);
            } else {
                GL11.glDisable(GL11.GL_STENCIL_TEST);
            }
        }

        setTwoSidedEnabled(enable ? twoSided : false, record);
        record.enabled = enable;
    }

    private void setTwoSidedEnabled(boolean enable, StencilStateRecord record) {
        if (twoSidedSupport) {
            if (record.isValid()) {
                if (enable && !record.useTwoSided) {
                    GL11.glEnable(EXTStencilTwoSide.GL_STENCIL_TEST_TWO_SIDE_EXT);
                } else if (!enable && record.useTwoSided) {
                    GL11.glDisable(EXTStencilTwoSide.GL_STENCIL_TEST_TWO_SIDE_EXT);
                }
            } else {
                if (enable) {
                    GL11.glEnable(EXTStencilTwoSide.GL_STENCIL_TEST_TWO_SIDE_EXT);
                } else {
                    GL11.glDisable(EXTStencilTwoSide.GL_STENCIL_TEST_TWO_SIDE_EXT);
                }
            }
        }
        record.useTwoSided = enable;
    }

    private void applyMask(int writeMask, StencilStateRecord record, int face) {
//        if (!record.isValid() || writeMask != record.writeMask[face]) {
            GL11.glStencilMask(writeMask);
//            record.writeMask[face] = writeMask;
//        }
    }

    private void applyFunc(int glfunc, int stencilRef, int funcMask,
            StencilStateRecord record, int face) {
//        if (!record.isValid() || glfunc != record.func[face] || stencilRef != record.ref[face]
//                || funcMask != record.funcMask[face]) {
            GL11.glStencilFunc(glfunc, stencilRef, funcMask);
//            record.func[face] = glfunc;
//            record.ref[face] = stencilRef;
//            record.funcMask[face] = funcMask;
//        }
    }

    private void applyOp(int fail, int zfail, int zpass,
            StencilStateRecord record, int face) {
//        if (!record.isValid() || fail != record.fail[face] || zfail != record.zfail[face]
//                || zpass != record.zpass[face]) {
            GL11.glStencilOp(fail, zfail, zpass);
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
