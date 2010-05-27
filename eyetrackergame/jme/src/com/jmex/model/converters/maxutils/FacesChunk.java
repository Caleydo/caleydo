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

package com.jmex.model.converters.maxutils;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * parent == OBJ_TRIMESH == 4100<br>
 * type == FACES_ARRAY == 4120<br>
 *
 * @author Jack Lindamood
 */
class FacesChunk extends ChunkerClass{
    private static final Logger logger = Logger.getLogger(FacesChunk.class
            .getName());
    
    int nFaces;
    int[][] faces;
    int [] smoothingGroups;
    ArrayList<String> materialNames;
    ArrayList<int[]> materialIndexes;


    public FacesChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    protected void initializeVariables() throws IOException {
        nFaces=myIn.readUnsignedShort();
        if (DEBUG || DEBUG_LIGHT) logger.info("Reading faces #=" + nFaces);
        faces=new int[nFaces][];
        smoothingGroups=new int[nFaces];
        materialNames=new ArrayList<String>();
        materialIndexes=new ArrayList<int[]>();

        for (int i=0;i<nFaces;i++){
            faces[i]=new int[]{myIn.readUnsignedShort(),myIn.readUnsignedShort(),myIn.readUnsignedShort()};
            short flag=myIn.readShort();
        }
        decrHeaderLen(2 + nFaces*(3*2+2));
    }


    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case SMOOTH_GROUP:
                readSmoothing();
                return true;
            case MESH_MAT_GROUP:
                readMeshMaterialGroup();
                return true;
            default:
                return false;
            }
    }

    private void readMeshMaterialGroup() throws IOException {
        String name=readcStr();
        int numFace=myIn.readUnsignedShort();
        int[] appliedFacesIndexes=new int[numFace];
        if (DEBUG || DEBUG_LIGHT) logger.info("Material " + name + " is applied to " + numFace + " faces");
        for (int i=0;i<numFace;i++){
            appliedFacesIndexes[i]=myIn.readUnsignedShort();
        }
        materialIndexes.add(appliedFacesIndexes);
        materialNames.add(name);
    }

    private void readSmoothing() throws IOException {
        for (int i=0;i<nFaces;i++)
            smoothingGroups[i]=myIn.readInt();
    }
}
