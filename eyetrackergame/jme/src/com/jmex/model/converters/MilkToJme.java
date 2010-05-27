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

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

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
import com.jme.scene.state.CullState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.system.dummy.DummyDisplaySystem;
import com.jme.system.dummy.DummySystemProvider;
import com.jme.util.LittleEndien;
import com.jme.util.TextureKey;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.geom.BufferUtils;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.model.JointMesh;
import com.jmex.model.animation.JointController;

/**
 * Started Date: Jun 8, 2004
 * <p>
 * This class converts a .ms3d file to jME's binary format. The way it converts
 * is by first building the .ms3d scenegraph object, then saving that object to
 * binary format via {@link com.jmex.model.XMLparser.JmeBinaryWriter}. This
 * requires a {@link com.jme.system.DisplaySystem} to function correctly
 * (as all loaders do).
 * <p>
 * This will normally be provided within a game environment (such as
 * {@link com.jme.app.SimpleGame}). However if you wish to use this in a
 * stand-alone environment, such as part of a tool conversion utility, you
 * should create a {@link DummyDisplaySystem} before using this class.
 * @author Jack Lindamood
 */
public class MilkToJme extends FormatConverter{
    
    private DataInput inFile;
    private byte[] tempChar=new byte[128];
    private int nNumVertices;
    private int nNumTriangles;
    private MilkTriangle[] myTris;
    private MilkVertex[] myVerts;
    private int[] materialIndexes;

    /**
     * Converts a MS3D file to jME format.  The syntax is: "MilkToJme runner.ms3d out.jme".
     * @param args The array of parameters
     */
    public static void main(String[] args){
    	DisplaySystem.getDisplaySystem(DummySystemProvider.DUMMY_SYSTEM_IDENTIFIER);
        new MilkToJme().attemptFileConvert(args);
    }


    /**
     * The node that represents the .ms3d file.  It's Children are MS meshes
     */
    private Node finalNode;

    /**
     * This class's only public function.  It creates a node from a .ms3d stream and then writes that node to the given
     * OutputStream in binary format
     * @param MSFile An inputStream that is the .ms3d file
     * @param o The Stream to write it's jME binary equivalent to
     * @throws IOException If anything funky goes wrong with reading information
     */
    public void convert(InputStream MSFile,OutputStream o) throws IOException {
        inFile=new LittleEndien(MSFile);
        finalNode=new Node("ms3d file");
        CullState CS=DisplaySystem.getDisplaySystem().getRenderer().createCullState();
        CS.setCullFace(CullState.Face.Back);
        CS.setEnabled(true);
        finalNode.setRenderState(CS);
        checkHeader();
        readVerts();
        readTriangles();
        readGroups();
        readMats();
        readJoints();
        
        
        BinaryExporter.getInstance().save(finalNode,o);
        nullAll();
    }
    
    private void addJointMeshes(Node parentNode, JointController jc) {
    	for (int i=0;i<parentNode.getQuantity();i++){
            if (parentNode.getChild(i) instanceof JointMesh)
                jc.addJointMesh((JointMesh) parentNode.getChild(i));
        }
    }

    private void nullAll() {
        myTris=null;
        myVerts=null;
        finalNode=null;
    }

    private boolean readJoints() throws IOException {
        float fAnimationFPS=inFile.readFloat();
        float curTime=inFile.readFloat();     // Ignore currentTime
        int iTotalFrames=inFile.readInt();      // Ignore total Frames
        int nNumJoints=inFile.readUnsignedShort();
        if (nNumJoints==0) return false;
        String[] jointNames=new String[nNumJoints];
        String[] parentNames=new String[nNumJoints];
        JointController jc=new JointController(nNumJoints);
        jc.FPS=fAnimationFPS;

        for (int i=0;i<nNumJoints;i++){
            inFile.readByte();  // Ignore flags
            inFile.readFully(tempChar,0,32);
            jointNames[i]=cutAtNull(tempChar);
            inFile.readFully(tempChar,0,32);
            parentNames[i]=cutAtNull(tempChar);
            jc.localRefMatrix[i].setEulerRot(inFile.readFloat(),inFile.readFloat(),inFile.readFloat());
            jc.localRefMatrix[i].setTranslation(inFile.readFloat(),inFile.readFloat(),inFile.readFloat());
            int numKeyFramesRot=inFile.readUnsignedShort();
            int numKeyFramesTrans=inFile.readUnsignedShort();
            for (int j=0;j<numKeyFramesRot;j++)
                jc.setRotation(i,inFile.readFloat(),inFile.readFloat(),inFile.readFloat(),inFile.readFloat());
            for (int j=0;j<numKeyFramesTrans;j++)
                jc.setTranslation(i,inFile.readFloat(),inFile.readFloat(),inFile.readFloat(),inFile.readFloat());

        }
        for (int i=0;i<nNumJoints;i++){
            jc.parentIndex[i]=-1;
            for (int j=0;j<nNumJoints;j++){
                if (parentNames[i].equals(jointNames[j])) jc.parentIndex[i]=j;
            }
        }
        jc.setRepeatType(Controller.RT_WRAP);
        finalNode.addController(jc);
        addJointMeshes(finalNode, jc);
        return true;
    }

    private void readMats() throws IOException {
        int nNumMaterials=inFile.readUnsignedShort();
        for (int i=0;i<nNumMaterials;i++){
            inFile.skipBytes(32);   // Skip the name, unused
            MaterialState matState=DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
            matState.setAmbient(getNextColor());
            matState.setDiffuse(getNextColor());
            matState.setSpecular(getNextColor());
            matState.setEmissive(getNextColor());
            matState.setShininess(inFile.readFloat());
            float alpha = inFile.readFloat();
            matState.getDiffuse().a = alpha;
            matState.getEmissive().a = alpha;
            matState.getAmbient().a = alpha;
            
            inFile.readByte();      // Mode is ignored

            inFile.readFully(tempChar,0,128);
            TextureState texState=null;
            String texFile=cutAtNull(tempChar);
            if (texFile.length()!=0){
                URL texURL = ResourceLocatorTool.locateResource(
                        ResourceLocatorTool.TYPE_TEXTURE, texFile);
                if (texURL != null) {
                    texState = DisplaySystem.getDisplaySystem().getRenderer()
                    .createTextureState();
                    Texture tempTex = new Texture2D();
                    tempTex.setTextureKey(new TextureKey(texURL, true,
                            TextureManager.COMPRESS_BY_DEFAULT ? Image.Format.Guess
                                    : Image.Format.GuessNoCompression));
                    tempTex.setAnisotropicFilterPercent(0.0f);
                    tempTex.setMinificationFilter(Texture.MinificationFilter.BilinearNearestMipMap);
                    tempTex.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
                    tempTex.setWrap(Texture.WrapMode.Repeat);
                    texState.setTexture(tempTex);
                }
            }
            inFile.readFully(tempChar,0,128);   // Alpha map, but it is ignored
            //TODO: Implement Alpha Maps

            applyStates(matState,texState,i);
        }
    }

    /**
     * Every child of finalNode (IE the .ms3d file) whos materialIndex is the given index, gets the two RenderStates applied
     * @param matState A Material state to apply
     * @param texState A TextureState to apply
     * @param index The index that a TriMesh should have from <code>materialIndex</code> to get the given
     * states
     */
    private void applyStates(MaterialState matState, TextureState texState,int index) {
        for (int i=0;i<finalNode.getQuantity();i++){
            if (materialIndexes[i]==index){
                if (matState!=null) ((TriMesh)finalNode.getChild(i)).setRenderState(matState);
                if (texState!=null) ((TriMesh)finalNode.getChild(i)).setRenderState(texState);
            }
        }
    }

    private ColorRGBA getNextColor() throws IOException {
        return new ColorRGBA(
                inFile.readFloat(),
                inFile.readFloat(),
                inFile.readFloat(),
                inFile.readFloat()
        );
    }

    private void readGroups() throws IOException {
        int nNumGroups=inFile.readUnsignedShort();
        materialIndexes=new int[nNumGroups];
        for (int i=0;i<nNumGroups;i++){
            inFile.readByte();      // Ignore flags
            inFile.readFully(tempChar,0,32);    // Name
            int numTriLocal=inFile.readUnsignedShort();
            Vector3f[] meshVerts=new Vector3f[numTriLocal*3],meshNormals=new Vector3f[numTriLocal*3];
            Vector3f[] origVerts=new Vector3f[numTriLocal*3],origNormals=new Vector3f[numTriLocal*3];
            Vector2f[] meshTexCoords=new Vector2f[numTriLocal*3];
            int[] meshIndex=new int[numTriLocal*3];
            int[] jointIndex=new int[numTriLocal*3];
            JointMesh theMesh=new JointMesh(cutAtNull(tempChar));

            for (int j=0;j<numTriLocal;j++){
                int triIndex=inFile.readUnsignedShort();
                for (int k=0;k<3;k++){
                    meshVerts       [j*3+k]=myVerts[myTris[triIndex].vertIndicies[k]].vertex;
                    meshNormals     [j*3+k]=myTris[triIndex].vertNormals[k];
                    meshTexCoords   [j*3+k]=myTris[triIndex].vertTexCoords[k];
                    meshIndex       [j*3+k]=j*3+k;
                    origVerts       [j*3+k]=meshVerts[j*3+k];
                    origNormals     [j*3+k]=meshNormals[j*3+k];
                    jointIndex      [j*3+k]=myVerts[myTris[triIndex].vertIndicies[k]].boneID;
                }
            }
            theMesh.reconstruct(BufferUtils.createFloatBuffer(meshVerts),
                    BufferUtils.createFloatBuffer(meshNormals), null,
                    TexCoords.makeNew(meshTexCoords), 
                    BufferUtils.createIntBuffer(meshIndex));
            theMesh.originalNormal=origNormals;
            theMesh.originalVertex=origVerts;
            theMesh.jointIndex=jointIndex;
            finalNode.attachChild(theMesh);
            materialIndexes[i]=inFile.readByte();
        }
    }

    private void readTriangles() throws IOException {
        nNumTriangles=inFile.readUnsignedShort();
        myTris=new MilkTriangle[nNumTriangles];
        for (int i=0;i<nNumTriangles;i++){
            int j;
            myTris[i]=new MilkTriangle();
            inFile.readUnsignedShort(); // Ignore flags
            for (j=0;j<3;j++)
                myTris[i].vertIndicies[j]=inFile.readUnsignedShort();
            for (j=0;j<3;j++){
                myTris[i].vertNormals[j]=new Vector3f(
                        inFile.readFloat(),
                        inFile.readFloat(),
                        inFile.readFloat()
                );
            }
            for (j=0;j<3;j++){
                myTris[i].vertTexCoords[j]=new Vector2f();
                myTris[i].vertTexCoords[j].x=inFile.readFloat();
            }
            for (j=0;j<3;j++)
                myTris[i].vertTexCoords[j].y=1-inFile.readFloat();
            inFile.readByte();      // Ignore smoothingGroup
            inFile.readByte();      // Ignore groupIndex
        }
    }

    private void readVerts() throws IOException {
        nNumVertices=inFile.readUnsignedShort();
        myVerts=new MilkVertex[nNumVertices];
        for (int i=0;i<nNumVertices;i++){
            myVerts[i]=new MilkVertex();
            inFile.readByte(); // Ignore flags
            myVerts[i].vertex=new Vector3f(
                    inFile.readFloat(),
                    inFile.readFloat(),
                    inFile.readFloat()
            );
            myVerts[i].boneID=inFile.readByte();
            inFile.readByte();  // Ignore referenceCount
        }
    }

    private void checkHeader() throws IOException {
        inFile.readFully(tempChar,0,10);
        if (!"MS3D000000".equals(new String(tempChar,0,10))) throw new JmeException("Wrong File type: not Milkshape file??");
        if (inFile.readInt()!=4) throw new JmeException("Wrong file version: Not 4");   // version
    }

    private static class MilkVertex{
        Vector3f vertex;
        byte boneID;
    }
    private static class MilkTriangle{
        int[] vertIndicies=new int[3];              // 3 ints
        Vector3f[] vertNormals=new Vector3f[3];     // 3 Vector3fs
        Vector2f[] vertTexCoords=new Vector2f[3];   // 3 Texture Coords
    }
    private static String cutAtNull(byte[] inString) {
        for (int i=0;i<inString.length;i++)
            if (inString[i]==0) return new String(inString,0,i);
        return new String(inString);
    }

    /**
     * This function returns the controller of a loaded Milkshape3D model.  Will return
     * null if a correct JointController could not be found, or if one does not exist.
     * @param model The model that was loaded.
     * @return The controller for that milkshape model.
     */
    public static JointController findController(Node model){
        if (model.getQuantity()==0 ||
                model.getChild(0).getControllers().size()==0 ||
                !(model.getChild(0).getController(0) instanceof JointController))
            return null;
        return (JointController) (model.getChild(0)).getController(0);
    }
}