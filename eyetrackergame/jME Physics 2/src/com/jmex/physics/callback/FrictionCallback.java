/*
 * Copyright (c) 2005-2007 jME Physics 2
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
package com.jmex.physics.callback;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;

/**
 * FrictionCallback provides features to apply friction per step for a DynamicPhysicsNode.
 * This class is thread-safe.
 *
 * @author Matthew D. Hicks
 */
public class FrictionCallback implements PhysicsUpdateCallback {
    private Collection<FrictionEncapsulation> nodes;

    public FrictionCallback() {
        nodes = new ConcurrentLinkedQueue<FrictionEncapsulation>();
    }

    public boolean add( DynamicPhysicsNode node, float forceFriction, float angularFriction ) {
        return nodes.add( new FrictionEncapsulation( node, forceFriction, angularFriction ) );
    }

    public void afterStep( PhysicsSpace space, float time ) {
        Iterator<FrictionEncapsulation> iterator = nodes.iterator();
        while ( iterator.hasNext() ) {
            FrictionEncapsulation fe = iterator.next();
            if ( !fe.call( time ) ) {
                iterator.remove();
            }
        }
    }

    public void beforeStep( PhysicsSpace space, float time ) {
    }

    private class FrictionEncapsulation {
        private final Vector3f store = new Vector3f();

        private WeakReference<DynamicPhysicsNode> node;
        private float forceFriction;
        private float angularFriction;

        public FrictionEncapsulation( DynamicPhysicsNode dpn, float forceFriction, float angularFriction ) {
            this.node = new WeakReference<DynamicPhysicsNode>( dpn );
            this.forceFriction = forceFriction;
            this.angularFriction = angularFriction;
        }

        public boolean call( float time ) {
            DynamicPhysicsNode dpn = node.get();
            if ( dpn == null ) {
                return false;
            }

            if ( forceFriction != 0.0f ) {
                dpn.getLinearVelocity( store );
                float change = forceFriction * time;
                applyFriction( store, change );
                dpn.setLinearVelocity( store );
            }
            if ( angularFriction != 0.0f ) {
                dpn.getAngularVelocity( store );
                float change = angularFriction * time;
                applyFriction( store, change );
                dpn.setAngularVelocity( store );
            }

            return true;
        }

        private void applyFriction( Vector3f velocity, float change ) {
            float newLength = velocity.length() - change;
            if ( newLength < 0 ) {
                velocity.set( 0, 0, 0 );
            } else {
                velocity.normalizeLocal().multLocal( newLength );
            }
        }
    }
}