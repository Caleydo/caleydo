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
import com.jme.scene.state.ColorMaskState;
import com.jme.scene.state.jogl.records.ColorMaskStateRecord;
import com.jme.system.DisplaySystem;

/**
 *
 * <code>JOGLColorMaskState</code>
 * @author Mike Talbot
 * @author Joshua Slack - reworked for StateRecords.
 * @author Steve Vaughan - JOGL port
 * @version $Id: JOGLColorMaskState.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class JOGLColorMaskState extends ColorMaskState {
    private static final long serialVersionUID = 1L;

    public void apply() {
        final GL gl = GLU.getCurrentGL();

        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        ColorMaskStateRecord record = (ColorMaskStateRecord) context.getStateRecord(StateType.ColorMask);
        context.currentStates[StateType.ColorMask.ordinal()] = this;

        if (isEnabled()) {
            if (!record.isValid() || !record.is(red, green, blue, alpha)) {
                gl.glColorMask(red, green, blue, alpha);
                record.set(red, green, blue, alpha);
            }
        } else if (!record.isValid() || !record.is(true, true, true, true)) {
            gl.glColorMask(true, true, true, true);
            record.set(true, true, true, true);
        }

        if (!record.isValid())
            record.validate();
    }

    @Override
    public ColorMaskStateRecord createStateRecord() {
        return new ColorMaskStateRecord();
    }
}
