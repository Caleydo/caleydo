package jmetest.effects;


import java.net.URISyntaxException;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleSystem;
import com.jmex.effects.particles.RampEntry;

/**
 * TestParticleRamp makes use of RampEntries to change the size
 * and color of particles over time.
 */
public class TestParticleRamp extends SimpleGame {

    public static void main(final String[] args) {
        TestParticleRamp game = new TestParticleRamp();
        game.setConfigShowMode(ConfigShowMode.AlwaysShow);
        game.start();
    }

    /**
     * create the ParticleSystem and the necessary RenderStates
     */
    @Override
    protected void simpleInitGame() {
        // add a resource locator to point to the needed textures folder
        try {
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE,
                    new SimpleResourceLocator(TestParticleRamp.class.getResource("/jmetest/data/texture/")));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // ParticleSystem using RampEntries to change the color and size over time
        ParticleSystem particles = ParticleFactory.buildParticles("particles", 200);
        particles.setEmissionDirection(new Vector3f(0, 1, 0));
        particles.setInitialVelocity(0.05f);
        particles.setMinimumLifeTime(2500);
        particles.setMaximumLifeTime(2500);
        particles.setMaximumAngle(45 * FastMath.DEG_TO_RAD);
        particles.getParticleController().setControlFlow(true);
        particles.getParticleController().setSpeed(0.5f);
        particles.setParticlesInWorldCoords(true);
        particles.setLocalTranslation(0, -5, 0);
        particles.setLocalScale(0.1f);
        
        // Start color is RED, opaque
        particles.setStartColor(new ColorRGBA(1, 0, 0, 1));
        particles.setStartSize(2.5f);

        // At 25% life, let's have the color be WHITE, opaque
        final RampEntry entry25 = new RampEntry(0.25f);
        entry25.setColor(new ColorRGBA(1, 1, 1, 1));
        particles.getRamp().addEntry(entry25);

        // At 50% life, (25% higher than previous) let's have the color be GREEN, opaque and much bigger.
        // Note that at 25% life the size will be about 3.75 since we did not set a size on that.
        final RampEntry entry50 = new RampEntry(0.25f);
        entry50.setColor(new ColorRGBA(0, 1, 0, 1));
        entry50.setSize(6.5f);
        particles.getRamp().addEntry(entry50);

        // At 75% life, (25% higher than previous) let's have the color be WHITE, opaque
        final RampEntry entry75 = new RampEntry(0.25f);
        entry75.setColor(new ColorRGBA(1, 1, 1, 1));
        particles.getRamp().addEntry(entry75);

        // End color is BLUE, opaque (size is back to 2.5 now.
        particles.setEndColor(new ColorRGBA(0, 0, 1, 1));
        particles.setEndSize(2.5f);

        particles.warmUp(60);

        // set up a BlendState to enable transparency
        final BlendState blend = display.getRenderer().createBlendState();
        blend.setEnabled(true);
        blend.setBlendEnabled(true);
        blend.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        blend.setDestinationFunction(BlendState.DestinationFunction.One);
        blend.setTestEnabled(true);
        blend.setTestFunction(BlendState.TestFunction.GreaterThan);
        particles.setRenderState(blend);

        // load the particles Texture
        final TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture(ResourceLocatorTool.locateResource(
                ResourceLocatorTool.TYPE_TEXTURE, "flaresmall.jpg")));
        ts.setEnabled(true);
        particles.setRenderState(ts);

        // set up a non-writable ZBuffer
        final ZBufferState zstate = display.getRenderer().createZBufferState();
        zstate.setWritable(false);
        zstate.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        particles.setRenderState(zstate);

        particles.getParticleGeometry().setModelBound(new BoundingBox());
        particles.getParticleGeometry().updateModelBound();        
        
        rootNode.attachChild(particles);
    }
}

