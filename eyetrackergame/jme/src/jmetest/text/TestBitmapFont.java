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

package jmetest.text;

import java.net.URL;
import java.util.concurrent.Callable;

import com.jme.input.MouseInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial.CullHint;
import com.jme.util.GameTaskQueueManager;
import com.jmex.angelfont.BitmapFont;
import com.jmex.angelfont.BitmapFontLoader;
import com.jmex.angelfont.BitmapText;
import com.jmex.angelfont.Rectangle;
import com.jmex.angelfont.BitmapFont.Align;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;

/**
 * This test displays 3D text flatly on the screen.
 * 
 * @author Matthew D. Hicks
 */
public class TestBitmapFont {

    public static void main(String[] args) throws Exception {
        final StandardGame game = new StandardGame("Test BitmapFont & BitmapText");
        game.start();

        final String txtB = "This extension provides a mechanism\n to specify vertex attrib and "
                + "element array locations using GPU addresses.";
        
        final String txtC = "This extension provides a mechanism to specify vertex attrib and "
            + "element array locations using GPU addresses.";

        GameTaskQueueManager.getManager().update(new Callable<Void>() {

            public Void call() throws Exception {
                MouseInput.get().setCursorVisible(true);
                final DebugGameState debug = new DebugGameState();
                GameStateManager.getInstance().attachChild(debug);
                debug.setActive(true);

                Node orthoNode = new Node();

                // URL fontFile =
                // getClass().getClassLoader().getResource("jmetest/data/texture/angelFont.fnt");
                // URL textureFile =
                // getClass().getClassLoader().getResource("jmetest/data/texture/angelFont.png");
                // BitmapFont fnt = BitmapFontLoader.load(fontFile,
                // textureFile);

                BitmapFont fnt = BitmapFontLoader.loadDefaultFont();

                BitmapText txt = new BitmapText(fnt, false);
                txt.setBox(new Rectangle(10, -10, game.getDisplay().getWidth() - 20,
                                game.getDisplay().getHeight() - 20));
                txt.setSize(32);
                txt.setDefaultColor(ColorRGBA.green.clone());
                txt.setText(txtB);
                txt.update();

                BitmapText txt2 = new BitmapText(fnt, false);
                txt2.setBox(new Rectangle(10, (float) (-game.getDisplay().getHeight() * 0.3), game.getDisplay()
                        .getWidth() - 20, game.getDisplay().getHeight() - 20));
                txt2.setSize(32);
                txt2.setUseKerning(false);
                txt2.setAlignment(Align.Center);
                txt2.setDefaultColor(ColorRGBA.orange.clone());
                txt2.setText(txtB);
                txt2.update();

                BitmapText txt3 = new BitmapText(fnt, false);
                txt3.setBox(new Rectangle(10, (float) (-game.getDisplay().getHeight() * 0.6), game.getDisplay()
                        .getWidth() - 20, game.getDisplay().getHeight() - 20));
                txt3.setSize(32);
                txt3.setAlignment(Align.Right);
                txt3.setDefaultColor(ColorRGBA.blue.clone());
                txt3.setText(txtC);
                txt3.update();

                BitmapText txt4 = new BitmapText(fnt, false);
                txt4.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
                txt4.setSize(3);
                txt4.setAlignment(Align.Center);
                txt4.setText("Text without restriction.\n Text without\n restriction.\n Text without restriction. Text without restriction");
                txt4.setDefaultColor(ColorRGBA.red.clone());
                txt4.setLocalRotation(new Quaternion().fromAngleAxis(55 * FastMath.DEG_TO_RAD, new Vector3f(0, 1, 0)));
                txt4.update();

                txt4.setText("Shortened it!\n :)");
                txt4.update();

                txt4.setText("Elongated\n it to test! :)");
                txt4.update();
                
                debug.getRootNode().attachChild(txt4);

                orthoNode.setLocalTranslation(0, game.getDisplay().getHeight(), 0);
                orthoNode.setCullHint(CullHint.Never);
                orthoNode.attachChild(txt);
                orthoNode.attachChild(txt2);
                orthoNode.attachChild(txt3);

                debug.getRootNode().attachChild(orthoNode);
                return null;
            }
        });
    }
}
