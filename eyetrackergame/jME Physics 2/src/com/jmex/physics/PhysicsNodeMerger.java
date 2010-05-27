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

package com.jmex.physics;

import com.jme.scene.Spatial;

import java.util.ArrayList;

/**
 * Helper class to provide physics node merging interface
 * 
 * @author jspohr
 */
public class PhysicsNodeMerger {
    /**
     * Merges the set of children of a physics node into another's.
     * The source node will have its children removed.
     * 
     * @param target PhysicsNode which receives all children of the source.
     * @param source PhysicsNode whose children are moved to the target.
     */
	static public void mergePhysicsNodes( PhysicsNode target, PhysicsNode source ) {
		if ( source.getChildren() != null ) {
			ArrayList<Spatial> children = new ArrayList<Spatial>( source.getChildren() );
			for ( Spatial child : children ) {
				if ( child instanceof PhysicsCollisionGeometry ) {
					PhysicsCollisionGeometry collisionGeometry = (PhysicsCollisionGeometry) child;
					boolean inheritsMaterial = ( collisionGeometry.getMaterial() == source.getMaterial() )
						&& ( source.getMaterial() != source.getSpace().getDefaultMaterial() );
					if ( inheritsMaterial )
						collisionGeometry.setMaterial( source.getMaterial() );
				}
				target.attachChild( child );
			}
		}
	}
}

/*
 * $Log: PhysicsNodeMerger.java,v $
 * Revision 1.2  2007/09/22 14:28:37  irrisor
 * spongy contacts, improved material handling, center of mass correctly handled now, breakable joints, use odejava 0.3.1
 *
 * Revision 1.1  2007/02/22 15:55:14  jspohr
 * Changed behavior in PhysicsCollisionGeometry: geometries can be attached to a different PhysicsNode, the underlying implementation is required to handle this.
 * PhysicsNode.mergeWith(PhysicsNode) is deprecated, use the PhysicsNodeMerger helper class instead.
 *
 */
