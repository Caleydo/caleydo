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

package com.jme.util.geom;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.animation.SkinNode;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingCapsule;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.AxisRods;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Capsule;
import com.jme.scene.shape.OrientedBox;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;

/**
 * <code>Debugger</code> provides tools for viewing scene data such as
 * boundings and normals.
 * 
 * @author Joshua Slack
 * @author Emond Papegaaij (normals ideas and previous normal tool)
 * @version $Id: Debugger.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public final class Debugger {

    // -- **** METHODS FOR DRAWING BOUNDING VOLUMES **** -- //

    private static final Sphere boundingSphere = new Sphere("bsphere", 10, 10, 1);
    private static final Box boundingBox = new Box("bbox", new Vector3f(), 1, 1, 1);
    private static final OrientedBox boundingOB = new OrientedBox("bobox");
    private static final Capsule boundingCapsule = new Capsule("bcap", 3, 10, 10, 1, 1);

    static {
        boundingSphere.setRenderQueueMode(Renderer.QUEUE_SKIP);
        boundingBox.setRenderQueueMode(Renderer.QUEUE_SKIP);
        boundingOB.setRenderQueueMode(Renderer.QUEUE_SKIP);
        boundingCapsule.setRenderQueueMode(Renderer.QUEUE_SKIP);
    }

    private static WireframeState boundsWireState;
    private static ZBufferState boundsZState;

    /**
     * <code>drawBounds</code> draws the bounding volume for a given Spatial
     * and its children.
     * 
     * @param se
     *            the Spatial to draw boundings for.
     * @param r
     *            the Renderer to use to draw the bounding.
     */
    public static void drawBounds(Spatial se, Renderer r) {
        drawBounds(se, r, true);
    }

    /**
     * <code>drawBounds</code> draws the bounding volume for a given Spatial
     * and optionally its children.
     * 
     * @param se
     *            the Spatial to draw boundings for.
     * @param r
     *            the Renderer to use to draw the bounding.
     * @param doChildren
     *            if true, boundings for any children will also be drawn
     */
    public static void drawBounds(Spatial se, Renderer r,
            boolean doChildren) {
        if (se == null)
            return;

        if (boundsWireState == null) {
            boundsWireState = r.createWireframeState();
            boundsZState = r.createZBufferState();
            boundingBox.setRenderState(boundsWireState);
            boundingBox.setRenderState(boundsZState);
            boundingBox.updateRenderState();
            boundingOB.setRenderState(boundsWireState);
            boundingOB.setRenderState(boundsZState);
            boundingOB.updateRenderState();
            boundingSphere.setRenderState(boundsWireState);
            boundingSphere.setRenderState(boundsZState);
            boundingSphere.updateRenderState();
            boundingCapsule.setRenderState(boundsWireState);
            boundingCapsule.setRenderState(boundsZState);
            boundingCapsule.updateRenderState();
        }

        if (se.getWorldBound() != null
                && se.getCullHint() != Spatial.CullHint.Always) {
            int state = r.getCamera().getPlaneState();
            if (r.getCamera().contains(se.getWorldBound()) != Camera.FrustumIntersect.Outside)
                drawBounds(se.getWorldBound(), r);
            else
                doChildren = false;
            r.getCamera().setPlaneState(state);
        }
        if (doChildren && se instanceof Node) {
            Node n = (Node) se;
            if (n.getChildren() != null) {
                for (int i = n.getChildren().size(); --i >= 0;)
                    drawBounds(n.getChild(i), r, true);
            }
        }
    }

    private static void drawBounds(BoundingVolume bv, Renderer r) {

        switch (bv.getType()) {
            case AABB:
                drawBoundingBox((BoundingBox) bv, r);
                break;
            case Sphere:
                drawBoundingSphere((BoundingSphere) bv, r);
                break;
            case OBB:
                drawOBB((OrientedBoundingBox) bv, r);
                break;
            case Capsule:
                drawBoundingCapsule((BoundingCapsule) bv, r);
                break;
            default:
                break;
        }
    }

    public static void setBoundsColor(ColorRGBA color) {
        boundingBox.setSolidColor(color);
        boundingOB.setSolidColor(color);
        boundingCapsule.setSolidColor(color);
        boundingSphere.setSolidColor(color);
    }

    private static void drawBoundingSphere(BoundingSphere sphere, Renderer r) {
        boundingSphere.getCenter().set(sphere.getCenter());
        boundingSphere.updateGeometry(boundingSphere.getCenter(), 10, 10, sphere
                .getRadius()); // pass back bs center to prevent accidently
        // data access.
        boundingSphere.draw(r);
    }

    private static void drawBoundingBox(BoundingBox box, Renderer r) {
        boundingBox.getCenter().set(box.getCenter());
        boundingBox.updateGeometry(boundingBox.getCenter(),
                box.xExtent,
                box.yExtent,
                box.zExtent);
        boundingBox.draw(r);
    }

    private static void drawOBB(OrientedBoundingBox box, Renderer r) {
        boundingOB.getCenter().set(box.getCenter());
        boundingOB.getXAxis().set(box.getXAxis());
        boundingOB.getYAxis().set(box.getYAxis());
        boundingOB.getZAxis().set(box.getZAxis());
        boundingOB.getExtent().set(box.getExtent());
        boundingOB.updateGeometry();
        boundingOB.draw(r);
    }

    private static final Vector3f start = new Vector3f();
    private static final Vector3f end = new Vector3f();

    private static void drawBoundingCapsule(BoundingCapsule cap, Renderer r) {
        boundingCapsule.updateGeometry(
                cap.getLineSegment().getNegativeEnd(start),
                cap.getLineSegment().getPositiveEnd(end),
                cap.getRadius());
        boundingCapsule.draw(r);
    }

    // -- **** METHODS FOR DRAWING NORMALS **** -- //

    private static final Line normalLines = new Line("normLine");
    static {
        normalLines.setLineWidth(3.0f);
        normalLines.setMode(Line.Mode.Segments);
        normalLines.setVertexBuffer(BufferUtils.createVector3Buffer(500));
        normalLines.setColorBuffer(BufferUtils.createColorBuffer(500));
    }
    private static final Vector3f _normalVect = new Vector3f();
    private static ZBufferState normZState;
    public static ColorRGBA NORMAL_COLOR_BASE = ColorRGBA.red.clone();
    public static ColorRGBA NORMAL_COLOR_TIP = ColorRGBA.pink.clone();
    public static ColorRGBA TANGENT_COLOR_BASE = ColorRGBA.red.clone();
    public static BoundingBox measureBox = new BoundingBox();
    public static float AUTO_NORMAL_RATIO = .05f;

    /**
     * <code>drawNormals</code> draws lines representing normals for a given
     * Spatial and its children.
     * 
     * @param element
     *            the Spatial to draw normals for.
     * @param r
     *            the Renderer to use to draw the normals.
     */
    public static void drawNormals(Spatial element, Renderer r) {
        drawNormals(element, r, -1f, true);
    }

    public static void drawTangents(Spatial element, Renderer r) {
        drawTangents(element, r, -1f, true);
    }

    /**
     * <code>drawNormals</code> draws the normals for a given Spatial and
     * optionally its children.
     * 
     * @param element
     *            the Spatial to draw normals for.
     * @param r
     *            the Renderer to use to draw the normals.
     * @param size
     *            the length of the drawn normal (default is -1.0f which means
     *            autocalc based on boundings - if any).
     * @param doChildren
     *            if true, normals for any children will also be drawn
     */
    public static void drawNormals(Spatial element, Renderer r,
            float size, boolean doChildren) {
        if (element == null)
            return;

        if (normZState == null) {
            normZState = r.createZBufferState();
            normalLines.setRenderState(normZState);
            normalLines.updateRenderState();
        }

        int state = r.getCamera().getPlaneState();
        if (element.getWorldBound() != null
                && r.getCamera().contains(element.getWorldBound()) == Camera.FrustumIntersect.Outside) {
            r.getCamera().setPlaneState(state);
            return;
        }
        r.getCamera().setPlaneState(state);
        if (element instanceof Geometry
                && element.getCullHint() != Spatial.CullHint.Always) {
            Geometry geom = (Geometry) element;

            float rSize = size;
            if (rSize == -1) {
                BoundingVolume vol = element.getWorldBound();
                if (vol != null) {
                    measureBox.setCenter(vol.getCenter());
                    measureBox.xExtent = 0;
                    measureBox.yExtent = 0;
                    measureBox.zExtent = 0;
                    measureBox.mergeLocal(vol);
                    rSize = AUTO_NORMAL_RATIO
                            * ((measureBox.xExtent + measureBox.yExtent + measureBox.zExtent) / 3f);
                } else
                    rSize = 1.0f;
            }

            FloatBuffer norms = geom.getNormalBuffer();
            FloatBuffer verts = geom.getVertexBuffer();
            if (norms != null && verts != null
                    && norms.limit() == verts.limit()) {
                FloatBuffer lineVerts = normalLines.getVertexBuffer();
                if (lineVerts.capacity() < (3 * (2 * geom.getVertexCount()))) {
                    normalLines.setVertexBuffer(null);
                    System.gc();
                    lineVerts = BufferUtils.createVector3Buffer(geom
                            .getVertexCount() * 2);
                    normalLines.setVertexBuffer(lineVerts);
                } else {
                    normalLines.setVertexCount(2 * geom.getVertexCount());
                    lineVerts.clear();
                }

                FloatBuffer lineColors = normalLines.getColorBuffer();
                if (lineColors.capacity() < (4 * (2 * geom.getVertexCount()))) {
                    normalLines.setColorBuffer(null);
                    System.gc();
                    lineColors = BufferUtils.createColorBuffer(geom
                            .getVertexCount() * 2);
                    normalLines.setColorBuffer(lineColors);
                } else {
                    lineColors.clear();
                }

                IntBuffer lineInds = normalLines.getIndexBuffer();
                if (lineInds == null
                        || lineInds.capacity() < (normalLines.getVertexCount())) {
                    normalLines.setIndexBuffer(null);
                    System.gc();
                    lineInds = BufferUtils.createIntBuffer(geom
                            .getVertexCount() * 2);
                    normalLines.setIndexBuffer(lineInds);
                } else {
                    lineInds.clear();
                    lineInds.limit(normalLines.getVertexCount());
                }

                verts.rewind();
                norms.rewind();
                lineVerts.rewind();
                lineInds.rewind();

                for (int x = 0; x < geom.getVertexCount(); x++) {
                    _normalVect.set(verts.get(), verts.get(), verts.get());
                    _normalVect.multLocal(geom.getWorldScale());
                    lineVerts.put(_normalVect.x);
                    lineVerts.put(_normalVect.y);
                    lineVerts.put(_normalVect.z);

                    lineColors.put(NORMAL_COLOR_BASE.r);
                    lineColors.put(NORMAL_COLOR_BASE.g);
                    lineColors.put(NORMAL_COLOR_BASE.b);
                    lineColors.put(NORMAL_COLOR_BASE.a);

                    lineInds.put(x * 2);

                    _normalVect.addLocal(norms.get() * rSize, norms.get()
                            * rSize, norms.get() * rSize);
                    lineVerts.put(_normalVect.x);
                    lineVerts.put(_normalVect.y);
                    lineVerts.put(_normalVect.z);

                    lineColors.put(NORMAL_COLOR_TIP.r);
                    lineColors.put(NORMAL_COLOR_TIP.g);
                    lineColors.put(NORMAL_COLOR_TIP.b);
                    lineColors.put(NORMAL_COLOR_TIP.a);

                    lineInds.put((x * 2) + 1);
                }

                normalLines.setLocalTranslation(geom.getWorldTranslation());
                normalLines.setLocalRotation(geom.getWorldRotation());
                normalLines.onDraw(r);
            }

        }

        if (doChildren && element instanceof Node) {
            Node n = (Node) element;
            if (n.getChildren() != null) {
                for (int i = n.getChildren().size(); --i >= 0;)
                    drawNormals(n.getChild(i), r, size, true);
            }
        }
    }

    public static void drawTangents(Spatial element, Renderer r,
            float size, boolean doChildren) {
        if (element == null)
            return;

        if (normZState == null) {
            normZState = r.createZBufferState();
            normalLines.setRenderState(normZState);
            normalLines.updateRenderState();
        }

        int state = r.getCamera().getPlaneState();
        if (element.getWorldBound() != null
                && r.getCamera().contains(element.getWorldBound()) == Camera.FrustumIntersect.Outside) {
            r.getCamera().setPlaneState(state);
            return;
        }
        r.getCamera().setPlaneState(state);
        if (element instanceof Geometry
                && element.getCullHint() != Spatial.CullHint.Always) {
            Geometry geom = (Geometry) element;

            float rSize = size;
            if (rSize == -1) {
                BoundingVolume vol = element.getWorldBound();
                if (vol != null) {
                    measureBox.setCenter(vol.getCenter());
                    measureBox.xExtent = 0;
                    measureBox.yExtent = 0;
                    measureBox.zExtent = 0;
                    measureBox.mergeLocal(vol);
                    rSize = AUTO_NORMAL_RATIO
                            * ((measureBox.xExtent + measureBox.yExtent + measureBox.zExtent) / 3f);
                } else
                    rSize = 1.0f;
            }

            FloatBuffer norms = geom.getTangentBuffer();
            FloatBuffer verts = geom.getVertexBuffer();
            if (norms != null && verts != null
                    && norms.limit() == verts.limit()) {
                FloatBuffer lineVerts = normalLines.getVertexBuffer();
                if (lineVerts.capacity() < (3 * (2 * geom.getVertexCount()))) {
                    normalLines.setVertexBuffer(null);
                    System.gc();
                    lineVerts = BufferUtils.createVector3Buffer(geom
                            .getVertexCount() * 2);
                    normalLines.setVertexBuffer(lineVerts);
                } else {
                    normalLines.setVertexCount(2 * geom.getVertexCount());
                    lineVerts.clear();
                }

                FloatBuffer lineColors = normalLines.getColorBuffer();
                if (lineColors.capacity() < (4 * (2 * geom.getVertexCount()))) {
                    normalLines.setColorBuffer(null);
                    System.gc();
                    lineColors = BufferUtils.createColorBuffer(geom
                            .getVertexCount() * 2);
                    normalLines.setColorBuffer(lineColors);
                } else {
                    lineColors.clear();
                }

                IntBuffer lineInds = normalLines.getIndexBuffer();
                if (lineInds == null
                        || lineInds.capacity() < (normalLines.getVertexCount())) {
                    normalLines.setIndexBuffer(null);
                    System.gc();
                    lineInds = BufferUtils.createIntBuffer(geom
                            .getVertexCount() * 2);
                    normalLines.setIndexBuffer(lineInds);
                } else {
                    lineInds.clear();
                    lineInds.limit(normalLines.getVertexCount());
                }

                verts.rewind();
                norms.rewind();
                lineVerts.rewind();
                lineInds.rewind();

                for (int x = 0; x < geom.getVertexCount(); x++) {
                    _normalVect.set(verts.get(), verts.get(), verts.get());
                    _normalVect.multLocal(geom.getWorldScale());
                    lineVerts.put(_normalVect.x);
                    lineVerts.put(_normalVect.y);
                    lineVerts.put(_normalVect.z);

                    lineColors.put(TANGENT_COLOR_BASE.r);
                    lineColors.put(TANGENT_COLOR_BASE.g);
                    lineColors.put(TANGENT_COLOR_BASE.b);
                    lineColors.put(TANGENT_COLOR_BASE.a);

                    lineInds.put(x * 2);

                    _normalVect.addLocal(norms.get() * rSize, norms.get()
                            * rSize, norms.get() * rSize);
                    lineVerts.put(_normalVect.x);
                    lineVerts.put(_normalVect.y);
                    lineVerts.put(_normalVect.z);

                    lineColors.put(TANGENT_COLOR_BASE.r);
                    lineColors.put(TANGENT_COLOR_BASE.g);
                    lineColors.put(TANGENT_COLOR_BASE.b);
                    lineColors.put(TANGENT_COLOR_BASE.a);

                    lineInds.put((x * 2) + 1);
                }

                if (geom != null) {
                    normalLines.setLocalTranslation(geom.getWorldTranslation());
                    normalLines.setLocalRotation(geom.getWorldRotation());
                    normalLines.onDraw(r);
                }
            }

        }

        if (doChildren && element instanceof Node) {
            Node n = (Node) element;
            if (n.getChildren() != null) {
                for (int i = n.getChildren().size(); --i >= 0;)
                    drawTangents(n.getChild(i), r, size, true);
            }
        }
    }

    // -- **** METHODS FOR DRAWING AXIS **** -- //

    private static final AxisRods rods = new AxisRods("debug_rods", true, 1);
    static {
        rods.setRenderQueueMode(Renderer.QUEUE_SKIP);
    }
    private static boolean axisInited = false;

    public static void drawAxis(Spatial spat, Renderer r) {
        drawAxis(spat, r, true, false);
    }

    public static void drawAxis(Spatial spat, Renderer r, boolean drawChildren, boolean drawAll) {
        if (!axisInited) {
            BlendState blendState = r.createBlendState();
            blendState.setBlendEnabled(true);
            blendState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
            blendState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
            rods.setRenderState(blendState);
            rods.updateRenderState();
            rods.updateGeometricState(0, false);
            axisInited = true;
        }

        if (drawAll
                || (spat instanceof Geometry && !(spat.getParent() instanceof SkinNode))
                || (spat instanceof SkinNode)) {
            if (spat.getWorldBound() != null) {
                float rSize;
                BoundingVolume vol = spat.getWorldBound(); 
                if (vol != null) {
                    measureBox.setCenter(vol.getCenter());
                    measureBox.xExtent = 0;
                    measureBox.yExtent = 0;
                    measureBox.zExtent = 0;
                    measureBox.mergeLocal(vol);
                    rSize = 1f * ((measureBox.xExtent + measureBox.yExtent + measureBox.zExtent) / 3f);
                } else
                    rSize = 1.0f;

                rods.getLocalTranslation().set(spat.getWorldBound().getCenter());
                rods.getLocalScale().set(rSize, rSize, rSize);
            } else {
                rods.getLocalTranslation().set(spat.getWorldTranslation());
                rods.getLocalScale().set(spat.getWorldScale());
            }
            rods.getLocalRotation().set(spat.getWorldRotation());
            rods.updateGeometricState(0, false);
    
            rods.draw(r);
        }

        if ((spat instanceof Node) && drawChildren) {
            Node n = (Node) spat;
            if (n.getChildren() == null) return;
            for (int x = 0, count = n.getChildren().size(); x < count; x++) {
                drawAxis(n.getChild(x), r, drawChildren, drawAll);
            }
        }
    }


    // -- **** METHODS FOR DISPLAYING BUFFERS **** -- //
    public static final int NORTHWEST = 0;
    public static final int NORTHEAST = 1;
    public static final int SOUTHEAST = 2;
    public static final int SOUTHWEST = 3;

    private static final Quad bQuad = new Quad("", 128, 128);
    private static Texture2D bufTexture;
    private static TextureRenderer bufTexRend;

    static {
        bQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        bQuad.setCullHint(Spatial.CullHint.Never);
    }

    public static void drawBuffer(Texture.RenderToTextureType rttSource, int location, Renderer r) {
        drawBuffer(rttSource, location, r, r.getWidth() / 6.25f);
    }

    public static void drawBuffer(Texture.RenderToTextureType rttSource, int location, Renderer r,
            float size) {
        r.flush();
        float locationX = r.getWidth(), locationY = r.getHeight();
        bQuad.resize(size, (r.getHeight() / (float) r.getWidth()) * size);
        if (bQuad.getRenderState(RenderState.StateType.Texture) == null) {
            TextureState ts = r.createTextureState();
            bufTexture = new Texture2D();
            ts.setTexture(bufTexture);
            bQuad.setRenderState(ts);
            bQuad.updateRenderState();
        }

        bufTexture.setRenderToTextureType(rttSource);

        if (bufTexRend == null) {
            bufTexRend = DisplaySystem.getDisplaySystem()
                    .createTextureRenderer(256, 256,
                            TextureRenderer.Target.Texture2D);
            bufTexRend.setupTexture(bufTexture);
        }
        int width = r.getWidth();
        if (!FastMath.isPowerOfTwo(width)) {
            int newWidth = 2;
            do {
                newWidth <<= 1;

            } while (newWidth < width);
            bQuad.getTextureCoords(0).coords.put(4, width / (float) newWidth);
            bQuad.getTextureCoords(0).coords.put(6, width / (float) newWidth);
            width = newWidth;
        }

        int height = r.getHeight();
        if (!FastMath.isPowerOfTwo(height)) {
            int newHeight = 2;
            do {
                newHeight <<= 1;

            } while (newHeight < height);
            bQuad.getTextureCoords(0).coords.put(1, height / (float) newHeight);
            bQuad.getTextureCoords(0).coords.put(7, height / (float) newHeight);
            height = newHeight;
        }

        bufTexRend.copyToTexture(bufTexture, width, height);

        float loc = size * .75f;
        switch (location) {
            case NORTHWEST:
                locationX = loc;
                locationY -= loc;
                break;
            case NORTHEAST:
                locationX -= loc;
                locationY -= loc;
                break;
            case SOUTHEAST:
                locationX -= loc;
                locationY = loc;
                break;
            case SOUTHWEST:
            default:
                locationX = loc;
                locationY = loc;
                break;
        }

        bQuad.getWorldTranslation().set(locationX, locationY, 0);

        bQuad.onDraw(r);
        r.flush();
    }
}