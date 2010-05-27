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

package jmetest.scalarfields;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Text;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;



/**
 * Demo to show off the {@link ScalarFieldPolygonisator} for scalar fields.
 *
 * @author Daniel Gronau
 * @author Joshua Ellen (performace and test update)
 */
public class TestMetaballs extends SimpleGame {

    private MetaBalls metaBalls = null;
    private Texture texture = null;
    //
    private Text envMapToggleText = null;
    private final String envMapToggleString = "Press E to toggle EnvMap mode: ";
    private int frameCounter = 0;

    public static void main( String[] args ) {
        TestMetaballs app = new TestMetaballs();
        app.setConfigShowMode( ConfigShowMode.NeverShow );
        app.start();
    }

    @Override
    protected void simpleInitGame() {

        display.setTitle( "MetaBalls Test" );

        metaBalls = new MetaBalls();
        metaBalls.setRenderState( createTextureState( "terrain/road.jpg" ) );
        rootNode.attachChild( metaBalls );

        // light up the scene a little (attention works for SimpleGame only)
        this.lightState.get( 0 ).setAmbient( ColorRGBA.white );
        this.lightState.get( 0 ).setDiffuse( ColorRGBA.white );
        final MaterialState state = display.getRenderer().createMaterialState();
        state.setColorMaterial( MaterialState.ColorMaterial.Diffuse );
        state.setShininess( 100 );
        state.setAmbient( new ColorRGBA( 0.4f, 0.4f, 0.4f, 1 ) );
        metaBalls.setRenderState( state );

        lightState.setEnabled( false );         // Turn them off initially to see vertex coloring


        final String tempString = envMapToggleString + " (OFF)";
        envMapToggleText = new Text( tempString, tempString );
        envMapToggleText.setRenderState( Text.getDefaultFontTextureState() );
        envMapToggleText.setRenderState( Text.getFontBlend() );
        envMapToggleText.setLocalTranslation( 10, envMapToggleText.getHeight() + 5, 0 );

        statNode.attachChild( envMapToggleText );
        KeyBindingManager.getKeyBindingManager().set( "toggleEnvMap", KeyInput.KEY_E );
    }

    @Override
    protected void simpleUpdate() {

        if( KeyBindingManager.getKeyBindingManager().isValidCommand( "toggleEnvMap", false ) ){
            if( texture.getEnvironmentalMapMode().equals( Texture.EnvironmentalMapMode.ReflectionMap ) ){
                texture.setEnvironmentalMapMode( Texture.EnvironmentalMapMode.None );
                envMapToggleText.print( envMapToggleString + " (OFF)" );
            } else{
                texture.setEnvironmentalMapMode( Texture.EnvironmentalMapMode.ReflectionMap );
                envMapToggleText.print( envMapToggleString + " (ON)" );
            }
        }

        if( frameCounter % 10 == 0 ){
            metaBalls.updateGeometricState( tpf, false );
            frameCounter = -1;
        }
        ++frameCounter;
    }

    private TextureState createTextureState( String textureString ) {

        try{
            ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_TEXTURE,
                    new SimpleResourceLocator(
                    Thread.currentThread().getContextClassLoader().getResource(
                    "jmetest/data/texture/" ) ) );
        } catch( Exception e ){
            System.err.println( "Unable to access texture directory." );
            e.printStackTrace();
        }

        TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        texture = TextureManager.loadTexture( textureString, Texture.MinificationFilter.BilinearNoMipMaps,
                Texture.MagnificationFilter.Bilinear, ts.getMaxAnisotropic(), false );
        texture.setWrap( Texture.WrapMode.Repeat );

        ts.setTexture( texture );
        return ts;
    }
}