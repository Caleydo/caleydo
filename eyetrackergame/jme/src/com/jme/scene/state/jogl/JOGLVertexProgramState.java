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

package com.jme.scene.state.jogl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.jme.renderer.RenderContext;
import com.jme.renderer.jogl.JOGLContextCapabilities;
import com.jme.renderer.jogl.JOGLRenderer;
import com.jme.scene.state.StateRecord;
import com.jme.scene.state.VertexProgramState;
import com.jme.scene.state.jogl.records.VertexProgramStateRecord;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

/**
 * Implementation of the GL_ARB_vertex_program extension.
 *
 * @author Eric Woroshow
 * @author Joshua Slack - reworked for StateRecords.
 * @author Steve Vaughan - JOGL port
 * @version $Id: JOGLVertexProgramState.java,v 1.10 2004/08/07 21:53:18
 *          ericthered Exp $
 */
public class JOGLVertexProgramState extends VertexProgramState {
    private static final Logger logger = Logger.getLogger(JOGLVertexProgramState.class.getName());

    private static final long serialVersionUID = 1L;

    private int programID = -1;

    private JOGLContextCapabilities caps;
    
    public JOGLVertexProgramState() {
        this( ( ( JOGLRenderer ) DisplaySystem.getDisplaySystem().
        getRenderer()).getContextCapabilities() );
    }
    
    public JOGLVertexProgramState(JOGLContextCapabilities caps) {
        this.caps = caps;
    }
    
    /**
     * Determines if the current OpenGL context supports the
     * GL_ARB_vertex_program extension.
     *
     * @see com.jme.scene.state.VertexProgramState#isSupported()
     */
    public boolean isSupported() {
        return caps.GL_ARB_vertex_program;
    }

    /**
     * Loads the vertex program into a byte array.
     *
     * @see com.jme.scene.state.VertexProgramState#load(java.net.URL)
     */
    public void load(java.net.URL file) {
        InputStream inputStream = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16 * 1024);
            inputStream = new BufferedInputStream(file.openStream());
            byte[] buffer = new byte[1024];
            int byteCount = -1;

            // Read the byte content into the output stream first
            while((byteCount = inputStream.read(buffer)) > 0)
            {
                outputStream.write(buffer, 0, byteCount);
            }

            // Set data with byte content from stream
            byte[] data = outputStream.toByteArray();

            // Release resources
            inputStream.close();
            outputStream.close();

            program = BufferUtils.createByteBuffer(data.length);
            program.put(data);
            program.rewind();
            programID = -1;
            setNeedsRefresh(true);

        } catch (Exception e) {
            logger.severe("Could not load vertex program: " + e);
            logger.logp(Level.SEVERE, getClass().getName(), "load(URL)", "Exception", e);
        }
        finally {
            // Ensure that the stream is closed, even if there is an exception.
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException closeFailure) {
                    logger.log(Level.WARNING,
                            "Failed to close the vertex program",
                            closeFailure);
                }
            }

        }
    }

    /**
     * Loads the vertex program into a byte array.
     *
     * @see com.jme.scene.state.VertexProgramState#load(java.net.URL)
     */
    public void load(String programContents) {
        try {
            byte[] bytes = programContents.getBytes();
            program = BufferUtils.createByteBuffer(bytes.length);
            program.put(bytes);
            program.rewind();
            programID = -1;
            setNeedsRefresh(true);

        } catch (Exception e) {
            logger.severe("Could not load vertex program: " + e);
            logger.logp(Level.SEVERE, getClass().getName(), "load(URL)", "Exception", e);
        }
    }

    /**
     * Queries OpenGL for errors in the vertex program. Errors are logged as
     * SEVERE, noting both the line number and message.
     */
    private void checkProgramError() {
        final GL gl = GLU.getCurrentGL();

        if (gl.glGetError() == GL.GL_INVALID_OPERATION) {
            //retrieve the error position
            IntBuffer errorloc = BufferUtils.createIntBuffer(16);
            gl.glGetIntegerv(GL.GL_PROGRAM_ERROR_POSITION_ARB,
                    errorloc); // TODO Check for integer

            logger.severe("Error "
                    + gl.glGetString(GL.GL_PROGRAM_ERROR_STRING_ARB)
                    + " in vertex program on line " + errorloc.get(0));
        }
    }

    public String getProgram() {
        if (program == null) return null;
        program.rewind();
        byte[] stringContents = new byte[program.remaining()];
        program.get(stringContents);
        return new String(stringContents);
    }

    private void create() {
        final GL gl = GLU.getCurrentGL();

        //first assert that the program is loaded
        if (program == null) {
            logger.severe("Attempted to apply unloaded vertex program state.");
            return;
        }

        IntBuffer buf = BufferUtils.createIntBuffer(1);

        gl.glGenProgramsARB(buf.limit(),buf); // TODO Check <size>
        gl.glBindProgramARB(
                GL.GL_VERTEX_PROGRAM_ARB, buf.get(0));

        byte array[] = new byte[program.limit()];
        program.rewind();
        program.get(array);
        gl.glProgramStringARB(
                GL.GL_VERTEX_PROGRAM_ARB,
                GL.GL_PROGRAM_FORMAT_ASCII_ARB,array.length, new String(array)); // TODO Check cost of using non-buffer

        checkProgramError();

        programID = buf.get(0);
    }

    /**
     * Applies this vertex program to the current scene. Checks if the
     * GL_ARB_vertex_program extension is supported before attempting to enable
     * this program.
     *
     * @see com.jme.scene.state.RenderState#apply()
     */
    public void apply() {
        final GL gl = GLU.getCurrentGL();

        if (isSupported()) {
            //ask for the current state record
            RenderContext<?> context = DisplaySystem.getDisplaySystem()
                    .getCurrentContext();
            VertexProgramStateRecord record = (VertexProgramStateRecord) context.getStateRecord(StateType.VertexProgram);
            context.currentStates[StateType.VertexProgram.ordinal()] = this;

            if (!record.isValid() || record.getReference() != this) {
                record.setReference(this);
                if (isEnabled()) {
                    //Vertex program not yet loaded
                    if (programID == -1)
                        if (program != null)
                            create();
                        else
                            return;

                    gl.glEnable(GL.GL_VERTEX_PROGRAM_ARB);
                    gl.glBindProgramARB(
                            GL.GL_VERTEX_PROGRAM_ARB, programID);

                    //load environmental parameters...
                    for (int i = 0; i < envparameters.length; i++)
                        if (envparameters[i] != null)
                            gl.glProgramEnvParameter4fARB(
                                    GL.GL_VERTEX_PROGRAM_ARB, i,
                                    envparameters[i][0], envparameters[i][1],
                                    envparameters[i][2], envparameters[i][3]);

                    //load local parameters...
                    if (usingParameters) //No sense checking array if we are sure
                                         // no parameters are used
                        for (int i = 0; i < parameters.length; i++)
                            if (parameters[i] != null)
                                gl.glProgramLocalParameter4fARB(
                                        GL.GL_VERTEX_PROGRAM_ARB, i,
                                        parameters[i][0], parameters[i][1],
                                        parameters[i][2], parameters[i][3]);

                } else {
                    gl.glDisable(GL.GL_VERTEX_PROGRAM_ARB);
                }
            }

            if (!record.isValid())
                record.validate();
        }
    }

    @Override
    public StateRecord createStateRecord() {
        return new VertexProgramStateRecord();
    }
}
