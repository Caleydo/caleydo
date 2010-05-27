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
import java.util.logging.Logger;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * type ==  NAMED_OBJECT == 0x4000<br>
 * parent == 3d3d == EDIT_3DS<br>
 *
 * @author Jack Lindamood
 */
class NamedObjectChunk extends ChunkerClass{
    private static final Logger logger = Logger
            .getLogger(NamedObjectChunk.class.getName());
    
    String name;
//    ArrayList meshList;
//    ArrayList cameraList;
//    ArrayList lightList;
    Object whatIAm;
    public NamedObjectChunk(DataInput myIn, ChunkHeader header) throws IOException {
        super(myIn, header);
    }

    protected void initializeVariables() throws IOException {
//        meshList=new ArrayList();
//        cameraList=new ArrayList();
//        lightList=new ArrayList();
        name=readcStrAndDecrHeader();
        if (DEBUG) logger.info("Editable object name="+name);
    }


    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case OBJ_TRIMESH:
                if (whatIAm!=null)
                    throw new IOException("logic error whatIAm in Named Object isn't null");
                whatIAm=new TriMeshChunk(myIn,i);
                return true;
            case CAMERA_FLAG:
                if (whatIAm!=null)
                    throw new IOException("logic error whatIAm in Named Object isn't null");
                whatIAm=new CameraChunk(myIn,i);
                return true;
            case LIGHT_OBJ:
                if (whatIAm!=null)
                    throw new IOException("logic error whatIAm in Named Object isn't null");
                whatIAm=new LightChunk(myIn,i);
                return true;
            default:
                return false;
        }
    }
}
