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

package com.jme.light;

import java.io.IOException;

import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>DirectionalLight</code> defines a light that is assumed to be
 * infinitely far away (something similar to the sun). This means the direction
 * of the light rays are all parallel. The direction the light is coming from
 * is defined by the class.
 * @author Mark Powell
 * @version $Id: DirectionalLight.java 4634 2009-08-27 23:03:38Z skye.book $
 */
public class DirectionalLight extends Light {
    private static final long serialVersionUID = 1L;
	//direction the light is coming from.
    private Vector3f direction;

    /**
     * Constructor instantiates a new <code>DirectionalLight</code> object.
     * The initial light colors are white and the direction the light emits
     * from is (0,0,0).
     *
     */
    public DirectionalLight() {
        super();
        direction = new Vector3f();
    }

    /**
     * <code>getDirection</code> returns the direction the light is
     * emitting from.
     * @return the direction the light is emitting from.
     */
    public Vector3f getDirection() {
        return direction;
    }

    /**
     * <code>setDirection</code> sets the direction the light is emitting from.
     * @param direction the direction the light is emitting from.
     */
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    /**
     * <code>getType</code> returns this light's type (Type.Directional).
     * @see com.jme.light.Light#getType()
     */
    public Type getType() {
        return Type.Directional;
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(direction, "direction", Vector3f.ZERO);
       
    }
    
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        direction = (Vector3f)capsule.readSavable("direction", Vector3f.ZERO.clone());
        
    }

}
