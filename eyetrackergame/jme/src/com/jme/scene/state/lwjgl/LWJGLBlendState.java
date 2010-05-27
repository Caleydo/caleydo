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

import org.lwjgl.opengl.ARBImaging;
import org.lwjgl.opengl.EXTBlendColor;
import org.lwjgl.opengl.EXTBlendEquationSeparate;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.EXTBlendMinmax;
import org.lwjgl.opengl.EXTBlendSubtract;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.jme.renderer.RenderContext;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.lwjgl.records.BlendStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>LWJGLBlendState</code> subclasses the BlendState using the LWJGL API
 * to set OpenGL's blending state params.
 * 
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @version $Id: LWJGLBlendState.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class LWJGLBlendState extends BlendState {
    private static final long serialVersionUID = 1L;

    private static boolean inited = false;

    /**
     * Constructor instantiates a new <code>LWJGLBlendState</code> object with
     * default values.
     */
    public LWJGLBlendState() {
        super();
        if (!inited) {
            supportsConstantColor = supportsEq = GLContext.getCapabilities().GL_ARB_imaging;
            supportsSeparateFunc = GLContext.getCapabilities().GL_EXT_blend_func_separate;
            supportsSeparateEq = GLContext.getCapabilities().GL_EXT_blend_equation_separate;
            supportsMinMax = GLContext.getCapabilities().GL_EXT_blend_minmax;
            supportsSubtract = GLContext.getCapabilities().GL_EXT_blend_subtract;

            // We're done initing! Wee! :)
            inited = true;
        }
    }

    /**
     * <code>apply</code> is called to set the blend state. If blending is
     * enabled, the blend function is set up and if alpha testing is enabled the
     * alpha functions are set.
     * 
     * @see com.jme.scene.state.RenderState#apply()
     */
    public void apply() {
        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
        BlendStateRecord record = (BlendStateRecord) context.getStateRecord(StateType.Blend);
        context.currentStates[StateType.Blend.ordinal()] = this;

        if (isEnabled()) {
            applyBlendEquations(isBlendEnabled(), record);
            applyBlendColor(isBlendEnabled(), record);
            applyBlendFunctions(isBlendEnabled(), record);

            applyTest(isTestEnabled(), record);
        } else {
            // disable blend
            applyBlendEquations(false, record);

            // disable alpha test
            applyTest(false, record);
        }

        if (!record.isValid())
            record.validate();
    }

    private void applyBlendEquations(boolean enabled, BlendStateRecord record) {
        if (record.isValid()) {
            if (enabled) {
                if (!record.blendEnabled) {
                    GL11.glEnable(GL11.GL_BLEND);
                    record.blendEnabled = true;
                }
                int blendEqRGB = getGLEquationValue(getBlendEquationRGB());
                if (supportsSeparateEquations()) {
                    int blendEqAlpha = getGLEquationValue(getBlendEquationAlpha());
                    if (record.blendEqRGB != blendEqRGB
                            || record.blendEqAlpha != blendEqAlpha) {
                        EXTBlendEquationSeparate.glBlendEquationSeparateEXT(
                                blendEqRGB, blendEqAlpha);
                        record.blendEqRGB = blendEqRGB;
                        record.blendEqAlpha = blendEqAlpha;
                    }
                } else if (supportsEq) {
                    if (record.blendEqRGB != blendEqRGB) {
                        ARBImaging.glBlendEquation(blendEqRGB);
                        record.blendEqRGB = blendEqRGB;
                    }
                }
            } else if (record.blendEnabled) {
                GL11.glDisable(GL11.GL_BLEND);
                record.blendEnabled = false;
            }

        } else {
            if (enabled) {
                GL11.glEnable(GL11.GL_BLEND);
                record.blendEnabled = true;
                int blendEqRGB = getGLEquationValue(getBlendEquationRGB());
                if (supportsSeparateEquations()) {
                    int blendEqAlpha = getGLEquationValue(getBlendEquationAlpha());
                    EXTBlendEquationSeparate.glBlendEquationSeparateEXT(
                            blendEqRGB, blendEqAlpha);
                    record.blendEqRGB = blendEqRGB;
                    record.blendEqAlpha = blendEqAlpha;
                } else if (supportsEq) {
                    ARBImaging.glBlendEquation(blendEqRGB);
                    record.blendEqRGB = blendEqRGB;
                }
            } else {
                GL11.glDisable(GL11.GL_BLEND);
                record.blendEnabled = false;
            }
        }
    }

    private void applyBlendColor(boolean enabled, BlendStateRecord record) {
        if (record.isValid()) {
            if (enabled) {
                boolean applyConstant = getDestinationFunctionRGB()
                        .usesConstantColor()
                        || getSourceFunctionRGB().usesConstantColor()
                        || (supportsConstantColor() && (getDestinationFunctionAlpha()
                                .usesConstantColor() || getSourceFunctionAlpha()
                                .usesConstantColor()));
                if (applyConstant && supportsConstantColor()) {
                    float r = 0, g = 0, b = 0, a = 0;
                    if (getConstantColor() != null) {
                        r = getConstantColor().r;
                        g = getConstantColor().g;
                        b = getConstantColor().b;
                        a = getConstantColor().a;
                    }
                    if (supportsConstantColor && (record.blendColor.r != r || record.blendColor.g != g
                            || record.blendColor.b != b
                            || record.blendColor.a != a)) {
                        ARBImaging.glBlendColor(r, g, b, a);
                        record.blendColor.set(r, g, b, a);
                    }
                }
            }
        } else {
            if (enabled) {
                boolean applyConstant = getDestinationFunctionRGB()
                        .usesConstantColor()
                        || getSourceFunctionRGB().usesConstantColor()
                        || (supportsConstantColor() && (getDestinationFunctionAlpha()
                                .usesConstantColor() || getSourceFunctionAlpha()
                                .usesConstantColor()));
                if (applyConstant && supportsConstantColor()) {
                    float r = 0, g = 0, b = 0, a = 0;
                    if (getConstantColor() != null) {
                        r = getConstantColor().r;
                        g = getConstantColor().g;
                        b = getConstantColor().b;
                        a = getConstantColor().a;
                    }
                    EXTBlendColor.glBlendColorEXT(r, g, b, a);
                    record.blendColor.set(r, g, b, a);
                }
            }
        }
    }

    private void applyBlendFunctions(boolean enabled, BlendStateRecord record) {
        if (record.isValid()) {
            if (enabled) {
                int glSrcRGB = getGLSrcValue(getSourceFunctionRGB());
                int glDstRGB = getGLDstValue(getDestinationFunctionRGB());
                if (supportsSeparateFunctions()) {
                    int glSrcAlpha = getGLSrcValue(getSourceFunctionAlpha());
                    int glDstAlpha = getGLDstValue(getDestinationFunctionAlpha());
                    if (record.srcFactorRGB != glSrcRGB
                            || record.dstFactorRGB != glDstRGB
                            || record.srcFactorAlpha != glSrcAlpha
                            || record.dstFactorAlpha != glDstAlpha) {
                        EXTBlendFuncSeparate.glBlendFuncSeparateEXT(glSrcRGB,
                                glDstRGB, glSrcAlpha, glDstAlpha);
                        record.srcFactorRGB = glSrcRGB;
                        record.dstFactorRGB = glDstRGB;
                        record.srcFactorAlpha = glSrcAlpha;
                        record.dstFactorAlpha = glDstAlpha;
                    }
                } else if (record.srcFactorRGB != glSrcRGB
                        || record.dstFactorRGB != glDstRGB) {
                    GL11.glBlendFunc(glSrcRGB, glDstRGB);
                    record.srcFactorRGB = glSrcRGB;
                    record.dstFactorRGB = glDstRGB;
                }
            }
        } else {
            if (enabled) {
                int glSrcRGB = getGLSrcValue(getSourceFunctionRGB());
                int glDstRGB = getGLDstValue(getDestinationFunctionRGB());
                if (supportsSeparateFunctions()) {
                    int glSrcAlpha = getGLSrcValue(getSourceFunctionAlpha());
                    int glDstAlpha = getGLDstValue(getDestinationFunctionAlpha());
                    EXTBlendFuncSeparate.glBlendFuncSeparateEXT(glSrcRGB,
                            glDstRGB, glSrcAlpha, glDstAlpha);
                    record.srcFactorRGB = glSrcRGB;
                    record.dstFactorRGB = glDstRGB;
                    record.srcFactorAlpha = glSrcAlpha;
                    record.dstFactorAlpha = glDstAlpha;
                } else {
                    GL11.glBlendFunc(glSrcRGB, glDstRGB);
                    record.srcFactorRGB = glSrcRGB;
                    record.dstFactorRGB = glDstRGB;
                }
            }
        }
    }

    private int getGLSrcValue(SourceFunction function) {
        switch (function) {
            case Zero:
                return GL11.GL_ZERO;
            case DestinationColor:
                return GL11.GL_DST_COLOR;
            case OneMinusDestinationColor:
                return GL11.GL_ONE_MINUS_DST_COLOR;
            case SourceAlpha:
                return GL11.GL_SRC_ALPHA;
            case OneMinusSourceAlpha:
                return GL11.GL_ONE_MINUS_SRC_ALPHA;
            case DestinationAlpha:
                return GL11.GL_DST_ALPHA;
            case OneMinusDestinationAlpha:
                return GL11.GL_ONE_MINUS_DST_ALPHA;
            case SourceAlphaSaturate:
                return GL11.GL_SRC_ALPHA_SATURATE;
            case ConstantColor:
                if (supportsConstantColor())
                    return EXTBlendColor.GL_CONSTANT_COLOR_EXT;
                // FALLS THROUGH
            case OneMinusConstantColor:
                if (supportsConstantColor())
                    return EXTBlendColor.GL_ONE_MINUS_CONSTANT_COLOR_EXT;
                // FALLS THROUGH
            case ConstantAlpha:
                if (supportsConstantColor())
                    return EXTBlendColor.GL_CONSTANT_ALPHA_EXT;
                // FALLS THROUGH
            case OneMinusConstantAlpha:
                if (supportsConstantColor())
                    return EXTBlendColor.GL_ONE_MINUS_CONSTANT_ALPHA_EXT;
                // FALLS THROUGH
            case One:
                return GL11.GL_ONE;
        }
        throw new IllegalArgumentException("Invalid source function type: "
                + function);
    }

    private int getGLDstValue(DestinationFunction function) {
        switch (function) {
            case Zero:
                return GL11.GL_ZERO;
            case SourceColor:
                return GL11.GL_SRC_COLOR;
            case OneMinusSourceColor:
                return GL11.GL_ONE_MINUS_SRC_COLOR;
            case SourceAlpha:
                return GL11.GL_SRC_ALPHA;
            case OneMinusSourceAlpha:
                return GL11.GL_ONE_MINUS_SRC_ALPHA;
            case DestinationAlpha:
                return GL11.GL_DST_ALPHA;
            case OneMinusDestinationAlpha:
                return GL11.GL_ONE_MINUS_DST_ALPHA;
            case ConstantColor:
                if (supportsConstantColor())
                    return EXTBlendColor.GL_CONSTANT_COLOR_EXT;
                // FALLS THROUGH
            case OneMinusConstantColor:
                if (supportsConstantColor())
                    return EXTBlendColor.GL_ONE_MINUS_CONSTANT_COLOR_EXT;
                // FALLS THROUGH
            case ConstantAlpha:
                if (supportsConstantColor())
                    return EXTBlendColor.GL_CONSTANT_ALPHA_EXT;
                // FALLS THROUGH
            case OneMinusConstantAlpha:
                if (supportsConstantColor())
                    return EXTBlendColor.GL_ONE_MINUS_CONSTANT_ALPHA_EXT;
                // FALLS THROUGH
            case One:
                return GL11.GL_ONE;
        }
        throw new IllegalArgumentException(
                "Invalid destination function type: " + function);
    }

    private int getGLEquationValue(BlendEquation eq) {
        switch (eq) {
            case Min:
                if (supportsMinMax)
                    return EXTBlendMinmax.GL_MIN_EXT;
                // FALLS THROUGH
            case Max:
                if (supportsMinMax)
                    return EXTBlendMinmax.GL_MAX_EXT;
                else
                    return ARBImaging.GL_FUNC_ADD;
            case Subtract:
                if (supportsSubtract)
                    return EXTBlendSubtract.GL_FUNC_SUBTRACT_EXT;
                // FALLS THROUGH
            case ReverseSubtract:
                if (supportsSubtract)
                    return EXTBlendSubtract.GL_FUNC_REVERSE_SUBTRACT_EXT;
                // FALLS THROUGH
            case Add:
                return ARBImaging.GL_FUNC_ADD;
        }
        throw new IllegalArgumentException("Invalid blend equation: " + eq);
    }

    private void applyTest(boolean enabled, BlendStateRecord record) {
        if (record.isValid()) {
            if (enabled) {
                if (!record.testEnabled) {
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    record.testEnabled = true;
                }
                int glFunc = getGLFuncValue(getTestFunction());
                if (record.alphaFunc != glFunc
                        || record.alphaRef != getReference()) {
                    GL11.glAlphaFunc(glFunc, getReference());
                    record.alphaFunc = glFunc;
                    record.alphaRef = getReference();
                }
            } else if (record.testEnabled) {
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                record.testEnabled = false;
            }

        } else {
            if (enabled) {
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                record.testEnabled = true;
                int glFunc = getGLFuncValue(getTestFunction());
                GL11.glAlphaFunc(glFunc, getReference());
                record.alphaFunc = glFunc;
                record.alphaRef = getReference();
            } else {
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                record.testEnabled = false;
            }
        }
    }

    private int getGLFuncValue(TestFunction function) {
        switch (function) {
            case Never:
                return GL11.GL_NEVER;
            case LessThan:
                return GL11.GL_LESS;
            case EqualTo:
                return GL11.GL_EQUAL;
            case LessThanOrEqualTo:
                return GL11.GL_LEQUAL;
            case GreaterThan:
                return GL11.GL_GREATER;
            case NotEqualTo:
                return GL11.GL_NOTEQUAL;
            case GreaterThanOrEqualTo:
                return GL11.GL_GEQUAL;
            case Always:
                return GL11.GL_ALWAYS;
        }
        throw new IllegalArgumentException("Invalid test function type: "
                + function);
    }

    @Override
    public BlendStateRecord createStateRecord() {
        return new BlendStateRecord();
    }
}
