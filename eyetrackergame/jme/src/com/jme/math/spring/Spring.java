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

package com.jme.math.spring;

import java.io.IOException;

import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>Spring</code> defines a single spring connecting two SpringNodes
 * in a SpringSystem.
 *
 * @author Joshua Slack
 * @version $Id: Spring.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class Spring implements Savable {

	/** First node connected by this Spring. */
	public SpringPoint node1;
	/** Second node connected by this Spring. */
	public SpringPoint node2;
	/** Rest length of this Spring. */
	private float restLength = 1;
	/** The squared rest length of this spring */
	private float rlSquared = 1;
	/** The total mass of this spring */
	private float tMass = 1;
	/** Private vector used by Spring in update() method to avoid object creation. */
	private Vector3f delta = new Vector3f();

	/**
	 * Public constructor.
	 * @param node1 SpringNode
	 * @param node2 SpringNode
	 * @param restLength float
	 */
	public Spring(SpringPoint node1, SpringPoint node2, float restLength) {
		this.node1 = node1;
		this.node2 = node2;
		setRestLength(restLength);
		updateTotalMassFromNodes();
	}

	/**
	 * Set the rest length of this Spring.  Also, calculates and sets the
	 * squared rest length field.
	 *
	 * @param restLength float
	 */
	public void setRestLength(float restLength) {
		this.restLength = restLength;
		this.rlSquared = restLength * restLength;
	}

	/**
	 * Return the rest length of this Spring.
	 *
	 * @return float
	 */
	public float getRestLength() {
		return restLength;
	}

	/**
	 * Computes the spring collective mass from the node using inverted masses
	 * for stability
	 */
	public void updateTotalMassFromNodes() {
		tMass = 1f / (node1.invMass+node2.invMass);
	}

	/**
	 * Updates the positions of the nodes connected by this spring based on spring
	 * force calculations.  Relaxation method idea came from paper on physics system
	 * of Hitman game.
	 */
	public void update() {
        if(node1 == null || node2 == null) {
            return;
        }
		delta.set(node2.position).subtractLocal(node1.position);
		delta.multLocal(tMass-(2*rlSquared*tMass/(delta.lengthSquared()+rlSquared)));
        if (node1.invMass != 0)
    		node1.position.addLocal(
    				delta.x * node1.invMass,
    				delta.y * node1.invMass,
    				delta.z * node1.invMass);
        
        if (node2.invMass != 0)
    		node2.position.subtractLocal(
    				delta.x * node2.invMass,
    				delta.y * node2.invMass,
    				delta.z * node2.invMass);
	}

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(node1, "node1", null);
        capsule.write(node2, "node2", null);
        capsule.write(restLength, "restLength", 0);
        capsule.write(tMass, "tMass", 0);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        node1 = (SpringPoint)capsule.readSavable("node1", null);
        node2 = (SpringPoint)capsule.readSavable("node2", null);
        restLength = capsule.readFloat("restLength", 0);
        rlSquared = restLength * restLength;
        tMass = capsule.readFloat("tMass", 0);
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}
