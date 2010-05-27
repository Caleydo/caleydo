/*
 * Open Dynamics Engine for Java (odejava) Copyright (c) 2004, Odejava Project
 * Group, All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the odejava nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.odejava;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;


/**
 * Defines a common interface which several ode objects that deal with
 * transforms such as Body and Geom can implement.
 *
 * @author William Denniss
 */
public interface Placeable {

    /**
     * Sets the position of this transformable
     *
     * @param position to set
     */
    public void setPosition( Vector3f position );

    /**
     * Returns the current position.
     *
     * @return the current position.
     */
    public Vector3f getPosition();

    /**
     * Returns the current position using the provided Vector3f
     *
     * @param result The result Vector3f
     * @return the current position
     */
    public Vector3f getPosition( Vector3f result );

    /**
     * Sets the quaternion.
     *
     * @param quaternion the quaternion
     */
    public void setQuaternion( Quaternion quaternion );

    /**
     * Returns the quaternion.
     *
     * @return the quaternion.
     */
    public Quaternion getQuaternion();

    /**
     * Returns the quaternion using the provided Quad4f
     *
     * @param result the result Quad4f.
     * @return the quaternion.
     */
    public Quaternion getQuaternion( Quaternion result );

    /**
     * Returns the name of the Odejava Transformable.
     *
     * @return the name of the Odejava Transformable.
     */
    public String getName();

    /**
     * Returns true if this Placeable is dynamic and false if it is static.
     * Dynamic Placeable's have changing transform's whereas static ones
     * are fixed.
     *
     * @return rue if this Placeable is dynamic and false if it is static
     */
    public boolean fixed();
}
