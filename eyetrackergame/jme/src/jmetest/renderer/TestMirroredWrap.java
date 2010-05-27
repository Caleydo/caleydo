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
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestMirroredWrap</code>
 * @author Joshua Slack
 * @version $Id: TestMirroredWrap.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestMirroredWrap extends SimpleGame {

  public static void main(String[] args) {
    TestMirroredWrap app = new TestMirroredWrap();
    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();

  }
  
  protected void simpleInitGame() {
    display.setTitle("Mirrored Texture");
    lightState.setEnabled(false);
    
    Box box = new Box("a box", new Vector3f(), 120, .1f, 120); 
    box.setModelBound(new BoundingBox()); 
    box.updateModelBound(); 
    box.getLocalTranslation().y = -20;
    TextureState ts = display.getRenderer().createTextureState();
    Texture tex = TextureManager.loadTexture(
            TestEnvMap.class.getClassLoader().getResource(
            "jmetest/data/images/Monkey.png"),
        Texture.MinificationFilter.Trilinear,
        Texture.MagnificationFilter.Bilinear);
    tex.setWrap(Texture.WrapMode.MirroredRepeat);
    ts.setTexture(tex);
    box.setRenderState(ts); 
    box.scaleTextureCoordinates(0, 6);
    rootNode.attachChild(box); 
  }
}