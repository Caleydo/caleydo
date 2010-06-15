package game;



import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingCapsule;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.light.LightNode;
import com.jme.light.SpotLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Capsule;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.MaterialState.ColorMaterial;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.geometry.PhysicsBox;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

import eyetracker.TrackDataProvider;

public class ECgame extends SimplePhysicsGame
{
	
	private Skybox skybox_;
	
	private StaticPhysicsNode staticNode_;
	private StaticPhysicsNode ghostNode_;
	private StaticPhysicsNode playerNode_;
	private DynamicPhysicsNode dynamicNode_;
	
	private Box entranceRed_;
	private Box entranceGreen_;
	private Box entranceBlue_;
	
	private Sphere pointer_;
	private Vector2f tmp_screen_pos_ = new Vector2f();
	private Vector3f tmp_world_coord_ = new Vector3f();
	
	private Vector2f[] mouse_over_;
	
	private Box player_;
	private Capsule ghost_;
	
	private Quaternion tmp_quat_ = new Quaternion();
	
	
	private TrackDataProvider eyeTracker_;
	
	
	
	protected void simpleInitGame() 
	{
		display.setTitle("Eye-Controlled Game");
		input.removeAllFromAttachedHandlers();
		
		initMouseOverPos();
		
	    initCamera();
	    
		buildSkyBox();
		
		staticNode_ = getPhysicsSpace().createStaticNode();
		rootNode.attachChild( staticNode_ );
		

		initPlatforms();
		initWalls();
	
		initGhost();
		initPlayer();
		
		initTestMouse();
		KeyBindingManager.getKeyBindingManager().set("PLAYER_MOVE", KeyInput.KEY_LCONTROL);
		
        // Make the object default colors shine through
        MaterialState ms = display.getRenderer().createMaterialState();
        ms.setColorMaterial(ColorMaterial.AmbientAndDiffuse);
        ghostNode_.setRenderState(ms);
        ghostNode_.updateRenderState();
		
		//eyeTracker_ = new TrackDataProvider();

			    		
	}
	
	private void initGhost()
	{
		//translucent node
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
		
		player_ = new Box("player",new Vector3f(-2.5f,4.8f,0),0.75f,0.3f,10);
		
		TextureState textStateBricks = display.getRenderer().createTextureState();
		Texture textbricks = TextureManager.loadTexture(ECgame.class.getResource("/data/bricks.jpg"),
				Texture.MinificationFilter.Trilinear, 
				Texture.MagnificationFilter.Bilinear);
		
		textbricks.setWrap(Texture.WrapMode.Repeat);
		textStateBricks.setTexture(textbricks);
		player_.setRenderState(textStateBricks);
		
		playerNode_.attachChild(player_);
		
		playerNode_.generatePhysicsGeometry();
		
	}
	
	private void setAndUpdateModelBounds(TriMesh shape)
	{
		if(shape.getClass() == Box.class)
		{
			shape.setModelBound(new BoundingBox());
			shape.updateModelBound();
		}
		else if(shape.getClass() == Sphere.class)
		{
			shape.setModelBound(new BoundingSphere());
			shape.updateModelBound();
		}
		else if(shape.getClass() == Capsule.class)
		{
			shape.setModelBound(new BoundingCapsule());
			shape.updateModelBound();
		}
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
		entranceRed_ = new Box("entranceRed",new Vector3f(-9.75f,6,0),0.25f,1.5f,10);
		Box topLeftWall = new Box("topLeftWall", new Vector3f(-9.75f,8.75f,0),0.25f,1.25f,10);
		Box topRightWall = new Box("topRightWall", new Vector3f(9.75f,7.25f,0),0.25f,2.75f,10);
		
		entranceGreen_ = new Box("entranceGreen",new Vector3f(-9.75f,1,0),0.25f,1.5f,10);
		Box highMiddleLeftWall = new Box("highMiddleLeftWall",new Vector3f(-9.75f,3.5f,0),0.25f,1,10);
		Box lowMiddleLeftWall = new Box("lowMiddleLeftWall", new Vector3f(-9.75f,-1.5f,0),0.25f,1,10);
		Box highMiddleRightWall = new Box("highMiddleRightWall", new Vector3f(9.75f,2,0),0.25f,2.5f,10);
		
		
		entranceBlue_ = new Box("entranceBlue",new Vector3f(-9.75f,-4,0),0.25f,1.5f,10);
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
		
		
		
		staticNode_.attachChild(entranceRed_);
		staticNode_.attachChild(topLeftWall);
		staticNode_.attachChild(topRightWall);
		staticNode_.attachChild(entranceGreen_);
		staticNode_.attachChild(highMiddleLeftWall);
		staticNode_.attachChild(lowMiddleLeftWall);
		staticNode_.attachChild(highMiddleRightWall);
		staticNode_.attachChild(entranceBlue_);
		staticNode_.attachChild(bottomLeftWall);
		staticNode_.attachChild(bottomRightWall);
		staticNode_.attachChild(lowMiddleRightWall);
		
		staticNode_.generatePhysicsGeometry();
		
		
	}
	
	private void initPlatforms()
	{
		Box topFirst = new Box("topFirst",new Vector3f(-6.625f,4.8f,0),3.375f,0.3f,10);
		Box topSecond = new Box("topSecond",new Vector3f(0.75f,4.8f,0),2.5f,0.3f,10);
		Box topThird = new Box("topThird",new Vector3f(7.375f,4.8f,0),2.625f,0.3f,10);
		
		Box middleFirst = new Box("middleFirst",new Vector3f(-7.375f,-0.2f,0),2.625f,0.3f,10);
		Box middleSecond = new Box("middleSecond",new Vector3f(-0.75f,-0.2f,0),2.5f,0.3f,10);
		Box middleThird = new Box("middleThird",new Vector3f(6.625f,-0.2f,0),3.375f,0.3f,10);
		
		Box bottomFirst = new Box("bottomFirst",new Vector3f(-8.125f,-5.2f,0),1.875f,0.3f,10);
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
		//Handle Mouse
		tmp_screen_pos_.set(MouseInput.get().getXAbsolute(),MouseInput.get().getYAbsolute());
		tmp_world_coord_ = display.getWorldCoordinates(tmp_screen_pos_, 0);
		System.out.println("eyePos x: "+ tmp_world_coord_.x + "       eyePos y: "+ tmp_world_coord_.y);
		pointer_.getLocalTranslation().x = tmp_world_coord_.x;
		pointer_.getLocalTranslation().y = tmp_world_coord_.y;
		
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER_MOVE", true))
		{
			player_.getLocalTranslation().x = ghost_.getLocalTranslation().x + 2.5f;
			player_.getLocalTranslation().y = ghost_.getLocalTranslation().y  - 4.8f;
		}
		
		moveGhost();
		

		
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
