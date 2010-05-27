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
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.PickResults;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.state.LightState;
import com.jme.scene.state.LightUtil;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Geometry</code> defines a leaf node of the scene graph. The leaf node
 * contains the geometric data for rendering objects. It manages all rendering
 * information such as a collection of states and the data for a model.
 * Subclasses define what the model data is.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 */
public abstract class Geometry extends Spatial implements Serializable, Savable {
    private static final Logger logger = Logger.getLogger(Geometry.class.getName());

    private static final long serialVersionUID = 1;

    /** The local bounds of this Geometry object. */
    protected BoundingVolume bound;

    /** The number of vertexes in this geometry. */
    protected int vertQuantity = 0;

    /** The geometry's per vertex color information. */
    protected transient FloatBuffer colorBuf;

    /** The geometry's per vertex normal information. */
    protected transient FloatBuffer normBuf;

    /** The geometry's vertex information. */
    protected transient FloatBuffer vertBuf;

    /** The geometry's per Texture per vertex texture coordinate information. */
    protected transient ArrayList<TexCoords> texBuf;

    /** The geometry's per vertex color information. */
    protected transient FloatBuffer tangentBuf;

    /** The geometry's per vertex normal information. */
    protected transient FloatBuffer binormalBuf;

    /** The geometry's per vertex fog buffer depth values */
    protected transient FloatBuffer fogBuf;
    
    /** The geometry's VBO information. */
    protected transient VBOInfo vboInfo;

    protected boolean enabled = true;

    protected boolean castsShadows = true;

    protected boolean hasDirtyVertices = false;

    /**
     * The compiled list of renderstates for this geometry, taking into account
     * ancestors' states - updated with updateRenderStates()
     */
    public RenderState[] states = new RenderState[RenderState.StateType.values().length];

    private LightState lightState;
    
    protected ColorRGBA defaultColor = new ColorRGBA(ColorRGBA.white);

    /**
     * Non -1 values signal that drawing this scene should use the provided
     * display list instead of drawing from the buffers.
     */
    protected int displayListID = -1;

    /** Static computation field */
    protected static final Vector3f compVect = new Vector3f();

    /**
     * Empty Constructor to be used internally only.
     */
    public Geometry() {
        super();
        texBuf = new ArrayList<TexCoords>(1);
        texBuf.add(null);
    }

    /**
     * Constructor instantiates a new <code>Geometry</code> object. This is
     * the default object which has an empty vertex array. All other data is
     * null.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     */
    public Geometry(String name) {
        super(name);
        texBuf = new ArrayList<TexCoords>(1);
        texBuf.add(null);
        if (!(this instanceof SharedMesh))
            reconstruct(null, null, null, null);
    }

    /**
     * Constructor creates a new <code>Geometry</code> object. During
     * instantiation the geometry is set including vertex, normal, color and
     * texture information.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     * @param vertex
     *            the points that make up the geometry.
     * @param normal
     *            the normals of the geometry.
     * @param color
     *            the color of each point of the geometry.
     * @param coords
     *            the texture coordinates of the geometry (position 0.)
     */
    public Geometry(String name, FloatBuffer vertex, FloatBuffer normal,
            FloatBuffer color, TexCoords coords) {
        super(name);
        texBuf = new ArrayList<TexCoords>(1);
        texBuf.add(null);
        reconstruct(vertex, normal, color, coords);
    }

    /**
     * returns the number of vertices contained in this geometry.
     */
    @Override
    public int getVertexCount() {
        return vertQuantity;
    }

    public void setVertexCount(int vertQuantity) {
        this.vertQuantity = vertQuantity;
    }

    @Override
    public int getTriangleCount() {
        return 0;
    }

    /**
     * <code>reconstruct</code> reinitializes the geometry with new data. This
     * will reuse the geometry object.
     * 
     * @param vertices
     *            the new vertices to use.
     * @param normals
     *            the new normals to use.
     * @param colors
     *            the new colors to use.
     * @param coords
     *            the new texture coordinates to use (position 0).
     */
    public void reconstruct(FloatBuffer vertices, FloatBuffer normals,
            FloatBuffer colors, TexCoords coords) {
        if (vertices == null)
            setVertexCount(0);
        else
            setVertexCount(vertices.limit() / 3);

        setVertexBuffer(vertices);
        setNormalBuffer(normals);
        setColorBuffer(colors);
        if (getTextureCoords() == null) {
            setTextureCoords(new ArrayList<TexCoords>(1));
        }

        clearTextureBuffers();
        addTextureCoordinates(coords);

        if (getVBOInfo() != null)
            resizeTextureIds(1);
    }

    /**
     * Sets VBO info on this Geometry.
     * 
     * @param info
     *            the VBO info to set
     * @see VBOInfo
     */
    public void setVBOInfo(VBOInfo info) {
        vboInfo = info;
        if (vboInfo != null) {
            vboInfo.resizeTextureIds(texBuf.size());
        }
    }

    /**
     * @return VBO info object
     * @see VBOInfo
     */
    public VBOInfo getVBOInfo() {
        return vboInfo;
    }

    /**
     * <code>setSolidColor</code> sets the color array of this geometry to a
     * single color. For greater efficiency, try setting the the ColorBuffer to
     * null and using DefaultColor instead.
     * 
     * @param color
     *            the color to set.
     */
    public void setSolidColor(ColorRGBA color) {
        if (colorBuf == null)
            colorBuf = BufferUtils.createColorBuffer(vertQuantity);

        colorBuf.rewind();
        for (int x = 0, cLength = colorBuf.remaining(); x < cLength; x += 4) {
            colorBuf.put(color.r);
            colorBuf.put(color.g);
            colorBuf.put(color.b);
            colorBuf.put(color.a);
        }
        colorBuf.flip();
    }

    /**
     * Sets every color of this geometry's color array to a random color.
     */
    public void setRandomColors() {
        if (colorBuf == null)
            colorBuf = BufferUtils.createColorBuffer(vertQuantity);

        for (int x = 0, cLength = colorBuf.limit(); x < cLength; x += 4) {
            colorBuf.put(FastMath.nextRandomFloat());
            colorBuf.put(FastMath.nextRandomFloat());
            colorBuf.put(FastMath.nextRandomFloat());
            colorBuf.put(1);
        }
        colorBuf.flip();
    }

    /**
     * <code>getVertexBuffer</code> returns the float buffer that contains
     * this geometry's vertex information.
     * 
     * @return the float buffer that contains this geometry's vertex
     *         information.
     */
    public FloatBuffer getVertexBuffer() {
        return vertBuf;
    }

    /**
     * <code>setVertexBuffer</code> sets this geometry's vertices via a float
     * buffer consisting of groups of three floats: x,y and z.
     * 
     * @param vertBuf
     *            the new vertex buffer.
     */
    public void setVertexBuffer(FloatBuffer vertBuf) {
        this.vertBuf = vertBuf;
        if (vertBuf != null)
            vertQuantity = vertBuf.limit() / 3;
        else
            vertQuantity = 0;
    }

    /**
     * Set the fog coordinates buffer. This should have the vertex count entries
     * 
     * @param fogBuf The fog buffer to use in this geometry
     */
    public void setFogCoordBuffer(FloatBuffer fogBuf) {
    	this.fogBuf = fogBuf;
    }
    
    /**
     * The fog depth coord buffer
     * 
     * @return The per vertex depth values for fog coordinates
     */
    public FloatBuffer getFogBuffer() {
    	return fogBuf;
    }
    
    /**
     * <code>getNormalBuffer</code> retrieves this geometry's normal
     * information as a float buffer.
     * 
     * @return the float buffer containing the geometry information.
     */
    public FloatBuffer getNormalBuffer() {
        return normBuf;
    }

    /**
     * <code>setNormalBuffer</code> sets this geometry's normals via a float
     * buffer consisting of groups of three floats: x,y and z.
     * 
     * @param normBuf
     *            the new normal buffer.
     */
    public void setNormalBuffer(FloatBuffer normBuf) {
        this.normBuf = normBuf;
    }

    /**
     * <code>getColorBufferfer</code> retrieves the float buffer that contains
     * this geometry's color information.
     * 
     * @return the buffer that contains this geometry's color information.
     */
    public FloatBuffer getColorBuffer() {
        return colorBuf;
    }

    /**
     * <code>setColorBuffer</code> sets this geometry's colors via a float
     * buffer consisting of groups of four floats: r,g,b and a.
     * 
     * @param colorBuf
     *            the new color buffer.
     */
    public void setColorBuffer(FloatBuffer colorBuf) {
        this.colorBuf = colorBuf;
    }

    /**
     * <code>copyTextureCoords</code> copys the texture coordinates of a given
     * texture unit to another location. If the texture unit is not valid, then
     * the coordinates are ignored. Coords are multiplied by the given factor.
     * 
     * @param fromIndex
     *            the coordinates to copy.
     * @param toIndex
     *            the texture unit to set them to.
     * @param factor
     *            a multiple to apply when copying
     */
    public void copyTextureCoordinates(int fromIndex, int toIndex, float factor) {
        if (texBuf == null)
            return;

        if (fromIndex < 0 || fromIndex >= texBuf.size()
                || texBuf.get(fromIndex) == null) {
            return;
        }

        TexCoords src = texBuf.get(fromIndex);
        float[] factors = new float[src.perVert];
        for (int i = 0; i < factors.length; i++) {
			factors[i] = factor;
		}
        copyTextureCoordinates(fromIndex, toIndex, factors);

    }
    
    /**
     * <code>copyTextureCoords</code> copys the texture coordinates of a given
     * texture unit to another location. If the texture unit is not valid, then
     * the coordinates are ignored. Coords are multiplied by the given factor.
     * 
     * @param fromIndex
     *            the coordinates to copy.
     * @param toIndex
     *            the texture unit to set them to.
     * @param factor
     *            a multiple to apply when copying
     */
    public void copyTextureCoordinates(int fromIndex, int toIndex, float[] factor) {
        if (texBuf == null)
            return;

        if (fromIndex < 0 || fromIndex >= texBuf.size()
                || texBuf.get(fromIndex) == null) {
            return;
        }

        if (toIndex < 0 || toIndex == fromIndex) {
            return;
        }

        // make sure we are big enough
        while (toIndex >= texBuf.size()) {
            texBuf.add(null);
        }

        TexCoords dest = texBuf.get(toIndex);
        TexCoords src = texBuf.get(fromIndex);
        if (dest == null || dest.coords.capacity() != src.coords.limit()) {
            dest = new TexCoords(BufferUtils.createFloatBuffer(src.coords.capacity()), src.perVert);
            texBuf.set(toIndex, dest);
        }
        dest.coords.clear();
        int oldLimit = src.coords.limit();
        src.coords.clear();
        for (int i = 0, len = dest.coords.capacity(); i < len; i+=dest.perVert) {
            for (int j = 0; j < dest.perVert; j++) {
                dest.coords.put(factor[j] * src.coords.get());
            }
        }
        src.coords.limit(oldLimit);
        dest.coords.limit(oldLimit);

        if (vboInfo != null) {
            vboInfo.resizeTextureIds(this.texBuf.size());
        }

        checkTextureCoordinates();
    }

    /**
     * <code>getTextureBuffers</code> retrieves this geometry's texture
     * information contained within a float buffer array.
     * 
     * @return the float buffers that contain this geometry's texture
     *         information.
     */
    public ArrayList<TexCoords> getTextureCoords() {
        return texBuf;
    }

   /**
     * <code>getTextureAsFloatBuffer</code> retrieves the texture buffer of a
     * given texture unit.
     * 
     * @param textureUnit
     *            the texture unit to check.
     * @return the texture coordinates at the given texture unit.
     */
    public TexCoords getTextureCoords(int textureUnit) {
        if (texBuf == null)
            return null;
        if (textureUnit >= texBuf.size())
            return null;
        return texBuf.get(textureUnit);
    }

    /**
     * <code>setTextureBuffer</code> sets this geometry's textures (position
     * 0) via a float buffer. This convenience method assumes we are setting
     * coordinates for texture unit 0 and that there are 2 coordinate values per
     * vertex.
     * 
     * @param coords
     *            the new coords for unit 0.
     */
    public void setTextureCoords(TexCoords coords) {
        setTextureCoords(coords, 0);
    }

    /**
     * <code>setTextureBuffer</code> sets this geometry's textures at the
     * position given via a float buffer. This convenience method assumes that
     * there are 2 coordinate values per vertex.
     * 
     * @param coords
     *            the new coords.
     * @param unit
     *            the texture unit we are providing coordinates for.
     */
    public void setTextureCoords(TexCoords coords, int unit) {
        while (unit >= texBuf.size()) {
            texBuf.add(null);
        }
        texBuf.set(unit, coords);

        if (vboInfo != null) {
            vboInfo.resizeTextureIds(texBuf.size());
        }
        checkTextureCoordinates();
    }

    /**
     * Clears all vertex, normal, texture, and color buffers by setting them to
     * null.
     */
    public void clearBuffers() {
        reconstruct(null, null, null, null);
    }

    /**
     * <code>updateBound</code> recalculates the bounding object assigned to
     * the geometry. This resets it parameters to adjust for any changes to the
     * vertex information.
     */
    public void updateModelBound() {
        if (bound != null && getVertexBuffer() != null) {
            bound.computeFromPoints(getVertexBuffer());
            updateWorldBound();
        }
    }

    /**
     * <code>setModelBound</code> sets the bounding object for this geometry.
     * 
     * @param modelBound
     *            the bounding object for this geometry.
     */
    public void setModelBound(BoundingVolume modelBound) {
        this.worldBound = null;
        this.bound = modelBound;
    }

    /**
     * <code>draw</code> prepares the geometry for rendering to the display.
     * The renderstate is set and the subclass is responsible for rendering the
     * actual data.
     * 
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     * @param r
     *            the renderer that displays to the context.
     */
    @Override
    public void draw(Renderer r) {
    }

    /**
     * <code>updateWorldBound</code> updates the bounding volume that contains
     * this geometry. The location of the geometry is based on the location of
     * all this node's parents.
     * 
     * @see com.jme.scene.Spatial#updateWorldBound()
     */
    public void updateWorldBound() {
        if (bound != null) {
            worldBound = bound.transform(getWorldRotation(),
                    getWorldTranslation(), getWorldScale(), worldBound);
        }
    }

    /**
     * <code>applyRenderState</code> determines if a particular render state
     * is set for this Geometry. If not, the default state will be used.
     */
    @Override
    protected void applyRenderState(Stack<? extends RenderState>[] states) {
        for (int x = 0; x < states.length; x++) {
            if (states[x].size() > 0) {
                this.states[x] = ((RenderState) states[x].peek()).extract(
                        states[x], this);
            } else {
                this.states[x] = Renderer.defaultStateList[x];
            }
        }
    }

    /**
     * sorts the lights based on distance to geometry bounding volume
     */
    public void sortLights() {
        if (lightState != null && lightState.getLightList().size() > LightState.MAX_LIGHTS_ALLOWED) {
            LightUtil.sort(this, lightState.getLightList());
        }
    }
    
    /**
     * <code>randomVertex</code> returns a random vertex from the list of
     * vertices set to this geometry. If there are no vertices set, null is
     * returned.
     * 
     * @param fill
     *            a Vector3f to fill with the results. If null, one is created.
     *            It is more efficient to pass in a non-null vector.
     * @return Vector3f a random vertex from the vertex list. Null is returned
     *         if the vertex list is not set.
     */
    public Vector3f randomVertex(Vector3f fill) {
        if (getVertexBuffer() == null)
            return null;
        int i = (int) (FastMath.nextRandomFloat() * getVertexCount());

        if (fill == null)
            fill = new Vector3f();
        BufferUtils.populateFromBuffer(fill, getVertexBuffer(), i);

        localToWorld(fill, fill);

        return fill;
    }

    /**
     * Check if this geometry intersects the ray if yes add it to the results.
     * 
     * @param ray ray to check intersection with. The direction of the ray must
     *            be normalized (length 1).
     * @param requiredOnBits Collision will only be considered if 'this'
     *        has these bits of its collision mask set.
     * @param results
     *            result list
     */
    @Override
    public void findPick(Ray ray, PickResults results, int requiredOnBits) {
        if (getWorldBound() == null || !isCollidable(requiredOnBits)) {
            return;
        }
        if (getWorldBound().intersects(ray)) {
            // find the triangle that is being hit.
            // add this node and the triangle to the PickResults list.
            results.addPick(ray, this,requiredOnBits);
        }
    }

    /**
     * <code>setDefaultColor</code> sets the color to be used if no per vertex
     * color buffer is set.
     * 
     * @param color
     */
    public void setDefaultColor(ColorRGBA color) {
        defaultColor = color;
    }

    /**
     * <code>getWorldCoords</code> translates/rotates and scales the
     * coordinates of this Geometry to world coordinates based on its world
     * settings. The results are stored in the given FloatBuffer. If given
     * FloatBuffer is null, one is created.
     * 
     * @param store
     *            the FloatBuffer to store the results in, or null if you want
     *            one created.
     * @return store or new FloatBuffer if store == null.
     */
    public FloatBuffer getWorldCoords(FloatBuffer store) {
        final FloatBuffer vertBuf = getVertexBuffer();
        if (store == null || store.capacity() != vertBuf.limit()) {
            store = BufferUtils.createFloatBuffer(vertBuf.limit());
            if (store == null) {
                return null;
            }
        }

        for (int v = 0, vSize = store.capacity() / 3; v < vSize; v++) {
            BufferUtils.populateFromBuffer(compVect, vertBuf, v);
            localToWorld(compVect, compVect);
            BufferUtils.setInBuffer(compVect, store, v);
        }
        return store;
    }

    /**
     * <code>getWorldNormals</code> rotates the normals of this Geometry to
     * world normals based on its world settings. The results are stored in the
     * given FloatBuffer. If given FloatBuffer is null, one is created.
     * 
     * @param store
     *            the FloatBuffer to store the results in, or null if you want
     *            one created.
     * @return store or new FloatBuffer if store == null.
     */
    public FloatBuffer getWorldNormals(FloatBuffer store) {
        final FloatBuffer normBuf = getNormalBuffer();
        if (store == null || store.capacity() != normBuf.limit()) {
            store = BufferUtils.createFloatBuffer(normBuf.limit());
            if (store == null) {
                return null;
            }
        }

        for (int v = 0, vSize = store.capacity() / 3; v < vSize; v++) {
            BufferUtils.populateFromBuffer(compVect, normBuf, v);
            getWorldRotation().multLocal(compVect);
            BufferUtils.setInBuffer(compVect, store, v);
        }
        return store;
    }

    public int getDisplayListID() {
        return displayListID;
    }

    public void setDisplayListID(int displayListID) {
        this.displayListID = displayListID;
    }

    public void setTextureCoords(ArrayList<TexCoords> texBuf) {
        this.texBuf = texBuf;
        checkTextureCoordinates();
    }

    public void clearTextureBuffers() {
        if (texBuf != null) {
            texBuf.clear();
        }
    }

    public void addTextureCoordinates(TexCoords textureCoords) {
        addTextureCoordinates(textureCoords, 2);
    }

    public void addTextureCoordinates(TexCoords textureCoords, int coordSize) {
        if (texBuf != null) {
            texBuf.add(textureCoords);
        }
        checkTextureCoordinates();
    }

    public void resizeTextureIds(int i) {
        vboInfo.resizeTextureIds(i);
    }

    protected void checkTextureCoordinates() {
        int max = TextureState.getNumberOfFragmentTexCoordUnits();
        if (max == -1)
            return; // No texture state created yet.
        if (texBuf != null && texBuf.size() > max) {
            for (int i = max; i < texBuf.size(); i++) {
                if (texBuf.get(i) != null) {
                    logger.log(Level.WARNING, "Texture coordinates set for unit {0}."
                            + " Only {1} units are available.", new Integer[]{i, max});
                }
            }
        }
    }


    public void scaleTextureCoordinates(int index, float factor) {
        scaleTextureCoordinates(index, new Vector2f(factor, factor));
    }

    public void scaleTextureCoordinates(int index, Vector2f factor) {
        if (texBuf == null)
            return;

        if (index < 0 || index >= texBuf.size() || texBuf.get(index) == null) {
            return;
        }

        TexCoords tc = texBuf.get(index);

        for (int i = 0, len = tc.coords.limit() / 2; i < len; i++) {
            BufferUtils.multInBuffer(factor, tc.coords, i);
        }

        if (vboInfo != null) {
            vboInfo.resizeTextureIds(this.texBuf.size());
        }
    }

    public boolean isCastsShadows() {
        return castsShadows;
    }

    public void setCastsShadows(boolean castsShadows) {
        this.castsShadows = castsShadows;
    }

    /**
     * <code>getNumberOfUnits</code> returns the number of texture units this
     * geometry is currently using.
     * 
     * @return the number of texture units in use.
     */
    public int getNumberOfUnits() {
        if (texBuf == null)
            return 0;
        return texBuf.size();
    }

    @Override
    public void lockMeshes(Renderer r) {
        if (getDisplayListID() != -1) {
            logger.warning("This Geometry already has locked meshes."
                    + "(Use unlockMeshes to clear)");
            return;
        }

        updateRenderState();
        lockedMode |= LOCKED_MESH_DATA;

        setDisplayListID(r.createDisplayList(this));
    }

    @Override
    public void unlockMeshes(Renderer r) {
        lockedMode &= ~LOCKED_MESH_DATA;

        if (getDisplayListID() != -1) {
            r.releaseDisplayList(getDisplayListID());
            setDisplayListID(-1);
        }
    }

    /**
     * Called just before renderer starts drawing this geometry. If it returns
     * false, we'll skip rendering.
     */
    public boolean predraw(Renderer r) {
        return true;
    }

    /**
     * Called after renderer finishes drawing this geometry.
     */
    public void postdraw(Renderer r) {
    }

    public void translatePoints(float x, float y, float z) {
        translatePoints(new Vector3f(x, y, z));
    }

    public void translatePoints(Vector3f amount) {
        for (int x = 0; x < vertQuantity; x++) {
            BufferUtils.addInBuffer(amount, vertBuf, x);
        }
    }

    public void rotatePoints(Quaternion rotate) {
        Vector3f store = new Vector3f();
        for (int x = 0; x < vertQuantity; x++) {
            BufferUtils.populateFromBuffer(store, vertBuf, x);
            rotate.mult(store, store);
            BufferUtils.setInBuffer(store, vertBuf, x);
        }
    }

    public void rotateNormals(Quaternion rotate) {
        Vector3f store = new Vector3f();
        for (int x = 0; x < vertQuantity; x++) {
            BufferUtils.populateFromBuffer(store, normBuf, x);
            rotate.mult(store, store);
            BufferUtils.setInBuffer(store, normBuf, x);
        }
    }

    /**
     * <code>getDefaultColor</code> returns the color used if no per vertex
     * colors are specified.
     * 
     * @return default color
     */
    public ColorRGBA getDefaultColor() {
        return defaultColor;
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(colorBuf, "colorBuf", null);
        capsule.write(normBuf, "normBuf", null);
        capsule.write(vertBuf, "vertBuf", null);
        capsule.writeSavableArrayList(texBuf, "texBuf",
                new ArrayList<TexCoords>(1));
        capsule.write(tangentBuf, "tangentBuf", null);
        capsule.write(binormalBuf, "binormalBuf", null);
        capsule.write(enabled, "enabled", true);
        capsule.write(castsShadows, "castsShadows", true);
        capsule.write(bound, "bound", null);
        capsule.write(defaultColor, "defaultColor", ColorRGBA.white);
        capsule.write(vboInfo, "vboInfo", null);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);

        colorBuf = capsule.readFloatBuffer("colorBuf", null);
        normBuf = capsule.readFloatBuffer("normBuf", null);
        vertBuf = capsule.readFloatBuffer("vertBuf", null);
        if (vertBuf != null)
            vertQuantity = vertBuf.limit() / 3;
        else
            vertQuantity = 0;
        tangentBuf = capsule.readFloatBuffer("tangentBuf", null);
        binormalBuf = capsule.readFloatBuffer("binormalBuf", null);
        texBuf = capsule.readSavableArrayList("texBuf",
                new ArrayList<TexCoords>(1));
        checkTextureCoordinates();

        enabled = capsule.readBoolean("enabled", true);
        castsShadows = capsule.readBoolean("castsShadows", true);
        bound = (BoundingVolume) capsule.readSavable("bound", null);
        if (bound != null)
            worldBound = bound.clone(null);
        defaultColor = (ColorRGBA) capsule.readSavable("defaultColor",
                ColorRGBA.white.clone());
        vboInfo = (VBOInfo) capsule.readSavable("vboInfo", null);
    }

    /**
     * <code>getModelBound</code> retrieves the bounding object that contains
     * the geometry's vertices.
     * 
     * @return the bounding object for this geometry.
     */
    public BoundingVolume getModelBound() {
        return bound;
    }

    public boolean hasDirtyVertices() {
        return hasDirtyVertices;
    }

    public void setHasDirtyVertices(boolean flag) {
        hasDirtyVertices = flag;
    }

    public void setTangentBuffer(FloatBuffer tangentBuf) {
        this.tangentBuf = tangentBuf;
    }

    public FloatBuffer getTangentBuffer() {
        return this.tangentBuf;
    }

    public void setBinormalBuffer(FloatBuffer binormalBuf) {
        this.binormalBuf = binormalBuf;
    }

    public FloatBuffer getBinormalBuffer() {
        return binormalBuf;
    }
    
    public void setLightState(LightState lightState) {
        this.lightState = lightState;
    }

    public LightState getLightState() {
        return lightState;
    }
}

