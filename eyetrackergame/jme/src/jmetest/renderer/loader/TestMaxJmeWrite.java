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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.MaxToJme;

/**
 * Started Date: Jun 26, 2004<br>
 * <br>
 * This class test the ability to save and write .3ds files
 * 
 * @author Jack Lindamood
 */
public class TestMaxJmeWrite extends SimpleGame {
    private static final Logger logger = Logger.getLogger(TestMaxJmeWrite.class
            .getName());
    
    public static void main(String[] args) {

        TestMaxJmeWrite app = new TestMaxJmeWrite();
        if (args.length > 0) {
            app.setModelToLoad(args[0]);
        }
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    private URL modelToLoad = null;

    private void setModelToLoad(String string) {
        try {
            modelToLoad = (new File(string)).toURI().toURL();
        } catch (MalformedURLException e) {
        }
    }

    protected void simpleInitGame() {
        if (modelToLoad == null) {
            modelToLoad = TestMaxJmeWrite.class.getClassLoader().getResource(
                    "jmetest/data/model/char.3ds");
        }
        try {
            MaxToJme C1 = new MaxToJme();
            ByteArrayOutputStream BO = new ByteArrayOutputStream();
            C1.convert(new BufferedInputStream(modelToLoad.openStream()), BO);
            Node r1 = (Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
            //Node r = new Node("parent stuff");
            //r.attachChild(C1.get(new BufferedInputStream(modelToLoad.openStream()), BO));
            //r.setLocalScale(.1f);
            r1.setLocalScale(.1f);
            if (r1.getChild(0).getControllers().size() != 0)
                r1.getChild(0).getController(0).setSpeed(20);
            
            Quaternion temp = new Quaternion();
            temp.fromAngleAxis(FastMath.PI / 2, new Vector3f(-1, 0, 0));
            rootNode.setLocalRotation(temp);
            r1.setLocalTranslation(new Vector3f(10,0,0));
            //rootNode.attachChild(r);
            rootNode.attachChild(r1);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load Max file", e);
        }
    }
}
