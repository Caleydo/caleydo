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

package com.jme.scene;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.renderer.Renderer;
import com.jme.scene.state.RenderState;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.Savable;

/**
 * <code>Node</code> defines an internal node of a scene graph. The internal
 * node maintains a collection of children and handles merging said children
 * into a single bound to allow for very fast culling of multiple nodes. Node
 * allows for any number of children to be attached.
 * 
 * @author Mark Powell
 * @author Gregg Patton
 * @author Joshua Slack
 */
public class Node extends Spatial implements Serializable, Savable {
    private static final Logger logger = Logger.getLogger(Node.class.getName());

    private static final long serialVersionUID = 1L;

    /** This node's children. */
    protected List<Spatial> children;

    /**
     * Default constructor.
     */
    public Node() {
        logger.fine("Node created.");
        setCollisionMask(-1,false);
    }

    /**
     * Constructor instantiates a new <code>Node</code> with a default empty
     * list for containing children.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     */
    public Node(String name) {
        super(name);
        setCollisionMask(-1,false);
        // Newly constitued (not reconsituted) Nodes should allow for maximum
        // collision nesting.  If anything other than -1, the node would block
        // collisions for all descendant Spatials.
        logger.fine("Node created.");
    }

    /**
     * 
     * <code>getQuantity</code> returns the number of children this node
     * maintains.
     * 
     * @return the number of children this node maintains.
     */
    public int getQuantity() {
        if(children == null) {
            return 0;
        } 
            
        return children.size();        
    }
    
    /**
     * <code>getTriangleCount</code> returns the number of triangles contained
     * in all sub-branches of this node that contain geometry.
     * @return the triangle count of this branch.
     */
    @Override
    public int getTriangleCount() {
        int count = 0;
        if(children != null) {
            for(int i = 0; i < children.size(); i++) {
                count += children.get(i).getTriangleCount();
            }
        }
        
        return count;
    }
    
    /**
     * <code>getVertexCount</code> returns the number of vertices contained
     * in all sub-branches of this node that contain geometry.
     * @return the vertex count of this branch.
     */
    @Override
    public int getVertexCount() {
        int count = 0;
        if(children != null) {
            for(int i = 0; i < children.size(); i++) {
               count += children.get(i).getVertexCount();
            }
        }
        
        return count;
    }

    /**
     * 
     * <code>attachChild</code> attaches a child to this node. This node
     * becomes the child's parent. The current number of children maintained is
     * returned.
     * <br>
     * If the child already had a parent it is detached from that former parent.
     * 
     * @param child
     *            the child to attach to this node.
     * @return the number of children maintained by this node.
     */
    public int attachChild(Spatial child) {
        if (child != null) {
            if (child.getParent() != this) {
                if (child.getParent() != null) {
                    child.getParent().detachChild(child);
                }
                child.setParent(this);
                if(children == null) {
                    children = Collections.synchronizedList(new ArrayList<Spatial>(1));  
                }
                children.add(child);
                logger.log(Level.FINE,
                        "Child \"{0}\" attached to this node \"{1}\"", 
                        new String[] {child.getName(), getName()});
            }
        }
        
        if (children == null) return 0;
        return children.size();
    }
    
    /**
     * 
     * <code>attachChildAt</code> attaches a child to this node at an index. This node
     * becomes the child's parent. The current number of children maintained is
     * returned.
     * <br>
     * If the child already had a parent it is detached from that former parent.
     * 
     * @param child
     *            the child to attach to this node.
     * @return the number of children maintained by this node.
     */
    public int attachChildAt(Spatial child, int index) {
        if (child != null) {
            if (child.getParent() != this) {
                if (child.getParent() != null) {
                    child.getParent().detachChild(child);
                }
                child.setParent(this);
                if(children == null) {
                    children = Collections.synchronizedList(new ArrayList<Spatial>(1));  
                }
                children.add(index, child);
                logger.log(Level.FINE,
                        "Child \"{0}\" attached to this node \"{1}\"", 
                        new String[] {child.getName(), getName()});
            }
        }

        if (children == null) return 0;
        return children.size();
    }

    /**
     * <code>detachChild</code> removes a given child from the node's list.
     * This child will no longer be maintained.
     * 
     * @param child
     *            the child to remove.
     * @return the index the child was at. -1 if the child was not in the list.
     */
    public int detachChild(Spatial child) {
        if(children == null) {
            return -1;
        }
        if (child == null)
            return -1;
        if (child.getParent() == this) {
            int index = children.indexOf(child);
            if (index != -1) {
                detachChildAt(index);
            }
            return index;
        } 
            
        return -1;        
    }

    /**
     * <code>detachChild</code> removes a given child from the node's list.
     * This child will no longer be maintained. Only the first child with a
     * matching name is removed.
     * 
     * @param childName
     *            the child to remove.
     * @return the index the child was at. -1 if the child was not in the list.
     */
    public int detachChildNamed(String childName) {
        if(children == null) {
            return -1;
        }
        if (childName == null)
            return -1;
        for (int x = 0, max = children.size(); x < max; x++) {
            Spatial child =  children.get(x);
            if (childName.equals(child.getName())) {
                detachChildAt( x );
                return x;
            }
        }
        return -1;
    }

    /**
     * 
     * <code>detachChildAt</code> removes a child at a given index. That child
     * is returned for saving purposes.
     * 
     * @param index
     *            the index of the child to be removed.
     * @return the child at the supplied index.
     */
    public Spatial detachChildAt(int index) {
        if(children == null) {
            return null;
        }
        Spatial child =  children.remove(index);
        if ( child != null ) {
            child.setParent( null );
            logger.fine("Child removed.");
        }
        return child;
    }

    /**
     * 
     * <code>detachAllChildren</code> removes all children attached to this
     * node.
     */
    public void detachAllChildren() {
        if(children != null) {
            for ( int i = children.size() - 1; i >= 0; i-- ) {
                detachChildAt( i );
            }
            logger.fine("All children removed.");
        }
    }

    public int getChildIndex(Spatial sp) {
        if(children == null) {
            return -1;
        }
        return children.indexOf(sp);
    }

    public void swapChildren(int index1, int index2) {
        Spatial c2 =  children.get(index2);
        Spatial c1 =  children.remove(index1);
        children.add(index1, c2);
        children.remove(index2);
        children.add(index2, c1);
    }

    /**
     * 
     * <code>getChild</code> returns a child at a given index.
     * 
     * @param i
     *            the index to retrieve the child from.
     * @return the child at a specified index.
     */
    public Spatial getChild(int i) {
        if(children == null) {
            return null;
        }
        return children.get(i);
    }

    /**
     * <code>getChild</code> returns the first child found with exactly the
     * given name (case sensitive.)
     * 
     * @param name
     *            the name of the child to retrieve. If null, we'll return null.
     * @return the child if found, or null.
     */
    public Spatial getChild(String name) {
        if (name == null) return null;
        for (int x = 0, cSize = getQuantity(); x < cSize; x++) {
            Spatial child = children.get(x);
            if (name.equals(child.getName())) {
                return child;
            } else if(child instanceof Node) {
                Spatial out = ((Node)child).getChild(name);
                if(out != null) {
                    return out;
                }
            }
        }
        return null;
    }
    
    /**
     * determines if the provided Spatial is contained in the children list of
     * this node.
     * 
     * @param spat
     *            the child object to look for.
     * @return true if the object is contained, false otherwise.
     */
    public boolean hasChild(Spatial spat) {
        if(children == null) {
            return false;
        }
        if (children.contains(spat))
            return true;

        for (int i = 0, max = getQuantity(); i < max; i++) {
            Spatial child =  children.get(i);
            if (child instanceof Node && ((Node) child).hasChild(spat))
                return true;
        }

        return false;
    }

    /**
     * <code>updateWorldData</code> updates all the children maintained by
     * this node.
     * 
     * @param time
     *            the frame time.
     */
    @Override
    public void updateWorldData(float time) {
        super.updateWorldData(time);

        Spatial child;
        for (int i = 0, n = getQuantity(); i < n; i++) {
            try {
                child = children.get(i);
            } catch (IndexOutOfBoundsException e) {
                // a child was removed in updateGeometricState (note: this may
                // skip one child)
                break;
            }
            if (child != null) {
                child.updateGeometricState(time, false);
            }
        }
    }

    @Override
    public void updateWorldVectors(boolean recurse) {
        if (((lockedMode & Spatial.LOCKED_TRANSFORMS) == 0)) {
            updateWorldScale();
            updateWorldRotation();
            updateWorldTranslation();
            
            if (recurse) {
                for (int i = 0, n = getQuantity(); i < n; i++) {
                    children.get(i).updateWorldVectors(true);
                }
            }
        }
    }
    
    @Override
    public void lockBounds() {
        super.lockBounds();
        for (int i = 0, max = getQuantity(); i < max; i++) {
            Spatial child =  children.get(i);
            if (child != null) {
                child.lockBounds();
            }
        }
    }

    @Override
    public void lockShadows() {
        super.lockShadows();
        for (int i = 0, max = getQuantity(); i < max; i++) {
            Spatial child =  children.get(i);
            if (child != null) {
                child.lockShadows();
            }
        }
    }
    
    @Override
    public void lockTransforms() {
        super.lockTransforms();
        for (int i = 0, max = getQuantity(); i < max; i++) {
            Spatial child =  children.get(i);
            if (child != null) {
                child.lockTransforms();
            }
        }
    }

    @Override
    public void lockMeshes(Renderer r) {
        super.lockMeshes(r);
        for (int i = 0, max = getQuantity(); i < max; i++) {
            Spatial child =  children.get(i);
            if (child != null) {
                child.lockMeshes(r);
            }
        }
    }
    
    @Override
    public void unlockBounds() {
        super.unlockBounds();
        for (int i = 0, max = getQuantity(); i < max; i++) {
            Spatial child =  children.get(i);
            if (child != null) {
                child.unlockBounds();
            }
        }
    }
    
    @Override
    public void unlockShadows() {
        super.unlockShadows();
        for (int i = 0, max = getQuantity(); i < max; i++) {
            Spatial child =  children.get(i);
            if (child != null) {
                child.unlockShadows();
            }
        }
    }
    
    @Override
    public void unlockTransforms() {
        super.unlockTransforms();
        for (int i = 0, max = getQuantity(); i < max; i++) {
            Spatial child =  children.get(i);
            if (child != null) {
                child.unlockTransforms();
            }
        }
    }

    @Override
    public void unlockMeshes(Renderer r) {
        super.unlockMeshes(r);
        for (int i = 0, max = getQuantity(); i < max; i++) {
            Spatial child =  children.get(i);
            if (child != null) {
                child.unlockMeshes(r);
            }
        }
    }

    /**
     * <code>draw</code> calls the onDraw method for each child maintained by
     * this node.
     * 
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     * @param r
     *            the renderer to draw to.
     */
    @Override
    public void draw(Renderer r) {
        if(children == null) {
            return;
        }
        Spatial child;
        for (int i = 0, cSize = children.size(); i < cSize; i++) {
            child =  children.get(i);
            if (child != null)
                child.onDraw(r);
        }
    }

    /**
     * Applies the stack of render states to each child by calling
     * updateRenderState(states) on each child.
     * 
     * @param states
     *            The Stack[] of render states to apply to each child.
     */
    @Override
    protected void applyRenderState(Stack<? extends RenderState>[] states) {
        if(children == null) {
            return;
        }
        for (int i = 0, cSize = children.size(); i < cSize; i++) {
            Spatial pkChild = getChild(i);
            if (pkChild != null)
                pkChild.updateRenderState(states);
        }
    }

    @Override
    public void sortLights() {
        if(children == null) {
            return;
        }
        for (int i = 0, cSize = children.size(); i < cSize; i++) {
            Spatial pkChild = getChild(i);
            if (pkChild != null)
                pkChild.sortLights();
        }
    }

    /**
     * <code>updateWorldBound</code> merges the bounds of all the children
     * maintained by this node. This will allow for faster culling operations.
     * 
     * @see com.jme.scene.Spatial#updateWorldBound()
     */
    @Override
    public void updateWorldBound() 
    {
    	updateWorldBound(false);
    }

    /**
     * <code>updateWorldBound</code> merges the bounds of all the children
     * maintained by this node. This will allow for faster culling operations.
     * 
     * @param recursive - if true the whole subtree and its spatials are updated first 
     * 
     */
    public void updateWorldBound(boolean recursive) {
        if ((lockedMode & Spatial.LOCKED_BOUNDS) != 0) return;
        if (children == null) {
            return;
        }
        BoundingVolume worldBound = null;
        for (int i = 0, cSize = children.size(); i < cSize; i++) {
            Spatial child =  children.get(i);
            if (child != null) {
            	// first update the whole subtree
            	if (recursive)
            		if (child instanceof Node)
            			((Node)child).updateWorldBound(true);
            		else
            			child.updateWorldBound();
            	
                if (worldBound != null) {
                    // merge current world bound with child world bound
                    worldBound.mergeLocal(child.getWorldBound());

                } else {
                    // set world bound to first non-null child world bound
                    if (child.getWorldBound() != null) {
                        worldBound = child.getWorldBound().clone(this.worldBound);
                    }
                }
            }
        }
        this.worldBound = worldBound;
    }

    @Override
    public void findCollisions(
            Spatial scene, CollisionResults results, int requiredOnBits) {
        if (getWorldBound() != null && isCollidable(requiredOnBits)
                && scene.isCollidable(requiredOnBits)) {
            if (getWorldBound().intersects(scene.getWorldBound())) {
                // further checking needed.
                for (int i = 0; i < getQuantity(); i++) {
                    getChild(i).findCollisions(scene, results, requiredOnBits);
                }
            }
        }
    }

    @Override
    public boolean hasCollision(
            Spatial scene, boolean checkTriangles, int requiredOnBits) {
        if (this == scene) return false;  // No Collision with "self"
        if (getWorldBound() != null && isCollidable(requiredOnBits)
                && scene.isCollidable(requiredOnBits)) {
            if (getWorldBound().intersects(scene.getWorldBound())) {
                if(children == null && !checkTriangles) {
                    return true;
                }
                // further checking needed.
                for (int i = 0; i < getQuantity(); i++) {
                    if (getChild(i).hasCollision(
                            scene, checkTriangles, requiredOnBits)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void findPick(Ray toTest, PickResults results, int requiredOnBits) {
        if (children == null || getWorldBound() == null
                || !isCollidable(requiredOnBits)
                || !getWorldBound().intersects(toTest)) return;
        // further checking needed.
        for (int i = 0; i < getQuantity(); i++)
            children.get(i).findPick(toTest, results, requiredOnBits);
    }

	/**
	 * Returns all children to this node.
	 *
	 * @return a list containing all children to this node
	 */
	public List<Spatial> getChildren() {
        return children;
    }

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void readObject(java.io.ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        if (children != null) {
            // go through children and set parent to this node
            for (int x = 0, cSize = children.size(); x < cSize; x++) {
                Spatial child = children.get(x);
                child.parent = this;
            }
        }
    }

    public void childChange(Geometry geometry, int index1, int index2) {
        //just pass to parent
        if(parent != null) {
            parent.childChange(geometry, index1, index2);
        }
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        if (children == null)
            e.getCapsule(this).writeSavableArrayList(null, "children", null);
        else
            e.getCapsule(this).writeSavableArrayList(new ArrayList<Spatial>(children), "children", null);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        ArrayList<Spatial> cList = e.getCapsule(this).readSavableArrayList("children", null);
        if (cList == null)
            children = null;
        else
            children = Collections.synchronizedList(cList);

        // go through children and set parent to this node
        if (children != null) {
            for (int x = 0, cSize = children.size(); x < cSize; x++) {
                Spatial child = children.get(x);
                child.parent = this;
            }
        }
    }

    @Override
    public void setModelBound(BoundingVolume modelBound) {
        if(children != null) {
            for(int i = 0, max = children.size(); i < max; i++) {
                children.get(i).setModelBound(modelBound != null ? modelBound.clone(null) : null);
            }
        }
    }

    @Override
    public void updateModelBound() {
        if(children != null) {
            for(int i = 0, max = children.size(); i < max; i++) {
                children.get(i).updateModelBound();
            }
        }
    }

    /**
     * Returns flat list of Spatials implementing the specified class AND
     * with name matching the specified pattern.
     * </P> <P>
     * Note that we are <i>matching</i> the pattern, therefore the pattern
     * must match the entire pattern (i.e. it behaves as if it is sandwiched
     * between "^" and "$").
     * You can set regex modes, like case insensitivity, by using the (?X)
     * or (?X:Y) constructs.
     * </P> <P>
     * By design, it is always safe to code loops like:<CODE><PRE>
     *     for (Spatial spatial : node.descendantMatches(AClass.class, "regex"))
     * </PRE></CODE>
     * </P> <P>
     * "Descendants" does not include self, per the definition of the word.
     * To test for descendants AND self, you must do a
     * <code>node.matches(aClass, aRegex)</code> + 
     * <code>node.descendantMatches(aClass, aRegex)</code>.
     * <P>
     *
     * @param spatialSubclass Subclass which matching Spatials must implement.
     *                        Null causes all Spatials to qualify.
     * @param nameRegex  Regular expression to match Spatial name against.
     *                        Null causes all Names to qualify.
     * @return Non-null, but possibly 0-element, list of matching Spatials (also Instances extending Spatials).
     *
     * @see java.util.regex.Pattern
     * @see Spatial#matches(Class<? extends Spatial>, String)
     */
    @SuppressWarnings("unchecked")
    public <T extends Spatial>List<T> descendantMatches(
            Class<T> spatialSubclass, String nameRegex) {
        List<T> newList = new ArrayList<T>();
        if (getQuantity() < 1) return newList;
        for (Spatial child : getChildren()) {
            if (child.matches(spatialSubclass, nameRegex))
                newList.add((T)child);
            if (child instanceof Node)
                newList.addAll(((Node) child).descendantMatches(
                        spatialSubclass, nameRegex));
        }
        return newList;
    }

    /**
     * Convenience wrapper.
     *
     * @see #descendantMatches(Class<? extends Spatial>, String)
     */
    public <T extends Spatial>List<T> descendantMatches(
            Class<T> spatialSubclass) {
        return descendantMatches(spatialSubclass, null);
    }

    /**
     * Convenience wrapper.
     *
     * @see #descendantMatches(Class<? extends Spatial>, String)
     */
    public <T extends Spatial>List<T> descendantMatches(String nameRegex) {
        return descendantMatches(null, nameRegex);
    }
    
    /**
     *  Sets the collision-bits for this node and all children 
     *  
     *  @param collisionBits
     */
	public void setCollisionMask(int collisionBits) {
		this.setCollisionMask(collisionBits,true);
	}

	/**
	 * 
	 * Sets the collision-bits for this node. If excludeChildren is false all the children's collisionbit is also set
	 * 
	 * @param collisionBits
	 * @param includeChildren
	 */
	public void setCollisionMask(int collisionBits,boolean includeChildren) {
		super.setCollisionMask(collisionBits);
		if (includeChildren==true && getChildren()!=null)
		{
	        if (children!=null)
	        {
				for (Spatial child : getChildren()) {
		        	if (child instanceof Node)
		        	{
		        		((Node)child).setCollisionMask(collisionBits,includeChildren);
		        	}
		        	else
		        	{
		        		child.setCollisionMask(collisionBits);
		        	}
		        }
	        }
		}
	}    
}
