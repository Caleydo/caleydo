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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.dummy.DummySystemProvider;
import com.jme.util.TextureKey;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.geom.BufferUtils;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.model.Face;

/**
 * Started Date: Jul 1, 2004<br><br>
 *
 * Converts .ase files to .jme format.  Loads models but not animations.
 * 
 * @author Jack Lindamood
 */
public class AseToJme extends FormatConverter{
    private static final Logger logger = Logger.getLogger(AseToJme.class
            .getName());

    /**
     * Converts an ase file to jme format.  The syntax is: AseToJme file.ase out.jme
     * @param args The array of parameters
     */ 
    public static void main(String[] args){
    	DisplaySystem.getDisplaySystem(DummySystemProvider.DUMMY_SYSTEM_IDENTIFIER);
        new AseToJme().attemptFileConvert(args);
    }

    /**
     * Creates a node from a .ase InputStream and then writes that node to the given
     * OutputStream in jME's binary format
     * @param ASEStream An InputStream pointing to the .ase file
     * @param o The stream to write it's binary equivalent to
     * @throws java.io.IOException If anything funky goes wrong with reading information
     */
    public void convert(InputStream ASEStream,OutputStream o) throws IOException {
        if (ASEStream==null)
            throw new NullPointerException("Unable to load null streams");
        Node newnode=new Node("ase model");
        new AseToJme.ASEModelCopy(ASEStream,newnode);
        BinaryExporter.getInstance().save(newnode,o);
    }

    /**
     * <code>ASEModel</code> defines a model using the ASE model format.
     * This loader builds the mesh of the model but currently does not
     * build any animations defined for the format. Therefore, if a
     * call to <code>getAnimationController</code> is made, null will
     * be returned.
     *
     * @author Mark Powell
     * @version $Id: AseToJme.java 4643 2009-08-31 00:16:36Z skye.book $
     */
    private class ASEModelCopy{
        private static final long serialVersionUID = 1L;
		//ASE file tags.
        private static final String OBJECT = "*GEOMOBJECT";
        private static final String NUM_VERTEX = "*MESH_NUMVERTEX";
        private static final String NUM_FACES = "*MESH_NUMFACES";
        private static final String NUM_TVERTEX = "*MESH_NUMTVERTEX";
        private static final String VERTEX = "*MESH_VERTEX";
        private static final String FACE = "*MESH_FACE";
        private static final String NORMALS = "*MESH_NORMALS";
        private static final String FACE_NORMAL = "*MESH_FACENORMAL";
        private static final String NVERTEX = "*MESH_VERTEXNORMAL";
        private static final String TVERTEX = "*MESH_TVERT";
        private static final String TFACE = "*MESH_TFACE";
        private static final String TEXTURE = "*BITMAP";
        private static final String UTILE = "*UVW_U_TILING";
        private static final String VTILE = "*UVW_V_TILING";
        private static final String UOFFSET = "*UVW_U_OFFSET";
        private static final String VOFFSET = "*UVW_V_OFFSET";
        private static final String MATERIAL_ID = "*MATERIAL_REF";
        private static final String MATERIAL_COUNT = "*MATERIAL_COUNT";
        private static final String MATERIAL = "*MATERIAL";
        private static final String MATERIAL_NAME = "*MATERIAL_NAME";
        private static final String MATERIAL_DIFFUSE = "*MATERIAL_DIFFUSE";
        private static final String MATERIAL_AMBIENT = "*MATERIAL_AMBIENT";
        private static final String MATERIAL_SPECULAR = "*MATERIAL_SPECULAR";
        private static final String MATERIAL_SHINE = "*MATERIAL_SHINE";

        //path to the model and texture file.
        private BufferedReader reader = null;
        private StringTokenizer tokenizer;
        private String fileContents;

        private int numOfObjects; // The number of objects in the model
        private int numOfMaterials; // The number of materials for the model
        private ArrayList<ASEMaterialInfo> materials = new ArrayList<ASEMaterialInfo>();
        private ArrayList<ASEObject> objectList = new ArrayList<ASEObject>();

        Node mynode;
        /**
         * Constructor instantiates a new <code>ASEModel</code> object.
         * No data is loaded at this time and a call to <code>load</code>
         * is required to initialize the model with data.
         * @param name the name of the scene element. This is required for identification and
         *                                                          		comparision purposes.
         */
        public ASEModelCopy(String name,Node mynode) {
            this.mynode=mynode;
        }

        /**
         * Constructor instantiates a new <code>ASEModel</code> object. The
         * file provided is then read and the data loaded. Thefore, a call
         * to <code>load</code> is not required.
         *
         * @param file the InputStream of a file to load.
         */
        public ASEModelCopy(InputStream file,Node mynode) {
            this.mynode=mynode;
            load(file);
        }


        /**
         *  <code>load</code> parses a given Stream, loading the mesh data into
         * a structure that jME can render. Each Geomobject the ase file defines
         * is created as a <code>TriMesh</code> and attached to this
         * <code>Model</code>. Animation is currently not supported.
         * @param is the InputStream of the ase file to load.
         */
        public void load(InputStream is) {
            if (null == is) {
                logger.warning("Null URL could not load ASE.");
                return;
            }

            int fileSize = 0;
            try {
                fileSize = is.available();

                reader = new BufferedReader(new InputStreamReader(is));

                StringBuffer fc = new StringBuffer();

                String line;
                while ((line = reader.readLine()) != null) {
                    fc.append(line + "\n");
                }

                fileContents = fc.toString();

                reader.close();

                parseFile();
                computeNormals();
                convertToTriMesh();
            } catch (IOException e) {
                logger.warning("Could not load " + is.toString());
            }
        }

        /**
         * <code>getAnimationController</code> returns the animation
         * controller. Currently, no animation is loaded, and null will
         * be returned until the animation is implemented.
         *
         * @return @see com.jmex.model.Model#getAnimationController()
         */
        public Controller getAnimationController() {
            return null;
        }

        /**
         *
         * <code>parseFile</code> reads the file contents. First, the
         * number of materials and objects are read, then each material
         * is read and each object is read.
         *
         */
        private void parseFile() {
            ASEMaterialInfo textureInfo = new ASEMaterialInfo();
            ASEObject mesh = new ASEObject("ASEMesh");

            numOfObjects = getObjectCount();
            numOfMaterials = getMaterialCount();

            //Build texture list (not sure if this makes since, there can only be
            //one texture per mesh, and the are reading it in for the entire
            //object, not on a per object basis.
            for (int i = 0; i < numOfMaterials; i++) {
                materials.add(textureInfo);

                getMaterialInfo(materials.get(i), i + 1);
            }

            for (int i = 0; i < numOfObjects; i++) {
                mesh.materialID = -1;
                moveToObject(i + 1);
                readObjectInfo(mesh, i + 1);
                readObjectData(mesh, i + 1);
                objectList.add(mesh);
            }

        }

        /**
         *
         * <code>convertToTriMesh</code> converts the data read into
         * a collection of <code>TriMesh</code> classes that the
         * jME renderer can display.
         *
         */
        private void convertToTriMesh() {

            for (int i = 0; i < numOfObjects; i++) {
                ASEObject object = objectList.get(i);
                Vector2f[] texCoords2 = new Vector2f[object.tm.getVertexCount()];
                for (int j = 0; j < object.faces.length; j++) {
                    int index = object.faces[j].vertIndex[0];
                    texCoords2[index] = new Vector2f();
                    texCoords2[index] =
                        object.tempTexVerts[object.faces[j].coordIndex[0]];

                    index = object.faces[j].vertIndex[1];
                    texCoords2[index] = new Vector2f();
                    texCoords2[index] =
                        object.tempTexVerts[object.faces[j].coordIndex[1]];

                    index = object.faces[j].vertIndex[2];
                    texCoords2[index] = new Vector2f();
                    texCoords2[index] =
                        object.tempTexVerts[object.faces[j].coordIndex[2]];
                }

                int[] indices = new int[object.faces.length * 3];
                int count = 0;
                for (int j = 0; j < object.faces.length; j++) {
                    indices[count] = object.faces[j].vertIndex[0];
                    count++;
                    indices[count] = object.faces[j].vertIndex[1];
                    count++;
                    indices[count] = object.faces[j].vertIndex[2];
                    count++;
                }

                object.tm.setIndexBuffer(BufferUtils.createIntBuffer(indices));
                object.tm.setTextureCoords(TexCoords.makeNew(texCoords2));
                object.tm.setModelBound(new BoundingBox());
                object.tm.updateModelBound();
                mynode.attachChild(object.tm);
            }

            for (int j = 0; j < numOfMaterials; j++) {
                ASEMaterialInfo mat =
                    materials.get(j);
                if (mat.file.length() > 0) {
                    MaterialState ms =
                        DisplaySystem
                            .getDisplaySystem()
                            .getRenderer()
                            .createMaterialState();

                    ms.setEnabled(true);
                    ms.setAmbient(
                        new ColorRGBA(
                            mat.ambient[0],
                            mat.ambient[1],
                            mat.ambient[2],
                            1));
                    ms.setDiffuse(
                        new ColorRGBA(
                            mat.diffuse[0],
                            mat.diffuse[1],
                            mat.diffuse[2],
                            1));
                    ms.setSpecular(
                        new ColorRGBA(
                            mat.specular[0],
                            mat.specular[1],
                            mat.specular[2],
                            1));
                    ms.setEmissive(new ColorRGBA(0, 0, 0, 1));
                    ms.setShininess(mat.shine);
                    mynode.setRenderState(ms);
                }
            }

            for (int j = 0; j < numOfMaterials; j++) {
                // Check if the current material has a file name
                if (materials.get(j).file.length()
                    > 0) {

                    String filename =
                        materials.get(j).file;
                    URL fileURL = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, filename);
                    if (fileURL == null) {
                        logger.warning("Could not locate texture: " + filename);
                        continue;
                    }
                    TextureState ts =
                        DisplaySystem
                            .getDisplaySystem()
                            .getRenderer()
                            .createTextureState();
                    ts.setEnabled(true);
                        
                    Texture t=new Texture2D();
                    t.setImageLocation("file:/"+filename);
                    t.setTextureKey(new TextureKey(fileURL, true, TextureManager.COMPRESS_BY_DEFAULT ? Image.Format.Guess : Image.Format.GuessNoCompression));
                    t.setAnisotropicFilterPercent(0.0f);
                    t.setMinificationFilter(Texture.MinificationFilter.Trilinear);
                    t.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
                    ts.setTexture(t);
                    mynode.setRenderState(ts);
                }

            }

        }

        /**
         *
         * <code>getObjectCount</code> counts the number of Geomobject entries
         * in the ASE file. This count is then returned.
         * @return the number of Geomobject entries.
         */
        private int getObjectCount() {
            int objectCount = 0;
            tokenizer = new StringTokenizer(fileContents);

            while (tokenizer.hasMoreTokens()) {
                // Check if we hit the start of an object
                if (OBJECT.equals(tokenizer.nextToken())) {
                    objectCount++;
                }
            }

            return objectCount;
        }

        /**
         *
         * <code>getMaterialCount</code> retrieves the number of materials in the
         * ASE file. The file is read until the *MATERIAL flag is encountered. Once
         * this flag is found, the value is read.
         *
         * @return the number of materials as defined in the ASE file.
         */
        private int getMaterialCount() {
            int materialCount = 0;

            // Go to the beginning of the file
            tokenizer = new StringTokenizer(fileContents);

            // GO through the whole file until we hit the end
            while (tokenizer.hasMoreTokens()) {
                if (MATERIAL_COUNT.equals(tokenizer.nextToken())) {
                    materialCount = Integer.parseInt(tokenizer.nextToken());
                    return materialCount;
                }
            }

            //Material tag never found
            return 0;
        }

        /**
         *
         * <code>getMaterialInfo</code> reads the data for a given material
         * entry in the file. The material state information is read and
         * set as well as the texture state information.
         * @param material the material structure to store into.
         * @param desiredMaterial the material to load from the file.
         */
        private void getMaterialInfo(
            ASEMaterialInfo material,
            int desiredMaterial) {
            String strWord;
            int materialCount = 0;

            // Go to the beginning of the file
            tokenizer = new StringTokenizer(fileContents);

            //read through the file until the correct material entry is found.
            while (tokenizer.hasMoreTokens()) {
                if (MATERIAL.equals(tokenizer.nextToken())) {
                    materialCount++;

                    // Check if it's the one we want to stop at, if so break
                    if (materialCount == desiredMaterial)
                        break;
                }
            }

            while (tokenizer.hasMoreTokens()) {
                strWord = tokenizer.nextToken();

                if (strWord.equals(MATERIAL)) {
                    return;
                }

                //read material properties.
                if (strWord.equals(MATERIAL_AMBIENT)) {
                    material.ambient[0] = Float.parseFloat(tokenizer.nextToken());
                    material.ambient[1] = Float.parseFloat(tokenizer.nextToken());
                    material.ambient[2] = Float.parseFloat(tokenizer.nextToken());
                } else if (strWord.equals(MATERIAL_DIFFUSE)) {
                    material.diffuse[0] = Float.parseFloat(tokenizer.nextToken());
                    material.diffuse[1] = Float.parseFloat(tokenizer.nextToken());
                    material.diffuse[2] = Float.parseFloat(tokenizer.nextToken());
                } else if (strWord.equals(MATERIAL_SPECULAR)) {
                    material.specular[0] = Float.parseFloat(tokenizer.nextToken());
                    material.specular[1] = Float.parseFloat(tokenizer.nextToken());
                    material.specular[2] = Float.parseFloat(tokenizer.nextToken());
                } else if (strWord.equals(MATERIAL_SHINE)) {
                    material.shine = Float.parseFloat(tokenizer.nextToken());
                }

                //read texture information.
                if (strWord.equals(TEXTURE)) {
                    material.file = tokenizer.nextToken().replace('"', ' ').trim();
                } else if (strWord.equals(MATERIAL_NAME)) {
                    material.name = tokenizer.nextToken();
                } else if (strWord.equals(UTILE)) {
                    material.uTile = Float.parseFloat(tokenizer.nextToken());
                } else if (strWord.equals(VTILE)) {
                    material.vTile = Float.parseFloat(tokenizer.nextToken());
                }
            }
        }

        /**
         *
         * <code>moveToObject</code> moves the file pointer to a specific
         * GEOMOBJECT entry in the ase file.
         * @param desiredObject the object number to move to.
         */
        private void moveToObject(int desiredObject) {
            int objectCount = 0;

            tokenizer = new StringTokenizer(fileContents);

            while (tokenizer.hasMoreTokens()) {
                if (OBJECT.equals(tokenizer.nextToken())) {
                    objectCount++;

                    if (objectCount == desiredObject)
                        return;
                }
            }
        }

        /**
         *
         * <code>readObjectInfo</code> reads the mesh information defined by
         * the GEOMOBJECT entry in the file. This information is kept in the
         * ASEObject class until it is ready to be converted to a TriMesh.
         * @param currentObject the object to store the data in.
         * @param desiredObject the object to read.
         */
        private void readObjectInfo(ASEObject currentObject, int desiredObject) {
            String word;

            moveToObject(desiredObject);

            while (tokenizer.hasMoreTokens()) {
                word = tokenizer.nextToken();

                if (word.equals("*NODE_NAME")) {
                    currentObject.tm.setName(tokenizer.nextToken());
                }

                if (word.equals(NUM_VERTEX)) {
                    int numOfVerts = Integer.parseInt(tokenizer.nextToken());
                    currentObject.tm.setVertexBuffer(BufferUtils.createVector3Buffer(numOfVerts));
                } else if (word.equals(NUM_FACES)) {
                    int numOfFaces = Integer.parseInt(tokenizer.nextToken());
                    currentObject.faces = new Face[numOfFaces];
                } else if (word.equals(NUM_TVERTEX)) {
                    int numTexVertex = Integer.parseInt(tokenizer.nextToken());

                    currentObject.tempTexVerts = new Vector2f[numTexVertex];
                } else if (word.equals(OBJECT)) {
                    return;
                }
            }
        }

        /**
         *
         * <code>readObjectData</code> reads each bit of data defined by a
         * GEOMOBJECT. Namely, material id, vertices, texture vertices, faces,
         * texture faces, texture file, u and v tiling.
         * @param currentObject the object to store the information in.
         * @param desiredObject the object to read.
         */
        private void readObjectData(ASEObject currentObject, int desiredObject) {
            // Load the material ID for this object
            getData(currentObject, MATERIAL_ID, desiredObject);

            // Load the vertices for this object
            getData(currentObject, VERTEX, desiredObject);

            // Load the texture coordinates for this object
            getData(currentObject, TVERTEX, desiredObject);

            // Load the vertex faces list for this object
            getData(currentObject, FACE, desiredObject);

            // Load the texture face list for this object
            getData(currentObject, TFACE, desiredObject);

            // Load the texture for this object
            getData(currentObject, TEXTURE, desiredObject);

            // Load the U tile for this object
            getData(currentObject, UTILE, desiredObject);

            // Load the V tile for this object
            getData(currentObject, VTILE, desiredObject);
        }

        /**
         *
         * <code>getData</code> reads a specified bit of data out of a GEOMOBECT
         * entry in the ase file.
         * @param currentObject the object to save the data in.
         * @param desiredData the object type to read.
         * @param desiredObject the object to read.
         */
        private void getData(
            ASEObject currentObject,
            String desiredData,
            int desiredObject) {
            String word;

            moveToObject(desiredObject);

            // Go through the file until we reach the end
            while (tokenizer.hasMoreTokens()) {
                word = tokenizer.nextToken();

                // If we reached an object tag, stop read because we went to far
                if (word.equals(OBJECT)) {
                    // Stop reading because we are done with the current object
                    return;
                }
                // If we hit a vertex tag
                else if (word.equals(VERTEX)) {
                    // Make sure that is the data that we want to read in
                    if (desiredData.equals(VERTEX)) {
                        // Read in a vertex
                        readVertex(currentObject);
                    }
                }
                // If we hit a texture vertex
                else if (word.equals(TVERTEX)) {
                    // Make sure that is the data that we want to read in
                    if (desiredData.equals(TVERTEX)) {
                        // Read in a texture vertex
                        readTextureVertex(
                            currentObject,
                            materials.get(
                                currentObject.materialID));
                    }
                }
                // If we hit a vertex index to a face
                else if (word.equals(FACE)) {
                    // Make sure that is the data that we want to read in
                    if (desiredData.equals(FACE)) {
                        // Read in a face
                        readFace(currentObject);
                    }
                }
                // If we hit a texture index to a face
                else if (word.equals(TFACE)) {
                    // Make sure that is the data that we want to read in
                    if (desiredData.equals(TFACE)) {
                        // Read in a texture index for a face
                        readTextureFace(currentObject);
                    }
                }
                // If we hit the material ID to the object
                else if (word.equals(MATERIAL_ID)) {
                    // Make sure that is the data that we want to read in
                    if (desiredData.equals(MATERIAL_ID)) {
                        // Read in the material ID assigned to this object
                        currentObject.materialID =
                            (int) Float.parseFloat(tokenizer.nextToken());
                        return;
                    }
                }
            }
        }

        /**
         *
         * <code>readVertex</code> reads the vertices information from a
         * GEOMOBJECT entry. Some converting is required to get the
         * coordinate axes into the default jme axes.
         * @param currentObject the object to start the vertex in.
         */
        private void readVertex(ASEObject currentObject) {
            int index = 0;

            // Read past the vertex index
            index = Integer.parseInt(tokenizer.nextToken());
            float x = Float.parseFloat(tokenizer.nextToken());
            float z =-Float.parseFloat(tokenizer.nextToken());
            float y = Float.parseFloat(tokenizer.nextToken());
            currentObject.tm.getVertexBuffer().position(index*3);
            currentObject.tm.getVertexBuffer().put(x).put(y).put(z);

        }

        /**
         *
         * <code>readTextureVertex</code> reads in a single texture coordinate
         * from the ase file.
         * @param currentObject the object that has the coordinate.
         * @param texture the object that defines the texture.
         */
        private void readTextureVertex(
            ASEObject currentObject,
            ASEMaterialInfo texture) {
            int index = 0;

            // Here we read past the index of the texture coordinate
            index = Integer.parseInt(tokenizer.nextToken());
            currentObject.tempTexVerts[index] = new Vector2f();

            // Next, we read in the (U, V) texture coordinates.
            currentObject.tempTexVerts[index].x =
                Float.parseFloat(tokenizer.nextToken());
            currentObject.tempTexVerts[index].y =
                Float.parseFloat(tokenizer.nextToken());

            currentObject.tempTexVerts[index].x *= texture.uTile;
            currentObject.tempTexVerts[index].y *= texture.vTile;

        }

        /**
         *
         * <code>readFace</code> reads the face of a triangle, that
         * is how vertices are put together to
         * form the mesh.
         * @param currentObject the object to store the information
         * in.
         */
        private void readFace(ASEObject currentObject) {
            int index = 0;

            // Read past the index of this Face
            String temp = tokenizer.nextToken();
            if (temp.indexOf(":") > 0) {
                temp = temp.substring(0, temp.length() - 1);
            }
            index = Integer.parseInt(temp);
            currentObject.faces[index] = new Face();

            tokenizer.nextToken(); // "A:"
            currentObject.faces[index].vertIndex[0] =
                Integer.parseInt(tokenizer.nextToken());
            tokenizer.nextToken(); // "B:"
            currentObject.faces[index].vertIndex[1] =
                Integer.parseInt(tokenizer.nextToken());
            tokenizer.nextToken(); // "C:"
            currentObject.faces[index].vertIndex[2] =
                Integer.parseInt(tokenizer.nextToken());
        }

        /**
         *
         * <code>readFace</code> reads the face of a triangle, that
         * is how texture vertices are put together to
         * form the mesh.
         * @param currentObject the object to store the information
         * in.
         */
        private void readTextureFace(ASEObject currentObject) {
            int index = 0;

            // Read past the index for this texture coordinate
            index = Integer.parseInt(tokenizer.nextToken());

            // Now we read in the UV coordinate index for the current face.
            // This will be an index into pTexCoords[] for each point in the face.
            currentObject.faces[index].coordIndex[0] =
                Integer.parseInt(tokenizer.nextToken());
            currentObject.faces[index].coordIndex[1] =
                Integer.parseInt(tokenizer.nextToken());
            currentObject.faces[index].coordIndex[2] =
                Integer.parseInt(tokenizer.nextToken());
        }

        /**
         *
         * <code>computeNormals</code> normals are not defined in the
         * ase file, so we calculate them manually. Each vertex has a
         * matching normal. This normal is the average of all the face
         * normals surrounding the vertex.
         *
         */
        private void computeNormals() {
            if (numOfObjects <= 0) {
                return;
            }

            Vector3f vector1 = new Vector3f();
            Vector3f vector2 = new Vector3f();
            Vector3f vector3 = new Vector3f();

            // Go through each of the objects to calculate their normals
            for (int index = 0; index < numOfObjects; index++) {
                // Get the current object
                ASEObject object = objectList.get(index);
                // Here we allocate all the memory we need to calculate the normals
                Vector3f[] tempNormals = new Vector3f[object.faces.length];
                Vector3f[] normals = new Vector3f[object.tm.getVertexCount()];

                // Go though all of the faces of this object
                for (int i = 0; i < object.faces.length; i++) {
                    BufferUtils.populateFromBuffer(vector1, object.tm.getVertexBuffer(), object.faces[i].vertIndex[0]);
                    BufferUtils.populateFromBuffer(vector2, object.tm.getVertexBuffer(), object.faces[i].vertIndex[1]);
                    BufferUtils.populateFromBuffer(vector3, object.tm.getVertexBuffer(), object.faces[i].vertIndex[2]);
                    
                    vector1.subtractLocal(vector3);
                    
                    tempNormals[i] = vector1.cross(vector3.subtract(vector2)).normalizeLocal();
                }

                Vector3f sum = new Vector3f();
                int shared = 0;

                for (int i = 0; i < object.tm.getVertexCount(); i++) {
                    for (int j = 0; j < object.faces.length; j++) {
                        if (object.faces[j].vertIndex[0] == i
                            || object.faces[j].vertIndex[1] == i
                            || object.faces[j].vertIndex[2] == i) {
                            sum.addLocal(tempNormals[j]);

                            shared++;
                        }
                    }

                    normals[i] = sum.divide((-shared)).normalizeLocal();

                    sum.zero(); // Reset the sum
                    shared = 0; // Reset the shared
                }

                object.tm.setNormalBuffer(BufferUtils.createFloatBuffer(normals));

            }
        }

        /**
         *
         * <code>ASEMaterialInfo</code> holds material and texture information.
         */
        private class ASEMaterialInfo {
            String name; // The texture name
            public String file;
            // The texture file name (If this is set it's a texture map)
            public float[] diffuse = new float[3];
            public float[] ambient = new float[3];
            public float[] specular = new float[3];
            public float shine;
            // The color of the object (R, G, B)
            float uTile; // u tiling of texture (Currently not used)
            float vTile; // v tiling of texture (Currently not used)
            float uOffset; // u offset of texture (Currently not used)
            float vOffset; // v offset of texture (Currently not used)
        };

        /**
         *
         * <code>ASEObject</code> holds the data for the mesh.
         */
        public class ASEObject {
            private static final long serialVersionUID = 1L;
			public int materialID;
            public Vector2f[] tempTexVerts; // The texture's UV coordinates
            public Face[] faces; // The faces information of the object

            public TriMesh tm;
            public ASEObject(String name) {
                tm =new TriMesh(name);
            }
        };
    }
}
