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

package jmetest.input;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Text;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestAbsoluteMouse</code>
 * 
 * @author Mark Powell
 * @version
 */
public class TestAbsoluteMouse extends SimpleGame {

    private Text text;
    private AbsoluteMouse mouse;

    public static void main(String[] args) {
        TestAbsoluteMouse app = new TestAbsoluteMouse();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        text.print("Position: " + mouse.getLocalTranslation().x + " , "
                + mouse.getLocalTranslation().y);
    }

    protected void simpleInitGame() {
        lightState.setEnabled(false);
        display.getRenderer().setBackgroundColor(ColorRGBA.blue.clone());
        mouse = new AbsoluteMouse("Mouse Input", display.getWidth(), display
                .getHeight());
        TextureState cursor = display.getRenderer().createTextureState();
        cursor.setEnabled(true);
        cursor.setTexture(TextureManager.loadTexture(TestAbsoluteMouse.class
                .getClassLoader().getResource("jmetest/data/cursor/testcursor.png"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear));
        mouse.setRenderState(cursor);
        mouse.registerWithInputHandler(input);

        text = Text.createDefaultTextLabel("Text Label", "Testing Mouse");
        text.setLocalTranslation(new Vector3f(1, 60, 0));
        BlendState as1 = display.getRenderer().createBlendState();
        as1.setBlendEnabled(true);
        as1.setSourceFunction(BlendState.SourceFunction.One);
        as1.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceColor);
        as1.setTestEnabled(true);
        as1.setTestFunction(BlendState.TestFunction.GreaterThan);
        mouse.setRenderState(as1);
        rootNode.attachChild(text);
        rootNode.attachChild(mouse);
    }
}
