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

import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.util.geom.BufferUtils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * WeightBuffer contains associations of vertexes to bones and their weights.
 * The WeightBuffer can be sent to a shader or processed on the CPU
 * to do skinning.
 */
public final class WeightBuffer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Each 4 bytes in the boneIndex buffer are assigned to a vertex.
     *
     */
    ByteBuffer indexes;

    /**
     * The weight of each bone specified in the index buffer
     */
    FloatBuffer weights;

    /**
     * The maximum number of weighted bones used by the vertices
     * Can be 1-4. The indexes and weights still have 4 components per vertex,
     * regardless of this value.
     */
    int maxWeightsPerVert = 0;

    public WeightBuffer(int vertexCount){
        indexes = BufferUtils.createByteBuffer(vertexCount * 4);
        weights = BufferUtils.createFloatBuffer(vertexCount * 4);
    }

    public WeightBuffer(ByteBuffer indexes, FloatBuffer weights){
        this.indexes = indexes;
        this.weights = weights;
    }

    public ByteBuffer getIndexes() {
		return indexes;
	}

	public FloatBuffer getWeights() {
		return weights;
	}
    
    public void sendToShader(GLSLShaderObjectsState shader){
        indexes.rewind();
        shader.setAttributePointer("indexes", 4, false, true, 0, indexes);

        if (maxWeightsPerVert > 1){
            weights.rewind();
            shader.setAttributePointer("weights", 4, true, 0, weights);
        }
    }

    /**
     * Normalizes weights if needed and finds largest amount of weights used
     * for all vertices in the buffer.
     */
    public void initializeWeights(){
        int nVerts = weights.capacity() / 4;
        weights.rewind();
        for (int v = 0; v < nVerts; v++){
            float w0 = weights.get(),
                  w1 = weights.get(),
                  w2 = weights.get(),
                  w3 = weights.get();

            if (w3 > 0.01f){
                maxWeightsPerVert = Math.max(maxWeightsPerVert, 4);
            }else if (w2 > 0.01f){
                maxWeightsPerVert = Math.max(maxWeightsPerVert, 3);
            }else if (w1 > 0.01f){
                maxWeightsPerVert = Math.max(maxWeightsPerVert, 2);
            }else if (w0 > 0.01f){
                maxWeightsPerVert = Math.max(maxWeightsPerVert, 1);
            }

            float sum = w0 + w1 + w2 + w3;
            if (sum != 1f){
                weights.position(weights.position()-4);
                weights.put(w0 / sum);
                weights.put(w1 / sum);
                weights.put(w2 / sum);
                weights.put(w3 / sum);
            }
        }
        weights.rewind();
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(maxWeightsPerVert);
        out.writeInt(indexes.capacity());

        for (int i = 0; i < indexes.capacity(); i++) {
            byte b = indexes.get();
            out.writeByte(b);
        }

        out.writeInt(weights.capacity());
        for (int i = 0; i < weights.capacity(); i++) {
            float f = weights.get();
            out.writeFloat(f);
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
                                    ClassNotFoundException {
        maxWeightsPerVert = in.readInt();
        int indexesArrayLength = in.readInt();

        indexes = BufferUtils.createByteBuffer(indexesArrayLength);
        for (int i = 0; i < indexesArrayLength; i++) {
            byte b = in.readByte();
            indexes.put(b);
        }

        int weightsArrayLength = in.readInt();
        weights = BufferUtils.createFloatBuffer(weightsArrayLength);
        for (int i = 0; i < weightsArrayLength; i++) {
            float f = in.readFloat();
            weights.put(f);
        }
    }
}
