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
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>StippleState</code> maintains a ByteBuffer containing the stipple mask
 * which is applied by <code>glPolygonStipple(mask)</code>.<br>
 * The ByteBuffer needs to be 1024 Bytes big (32x32).<br>
 * 
 * @author Christoph Luder
 */
public abstract class StippleState extends RenderState {
    private static final Logger log = Logger.getLogger(StippleState.class.getName());
    /**
     * The ByteBuffer containing the stipple pattern.
     */
    private ByteBuffer stippleMask;

    /**
     * <code>getStateType</code> returns {@link RenderState.StateType#Stipple}
     * @return {@link RenderState.StateType#Stipple}
     * @see RenderState#getStateType()
     */
    @Override
    public StateType getStateType() {
        return RenderState.StateType.Stipple;
    }

    @Deprecated
    @Override
    public int getType() {
        log.severe("getType() is deprecated and not supported by Stipplestate,"
                + "use getStateType()");
        return 0;
    }

    /**
     * Returns the ByteBuffer containing the stipple mask.
     * @return the stipple mask
     */
    public ByteBuffer getStippleMask() {
        return stippleMask;
    }

    /**
     * Sets the stipple mask to be used.<br>
     * The ByteBuffer needs to be 1024 byte big.
     * 
     * @param stippleMask
     *            ByteBuffer containing the stipple mask.
     */
    public void setStippleMask(ByteBuffer stippleMask) {
        this.stippleMask = stippleMask;
        setNeedsRefresh(true);
    }

    @Override
    public Class<?> getClassTag() {
        return StippleState.class;
    }

    @Override
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(stippleMask, "stippleMask", null);
    }

    @Override
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        stippleMask = capsule.readByteBuffer("stippleMask", stippleMask);
    }
}
