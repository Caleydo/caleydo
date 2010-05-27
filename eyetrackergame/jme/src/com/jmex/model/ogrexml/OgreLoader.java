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

package com.jmex.model.ogrexml;

import com.jmex.model.ogrexml.anim.*;

import com.jme.bounding.BoundingBox;
import java.io.IOException;
import java.net.URL;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.io.InputStream;
import java.util.HashMap;
import org.w3c.dom.Node;

import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.TriMesh.Mode;
import com.jme.util.geom.BufferUtils;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.ResourceLocator;
import com.jme.util.resource.RelativeResourceLocator;
import com.jmex.model.ModelFormatException;
import static com.jmex.model.XMLUtil.*;

/**
 * Loads Ogre MESH.XML and SKELETON.XML files<br/>
 *
 * You must call OgreLoader.setMaterials otherwise OgreLoader won't
 * find the materials.
 *
 * @author Momoko_Fan
 * @see <A href="http://ogre.svn.sourceforge.net/viewvc/ogre/trunk/Tools/XMLConverter/docs/ogremeshxml.dtd"
 *       target="other1">Ogre's *.mesh.xml DTD</A>.
 * @see <A href="http://ogre.svn.sourceforge.net/viewvc/ogre/trunk/Tools/XMLConverter/docs/ogreskeletonxml.dtd"
 *       target="other2">Ogre's *.skeleton.xml DTD</A>.
 */
public class OgreLoader {

    private static final Logger logger = Logger.getLogger(OgreLoader.class.getName());

    private static NumberFormat intFormatter =
            NumberFormat.getIntegerInstance();
    static {
        intFormatter.setMinimumIntegerDigits(3);
        intFormatter.setGroupingUsed(false);
    }

    /**
     * sharedgeom contains all the sharedgeometry vertexbuffers defined in the mesh
     * file combined together. The vertexes are referenced by the submeshes through the index buffers.
     * Since jME does not really support shared vertex buffers yet, those references
     * copied into the submeshes' trimeshes.
     */
    private OgreMesh sharedgeom;

    /**
     * List of submeshes. Contains all submeshes in the file.
     */
    private List<OgreMesh> submeshes = new ArrayList<OgreMesh>();

    /**
     * Node containing all renderable meshes in the file.
     * Returned by the call loadMesh()
     */
    private OgreEntityNode rootnode;

    /**
     * Skeleton of this mesh, if the mesh is bone-animated
     */
    private Skeleton skeleton;

    /**
     * Bone and/or mesh animations for this mesh.
     */
    private Map<String, Animation> animations = new HashMap<String, Animation>();

    /**
     * A mapping of the material names to the material object.
     * The mapping of materials must be seperately loaded by the MaterialLoader,
     * or generated in code.
     */
    private Map<String, Material> materialMap;

    /**
     * Show debugging messages of OgreLoaders or not.
     */
    private static final boolean DEBUG = false;

    /**
     * Print a debugging message to standard output.
     *
     * @deprecated  If you want to see debugging messages, you should use the
     *              logging infrastructure which is there for this purpose.
     *              This allows you to specify when to see messages
     *              declaratively, without changing any source code.
     */
    @Deprecated
    public void println(String str){
        logger.fine(str);
    }

    /**
     * Applies a named material to the specified spatial.
     *
     * @param name
     * @param target
     */
    private void applyMaterial(String name, Spatial target){
        // no materials mapping defined, do not apply any materials
        if (materialMap == null)
            return;

        // find the named material and apply it to the target
        Material mat = materialMap.get(name);
        if (mat != null){
            mat.apply(target);
        }else{
            logger.warning("Cannot find material " + name
                    + " for submesh '" + target.getName() + "'");
        }
    }

    /**
     * Specify the mapping of materials to use when reading submeshes.
     * @param materials
     */
    public void setMaterials(Map<String, Material> materials){
        materialMap = materials;
    }

    /**
     * Legacy wrapper.
     *
     * @see #loadModel(URL, String)
     */
    public OgreEntityNode loadModel(URL url)
            throws IOException, ModelFormatException {
        return loadModel(url, null);
    }

    /**
     * Load a MESH.XML model from the specified URL,
     * automatically adding the containing directory to the Model resource
     * locator paths for the duration of the load (in order to pull in
     * reference skeletons files).
     *
     * @param url The URL that specifies the mesh.xml file
     * @param nodeName  The name of the generated OgreNode.
     *                  If null, then will use the last segment of the URL.
     * @return The model loaded
     * @see RelativeResourceLocator
     */
    public OgreEntityNode loadModel(URL url, String nodeName)
            throws IOException, ModelFormatException {
        logger.fine("MESH("+url.getFile()+")");
        String name = null;
        if (nodeName == null) {
            String urlPath = url.getPath();
            if (urlPath == null) {
                throw new IOException("URL contains no path: " + url);
            }
            name = urlPath.replaceFirst(".*[\\\\/]", "").
                      replaceFirst("\\..*", "");
            if (name.length() < 1) {
                name = "OgreNode";
                logger.warning("Falling back to node name 'OgreNode', since "
                        + "failed to generate a good name from URL '"
                        + url + "'");
            }
        } else {
            name = nodeName;
        }
        ResourceLocator locator = null;
        try {
            locator = new RelativeResourceLocator(url);
        } catch (URISyntaxException use) {
            throw new RuntimeException(use);
            // Will not get here, since our source URL has been validated.
        }
        ResourceLocatorTool.addResourceLocator(
                ResourceLocatorTool.TYPE_MODEL, locator);
          // This is to automatically load referenced Skeleton files located
          // alongside the mesh file.
        InputStream stream = null;
        try {
            return loadMesh(loadDocument(url.openStream(), "mesh"), name);
        } finally {
            ResourceLocatorTool.removeResourceLocator(
                    ResourceLocatorTool.TYPE_MODEL, locator);
            locator = null;  // Just to encourage GC
        }
    }

    private IntBuffer loadLODFaceList(Node lodfacelistNode){
        int faces = getIntAttribute(lodfacelistNode, "numfaces");
        Node face = lodfacelistNode.getFirstChild();
        IntBuffer ib = BufferUtils.createIntBuffer(faces*3);
        while (face != null){
            if (!face.getNodeName().equals("face")){
                face = face.getNextSibling();
                continue;
            }

            String nv1 = getAttribute(face, "v1");
            String nv2 = getAttribute(face, "v2");
            String nv3 = getAttribute(face, "v3");

            ib.put(Integer.parseInt(nv1));
            ib.put(Integer.parseInt(nv2));
            ib.put(Integer.parseInt(nv3));

            face = face.getNextSibling();
        }
        ib.flip();
        return ib;
    }

    private void loadLOD(Node lodNode){
        Node lodgeneratedNode = lodNode.getFirstChild();
        int numLevels = getIntAttribute(lodNode, "numlevels")-1;
        int curLevel = 0;

        if (getBoolAttribute(lodNode, "manual", false) == true){
            logger.warning("Manual LOD not supported, ignored.");
        }

        IntBuffer[][] lodLevelsArray = new IntBuffer[submeshes.size()][numLevels];
        while (lodgeneratedNode != null){
            if (lodgeneratedNode.getNodeName().equals("lodgenerated")){
                Node lodfacelistNode = lodgeneratedNode.getFirstChild();
                while (lodfacelistNode != null){
                    if (lodfacelistNode.getNodeName().equals("lodfacelist")){
                        int index = getIntAttribute(lodfacelistNode, "submeshindex");
                        lodLevelsArray[index][curLevel] = loadLODFaceList(lodfacelistNode);
                    }

                    lodfacelistNode = lodfacelistNode.getNextSibling();
                }
                curLevel++;
            }

            lodgeneratedNode = lodgeneratedNode.getNextSibling();
        }

        for (int i = 0; i < submeshes.size(); i++){
            submeshes.get(i).setLodLevels(lodLevelsArray[i]);
        }
    }

    /**
     * Append a vertexbuffer element onto a TriMesh
     * @param target
     * @param vertexbuffer
     */
    private void loadVertexBuffer(OgreMesh mesh, Node vertexbuffer)
            throws IOException, ModelFormatException {
        // read all buffers
        FloatBuffer vb = mesh.getVertexBuffer();
        FloatBuffer nb = mesh.getNormalBuffer();
        FloatBuffer cb = mesh.getColorBuffer();
        FloatBuffer tanb = mesh.getTangentBuffer();
        FloatBuffer binb = mesh.getBinormalBuffer();
        ArrayList<TexCoords> tb = mesh.getTextureCoords();

        // next texture coordinate buffer
        int startCoordIndex = tb.size();

        if (mesh.getVertexCount() == 0)
            throw new IOException("Invalid vertex count value");

        // vertex positions
        if (getBoolAttribute(vertexbuffer, "positions", false)){
            if (vb == null)
                vb = BufferUtils.createFloatBuffer(mesh.getVertexCount() * 3);
        }

        // vertex normals
        if (getBoolAttribute(vertexbuffer, "normals", false)){
            if (nb == null)
                nb = BufferUtils.createFloatBuffer(mesh.getVertexCount() * 3);
        }

        // texture coordinates
        int texbuffersN = getIntAttribute(vertexbuffer, "texture_coords", 0);
        if (texbuffersN != 0){
            if (tb == null)
                tb = new ArrayList<TexCoords>();

            for (int i = 0; i < texbuffersN; i++){
                // read dimensions
                // default is 2 dimensions for texcoord (UV)
                int dimensions = getIntAttribute(vertexbuffer, "texture_coord_dimensions_"+i, 2);

                FloatBuffer texCoords = BufferUtils.createFloatBuffer(mesh.getVertexCount()*dimensions);
                tb.add(new TexCoords(texCoords, dimensions));
            }
        }

        // vertex colors
        if (getBoolAttribute(vertexbuffer, "colours_diffuse", false)){
            if (cb == null)
                cb = BufferUtils.createFloatBuffer(mesh.getVertexCount()*4);
        }

        // specular/secondary colors
        // not supported as of jME 2.0, ignore
        if (getBoolAttribute(vertexbuffer, "colours_specular", false)){
            logger.warning("Specular colors are not supported!");
        }

        // tangents
        int tangentDimensions = getIntAttribute(vertexbuffer, "tangent_dimensions", 3);
        if (getBoolAttribute(vertexbuffer, "tangents", false)){
            if (tanb == null)
                tanb = BufferUtils.createFloatBuffer(mesh.getVertexCount()*tangentDimensions);
        }

        // binormals
        if (getBoolAttribute(vertexbuffer, "binormals", false)){
            if (binb == null)
                binb = BufferUtils.createFloatBuffer(mesh.getVertexCount()*3);
        }

        // set the buffers in case any were created
        mesh.setVertexBuffer(vb);
        mesh.setNormalBuffer(nb);
        mesh.setColorBuffer(cb);
        mesh.setTangentBuffer(tanb);
        mesh.setBinormalBuffer(binb);
        mesh.setTextureCoords(tb);

        // Read the vertexbuffer
        Node vertex = vertexbuffer.getFirstChild();
        while (vertex != null){
            if (!vertex.getNodeName().equals("vertex")){
                vertex = vertex.getNextSibling();
                continue;
            }

            Node position = getChildNode(vertex, "position");
            if (position != null){
                vb.put(getFloatAttribute(position, "x"))
                  .put(getFloatAttribute(position, "y"))
                  .put(getFloatAttribute(position, "z"));
            }

            Node normal = getChildNode(vertex, "normal");
            if (normal != null){
                nb.put(getFloatAttribute(normal, "x"))
                  .put(getFloatAttribute(normal, "y"))
                  .put(getFloatAttribute(normal, "z"));
            }

            Node tangent = getChildNode(vertex, "tangent");
            if (tangent != null){
                tanb.put(getFloatAttribute(tangent, "x"))
                    .put(getFloatAttribute(tangent, "y"))
                    .put(getFloatAttribute(tangent, "z"));
                if (tangentDimensions == 4)
                    tanb.put(getFloatAttribute(tangent, "w"));
            }

            Node binormal = getChildNode(vertex, "binormal");
            if (binormal != null){
                binb.put(getFloatAttribute(binormal, "x"))
                    .put(getFloatAttribute(binormal, "y"))
                    .put(getFloatAttribute(binormal, "z"));
            }

            if (tb != null){
                Node texcoord = vertex.getFirstChild();
                int texCoordIndex = 0;
                while (texcoord != null){
                    if (texcoord.getNodeName().equals("texcoord")){
                        TexCoords coords = tb.get(startCoordIndex + texCoordIndex);
                        FloatBuffer texbuf = coords.coords;

                        texbuf.put(getFloatAttribute(texcoord, "u"));
                        if (coords.perVert > 1){
                            texbuf.put(getFloatAttribute(texcoord, "v"));
                            if (coords.perVert == 3)
                                texbuf.put(getFloatAttribute(texcoord, "w"));
                        }

                        texCoordIndex++;
                    }

                    texcoord = texcoord.getNextSibling();
                }
            }

            Node color = getChildNode(vertex, "colour_diffuse");
            if (color != null){
                String colorString = getAttribute(color, "value");
                Matcher floatMatcher = float3Pattern.matcher(colorString);
                if (!floatMatcher.matches())
                    floatMatcher = float4Pattern.matcher(colorString);
                if (!floatMatcher.matches())
                    throw new ModelFormatException(
                            "Malformatted Color value: " + colorString);
                cb.put(Float.parseFloat(floatMatcher.group(1)))
                        .put(Float.parseFloat(floatMatcher.group(2)))
                        .put(Float.parseFloat(floatMatcher.group(3)))
                        .put((floatMatcher.groupCount() == 4)
                            ? Float.parseFloat(floatMatcher.group(4)) : 1.0f);
            }

            vertex = vertex.getNextSibling();
        }

        mesh.setVertexCount(vb.position() / 3);
    }

    /**
     * Loads a submesh from an XML node
     *
     * @param submesh XML node
     * @return
     */
    private OgreMesh loadSubmesh(Node submesh)
            throws IOException, ModelFormatException {
        OgreMesh mesh = new OgreMesh();
        mesh.getTextureCoords().clear();

        mesh.setName(rootnode.getName() + "Mesh"
                + intFormatter.format(rootnode.getQuantity()));

        // ==material==
        // try to load a material if it is defined
        String material = getAttribute(submesh, "material");
        if (material != null)
            applyMaterial(material, mesh);

        // ==usesharedvertices==
        // using shared verticles?
        boolean sharedVerts = getBoolAttribute(submesh, "usesharedvertices", true);

        // ==operationtype==
        // determine triangle mode
        String operationtype = getAttribute(submesh, "operationtype", "triangle_list");
        if (operationtype.equals("triangle_list"))
            mesh.setMode(Mode.Triangles);
        else if (operationtype.equals("triangle_strip"))
            mesh.setMode(Mode.Strip);
        else if (operationtype.equals("triangle_fan"))
            mesh.setMode(Mode.Fan);
        else
            logger.warning("Invalid triangle mode specified, assuming indexed triangles");

        int vertexCount = -1;
        if (!sharedVerts){
            // ==geometry==
            Node geometry = getChildNode(submesh, "geometry");
            vertexCount = getIntAttribute(geometry, "vertexcount");

            int startVertexCount = mesh.getVertexCount();
            mesh.setVertexCount(vertexCount);

            // ==vertexbuffer==
            Node vertexbuffer = geometry.getFirstChild();
            while (vertexbuffer != null){
                if (vertexbuffer.getNodeName().equals("vertexbuffer")){
                    // inherit geometry data from unique vertex buffer defined here
                    loadVertexBuffer(mesh, vertexbuffer);
                }
                vertexbuffer = vertexbuffer.getNextSibling();
            }

            int deltaVertexCount = mesh.getVertexCount() - startVertexCount;
            assert deltaVertexCount == vertexCount;
        }else{
            // inherit geometry data from shared geometry
            mesh.setVertexBuffer(sharedgeom.getVertexBuffer());
            mesh.setNormalBuffer(sharedgeom.getNormalBuffer());
            mesh.setColorBuffer(sharedgeom.getColorBuffer());
            mesh.setTangentBuffer(sharedgeom.getTangentBuffer());
            mesh.setBinormalBuffer(sharedgeom.getBinormalBuffer());
            mesh.setTextureCoords(sharedgeom.getTextureCoords());
        }

        // ==faces==
        // check if faces definition exists (required for submesh)
        Node faces = getChildNode(submesh, "faces");
        if (faces == null){
            throw new IOException("Cannot load submesh: faces definition required");
        }

        // Read face/triangle data
        int faceCount = getIntAttribute(faces, "count");

        // mesh -> submesh -> faces -> face
        Node face = faces.getFirstChild();
        IntBuffer ib = BufferUtils.createIntBuffer(faceCount*3);
        while (face != null){
            if (face.getNodeType() == Node.TEXT_NODE
                            || face.getNodeType() == Node.COMMENT_NODE){
                face = face.getNextSibling();
                continue;
            }

            String nv1 = getAttribute(face, "v1");
            String nv2 = getAttribute(face, "v2");
            String nv3 = getAttribute(face, "v3");

            ib.put(Integer.parseInt(nv1));
            if (nv2 != null && nv3 != null)
                ib.put(Integer.parseInt(nv2)).put(Integer.parseInt(nv3));

            face = face.getNextSibling();
        }

        // Assign index buffer
        mesh.setIndexBuffer(ib);

        // ignore weights if no skeleton defined
        // also ignore if geometry data is shared,
        // as weights are specified in the root mesh object and not in a submesh
        if (skeleton != null && !sharedVerts){
            // ==boneassignments==
            Node boneassignments = getChildNode(submesh, "boneassignments");
            if (boneassignments != null){
                // ==vertexboneassignment==
                // assign weights to skin
                WeightBuffer wb = BoneAnimationLoader.loadWeightBuffer(boneassignments, vertexCount);
                mesh.setWeightBuffer(wb);
            }
        }

        rootnode.attachChild(mesh);
        submeshes.add(mesh);

        return mesh;
    }

    /**
     * @param nodeName  Name of the generated OgreNode.
     *                  If null, then will use the literal "OgreNode".
     */
    private OgreEntityNode loadMesh(Node meshNode, String nodeName)
            throws IOException, ModelFormatException {
        rootnode = new OgreEntityNode(
                (nodeName == null) ? "OgreNode" : nodeName);

        // ==skeletonlink==
        Node skeletonlinkNode = getChildNode(meshNode, "skeletonlink");
        if (skeletonlinkNode != null){
            String name = getAttribute(skeletonlinkNode, "name") + ".xml";

            URL skeletonURL = ResourceLocatorTool.locateResource(
                    ResourceLocatorTool.TYPE_MODEL, name);

            if (skeletonURL != null) {
                InputStream in = skeletonURL.openStream();
                Node skeletonNode = loadDocument(in, "skeleton");
                if (skeletonNode == null) {
                    logger.warning("Proceeding without skeleton because could "
                            + "not access the skeleton file");
                } else {
                    skeleton = SkeletonLoader.loadSkeleton(skeletonNode);

                    Node animationsNode = getChildNode(skeletonNode, "animations");
                    if (animationsNode != null){
                        BoneAnimationLoader.loadAnimations(animationsNode, skeleton, animations);
                    }
                    logger.finest("Successfully loaded bone animations");
                }
                in.close();
            }
        }

        // ==sharedgeometry==
        Node sharedgeometryNode = getChildNode(meshNode, "sharedgeometry");
        if (sharedgeometryNode != null){
            sharedgeom = new OgreMesh("OgreSharedMesh");

            int vertexCount = getIntAttribute(sharedgeometryNode, "vertexcount");
            sharedgeom.setVertexCount(vertexCount);

            // submeshes reference geometry from sharedgeom mesh
            // so itself it does not get drawn
            sharedgeom.setCullHint(CullHint.Always);

            Node vertexbuffer = sharedgeometryNode.getFirstChild();
            while (vertexbuffer != null){
                if (vertexbuffer.getNodeName().equals("vertexbuffer")){
                    loadVertexBuffer(sharedgeom, vertexbuffer);
                }

                vertexbuffer = vertexbuffer.getNextSibling();
            }
        }

        // ==submeshes==
        Node submeshesNode = getChildNode(meshNode, "submeshes");
        Node submesh = submeshesNode.getFirstChild();
        if (submesh == null) {
            throw new ModelFormatException(
                    "Mesh file has no submeshes, as required by the DTD");
        }
        while (submesh != null){
            if (submesh.getNodeName().equals("submesh")){
                // ==submesh==
                loadSubmesh(submesh);
            }

            submesh = submesh.getNextSibling();
        }

        // ==boneassignments==
        // for sharedgeometry
        Node boneassignments = getChildNode(meshNode, "boneassignments");
        if (boneassignments != null){
            // bone assignments defined for shared geometry,
            if (boneassignments != null){
                // ==vertexboneassignment==
                // assign weights to skin
                int vertexCount = sharedgeom.getVertexCount();
                WeightBuffer wb = BoneAnimationLoader.loadWeightBuffer(boneassignments, vertexCount);
                sharedgeom.setWeightBuffer(wb);
            }
        }

        // ==submeshnames==
        Node submeshnamesNode = getChildNode(meshNode, "submeshnames");
        if (submeshnamesNode != null){
            Node submeshname = submeshnamesNode.getFirstChild();
            while (submeshname != null){
                if (submeshname.getNodeName().equals("submeshname")){
                    // This overrides the derived name set int the
                    // loadSubmesh() method.
                    rootnode.getChild(getIntAttribute(submeshname, "index"))

                            .setName(getAttribute(submeshname, "name"));
                }

                submeshname = submeshname.getNextSibling();
            }
        }

        OgreMesh[] targets;
        if (sharedgeom == null){
            targets = new OgreMesh[submeshes.size()];
            submeshes.toArray(targets);
        }else{
            // only need to update the verticles and normals
            // of sharedgeom-based meshes, so no need to associate
            // the submeshes into this (submeshes only have unique index buffers)
            targets = new OgreMesh[]{ sharedgeom };
        }

        // ==poses==
        Node posesNode = getChildNode(meshNode, "poses");
        Node animationsNode = getChildNode(meshNode, "animations");

        if (posesNode != null){
            List<Pose> poseList = MeshAnimationLoader.loadPoses(posesNode, sharedgeom, submeshes);
            if (animationsNode != null){
                MeshAnimationLoader.loadMeshAnimations(animationsNode, poseList, sharedgeom, submeshes, animations);
            }
        }

        // ===levelofdetail==
        Node lodNode = getChildNode(meshNode, "levelofdetail");
        if (lodNode != null){
            loadLOD(lodNode);
        }

        if (animations.size() > 0){
            MeshAnimationController controller = new MeshAnimationController(targets,
                                                                             skeleton,
                                                                             animations);
            rootnode.addController(controller);
        }
        logger.finer(
                Integer.toString(animations.size())
                +  " animations loaded for Mesh " + rootnode.getName());

        rootnode.setModelBound(new BoundingBox());
        rootnode.updateModelBound();

        rootnode.updateGeometricState(0, true);
        rootnode.updateRenderState();

        return rootnode;
    }
}
