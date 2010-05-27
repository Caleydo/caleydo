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
package jmetest.settings;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.concurrent.Callable;

import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import com.jme.input.MouseInput;
import com.jme.util.GameTaskQueueManager;
import com.jmex.awt.swingui.JMEDesktopState;
import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.game.StandardGame;
import com.jmex.game.state.GameStateManager;

/**
 * @author Matthew D. Hicks
 */
public class TestSwingSettingsEditor {
	public static void main(String[] args) throws Exception {
		final StandardGame game = new StandardGame("TestSwingSettingsEditor");
		game.start();

		GameTaskQueueManager.getManager().update(new Callable<Void>(){

			public Void call() throws Exception {
				// Create a game state to display the configuration menu
				final JMEDesktopState desktopState = new JMEDesktopState();
				GameStateManager.getInstance().attachChild(desktopState);
				desktopState.setActive(true);

				// Show the mouse cursor
				MouseInput.get().setCursorVisible(true);

				SwingUtilities.invokeAndWait(new Runnable() {

					public void run() {
						JInternalFrame frame = new JInternalFrame();
						frame.setTitle("Configure Settings");
						Container c = frame.getContentPane();
						c.setLayout(new BorderLayout());

						GameSettingsPanel csp = new GameSettingsPanel(game.getSettings());
						c.add(csp, BorderLayout.CENTER);

						frame.pack();
						frame.setLocation(200, 100);
						frame.setVisible(true);
						desktopState.getDesktop().getJDesktop().add(frame);
					}});

				return null;
			}
		});
	}
}