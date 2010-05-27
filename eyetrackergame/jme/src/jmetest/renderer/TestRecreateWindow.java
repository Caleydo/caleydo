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

package jmetest.renderer;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Text;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestRecreateWindow</code>
 * @author Joshua Slack
 * @version $Id: TestRecreateWindow.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestRecreateWindow extends SimpleGame {
  private Quaternion rotQuat = new Quaternion();
  private float angle = 0;
  private Vector3f axis = new Vector3f(1, 1, 0);
  private Sphere s;
  private int resolution = -1;
  private int width, height;
  private int depth, freq;
  private boolean fs, changeScreen;
  private Text rez;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestRecreateWindow app = new TestRecreateWindow();
    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();
  }

  protected void simpleUpdate() {
    if (tpf < 1) {
      angle = angle + (tpf * 1);
      if (angle > 360) {
        angle = 0;
      }
    }
    rotQuat.fromAngleAxis(angle, axis);
    s.setLocalRotation(rotQuat);

    if (KeyBindingManager.getKeyBindingManager().isValidCommand("full_screen", false)) {
      changeScreen = true;
      fs = !fs;
    }

    if (KeyBindingManager.getKeyBindingManager().isValidCommand("resolution", false)) {
      changeScreen = true;
      resolution++;
      if (resolution > 2) resolution = 0;
      switch (resolution) {
        case 0:
          width = 640;
          height = 480;
          break;
        case 1:
          width = 800;
          height = 600;
          break;
        case 2:
          width = 1024;
          height = 768;
          break;
      }
    }

    if (changeScreen) {
      changeScreen = false;
      updateRezText();
      display.recreateWindow(width, height, depth, freq, fs);
    }
  }

  private void updateRezText() {
    String rezText = width + " x " + height + "  " +
        depth + "bpp  " +
        freq + "Hz " +
        (fs ? "(full screen)" : "(windowed)");
    rez.print(rezText);
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("Recreate Window Demo");
    KeyBindingManager.getKeyBindingManager().set("full_screen", KeyInput.KEY_F);
    KeyBindingManager.getKeyBindingManager().set("resolution", KeyInput.KEY_BACK);

    width = display.getWidth();
    height = display.getHeight();
    freq = display.getFrequency();
    depth = display.getBitDepth();
    fs = display.isFullScreen();

    s = new Sphere("Sphere", 63, 50, 25);
    s.setLocalTranslation(new Vector3f(0,0,-40));
    s.setModelBound(new BoundingBox());
    s.updateModelBound();
    rootNode.attachChild(s);

    TextureState ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    ts.setTexture(
        TextureManager.loadTexture(
        TestBoxColor.class.getClassLoader().getResource(
        "jmetest/data/images/Monkey.jpg"),
        Texture.MinificationFilter.Trilinear,
        Texture.MagnificationFilter.Bilinear));

    rootNode.setRenderState(ts);

    rez = Text.createDefaultTextLabel("rez", "rez");
    rez.setLocalTranslation(new Vector3f(0,20,0));
    updateRezText();
    statNode.attachChild(rez);
  }
}
