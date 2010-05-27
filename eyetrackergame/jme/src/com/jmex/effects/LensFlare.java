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

package com.jmex.effects;

import java.io.IOException;
import java.util.ArrayList;

import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>LensFlare</code> Lens flare effect for jME. Notice that currently, it
 * doesn't do occlusion culling.
 * 
 * The easiest way to use this class is to use the LensFlareFactory to create
 * your LensFlare and then attach it as a child to a lightnode. Optionally you
 * can make it a child or a sibling of an object you wish to have a 'glint' on.
 * In the case of sibling, use
 * setLocalTranslation(sibling.getLocalTranslation()) or something similar to
 * ensure position.
 * 
 * Only FlareQuad objects are acceptable as children.
 * 
 * @author Joshua Slack
 * @version $Id: LensFlare.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */

public class LensFlare extends Node {
    private static final long serialVersionUID = 1L;

    private Vector2f midPoint;

    private Vector3f flarePoint;

    private Vector3f scale = new Vector3f(1, 1, 1);
    
    private boolean triangleOcclusion = false;

    private Ray pickRay = new Ray();

    private float maxNotOccludedOffset;

    private float minNotOccludedOffset;

    private Ray secondRay = new Ray();

    private Vector2f secondScreenPos = new Vector2f();

    private Vector3f flaresWorldAxis = new Vector3f();

    private Vector2f screenPos = new Vector2f();

    private ArrayList<Integer> pickTriangles = new ArrayList<Integer>();

    private ArrayList<Geometry> pickBoundsGeoms = new ArrayList<Geometry>();

    private ArrayList<Geometry> occludingTriMeshes = new ArrayList<Geometry>();

    public LensFlare() {} 
    
    /**
     * Creates a new LensFlare node without FlareQuad children. Use attachChild
     * to attach FlareQuads.
     * 
     * @param name
     *            The name of the node.
     */
    public LensFlare(String name) {
        super(name);
        init();
    }

    /**
     * Init basic params of Lensflare...
     */
    private void init() {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        midPoint = new Vector2f(display.getWidth() >> 1,
                display.getHeight() >> 1);

        // Set the renderstates for lensflare to all defaults...
        for (int i = 0; i < RenderState.StateType.values().length; i++) {
            setRenderState(Renderer.defaultStateList[i]);
        }

        // Set a alpha blending state.
        BlendState as1 = display.getRenderer().createBlendState();
        as1.setBlendEnabled(true);
        as1.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as1.setDestinationFunction(BlendState.DestinationFunction.One);
        as1.setTestEnabled(true);
        as1.setTestFunction(BlendState.TestFunction.GreaterThan);
        as1.setEnabled(true);
        setRenderState(as1);

        setRenderQueueMode(Renderer.QUEUE_ORTHO);
        setLightCombineMode(Spatial.LightCombineMode.Off);
        setTextureCombineMode(TextureCombineMode.Replace);
    }

    /**
     * Get the flare's reference midpoint, usually the center of the screen.
     * 
     * @return Vector2f
     */
    public Vector2f getMidPoint() {
        return midPoint;
    }

    /**
     * Set the flare's reference midpoint, the center of the screen by default.
     * It may be useful to change this if the whole screen is not used for a
     * scene (for example, if part of the screen is taken up by a status bar.)
     * 
     * @param midPoint
     *            Vector2f
     */
    public void setMidPoint(Vector2f midPoint) {
        this.midPoint = midPoint;
    }

    /**
     * Query intensity of the flares.
     * 
     * @return current value of field intensity
     * @see #setIntensity(float)
     */
    public float getIntensity() {
        return this.intensity;
    }

    /**
     * store the value for field intensity.
     */
    private float intensity = 1;

    /**
     * Set intensity of the flare. Intensity 0 means flares are not visible, 1
     * means maximum size and opacity.
     * 
     * @param value
     *            new value between 0 and 1
     */
    public void setIntensity(final float value) {
        if (value > 1) {
            this.intensity = 1;
        } else if (value < 0) {
            this.intensity = 0;
        } else {
            this.intensity = value;
        }
    }

    /**
     * <code>onDraw</code> checks the node with the camera to see if it should
     * be culled, if not, the node's draw method is called.
     * 
     * @param r
     *            the renderer used for display.
     */
    public void onDraw(Renderer r) {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        midPoint.set(r.getWidth() >> 1, r.getHeight() >> 1);
        // Locate light src on screen x,y
        flarePoint = display.getScreenCoordinates(worldTranslation, flarePoint)
                .subtractLocal(midPoint.x, midPoint.y, 0);
        if (flarePoint.z >= 1.0f) { // if it's behind us
            setCullHint(Spatial.CullHint.Always);
            return;
        } 
            
        setCullHint(Spatial.CullHint.Dynamic);
        // define a line from light src to one opposite across the center point
        // draw main flare at src point

        super.onDraw(r);
    }

    /**
     * <code>draw</code> calls the onDraw method for each child maintained by
     * this node.
     * 
     * @param r
     *            the renderer to draw to.
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     */
    public void draw(Renderer r) {
        DisplaySystem display = DisplaySystem.getDisplaySystem();

        if (rootNode != null) {
            pickResults.clear();
            Vector3f origin = pickRay.getOrigin();
            screenPos.set(flarePoint.x + midPoint.x, flarePoint.y + midPoint.y);
            display.getWorldCoordinates(screenPos, 0, origin); // todo:
                                                                // neccessary?!
            pickRay.getDirection().set(getWorldTranslation()).subtractLocal(
                    origin).normalizeLocal();
            pickBoundsGeoms.clear();
            rootNode.findPick(pickRay, pickResults);
            this.setIntensity(1);
            occludingTriMeshes.clear();
            for (int i = pickBoundsGeoms.size() - 1; i >= 0; i--) {
                Geometry mesh = pickBoundsGeoms.get(i);
                // If we're not colocated with the LF origin, and not a skybox, and not transparent
                // we might block this LF.
                if (!mesh.getWorldTranslation().equals(
                        this.getWorldTranslation())
                        && (!(mesh.getParent() instanceof Skybox))
                        && mesh.getRenderQueueMode() != Renderer.QUEUE_TRANSPARENT) {

                    if (useTriangleAccurateOcclusion() && mesh instanceof TriMesh) {
                        occludingTriMeshes.add(mesh);
                    } else { // XXX: escape out if this is not a triangle mesh...  probably should be handled differently
                        this.setIntensity(0);
                        break;
                    }
                }
            }
            
            if (occludingTriMeshes.size() > 0 && getIntensity() > 0) {
                checkRealOcclusion();
            }
        }

        // irrisor: compensate for different size renderer
        float intensity = getIntensity();
        if (intensity == 0) return; // renanse: don't draw

        if (display.getWidth() != r.getWidth()
                || display.getHeight() != r.getHeight()) {
            float factorX = (float) display.getWidth() / r.getWidth();
            flarePoint.x *= factorX;
            float factorY = (float) display.getHeight() / r.getHeight();
            flarePoint.y *= factorY;
            midPoint.x *= factorX;
            midPoint.y *= factorY;
            scale.x = intensity;
            scale.y = intensity * factorY / factorX;
            scale.z = intensity;
        } else {
            scale.x = intensity;
            scale.y = intensity;
            scale.z = intensity;
        }


        for (int x = getQuantity(); --x >= 0;) {
            FlareQuad fq = (FlareQuad) getChild(x);
            fq.setLocalScale(scale);
            fq.updatePosition(flarePoint, midPoint);
        }

        super.draw(r);
    }

    private void checkRealOcclusion() {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        secondRay.direction.set(pickRay.direction);

        float flareDistanceFromMidPoint = flarePoint.length();
        secondScreenPos.x = screenPos.x + flarePoint.x
                / flareDistanceFromMidPoint;
        secondScreenPos.y = screenPos.y + flarePoint.y
                / flareDistanceFromMidPoint;
        display.getWorldCoordinates(secondScreenPos, 0, flaresWorldAxis);
        flaresWorldAxis.subtractLocal(pickRay.origin).normalizeLocal()
                .multLocal(0.01f);

        final int radius = 15;
        secondRay.origin.set(flaresWorldAxis).multLocal(-radius).addLocal(
                pickRay.origin);
        maxNotOccludedOffset = -radius;
        minNotOccludedOffset = -radius;
        while (isRayOccluded(secondRay) && (maxNotOccludedOffset < radius)) {
            secondRay.origin.addLocal(flaresWorldAxis);
            minNotOccludedOffset += 1;
            maxNotOccludedOffset += 1;
        }
        if (maxNotOccludedOffset < radius) {
            do {
                secondRay.origin.addLocal(flaresWorldAxis);
                maxNotOccludedOffset += 1;
            } while (!isRayOccluded(secondRay)
                    && (maxNotOccludedOffset < radius));
        }

        setIntensity(Math.abs(maxNotOccludedOffset - minNotOccludedOffset)
                / (radius));
    }

    private boolean isRayOccluded(Ray ray) {
        pickTriangles.clear();
        for (int i = occludingTriMeshes.size() - 1; i >= 0; i--) {
            TriMesh triMesh = (TriMesh) occludingTriMeshes.get(i);
        	triMesh.findTrianglePick(ray, pickTriangles);
            if (pickTriangles.size() > 0) {
                return true;
            } 
                
            // fine - not occluded by this one            
        }
        return false;
    }

    /**
     * Calls Node's attachChild after ensuring child is a FlareQuad.
     * 
     * @see com.jme.scene.Node#attachChild(Spatial)
     * @param spat
     *            Spatial
     * @return int
     */
    public int attachChild(Spatial spat) {
        if (!(spat instanceof FlareQuad))
            throw new JmeException(
                    "Only children of type FlareQuad may be attached to LensFlare.");
        return super.attachChild(spat);
    }

    /**
     * getter for field rootNode
     * 
     * @return current value of field rootNode
     */
    public Node getRootNode() {
        return this.rootNode;
    }

    /**
     * store the value for field rootNode
     */
    private Node rootNode;

    /**
     * setter for field rootNode
     * 
     * @param value
     *            new value
     */
    public void setRootNode(final Node value) {
        final Node oldValue = this.rootNode;
        if (oldValue != value) {
            this.rootNode = value;
        }
    }

    // optimize memory allocation:
    private PickResults pickResults = new BoundingPickResults() {
        public void addPick(Ray ray, Geometry s) {
            pickBoundsGeoms.add(s);
        }
    };

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(midPoint, "midPoint", new Vector2f());
        capsule.write(flarePoint, "flarePoint", Vector3f.ZERO);
        capsule.write(scale, "scale", Vector3f.UNIT_XYZ);
        capsule.write(intensity, "intensity", 1);
        
        capsule.write(rootNode, "rootNode", null);
        
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        midPoint = (Vector2f)capsule.readSavable("midPoint", new Vector2f());
        flarePoint = (Vector3f)capsule.readSavable("flarePoint", Vector3f.ZERO.clone());
        scale = (Vector3f)capsule.readSavable("scale", Vector3f.UNIT_XYZ.clone());
        intensity = capsule.readFloat("intensity", 1);
        
        rootNode = (Node)capsule.readSavable("rootNode", null);
    }
    
    /**
	 * @return true if additional triangle accurate lens flare occlusion checks
	 *         should done for this lens flare. (Useful for terrain, etc.)
	 */
    public boolean useTriangleAccurateOcclusion() {
    		return triangleOcclusion;
    }
    
    public void setTriangleAccurateOcclusion(boolean use) {
    		triangleOcclusion = use;
    }
}
