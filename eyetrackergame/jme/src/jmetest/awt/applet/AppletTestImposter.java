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

package jmetest.awt.applet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.BillboardNode;
import com.jme.scene.Controller;
import com.jme.scene.ImposterNode;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.awt.applet.SimpleJMEApplet;
import com.jmex.model.animation.KeyframeController;
import com.jmex.model.converters.Md2ToJme;

public class AppletTestImposter extends SimpleJMEApplet {
    private static final Logger logger = Logger
            .getLogger(AppletTestImposter.class.getName());
    
    private static final long serialVersionUID = 1L;
    private Node fakeScene;

    private Node freakmd2;

    private String FILE_NAME = "jmetest/data/model/drfreak.md2";
    private String TEXTURE_NAME = "jmetest/data/model/drfreak.jpg";

    private ImposterNode iNode;
    
    public void simpleAppletSetup() {
        getCamera().setLocation(new Vector3f(0.0f, 0.0f, 25.0f));
        getCamera().update();

        // setup the scene to be 'impostered'
        
        Md2ToJme converter=new Md2ToJme();
        ByteArrayOutputStream BO=new ByteArrayOutputStream();

        URL textu=AppletTestImposter.class.getClassLoader().getResource(TEXTURE_NAME);
        URL freak=AppletTestImposter.class.getClassLoader().getResource(FILE_NAME);
        freakmd2=null;

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
        
        ((KeyframeController) freakmd2.getChild(0).getController(0)).setSpeed(10);
        ((KeyframeController) freakmd2.getChild(0).getController(0)).setRepeatType(Controller.RT_WRAP);
        fakeScene = new Node("Fake node");
        fakeScene.attachChild(freakmd2);
        TextureState ts = getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(
        TextureManager.loadTexture(
            textu,
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear));
        freakmd2.setRenderState(ts);
        // apply the appropriate texture to the imposter scene
        TextureState ts2 = getRenderer().createTextureState();
        ts2.setEnabled(true);
        ts2.setTexture(
            TextureManager.loadTexture(
            AppletTestImposter.class.getClassLoader().getResource(TEXTURE_NAME),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear));
        fakeScene.setRenderState(ts2);

        ZBufferState buf = getRenderer().createZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        fakeScene.setRenderState(buf);
        fakeScene.updateRenderState();

        // setup the imposter node...
        // we first determine a good texture size (must be equal to or less than the display size)
        int tSize = 256;
        if (DisplaySystem.getDisplaySystem().getHeight() > 512)
          tSize = 512;
        iNode = new ImposterNode("model imposter", 10, tSize, tSize);
        iNode.attachChild(fakeScene);
        iNode.setCameraDistance(100);
        iNode.setRedrawRate(.05f); // .05 = update texture 20 times a second on average
//        iNode.setCameraThreshold(15*FastMath.DEG_TO_RAD);

        // Now add the imposter to a Screen Aligned billboard so the deception is complete.
        BillboardNode bnode = new BillboardNode("imposter bbnode");
        bnode.setAlignment(BillboardNode.SCREEN_ALIGNED);
        bnode.attachChild(iNode);
        getRootNode().attachChild(bnode);
    }

    @Override
    public void simpleAppletRender() {
        if (status == STATUS_DESTROYING) {
            iNode.getTextureRenderer().cleanup();
            status = STATUS_DEAD;
        }
    }
    
    @Override
    public void stop() {
        status = STATUS_DESTROYING;
        long time = System.currentTimeMillis();
        while (status != STATUS_DEAD && (System.currentTimeMillis() - time) < 5000) { // only keep waiting for 5 secs
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.logp(Level.SEVERE, this.getClass().toString(), "stop()", "Exception", e);
            }
        }
        super.stop();
    }
}
