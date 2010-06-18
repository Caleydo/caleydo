package game;



import java.util.Random;

import jmetest.TutorialGuide.ExplosionFactory;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Capsule;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.MaterialState.ColorMaterial;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

import eyetracker.TrackDataProvider;

public class ECgame extends SimplePhysicsGame
{
	
	private Skybox skybox_;
	
	private StaticPhysicsNode staticNode_;
	
	private StaticPhysicsNode ghostNode_;
	private StaticPhysicsNode playerNode_;
	
	private StaticPhysicsNode redNode_;
	private StaticPhysicsNode greenNode_;
	private StaticPhysicsNode blueNode_;
	
	private DynamicPhysicsNode[] ballNode_;
	private Sphere[] ball_;

	
	private Node hud_;
	
	MaterialState ms_;
	
	private Box entranceRed_;
	private Box entranceGreen_;
	private Box entranceBlue_;
	
	private Sphere pointer_;
	private Vector2f tmp_screen_pos_ = new Vector2f();
	private Vector3f tmp_world_coord_ = new Vector3f();
	private float[] eye_pos_ = new float[] { 0f, 0f };
	private Sphere red_ball_;
	private Sphere green_ball_;
	private Sphere blue_ball_;
	
	private Text player_score_;
	private Text player_lives_;
	private int lives_ = 3;
	private int score_ = 0;
	private String score_string_;
	
	private Vector2f[] mouse_over_;
	
	private Box player_;
	private Capsule ghost_;
	
	private Quaternion tmp_quat_ = new Quaternion();
	
	private TrackDataProvider eyeTracker_;
	
	private enum Color {RED, GREEN, BLUE}
	
	private boolean red_opened_ = false;
	private boolean green_opened_ = false;
	private boolean blue_opened_ = false;
	private boolean opening_ = false;
	private boolean closing_ = false;
	
	private int ball_counter_ = 0;
	private int max_ball_count_ = 5;
	
	private Random generator_;	
	private boolean test = true;
	private Color spawn_color_;
	
	protected void simpleInitGame() 
	{
		display.setTitle("Eye-Controlled Game");
		input.removeAllFromAttachedHandlers();
		graphNode.detachChildNamed("f4");
		
		generator_ = new Random();
		initMouseOverPos();
		initCamera();
	    
		buildSkyBox();
	
		staticNode_ = getPhysicsSpace().createStaticNode();
		rootNode.attachChild( staticNode_ );
		
        // Make the object default colors shine through
        ms_ = display.getRenderer().createMaterialState();
        ms_.setColorMaterial(ColorMaterial.AmbientAndDiffuse);
		ExplosionFactory.warmup();
		
		initPlatforms();
		initWalls();
	
		ballNode_ = new DynamicPhysicsNode[5];
		ball_ = new Sphere[5];
		for(int i = 0; i < 5; i++)
		{
			ballNode_[i] = null;
			ball_[i] = null;
		}
		
		
		initHUD();
		initGhost();
		initPlayer();
		
		
		
		initTestMouse();
		KeyBindingManager.getKeyBindingManager().set("PLAYER_MOVE", KeyInput.KEY_LCONTROL);
		KeyBindingManager.getKeyBindingManager().set("DETONATE", KeyInput.KEY_SPACE);
		KeyBindingManager.getKeyBindingManager().set("TEST1", KeyInput.KEY_LEFT);
		KeyBindingManager.getKeyBindingManager().set("TEST2", KeyInput.KEY_RIGHT);
		
        //init eye tracking
		eyeTracker_ = new TrackDataProvider();
		eyeTracker_.startTracking();
		
		//rootNode.setRenderState(ms_);
		//rootNode.updateRenderState();
		
		//showPhysics = true;

			    		
	}
	
	private void createBall(Color color)
	{	
		if(ball_counter_ == max_ball_count_)
			return;
		
		int index;
		for(index = 0; index < 5; index++)
		{
			if(ballNode_[index] == null)
			{
				ballNode_[index] = getPhysicsSpace().createDynamicNode();
				break;
			}
		}
		
		rootNode.attachChild(ballNode_[index]);
		
		ColorRGBA temp_color;
		Vector3f start_pos;
		
		switch(color)
		{
		case RED:
			temp_color = ColorRGBA.red;
			start_pos = new Vector3f(-11,5.5f,0);
			break;
		case GREEN:
			temp_color = ColorRGBA.green;
			start_pos = new Vector3f(-11,0.5f,0);
			break;
		case BLUE:
			temp_color = ColorRGBA.blue;
			start_pos = new Vector3f(-11,-4.5f,0);
			break;
		default:
			return;
		}
			
		String name = "ball"+index;
		ball_[index] = new Sphere(name,start_pos,10,10,0.4f);
		ball_[index].setDefaultColor(temp_color);
		//ballNode_[index].setMaterial(Material.OSMIUM);
		ballNode_[index].attachChild(ball_[index]);
		ballNode_[index].generatePhysicsGeometry();
		
        ballNode_[index].setRenderState(ms_);
        ballNode_[index].updateRenderState();
		
        ball_counter_++;
	}
	
	private void removeBall(int index)
	{
		ball_[index] = null;
		ballNode_[index].delete();
		ballNode_[index] = null;
	}
	
	private void initHUD()
	{
		hud_ = new Node();
		rootNode.attachChild(hud_);
		
		//lives
		player_lives_ = Text.createDefaultTextLabel("lives", "LIVES:");
		player_lives_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		player_lives_.setLightCombineMode(Spatial.LightCombineMode.Off);
		player_lives_.setLocalTranslation(new Vector3f(50 , display.getHeight() -30, 0));
		player_lives_.setLocalScale(new Vector3f(1.5f,1.5f,0));
		

		Vector2f text = new Vector2f(50, display.getHeight()-30);
		Vector3f temp = display.getWorldCoordinates(text, 0);	
		Vector2f offset = new Vector2f(display.getWidth()/2+120, display.getHeight()/2+ 12);
		Vector3f tempoffset = display.getWorldCoordinates(offset, 0);
		
		temp.x += tempoffset.x;
		temp.y += tempoffset.y;
		temp.z = 0;
		
		//System.out.println(temp.x + "  "+temp.y);
		Sphere life1 = new Sphere("life1", temp,10, 10, 0.25f);
		life1.setDefaultColor(ColorRGBA.red);
		
		temp.x += 0.6f;
		Sphere life2 = new Sphere("life2", temp,10, 10, 0.25f);
		life2.setDefaultColor(ColorRGBA.green);
		
		temp.x += 0.6f;
		Sphere life3 = new Sphere("life3", temp,10, 10, 0.25f);
		life3.setDefaultColor(ColorRGBA.blue);
		
		//score	
		score_string_ = "SCORE: " + score_;
		player_score_ = Text.createDefaultTextLabel("score", score_string_);
		player_score_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		player_score_.setLightCombineMode(Spatial.LightCombineMode.Off);
		player_score_.setLocalTranslation(new Vector3f(display.getWidth()-300 , display.getHeight() -30, 0));
		player_score_.setLocalScale(new Vector3f(1.5f,1.5f,0));
		
		
		hud_.attachChild(player_lives_);
		hud_.attachChild(player_score_);
		hud_.attachChild(life1);
		hud_.attachChild(life2);
		hud_.attachChild(life3);
		
        hud_.setRenderState(ms_);
        hud_.updateRenderState();
	
	}
	
	private void initGhost()
	{
		
		ghostNode_ = getPhysicsSpace().createStaticNode();
		rootNode.attachChild(ghostNode_);
		
		ghost_ = new Capsule("ghost", 10, 10, 10, 0.3f, 0.90f);
		
		ghost_.getLocalTranslation().x = -2.5f;
		ghost_.getLocalTranslation().y = 4.8f;
		ghost_.getLocalTranslation().z = 0;
		tmp_quat_.fromAngleAxis(FastMath.PI / 2.0f, new Vector3f(0,0,1));
		ghost_.setLocalRotation(tmp_quat_);
		
		ghost_.setDefaultColor(ColorRGBA.gray);
		ghostNode_.attachChild(ghost_);	
		
        ghostNode_.setRenderState(ms_);
        ghostNode_.updateRenderState();
		
		
	}
	
	private void initMouseOverPos()
	{
		mouse_over_ = new Vector2f[24];
		
		mouse_over_[0] = new Vector2f(-4,7);
		mouse_over_[1] = new Vector2f(-0.9f,7);
		mouse_over_[2] = new Vector2f(-4,2.6f);
		mouse_over_[3] = new Vector2f(-0.9f,2.6f);
		mouse_over_[4] = new Vector2f(2.2f,7);
		mouse_over_[5] = new Vector2f(5.7f,7);
		mouse_over_[6] = new Vector2f(2.2f,2.6f);
		mouse_over_[7] = new Vector2f(5.7f,2.6f);
		mouse_over_[8] = new Vector2f(-5.8f,2);
		mouse_over_[9] = new Vector2f(-2.3f,2);
		mouse_over_[10] = new Vector2f(-5.8f,-2.3f);
		mouse_over_[11] = new Vector2f(-2.3f,-2.3f);
		mouse_over_[12] = new Vector2f(0.5f,2);
		mouse_over_[13] = new Vector2f(4.3f,2);
		mouse_over_[14] = new Vector2f(0.5f,-2.3f);
		mouse_over_[15] = new Vector2f(4.3f,-2.3f);
		mouse_over_[16] = new Vector2f(-7.3f,-3.2f);
		mouse_over_[17] = new Vector2f(-3.5f,-3.2f);
		mouse_over_[18] = new Vector2f(-7.3f,-7.2f);
		mouse_over_[19] = new Vector2f(-3.5f,-7.2f);
		mouse_over_[20] = new Vector2f(-0.8f,-3.2f);
		mouse_over_[21] = new Vector2f(2.5f,-3.2f);
		mouse_over_[22] = new Vector2f(-0.8f,-7.2f);
		mouse_over_[23] = new Vector2f(2.5f,-7.2f);
	}
	
	private void initPlayer()
	{
		playerNode_ = getPhysicsSpace().createStaticNode();
		
		rootNode.attachChild(playerNode_);

		/*player_ = playerNode_.createBox("player");
		player_.getLocalScale().set( 0.75f, 0.3f, 10 );
		player_.getLocalTranslation().x = 12.5f;
		player_.getLocalTranslation().y = 4.8f;
		player_.getLocalTranslation().z = 0;
		
		TextureState textStateBricks = display.getRenderer().createTextureState();
		Texture textbricks = TextureManager.loadTexture(ECgame.class.getResource("/data/bricks.jpg"),
				Texture.MinificationFilter.Trilinear, 
				Texture.MagnificationFilter.Bilinear);
		
		textbricks.setWrap(Texture.WrapMode.Repeat);
		textStateBricks.setTexture(textbricks);
		player_.setRenderState(textStateBricks);*/
		
		//player_ = new Box("player",new Vector3f(-2.5f,4.8f,0),0.75f,0.3f,10);
		
		player_ = new Box("player",new Vector3f(0,0,0),0.75f,0.3f,10);
		
		//playerNode_.createBox("player");
		
		
		//player_.setModelBound(new BoundingBox());
		//player_.updateModelBound();
		
		TextureState textStateBricks = display.getRenderer().createTextureState();
		Texture textbricks = TextureManager.loadTexture(ECgame.class.getResource("/data/bricks.jpg"),
				Texture.MinificationFilter.Trilinear, 
				Texture.MagnificationFilter.Bilinear);
		
		textbricks.setWrap(Texture.WrapMode.Repeat);
		textStateBricks.setTexture(textbricks);
		player_.setRenderState(textStateBricks);
		
		playerNode_.attachChild(player_);
		
		playerNode_.getLocalTranslation().set(new Vector3f(-2.5f,4.8f,0));
		
		playerNode_.generatePhysicsGeometry();
		
	}
	
	private void initTestMouse()
	{
		pointer_ = new Sphere("pointer", new Vector3f(0,0,10.01f),10, 10, 0.1f);
		rootNode.attachChild(pointer_);
	}
	
    public static void main(String[] args) 
    {
        ECgame game = new ECgame();
        game.setConfigShowMode(ConfigShowMode.AlwaysShow);
        game.start();
    }
    
	private void initWalls()
	{		
		redNode_ = getPhysicsSpace().createStaticNode();
		greenNode_ = getPhysicsSpace().createStaticNode();
		blueNode_ = getPhysicsSpace().createStaticNode();
		
		rootNode.attachChild(redNode_);
		rootNode.attachChild(greenNode_);
		rootNode.attachChild(blueNode_);
		
		entranceRed_ = new Box("entranceRed",new Vector3f(0,0,0),0.25f,1.5f,10);
		Box topLeftWall = new Box("topLeftWall", new Vector3f(-9.75f,8.75f,0),0.25f,1.25f,10);
		Box topRightWall = new Box("topRightWall", new Vector3f(9.75f,7.25f,0),0.25f,2.75f,10);
			
		
		entranceGreen_ = new Box("entranceGreen",new Vector3f(0,0,0),0.25f,1.5f,10);
		Box highMiddleLeftWall = new Box("highMiddleLeftWall",new Vector3f(-9.75f,3.5f,0),0.25f,1,10);
		Box lowMiddleLeftWall = new Box("lowMiddleLeftWall", new Vector3f(-9.75f,-1.5f,0),0.25f,1,10);
		Box highMiddleRightWall = new Box("highMiddleRightWall", new Vector3f(9.75f,2,0),0.25f,2.5f,10);
		
		
		entranceBlue_ = new Box("entranceBlue",new Vector3f(0,0,0),0.25f,1.5f,10);
		Box bottomLeftWall = new Box("bottomLeftWall", new Vector3f(-9.75f,-7.75f,0),0.25f,2.25f,10);
		Box bottomRightWall = new Box("bottomRightWall", new Vector3f(9.75f,-7.75f,0),0.25f,2.25f,10);
		Box lowMiddleRightWall = new Box("lowMiddleRightWall", new Vector3f(9.75f,-3,0),0.25f,2.5f,10);

		
		TextureState textStateRed = display.getRenderer().createTextureState();
		Texture textred = TextureManager.loadTexture(ECgame.class.getResource("/data/red.jpg"),
				Texture.MinificationFilter.Trilinear, 
				Texture.MagnificationFilter.Bilinear);
		
		textred.setWrap(Texture.WrapMode.Repeat);
		textStateRed.setTexture(textred);
		entranceRed_.setRenderState(textStateRed);
		topLeftWall.setRenderState(textStateRed);
		topRightWall.setRenderState(textStateRed);
		
		TextureState textStateGreen = display.getRenderer().createTextureState();
		Texture textgreen = TextureManager.loadTexture(ECgame.class.getResource("/data/green.jpg"),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		
		textStateGreen.setTexture(textgreen);
		entranceGreen_.setRenderState(textStateGreen);
		highMiddleLeftWall.setRenderState(textStateGreen);
		highMiddleRightWall.setRenderState(textStateGreen);
		
		
		TextureState textStateBlue = display.getRenderer().createTextureState();
		Texture textblue = TextureManager.loadTexture(ECgame.class.getResource("/data/blue.jpg"),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		
		textStateBlue.setTexture(textblue);
		entranceBlue_.setRenderState(textStateBlue);
		lowMiddleRightWall.setRenderState(textStateBlue);
		lowMiddleLeftWall.setRenderState(textStateBlue);
		
		TextureState textStateBricks = display.getRenderer().createTextureState();
		Texture textbricks = TextureManager.loadTexture(ECgame.class.getResource("/data/bricks.jpg"),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		
		textStateBricks.setTexture(textbricks);		
		bottomLeftWall.setRenderState(textStateBricks);
		bottomRightWall.setRenderState(textStateBricks);
		
		
		
		redNode_.attachChild(entranceRed_);
		redNode_.getLocalTranslation().set(new Vector3f(-9.75f,6,0));
		
		staticNode_.attachChild(topLeftWall);
		staticNode_.attachChild(topRightWall);
		
		greenNode_.attachChild(entranceGreen_);
		greenNode_.getLocalTranslation().set(new Vector3f(-9.75f,1,0));
		
		staticNode_.attachChild(highMiddleLeftWall);
		staticNode_.attachChild(lowMiddleLeftWall);
		staticNode_.attachChild(highMiddleRightWall);
		
		blueNode_.attachChild(entranceBlue_);		
		blueNode_.getLocalTranslation().set(new Vector3f(-9.75f,-4,0));
		
		staticNode_.attachChild(bottomLeftWall);
		staticNode_.attachChild(bottomRightWall);
		staticNode_.attachChild(lowMiddleRightWall);
		
		redNode_.generatePhysicsGeometry();
		greenNode_.generatePhysicsGeometry();
		blueNode_.generatePhysicsGeometry();
		staticNode_.generatePhysicsGeometry();
		
		
	}
	
	private void initPlatforms()
	{
		
		Box topFirst = new Box("topFirst",new Vector3f(-8.125f,4.8f,0),4.875f,0.3f,10);
		Box topSecond = new Box("topSecond",new Vector3f(0.75f,4.8f,0),2.5f,0.3f,10);
		Box topThird = new Box("topThird",new Vector3f(7.375f,4.8f,0),2.625f,0.3f,10);	
		
		Box middleFirst = new Box("middleFirst",new Vector3f(-8.875f,-0.2f,0),4.125f,0.3f,10);
		Box middleSecond = new Box("middleSecond",new Vector3f(-0.75f,-0.2f,0),2.5f,0.3f,10);
		Box middleThird = new Box("middleThird",new Vector3f(6.625f,-0.2f,0),3.375f,0.3f,10);		
		
		Box bottomFirst = new Box("bottomFirst",new Vector3f(-9.625f,-5.2f,0),3.375f,0.3f,10);
		Box bottomSecond = new Box("bottomSecond",new Vector3f(-2.25f,-5.2f,0),2.5f,0.3f,10);
		Box bottomThird = new Box("bottomThird",new Vector3f(5.875f,-5.2f,0),4.125f,0.3f,10);
		
		TextureState textStateBricks = display.getRenderer().createTextureState();
		Texture textbricks = TextureManager.loadTexture(ECgame.class.getResource("/data/bricks.jpg"),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		
		textStateBricks.setTexture(textbricks);		
		topFirst.setRenderState(textStateBricks);
		topSecond.setRenderState(textStateBricks);
		topThird.setRenderState(textStateBricks);
		middleFirst.setRenderState(textStateBricks);
		middleSecond.setRenderState(textStateBricks);
		middleThird.setRenderState(textStateBricks);
		bottomFirst.setRenderState(textStateBricks);
		bottomSecond.setRenderState(textStateBricks);
		bottomThird.setRenderState(textStateBricks);
		
		staticNode_.attachChild(topFirst);
		staticNode_.attachChild(topSecond);
		staticNode_.attachChild(topThird);
		staticNode_.attachChild(middleFirst);
		staticNode_.attachChild(middleSecond);
		staticNode_.attachChild(middleThird);
		staticNode_.attachChild(bottomFirst);
		staticNode_.attachChild(bottomSecond);
		staticNode_.attachChild(bottomThird);
		
		staticNode_.generatePhysicsGeometry();
	}
    
    private void initCamera()
    {
		float zoom = 10f;
		// setupCamProperties
	    cam.setParallelProjection(true);
	    //float aspect = (float) display.getWidth() / display.getHeight();
	    cam.setFrustum(0f, 100.0f, zoom, -zoom, -zoom, zoom);
	    cam.update();

	    cam.setLeft(Vector3f.UNIT_X);
	    cam.setUp(Vector3f.UNIT_Y);
	    cam.setDirection(Vector3f.UNIT_Z);
	    cam.setLocation(new Vector3f(0, 0, 100));
	    cam.setDirection(new Vector3f(0, 0, -1));
	    cam.update();
    	
    }
    private void buildSkyBox() {
		skybox_ = new Skybox("skybox", 10, 10, 10);	
		
		
		Texture north = TextureManager.loadTexture(ECgame.class.getResource("/data/sky1.jpg"),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear);
		Texture south = TextureManager.loadTexture(ECgame.class.getResource("/data/sky3.jpg"),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear);
		Texture east = TextureManager.loadTexture(ECgame.class.getResource("/data/sky2.jpg"),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear);
		Texture west = TextureManager.loadTexture(ECgame.class.getResource("/data/sky4.jpg"),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear);
		Texture up = TextureManager.loadTexture(ECgame.class.getResource("/data/sky6.jpg"),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear);
		Texture down = TextureManager.loadTexture(ECgame.class.getResource("/data/sky5.jpg"),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear);

		skybox_.setTexture(Skybox.Face.North, north);
		skybox_.setTexture(Skybox.Face.West, west);
		skybox_.setTexture(Skybox.Face.South, south);
		skybox_.setTexture(Skybox.Face.East, east);
		skybox_.setTexture(Skybox.Face.Up, up);
		skybox_.setTexture(Skybox.Face.Down, down);
		skybox_.preloadTextures();

		CullState cullState = display.getRenderer().createCullState();
		cullState.setCullFace(CullState.Face.None);
		cullState.setEnabled(true);
		skybox_.setRenderState(cullState);

		ZBufferState zState = display.getRenderer().createZBufferState();
		zState.setEnabled(false);
		skybox_.setRenderState(zState);

		FogState fs = display.getRenderer().createFogState();
		fs.setEnabled(false);
		skybox_.setRenderState(fs);

		skybox_.setLightCombineMode(Spatial.LightCombineMode.Off);
		skybox_.setCullHint(Spatial.CullHint.Never);
		skybox_.setTextureCombineMode(TextureCombineMode.Replace);
		skybox_.updateRenderState();

		skybox_.lockBounds();
		skybox_.lockMeshes();

		rootNode.attachChild(skybox_);
	}
    
    protected void simpleUpdate()
    {  	
    	handleMouse();
    	//handleEyeInput();
		
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER_MOVE", true))
		{
			playerNode_.getLocalTranslation().x = ghost_.getLocalTranslation().x;
			playerNode_.getLocalTranslation().y = ghost_.getLocalTranslation().y;
			

		}
		
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("DETONATE", true))
		{
			ParticleMesh explosion = ExplosionFactory.getExplosion();
			explosion.getLocalScale().set(new Vector3f(0.05f,0.05f,0.05f));
			explosion.getLocalTranslation().set(playerNode_.getLocalTranslation().x,playerNode_.getLocalTranslation().y+0.7f,10);
			explosion.forceRespawn();
			rootNode.attachChild(explosion);
			
			
		}
		
		moveGhost();
		
		//spawn a ball
		if(test)
		{
			spawn_color_ = getRandomColor();
			openEntrance(spawn_color_);
			opening_ = true;
			test = false;
		}
		
		if(!test && opening_)
		{
			openEntrance(spawn_color_);
		}
		
		if(entranceOpened(spawn_color_))
		{
			if(!closing_)
			createBall(spawn_color_);
			closeEntrance(spawn_color_);
			closing_ = true;
			clearBallStartDynamics();
		}
		
		if(!test && closing_)
		{
			closeEntrance(spawn_color_);
		}
		
		

		
    }
    
    private void clearBallStartDynamics()
    {
    	for(int i = 0; i < 5; i++)
    	{
    		if(ballNode_[i] != null)
    		{
    				ballNode_[i].clearDynamics();
    		}
    	}
    }
    
    private void openEntrance(Color color)
    {
    	Vector3f movement = new Vector3f(-1.5f,0,0);
    	
    	switch(color)
    	{
    	case RED:
    		if(redNode_.getLocalTranslation().x > -12)
    		{
    			//System.out.println("jep");
    			redNode_.getLocalTranslation().addLocal(movement.mult(timer.getTimePerFrame()));
    		}
    		else
    			red_opened_ = true;
    		return;	
    	case GREEN:
    		if(greenNode_.getLocalTranslation().x > -12)
    			greenNode_.getLocalTranslation().addLocal(movement.mult(timer.getTimePerFrame()));
    		else
    			green_opened_ = true;
    		return;
    	case BLUE:
    		if(blueNode_.getLocalTranslation().x > -12)
    			blueNode_.getLocalTranslation().addLocal(movement.mult(timer.getTimePerFrame()));
    		else
    			blue_opened_ = true;
    		return;
    	default:
    		return;
    	}
    	
    	
    }
    
    private boolean entranceOpened(Color color)
    {
    	switch(color)
    	{
    	case RED:
    			return red_opened_;
    	case GREEN:
    			return green_opened_;
    	case BLUE:
    			return blue_opened_ ;
    	default: //will not happen
    			return false;
    	}
    }
    
    private Color getRandomColor()
    {
    	switch(generator_.nextInt(3))
    	{
    	case 0:
    		return Color.RED;
    	case 1:
    		return Color.GREEN;
    	case 2:
    		return Color.BLUE;	
    	default:
    			return null;
    	}
    }
    
    private void closeEntrance(Color color)
    {
    	Vector3f movement = new Vector3f(1.5f,0,0);
    	
    	switch(color)
    	{
    	case RED:
    		if(redNode_.getLocalTranslation().x < -9.75f)
    			redNode_.getLocalTranslation().addLocal(movement.mult(timer.getTimePerFrame()));
    		else
    			red_opened_ = false;
    		return;	
    	case GREEN:
    		if(greenNode_.getLocalTranslation().x < -9.75f)
    			greenNode_.getLocalTranslation().addLocal(movement.mult(timer.getTimePerFrame()));
    		else
    			green_opened_ = false;
    	case BLUE:
    		if(blueNode_.getLocalTranslation().x < -9.75f)
    			blueNode_.getLocalTranslation().addLocal(movement.mult(timer.getTimePerFrame()));
    		else
    			blue_opened_ = false;
    	default:
    		return;
    	}
    	
    }
    
    
    private void handleMouse()
    {
		//Handle Mouse
		tmp_screen_pos_.set(MouseInput.get().getXAbsolute(),MouseInput.get().getYAbsolute());	
		tmp_world_coord_ = display.getWorldCoordinates(tmp_screen_pos_, 0);
		//System.out.println("eyePos x: "+ tmp_screen_pos_.x + "       eyePos y: "+ tmp_screen_pos_.y);
		//System.out.println("eyePos x: "+ tmp_world_coord_.x + "       eyePos y: "+ tmp_world_coord_.y);
		pointer_.getLocalTranslation().x = tmp_world_coord_.x;
		pointer_.getLocalTranslation().y = tmp_world_coord_.y;
    }
    
    private void handleEyeInput()
    {
		//Handle Eye Input
		eye_pos_ = eyeTracker_.getEyeTrackData();
		tmp_screen_pos_.set(eye_pos_[0], eye_pos_[1]);
		//System.out.println("eyePos x: "+ eye_pos_[0] + "       eyePos y: "+ eye_pos_[1]);
		tmp_world_coord_ = display.getWorldCoordinates(tmp_screen_pos_, 0);
		pointer_.getLocalTranslation().x = tmp_world_coord_.x;
		pointer_.getLocalTranslation().y = tmp_world_coord_.y;
		
		if(eye_pos_[0] != 0.0f || eye_pos_[1] != 0.0f)
			System.out.println("eyePos x: "+ eye_pos_[0] + "       eyePos y: "+ eye_pos_[1]);
		//System.out.println("coord x: "+ tmp_world_coord_.x + "       coord y: "+ tmp_world_coord_.y);
    }
    
    private void moveGhost()
    {
    	for(int i = 0; i <= 20; i+=4)
    	{
    		if(checkInsideArea(mouse_over_[i], mouse_over_[i+1], mouse_over_[i+2], mouse_over_[i+3]))
    		{
    			switch(i/4)
    			{
    			case 0:
    				ghost_.getLocalTranslation().x = -2.5f;
    				ghost_.getLocalTranslation().y = 4.8f;
    				return;
    			case 1:
    				ghost_.getLocalTranslation().x = 4;
    				ghost_.getLocalTranslation().y = 4.8f;
    				return;
    			case 2:
    				ghost_.getLocalTranslation().x = -4;
    				ghost_.getLocalTranslation().y = -0.2f;
    				return;
    			case 3:
    				ghost_.getLocalTranslation().x = 2.5f;
    				ghost_.getLocalTranslation().y = -0.2f;
    				return;
    			case 4:
    				ghost_.getLocalTranslation().x = -5.5f;
    				ghost_.getLocalTranslation().y = -5.2f;
    				return;
    			case 5:
    				ghost_.getLocalTranslation().x = 1;
    				ghost_.getLocalTranslation().y = -5.2f;
    				return;
    			}
    		}
    	}
    }
    
    private boolean checkInsideArea(Vector2f pos1, Vector2f pos2, Vector2f pos3, Vector2f pos4)
    {
    	if(tmp_world_coord_.x >= pos1.x && tmp_world_coord_.x <= pos2.x &&
    			tmp_world_coord_.y <= pos1.y && tmp_world_coord_.y >= pos3.y )
    		return true;
    	else
    		return false;
    }
   
}
