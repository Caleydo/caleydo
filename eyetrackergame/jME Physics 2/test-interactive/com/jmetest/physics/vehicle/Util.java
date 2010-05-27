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

package com.jmetest.physics.vehicle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import com.jme.bounding.BoundingBox;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.ZBufferState.TestFunction;
import com.jme.system.DisplaySystem;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;

/**
 * Helper class for loading models and other stuff. Currently only loads JME
 * binary models, but it's easily customizable for loading other types.
 *
 * @author Erick B Passos
 */
public class Util {

    // So JME can find the model textures easily.
    private static URL resourceSearchPath;

    static {
        resourceSearchPath = Util.class.getResource( "data/" );
        try {
            ResourceLocatorTool.addResourceLocator( ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator( resourceSearchPath ) );
        } catch ( URISyntaxException e1 ) {
            e1.printStackTrace();
        }
    }

    /**
     * Loads a jme binary model from file, creating a bounding box for it.
     *
     * @param path to jme file
     * @return loaded model, null on error
     */
    public static Node loadModel( String path ) {
        try {
            URL modelURL = new URL( resourceSearchPath, path );
            BufferedInputStream stream = new BufferedInputStream( modelURL.openStream() );
            Node node = new Node();
            node.attachChild( (Spatial) BinaryImporter.getInstance().load( stream ) );
            node.setModelBound( new BoundingBox() );
            node.updateModelBound();
            return node;
        } catch ( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method to apply a ZBufferState to a node.
     *
     * @param node where to apply to
     */
    public static void applyZBuffer( Node node ) {
        ZBufferState zbuf = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
        zbuf.setWritable( false );
        zbuf.setEnabled( true );
        zbuf.setFunction( TestFunction.LessThanOrEqualTo );
        node.setRenderState(zbuf);
	}
}
