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
package com.jmetest.physics;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.input.MouseInput;
import com.jme.math.Vector3f;
import com.jme.scene.Text;
import com.jme.scene.shape.Box;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.util.PhysicsPicker;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * @author forum user sbayless
 */
public class TestCenter extends SimplePhysicsGame {

    protected void simpleInitGame() {
        StaticPhysicsNode staticNode = getPhysicsSpace().createStaticNode();
        rootNode.attachChild( staticNode );

        final Box visualFloorBox = new Box( "floor", new Vector3f(), 5, 0.25f, 5 );
        staticNode.attachChild( visualFloorBox );
        staticNode.generatePhysicsGeometry();

        DynamicPhysicsNode dynamicNode = getPhysicsSpace().createDynamicNode();
        rootNode.attachChild( dynamicNode );

        final Box visualFallingBox = new Box( "falling box", new Vector3f(), 0.5f, 0.5f, 0.5f );
        visualFallingBox.setModelBound( new BoundingBox() );
        visualFallingBox.updateModelBound();
        visualFallingBox.getLocalTranslation().set(-4,0,0);  //make the box off centered

        dynamicNode.attachChild( visualFallingBox );

        dynamicNode.generatePhysicsGeometry();

        dynamicNode.computeMass(); // this should compute the correct center of mass to let the box behave normally

        dynamicNode.getLocalTranslation().set( 0, 5, 0 );

        showPhysics = true;

        Text label = Text.createDefaultTextLabel( "info", "The box should behave 'normally' as the center of mass " +
                "is in the center of the box again." );
        label.setLocalTranslation( 0, 20, 0 );
        statNode.attachChild( label );
        
        cameraInputHandler.setEnabled( false );
        MouseInput.get().setCursorVisible( true );
        new PhysicsPicker( input, rootNode, getPhysicsSpace(), true );
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING ); // to see the important stuff
        new TestCenter().start();
    }
}

/*
 * $Log: TestCenter.java,v $
 * Revision 1.2  2007/09/22 14:28:36  irrisor
 * spongy contacts, improved material handling, center of mass correctly handled now, breakable joints, use odejava 0.3.1
 *
 * Revision 1.1  2007/09/09 10:25:48  irrisor
 * added ragdoll from mud2005, new interface PhysicsSpatial for physics node and collision geometry, new api PhysicsSpace#collide(PhysicsSpatial, PhysicsSpatial)
 *
 */

