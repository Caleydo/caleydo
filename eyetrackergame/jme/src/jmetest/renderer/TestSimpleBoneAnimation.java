package jmetest.renderer;

import com.jme.animation.AnimationController;
import com.jme.animation.Bone;
import com.jme.animation.BoneAnimation;
import com.jme.animation.BoneTransform;
import com.jme.animation.SkinNode;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.BoneDebugger;
import com.jme.util.TextureManager;

public class TestSimpleBoneAnimation extends SimpleGame {

    SkinNode mySkin;
    Bone theBone, theBone2;

    public static void main(String[] args) {
        TestSimpleBoneAnimation game = new TestSimpleBoneAnimation();
        game.setConfigShowMode(ConfigShowMode.AlwaysShow);
        game.start();
    }

    protected void simpleRender() {
        BoneDebugger.drawBones(rootNode, display.getRenderer(), true);
    }

    protected void simpleUpdate() {
    }

    protected void simpleInitGame() {
        Node modelNode = new Node("model");
        Box b = new Box("test", new Vector3f(0, 0, 0), 2f, .5f, .5f);
        b.setModelBound(new BoundingBox());
        b.updateModelBound();
        mySkin = new SkinNode("test skin");
        mySkin.addSkin(b);
        modelNode.attachChild(mySkin);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture(TestSimpleBoneAnimation.class
                .getClassLoader().getResource(
                        "test/data/model/Player/trex-eye.tga"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear, 0.0f, true));
        b.setRenderState(ts);

        MaterialState ms = display.getRenderer().createMaterialState();
        ms.setSpecular(new ColorRGBA(0.9f, 0.9f, 0.9f, 1));
        ms.setShininess(10);
        b.setRenderState(ms);

        theBone = new Bone("Bone01");
        int[] verts = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                14, 15, 16, 17, 18, 19, 20, 21, 22, 23 };
        float[] weights = new float[] { 0.463532f, 1, 1, 0.399379f, 1, 1, 1, 1,
                1, 0.039898f, 0.038145f, 1, 0.039898f, 0.463532f, 0.399379f,
                0.038145f, 1, 1, 0.038145f, 0.399379f, 0.463532f, 0.039898f, 1,
                1 };

        for (int x = 0; x < verts.length; x++) {
            mySkin.addBoneInfluence(0, verts[x], theBone, weights[x]);
        }

        modelNode.attachChild(theBone);

        theBone2 = new Bone("Bone02");
        int[] verts2 = new int[] { 0, 13, 20, 3, 14, 19, 9, 12, 21, 10, 15, 18 };
        float[] weights2 = new float[] { 0.536468f, 0.536468f, 0.536468f,
                0.600621f, 0.600621f, 0.600621f, 0.960102f, 0.960102f,
                0.960102f, 0.961855f, 0.961855f, 0.961855f };

        for (int x = 0; x < verts2.length; x++) {
            mySkin.addBoneInfluence(0, verts2[x], theBone2, weights2[x]);
        }

        theBone.attachChild(theBone2);
        theBone.updateGeometricState(0, true);
        mySkin.setSkeleton(theBone);

        Quaternion b1Q1 = new Quaternion().fromAngleAxis(-0.00123886f,
                new Vector3f(0, 0, 1));
        Quaternion b1Q2 = new Quaternion().fromAngleAxis(-86.3343f,
                new Vector3f(0, 1, 0));
        Quaternion b1Q3 = new Quaternion().fromAngleAxis(90.0012f,
                new Vector3f(1, 0, 0));
        theBone.setLocalRotation(b1Q1.mult(b1Q2).mult(b1Q3));
        theBone.setLocalTranslation(new Vector3f(0.0168828f, -2.66517e-009f,
                0.060972f));

        Quaternion b2Q1 = new Quaternion().fromAngleAxis(-13.8716f,
                new Vector3f(0, 0, 1));
        Quaternion b2Q2 = new Quaternion().fromAngleAxis(2.66225e-012f,
                new Vector3f(0, 1, 0));
        Quaternion b2Q3 = new Quaternion().fromAngleAxis(5.51267e-013f,
                new Vector3f(1, 0, 0));
        theBone2.setLocalRotation(b2Q1.mult(b2Q2).mult(b2Q3));
        theBone2.setLocalTranslation(new Vector3f(0.0639104f, -1.04178f,
                4.47038e-008f));

        theBone.updateGeometricState(0, true);
        mySkin.normalizeWeights();
        mySkin.regenInfluenceOffsets();

        rootNode.attachChild(modelNode);

        this.input = new NodeHandler(theBone, 10, 10);
        
        Quaternion[] rotations = new Quaternion[5];
        Vector3f[] translations = new Vector3f[5];
        
        Vector3f axis = new Vector3f(1,0,0);
        rotations[0] = new Quaternion();
        rotations[0].fromAngleAxis(10, axis);
        rotations[1] = new Quaternion();
        rotations[1].fromAngleAxis(30, axis);
        rotations[2] = new Quaternion();
        rotations[2].fromAngleAxis(60, axis);
        rotations[3] = new Quaternion();
        rotations[3].fromAngleAxis(90, axis);
        rotations[4] = new Quaternion();
        rotations[4].fromAngleAxis(120, axis);
        
        translations[0] = new Vector3f(0,0,0);
        translations[1] = new Vector3f(0,1,0);
        translations[2] = new Vector3f(0,2,0);
        translations[3] = new Vector3f(0,1,0);
        translations[4] = new Vector3f(0,2,0);
        
        BoneTransform bt = new BoneTransform();
        bt.setBone(theBone);
        bt.setRotations(rotations);
        bt.setTranslations(translations);
        
        float[] times = new float[5];
        times[0] = 0.3333f;
        times[1] = 0.6333f;
        times[2] = 0.9333f;
        times[3] = 1.3333f;
        times[4] = 1.6333f;
        
        BoneAnimation bac = new BoneAnimation();
        bac.setInterpolationTypes(new int[] {0, 0, 0, 0, 0});
        bac.addBoneTransforms(bt);
        bac.setTimes(times);
        bac.setEndFrame(4);
        
        AnimationController ac = new AnimationController();
        ac.addAnimation(bac);
        ac.setActiveAnimation(bac);
        ac.setRepeatType(Controller.RT_WRAP);
        theBone.addController(ac);
    }

}
