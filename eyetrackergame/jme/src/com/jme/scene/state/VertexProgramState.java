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

package com.jme.scene.state;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * Implementation of the GL_ARB_vertex_program extension.
 * @author Eric Woroshow
 * @version $Id: VertexProgramState.java 4336 2009-05-03 20:57:01Z christoph.luder $
 */
public abstract class VertexProgramState extends RenderState {

    /** Environmental parameters applied to all vertex programs */
    protected static float[][] envparameters = new float[96][];

    /** If any local parameters for this VP state are set */
    protected boolean usingParameters = false;

    /** Parameters local to this vertex program */
    protected float[][] parameters;
    protected ByteBuffer program;

    /**
     * <code>setEnvParameter</code> sets an environmental vertex program
     * parameter that is accessable by all vertex programs in memory.
     * @param param four-element array of floating point numbers
     * @param paramID identity number of the parameter, ranging from 0 to 95
     */
    public static void setEnvParameter(float[] param, int paramID){
        if (paramID < 0 || paramID > 95)
            throw new IllegalArgumentException("Invalid parameter ID");
        if (param != null && param.length != 4)
            throw new IllegalArgumentException("Vertex program parameters must be of type float[4]");

        envparameters[paramID] = param;
    }

    /**
     * <code>isSupported</code> determines if the ARB_vertex_program extension
     * is supported by current graphics configuration.
     * @return if ARB vertex programs are supported
     */
    public abstract boolean isSupported();

    /**
     * Creates a new VertexProgramState. <code>load(URL)</code> must be called before
     * the state can be used.
     */
    public VertexProgramState() {
        parameters = new float[96][];
    }

    /**
     * <code>setParameter</code> sets a parameter for this vertex program.
     * @param paramID identity number of the parameter, ranging from 0 to 95
     * @param param four-element array of floating point numbers
     */
    public void setParameter(float[] param, int paramID) {
        if (paramID < 0 || paramID > 95)
                throw new IllegalArgumentException("Invalid parameter ID");
        if (param != null && param.length != 4)
                throw new IllegalArgumentException("Vertex program parameters must be of type float[4]");

        usingParameters = true;
        parameters[paramID] = param;
        setNeedsRefresh(true);
    }

    /**
     * @return RS_VERTEX_PROGRAM
     * @see com.jme.scene.state.RenderState#getType()
     * @deprecated As of 2.0, use {@link RenderState#getStateType()} instead.
     */
    public int getType() {
        return RS_VERTEX_PROGRAM;
    }

    /**
     * <code>getStateType</code> returns the type {@link RenderState.StateType#VertexProgram}
     * 
     * @return {@link RenderState.StateType#VertexProgram}
     * @see com.jme.scene.state.RenderState#getStateType()
     */
    public StateType getStateType() {
    	
        return StateType.VertexProgram;
    }

    /**
     * <code>load</code> loads the vertex program from the specified file.
     * The program must be in ASCII format. We delegate the loading to each
     * implementation because we do not know in what format the underlying API
     * wants the data.
     * @param file text file containing the vertex program
     */
    public abstract void load(URL file);
    public abstract void load(String programContents);
    
    public abstract String getProgram();

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @see java.io.Serializable
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (program == null)
            s.writeInt(0);
        else {
            s.writeInt(program.capacity());
            program.rewind();
            for (int x = 0, len = program.capacity(); x < len; x++)
                s.writeByte(program.get());
        }
    }

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void readObject(java.io.ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        int len = s.readInt();
        if (len == 0) {
            program = null;
        } else {
            program = BufferUtils.createByteBuffer(len);
            for (int x = 0; x < len; x++)
                program.put(s.readByte());
        }
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(usingParameters, "usingParameters", false);
        capsule.write(parameters, "parameters", new float[96][]);
        capsule.write(program, "program", null);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        usingParameters = capsule.readBoolean("usingParameters", false);
        parameters = capsule.readFloatArray2D("parameters", new float[96][]);
        program = capsule.readByteBuffer("program", null);
    }
    
    public Class<?> getClassTag() {
        return VertexProgramState.class;
    }
}
