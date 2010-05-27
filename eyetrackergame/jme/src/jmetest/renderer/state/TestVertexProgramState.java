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

package jmetest.renderer.state;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.VertexProgramState;
import com.jme.scene.state.WireframeState;
import com.jme.util.TextureManager;

/**
 * @author Eric Woroshow
 * @version $Id: TestVertexProgramState.java,v 1.8 2004/04/23 03:10:04 renanse
 *          Exp $
 */
public class TestVertexProgramState extends SimpleGame {

    /** The position of the light in object space */
    private final float[] lightPosition = { -0.8f, 0.8f, 0.8f, 0.0f };

    public static void main(String[] args) {
        TestVertexProgramState app = new TestVertexProgramState();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    /**
     * Set up the scene.
     * 
     * @see com.jme.app.AbstractGame#initGame()
     */
    protected void simpleInitGame() {
        display.setTitle( "Vertex Programs" );
        display.getRenderer().setBackgroundColor( new ColorRGBA( 0.02f, 0.0f, 0.776f, 1.0f ) );

        cam.setLocation( new Vector3f( 0, 0, 45 ) );
        cam.update();
        input = new NodeHandler( rootNode, 10, 2 );

        //To acheive a cartoon render look, we attatch both a cel shaded
        //torus and its outline to the scene. The two torii occupy the
        //same space, so the outline will overlap and highlight the lit torus.
        Torus shaded = createShadedTorus(), outline = createOutlineTorus();
        rootNode.attachChild( shaded );
        rootNode.attachChild( outline );

        //Allow the torus to be controlled by the mouse.
        //By attatching the controller to the scene root, we can manipulate
        //both torii at once, thus guaranteeing that the outline and shaded
        //version will never be out of sync.
        rootNode.updateRenderState();
    }

    private Torus createShadedTorus() {
        //Load the vertex program from a file and bind it to a render state
        VertexProgramState vp = display.getRenderer().createVertexProgramState();
        vp.setParameter(lightPosition, 8);
        vp.load(TestVertexProgramState.class.getClassLoader().getResource(
                "jmetest/data/images/celshaderARB.vp"));
        vp.setEnabled(true);

        //Bind a 1-dimensional luminance texture for use by the vertex program
        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.loadTexture(
                TestVertexProgramState.class.getClassLoader().getResource(
                        "jmetest/data/images/shader.png"), Texture.MinificationFilter.NearestNeighborNoMipMaps,
                Texture.MagnificationFilter.NearestNeighbor));

        //Generate the torus
        Torus torus = new Torus("shadedTorus", 128, 32, 3.0f, 5.0f);
        torus.setRenderState(vp);
        torus.setRenderState(ts);

        return torus;
    }

    private Torus createOutlineTorus() {
        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Front);
        cs.setEnabled(true);

        WireframeState ws = display.getRenderer().createWireframeState();
        ws.setLineWidth(6.0f);
        ws.setFace(WireframeState.Face.Back);
        ws.setEnabled(true);

        Torus torus = new Torus("outlineTorus", 128, 32, 3.0f, 5.0f);

        torus.setDefaultColor(ColorRGBA.black.clone());

        torus.setRenderState(cs);
        torus.setRenderState(ws);

        return torus;
    }
}