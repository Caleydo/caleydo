/*
 * Copyright (c) 2005-2006 jME Physics 2
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.jme.scene.Node;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.binary.BinaryImporter;

/**
 * @author Irrisor
 */
public class SystemTest extends junit.framework.TestCase {
    public void testCreate() {
        PhysicsSpace physicsSpace = PhysicsSpace.create();
        assertNotNull( "physics space should not be null", physicsSpace );

        DynamicPhysicsNode dynamicNode = physicsSpace.createDynamicNode();
        assertNotNull( "create node should not return null", dynamicNode );

        StaticPhysicsNode staticNode = physicsSpace.createStaticNode();
        assertNotNull( "create node should not return null", staticNode );

        Joint joint = physicsSpace.createJoint();
        assertNotNull( "create joint should not return null", joint );
    }

    public void testBinaryLoading() throws IOException {
    	PhysicsSpace physicsSpace = PhysicsSpace.create();
    	physicsSpace.setupBinaryClassLoader( BinaryImporter.getInstance() );

        Node parent = new Node();
        DynamicPhysicsNode dynamicNode = physicsSpace.createDynamicNode();
        dynamicNode.createSphere( null );
        dynamicNode.createBox( null );
        dynamicNode.createCylinder( null );
        dynamicNode.createCapsule( null );
//        dynamicNode.createMesh( null );
        parent.attachChild( dynamicNode );
        dynamicNode.setMass( 10.0f );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryExporter.getInstance().save( dynamicNode, baos );
        DynamicPhysicsNode loadedDynamicNode = (DynamicPhysicsNode) BinaryImporter.getInstance().load( new ByteArrayInputStream( baos.toByteArray() ) );
        assertNotNull( "loaded node should not be null", loadedDynamicNode );
        assertNull( "loaded node's parent should be null", loadedDynamicNode.getParent() );
        assertEquals( "loaded node should have 4 children", 4, loadedDynamicNode.getChildren().size() );
        assertEquals( "loaded node should have mass 10", 10.0f, loadedDynamicNode.getMass() );

        StaticPhysicsNode staticNode = physicsSpace.createStaticNode();
		baos = new ByteArrayOutputStream();
        BinaryExporter.getInstance().save( staticNode, baos );
        StaticPhysicsNode loadedStaticNode = (StaticPhysicsNode) BinaryImporter.getInstance().load( new ByteArrayInputStream( baos.toByteArray() ) );
        assertNotNull( "loaded node should not be null", loadedStaticNode );

        Joint joint = physicsSpace.createJoint();
        joint.createRotationalAxis();
        joint.createTranslationalAxis();
		baos = new ByteArrayOutputStream();
        BinaryExporter.getInstance().save( joint, baos );
        Joint loadedJoint = (Joint) BinaryImporter.getInstance().load( new ByteArrayInputStream( baos.toByteArray() ) );
        assertNotNull( "loaded joint should not be null", loadedJoint );
        assertEquals( "loaded joint should have 2 axes", 2, loadedJoint.getAxes().size() );

    }
}

/*
* $log$
*/