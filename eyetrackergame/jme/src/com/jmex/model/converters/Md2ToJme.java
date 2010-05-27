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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.system.dummy.DummySystemProvider;
import com.jme.util.BinaryFileReader;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.geom.BufferUtils;
import com.jmex.model.animation.KeyframeController;

/**
 * Started Date: Jun 14, 2004<br>
 * <br>
 * This class converts a .md2 file to jME's binary format.
 * 
 * @author Jack Lindamood
 */
public class Md2ToJme extends FormatConverter {
    
    private static final Logger logger = Logger.getLogger(Md2ToJme.class
            .getName());

    /**
     * Converts an Md2 file to jME format. The syntax is: "Md2ToJme drfreak.md2
     * outfile.jme".
     * 
     * @param args
     *            The array of parameters
     */
    public static void main(String[] args) {
    	DisplaySystem.getDisplaySystem(DummySystemProvider.DUMMY_SYSTEM_IDENTIFIER);
        new Md2ToJme().attemptFileConvert(args);
    }

    /**
     * It creates a node from a .md2 stream and then writes that node to the
     * given OutputStream in jME's binary format.
     * 
     * @param Md2Stream
     *            A stream representing the .md2 file
     * @param o
     *            The stream to write it's binary equivalent to
     * @throws java.io.IOException
     *             If anything funky goes wrong with reading information
     */
    public void convert(InputStream Md2Stream, OutputStream o)
            throws IOException {
        if (Md2Stream == null)
            throw new NullPointerException("Unable to load null streams");
        Md2ConverterCopy mcc = new Md2ConverterCopy(Md2Stream);
        Node newnode = new Node(mcc.mesh.getName());
        newnode.attachChild(mcc.mesh);
        BinaryExporter.getInstance().save(newnode, o);
    }

    /**
     * 95% a Copy/paste of the .md2 loader by Mark Powell modified for
     * efficiency (use of empty TriMesh) and VertexController as well as a few
     * tiny adjustments here and there on memory.
     * 
     * @author Mark Powell
     * @author Jack Lindamood
     */
    private static class Md2ConverterCopy {
        private static final long serialVersionUID = 1L;

        private BinaryFileReader bis = null;

        private Header header;

        private Vector2f[] texCoords;
        private Md2Face[] triangles;
        private Md2Frame[] frames;

        // holds each keyframe.
        private TriMesh[] triMesh;
        // controller responsible for handling keyframe morphing.
        private KeyframeController controller;

        public TriMesh mesh;

        /**
         * Loads an MD2 model. The corresponding <code>TriMesh</code> objects
         * are created and attached to the model. Each keyframe is then loaded
         * and assigned to a <code>KeyframeController</code>. MD2 does not
         * keep track of it's own texture or material settings, so the user is
         * responsible for setting these.
         * 
         * @param input
         *            the InputStream of the file to load.
         */
        public Md2ConverterCopy(InputStream input) {

            if (null == input) {
                throw new JmeException("Null data. Cannot load.");
            }

            mesh = new TriMesh("MD2 mesh" + new Random().nextInt());

            bis = new BinaryFileReader(input);

            header = new Header();

            if (header.version != 8) {
                throw new JmeException("Invalid file format (Version not 8)!");
            }

            parseMesh();
            convertDataStructures();

            triangles = null;
            texCoords = null;
            frames = null;
        }

        /**
         * <code>getAnimationController</code> returns the animation
         * controller used for MD2 animation (VertexKeyframeController).
         * 
         * @return
         * @see com.jmex.model.Model#getAnimationController()
         */
        public Controller getAnimationController() {
            return controller;
        }

        /**
         * <code>parseMesh</code> reads the MD2 file and builds the necessary
         * data structures. These structures are specific to MD2 and therefore
         * require later conversion to jME data structures.
         */
        private void parseMesh() {
            String[] skins = new String[header.numSkins];
            texCoords = new Vector2f[header.numTexCoords];
            triangles = new Md2Face[header.numTriangles];
            frames = new Md2Frame[header.numFrames];

            // start with skins. Move the file pointer to the correct position.
            bis.setOffset(header.offsetSkins);

            // Read in each skin for this model
            for (int j = 0; j < header.numSkins; j++) {
                skins[j] = bis.readString(64);
            }

            // Now read in texture coordinates.
            bis.setOffset(header.offsetTexCoords);
            for (int j = 0; j < header.numTexCoords; j++) {
                texCoords[j] = new Vector2f();
                texCoords[j].x = (float) bis.readShort();
                texCoords[j].y = (float) bis.readShort();
            }

            // read the vertex data.
            bis.setOffset(header.offsetTriangles);
            for (int j = 0; j < header.numTriangles; j++) {
                triangles[j] = new Md2Face();
            }

            bis.setOffset(header.offsetFrames);
            // Each keyframe has the same type of data, so read each
            // keyframe one at a time.
            for (int i = 0; i < header.numFrames; i++) {
                VectorKeyframe frame = new VectorKeyframe();
                frames[i] = new Md2Frame();

                frames[i].vertices = new Triangle[header.numVertices];
                Vector3f[] aliasVertices = new Vector3f[header.numVertices];
                int[] aliasLightNormals = new int[header.numVertices];

                // Read in the first frame of animation
                for (int j = 0; j < header.numVertices; j++) {
                    aliasVertices[j] = new Vector3f(bis.readByte(), bis
                            .readByte(), bis.readByte());
                    aliasLightNormals[j] = bis.readByte();
                }

                // Copy the name of the animation to our frames array
                frames[i].name = frame.name;
                Triangle[] verices = frames[i].vertices;

                for (int j = 0; j < header.numVertices; j++) {
                    verices[j] = new Triangle();
                    verices[j].vertex.x = aliasVertices[j].x * frame.scale.x
                            + frame.translate.x;
                    verices[j].vertex.z = -1
                            * (aliasVertices[j].y * frame.scale.y + frame.translate.y);
                    verices[j].vertex.y = aliasVertices[j].z * frame.scale.z
                            + frame.translate.z;

                    if (aliasLightNormals[j] < norms.length) {
                        verices[j].normal.x = norms[aliasLightNormals[j]][0];
                        verices[j].normal.y = norms[aliasLightNormals[j]][2];
                        verices[j].normal.z = -norms[aliasLightNormals[j]][1];
                    } else {
                        verices[j].normal.set(0, 1, 0); // DEFAULT?
                        logger.warning("Referenced an invalid normal: "+aliasLightNormals[j]);
                    }
                }
            }

            // TODO: Read OPENGL commands here...
            bis.setOffset(header.offsetGlCommands);

        }

        private static class VectorTex {
            private VectorTex(final int vIndex, final int tIndex) {
                v = vIndex;
                t = tIndex;
            }

            public boolean equals(final Object o) {
                if (this == o)
                    return true;
                if (o == null || getClass() != o.getClass())
                    return false;

                final VectorTex that = (VectorTex) o;

                if (t != that.t)
                    return false;
                if (v != that.v)
                    return false;

                return true;
            }

            public int hashCode() {
                int l_result;
                l_result = v;
                l_result = 31 * l_result + t;
                return l_result;
            }

            int v;
            int t;

            public boolean matches(final Md2Face face, final int faceVert) {
                return face.vertexIndices[faceVert] == v
                        && face.textureIndices[faceVert] == t;
            }
        }

        /**
         * <code>convertDataStructures</code> takes the loaded MD2 data and
         * converts it into jME data.
         */
        private void convertDataStructures() {
            triMesh = new TriMesh[header.numFrames];
            List<VectorTex> vectorTexcoords = new ArrayList<VectorTex>();
            controller = new KeyframeController();
            for (int i = 0; i < header.numFrames; i++) {
                int numOfVerts = header.numVertices;
                int numTexVertex = header.numTexCoords;
                if (i != 0)
                    triMesh[i] = new TriMesh();
                else
                    triMesh[i] = mesh;
                Vector3f[] uniqueVerts = new Vector3f[numOfVerts];
                Vector3f[] uniqueNorms = new Vector3f[numOfVerts];
                Vector2f[] texVerts = new Vector2f[numTexVertex];

                // assign a vector array for the trimesh.
                for (int j = 0; j < numOfVerts; j++) {
                    if (i != 0) {
                        uniqueVerts[j] = frames[i].vertices[j].vertex;
                        uniqueNorms[j] = frames[i].vertices[j].normal;
                    } else {
                        uniqueVerts[j] = new Vector3f(
                                frames[i].vertices[j].vertex);
                        uniqueNorms[j] = new Vector3f(
                                frames[i].vertices[j].normal);
                    }
                }

                if (i == 0) {
                    // texture coordinates.
                    for (int j = 0; j < numTexVertex; j++) {
                        texVerts[j] = new Vector2f();
                        texVerts[j].x = texCoords[j].x / (header.skinWidth);
                        texVerts[j].y = 1 - texCoords[j].y
                                / (header.skinHeight);
                    }

                    // collect all used combinations of vertices and texcoords
                    if (numTexVertex != 0) {
                        for (int j = 0; j < header.numTriangles; j++) {
                            for (int k = 0; k < 3; k++) {
                                final VectorTex l_tex = new VectorTex(
                                        triangles[j].vertexIndices[k],
                                        triangles[j].textureIndices[k]);
                                if (!vectorTexcoords.contains(l_tex)) {
                                    vectorTexcoords.add(l_tex);
                                }
                            }
                        }
                    }
                    // build indices
                    List<Integer> indices = new ArrayList<Integer>();
                    for (int j = 0; j < header.numTriangles; j++) {
                        for (int k = 0; k < 3; k++) {
                            for (int i1 = 0; i1 < vectorTexcoords.size(); i1++) {
                                VectorTex vectorTexcoord = vectorTexcoords
                                        .get(i1);
                                if (vectorTexcoord.matches(triangles[j], k)) {
                                    indices.add(i1);
                                    break;
                                }
                            }
                        }
                    }
                    int[] indexArray = new int[indices.size()];
                    for (int x = 0; x < indexArray.length; x++) {
                        indexArray[x] = indices.get(x).intValue();
                    }

                    triMesh[0].setIndexBuffer(BufferUtils
                            .createIntBuffer(indexArray));
                    triMesh[0]
                            .setTextureCoords(TexCoords.makeNew(extractTexCoords(
                                            vectorTexcoords, texVerts, indices
                                                    .size())));
                    controller.setMorphingMesh(triMesh[0]);

                } // End if (i==0)

                final Vector3f[] allVerts = extractVertices(vectorTexcoords,
                        uniqueVerts);
                triMesh[i].setVertexBuffer(BufferUtils
                        .createFloatBuffer(allVerts));

                final Vector3f[] allNorms = extractNormals(vectorTexcoords,
                        uniqueNorms);
                triMesh[i].setNormalBuffer(BufferUtils
                        .createFloatBuffer(allNorms));

                controller.setKeyframe(i, triMesh[i]);
            }

            mesh.addController(controller);
        }

        private Vector3f[] extractVertices(
                final List<VectorTex> vectorTexcoords,
                final Vector3f[] uniqueVerts) {
            final List<Vector3f> ret = new ArrayList<Vector3f>();
            for (int i = 0; i < vectorTexcoords.size(); i++) {
                VectorTex vectorTex = vectorTexcoords.get(i);
                final Vector3f vert = uniqueVerts[vectorTex.v];
                ret.add(vert);
            }
            return ret.toArray(new Vector3f[ret.size()]);
        }

        private Vector3f[] extractNormals(
                final List<VectorTex> vectorTexcoords,
                final Vector3f[] uniqueNormals) {
            final List<Vector3f> ret = new ArrayList<Vector3f>();
            for (int i = 0; i < vectorTexcoords.size(); i++) {
                VectorTex vectorTex = vectorTexcoords.get(i);
                final Vector3f norm = uniqueNormals[vectorTex.v];
                ret.add(norm);
            }
            return ret.toArray(new Vector3f[ret.size()]);
        }

        private Vector2f[] extractTexCoords(
                final List<VectorTex> vectorTexcoords,
                final Vector2f[] texVerts, final int coordCount) {
            final Vector2f[] ret = new Vector2f[coordCount];
            int count = 0;
            for (int i = 0; i < vectorTexcoords.size(); i++) {
                VectorTex vectorTex = vectorTexcoords.get(i);
                final Vector2f vert = texVerts[vectorTex.t];
                ret[count++] = vert;
            }
            return ret;
        }

        // This holds the header information that is read in at the beginning of
        // the file
        private class Header {
            int magic; // This is used to identify the file
            int version; // The version number of the file (Must be 8)
            int skinWidth; // The skin width in pixels
            int skinHeight; // The skin height in pixels
            int frameSize; // The size in bytes the frames are
            int numSkins; // The number of skins associated with the model
            int numVertices; // The number of vertices (constant for each
            // frame)
            int numTexCoords; // The number of texture coordinates
            int numTriangles; // The number of faces (polygons)
            int numGlCommands; // The number of gl commands
            int numFrames; // The number of animation frames
            int offsetSkins; // The offset in the file for the skin data
            int offsetTexCoords; // The offset in the file for the texture
            // data
            int offsetTriangles; // The offset in the file for the face data
            int offsetFrames; // The offset in the file for the frames data
            int offsetGlCommands;
            // The offset in the file for the gl commands data
            int offsetEnd; // The end of the file offset

            Header() {
                magic = bis.readInt();
                version = bis.readInt();
                skinWidth = bis.readInt();
                skinHeight = bis.readInt();
                frameSize = bis.readInt();
                numSkins = bis.readInt();
                numVertices = bis.readInt();
                numTexCoords = bis.readInt();
                numTriangles = bis.readInt();
                numGlCommands = bis.readInt();
                numFrames = bis.readInt();
                offsetSkins = bis.readInt();
                offsetTexCoords = bis.readInt();
                offsetTriangles = bis.readInt();
                offsetFrames = bis.readInt();
                offsetGlCommands = bis.readInt();
                offsetEnd = bis.readInt();
            }

        };

        // This stores the normals and vertices for the frames
        private class Triangle {
            Vector3f vertex = new Vector3f();
            Vector3f normal = new Vector3f();
        };

        // This stores the indices into the vertex and texture coordinate arrays
        private class Md2Face {
            int[] vertexIndices = new int[3]; // short
            int[] textureIndices = new int[3]; // short

            Md2Face() {
                vertexIndices = new int[] { bis.readShort(), bis.readShort(),
                        bis.readShort() };
                textureIndices = new int[] { bis.readShort(), bis.readShort(),
                        bis.readShort() };
            }
        };

        // This stores the animation scale, translation and name information for
        // a
        // frame, plus verts
        private class VectorKeyframe {
            private Vector3f scale = new Vector3f();
            private Vector3f translate = new Vector3f();
            private String name;

            VectorKeyframe() {
                scale.x = bis.readFloat();
                scale.y = bis.readFloat();
                scale.z = bis.readFloat();

                translate.x = bis.readFloat();
                translate.y = bis.readFloat();
                translate.z = bis.readFloat();
                name = bis.readString(16);
            }
        };

        // This stores the frames vertices after they have been transformed
        private class Md2Frame {
            String name; // char [16]
            Triangle[] vertices;

            Md2Frame() {
            }
        };
    }

    /**
     * This function returns the KeyframeController that animates an MD2
     * converted mesh. Null is returned if a KeyframeController cannot be found.
     * 
     * @param model
     *            The MD2 mesh.
     * @return This mesh's controller.
     */
    public static KeyframeController findController(Node model) {
        if (model.getQuantity() == 0
                || model.getChild(0).getControllers().size() == 0
                || !(model.getChild(0).getController(0) instanceof KeyframeController))
            return null;
        return (KeyframeController) model.getChild(0).getController(0);
    }

    private static final float[][] norms = {
            { -0.525731f, 0.000000f, 0.850651f },
            { -0.442863f, 0.238856f, 0.864188f },
            { -0.295242f, 0.000000f, 0.955423f },
            { -0.309017f, 0.500000f, 0.809017f },
            { -0.162460f, 0.262866f, 0.951056f },
            { 0.000000f, 0.000000f, 1.000000f },
            { 0.000000f, 0.850651f, 0.525731f },
            { -0.147621f, 0.716567f, 0.681718f },
            { 0.147621f, 0.716567f, 0.681718f },
            { 0.000000f, 0.525731f, 0.850651f },
            { 0.309017f, 0.500000f, 0.809017f },
            { 0.525731f, 0.000000f, 0.850651f },
            { 0.295242f, 0.000000f, 0.955423f },
            { 0.442863f, 0.238856f, 0.864188f },
            { 0.162460f, 0.262866f, 0.951056f },
            { -0.681718f, 0.147621f, 0.716567f },
            { -0.809017f, 0.309017f, 0.500000f },
            { -0.587785f, 0.425325f, 0.688191f },
            { -0.850651f, 0.525731f, 0.000000f },
            { -0.864188f, 0.442863f, 0.238856f },
            { -0.716567f, 0.681718f, 0.147621f },
            { -0.688191f, 0.587785f, 0.425325f },
            { -0.500000f, 0.809017f, 0.309017f },
            { -0.238856f, 0.864188f, 0.442863f },
            { -0.425325f, 0.688191f, 0.587785f },
            { -0.716567f, 0.681718f, -0.147621f },
            { -0.500000f, 0.809017f, -0.309017f },
            { -0.525731f, 0.850651f, 0.000000f },
            { 0.000000f, 0.850651f, -0.525731f },
            { -0.238856f, 0.864188f, -0.442863f },
            { 0.000000f, 0.955423f, -0.295242f },
            { -0.262866f, 0.951056f, -0.162460f },
            { 0.000000f, 1.000000f, 0.000000f },
            { 0.000000f, 0.955423f, 0.295242f },
            { -0.262866f, 0.951056f, 0.162460f },
            { 0.238856f, 0.864188f, 0.442863f },
            { 0.262866f, 0.951056f, 0.162460f },
            { 0.500000f, 0.809017f, 0.309017f },
            { 0.238856f, 0.864188f, -0.442863f },
            { 0.262866f, 0.951056f, -0.162460f },
            { 0.500000f, 0.809017f, -0.309017f },
            { 0.850651f, 0.525731f, 0.000000f },
            { 0.716567f, 0.681718f, 0.147621f },
            { 0.716567f, 0.681718f, -0.147621f },
            { 0.525731f, 0.850651f, 0.000000f },
            { 0.425325f, 0.688191f, 0.587785f },
            { 0.864188f, 0.442863f, 0.238856f },
            { 0.688191f, 0.587785f, 0.425325f },
            { 0.809017f, 0.309017f, 0.500000f },
            { 0.681718f, 0.147621f, 0.716567f },
            { 0.587785f, 0.425325f, 0.688191f },
            { 0.955423f, 0.295242f, 0.000000f },
            { 1.000000f, 0.000000f, 0.000000f },
            { 0.951056f, 0.162460f, 0.262866f },
            { 0.850651f, -0.525731f, 0.000000f },
            { 0.955423f, -0.295242f, 0.000000f },
            { 0.864188f, -0.442863f, 0.238856f },
            { 0.951056f, -0.162460f, 0.262866f },
            { 0.809017f, -0.309017f, 0.500000f },
            { 0.681718f, -0.147621f, 0.716567f },
            { 0.850651f, 0.000000f, 0.525731f },
            { 0.864188f, 0.442863f, -0.238856f },
            { 0.809017f, 0.309017f, -0.500000f },
            { 0.951056f, 0.162460f, -0.262866f },
            { 0.525731f, 0.000000f, -0.850651f },
            { 0.681718f, 0.147621f, -0.716567f },
            { 0.681718f, -0.147621f, -0.716567f },
            { 0.850651f, 0.000000f, -0.525731f },
            { 0.809017f, -0.309017f, -0.500000f },
            { 0.864188f, -0.442863f, -0.238856f },
            { 0.951056f, -0.162460f, -0.262866f },
            { 0.147621f, 0.716567f, -0.681718f },
            { 0.309017f, 0.500000f, -0.809017f },
            { 0.425325f, 0.688191f, -0.587785f },
            { 0.442863f, 0.238856f, -0.864188f },
            { 0.587785f, 0.425325f, -0.688191f },
            { 0.688191f, 0.587785f, -0.425325f },
            { -0.147621f, 0.716567f, -0.681718f },
            { -0.309017f, 0.500000f, -0.809017f },
            { 0.000000f, 0.525731f, -0.850651f },
            { -0.525731f, 0.000000f, -0.850651f },
            { -0.442863f, 0.238856f, -0.864188f },
            { -0.295242f, 0.000000f, -0.955423f },
            { -0.162460f, 0.262866f, -0.951056f },
            { 0.000000f, 0.000000f, -1.000000f },
            { 0.295242f, 0.000000f, -0.955423f },
            { 0.162460f, 0.262866f, -0.951056f },
            { -0.442863f, -0.238856f, -0.864188f },
            { -0.309017f, -0.500000f, -0.809017f },
            { -0.162460f, -0.262866f, -0.951056f },
            { 0.000000f, -0.850651f, -0.525731f },
            { -0.147621f, -0.716567f, -0.681718f },
            { 0.147621f, -0.716567f, -0.681718f },
            { 0.000000f, -0.525731f, -0.850651f },
            { 0.309017f, -0.500000f, -0.809017f },
            { 0.442863f, -0.238856f, -0.864188f },
            { 0.162460f, -0.262866f, -0.951056f },
            { 0.238856f, -0.864188f, -0.442863f },
            { 0.500000f, -0.809017f, -0.309017f },
            { 0.425325f, -0.688191f, -0.587785f },
            { 0.716567f, -0.681718f, -0.147621f },
            { 0.688191f, -0.587785f, -0.425325f },
            { 0.587785f, -0.425325f, -0.688191f },
            { 0.000000f, -0.955423f, -0.295242f },
            { 0.000000f, -1.000000f, 0.000000f },
            { 0.262866f, -0.951056f, -0.162460f },
            { 0.000000f, -0.850651f, 0.525731f },
            { 0.000000f, -0.955423f, 0.295242f },
            { 0.238856f, -0.864188f, 0.442863f },
            { 0.262866f, -0.951056f, 0.162460f },
            { 0.500000f, -0.809017f, 0.309017f },
            { 0.716567f, -0.681718f, 0.147621f },
            { 0.525731f, -0.850651f, 0.000000f },
            { -0.238856f, -0.864188f, -0.442863f },
            { -0.500000f, -0.809017f, -0.309017f },
            { -0.262866f, -0.951056f, -0.162460f },
            { -0.850651f, -0.525731f, 0.000000f },
            { -0.716567f, -0.681718f, -0.147621f },
            { -0.716567f, -0.681718f, 0.147621f },
            { -0.525731f, -0.850651f, 0.000000f },
            { -0.500000f, -0.809017f, 0.309017f },
            { -0.238856f, -0.864188f, 0.442863f },
            { -0.262866f, -0.951056f, 0.162460f },
            { -0.864188f, -0.442863f, 0.238856f },
            { -0.809017f, -0.309017f, 0.500000f },
            { -0.688191f, -0.587785f, 0.425325f },
            { -0.681718f, -0.147621f, 0.716567f },
            { -0.442863f, -0.238856f, 0.864188f },
            { -0.587785f, -0.425325f, 0.688191f },
            { -0.309017f, -0.500000f, 0.809017f },
            { -0.147621f, -0.716567f, 0.681718f },
            { -0.425325f, -0.688191f, 0.587785f },
            { -0.162460f, -0.262866f, 0.951056f },
            { 0.442863f, -0.238856f, 0.864188f },
            { 0.162460f, -0.262866f, 0.951056f },
            { 0.309017f, -0.500000f, 0.809017f },
            { 0.147621f, -0.716567f, 0.681718f },
            { 0.000000f, -0.525731f, 0.850651f },
            { 0.425325f, -0.688191f, 0.587785f },
            { 0.587785f, -0.425325f, 0.688191f },
            { 0.688191f, -0.587785f, 0.425325f },
            { -0.955423f, 0.295242f, 0.000000f },
            { -0.951056f, 0.162460f, 0.262866f },
            { -1.000000f, 0.000000f, 0.000000f },
            { -0.850651f, 0.000000f, 0.525731f },
            { -0.955423f, -0.295242f, 0.000000f },
            { -0.951056f, -0.162460f, 0.262866f },
            { -0.864188f, 0.442863f, -0.238856f },
            { -0.951056f, 0.162460f, -0.262866f },
            { -0.809017f, 0.309017f, -0.500000f },
            { -0.864188f, -0.442863f, -0.238856f },
            { -0.951056f, -0.162460f, -0.262866f },
            { -0.809017f, -0.309017f, -0.500000f },
            { -0.681718f, 0.147621f, -0.716567f },
            { -0.681718f, -0.147621f, -0.716567f },
            { -0.850651f, 0.000000f, -0.525731f },
            { -0.688191f, 0.587785f, -0.425325f },
            { -0.587785f, 0.425325f, -0.688191f },
            { -0.425325f, 0.688191f, -0.587785f },
            { -0.425325f, -0.688191f, -0.587785f },
            { -0.587785f, -0.425325f, -0.688191f },
            { -0.688191f, -0.587785f, -0.425325f } };

}
