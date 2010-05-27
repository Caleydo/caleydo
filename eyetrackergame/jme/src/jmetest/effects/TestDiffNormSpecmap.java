package jmetest.effects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.logging.Logger;

import jmetest.renderer.loader.TestNormalmap;

import com.jme.app.SimpleGame;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.TangentBinormalGenerator;

/**
 * @author dhdd (Andreas Grabner)
 */
public class TestDiffNormSpecmap extends SimpleGame {
    private static final Logger logger = Logger.getLogger(TestNormalmap.class.getName());

    private Vector3f lightDir = new Vector3f();
    private GLSLShaderObjectsState so;
    private String currentShaderStr = "jmetest/data/shaders/normalmap";
    private Sphere lightSphere1, lightSphere2, lightSphere3;

    private PointLight pl1, pl2, pl3;

    public static void main(String[] args) {
        TestDiffNormSpecmap app = new TestDiffNormSpecmap();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        float spinValX1 = FastMath.sin(timer.getTimeInSeconds() * 1.0f);
        float spinValY1 = FastMath.cos(timer.getTimeInSeconds() * 0.4f);
        float spinValZ1 = FastMath.cos(timer.getTimeInSeconds() * 0.5f);
        float spinValX2 = FastMath.sin(timer.getTimeInSeconds() * 0.5f);
        float spinValY2 = FastMath.cos(timer.getTimeInSeconds() * 0.6f);
        float spinValZ2 = FastMath.cos(timer.getTimeInSeconds() * 0.2f);
        float spinValX3 = FastMath.sin(timer.getTimeInSeconds() * 0.4f);
        float spinValY3 = FastMath.cos(timer.getTimeInSeconds() * 1.0f);
        float spinValZ3 = FastMath.cos(timer.getTimeInSeconds() * 0.65f);

        lightDir.set(spinValX1, spinValY1, spinValZ1).normalizeLocal();
        lightSphere1.setLocalTranslation(lightDir.negate().multLocal(30));
        pl1.setLocation(lightDir.negate().multLocal(30));

        lightDir.set(spinValX2, spinValY2, spinValZ2).normalizeLocal();
        lightSphere2.setLocalTranslation(lightDir.negate().multLocal(30));
        pl2.setLocation(lightDir.negate().multLocal(30));

        lightDir.set(spinValX3, spinValY3, spinValZ3).normalizeLocal();
        lightSphere3.setLocalTranslation(lightDir.negate().multLocal(30));
        pl3.setLocation(lightDir.negate().multLocal(30));
    }

    protected void simpleInitGame() {

        cam.setAxes(new Vector3f(-1, 0, 0), new Vector3f(0, 0, 1), new Vector3f(0, 1, 0));
        cam.setLocation(new Vector3f(0, -100, 0));

        pl1 = new PointLight();
        pl1.setAmbient(new ColorRGBA(0, 0, 0, 1));
        pl1.setDiffuse(new ColorRGBA(0.4f, 0, 0, 1));
        pl1.setSpecular(new ColorRGBA(1, 0, 0, 1));
        pl1.setEnabled(true);

        pl2 = new PointLight();
        pl2.setAmbient(new ColorRGBA(0, 0, 0, 1));
        pl2.setDiffuse(new ColorRGBA(0, 0.4f, 0, 1));
        pl2.setSpecular(new ColorRGBA(0, 1, 0, 1));
        pl2.setEnabled(true);

        pl3 = new PointLight();
        pl3.setAmbient(new ColorRGBA(0, 0, 0, 1));
        pl3.setDiffuse(new ColorRGBA(0, 0, 0.4f, 1));
        pl3.setSpecular(new ColorRGBA(0, 0, 1, 1));
        pl3.setEnabled(true);

        lightState.detachAll();
        lightState.setGlobalAmbient(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));
        lightState.setTwoSidedLighting(false);
        lightState.setSeparateSpecular(true);
        lightState.attach(pl1);
        lightState.attach(pl2);
        lightState.attach(pl3);

        TextureState ts = display.getRenderer().createTextureState();

        // Base texture
        Texture baseMap = TextureManager.loadTexture(TestNormalmap.class.getClassLoader().getResource(
                "jmetest/data/images/Fieldstone.jpg"), Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        ts.setTexture(baseMap, 0);

        // Normal map
        Texture normalMap = TextureManager.loadTexture(TestNormalmap.class.getClassLoader().getResource(
                "jmetest/data/images/FieldstoneNormal.jpg"), Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear, Image.Format.GuessNoCompression, 0.0f, true);
        ts.setTexture(normalMap, 1);

        // Specular map
        Texture specMap = TextureManager.loadTexture(TestNormalmap.class.getClassLoader().getResource(
                "jmetest/data/images/FieldstoneSpec.jpg"), Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        ts.setTexture(specMap, 2);

        Sphere model = new Sphere("sphere", new Vector3f(0, 0, 0), 50, 50, 20.0f, false);

        lightSphere1 = new Sphere("light1", new Vector3f(0, 0, 0), 10, 10, 1.0f, false);
        lightSphere1.setLightCombineMode(LightCombineMode.Off);
        lightSphere1.setDefaultColor(ColorRGBA.red.clone());
        lightSphere2 = new Sphere("light2", new Vector3f(0, 0, 0), 10, 10, 1.0f, false);
        lightSphere2.setLightCombineMode(LightCombineMode.Off);
        lightSphere2.setDefaultColor(ColorRGBA.green.clone());
        lightSphere3 = new Sphere("light3", new Vector3f(0, 0, 0), 10, 10, 1.0f, false);
        lightSphere3.setLightCombineMode(LightCombineMode.Off);
        lightSphere3.setDefaultColor(ColorRGBA.blue.clone());
        createShader(lightState.getQuantity(), model);

        MaterialState ms = display.getRenderer().createMaterialState();
        ms.setAmbient(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        ms.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        ms.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        ms.setShininess(50.0f);

        // Set all states on model
        model.setRenderState(ts);
        model.setRenderState(so);
        model.setRenderState(ms);
        model.setRenderState(lightState);
        model.updateGeometricState(0.0f, true);
        model.updateRenderState();

        rootNode.attachChild(model);
        rootNode.attachChild(lightSphere1);
        rootNode.attachChild(lightSphere2);
        rootNode.attachChild(lightSphere3);
        

        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderState();

        input = new FirstPersonHandler(cam, 80, 1);
    }

    /**
     * Loads shader from URL
     * 
     * @param url
     * @return String with shader
     */
    private String load(URL url) {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buf = new StringBuffer();
            while (r.ready()) {
                buf.append(r.readLine()).append('\n');
            }
            r.close();
            return buf.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void createShader(int numLights, TriMesh geometry) {
        so = DisplaySystem.getDisplaySystem().getRenderer().createGLSLShaderObjectsState();
        String vert = load(TestDiffNormSpecmap.class.getClassLoader().getResource(currentShaderStr + ".vert"));
        String frag = load(TestDiffNormSpecmap.class.getClassLoader().getResource(currentShaderStr + ".frag"));
        vert = vert.replace("$NL$", "" + numLights);
        frag = frag.replace("$NL$", "" + numLights);
        TangentBinormalGenerator.generate(geometry);
        so.load(vert, frag);
        so.setUniform("baseMap", 0);
        so.setUniform("normalMap", 1);
        so.setUniform("specularMap", 2);
        FloatBuffer tangent = geometry.getTangentBuffer();
        // the binormal is computed in the shader from tangent and normal
        so.setAttributePointer("modelTangent", 3, false, 0, tangent);
    }
}
