package jmetest.renderer.loader;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme.animation.AnimationController;
import com.jme.animation.Bone;
import com.jme.animation.BoneAnimation;
import com.jme.animation.SkinNode;
import com.jme.app.SimpleGame;
import com.jme.input.FirstPersonHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.util.BoneDebugger;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.collada.ColladaImporter;

/**
 * Shows how to load a COLLADA file and apply an animation to it.
 * @author Mark Powell
 *
 */
public class TestColladaLoading extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestColladaLoading.class.getName());
    
    AnimationController ac;
    boolean boneOn = false;
    public static void main(String[] args) {
        TestColladaLoading app = new TestColladaLoading();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }
    
    protected void simpleUpdate() {
        if( KeyBindingManager.getKeyBindingManager().isValidCommand( "bones", false ) ) {
            boneOn = !boneOn;
        }
    }

    protected void simpleRender() {
        //If we want to display the skeleton use the BoneDebugger.
        if(boneOn) {
            BoneDebugger.drawBones(rootNode, display.getRenderer(), true);
        }
    }

    protected void simpleInitGame() {
        try {
            ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_TEXTURE,
                    new SimpleResourceLocator(TestColladaLoading.class
                            .getClassLoader().getResource(
                                    "jmetest/data/model/collada/")));
        } catch (URISyntaxException e1) {
            logger.warning("Unable to add texture directory to RLT: "
                    + e1.toString());
        }

        KeyBindingManager.getKeyBindingManager().set( "bones", KeyInput.KEY_SPACE );
        
        //Our model is Z up so orient the camera properly.
        cam.setAxes(new Vector3f(-1,0,0), new Vector3f(0,0,1), new Vector3f(0,1,0));
        cam.setLocation(new Vector3f(0,-100,20));
        input = new FirstPersonHandler( cam, 80,
                1 );
        
        //this stream points to the model itself.
        InputStream mobboss = TestColladaLoading.class.getClassLoader()
                .getResourceAsStream("jmetest/data/model/collada/man.dae");
        //this stream points to the animation file. Note: You don't necessarily
        //have to split animations out into seperate files, this just helps.
        InputStream animation = TestColladaLoading.class.getClassLoader()
        .getResourceAsStream("jmetest/data/model/collada/man_walk.dae");
        if (mobboss == null) {
            logger.info("Unable to find file, did you include jme-test.jar in classpath?");
            System.exit(0);
        }
        //tell the importer to load the mob boss
        ColladaImporter.load(mobboss, "model");
        //we can then retrieve the skin from the importer as well as the skeleton
        SkinNode sn = ColladaImporter.getSkinNode(ColladaImporter.getSkinNodeNames().get(0));
        Bone skel = ColladaImporter.getSkeleton(ColladaImporter.getSkeletonNames().get(0));
        //clean up the importer as we are about to use it again.
        ColladaImporter.cleanUp();
        
        //load the animation file.
        ColladaImporter.load(animation, "anim");
        //this file might contain multiple animations, (in our case it's one)
        ArrayList<String> animations = ColladaImporter.getControllerNames();
        if(animations != null) {
	        logger.info("Number of animations: " + animations.size());
	        for(int i = 0; i < animations.size(); i++) {
	            logger.info(animations.get(i));
	        }
	        //Obtain the animation from the file by name
	        BoneAnimation anim1 = ColladaImporter.getAnimationController(animations.get(0));
	        
	        //set up a new animation controller with our BoneAnimation
	        ac = new AnimationController();
	        ac.addAnimation(anim1);
	        ac.setRepeatType(Controller.RT_WRAP);
	        ac.setActive(true);
	        ac.setActiveAnimation(anim1);
	        
	        //assign the animation controller to our skeleton
	        skel.addController(ac);
        }
        
        //attach the skeleton and the skin to the rootNode. Skeletons could possibly
        //be used to update multiple skins, so they are separate objects.
        rootNode.attachChild(sn);
        rootNode.attachChild(skel);
        
        rootNode.updateGeometricState(0, true);
        //all done clean up.
        ColladaImporter.cleanUp();
        
        lightState.detachAll();
        
        PointLight pl = new PointLight();
        pl.setAmbient(new ColorRGBA(0.5f,0.5f,0.5f,1));
        pl.setDiffuse(new ColorRGBA(1,1,1,1));
        pl.setLocation(new Vector3f(10,-50,20));
        pl.setEnabled(true);
        lightState.attach(pl);
    }
}