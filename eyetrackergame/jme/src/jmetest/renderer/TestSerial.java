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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jmetest.curve.TestBezierCurve;
import jmetest.renderer.state.TestTextureState;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.curve.BezierCurve;
import com.jme.curve.CurveController;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.geom.BufferUtils;
import com.jmex.model.converters.Md2ToJme;

/**
 * Started Date: Jul 5, 2004<br><br>
 * Test the Serializability of jME's scenegraph.
 * 
 * @author Jack Lindamood
 */
public class TestSerial extends SimpleGame{
    private static final Logger logger = Logger.getLogger(TestSerial.class
            .getName());

    ByteArrayOutputStream skybox;
    ByteArrayOutputStream freaky;
    ByteArrayOutputStream curve;
    Node mainNode=new Node("blarg");


    public static void main(String[] args){
        TestSerial app=new TestSerial();
        JOptionPane.showMessageDialog(null,"This will take a while to load.\nPress U to load Dr.Freak, Press O to load skybox, Press I to load curve");
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("load_freak", false)) {

            try {
                rootNode.detachChild(mainNode);
                mainNode=(Node) new ObjectInputStream(new ByteArrayInputStream(freaky.toByteArray())).readObject();
                rootNode.attachChild(mainNode);
                rootNode.updateRenderState();
            } catch (IOException e) {
                logger.logp(Level.SEVERE, this.getClass().toString(),
                        "simpleUpdate()", "Exception", e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                logger.logp(Level.SEVERE, this.getClass().toString(),
                        "simpleUpdate()", "Exception", e);
                System.exit(0);
            }
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("load_sky", false)) {
            try {
                rootNode.detachChild(mainNode);
                mainNode=(Node) new ObjectInputStream(new ByteArrayInputStream(skybox.toByteArray())).readObject();
                rootNode.attachChild(mainNode);
                rootNode.updateRenderState();
            } catch (IOException e) {
                logger.logp(Level.SEVERE, this.getClass().toString(),
                        "simpleUpdate()", "Exception", e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                logger.logp(Level.SEVERE, this.getClass().toString(),
                        "simpleUpdate()", "Exception", e);
                System.exit(0);
            }
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("load_curve", false)) {
            try {
                rootNode.detachChild(mainNode);
                mainNode=(Node) new ObjectInputStream(new ByteArrayInputStream(curve.toByteArray())).readObject();
                rootNode.attachChild(mainNode);
                rootNode.updateRenderState();
            } catch (IOException e) {
                logger.logp(Level.SEVERE, this.getClass().toString(),
                        "simpleUpdate()", "Exception", e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                logger.logp(Level.SEVERE, this.getClass().toString(),
                        "simpleUpdate()", "Exception", e);
                System.exit(0);
            }
        }

    }

    protected void simpleInitGame() {
        logger.info("Requesting skybox");
        skybox=getSkyBox();
        try {
            logger.info("requesting drfreak");
            freaky=getFreaky();
            logger.info("Requesting curve");
            curve=getCurve();
        } catch (IOException e) {
            throw new JmeException("damn");
        }
        KeyBindingManager.getKeyBindingManager().set("load_sky",KeyInput.KEY_O);
        KeyBindingManager.getKeyBindingManager().set("load_freak",KeyInput.KEY_U);
        KeyBindingManager.getKeyBindingManager().set("load_curve",KeyInput.KEY_I);
        rootNode.attachChild(mainNode);
    }

    private ByteArrayOutputStream getSkyBox() {
        Node toReturn=new Node("sky");
        Skybox m_skybox;
        Torus t = new Torus("Torus", 20, 20, 5, 10);
        t.setModelBound(new BoundingBox());
        t.updateModelBound();
        t.setLocalTranslation(new Vector3f(-40, 0, 10));
        t.setVBOInfo(new VBOInfo(true));
        toReturn.attachChild(t);

        Sphere s = new Sphere("Sphere", 20, 20, 25);
        s.setModelBound(new BoundingBox());
        s.updateModelBound();
        s.setLocalTranslation(new Vector3f(40, 0, -10));
        toReturn.attachChild(s);
        s.setVBOInfo(new VBOInfo(true));

        Box b = new Box("box", new Vector3f(-25, 70, -45), 20, 20, 20);
        b.setModelBound(new BoundingBox());
        b.updateModelBound();
        b.setVBOInfo(new VBOInfo(true));
        toReturn.attachChild(b);


        // Create a skybox
        // we pick 570 because our clip plane is at 1000 -- see SimpleGame
        // (570^2 + 570^2 + 570^2)^.5 = ~988 so it won't get clipped.
        // If our scene has stuff larger than will fit in the box, we'll
        // need to increase max clip.
        m_skybox = new Skybox("skybox", 570, 570, 570);

        Texture north = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/north.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear);
        Texture south = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/south.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear);
        Texture east = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/east.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear);
        Texture west = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/west.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear);
        Texture up = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/top.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear);
        Texture down = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/bottom.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear);

        m_skybox.setTexture(Skybox.Face.North, north);
        m_skybox.setTexture(Skybox.Face.West, west);
        m_skybox.setTexture(Skybox.Face.South, south);
        m_skybox.setTexture(Skybox.Face.East, east);
        m_skybox.setTexture(Skybox.Face.Up, up);
        m_skybox.setTexture(Skybox.Face.Down, down);
        toReturn.attachChild(m_skybox);
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos=new ObjectOutputStream(BO);
            oos.writeObject(toReturn);
            return BO;
        } catch (IOException e) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "getSkyBox()", "Exception", e);
        }
        return null;
    }

    private ByteArrayOutputStream getFreaky() throws IOException {
        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
                TestTextureState.class.getClassLoader().getResource("jmetest/data/model/drfreak.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear));
        Md2ToJme mtj=new Md2ToJme();
        ByteArrayOutputStream BO2=new ByteArrayOutputStream();

        mtj.convert(TestSerial.class.getClassLoader().getResource("jmetest/data/model/drfreak.md2").openStream(),BO2);
        Node it=(Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO2.toByteArray()));
        it.getChild(0).getController(0).setSpeed(10);
        it.setRenderState(ts);
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos=new ObjectOutputStream(BO);
            oos.writeObject(it);
            return BO;
        } catch (IOException e) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "getFreaky()", "Exception", e);
        }
        return null;
    }

    private ByteArrayOutputStream getCurve(){
        Vector3f up = new Vector3f(0, 1, 0);
        //create control Points
        Vector3f[] points = new Vector3f[4];
        points[0] = new Vector3f( -4, 0, 0);
        points[1] = new Vector3f( -2, 3, 2);
        points[2] = new Vector3f(2, -3, -2);
        points[3] = new Vector3f(4, 0, 0);

        BezierCurve curve = new BezierCurve("Curve", points);
        ColorRGBA[] colors = new ColorRGBA[4];
        colors[0] = new ColorRGBA(0, 1, 0, 1);
        colors[1] = new ColorRGBA(1, 0, 0, 1);
        colors[2] = new ColorRGBA(1, 1, 0, 1);
        colors[3] = new ColorRGBA(0, 0, 1, 1);
        curve.setColorBuffer(BufferUtils.createFloatBuffer(colors));

        Vector3f min = new Vector3f( -0.1f, -0.1f, -0.1f);
        Vector3f max = new Vector3f(0.1f, 0.1f, 0.1f);

        ZBufferState buf = display.getRenderer().createZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);

        TriMesh t = new Box("Control 1", min, max);
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();

        t.setLocalTranslation(points[0]);

        TriMesh t2 = new Box("Control 2", min, max);
        t2.setModelBound(new BoundingSphere());
        t2.updateModelBound();

        t2.setLocalTranslation(points[1]);

        TriMesh t3 = new Box("Control 3", min, max);
        t3.setModelBound(new BoundingSphere());
        t3.updateModelBound();

        t3.setLocalTranslation(points[2]);

        TriMesh t4 = new Box("Control 4", min, max);
        t4.setModelBound(new BoundingSphere());
        t4.updateModelBound();

        t4.setLocalTranslation(points[3]);

        TriMesh box = new Box("Controlled Box", min.mult(5), max.mult(5));
        box.setModelBound(new BoundingSphere());
        box.updateModelBound();

        box.setLocalTranslation(points[0]);

        CurveController cc = new CurveController(curve, box);
        box.addController(cc);
        cc.setRepeatType(Controller.RT_CYCLE);
        cc.setUpVector(up);
        cc.setSpeed(0.5f);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
            TestBezierCurve.class.getClassLoader().getResource(
            "jmetest/data/images/Monkey.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear));
        box.setRenderState(ts);

        Node it=new Node("blargggg");

        it.setRenderState(buf);
        it.attachChild(t);
        it.attachChild(t2);
        it.attachChild(t3);
        it.attachChild(t4);
        it.attachChild(box);
        it.attachChild(curve);
        it.setLocalScale(10);
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos=new ObjectOutputStream(BO);
            oos.writeObject(it);
            return BO;
        } catch (IOException e) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "getCurve()", "Exception", e);
        }
        return null;
    }
}
