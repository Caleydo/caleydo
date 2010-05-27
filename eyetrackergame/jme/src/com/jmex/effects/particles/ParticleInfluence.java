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

package com.jmex.effects.particles;

import java.io.IOException;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>ParticleInfluence</code> is an abstract class defining an external
 * influence to be used with the ParticleMesh class.
 * 
 * @author Joshua Slack
 * @version $Id: ParticleInfluence.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public abstract class ParticleInfluence implements Savable {

    /**
     * Is this influence enabled? ie, should it be used when updating particles.
     */
    private boolean enabled = true;

    /**
     * Set this influence enabled or not.
     * @param enabled boolean
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Return whether or not this influence is enabled.
     * @return boolean
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gives the influence a chance to perform any necessary initialization
     * immediately before {@link #apply} is called on each particle for the
     * current frame.
     * @param particleGeom the particle system containing the influence
     */
    public void prepare(ParticleSystem particleGeom) {
    }
    
    /**
     * Apply the influence defined by this class on a given particle. Should
     * probably do this by making a call to
     * <i>particle.getSpeed().addLocal(....);</i> etc.
     * 
     * @param dt
     *            amount of time since last apply call in ms.
     * @param particle
     *            the particle to apply the influence to.
     * @param index
     *            the index of the particle we are working with. This is useful
     *            for adding small steady amounts of variation, or remembering information.
     */
    public abstract void apply(float dt, Particle particle, int index);
    
    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(enabled, "enabled", true);
    }
    
    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        enabled = capsule.readBoolean("enabled", true);
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}
