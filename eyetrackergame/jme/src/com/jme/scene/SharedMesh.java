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
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.state.RenderState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>SharedMesh</code> allows the sharing of data between multiple nodes.
 * A provided TriMesh is used as the model for this node. This allows the user
 * to place multiple copies of the same object throughout the scene without
 * having to duplicate data. It should be known that any change to the provided
 * target mesh will affect the appearance of this mesh, including animations.
 * Secondly, the SharedMesh is read only. Any attempt to write to the mesh data
 * via set* methods, will result in a warning being logged and nothing else. Any
 * changes to the mesh should happened to the target mesh being shared. <br>
 * If you plan to use collisions with a <code>SharedMesh</code> it is
 * recommended that you disable the passing of <code>updateCollisionTree</code>
 * calls to the target mesh. This is to prevent multiple calls to the target's
 * <code>updateCollisionTree</code> method, from different shared meshes.
 * Instead of this method being called from the scenegraph, you can now invoke
 * it directly on the target mesh, thus ensuring it will only be invoked once.
 * <br>
 * <b>Important:</b> It is highly recommended that the Target mesh is NOT
 * placed into the scenegraph, as its translation, rotation and scale are
 * replaced by the shared meshes using it before they are rendered. <br>
 * <b>Note:</b> Special thanks to Kevin Glass.
 * 
 * @author Mark Powell
 * @version $Revision: 4743 $, $Date: 2009-11-01 16:57:44 +0100 (So, 01 Nov 2009) $
 */
public class SharedMesh extends TriMesh {
    private static final Logger logger = Logger.getLogger(SharedMesh.class
            .getName());

    private static final long serialVersionUID = 1L;

    private TriMesh target;

    public SharedMesh() {
        super();
        defaultColor = null;
    }

	/**
	 * Constructor creates a new <code>SharedMesh</code> object. Uses the name
	 * of the target.
	 * 
	 * @param target
	 *            the TriMesh to share the data.
	 */
    public SharedMesh(TriMesh target) {
        this(target.getName(), target);
    }

    /**
     * Constructor creates a new <code>SharedMesh</code> object.
     * 
     * @param name
     *            the name of this shared mesh.
     * @param target
     *            the TriMesh to share the data.
     */
    public SharedMesh(String name, TriMesh target) {

        super(name);
        defaultColor = null;

        if (target instanceof SharedMesh) {
            setTarget(((SharedMesh) target).getDeepTarget());
            this.setName(target.getName());
            this.setCullHint(target.cullHint);
            this.setLightCombineMode(target.lightCombineMode);
            this.getLocalRotation().set(target.getLocalRotation());
            this.getLocalScale().set(target.getLocalScale());
            this.getLocalTranslation().set(target.getLocalTranslation());
            this.setRenderQueueMode(target.renderQueueMode);
            this.setTextureCombineMode(target.textureCombineMode);
            this.setZOrder(target.getZOrder());
            for (RenderState.StateType type : RenderState.StateType.values()) {
                RenderState state = target.getRenderState( type );
                if (state != null) {
                    this.setRenderState(state );
                }
            }
        } else {
            setTarget(target);
        }

        // Commented out the below as the "target"s local[RotTransScale] values may not be the values it was originally given.
        // The values in the target object are used as temporary storage when calculating things for all other shared 
        // objects and therefore cannot be considered a reliable value of the original objects positions and must be  
        // set independently by the creating process on each new sharedmesh. 
//        this.localRotation.set(target.getLocalRotation());
//        this.localScale.set(target.getLocalScale());
//        this.localTranslation.set(target.getLocalTranslation());
    }

    /**
     * <code>setTarget</code> sets the shared data mesh.
     * 
     * @param target
     *            the TriMesh to share the data.
     */
    public void setTarget(TriMesh target) {
        this.target = target;
        UserDataManager.getInstance().bind(this, target);
        for (RenderState.StateType type : RenderState.StateType.values()) {
            RenderState state = this.target.getRenderState( type );
            if (state != null) {
                setRenderState(state);
            }
        }

        setCullHint(target.getLocalCullHint());
        setLightCombineMode(target.getLocalLightCombineMode());
        setRenderQueueMode(target.getLocalRenderQueueMode());
        setTextureCombineMode(target.getLocalTextureCombineMode());
        setZOrder(target.getZOrder());
    }


    /**
     * <code>getTarget</code> returns the mesh that is being shared by this
     * object.
     * 
     * @return the mesh being shared.
     */
    public TriMesh getTarget() {
        return target;
    }

    
    /**
     * As a sharedmesh can be created from another sharedmesh, there is a possibility of the target 
     * being nested more than one level down (sharedmesh->sharedmesh->sharedmesh->target).
     * Adding this check will find the actual target in these cases, otherwise creating a 
     * sharedmesh from a shared mesh should be disallowed.
     * 
     * @return the base mesh being shared.
     */
    public TriMesh getDeepTarget() {
    	if( target instanceof SharedMesh )
    		return ((SharedMesh)target).getDeepTarget();
        return target;
    }
    
    
    
    /**
     * <code>reconstruct</code> is not supported in SharedMesh.
     * 
     * @param vertices
     *            the new vertices to use.
     * @param normals
     *            the new normals to use.
     * @param colors
     *            the new colors to use.
     * @param textureCoords
     *            the new texture coordinates to use (position 0).
     */
    @Override
    public void reconstruct(FloatBuffer vertices, FloatBuffer normals,
            FloatBuffer colors, TexCoords textureCoords) {
        logger.info("SharedMesh will ignore reconstruct.");
    }

    /**
     * <code>setVBOInfo</code> is not supported in SharedMesh.
     */
    @Override
    public void setVBOInfo(VBOInfo info) {
        logger.warning("SharedMesh does not allow the manipulation"
                + "of the the mesh data.");
    }

    /**
     * <code>getVBOInfo</code> returns the target mesh's vbo info.
     */
    @Override
    public VBOInfo getVBOInfo() {
        return target.getVBOInfo();
    }

    /**
     * <code>setSolidColor</code> is not supported by SharedMesh.
     * 
     * @param color
     *            the color to set.
     */
    @Override
    public void setSolidColor(ColorRGBA color) {
        logger.warning("SharedMesh does not allow the manipulation"
                + "of the the mesh data.");
    }

    /**
     * <code>setRandomColors</code> is not supported by SharedMesh.
     */
    @Override
    public void setRandomColors() {
        logger.warning("SharedMesh does not allow the manipulation"
                + "of the the mesh data.");
    }

    /**
     * <code>getVertexBuffer</code> returns the float buffer that contains the
     * target geometry's vertex information.
     * 
     * @return the float buffer that contains the target geometry's vertex
     *         information.
     */
    @Override
    public FloatBuffer getVertexBuffer() {
        return target.getVertexBuffer();
    }

    /**
     * <code>setVertexBuffer</code> is not supported by SharedMesh.
     * 
     * @param buff
     *            the new vertex buffer.
     */
    @Override
    public void setVertexBuffer(FloatBuffer buff) {
        logger.warning("SharedMesh does not allow the manipulation"
                + "of the the mesh data.");
    }

    /**
     * Returns the number of vertexes defined in the target's Geometry object.
     * 
     * @return The number of vertexes in the target Geometry object.
     */
    @Override
    public int getVertexCount() {
        return target.getVertexCount();
    }

    /**
     * <code>getNormalBuffer</code> retrieves the target geometry's normal
     * information as a float buffer.
     * 
     * @return the float buffer containing the target geometry information.
     */
    @Override
    public FloatBuffer getNormalBuffer() {
        return target.getNormalBuffer();
    }

    /**
     * <code>setNormalBuffer</code> is not supported by SharedMesh.
     * 
     * @param buff
     *            the new normal buffer.
     */
    @Override
    public void setNormalBuffer(FloatBuffer buff) {
        logger.warning("SharedMesh does not allow the manipulation"
                + "of the the mesh data.");
    }

    /**
     * <code>getColorBuffer</code> retrieves the float buffer that contains
     * the target geometry's color information.
     * 
     * @return the buffer that contains the target geometry's color information.
     */
    @Override
    public FloatBuffer getColorBuffer() {
        return target.getColorBuffer();
    }

    /**
     * <code>setColorBuffer</code> is not supported by SharedMesh.
     * 
     * @param buff
     *            the new color buffer.
     */
    @Override
    public void setColorBuffer(FloatBuffer buff) {
        logger.warning("SharedMesh does not allow the manipulation"
                + "of the the mesh data.");
    }

    /**
     * <code>getIndexAsBuffer</code> retrieves the target's indices array as
     * an <code>IntBuffer</code>.
     * 
     * @return the indices array as an <code>IntBuffer</code>.
     */
    @Override
    public IntBuffer getIndexBuffer() {
        return target.getIndexBuffer();
    }

    /**
     * <code>setIndexBuffer</code> is not supported by SharedMesh.
     * 
     * @param indices
     *            the index array as an IntBuffer.
     */
    @Override
    public void setIndexBuffer(IntBuffer indices) {
        logger.warning("SharedMesh does not allow the manipulation"
                + "of the the mesh data.");
    }

    /**
     * Stores in the <code>storage</code> array the indices of triangle
     * <code>i</code>. If <code>i</code> is an invalid index, or if
     * <code>storage.length<3</code>, then nothing happens
     * 
     * @param i
     *            The index of the triangle to get.
     * @param storage
     *            The array that will hold the i's indexes.
     */
    @Override
    public void getTriangle(int i, int[] storage) {
        target.getTriangle(i, storage);
    }

    /**
     * Stores in the <code>vertices</code> array the vertex values of triangle
     * <code>i</code>. If <code>i</code> is an invalid triangle index,
     * nothing happens.
     * 
     * @param i
     * @param vertices
     */
    @Override
    public void getTriangle(int i, Vector3f[] vertices) {
        target.getTriangle(i, vertices);
    }

    /**
     * Returns the number of triangles the target TriMesh contains.
     * 
     * @return The current number of triangles.
     */
    @Override
    public int getTriangleCount() {
        return target.getTriangleCount();
    }

    /**
     * <code>copyTextureCoords</code> is not supported by SharedMesh.
     * 
     * @param fromIndex
     *            the coordinates to copy.
     * @param toIndex
     *            the texture unit to set them to.
     */
    @Override
    public void copyTextureCoordinates(int fromIndex, int toIndex, float factor) {
        logger.warning("SharedMesh does not allow the manipulation"
                + "of the the mesh data.");
    }

    /**
     * <code>getTextureBuffers</code> retrieves the target geometry's texture
     * information contained within a float buffer array.
     * 
     * @return the float buffers that contain the target geometry's texture
     *         information.
     */
    @Override
    public ArrayList<TexCoords> getTextureCoords() {
        return target.getTextureCoords();
    }

    /**
     * <code>getTextureAsFloatBuffer</code> retrieves the texture buffer of a
     * given texture unit.
     * 
     * @param textureUnit
     *            the texture unit to check.
     * @return the texture coordinates at the given texture unit.
     */
    @Override
    public TexCoords getTextureCoords(int textureUnit) {
        return target.getTextureCoords(textureUnit);
    }

    /**
     * retrieves the mesh as triangle vertices of the target mesh.
     */
    @Override
    public Vector3f[] getMeshAsTrianglesVertices(Vector3f[] verts) {
        return target.getMeshAsTrianglesVertices(verts);
    }

    /**
     * clearBuffers is not supported by SharedMesh
     */
    @Override
    public void clearBuffers() {
        logger.warning("SharedMesh does not allow the manipulation"
                + "of the the mesh data.");
    }

    /**
     * This function checks for intersection between the target trimesh and the
     * given one. On the first intersection, true is returned.
     * 
     * @param toCheck The intersection testing mesh.
     * @param requiredOnBits Collision will only be considered if both 'this'
     *        and 'toCheck' have these bits of their collision masks set.
     * @return True if they intersect.
     */
    @Override
    public boolean hasTriangleCollision(TriMesh toCheck, int requiredOnBits) {
        target.setLocalTranslation(worldTranslation);
        target.setLocalRotation(worldRotation);
        target.setLocalScale(worldScale);
        target.updateWorldBound();
        return target.hasTriangleCollision(toCheck, requiredOnBits);
    }

    /**
     * This function finds all intersections between this trimesh and the
     * checking one. The intersections are stored as Integer objects of Triangle
     * indexes in each of the parameters.
     * 
     * @param toCheck
     *            The TriMesh to check.
     * @param thisIndex
     *            The array of triangle indexes intersecting in this mesh.
     * @param otherIndex
     *            The array of triangle indexes intersecting in the given mesh.
     */
    @Override
    public void findTriangleCollision(TriMesh toCheck,
            ArrayList<Integer> thisIndex, ArrayList<Integer> otherIndex) {
        target.setLocalTranslation(worldTranslation);
        target.setLocalRotation(worldRotation);
        target.setLocalScale(worldScale);
        target.updateWorldBound();
        target.findTriangleCollision(toCheck, thisIndex, otherIndex);
    }

    /**
     * <code>findTrianglePick</code> determines the triangles of the target
     * trimesh that are being touched by the ray. The indices of the triangles
     * are stored in the provided ArrayList.
     * 
     * @param toTest
     *            the ray to test.
     * @param results
     *            the indices to the triangles.
     */
    @Override
    public void findTrianglePick(Ray toTest, ArrayList<Integer> results) {
        target.setLocalTranslation(worldTranslation);
        target.setLocalRotation(worldRotation);
        target.setLocalScale(worldScale);
        target.updateWorldBound();
        target.findTrianglePick(toTest, results);
    }

    @Override
    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(target, "target", null);
        super.write(e);
    }

    @Override
    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        target = (TriMesh) capsule.readSavable("target", null);
        super.read(e);
    }

    /**
     * @see Geometry#randomVertex(Vector3f)
     */
    @Override
    public Vector3f randomVertex(Vector3f fill) {
        return target.randomVertex(fill);
    }

    /**
     * <code>setTextureBuffer</code> is not supported by SharedMesh.
     * 
     * @param buff
     *            the new vertex buffer.
     */
    @Override
    public void setTextureCoords(TexCoords buff) {
        logger.warning("SharedMesh does not allow the manipulation"
                + "of the the mesh data.");
    }

    /**
     * <code>setTextureBuffer</code> not supported by SharedMesh
     * 
     * @param buff
     *            the new vertex buffer.
     */
    @Override
    public void setTextureCoords(TexCoords buff, int position) {
        logger.warning("SharedMesh does not allow the manipulation"
                + "of the the mesh data.");
    }

    @Override
    public void setTangentBuffer(FloatBuffer tangentBuf) {
        logger.warning("SharedMesh does not allow the manipulation"
                + "of the the mesh data.");
    }

    @Override
    public FloatBuffer getTangentBuffer() {
        return target.getTangentBuffer();
    }

    @Override
    public void setBinormalBuffer(FloatBuffer binormalBuf) {
        logger.warning("SharedMesh does not allow the manipulation"
                + "of the the mesh data.");
    }

    @Override
    public FloatBuffer getBinormalBuffer() {
        return target.getBinormalBuffer();
    }

    /**
     * <code>updateWorldBound</code> updates the bounding volume that contains
     * this geometry. The location of the geometry is based on the location of
     * all this node's parents.
     * 
     * @see com.jme.scene.Spatial#updateWorldBound()
     */
    @Override
    public void updateWorldBound() {
        if (target.getModelBound() != null) {
            worldBound = target.getModelBound().transform(getWorldRotation(),
                    getWorldTranslation(), getWorldScale(), worldBound);
        }
    }

    /**
     * <code>setModelBound</code> sets the bounding object for this geometry.
     * 
     * @param modelBound
     *            the bounding object for this geometry.
     */
    @Override
    public void setModelBound(BoundingVolume modelBound) {
        target.setModelBound(modelBound);
    }

    /**
     * <code>updateBound</code> recalculates the bounding object assigned to
     * the geometry. This resets it parameters to adjust for any changes to the
     * vertex information.
     */
    @Override
    public void updateModelBound() {
        if (target.getModelBound() != null) {
            target.updateModelBound();
            updateWorldBound();
        }
    }

    /**
     * returns the model bound of the target object.
     */
    @Override
    public BoundingVolume getModelBound() {
        return target.getModelBound();
    }

    /**
     * draw renders the target mesh, at the translation, rotation and scale of
     * this shared mesh.
     * 
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     */
    @Override
    public void draw(Renderer r) {
        if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this))
                return;
        }

        target.getWorldTranslation().set(getWorldTranslation());
        target.getWorldRotation().set(getWorldRotation());
        target.getWorldScale().set(getWorldScale());
        target.setDefaultColor(getDefaultColor());
        System.arraycopy(this.states, 0, target.states, 0, states.length);

        r.draw(target);
    }

    @Override
    public void lockMeshes(Renderer r) {
        target.lockMeshes(r);
    }

    @Override
    public boolean hasDirtyVertices() {
        return target.hasDirtyVertices;
    }

    @Override
    public ColorRGBA getDefaultColor() {
        if (defaultColor == null) {
            return target.getDefaultColor();
        } else {
            return defaultColor;
        }
    }
}
