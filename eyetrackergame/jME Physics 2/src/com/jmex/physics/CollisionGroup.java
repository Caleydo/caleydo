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
package com.jmex.physics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The collision groups provides a concept to get more control about the collision detection that is performed by the
 * physics engine. Each physics node can be assigned to a collision group.
 * For each collision group one can define which other groups can collide with nodes in this group.
 *
 * @see #collidesWith(CollisionGroup, boolean)
 */
public abstract class CollisionGroup {
    private List<CollisionGroup> collidesWith = new ArrayList<CollisionGroup>();
    private List<CollisionGroup> collidesWithReadOnly = Collections.unmodifiableList( collidesWith );

    protected final PhysicsSpace space;
    private final String name;

    public String getName() {
        return name;
    }

    protected CollisionGroup( PhysicsSpace space, String name ) {
        this.space = space;
        this.name = name;
        space.addCollisionGroup( this );
    }

    /**
     * Specify whether or not the nodes in this group collide with nodes from the other group.
     *
     * @param group    group to specify collision detection with (may be <code>this</code>)
     * @param collides true to enable collision detection between groups, false to disable
     */
    public void collidesWith( CollisionGroup group, boolean collides ) {
        if ( group == null ) {
            throw new IllegalArgumentException( "group cannot be null" );
        }
        collidesWith.remove( group );
        if ( group != this ) {
            group.collidesWith.remove( this );
        }
        if ( collides ) {
            collidesWith.add( group );
            if ( group != this ) {
                group.collidesWith.add( this );
            }
        }
    }

    public List<CollisionGroup> getCollidesWith() {
        return collidesWithReadOnly;
    }

    public void delete() {
        if ( numNodes > 0 ) {
            throw new IllegalStateException( "Cannot delete a collision group which contains nodes. " +
                    "Move nodes to another group first." );
        }
        for ( CollisionGroup group : new ArrayList<CollisionGroup>( getCollidesWith() ) ) {
            collidesWith( group, false );
        }
        space.removeCollisionGroup( this );
    }

    private int numNodes = 0;

    final void addToNodes( PhysicsNode value ) {
        numNodes++;
        nodeAdded(value);
    }

    protected abstract void nodeAdded( PhysicsNode node );

    protected abstract void nodeRemoved( PhysicsNode node );

    final void removeFromNodes( PhysicsNode value ) {
        numNodes--;
        nodeRemoved(value);
    }

    public String toString() {
        return "CollisionGroup("+getName()+")";
    }
}

/*
 * $Log: CollisionGroup.java,v $
 * Revision 1.2  2008/03/17 08:47:54  irrisor
 * fixed dublicte collision detection; fixed surface test
 *
 * Revision 1.1  2008/03/16 19:11:50  irrisor
 * added "collision groups" feature
 *
 */

