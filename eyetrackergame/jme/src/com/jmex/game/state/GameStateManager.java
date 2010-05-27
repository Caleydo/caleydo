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

package com.jmex.game.state;

import java.util.logging.Logger;

/**
 * <code>GameStateManager</code> is nothing more than a singleton 
 * <code>GameStateNode</code>. It should be the root of the GameState "tree".
 * 
 * @see GameStateNode
 * @see GameState
 * 
 * @author Per Thulin
 */
public class GameStateManager extends GameStateNode<GameState> {
    private static final Logger logger = Logger
            .getLogger(GameStateManager.class.getName());
	
	/** The singleton. */
	private static GameStateManager instance;
	
	/**
	 * Private constructor.
	 */
	private GameStateManager() {
		super("Game State Manager");
	}
	
	/**
	 * Creates a new <code>GameStateManager</code>.
	 * 
	 * @return If this is the first time create() is called, a new instance
	 * will be created and returned. Otherwise one should use getInstance()
	 * instead.
	 */
	public static GameStateManager create() {
		if (instance == null) {
			instance = new GameStateManager();
			logger.info("Created GameStateManager");
		}
		return instance;
	}
	
	/**
	 * Returns the singleton instance of this class. <b>Note that create() has
	 * to have been called before this.</b>
	 * 
	 * @return The singleton.
	 */
	public static GameStateManager getInstance() {
		return instance;
	}
	
}
