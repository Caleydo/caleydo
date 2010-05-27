/*
 * Copyright (c) 2003-2009 jMonkeyEngine All rights reserved. Redistribution and
 * use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: * Redistributions of source
 * code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. * Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the distribution. *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.text;

import java.awt.Font;
import java.util.logging.Logger;

import jmetest.renderer.state.TestTextureState;

import com.jme.app.SimpleGame;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.CullState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jmex.font2d.Font2D;
import com.jmex.font2d.Text2D;
import com.jmex.font3d.Font3D;
import com.jmex.font3d.Text3D;
import com.jmex.font3d.effects.Font3DTexture;

/**
 * <code>TestSimpleGame</code>
 * 
 * @author Joshua Slack
 * @version $Id: Test3DText.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class Test3DText extends SimpleGame {
    private static final Logger logger = Logger.getLogger(Test3DText.class
            .getName());

    public static void main(String[] args) {
        Test3DText app = new Test3DText();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }


	private Font3D myfont;

    @Override
    protected void simpleInitGame() {
        display.setTitle("Test Text3D");
        
        // Create the font
        myfont = new Font3D(new Font("Arial", Font.PLAIN, 2), 0.1, true, true, true);
        
        // Create and apply some effects
        //Font3DGradient gradient = new Font3DGradient(new Vector3f(0.5f,1,0), ColorRGBA.lightGray, new ColorRGBA(0,0,1,1f));
        //gradient.applyEffect(myfont);
        Font3DTexture fonttexture = new Font3DTexture(TestTextureState.class.getClassLoader().getResource("jmetest/data/model/marble.bmp"));
        fonttexture.applyEffect(myfont);
        //Font3DBorder fontborder = new Font3DBorder(0.05f, ColorRGBA.red, new ColorRGBA(1,1,0,0.3f));
        //fontborder.applyEffect(myfont);
        
        // Create some text-blocks
        //for (int i = -20; i < 20; i++) {
        for (int i = 0; i < 3; i++) {
            Text3D mytext = myfont.createText(
                    "---- ("+i+") ---- Text3D is nice !!!", 2, 0);
            ColorRGBA fontcolor = new ColorRGBA(1, (float) Math.random(), (float) Math.random(), 1);
            mytext.setFontColor(fontcolor);
            mytext.setLocalTranslation(new Vector3f(2, i*2, 0));
            mytext.setLocalRotation(new Quaternion().fromAngleNormalAxis(
                   FastMath.TWO_PI * (i / 20f), Vector3f.UNIT_Y));
            rootNode.attachChild(mytext);
            mytext.setCullHint(Spatial.CullHint.Dynamic);
            if(i % 3 == 0)
            	mytext.setLightCombineMode(Spatial.LightCombineMode.Off);
            mytext.updateRenderState();
            Box box = new Box("ReferenceBox"+i, mytext.getLocalTranslation(), 1,1,1);
            box.setDefaultColor(fontcolor);
            box.setCullHint(Spatial.CullHint.Dynamic);
            box.setModelBound(new OrientedBoundingBox());
            box.updateModelBound();
            rootNode.attachChild(box);
        }
        rootNode.updateWorldBound();
        
        // Just add a box
        /*
        {
        	Box box = new Box("MyBox", new Vector3f(2,2,2), 1,1,1);
        	//box.setRenderState(ts);
        	SharedMesh sharedbox = new SharedMesh("MyShardBox", box);
        	//sharedbox.setDefaultColor(ColorRGBA.blue);
        	//sharedbox.setRenderState(ts);
        	
        	rootNode.attachChild(sharedbox);
        }
        */

        // And to make sure text is OK we add some backface culling
        CullState bfculling = DisplaySystem.getDisplaySystem().getRenderer()
                .createCullState();
        bfculling.setCullFace(CullState.Face.Back);
        rootNode.setRenderState(bfculling);
        rootNode.setCullHint(Spatial.CullHint.Never);

        // Now some 2D text
        Font2D my2dfont = new Font2D();
        {
            Text2D my2dtext = my2dfont.createText(
                    "You can press \"u\" to toggle locked/unlocked mode on the glyphs", 10, 0);
            my2dtext.setLocalTranslation(new Vector3f(100, 100, 0));
            my2dtext.setRenderQueueMode(Renderer.QUEUE_ORTHO);
            ZBufferState zbs = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
            zbs.setFunction(ZBufferState.TestFunction.Always);
            my2dtext.setRenderState(zbs);
            rootNode.attachChild(my2dtext);
        }
        {
            Text2D my2dtext = my2dfont.createText(
                    "And you can press \"l\" to toggle lights.", 10, 0);
            my2dtext.setLocalTranslation(new Vector3f(100, 80, 0));
            my2dtext.setRenderQueueMode(Renderer.QUEUE_ORTHO);
            ZBufferState zbs = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
            zbs.setFunction(ZBufferState.TestFunction.Always);
            my2dtext.setRenderState(zbs);
            rootNode.attachChild(my2dtext);
        }

        // Setup keys for locking the stuff
        /** Assign key U to action "toggle_locked_font_mesh". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_locked_font_mesh", KeyInput.KEY_U );
        /** Assign key I to action "update_render_states". */
        KeyBindingManager.getKeyBindingManager().set( "update_render_states", KeyInput.KEY_I );

        
		// And to make gradients work...
        //lightState.detachAll();
        //lightState.setEnabled(false);
    }
    
    
    @Override
	public void simpleUpdate()
    {
        /** If toggle_lights is a valid command (via key L), change lightstate. */
        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_locked_font_mesh", false ) ) {
	    	if((myfont.getRenderNode().getLocks() & Spatial.LOCKED_MESH_DATA) == 0)
	    	{
	    		logger.info("Locked !");
	    		myfont.getRenderNode().lockMeshes();
	    	}
	    	else
	    	{
	    		logger.info("Unlocked !");
	    		myfont.getRenderNode().unlockMeshes();
	    	}
        }
        if( KeyBindingManager.getKeyBindingManager().isValidCommand( 
        		"update_render_states", false)) {
        	rootNode.updateRenderState();
        }
    }
}