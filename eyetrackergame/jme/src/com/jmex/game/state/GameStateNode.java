/*
 * Copyright (c) 2003-2010 jMonkeyEngine
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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <code>GameStateNode</code> maintains a list of other <code>GameState</code>s
 * to process (update and render). It's typically good for handling groups of 
 * GameStates that you want to process together. A concrete example would be 
 * an ingame state together with an ingame-menu state.
 * <p>
 * Due to it extending GameState, you can attach other GameStateNodes,
 * forming a tree structure. 
 * 
 * @author Per Thulin
 */
public class GameStateNode<G extends GameState> extends GameState {
	
	/** Contains all the maintained children. */
	protected ArrayList<G> children;
	
	/**
	 * Creates a new GameStateNode with a given name.
	 * 
	 * @param name The name of this GameStateNode.
	 */
	public GameStateNode(String name) {
		this.name = name;
		children = new ArrayList<G>();
	}
	
	/**
	 * Updates all maintained children (calling their update method).
	 */
	public void update(float tpf) {
		for (int i = 0; i < children.size(); i++) {
			GameState state = children.get(i);
			if (state.isActive()) {
				state.update(tpf);
			}
		}
	}

	/**
	 * Renders all maintained children (calling their render method).
	 */
	public void render(float tpf) {
		for (int i = 0; i < children.size(); i++) {
			GameState state = children.get(i);
			if (state.isActive()) {
				state.render(tpf);
			}
		}
	}

	/**
	 * Will perform cleanup on and detach all maintained GameState's.
	 */
	public void cleanup() {
		for (int i = 0; i < children.size(); i++) {
			GameState state = children.get(i);
			state.cleanup();
		}
		detachAllChildren();
	}
	
	/**
	 * Attaches a child to this node. This node will become the child's parent.
	 * 
	 * @param state The child to attach.
	 */
	public void attachChild(G state) {
		state.setParent(this);
		children.add(state);
	}
	
	/**
	 * Detaches a given child.
	 * 
	 * @param state The child to detach.
	 */
	public void detachChild(GameState state) {
		children.remove(state);
	}
	
	/**
	 * Detaches the first child found with a given name (case sensitive).
	 * 
	 * @param name The name of the child to detach.
	 */
	public void detachChild(String name) {
		detachChild(getChild(name));
	}
	
	/**
	 * Detaches a child at a given index.
	 * 
	 * @param i The index of the child to be detached.
	 */
	public void detachChild(int i) {
		detachChild(getChild(i));
	}
	
	/**
	 * Detaches all children of this GameStateNode.
	 */
	public void detachAllChildren() {
        children.clear();
	}
	
	/**
	 * Returns the list of GameStates maintained by this GameStateNode.
	 * 
	 * @return The list of GameStates maintained by this GameStateNode.
	 */
	public ArrayList<G> getChildren() {
		return children;
	}
	
    /**
     * <code>getChild</code> returns the first child found with
     * exactly the given name (case sensitive).
     * 
     * @param name
     *            the name of the child to retrieve.
     * @return the child if found, or null.
     */
    public GameState getChild(String name) {
        Iterator<G> it = children.iterator();
        while (it.hasNext()) {
            G child = it.next();
            if (name.equals(child.getName())) return child;
        }
        return null;
    }
    
    /**
     * <code>getChild</code> returns a child at a given index.
     * 
     * @param i
     *         The index to retrieve the child from.
     * @return The child at a specified index.
     */
    public G getChild(int i) {
    	return children.get(i);
    }
	
    /**
     * <code>getQuantity</code> returns the number of children this node
     * maintains.
     * 
     * @return The number of children this node maintains.
     */
    public int getQuantity() {
        return children.size();
    }
    
    /**
     * Determines if the provided <code>GameState</code> is contained in the 
     * children list of this node.
     * 
     * @param state The <code>GameState</code> object to check.
     * @return true if the object is contained, false otherwise.
     */
    public boolean hasChild(G state) {
    	return children.contains(state);
    }
    
    /**
     * Determines if a <code>GameState</code> with the name provided is contained
     * in the children list of this node.
     * @param stateName The name of the <code>GameState</code> being searched for.
     * @return true if a <code>GameState</code> with the name provided is found.
     */
    public boolean hasChild(String stateName){
    	Iterator<G> gsIterator = children.iterator();
    	while(gsIterator.hasNext()){
    		if(stateName.equals(gsIterator.next().getName()))
    			return true;
    	}
    	return false;
    }
    
    /**
     * Will call setActive(true) on all GameStates maintained.
     */
    public void activateAllChildren() {
    	for (int i = 0; i < children.size(); i++) {
    		G state = children.get(i);
    		state.setActive(true);
    	}
    }
    
    /**
     * Deactivates all maintained children contained by this GameStateNode.
     */
    public void deactivateAllChildren() {
    	for (int i = 0; i < children.size(); i++) {
    		G state = children.get(i);
    		state.setActive(false);
    	}
    }
    
	/**
	 * Activates the first child found with a given name. Just a wrapper for 
	 * <code>getChild(name).setActive(true)</code>.
	 * 
	 * @param name The name of the GameState to activate.
	 */
	public void activateChildNamed(String name) {
		getChild(name).setActive(true);
	}
	
	/**
	 * Deactivates the first child found with a given name. Just a wrapper for 
	 * <code>getChild(name).setActive(false)</code>.
	 * 
	 * @param name The name of the GameState to deactivate.
	 */
	public void deactivateChildNamed(String name) {
		getChild(name).setActive(false);
	}

}
