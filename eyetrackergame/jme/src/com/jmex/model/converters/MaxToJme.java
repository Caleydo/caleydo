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

package com.jmex.model.converters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jme.animation.SpatialTransformer;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jme.system.dummy.DummySystemProvider;
import com.jme.util.LittleEndien;
import com.jme.util.export.binary.BinaryExporter;
import com.jmex.model.converters.maxutils.TDSFile;

/**
 * Started Date: Jun 26, 2004<br><br>
 *
 * Converts .3ds files into jME binary
 *
 * @author Jack Lindamood
 */
public class MaxToJme extends FormatConverter {
	private LittleEndien myIn;

    private TDSFile chunkedTDS=null;

    /**
     * Converts a .3ds file to .jme via command prompt
     * @param args The array of parameters.  args="file1.3ds file2.jme" will convert file1.3ds to jme and save it to file2.jme.
     */
    public static void main(String[] args){
    	DisplaySystem.getDisplaySystem(DummySystemProvider.DUMMY_SYSTEM_IDENTIFIER);
        new MaxToJme().attemptFileConvert(args);
    }

    /**
     * Converts a .3ds file (represented by the InputStream) to jME format.
     * @param max The .3ds file as an InputStream
     * @param bin The place to put the jME format
     * @throws IOException If read/write goes wrong.
     */
    public void convert(InputStream max,OutputStream bin) throws IOException {
        myIn=new LittleEndien(max);
        chunkedTDS=new TDSFile(myIn, this);
        Node toReturn=chunkedTDS.buildScene();
        chunkedTDS=null;
        myIn=null;
        BinaryExporter.getInstance().save(toReturn, bin);
    }


    /**
     * This function returns the controller of a loaded 3ds model.  Will return
     * null if a correct SpatialTransformer could not be found, or if one does not exist.
     * @param model The model that was loaded.
     * @return The controller for that 3ds model.
     */
    public static SpatialTransformer findController(Node model) {
        if (model.getQuantity()==0 ||
                model.getChild(0).getControllers().size()==0 ||
                !(model.getChild(0).getController(0) instanceof SpatialTransformer))
            return null;
        return (SpatialTransformer) (model.getChild(0)).getController(0);
    }
}