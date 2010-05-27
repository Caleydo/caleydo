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

package com.jmex.effects;

import java.io.IOException;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>FlareQuad</code> represents a single light reflection in a LensFlare
 * object.
 * 
 * @author Joshua Slack
 * @version $Id: FlareQuad.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */

public class FlareQuad extends Quad {

    private static final long serialVersionUID = 1L;

    Vector2f positionPercent = new Vector2f(1, 1);

    public FlareQuad() {}
    
    /**
     * Creates a new Quad to act as a single lens flare reflection in a
     * LensFlare. The width and height of the quad are in ortho terms.
     * 
     * @param name
     *            String
     * @param width
     *            float
     * @param height
     *            float
     */
    public FlareQuad(String name, float width, float height) {
        super(name, width, height);
    }

    /**
     * Set the offset of this FlareQuad from the center point of the screen
     * using a ratio where 1.0f (100%) = the position of the light source (or
     * screen position of the worldTranslation of the LensFlare.) A negative
     * value for x or y flips it across the axis from the light position.
     * 
     * @param amountX
     *            float
     * @param amountY
     *            float
     */
    public void setOffset(float amountX, float amountY) {
        positionPercent.x = 1f / amountX;
        positionPercent.y = 1f / amountY;
    }

    /**
     * Updates worldTranslation of this FlareQuad. Called by LensFlare during
     * it's updateWorldData method.
     * 
     * @param flarePoint
     *            Vector3f
     * @param midPoint
     *            Vector2f
     */
    public void updatePosition(Vector3f flarePoint, Vector2f midPoint) {
        Vector3f tempPoint = FlareQuad.this.getWorldTranslation();
        tempPoint.x = (flarePoint.x * positionPercent.x) + midPoint.x;
        tempPoint.y = (flarePoint.y * positionPercent.y) + midPoint.y;
        tempPoint.z = 0;
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(positionPercent, "positionPercent", new Vector2f(1, 1));
        
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        positionPercent = (Vector2f)capsule.readSavable("positionPercent", new Vector2f(1, 1));
        
        
    }
}