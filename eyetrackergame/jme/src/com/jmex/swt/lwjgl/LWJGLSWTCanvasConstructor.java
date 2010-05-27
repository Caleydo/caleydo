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

package com.jmex.swt.lwjgl;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.lwjgl.LWJGLException;

import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.system.canvas.CanvasConstructor;
import com.jme.system.canvas.JMECanvas;

public class LWJGLSWTCanvasConstructor implements CanvasConstructor {

    public JMECanvas makeCanvas(HashMap<String, Object> props) {
        try {
            Composite parent = (Composite) props.get(LWJGLSWTConstants.PARENT);

            GLData data = new GLData();
            data.doubleBuffer = true;

            Integer style = (Integer) props.get(LWJGLSWTConstants.STYLE);
            if (style == null) {
                style = SWT.NONE;
            }

            Integer depthBits = (Integer) props
                    .get(LWJGLSWTConstants.DEPTH_BITS);
            if (depthBits != null) {
                data.depthSize = depthBits;
            } else {
                data.depthSize = DisplaySystem.getDisplaySystem()
                        .getMinDepthBits();
            }

            Integer alphaBits = (Integer) props
                    .get(LWJGLSWTConstants.ALPHA_BITS);
            if (alphaBits != null) {
                data.alphaSize = alphaBits;
            } else {
                data.alphaSize = DisplaySystem.getDisplaySystem()
                        .getMinAlphaBits();
            }

            Integer stencilBits = (Integer) props
                    .get(LWJGLSWTConstants.STENCIL_BITS);
            if (stencilBits != null) {
                data.stencilSize = stencilBits;
            } else {
                data.stencilSize = DisplaySystem.getDisplaySystem()
                        .getMinStencilBits();
            }

            // NOTE: SWT does not actually implement samples yet.
            Integer samples = (Integer) props.get(LWJGLSWTConstants.SAMPLES);
            if (samples != null) {
                data.samples = samples;
            } else {
                data.samples = DisplaySystem.getDisplaySystem()
                        .getMinStencilBits();
            }

            final LWJGLSWTCanvas canvas = new LWJGLSWTCanvas(parent, style,
                    data);

            canvas.addPaintListener(new PaintListener() {
                public void paintControl(PaintEvent e) {
                    canvas.makeDirty();
                }
            });

            return canvas;
        } catch (LWJGLException e) {
            e.printStackTrace();
            throw new JmeException("Unable to create lwjgl-swt canvas: "
                    + e.getLocalizedMessage());
        }
    }
}
