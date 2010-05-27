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

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.MaterialState.ColorMaterial;

/**
 * TestPong
 */
public class TestPong extends SimpleGame {
    // Side walls and goal detectors
    private Node sideWalls;
    private Box player1GoalWall;
    private Box player2GoalWall;

    // Ball
    private Sphere ball;
    private Vector3f ballVelocity;

    // Player 1
    private Box player1;
    private float player1Speed = 100.0f;
    private int player1Score = 0;
    private Text player1ScoreText;

    // Player 2
    private Box player2;
    private float player2Speed = 100.0f;
    private int player2Score = 0;
    private Text player2ScoreText;

    public static void main(String[] args) {
        TestPong app = new TestPong();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        // Player 1 movement
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER1_MOVE_UP", true)) {
            player1.getLocalTranslation().z -= player1Speed* timer.getTimePerFrame();
        }
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER1_MOVE_DOWN", true)) {
            player1.getLocalTranslation().z += player1Speed* timer.getTimePerFrame();
        }
        player1.getLocalTranslation().z = FastMath.clamp(player1.getLocalTranslation().z, -38, 38);

        // Player 2 movement
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER2_MOVE_UP", true)) {
            player2.getLocalTranslation().z -= player2Speed* timer.getTimePerFrame();
        }
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER2_MOVE_DOWN", true)) {
            player2.getLocalTranslation().z += player2Speed* timer.getTimePerFrame();
        }
        player2.getLocalTranslation().z = FastMath.clamp(player2.getLocalTranslation().z, -38, 38);

        // Collision with player pads
        if (player1.hasCollision(ball, false) || player2.hasCollision(ball, false)) {
            ballVelocity.x *= -1f;
        }
        // Collision with side walls
        if (sideWalls.hasCollision(ball, false)) {
            ballVelocity.z *= -1f;
        }

        // Checking for goals (ie collision with back walls)
        if (player1GoalWall.hasCollision(ball, false)) {
            player1Score++;
            player1ScoreText.getText().replace(0, player1ScoreText.getText().length(), "" + player1Score);
            ball.getLocalTranslation().set(0,0,0);
        } else if (player2GoalWall.hasCollision(ball, false)) {            
            player2Score++;
            player2ScoreText.getText().replace(0, player2ScoreText.getText().length(), "" + player2Score);
            ball.getLocalTranslation().set(0,0,0);
        }

        // Move ball according to velocity
        ball.getLocalTranslation().addLocal(ballVelocity.mult(timer.getTimePerFrame()));
    }

    protected void simpleInitGame() {
        display.setTitle("jME - Pong");

        // Initialize camera
        cam.setFrustumPerspective(45.0f, (float) display.getWidth()
                / (float) display.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(-150, 200, 80));
        cam.lookAt(new Vector3f(-30, 0, 10), Vector3f.UNIT_Y);
        cam.update();

        // Create ball
        ball = new Sphere("Ball", 8, 8, 2);
        ball.setModelBound(new BoundingSphere());
        ball.updateModelBound();
        ball.setDefaultColor(ColorRGBA.blue);
        rootNode.attachChild(ball);

        // Initialize ball velocity
        ballVelocity = new Vector3f(100f, 0f, 50f);

        // Create Player 1 pad
        player1 = new Box("Player1", new Vector3f(), 2, 5, 10);
        player1.setModelBound(new BoundingBox());
        player1.updateModelBound();
        player1.getLocalTranslation().set(-100, 0, 0);
        player1.setDefaultColor(ColorRGBA.green);
        rootNode.attachChild(player1);

        // Create Player 2 pad
        player2 = new Box("Player2", new Vector3f(), 2, 5, 10);
        player2.setModelBound(new BoundingBox());
        player2.updateModelBound();
        player2.getLocalTranslation().set(100, 0, 0);
        player2.setDefaultColor(ColorRGBA.green);
        rootNode.attachChild(player2);

        // Create side walls
        sideWalls = new Node("Walls");
        rootNode.attachChild(sideWalls);

        Box wall = new Box("Wall1", new Vector3f(), 112, 2, 2);
        wall.setModelBound(new BoundingBox());
        wall.updateModelBound();
        wall.getLocalTranslation().set(0, 0, 50);
        sideWalls.attachChild(wall);

        wall = new Box("Wall2", new Vector3f(), 112, 2, 2);
        wall.setModelBound(new BoundingBox());
        wall.updateModelBound();
        wall.getLocalTranslation().set(0, 0, -50);
        sideWalls.attachChild(wall);

        // Create back wall, goal detector for player 1
        player1GoalWall = new Box("player1GoalWall", new Vector3f(), 2, 2, 50);
        player1GoalWall.setModelBound(new BoundingBox());
        player1GoalWall.updateModelBound();
        player1GoalWall.getLocalTranslation().set(110, 0, 0);
        rootNode.attachChild(player1GoalWall);

        // Create back wall, goal detector for player 2
        player2GoalWall = new Box("player2GoalWall", new Vector3f(), 2, 2, 50);
        player2GoalWall.setModelBound(new BoundingBox());
        player2GoalWall.updateModelBound();
        player2GoalWall.getLocalTranslation().set(-110, 0, 0);
        rootNode.attachChild(player2GoalWall);

        // Assign key bindings
		KeyBindingManager.getKeyBindingManager().set("PLAYER1_MOVE_UP", KeyInput.KEY_1);
		KeyBindingManager.getKeyBindingManager().set("PLAYER1_MOVE_DOWN", KeyInput.KEY_2);
		KeyBindingManager.getKeyBindingManager().set("PLAYER2_MOVE_UP", KeyInput.KEY_9);
		KeyBindingManager.getKeyBindingManager().set("PLAYER2_MOVE_DOWN", KeyInput.KEY_0);

        // Create score showing items
        player1ScoreText = Text.createDefaultTextLabel("player1ScoreText", "0");
        player1ScoreText.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        player1ScoreText.setLightCombineMode(Spatial.LightCombineMode.Off);
        player1ScoreText.setLocalTranslation(new Vector3f(0, display.getHeight()/2, 1));
        rootNode.attachChild(player1ScoreText);

        player2ScoreText = Text.createDefaultTextLabel("player2ScoreText", "0");
        player2ScoreText.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        player2ScoreText.setLightCombineMode(Spatial.LightCombineMode.Off);
        player2ScoreText.setLocalTranslation(new Vector3f(display.getWidth() - 30, display.getHeight()/2, 1));
        rootNode.attachChild(player2ScoreText);

        // Make the object default colors shine through
        MaterialState ms = display.getRenderer().createMaterialState();
        ms.setColorMaterial(ColorMaterial.AmbientAndDiffuse);
        rootNode.setRenderState(ms);
    }
}
