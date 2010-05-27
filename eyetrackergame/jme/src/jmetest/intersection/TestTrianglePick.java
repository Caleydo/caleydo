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

package jmetest.intersection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.MouseInput;
import com.jme.intersection.PickData;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.scene.Point;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.geom.BufferUtils;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.ObjToJme;

/**
 * Started Date: Jul 22, 2004 <br>
 * <br>
 * Demonstrates picking with the mouse.
 * 
 * @author Jack Lindamood
 */
public class TestTrianglePick extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestTrianglePick.class.getName());

    // This will be my mouse
    AbsoluteMouse am;

    private Point pointSelection;

    Spatial maggie;

    private Line[] selection;

    public static void main(String[] args) {
        TestTrianglePick app = new TestTrianglePick();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        // Create a new mouse. Restrict its movements to the display screen.
        am = new AbsoluteMouse("The Mouse", display.getWidth(), display
                .getHeight());

        // Get a picture for my mouse.
        TextureState ts = display.getRenderer().createTextureState();
        URL cursorLoc = TestTrianglePick.class.getClassLoader().getResource(
                "jmetest/data/cursor/cursor1.png");
        Texture t = TextureManager.loadTexture(cursorLoc, Texture.MinificationFilter.NearestNeighborNoMipMaps,
                Texture.MagnificationFilter.Bilinear);
        ts.setTexture(t);
        am.setRenderState(ts);

        // Make the mouse's background blend with what's already there
        BlendState as = display.getRenderer().createBlendState();
        as.setBlendEnabled(true);
        as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        as.setTestEnabled(true);
        as.setTestFunction(BlendState.TestFunction.GreaterThan);
        am.setRenderState(as);

        // Move the mouse to the middle of the screen to start with
        am.setLocalTranslation(new Vector3f(display.getWidth() / 2, display
                .getHeight() / 2, 0));
        // Assign the mouse to an input handler
        am.registerWithInputHandler(input);

        // Create the box in the middle. Give it a bounds
        URL model = TestTrianglePick.class.getClassLoader().getResource(
                "jmetest/data/model/maggie.obj");
        try {
            FormatConverter converter = new ObjToJme();
            converter.setProperty("mtllib", model);
            ByteArrayOutputStream BO = new ByteArrayOutputStream();
            converter.convert(model.openStream(), BO);
            maggie = (Spatial) BinaryImporter.getInstance().load(
                    new ByteArrayInputStream(BO.toByteArray()));
            // scale rotate and translate to confirm that world transforms are
            // handled
            // correctly.
            maggie.setLocalScale(.1f);
            maggie.setLocalTranslation(new Vector3f(3, 1, -5));
            Quaternion q = new Quaternion();
            q.fromAngleAxis(0.5f, new Vector3f(0, 1, 0));
            maggie.setLocalRotation(q);
        } catch (IOException e) { // Just in case anything happens
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "simpleInitGame()", "Exception", e);
            System.exit(0);
        }

        maggie.setModelBound(new BoundingSphere());
        maggie.updateModelBound();
        // Attach Children
        rootNode.attachChild(maggie);
        rootNode.attachChild(am);

        maggie.lockBounds();
        maggie.lockTransforms();
        results.setCheckDistance(true);

        pointSelection = new Point("selected triangle", new Vector3f[1], null,
                new ColorRGBA[1], null);
        pointSelection.setSolidColor(new ColorRGBA(1, 0, 0, 1));
        pointSelection.setPointSize(10);
        pointSelection.setAntialiased(true);
        ZBufferState zbs = display.getRenderer().createZBufferState();
        zbs.setFunction(ZBufferState.TestFunction.Always);
        pointSelection.setRenderState(zbs);
        pointSelection.setLightCombineMode(Spatial.LightCombineMode.Off);

        rootNode.attachChild(pointSelection);
    }

    private void createSelectionTriangles(int number) {
        clearPreviousSelections();
        selection = new Line[number];
        for (int i = 0; i < selection.length; i++) {
            selection[i] = new Line("selected triangle" + i, new Vector3f[4],
                    null, new ColorRGBA[4], null);
            selection[i].setSolidColor(new ColorRGBA(0, 1, 0, 1));
            selection[i].setLineWidth(5);
            selection[i].setAntialiased(true);
            selection[i].setMode(Line.Mode.Connected);

            ZBufferState zbs = display.getRenderer().createZBufferState();
            zbs.setFunction(ZBufferState.TestFunction.Always);
            selection[i].setRenderState(zbs);
            selection[i].setLightCombineMode(Spatial.LightCombineMode.Off);

            rootNode.attachChild(selection[i]);
        }

        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderState();
    }

    private void clearPreviousSelections() {
        if (selection != null) {
            for (Line line : selection) {
                rootNode.detachChild(line);
            }
        }
    }

    TrianglePickResults results = new TrianglePickResults() {

        public void processPick() {

            // initialize selection triangles, this can go across multiple
            // target
            // meshes.
            int total = 0;
            for (int i = 0; i < getNumber(); i++) {
                total += getPickData(i).getTargetTris().size();
            }
            createSelectionTriangles(total);
            if (getNumber() > 0) {
                int previous = 0;
                for (int num = 0; num < getNumber(); num++) {
                    PickData pData = getPickData(num);
                    List<Integer> tris = pData.getTargetTris();
                    TriMesh mesh = (TriMesh) pData.getTargetMesh();

                    for (int i = 0; i < tris.size(); i++) {
                        int triIndex = tris.get(i);
                        Vector3f[] vec = new Vector3f[3];
                        mesh.getTriangle(triIndex, vec);
                        FloatBuffer buff = selection[i + previous]
                                .getVertexBuffer();

                        for (Vector3f v : vec) {
                            v.multLocal(mesh.getWorldScale());
                            mesh.getWorldRotation().mult(v, v);
                            v.addLocal(mesh.getWorldTranslation());
                        }

                        BufferUtils.setInBuffer(vec[0], buff, 0);
                        BufferUtils.setInBuffer(vec[1], buff, 1);
                        BufferUtils.setInBuffer(vec[2], buff, 2);
                        BufferUtils.setInBuffer(vec[0], buff, 3);

                        if (num == 0 && i == 0) {
                            selection[i + previous]
                                    .setSolidColor(new ColorRGBA(1, 0, 0, 1));
                            Vector3f loc = new Vector3f();
                            pData.getRay().intersectWhere(vec[0], vec[1],
                                    vec[2], loc);
                            BufferUtils.setInBuffer(loc, pointSelection
                                    .getVertexBuffer(), 0);
                        }
                    }

                    previous = tris.size();
                }
            }
        }
    };

    // This is called every frame. Do changing of values here.
    protected void simpleUpdate() {

        // Is button 0 down? Button 0 is left click
        if (MouseInput.get().isButtonDown(0)) {
            Vector2f screenPos = new Vector2f();
            // Get the position that the mouse is pointing to
            screenPos.set(am.getHotSpotPosition().x, am.getHotSpotPosition().y);
            // Get the world location of that X,Y value
            Vector3f worldCoords = display.getWorldCoordinates(screenPos, 1.0f);
            // Create a ray starting from the camera, and going in the direction
            // of the mouse's location
            final Ray mouseRay = new Ray(cam.getLocation(), worldCoords
                    .subtractLocal(cam.getLocation()));
            mouseRay.getDirection().normalizeLocal();
            results.clear();

            maggie.calculatePick(mouseRay, results);

        }
    }
}