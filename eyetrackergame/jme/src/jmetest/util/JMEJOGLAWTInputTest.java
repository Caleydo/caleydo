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

package jmetest.util;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import com.jme.input.InputHandler;
import com.jme.input.InputSystem;
import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.controls.GameControl;
import com.jme.input.controls.GameControlManager;
import com.jme.input.controls.binding.KeyboardBinding;
import com.jme.input.controls.controller.ActionChangeController;
import com.jme.input.controls.controller.ControlChangeListener;
import com.jme.system.PreferencesGameSettings;
import com.jme.system.jogl.JOGLSystemProvider;
import com.jmex.game.StandardGame;
import com.jmex.game.StandardGame.GameType;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.GameStateManager;

public class JMEJOGLAWTInputTest {

    private StandardGame game;

    private static final Logger logger = Logger.getLogger(JMEJOGLAWTInputTest.class.getName());

    private JMEJOGLAWTInputTest() {
        PreferencesGameSettings pgs = new PreferencesGameSettings( Preferences.userRoot() );
        pgs.setRenderer( JOGLSystemProvider.SYSTEM_IDENTIFIER );
        pgs.setMusic( false );
        pgs.setSFX( false );
        pgs.setWidth( 640 );
        pgs.setHeight( 480 );
        pgs.setFullscreen( false );
        this.game=new StandardGame( "testcase" , GameType.GRAPHICAL , pgs );
        this.game.start();
        BasicGameState state=new BasicGameState("testcase") {
            private InputHandler input=null;

            public final void update(final float tpf) {
                super.update(tpf);
                if(input==null)
                    {GameControlManager gcm=new GameControlManager();
                     GameControl exitControl=gcm.addControl("exit");
                     exitControl.addBinding(new KeyboardBinding(KeyInput.KEY_ESCAPE));
                     this.rootNode.addController(new ActionChangeController(exitControl,new ControlChangeListener(){
                        public void changed(GameControl control, float oldValue,float newValue, float time) {
                            logger.info("[CONTROL]"+control.getName());
                        }

                     }));
                     KeyAdapter ka=new KeyAdapter(){
                         @Override
                         public void keyPressed(KeyEvent e){
                             logger.info(e.toString());
                         }
                     };
                     for(Frame frame:Frame.getFrames())
                         {frame.addKeyListener(ka);
                          for(Component c:frame.getComponents())
                              c.addKeyListener(ka);
                         }
                     KeyInput.get().addListener(new KeyInputListener(){

                         public void onKey(char character, int keyCode, boolean pressed) {
                             logger.info("[onKey] "+character+" "+keyCode+" "+pressed);
                         }

                     });
                     this.input=new InputHandler(){
                         boolean init=false;
                         public void update(float tpf){
                             super.update(tpf);
                             if(!init)
                                 {init=true;
                                  addAction(new InputAction(){
                                    public void performAction(
                                            InputActionEvent evt) {
                                                logger.info("[performAction] "+evt);
                                    }
                                  },InputHandler.DEVICE_KEYBOARD,InputHandler.BUTTON_ALL,InputHandler.AXIS_ALL,true);
                                 }
                         }
                     };
                    }
                this.input.update(tpf);
                InputSystem.update();
            }
        };
        GameStateManager.getInstance().attachChild(state);
        GameStateManager.getInstance().activateAllChildren();
    }

    public static final void main(String[] args) {
        new JMEJOGLAWTInputTest();
    }
}