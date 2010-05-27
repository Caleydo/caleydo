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
import java.util.HashMap;
import java.util.Stack;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Matrix3f;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.RenderState.StateType;
import com.jme.system.DisplaySystem;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>Spatial</code> defines the base class for scene graph nodes. It
 * maintains a link to a parent, it's local transforms and the world's
 * transforms. All other nodes, such as <code>Node</code> and
 * <code>Geometry</code> are subclasses of <code>Spatial</code>.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Revision: 4892 $, $Data$
 */
public abstract class Spatial implements Serializable, Savable {

    /**
     * Describes how to combine textures from ancestor texturestates when an
     * updateRenderState is called on a Spatial.
     * 
     * @author Joshua Slack
     */
    public enum TextureCombineMode {
        /** When updating render states, turn off texturing for this spatial. */
        Off,

        /**
         * Combine textures starting from the root node and working towards the
         * given Spatial. Ignore disabled states.
         */
        CombineFirst,

        /**
         * Combine textures starting from the given Spatial and working towards
         * the root. Ignore disabled states. (Default)
         */
        CombineClosest,

        /**
         * Similar to CombineClosest, but if a disabled state is encountered, it
         * will stop combining at that point.
         */
        CombineClosestEnabled,

        /** Inherit mode from parent. */
        Inherit,

        /** Do not combine textures, just use the most recent texture state. */
        Replace;
    }

    /**
     * Describes how to combine lights from ancestor lightstates when an
     * updateRenderState is called on a Spatial.
     * 
     * @author Joshua Slack
     */
    public enum LightCombineMode {
        /** When updating render states, turn off lighting for this spatial. */
        Off,

        /**
         * Combine lights starting from the root node and working towards the
         * given Spatial. Ignore disabled states. Stop combining when lights ==
         * MAX_LIGHTS_ALLOWED
         */
        CombineFirst,

        /**
         * Combine lights starting from the given Spatial and working up towards
         * the root. Ignore disabled states. Stop combining when lights ==
         * MAX_LIGHTS_ALLOWED
         */
        CombineClosest,

        /**
         * Similar to CombineClosest, but if a disabled state is encountered, it
         * will stop combining at that point. Stop combining when lights ==
         * MAX_LIGHTS_ALLOWED
         */
        CombineClosestEnabled,

        /** Inherit mode from parent. */
        Inherit,

        /** Do not combine lights, just use the most recent light state. */
        Replace;
    }

    public enum CullHint {
        /** Do whatever our parent does. If no parent, we'll default to dynamic. */
        Inherit,
        /**
         * Do not draw if we are not at least partially within the view frustum
         * of the renderer's camera.
         */
        Dynamic,
        /** Always cull this from view. */
        Always,
        /**
         * Never cull this from view. Note that we will still get culled if our
         * parent is culled.
         */
        Never;
    }

    public enum NormalsMode {
        /**
         * Do whatever our parent does. If no parent, we'll default to
         * NormalizeIfScaled.
         */
        Inherit,
        /** Send through the normals currently set as-is. */
        UseProvided,
        /**
         * Tell the card to normalize any normals data we might give it for this
         * Spatial.
         */
        AlwaysNormalize,
        /**
         * If this Spatial is scaled other than 1,1,1 then tell the card to
         * normalize any normals data we might give it.
         */
        NormalizeIfScaled,
        /**
         * Do not send normal data to the card for this Spatial, even if we have
         * some.
         */
        Off;
    }

    public static final int LOCKED_NONE = 0;
    public static final int LOCKED_BOUNDS = 1;
    public static final int LOCKED_MESH_DATA = 2;
    public static final int LOCKED_TRANSFORMS = 4;
    public static final int LOCKED_SHADOWS = 8;
    public static final int LOCKED_BRANCH = 16;

    /**
     * A flag indicating how normals should be treated by the renderer.
     */
    protected NormalsMode normalsMode = NormalsMode.Inherit;

    /**
     * A flag indicating if scene culling should be done on this object by
     * inheritance, dynamically, never, or always.
     */
    protected CullHint cullHint = CullHint.Inherit;

    /** Spatial's bounding volume relative to the world. */
    protected BoundingVolume worldBound;

    /** The render states of this spatial. */
    protected RenderState[] renderStateList;

    protected int renderQueueMode = Renderer.QUEUE_INHERIT;

    /** Used to determine draw order for ortho mode rendering. */
    protected int zOrder = 0;

    /**
     * Used to indicate this spatial (and any below it in the case of Node) is
     * locked against certain changes.
     */
    protected int lockedMode = LOCKED_NONE;

    /**
     * Flag signaling how lights are combined for this node. By default set to
     * INHERIT.
     */
    protected LightCombineMode lightCombineMode = LightCombineMode.Inherit;

    /**
     * Flag signaling how textures are combined for this node. By default set to
     * INHERIT.
     */
    protected TextureCombineMode textureCombineMode = TextureCombineMode.Inherit;

    /** This spatial's name. */
    protected String name;

    // scale values
    protected Camera.FrustumIntersect frustrumIntersects = Camera.FrustumIntersect.Intersects;

    /**
     * Defines if this spatial will be used in intersection operations or not.
     * Default is true
     */
    protected int collisionBits = 1;

    public transient float queueDistance = Float.NEGATIVE_INFINITY;

    private static final long serialVersionUID = 2L;

    /** Spatial's rotation relative to its parent. */
    protected Quaternion localRotation;

    /** Spatial's world absolute rotation. */
    protected Quaternion worldRotation;

    /** Spatial's translation relative to its parent. */
    protected Vector3f localTranslation;

    /** Spatial's world absolute translation. */
    protected Vector3f worldTranslation;

    /** Spatial's scale relative to its parent. */
    protected Vector3f localScale;

    /** Spatial's world absolute scale. */
    protected Vector3f worldScale;

    /** Spatial's parent, or null if it has none. */
    protected transient Node parent;

    /** ArrayList of controllers for this spatial. */
    protected ArrayList<Controller> geometricalControllers;

    private static final Vector3f compVecA = new Vector3f();
    private static final Quaternion compQuat = new Quaternion();

    /**
     * Default Constructor.
     */
    public Spatial() {
        localRotation = new Quaternion();
        worldRotation = new Quaternion();
        localTranslation = new Vector3f();
        worldTranslation = new Vector3f();
        localScale = new Vector3f(1.0f, 1.0f, 1.0f);
        worldScale = new Vector3f(1.0f, 1.0f, 1.0f);
    }

    /**
     * Constructor instantiates a new <code>Spatial</code> object setting the
     * rotation, translation and scale value to defaults.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparison purposes.
     */
    public Spatial(String name) {
        this();
        this.name = name;
    }

    /**
     * Adds a Controller to this Spatial's list of controllers.
     * 
     * @param controller
     *            The Controller to add
     * @see com.jme.scene.Controller
     */
    public void addController(Controller controller) {
        if (geometricalControllers == null) {
            geometricalControllers = new ArrayList<Controller>(1);
        }
        geometricalControllers.add(controller);
    }

    /**
     * Removes a Controller from this Spatial's list of controllers, if it
     * exist.
     * 
     * @param controller
     *            The Controller to remove
     * @return True if the Controller was in the list to remove.
     * @see com.jme.scene.Controller
     */
    public boolean removeController(Controller controller) {
        if (geometricalControllers == null) {
            return false;
        }
        return geometricalControllers.remove(controller);
    }

    /**
     * Removes a Controller from this Spatial's list of controllers by index.
     * 
     * @param index
     *            The index of the controller to remove
     * @return The Controller removed or null if nothing was removed.
     * @see com.jme.scene.Controller
     */
    public Controller removeController(int index) {
        if (geometricalControllers == null) {
            return null;
        }
        return geometricalControllers.remove(index);
    }

    /**
     * Removes all Controllers from this Spatial's list of controllers.
     * 
     * @see com.jme.scene.Controller
     */
    public void clearControllers() {
        if (geometricalControllers != null) {
            geometricalControllers.clear();
        }
    }
    
    /**
     * Returns the controller in this list of controllers at index i.
     * 
     * @param i
     *            The index to get a controller from.
     * @return The controller at index i.
     * @see com.jme.scene.Controller
     */
    public Controller getController(int i) {
        if (geometricalControllers == null) {
            geometricalControllers = new ArrayList<Controller>(1);
        }
        return geometricalControllers.get(i);
    }
    
    /**
     * Returns the first controller hopefully the only
     * 	in this list of controllers with class c.
     * 
     * @param c
     *            The class type to get a controller of.
     * @return The controller with Class c.
     * @see com.jme.scene.Controller
     */
	@SuppressWarnings("unchecked")
	public <T extends Controller> T getController(Class<T> c) {
        if (geometricalControllers == null) {
            geometricalControllers = new ArrayList<Controller>(1);
        }
        for (Controller controller : geometricalControllers) {
        	if (controller.getClassTag() ==  c) {
                return (T)controller;       		
        	}
		}
        return null;

    }

    /**
     * Returns the ArrayList that contains this spatial's Controllers.
     * 
     * @return This spatial's geometricalControllers.
     */
    public ArrayList<Controller> getControllers() {
        if (geometricalControllers == null) {
            geometricalControllers = new ArrayList<Controller>(1);
        }
        return geometricalControllers;
    }
    
    /**
     * Returns the all controllers
     * 	in this list of controllers with class c.
     * 
     * @param c
     *            The class type get to controllers of.
     * @return The controllers with Class c.
     * @see com.jme.scene.Controller
     */
	@SuppressWarnings("unchecked")
	public <T extends Controller> ArrayList<T> getControllers(Class<T> c) {
        if (geometricalControllers == null) {
            geometricalControllers = new ArrayList<Controller>(1);
        }
        ArrayList<T> retControllers = new ArrayList<T>();
        for (Controller controller : geometricalControllers) {
        	if (controller.getClassTag() ==  c) {
                retControllers.add((T)controller);     		
        	}
		}
        return retControllers;
    }

    /**
     * @return the number of controllers set on this Spatial.
     */
    public int getControllerCount() {
        if (geometricalControllers == null) {
            return 0;
        }
        return geometricalControllers.size();
    }

    /**
     * <code>onDraw</code> checks the spatial with the camera to see if it
     * should be culled, if not, the node's draw method is called.
     * <p>
     * This method is called by the renderer. Usually it should not be called
     * directly.
     * 
     * @param r
     *            the renderer used for display.
     */
    public void onDraw(Renderer r) {
        CullHint cm = getCullHint();
        if (cm == Spatial.CullHint.Always) {
            setLastFrustumIntersection(Camera.FrustumIntersect.Outside);
            return;
        } else if (cm == Spatial.CullHint.Never) {
            setLastFrustumIntersection(Camera.FrustumIntersect.Intersects);
            draw(r);
            return;
        }

        Camera camera = r.getCamera();
        int state = camera.getPlaneState();

        // check to see if we can cull this node
        frustrumIntersects = (parent != null ? parent.frustrumIntersects
                : Camera.FrustumIntersect.Intersects);

        if (cm == Spatial.CullHint.Dynamic
                && frustrumIntersects == Camera.FrustumIntersect.Intersects) {
            frustrumIntersects = camera.contains(worldBound);
        }

        if (frustrumIntersects != Camera.FrustumIntersect.Outside) {
            draw(r);
        }
        camera.setPlaneState(state);
    }

    /**
     * <code>getWorldRotation</code> retrieves the absolute rotation of the
     * Spatial.
     * 
     * @return the Spatial's world rotation matrix.
     */
    public Quaternion getWorldRotation() {
        return worldRotation;
    }

    /**
     * <code>getWorldTranslation</code> retrieves the absolute translation of
     * the spatial.
     * 
     * @return the world's translation vector.
     */
    public Vector3f getWorldTranslation() {
        return worldTranslation;
    }

    /**
     * <code>getWorldScale</code> retrieves the absolute scale factor of the
     * spatial.
     * 
     * @return the world's scale factor.
     */
    public Vector3f getWorldScale() {
        return worldScale;
    }

    /**
     * <code>rotateUpTo</code> is a util function that alters the
     * localrotation to point the Y axis in the direction given by newUp.
     * 
     * @param newUp
     *            the up vector to use - assumed to be a unit vector.
     */
    public void rotateUpTo(Vector3f newUp) {
        // First figure out the current up vector.
        Vector3f upY = compVecA.set(Vector3f.UNIT_Y);
        localRotation.multLocal(upY);

        // get angle between vectors
        float angle = upY.angleBetween(newUp);

        // figure out rotation axis by taking cross product
        Vector3f rotAxis = upY.crossLocal(newUp).normalizeLocal();

        // Build a rotation quat and apply current local rotation.
        Quaternion q = compQuat;
        q.fromAngleNormalAxis(angle, rotAxis);
        q.mult(localRotation, localRotation);
    }

    /**
     * <code>lookAt</code> is a convenience method for auto-setting the local
     * rotation based on a position and an up vector. It computes the rotation
     * to transform the z-axis to point onto 'position' and the y-axis to 'up'.
     * Unlike {@link Quaternion#lookAt} this method takes a world position to
     * look at not a relative direction.
     * 
     * @param position
     *            where to look at in terms of world coordinates
     * @param upVector
     *            a vector indicating the (local) up direction. (typically {0,
     *            1, 0} in jME.)
     */
    public void lookAt(Vector3f position, Vector3f upVector)
    {
    	lookAt(position,upVector,false);
    }
    
	// with setting the 3rd parameter (takeParentInAccount) to true the localrotation 
	// is set so that the spatials worldrotation (and not the localroation) is looking to the 
	// given position
    /**
     * <code>lookAt</code> is a convenience method for auto-setting the local
     * rotation based on a position and an up vector. It computes the rotation
     * to transform the z-axis to point onto 'position' and the y-axis to 'up'.
     * Unlike {@link Quaternion#lookAt} this method takes a world position to
     * look at not a relative direction.
     * 
     * @param position
     *            where to look at in terms of world coordinates
     * @param upVector
     *            a vector indicating the (local) up direction. (typically {0,
     *            1, 0} in jME.)
     * @param takeParentInAccount
     * 		      if true : the localrotation is fixed so that this spatial's worldrotation
     *                      is pointing to the position
     *            if false: the localrotation is pointing to the position (worldrotation might be
     *                      not pointing to it
     */
    public void lookAt(Vector3f position, Vector3f upVector, boolean takeParentInAccount) {
        compVecA.set(position).subtractLocal(getWorldTranslation());
        getLocalRotation().lookAt(compVecA, upVector);
		if (takeParentInAccount && parent!=null)
			getLocalRotation().multLocal(parent.getWorldRotation().inverse());        
    }

    /**
     * <code>updateGeometricState</code> updates all the geometry information
     * for the node.
     * 
     * @param time
     *            the frame time.
     * @param initiator
     *            true if this node started the update process.
     */
    public void updateGeometricState(float time, boolean initiator) {
        if ((lockedMode & Spatial.LOCKED_BRANCH) != 0)
            return;
        updateWorldData(time);
        if ((lockedMode & Spatial.LOCKED_BOUNDS) == 0) {
            updateWorldBound();
            if (initiator) {
                propagateBoundToRoot();
            }
        }
    }

    /**
     * <code>updateWorldData</code> updates the world transforms from the
     * parent down to the leaf.
     * 
     * @param time
     *            the frame time.
     */
    public void updateWorldData(float time) {
        // update spatial state via controllers
        if (geometricalControllers != null) {
            for (int i = 0, gSize = geometricalControllers.size(); i < gSize; i++) {
                try {
                    Controller controller = geometricalControllers.get(i);
                    if (controller != null) {
                        if (controller.isActive()) {
                            controller.update(time);
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    // a controller was removed in Controller.update (note: this
                    // may skip one controller)
                    break;
                }
            }
        }

        updateWorldVectors();
    }

    /**
     * If not locked, updates worldscale, worldrotation and worldtranslation
     */
    public void updateWorldVectors() {
        updateWorldVectors(false);
    }

    /**
     * If not locked, updates worldscale, worldrotation and worldtranslation
     * 
     * @param recurse
     *            usually false when updating the tree. Set to true when you
     *            just want to update the world transforms for a branch without
     *            updating geometric state.
     */
    public void updateWorldVectors(boolean recurse) {
        if (((lockedMode & Spatial.LOCKED_TRANSFORMS) == 0)) {
            updateWorldScale();
            updateWorldRotation();
            updateWorldTranslation();
        }
    }

    protected void updateWorldTranslation() {
        if (parent != null) {
            parent.localToWorld(localTranslation, worldTranslation);
        } else {
            worldTranslation.set(localTranslation);
        }
    }

    protected void updateWorldRotation() {
        if (parent != null) {
            parent.getWorldRotation().mult(localRotation, worldRotation);
        } else {
            worldRotation.set(localRotation);
        }
    }

    protected void updateWorldScale() {
        if (parent != null) {
            worldScale.set(parent.getWorldScale()).multLocal(localScale);
        } else {
            worldScale.set(localScale);
        }
    }

    /**
     * Convert a vector (in) from this spatials local coordinate space to world
     * coordinate space.
     * 
     * @param in
     *            vector to read from
     * @param store
     *            where to write the result (null to create a new vector, may be
     *            same as in)
     * @return the result (store)
     */
    public Vector3f localToWorld(final Vector3f in, Vector3f store) {
        if (store == null)
            store = new Vector3f();
        // multiply with scale first, then rotate, finally translate (cf.
        // Eberly)
        return getWorldRotation().mult(
                store.set(in).multLocal(getWorldScale()), store).addLocal(
                getWorldTranslation());
    }

    /**
     * Convert a vector (in) from world coordinate space to this spatials local
     * coordinate space.
     * 
     * @param in
     *            vector to read from
     * @param store
     *            where to write the result
     * @return the result (store)
     */
    public Vector3f worldToLocal(final Vector3f in, final Vector3f store) {
        in.subtract(getWorldTranslation(), store).divideLocal(getWorldScale());
        getWorldRotation().inverse().mult(store, store);
        return store;
    }

    /**
     * <code>getParent</code> retrieve's this node's parent. If the parent is
     * null this is the root node.
     * 
     * @return the parent of this node.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Called by {@link Node#attachChild(Spatial)} and
     * {@link Node#detachChild(Spatial)} - don't call directly.
     * <code>setParent</code> sets the parent of this node.
     * 
     * @param parent
     *            the parent of this node.
     */
    protected void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * <code>removeFromParent</code> removes this Spatial from it's parent.
     * 
     * @return true if it has a parent and performed the remove.
     */
    public boolean removeFromParent() {
        if (parent != null) {
            parent.detachChild(this);
            return true;
        }
        return false;
    }

    /**
     * determines if the provided Node is the parent, or parent's parent, etc. of this Spatial.
     * 
     * @param ancestor
     *            the ancestor object to look for.
     * @return true if the ancestor is found, false otherwise.
     */
    public boolean hasAncestor(Node ancestor) {
        if (parent == null) {
            return false;
        } else if (parent.equals(ancestor)) {
            return true;
        } else {
            return parent.hasAncestor(ancestor);
        }
    }
    
    /**
     * <code>getLocalRotation</code> retrieves the local rotation of this
     * node.
     * 
     * @return the local rotation of this node.
     */
    public Quaternion getLocalRotation() {
        return localRotation;
    }

    /**
     * <code>setLocalRotation</code> sets the local rotation of this node.
     * 
     * @param rotation
     *            the new local rotation.
     */
    public void setLocalRotation(Matrix3f rotation) {
        if (localRotation == null)
            localRotation = new Quaternion();
        localRotation.fromRotationMatrix(rotation);
        this.worldRotation.set(this.localRotation);
    }

    /**
     * <code>setLocalRotation</code> sets the local rotation of this node,
     * using a quaternion to build the matrix.
     * 
     * @param quaternion
     *            the quaternion that defines the matrix.
     */
    public void setLocalRotation(Quaternion quaternion) {
        localRotation = quaternion;
        this.worldRotation.set(this.localRotation);
    }

    /**
     * <code>getLocalScale</code> retrieves the local scale of this node.
     * 
     * @return the local scale of this node.
     */
    public Vector3f getLocalScale() {
        return localScale;
    }

    /**
     * <code>setLocalScale</code> sets the local scale of this node.
     * 
     * @param localScale
     *            the new local scale, applied to x, y and z
     */
    public void setLocalScale(float localScale) {
        this.localScale.x = localScale;
        this.localScale.y = localScale;
        this.localScale.z = localScale;
        this.worldScale.set(this.localScale);
    }

    /**
     * <code>setLocalScale</code> sets the local scale of this node.
     * 
     * @param localScale
     *            the new local scale.
     */
    public void setLocalScale(Vector3f localScale) {
        this.localScale = localScale;
        this.worldScale.set(this.localScale);
    }

    /**
     * <code>getLocalTranslation</code> retrieves the local translation of
     * this node.
     * 
     * @return the local translation of this node.
     */
    public Vector3f getLocalTranslation() {
        return localTranslation;
    }

    /**
     * <code>setLocalTranslation</code> sets the local translation of this
     * node.
     * 
     * @param localTranslation
     *            the local translation of this node.
     */
    public void setLocalTranslation(Vector3f localTranslation) {
        this.localTranslation = localTranslation;
        this.worldTranslation.set(this.localTranslation);
    }

    public void setLocalTranslation(float x, float y, float z) {
        localTranslation.set(x, y, z);
        worldTranslation.set(localTranslation);
    }

    /**
     * Sets the zOrder of this Spatial and, if setOnChildren is true, all
     * children as well. This value is used in conjunction with the RenderQueue
     * and QUEUE_ORTHO for determining draw order.
     * 
     * @param zOrder
     *            the new zOrder.
     * @param setOnChildren
     *            if true, children will also have their zOrder set to the given
     *            value.
     */
    public void setZOrder(int zOrder, boolean setOnChildren) {
        setZOrder(zOrder);
        if (setOnChildren) {
            if (this instanceof Node) {
                Node n = (Node) this;
                if (n.getChildren() != null) {
                    for (Spatial child : n.getChildren()) {
                        child.setZOrder(zOrder, true);
                    }
                }
            }
        }
    }

    /**
     * @see #setCullHint(CullHint)
     * @return the cull mode of this spatial, or if set to INHERIT, the cullmode
     *         of it's parent.
     */
    public CullHint getCullHint() {
        if (cullHint != CullHint.Inherit)
            return cullHint;
        else if (parent != null)
            return parent.getCullHint();
        else
            return CullHint.Dynamic;
    }

    /**
     * Returns this spatial's texture combine mode. If the mode is set to
     * inherit, then the spatial gets its combine mode from its parent.
     * 
     * @return The spatial's texture current combine mode.
     */
    public TextureCombineMode getTextureCombineMode() {
        if (textureCombineMode != TextureCombineMode.Inherit)
            return textureCombineMode;
        else if (parent != null)
            return parent.getTextureCombineMode();
        else
            return TextureCombineMode.CombineClosest;
    }

    /**
     * Returns this spatial's light combine mode. If the mode is set to inherit,
     * then the spatial gets its combine mode from its parent.
     * 
     * @return The spatial's light current combine mode.
     */
    public LightCombineMode getLightCombineMode() {
        if (lightCombineMode != LightCombineMode.Inherit)
            return lightCombineMode;
        else if (parent != null)
            return parent.getLightCombineMode();
        else
            return LightCombineMode.CombineFirst;
    }

    /**
     * Returns this spatial's renderqueue mode. If the mode is set to inherit,
     * then the spatial gets its renderqueue mode from its parent.
     * 
     * @return The spatial's current renderqueue mode.
     */
    public int getRenderQueueMode() {
        if (renderQueueMode != Renderer.QUEUE_INHERIT)
            return renderQueueMode;
        else if (parent != null)
            return parent.getRenderQueueMode();
        else
            return Renderer.QUEUE_SKIP;
    }

    /**
     * Returns this spatial's normals mode. If the mode is set to inherit, then
     * the spatial gets its normals mode from its parent.
     * 
     * @return The spatial's current normals mode.
     */
    public NormalsMode getNormalsMode() {
        if (normalsMode != NormalsMode.Inherit)
            return normalsMode;
        else if (parent != null)
            return parent.getNormalsMode();
        else
            return NormalsMode.NormalizeIfScaled;
    }

    /**
     * Called during updateRenderState(Stack[]), this function goes up the scene
     * graph tree until the parent is null and pushes RenderStates onto the
     * states Stack array.
     * 
     * @param states
     *            The Stack[] to push states onto.
     */
    @SuppressWarnings("unchecked")
    public void propagateStatesFromRoot(Stack[] states) {
        // traverse to root to allow downward state propagation
        if (parent != null)
            parent.propagateStatesFromRoot(states);

        // push states onto current render state stack
        for (RenderState.StateType type : RenderState.StateType.values()) {
            if (getRenderState(type) != null)
                states[type.ordinal()].push(getRenderState(type));
        }
    }

    /**
     * <code>propagateBoundToRoot</code> passes the new world bound up the
     * tree to the root.
     */
    public void propagateBoundToRoot() {
        if (parent != null) {
            parent.updateWorldBound();
            parent.propagateBoundToRoot();
        }
    }

    /**
     * Convenience wrapper for
     * calculateCollisions(Spatial, CollisionResults, int) using default
     * collidability (first bit of the collidable bit mask).
     *
     * @see #calculateCollisions(Spatial, CollisionResults, int)
     */
    final public void calculateCollisions(
            Spatial scene, CollisionResults results) {
        calculateCollisions(scene, results, 1);
    }

    /**
     * <code>calculateCollisions</code> calls findCollisions to populate the
     * CollisionResults object then processes the collision results.
     * 
     * @param scene the scene to test against.
     * @param results the results object.
     * @param requiredOnBits considered a collision only if these bits are 'on'
     *    in both 'this' and the 'scene' spatial.
     */
    final public void calculateCollisions(
            Spatial scene, CollisionResults results, int requiredOnBits) {
        findCollisions(scene, results, requiredOnBits);
        results.processCollisions();
    }

    /**
     * <code>updateBound</code> recalculates the bounding object for this
     * Spatial.
     */
    public abstract void updateModelBound();

    /**
     * <code>setModelBound</code> sets the bounding object for this Spatial.
     * 
     * @param modelBound
     *            the bounding object for this spatial.
     */
    public abstract void setModelBound(BoundingVolume modelBound);

    /**
     * Convenience wrapper for
     * findCollisions(Spatial, CollisionResults, int) using default
     * collidability (first bit of the collidable bit mask).
     *
     * @see #findCollisions(Spatial, CollisionResults, int)
     */
    final public void findCollisions(Spatial scene, CollisionResults results) {
        findCollisions(scene, results, 1);
    }

    /**
     * checks this spatial against a second spatial, any collisions are stored
     * in the results object.
     * 
     * @param scene the scene to test against.
     * @param results the results of the collisions.
     * @param requiredOnBits considered a collision only if these bits are 'on'
     *    in both 'this' and the 'scene' spatial.
     */
    public abstract void findCollisions(
            Spatial scene, CollisionResults results, int requiredOnBits);

    /**
     * Convenience wrapper for
     * hasCollision(Spatial, CollisionResults, int) using default
     * collidability (first bit of the collidable bit mask).
     *
     * @see #hasCollisionsSpatial, CollisionResults, boolean)
     */
    final public boolean hasCollision(Spatial scene, boolean checkTriangles) {
        return hasCollision(scene, checkTriangles, 1);
    }

    /**
     * Checks this spatial against a second spatial for collisions.
     * 
     * @param scene the scene to test against.
     * @param checkTriangles check for collisions on triangle accuracy level
     * @param requiredOnBits considered a collision only if these bits are 'on'
     *    in both 'this' and the 'scene' spatial.
     * @return true if any collision were found
     */
    public abstract boolean hasCollision(
            Spatial scene, boolean checkTriangles, int requiredOnBits);

    /**
     * Convenience wrapper for
     * calculatePick(Ray, PickResults, int) using default
     * collidability (first bit of the collidable bit mask).
     *
     * @see #calculatePick(Ray, PickResults, int)
     */
    final public void calculatePick(Ray ray, PickResults results) {
        calculatePick(ray, results, 1);
    }

    /**
     * @param requiredOnBits considered a collision only if these bits are 'on'
     *    in 'this' spatial.
     */
    final public void calculatePick(
            Ray ray, PickResults results, int requiredOnBits) {
        findPick(ray, results, requiredOnBits);
        results.processPick();
    }

    /**
     * Convenience wrapper for
     * findPick(Ray, PickResults, int) using default
     * collidability (first bit of the collidable bit mask).
     *
     * @see #findPick(Ray, PickResults, int)
     */
    final public void findPick(Ray toTest, PickResults results) {
        findPick(toTest, results, 1);
    }

    /**
     * Tests a ray against this spatial, and stores the results in the result
     * object.
     * 
     * @param toTest ray to test picking against
     * @param results the results of the picking
     * @param requiredOnBits considered a collision only if these bits are 'on'
     *    in 'this' spatial.
     */
    public abstract void findPick(
            Ray toTest, PickResults results, int requiredOnBits);

    /**
     * Stores user define data for this Spatial.
     * 
     * @param key
     *            the key component to retrieve the data from the hash map.
     * @param data
     *            the data to store.
     */
    public void setUserData(String key, Savable data) {
        UserDataManager.getInstance().setUserData(this, key, data);
    }

    /**
     * Retrieves user data from the hashmap defined by the provided key.
     * 
     * @param key
     *            the key of the data to obtain.
     * @return the data referenced by the key. If the key is invalid, null is
     *         returned.
     */
    public Savable getUserData(String key) {
        return UserDataManager.getInstance().getUserData(this, key);
    }

    /**
     * Removes user data from the hashmap defined by the provided key.
     * 
     * @param key
     *            the key of the data to remove.
     * @return the data that has been removed, null if no data existed.
     */
    public Savable removeUserData(String key) {
        return UserDataManager.getInstance().removeUserData(this, key);
    }

    public abstract int getVertexCount();

    public abstract int getTriangleCount();

    public void write(JMEExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(name, "name", null);
        capsule.write(collisionBits, "collisionBits", 1);
        capsule.write(cullHint, "cullMode", CullHint.Inherit);

        capsule.write(renderQueueMode, "renderQueueMode",
                Renderer.QUEUE_INHERIT);
        capsule.write(zOrder, "zOrder", 0);
        capsule.write(lightCombineMode, "lightCombineMode", LightCombineMode.Inherit);
        capsule.write(textureCombineMode, "textureCombineMode",
                TextureCombineMode.Inherit);
        capsule.write(normalsMode, "normalsMode", NormalsMode.Inherit);
        capsule.write(renderStateList, "renderStateList", null);
        capsule.write(localRotation, "localRotation", new Quaternion());
        capsule.write(localTranslation, "localTranslation", Vector3f.ZERO);
        capsule.write(localScale, "localScale", Vector3f.UNIT_XYZ);

        capsule.writeStringSavableMap(UserDataManager.getInstance().getAllData(
                this), "userData", null);

        capsule.writeSavableArrayList(geometricalControllers,
                "geometricalControllers", null);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        name = capsule.readString("name", null);
        /*
         * The following makes it impossible to restore a Savable with no
         * (null) name.  This is a bad practice and is only in place for
         * backwards compatibility.  For jME 3.x, or any other clean break
         * for compatibility, this should be dropped.
         */
        if (name == null) name = "";
        boolean collisionBoolean = capsule.readBoolean("isCollidable", true);
        collisionBits = capsule.readInt("collisionBits", 1);

        // Handle legacy model files that have stored 'isCollidable' instead of
        // 'collisionBits'.
        if (!collisionBoolean && collisionBits == 1) collisionBits = 0;
        /* Our exporter routines do not allow us to always detect whether
         * 'isCollidable' was actually persisted.
         * However, if 'isCollidable' has a non-default val and 'collisionBits'
         * has default val, we know that legacy value was stored.  */

        cullHint = capsule.readEnum("cullMode", CullHint.class,
                CullHint.Inherit);

        renderQueueMode = capsule.readInt("renderQueueMode",
                Renderer.QUEUE_INHERIT);
        zOrder = capsule.readInt("zOrder", 0);
        lightCombineMode = capsule.readEnum("lightCombineMode", LightCombineMode.class,
                LightCombineMode.Inherit);
        textureCombineMode = capsule.readEnum("textureCombineMode", TextureCombineMode.class,
                TextureCombineMode.Inherit);
        normalsMode = capsule.readEnum("normalsMode", NormalsMode.class,
                NormalsMode.Inherit);

        Savable[] savs = capsule.readSavableArray("renderStateList", null);
        if (savs == null)
            renderStateList = null;
        else {
            renderStateList = new RenderState[StateType.values().length];
            for (int x = 0; x < savs.length; x++) {
                renderStateList[x] = (RenderState) savs[x];
            }
        }

        localRotation = (Quaternion) capsule.readSavable("localRotation",
                new Quaternion());
        localTranslation = (Vector3f) capsule.readSavable("localTranslation",
                Vector3f.ZERO.clone());
        localScale = (Vector3f) capsule.readSavable("localScale",
                Vector3f.UNIT_XYZ.clone());

        HashMap<String, Savable> map = (HashMap<String, Savable>) capsule
                .readStringSavableMap("userData", null);
        if (map != null) {
            UserDataManager.getInstance().setAllData(this, map);
        }

        geometricalControllers = capsule.readSavableArrayList(
                "geometricalControllers", null);

        worldRotation = new Quaternion();
        worldTranslation = new Vector3f();
        worldScale = new Vector3f(1.0f, 1.0f, 1.0f);
    }

    /**
     * Sets the name of this spatial.
     * 
     * @param name
     *            The spatial's new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this spatial.
     * 
     * @return This spatial's name.
     */
    public String getName() {
        return name;
    }

    public void setCollisionMask(int collisionBits) {
        this.collisionBits = collisionBits;
    }

    public int getCollisionMask() {
        return collisionBits;
    }

    /**
     * Sets if this Spatial is to be used in intersection (collision and
     * picking) calculations. By default this is true.
     *
     * Turns on or off the first (least significant) bit in the collision bit
     * set.
     * 
     * @param isCollidable
     *            true if this Spatial is to be used in intersection
     *            calculations, false otherwise.
     */
    public void setIsCollidable(boolean isCollidable) {
        collisionBits = isCollidable ? (collisionBits | 1)
                                     : (collisionBits & (~1));
    }

    /**
     * Defines if this Spatial is to be used in intersection (collision and
     * picking) calculations. By default this is true.
     * 
     * @return true if this Spatial is to be used in intersection calculations,
     *         false otherwise.
     */
    public boolean isCollidable() {
        return isCollidable(1);
    }

    /**
     * @returns true if this Spatial matches all set bits in the specified value
     */
    public boolean isCollidable(int requiredOnBits) {
        return ((requiredOnBits & collisionBits) ^ requiredOnBits) == 0;
    }

    /**
     * <code>getWorldBound</code> retrieves the world bound at this node
     * level.
     * 
     * @return the world bound at this level.
     */
    public BoundingVolume getWorldBound() {
        return worldBound;
    }

    /**
     * <code>draw</code> abstract method that handles drawing data to the
     * renderer if it is geometry and passing the call to it's children if it is
     * a node.
     * 
     * @param r
     *            the renderer used for display.
     */
    public abstract void draw(Renderer r);

    /**
     * <code>setCullHint</code> sets how scene culling should work on this
     * spatial during drawing. CullHint.Dynamic: Determine via the defined
     * Camera planes whether or not this Spatial should be culled.
     * CullHint.Always: Always throw away this object and any children during
     * draw commands. CullHint.Never: Never throw away this object (always draw
     * it) CullHint.Inherit: Look for a non-inherit parent and use its cull
     * mode. NOTE: You must set this AFTER attaching to a parent or it will be
     * reset with the parent's cullMode value.
     * 
     * @param hint
     *            one of CullHint.Dynamic, CullHint.Always, CullHint.Inherit or
     *            CullHint.Never
     */
    public void setCullHint(CullHint hint) {
        cullHint = hint;
    }

    /**
     * @return the cullmode set on this Spatial
     */
    public CullHint getLocalCullHint() {
        return cullHint;
    }

    /**
     * Calling this method tells the scenegraph that it is not necessary to
     * update bounds from this point in the scenegraph on down to the leaves.
     * This is useful for performance gains where you have scene items that do
     * not move (at all) or change shape and thus do not need constant
     * re-calculation of boundaries. When you call lock, the bounds are first
     * updated to ensure current bounds are accurate.
     * 
     * @see #unlockBounds()
     */
    public void lockBounds() {
        updateGeometricState(0, true);
        lockedMode |= LOCKED_BOUNDS;
    }

    /**
     * Calling this method tells the scenegraph that it is not necessary to
     * update Shadow volumes that may be associated with this Spatial. This is
     * useful for skipping various checks for spatial transformation when
     * deciding whether or not to recalc a shadow volume for a Spatial.
     * 
     * @see #unlockShadows()
     */
    public void lockShadows() {
        lockedMode |= LOCKED_SHADOWS;
    }

    /**
     * Calling this method tells the scenegraph that it is not necessary to
     * traverse this Spatial or any below it during the update phase. This
     * should be called *after* any other lock call to ensure they are able to
     * update any bounds or vectors they might need to update.
     * 
     * @see #unlockBranch()
     */
    public void lockBranch() {
        lockedMode |= LOCKED_BRANCH;
    }

    /**
     * Flags this spatial and those below it in the scenegraph to not
     * recalculate world transforms such as translation, rotation and scale on
     * every update. This is useful for efficiency when you have scene items
     * that stay in one place all the time as it avoids needless recalculation
     * of transforms.
     * 
     * @see #unlockTransforms()
     */
    public void lockTransforms() {
        updateWorldVectors();
        lockedMode |= LOCKED_TRANSFORMS;
    }

    /**
     * Flags this spatial and those below it that any meshes in the specified
     * scenegraph location or lower will not have changes in vertex, texcoord,
     * normal or color data. This allows optimizations by the engine such as
     * creating display lists from the data. Calling this method does not
     * provide a guarantee that data changes will not be allowed or will/won't
     * show up in the scene. It is merely a hint to the engine.
     * 
     * @param r
     *            A renderer to lock against.
     * @see #unlockMeshes(Renderer)
     */
    public void lockMeshes(Renderer r) {
        updateRenderState();
        lockedMode |= LOCKED_MESH_DATA;
    }

    /**
     * Flags this spatial and those below it that any meshes in the specified
     * scenegraph location or lower will not have changes in vertex, texcoord,
     * normal or color data. This allows optimizations by the engine such as
     * creating display lists from the data. Calling this method does not
     * provide a guarantee that data changes will not be allowed or will/won't
     * show up in the scene. It is merely a hint to the engine. Calls
     * lockMeshes(Renderer) with the current display system's renderer.
     * 
     * @see #lockMeshes(Renderer)
     */
    public void lockMeshes() {
        lockMeshes(DisplaySystem.getDisplaySystem().getRenderer());
    }

    /**
     * Convenience function for locking all aspects of a Spatial.
     * 
     * @see #lockBounds()
     * @see #lockTransforms()
     * @see #lockMeshes(Renderer)
     * @see #lockShadows()
     */
    public void lock(Renderer r) {
        lockBounds();
        lockTransforms();
        lockMeshes(r);
        lockShadows();
        lockBranch();
    }

    /**
     * Convenience function for locking all aspects of a Spatial. It calls:
     * <code>lock(DisplaySystem.getDisplaySystem().getRenderer());</code>
     * 
     * @see #lockBounds()
     * @see #lockTransforms()
     * @see #lockMeshes()
     * @see #lockShadows()
     */
    public void lock() {
        lock(DisplaySystem.getDisplaySystem().getRenderer());
    }

    /**
     * Flags this spatial and those below it to allow for bounds updating (the
     * default).
     * 
     * @see #lockBounds()
     */
    public void unlockBounds() {
        lockedMode &= ~LOCKED_BOUNDS;
    }

    /**
     * Flags this spatial and those below it to allow for shadow volume updates
     * (the default).
     * 
     * @see #lockShadows()
     */
    public void unlockShadows() {
        lockedMode &= ~LOCKED_SHADOWS;
    }

    /**
     * Flags this Spatial and any below it as being traversable during the
     * update phase.
     * 
     * @see #lockBranch()
     */
    public void unlockBranch() {
        lockedMode &= ~LOCKED_BRANCH;
    }

    /**
     * Flags this spatial and those below it to allow for transform updating
     * (the default).
     * 
     * @see #lockTransforms()
     */
    public void unlockTransforms() {
        lockedMode &= ~LOCKED_TRANSFORMS;
    }

    /**
     * Flags this spatial and those below it to allow for mesh updating (the
     * default). Generally this means that any display lists setup will be
     * erased and released. Calls unlockMeshes(Renderer) with the current
     * display system's renderer.
     * 
     * @see #unlockMeshes(Renderer)
     */
    public void unlockMeshes() {
        unlockMeshes(DisplaySystem.getDisplaySystem().getRenderer());
    }

    /**
     * Flags this spatial and those below it to allow for mesh updating (the
     * default). Generally this means that any display lists setup will be
     * erased and released.
     * 
     * @param r
     *            The renderer used to lock against.
     * @see #lockMeshes(Renderer)
     */
    public void unlockMeshes(Renderer r) {
        lockedMode &= ~LOCKED_MESH_DATA;
    }

    /**
     * Convenience function for unlocking all aspects of a Spatial.
     * 
     * @see #unlockBounds()
     * @see #unlockTransforms()
     * @see #unlockMeshes(Renderer)
     * @see #unlockShadows()
     * @see #unlockBranch()
     */
    public void unlock(Renderer r) {
        unlockBounds();
        unlockBranch();
        unlockTransforms();
        unlockMeshes(r);
        unlockShadows();
        unlockBranch();
    }

    /**
     * Convenience function for unlocking all aspects of a Spatial. For
     * unlockMeshes it calls:
     * <code>unlockMeshes(DisplaySystem.getDisplaySystem().getRenderer());</code>
     * 
     * @see #unlockBounds()
     * @see #unlockTransforms()
     * @see #unlockMeshes()
     * @see #unlockShadows()
     * @see #unlockBranch()
     */
    public void unlock() {
        unlockBounds();
        unlockBranch();
        unlockTransforms();
        unlockMeshes();
        unlockShadows();
        unlockBranch();
    }

    /**
     * @return a bitwise combination of the current locks established on this
     *         Spatial.
     */
    public int getLocks() {
        return lockedMode;
    }

    /**
     * Note: Uses the currently set Renderer to generate a display list if
     * LOCKED_MESH_DATA is set.
     * 
     * @param locks
     *            a bitwise combination of the locks to establish on this
     *            Spatial.
     */
    public void setLocks(int lockedMode) {
        if ((lockedMode & Spatial.LOCKED_BOUNDS) != 0) {
            lockBounds();
        } else {
            unlockBounds();
        }
        if ((lockedMode & Spatial.LOCKED_BRANCH) != 0) {
            lockBranch();
        } else {
            unlockBranch();
        }
        if ((lockedMode & Spatial.LOCKED_MESH_DATA) != 0) {
            lockMeshes();
        } else {
            unlockMeshes();
        }
        if ((lockedMode & Spatial.LOCKED_SHADOWS) != 0) {
            lockShadows();
        } else {
            unlockShadows();
        }
        if ((lockedMode & Spatial.LOCKED_TRANSFORMS) != 0) {
            lockTransforms();
        } else {
            unlockTransforms();
        }
    }

    /**
     * @param locks
     *            a bitwise combination of the locks to establish on this
     *            Spatial.
     * @param r
     *            the renderer to create display lists with if LOCKED_MESH_DATA
     *            is set.
     */
    public void setLocks(int locks, Renderer r) {
        if ((lockedMode & Spatial.LOCKED_BOUNDS) != 0)
            lockBounds();
        if ((lockedMode & Spatial.LOCKED_MESH_DATA) != 0)
            lockMeshes(r);
        if ((lockedMode & Spatial.LOCKED_SHADOWS) != 0)
            lockShadows();
        if ((lockedMode & Spatial.LOCKED_TRANSFORMS) != 0)
            lockTransforms();
    }

    /**
     * <code>updateWorldBound</code> updates the bounding volume of the world.
     * Abstract, geometry transforms the bound while node merges the children's
     * bound. In most cases, users will want to call updateModelBound() and let
     * this function be called automatically during updateGeometricState().
     */
    public abstract void updateWorldBound();

    /**
     * Updates the render state values of this Spatial and and children it has.
     * Should be called whenever render states change.
     */
    public void updateRenderState() {
        updateRenderState(null);
    }

    /**
     * Called internally. Updates the render states of this Spatial. The stack
     * contains parent render states.
     * 
     * @param parentStates
     *            The list of parent renderstates.
     */
    @SuppressWarnings("unchecked")
    protected void updateRenderState(Stack[] parentStates) {
        boolean initiator = (parentStates == null);

        // first we need to get all the states from parent to us.
        if (initiator) {
            // grab all states from root to here.
            parentStates = new Stack[RenderState.StateType.values().length];
            for (int x = 0; x < parentStates.length; x++)
                parentStates[x] = new Stack<RenderState>();
            propagateStatesFromRoot(parentStates);
        } else {
            for (RenderState.StateType type : RenderState.StateType.values()) {
                if (getRenderState(type) != null)
                    parentStates[type.ordinal()].push(getRenderState(type));
            }
        }

        applyRenderState(parentStates);

        // restore previous if we are not the initiator
        if (!initiator) {
            for (RenderState.StateType type : RenderState.StateType.values()) {
                if (getRenderState(type) != null)
                	parentStates[type.ordinal()].pop();
            }
        }

    }

    /**
     * Called during updateRenderState(Stack[]), this function determines how
     * the render states are actually applied to the spatial and any children it
     * may have. By default, this function does nothing.
     * 
     * @param states
     *            An array of stacks for each state.
     */
    protected void applyRenderState(Stack<? extends RenderState>[] states) {
    }

    public void sortLights() {        
    }
    
    /**
     * <code>setRenderState</code> sets a render state for this node. Note,
     * there can only be one render state per type per node. That is, there can
     * only be a single BlendState a single TextureState, etc. If there is
     * already a render state for a type set the old render state will be
     * returned. Otherwise, null is returned.
     * 
     * @param rs
     *            the render state to add.
     * @return the old render state.
     */
    public RenderState setRenderState(RenderState rs) {
        if (rs == null) {
            return null;
        }

        if (renderStateList == null) {
            renderStateList = new RenderState[RenderState.StateType.values().length];
        }

        RenderState oldState = renderStateList[rs.getStateType().ordinal()];
        renderStateList[rs.getStateType().ordinal()] = rs;
        
        return oldState;
    }

    /**
     * Returns the requested RenderState that this Spatial currently has set or
     * null if none is set.
     * 
     * @param type
     *            the renderstate type to retrieve
     * @return a renderstate at the given position or null
     * @deprecated As of 2.0, use {@link #getRenderState(com.jme.scene.state.RenderState.StateType)} instead.
     */
    public RenderState getRenderState(int type) {
        return renderStateList != null ? renderStateList[type] : null;
    }

    /**
     * Returns the requested RenderState that this Spatial currently has set or
     * null if none is set.
     * 
     * @param type
     *            the {@link RenderState.StateType} to return
     * @return a {@link RenderState} that matches the given {@link RenderState.StateType} or null
     */
    public RenderState getRenderState(RenderState.StateType type) {

        return renderStateList != null ? renderStateList[type.ordinal()] : null;
    }

    /**
     * Clears a given render state index by setting it to null.
     * 
     * @param renderStateType
     *            The index of a RenderState to clear
     * @see com.jme.scene.state.RenderState#getType()
     * @deprecated As of 2.0, use {@link #clearRenderState(com.jme.scene.state.RenderState.StateType)} instead.
     */
    public void clearRenderState(int renderStateType) {
        if (renderStateList != null) {
            renderStateList[renderStateType] = null;
        }
    }

    /**
     * Clears a given render state index by setting it to null.
     * 
     * @param renderStateType
     *            The index of a RenderState to clear
     * @see com.jme.scene.state.RenderState#getType()
     */
    public void clearRenderState(RenderState.StateType type) {
        if (renderStateList != null) {
            renderStateList[type.ordinal()] = null;
        }
    }

    /**
     * <code>setRenderQueueMode</code> determines at what phase of the
     * rendering process this Spatial will rendered. There are 4 different
     * phases: QUEUE_SKIP - The spatial will be drawn as soon as possible,
     * before the other phases of rendering. QUEUE_OPAQUE - The renderer will
     * try to find the optimal order for rendering all objects using this mode.
     * You should use this mode for most normal objects, except transparent
     * ones, as it could give a nice performance boost to your application.
     * QUEUE_TRANSPARENT - This is the mode you should use for object with
     * Transparency in them. It will ensure the objects farthest away are
     * rendered first. That ensures when another transparent object is drawn on
     * top of previously drawn objects, you can see those (and the object drawn
     * using SKIP and OPAQUE) through the transparent parts of the newly drawn
     * object. QUEUE_ORTHO - This is a special mode, for drawing 2D object
     * without perspective (such as GUI or HUD parts) Lastly, there is a special
     * mode, QUEUE_INHERIT, that will ensure that this spatial uses the same
     * mode as the parent Node does.
     * 
     * @param renderQueueMode
     *            The mode to use for this Spatial.
     */
    public void setRenderQueueMode(int renderQueueMode) {
        this.renderQueueMode = renderQueueMode;
    }

    /**
     * @return
     */
    public int getLocalRenderQueueMode() {
        return renderQueueMode;
    }

    /**
     * @param zOrder
     */
    public void setZOrder(int zOrder) {
        this.zOrder = zOrder;
    }

    /**
     * @return
     */
    public int getZOrder() {
        return zOrder;
    }

    /**
     * @return
     */
    public NormalsMode getLocalNormalsMode() {
        return normalsMode;
    }

    /**
     * @param mode
     */
    public void setNormalsMode(NormalsMode mode) {
        this.normalsMode = mode;
    }

    /**
     * Sets how lights from parents should be combined for this spatial.
     * 
     * @param mode
     *            The light combine mode for this spatial
     * @throws IllegalArgumentException
     *             if mode is null
     */
    public void setLightCombineMode(LightCombineMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("mode can not be null.");
        }
        this.lightCombineMode = mode;
    }

    /**
     * @return the lightCombineMode set on this Spatial
     */
    public LightCombineMode getLocalLightCombineMode() {
        return lightCombineMode;
    }

    /**
     * Sets how textures from parents should be combined for this Spatial.
     * 
     * @param mode
     *            The new texture combine mode for this spatial.
     * @throws IllegalArgumentException
     *             if mode is null
     */
    public void setTextureCombineMode(TextureCombineMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("mode can not be null.");
        }
        this.textureCombineMode = mode;
    }

    /**
     * @return the textureCombineMode set on this Spatial
     */
    public TextureCombineMode getLocalTextureCombineMode() {
        return textureCombineMode;
    }

    /**
     * Returns this spatial's last frustum intersection result. This int is set
     * when a check is made to determine if the bounds of the object fall inside
     * a camera's frustum. If a parent is found to fall outside the frustum, the
     * value for this spatial will not be updated.
     * 
     * @return The spatial's last frustum intersection result.
     */
    public Camera.FrustumIntersect getLastFrustumIntersection() {
        return frustrumIntersects;
    }

    /**
     * Overrides the last intersection result. This is useful for operations
     * that want to start rendering at the middle of a scene tree and don't want
     * the parent of that node to influence culling. (See texture renderer code
     * for example.)
     * 
     * @param intersects
     *            the new value
     */
    public void setLastFrustumIntersection(Camera.FrustumIntersect intersects) {
        frustrumIntersects = intersects;
    }

    /**
     * Returns the Spatial's name.
     *
     * <p>
     * If you want to display a class name, then use
     * Spatial.class.getName() or getClass().getName().
     * That's that those methods are there for.
     * </p>
     */
    public String toString() {
        String nameString = getName(); // A subclass may overwrite getName()!
        return (nameString == null) ? "<NONAME>" : nameString;
    }

    public Matrix4f getLocalToWorldMatrix(Matrix4f store) {
        if (store == null) {
            store = new Matrix4f();
        } else {
            store.loadIdentity();
        }
        // multiply with scale first, then rotate, finally translate (cf.
        // Eberly)
        store.scale(getWorldScale());
        store.multLocal(getWorldRotation());
        store.setTranslation(getWorldTranslation());
        return store;
    }

    public Class<? extends Spatial> getClassTag() {
        return this.getClass();
    }

    /**
     * Note that we are <i>matching</i> the pattern, therefore the pattern
     * must match the entire pattern (i.e. it behaves as if it is sandwiched
     * between "^" and "$").
     * You can set regex modes, like case insensitivity, by using the (?X)
     * or (?X:Y) constructs.
     *
     * @param spatialSubclass Subclass which this must implement.
     *                        Null causes all Spatials to qualify.
     * @param nameRegex  Regular expression to match this name against.
     *                        Null causes all Names to qualify.
     * @return true if this implements the specified class and this's name
     *         matches the specified pattern.
     *
     * @see java.util.regex.Pattern
     */
    public boolean matches(
            Class<? extends Spatial> spatialSubclass, String nameRegex) {
        if (spatialSubclass != null && !spatialSubclass.isInstance(this))
            return false;
        if (nameRegex != null && (name == null || !name.matches(nameRegex)))
            return false;
        return true;
    }

    /**
     * A direct extension of String.matches(), which matches against the
     * Spatial's name.
     *
     * @return false if the spatial name is null
     * @see #matches(Class<? extends Spatial>, String)
     * @see String#matches(String)
     */
    public boolean matches(String nameRegex) {
        return matches(null, nameRegex);
    }
}
