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
import java.nio.FloatBuffer;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

/**
 * Started Date: Jul 21, 2004<br><br>
 *
 * This program demonstrates using key inputs to change things.
 * 
 * @author Jack Lindamood
 */
public class HelloKeyInput extends SimpleGame {
    // The TriMesh that I will change
    TriMesh square;
    // A scale of my current texture values
    float coordDelta;
    public static void main(String[] args) {
        HelloKeyInput app = new HelloKeyInput();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        // Vertex positions for the mesh
        Vector3f[] vertexes={
            new Vector3f(0,0,0),
            new Vector3f(1,0,0),
            new Vector3f(0,1,0),
            new Vector3f(1,1,0)
        };

        // Texture Coordinates for each position
        coordDelta=1;
        Vector2f[] texCoords={
            new Vector2f(0,0),
            new Vector2f(coordDelta,0),
            new Vector2f(0,coordDelta),
            new Vector2f(coordDelta,coordDelta)
        };

        // The indexes of Vertex/Normal/Color/TexCoord sets.  Every 3 makes a triangle.
        int[] indexes={
            0,1,2,1,2,3
        };
        // Create the square
        square=new TriMesh("My Mesh",BufferUtils.createFloatBuffer(vertexes),null, null, TexCoords.makeNew(texCoords), BufferUtils.createIntBuffer(indexes));
        // Point to the monkey image
        URL monkeyLoc=HelloKeyInput.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg");
        // Get my TextureState
        TextureState ts=display.getRenderer().createTextureState();
        // Get my Texture
        Texture t=TextureManager.loadTexture(monkeyLoc,Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear);
        // Set a wrap for my texture so it repeats
        t.setWrap(Texture.WrapMode.Repeat);
        // Set the texture to the TextureState
        ts.setTexture(t);

        // Assign the TextureState to the square
        square.setRenderState(ts);
        // Scale my square 10x larger
        square.setLocalScale(10);
        // Attach my square to my rootNode
        rootNode.attachChild(square);

        // Assign the "+" key on the keypad to the command "coordsUp"
        KeyBindingManager.getKeyBindingManager().set(
            "coordsUp",
            KeyInput.KEY_ADD);

        // Adds the "u" key to the command "coordsUp"
        KeyBindingManager.getKeyBindingManager().add(
            "coordsUp",
            KeyInput.KEY_U);

        // Assign the "-" key on the keypad to the command "coordsDown"
        KeyBindingManager.getKeyBindingManager().set(
            "coordsDown",
            KeyInput.KEY_SUBTRACT);
    }

    // Called every frame update
    protected void simpleUpdate(){

        boolean updateTex = false;
        // If the coordsDown command was activated
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("coordsDown",true)){
            // Scale my texture down
            coordDelta-=.01f;
            updateTex = true;
        }
        // if the coordsUp command was activated
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("coordsUp",true)){
            // Scale my texture up
            coordDelta+=.01f;
            updateTex = true;
        }
        
        if (updateTex) {
            // Get my square's texture array
            FloatBuffer texBuf = square.getTextureCoords(0).coords;
            texBuf.rewind().position(2); // start after the 1st texcoord (2 floats wide)
            // Change the values of the texture coords in the buffer
            texBuf.put(coordDelta).put(0);
            texBuf.put(0).put(coordDelta);
            texBuf.put(coordDelta).put(coordDelta);            
        }
    }
}