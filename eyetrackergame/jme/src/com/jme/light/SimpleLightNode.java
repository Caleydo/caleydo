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

import com.jme.math.Quaternion;
import com.jme.scene.Node;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * Started Date: Jul 21, 2004<br><br>
 *
 * <code>SimpleLightNode</code> defines a scene node that contains and maintains a
 * light object. A light node contains a single light, and positions the light
 * based on it's translation vector. If the contained light is a spot light, the
 * rotation of the node determines it's direction. If the contained light is a
 * Directional light rotation determines it's direction. It has no concept of
 * location.
 *
 * @author Mark Powell
 * @author Jack Lindamood
 */
public class SimpleLightNode extends Node{
    private static final long serialVersionUID = 1L;
	private Light light;
    private Quaternion lightRotate;
    
    public SimpleLightNode() { }
    
    /**
     * Constructor creates a new <code>LightState</code> object. The light
     * state the node controls is required at construction time.
     *
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparison purposes.
     * @param light The light to be moved around by this Node
     */
    public SimpleLightNode(String name,Light light) {
        super(name);
        this.light=light;
    }

    /**
     * @return the light that was specified in the constructor
     * @see #SimpleLightNode(String, Light)
     */
    public Light getLight() {
        return light;
    }

    /**
     * <code>updateWorldData</code> modifies the light data based on any
     * change the light node has made.
     *
     * @param time
     *            the time between frames.
     */
    public void updateWorldData(float time) {
        super.updateWorldData(time);
        if(light == null) {
            return;
        }
        lightRotate = worldRotation.mult(localRotation, lightRotate);

        switch (light.getType()) {
        case Directional:
            {
                DirectionalLight dLight = (DirectionalLight) light;
                dLight.setDirection(lightRotate.getRotationColumn(2,
                        dLight.getDirection()));
                break;
            }

        case Point:
            {
                PointLight pLight = (PointLight) light;
                pLight.setLocation(worldTranslation);
                break;
            }

        case Spot:
            {
                SpotLight sLight = (SpotLight) light;
                sLight.setLocation(worldTranslation);
                sLight.setDirection(lightRotate.getRotationColumn(2, sLight
                        .getDirection()));
                break;
            }

        default:
            break;
        }

    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(light, "light", null);
        capsule.write(lightRotate, "lightRotate", new Quaternion());
       
    }
    
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        light = (Light)capsule.readSavable("light", null);
        lightRotate = (Quaternion)capsule.readSavable("lightRotate", new Quaternion());
    }
}