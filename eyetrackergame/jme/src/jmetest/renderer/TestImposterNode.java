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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import jmetest.renderer.loader.TestMd2JmeWrite;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.BillboardNode;
import com.jme.scene.Controller;
import com.jme.scene.ImposterNode;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.animation.KeyframeController;
import com.jmex.model.converters.Md2ToJme;

/**
 * <code>TestImposterNode</code> shows off the use of the ImposterNode in jME.
 * @author Joshua Slack
 * @version $Id: TestImposterNode.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestImposterNode extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestImposterNode.class.getName());
    
  private Node fakeScene;

  private Node freakmd2;

  private String FILE_NAME = "jmetest/data/model/drfreak.md2";
  private String TEXTURE_NAME = "jmetest/data/model/drfreak.jpg";

  private ImposterNode iNode;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestImposterNode app = new TestImposterNode();
    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("Imposter Test");
    cam.setLocation(new Vector3f(0.0f, 0.0f, 25.0f));
    cam.update();

    // setup the scene to be 'impostered'
    
    Md2ToJme converter=new Md2ToJme();
    ByteArrayOutputStream BO=new ByteArrayOutputStream();

    URL freak=TestMd2JmeWrite.class.getClassLoader().getResource(FILE_NAME);
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
    
    // apply the appropriate texture to the imposter scene
    TextureState ts2 = display.getRenderer().createTextureState();
    ts2.setEnabled(true);
    ts2.setTexture(
        TextureManager.loadTexture(
        TestImposterNode.class.getClassLoader().getResource(TEXTURE_NAME),
        Texture.MinificationFilter.Trilinear,
        Texture.MagnificationFilter.Bilinear));
    fakeScene.setRenderState(ts2);

    ZBufferState buf = display.getRenderer().createZBufferState();
    buf.setEnabled(true);
    buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
    fakeScene.setRenderState(buf);
    fakeScene.updateRenderState();

    // setup the imposter node...
    iNode = new ImposterNode("model imposter", 10, display.getWidth(), display.getHeight());
    iNode.attachChild(fakeScene);
    iNode.setCameraDistance(100);
    iNode.setRedrawRate(.05f); // .05 = update texture 20 times a second on average
//    iNode.setCameraThreshold(15*FastMath.DEG_TO_RAD);

    // Now add the imposter to a Screen Aligned billboard so the deception is complete.
    BillboardNode bnode = new BillboardNode("imposter bbnode");
    bnode.setAlignment(BillboardNode.SCREEN_ALIGNED);
    bnode.attachChild(iNode);
    rootNode.attachChild(bnode);
  }
  
    @Override
    protected void cleanup() {
        iNode.getTextureRenderer().cleanup();
        super.cleanup();
    }
}
