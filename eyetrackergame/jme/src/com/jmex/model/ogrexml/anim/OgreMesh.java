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

package com.jmex.model.ogrexml.anim;

import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Holds the bind pose, lod levels and a weightbuffer that defines vertex->bone/weight associations.
 */
public class OgreMesh extends TriMesh {

    private static final long serialVersionUID = 8831653270716808462L;

    private transient FloatBuffer vertexBufferOriginal;
    private transient FloatBuffer normalBufferOriginal;

    private WeightBuffer weightBuffer;

    private IntBuffer levelZero;
    private IntBuffer[] lodLevels;

    public OgreMesh(String name){
        super(name);
    }

    /**
     * Instantiate an OgreMesh without name. To be populated before use.
     */
    public OgreMesh(){
        super();
    }

    public void cloneFromMesh(OgreMesh source){
        vertexBufferOriginal = source.vertexBufferOriginal;
        normalBufferOriginal = source.normalBufferOriginal;

        if (hasBindPose()){
            setVertexBuffer(BufferUtils.createFloatBuffer(source.getVertexBuffer().capacity()));
            setNormalBuffer(BufferUtils.createFloatBuffer(source.getNormalBuffer().capacity()));
            restoreBindPose();
        }

        setWeightBuffer(source.weightBuffer);
        setLodLevels(source.lodLevels);
    }

    public void setWeightBuffer(WeightBuffer weightBuf){
        if (weightBuf == null)
            return;

        if (weightBuf.indexes.limit() / 4 != this.getVertexCount())
            throw new IllegalArgumentException(
                    "Vertex weight element count mismatch.  Expected "
                    + (getVertexCount() * 4) + ", but got "
                    + weightBuf.indexes.limit());

        weightBuffer = weightBuf;
    }

    public WeightBuffer getWeightBuffer(){
        return weightBuffer;
    }

    public FloatBuffer getVertexBufferOriginal(){
        return vertexBufferOriginal;
    }

    public FloatBuffer getNormalBufferOriginal(){
        return normalBufferOriginal;
    }

    public void setLodLevels(IntBuffer[] lodLevels){
        this.levelZero = getIndexBuffer();
        this.lodLevels = lodLevels;
    }

    /**
     * Set the current LOD level.
     * LOD level zero is the model in max quality,
     * levels 1 and below reduce the quality/vertex count of the model
     * by a certain amount to increase rendering speed.
     * @param level
     */
    public void setLodLevel(int level){
        IntBuffer target;

        if (level == 0)
            target = levelZero;
        else
            target = lodLevels[level-1];

        if (target != indexBuffer){
            setIndexBuffer(target);
//            DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(vboInfo.getVBOVertexID());
//            vboInfo.setVBOIndexID(-1);
        }
    }

    @Override
    public void setHasDirtyVertices(boolean flag){
        super.setHasDirtyVertices(flag);

        if (flag && (vboInfo != null && vboInfo.isVBOVertexEnabled() && vboInfo.isVBONormalEnabled())){
            // update VBO data here
            // not supported by jME yet..
        }
    }

    /**
     * @return Total number of lod levels
     */
    public int getLodLevelCount(){
        if (lodLevels == null)
            return 1;

        return lodLevels.length + 1;
    }

    /**
     * Clears all bind pose data
     */
    public void clearBindPose(){
        vertexBufferOriginal = null;
        normalBufferOriginal = null;
    }

    /**
     * Saves the current mesh state to it's bind pose.
     */
    public void saveCurrentToBindPose(){
        if (vertexBufferOriginal == null){
            vertexBufferOriginal = BufferUtils.createFloatBuffer(vertBuf.capacity());
        }
        if (normalBufferOriginal == null){
            normalBufferOriginal = BufferUtils.createFloatBuffer(normBuf.capacity());
        }

        vertBuf.rewind();
        vertexBufferOriginal.rewind();
        vertexBufferOriginal.put(vertBuf);

        normBuf.rewind();
        normalBufferOriginal.rewind();
        normalBufferOriginal.put(normBuf);
    }

    /**
     * Restores bind pose
     */
    public void restoreBindPose(){
        vertBuf.rewind();
        vertexBufferOriginal.rewind();
        vertBuf.put(vertexBufferOriginal);

        normBuf.rewind();
        normalBufferOriginal.rewind();
        normBuf.put(normalBufferOriginal);
    }

    /**
     * True if bind pose data is available
     * @return
     */
    public boolean hasBindPose(){
        return vertexBufferOriginal != null &&
               (normBuf == null || normalBufferOriginal != null);
    }

    @Override
    public void write(JMEExporter e) throws IOException {
        // dont want to write a vertex buffer in an animation here..
        // make sure to restore bind pose
        if (hasBindPose())
            restoreBindPose();

        super.write(e);

        OutputCapsule out = e.getCapsule(this);
        out.write(hasBindPose(), "hadBindPose", false);
        if (weightBuffer != null) {
            out.write(weightBuffer.indexes, "boneIndexes", null);
            out.write(weightBuffer.weights, "boneWeights", null);
            out.write(weightBuffer.maxWeightsPerVert, "maxWeightsPerVert", 0);
        }
    }

    @Override
    public void read(JMEImporter i) throws IOException {
        super.read(i);

        InputCapsule in = i.getCapsule(this);
        if (in.readBoolean("hadBindPose", false)) {
            saveCurrentToBindPose();
        }

        ByteBuffer indexes = in.readByteBuffer("boneIndexes", null);
        if (indexes != null) {
            FloatBuffer weights = in.readFloatBuffer("boneWeights", null);
            weightBuffer = new WeightBuffer(indexes, weights);
            weightBuffer.maxWeightsPerVert = in.readInt("maxWeightsPerVert", 0);
        }
    }
}
