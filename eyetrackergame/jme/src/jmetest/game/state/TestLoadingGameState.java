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

import com.jme.util.GameTaskQueueManager;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.load.LoadingGameState;

/**
 * @author Matthew D. Hicks
 */
public class TestLoadingGameState {
	public static void main(String[] args) throws Exception {
		final StandardGame game = new StandardGame("Test LoadingGameState");
		game.getSettings().clear();
		game.start();

		GameTaskQueueManager.getManager().update(new Callable<Void>(){

			public Void call() throws Exception {
				// Create LoadingGameState and enable
				final LoadingGameState loading = new LoadingGameState();
				GameStateManager.getInstance().attachChild(loading);
				loading.setActive(true);

				// Enable DebugGameState
				DebugGameState debug = new DebugGameState(game);
				GameStateManager.getInstance().attachChild(debug);
				debug.setActive(true);

				GameTaskQueueManager.getManager().update(new LoadingTask(loading, 0));
				
				return null;
			}
		});
	}
	
	private static class LoadingTask implements Callable<Void> {
		private final LoadingGameState loading;
		private final int progress;
		
		public LoadingTask(LoadingGameState loading, int progress) {
			super();
			this.loading = loading;
			this.progress = progress;
		}

		public Void call() throws Exception {
			String status;			
			if (progress == 100) {
				status = "I'm Finished!";
			} else if (progress > 80) {
				status = "Almost There!";
			} else if (progress > 70) {
				status = "Loading Something Extremely Useful";
			} else if (progress > 50) {
				status = "More Than Half-Way There!";
			} else if (progress > 20) {
				status = "Loading Something That You Probably Won't Care About";
			} else {
				status = "Started Loading";
			}
									
			Thread.sleep(100);
			loading.setProgress(progress / 100.0f, status);

			if (progress < 100) {				
				GameTaskQueueManager.getManager().update(new LoadingTask(loading, progress + 1));
			}
			return null;
		}
	}
}
