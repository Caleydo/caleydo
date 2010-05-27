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

package jmetest.renderer.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.animation.KeyframeController;
import com.jmex.model.converters.Md2ToJme;

/**
 * Started Date: Jun 14, 2004<br><br>
 * Test class to test the ability to load and save .md2 files to jME binary format
 * 
 * @author Jack Lindamood
 */
public class TestMd2JmeWrite extends SimpleGame{
    private static final Logger logger = Logger.getLogger(TestMd2JmeWrite.class
            .getName());
    
    float totalFPS;
    long totalCounts;
    private KeyframeController kc;
    private static final String helpMessage="Fun with KeyframeController and md2 models.  Keys are:\n" +
            "R: Make drFreak run\n" +
            "H: Make drFreak attack\n" +
            "Z: Toggle repeat type wrap and cycle\n" +
            "E: Do a quick transform to the begining\n" +
            "B: Do a smooth transform to the begining\n" +
            "Q: Do a smooth transform to drfreak's death\n";

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null,helpMessage);
        TestMd2JmeWrite app=new TestMd2JmeWrite();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }
    protected void simpleInitGame() {

        Md2ToJme converter=new Md2ToJme();
        ByteArrayOutputStream BO=new ByteArrayOutputStream();

        URL textu=TestMd2JmeWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.jpg");
        URL freak=TestMd2JmeWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.md2");

        
        Node freakmd2=null;

        try {
            long time = System.currentTimeMillis();
            converter.convert(freak.openStream(),BO);
            logger.info("Time to convert from md2 to .jme:"+ ( System.currentTimeMillis()-time));
        } catch (IOException e) {
            logger.info("damn exceptions:" + e.getMessage());
        }

        try {
            long time=System.currentTimeMillis();
            freakmd2=(Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
            logger.info("Time to convert from .jme to SceneGraph:"+ ( System.currentTimeMillis()-time));
        } catch (IOException e) {
            logger.info("damn exceptions:" + e.getMessage());
        }

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(
        TextureManager.loadTexture(
            textu,
            Texture.MinificationFilter.Trilinear,
            Texture.MagnificationFilter.Bilinear));
        freakmd2.setRenderState(ts);
        
        MaterialState ms = display.getRenderer().createMaterialState();
        ms.setSpecular(new ColorRGBA(0,0,0,1));
        ms.setShininess(128f);
        ms.setMaterialFace(MaterialState.MaterialFace.FrontAndBack);
        freakmd2.setRenderState(ms);
        
        freakmd2.setLocalTranslation(new Vector3f(0,0,-20));
        freakmd2.setLocalScale(.5f);
        kc=(KeyframeController) freakmd2.getChild(0).getController(0);
        kc.setSpeed(10);
        kc.setRepeatType(Controller.RT_WRAP);
        lightState.setGlobalAmbient(new ColorRGBA(ColorRGBA.white.clone()));
        lightState.get(0).setAmbient(new ColorRGBA(ColorRGBA.white.clone()));
        // Note: W S A D Left Down Up Right F12 ESC T L B C Already used
        KeyBindingManager.getKeyBindingManager().set("start_run",KeyInput.KEY_R);
        KeyBindingManager.getKeyBindingManager().set("start_hit",KeyInput.KEY_H);
        KeyBindingManager.getKeyBindingManager().set("toggle_wrap",KeyInput.KEY_Z);
        KeyBindingManager.getKeyBindingManager().set("start_end",KeyInput.KEY_E);
        KeyBindingManager.getKeyBindingManager().set("start_smoothbegin",KeyInput.KEY_B);
        KeyBindingManager.getKeyBindingManager().set("start_smoothdeath",KeyInput.KEY_Q);
        rootNode.attachChild(freakmd2);
     }
    protected void simpleUpdate(){
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_run", false)) {
            kc.setNewAnimationTimes(39,44);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_hit", false)) {
            kc.setNewAnimationTimes(45,52);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_end", false)) {
            kc.setNewAnimationTimes(0,196);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_smoothbegin", false)) {
            kc.setSmoothTranslation(0,25,0,196);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_smoothdeath", false)) {
            kc.setSmoothTranslation(175,25,175,182);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("toggle_wrap", false)) {
            if (kc.getRepeatType()==Controller.RT_CYCLE)
                kc.setRepeatType(Controller.RT_WRAP);
            else
                kc.setRepeatType(Controller.RT_CYCLE);
        }
    }
 }