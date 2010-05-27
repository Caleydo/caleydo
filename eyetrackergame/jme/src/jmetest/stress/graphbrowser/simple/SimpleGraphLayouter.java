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

package jmetest.stress.graphbrowser.simple;

import jmetest.stress.graphbrowser.GraphLayouter;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * Very simple implementation of a {@link GraphLayouter}. For testing {@link jmetest.stress.graphbrowser.GraphBrowser}.
 */
public class SimpleGraphLayouter implements GraphLayouter {
    private Object lastNode;
    private Vector3f lastPos = new Vector3f();
    private Quaternion rotation0 = new Quaternion().fromAngleNormalAxis( 0.2f, new Vector3f( 1, 2, 0 ).normalizeLocal() );
    private Quaternion rotation1 = new Quaternion().fromAngleNormalAxis( -0.2f, new Vector3f( 1, 3, 0 ).normalizeLocal() );
    private Vector3f translation0 = new Vector3f( 10, 0, 0 );
    private Vector3f translation1 = new Vector3f( 0, 5, 0 );
//    private int frame;

    /**
     * Create a simple graph layouter.
     * @param accessor accompanying accessor
     */
    public SimpleGraphLayouter( SimpleGraphAccessor accessor ) {
        if ( accessor == null )
        {
            throw new NullPointerException( "SimpleGraphLayouter is for testing SimpleGraphAccessor, only" );
        }
    }

    /**
     * Query the coordinates for a node.
     * @param node graph node of interest
     * @return position for that node
     */
    public Vector3f getCoordinates( Object node ) {
        if ( lastNode != node )
        {
            lastNode = node;
            int nodeNum = ((Integer)node).intValue();
            lastPos.set( (nodeNum & 1) * 40, 0, 0 );
//            if ( nodeNum == 0 )
//            {
//                frame++;
//            }
            while ( nodeNum != 0 )
            {
                if ( (nodeNum & 1) == 0 )
                {
                    lastPos.addLocal( translation0 );
                    rotation0.multLocal( lastPos );
                }
                else
                {
                    lastPos.addLocal( translation1 );
                    rotation1.multLocal( lastPos );
                }
                nodeNum >>= 1;
            }
        }
        return lastPos;
    }
}
