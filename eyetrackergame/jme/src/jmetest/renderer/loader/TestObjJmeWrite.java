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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.scene.Node;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.ObjToJme;

/**
 * Started Date: Jul 17, 2004<br><br>
 *
 * Test the ability to read and write obj files.
 * 
 * @author Jack Lindamood
 */
public class TestObjJmeWrite extends SimpleGame{
    private static final Logger logger = Logger.getLogger(TestObjJmeWrite.class
            .getName());
    
    public static void main(String[] args) {
        TestObjJmeWrite app=new TestObjJmeWrite();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }
    protected void simpleInitGame() {
        ObjToJme converter=new ObjToJme();
        try {
            URL objFile=TestObjJmeWrite.class.getClassLoader().getResource("jmetest/data/model/maggie.obj");
            converter.setProperty("mtllib",objFile);
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            logger.info("Starting to convert .obj to .jme");
            converter.convert(objFile.openStream(),BO);
            
            //jbr.setProperty("texurl",new File(".").toURL());
            logger.info("Done converting, now watch how fast it loads!");
            long time=System.currentTimeMillis();
            Node r=(Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
            logger.info("Finished loading time is "+(System.currentTimeMillis()-time));
            r.setLocalScale(.1f);
            rootNode.attachChild(r);
        } catch (IOException e) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "simpleInitGame()", "Exception", e);
        }
    }
}
