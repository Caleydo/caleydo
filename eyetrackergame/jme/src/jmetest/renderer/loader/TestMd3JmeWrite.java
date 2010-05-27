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
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.Md3ToJme;

/**
 * Started Date: Jul 15, 2004<br><br>
 *
 * Test the ability to load MD3 files.
 * 
 * @author Jack Lindamood
 */
public class TestMd3JmeWrite extends SimpleGame{
    private static final Logger logger = Logger.getLogger(TestMd3JmeWrite.class
            .getName());
    
    public static void main(String[] args) {
        TestMd3JmeWrite app=new TestMd3JmeWrite();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }
    protected void simpleInitGame() {
        Md3ToJme converter=new Md3ToJme();
        URL model=null;
        model=TestMd3JmeWrite.class.getClassLoader().getResource("jmetest/data/model/nordhorse.md3");
        URL tex=TestMd3JmeWrite.class.getClassLoader().getResource("jmetest/data/model/nordhorse_color.jpg");
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        try {
            converter.convert(model.openStream(),BO);
            logger.info("Done converting, now watch how fast it loads!");
            long time=System.currentTimeMillis();
            Spatial r=(Spatial)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
            r.setModelBound(new BoundingBox());
            r.updateModelBound();
            logger.info("Finished loading time is "+(System.currentTimeMillis()-time));
            TextureState ts=display.getRenderer().createTextureState();
            ts.setTexture(TextureManager.loadTexture(tex,Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear));
            ts.setEnabled(true);
            r.setRenderState(ts);
            rootNode.attachChild(r);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load Md3 file", e);
        }

        // move camera to other side.
        cam.getLocation().set(8, 8, -15);

        // force bounds updating down scenegraph
        rootNode.updateGeometricState(0, true);
        // Have camera look at center of bounds
        cam.lookAt(new Vector3f(rootNode.getWorldBound().getCenter()), Vector3f.UNIT_Y);
    }
}