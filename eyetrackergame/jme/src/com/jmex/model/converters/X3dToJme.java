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
/*
 * X3D model file loader for jMonkeyEngine (http://www.jmonkeyengine.com), 
 * written by Michael Sattler.
 * This loader is supposed to support as many of X3D's features as possible, 
 * but currently only a subset of the features that could be implemented in jME 
 * is supported. Thus, you are encouraged to extend this loader at will to be 
 * able to use more features. All I'm asking is two things: 1) A little credit ;) 
 * And 2) Please stick to Sun's code conventions when editing this class 
 * (http://java.sun.com/docs/codeconv/html/CodeConvTOC.doc.html).
 */
package com.jmex.model.converters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;

import com.jme.bounding.BoundingBox;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.light.DirectionalLight;
import com.jme.light.Light;
import com.jme.light.PointLight;
import com.jme.light.SimpleLightNode;
import com.jme.light.SpotLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Spatial;
import com.jme.scene.SwitchNode;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cone;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Disk;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.BumpMapColorController;
import com.jme.util.CloneImportExport;
import com.jme.util.TextureKey;
import com.jme.util.TextureManager;
import com.jme.util.export.Savable;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.geom.BufferUtils;
import com.jme.util.geom.NonIndexedNormalGenerator;
import com.jme.util.geom.NormalGenerator;

/**
 * A Loader class to load models from XML-encoded X3D files (see <a
 * href="http://www.web3d.org/x3d/specifications/ISO-IEC-19775-X3DAbstractSpecification/X3D.html">this
 * link</a> for the specification) into jME.<br />
 * <br />
 * This Loader is designed to be able to load the geometry and lights contained
 * in an X3D file and set the necessary properties to properly set up a model
 * for jME. It uses as many of the features of X3D as possible.<br />
 * <br />
 * X3D is based on the ISO standard VRML97 (see <a
 * href="http://www.web3d.org/x3d/specifications/vrml/ISO-IEC-14772-VRML97/">the
 * ISO specification</a>), which was originally created as a language for
 * describing interactive 3D scenes. The goal of X3D was to split up the VRML
 * functionality into several Components (e.g. a Core Component, the Grouping
 * Component, the Geometry3D Component, the Texturing Component, and so on). The
 * Components are grouped into Profiles, which can be used to tell the X3D
 * browser which set of Components to load (e.g. the Interchange Profile, which
 * is used for basic geometry, the Immersive Profile, which supports more
 * geometry features and additionally interaction functionality, and so on).<br />
 * However, this loader is NOT designed to support certain Profiles or
 * Components. Instead it tries to load all the features that can be realized in
 * jME.<br />
 * <br />
 * The loader currently supports the following functions and X3D Elements:
 * <ul>
 * <li>Grouping (<i>Group</i>, <i>StaticGroup</i>, <i>Switch</i>,
 * <i>Transform</i>) and definition of bounding boxes for groups. In
 * <i>Transform</i>, the use of the <i>center</i> and <i>scaleOrientation</i>
 * attributes is NOT supported</li>
 * <li>3D solid geometry (<i>Box</i>, <i>Cone</i>, <i>Cylinder</i>,
 * <i>Sphere</i>, <i>IndexedFaceSet</i>). In <i>Cylinder</i>, the attributes
 * <i>side</i>, <i>bottom</i> and <i>top</i> are currently not fully
 * supported: If either <i>bottom</i> or <i>top</i> is <code>false</code>,
 * the bottom AND top of the cylinder are removed; <i>side</i> is ignored
 * altogether.</li>
 * <li>Line geometry (<i>LineSet</i>)</li>
 * <li>Appearance properties (<i>Shape</i>, <i>Appearance</i>, <i>Material</i>)
 * and bounding boxes for shapes</li>
 * <li>Texturing of geometry (<i>ImageTexture</i>, <i>MultiTexture</i>). The
 * use of <i>MultiTextureCoordinate</i> to specify different tex coords for the
 * textures of a <i>MultiTexture</i> is currently not supported; not sure if
 * this feature can be implemented in the future</li>
 * <li>Lights (<i>DirectionalLight</i>, <i>PointLight</i> and <i>SpotLight</i>).
 * <i>DirectionalLight</i> lights the whole scene instead of just its sibling
 * nodes in the scenegraph. For <i>PointLight</i> the attribute <i>radius</i>
 * is not supported. For <i>SpotLight</i> the attributes <i>radius</i> and
 * <i>beamWidth</i> are not supported. </li>
 * </ul>
 * <br />
 * The fields <i>DEF</i> and <i>USE</i> are actually supported too, but there
 * is one limitation with the USE of Objects with bump maps: The
 * BumpMapColorController attached to bumpmapped objects is not cloned by
 * CloneImportExport. When you try to re-USE a bumpmapped Appearance for a
 * different object, you can add a BumpMapColorController yourself to fix this
 * issue. This also applies to the method
 * {@link #convert(InputStream, OutputStream)}: As this method uses a binary
 * exporter to write the parsed scene to the output stream, the
 * BumpMapColorController gets lost here too, so you have to set it again in
 * order to display the bump maps properly. <br />
 * 
 * @version 2008-03-11
 * @author Michael Sattler
 * @author Stephen Larson (LineSet, some bugfixes)
 */
public class X3dToJme extends FormatConverter {

    private static final Logger logger = Logger.getLogger(X3dToJme.class
            .getName());

    /**
     * A regular expression that can be used in String's split-method. It causes
     * the method to split at any sequence of spaces, linefeeds or carriage
     * returns or a combination.
     */
    private static final String WHITESPACE_REGEX = "\\s+";

    /**
     * A regular expression that can be used in String's split-method. It causes
     * the method to split at any sequence of whitespace characters (see
     * <code>WHITESPACE_REGEX</code> or a comma (either with whitespaces or
     * not).
     */
    private static final String WHITESPACE_COMMA_REGEX = "(\\s*,\\s*)|\\s+";

    /** A List of all X3D scene node types this loader understands. */
    private static final String[] SCENE_NODE_TYPES = { "Group", "StaticGroup",
            "Transform", "Switch", "Shape", "DirectionalLight", "PointLight",
            "SpotLight" };

    /**
     * A list of geometry node types this loader understands. The most
     * frequently used types are at the beginning of the array for performance
     * reasons.
     */
    private static final String[] GEOMETRY_TYPES = { "Box", "IndexedFaceSet",
            "Sphere", "Cylinder", "Cone", "LineSet" };

    /** The default number of samples used for jME Spheres along the Z axis */
    private static final int SPHERE_Z_SAMPLES = 16;

    /** The default number of radial samples used for jME Spheres */
    private static final int SPHERE_RADIAL_SAMPLES = 16;

    /** The default number of samples used for jME Cylinders along the axis */
    private static final int CYLINDER_AXIS_SAMPLES = 2;

    /** The default number of radial samples used for jME Cylinders */
    private static final int CYLINDER_RADIAL_SAMPLES = 20;

    private DocumentBuilder documentBuilder;

    /**
     * Stores Objects ((arrays of) byte arrays) defined in the X3D file using
     * DEF
     */
    private Hashtable<String, Object> defs = new Hashtable<String, Object>();

    /**
     * Maps the paths of texture files to the InputStreams holding the texture
     * data
     */
    private Map<String, InputStream> texData;

    /** The LightState all Lights in the scene are attached to */
    private LightState lightState;

    /**
     * A normal generator used to generate the normals for IndexedFaceSets that
     * do not define normals themselves.
     */
    private NormalGenerator normalGenerator = new NormalGenerator();

    /**
     * A normal generator used to generate the normals for IndexedFaceSets that
     * do not define normals themselves but use normal, color or texCoord
     * indices.
     */
    private NonIndexedNormalGenerator nonIndexedNormalGenerator = new NonIndexedNormalGenerator();

    /**
     * Indicates whether the shape currently processed contains a Bump Map
     * Texture and therefore the Geometry needs a BumpMapColorController
     */
    private boolean createBumpController = false;

    /**
     * Indicates whether the appearance attributes for the currently processed
     * geometry contain transparency and therefore the geometry has to be added
     * to the transparency render queue
     */
    private boolean addToTransparentQueue = false;

    /**
     * Creates the X3DLoader.
     * 
     * @throws InstantiationException
     *             In case the XML DocumentBuilder cannot be instantiated
     */
    public X3dToJme() throws InstantiationException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            documentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new InstantiationException("X3DLoader creation failed: "
                    + "Unable to instantiate XML DocumentBuilder!");
        }
    }

    /**
     * Passes an <code>EntityResolver</code> to the
     * <code>DocumentBuilder</code> used to parse the X3D files' XML
     * structure. Using a resolver can save a lot of time when parsing X3D
     * files, because usually an HTTP connection to the Web3D.org server is
     * opened to get the DTD files for X3D. If you specify a resolver, you can
     * provide the DTDs on the local file system, for example.<br />
     * For details on how an EntityResolver works, please refer to the J2SE API
     * documentation.<br />
     * The utility class <code>X3DResolver</code> can be used to provide a
     * mapping of the file names to <code>InputStreams</code> containing the
     * files; see the documentation there.<br />
     * The DTDs usually needed for X3D parsing are:
     * <ul>
     * <li>x3d-3.0.dtd</li>
     * <li>x3d-3.0-InputOutputFields.dtd</li>
     * <li>x3d-3.0-Web3dExtensionsPrivate.dtd</li>
     * <li>x3d-3.0-Web3dExtensionsPublic.dtd</li>
     * </ul>
     * 
     * @param resolver
     *            An <code>EntityResolver</code> to resolve the DTD references
     *            for X3D
     */
    public void setDTDResolver(EntityResolver resolver) {
        documentBuilder.setEntityResolver(resolver);
    }

    /**
     * Converts the .x3d file read from the specified InputStream to the .jme
     * format and writes it to the specified OutputStream. If the model contains
     * any textures, please specify the folder containing the textures with
     * <code>setProperty("textures", new URL(folder))</code>. If you use this
     * method instead of {@link #loadScene(InputStream, Map, LightState)}, any
     * lights in the scene will always be attached to a LightState at the root
     * of this scene, thus only lighting the X3D scene itself and not the scene
     * containing it. Additionally, any <code>BumpMapColorController</code>s
     * created while loading the model will get lost in the exporting process,
     * so if the model contains any bump maps, the controller has to be set
     * manually when the exported model is being re-imported.
     * 
     * @param in
     *            The InputStream to read the .x3d file from
     * @param jmeOut
     *            The OutputStream to write the .jme model to
     * @throws IOException
     *             If an error occurs
     */
    @Override
    public void convert(InputStream in, OutputStream jmeOut) throws IOException {
        try {
            Spatial scene = loadScene(in, null, null);
            BinaryExporter.getInstance().save(scene, jmeOut);
        } catch (Exception e) {
            logger.info("Unable to load file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads a scene (model) from the X3D file contained in the specified input
     * stream.<br />
     * <strong>Please note:</strong> This loader is NOT thread-safe!
     * 
     * @param x3dIn
     *            An InputStream holding the X3D file
     * @param texData
     *            A Map mapping the paths of texture files to InputStreams
     *            holding the file content
     * @param lightState
     *            The LightState to attach scene lights to. If <code>null</code>
     *            is passed, a new LightState is created and attached to the
     *            model root, which causes the lights in the X3D scene to light
     *            only the X3D scene itself
     * @return The model, if it was successfully loaded, otherwise
     *         <code>null</code>
     * @throws An
     *             Exception, in case any error occurs
     */
    public Spatial loadScene(InputStream x3dIn,
            Map<String, InputStream> texData, LightState lightState)
            throws Exception {

        this.texData = texData;

        // Timer timer = Timer.getTimer();
        // timer.reset();

        // Parse the XML document, build the DOM-document
        Document doc;
        // try {
        doc = documentBuilder.parse(x3dIn);
        // } catch (Exception e) {
        // logger.info("Unable to parse X3D file: " + e + " ("
        // + e.getMessage() + ")");
        // return new com.jme.scene.Node();
        // }

        // float parsingTime = timer.getTimeInSeconds();
        // logger.info("X3D parsed in "+(parsingTime * 1000)+" ms");
        // timer.reset();

        // Create the scene root
        NodeList nodes = doc.getElementsByTagName("X3D");
        if (nodes.getLength() == 0) {
            logger.info("No X3D document root!");
            return new com.jme.scene.Node();
        }
        Node scene = getChildNode(nodes.item(0), "Scene");
        com.jme.scene.Node sceneRoot = new com.jme.scene.Node();

        /*
         * Check the LightState. If none has been passed, create a new one and
         * attach it to the scene root
         */
        if (lightState == null) {
            this.lightState = DisplaySystem.getDisplaySystem().getRenderer()
                    .createLightState();
            this.lightState.setEnabled(true);
            sceneRoot.setRenderState(this.lightState);
        } else {
            this.lightState = lightState;
        }

        // Set the scene's title
        Node worldInfo = getChildNode(scene, "WorldInfo");
        if (worldInfo != null) {
            Node titleNode = worldInfo.getAttributes().getNamedItem("title");
            if (titleNode != null) {
                sceneRoot.setName(titleNode.getNodeValue());
            }
        }

        // Check for a Layer3D child node. If one exists, use it instead of X3D
        Node layer3D = getChildNode(scene, "Layer3D");
        if (layer3D != null) {
            scene = layer3D;
        }

        // Process all child nodes
        Node child = scene.getFirstChild();
        while (child != null) {
            if (child.getNodeType() != Node.TEXT_NODE
                    && child.getNodeType() != Node.COMMENT_NODE) {
                Spatial node = parseNode(child);
                if (node != null) {
                    sceneRoot.attachChild(node);
                }
            }
            child = child.getNextSibling();
        }

        // Update the bounds of the root node and all children
        sceneRoot.setModelBound(new BoundingBox());
        sceneRoot.updateModelBound();

        // Reset all used data
        defs.clear();
        this.texData = null;
        this.lightState = null;
        this.addToTransparentQueue = false;
        this.createBumpController = false;
        try {
            x3dIn.close();
        } catch (IOException e) {
        }

        // parsingTime = timer.getTimeInSeconds();
        // logger.info("Scene parsed in "+(parsingTime * 1000)+" ms");

        return sceneRoot;
    }

    /**
     * Gets the child node with the specified name from the specified node.
     * 
     * @param node
     *            The node to get the child node from
     * @param name
     *            The name of the child element to get (case is ignored)
     * @return The first child node with the specified name, or
     *         <code>null</code> if such a node does not exist
     */
    private Node getChildNode(Node node, String name) {
        Node child = node.getFirstChild();
        while (child != null && child.getNodeName() != name) {
            child = child.getNextSibling();
        }
        return child;
    }

    /**
     * Parses a node in the DOM (grouping, shape or light node) and creates a
     * scene node according to the DOM node's attributes and child elements.
     * 
     * @param node
     *            The DOM node
     * @return The jME Node
     * @throws Exception
     *             In case an error occurs during parsing
     */
    private Spatial parseNode(Node node) throws Exception {
        // Check for the USE attribute
        Node use = node.getAttributes().getNamedItem("USE");
        if (use != null) {
            return (Spatial) getDef(use.getNodeValue());
        }

        // Parse the node
        String type = node.getNodeName();
        Spatial result = null;
        if (type.equals("Group") || type.equals("StaticGroup")
                || type.equals("Transform") || type.equals("Switch")) {
            result = parseGroup(node);
        } else if (type.equals("Shape")) {
            result = parseShape(node);
        } else if (type.equals("DirectionalLight") || type.equals("PointLight")
                || type.equals("SpotLight")) {
            result = parseLight(node);
        }

        // Check for the DEF attribute
        if (result != null) {
            Node def = node.getAttributes().getNamedItem("DEF");
            if (def != null) {
                result.setName(def.getNodeValue());
                CloneImportExport cloneEx = new CloneImportExport();
                cloneEx.saveClone(result);
                defs.put(def.getNodeValue(), cloneEx);
            }
        }

        return result;
    }

    /**
     * Gets a clone of a previously defined Savable.
     * 
     * @param result
     *            A Savable to store the clone data.
     * @param def
     *            The ID of the predefined Savable
     * @return The passed Savable, filled with the clone data, or
     *         <code>null</code> if no predefined object matching the ID was
     *         found
     */
    private Savable getDef(String def) {
        CloneImportExport cloneIn = (CloneImportExport) defs.get(def);
        if (cloneIn != null) {
            return cloneIn.loadClone();
        }
        return null;
    }

    /**
     * Parses a DOM Group, StaticGroup, TransformGroup or Switch node and
     * creates a jME node with the appropriate properties
     * 
     * @param node
     *            The DOM node
     * @return The jME node
     * @throws Exception
     *             In case an error occurs during parsing
     */
    private com.jme.scene.Node parseGroup(Node node) throws Exception {
        // Init node
        boolean isSwitch;
        com.jme.scene.Node group;
        if (node.getNodeName().equals("Switch")) {
            group = new SwitchNode();
            isSwitch = true;
        } else {
            group = new com.jme.scene.Node();
            isSwitch = false;
        }

        // Parse BoundingBox
        BoundingBox bbox = parseBoundingBox(node);
        if (bbox != null) {
            group.setModelBound(bbox);
        }

        // Parse children
        Node child = node.getFirstChild();
        while (child != null) {
            if (isSceneNodeType(child.getNodeName())) {
                Spatial subnode = parseNode(child);
                if (subnode != null) {
                    group.attachChild(subnode);
                }
            }
            child = child.getNextSibling();
        }

        // Extra settings for Switch and Transform nodes
        if (isSwitch) {
            String selection = node.getAttributes().getNamedItem("whichChoice")
                    .getNodeValue().trim();
            try {
                int item = Integer.parseInt(selection);
                ((SwitchNode) group).setActiveChild(item);
            } catch (NumberFormatException e) {
                ((SwitchNode) group).setActiveChild(-1);
            }
        } else if (node.getNodeName().equals("Transform")) {
            setTransformation(group, node);
        }

        return group;
    }

    /**
     * Creates a BoundingBox according to the attributes bboxCenter and bboxSize
     * of the given node.
     * 
     * @param node
     *            The node
     * @return A BoundingBox created from the attribute values, or
     *         <code>null</code> if the attributes were unavailable or
     *         specified a buggy or negative box.
     */
    private BoundingBox parseBoundingBox(Node node) {
        NamedNodeMap attrs = node.getAttributes();

        // Parse size
        Node sizeNode = attrs.getNamedItem("bboxSize");
        if (sizeNode == null) {
            return null;
        }
        String size = sizeNode.getNodeValue().trim();
        String[] split = size.split(WHITESPACE_REGEX);
        if (split.length < 3) {
            return null;
        }
        float sizeX = getFloat(split[0], -1);
        float sizeY = getFloat(split[1], -1);
        float sizeZ = getFloat(split[2], -1);
        if (sizeX < 0 || sizeY < 0 || sizeZ < 0) {
            return null;
        }

        // Parse center
        Node centerNode = attrs.getNamedItem("bboxCenter");
        if (centerNode == null) {
            return null;
        }
        String center = centerNode.getNodeValue().trim();
        split = center.split(WHITESPACE_REGEX);
        if (split.length < 3) {
            return null;
        }
        float centerX = getFloat(split[0], 0);
        float centerY = getFloat(split[1], 0);
        float centerZ = getFloat(split[2], 0);

        return new BoundingBox(new Vector3f(centerX, centerY, centerZ),
                sizeX * 0.5f, sizeY * 0.5f, sizeZ * 0.5f);
    }

    /**
     * Checks if the specified String represents a scene node type (i.e. a
     * group, shape or light type)
     * 
     * @param type
     *            The type String
     * @return <code>true</code>, if the String is a scene node type,
     *         otherwise <code>false</code>
     */
    private boolean isSceneNodeType(String type) {
        for (String node : SCENE_NODE_TYPES) {
            if (type.equals(node)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the translation, rotation and scaling values for the given scene
     * node according to the attributes of the DOM node, if it is a Transform
     * node.
     * 
     * @param sceneNode
     *            The scene node to set the transformation for
     * @param node
     *            The DOM node
     */
    private void setTransformation(com.jme.scene.Node sceneNode, Node node) {
        NamedNodeMap attrs = node.getAttributes();

        // Set translation
        Node translationNode = attrs.getNamedItem("translation");
        if (translationNode != null) {
            String translation = translationNode.getNodeValue().trim();
            String[] split = translation.split(WHITESPACE_REGEX);
            if (split.length >= 3) {
                float x = getFloat(split[0], 0f);
                float y = getFloat(split[1], 0f);
                float z = getFloat(split[2], 0f);
                sceneNode.setLocalTranslation(x, y, z);
            }
        }

        // Set rotation
        Node rotationNode = attrs.getNamedItem("rotation");
        if (rotationNode != null) {
            String rotation = rotationNode.getNodeValue().trim();
            String[] split = rotation.split(WHITESPACE_REGEX);
            if (split.length >= 4) {
                try {
                    float axisX = Float.parseFloat(split[0]);
                    float axisY = Float.parseFloat(split[1]);
                    float axisZ = Float.parseFloat(split[2]);
                    float angle = Float.parseFloat(split[3]);
                    // logger.info("Setting rotation: ("+axisX+", "+axisY+",
                    // "+axisZ+"), "+angle);
                    sceneNode.getLocalRotation().fromAngleAxis(angle,
                            new Vector3f(axisX, axisY, axisZ));
                } catch (NumberFormatException e) {
                }
            }
        }

        // Set scaling
        Node scaleNode = attrs.getNamedItem("scale");
        if (scaleNode != null) {
            String scale = scaleNode.getNodeValue().trim();
            String[] split = scale.split(WHITESPACE_REGEX);
            if (split.length >= 3) {
                float x = getFloat(split[0], 1f);
                float y = getFloat(split[1], 1f);
                float z = getFloat(split[2], 1f);
                // logger.info("Setting scaling: ("+x+", "+y+", "+z+")");
                sceneNode.setLocalScale(new Vector3f(x, y, z));
            }
        }
    }

    /**
     * Gets an integer value from the specified String.
     * 
     * @param num
     *            The String containing the value
     * @param standard
     *            A value to be used in case the String is <code>null</code>
     *            or the parsing fails
     * @return The resulting int value
     */
    private int getInt(String num, int standard) {
        if (num != null) {
            try {
                return Integer.parseInt(num);
            } catch (NumberFormatException e) {
            }
        }
        return standard;
    }

    /**
     * Gets a float value from the specified String.
     * 
     * @param num
     *            The String containing the value
     * @param standard
     *            A value to be used in case the String is <code>null</code>
     *            or the parsing fails
     * @return The resulting float value
     */
    private float getFloat(String num, float standard) {
        if (num != null) {
            try {
                return Float.parseFloat(num);
            } catch (NumberFormatException e) {
            }
        }
        return standard;
    }

    /**
     * Parses the contents of an X3D Shape node and its children and creates a
     * jME Node containing the geometry and appearance parameters read from the
     * X3D shape node.
     * 
     * @param node
     *            The Shape node
     * @return The jME Geometry
     * @throws Exception
     *             In case an error occurs during loading
     */
    private Spatial parseShape(Node node) throws Exception {
        com.jme.scene.Node shape = new com.jme.scene.Node();
        // Check for a bounding box definition
        BoundingBox bbox = parseBoundingBox(node);

        // Find the geometry and appearance nodes
        Node geometryNode = null;
        Node appearanceNode = null;
        Node child = node.getFirstChild();
        while (child != null) {
            if (child.getNodeName().equals("Appearance")) {
                appearanceNode = child;
            } else if (isGeometryType(child.getNodeName())) {
                geometryNode = child;
            }
            child = child.getNextSibling();
        }

        // Parse and add the geometry, if available
        Geometry geom = null;
        if (geometryNode != null) {
            geom = parseGeometry(geometryNode);
            if (geom != null) {
                shape.attachChild(geom);
            }
            if (geom != null && bbox != null) {
                geom.setModelBound(bbox);
            }
        }

        // Parse and set the appearance properties, if available
        createBumpController = false; // Reset the create controller flag
        addToTransparentQueue = false;
        if (appearanceNode != null) {
            RenderState[] states = parseAppearance(appearanceNode);
            if (states != null && geom != null) {
                /*
                 * Check if the parsed Appearance contained a Bump Map => set up
                 * a BumpMapColorController for the Geometry
                 */
                if (createBumpController) {
                    BumpMapColorController con = new BumpMapColorController(
                            geom);
                    geom.addController(con);
                }

                /*
                 * Check if the parsed Appearance contained transparency => add
                 * the geometry to the transparency renderQueue
                 */
                if (addToTransparentQueue) {
                    geom.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
                }

                // Apply the RenderStates
                for (RenderState state : states) {
                    if (state != null) {
                        shape.setRenderState(state);
                        // If there are multiple textures, copy the texCoords
                        // (DUMMY)
                        if (state instanceof TextureState
                                && ((TextureState) state)
                                        .getNumberOfSetTextures() > 1) {
                            copyTexCoords((TextureState) state, geom);
                        }
                    }
                }
            }
        }

        return shape;
    }

    /**
     * Checks if the given String represents one of the types of geometry nodes
     * this loader understands.
     * 
     * @param type
     *            The String to check
     * @return <code>true</code>, if the String contains a valid geometry
     *         type, otherwise <code>false</code>
     */
    private boolean isGeometryType(String type) {
        for (String geom : GEOMETRY_TYPES) {
            if (type.equals(geom)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses any X3D geometry node (Box, Cone, Cylinder, IndexedFaceSet,
     * Sphere) and creates a corresponding jME Geometry object.
     * 
     * @param node
     *            The X3D node
     * @return The jME Geometry
     * @throws Exception
     *             In case an error occurs during parsing
     */
    private Geometry parseGeometry(Node node) throws Exception {
        // Check for the USE attribute
        Node use = node.getAttributes().getNamedItem("USE");
        if (use != null) {
            return (Geometry) getDef(use.getNodeValue());
        }

        // Check for the DEF attribute
        Node def = node.getAttributes().getNamedItem("DEF");
        String title = null;
        if (def != null) {
            title = def.getNodeValue();
        }

        // Parse the Geometry
        Geometry geom = null;
        String nodeName = node.getNodeName();
        if (nodeName.equals("Box")) {
            geom = parseBox(node, title);
        } else if (nodeName.equals("IndexedFaceSet")) {
            geom = parseIFS(node, title);
        } else if (nodeName.equals("Sphere")) {
            geom = parseSphere(node, title);
        } else if (nodeName.equals("Cylinder")) {
            geom = parseCylinder(node, title);
        } else if (nodeName.equals("Cone")) {
            geom = parseCone(node, title);
        } else if (nodeName.equals("LineSet")) {
            geom = parseLS(node, title);
        }

        if (geom != null) {
            // Set up bounding volume
            geom.setModelBound(new BoundingBox());

            // Translate attribute "solid" into a CullState
            boolean solid = true; // X3D default: true
            Node solidNode = node.getAttributes().getNamedItem("solid");
            if (solidNode != null
                    && solidNode.getNodeValue().equalsIgnoreCase("false")) {
                solid = false;
            }
            if (solid) {
                CullState culling = DisplaySystem.getDisplaySystem()
                        .getRenderer().createCullState();
                culling.setCullFace(CullState.Face.Back);
                geom.setRenderState(culling);
            }

            // If the DEF attribute exists, store the Geometry for future USEs
            if (title != null) {
                CloneImportExport cloneEx = new CloneImportExport();
                cloneEx.saveClone(geom);
                defs.put(title, cloneEx);
            }
        }

        return geom;
    }

    /**
     * Parses an X3D Box node and creates a corresponding jME Box.
     * 
     * @param node
     *            The X3D Box node
     * @param title
     *            A title for the box. If <code>null</code> is passed, a
     *            generic title is used.
     * @return The jME Box
     */
    private Box parseBox(Node node, String title) {
        Box box = null;
        String size = node.getAttributes().getNamedItem("size").getNodeValue()
                .trim();
        String[] split = size.split(WHITESPACE_REGEX);
        if (split.length >= 3) {
            // jME interprets scaling as extent from center => size is scaled by
            // 0.5
            float x = getFloat(split[0], 0) * 0.5f;
            float y = getFloat(split[1], 0) * 0.5f;
            float z = getFloat(split[2], 0) * 0.5f;
            if (x > 0 && y > 0 && z > 0) {
                if (title != null) {
                    box = new Box(title, new Vector3f(), x, y, z);
                } else {
                    box = new Box("X3D_Box", new Vector3f(), x, y, z);
                }
            }
        }
        return box;
    }

    /**
     * Parses an X3D IndexedFaceSet node and creates a corresponding jME
     * Geometry node. Because of jME's limitation that there can only be one
     * index array that is used for vertices as well as normals, colors and
     * texture coordinates, this method uses a workaround for the X3D
     * IndexedFaceSet's colorIndex, normalIndex and texCoordIndex attributes:
     * The vertices are "expanded" according to the indices from the coordIndex
     * attributes, so that every three consecutive vertices define one triangle
     * and the corresponding index array looks like [0, 1, 2, 3, 4, ...];
     * polygons with more than three vertices are split up into triangles in the
     * process. The attributes colorPerVertex and normalPerVertex are ignored,
     * most likely resulting in an ArrayOutOfBoundsException or simply rendering
     * errors when they are used in the X3D file (this should be fixed sometime
     * in the future, so that colors or normals respectively are ignored
     * altogether if these attributes are used)
     * 
     * @param node
     *            The X3D IndexedFaceSet node
     * @param title
     *            The name for the node
     * @return The jME Geometry for the IndexedFaceSet
     */
    private Geometry parseIFS(Node node, String title) {
        // Parse the vertices
        float[] vertices = null;
        Node coords = getChildNode(node, "Coordinate");
        if (coords != null) {
            vertices = parseValues(coords, "point");
        }
        if (vertices == null) {
            return null;
        }

        // Parse the coord indices
        int[] indices = parseIndices(node, "coordIndex");
        boolean indicesAvailable = false;
        if (indices == null) {
            // No indices specified => Every three consecutive vertices define a
            // triangle
            indices = new int[vertices.length];
            for (int i = 0; i < indices.length; i++) {
                indices[i] = i;
            }
        } else {
            /*
             * Indices available => in order to also use color and normal
             * indices, the vertices list must be expanded
             */
            indicesAvailable = true;
        }

        // Parse the colors
        float[] colorValues = null;
        int colorSize = 3; // Number of values per color (Color or ColorRGBA)
        Node colors = getChildNode(node, "Color");
        if (colors == null) {
            colorSize = 4;
            colors = getChildNode(node, "ColorRGBA");
        }
        if (colors != null) {
            colorValues = parseValues(colors, "color");
        }

        // Parse the color indices
        int[] colorIndices = null;
        if (colorValues != null) {
            colorIndices = parseIndices(node, "colorIndex");
        }

        // Expand RGB color values to RGBA values
        if (colorValues != null && colorSize == 3) {
            float[] temp = new float[colorValues.length / 3 * 4];
            for (int i = 0; i * 3 < colorValues.length; i++) {
                temp[i * 4 + 0] = colorValues[i * 3 + 0];
                temp[i * 4 + 1] = colorValues[i * 3 + 1];
                temp[i * 4 + 2] = colorValues[i * 3 + 2];
                temp[i * 4 + 3] = 1.0f;
            }
            colorValues = temp;
            colorSize = 4;
        }

        // // If no colors are available, set up default colors
        // if (colorValues == null) {
        // colorValues = new float[indices.length * 4];
        // for (int i = 0; i < colorValues.length; i++) {
        // colorValues[i] = i;
        // }
        // }

        // Parse the normals
        float[] normalValues = null;
        Node normals = getChildNode(node, "Normal");
        if (normals != null) {
            normalValues = parseValues(normals, "vector");
        }

        // Parse the normal indices
        int[] normalIndices = null;
        if (normalValues != null) {
            normalIndices = parseIndices(node, "normalIndex");
        }

        // Get the crease angle, in case no normals are specified
        float creaseAngle = getFloat(node.getAttributes().getNamedItem(
                "creaseAngle").getNodeValue().trim(), 0);

        // Parse the texture coordinates
        // TODO: Enable MultiTextureCoordinate parsing!
        float[] texCoordValues = null;
        Node texCoords = getChildNode(node, "TextureCoordinate");
        if (texCoords != null) {
            texCoordValues = parseValues(texCoords, "point");
        }

        // Parse the texCoord indices
        int[] texCoordIndices = null;
        if (texCoordValues != null) {
            texCoordIndices = parseIndices(node, "texCoordIndex");
        }

        // If no tex coords are available, generate them
        if (texCoordValues == null) {
            texCoordValues = generateTexCoords(vertices);
        }

        // Create the Mesh
        TriMesh mesh;
        if (indicesAvailable && normalIndices == null && colorIndices == null
                && texCoordIndices == null) {

            // The mesh contains vertex indices, but no separate indices for
            // normals, colors or texCoords => The values can be used without
            // modification
            IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices);
            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices);
            FloatBuffer colorBuffer = BufferUtils
                    .createFloatBuffer(colorValues);
            FloatBuffer normalBuffer = BufferUtils
                    .createFloatBuffer(normalValues);
            FloatBuffer texCoordBuffer = BufferUtils
                    .createFloatBuffer(texCoordValues);
            mesh = new TriMesh(null, vertexBuffer, normalBuffer, colorBuffer,
                    new TexCoords(texCoordBuffer), indexBuffer);

            // If no normals are available, they are generated
            if (normalBuffer == null) {
                normalGenerator.generateNormals(mesh, creaseAngle);
            }
        } else {
            int[] indicesBackup = indices.clone();

            // If vertex indices are specified as well as normal/color/texCoord
            // indices, the vertices have to be expanded so that every three
            // consecutive vertices define on triangle
            if (indicesAvailable) {
                vertices = expandValues(vertices, indices, 3, true);
            }

            // If separate indices are specified for normals/colors/texCoords,
            // expand the corresponding values accordingly. Otherwise, expand
            // them using the vertex indices, if available
            if (normalValues != null) {
                if (normalIndices != null) {
                    normalValues = expandValues(normalValues, normalIndices, 3,
                            false);
                } else if (indicesAvailable) {
                    normalValues = expandValues(normalValues, indicesBackup, 3,
                            false);
                }
            }
            if (colorValues != null) {
                if (colorIndices != null) {
                    colorValues = expandValues(colorValues, colorIndices,
                            colorSize, false);
                } else if (indicesAvailable) {
                    colorValues = expandValues(colorValues, indicesBackup,
                            colorSize, false);
                }
            }
            if (texCoordValues != null) {
                if (texCoordIndices != null) {
                    texCoordValues = expandValues(texCoordValues,
                            texCoordIndices, 2, false);
                } else if (indicesAvailable) {
                    texCoordValues = expandValues(texCoordValues,
                            indicesBackup, 2, false);
                }
            }

            // If no normals are available, they are generated
            if (normalValues == null) {
                normalValues = nonIndexedNormalGenerator.generateNormals(
                        vertices, indicesBackup, creaseAngle);
            }

            // Set up the mesh
            IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices);
            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices);
            FloatBuffer colorBuffer = BufferUtils
                    .createFloatBuffer(colorValues);
            FloatBuffer normalBuffer = BufferUtils
                    .createFloatBuffer(normalValues);
            FloatBuffer texCoordBuffer = BufferUtils
                    .createFloatBuffer(texCoordValues);
            mesh = new TriMesh(null, vertexBuffer, normalBuffer, colorBuffer,
                    new TexCoords(texCoordBuffer), indexBuffer);
        }

        if (colorValues == null) {
            mesh.setDefaultColor(ColorRGBA.white.clone());
        }

        String name = (title != null) ? title : "X3D_IndexedFaceSet";
        mesh.setName(name);

        return mesh;
    }

    /**
     * Parses the values contained in the given X3D Coordinate, Color, Color3f
     * or Normal node and stores them in a float array
     * 
     * @param node
     *            The X3D node
     * @param attributeName
     *            The name of the attribute containing the values
     * @return An array containing the values
     */
    private float[] parseValues(Node node, String attributeName) {
        Node valuesNode = node.getAttributes().getNamedItem(attributeName);
        if (valuesNode == null) {
            return null;
        }
        String valuesString = valuesNode.getNodeValue().trim();
        String[] split = valuesString.split(WHITESPACE_COMMA_REGEX);
        float[] values = new float[split.length];
        for (int i = 0; i < split.length; i++) {
            values[i] = getFloat(split[i], 0);
        }
        return values;
    }

    /**
     * Parses one of the index attributes of an X3D IndexedFaceSet node,
     * translates the per-polygon indices into per-triangle indices and returns
     * the indices as an array. This method can be used for all of the index
     * attributes of an IndexedFaceSet, as they are declared in the same manner
     * (consecutive indices define a polygon until a "-1"-index occurs)
     * 
     * @param node
     *            The X3D IndexedFaceSet node
     * @param attributeName
     *            The name of the index attribute to be parsed
     * @return The index array, or <code>null</code> if no coordIndex
     *         attribute was available
     */
    private int[] parseIndices(Node node, String attributeName) {
        Node indexNode = node.getAttributes().getNamedItem(attributeName);
        if (indexNode == null) {
            return null;
        }
        String indexString = indexNode.getNodeValue().trim();
        String[] split = indexString.split(WHITESPACE_COMMA_REGEX);

        int maxVerts = 5;
        int[] polygon = new int[maxVerts];
        int indexCount = 0;
        ArrayList<int[]> triangles = new ArrayList<int[]>(split.length / 4 * 3);
        for (int i = 0; i < split.length; i++) {
            int index = getInt(split[i], 0);
            if (index > -1) {
                // One more index for the current polygon
                if (indexCount == maxVerts) {
                    int[] temp = new int[maxVerts + 1];
                    System.arraycopy(polygon, 0, temp, 0, maxVerts);
                    polygon = temp;
                    maxVerts++;
                }
                polygon[indexCount++] = index;
            } else {
                // Value <= -1: current polygon complete; split up into
                // triangles
                splitPolygon(polygon, triangles, indexCount);
                indexCount = 0;
            }
        }
        if (indexCount > 2) {
            splitPolygon(polygon, triangles, indexCount);
        }

        // Assemble the index array
        int[] result = new int[triangles.size() * 3];
        for (int i = 0; i < triangles.size(); i++) {
            int[] triangle = triangles.get(i);
            result[i * 3 + 0] = triangle[0];
            result[i * 3 + 1] = triangle[1];
            result[i * 3 + 2] = triangle[2];
        }

        return result;
    }

    /**
     * Splits a polygon of 3 or more indices into triangles. This is currently
     * achieved by using the last vertex of the polygon as the center of a fan.
     * 
     * @param polyIndices
     *            An array containing <code>indexCount</code> polygon indices
     * @param triangles
     *            A list to store arrays of indices, each one containing the
     *            indices of one triangle
     * @param indexCount
     *            The number of indices of the current polygon
     */
    private void splitPolygon(int[] polyIndices, ArrayList<int[]> triangles,
            int indexCount) {
        for (int i = 0; i < indexCount - 2; i++) {
            triangles.add(new int[] { polyIndices[i], polyIndices[i + 1],
                    polyIndices[indexCount - 1] });
        }
    }

    /**
     * Expands the list of value sets so that every three consecutive value sets
     * define a triangle. The corresponding index array can be overwritten
     * accordingly, so that it looks like this: (0, 1, 2, 3, 4, ...)
     * 
     * @param values
     *            The values
     * @param indices
     *            The indices
     * @param setLength
     *            The length of one set of values (e.g. a vector, a color, ...).
     *            The length for vectors is 3, for RGB-colors 3, for RGBA-Colors
     *            4 and for texture coordinates 2
     * @param rearrangeIndices
     *            If <code>true</code>, the index array is overwritten
     * @return The expanded vertices list
     */
    private float[] expandValues(float[] values, int[] indices, int setLength,
            boolean rearrangeIndices) {
        float[] expValues = new float[indices.length * setLength];
        for (int i = 0; i < indices.length; i++) {
            for (int j = 0; j < setLength; j++) {
                expValues[i * setLength + j] = values[indices[i] * setLength
                        + j];
            }
            if (rearrangeIndices) {
                indices[i] = i;
            }
        }
        return expValues;
    }

    // /**
    // * Creates an index array that contains equal indices to equal vectors in
    // * the array created by the method
    // * {@link #expandValues(float[], int[], int, boolean)}. Each index points
    // * to the first occurrence of the vector in the expanded value array.
    // * (Yeah, I know the method's name sounds stupid, couldn't find an
    // * appropriate one...)
    // * @param indices An array containing the original vertex indices
    // * @return The array with the changed indices
    // */
    // private int[] changeIndices(int[] indices, int[] target) {
    // int[] sortedIndices = target;
    // if (sortedIndices == null) {
    // sortedIndices = new int[indices.length];
    // }
    // Arrays.fill(sortedIndices, -1);
    // for (int i = 0; i < indices.length; i++) {
    // if (sortedIndices[i] == -1) {
    // for (int j = i; j < indices.length; j++) {
    // if (indices[j] == indices[i]) {
    // sortedIndices[j] = i;
    // }
    // }
    // }
    // }
    // return sortedIndices;
    // }

    /**
     * Generates texture coordinates for a set of vertices defined by the
     * specified float array. The texCoords are generated as follows:<br />
     * First, the extents of the object defined by the vertices are calculated.
     * The coordinate direction with the largest extent will define the S
     * texture coordinate, the second largest will define the T coordinate. Now
     * the texture coordinates are retrieved from the vertex list. The
     * coordinates are scaled so that the lowest space coordinate value will
     * correspond to the tex coord value 0 and the highest to 1.<br />
     * See the X3D specification for details and examples.
     * 
     * @param vertices
     *            A list of vertices defined by their coordinate values
     * @return The generated texture coordinate array
     */
    private float[] generateTexCoords(float[] vertices) {
        float[] texCoords = new float[vertices.length / 3 * 2];

        float minX = 0;
        float maxX = 0;
        float minY = 0;
        float maxY = 0;
        float minZ = 0;
        float maxZ = 0;

        // Get the extents of the object
        for (int i = 0; i * 3 < vertices.length; i++) {
            minX = Math.min(minX, vertices[i * 3 + 0]);
            maxX = Math.max(maxX, vertices[i * 3 + 0]);
            minY = Math.min(minY, vertices[i * 3 + 1]);
            maxY = Math.max(maxY, vertices[i * 3 + 1]);
            minZ = Math.min(minZ, vertices[i * 3 + 2]);
            maxZ = Math.max(maxZ, vertices[i * 3 + 2]);
        }

        // The coordinate with the largest extent is s, the second largest t
        float xExtent = maxX - minX;
        float yExtent = maxY - minY;
        float zExtent = maxZ - minZ;
        int sCoord = 0;
        int tCoord = 1;
        // default order: X-Y-Z
        if (yExtent > xExtent) {
            if (zExtent > yExtent) { // order: Z-Y-X
                sCoord = 2;
                tCoord = 1;
            } else {
                sCoord = 1;
                if (zExtent > xExtent) { // order: Y-Z-X
                    tCoord = 2;
                } else { // order: Y-X-Z
                    tCoord = 0;
                }
            }
        } else {
            if (zExtent > xExtent) { // order: Z-X-Y
                sCoord = 2;
                tCoord = 0;
            } else {
                if (zExtent > yExtent) { // order: X-Z-Y
                    tCoord = 2;
                } // else order: X-Y-Z (default)
            }
        }

        // Fill the texCoords array
        float[] minValues = { minX, minY, minZ };
        float[] extents = { xExtent, yExtent, zExtent };
        for (int i = 0; i * 3 < vertices.length; i++) {
            texCoords[i * 2 + 0] = (vertices[i * 3 + sCoord] - minValues[sCoord])
                    / extents[sCoord];
            texCoords[i * 2 + 1] = (vertices[i * 3 + tCoord] - minValues[tCoord])
                    / extents[tCoord];
        }

        return texCoords;
    }

    /**
     * Parses an X3D Sphere node and creates a corresponding jME Sphere.
     * 
     * @param node
     *            The X3D Sphere node
     * @param title
     *            A title for the sphere. If <code>null</code> is passed, a
     *            generic title is used.
     * @return The jME Sphere
     */
    private Sphere parseSphere(Node node, String title) {
        Sphere sphere = null;
        String radius = node.getAttributes().getNamedItem("radius")
                .getNodeValue().trim();
        float r = getFloat(radius, 1);
        if (title != null) {
            sphere = new Sphere(title, SPHERE_Z_SAMPLES, SPHERE_RADIAL_SAMPLES,
                    r);
        } else {
            sphere = new Sphere("X3D_Sphere", SPHERE_Z_SAMPLES,
                    SPHERE_RADIAL_SAMPLES, r);
        }
        sphere.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI,
                Vector3f.UNIT_X);
        return sphere;
    }

    /**
     * Parses an X3D Cylinder node and creates a corresponding jME Cylinder
     * 
     * @param node
     *            The X3D Cylinder node
     * @param title
     *            A title for the cylinder. If <code>null</code> is passed, a
     *            generic title is used.
     * @return The jME Cylinder
     */
    private Cylinder parseCylinder(Node node, String title) {
        // TODO: Implement the possibility to fully use the "top", "bottom" and
        // "side" attributes

        Cylinder cylinder = null;
        String heightAtt = node.getAttributes().getNamedItem("height")
                .getNodeValue().trim();
        float height = getFloat(heightAtt, 2);
        String radiusAtt = node.getAttributes().getNamedItem("radius")
                .getNodeValue().trim();
        float radius = getFloat(radiusAtt, 1);
        Node topNode = node.getAttributes().getNamedItem("top");
        boolean top = true;
        if (topNode != null) {
            String topAtt = topNode.getNodeValue().trim();
            top = Boolean.valueOf(topAtt);
        }

        Node botNode = node.getAttributes().getNamedItem("bottom");
        boolean bottom = true;

        if (botNode != null) {
            String bottomAtt = botNode.getNodeValue().trim();
            bottom = Boolean.valueOf(bottomAtt);
        }

        if (title != null) {
            cylinder = new Cylinder(title, CYLINDER_AXIS_SAMPLES,
                    CYLINDER_RADIAL_SAMPLES, radius, height, (top && bottom));
        } else {
            cylinder = new Cylinder("X3D_Cylinder", CYLINDER_AXIS_SAMPLES,
                    CYLINDER_RADIAL_SAMPLES, radius, height, (top && bottom));
        }
        cylinder.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI,
                Vector3f.UNIT_X);
        return cylinder;
    }

    /**
     * Parses an X3D Cone node and creates a corresponding jME Cone
     * 
     * @param node
     *            The X3D Cone node
     * @param title
     *            A title for the cone. If <code>null</code> is passed, a
     *            generic title is used.
     * @return The jME Cone
     */
    private Geometry parseCone(Node node, String title) {
        String heightAtt = node.getAttributes().getNamedItem("height")
                .getNodeValue().trim();
        float height = getFloat(heightAtt, 2);
        String radiusAtt = node.getAttributes().getNamedItem("bottomRadius")
                .getNodeValue().trim();
        float radius = getFloat(radiusAtt, 1);

        Node sideNode = node.getAttributes().getNamedItem("side");
        boolean side = true;
        if (sideNode != null) {
            String sideAtt = sideNode.getNodeValue().trim();
            side = Boolean.valueOf(sideAtt);
        }

        boolean bottom = true;
        Node botNode = node.getAttributes().getNamedItem("bottom");
        if (botNode != null) {
            String bottomAtt = botNode.getNodeValue().trim();
            bottom = Boolean.valueOf(bottomAtt);
        }
        if (side) {
            // Sides active: Create a cone
            Cone cone = null;
            if (title != null) {
                cone = new Cone(title, CYLINDER_AXIS_SAMPLES,
                        CYLINDER_RADIAL_SAMPLES, radius, height, bottom);
            } else {
                cone = new Cone("X3D_Cone", CYLINDER_AXIS_SAMPLES,
                        CYLINDER_RADIAL_SAMPLES, radius, height, bottom);
            }
            cone.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI,
                    Vector3f.UNIT_X);
            return cone;
        } else {
            // Sides deactivated: Create only the disk for the bottom
            Disk disk = null;
            if (title != null) {
                disk = new Disk(title, 1, CYLINDER_RADIAL_SAMPLES, radius);
            } else {
                disk = new Disk("X3D_Cone", 1, CYLINDER_RADIAL_SAMPLES, radius);
            }
            disk.setLocalTranslation(0, -height * 0.5f, 0);
            disk.getLocalRotation().fromAngleNormalAxis(FastMath.HALF_PI,
                    Vector3f.UNIT_X);

            return disk;
        }

    }

    /**
     * Parses an X3D LineSet node and creates a corresponding jME Line
     * 
     * @param node
     *            The X3D LineSet node
     * @param title
     *            A title for the LineSet. If <code>null</code> is passed, a
     *            generic title is used.
     * @return The jME Line
     */
    private Line parseLS(Node node, String title) {
        // jME doesn't need vertexCount, because the Line constructor is smart
        // enough to know how many vertices exist just from the float list.
        // Thus it is commented out.
        // String vertexCountAtt =
        // node.getAttributes().getNamedItem("vertexCount").getNodeValue();
        // int vertexCount = getInt(vertexCountAtt, 2);

        // Parse the vertices
        float[] vertices = null;
        Node coords = getChildNode(node, "Coordinate");
        if (coords != null) {
            vertices = parseValues(coords, "point");
        }
        if (vertices == null) {
            return null;
        }

        // Parse the colors
        float[] colorValues = null;
        int colorSize = 3; // Number of values per color (Color or ColorRGBA)
        Node colors = getChildNode(node, "Color");
        if (colors == null) {
            colorSize = 4;
            colors = getChildNode(node, "ColorRGBA");
        }
        if (colors != null) {
            colorValues = parseValues(colors, "color");
        }

        // Expand RGB color values to RGBA values
        if (colorValues != null && colorSize == 3) {
            float[] temp = new float[colorValues.length / 3 * 4];
            for (int i = 0; i * 3 < colorValues.length; i++) {
                temp[i * 4 + 0] = colorValues[i * 3 + 0];
                temp[i * 4 + 1] = colorValues[i * 3 + 1];
                temp[i * 4 + 2] = colorValues[i * 3 + 2];
                temp[i * 4 + 3] = 1.0f;
            }
            colorValues = temp;
            colorSize = 4;
        }

        // Create the Line
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices);
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colorValues);
        String name = (title != null) ? title : "X3D_LineSet";
        Line line = new Line(name, vertexBuffer, null, colorBuffer, null);
        line.setMode(Line.Mode.Connected);
        return line;
    }

    /**
     * Parses an X3D Appearance node and generates the corresponding
     * RenderStates.
     * 
     * @param node
     *            The X3D Appearance node
     * @return An array containing the created RenderStates
     */
    private RenderState[] parseAppearance(Node node) {

        // Check for the USE attribute
        Node use = node.getAttributes().getNamedItem("USE");
        if (use != null) {
            return getRenderStateDEFs(use.getNodeValue());
        }

        ArrayList<RenderState> states = new ArrayList<RenderState>(3);

        // Check Texture
        TextureState textureState = null;
        Node texNode = getChildNode(node, "ImageTexture");
        if (texNode != null) {
            Texture texture = parseTexture(texNode, null, 0);
            if (texture != null) {
                textureState = DisplaySystem.getDisplaySystem().getRenderer()
                        .createTextureState();
                textureState.setEnabled(true);
                textureState.setTexture(texture);
            }
        } else {
            texNode = getChildNode(node, "MultiTexture");
            if (texNode != null) {
                textureState = parseMultiTexture(texNode);
            }
        }

        if (textureState != null) {
            states.add(textureState);
        }

        // Check Material
        Node materialNode = getChildNode(node, "Material");
        if (materialNode != null) {
            RenderState[] matStates = parseMaterial(materialNode);
            for (RenderState state : matStates) {
                if (state != null) {
                    states.add(state);
                }
            }
        }

        RenderState[] result = states.toArray(new RenderState[states.size()]);

        // Check for the DEF attribute
        Node def = node.getAttributes().getNamedItem("DEF");
        if (def != null) {
            storeRenderStateDEFs(result, def.getNodeValue());
        }

        return result;
    }

    /**
     * Finds the array of RenderStates in the DEF-Map that is mapped to the
     * given ID and retrieves an array containing all RenderStates in the stored
     * array (by reference!).
     * 
     * @param defID
     *            The ID
     * @return An array containing the cloned RenderStates
     */
    private RenderState[] getRenderStateDEFs(String defID) {
        // Cloning RenderStates should be unnecessary...
        // CloneImportExport[] def = (CloneImportExport[]) defs.get(defID);
        RenderState[] def = (RenderState[]) defs.get(defID);
        if (def != null) {
            RenderState[] states = new RenderState[def.length];
            // for (int i = 0; i < def.length; i++) {
            // if (def[i] != null) {
            // states[i] = (RenderState)def[i].loadClone();
            // }
            // }
            System.arraycopy(def, 0, states, 0, def.length);
            return states;
        }
        return null;
    }

    /**
     * Creates an array with clones of all RenderStates in the given array and
     * stores it in the DEF map under the given ID
     * 
     * @param states
     *            The RenderStates to be stored
     * @param defID
     *            The ID
     */
    private void storeRenderStateDEFs(RenderState[] states, String defID) {
        // Cloning RenderStates should be unnecessary...
        // CloneImportExport[] ex = new CloneImportExport[states.length];
        // for (int i = 0; i < states.length; i++) {
        // ex[i] = new CloneImportExport();
        // ex[i].saveClone(states[i]);
        // }
        // defs.put(defID, ex);
        defs.put(defID, states);
    }

    /**
     * Parses an X3D Material and generates a corresponding MaterialState and,
     * if necessary, Alpha State.
     * 
     * @param node
     *            The X3D Material node
     * @return An array containing the generated states
     */
    private RenderState[] parseMaterial(Node node) {

        // Check for the USE attribute
        Node use = node.getAttributes().getNamedItem("USE");
        if (use != null) {
            return getRenderStateDEFs(use.getNodeValue());
        }

        MaterialState matState = DisplaySystem.getDisplaySystem().getRenderer()
                .createMaterialState();
        matState.setEnabled(true);

        /*
         * If a bump map is present, geometry colors must be enabled for diffuse
         * so the BumpMapColorController can update the colors from the bump map
         * calculation properly
         */
        if (createBumpController) {
            matState.setColorMaterial(MaterialState.ColorMaterial.AmbientAndDiffuse);
        }

        // Parse diffuse color
        ColorRGBA diffuse = new ColorRGBA(0.8f, 0.8f, 0.8f, 1);
        Node diffuseNode = node.getAttributes().getNamedItem("diffuseColor");
        if (diffuseNode != null) {
            String split[] = diffuseNode.getNodeValue().trim().split(
                    WHITESPACE_REGEX);
            if (split.length >= 3) {
                float r = getFloat(split[0], 0);
                r = FastMath.clamp(r, 0, 1);
                float g = getFloat(split[1], 0);
                g = FastMath.clamp(g, 0, 1);
                float b = getFloat(split[2], 0);
                b = FastMath.clamp(b, 0, 1);
                diffuse.set(r, g, b, 1);
            }
        }
        matState.setDiffuse(diffuse);

        // Parse ambient factor
        Node ambientNode = node.getAttributes()
                .getNamedItem("ambientIntensity");
        if (ambientNode != null) {
            float ambient = 0.2f;
            ambient = getFloat(ambientNode.getNodeValue().trim(), 0.2f);
            ambient = FastMath.clamp(ambient, 0, 1);
            matState.setAmbient(new ColorRGBA(diffuse.r * ambient, diffuse.g
                    * ambient, diffuse.b * ambient, 1));
        }

        // Parse emissive color
        ColorRGBA emissive = new ColorRGBA(0, 0, 0, 1);
        Node emissiveNode = node.getAttributes().getNamedItem("emissiveColor");
        if (emissiveNode != null) {
            String split[] = emissiveNode.getNodeValue().trim().split(
                    WHITESPACE_REGEX);
            if (split.length >= 3) {
                float r = getFloat(split[0], 0);
                r = FastMath.clamp(r, 0, 1);
                float g = getFloat(split[1], 0);
                g = FastMath.clamp(g, 0, 1);
                float b = getFloat(split[2], 0);
                b = FastMath.clamp(b, 0, 1);
                emissive.set(r, g, b, 1);
            }
        }
        matState.setEmissive(emissive);

        // Parse shininess
        float shininess = 0.2f;
        Node shininessNode = node.getAttributes().getNamedItem("shininess");
        if (shininessNode != null) {
            shininess = getFloat(shininessNode.getNodeValue().trim(), 0.2f);
            shininess = FastMath.clamp(shininess, 0.1f, 128.0f);
        }
        matState.setShininess(shininess);

        // Parse specularColor
        ColorRGBA specular = new ColorRGBA(0, 0, 0, 1);
        Node specularNode = node.getAttributes().getNamedItem("specularColor");
        if (specularNode != null) {
            String split[] = specularNode.getNodeValue().trim().split(
                    WHITESPACE_REGEX);
            if (split.length >= 3) {
                float r = getFloat(split[0], 0);
                r = FastMath.clamp(r, 0, 1);
                float g = getFloat(split[1], 0);
                g = FastMath.clamp(g, 0, 1);
                float b = getFloat(split[2], 0);
                b = FastMath.clamp(b, 0, 1);
                specular.set(r, g, b, 1);
            }
            matState.setSpecular(specular);
        }

        // Parse transparency
        BlendState blendState = null;
        Node transparencyNode = node.getAttributes().getNamedItem(
                "transparency");
        if (transparencyNode != null) {
            float transparency = getFloat(transparencyNode.getNodeValue()
                    .trim(), 0);
            transparency = FastMath.clamp(transparency, 0, 1);
            if (transparency > 0) {
                blendState = DisplaySystem.getDisplaySystem().getRenderer()
                        .createBlendState();
                blendState.setEnabled(true);
                blendState.setBlendEnabled(true);
                blendState.setTestEnabled(true);
                // blendState.setSrcFunction(BlendState.SB_SRC_ALPHA);
                // blendState.setDstFunction(BlendState.DB_ONE_MINUS_SRC_ALPHA);
                matState.getAmbient().a = 1 - transparency;
                matState.getDiffuse().a = 1 - transparency;
                matState.getEmissive().a = 1 - transparency;
                matState.getSpecular().a = 1 - transparency;
                addToTransparentQueue = true;
            }
        }

        RenderState[] result;
        if (blendState != null) {
            result = new RenderState[] { matState, blendState };
        } else {
            result = new RenderState[] { matState };
        }

        // Check for the DEF attribute
        Node def = node.getAttributes().getNamedItem("DEF");
        if (def != null) {
            storeRenderStateDEFs(result, def.getNodeValue());
        }

        return result;
    }

    /**
     * Loads a Texture according to the specified X3D ImageTexture node and
     * other parameters. ImageTextures with a DEF attribute are not stored in
     * the map <code>defs</code>. Instead, jME's TextureManager is used to
     * cache the textures.
     * 
     * @param node
     *            The X3D ImageTexture node
     * @param applyMode
     *            Tha apply mode to use for this texture. If null or an invalid
     *            mode is specified, the default mode (MODULATE) is used.
     * @param texUnit
     *            The texture unit that this texture will be applied to. This is
     *            used only for bump map textures to determine what the color
     *            source for the combination is (base color or previous texture.
     * @return The jME Texture
     */
    private Texture parseTexture(Node node, String applyMode, int texUnit) {
        Node urlNode = node.getAttributes().getNamedItem("url");
        if (urlNode == null) {
            return null;
        }
        String urlString = urlNode.getNodeValue().replaceAll("\"", "")
                .replaceAll("\'", "").trim();

        // Try to find cached texture
        Texture texture = null;
        TextureKey tKey = new TextureKey();
        try {
            tKey.setLocation(new File(urlString).toURI().toURL());
        } catch (MalformedURLException e) {
        }
        Texture cachedTexture = TextureManager.findCachedTexture(tKey);
        if (cachedTexture != null) {
            texture = cachedTexture.createSimpleClone();
            if (texture.getCombineFuncRGB() == Texture.CombinerFunctionRGB.Dot3RGB) {
                createBumpController = true;
            }
            return texture;
        }

        // If the texture is not in cache, load it
        if (texData != null) {
            InputStream texIn = texData.get(urlString);
            if (texIn == null) {
                logger.info("No data InputStream found for texture "
                        + urlString + "!");
                return null;
            }
            Image image = TextureManager.loadImage(urlString
                    .substring(urlString.indexOf('.') + 1), texIn, true);
            try {
                texIn.close();
            } catch (IOException e) {
            }
            texture = new Texture2D();
            texture.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
            texture.setMinificationFilter(Texture.MinificationFilter.Trilinear);
            texture.setImage(image);
        } else {
            URL texURL = null;
            URL texBase = (URL) getProperty("textures");
            if (texBase != null) {
                try {
                    texURL = new URL(texBase, urlString);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            if (texURL != null) {
                texture = TextureManager.loadTexture(texURL, true);
                texture.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
                texture.setMinificationFilter(Texture.MinificationFilter.Trilinear);
            } else {
                return null;
            }
        }

        // Parse repeat mode
        Node repeatSNode = node.getAttributes().getNamedItem("repeatS");
        boolean repeatS = Boolean.parseBoolean(repeatSNode.getNodeValue()
                .trim());
        Node repeatTNode = node.getAttributes().getNamedItem("repeatT");
        boolean repeatT = Boolean.parseBoolean(repeatTNode.getNodeValue()
                .trim());
        texture.setWrap(Texture.WrapAxis.S, repeatS ? Texture.WrapMode.Repeat : Texture.WrapMode.Clamp);
        texture.setWrap(Texture.WrapAxis.T, repeatT ? Texture.WrapMode.Repeat : Texture.WrapMode.Clamp);

        // Check if an apply mode other than "MODULATE" is used
        if (applyMode != null) {
            /*
             * Values separated by commas are used to specify different modes
             * for RGB and alpha. Can't be realized in jME, so ignore it
             */
            int comma = applyMode.indexOf(',');
            if (comma > -1) {
                applyMode = applyMode.substring(0, comma);
            }
            if (applyMode.equalsIgnoreCase("REPLACE")) {
                texture.setApply(Texture.ApplyMode.Replace);
            } else if (applyMode.equalsIgnoreCase("ADD")) {
                texture.setApply(Texture.ApplyMode.Add);
            } else if (applyMode.equalsIgnoreCase("BLENDFACTORALPHA")) {
                texture.setApply(Texture.ApplyMode.Decal);
            } else if (applyMode.equalsIgnoreCase("DOTPRODUCT3")) {
                texture.setApply(Texture.ApplyMode.Combine);
                texture.setCombineFuncRGB(Texture.CombinerFunctionRGB.Dot3RGB);
                texture.setCombineSrc0RGB(Texture.CombinerSource.CurrentTexture);
                if (texUnit == 0) {
                    texture.setCombineSrc1RGB(Texture.CombinerSource.PrimaryColor);
                } else {
                    texture.setCombineSrc1RGB(Texture.CombinerSource.Previous);
                }

                // Set the create controller flag for the method
                // parseShape(Node)
                createBumpController = true;
            }
        }

        // Store the Texture in the TextureManager's cache
        texture.setTextureKey(tKey);
        texture.setStoreTexture(true);
        TextureManager.addToCache(texture);

        return texture;
    }

    /**
     * Parses an X3D MultiTexture node and creates a TextureState containing all
     * textures with the appropriate settings
     * 
     * @param node
     *            The X3D MultiTexture nodes
     * @return The TextureState
     */
    private TextureState parseMultiTexture(Node node) {

        // Check for the USE attribute
        Node use = node.getAttributes().getNamedItem("USE");
        if (use != null) {
            TextureState state = (TextureState) getDef(use.getNodeValue());
            if (state != null) {
                for (int i = 0; i < state.getNumberOfSetTextures(); i++) {
                    if (state.getTexture(i).getCombineFuncRGB() == Texture.CombinerFunctionRGB.Dot3RGB) {
                        createBumpController = true;
                    }
                }
            }
            return state;
        }

        TextureState state = DisplaySystem.getDisplaySystem().getRenderer()
                .createTextureState();

        // Check if special apply modes for the textures are defined
        Node modeNode = node.getAttributes().getNamedItem("mode");
        String[] modes = null;
        if (modeNode != null) {
            modes = modeNode.getNodeValue().trim()
                    .split(WHITESPACE_COMMA_REGEX);
        }

        // Parse all contained textures
        int texCount = 0;
        Node child = node.getFirstChild();
        while (child != null) {
            if (child.getNodeName().equals("ImageTexture")) {
                String mode = null;
                if (modes != null && modes.length > texCount) {
                    mode = modes[texCount];
                }
                Texture texture = parseTexture(child, mode, texCount);
                if (texture != null) {
                    state.setTexture(texture, texCount);
                    texCount++;
                }
            }
            child = child.getNextSibling();
        }

        // Check for DEF attribute
        Node def = node.getAttributes().getNamedItem("def");
        if (def != null) {
            CloneImportExport ex = new CloneImportExport();
            ex.saveClone(state);
            defs.put(def.getNodeValue(), ex);
        }

        return state;
    }

    /**
     * Copies the set of tex coords for the first texture of the Geometry for
     * all other textures set in the TextureState
     * 
     * @param ts
     *            The TexturState
     * @param geom
     *            The Geometry
     */
    private void copyTexCoords(TextureState ts, Geometry geom) {
        if (geom != null) {
            for (int i = 1; i < ts.getNumberOfSetTextures(); i++) {
                geom.copyTextureCoordinates(0, i, 1);
            }
        }
    }

    /**
     * Parses an X3D DirecionalLight, PointLight or SpotLight node and creates a
     * jME SimpleLightNode containing the corresponding jME Light.
     * 
     * @param node
     *            The X3D light node
     * @return The SimpleLightNode
     */
    private SimpleLightNode parseLight(Node node) {
        // Get a name for the light node
        String name = "X3D_Light";
        Node def = node.getAttributes().getNamedItem("DEF");
        if (def != null) {
            name = def.getNodeValue();
        }

        // Parse the light node
        SimpleLightNode lightNode = null;
        Light light = null;
        if (node.getNodeName().equals("DirectionalLight")) {
            light = new DirectionalLight();
            lightState.attach(light);
            lightNode = new SimpleLightNode(name, light);
            setDirection(node, lightNode);
        } else if (node.getNodeName().equals("PointLight")) {
            light = new PointLight();
            lightState.attach(light);
            lightNode = new SimpleLightNode(name, light);
            setLocation(node, lightNode);
            setAttenuation(node, light);
        } else if (node.getNodeName().equals("SpotLight")) {
            SpotLight sLight = new SpotLight();
            light = sLight;
            lightState.attach(light);
            lightNode = new SimpleLightNode(name, light);
            setLocation(node, lightNode);
            setDirection(node, lightNode);
            setAttenuation(node, light);
            Node cutOffNode = node.getAttributes().getNamedItem("cutOffAngle");
            float cutOff = getFloat(cutOffNode.getNodeValue().trim(),
                    0.5f * FastMath.HALF_PI);
            sLight.setAngle(cutOff * 180 / FastMath.PI);
        }

        // Set the light colors
        Node intensityNode = node.getAttributes().getNamedItem("intensity");
        float intensity = getFloat(intensityNode.getNodeValue().trim(), 1);
        intensity = FastMath.clamp(intensity, 0, 1);

        Node diffuseNode = node.getAttributes().getNamedItem("color");
        String[] split = diffuseNode.getNodeValue().trim().split(
                WHITESPACE_REGEX);
        float r = 1;
        float g = 1;
        float b = 1;
        if (split.length >= 3) {
            r = getFloat(split[0], 1);
            r = FastMath.clamp(r, 0, 1) * intensity;
            g = getFloat(split[1], 1);
            g = FastMath.clamp(g, 0, 1) * intensity;
            b = getFloat(split[2], 1);
            b = FastMath.clamp(b, 0, 1) * intensity;
        }

        Node ambientNode = node.getAttributes()
                .getNamedItem("ambientIntensity");
        float ambient = getFloat(ambientNode.getNodeValue().trim(), 0);
        ambient = FastMath.clamp(ambient, 0, 1);

        light.getAmbient().set(r * ambient, g * ambient, b * ambient, 1);
        light.getDiffuse().set(r, g, b, 1);
        light.getSpecular().set(r * intensity, g * intensity, b * intensity, 1);

        // Is the light source active?
        Node onNode = node.getAttributes().getNamedItem("on");
        if (onNode.getNodeValue().trim().equalsIgnoreCase("false")) {
            light.setEnabled(false);
        } else {
            light.setEnabled(true);
        }

        return lightNode;
    }

    /**
     * Retrieves the direction from the given X3D light node and translates it
     * into a rotation for the given light Node
     * 
     * @param node
     *            The X3D light node
     * @param lightNode
     *            The jME light node
     */
    private void setDirection(Node node, com.jme.scene.Node lightNode) {
        Node directionNode = node.getAttributes().getNamedItem("direction");
        String[] split = directionNode.getNodeValue().trim().split(
                WHITESPACE_REGEX);
        if (split.length >= 3) {
            float x = getFloat(split[0], 0);
            float y = getFloat(split[1], 0);
            float z = getFloat(split[2], -1);
            Vector3f direction = new Vector3f(x, y, z);
            if (parallelToYAxis(direction)) {
                lightNode.getLocalRotation().lookAt(direction, Vector3f.UNIT_Z);
            } else {
                lightNode.getLocalRotation().lookAt(direction, Vector3f.UNIT_Y);
            }
        } else {
            lightNode.getLocalRotation().set(1, 0, 0, FastMath.PI);
        }
        // lightNode.getLocalRotation().lookAt(new Vector3f(0, -1, 0),
        // Vector3f.UNIT_Z);
        // Vector3f x = lightNode.getLocalRotation().getRotationColumn(0);
        // Vector3f y = lightNode.getLocalRotation().getRotationColumn(1);
        // Vector3f z = lightNode.getLocalRotation().getRotationColumn(2);
        // System.out.println("X: "+x+"\nY:"+y+"\nZ:"+z);
    }

    /**
     * Retrieves the location from the given X3D light node and sets the
     * translation of the given jME light node accordingly
     * 
     * @param node
     *            The X3D light node
     * @param lightNode
     *            The jME light node
     */
    private void setLocation(Node node, com.jme.scene.Node lightNode) {
        Node locationNode = node.getAttributes().getNamedItem("location");
        String[] split = locationNode.getNodeValue().trim().split(
                WHITESPACE_REGEX);
        if (split.length >= 3) {
            float x = getFloat(split[0], 0);
            float y = getFloat(split[1], 0);
            float z = getFloat(split[2], 0);
            lightNode.setLocalTranslation(x, y, z);
        }
    }

    /**
     * Gets the attenuation factors from the given X3D light node and sets the
     * corresponding attenuation parameters of the given jME light accordingly
     * 
     * @param node
     *            The X3D light node
     * @param light
     *            The jME Light
     */
    private void setAttenuation(Node node, Light light) {
        Node attNode = node.getAttributes().getNamedItem("attenuation");
        String[] split = attNode.getNodeValue().trim().split(WHITESPACE_REGEX);
        if (split.length >= 3) {
            float cAtt = getFloat(split[0], 1);
            float lAtt = getFloat(split[1], 0);
            float qAtt = getFloat(split[2], 0);
            light.setAttenuate(true);
            light.setConstant(cAtt);
            light.setLinear(lAtt);
            light.setQuadratic(qAtt);
        }
    }

    /**
     * Checks if the specified vector is parallel to the y axis.
     * 
     * @param vec
     *            The vector
     * @return <code>true</code>, if it is parallel to the y-axis
     */
    private boolean parallelToYAxis(Vector3f vec) {
        return (FastMath.abs(vec.x) <= FastMath.ZERO_TOLERANCE
                && FastMath.abs(vec.z) <= FastMath.ZERO_TOLERANCE && FastMath
                .abs(vec.y) > FastMath.ZERO_TOLERANCE);
    }
}
