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

package com.jme.scene.state.lwjgl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.opengl.ARBFragmentProgram;
import org.lwjgl.opengl.ARBProgram;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.jme.renderer.RenderContext;
import com.jme.scene.state.FragmentProgramState;
import com.jme.scene.state.StateRecord;
import com.jme.scene.state.lwjgl.records.FragmentProgramStateRecord;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

/**
 * @author Eric Woroshow
 * @author Joshua Slack - misc, plus StateRecord code.
 * @version $Id: LWJGLFragmentProgramState.java,v 1.1 2004/08/20 23:21:20
 *          ericthered Exp $
 */
public final class LWJGLFragmentProgramState extends FragmentProgramState {
    private static final Logger logger = Logger.getLogger(LWJGLFragmentProgramState.class.getName());

    private static final long serialVersionUID = 1L;

    private int programID = -1;

    /**
     * Determines if the current OpenGL context supports the
     * GL_ARB_fragment_program extension.
     * 
     * @see com.jme.scene.state.FragmentProgramState#isSupported()
     */
    public boolean isSupported() {
        return GLContext.getCapabilities().GL_ARB_fragment_program;
    }

    /**
     * Loads the fragment program into a byte array.
     * 
     * @see com.jme.scene.state.FragmentProgramState#load(java.net.URL)
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
            byte data[] = outputStream.toByteArray();

            // Release resources
            inputStream.close();
            outputStream.close();

            program = BufferUtils.createByteBuffer(data.length);
            program.put(data);
            program.rewind();
            programID = -1;
            setNeedsRefresh(true);
        } catch (Exception e) {
            logger.severe("Could not load fragment program: " + e);
            logger.logp(Level.SEVERE, getClass().getName(), "load(URL)", "Exception", e);
        }
        finally {
            // Ensure that the stream is closed, even if there is an exception.
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException closeFailure) {
                    logger.log(Level.WARNING,
                            "Failed to close the fragment program",
                            closeFailure);
                }
            }
        }
    }

    /**
     * Loads the fragment program into a byte array.
     * 
     * @see com.jme.scene.state.FragmentProgramState#load(java.net.URL)
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
            logger.severe("Could not load fragment program: " + e);
            logger.logp(Level.SEVERE, getClass().getName(), "load(URL)", "Exception", e);
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
        //first assert that the program is loaded
        if (program == null) {
            logger.severe("Attempted to apply unloaded fragment program state.");
            return;
        }

        IntBuffer buf = BufferUtils.createIntBuffer(1);

        ARBProgram.glGenProgramsARB(buf);
        ARBProgram.glBindProgramARB(
                ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB, buf.get(0));
        ARBProgram.glProgramStringARB(
                ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB,
                ARBProgram.GL_PROGRAM_FORMAT_ASCII_ARB, program);

        checkProgramError();

        programID = buf.get(0);
    }

    /**
     * Queries OpenGL for errors in the fragment program. Errors are logged as
     * SEVERE, noting both the line number and message.
     */
    private void checkProgramError() {
        if (GL11.glGetError() == GL11.GL_INVALID_OPERATION) {
            //retrieve the error position
            IntBuffer errorloc = BufferUtils.createIntBuffer(16);
            GL11.glGetInteger(ARBProgram.GL_PROGRAM_ERROR_POSITION_ARB,
                    errorloc);

            logger.severe("Error "
                    + GL11.glGetString(ARBProgram.GL_PROGRAM_ERROR_STRING_ARB)
                    + " in fragment program on line " + errorloc.get(0));
        }
    }

    public void apply() {
        if (isSupported()) {
            RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
            FragmentProgramStateRecord record = (FragmentProgramStateRecord) context.getStateRecord(StateType.FragmentProgram);
            
            context.currentStates[StateType.FragmentProgram.ordinal()] = this;

            if (!record.isValid() || record.getReference() != this) {
                record.setReference(this);
                if (isEnabled()) {
                    //Fragment program not yet loaded
                    if (programID == -1)
                        if (program != null)
                            create();
                        else
                            return;
    
                    GL11.glEnable(ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB);
                    ARBProgram.glBindProgramARB(
                            ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB, programID);
    
                    //load environmental parameters...
                    //TODO: Reevaluate how this is done.
                    /*
                     * for (int i = 0; i < envparameters.length; i++) if
                     * (envparameters[i] != null)
                     * ARBFragmentProgram.glProgramEnvParameter4fARB(
                     * ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB, i,
                     * envparameters[i][0], envparameters[i][1],
                     * envparameters[i][2], envparameters[i][3]);
                     */
    
                    //load local parameters...
                    if (usingParameters) //No sense checking array if we are sure
                                         // no parameters are used
                        for (int i = 0; i < parameters.length; i++)
                            if (parameters[i] != null)
                                ARBProgram.glProgramLocalParameter4fARB(
                                        ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB,
                                        i, parameters[i][0], parameters[i][1],
                                        parameters[i][2], parameters[i][3]);
    
                } else {
                    GL11.glDisable(ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB);
                }
            }
            
            if (!record.isValid())
                record.validate();
        }
    }

    @Override
    public StateRecord createStateRecord() {
        return new FragmentProgramStateRecord();
    }
}
