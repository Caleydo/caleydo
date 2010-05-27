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

package jmetest.TutorialGuide;

import java.net.URL;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.FirstPersonHandler;
import com.jme.input.MouseInput;
import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * Started Date: Jul 22, 2004 <br>
 * <br>
 *
 * Demonstrates picking with the mouse.
 *
 * @author Jack Lindamood
 */
public class HelloMousePick extends SimpleGame {
    private static final Logger logger = Logger.getLogger(HelloMousePick.class
            .getName());
    
	// This will be my mouse
	AbsoluteMouse am;

	// This will be he box in the middle
	Box b;

	PickResults pr;

	public static void main(String[] args) {
		HelloMousePick app = new HelloMousePick();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}

	protected void simpleInitGame() {
		// Create a new mouse. Restrict its movements to the display screen.
		am = new AbsoluteMouse("The Mouse", display.getWidth(), display
				.getHeight());

		// Get a picture for my mouse.
		TextureState ts = display.getRenderer().createTextureState();
        URL cursorLoc = HelloMousePick.class.getClassLoader().getResource(
                "jmetest/data/cursor/cursor1.png" );
        Texture t = TextureManager.loadTexture(cursorLoc, Texture.MinificationFilter.NearestNeighborNoMipMaps,
				Texture.MagnificationFilter.Bilinear);
		ts.setTexture(t);
		am.setRenderState(ts);

		// Make the mouse's background blend with what's already there
		BlendState as = display.getRenderer().createBlendState();
		as.setBlendEnabled(true);
		as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		as.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
		as.setTestEnabled(true);
		as.setTestFunction(BlendState.TestFunction.GreaterThan);
		am.setRenderState(as);

		// Get the mouse input device and assign it to the AbsoluteMouse
		// Move the mouse to the middle of the screen to start with
		am.setLocalTranslation(new Vector3f(display.getWidth() / 2, display
				.getHeight() / 2, 0));
		// Assign the mouse to an input handler
        am.registerWithInputHandler( input );

        // Create the box in the middle. Give it a bounds
		b = new Box("My Box", new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1));
		b.setModelBound(new BoundingBox() );
		b.updateModelBound();
		// Attach Children
		rootNode.attachChild(b);
		rootNode.attachChild(am);
		// Remove all the lightstates so we can see the per-vertex colors
		lightState.detachAll();
      b.setLightCombineMode( LightCombineMode.Off );
      pr = new BoundingPickResults();
      (( FirstPersonHandler ) input ).getMouseLookHandler().setEnabled( false );
   }

	protected void simpleUpdate() {
		// Get the mouse input device from the jME mouse
		// Is button 0 down? Button 0 is left click
		if (MouseInput.get().isButtonDown(0)) {
			Vector2f screenPos = new Vector2f();
			// Get the position that the mouse is pointing to
			screenPos.set(am.getHotSpotPosition().x, am.getHotSpotPosition().y);
			// Get the world location of that X,Y value
			Vector3f worldCoords = display.getWorldCoordinates(screenPos, 0);
			Vector3f worldCoords2 = display.getWorldCoordinates(screenPos, 1);
            logger.info( worldCoords.toString() );
            // Create a ray starting from the camera, and going in the direction
			// of the mouse's location
			Ray mouseRay = new Ray(worldCoords, worldCoords2
					.subtractLocal(worldCoords).normalizeLocal());
			// Does the mouse's ray intersect the box's world bounds?
			pr.clear();
			rootNode.findPick(mouseRay, pr);

			for (int i = 0; i < pr.getNumber(); i++) {
				pr.getPickData(i).getTargetMesh().setRandomColors();
			}
		}
	}
}