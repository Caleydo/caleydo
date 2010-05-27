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
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Text;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.ClipState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;
import com.jme.util.TextureManager;



/**
 * <code>TestClipState</code>
 * @author Mark Powell
 * @version $Id: TestClipState.java 4130 2009-03-19 20:04:51Z blaine.dev $
 * @version $Id: TestClipState.java 4130 2009-03-19 20:04:51Z blaine.dev $
 * 
 * Demonstrats the use of clips planes in an interactive demo.  A clip plane splits 
 * the rendering 'area' in 2, one side is rendered unchanged while the other is not
 * rendered.  'Under the hood' the verticies must still be calculated and additional verticies
 * may be 'inserted', but there should be a considerable performance increase
 * in direct relation to the number of 'clipped' faces.
 * A clip plane is constructed by supplying the planes normal vector (X,Y,Z) and 
 * the distance from the origin (W).
 * 
 * Through use of jME RenderingPasses (and enabling/disabling clip planes) it should 
 * be possible to complete rendering techniques like the following URL with ease: 
 * http://glbook.gamedev.net/moglgp/advclip.asp
 */
public class TestClipState extends SimpleGame {

    private int textY = 10;
    private Text equationText = null;
    private ClipState clipState = null;
    private float dist = 50;
    //
    private final Vector3f planeNormal = new Vector3f( 1, 0, 0 );
    private final Quad pseudoPlane = new Quad( "", 1000, 1000 );

    /**
     * Entry point for the test,
     * @param args
     */
    public static void main( String[] args ) {
        TestClipState app = new TestClipState();
        app.setConfigShowMode( ConfigShowMode.AlwaysShow );
        app.start();

    }

    @Override
    protected void simpleUpdate() {
        boolean update = false;
        double change = 0;

        if( KeyBindingManager.getKeyBindingManager().isValidCommand( "UP", true ) ){
            change = 0.1;
            update = true;
        }
        if( KeyBindingManager.getKeyBindingManager().isValidCommand( "DOWN", true ) ){
            change = -0.1;
            update = true;
        }

        if( KeyBindingManager.getKeyBindingManager().isValidCommand( "X", false ) ){
            planeNormal.set( 1, 0, 0 );
            update = true;
        }
        if( KeyBindingManager.getKeyBindingManager().isValidCommand( "Y", false ) ){
            planeNormal.set( 0, 1, 0 );
            update = true;
        }
        if( KeyBindingManager.getKeyBindingManager().isValidCommand( "Z", false ) ){
            planeNormal.set( 0, 0, 1 );
            update = true;
        }
        if( KeyBindingManager.getKeyBindingManager().isValidCommand( "NEGATE", false ) ){
            planeNormal.negateLocal();
            dist = -dist;
            update = true;
        }

        if( KeyBindingManager.getKeyBindingManager().isValidCommand( "RESET", false ) ){
            planeNormal.set( 1, 0, 0 );
            dist = 50;
            update = true;
        }

        if( KeyBindingManager.getKeyBindingManager().isValidCommand( "TOGGLE", false ) ){
            clipState.setEnabled( !clipState.isEnabled() );
            if( clipState.isEnabled() ){
                pseudoPlane.setCullHint( Quad.CullHint.Never );
            } else{
                pseudoPlane.setCullHint( Quad.CullHint.Always );
            }
        }

        if( update == true ){
            dist += change;
            updateClipPlane();
        }
    }

    protected void simpleInitGame() {

        initEquationText();
        statNode.attachChild( addText( "Press < to decrease distance" ) );
        statNode.attachChild( addText( "Press > to increase distance" ) );
        statNode.attachChild( addText( "Press X Y or Z to change the plane orientation" ) );
        statNode.attachChild( addText( "Press SPACE to negate the plane normal" ) );
        statNode.attachChild( addText( "Press RETURN to toggle the clip state" ) );
        statNode.attachChild( addText( "Press HOME to reset the plane" ) );

        KeyBindingManager.getKeyBindingManager().set( "UP", KeyInput.KEY_PERIOD );
        KeyBindingManager.getKeyBindingManager().set( "DOWN", KeyInput.KEY_COMMA );
        //
        KeyBindingManager.getKeyBindingManager().set( "X", KeyInput.KEY_X );
        KeyBindingManager.getKeyBindingManager().set( "Y", KeyInput.KEY_Y );
        KeyBindingManager.getKeyBindingManager().set( "Z", KeyInput.KEY_Z );
        KeyBindingManager.getKeyBindingManager().set( "NEGATE", KeyInput.KEY_SPACE );
        KeyBindingManager.getKeyBindingManager().set( "RESET", KeyInput.KEY_HOME );
        KeyBindingManager.getKeyBindingManager().set( "TOGGLE", KeyInput.KEY_RETURN );

        display.setTitle( "Clip State Test" );
        cam.setLocation( new Vector3f( -300, 300, 300 ) );
        cam.lookAt( Vector3f.ZERO, Vector3f.UNIT_Y );
        cam.update();

        lightState.setEnabled( false );
        MouseInput.get().setCursorVisible( true );
        ( (FirstPersonHandler) input ).setButtonPressRequired( true );

        Vector3f max = new Vector3f( 100, 100, 100 );
        Vector3f min = new Vector3f( -100, -100, -100 );

        final Box box = new Box( "Box", min, max );
        box.setModelBound( new BoundingSphere() );
        box.updateModelBound();

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled( true );
        ts.setTexture(
                TextureManager.loadTexture(
                TestClipState.class.getClassLoader().getResource( "jmetest/data/images/Monkey.jpg" ),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear ) );

        box.setRenderState( ts );

        rootNode.attachChild( box );

        clipState = display.getRenderer().createClipState();
        clipState.setEnableClipPlane( ClipState.CLIP_PLANE0, true );
        rootNode.setRenderState( clipState );

        initPseudoPlane();
        rootNode.attachChild( pseudoPlane );
        updateClipPlane();
    }

    private void updateClipPlane() {
        clipState.setClipPlaneEquation( ClipState.CLIP_PLANE0, planeNormal.x, planeNormal.y, planeNormal.z, dist );
        updatePseudoPlanePosition();
        equationText.print( "Plane Equation: (" + planeNormal.x + ", " + planeNormal.y + ", " + planeNormal.z + ") Dist: " + dist );
    }

    private void updatePseudoPlanePosition() {
        final Vector3f tempVector = new Vector3f();

        // Set the plane to the correct 'visual' rotation to match the clip plane normal
        if( planeNormal.x == 1 || planeNormal.x == -1 ){
            tempVector.set( 0, 1, 0 );

        } else if( planeNormal.y == 1 || planeNormal.y == -1 ){
            tempVector.set( 1, 0, 0 );

        } else if( planeNormal.z == 1 || planeNormal.z == -1 ){
            tempVector.set( 0, 0, 1 );
        }
        pseudoPlane.getLocalRotation().fromAngleNormalAxis( FastMath.HALF_PI, tempVector );
        pseudoPlane.getLocalTranslation().set( planeNormal ).multLocal( ( dist - 0.1f ) ).negateLocal();
        pseudoPlane.updateGeometricState( 0, true );
    }

    private void initPseudoPlane() {

        pseudoPlane.setDefaultColor( new ColorRGBA( 1, 0, 0, 0.5f ) );

        final CullState cullState = display.getRenderer().createCullState();
        cullState.setCullFace( CullState.Face.None );

        final BlendState blendState = display.getRenderer().createBlendState();
        blendState.setSourceFunctionAlpha( SourceFunction.SourceAlpha );
        blendState.setDestinationFunctionAlpha( DestinationFunction.OneMinusSourceAlpha );
        blendState.setEnabled( true );
        blendState.setTestEnabled( true );
        blendState.setBlendEnabled( true );
        blendState.setTestFunction( BlendState.TestFunction.NotEqualTo );

        pseudoPlane.setModelBound( new BoundingBox() );
        pseudoPlane.updateModelBound();
        pseudoPlane.setRenderState( blendState );
        pseudoPlane.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
        pseudoPlane.updateRenderState();

        updatePseudoPlanePosition();
    }

    private Text addText( String s ) {
        Text text = new Text( s, s );
        text.setRenderState( Text.getDefaultFontTextureState() );
        text.setRenderState( Text.getFontBlend() );
        text.setLocalTranslation( 10, display.getRenderer().getHeight() - text.getHeight() - textY, 0 );
        textY += text.getHeight() * 1.5f;
        return text;
    }

    private void initEquationText() {
        equationText = new Text( "equationText", "" );
        equationText.setRenderState( Text.getDefaultFontTextureState() );
        equationText.setRenderState( Text.getFontBlend() );
        equationText.setLocalTranslation( 10, 10, 0 );
        statNode.attachChild( equationText );
    }
}