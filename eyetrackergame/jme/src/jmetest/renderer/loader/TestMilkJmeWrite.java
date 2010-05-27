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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.scene.Node;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.converters.MilkToJme;

/**
 * Started Date: Jun 8, 2004
 * This class test the ability to correctly read and write .ms3d scenegraph files.
 * 
 * @author Jack Lindamood
 */
public class TestMilkJmeWrite extends SimpleGame{
    private static final Logger logger = Logger
            .getLogger(TestMilkJmeWrite.class.getName());
    
    public static void main(String[] args) {
        new TestMilkJmeWrite().start();
    }

    protected void simpleInitGame() {
        try {
            ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_TEXTURE,
                    new SimpleResourceLocator(TestMilkJmeWrite.class
                            .getClassLoader().getResource(
                                    "jmetest/data/model/msascii/")));
        } catch (URISyntaxException e1) {
            logger.log(Level.WARNING, "unable to setup texture directory.", e1);
        }

        MilkToJme converter=new MilkToJme();
        URL MSFile=TestMilkJmeWrite.class.getClassLoader().getResource(
        "jmetest/data/model/msascii/run.ms3d");
        ByteArrayOutputStream BO=new ByteArrayOutputStream();

        try {
            converter.convert(MSFile.openStream(),BO);
        } catch (IOException e) {
            logger.info("IO problem writting the file!!!");
            logger.info(e.getMessage());
            System.exit(0);
        }

        Node i=null;
        try {
            i=(Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
        } catch (IOException e) {
            logger.info("darn exceptions:" + e.getMessage());
        }
        i.setLocalScale(.1f);
        rootNode.attachChild(i);
    }
}