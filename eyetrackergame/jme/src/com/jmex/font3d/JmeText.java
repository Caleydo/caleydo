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
package com.jmex.font3d;

import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * This is the interface for some peace of text in JME. The implementation, and
 * hence the rendering of the text depends on the kind of text (3D, 2D, etc.).
 * 
 * It is good practice to create an implementation of {@link TextFactory} and then
 * have that create instances of {@link JmeText}.
 *  
 * @author emanuel
 */
public interface JmeText {
    /**
     * @return the factory where this text was created, or null if it does not
     *         know.
     */
    TextFactory getFactory();

    /**
     * @return the string of text that this object is visualizing.
     */
    StringBuffer getText();

    /**
     * Sets the string of text that this object is visualizing, the geometry of
     * the object should change to reflect the change.
     * 
     * @param text
     */
    void setText(String text);

    /**
     * Append text to the string of text that this object is visualizing.
     * 
     * @param text
     */
    void appendText(String text);

    /**
     * @return the flags that were given when this text was created.
     */
    int getFlags();

    /**
     * @return the size of the text (normally size 12 refers to 12pt, in jme I
     *         guess it refers to jme-units).
     */
    float getSize();

    /**
     * change the size of the font, this will most likely be implemented with
     * scaling, so watch out when using this and setLocalScale(...).
     * 
     * @param size
     */
    void setSize(float size);

    // For compatability with Spatial/Geometry
    void setLocalRotation(Matrix3f rotation);

    void setLocalRotation(Quaternion quaternion);

    void setLocalScale(float localScale);

    void setLocalScale(Vector3f trans);

    void setLocalTranslation(Vector3f trans);
}
