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

import java.util.concurrent.Callable;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.util.GameTaskQueueManager;
import com.jmex.awt.swingui.JMEDesktopState;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;

/**
 * @author Matthew D. Hicks
 */
public class TestJMEDesktopState extends JMEDesktopState {
	public static void main(String[] args) throws Exception {
		final StandardGame game = new StandardGame("Testing JMEDesktopState");
		game.start();
		
		GameTaskQueueManager.getManager().update(new Callable<Void>() {

			public Void call() throws Exception {
				// Create a DebugGameState - has all the built-in features that SimpleGame provides
				// NOTE: for a distributable game implementation you'll want to use something like
				// BasicGameState instead and provide control features yourself.
				DebugGameState state = new DebugGameState(game);
				Box box = new Box("my box", new Vector3f(0, 0, 0), 2, 2, 2);
				box.setModelBound(new BoundingSphere());
				box.updateModelBound();
				box.updateRenderState();
				state.getRootNode().attachChild(box);
				GameStateManager.getInstance().attachChild(state);
				state.setActive(true);

				final JMEDesktopState desktop = new JMEDesktopState();
				SwingUtilities.invokeAndWait(new Runnable() {

					public void run() {
						JButton button = new JButton("Click Me");
						desktop.getDesktop().getJDesktop().add(button);
						button.setLocation(200, 200);
						button.setSize(button.getPreferredSize());
					}
				});

				GameStateManager.getInstance().attachChild(desktop);
				desktop.setActive(true);

				return null;
			}
		});
	}
}
