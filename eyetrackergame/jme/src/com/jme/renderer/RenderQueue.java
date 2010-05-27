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

import java.util.Arrays;
import java.util.Comparator;

import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.scene.state.CullState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.JmeException;
import com.jme.util.SortUtil;

/**
 * This optional class supports queueing of rendering states that are drawn when
 * displayBackBuffer is called on the renderer. All spatials in the opaque
 * bucket are rendered first in order closest to farthest. Then all spatials in
 * the opaque bucket are rendered in order farthest to closest. Finally all
 * spatials in the ortho bucket are rendered in ortho mode from highest to
 * lowest Z order. As a user, you shouldn't need to use this class directly. All
 * you'll need to do is call Spatial.setRenderQueueMode .
 * 
 * @author Joshua Slack
 * @author Jack Lindamood (javadoc + SpatialList only)
 * @see com.jme.scene.Spatial#setRenderQueueMode(int)
 *  
 */
public class RenderQueue {

    /** List of all transparent object to render. */
    private SpatialList transparentBucket;
    private SpatialList transparentBackBucket;

    /** List of all opaque object to render. */
    private SpatialList opaqueBucket;
    private SpatialList opaqueBackBucket;

    /** List of all ortho object to render. */
    private SpatialList orthoBucket;
    private SpatialList orthoBackBucket;

    /** The renderer. */
    private Renderer renderer;
    
    /** CullState for two pass transparency rendering. */
    private CullState tranCull;
    
    /** ZBufferState for two pass transparency rendering. */
    private ZBufferState tranZBuff;
    
    /** boolean for enabling / disabling two pass transparency rendering. */
    private boolean twoPassTransparent = true;
    
    private Vector3f tempVector = new Vector3f();

    /**
     * Creates a new render queue that will work with the given renderer.
     * 
     * @param r
     */
    public RenderQueue(Renderer r) {
        this.renderer = r;
        tranCull = r.createCullState();
        tranZBuff = r.createZBufferState();
        tranZBuff.setWritable(false);
        tranZBuff.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        setupBuckets();
    }

    /**
     * Enables/Disables two pass transparency rendering. If enabled, objects in
     * the TRANSPARENT queue will be rendered in two passes. On the first pass,
     * objects are rendered with front faces culled. On the second pass, objects
     * are rendered with back faces culled.
     * 
     * This allows complex transparent objects to be rendered whole without
     * concern as to the order of the faces drawn.
     * 
     * @param enabled
     *            set true to turn on two pass transparency rendering
     */
    public void setTwoPassTransparency(boolean enabled) {
        twoPassTransparent = enabled;
    }
    
    /**
     * @return true if two pass transparency rendering is enabled.
     */
    public boolean isTwoPassTransparency() {
        return twoPassTransparent;
    }
    
    /**
     * Creates the buckets needed.
     */
    private void setupBuckets() {
        opaqueBucket = new SpatialList(new OpaqueComp());
        opaqueBackBucket = new SpatialList(new OpaqueComp());
        transparentBucket = new SpatialList(new TransparentComp());
        transparentBackBucket = new SpatialList(new TransparentComp());
        orthoBucket = new SpatialList(new OrthoComp());
        orthoBackBucket = new SpatialList(new OrthoComp());
    }

    /**
     * Add a given Spatial to the RenderQueue. This is how jME adds data tothe
     * render queue. As a user, in 99% of casees you'll want to use the function
     * Spatail.setRenderQueueMode and let jME add the item to the queue itself.
     * 
     * @param s
     *            Spatial to add.
     * @param bucket
     *            A bucket type to add to.
     * @see com.jme.scene.Spatial#setRenderQueueMode(int)
     * @see com.jme.renderer.Renderer#QUEUE_OPAQUE
     * @see com.jme.renderer.Renderer#QUEUE_ORTHO
     * @see com.jme.renderer.Renderer#QUEUE_TRANSPARENT
     */
    public void addToQueue(Spatial s, int bucket) {
        switch (bucket) {
        case Renderer.QUEUE_OPAQUE:
            opaqueBucket.add(s);
            break;
        case Renderer.QUEUE_TRANSPARENT:
            transparentBucket.add(s);
            break;
        case Renderer.QUEUE_ORTHO:
            orthoBucket.add(s);
            break;
        default:
            throw new JmeException("Illegal Render queue order of " + bucket);
        }
    }

    /**
     * Calculates the distance from a spatial to the camera. Distance is a
     * squared distance.
     * 
     * @param spat
     *            Spatial to distancize.
     * @return Distance from Spatial to camera.
     */
    private float distanceToCam(Spatial spat) {
        if (spat.queueDistance != Float.NEGATIVE_INFINITY)
                return spat.queueDistance;
        Camera cam = renderer.getCamera();
        spat.queueDistance = 0;
        
        Vector3f camPosition = cam.getLocation();
        Vector3f spatPosition = null;
        Vector3f viewVector = cam.getDirection();
        
        if (Vector3f.isValidVector(cam.getLocation())) {
            if (spat.getWorldBound() != null && Vector3f.isValidVector(spat.getWorldBound().getCenter()))
                spatPosition = spat.getWorldBound().getCenter();
            else if (spat instanceof Spatial && Vector3f.isValidVector(((Spatial)spat).getWorldTranslation()))
                spatPosition = ((Spatial) spat).getWorldTranslation();
        }

        if (spatPosition != null) {
            spatPosition.subtract(camPosition, tempVector);

            float retval = Math.abs(tempVector.dot(viewVector)
                    / viewVector.dot(viewVector));
            tempVector = viewVector.mult(retval, tempVector);

            spat.queueDistance = tempVector.length();
        }

        return spat.queueDistance;
    }

    /**
     * clears all of the buckets.
     */
    public void clearBuckets() {
        transparentBucket.clear();
        opaqueBucket.clear();
        orthoBucket.clear();
    }

    /**
     * swaps all of the buckets with the back buckets.
     */
    public void swapBuckets() {
        SpatialList swap = transparentBucket;
        transparentBucket = transparentBackBucket;
        transparentBackBucket = swap;

        swap = orthoBucket;
        orthoBucket = orthoBackBucket;
        orthoBackBucket = swap;

        swap = opaqueBucket;
        opaqueBucket = opaqueBackBucket;
        opaqueBackBucket = swap;
    }

    /**
     * Renders the opaque, clone, transparent, and ortho buckets in that order.
     */
    public void renderBuckets() {
        renderOpaqueBucket();
        renderTransparentBucket();
        renderOrthoBucket();
    }

    /**
     * Renders the opaque buckets. Those closest to the camera are rendered
     * first.
     */
    private void renderOpaqueBucket() {
        opaqueBucket.sort();        
        for (int i = 0; i < opaqueBucket.listSize; i++) {
            opaqueBucket.list[i].draw(renderer);
        }
        opaqueBucket.clear();
    }

    /**
     * Renders the transparent buckets. Those farthest from the camera are
     * rendered first. Note that any items in the transparent bucket will
     * have their cullstate values overridden. Therefore, any settings assigned
     * to the cullstate of the rendered object will not be used.
     */
    private void renderTransparentBucket() {
        transparentBucket.sort();
            for (int i = 0; i < transparentBucket.listSize; i++) {
                Spatial obj = transparentBucket.list[i]; 

                if (twoPassTransparent
                    && obj instanceof Geometry) {
                    Geometry geom = (Geometry)obj;
                    RenderState oldCullState = geom.states[RenderState.StateType.Cull.ordinal()];
                    geom.states[RenderState.StateType.Cull.ordinal()] = tranCull;
                    ZBufferState oldZState = (ZBufferState)geom.states[RenderState.StateType.ZBuffer.ordinal()];
                    geom.states[RenderState.StateType.ZBuffer.ordinal()] = tranZBuff;

                    // first render back-facing tris only
                    tranCull.setCullFace(CullState.Face.Front);
                    obj.draw(renderer);
                    
                    // then render front-facing tris only
                    geom.states[RenderState.StateType.ZBuffer.ordinal()] = oldZState;
                    tranCull.setCullFace(CullState.Face.Back);
                    obj.draw(renderer);
                    geom.states[RenderState.StateType.Cull.ordinal()] = oldCullState;
                } else {
                    // draw as usual
                    obj.draw(renderer);
                }
                obj.queueDistance = Float.NEGATIVE_INFINITY;
            }
        transparentBucket.clear();
    }

    /**
     * Renders the ortho buckets. Those will the highest ZOrder are rendered
     * first.
     */
    private void renderOrthoBucket() {
        orthoBucket.sort();
        if (orthoBucket.listSize > 0) {
            renderer.setOrtho();
            for (int i = 0; i < orthoBucket.listSize; i++) {
                orthoBucket.list[i].draw(renderer);
            }
            renderer.unsetOrtho();
        }
        orthoBucket.clear();
    }

    /**
     * This class is a special function list of Spatial objects for render
     * queueing.
     * 
     * @author Jack Lindamood
     * @author Three Rings - better sorting alg.
     */
    private class SpatialList {

        Spatial[] list, tlist;

        int listSize;

        private static final int DEFAULT_SIZE = 32;

        private Comparator<Spatial> c;

        SpatialList(Comparator<Spatial> c) {
            listSize = 0;
            list = new Spatial[DEFAULT_SIZE];
            this.c = c;
        }

        /**
         * Adds a spatial to the list. List size is doubled if there is no room.
         * 
         * @param s
         *            The spatial to add.
         */
        void add(Spatial s) {
            if (listSize == list.length) {
                Spatial[] temp = new Spatial[listSize * 2];
                System.arraycopy(list, 0, temp, 0, listSize);
                list = temp;
            }
            list[listSize++] = s;
        }

        /**
         * Resets list size to 0.
         */
        void clear() {
            for (int i = 0; i < listSize; i++)
                list[i] = null;
            if (tlist != null)
                Arrays.fill(tlist, null);
            listSize = 0;
        }

        /**
         * Sorts the elements in the list acording to their Comparator.
         */
        void sort() {
            if (listSize > 1) {
                // resize or populate our temporary array as necessary
                if (tlist == null || tlist.length != list.length) {
                    tlist = list.clone();
                } else {
                    System.arraycopy(list, 0, tlist, 0, list.length);
                }
                // now merge sort tlist into list
                SortUtil.msort(tlist, list, 0, listSize, c);
            }
        }
    }

    private class OpaqueComp implements Comparator<Spatial> {

        public int compare(Spatial o1, Spatial o2) {
            if (o1 instanceof Geometry && o2 instanceof Geometry) {
                return compareByStates((Geometry) o1, (Geometry) o2);
            }
            
            float d1 = distanceToCam(o1);
            float d2 = distanceToCam(o2);
            if (d1 == d2)
                return 0;
            else if (d1 < d2)
                return -1;
            else
                return 1;           
        }

        /**
         * Compare opaque items by their texture states - generally the most
         * expensive switch. Later this might expand to comparisons by other
         * states as well, such as lighting or material.
         */
        private int compareByStates(Geometry g1, Geometry g2) {
            TextureState ts1 = (TextureState)g1.states[RenderState.StateType.Texture.ordinal()];
            TextureState ts2 = (TextureState)g2.states[RenderState.StateType.Texture.ordinal()];
            if (ts1 == ts2) return 0;
            else if (ts1 == null && ts2 != null) return -1;
            else if (ts2 == null && ts1 != null) return  1;
                        
            for (int x = 0, nots = Math.min(ts1.getNumberOfSetTextures(), ts2.getNumberOfSetTextures()); x < nots; x++) {
                
                int tid1 = ts1.getTextureID(x);
                int tid2 = ts2.getTextureID(x);                
                if (tid1 == tid2)
                    continue;
                else if (tid1 < tid2)
                    return -1;
                else
                    return 1;
            }

            if (ts1.getNumberOfSetTextures() != ts2.getNumberOfSetTextures()) {
                return ts2.getNumberOfSetTextures() - ts1.getNumberOfSetTextures();
            }

            return 0;
        }
    }

    private class TransparentComp implements Comparator<Spatial> {

        public int compare(Spatial o1, Spatial o2) {
            float d1 = distanceToCam(o1);
            float d2 = distanceToCam(o2);
            if (d1 == d2)
                return 0;
            else if (d1 < d2)
                return 1;
            else
                return -1;
        }
    }

    private class OrthoComp implements Comparator<Spatial> {
        public int compare(Spatial o1, Spatial o2) {
            if (o2.getZOrder() == o1.getZOrder()) {
                return 0;
            } else if (o2.getZOrder() < o1.getZOrder()) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}

