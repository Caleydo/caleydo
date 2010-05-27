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
import java.util.logging.Logger;

import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.system.DisplaySystem;
import com.jme.system.dummy.DummySystemProvider;
import com.jme.util.BinaryFileReader;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.geom.BufferUtils;
import com.jmex.model.animation.KeyframeController;

/**
 * Started Date: Jul 15, 2004<br>
 * <br>
 * Converts from MD3 files to jme binary.
 * 
 * @author Jack Lindamood
 */
public class Md3ToJme extends FormatConverter {
    private static final Logger logger = Logger.getLogger(Md3ToJme.class
            .getName());

    private BinaryFileReader file;
    private MD3Header head;
    private MD3Frame[] frames;
    private MD3Tag[][] tags;
    private MD3Surface[] surfaces;
    private KeyframeController vkc;

    public static void main(String[] args) {
    	DisplaySystem.getDisplaySystem(DummySystemProvider.DUMMY_SYSTEM_IDENTIFIER);
        new Md3ToJme().attemptFileConvert(args);
    }

    public void convert(InputStream format, OutputStream jMEFormat)
            throws IOException {
        file = new BinaryFileReader(format);
        readHeader();
        readFrames();
        readTags();
        readSurfaces();
        BinaryExporter.getInstance().save(constructMesh(), jMEFormat);
    }

    private Node constructMesh() {
        Node toReturn = new Node("MD3 File");
        for (int i = 0; i < head.numSurface; i++) {
            vkc = new KeyframeController();
            MD3Surface thisSurface = surfaces[i];
            TriMesh object = new TriMesh(thisSurface.name);
            object.setIndexBuffer(BufferUtils
                    .createIntBuffer(thisSurface.triIndexes));
            object.setVertexBuffer(BufferUtils
                    .createFloatBuffer(thisSurface.verts[0]));
            object.setNormalBuffer(BufferUtils
                    .createFloatBuffer(thisSurface.norms[0]));
            object.setTextureCoords(TexCoords.makeNew(thisSurface.texCoords));
            toReturn.attachChild(object);
            vkc.setMorphingMesh(object);
            for (int j = 0; j < head.numFrames; j++) {
                TriMesh etm = new TriMesh();
                etm.setVertexBuffer(BufferUtils
                        .createFloatBuffer(thisSurface.verts[j]));
                etm.setNormalBuffer(BufferUtils
                        .createFloatBuffer(thisSurface.norms[j]));
                vkc.setKeyframe(j, etm);
            }
            vkc.setActive(true);
            vkc.setSpeed(5);
            object.addController(vkc);
            toReturn.addController(vkc);
        }
        nullAll();
        return toReturn;
    }

    public KeyframeController getController() {
        return this.vkc;
    }

    private void nullAll() {
        frames = null;
        tags = null;
        surfaces = null;
        head = null;
        file = null;
    }

    public static KeyframeController findController(Node model) {
        if (model.getQuantity() == 0
                || model.getChild(0).getControllers().size() == 0
                || !(model.getChild(0).getController(0) instanceof KeyframeController))
            return null;
        return (KeyframeController) model.getChild(0).getController(0);
    }

    private void readSurfaces() throws IOException {
        file.setOffset(head.surfaceOffset);
        surfaces = new MD3Surface[head.numSurface];
        for (int i = 0; i < head.numSurface; i++) {
            surfaces[i] = new MD3Surface();
            surfaces[i].readMe();
        }
    }

    private void readTags() {
        file.setOffset(head.tagOffset);
        tags = new MD3Tag[head.numFrames][];
        for (int i = 0; i < head.numFrames; i++) {
            tags[i] = new MD3Tag[head.numTags];
            for (int j = 0; j < head.numTags; j++) {
                tags[i][j] = new MD3Tag();
                tags[i][j].readMe();
            }
        }

    }

    private void readFrames() {
        file.setOffset(head.frameOffset);
        frames = new MD3Frame[head.numFrames];
        for (int i = 0; i < head.numFrames; i++) {
            frames[i] = new MD3Frame();
            frames[i].readMe();
        }
    }

    private void readHeader() throws IOException {
        head = new MD3Header();
        head.readMe();
    }

    private class MD3Header {
        int version;
        String name;
        int flags;
        int numFrames;
        int numTags;
        int numSurface;
        int numSkins;
        int frameOffset;
        int tagOffset;
        int surfaceOffset;
        int fileOffset;

        void readMe() throws IOException {
            int ident = file.readInt();
            if (ident != 0x33504449)
                throw new IOException("Unknown file format:" + ident);
            version = file.readInt();
            if (version != 15)
                throw new IOException("Unsupported version " + version
                        + ", only know ver 15");
            name = file.readString(64);
            flags = file.readInt();
            numFrames = file.readInt();
            numTags = file.readInt();
            numSurface = file.readInt();
            numSkins = file.readInt();
            frameOffset = file.readInt();
            tagOffset = file.readInt();
            surfaceOffset = file.readInt();
            fileOffset = file.readInt();
        }
    }

    private class MD3Frame {
        Vector3f minBounds = new Vector3f();
        Vector3f maxBounds = new Vector3f();
        Vector3f localOrigin = new Vector3f();
        float scale;
        String name;

        void readMe() {
            readVecFloat(minBounds);
            readVecFloat(maxBounds);
            readVecFloat(localOrigin);
            scale = file.readFloat();
            name = file.readString(16);
        }
    }

    private class MD3Tag {
        String path;
        Vector3f origin = new Vector3f();
        Matrix3f axis;

        void readMe() {
            path = file.readString(64);
            readVecFloat(origin);
            float[] axisFs = new float[9];
            for (int i = 0; i < 9; i++)
                axisFs[i] = file.readFloat();
            axis = new Matrix3f();
            axis.set(axisFs);
        }
    }

    private class MD3Surface {
        String name;
        int flags;
        int numFrames;
        int numShaders;
        int numVerts;
        int numTriangles;
        int offTriangles;
        int offShaders;
        int offTexCoord;
        int offXyzNor;
        int offEnd;
        int[] triIndexes;
        Vector2f[] texCoords;
        Vector3f[][] verts;
        Vector3f[][] norms;
        private final static float XYZ_SCALE = 1.0f / 64;
        private static final boolean DEBUG = false;

        public void readMe() throws IOException {
            file.markPos();
            int ident = file.readInt();
            if (ident != 0x33504449)
                throw new IOException("Unknown file format:" + ident);
            name = file.readString(64);
            flags = file.readInt();
            numFrames = file.readInt();
            numShaders = file.readInt();
            numVerts = file.readInt();
            numTriangles = file.readInt();
            offTriangles = file.readInt();
            offShaders = file.readInt();
            offTexCoord = file.readInt();
            offXyzNor = file.readInt();
            offEnd = file.readInt();
            // readShader(); // Skip shader info
            readTriangles();
            readTexCoord();
            readVerts();
        }

        private void readVerts() {
            file.seekMarkOffset(offXyzNor);
            verts = new Vector3f[head.numFrames][];
            norms = new Vector3f[head.numFrames][];
            for (int i = 0; i < head.numFrames; i++) {
                verts[i] = new Vector3f[numVerts];
                norms[i] = new Vector3f[numVerts];
                for (int j = 0; j < numVerts; j++) {
                    verts[i][j] = new Vector3f();
                    norms[i][j] = new Vector3f();
                    readVecShort(verts[i][j]);
                    readNormal(norms[i][j]);
                }
            }
        }

        private void readVecShort(Vector3f vector3f) {
            vector3f.z = file.readSignedShort() * XYZ_SCALE;
            vector3f.x = file.readSignedShort() * XYZ_SCALE;
            vector3f.y = file.readSignedShort() * XYZ_SCALE;
        }

        private void readNormal(Vector3f norm) {
            int zenith = file.readByte();
            int azimuth = file.readByte();
            float lat = (zenith * 2 * FastMath.PI) / 255;
            float lng = (azimuth * 2 * FastMath.PI) / 255;
            norm.x = FastMath.cos(lat) * FastMath.sin(lng);
            norm.y = FastMath.sin(lat) * FastMath.sin(lng);
            norm.z = FastMath.cos(lng);
        }

        private void readTexCoord() {
            file.seekMarkOffset(offTexCoord);
            texCoords = new Vector2f[numVerts];
            for (int i = 0; i < texCoords.length; i++) {
                texCoords[i] = new Vector2f();
                texCoords[i].x = file.readFloat();
                texCoords[i].y = 1 - file.readFloat();
            }

        }

        private void readTriangles() {
            file.seekMarkOffset(offTriangles);
            triIndexes = new int[numTriangles * 3];
            for (int i = 0; i < triIndexes.length; i++)
                triIndexes[i] = file.readInt();
        }

        private void readShader() {
            file.seekMarkOffset(offShaders);
            for (int i = 0; i < numShaders; i++) {
                String pathName = file.readString(64);
                int shaderIndex = file.readInt();
                if (DEBUG)
                    logger.info("path:" + pathName + " Index:" + shaderIndex);
            }
        }

    }

    void readVecFloat(Vector3f in) {
        in.z = file.readFloat();
        in.x = file.readFloat();
        in.y = file.readFloat();

    }
}