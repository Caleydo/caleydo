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

package jmetest.game.state;

import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;

/**
 * The input handler we use to navigate the menu. E.g. has an absolute mouse.
 * If the escape key is pressed the application will be ended using the static
 * exit method of TestGameStateSystem.
 * 
 * @author Per Thulin
 */
public class MenuHandler extends InputHandler {
    private GameState myState;

    public MenuHandler( GameState myState ) {
        setKeyBindings();
        this.myState = myState;
    }

    private void setKeyBindings() {
        KeyBindingManager.getKeyBindingManager().set("exit", KeyInput.KEY_ESCAPE);
        addAction( new ExitAction(), "exit", false );

        KeyBindingManager.getKeyBindingManager().set("enter", KeyInput.KEY_RETURN);
        addAction( new EnterAction(), "enter", false );
    }

    private static class ExitAction extends InputAction {
        public void performAction( InputActionEvent evt ) {
            TestGameStateSystem.exit();
        }
    }

    private class EnterAction extends InputAction {
        public void performAction( InputActionEvent evt ) {
            GameState ingame = new IngameState("ingame");
            ingame.setActive(true);
            GameStateManager.getInstance().attachChild(ingame);
            myState.setActive(false); // Deactivate this (the menu) state.
        }
    }
}