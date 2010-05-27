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
import com.jme.scene.state.BlendState;
import com.jme.scene.state.jogl.records.BlendStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>JOGLBlendState</code> subclasses the BlendState using the JOGL API
 * to set OpenGL's blending state params.
 *
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @author Steve Vaughan - JOGL port
 * @version $Id: JOGLBlendState.java 4458 2009-07-02 08:39:10Z julien.gouesse $
 */
public class JOGLBlendState extends BlendState {
    private static final long serialVersionUID = 1L;

    private static boolean inited = false;

    
    public JOGLBlendState() {
        this( ( ( JOGLRenderer ) DisplaySystem.getDisplaySystem()
        .getRenderer()).getContextCapabilities() );
    }
    
    /**
     * Constructor instantiates a new <code>JOGLBlendState</code> object with
     * default values.
     */
    public JOGLBlendState(JOGLContextCapabilities caps) {
        super();

        if (!inited) {
            supportsConstantColor = supportsEq = caps.GL_ARB_imaging;
            supportsSeparateFunc = caps.GL_EXT_blend_func_separate;
            supportsSeparateEq = caps.GL_EXT_blend_equation_separate;
            supportsMinMax = caps.GL_EXT_blend_minmax;
            supportsSubtract = caps.GL_EXT_blend_subtract;

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
        RenderContext<?> context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
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
        final GL gl = GLU.getCurrentGL();

        if (record.isValid()) {
            if (enabled) {
                if (!record.blendEnabled) {
                    gl.glEnable(GL.GL_BLEND);
                    record.blendEnabled = true;
                }
                int blendEqRGB = getGLEquationValue(getBlendEquationRGB());
                if (supportsSeparateEquations()) {
                    int blendEqAlpha = getGLEquationValue(getBlendEquationAlpha());
                    if (record.blendEqRGB != blendEqRGB
                            || record.blendEqAlpha != blendEqAlpha) {
                        gl.glBlendEquationSeparateEXT(
                                blendEqRGB, blendEqAlpha);
                        record.blendEqRGB = blendEqRGB;
                        record.blendEqAlpha = blendEqAlpha;
                    }
                } else if (supportsEq) {
                    if (record.blendEqRGB != blendEqRGB) {
                        gl.glBlendEquation(blendEqRGB);
                        record.blendEqRGB = blendEqRGB;
                    }
                }
            } else if (record.blendEnabled) {
                gl.glDisable(GL.GL_BLEND);
                record.blendEnabled = false;
            }

        } else {
            if (enabled) {
                gl.glEnable(GL.GL_BLEND);
                record.blendEnabled = true;
                int blendEqRGB = getGLEquationValue(getBlendEquationRGB());
                if (supportsSeparateEquations()) {
                    int blendEqAlpha = getGLEquationValue(getBlendEquationAlpha());
                    gl.glBlendEquationSeparateEXT(
                            blendEqRGB, blendEqAlpha);
                    record.blendEqRGB = blendEqRGB;
                    record.blendEqAlpha = blendEqAlpha;
                } else if (supportsEq) {
                    gl.glBlendEquation(blendEqRGB);
                    record.blendEqRGB = blendEqRGB;
                }
            } else {
                gl.glDisable(GL.GL_BLEND);
                record.blendEnabled = false;
            }
        }
    }

    private void applyBlendColor(boolean enabled, BlendStateRecord record) {
        final GL gl = GLU.getCurrentGL();

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
                        gl.glBlendColor(r, g, b, a);
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
                    gl.glBlendColor(r, g, b, a);
                    record.blendColor.set(r, g, b, a);
                }
            }
        }
    }

    private void applyBlendFunctions(boolean enabled, BlendStateRecord record) {
        final GL gl = GLU.getCurrentGL();

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
                        gl.glBlendFuncSeparateEXT(glSrcRGB,
                                glDstRGB, glSrcAlpha, glDstAlpha);
                        record.srcFactorRGB = glSrcRGB;
                        record.dstFactorRGB = glDstRGB;
                        record.srcFactorAlpha = glSrcAlpha;
                        record.dstFactorAlpha = glDstAlpha;
                    }
                } else if (record.srcFactorRGB != glSrcRGB
                        || record.dstFactorRGB != glDstRGB) {
                    gl.glBlendFunc(glSrcRGB, glDstRGB);
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
                    gl.glBlendFuncSeparateEXT(glSrcRGB,
                            glDstRGB, glSrcAlpha, glDstAlpha);
                    record.srcFactorRGB = glSrcRGB;
                    record.dstFactorRGB = glDstRGB;
                    record.srcFactorAlpha = glSrcAlpha;
                    record.dstFactorAlpha = glDstAlpha;
                } else {
                    gl.glBlendFunc(glSrcRGB, glDstRGB);
                    record.srcFactorRGB = glSrcRGB;
                    record.dstFactorRGB = glDstRGB;
                }
            }
        }
    }

    private int getGLSrcValue(SourceFunction function) {
        switch (function) {
            case Zero:
                return GL.GL_ZERO;
            case DestinationColor:
                return GL.GL_DST_COLOR;
            case OneMinusDestinationColor:
                return GL.GL_ONE_MINUS_DST_COLOR;
            case SourceAlpha:
                return GL.GL_SRC_ALPHA;
            case OneMinusSourceAlpha:
                return GL.GL_ONE_MINUS_SRC_ALPHA;
            case DestinationAlpha:
                return GL.GL_DST_ALPHA;
            case OneMinusDestinationAlpha:
                return GL.GL_ONE_MINUS_DST_ALPHA;
            case SourceAlphaSaturate:
                return GL.GL_SRC_ALPHA_SATURATE;
            case ConstantColor:
                if (supportsConstantColor())
                    return GL.GL_CONSTANT_COLOR;
                // FALLS THROUGH
            case OneMinusConstantColor:
                if (supportsConstantColor())
                    return GL.GL_ONE_MINUS_CONSTANT_COLOR;
                // FALLS THROUGH
            case ConstantAlpha:
                if (supportsConstantColor())
                    return GL.GL_CONSTANT_ALPHA;
                // FALLS THROUGH
            case OneMinusConstantAlpha:
                if (supportsConstantColor())
                    return GL.GL_ONE_MINUS_CONSTANT_ALPHA;
                // FALLS THROUGH
            case One:
                return GL.GL_ONE;
        }
        throw new IllegalArgumentException("Invalid source function type: "
                + function);
    }

    private int getGLDstValue(DestinationFunction function) {
        switch (function) {
            case Zero:
                return GL.GL_ZERO;
            case SourceColor:
                return GL.GL_SRC_COLOR;
            case OneMinusSourceColor:
                return GL.GL_ONE_MINUS_SRC_COLOR;
            case SourceAlpha:
                return GL.GL_SRC_ALPHA;
            case OneMinusSourceAlpha:
                return GL.GL_ONE_MINUS_SRC_ALPHA;
            case DestinationAlpha:
                return GL.GL_DST_ALPHA;
            case OneMinusDestinationAlpha:
                return GL.GL_ONE_MINUS_DST_ALPHA;
            case ConstantColor:
                if (supportsConstantColor())
                    return GL.GL_CONSTANT_COLOR;
                // FALLS THROUGH
            case OneMinusConstantColor:
                if (supportsConstantColor())
                    return GL.GL_ONE_MINUS_CONSTANT_COLOR;
                // FALLS THROUGH
            case ConstantAlpha:
                if (supportsConstantColor())
                    return GL.GL_CONSTANT_ALPHA;
                // FALLS THROUGH
            case OneMinusConstantAlpha:
                if (supportsConstantColor())
                    return GL.GL_ONE_MINUS_CONSTANT_ALPHA;
                // FALLS THROUGH
            case One:
                return GL.GL_ONE;
        }
        throw new IllegalArgumentException(
                "Invalid destination function type: " + function);
    }

    private int getGLEquationValue(BlendEquation eq) {
        switch (eq) {
            case Min:
                if (supportsMinMax)
                    return GL.GL_MIN;
                // FALLS THROUGH
            case Max:
                if (supportsMinMax)
                    return GL.GL_MAX;
                else
                    return GL.GL_FUNC_ADD;
            case Subtract:
                if (supportsSubtract)
                    return GL.GL_FUNC_SUBTRACT;
                // FALLS THROUGH
            case ReverseSubtract:
                if (supportsSubtract)
                    return GL.GL_FUNC_REVERSE_SUBTRACT;
                // FALLS THROUGH
            case Add:
                return GL.GL_FUNC_ADD;
        }
        throw new IllegalArgumentException("Invalid blend equation: " + eq);
    }

    private void applyTest(boolean enabled, BlendStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        if (record.isValid()) {
            if (enabled) {
                if (!record.testEnabled) {
                    gl.glEnable(GL.GL_ALPHA_TEST);
                    record.testEnabled = true;
                }
                int glFunc = getGLFuncValue(getTestFunction());
                if (record.alphaFunc != glFunc
                        || record.alphaRef != getReference()) {
                    gl.glAlphaFunc(glFunc, getReference());
                    record.alphaFunc = glFunc;
                    record.alphaRef = getReference();
                }
            } else if (record.testEnabled) {
                gl.glDisable(GL.GL_ALPHA_TEST);
                record.testEnabled = false;
            }

        } else {
            if (enabled) {
                gl.glEnable(GL.GL_ALPHA_TEST);
                record.testEnabled = true;
                int glFunc = getGLFuncValue(getTestFunction());
                gl.glAlphaFunc(glFunc, getReference());
                record.alphaFunc = glFunc;
                record.alphaRef = getReference();
            } else {
                gl.glDisable(GL.GL_ALPHA_TEST);
                record.testEnabled = false;
            }
        }
    }

    private int getGLFuncValue(TestFunction function) {
        switch (function) {
            case Never:
                return GL.GL_NEVER;
            case LessThan:
                return GL.GL_LESS;
            case EqualTo:
                return GL.GL_EQUAL;
            case LessThanOrEqualTo:
                return GL.GL_LEQUAL;
            case GreaterThan:
                return GL.GL_GREATER;
            case NotEqualTo:
                return GL.GL_NOTEQUAL;
            case GreaterThanOrEqualTo:
                return GL.GL_GEQUAL;
            case Always:
                return GL.GL_ALWAYS;
        }
        throw new IllegalArgumentException("Invalid test function type: "
                + function);
    }

    @Override
    public BlendStateRecord createStateRecord() {
        return new BlendStateRecord();
    }
}
