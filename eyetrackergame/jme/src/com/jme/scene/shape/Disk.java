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
// $Id: Disk.java 4770 2009-12-05 23:07:37Z ahmadabdolkader $
package com.jme.scene.shape;

import java.io.IOException;

import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * A flat sector of a discus, defined by it's radius and central angle.
 * 
 * @author Mark Powell
 * @author Ahmed Abdelkader (added the central angle parameter)
 * @version $Revision: 4770 $, $Date: 2009-12-06 00:07:37 +0100 (So, 06 Dez 2009) $
 */
public class Disk extends TriMesh {

    private static final long serialVersionUID = 1L;
    
    private float centralAngle;

    private int shellSamples;

    private int radialSamples;

    private float radius;

    public Disk() {
    }

    public Disk(String name, int shellSamples, int radialSamples, float radius) {
        this(name, FastMath.TWO_PI, shellSamples, radialSamples, radius);
    }

    /**
     * Creates a flat sector of a disk (circle) at the origin flat along the Z.
     * Usually, a higher sample number creates a better looking cylinder, but
     * at the cost of more vertex information.
     * 
     * @param name
     *            The name of the disk.
     * @param shellSamples
     *            The number of shell samples.
     * @param radialSamples
     *            The number of radial samples.
     * @param radius
     *            The radius of the disk.
     * @param centralAngle
     *            The central angle of the sector.
     */ 
    public Disk(String name, float centralAngle, int shellSamples, int radialSamples, float radius) {
        super(name);
        updateGeometry(centralAngle, shellSamples, radialSamples, radius);
    }
    
    public float getCentralAngle() {
    	return centralAngle;
    }

    public int getRadialSamples() {
        return radialSamples;
    }

    public float getRadius() {
        return radius;
    }

    public int getShellSamples() {
        return shellSamples;
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        shellSamples = capsule.readInt("shellSamples", 0);
        radialSamples = capsule.readInt("radialSamples", 0);
        radius = capsule.readFloat("raidus", 0);
        centralAngle = capsule.readFloat("centralAngle", FastMath.TWO_PI);
    }

    /**
     * Rebuild this disk based on a new set of parameters.
     * 
     * @param shellSamples the number of shell samples.
     * @param radialSamples the number of radial samples.
     * @param radius the radius of the disk.
     */
    public void updateGeometry(float centralAngle, int shellSamples, int radialSamples, float radius) {
        this.centralAngle = centralAngle = FastMath.normalize(centralAngle, -FastMath.TWO_PI, FastMath.TWO_PI);
        this.shellSamples = shellSamples;
        this.radialSamples = radialSamples;
        this.radius = radius;
        int shellLess = shellSamples - 1;
        // Allocate vertices
        setVertexCount(1 + (radialSamples+1) * shellLess);
        setVertexBuffer(BufferUtils.createVector3Buffer(getVertexCount()));
        setNormalBuffer(BufferUtils.createVector3Buffer(getVertexCount()));
        getTextureCoords().set(0, new TexCoords(BufferUtils.createVector3Buffer(getVertexCount())));
        setTriangleQuantity(radialSamples * (2 * shellLess - 1));
        setIndexBuffer(BufferUtils.createIntBuffer(3 * getTriangleCount()));
        
        // generate geometry
        // center of disk
        getVertexBuffer().put(0).put(0).put(0);
        
        for (int x = 0; x < getVertexCount(); x++) {
            getNormalBuffer().put(0).put(0).put(1);
        }
        
        getTextureCoords().get(0).coords.put(.5f).put(.5f);
        float inverseShellLess = 1.0f / shellLess;
        float inverseRadial = 1.0f / radialSamples;
        Vector3f radialFraction = new Vector3f();
        Vector2f texCoord = new Vector2f();
        for (int radialCount = 0; radialCount <= radialSamples; radialCount++) {
            float angle = centralAngle * inverseRadial * radialCount;
            float cos = FastMath.cos(angle);
            float sin = FastMath.sin(angle);
            Vector3f radial = new Vector3f(cos, sin, 0);
        
            for (int shellCount = 1; shellCount < shellSamples; shellCount++) {
                float fraction = inverseShellLess * shellCount; // in (0,R]
                radialFraction.set(radial).multLocal(fraction);
                int i = shellCount + shellLess * radialCount;
                texCoord.x = 0.5f * (1.0f + radialFraction.x);
                texCoord.y = 0.5f * (1.0f + radialFraction.y);
                BufferUtils.setInBuffer(texCoord, getTextureCoords().get(0).coords, i);
                radialFraction.multLocal(radius);
                BufferUtils.setInBuffer(radialFraction, getVertexBuffer(), i);
            }
        }
        
        // Generate connectivity
        int index = 0;
        for (int radialCount0 = 0, radialCount1 = 1; radialCount1 <= radialSamples; radialCount0 = radialCount1++) {
            getIndexBuffer().put(0);
            getIndexBuffer().put(1 + shellLess * radialCount0);
            getIndexBuffer().put(1 + shellLess * radialCount1);
            index += 3;
            for (int iS = 1; iS < shellLess; iS++, index += 6) {
                int i00 = iS + shellLess * radialCount0;
                int i01 = iS + shellLess * radialCount1;
                int i10 = i00 + 1;
                int i11 = i01 + 1;
                getIndexBuffer().put(i00);
                getIndexBuffer().put(i10);
                getIndexBuffer().put(i11);
                getIndexBuffer().put(i00);
                getIndexBuffer().put(i11);
                getIndexBuffer().put(i01);
            }
        }
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(shellSamples, "shellSamples", 0);
        capsule.write(radialSamples, "radialSamples", 0);
        capsule.write(radius, "radius", 0);
        capsule.write(centralAngle, "centralAngle", FastMath.TWO_PI);
    }

}