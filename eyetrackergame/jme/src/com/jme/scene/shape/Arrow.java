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
// $Id: Arrow.java 4396 2009-06-14 12:01:14Z christoph.luder@gmail.com $
package com.jme.scene.shape;

import java.io.IOException;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * A cylinder with a pyramid at one end.
 * 
 * @author Joshua Slack
 * @version $Revision: 4396 $, $Date: 2009-06-14 14:01:14 +0200 (So, 14 Jun 2009) $
 */
public class Arrow extends Node {

    private static final long serialVersionUID = 1L;

    private float length = 1;
    private float width = .25f;

    private transient Cylinder shaft;
    private transient Pyramid tip;

    public Arrow() {
        this(null, 1.0f, 0.25f);
    }

    public Arrow(String name) {
        this(name, 1.0f, 0.25f);
    }

    public Arrow(String name, float length, float width) {
        super(name);
        updateGeometry(length, width);
    }

    public float getLength() {
        return length;
    }

    public float getWidth() {
        return width;
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        length = capsule.readFloat("length", 1);
        width = capsule.readFloat("width", .25f);
        updateGeometry(length, width);
    }

    public void setDefaultColor(ColorRGBA color) {
        for (int x = 0; x < getQuantity(); x++) {
            if (getChild(x) instanceof Geometry) {
                ((Geometry) getChild(x)).setDefaultColor(color);
            }
        }
    }

    /**
     * @deprecated use {@link #updateGeometry(float, float)}.
     */
    public void setLength(float length) {
        this.length = length;
    }

    public void setSolidColor(ColorRGBA color) {
        if (shaft != null) {
            shaft.setSolidColor(color);
            tip.setSolidColor(color);
        }
    }

    /**
     * @deprecated use {@link #updateGeometry(float, float)}.
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Rebuild this arrow based on a new set of parameters.
     * 
     * @param length
     * @param width
     */
    public void updateGeometry(float length, float width) {
        this.length = length;
        this.width = width;
        if (shaft == null) {
            shaft = new Cylinder("base", 4, 16, width * .75f, length);
            Quaternion q = new Quaternion();
            q.fromAngles(90 * FastMath.DEG_TO_RAD, 0, 0);
            shaft.rotatePoints(q);
            shaft.rotateNormals(q);
            attachChild(shaft);
            tip = new Pyramid("tip", 2 * width, length / 2f);
            tip.translatePoints(0, length * .75f, 0);
            attachChild(tip);
        } else {
            shaft.updateGeometry(4, 16, width * .75f, width * .75f, length,
                    false, false);
            Quaternion q = new Quaternion();
            q.fromAngles(90 * FastMath.DEG_TO_RAD, 0, 0);
            shaft.rotatePoints(q);
            shaft.rotateNormals(q);
            tip.updateGeometry(2 * width, length / 2f);
            tip.translatePoints(0, length * .75f, 0);
        }
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(length, "length", 1);
        capsule.write(width, "width", .25f);
    }

}
