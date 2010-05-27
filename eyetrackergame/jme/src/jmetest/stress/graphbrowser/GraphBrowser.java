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

package jmetest.stress.graphbrowser;

import java.util.HashMap;
import java.util.Map;

import jmetest.stress.StressApp;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;

/**
 * Many Boxes and lines make up some graph. Graph data is specified by a {@link GraphAccessor} and
 * layout by a {@link GraphLayouter}.
 * @author Irrisor
 * @created 08.07.2005, 16:19:38
 */
public class GraphBrowser extends StressApp {

    /**
     * Map from graph node (Object) to visualization (Spatial)
     */
    Map<Object, Spatial> nodes = new HashMap<Object, Spatial>();
    /**
     * Map from graph edge (Object) to visualization (Line)
     */
    Map<Object, Line> edges = new HashMap<Object, Line>();
    /**
     * Accessor used for reading the graph.
     */
    private GraphAccessor accessor;
    /**
     * Layouter to query positions for graph node visuals.
     */
    private GraphLayouter layouter;
    /**
     * Flag for toggling visibility of all/path edges.
     */
    private boolean pathOnly;
    /**
     * Command for toggling edges.
     */
    private static final String COMMAND_PATH_ONLY = "toggle_path_only";
    private Box box;

    /**
     * Create a graphbrowser app that uses given {@link GraphAccessor} and {@link GraphLayouter}.
     * @param accessor graph data
     * @param layouter graph layout
     */
    public GraphBrowser( GraphAccessor accessor, GraphLayouter layouter ) {
        this.accessor = accessor;
        this.layouter = layouter;
    }

    /**
     * Create all the visual stuff for the graph.
     */
    protected void simpleInitGame() {
        lightState.setGlobalAmbient(new ColorRGBA(0.5f,0.5f,0.5f,1));
        box = new Box( "box", new Vector3f( -1, -1, -1 ), new Vector3f( 1, 1, 1 ) );

        MaterialState material = display.getRenderer().createMaterialState();
        material.setEnabled( true );
        ColorRGBA white = new ColorRGBA( 1, 1, 1, 1 );
        material.setDiffuse( white );
        rootNode.setRenderState( material );

        for ( int i = accessor.getNodeCount() - 1; i >= 0; i-- ) {
            Object node = accessor.getNode( i );
            Spatial nodeVis = new SharedMesh( String.valueOf( node ), box );
            nodeVis.getLocalTranslation().set( layouter.getCoordinates( node ) );

            ColorRGBA color = colorForNode( node );
            if ( !white.equals( color ) )
            {
                material = display.getRenderer().createMaterialState();
                material.setEnabled( true );
                material.setDiffuse( color );
                nodeVis.setRenderState( material );
            }

            rootNode.attachChild( nodeVis );
            nodes.put( node, nodeVis );
        }

        Node lines = new Node("lines");
        material = display.getRenderer().createMaterialState();
        material.setEnabled( true );
        material.setDiffuse( white );
        material.setEmissive( white );
        lines.setRenderState( material );
        rootNode.attachChild( lines );

        for ( int i = accessor.getEdgeCount() - 1; i >= 0; i-- ) {
            Object edge = accessor.getEdge( i );

            Spatial fromVis = nodes.get( accessor.getEdgeSource( edge ) );
            Spatial toVis = nodes.get( accessor.getEdgeTarget( edge ) );
            
            Vector3f[] points = {fromVis.getLocalTranslation(), toVis.getLocalTranslation()};
            Line edgeVis = new Line( edge.toString(), points, null, null, null );

            ColorRGBA color = colorForEdge( edge );
            if ( !white.equals( color ) )
            {
                material = display.getRenderer().createMaterialState();
                material.setEnabled( true );
                material.setDiffuse( color );
                material.setEmissive( color );
                edgeVis.setRenderState( material );
            }
            edgeVis.setLightCombineMode( LightCombineMode.CombineClosest );

            lines.attachChild( edgeVis );
            edgeVis.updateRenderState();
            edges.put( edge, edgeVis );
        }

        KeyBindingManager.getKeyBindingManager().set( COMMAND_PATH_ONLY, KeyInput.KEY_O );
        final Text text = createText( "Press O to toggle edges/path" );
        text.getLocalTranslation().set( 0, 20, 0 );
        statNode.attachChild( text );

        cam.getLocation().set( 40, 40, 100 );
        cam.update();
    }

    /**
     * Query color for an edge - could be moved to layouter...
     * @param edge edge of interest
     * @return any color (not null)
     */
    private ColorRGBA colorForEdge( Object edge ) {
        boolean steiner = accessor.isEdgePath( edge );
        return new ColorRGBA( 1, steiner ? 0 : 1, steiner ? 0 : 1, 1 );
    }

    /**
     * Query color for a node - could be moved to layouter...
     * @param node node of interest
     * @return any color (not null)
     */
    private ColorRGBA colorForNode( Object node ) {
        return new ColorRGBA( 1, 1, accessor.isNodeTerminal( node ) ? 0 : 1, 1 );
    }

    /**
     * Process key input.
     */
    protected void simpleUpdate() {
        super.simpleUpdate();

        if ( KeyBindingManager
                .getKeyBindingManager()
                .isValidCommand( COMMAND_PATH_ONLY, false ) ) {
            pathOnly = !pathOnly;
            for ( int i = accessor.getEdgeCount() - 1; i >= 0; i-- ) {
                Object edge = accessor.getEdge( i );
                if ( !accessor.isEdgePath( edge ) ) {
                    Spatial spatial = edges.get( edge );
                    if ( spatial != null ) {
                        spatial.setCullHint( pathOnly ? Spatial.CullHint.Always : Spatial.CullHint.Dynamic);
                    }
                }
            }
        }

        // rearrange nodes
//        for ( int i = accessor.getNodeCount() - 1; i >= 0; i-- ) {
//            Object node = accessor.getNode( i );
//
//            Spatial nodeVis = (Spatial) nodes.get( node );
//
//            nodeVis.getLocalTranslation().set( layouter.getCoordinate( node, 0 ),
//                    layouter.getCoordinate( node, 1 ),
//                    layouter.getCoordinate( node, 2 ) );
//        }
    }
}
