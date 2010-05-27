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

import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>BillboardNode</code> defines a node that always orients towards the
 * camera. However, it does not tilt up/down as the camera rises. This keep
 * geometry from appearing to fall over if the camera rises or lowers.
 * <code>BillboardNode</code> is useful to contain a single quad that has a
 * image applied to it for lowest detail models. This quad, with the texture,
 * will appear to be a full model at great distances, and save on rendering and
 * memory. It is important to note that for AXIAL mode, the billboards
 * orientation will always be up (0,1,0). This means that a "standard" jME
 * camera with up (0,1,0) is the only camera setting compatible with AXIAL mode.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: BillboardNode.java 4636 2009-08-28 14:52:25Z skye.book $
 */
public class BillboardNode extends Node {
    private static final long serialVersionUID = 1L;

    private float lastTime;

    private Matrix3f orient;

    private Vector3f look;

    private Vector3f left;

    private int alignment;

    /** Alligns this Billboard Node to the screen. */
    public static final int SCREEN_ALIGNED = 0;

    /** Alligns this Billboard Node to the screen, but keeps the Y axis fixed. */
    public static final int AXIAL = 1;
    public static final int AXIAL_Y = 1;

    /** Alligns this Billboard Node to the camera position. */
    public static final int CAMERA_ALIGNED = 2;

    /** Alligns this Billboard Node to the screen, but keeps the Z axis fixed. */
    public static final int AXIAL_Z = 3;

    
    public BillboardNode() {}
    /**
     * Constructor instantiates a new <code>BillboardNode</code>. The name of
     * the node is supplied during construction.
     * 
     * @param name
     *            the name of the node.
     */
    public BillboardNode(String name) {
        super(name);
        orient = new Matrix3f();
        look = new Vector3f();
        left = new Vector3f();
        alignment = SCREEN_ALIGNED;
    }

    /**
     * <code>updateWorldData</code> defers the updating of the billboards
     * orientation until rendering. This keeps the billboard from being
     * needlessly oriented if the player can not actually see it.
     * 
     * @param time
     *            the time between frames.
     * @see com.jme.scene.Spatial#updateWorldData(float)
     */
    public void updateWorldData(float time) {
        // removed due to bounding problems (incorrect bound -> culled -> not drawn -> not updated)
        // see topic 5684
        // TODO: optimze this again?
        lastTime = 0; // time
        super.updateWorldData( time );
    }

    /**
     * <code>draw</code> updates the billboards orientation then renders the
     * billboard's children.
     * 
     * @param r
     *            the renderer used to draw.
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     */
    public void draw(Renderer r) {
        Camera cam = r.getCamera();
        rotateBillboard(cam);

        super.draw(r);
    }

    /**
     * rotate the billboard based on the type set
     * 
     * @param cam
     *            Camera
     */
    public void rotateBillboard(Camera cam) {
        // get the scale, translation and rotation of the node in world space
        updateWorldVectors();

        switch (alignment) {
            case AXIAL_Y:
                rotateAxial(cam, Vector3f.UNIT_Y);
                break;
            case AXIAL_Z:
                rotateAxial(cam, Vector3f.UNIT_Z);
                break;
            case SCREEN_ALIGNED:
                rotateScreenAligned(cam);
                break;
            case CAMERA_ALIGNED:
                rotateCameraAligned(cam);
                break;
        }

        if (children == null) return;
        for (int i = 0, cSize = getChildren().size(); i < cSize; i++) {
            Spatial child = getChildren().get(i);
            if (child != null) {
                child.updateGeometricState(lastTime, false);
            }
        }
    }

    /**
     * Aligns this Billboard Node so that it points to the camera position.
     * 
     * @param camera
     *            Camera
     */
    private void rotateCameraAligned(Camera camera) {
        look.set(camera.getLocation()).subtractLocal(worldTranslation);
        // coopt left for our own purposes.
        Vector3f xzp = left;
        // The xzp vector is the projection of the look vector on the xz plane
        xzp.set(look.x, 0, look.z);
        
        // check for undefined rotation...
        if (xzp.equals(Vector3f.ZERO)) return;

        look.normalizeLocal();
        xzp.normalizeLocal();
        float cosp = look.dot(xzp);

        // compute the local orientation matrix for the billboard
        orient.m00 = xzp.z;
        orient.m01 = xzp.x * -look.y;
        orient.m02 = xzp.x * cosp;
        orient.m10 = 0;
        orient.m11 = cosp;
        orient.m12 = look.y;
        orient.m20 = -xzp.x;
        orient.m21 = xzp.z * -look.y;
        orient.m22 = xzp.z * cosp;

        // The billboard must be oriented to face the camera before it is
        // transformed into the world.
        worldRotation.apply(orient);
    }

    /**
     * Rotate the billboard so it points directly opposite the direction
     * that the camera is facing.
     * 
     * @param camera
     *            Camera
     */
    private void rotateScreenAligned(Camera camera) {
        // coopt diff for our in direction:
        look.set(camera.getDirection()).negateLocal();
        // coopt loc for our left direction:
        left.set(camera.getLeft()).negateLocal();
        orient.fromAxes(left, camera.getUp(), look);
        worldRotation.fromRotationMatrix(orient);
    }

    /**
     * Rotate the billboard towards the camera, but keep the given axis fixed.
     * 
     * @param camera
     *            Camera
     */
    private void rotateAxial(Camera camera, Vector3f axis) {
        // Compute the additional rotation required for the billboard to face
        // the camera. To do this, the camera must be inverse-transformed into
        // the model space of the billboard.
        look.set(camera.getLocation()).subtractLocal(worldTranslation);
        worldRotation.mult(look, left); // coopt left for our own purposes.
        left.x *= 1.0f / worldScale.x;
        left.y *= 1.0f / worldScale.y;
        left.z *= 1.0f / worldScale.z;

        // squared length of the camera projection in the xz-plane
        float lengthSquared = left.x * left.x + left.z * left.z;
        if (lengthSquared < FastMath.FLT_EPSILON) {
            // camera on the billboard axis, rotation not defined
            return;
        }

        // unitize the projection
        float invLength = FastMath.invSqrt(lengthSquared);
        if (axis.y == 1) {
            left.x *= invLength;
            left.y = 0.0f;
            left.z *= invLength;

            // compute the local orientation matrix for the billboard
            orient.m00 = left.z;
            orient.m01 = 0;
            orient.m02 = left.x;
            orient.m10 = 0;
            orient.m11 = 1;
            orient.m12 = 0;
            orient.m20 = -left.x;
            orient.m21 = 0;
            orient.m22 = left.z;
        } else if (axis.z == 1) {
            left.x *= invLength;
            left.y *= invLength;            
            left.z = 0.0f;

            // compute the local orientation matrix for the billboard
            orient.m00 = left.y;
            orient.m01 = left.x;
            orient.m02 = 0;
            orient.m10 = -left.y;
            orient.m11 = left.x;
            orient.m12 = 0;
            orient.m20 = 0;
            orient.m21 = 0;
            orient.m22 = 1;
        }

        // The billboard must be oriented to face the camera before it is
        // transformed into the world.
        worldRotation.apply(orient);
    }

    /**
     * Returns the alignment this BillboardNode is set too.
     * 
     * @return The alignment of rotation, AXIAL, CAMERA or SCREEN.
     */
    public int getAlignment() {
        return alignment;
    }

    /**
     * Sets the type of rotation this BillboardNode will have. The alignment can
     * be CAMERA_ALIGNED, SCREEN_ALIGNED or AXIAL. Invalid alignments will
     * assume no billboard rotation.
     */
    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }
    
    @Override
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(orient, "orient", new Matrix3f());
        capsule.write(look, "look", Vector3f.ZERO);
        capsule.write(left, "left", Vector3f.ZERO);
        capsule.write(alignment, "alignment", SCREEN_ALIGNED);
    }

    @Override
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        orient = (Matrix3f)capsule.readSavable("orient", new Matrix3f());
        look = (Vector3f)capsule.readSavable("look", Vector3f.ZERO.clone());
        left = (Vector3f)capsule.readSavable("left", Vector3f.ZERO.clone());
        alignment = capsule.readInt("alignment", SCREEN_ALIGNED);
    }
}