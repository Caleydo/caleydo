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

package jmetest.TutorialGuide;

import java.net.URL;
import java.nio.FloatBuffer;

import javax.swing.ImageIcon;

import jmetest.terrain.TestProceduralSplatTexture;

import com.jme.app.SimplePassGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.DirectionalLight;
import com.jme.math.FastMath;
import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.AudioTrack.TrackType;
import com.jmex.audio.MusicTrackQueue.RepeatType;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleInfluence;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.effects.particles.SimpleParticleInfluenceFactory;
import com.jmex.effects.water.WaterRenderPass;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.ImageBasedHeightMap;
import com.jmex.terrain.util.ProceduralSplatTextureGenerator;

/**
 */
public class TestPongCool extends SimplePassGame {
	private static final float SIZE = 400.0f;

	private AudioSystem audio;
	private AudioTrack padCollideSound;
	private AudioTrack wallCollideSound;

	private WaterRenderPass waterEffectRenderPass;
	private Quad waterQuad;
	private Spatial splatTerrain;
	private TerrainPage page;
	private Skybox skybox;

	private ParticleMesh particles;

	private float farPlane = 20000.0f;
	private float textureScale = 0.07f;
	private float globalSplatScale = 90.0f;

	private Node arena;

	private Node sideWalls;
	private Box player1GoalWall;
	private Box player2GoalWall;

	private Box player1;
	private float player1Speed = 1000.0f;
	private int player1Score = 0;
	private Text player1ScoreText;

	private Box player2;
	private float player2Speed = 1000.0f;
	private int player2Score = 0;
	private Text player2ScoreText;

	private Sphere ball;
	private Vector3f ballVelocity;

	private Quaternion tmpQuat = new Quaternion();
	private Vector3f tmpVec = new Vector3f();

	public static void main(String[] args) {
		TestPongCool app = new TestPongCool();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}

	Vector3f ballpos = new Vector3f();
	protected void simpleUpdate() {
//		ballpos.interpolate(ball.getLocalTranslation(), 0.01f);
//		cam.lookAt(ballpos, Vector3f.UNIT_Y);

		skybox.getLocalTranslation().set(cam.getLocation());
		skybox.updateGeometricState(0.0f, true);

		if (waterEffectRenderPass != null && waterEffectRenderPass.isEnabled()) {
			Vector3f transVec = new Vector3f(cam.getLocation().x,
					waterEffectRenderPass.getWaterHeight(), cam.getLocation().z);
			setTextureCoords(0, transVec.x, -transVec.z, textureScale);
			setVertexCoords(transVec.x, transVec.y, transVec.z);
		}

//		Player 1 movement
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER1_MOVE_UP", true)) {
			player1.getLocalTranslation().z -= player1Speed* timer.getTimePerFrame();
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER1_MOVE_DOWN", true)) {
			player1.getLocalTranslation().z += player1Speed* timer.getTimePerFrame();
		}
		player1.getLocalTranslation().z = FastMath.clamp(player1.getLocalTranslation().z, -SIZE + SIZE/6, SIZE - SIZE/6);

//		Player 2 movement
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER2_MOVE_UP", true)) {
			player2.getLocalTranslation().z -= player2Speed* timer.getTimePerFrame();
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER2_MOVE_DOWN", true)) {
			player2.getLocalTranslation().z += player2Speed* timer.getTimePerFrame();
		}
		player2.getLocalTranslation().z = FastMath.clamp(player2.getLocalTranslation().z, -SIZE + SIZE/6, SIZE - SIZE/6);
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("RESET", false)) {
			ball.getLocalTranslation().x = 0;
			ball.getLocalTranslation().z = 0;
			ballVelocity.set(FastMath.rand.nextFloat() * 200, 0, FastMath.rand.nextFloat() * 200);
		}

		padCollideSound.setWorldPosition(ball.getWorldTranslation());
		wallCollideSound.setWorldPosition(ball.getWorldTranslation());

//		Collision with player pads
		if (player1.hasCollision(ball, false) || player2.hasCollision(ball, false)) {
			ballVelocity.x *= -15f;
			ballVelocity.z += FastMath.rand.nextFloat() * 2000.0f - 1000.0f;
			padCollideSound.play();

			ParticleMesh explosion = ExplosionFactory.getExplosion();
			explosion.setOriginOffset(ball.getLocalTranslation().clone());
			explosion.forceRespawn();
			rootNode.attachChild(explosion);

		}
//		Collision with side walls
		if (sideWalls.hasCollision(ball, false)) {
			ballVelocity.z *= -1f;
			padCollideSound.play();
		}

//		Checking for goals (ie collision with back walls)
		if (player1GoalWall.hasCollision(ball, false)) {
			player1Score++;
			player1ScoreText.getText().replace(0, player1ScoreText.getText().length(), "" + player1Score);
			ball.getLocalTranslation().x = 0;
			ball.getLocalTranslation().z = 0;
			ballVelocity.set(FastMath.rand.nextFloat() * 800, 0, FastMath.rand.nextFloat() * 800);
			wallCollideSound.play();
		} else if (player2GoalWall.hasCollision(ball, false)) {            
			player2Score++;
			player2ScoreText.getText().replace(0, player2ScoreText.getText().length(), "" + player2Score);
			ball.getLocalTranslation().x = 0;
			ball.getLocalTranslation().z = 0;
			ballVelocity.set(FastMath.rand.nextFloat() * 800, 0, FastMath.rand.nextFloat() * 800);
			wallCollideSound.play();
		}

		ballVelocity.x = FastMath.clamp(ballVelocity.x, -800.0f, 800.0f);
		ballVelocity.z = FastMath.clamp(ballVelocity.z, -800.0f, 800.0f);

//		Move ball according to velocity
		ball.getLocalTranslation().addLocal(ballVelocity.mult(tpf));
		ball.getLocalTranslation().y = page.getHeight(ball.getLocalTranslation());
		if (Float.isNaN(ball.getLocalTranslation().y)) {
			ball.getLocalTranslation().y = 0.0f;
		}

		float omega = ballVelocity.length() / ball.getRadius();
		Vector3f worldUpVec = Vector3f.UNIT_Y;
		Vector3f direction = ballVelocity.normalize();

//		get axis of rotation
		tmpVec.set(worldUpVec).crossLocal(direction);
//		create per frame rotation
		tmpQuat.fromAngleAxis(omega * tpf, tmpVec);
//		rotate ball
		ball.getLocalRotation().set(tmpQuat.multLocal(ball.getLocalRotation()));

		Vector3f normal = page.getSurfaceNormal(ball.getLocalTranslation(), tmpVec);
		if (normal != null) {
			ballVelocity.x += normal.x * 6000.0f * tpf;
			ballVelocity.z += normal.z * 6000.0f * tpf;
		}
		ballVelocity.multLocal(1.0f - tpf * 0.2f);

		particles.getOriginOffset().set(ball.getLocalTranslation());

//		update our audio system here:
		audio.update();
	}

	protected void simpleInitGame() {
		display.setTitle("jME - Pong Improved");

		lightState.detachAll();
		DirectionalLight dLight = new DirectionalLight();
		dLight.setEnabled(true);
		dLight.setDiffuse(new ColorRGBA(1, 1, 1, 1));
		dLight.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.7f, 1));
		dLight.setDirection(new Vector3f(-0.8f, -1f, -0.8f));
		lightState.attach(dLight);

		initSound();

		buildSkyBox();

		initPongStuff();

		initParticleSystem();

		setupEnvironment();

		createTerrain();

		createWater();

		rootNode.updateGeometricState(0.0f, true);
		rootNode.updateRenderState();

		pManager.add(waterEffectRenderPass);

		RenderPass rootPass = new RenderPass();
		rootPass.add(rootNode);
		pManager.add(rootPass);

		// BloomRenderPass bloomRenderPass = new BloomRenderPass(cam, 4);
		// if (!bloomRenderPass.isSupported()) {
		// Text t = new Text("Text", "GLSL Not supported on this computer.");
		// t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		// t.setLightCombineMode(Spatial.LightCombineMode.Off);
		// t.setLocalTranslation(new Vector3f(0, 20, 0));
		// fpsNode.attachChild(t);
		// } else {
		// bloomRenderPass.setExposurePow(2.0f);
		// bloomRenderPass.setBlurIntensityMultiplier(0.5f);
		//            
		// bloomRenderPass.add(rootNode);
		// bloomRenderPass.setUseCurrentScene(true);
		// pManager.add(bloomRenderPass);
		// }

//		ShadowedRenderPass sPass = new ShadowedRenderPass();
//		sPass.add(rootNode);
//		sPass.addOccluder(ball);
//		sPass.setRenderShadows(true);
//		sPass.setLightingMethod(ShadowedRenderPass.LightingMethod.Additive);
//		pManager.add(sPass);

		RenderPass statPass = new RenderPass();
		statPass.add(statNode);
		pManager.add(statPass);

		rootNode.setCullHint(Spatial.CullHint.Never);
	}

	private void initSound() {
		// grab a handle to our audio system.
		audio = AudioSystem.getSystem();

		// setup our ear tracker to track the camera's position and orientation.
		audio.getEar().trackOrientation(cam);
		audio.getEar().trackPosition(cam);

		// setup a music score for our demo
		AudioTrack music1 = getMusic(TestPongCool.class.getResource("/jmetest/data/sound/test.ogg"));
		audio.getMusicQueue().setRepeatType(RepeatType.ALL);
		audio.getMusicQueue().setCrossfadeinTime(2.5f);
		audio.getMusicQueue().setCrossfadeoutTime(2.5f);
		audio.getMusicQueue().addTrack(music1);
		audio.getMusicQueue().play();

		padCollideSound = audio.createAudioTrack("/jmetest/data/sound/explosion.ogg", false);
		padCollideSound.setRelative(true);
		padCollideSound.setMaxAudibleDistance(100000);
		padCollideSound.setVolume(1.0f);

		wallCollideSound = audio.createAudioTrack("/jmetest/data/sound/laser.ogg", false);
		wallCollideSound.setRelative(false);
		wallCollideSound.setRelative(true);
		wallCollideSound.setMaxAudibleDistance(100000);
		wallCollideSound.setVolume(1.0f);
	}

	private void createWater() {
		waterEffectRenderPass = new WaterRenderPass(cam, 2, false, false);
		waterEffectRenderPass.setWaterPlane(new Plane(new Vector3f(0.0f, 1.0f,
				0.0f), 0.0f));
		waterEffectRenderPass.setClipBias(-1.0f);
		waterEffectRenderPass.setReflectionThrottle(0.0f);
		waterEffectRenderPass.setRefractionThrottle(0.0f);

		waterQuad = new Quad("waterQuad", 1, 1);
		FloatBuffer normBuf = waterQuad.getNormalBuffer();
		normBuf.clear();
		normBuf.put(0).put(1).put(0);
		normBuf.put(0).put(1).put(0);
		normBuf.put(0).put(1).put(0);
		normBuf.put(0).put(1).put(0);

		waterEffectRenderPass.setWaterEffectOnSpatial(waterQuad);
		rootNode.attachChild(waterQuad);

		waterEffectRenderPass.setReflectedScene(skybox);
		waterEffectRenderPass.addReflectedScene(splatTerrain);
		waterEffectRenderPass.addReflectedScene(arena);
		waterEffectRenderPass.setSkybox(skybox);
	}

	private void initParticleSystem() {
		ExplosionFactory.warmup();

		particles = ParticleFactory.buildParticles("particles", 60);
		particles.setEmissionDirection(new Vector3f(0, 1, 0));
		particles.setStartSize(3f);
		particles.setEndSize(1.5f);
		particles.setOriginOffset(new Vector3f(0, 0, 0));
		particles.setInitialVelocity(.05f);
		particles.setMinimumLifeTime(400f);
		particles.setMaximumLifeTime(1000f);
		particles.setStartColor(new ColorRGBA(1, 0, 0, 1));
		particles.setEndColor(new ColorRGBA(0, 1, 0, 1));
		particles.setMaximumAngle(360f * FastMath.DEG_TO_RAD);
		particles.getParticleController().setControlFlow(false);
		particles.getParticleController().setSpeed(1);
		ParticleInfluence wind = SimpleParticleInfluenceFactory.createBasicWind(.6f, new Vector3f(0, 1, 0), true, true);
//		wind.setEnabled(true);
		particles.addInfluence(wind);

		BlendState as1 = display.getRenderer().createBlendState();
		as1.setBlendEnabled(true);
		as1.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		as1.setDestinationFunction(BlendState.DestinationFunction.One);
		as1.setTestEnabled(true);
		as1.setTestFunction(BlendState.TestFunction.GreaterThan);
		as1.setEnabled(true);
		particles.setRenderState(as1);

		TextureState ts = display.getRenderer().createTextureState();
		ts.setTexture(
				TextureManager.loadTexture(
						TestPongCool.class.getClassLoader().getResource(
						"jmetest/data/texture/flaresmall.jpg"),
						Texture.MinificationFilter.Trilinear,
						Texture.MagnificationFilter.Bilinear));
		ts.setEnabled(true);
		particles.setRenderState(ts);

		ZBufferState zstate = display.getRenderer().createZBufferState();
		zstate.setEnabled(true);
		zstate.setWritable(false);
		particles.setRenderState(zstate);

		particles.setModelBound(new BoundingBox());
		particles.updateModelBound();

		arena.attachChild(particles);
		/*        
        try {
//        	URL url = TestPongCool.class.getClassLoader().getResource("C:\\fireParticle.jme");
        	URL url = new File("C:\\fireParticle.jme").toURI().toURL();
        	System.out.println(url);
			ParticleMesh particleMesh1 = (ParticleMesh)BinaryImporter.getInstance().load(url);
			arena.attachChild(particleMesh1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		 */
	}

	private void initPongStuff() {
		arena = new Node("Arena");

		TextureState ts = display.getRenderer().createTextureState();
		Texture t0 = TextureManager.loadTexture(
				TestPongCool.class.getClassLoader().getResource(
				"jmetest/data/images/rockwall2.jpg"),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		t0.setWrap(Texture.WrapMode.Repeat);
		ts.setTexture(t0);
		arena.setRenderState(ts); 

		ball = new Sphere("Ball", 16, 16, 13);
		ball.setModelBound(new BoundingSphere());
		ball.updateModelBound();

		ts = display.getRenderer().createTextureState();
		t0 = TextureManager.loadTexture(
				TestPongCool.class.getClassLoader().getResource(
				"jmetest/data/images/Monkey.jpg"),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		t0.setWrap(Texture.WrapMode.Repeat);
		ts.setTexture(t0);
		ball.setRenderState(ts); 

		arena.attachChild(ball);

		// Initialize ball velocity
		ballVelocity = new Vector3f(0f, 0f, 0f);
		ballVelocity.set(FastMath.rand.nextFloat() * 10, 0, FastMath.rand.nextFloat() * 10);

		// Create Player 1 pad
		player1 = new Box("Player1", new Vector3f(), SIZE/24, SIZE/12, SIZE/6);
		player1.setModelBound(new BoundingBox());
		player1.updateModelBound();
		player1.getLocalTranslation().set(-SIZE, 0, 0);
		player1.setDefaultColor(ColorRGBA.green);
		arena.attachChild(player1);

		// Create Player 2 pad
		player2 = new Box("Player2", new Vector3f(), SIZE/24, SIZE/12, SIZE/6);
		player2.setModelBound(new BoundingBox());
		player2.updateModelBound();
		player2.getLocalTranslation().set(SIZE, 0, 0);
		player2.setDefaultColor(ColorRGBA.green);
		arena.attachChild(player2);

		// Create side walls
		sideWalls = new Node("Walls");
		arena.attachChild(sideWalls);

		Box wall = new Box("Wall1", new Vector3f(), SIZE + SIZE/12 + SIZE/32, SIZE/32, SIZE/32);
		wall.setModelBound(new BoundingBox());
		wall.updateModelBound();
		wall.getLocalTranslation().set(0, 0, SIZE);
		sideWalls.attachChild(wall);

		wall = new Box("Wall2", new Vector3f(), SIZE + SIZE/12 + SIZE/32, SIZE/32, SIZE/32);
		wall.setModelBound(new BoundingBox());
		wall.updateModelBound();
		wall.getLocalTranslation().set(0, 0, -SIZE);
		sideWalls.attachChild(wall);

		// Create back wall, goal detector for player 1
		player1GoalWall = new Box("player1GoalWall", new Vector3f(), SIZE/32, SIZE/32, SIZE);
		player1GoalWall.setModelBound(new BoundingBox());
		player1GoalWall.updateModelBound();
		player1GoalWall.getLocalTranslation().set(SIZE + SIZE/12, 0, 0);
		arena.attachChild(player1GoalWall);

		// Create back wall, goal detector for player 2
		player2GoalWall = new Box("player2GoalWall", new Vector3f(), SIZE/32, SIZE/32, SIZE);
		player2GoalWall.setModelBound(new BoundingBox());
		player2GoalWall.updateModelBound();
		player2GoalWall.getLocalTranslation().set(-SIZE - SIZE/12, 0, 0);
		arena.attachChild(player2GoalWall);

		rootNode.attachChild(arena);

		input = new FirstPersonHandler(cam, 1000.0f, 1.0f);

		KeyBindingManager.getKeyBindingManager().set("PLAYER1_MOVE_UP", KeyInput.KEY_1);
		KeyBindingManager.getKeyBindingManager().set("PLAYER1_MOVE_DOWN", KeyInput.KEY_2);
		KeyBindingManager.getKeyBindingManager().set("PLAYER2_MOVE_UP", KeyInput.KEY_9);
		KeyBindingManager.getKeyBindingManager().set("PLAYER2_MOVE_DOWN", KeyInput.KEY_0);
		KeyBindingManager.getKeyBindingManager().set("RESET", KeyInput.KEY_F);

		player1ScoreText = Text.createDefaultTextLabel("player1ScoreText", "0");
		player1ScoreText.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		player1ScoreText.setLightCombineMode(Spatial.LightCombineMode.Off);
		player1ScoreText.setLocalTranslation(new Vector3f(0, display.getHeight()/2, 1));
		rootNode.attachChild(player1ScoreText);

		player2ScoreText = Text.createDefaultTextLabel("player2ScoreText", "0");
		player2ScoreText.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		player2ScoreText.setLightCombineMode(Spatial.LightCombineMode.Off);
		player2ScoreText.setLocalTranslation(new Vector3f(display.getWidth() - 30, display.getHeight()/2, 1));
		rootNode.attachChild(player2ScoreText);
	}

	private void createTerrain() {
		URL grayScale = TestPongCool.class.getClassLoader().getResource( "jmetest/data/texture/terrain/trough3.png" );
		ImageBasedHeightMap heightMap = new ImageBasedHeightMap( new javax.swing.ImageIcon( grayScale ).getImage() );

		Vector3f terrainScale = new Vector3f(6, 0.4f, 6);
		heightMap.setHeightScale(0.001f);
		page = new TerrainPage("Terrain", 33, heightMap.getSize() + 1,
				terrainScale, heightMap.getHeightMap());
		page.getLocalTranslation().set(0, -9.5f, 0);
		page.setDetailTexture(1, 16);

		ProceduralSplatTextureGenerator pst = new ProceduralSplatTextureGenerator(heightMap);
		pst.addTexture(new ImageIcon(TestProceduralSplatTexture.class.getClassLoader().getResource(
		"jmetest/data/texture/grassb.png")), -128, 0, 128);
		pst.addTexture(new ImageIcon(TestProceduralSplatTexture.class.getClassLoader().getResource(
		"jmetest/data/texture/dirt.jpg")), 0, 128, 255);
		pst.addTexture(new ImageIcon(TestProceduralSplatTexture.class.getClassLoader().getResource(
		"jmetest/data/texture/highest.jpg")), 128, 255, 384);

		pst.addSplatTexture(new ImageIcon(TestProceduralSplatTexture.class.getClassLoader().getResource(
		"jmetest/data/texture/terrainTex.png")), new ImageIcon(TestProceduralSplatTexture.class.getClassLoader().getResource(
		"jmetest/data/texture/water.png")));

		pst.createTexture(1024);

		TextureState ts = display.getRenderer().createTextureState();
		ts.setEnabled(true);
		Texture t1 = TextureManager.loadTexture(pst.getImageIcon().getImage(), Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear, true);
		ts.setTexture(t1, 0);

		Texture t2 = TextureManager.loadTexture(TestProceduralSplatTexture.class.getClassLoader().getResource(
		"jmetest/data/texture/Detail.jpg"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);

		ts.setTexture(t2, 1);
		t2.setWrap(Texture.WrapMode.Repeat);

		t1.setApply(Texture.ApplyMode.Combine);
		t1.setCombineFuncRGB(Texture.CombinerFunctionRGB.Modulate);
		t1.setCombineSrc0RGB(Texture.CombinerSource.CurrentTexture);
		t1.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
		t1.setCombineSrc1RGB(Texture.CombinerSource.PrimaryColor);
		t1.setCombineOp1RGB(Texture.CombinerOperandRGB.SourceColor);

		t2.setApply(Texture.ApplyMode.Combine);
		t2.setCombineFuncRGB(Texture.CombinerFunctionRGB.AddSigned);
		t2.setCombineSrc0RGB(Texture.CombinerSource.CurrentTexture);
		t2.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
		t2.setCombineSrc1RGB(Texture.CombinerSource.Previous);
		t2.setCombineOp1RGB(Texture.CombinerOperandRGB.SourceColor);
		page.setRenderState(ts);

		splatTerrain = page;

		// create some interesting texturestates for splatting
		/*
		TextureState ts1 = createSplatTextureState(
                "jmetest/data/texture/terrain/baserock.jpg", null);

        // //////////////////// PASS STUFF START
        // try out a passnode to use for splatting
        PassNode splattingPassNode = new PassNode("SplatPassNode");
        splattingPassNode.attachChild(page);

        PassNodeState passNodeState = new PassNodeState();
        passNodeState.setPassState(ts1);
        splattingPassNode.addPass(passNodeState);

        // lock some things to increase the performance
        splattingPassNode.lockBounds();
        splattingPassNode.lockTransforms();
        splattingPassNode.lockShadows();

        splatTerrain = splattingPassNode;
		 */
//		splatTerrain.setLightCombineMode(Spatial.LightCombineMode.Off);

		rootNode.attachChild(splatTerrain);
	}

	private void setupEnvironment() {
		cam.setFrustumPerspective(45.0f, (float) display.getWidth()
				/ (float) display.getHeight(), 1f, farPlane);
		cam.setLocation(new Vector3f(-300, 600, 800));
		cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
		cam.update();

		CullState cs = display.getRenderer().createCullState();
		cs.setCullFace(CullState.Face.Back);
		rootNode.setRenderState(cs);

		FogState fogState = display.getRenderer().createFogState();
		fogState.setDensity(1.0f);
		fogState.setEnabled(true);
		fogState.setColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		fogState.setEnd(farPlane);
		fogState.setStart(farPlane / 10.0f);
		fogState.setDensityFunction(FogState.DensityFunction.Linear);
		fogState.setQuality(FogState.Quality.PerVertex);
		rootNode.setRenderState(fogState);
	}

	private void addAlphaSplat(TextureState ts, String alpha) {
		Texture t1 = TextureManager.loadTexture(TestPongCool.class
				.getClassLoader().getResource(alpha),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		t1.setWrap(Texture.WrapMode.Repeat);
		t1.setApply(Texture.ApplyMode.Combine);
		t1.setCombineFuncRGB(Texture.CombinerFunctionRGB.Replace);
		t1.setCombineSrc0RGB(Texture.CombinerSource.Previous);
		t1.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
		t1.setCombineFuncAlpha(Texture.CombinerFunctionAlpha.Replace);
		ts.setTexture(t1, ts.getNumberOfSetTextures());
	}

	@SuppressWarnings("unused")
    private TextureState createSplatTextureState(String texture, String alpha) {
		TextureState ts = display.getRenderer().createTextureState();

		Texture t0 = TextureManager.loadTexture(TestPongCool.class
				.getClassLoader().getResource(texture),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		t0.setWrap(Texture.WrapMode.Repeat);
		t0.setApply(Texture.ApplyMode.Modulate);
		t0.setScale(new Vector3f(globalSplatScale, globalSplatScale, 1.0f));
		ts.setTexture(t0, 0);

		if (alpha != null) {
			addAlphaSplat(ts, alpha);
		}

		return ts;
	}

	@SuppressWarnings("unused")
    private TextureState createLightmapTextureState(String texture) {
		TextureState ts = display.getRenderer().createTextureState();

		Texture t0 = TextureManager.loadTexture(TestPongCool.class
				.getClassLoader().getResource(texture),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		t0.setWrap(Texture.WrapMode.Repeat);
		ts.setTexture(t0, 0);

		return ts;
	}

	private void buildSkyBox() {
		skybox = new Skybox("skybox", 10, 10, 10);

		String dir = "jmetest/data/skybox1/";
		Texture north = TextureManager.loadTexture(TestPongCool.class
				.getClassLoader().getResource(dir + "1.jpg"),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear);
		Texture south = TextureManager.loadTexture(TestPongCool.class
				.getClassLoader().getResource(dir + "3.jpg"),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear);
		Texture east = TextureManager.loadTexture(TestPongCool.class
				.getClassLoader().getResource(dir + "2.jpg"),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear);
		Texture west = TextureManager.loadTexture(TestPongCool.class
				.getClassLoader().getResource(dir + "4.jpg"),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear);
		Texture up = TextureManager.loadTexture(TestPongCool.class
				.getClassLoader().getResource(dir + "6.jpg"),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear);
		Texture down = TextureManager.loadTexture(TestPongCool.class
				.getClassLoader().getResource(dir + "5.jpg"),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear);

		skybox.setTexture(Skybox.Face.North, north);
		skybox.setTexture(Skybox.Face.West, west);
		skybox.setTexture(Skybox.Face.South, south);
		skybox.setTexture(Skybox.Face.East, east);
		skybox.setTexture(Skybox.Face.Up, up);
		skybox.setTexture(Skybox.Face.Down, down);
		skybox.preloadTextures();

		CullState cullState = display.getRenderer().createCullState();
		cullState.setCullFace(CullState.Face.None);
		cullState.setEnabled(true);
		skybox.setRenderState(cullState);

		ZBufferState zState = display.getRenderer().createZBufferState();
		zState.setEnabled(false);
		skybox.setRenderState(zState);

		FogState fs = display.getRenderer().createFogState();
		fs.setEnabled(false);
		skybox.setRenderState(fs);

		skybox.setLightCombineMode(Spatial.LightCombineMode.Off);
		skybox.setCullHint(Spatial.CullHint.Never);
		skybox.setTextureCombineMode(TextureCombineMode.Replace);
		skybox.updateRenderState();

		skybox.lockBounds();
		skybox.lockMeshes();

		rootNode.attachChild(skybox);
	}

	private void setVertexCoords(float x, float y, float z) {
		FloatBuffer vertBuf = waterQuad.getVertexBuffer();
		vertBuf.clear();

		vertBuf.put(x - farPlane).put(y).put(z - farPlane);
		vertBuf.put(x - farPlane).put(y).put(z + farPlane);
		vertBuf.put(x + farPlane).put(y).put(z + farPlane);
		vertBuf.put(x + farPlane).put(y).put(z - farPlane);
	}

	private void setTextureCoords(int buffer, float x, float y,
			float textureScale) {
		x *= textureScale * 0.5f;
		y *= textureScale * 0.5f;
		textureScale = farPlane * textureScale;
		FloatBuffer texBuf;
		texBuf = waterQuad.getTextureCoords(buffer).coords;
		texBuf.clear();
		texBuf.put(x).put(textureScale + y);
		texBuf.put(x).put(y);
		texBuf.put(textureScale + x).put(y);
		texBuf.put(textureScale + x).put(textureScale + y);
	}

	@SuppressWarnings("unused")
    private void initSpatial(Spatial spatial) {
		ZBufferState buf = display.getRenderer().createZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		spatial.setRenderState(buf);

		CullState cs = display.getRenderer().createCullState();
		cs.setCullFace(CullState.Face.Back);
		spatial.setRenderState(cs);

		spatial.setCullHint(Spatial.CullHint.Never);

		spatial.updateGeometricState(0.0f, true);
		spatial.updateRenderState();
	}

	private AudioTrack getMusic(URL resource) {
		// Create a non-streaming, non-looping, relative sound clip.
		AudioTrack sound = AudioSystem.getSystem().createAudioTrack(resource, true);
		sound.setType(TrackType.MUSIC);
		sound.setRelative(true);
		sound.setTargetVolume(0.7f);
		sound.setLooping(false);
		return sound;
	}

	@SuppressWarnings("unused")
    private AudioTrack getSFX(URL resource) {
		// Create a non-streaming, looping, positional sound clip.
		AudioTrack sound = AudioSystem.getSystem().createAudioTrack(resource, false);
		sound.setType(TrackType.POSITIONAL);
		sound.setRelative(false);
		sound.setLooping(true);
		return sound;
	}
}
