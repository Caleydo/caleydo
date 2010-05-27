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
package jmetest.input.controls;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jme.image.Texture;
import com.jme.input.MouseInput;
import com.jme.input.controls.Binding;
import com.jme.input.controls.GameControl;
import com.jme.input.controls.GameControlManager;
import com.jme.input.controls.controller.ActionChangeController;
import com.jme.input.controls.controller.ActionController;
import com.jme.input.controls.controller.ActionRepeatController;
import com.jme.input.controls.controller.Axis;
import com.jme.input.controls.controller.CameraController;
import com.jme.input.controls.controller.ControlChangeListener;
import com.jme.input.controls.controller.GameControlAction;
import com.jme.input.controls.controller.RotationController;
import com.jme.input.controls.controller.ThrottleController;
import com.jme.input.controls.controller.camera.CameraPerspective;
import com.jme.input.controls.controller.camera.FixedCameraPerspective;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jmex.awt.swingui.JMEDesktopState;
import com.jmex.editors.swing.controls.GameControlEditor;
import com.jmex.game.StandardGame;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.TextGameState;

/**
 * @author Matthew D. Hicks
 */
public class TestSwingControlEditor {
    private static final Logger logger = Logger
            .getLogger(TestSwingControlEditor.class.getName());
    
	private static GameControlManager manager;
	
	public static void main(String[] args) throws Exception {
		final StandardGame game = new StandardGame("TestSwingControlEditor");
		game.start();
		
		GameTaskQueueManager.getManager().update(new SetupState(game));		
	}
			
    private static class SetupState implements Callable<Void> {
    	private final StandardGame game;
    	
    	public SetupState(StandardGame game) {
    		super();
    		this.game = game;
    	}

    	public Void call() throws Exception {
    		// Create our sample GameControls
    		manager = GameControlManager.load(game.getSettings());
    		if (manager == null) {
    			manager = new GameControlManager();
    			manager.addControl("Forward");
    			manager.addControl("Backward");
    			manager.addControl("Rotate Left");
    			manager.addControl("Rotate Right");
    			manager.addControl("Jump");
    			manager.addControl("Crouch");
    			manager.addControl("Run");
    			manager.addControl("Fire");
    			manager.addControl("Cycle Camera");
    		}

    		// Create a game state to display the configuration menu
    		final JMEDesktopState desktopState = new JMEDesktopState();
    		GameStateManager.getInstance().attachChild(desktopState);
    		desktopState.setActive(true);

    		BasicGameState state = new BasicGameState("Basic");
    		GameStateManager.getInstance().attachChild(state);
    		state.setActive(true);

    		// Create Box
    		Box box = new Box("Test Node", new Vector3f(), 5.0f, 5.0f, 5.0f);
    		state.getRootNode().attachChild(box);
    		TextureState ts = game.getDisplay().getRenderer().createTextureState();
    		Texture t = TextureManager.loadTexture(TestSwingControlEditor.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"), Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
    		t.setWrap(Texture.WrapMode.Repeat);
    		ts.setTexture(t);
    		box.setRenderState(ts); 
    		box.updateRenderState();

    		// Create Throttle Controller
    		final ThrottleController throttle = new ThrottleController(box, manager.getControl("Forward"), 1.0f, manager.getControl("Backward"), -1.0f, 0.05f, 0.5f, 1.0f, false, Axis.Z);
    		state.getRootNode().addController(throttle);

    		final TextGameState textState = new TextGameState("Throttle: 0");
    		GameStateManager.getInstance().attachChild(textState);
    		textState.setActive(true);

    		// Monitor the throttle
    		Controller monitor = new Controller() {
    			private static final long serialVersionUID = 1L;

    			public void update(float time) {
    				textState.setText(throttle.getCurrentThrottle() + ", " + throttle.getThrust());
    			}
    		};
    		state.getRootNode().addController(monitor);

    		// Create Rotation Controller
    		state.getRootNode().addController(new RotationController(box, manager.getControl("Rotate Left"), manager.getControl("Rotate Right"), 0.2f, Axis.Y));
    		// Create ActionController
    		GameControlAction action = new GameControlAction() {
    			public void pressed(GameControl control, float time) {
    				logger.info("Pressed: " + control.getName() + ", elapsed: " + time);
    			}

    			public void released(GameControl control, float time) {
    				logger.info("Released: " + control.getName() + " after " + time);
    			}
    		};
    		// Jump and Crouch only care about press and release
    		state.getRootNode().addController(new ActionController(manager.getControl("Jump"), action));
    		state.getRootNode().addController(new ActionController(manager.getControl("Crouch"), action));
    		// Run cares about the change - doesn't really make sense, but this is just for testing
    		ControlChangeListener listener = new ControlChangeListener() {
    			public void changed(GameControl control, float oldValue, float newValue, float time) {
    				logger.info("Changed: " + control.getName() + ", " + oldValue + ", " + newValue + ", " + time);
    			}
    		};
    		state.getRootNode().addController(new ActionChangeController(manager.getControl("Run"), listener));
    		Runnable runnable = new Runnable() {
    			private long lastRun;
    			public void run() {
    				if (lastRun == 0) lastRun = System.currentTimeMillis();
    				logger.info("KABOOM: " + (System.currentTimeMillis() - lastRun));
    				lastRun = System.currentTimeMillis();
    			}
    		};
    		// Fire action can only occur once per second
    		state.getRootNode().addController(new ActionRepeatController(manager.getControl("Fire"), 1000, runnable));
    		// Create CameraController
    		CameraController cc = new CameraController(box, game.getCamera(), manager.getControl("Cycle Camera"));
    		cc.addPerspective(new CameraPerspective() {
    			private Camera camera;
    			private Vector3f location;
    			private Vector3f dir;
    			private Vector3f left;
    			private Vector3f up;

    			public void update(Camera camera, Spatial spatial, float time) {
    				if (this.camera == null) {
    					this.camera = camera;
    					try {
    						location = (Vector3f)camera.getLocation().clone();
    						dir = (Vector3f)camera.getDirection().clone();
    						left = (Vector3f)camera.getLeft().clone();
    						up = (Vector3f)camera.getUp().clone();
    					} catch(Exception exc) {
    						logger.logp(Level.SEVERE, this.getClass().toString(),
    								"main(args)", "Exception", exc);
    					}
    				} else if (!camera.getLocation().equals(location)) {
    					logger.info("Changing from: " + camera.getDirection() + " to " + dir);
    					logger.info("Another: " + camera.getUp() + "\nAnd: " + camera.getLeft());
    					camera.setLocation(location);
    					camera.setDirection(dir);
    					camera.setLeft(left);
    					camera.setUp(up);
    				}
    			}


    			public void setActive(Camera camera, Spatial spatial, boolean active) {
    			}
    		});
    		cc.addPerspective(new FixedCameraPerspective(new Vector3f(0.0f, 0.0f, -15.0f)));
    		state.getRootNode().addController(cc);

    		// Show the mouse cursor
    		MouseInput.get().setCursorVisible(true);

    		SwingUtilities.invokeAndWait(new UISetup(game, desktopState));
    		
    		return null;
    	}};
    	
    private static class UISetup implements Runnable {
    	private final StandardGame game;
    	private final JMEDesktopState desktopState;
    	
    	public UISetup(StandardGame game, JMEDesktopState desktopState) {
			super();
			this.game = game;
			this.desktopState = desktopState;
		}

		public void run() {
			JInternalFrame frame = new JInternalFrame();
			frame.setTitle("Configure Controls");
			Container c = frame.getContentPane();
			c.setLayout(new BorderLayout());
			final GameControlEditor editor = new GameControlEditor(manager, 2);
			c.add(editor, BorderLayout.CENTER);
			JPanel bottom = new JPanel();
			bottom.setLayout(new FlowLayout());
			JButton button = new JButton("Close");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					game.finish();
				}
			});
			bottom.add(button);
			button = new JButton("Clear");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					editor.clear();
				}
			});
			bottom.add(button);
			button = new JButton("Reset");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					editor.reset();
				}
			});
			bottom.add(button);
			button = new JButton("Apply");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					editor.apply();	// Apply bindings
					GameControlManager.save(manager, game.getSettings());	// Save them
					for (GameControl control : manager.getControls()) {
						logger.info(control.getName() + ":");
						for (Binding binding : control.getBindings()) {
							logger.info("\t" + binding.getName());
						}
						logger.info("-------");
					}
				}
			});
			bottom.add(button);
			c.add(bottom, BorderLayout.SOUTH);
			frame.pack();
			frame.setLocation(200, 100);
			frame.setVisible(true);
			desktopState.getDesktop().getJDesktop().add(frame);
		}
    	
    }

}
