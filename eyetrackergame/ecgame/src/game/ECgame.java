//----------------------------------------------------------------------
/// Filename: ECgame.java
/// Description: Eye-Controlled Game
/// Author: Michael Kerber 0731395
/// Date of Creation: 01.03.2010
/// Last Changes: 17.11.2010
//----------------------------------------------------------------------

package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import jmetest.TutorialGuide.ExplosionFactory;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
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
import com.jme.scene.Spatial.CullHint;
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
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.AudioTrack.TrackType;
import com.jmex.audio.MusicTrackQueue.RepeatType;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.util.SimplePhysicsGame;
import eyetracker.TrackDataProvider;

/**
 * This class initializes the game world and runs the game loop of an eye-controlled game
 */
public class ECgame extends SimplePhysicsGame
{	
	//----------------------------------------------------------------------
	// USE THIS TO SWITCH MOUSE/EYE INPUT -> (false/true)
	//----------------------------------------------------------------------
	private boolean eyeinput_ = true;
	//----------------------------------------------------------------------
	
	private Node explosions_;
	private Node highscoreNode_ = null;
	private Node hud_;
	
	private StaticPhysicsNode staticNode_;
	private StaticPhysicsNode ghostNode_;
	private StaticPhysicsNode playerNode_;
	private StaticPhysicsNode redNode_;
	private StaticPhysicsNode greenNode_;
	private StaticPhysicsNode blueNode_;
	
	private DynamicPhysicsNode[] ballNode_;
	
	private Skybox skybox_;

	private Sphere pointer_;
	private Sphere[] ball_;
	
	private Box entranceRed_;
	private Box entranceGreen_;
	private Box entranceBlue_;
	private Box player_;
	
	private Capsule ghost_;
	
	private Quaternion tmp_quat_ = new Quaternion();
	
	private Vector2f tmp_screen_pos_ = new Vector2f();
	private Vector2f[] mouse_over_;
	private Vector3f tmp_world_coord_ = new Vector3f();
	
	private TrackDataProvider eyeTracker_;
	
	MaterialState ms_;
	
	private Random generator_;
	
	private AudioSystem audio_;
	private AudioTrack explosion_sound_;
	private AudioTrack bounce_sound_;
	private AudioTrack music_normal_;
	private AudioTrack music_game_over_;
	
	private Text player_score_;
	private Text player_lives_;
	private Text show_speed_ball_lvl_;
	private Text show_game_over_;
	private Text show_game_over_score_;
	private Text show_game_over_restart_;
	private Text game_restarted_;
	private Text muted_;
	private Text difficulty_select_;
	private Text difficulty_;
	private Text show_highscore_;
	private Text show_insert_name_;
	private Text insert_name1_;
	private Text insert_name2_;
	private Text insert_name3_;

	private String name_input_;
	private String score_string_;
	private String speed_ball_string_;
	private String[] difficulty_strings_ = {"[<<] EASY [>>]","[<<] NORMAL [>>]","[<<] HARD [>>]","[<<] INSANE [>>]"};
	private String[] alphabet_ = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","_"};
	
	private enum Color {RED, GREEN, BLUE}
	private Color spawn_color_ = null;
	
	private boolean red_opened_ = false;
	private boolean green_opened_ = false;
	private boolean blue_opened_ = false;
	private boolean opening_ = false;
	private boolean closing_ = false;
	private boolean spawning_ = false;
	private boolean game_over_ = false;
	private boolean restarted_ = false;
	private boolean start_screen_ = true;
	private boolean display_highscore_ = false;
	private boolean inserting_ = false;

	private int difficulty_index_ = 1;
	private int lives_ = 3;
	private int score_ = 0;
	private int ball_counter_ = 0;
	private int balls_in_game_ = 1;
	private int max_ball_count_ = 5;
	private int speed_level_ = 1;
	private int max_speed_level_ = 5;
	private int speed_level_step_ = 200;
	private int ball_in_game_step_ = 1000;
	private int step_offset_ = 0;
	private int insert_count_ = 0;
	private int alphabet_select_index_ = 0;
	private ArrayList<Integer> spawn_list_;
	
	private float speed_;
	private float[] eye_pos_ = new float[] { 0f, 0f };
	
	//---------------------------------------------------------------------------------
    /**
     * Initializes the game world
     */
	protected void simpleInitGame() 
	{			
		display.setTitle("Eye-Controlled Game");
		
		//Removes predefined controls
		input.removeAllFromAttachedHandlers();
		
		//Removes the f4 label 
		graphNode.detachChildNamed("f4");
		
		rootNode.setCullHint(CullHint.Never);
		
		generator_ = new Random();
		
		initMouseOverPos();
		initCamera();
		
	    explosions_ = new Node();
	    rootNode.attachChild(explosions_);
	    
		buildSkyBox();
	
		staticNode_ = getPhysicsSpace().createStaticNode();
		rootNode.attachChild( staticNode_ );
		 
        // Make the object default colors shine through
        ms_ = display.getRenderer().createMaterialState();
        ms_.setColorMaterial(ColorMaterial.AmbientAndDiffuse);
        rootNode.setRenderState(ms_);
        
		ExplosionFactory.warmup();
		
		spawn_list_ = new ArrayList<Integer>(); 
		spawn_list_.clear();
		
		initPlatforms();
		initWalls();
	
		ballNode_ = new DynamicPhysicsNode[5];
		ball_ = new Sphere[5];
		
		//Initialize ball_ and ballNode_
		for(int i = 0; i < 5; i++)
		{
			ballNode_[i] = null;
			ball_[i] = null;
		}
		
		//Ball movement speed
		speed_ = 1.3f;
		
		initHUD();
		initGhost();
		initPlayer();
		initTestMouse();
		initSound();
		
		//key bindings
		KeyBindingManager.getKeyBindingManager().set("PLAYER_MOVE", KeyInput.KEY_LCONTROL);
		KeyBindingManager.getKeyBindingManager().set("DETONATE", KeyInput.KEY_SPACE);
		KeyBindingManager.getKeyBindingManager().set("MUTE", KeyInput.KEY_M);
		KeyBindingManager.getKeyBindingManager().set("COMMIT", KeyInput.KEY_RETURN);
		KeyBindingManager.getKeyBindingManager().set("BACKWARD", KeyInput.KEY_LEFT);
		KeyBindingManager.getKeyBindingManager().set("FORWARD", KeyInput.KEY_RIGHT);
		KeyBindingManager.getKeyBindingManager().set("HIGHSCORE", KeyInput.KEY_TAB);
		KeyBindingManager.getKeyBindingManager().set("UP", KeyInput.KEY_UP);
		KeyBindingManager.getKeyBindingManager().set("DOWN", KeyInput.KEY_DOWN);
		KeyBindingManager.getKeyBindingManager().set("RESTART", KeyInput.KEY_BACK);
		//KeyBindingManager.getKeyBindingManager().set("TEST1", KeyInput.KEY_Q);
		//KeyBindingManager.getKeyBindingManager().set("TEST2", KeyInput.KEY_W);
		//KeyBindingManager.getKeyBindingManager().set("TEST3", KeyInput.KEY_E);
		//KeyBindingManager.getKeyBindingManager().set("TEST4", KeyInput.KEY_R);
		
        //init eye tracking
		eyeTracker_ = new TrackDataProvider();
		eyeTracker_.startTracking();
		
		//showPhysics = true;		    		
	}
	//---------------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------------
    /**
     * Adds a ball to the scene
     * @param color : Color of the ball
     */
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
		
		if(index == 5)
			return;
		
		rootNode.attachChild(ballNode_[index]);
		
		ColorRGBA temp_color;
		Vector3f start_pos;
		
		switch(color)
		{
			case RED:
				start_pos = new Vector3f(-11,5.5f,0);
				break;
			case GREEN:
				start_pos = new Vector3f(-11,0.5f,0);
				break;
			case BLUE:
				start_pos = new Vector3f(-11,-4.5f,0);
				break;
			default:
				return;
		}
		
		Color new_color = getRandomColor();	
		
		switch(new_color)
		{
			case RED:
				temp_color = ColorRGBA.red;
				break;
			case GREEN:
				temp_color = ColorRGBA.green;
				break;
			case BLUE:
				temp_color = ColorRGBA.blue;
				break;
			default:
				return;
		}
				
		String name = "ball"+index;
		
		ball_[index] = new Sphere(name,new Vector3f(0,0,0),10,10,0.4f);
		ball_[index].setModelBound(new BoundingSphere());
		ball_[index].updateModelBound();
		ball_[index].setDefaultColor(temp_color);
	
		ballNode_[index].attachChild(ball_[index]);
		
		ballNode_[index].getLocalTranslation().set(start_pos);
		ballNode_[index].generatePhysicsGeometry();
        ballNode_[index].setRenderState(ms_);
        ballNode_[index].updateRenderState();
		
        ball_counter_++;
	}
	//---------------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------------
    /**
     * Removes a Ball from the game
     * @param index : index in ball_ and ballNode_
     */
	private void removeBall(int index)
	{
		ball_[index] = null;
		ballNode_[index].delete();
		ballNode_[index] = null;
		
		ball_counter_--;
	}
	//---------------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------------
    /**
     * Initializes the Head Up Display
     */
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
		
		//speed and ball level
		speed_ball_string_ = "BALLS/SPEED: " + balls_in_game_ +"/"+speed_level_;
		show_speed_ball_lvl_ = Text.createDefaultTextLabel("speed",speed_ball_string_);
		show_speed_ball_lvl_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		show_speed_ball_lvl_.setLightCombineMode(Spatial.LightCombineMode.Off);
		show_speed_ball_lvl_.setLocalTranslation(new Vector3f(display.getWidth()/2 -150 , display.getHeight() -30, 0));
		show_speed_ball_lvl_.setLocalScale(new Vector3f(1.5f,1.5f,0));
		
		//init game over text
		show_game_over_ = Text.createDefaultTextLabel("gameover","GAME OVER");
		show_game_over_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		show_game_over_.setLightCombineMode(Spatial.LightCombineMode.Off);
		show_game_over_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
		show_game_over_.setLocalScale(new Vector3f(1.5f,1.5f,0));
		show_game_over_.setTextColor(ColorRGBA.red);
		hud_.attachChild(show_game_over_);
		
		//init game over score
		show_game_over_score_ = Text.createDefaultTextLabel("finalscore", "SCORE: ");
		show_game_over_score_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		show_game_over_score_.setLightCombineMode(Spatial.LightCombineMode.Off);
		show_game_over_score_.setTextColor(ColorRGBA.red);
		show_game_over_score_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
		show_game_over_score_.setLocalScale(new Vector3f(1.5f,1.5f,0));
		
		//init game over restart
		show_game_over_restart_ = Text.createDefaultTextLabel("score", "HIT [BACKSPACE] TO RESTART");
		show_game_over_restart_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		show_game_over_restart_.setLightCombineMode(Spatial.LightCombineMode.Off);
		show_game_over_restart_.setTextColor(ColorRGBA.red);
		show_game_over_restart_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
		show_game_over_restart_.setLocalScale(new Vector3f(1.5f,1.5f,0));
		
		//show restarted
		game_restarted_ = Text.createDefaultTextLabel("restarted","GAME RESTARTED");
		game_restarted_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		game_restarted_.setLightCombineMode(Spatial.LightCombineMode.Off);
		game_restarted_.setTextColor(ColorRGBA.red);
		game_restarted_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
		game_restarted_.setLocalScale(new Vector3f(1.5f,1.5f,0));
		
		//show muted
		muted_ = Text.createDefaultTextLabel("muted","GAME MUTED");
		muted_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		muted_.setLightCombineMode(Spatial.LightCombineMode.Off);
		muted_.setTextColor(ColorRGBA.red);
		muted_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
		muted_.setLocalScale(new Vector3f(1.5f,1.5f,0));
		
		//show difficulty select	
		difficulty_select_ = Text.createDefaultTextLabel("diffselect","SELECT DIFFICULTY");
		difficulty_select_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		difficulty_select_.setLightCombineMode(Spatial.LightCombineMode.Off);
		difficulty_select_.setTextColor(ColorRGBA.red);
		difficulty_select_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
		difficulty_select_.setLocalScale(new Vector3f(4f,4f,4f));
		
		//show difficulty
		difficulty_ = Text.createDefaultTextLabel("diffselect",difficulty_strings_[difficulty_index_]);
		difficulty_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		difficulty_.setLightCombineMode(Spatial.LightCombineMode.Off);
		difficulty_.setTextColor(ColorRGBA.red);
		difficulty_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
		difficulty_.setLocalScale(new Vector3f(5f,5f,5f));
		
		//show highscore
		show_highscore_ = Text.createDefaultTextLabel("highscore","HIT [TAB] TO SEE HIGHSCORES");
		show_highscore_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		show_highscore_.setLightCombineMode(Spatial.LightCombineMode.Off);
		show_highscore_.setTextColor(ColorRGBA.red);
		show_highscore_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
		show_highscore_.setLocalScale(new Vector3f(1.5f,1.5f,1.5f));
		
		//show insert name
		show_insert_name_ = Text.createDefaultTextLabel("showinsert","INSERT NAME:");
		show_insert_name_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		show_insert_name_.setLightCombineMode(Spatial.LightCombineMode.Off);
		show_insert_name_.setTextColor(ColorRGBA.red);
		show_insert_name_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
		show_insert_name_.setLocalScale(new Vector3f(3f,3f,3f));
		
		//insert name
		insert_name1_= Text.createDefaultTextLabel("insert1","_");
		insert_name1_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		insert_name1_.setLightCombineMode(Spatial.LightCombineMode.Off);
		insert_name1_.setTextColor(ColorRGBA.red);
		insert_name1_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
		insert_name1_.setLocalScale(new Vector3f(3f,3f,3f));
		
		//insert name
		insert_name2_ = Text.createDefaultTextLabel("insert2","_");
		insert_name2_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		insert_name2_.setLightCombineMode(Spatial.LightCombineMode.Off);
		insert_name2_.setTextColor(ColorRGBA.red);
		insert_name2_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
		insert_name2_.setLocalScale(new Vector3f(3f,3f,3f));
		
		//insert name
		insert_name3_ = Text.createDefaultTextLabel("insert3","_");
		insert_name3_.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		insert_name3_.setLightCombineMode(Spatial.LightCombineMode.Off);
		insert_name3_.setTextColor(ColorRGBA.red);
		insert_name3_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
		insert_name3_.setLocalScale(new Vector3f(3f,3f,3f));
		
		hud_.attachChild(player_lives_);
		hud_.attachChild(player_score_);
		hud_.attachChild(show_speed_ball_lvl_);
		hud_.attachChild(show_game_over_score_);
		hud_.attachChild(show_game_over_restart_);
		hud_.attachChild(game_restarted_);
		hud_.attachChild(muted_);
		hud_.attachChild(difficulty_select_);
		hud_.attachChild(difficulty_);
		hud_.attachChild(show_highscore_);
		hud_.attachChild(show_insert_name_);
		hud_.attachChild(insert_name1_);
		hud_.attachChild(insert_name2_);
		hud_.attachChild(insert_name3_);
		hud_.attachChild(life1);
		hud_.attachChild(life2);
		hud_.attachChild(life3);
        hud_.setRenderState(ms_);
        hud_.updateRenderState();
	}
	//---------------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------------
    /**
     * Redraws the HUD Speedlevel and Ballcount
     */
	private void updateHUDSpeedAndBalls()
	{
		speed_ball_string_ = "BALLS/SPEED: "+balls_in_game_+"/"+speed_level_;
		show_speed_ball_lvl_.print(speed_ball_string_);
	}
	//---------------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------------
    /**
     * Redraws the HUD Score and Lives
     */
	private void updateHUDScoreAndLives()
	{
		score_string_ = "SCORE: " + score_;
		player_score_.print(score_string_);
		
		ParticleMesh explosion;
		int temp = hud_.getChildren().size()-15;
		
		if(temp != lives_)
		{
			switch(lives_)
			{
				case 0:
					explosion = createExplosion(ColorRGBA.red);
					explosion.getLocalScale().set(new Vector3f(0.03f,0.03f,0.03f));
					Vector3f temp_vec = ((Sphere)hud_.getChild("life1")).center;
					explosion.getLocalTranslation().set(temp_vec.x-1.2f,temp_vec.y,temp_vec.z);
					explosion_sound_.setWorldPosition(explosion.getWorldTranslation());
					explosion_sound_.play();
					hud_.detachChild(hud_.getChild("life1"));
					return;
				case 1:
					explosion = createExplosion(ColorRGBA.green);
					explosion.getLocalScale().set(new Vector3f(0.03f,0.03f,0.03f));
					Vector3f temp_vec1 = ((Sphere)hud_.getChild("life2")).center;
					explosion.getLocalTranslation().set(temp_vec1.x-0.6f,temp_vec1.y,temp_vec1.z);
					explosion_sound_.setWorldPosition(explosion.getWorldTranslation());
					explosion_sound_.play();
					hud_.detachChild(hud_.getChild("life2"));
					return;
				case 2:
					explosion = createExplosion(ColorRGBA.blue);
					explosion.getLocalScale().set(new Vector3f(0.03f,0.03f,0.03f));
					explosion.getLocalTranslation().set(((Sphere)hud_.getChild("life3")).center);
					explosion_sound_.setWorldPosition(explosion.getWorldTranslation());
					explosion_sound_.play();
					hud_.detachChild(hud_.getChild("life3"));
					return;
				case 3:
					return;
				default:
					game_over_ = true;
			}
		}	
	}
	//---------------------------------------------------------------------------------	
	
	//---------------------------------------------------------------------------------
    /**
     * Initializes the ghost preview
     */
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
	//---------------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------------
    /**
     * Defines the mouse/eye positions to move the ghost preview (6 squares)
     */
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
	//---------------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------------
    /**
     * Initialize the player bridge
     */
	private void initPlayer()
	{
		playerNode_ = getPhysicsSpace().createStaticNode();	
		rootNode.attachChild(playerNode_);
		
		player_ = new Box("player",new Vector3f(0,0,0),0.75f,0.3f,10);
			
		player_.setModelBound(new BoundingBox());
		player_.updateModelBound();
		player_.setDefaultColor(ColorRGBA.lightGray);
		
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
	//---------------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------------
    /**
     * Initialize the pointer
     */
	private void initTestMouse()
	{
		pointer_ = new Sphere("pointer", new Vector3f(0,0,10.01f),10, 10, 0.1f);
		rootNode.attachChild(pointer_);
	}
	//---------------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------------
    /**
     * Main function to start the game
     */
    public static void main(String[] args) 
    {
        ECgame game = new ECgame();
        game.setConfigShowMode(ConfigShowMode.AlwaysShow, ECgame.class.getResource("/data/icg.png"));
        game.start();
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Creates the Doors and Walls on the left and right side of the screen
     */
	private void initWalls()
	{		
		redNode_ = getPhysicsSpace().createStaticNode();
		greenNode_ = getPhysicsSpace().createStaticNode();
		blueNode_ = getPhysicsSpace().createStaticNode();
		
		rootNode.attachChild(redNode_);
		rootNode.attachChild(greenNode_);
		rootNode.attachChild(blueNode_);
		
		entranceRed_ = new Box("entranceRed",new Vector3f(0,0,0),0.25f,1.5f,10);
		entranceRed_.setModelBound(new BoundingBox());
		entranceRed_.updateModelBound();
		Box topLeftWall = new Box("topLeftWall", new Vector3f(-9.75f,8.75f,0),0.25f,1.25f,10);
		topLeftWall.setModelBound(new BoundingBox());
		topLeftWall.updateModelBound();
		Box topRightWall = new Box("topRightWall", new Vector3f(9.75f,7.25f,0),0.25f,2.75f,10);
		topRightWall.setModelBound(new BoundingBox());
		topRightWall.updateModelBound();
			
		
		entranceGreen_ = new Box("entranceGreen",new Vector3f(0,0,0),0.25f,1.5f,10);
		entranceGreen_.setModelBound(new BoundingBox());
		entranceGreen_.updateModelBound();
		Box highMiddleLeftWall = new Box("highMiddleLeftWall",new Vector3f(-9.75f,3.5f,0),0.25f,1,10);
		highMiddleLeftWall.setModelBound(new BoundingBox());
		highMiddleLeftWall.updateModelBound();
		Box lowMiddleLeftWall = new Box("lowMiddleLeftWall", new Vector3f(-9.75f,-1.5f,0),0.25f,1,10);
		lowMiddleLeftWall.setModelBound(new BoundingBox());
		lowMiddleLeftWall.updateModelBound();
		Box highMiddleRightWall = new Box("highMiddleRightWall", new Vector3f(9.75f,2,0),0.25f,2.5f,10);
		highMiddleRightWall.setModelBound(new BoundingBox());
		highMiddleRightWall.updateModelBound();
		
		
		entranceBlue_ = new Box("entranceBlue",new Vector3f(0,0,0),0.25f,1.5f,10);
		entranceBlue_.setModelBound(new BoundingBox());
		entranceBlue_.updateModelBound();
		Box bottomLeftWall = new Box("bottomLeftWall", new Vector3f(-9.75f,-7.75f,0),0.25f,2.25f,10);
		bottomLeftWall.setModelBound(new BoundingBox());
		bottomLeftWall.updateModelBound();
		Box bottomRightWall = new Box("bottomRightWall", new Vector3f(9.75f,-7.75f,0),0.25f,2.25f,10);
		bottomRightWall.setModelBound(new BoundingBox());
		bottomRightWall.updateModelBound();
		Box lowMiddleRightWall = new Box("lowMiddleRightWall", new Vector3f(9.75f,-3,0),0.25f,2.5f,10);
		lowMiddleRightWall.setModelBound(new BoundingBox());
		lowMiddleRightWall.updateModelBound();

		
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
	//---------------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------------
    /**
     * Creates the three platform floors
     */
	private void initPlatforms()
	{
		Box topFirst = new Box("topFirst",new Vector3f(-8.125f,4.8f,0),4.875f,0.3f,10);
		topFirst.setModelBound(new BoundingBox());
		topFirst.updateModelBound();
		Box topSecond = new Box("topSecond",new Vector3f(0.75f,4.8f,0),2.5f,0.3f,10);
		topSecond.setModelBound(new BoundingBox());
		topSecond.updateModelBound();
		Box topThird = new Box("topThird",new Vector3f(7.375f,4.8f,0),2.625f,0.3f,10);	
		topThird.setModelBound(new BoundingBox());
		topThird.updateModelBound();
		
		Box middleFirst = new Box("middleFirst",new Vector3f(-8.875f,-0.2f,0),4.125f,0.3f,10);
		middleFirst.setModelBound(new BoundingBox());
		middleFirst.updateModelBound();
		Box middleSecond = new Box("middleSecond",new Vector3f(-0.75f,-0.2f,0),2.5f,0.3f,10);
		middleSecond.setModelBound(new BoundingBox());
		middleSecond.updateModelBound();
		Box middleThird = new Box("middleThird",new Vector3f(6.625f,-0.2f,0),3.375f,0.3f,10);
		middleThird.setModelBound(new BoundingBox());
		middleThird.updateModelBound();
		
		Box bottomFirst = new Box("bottomFirst",new Vector3f(-9.625f,-5.2f,0),3.375f,0.3f,10);
		bottomFirst.setModelBound(new BoundingBox());
		bottomFirst.updateModelBound();
		Box bottomSecond = new Box("bottomSecond",new Vector3f(-2.25f,-5.2f,0),2.5f,0.3f,10);
		bottomSecond.setModelBound(new BoundingBox());
		bottomSecond.updateModelBound();
		Box bottomThird = new Box("bottomThird",new Vector3f(5.875f,-5.2f,0),4.125f,0.3f,10);
		bottomThird.setModelBound(new BoundingBox());
		bottomThird.updateModelBound();
		
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
	//---------------------------------------------------------------------------------
    
	//---------------------------------------------------------------------------------
    /**
     * Initializes the static camera for 2D View
     */
    private void initCamera()
    {
		float zoom = 10f;
	    cam.setParallelProjection(true);
	    cam.setFrustum(0f, 100.0f, zoom, -zoom, -zoom, zoom);
	    cam.update();

	    cam.setLeft(Vector3f.UNIT_X);
	    cam.setUp(Vector3f.UNIT_Y);
	    cam.setDirection(Vector3f.UNIT_Z);
	    cam.setLocation(new Vector3f(0, 0, 100));
	    cam.setDirection(new Vector3f(0, 0, -1));
	    cam.update();
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Creates a box around the gaming field to simulate background in every direction
     */
    private void buildSkyBox() 
    {
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
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * The game loop - this is executed with 30 FPS
     */
    protected void simpleUpdate()
    {  	 
    	//check Restart Button
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("RESTART", false))
			restart();

		//Check toggle mute
		if(audio_.isMuted())
			showMuted();
		
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("MUTE", false))
		{
			if(audio_.isMuted())
			{
				audio_.unmute();
				muted_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
			}
			else
			{
				audio_.mute();
				muted_.getLocalTranslation().set(0 - muted_.getWidth(), 30, 0);
			}
		}
		
		if(restarted_)
			showRestarted();
		
		//Game over ?
    	if(game_over_)
    		gameOver();
    	else
    	{
    		if(!spawning_)
        		spawn_color_ = null;
    		
	    	ExplosionFactory.cleanExplosions();
	    	
	    	//change mouse simulation or eye tracking
	    	if(eyeinput_)
	    		handleEyeInput();
	    	else
	    		handleMouse();
			
	    	//move key pressed?
			if (KeyBindingManager.getKeyBindingManager().isValidCommand("PLAYER_MOVE", false))
			{
				playerNode_.getLocalTranslation().x = ghost_.getLocalTranslation().x;
				playerNode_.getLocalTranslation().y = ghost_.getLocalTranslation().y;
			}
			
			//explosion key pressed?
			if(KeyBindingManager.getKeyBindingManager().isValidCommand("DETONATE", false))
			{
				ParticleMesh explosion = ExplosionFactory.getExplosion();
				explosion.getLocalScale().set(new Vector3f(0.08f,0.08f,0.08f));
				explosion.getLocalTranslation().set(playerNode_.getLocalTranslation().x,playerNode_.getLocalTranslation().y+0.7f,10);
				explosion.forceRespawn();
				explosions_.attachChild(explosion);
				explosion_sound_.setWorldPosition(explosion.getWorldTranslation());
				explosion_sound_.play();
				
				//check if a ball was on the bridge
		    	for(int i = 0; i < 5; i++)
		    	{
		    		if(ballNode_[i] != null)
		    		{
		    			if(ball_[i].hasCollision(player_,false))
		    			{
		    				ballNode_[i].getLocalTranslation().set(new Vector3f(ballNode_[i].getLocalTranslation().x+3f,ballNode_[i].getLocalTranslation().y+7f,0));
		    				ParticleMesh explosion1 = ExplosionFactory.getExplosion();
		    				explosion1.getLocalScale().set(new Vector3f(0.08f,0.08f,0.08f));
		    				explosion1.getLocalTranslation().set(ballNode_[i].getLocalTranslation().x,ballNode_[i].getLocalTranslation().y,10);
		    				explosion1.forceRespawn();
		    				explosions_.attachChild(explosion1);
		    				explosion_sound_.setWorldPosition(explosion1.getWorldTranslation());
		    				explosion_sound_.play();
		    			}
		    		}
		    	}			
			}
			
			//check ball collision for bounce sound
	    	for(int i = 0; i < 5; i++)
	    	{
	    		if(ballNode_[i] != null)
	    		{
	    			Vector3f temp_vec = ballNode_[i].getLinearVelocity(null);

	    			if((ball_[i].hasCollision(playerNode_,false) || ball_[i].hasCollision(staticNode_,false)) && temp_vec.y < -3f)
	    			{
	    				bounce_sound_.setWorldPosition(ballNode_[i].getWorldTranslation());
	    				bounce_sound_.play();
	    			}   				
	    		}
	    	}
			
	    	//while playing change difficulty dynamically
	    	if(!start_screen_)
	    		dynamicDifficulty();
	    	else
	    	{
	    		//handle difficulty selection
				if (KeyBindingManager.getKeyBindingManager().isValidCommand("BACKWARD", false))
				{
					difficulty_index_ = (difficulty_index_ + 3) % 4;
					difficulty_.print(difficulty_strings_[difficulty_index_]);
					bounce_sound_.setWorldPosition(difficulty_.getWorldTranslation());
					bounce_sound_.play();
				}
				
				if (KeyBindingManager.getKeyBindingManager().isValidCommand("FORWARD", false))
				{
					difficulty_index_ = (difficulty_index_ + 1) % 4;
					difficulty_.print(difficulty_strings_[difficulty_index_]);
					bounce_sound_.setWorldPosition(difficulty_.getWorldTranslation());
					bounce_sound_.play();
				}
				
	    		showDifficultySelect();
	    		
				if (KeyBindingManager.getKeyBindingManager().isValidCommand("COMMIT", false))
					changeDifficulty();	
	    	}
	    	
	    	//update score and lives
			handleScoreAndLives();
			
			//handle ball movement from bottom to top floor
			handleBallFallingDown();
			
			//keep track of the spawning procedure
			handleList();
	
			/* Debug Key bindings
			if (KeyBindingManager.getKeyBindingManager().isValidCommand("TEST1", false))
			{
				//if(!spawning_)
				//spawn_list_.add(0);
				//writeHighscore();
			}
			
			if (KeyBindingManager.getKeyBindingManager().isValidCommand("TEST2", false))
			{
				game_over_ = true;
				//readHighscore();
			}
			if (KeyBindingManager.getKeyBindingManager().isValidCommand("TEST3", false))
			{
				speed_level_ = (speed_level_+ 1) % 6;
				speed_ = 1.3f + 0.1f*speed_level_;
				this.updateHUDSpeedAndBalls();	
			}
			if (KeyBindingManager.getKeyBindingManager().isValidCommand("TEST4", false))
			{
				balls_in_game_ = (balls_in_game_+ 1) % 6;
				this.updateHUDSpeedAndBalls();
			}
			*/
			
			//update ghost Position
			moveGhost();	
			
			//open close entrances and spawn balls
			if(spawn_color_ != null)
			{
				if(opening_)
					openEntrance(spawn_color_);
				else if(closing_)
					closeEntrance(spawn_color_);
				else if(entranceOpened(spawn_color_))
					spawnBall();
			}
    	}
    	
    	//update audio system
    	audio_.update();
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Checks if the player managed to get a high score position
     * @return true, false
     */
    private boolean checkInHighscore()
    {
    	ArrayList<String> from_file = readHighscore();
    	
    	if(from_file == null)
    		return true;
    	
    	if(Integer.parseInt(from_file.get(from_file.size()-3)) < score_ || (from_file.size()/3 < 10))
    		return true;
    	else
    		return false;
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Checks if the player managed to get a high score position
     */
    private void writeHighscore()
    {
		// Stream to write file	
    	ArrayList<String> from_file = readHighscore();  	
    	
    	if(from_file != null)
    	{
	    	if(from_file.size() >= 30)
	    	{		
		    	String[] sorted_ = new String[from_file.size()/3];
		    	
		    	for(int i = 0; i < from_file.size()-3; i+=3)
		    		sorted_[i/3] = from_file.get(i) + " " + from_file.get(i+1) + " " + from_file.get(i+2);
		    	
				try
				{
				    // Open an output stream
					FileOutputStream fout = new FileOutputStream ("bin/data/highscore.kerby",false);
				    PrintStream ps = new PrintStream(fout);
				    // Print a line of text
				    ps.println(sorted_[0]);
				    
				    ps.close();
				    fout.close();
				    
				    fout = new FileOutputStream ("bin/data/highscore.kerby",true);
				    ps = new PrintStream(fout);
				    
				    for(int i = 1; i < sorted_.length-1; i++)
				    	ps.println(sorted_[i]);
				    
				    // Close our output stream
				    ps.close();
				    fout.close();		
				}		
				
				// Catches any error conditions
				catch (IOException e)
				{
					System.err.println ("Unable to write to file");
					System.exit(-1);
				}
	    	}
    	}
    	
    	name_input_ = insert_name1_.getText().toString() + insert_name2_.getText().toString() + insert_name3_.getText().toString();
    	String temp = "N/A";
    	
    	switch(difficulty_index_)
    	{
	    	case 0:
	    		temp = "EASY";
	    		break;
	    	case 1:
	    		temp = "NORMAL";
	    		break;
	    	case 2:
	    		temp = "HARD";
	    		break;
	    	case 3:
	    		temp = "INSANE";
	    		break;
	    	default:
	    		break;	
    	}
    	
    	String highscore_output = score_ + " " + name_input_ + " " + temp; 
    			
		try
		{
		    // Open an output stream
			FileOutputStream fout = new FileOutputStream ("bin/data/highscore.kerby",true);

		    // Print a line of text
		    new PrintStream(fout).println(highscore_output);

		    // Close our output stream
		    fout.close();		
		}		
		
		// Catches any error conditions
		catch (IOException e)
		{
			System.err.println ("Unable to write to file");
			System.exit(-1);
		}
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Reads the high score file
     */
    private ArrayList<String> readHighscore()
    {
		boolean end = false;
		ArrayList<String> temp_list = new ArrayList<String>();
		
		try
		{
		    // Open an input stream
			File file = new File("bin/data/highscore.kerby");
			file.createNewFile();
			
			FileInputStream fin = new FileInputStream("bin/data/highscore.kerby");
			InputStreamReader isr = new InputStreamReader(fin);
		    BufferedReader bis = new BufferedReader(isr);
		    
		    // Read a line of text
		    while(!end)
		    {
		    	String temp = bis.readLine();
		    	if(temp == null)
		    		end = true;
		    	else
		    		temp_list.add(temp);
		    }
		        
		    if(temp_list.size() < 1)
		    	return null;
		    
		    // Close our input stream
		    bis.close();
		    isr.close();
		    fin.close();
		    
		    StringTokenizer tokenizer;
		    
		    ArrayList<String> return_string = new ArrayList<String>();

		    for(int i = 0; i < temp_list.size(); i++)
		    {
		    	tokenizer = new StringTokenizer(temp_list.get(i), " ");
		    	for(int j = 0; j < 3; j++)
		    		return_string.add(tokenizer.nextToken());
		    }
		    
		    for(int i = 0; i < return_string.size()-3; i +=3)
		    {
		    	for(int j = 0; j < return_string.size()-3; j +=3)
		    	{
		    		if(Integer.parseInt(return_string.get(j)) < Integer.parseInt(return_string.get(j+3)))
		    		{
		    			String temp1 = return_string.get(j);
		    			String temp2 = return_string.get(j+1);
		    			String temp3 = return_string.get(j+2);
		    			
		    			return_string.set(j, return_string.get(j+3));
		    			return_string.set(j+1, return_string.get(j+4));
		    			return_string.set(j+2, return_string.get(j+5));
		    			return_string.set(j+3, temp1);
		    			return_string.set(j+4, temp2);
		    			return_string.set(j+5, temp3);
		    		}	    			
		    	}
		    }
		   
		    return return_string;    
		}
		// Catches any error conditions
		catch (IOException e)
		{
			System.err.println ("Unable to read from file");
			System.exit(-1);
			return null;
		}
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Display high score Screen
     */
    private void showHighscore()
    {
    	show_game_over_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
    	show_highscore_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
    	
    	highscoreNode_ = new Node();
    	rootNode.attachChild(highscoreNode_);
    	
    	Text temp;
    	
		temp = Text.createDefaultTextLabel("HS","HIGHSCORES");
		temp.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		temp.setLightCombineMode(Spatial.LightCombineMode.Off);
		temp.setTextColor(ColorRGBA.red);
		temp.setLocalTranslation(new Vector3f(150, display.getHeight()-150, 0));
		temp.setLocalScale(new Vector3f(3f,3f,3f));
		highscoreNode_.attachChild(temp);
		
		temp = Text.createDefaultTextLabel("HSS","SCORE");
		temp.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		temp.setLightCombineMode(Spatial.LightCombineMode.Off);
		temp.setTextColor(ColorRGBA.red);
		temp.setLocalTranslation(new Vector3f(150, highscoreNode_.getChild("HS").getWorldTranslation().y-50, 0));
		temp.setLocalScale(new Vector3f(2.5f,2.5f,2.5f));
		highscoreNode_.attachChild(temp);
		
		temp = Text.createDefaultTextLabel("HSN","NAME");
		temp.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		temp.setLightCombineMode(Spatial.LightCombineMode.Off);
		temp.setTextColor(ColorRGBA.red);
		temp.setLocalTranslation(new Vector3f(150 + 250, highscoreNode_.getChild("HS").getWorldTranslation().y-50, 0));
		temp.setLocalScale(new Vector3f(2.5f,2.5f,2.5f));
		highscoreNode_.attachChild(temp);
		
		temp = Text.createDefaultTextLabel("HSD","DIFFICULTY");
		temp.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		temp.setLightCombineMode(Spatial.LightCombineMode.Off);
		temp.setTextColor(ColorRGBA.red);
		temp.setLocalTranslation(new Vector3f(150 + 500, highscoreNode_.getChild("HS").getWorldTranslation().y-50, 0));
		temp.setLocalScale(new Vector3f(2.5f,2.5f,2.5f));
		highscoreNode_.attachChild(temp);
    	  	
    	ArrayList<String> from_file = readHighscore();
    		
    	for(int i = 0; i < from_file.size(); i++)
    	{		
    		temp = Text.createDefaultTextLabel("HS"+i,from_file.get(i));
    		temp.setRenderQueueMode(Renderer.QUEUE_ORTHO);
    		temp.setLightCombineMode(Spatial.LightCombineMode.Off);
    		temp.setTextColor(ColorRGBA.red);
    		
    		switch(i%3)
    		{
	    		case 0:
	    			temp.setLocalTranslation(new Vector3f(150,  highscoreNode_.getChild("HSS").getWorldTranslation().y - 40 - ((i/3)*30), 0));
	    			break;
	    		case 1:
	    			temp.setLocalTranslation(new Vector3f(150 + 250,  highscoreNode_.getChild("HSS").getWorldTranslation().y - 40 - ((i/3)*30), 0));
	    			break;
	    		case 2:
	    			temp.setLocalTranslation(new Vector3f(150 + 500,  highscoreNode_.getChild("HSS").getWorldTranslation().y - 40 - ((i/3)*30), 0));
	    			break;
	    		default:
	    			break;
    		}
    		
    		temp.setLocalScale(new Vector3f(2f,2f,2f));
    		highscoreNode_.attachChild(temp);		
    	} 
    	
    	show_game_over_restart_.setLocalTranslation(new Vector3f(display.getWidth()/2 - show_game_over_restart_.getWidth()/2, temp.getWorldTranslation().y - 150, 0));
    	show_game_over_score_.print("YOUR SCORE: "+score_);
    	show_game_over_score_.setLocalTranslation(new Vector3f(display.getWidth()/2 - show_game_over_score_.getWidth()/2,temp.getWorldTranslation().y - 100, 0)); 	
    }
    //--------------------------------------------------------------------------------- 
    
    //---------------------------------------------------------------------------------
    /**
     * Handles the game over procedure and shows the game over screen
     */
    private void gameOver()
    {
    	//delete current balls
    	for(int i = 0; i < 5; i++)
    	{
    		if(ballNode_[i] != null)
    			ballNode_[i].delete();
    	}
    	
    	//already displaying high scores?
    	if(!display_highscore_)
    	{
	    	if(show_game_over_.getLocalTranslation().x == display.getWidth()+1000)
	    	{
	    		if(checkInHighscore())
	    			inserting_ = true;
	    		
	    		//change music
	    		audio_.getMusicQueue().clearTracks();
	    		audio_.getMusicQueue().addTrack(music_game_over_);
	    		audio_.getMusicQueue().play();
	    	}
	    	
	    	show_game_over_.setLocalTranslation(new Vector3f(display.getWidth()/2 - show_game_over_.getWidth()/2, display.getHeight()/2-show_game_over_.getHeight()/2, 0));
	 
	    	if(show_game_over_.getLocalScale().x <= 10)
	    		show_game_over_.getLocalScale().addLocal(new Vector3f(0.7f,0.7f,0.7f).mult(timer.getTimePerFrame()));
	    	else
	    	{ 	
	    		if(inserting_)
	    		{ 		
	    			//player is entering his name
	    			show_game_over_score_.setLocalScale(new Vector3f(3,3,3));
		    		show_game_over_score_.setTextColor(ColorRGBA.red);
		    		show_game_over_score_.print("SCORE: "+score_);
		    		show_game_over_score_.setLocalTranslation(new Vector3f(display.getWidth()/2 - show_game_over_score_.getWidth()/2, show_game_over_.getLocalTranslation().y-100, 0));
	    			
	    			show_insert_name_.setLocalTranslation(new Vector3f(new Vector3f(display.getWidth()/2 - show_insert_name_.getWidth()/2 - 100, show_game_over_.getLocalTranslation().y-200, 0)));
	    			insert_name1_.setLocalTranslation(new Vector3f(new Vector3f(show_insert_name_.getLocalTranslation().x + 400, show_insert_name_.getLocalTranslation().y, 0)));
	    			insert_name2_.setLocalTranslation(new Vector3f(new Vector3f(insert_name1_.getLocalTranslation().x + 30, show_insert_name_.getLocalTranslation().y, 0)));
	    			insert_name3_.setLocalTranslation(new Vector3f(new Vector3f(insert_name2_.getLocalTranslation().x + 30, show_insert_name_.getLocalTranslation().y, 0)));
	    			
	    			if(insert_count_ < 3)
	    				handleInsert();
	    			else
	    			{
	    				writeHighscore();
	    				insert_name1_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
	    				insert_name2_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
	    				insert_name3_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
	    				show_insert_name_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
	    				inserting_ = false;
	    			}
	    		}
	    		else
	    		{
	    			//player finished entering his name
		    		show_game_over_restart_.setLocalScale(new Vector3f(3,3,3));
		    		show_game_over_restart_.setTextColor(ColorRGBA.red);
		    		show_game_over_restart_.setLocalTranslation(new Vector3f(display.getWidth()/2 - show_game_over_restart_.getWidth()/2, show_game_over_.getLocalTranslation().y+show_game_over_.getHeight()/2+100, 0));  		
		    		
		    		show_game_over_score_.setLocalScale(new Vector3f(3,3,3));
		    		show_game_over_score_.setTextColor(ColorRGBA.red);
		    		show_game_over_score_.print("SCORE: "+score_);
		    		show_game_over_score_.setLocalTranslation(new Vector3f(display.getWidth()/2 - show_game_over_score_.getWidth()/2, show_game_over_.getLocalTranslation().y-100, 0));
		    		
		    		show_highscore_.setLocalScale(new Vector3f(3,3,3));
		    		show_highscore_.setTextColor(ColorRGBA.red);
		    		show_highscore_.setLocalTranslation(new Vector3f(display.getWidth()/2 - show_highscore_.getWidth()/2, show_game_over_restart_.getLocalTranslation().y+100, 0));
		    		  		
					if (KeyBindingManager.getKeyBindingManager().isValidCommand("HIGHSCORE", false))
					{
						display_highscore_ = true;
						showHighscore();
					}
	    		}
	    	}	
    	}
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Handles the name insert procedure
     */
    private void handleInsert()
    {  	
    	//up key pressed?
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("UP", false))
		{
			alphabet_select_index_ = (alphabet_select_index_ + 1) % 26;	
			
			switch(insert_count_)
			{
				case 0:
					insert_name1_.print(alphabet_[alphabet_select_index_]);
					return;
				case 1:
					insert_name2_.print(alphabet_[alphabet_select_index_]);
					return;
				case 2:
					insert_name3_.print(alphabet_[alphabet_select_index_]);
					return;
				default:
					return;
			}		
		}
		
		//down key pressed?
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("DOWN", false))
		{
			alphabet_select_index_ = (alphabet_select_index_ + 25) % 26;
			
			switch(insert_count_)
			{
				case 0:
					insert_name1_.print(alphabet_[alphabet_select_index_]);
					return;
				case 1:
					insert_name2_.print(alphabet_[alphabet_select_index_]);
					return;
				case 2:
					insert_name3_.print(alphabet_[alphabet_select_index_]);
					return;
				default:
					return;
			}
		}
		
		//commit key pressed?
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("COMMIT", false))
			insert_count_++;
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Change difficulty according to players selection
     */
    private void changeDifficulty()
    {
    	switch(difficulty_index_)
    	{
	    	case 0: //easy
	    		ball_in_game_step_ = 2500;
	    		speed_level_step_ = 500;
	    		break;
	    	case 1: //normal //slower on higher level
	    		ball_in_game_step_ = 1000;
	    		speed_level_step_ = 200;
	    		break;
	    	case 2:	//hard
	    		ball_in_game_step_ = 1000;
	    		speed_level_step_ = 100;
	    		break;
	    	case 3: //insane
	    		ball_in_game_step_ = 500;
	    		speed_level_step_ = 100;
	    		break;
	    	default:
	    		break;
    	}
    	
    	start_screen_ = false;
    	difficulty_select_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
    	difficulty_.setLocalTranslation(new Vector3f(display.getWidth()+1000, display.getHeight()+1000, 0));
    }
 	//---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Shows the difficulty selection screen
     */
    private void showDifficultySelect()
    {
    	difficulty_.setLocalTranslation(new Vector3f(display.getWidth()/2 - difficulty_.getWidth()/2, display.getHeight()/2-difficulty_.getHeight()/2 -100, 0));
    	difficulty_select_.getLocalTranslation().set(display.getWidth()/2 - difficulty_select_.getWidth()/2, difficulty_.getLocalTranslation().y+difficulty_select_.getHeight()/2+200, 0);
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Shows the "muted" Text label
     */
    private void showMuted()
    {
    	muted_.getLocalTranslation().addLocal(new Vector3f(200,0,0).mult(timer.getTimePerFrame()));
    	if(muted_.getLocalTranslation().x >= display.getWidth())
    		muted_.getLocalTranslation().set(0 - muted_.getWidth(), 30, 0);
    } 
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Shows the "restarted" Text label
     */
    private void showRestarted()
    {
    	game_restarted_.getLocalTranslation().addLocal(new Vector3f(200,0,0).mult(timer.getTimePerFrame()));
    	if(game_restarted_.getLocalTranslation().x >= display.getWidth())
    		restarted_ = false;
    }   
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Resets game information and restarts the game
     */
    private void restart()
    {
    	for(int i = 0; i < 5; i++)
    	{
    		if(ballNode_[i] != null)
    		{
    			ballNode_[i].delete();
    			ballNode_[i] = null;
    		}
    	}
    	
    	if(highscoreNode_ != null)
    	{
	    	highscoreNode_.detachAllChildren();
	    	rootNode.detachChild(highscoreNode_);
	    	highscoreNode_ = null;
    	}
    	
		red_opened_ = false;
		green_opened_ = false;
		blue_opened_ = false;
		opening_ = false;
		closing_ = false;
		spawning_ = false;
		game_over_ = false;
		start_screen_ = true;
		display_highscore_ = false;
		
		lives_ = 3;
		score_ = 0;
		ball_counter_ = 0;
		balls_in_game_ = 1;
		max_ball_count_ = 5;
		speed_level_ = 1;
		max_speed_level_ = 5;
		spawn_color_ = null;
		speed_ = 1.3f;
		speed_level_step_ = 200;
		ball_in_game_step_ = 1000;
		step_offset_ = 0;
		difficulty_index_ = 1;
		insert_count_ = 0;
		alphabet_select_index_ = 0;
		
		spawn_list_.clear();
    	
    	redNode_.getLocalTranslation().x = -9.75f;
    	greenNode_.getLocalTranslation().x = -9.75f;
    	blueNode_.getLocalTranslation().x = -9.75f;
    		  	
    	hud_.detachAllChildren();
    	
    	initHUD();
    	
		ghost_.getLocalTranslation().x = -2.5f;
		ghost_.getLocalTranslation().y = 4.8f;
		ghost_.getLocalTranslation().z = 0;
		
		audio_.getMusicQueue().clearTracks();
		audio_.getMusicQueue().addTrack(music_normal_);
		audio_.getMusicQueue().play();
    	
		playerNode_.getLocalTranslation().set(new Vector3f(-2.5f,4.8f,0));
		restarted_ = true;
		game_restarted_.getLocalTranslation().set(0 - game_restarted_.getWidth(), 50, 0);
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Changes difficulty during the game according to the player score
     */
    private void dynamicDifficulty()
    {	 	
    	if(ball_counter_ < balls_in_game_)
    	{
    		if(!spawning_)
    			spawn_list_.add(0);
    	}
    	
    	if(score_ == (balls_in_game_ * ball_in_game_step_ + step_offset_))
    	{
    		if(balls_in_game_ < 5)
    		{
    			balls_in_game_++;
    			
    			if(difficulty_index_ == 1)
    			{
	        		ball_in_game_step_ = 2500;
	        		speed_level_step_ = 500;	
	        		step_offset_ = -1500;
    			}
    			else if(difficulty_index_ == 2)
    	        	speed_level_step_ = 200;	
    			
    			speed_level_ = 1;
    			speed_ = 1.3f;
    			
    			updateHUDSpeedAndBalls();
    		}
    	}
    	
    	if(score_ == ((speed_level_ * speed_level_step_ + step_offset_)+((balls_in_game_-1)*ball_in_game_step_)))
    	{
    		speed_level_++;	
    		
    		if(speed_level_ <= max_speed_level_)
    		{
    			updateHUDSpeedAndBalls();
    			speed_ += 0.2f;
    		}		
    	}
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Handles the ball spawn buffer list
     */   
    private void handleList()
    {
    	if(!spawn_list_.isEmpty())
    	{
    		if(!spawning_)
    		{
    			initSpawnBall();
    			spawn_list_.remove(spawn_list_.size()-1);
    		}
    	}
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Spawns a new ball
     */
    private void spawnBall()
    {
		createBall(spawn_color_);
		closing_ = true;
    }   
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Changes logic flags so a door can open and a ball can spawn
     */
    private void initSpawnBall()
    {
    	spawning_ = true;
    	spawn_color_ = getRandomColor();
    	opening_ = true;
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Makes a ball able to "fall" from the bottom floor to the top floor
     */
    private void handleBallFallingDown()
    {
    	for(int i = 0; i < 5; i++)
    	{
    		if(ballNode_[i] != null)
    		{
    			if(ballNode_[i].getLocalTranslation().y < -11)
    			{
    				ballNode_[i].getLocalTranslation().setY(10);
    				ballNode_[i].getLocalTranslation().setX(ballNode_[i].getLocalTranslation().x-1.8f);
    			}
    		}
    	}	
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Creates an explosion of a specific color
     * @param color : Color of the explosion
     * @return ParticleMesh of the explosion
     */
    private ParticleMesh createExplosion(ColorRGBA color)
    {
		ParticleMesh explosion = ExplosionFactory.getExplosion();
		explosion.getLocalScale().set(new Vector3f(0.08f,0.08f,0.08f));
		explosion.setStartColor(color);
		explosion.setEndColor(color);
		explosion.forceRespawn();
		explosions_.attachChild(explosion);   	
		
		return explosion;
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Checks collision of balls and walls and adapts score and lives
     */
    private void handleScoreAndLives()
    {
    	ColorRGBA temp_color;
    	
    	for(int i = 0; i < 5; i++)
    	{
    		if(ballNode_[i] != null)
    		{	
    			temp_color = ball_[i].getDefaultColor();
    
    			if(ball_[i].hasCollision(staticNode_.getChild("topRightWall"),false))
    			{
    				//red
    				if(temp_color == ColorRGBA.red)
    				{
    					score_ += 100;
    					updateHUDScoreAndLives();
    					ParticleMesh explosion = createExplosion(temp_color);
    					explosion.getLocalTranslation().set(ballNode_[i].getLocalTranslation().x,ballNode_[i].getLocalTranslation().y,10);
    					removeBall(i);
    					explosion_sound_.setWorldPosition(explosion.getWorldTranslation());
    					explosion_sound_.play();
    				}
    				else
    				{
    					lives_--;
    					updateHUDScoreAndLives();
    					ParticleMesh explosion = createExplosion(ColorRGBA.darkGray);
    					explosion.getLocalTranslation().set(ballNode_[i].getLocalTranslation().x,ballNode_[i].getLocalTranslation().y,10);
    					removeBall(i);
    					explosion_sound_.setWorldPosition(explosion.getWorldTranslation());
    					explosion_sound_.play();
    				}
    			}
    			else if(ball_[i].hasCollision(staticNode_.getChild("highMiddleRightWall"),false))
				{	
					//green
    				if(temp_color == ColorRGBA.green)
    				{
    					score_ += 100;
    					updateHUDScoreAndLives();
    					ParticleMesh explosion = createExplosion(temp_color);
    					explosion.getLocalTranslation().set(ballNode_[i].getLocalTranslation().x,ballNode_[i].getLocalTranslation().y,10);
    					removeBall(i);
    					explosion_sound_.setWorldPosition(explosion.getWorldTranslation());
    					explosion_sound_.play();
    				}
    				else
    				{
    					lives_--;
    					updateHUDScoreAndLives();
    					ParticleMesh explosion = createExplosion(ColorRGBA.darkGray);
    					explosion.getLocalTranslation().set(ballNode_[i].getLocalTranslation().x,ballNode_[i].getLocalTranslation().y,10);
    					removeBall(i);
    					explosion_sound_.setWorldPosition(explosion.getWorldTranslation());
    					explosion_sound_.play();
    				}
				}
				else if(ball_[i].hasCollision(staticNode_.getChild("lowMiddleRightWall"),false))
				{
					//blue
    				if(temp_color == ColorRGBA.blue)
    				{
    					score_ += 100;
    					updateHUDScoreAndLives();
    					ParticleMesh explosion = createExplosion(temp_color);
    					explosion.getLocalTranslation().set(ballNode_[i].getLocalTranslation().x,ballNode_[i].getLocalTranslation().y,10);
    					removeBall(i);
    					explosion_sound_.setWorldPosition(explosion.getWorldTranslation());
    					explosion_sound_.play();
    				}
    				else
    				{
    					lives_--;
    					updateHUDScoreAndLives();
    					ParticleMesh explosion = createExplosion(ColorRGBA.darkGray);
    					explosion.getLocalTranslation().set(ballNode_[i].getLocalTranslation().x,ballNode_[i].getLocalTranslation().y,10);
    					removeBall(i);
    					explosion_sound_.setWorldPosition(explosion.getWorldTranslation());
    					explosion_sound_.play();
    				}
				}
    		}
    	}
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Opens the entrance with a specific color
     * @param color : Color of the entrance
     */ 
    private void openEntrance(Color color)
    {
    	Vector3f movement = new Vector3f(-1.5f,0,0);
    	
    	switch(color)
    	{
	    	case RED:
	    		if(redNode_.getLocalTranslation().x > -12)
	    			redNode_.getLocalTranslation().addLocal(movement.mult(timer.getTimePerFrame()));
	    		else
	    		{
	    			opening_ = false;
	    			red_opened_ = true;
	    		}
	    		return;	
	    	case GREEN:
	    		if(greenNode_.getLocalTranslation().x > -12)
	    			greenNode_.getLocalTranslation().addLocal(movement.mult(timer.getTimePerFrame()));
	    		else
	    		{
	    			opening_ = false;
	    			green_opened_ = true;
	    		}
	    		return;
	    	case BLUE:
	    		if(blueNode_.getLocalTranslation().x > -12)
	    			blueNode_.getLocalTranslation().addLocal(movement.mult(timer.getTimePerFrame()));
	    		else
	    		{
	    			opening_ = false;
	    			blue_opened_ = true;
	    		}
	    		return;
	    	default:
	    		return;
    	}	
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Checks if the entrance with a specific color is already open
     * @param color : Color of the entrance
     * @return status flag of the entrance
     */  
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
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Returns a random color
     * @return Green, Red, Blue
     */
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
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Closes the entrance with a specific color
     * @param color : Color of the entrance
     */
    private void closeEntrance(Color color)
    {
    	Vector3f movement = new Vector3f(speed_,0,0);
    	
    	switch(color)
    	{
	    	case RED:
	    		if(redNode_.getLocalTranslation().x < -9.75f)
	    			redNode_.getLocalTranslation().addLocal(movement.mult(timer.getTimePerFrame()));
	    		else
	    		{
	    			closing_ = false;
	    			red_opened_ = false;
	    			spawning_ = false;
	    		}
	    			
	    		return;	
	    	case GREEN:
	    		if(greenNode_.getLocalTranslation().x < -9.75f)
	    			greenNode_.getLocalTranslation().addLocal(movement.mult(timer.getTimePerFrame()));
	    		else
	        	{
	        		closing_ = false;
	        		green_opened_ = false;
	        		spawning_ = false;
	        	}
	    		return;
	    	case BLUE:
	    		if(blueNode_.getLocalTranslation().x < -9.75f)
	    			blueNode_.getLocalTranslation().addLocal(movement.mult(timer.getTimePerFrame()));
	    		else
	    		{
	    			closing_ = false;
	    			blue_opened_ = false;
	    			spawning_ = false;
	    		}
	    		return;
	    	default:
	    		return;
    	}   	
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Move the pointer according to the mouse position
     */
    private void handleMouse()
    {
		//Handle Mouse
		tmp_screen_pos_.set(MouseInput.get().getXAbsolute(),MouseInput.get().getYAbsolute());	
		tmp_world_coord_ = display.getWorldCoordinates(tmp_screen_pos_, 0);
		pointer_.getLocalTranslation().x = tmp_world_coord_.x;
		pointer_.getLocalTranslation().y = tmp_world_coord_.y;
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Moves the pointer according to the players gaze position
     */
    private void handleEyeInput()
    {
		//Handle Eye Input
		eye_pos_ = eyeTracker_.getEyeTrackData();
		tmp_screen_pos_.set(eye_pos_[0], eye_pos_[1]);
		
		//invert
		tmp_screen_pos_.y = display.getHeight()-tmp_screen_pos_.y;
		tmp_world_coord_ = display.getWorldCoordinates(tmp_screen_pos_, 0);
		pointer_.getLocalTranslation().x = tmp_world_coord_.x;
		pointer_.getLocalTranslation().y = tmp_world_coord_.y;
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Moves the ghost according to the pointer position
     */
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
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Checks if the mouse/gaze position is inside a specific area (rectangle)
     * @param pos1 : top left corner of the rectangle
     * @param pos2 : top right corner of the rectangle
     * @param pos3 : bottom left corner of the rectangle
     * @param pos4 : bottom right corner of the rectangle
     * @return true, false
     */
    private boolean checkInsideArea(Vector2f pos1, Vector2f pos2, Vector2f pos3, Vector2f pos4)
    {
    	if(tmp_world_coord_.x >= pos1.x && tmp_world_coord_.x <= pos2.x &&
    			tmp_world_coord_.y <= pos1.y && tmp_world_coord_.y >= pos3.y )
    		return true;
    	else
    		return false;
    }
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Initializes the audio system
     */
    private void initSound() 
    {
		// grab a handle to our audio system.
		audio_ = AudioSystem.getSystem();

		// setup ear tracker to track the camera's position and orientation.
		audio_.getEar().trackOrientation(cam);
		audio_.getEar().trackPosition(cam);

		// setup a music score
		music_normal_ = getMusic(ECgame.class.getResource("/data/theartofgardens.ogg"));
		music_game_over_ = getMusic(ECgame.class.getResource("/data/threedrops.ogg"));
		audio_.getMusicQueue().setRepeatType(RepeatType.ALL);
		audio_.getMusicQueue().setCrossfadeinTime(2.5f);
		audio_.getMusicQueue().setCrossfadeoutTime(2.5f);
		audio_.getMusicQueue().addTrack(music_normal_);
		audio_.getMusicQueue().play();
		
		explosion_sound_ = audio_.createAudioTrack("/data/explosion.ogg", false);
		explosion_sound_.setRelative(true);
		explosion_sound_.setMaxAudibleDistance(100000);
		explosion_sound_.setVolume(0.3f);
		explosion_sound_.setTargetVolume(0.3f);
		
		bounce_sound_ = audio_.createAudioTrack("/data/ball.wav", false);
		bounce_sound_.setRelative(true);
		bounce_sound_.setMaxAudibleDistance(100000);
		bounce_sound_.setVolume(0.4f);
		bounce_sound_.setTargetVolume(0.4f);
	}
    //---------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------
    /**
     * Loads a sound file
     * @param resource : URL of the sound file
     * @return AudioTrack of the sound file
     */
	private AudioTrack getMusic(URL resource) {
		// Create a non-streaming, non-looping, relative sound clip.
		AudioTrack sound = AudioSystem.getSystem().createAudioTrack(resource, true);
		sound.setType(TrackType.MUSIC);
		sound.setRelative(true);
		sound.setTargetVolume(0.7f);
		sound.setLooping(false);
		return sound;
	}

}
