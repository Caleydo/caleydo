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

import java.io.Serializable;

import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.util.export.Savable;

/**
 * <code>Camera</code> defines an interface that encapsulates viewport
 * management. Provided are convenience methods for setting up the view port and
 * the camera model. The frustum is also maintained here to allow for easy
 * frustum culling.
 *
 * @author Mark Powell
 * @author Gregg Patton
 */
public interface Camera extends Serializable, Savable {

    public enum FrustumIntersect {
        /**
         * defines a constant assigned to spatials that are completely outside
         * of this camera's view frustum.
         */
        Outside,
        /**
         * defines a constant assigned to spatials that are completely inside
         * the camera's view frustum.
         */
        Inside,
        /**
         * defines a constant assigned to spatials that are intersecting one of
         * the six planes that define the view frustum.
         */
        Intersects;
    }

    /**
     * <code>getLocation</code> returns the position of the camera.
     *
     * @return the position of the camera.
     */
    Vector3f getLocation();

    /**
     * <code>getDirection</code> returns the direction the camera is facing.
     *
     * @return the direction this camera object is facing.
     */
    Vector3f getDirection();

    /**
     * <code>getLeft</code> returns the left axis of the camera.
     *
     * @return the left axis of this camera object.
     */
    Vector3f getLeft();

    /**
     * <code>getUp</code> returns the up axis of the camera.
     *
     * @return the up axis of this camera object.
     */
    Vector3f getUp();

    /**
     * <code>setLocation</code> the position of the camera.
     *
     * @param location the position of the camera.
     */
    void setLocation( Vector3f location );

    /**
     * <code>setDirection</code> sets the direction the camera is facing.
     *
     * @param direction the new direction of the camera.
     */
    void setDirection( Vector3f direction );

    /**
     * <code>setLeft</code> sets the left axis of the camera.
     *
     * @param left the new left axis of the camera.
     */
    void setLeft( Vector3f left );

    /**
     * <code>setUp</code> sets the up axis of the camera.
     *
     * @param up the new up axis of the camera.
     */
    void setUp( Vector3f up );

    /**
     * <code>setAxes</code> sets the axes that define the camera's
     * orientation.
     *
     * @param left      the new left axis of the camera.
     * @param up        the new up axis of the camera.
     * @param direction the new direction of the camera.
     */
    void setAxes( Vector3f left, Vector3f up, Vector3f direction );

    /**
     * <code>setAxes</code> sets the camera's orientation via a rotational
     * matrix.
     *
     * @param axes the matrix that defines the camera orientation.
     */
    void setAxes( Quaternion axes );

    /**
     * <code>setFrustum</code> defines the frustum planes of the camera. This
     * frustum is defined by a six-sided box.
     *
     * @param near   the frustum plane closest to the eye point.
     * @param far    the frustum plane furthest from the eye point.
     * @param left   the frustum plane left of the eye point.
     * @param right  the frustum plane right of the eye point.
     * @param top    the frustum plane above the eye point.
     * @param bottom the frustum plane below the eye point.
     */
    void setFrustum( float near, float far, float left, float right,
                            float top, float bottom );

    /**
     * <code>setFrustumPerspective</code> defines the frustum for the camera.  This
     * frustum is defined by a viewing angle, aspect ratio, and near/far planes
     *
     * @param fovY   Frame of view angle along the Y.
     * @param aspect Width:Height ratio
     * @param near   Near view plane distance
     * @param far    Far view plane distance
     */
    void setFrustumPerspective( float fovY, float aspect, float near, float far );

    /**
     * <code>getFrustumBottom</code> returns the value of the bottom frustum
     * plane.
     *
     * @return the value of the bottom frustum plane.
     */
    float getFrustumBottom();

    /**
     * <code>setFrustumBottom</code> sets the value of the bottom frustum
     * plane.
     *
     * @param frustumBottom the value of the bottom frustum plane.
     */
    void setFrustumBottom( float frustumBottom );

    /**
     * <code>getFrustumFar</code> gets the value of the far frustum plane.
     *
     * @return the value of the far frustum plane.
     */
    float getFrustumFar();

    /**
     * <code>setFrustumFar</code> sets the value of the far frustum plane.
     *
     * @param frustumFar the value of the far frustum plane.
     */
    void setFrustumFar( float frustumFar );

    /**
     * <code>getFrustumLeft</code> gets the value of the left frustum plane.
     *
     * @return the value of the left frustum plane.
     */
    float getFrustumLeft();

    /**
     * <code>setFrustumLeft</code> sets the value of the left frustum plane.
     *
     * @param frustumLeft the value of the left frustum plane.
     */
    void setFrustumLeft( float frustumLeft );

    /**
     * <code>getFrustumNear</code> gets the value of the near frustum plane.
     *
     * @return the value of the near frustum plane.
     */
    float getFrustumNear();

    /**
     * <code>setFrustumNear</code> sets the value of the near frustum plane.
     *
     * @param frustumNear the value of the near frustum plane.
     */
    void setFrustumNear( float frustumNear );

    /**
     * <code>getFrustumRight</code> gets the value of the right frustum plane.
     *
     * @return frustumRight the value of the right frustum plane.
     */
    float getFrustumRight();

    /**
     * <code>setFrustumRight</code> sets the value of the right frustum plane.
     *
     * @param frustumRight the value of the right frustum plane.
     */
    void setFrustumRight( float frustumRight );

    /**
     * <code>getFrustumTop</code> gets the value of the top frustum plane.
     *
     * @return the value of the top frustum plane.
     */
    float getFrustumTop();

    /**
     * <code>setFrustumTop</code> sets the value of the top frustum plane.
     *
     * @param frustumTop the value of the top frustum plane.
     */
    void setFrustumTop( float frustumTop );

    /**
     * <code>setFrame</code> sets the view frame of the camera by setting the
     * location and orientation of the camera model.
     *
     * @param location  the position of the camera.
     * @param left      the left axis of the camera.
     * @param up        the up axis of the camera.
     * @param direction the direction the camera is facing.
     */
    void setFrame( Vector3f location, Vector3f left, Vector3f up,
                          Vector3f direction );

    /**
     * <code>setFrame</code> sets the view frame of the camera by setting the
     * location and the orientation of the camera model.
     *
     * @param location the position of the camera.
     * @param axes     the matrix that defines the orientation of the camera.
     */
    void setFrame( Vector3f location, Quaternion axes );

    /**
     * <code>update</code> updates the frustum viewport and frame of the
     * camera checking for any possible change in the position or orientation of
     * the camera.
     */
    void update();

    void normalize();

    /**
     * <code>getPlaneState</code> returns the state of the frustum planes. So
     * checks can be made as to which frustum plane has been examined for
     * culling thus far.
     *
     * @return the current plane state int.
     */
    int getPlaneState();

    /**
     * <code>setPlaneState</code> sets the state to keep track of tested
     * planes for culling.
     *
     * @param planeState the updated state.
     */
    void setPlaneState( int planeState );

    /**
     * <code>getViewPortLeft</code> gets the left boundary of the viewport
     *
     * @return the left boundary of the viewport
     */
    float getViewPortLeft();

    /**
     * <code>setViewPortLeft</code> sets the left boundary of the viewport
     *
     * @param left the left boundary of the viewport
     */
    void setViewPortLeft( float left );

    /**
     * <code>getViewPortRight</code> gets the right boundary of the viewport
     *
     * @return the right boundary of the viewport
     */
    float getViewPortRight();

    /**
     * <code>setViewPortRight</code> sets the right boundary of the viewport
     *
     * @param right the right boundary of the viewport
     */
    void setViewPortRight( float right );

    /**
     * <code>getViewPortTop</code> gets the top boundary of the viewport
     *
     * @return the top boundary of the viewport
     */
    float getViewPortTop();

    /**
     * <code>setViewPortTop</code> sets the top boundary of the viewport
     *
     * @param top the top boundary of the viewport
     */
    void setViewPortTop( float top );

    /**
     * <code>getViewPortBottom</code> gets the bottom boundary of the viewport
     *
     * @return the bottom boundary of the viewport
     */
    float getViewPortBottom();

    /**
     * <code>setViewPortBottom</code> sets the bottom boundary of the viewport
     *
     * @param bottom the bottom boundary of the viewport
     */
    void setViewPortBottom( float bottom );

    /**
     * <code>setViewPort</code> sets the boundaries of the viewport
     *
     * @param left   the left boundary of the viewport
     * @param right  the right boundary of the viewport
     * @param bottom the bottom boundary of the viewport
     * @param top    the top boundary of the viewport
     */
    void setViewPort( float left, float right, float bottom, float top );

    /**
     * <code>culled</code> tests a bounding volume against the planes of the
     * camera's frustum. The frustums planes are set such that the normals all
     * face in towards the viewable scene. Therefore, if the bounding volume is
     * on the negative side of the plane is can be culled out. If the object
     * should be culled (i.e. not rendered) true is returned, otherwise, false
     * is returned.
     *
     * @param bound the bound to check for culling
     * @return true if the bound should be culled, false otherwise.
     */
    Camera.FrustumIntersect contains( BoundingVolume bound );

    /**
     * <code>onFrustumChange</code> is an update callback that is activated if
     * the frustum values change.
     */
    void onFrustumChange();

    /**
     * <code>onViewPortChange</code> is an update callback that is activated
     * if the view port changes.
     */
    void onViewPortChange();

    /**
     * <code>onFrameChange</code> is an update callback that is activated if
     * the frame changes.
     */
    void onFrameChange();

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
    void lookAt( Vector3f pos, Vector3f worldUpVector );

    /**
     * Resizes this camera's view with the given width and height. This is
     * similar to constructing a new camera, but reusing the same Object. Camera
     * view dimensions are automatically set when the camera is associated with
     * a Renderer.
     * 
     * @param width
     *            the view width
     * @param height
     *            the view height
     */
    void resize(int width, int height);

    /**
     * @return true if parallel projection is enable, false if in normal perspective mode
     * @see #setParallelProjection(boolean)
     */
    boolean isParallelProjection();

    /**
     * Enable/disable parallel projection.
     *
     * @param value true to set up this camera for parallel projection is enable, false to enter normal perspective mode
     */
    void setParallelProjection( boolean value );

    /**
     * Convert screen to world coordinates.
     *
     * @param screenPosition Vector2f representing the screen position with 0,0 at the
     *                       bottom left
     * @param zPos           float The z position away from the viewing plane.
     * @return Vector3f The store vector, after storing it's result.
     */
    Vector3f getWorldCoordinates( Vector2f screenPosition, float zPos );

    /**
     * Convert screen to world coordinates.
     *
     * @param screenPosition Vector2f representing the screen position with 0,0 at the
     *                       bottom left
     * @param zPos           float The z position away from the viewing plane.
     * @param store          Vector3f The vector to store the result in.
     * @return Vector3f The store vector, after storing it's result.
     */
    Vector3f getWorldCoordinates( Vector2f screenPosition, float zPos, Vector3f store );

    /**
     * Convert world to screen coordinates.
     *
     * @param worldPosition Vector3f representing the world position
     * @return Vector3f Screen coordinates, with 0,0 at the bottom left.
     */
    Vector3f getScreenCoordinates( Vector3f worldPosition );

    /**
     * Convert world to screen coordinates.
     *
     * @param worldPosition Vector3f representing the world position
     * @param store         Vector3f The vector to store the result in.
     * @return Vector3f The store vector, after storing it's result.
     *         Screen coordinates, with 0,0 at the bottom left.
     */
    Vector3f getScreenCoordinates( Vector3f worldPosition, Vector3f store );

    /**
     * Apply the settings of the camera to the current graphics state.
     * <p>
     * If state should be applied even if not dirty, make sure to call {@link #update()} before.
     */
    void apply();
}
