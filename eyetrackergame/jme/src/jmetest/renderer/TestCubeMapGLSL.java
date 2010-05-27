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

import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.util.logging.Logger;

import jmetest.renderer.loader.TestColladaLoading;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.TextureCubeMap;
import com.jme.image.Texture.ApplyMode;
import com.jme.image.Texture.CombinerFunctionRGB;
import com.jme.image.Texture.EnvironmentalMapMode;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.image.Texture.WrapMode;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jme.util.geom.TangentBinormalGenerator;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;

/**
 * <code>TestCubeMapGLSL</code>
 * 
 * @author Joshua Slack
 * @version $Id: TestCubeMapGLSL.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestCubeMapGLSL extends SimpleGame {
    private static final Logger logger = Logger.getLogger(TestCubeMapGLSL.class
            .getName());

    private Quaternion rotQuat = new Quaternion();
    private float angle = 0;
    private Vector3f axis = new Vector3f(1, 1, 0);
    private TriMesh t;
    private Node sky;
    private Matrix4f model2WorldMat = new Matrix4f();
    private GLSLShaderObjectsState glsl;

    /**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
        // example: uncomment below to force stats off unless turned on on the command line.
//        boolean useStats = System.getProperty("jme.stats") != null;
        TestCubeMapGLSL app = new TestCubeMapGLSL();
//        if (!useStats)
//            System.clearProperty("jme.stats");
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        if (tpf < 1) {
            angle = angle + (tpf * 25);
            if (angle > 360) {
                angle = 0;
            }
        }

        rotQuat.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, axis);
        t.setLocalRotation(rotQuat);

        // line up the sky
        sky.getLocalTranslation().set(cam.getLocation());

        // update our glsl uniform values.
        glsl.setUniform("eyePosW", cam.getLocation());
        t.getLocalToWorldMatrix(model2WorldMat);
        glsl.setUniform("modelToWorld", model2WorldMat, true);
    }
    
    protected void simpleInitGame() {

        try {
            ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_TEXTURE,
                    new SimpleResourceLocator(TestColladaLoading.class
                            .getClassLoader().getResource(
                                    "jmetest/data/texture/")));
        } catch (URISyntaxException e1) {
            logger.warning("Unable to add texture directory to RLT: "
                    + e1.toString());
        }

        ((PointLight)lightState.get(0)).getLocation().set(10,10,10);
        
        t = new Torus("obj", 32, 32, 4, 6);
        t.setModelBound(new BoundingBox());
        t.updateModelBound();
        t.setCullHint(CullHint.Dynamic);
        t.scaleTextureCoordinates(0, 3.0f);
        rootNode.attachChild(t);
        TangentBinormalGenerator.generate((TriMesh)t);
        t.setTextureCoords(new TexCoords(t.getBinormalBuffer(), 3), 1);
        
        glsl = display.getRenderer().createGLSLShaderObjectsState();
        glsl.load(TestCubeMapGLSL.class.getClassLoader().getResource("jmetest/data/shaders/testcubemapglsl.vert"),
                TestCubeMapGLSL.class.getClassLoader().getResource("jmetest/data/shaders/testcubemapglsl.frag"));
        glsl.setEnabled(true);
        glsl.setUniform("eyePosW", cam.getLocation());
        glsl.setUniform("modelToWorld", model2WorldMat, true);
        glsl.setUniform("baseTex", 0);
        glsl.setUniform("cubeMapTex", 1);
        glsl.setUniform("glossMap", 2);
        glsl.setUniform("reflectionFactor", .5f);
        t.setRenderState(glsl);

        TextureState ts = display.getRenderer().createTextureState();
        Texture baseTex = TextureManager.loadTexture("tile-color.jpg", MinificationFilter.Trilinear, MagnificationFilter.Bilinear);
        baseTex.setWrap(WrapMode.Repeat);
        ts.setTexture(baseTex, 0);
        Texture glossMap = TextureManager.loadTexture("tile-gloss.jpg", MinificationFilter.BilinearNearestMipMap, MagnificationFilter.Bilinear);
        glossMap.setWrap(WrapMode.Repeat);
        ts.setTexture(glossMap, 2);
        Texture cubeMapTex = generateCubeMapTexture();
        cubeMapTex.setEnvironmentalMapMode(EnvironmentalMapMode.ReflectionMap);
        cubeMapTex.setApply(ApplyMode.Combine);
        cubeMapTex.setCombineFuncRGB(CombinerFunctionRGB.AddSigned);
        ts.setTexture(cubeMapTex, 1);
        t.setRenderState(ts);
        
        generateSky();
        
        rootNode.setCullHint(CullHint.Never);
        rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

    }

    private void generateSky() {
        TextureState ts;
        sky = new Node("sky");
        sky.setLightCombineMode(LightCombineMode.Off);
        rootNode.attachChild(sky);
        
        ts = display.getRenderer().createTextureState();
        ts.setTexture(generateCubeMapTexture());
        sky.setRenderState(ts);

        ZBufferState zbuff = display.getRenderer().createZBufferState();
        zbuff.setWritable(false);
        zbuff.setEnabled(true);
        zbuff.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        sky.setRenderState(zbuff);
        sky.setRenderQueueMode(Renderer.QUEUE_SKIP);

        Quad posXQuad = new Quad("test", 10, 10);
        FloatBuffer tbuf = BufferUtils.createFloatBuffer(12);
        tbuf.put(1).put(1).put(-1);
        tbuf.put(1).put(-1).put(-1);
        tbuf.put(1).put(-1).put(1);
        tbuf.put(1).put(1).put(1);
        posXQuad.setTextureCoords(new TexCoords(tbuf, 3), 0);
        posXQuad.setLocalRotation(new Quaternion(new float[] { 0,
                (float) Math.toRadians(270), 0 }));
        posXQuad.setLocalTranslation(new Vector3f(5, 0, 0));
        sky.attachChild(posXQuad);

        Quad negXQuad = new Quad("test", 10, 10);
        tbuf = BufferUtils.createFloatBuffer(12);
        tbuf.put(-1).put(1).put(1);
        tbuf.put(-1).put(-1).put(1);
        tbuf.put(-1).put(-1).put(-1);
        tbuf.put(-1).put(1).put(-1);
        negXQuad.setTextureCoords(new TexCoords(tbuf, 3), 0);
        negXQuad.setLocalRotation(new Quaternion(new float[] { 0,
                (float) Math.toRadians(90), 0 }));
        negXQuad.setLocalTranslation(new Vector3f(-5, 0, 0));
        sky.attachChild(negXQuad);

        Quad posYQuad = new Quad("test", 10, 10);
        tbuf = BufferUtils.createFloatBuffer(12);
        tbuf.put(-1).put(1).put(-1);
        tbuf.put(1).put(1).put(-1);
        tbuf.put(1).put(1).put(1);
        tbuf.put(-1).put(1).put(1);
        posYQuad.setTextureCoords(new TexCoords(tbuf, 3), 0);
        posYQuad.setLocalRotation(new Quaternion(new float[] {
                (float) Math.toRadians(90), (float) Math.toRadians(270), 0 }));
        posYQuad.setLocalTranslation(new Vector3f(0, 5, 0));
        sky.attachChild(posYQuad);

        Quad negYQuad = new Quad("test", 10, 10);
        tbuf = BufferUtils.createFloatBuffer(12);
        tbuf.put(1).put(-1).put(-1);
        tbuf.put(-1).put(-1).put(-1);
        tbuf.put(-1).put(-1).put(1);
        tbuf.put(1).put(-1).put(1);
        negYQuad.setTextureCoords(new TexCoords(tbuf, 3), 0);
        negYQuad.setLocalRotation(new Quaternion(new float[] {
                (float) Math.toRadians(270), (float) Math.toRadians(270), 0 }));
        negYQuad.setLocalTranslation(new Vector3f(0, -5, 0));
        sky.attachChild(negYQuad);

        Quad posZQuad = new Quad("test", 10, 10);
        tbuf = BufferUtils.createFloatBuffer(12);
        tbuf.put(1).put(1).put(1);
        tbuf.put(1).put(-1).put(1);
        tbuf.put(-1).put(-1).put(1);
        tbuf.put(-1).put(1).put(1);
        posZQuad.setTextureCoords(new TexCoords(tbuf, 3), 0);
        posZQuad.setLocalRotation(new Quaternion(new float[] { 0,
                (float) Math.toRadians(180), 0 }));
        posZQuad.setLocalTranslation(new Vector3f(0, 0, 5));
        sky.attachChild(posZQuad);

        Quad negZQuad = new Quad("test", 10, 10);
        tbuf = BufferUtils.createFloatBuffer(12);
        tbuf.put(-1).put(1).put(-1);
        tbuf.put(-1).put(-1).put(-1);
        tbuf.put(1).put(-1).put(-1);
        tbuf.put(1).put(1).put(-1);
        negZQuad.setTextureCoords(new TexCoords(tbuf, 3), 0);
        negZQuad.setLocalTranslation(new Vector3f(0, 0, -5));
        sky.attachChild(negZQuad);
    }

    private TextureCubeMap generateCubeMapTexture() {
        Image posX = TextureManager.loadImage(TestBoxColor.class
                .getClassLoader().getResource("jmetest/data/texture/cube_face_posx.png"), true);
        Image negX = TextureManager.loadImage(TestBoxColor.class
                .getClassLoader().getResource("jmetest/data/texture/cube_face_negx.png"), true);
        Image posY = TextureManager.loadImage(TestBoxColor.class
                .getClassLoader().getResource("jmetest/data/texture/cube_face_posy.png"), true);
        Image negY = TextureManager.loadImage(TestBoxColor.class
                .getClassLoader().getResource("jmetest/data/texture/cube_face_negy.png"), true);
        Image posZ = TextureManager.loadImage(TestBoxColor.class
                .getClassLoader().getResource("jmetest/data/texture/cube_face_posz.png"), true);
        Image negZ = TextureManager.loadImage(TestBoxColor.class
                .getClassLoader().getResource("jmetest/data/texture/cube_face_negz.png"), true);
        Image cubeMapImage = posX;
        cubeMapImage.addData(negX.getData(0));
        cubeMapImage.addData(posY.getData(0));
        cubeMapImage.addData(negY.getData(0));
        cubeMapImage.addData(posZ.getData(0));
        cubeMapImage.addData(negZ.getData(0));
        TextureCubeMap cubeMapTex = new TextureCubeMap();
        cubeMapTex.setImage(cubeMapImage);
        cubeMapTex.setMinificationFilter(MinificationFilter.BilinearNoMipMaps);
        cubeMapTex.setMagnificationFilter(MagnificationFilter.NearestNeighbor);
        cubeMapTex.setWrap(WrapMode.EdgeClamp);
        return cubeMapTex;
    }

}
