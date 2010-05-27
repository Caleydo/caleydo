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

package com.jme.renderer;

import java.io.IOException;
import java.util.logging.Logger;

import com.jme.bounding.BoundingVolume;
import com.jme.math.FastMath;
import com.jme.math.Matrix4f;
import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.math.Plane.Side;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>AbstractCamera</code> implments the <code>Camera</code> interface
 * implementing all non-API specific camera calculations. Those requiring API
 * (LWJGL, JOGL, etc) specific calls are not implemented making this class
 * abstract. API specific classes are expected to extend this class and handle
 * renderer viewport setting.
 *
 * @author Mark Powell
 * @author Joshua Slack
 */
public abstract class AbstractCamera implements Camera {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(AbstractCamera.class
            .getName());

    //planes of the frustum
    /**
     * LEFT_PLANE represents the left plane of the camera frustum.
     */
    public static final int LEFT_PLANE = 0;

    /**
     * RIGHT_PLANE represents the right plane of the camera frustum.
     */
    public static final int RIGHT_PLANE = 1;

    /**
     * BOTTOM_PLANE represents the bottom plane of the camera frustum.
     */
    public static final int BOTTOM_PLANE = 2;

    /**
     * TOP_PLANE represents the top plane of the camera frustum.
     */
    public static final int TOP_PLANE = 3;

    /**
     * FAR_PLANE represents the far plane of the camera frustum.
     */
    public static final int FAR_PLANE = 4;

    /**
     * NEAR_PLANE represents the near plane of the camera frustum.
     */
    public static final int NEAR_PLANE = 5;

    /**
     * FRUSTUM_PLANES represents the number of planes of the camera frustum.
     */
    public static final int FRUSTUM_PLANES = 6;

    /**
     * MAX_WORLD_PLANES holds the maximum planes allowed by the system.
     */
    public static final int MAX_WORLD_PLANES = 32;

    //the location and orientation of the camera.
    /**
     * Camera's location
     */
    protected Vector3f location;

    /**
     * Direction of camera's 'left'
     */
    protected Vector3f left;

    /**
     * Direction of 'up' for camera.
     */
    protected Vector3f up;

    /**
     * Direction the camera is facing.
     */
    protected Vector3f direction;

    /**
     * Distance from camera to near frustum plane.
     */
    protected float frustumNear;

    /**
     * Distance from camera to far frustum plane.
     */
    protected float frustumFar;

    /**
     * Distance from camera to left frustum plane.
     */
    protected float frustumLeft;

    /**
     * Distance from camera to right frustum plane.
     */
    protected float frustumRight;

    /**
     * Distance from camera to top frustum plane.
     */
    protected float frustumTop;

    /**
     * Distance from camera to bottom frustum plane.
     */
    protected float frustumBottom;

    //Temporary values computed in onFrustumChange that are needed if a
    //call is made to onFrameChange.
    protected float coeffLeft[];

    protected float coeffRight[];

    protected float coeffBottom[];

    protected float coeffTop[];

    /* Frustum planes always processed for culling. Seems to simply always be 6. */
    protected int planeQuantity;

    //view port coordinates
    /**
     * Percent value on display where horizontal viewing starts for this camera.
     * Default is 0.
     */
    protected float viewPortLeft;

    /**
     * Percent value on display where horizontal viewing ends for this camera.
     * Default is 1.
     */
    protected float viewPortRight;

    /**
     * Percent value on display where vertical viewing ends for this camera.
     * Default is 1.
     */
    protected float viewPortTop;

    /**
     * Percent value on display where vertical viewing begins for this camera.
     * Default is 0.
     */
    protected float viewPortBottom;

    /**
     * Array holding the planes that this camera will check for culling.
     */
    protected Plane[] worldPlane;

    /**
     * Computation vector used in lookAt operations.
     */
    protected Vector3f newDirection = new Vector3f();

    /**
     * A mask value set during contains() that allows fast culling of a Node's
     * children.
     */
    private int planeState;
    
    protected int width;
    protected int height;

    /**
     * Constructor instantiates a new <code>AbstractCamera</code> object. All
     * values of the camera are set to default.
     */
    public AbstractCamera() {
        this(false);
    }
    /**
     * Constructor instantiates a new <code>AbstractCamera</code> object. All
     * values of the camera are set to default.
     */
    public AbstractCamera(boolean dataOnly) {
        setDataOnly(dataOnly);
        location = new Vector3f();
        left = new Vector3f( 1, 0, 0 );
        up = new Vector3f( 0, 1, 0 );
        direction = new Vector3f( 0, 0, 1 );

        frustumNear = 1.0f;
        frustumFar = 2.0f;
        frustumLeft = -0.5f;
        frustumRight = 0.5f;
        frustumTop = 0.5f;
        frustumBottom = -0.5f;

        coeffLeft = new float[2];
        coeffRight = new float[2];
        coeffBottom = new float[2];
        coeffTop = new float[2];

        viewPortLeft = 0.0f;
        viewPortRight = 1.0f;
        viewPortTop = 1.0f;
        viewPortBottom = 0.0f;

        planeQuantity = 6;

        worldPlane = new Plane[MAX_WORLD_PLANES];
        for ( int i = 0; i < MAX_WORLD_PLANES; i++ ) {
            worldPlane[i] = new Plane();
        }

        //call the API specific rendering
        //FIX ME: this calls methods of subclasses before the constructors of the subclasses have been called!!!
        onFrustumChange();
        onViewPortChange();
        onFrameChange();

        logger.info("Camera created.");
    }

    /**
     * <code>getFrustumBottom</code> returns the value of the bottom frustum
     * plane.
     *
     * @return the value of the bottom frustum plane.
     */
    public float getFrustumBottom() {
        return frustumBottom;
    }

    /**
     * <code>setFrustumBottom</code> sets the value of the bottom frustum
     * plane.
     *
     * @param frustumBottom the value of the bottom frustum plane.
     */
    public void setFrustumBottom( float frustumBottom ) {
        this.frustumBottom = frustumBottom;
        onFrustumChange();
    }

    /**
     * <code>getFrustumFar</code> gets the value of the far frustum plane.
     *
     * @return the value of the far frustum plane.
     */
    public float getFrustumFar() {
        return frustumFar;
    }

    /**
     * <code>setFrustumFar</code> sets the value of the far frustum plane.
     *
     * @param frustumFar the value of the far frustum plane.
     */
    public void setFrustumFar( float frustumFar ) {
        this.frustumFar = frustumFar;
        onFrustumChange();
    }

    /**
     * <code>getFrustumLeft</code> gets the value of the left frustum plane.
     *
     * @return the value of the left frustum plane.
     */
    public float getFrustumLeft() {
        return frustumLeft;
    }

    /**
     * <code>setFrustumLeft</code> sets the value of the left frustum plane.
     *
     * @param frustumLeft the value of the left frustum plane.
     */
    public void setFrustumLeft( float frustumLeft ) {
        this.frustumLeft = frustumLeft;
        onFrustumChange();
    }

    /**
     * <code>getFrustumNear</code> gets the value of the near frustum plane.
     *
     * @return the value of the near frustum plane.
     */
    public float getFrustumNear() {
        return frustumNear;
    }

    /**
     * <code>setFrustumNear</code> sets the value of the near frustum plane.
     *
     * @param frustumNear the value of the near frustum plane.
     */
    public void setFrustumNear( float frustumNear ) {
        this.frustumNear = frustumNear;
        onFrustumChange();
    }

    /**
     * <code>getFrustumRight</code> gets the value of the right frustum plane.
     *
     * @return frustumRight the value of the right frustum plane.
     */
    public float getFrustumRight() {
        return frustumRight;
    }

    /**
     * <code>setFrustumRight</code> sets the value of the right frustum plane.
     *
     * @param frustumRight the value of the right frustum plane.
     */
    public void setFrustumRight( float frustumRight ) {
        this.frustumRight = frustumRight;
        onFrustumChange();
    }

    /**
     * <code>getFrustumTop</code> gets the value of the top frustum plane.
     *
     * @return the value of the top frustum plane.
     */
    public float getFrustumTop() {
        return frustumTop;
    }

    /**
     * <code>setFrustumTop</code> sets the value of the top frustum plane.
     *
     * @param frustumTop the value of the top frustum plane.
     */
    public void setFrustumTop( float frustumTop ) {
        this.frustumTop = frustumTop;
        onFrustumChange();
    }

    /**
     * <code>getLocation</code> retrieves the location vector of the camera.
     *
     * @return the position of the camera.
     * @see Camera#getLocation()
     */
    public Vector3f getLocation() {
        return location;
    }

    /**
     * <code>getDirection</code> retrieves the direction vector the camera is
     * facing.
     *
     * @return the direction the camera is facing.
     * @see Camera#getDirection()
     */
    public Vector3f getDirection() {
        return direction;
    }

    /**
     * <code>getLeft</code> retrieves the left axis of the camera.
     *
     * @return the left axis of the camera.
     * @see Camera#getLeft()
     */
    public Vector3f getLeft() {
        return left;
    }

    /**
     * <code>getUp</code> retrieves the up axis of the camera.
     *
     * @return the up axis of the camera.
     * @see Camera#getUp()
     */
    public Vector3f getUp() {
        return up;
    }

    /**
     * <code>setLocation</code> sets the position of the camera.
     *
     * @param location the position of the camera.
     * @see Camera#setLocation(com.jme.math.Vector3f)
     */
    public void setLocation( Vector3f location ) {
        this.location = location;
        onFrameChange();
    }

    /**
     * <code>setDirection</code> sets the direction this camera is facing. In
     * most cases, this changes the up and left vectors of the camera. If your
     * left or up vectors change, you must updates those as well for correct
     * culling.
     *
     * @param direction the direction this camera is facing.
     * @see Camera#setDirection(com.jme.math.Vector3f)
     */
    public void setDirection( Vector3f direction ) {
        this.direction = direction;
        onFrameChange();
    }

    /**
     * <code>setLeft</code> sets the left axis of this camera. In most cases,
     * this changes the up and direction vectors of the camera. If your
     * direction or up vectors change, you must updates those as well for
     * correct culling.
     *
     * @param left the left axis of this camera.
     * @see Camera#setLeft(com.jme.math.Vector3f)
     */
    public void setLeft( Vector3f left ) {
        this.left = left;
        onFrameChange();
    }

    /**
     * <code>setUp</code> sets the up axis of this camera. In most cases, this
     * changes the direction and left vectors of the camera. If your left or up
     * vectors change, you must updates those as well for correct culling.
     *
     * @param up the up axis of this camera.
     * @see Camera#setUp(com.jme.math.Vector3f)
     */
    public void setUp( Vector3f up ) {
        this.up = up;
        onFrameChange();
    }

    /**
     * <code>setAxes</code> sets the axes (left, up and direction) for this
     * camera.
     *
     * @param left      the left axis of the camera.
     * @param up        the up axis of the camera.
     * @param direction the direction the camera is facing.
     * @see Camera#setAxes(com.jme.math.Vector3f,com.jme.math.Vector3f,com.jme.math.Vector3f)
     */
    public void setAxes( Vector3f left, Vector3f up, Vector3f direction ) {
        this.left = left;
        this.up = up;
        this.direction = direction;
        onFrameChange();
    }

    /**
     * <code>setAxes</code> uses a rotational matrix to set the axes of the
     * camera.
     *
     * @param axes the matrix that defines the orientation of the camera.
     */
    public void setAxes( Quaternion axes ) {
        left = axes.getRotationColumn( 0, left );
        up = axes.getRotationColumn( 1, up );
        direction = axes.getRotationColumn( 2, direction );
        onFrameChange();
    }

    /**
     * normalize normalizes the camera vectors.
     */
    public void normalize() {
        left.normalizeLocal();
        up.normalizeLocal();
        direction.normalizeLocal();
    }

    /**
     * <code>setFrustum</code> sets the frustum of this camera object.
     *
     * @param near   the near plane.
     * @param far    the far plane.
     * @param left   the left plane.
     * @param right  the right plane.
     * @param top    the top plane.
     * @param bottom the bottom plane.
     * @see Camera#setFrustum(float, float, float, float,
     *      float, float)
     */
    public void setFrustum( float near, float far, float left, float right,
                            float top, float bottom ) {

        frustumNear = near;
        frustumFar = far;
        frustumLeft = left;
        frustumRight = right;
        frustumTop = top;
        frustumBottom = bottom;
        onFrustumChange();
    }

    public void setFrustumPerspective( float fovY, float aspect, float near,
                                       float far ) {
        if (Float.isNaN(aspect) || Float.isInfinite(aspect)) {
            // ignore.
            logger.warning("Invalid aspect given to setFrustumPerspective: "
                    + aspect);
            return;
        }
        float h = FastMath.tan( fovY * FastMath.DEG_TO_RAD * .5f) * near;
        float w = h * aspect;
        frustumLeft = -w;
        frustumRight = w;
        frustumBottom = -h;
        frustumTop = h;
        frustumNear = near;
        frustumFar = far;
        onFrustumChange();
    }

    /**
     * <code>setFrame</code> sets the orientation and location of the camera.
     *
     * @param location  the point position of the camera.
     * @param left      the left axis of the camera.
     * @param up        the up axis of the camera.
     * @param direction the facing of the camera.
     * @see Camera#setFrame(com.jme.math.Vector3f,
     *      com.jme.math.Vector3f, com.jme.math.Vector3f, com.jme.math.Vector3f)
     */
    public void setFrame( Vector3f location, Vector3f left, Vector3f up,
                          Vector3f direction ) {

        this.location = location;
        this.left = left;
        this.up = up;
        this.direction = direction;
        onFrameChange();

    }

    /**
     * <code>lookAt</code> is a convienence method for auto-setting the frame
     * based on a world position the user desires the camera to look at. It
     * repoints the camera towards the given position using the difference
     * between the position and the current camera location as a direction
     * vector and the worldUpVector to compute up and left camera vectors.
     *
     * @param pos           where to look at in terms of world coordinates
     * @param worldUpVector a normalized vector indicating the up direction of the world.
     *                      (typically {0, 1, 0} in jME.)
     */
    public void lookAt( Vector3f pos, Vector3f worldUpVector ) {
        newDirection.set( pos ).subtractLocal( location ).normalizeLocal();

        // check to see if we haven't really updated camera -- no need to call
        // sets.
        if ( newDirection.equals( direction ) ) {
            return;
        }
        direction.set( newDirection );

        up.set(worldUpVector).normalizeLocal();
        if (up.equals(Vector3f.ZERO))
            up.set(Vector3f.UNIT_Y);
        left.set(up).crossLocal(direction).normalizeLocal();
        if (left.equals(Vector3f.ZERO)) {
            if (direction.x != 0) {
                left.set(direction.y, -direction.x, 0f);
            } else {
                left.set(0f, direction.z, -direction.y);
            }
        }
        up.set(direction).crossLocal(left).normalizeLocal();
        onFrameChange();
    }

    /**
     * <code>setFrame</code> sets the orientation and location of the camera.
     * 
     * @param location
     *            the point position of the camera.
     * @param axes
     *            the orientation of the camera.
     */
    public void setFrame( Vector3f location, Quaternion axes ) {
        this.location = location;
        left = axes.getRotationColumn( 0, left );
        up = axes.getRotationColumn( 1, up );
        direction = axes.getRotationColumn( 2, direction );
        onFrameChange();
    }

    /**
     * <code>update</code> updates the camera parameters by calling
     * <code>onFrustumChange</code>,<code>onViewPortChange</code> and
     * <code>onFrameChange</code>.
     *
     * @see Camera#update()
     */
    public void update() {
        onFrustumChange();
        onViewPortChange();
        onFrameChange();
    }

    /**
     * <code>getPlaneState</code> returns the state of the frustum planes. So
     * checks can be made as to which frustum plane has been examined for
     * culling thus far.
     *
     * @return the current plane state int.
     */
    public int getPlaneState() {
        return planeState;
    }

    /**
     * <code>setPlaneState</code> sets the state to keep track of tested
     * planes for culling.
     *
     * @param planeState the updated state.
     */
    public void setPlaneState( int planeState ) {
        this.planeState = planeState;
    }

    /**
     * <code>getViewPortLeft</code> gets the left boundary of the viewport
     *
     * @return the left boundary of the viewport
     */
    public float getViewPortLeft() {
        return viewPortLeft;
    }

    /**
     * <code>setViewPortLeft</code> sets the left boundary of the viewport
     *
     * @param left the left boundary of the viewport
     */
    public void setViewPortLeft( float left ) {
        viewPortLeft = left;
    }

    /**
     * <code>getViewPortRight</code> gets the right boundary of the viewport
     *
     * @return the right boundary of the viewport
     */
    public float getViewPortRight() {
        return viewPortRight;
    }

    /**
     * <code>setViewPortRight</code> sets the right boundary of the viewport
     *
     * @param right the right boundary of the viewport
     */
    public void setViewPortRight( float right ) {
        viewPortRight = right;
    }

    /**
     * <code>getViewPortTop</code> gets the top boundary of the viewport
     *
     * @return the top boundary of the viewport
     */
    public float getViewPortTop() {
        return viewPortTop;
    }

    /**
     * <code>setViewPortTop</code> sets the top boundary of the viewport
     *
     * @param top the top boundary of the viewport
     */
    public void setViewPortTop( float top ) {
        viewPortTop = top;
    }

    /**
     * <code>getViewPortBottom</code> gets the bottom boundary of the viewport
     *
     * @return the bottom boundary of the viewport
     */
    public float getViewPortBottom() {
        return viewPortBottom;
    }

    /**
     * <code>setViewPortBottom</code> sets the bottom boundary of the viewport
     *
     * @param bottom the bottom boundary of the viewport
     */
    public void setViewPortBottom( float bottom ) {
        viewPortBottom = bottom;
    }

    /**
     * <code>setViewPort</code> sets the boundaries of the viewport
     *
     * @param left   the left boundary of the viewport
     * @param right  the right boundary of the viewport
     * @param bottom the bottom boundary of the viewport
     * @param top    the top boundary of the viewport
     */
    public void setViewPort( float left, float right, float bottom, float top ) {
        setViewPortLeft( left );
        setViewPortRight( right );
        setViewPortBottom( bottom );
        setViewPortTop( top );
    }

    /**
     * <code>culled</code> tests a bounding volume against the planes of the
     * camera's frustum. The frustums planes are set such that the normals all
     * face in towards the viewable scene. Therefore, if the bounding volume is
     * on the negative side of the plane is can be culled out. If the object
     * should be culled (i.e. not rendered) true is returned, otherwise, false
     * is returned. If bound is null, false is returned and the object will not
     * be culled.
     *
     * @param bound the bound to check for culling
     * @return true if the bound should be culled, false otherwise.
     */
    public Camera.FrustumIntersect contains( BoundingVolume bound ) {
        if ( bound == null ) {
            return FrustumIntersect.Inside;
        }

        int mask;
        FrustumIntersect rVal = FrustumIntersect.Inside;

        for ( int planeCounter = FRUSTUM_PLANES; planeCounter >= 0; planeCounter-- ) {
            if ( planeCounter == bound.getCheckPlane() ) {
                continue; // we have already checked this plane at first iteration
            }
            int planeId = ( planeCounter == FRUSTUM_PLANES ) ? bound.getCheckPlane() : planeCounter;

            mask = 1 << ( planeId );
            if ( ( planeState & mask ) == 0 ) {
                Side side = bound.whichSide( worldPlane[planeId] );

                if (side == Side.NEGATIVE) {
                    //object is outside of frustum
                    bound.setCheckPlane( planeId );
                    return FrustumIntersect.Outside;
                } else if (side == Side.POSITIVE) {
                    //object is visible on *this* plane, so mark this plane
                    //so that we don't check it for sub nodes.
                    planeState |= mask;
                } else {
                    rVal = FrustumIntersect.Intersects;
                }
            }
        }

        return rVal;
    }

    /**
     * <code>onFrustumChange</code> updates the frustum to reflect any changes
     * made to the planes. The new frustum values are kept in a temporary
     * location for use when calculating the new frame. It should be noted that
     * the abstract implementation of this class only updates the data, and does
     * not make any rendering calls. As such, any impelmenting subclass should
     * insure to override this method call it with super and then call the
     * rendering specific code.
     */
    public void onFrustumChange() {
        if ( !isParallelProjection() ) {
            float nearSquared = frustumNear * frustumNear;
            float leftSquared = frustumLeft * frustumLeft;
            float rightSquared = frustumRight * frustumRight;
            float bottomSquared = frustumBottom * frustumBottom;
            float topSquared = frustumTop * frustumTop;

            float inverseLength = FastMath.invSqrt( nearSquared + leftSquared );
            coeffLeft[0] = frustumNear * inverseLength;
            coeffLeft[1] = -frustumLeft * inverseLength;

            inverseLength = FastMath.invSqrt( nearSquared + rightSquared );
            coeffRight[0] = -frustumNear * inverseLength;
            coeffRight[1] = frustumRight * inverseLength;

            inverseLength = FastMath.invSqrt( nearSquared + bottomSquared );
            coeffBottom[0] = frustumNear * inverseLength;
            coeffBottom[1] = -frustumBottom * inverseLength;

            inverseLength = FastMath.invSqrt( nearSquared + topSquared );
            coeffTop[0] = -frustumNear * inverseLength;
            coeffTop[1] = frustumTop * inverseLength;
        }
        else {
            coeffLeft[0] = 1;
            coeffLeft[1] = 0;

            coeffRight[0] = -1;
            coeffRight[1] = 0;

            coeffBottom[0] = 1;
            coeffBottom[1] = 0;

            coeffTop[0] = -1;
            coeffTop[1] = 0;
        }

        updateMatrices = true;
    }

    /**
     * <code>onFrameChange</code> updates the view frame of the camera. It
     * should be noted that the abstract implementation of this class only
     * updates the data, and does not make any rendering calls. As such, any
     * implementing subclass should insure to override this method call it with
     * super and then call the rendering specific code.
     */
    public void onFrameChange() {
        float dirDotLocation = direction.dot( location );

        // left plane
        Vector3f leftPlaneNormal = worldPlane[LEFT_PLANE].normal;
        leftPlaneNormal.x = left.x * coeffLeft[0];
        leftPlaneNormal.y = left.y * coeffLeft[0];
        leftPlaneNormal.z = left.z * coeffLeft[0];
        leftPlaneNormal.addLocal( direction.x * coeffLeft[1], direction.y
                * coeffLeft[1], direction.z * coeffLeft[1] );
        worldPlane[LEFT_PLANE].setConstant( location.dot( leftPlaneNormal ) );

        // right plane
        Vector3f rightPlaneNormal = worldPlane[RIGHT_PLANE].normal;
        rightPlaneNormal.x = left.x * coeffRight[0];
        rightPlaneNormal.y = left.y * coeffRight[0];
        rightPlaneNormal.z = left.z * coeffRight[0];
        rightPlaneNormal.addLocal( direction.x * coeffRight[1], direction.y
                * coeffRight[1], direction.z * coeffRight[1] );
        worldPlane[RIGHT_PLANE].setConstant( location.dot( rightPlaneNormal ) );

        // bottom plane
        Vector3f bottomPlaneNormal = worldPlane[BOTTOM_PLANE].normal;
        bottomPlaneNormal.x = up.x * coeffBottom[0];
        bottomPlaneNormal.y = up.y * coeffBottom[0];
        bottomPlaneNormal.z = up.z * coeffBottom[0];
        bottomPlaneNormal.addLocal( direction.x * coeffBottom[1], direction.y
                * coeffBottom[1], direction.z * coeffBottom[1] );
        worldPlane[BOTTOM_PLANE].setConstant( location.dot( bottomPlaneNormal ) );

        // top plane
        Vector3f topPlaneNormal = worldPlane[TOP_PLANE].normal;
        topPlaneNormal.x = up.x * coeffTop[0];
        topPlaneNormal.y = up.y * coeffTop[0];
        topPlaneNormal.z = up.z * coeffTop[0];
        topPlaneNormal.addLocal( direction.x * coeffTop[1], direction.y
                * coeffTop[1], direction.z * coeffTop[1] );
        worldPlane[TOP_PLANE].setConstant( location.dot( topPlaneNormal ) );

        if ( isParallelProjection() ) {
            worldPlane[LEFT_PLANE].setConstant( worldPlane[LEFT_PLANE].getConstant() + frustumLeft );
            worldPlane[RIGHT_PLANE].setConstant( worldPlane[RIGHT_PLANE].getConstant() - frustumRight );
            worldPlane[TOP_PLANE].setConstant( worldPlane[TOP_PLANE].getConstant() + frustumTop );
            worldPlane[BOTTOM_PLANE].setConstant( worldPlane[BOTTOM_PLANE].getConstant() - frustumBottom );
        }

        // far plane
        worldPlane[FAR_PLANE].normal.set( -direction.x, -direction.y,
                -direction.z );
        worldPlane[FAR_PLANE].setConstant( -( dirDotLocation + frustumFar ) );

        // near plane
        worldPlane[NEAR_PLANE].normal
                .set( direction.x, direction.y, direction.z );
        worldPlane[NEAR_PLANE].setConstant( dirDotLocation + frustumNear );

        updateMatrices = true;
        updateSMatrices = true;
    }

    /**
     * @return true if parallel projection is enable, false if in normal perspective mode
     * @see #setParallelProjection(boolean)
     */
    public boolean isParallelProjection() {
        return this.parallelProjection;
    }

    /**
     * store the value for field parallelProjection
     */
    private boolean parallelProjection;

    /**
     * Enable/disable parallel projection.
     *
     * @param value true to set up this camera for parallel projection is enable, false to enter normal perspective mode
     */
    public void setParallelProjection( final boolean value ) {
        this.parallelProjection = value;
    }

    /* @see Camera#getWorldCoordinates */
    public Vector3f getWorldCoordinates( Vector2f screenPos, float zPos ) {
        return getWorldCoordinates( screenPos, zPos, null );
    }

    protected final Matrix4f _transMatrix = new Matrix4f();
    protected final Matrix4f _modelView = new Matrix4f();
    protected final Matrix4f _projection = new Matrix4f();


    public Matrix4f getProjectionMatrix() {
        if (isParallelProjection()) {
            _projection.loadIdentity();
            _projection.m00 = 2.0f / (frustumRight - frustumLeft);
            _projection.m11 = 2.0f / (frustumBottom - frustumTop);
            _projection.m22 = -2.0f / (frustumFar - frustumNear);
            _projection.m33 = 1f;
            _projection.m30 = -(frustumRight + frustumLeft) / (frustumRight - frustumLeft);
            _projection.m31 = -(frustumBottom + frustumTop) / (frustumBottom - frustumTop);
            _projection.m32 = -(frustumFar + frustumNear) / (frustumFar - frustumNear);
        } else {
            // XXX: Cache results or is this low cost enough to happen every time it is called?
            _projection.loadIdentity();
            _projection.m00 = (2.0f * frustumNear) / (frustumRight - frustumLeft);
            _projection.m11 = (2.0f * frustumNear) / (frustumTop - frustumBottom);
            _projection.m20 = (frustumRight + frustumLeft) / (frustumRight - frustumLeft);
            _projection.m21 = (frustumTop + frustumBottom) / (frustumTop - frustumBottom);
            _projection.m22 = -(frustumFar + frustumNear) / (frustumFar - frustumNear);
            _projection.m32 = -(2.0f * frustumFar * frustumNear) / (frustumFar - frustumNear);
            _projection.m23 = -1.0f;
            _projection.m33 = -0.0f;
        }
        return _projection;
    }

    public Matrix4f getModelViewMatrix() {
        // XXX: Cache results or is this low cost enough to happen every time it is called?
        _modelView.loadIdentity();
        _modelView.m00 = -left.x;
        _modelView.m10 = -left.y;
        _modelView.m20 = -left.z;

        _modelView.m01 = up.x;
        _modelView.m11 = up.y;
        _modelView.m21 = up.z;

        _modelView.m02 = -direction.x;
        _modelView.m12 = -direction.y;
        _modelView.m22 = -direction.z;

        _transMatrix.loadIdentity();
        _transMatrix.m30 = -location.x;
        _transMatrix.m31 = -location.y;
        _transMatrix.m32 = -location.z;

        _transMatrix.multLocal(_modelView);
        _modelView.set(_transMatrix);

        return _modelView;
    }

    private static final Quaternion tmp_quat = new Quaternion();

    private boolean updateMatrices = true;
    private boolean updateSMatrices = true;
    private final Matrix4f modelViewProjectionInverse = new Matrix4f();
    private final Matrix4f modelViewProjection = new Matrix4f();

    private boolean dataOnly;

    /* @see Camera#getWorldCoordinates */
    public Vector3f getWorldCoordinates( Vector2f screenPosition,
                                         float zPos, Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        checkViewProjection();
        if ( updateMatrices ) {
            modelViewProjection.invert( modelViewProjectionInverse );
            updateMatrices = false;
        }
        tmp_quat.set(
                ( screenPosition.x / getWidth() - viewPortLeft ) / ( viewPortRight - viewPortLeft ) * 2 - 1,
                ( screenPosition.y / getHeight() - viewPortBottom ) / ( viewPortTop - viewPortBottom ) * 2 - 1,
                zPos * 2 - 1, 1 );
        modelViewProjectionInverse.mult( tmp_quat, tmp_quat );
        tmp_quat.multLocal( 1.0f / tmp_quat.w );
        store.x = tmp_quat.x;
        store.y = tmp_quat.y;
        store.z = tmp_quat.z;
        return store;
    }

    /* @see Camera#getScreenCoordinates */
    public Vector3f getScreenCoordinates( Vector3f worldPos ) {
        return getScreenCoordinates( worldPos, null );
    }


    /**
     * Implementation contributed by Zbyl.
     *
     * @see Camera#getScreenCoordinates(Vector3f, Vector3f)
     */
    public Vector3f getScreenCoordinates( Vector3f worldPosition, Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        checkViewProjection();
        tmp_quat.set( worldPosition.x, worldPosition.y, worldPosition.z, 1 );
        modelViewProjection.mult( tmp_quat, tmp_quat );
        tmp_quat.multLocal( 1.0f / tmp_quat.w );
        store.x = ( ( tmp_quat.x + 1 ) * ( viewPortRight - viewPortLeft ) / 2 ) * getWidth();
        store.y = ( ( tmp_quat.y + 1 ) * ( viewPortTop - viewPortBottom ) / 2 ) * getHeight();
        store.z = ( tmp_quat.z + 1 ) / 2;

        return store;
    }

    /**
     * update modelViewProjection if necessary.
     */
    private void checkViewProjection() {
        if ( updateSMatrices ) {
            modelViewProjection.set( getModelViewMatrix() ).multLocal( getProjectionMatrix() );
            updateSMatrices = false;
        }
    }

    /**
     * @return the width/resolution of the display.
     */
    public abstract int getHeight();

    /**
     * @return the height/resolution of the display.
     */
    public abstract int getWidth();
    
    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(location, "location", Vector3f.ZERO);
        capsule.write(left, "left", Vector3f.UNIT_X);
        capsule.write(up, "up", Vector3f.UNIT_Y);
        capsule.write(direction, "direction", Vector3f.UNIT_Z);
        capsule.write(frustumNear, "frustumNear", 1);
        capsule.write(frustumFar, "frustumFar", 2);
        capsule.write(frustumLeft, "frustumLeft", -0.5f);
        capsule.write(frustumRight, "frustumRight", 0.5f);
        capsule.write(frustumTop, "frustumTop", 0.5f);
        capsule.write(frustumBottom, "frustumBottom", -0.5f);
        capsule.write(coeffLeft, "coeffLeft", new float[2]);
        capsule.write(coeffRight, "coeffRight", new float[2]);
        capsule.write(coeffBottom, "coeffBottom", new float[2]);
        capsule.write(coeffTop, "coeffTop", new float[2]);
        capsule.write(planeQuantity, "planeQuantity", 6);
        capsule.write(viewPortLeft, "viewPortLeft", 0);
        capsule.write(viewPortRight, "viewPortRight", 1);
        capsule.write(viewPortTop, "viewPortTop", 1);
        capsule.write(viewPortBottom, "viewPortBottom", 0);
        capsule.write(width, "width", 0);
        capsule.write(height, "height", 0);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        location = (Vector3f)capsule.readSavable("location", Vector3f.ZERO.clone());
        left = (Vector3f)capsule.readSavable("left", Vector3f.UNIT_X.clone());
        up = (Vector3f)capsule.readSavable("up", Vector3f.UNIT_Y.clone());
        direction = (Vector3f)capsule.readSavable("direction", Vector3f.UNIT_Z.clone());
        frustumNear = capsule.readFloat("frustumNear", 1);
        frustumFar = capsule.readFloat("frustumFar", 2);
        frustumLeft = capsule.readFloat("frustumLeft", -0.5f);
        frustumRight = capsule.readFloat("frustumRight", 0.5f);
        frustumTop = capsule.readFloat("frustumTop", 0.5f);
        frustumBottom = capsule.readFloat("frustumBottom", -0.5f);
        coeffLeft = capsule.readFloatArray("coeffLeft", new float[2]);
        coeffRight = capsule.readFloatArray("coeffRight", new float[2]);
        coeffBottom = capsule.readFloatArray("coeffBottom", new float[2]);
        coeffTop = capsule.readFloatArray("coeffTop", new float[2]);
        planeQuantity = capsule.readInt("planeQuantity", 6);
        viewPortLeft = capsule.readFloat("viewPortLeft", 0);
        viewPortRight = capsule.readFloat("viewPortRight", 1);
        viewPortTop = capsule.readFloat("viewPortTop", 1);
        viewPortBottom = capsule.readFloat("viewPortBottom", 0);
        width = capsule.readInt("width", 0);
        height = capsule.readInt("height", 0);
    }
    
    public Class<AbstractCamera> getClassTag() {
        return AbstractCamera.class;
    }
    
    public void setDataOnly(boolean dataOnly) {
        this.dataOnly = dataOnly;
    }
    
    public boolean isDataOnly() {
        return dataOnly;
    }
}

