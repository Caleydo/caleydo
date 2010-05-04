package game;


import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.input.AbsoluteMouse;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.MaterialState.ColorMaterial;

public class Prototype extends SimpleGame
{
	private Node ground_;
	
	private Box player_;
	private Box ghost_;
	
	private Box lowerLevel_;
	
	private Box cliff1_;
	private Box cliff2_;
	
	private Sphere runner_;
	private Sphere mousePointer_;
	private Vector3f runnerVelocity_;
	
	private Quaternion tmpQuat_ = new Quaternion();
	private Vector3f tmpVec_ = new Vector3f();
	private Vector2f tmpScreenPos_ = new Vector2f();
	private Vector3f tmpWorldCoord_ = new Vector3f();
	
	@Override
	protected void simpleInitGame() 
	{
		display.setTitle("Prototype of my game");
		display.getRenderer().setBackgroundColor(ColorRGBA.black);
		input.removeAllFromAttachedHandlers();
		float zoom = 75.0f;
		// setupCamProperties
	    cam.setParallelProjection(true);
	    float aspect = (float) display.getWidth() / display.getHeight();
	    cam.setFrustum(0f, 100.0f, -zoom*aspect, zoom*aspect, -zoom, zoom);
	    cam.update();

	    cam.setLeft(Vector3f.UNIT_X);
	    cam.setUp(Vector3f.UNIT_Y);
	    cam.setDirection(Vector3f.UNIT_Z);
	    cam.setLocation(new Vector3f(0, 0, -100));
	    cam.setDirection(new Vector3f(0, 0, 1));
	    cam.update();
		
		
		//create "runner"
		runner_ = new Sphere("Runner",10,10,3);
		runner_.setModelBound(new BoundingSphere());
		runner_.updateModelBound();
		runner_.setDefaultColor(ColorRGBA.red);	
		runner_.getLocalTranslation().x = 97;
		runner_.getLocalTranslation().y = 7;
		rootNode.attachChild(runner_);
			
		//set speed
		runnerVelocity_ = new Vector3f(-20f, 0f, 0f);
		                
        //create ground
        ground_ = new Node("Ground");
        rootNode.attachChild(ground_);
        
        Box part1 = new Box("Part1",new Vector3f(75,0,0),25,4,0.5f);
        part1.setModelBound(new BoundingBox());
        part1.updateModelBound();
        part1.setDefaultColor(ColorRGBA.white);
        ground_.attachChild(part1);
        
        Box part2 = new Box("Part1",new Vector3f(10,0,0),20,4,0.5f);
        part2.setModelBound(new BoundingBox());
        part2.updateModelBound();
        part2.setDefaultColor(ColorRGBA.white);
        ground_.attachChild(part2);
        
        Box part3 = new Box("Part1",new Vector3f(-65,0,0),35,4,0.5f);
        part3.setModelBound(new BoundingBox());
        part3.updateModelBound();
        part3.setDefaultColor(ColorRGBA.white);
        ground_.attachChild(part3);
        
        lowerLevel_ = new Box("LowerLevel",new Vector3f(0,-5,0),150,1,0.5f);
        lowerLevel_.setModelBound(new BoundingBox());
        lowerLevel_.updateModelBound();
        lowerLevel_.setDefaultColor(ColorRGBA.orange);
        rootNode.attachChild(lowerLevel_);
        
		cliff1_ = new Box("Cliff1",new Vector3f(40,3,0),10,15,0.25f);
        cliff1_.setModelBound(new BoundingBox());
        cliff1_.updateModelBound();
        cliff1_.setDefaultColor(ColorRGBA.black);
        rootNode.attachChild(cliff1_);
        
		cliff2_ = new Box("Cliff2",new Vector3f(-20,3,0),10,15,0.25f);
        cliff2_.setModelBound(new BoundingBox());
        cliff2_.updateModelBound();
        cliff2_.setDefaultColor(ColorRGBA.black);
        rootNode.attachChild(cliff2_);
        
		//create ghost
		ghost_ = new Box("Ghost",new Vector3f(40,3,0),10,1,0.5f);
        ghost_.setModelBound(new BoundingBox());
        ghost_.updateModelBound();
        ghost_.setDefaultColor(ColorRGBA.lightGray);
        rootNode.attachChild(ghost_);
        
		//create player
		player_ = new Box("Player",new Vector3f(40,3,0),10,1,0.5f);
        player_.setModelBound(new BoundingBox());
        player_.updateModelBound();
        player_.setDefaultColor(ColorRGBA.green);
        rootNode.attachChild(player_);
        
        //create mousepointer
		mousePointer_ = new Sphere("MousePointer",10,10,1);
		mousePointer_.setModelBound(new BoundingSphere());
		mousePointer_.updateModelBound();
		mousePointer_.setDefaultColor(ColorRGBA.yellow);
		rootNode.attachChild(mousePointer_);
        
        //runner rotation
		float omega = runnerVelocity_.length() / runner_.getRadius();
		Vector3f worldUpVec = Vector3f.UNIT_Y;
		Vector3f direction = runnerVelocity_.normalize();

		//get axis of rotation
		tmpVec_.set(worldUpVec).crossLocal(direction);
		//create per frame rotation
		tmpQuat_.fromAngleAxis(omega * tpf, tmpVec_);
        
        //test keybinds
		KeyBindingManager.getKeyBindingManager().set("PLAYER_MOVE_RIGHT", KeyInput.KEY_RIGHT);
		KeyBindingManager.getKeyBindingManager().set("PLAYER_MOVE_LEFT", KeyInput.KEY_LEFT);
		KeyBindingManager.getKeyBindingManager().set("PLAYER_MOVE", KeyInput.KEY_SPACE);
		KeyBindingManager.getKeyBindingManager().set("RESTART", KeyInput.KEY_RETURN);

        // Make the object default colors shine through
        MaterialState ms = display.getRenderer().createMaterialState();
        ms.setColorMaterial(ColorMaterial.AmbientAndDiffuse);
        rootNode.setRenderState(ms);
        
        
	}
	
	protected void simpleUpdate()
	{
		//Handle Mouse
		tmpScreenPos_.set(MouseInput.get().getXAbsolute(),MouseInput.get().getYAbsolute());
		tmpWorldCoord_ = display.getWorldCoordinates(tmpScreenPos_, 0);		
		mousePointer_.getLocalTranslation().x = tmpWorldCoord_.x;
		mousePointer_.getLocalTranslation().y = tmpWorldCoord_.y;
		
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER_MOVE", true)) 
			player_.getLocalTranslation().x = ghost_.getLocalTranslation().x;
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER_MOVE_RIGHT", true)) 
			player_.getLocalTranslation().x = -60;
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER_MOVE_LEFT", true)) 
			player_.getLocalTranslation().x = 0;
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("RESTART",true))
		{
			runner_.getLocalTranslation().x = 97;
			runner_.getLocalTranslation().y = 7;
			runnerVelocity_.x = -20;
			runnerVelocity_.y = 0;
			player_.getLocalTranslation().x = 0;
		}
		//move runner
		runner_.getLocalTranslation().addLocal(runnerVelocity_.mult(timer.getTimePerFrame()));
		//rotate runner
		runner_.getLocalRotation().set(tmpQuat_.multLocal(runner_.getLocalRotation()));
		
		//System.out.println("runner pos: " + runner_.getLocalTranslation().x);
		if(mousePointer_.hasCollision(cliff1_,false))
			ghost_.getLocalTranslation().x = 0;
		
		if(mousePointer_.hasCollision(cliff2_,false))
			ghost_.getLocalTranslation().x = -60;
		
		
		if((runner_.hasCollision(cliff1_, false) || runner_.hasCollision(cliff2_,false)) && !runner_.hasCollision(player_, false) )
		{
			runnerVelocity_.y = -20.0f;
		}
		
		if(runner_.hasCollision(lowerLevel_, false))
		{
			runnerVelocity_.x = 0.0f;
			runnerVelocity_.y = 0.0f;
		}
		
		if((runner_.getLocalTranslation().x <= -98) || (runner_.getLocalTranslation().x >= 98))
			runnerVelocity_.x *= -1.0f;
		
	}
	
    public static void main(String[] args) 
    {
        Prototype proto = new Prototype();
        proto.setConfigShowMode(ConfigShowMode.AlwaysShow);
        proto.start();
    }

}
