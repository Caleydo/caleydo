/*
 * Copyright (c) 2005-2008 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
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
package com.jmex.physics.impl.ode;

import com.jmex.physics.CollisionGroup;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import org.odejava.HashSpace;
import org.odejava.Space;

public class OdeCollisionGroup extends CollisionGroup {
    private Space odeSpace = new HashSpace();

    public OdeCollisionGroup( PhysicsSpace space, String name ) {
        super( space, name );
    }

    public Space getOdeSpace() {
        return odeSpace;
    }
    
    private int index;

    void setIndex( int index ) {
        this.index = index;
    }

    int getIndex() {
        return index;
    }

    protected void nodeAdded( PhysicsNode node ) {
    }

    protected void nodeRemoved( PhysicsNode node ) {
    }
}

/*
 * $Log: OdeCollisionGroup.java,v $
 * Revision 1.2  2008/03/17 08:47:48  irrisor
 * fixed dublicte collision detection; fixed surface test
 *
 * Revision 1.1  2008/03/16 19:11:43  irrisor
 * added "collision groups" feature
 *
 */

