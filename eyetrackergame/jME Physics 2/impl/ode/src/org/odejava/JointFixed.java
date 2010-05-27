/*
 * Open Dynamics Engine for Java (odejava) Copyright (c) 2004, Jani Laakso, All
 * rights reserved.
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

import org.odejava.ode.Ode;

/**
 * A joint that maintains a fixed relationship between the two bodies. This
 * joint type should only be used for debugging as two bodies being glued
 * together with this joint would be more effectively represented as a single
 * body.
 * <p/>
 * Created 20.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public class JointFixed extends Joint {

    /**
     * Create a new Fixed joint that belongs to the given world. The JointGroup
     * and name parameters are optional. If no group is provided, the joint is
     * attached to the global environment.
     *
     * @param name       A label string to associate with this joint
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointFixed( String name, World world, JointGroup jointGroup ) {
        super( name, jointGroup );
        jointId = Ode.dJointCreateFixed( world.getId(), jointGroupId );
    }

    /**
     * Create a new fixed joint that belongs to the given world. The JointGroup
     * is optional and the name is set to the null string. If no group is
     * provided, the joint is attached to the global environment.
     *
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointFixed( World world, JointGroup jointGroup ) {
        this( null, world, jointGroup );
    }

    /**
     * Create a new fixed joint that belongs to the given world and has that
     * name. This will not put the joint in any JointGroup. For this, use any
     * other constructor.
     *
     * @param name,  the name of the joint
     * @param world, the world to which this joint belongs to
     */
    public JointFixed( String name, World world ) {
        this( name, world, null );
    }

    /**
     * Once the two bodies are attached and located in their desired positions,
     * call this method to have ODE to remember these value of position and
     * orientation.
     */
    public void setFixed() {
        Ode.dJointSetFixed( jointId );
    }

    public void setParam( int parameter, float value ) {
        Ode.dJointSetFixedParam( jointId, parameter, value );
    }

    public void setAnchor( float x, float y, float z ) {
        // nothing to do
    }
}
