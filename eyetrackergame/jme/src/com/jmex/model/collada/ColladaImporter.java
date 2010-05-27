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
package com.jmex.model.collada;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.animation.Bone;
import com.jme.animation.BoneAnimation;
import com.jme.animation.BoneTransform;
import com.jme.animation.SkinNode;
import com.jme.animation.TextureKeyframeController;
import com.jme.bounding.BoundingBox;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture.WrapAxis;
import com.jme.light.DirectionalLight;
import com.jme.light.Light;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.light.SpotLight;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.CameraNode;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.SharedNode;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.ClipState;
import com.jme.scene.state.ColorMaskState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ShadeState;
import com.jme.scene.state.StencilState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.geom.BufferUtils;
import com.jme.util.geom.GeometryTool;
import com.jme.util.geom.VertMap;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.model.collada.schema.COLLADAType;
import com.jmex.model.collada.schema.IDREF_arrayType;
import com.jmex.model.collada.schema.InstanceWithExtra;
import com.jmex.model.collada.schema.Name_arrayType;
import com.jmex.model.collada.schema.TargetableFloat3;
import com.jmex.model.collada.schema.accessorType;
import com.jmex.model.collada.schema.animationType;
import com.jmex.model.collada.schema.assetType;
import com.jmex.model.collada.schema.bind_materialType;
import com.jmex.model.collada.schema.cameraType;
import com.jmex.model.collada.schema.collada_schema_1_4_1Doc;
import com.jmex.model.collada.schema.colorType;
import com.jmex.model.collada.schema.common_newparam_type;
import com.jmex.model.collada.schema.controllerType;
import com.jmex.model.collada.schema.effectType;
import com.jmex.model.collada.schema.float4x4;
import com.jmex.model.collada.schema.float_arrayType;
import com.jmex.model.collada.schema.fx_sampler2D_common;
import com.jmex.model.collada.schema.fx_surface_common;
import com.jmex.model.collada.schema.geometryType;
import com.jmex.model.collada.schema.imageType;
import com.jmex.model.collada.schema.instance_controllerType;
import com.jmex.model.collada.schema.instance_geometryType;
import com.jmex.model.collada.schema.instance_materialType;
import com.jmex.model.collada.schema.instance_physics_modelType;
import com.jmex.model.collada.schema.lambertType;
import com.jmex.model.collada.schema.library_animationsType;
import com.jmex.model.collada.schema.library_camerasType;
import com.jmex.model.collada.schema.library_controllersType;
import com.jmex.model.collada.schema.library_effectsType;
import com.jmex.model.collada.schema.library_geometriesType;
import com.jmex.model.collada.schema.library_imagesType;
import com.jmex.model.collada.schema.library_lightsType;
import com.jmex.model.collada.schema.library_materialsType;
import com.jmex.model.collada.schema.library_nodesType;
import com.jmex.model.collada.schema.library_physics_modelsType;
import com.jmex.model.collada.schema.library_physics_scenesType;
import com.jmex.model.collada.schema.library_visual_scenesType;
import com.jmex.model.collada.schema.lightType;
import com.jmex.model.collada.schema.materialType;
import com.jmex.model.collada.schema.meshType;
import com.jmex.model.collada.schema.nodeType2;
import com.jmex.model.collada.schema.opticsType;
import com.jmex.model.collada.schema.orthographicType;
import com.jmex.model.collada.schema.paramType3;
import com.jmex.model.collada.schema.passType3;
import com.jmex.model.collada.schema.perspectiveType;
import com.jmex.model.collada.schema.phongType;
import com.jmex.model.collada.schema.physics_modelType;
import com.jmex.model.collada.schema.physics_sceneType;
import com.jmex.model.collada.schema.polygonsType;
import com.jmex.model.collada.schema.rigid_bodyType;
import com.jmex.model.collada.schema.sceneType;
import com.jmex.model.collada.schema.shapeType2;
import com.jmex.model.collada.schema.skinType;
import com.jmex.model.collada.schema.sourceType;
import com.jmex.model.collada.schema.techniqueType2;
import com.jmex.model.collada.schema.techniqueType4;
import com.jmex.model.collada.schema.technique_commonType;
import com.jmex.model.collada.schema.technique_commonType2;
import com.jmex.model.collada.schema.technique_commonType4;
import com.jmex.model.collada.schema.textureType;
import com.jmex.model.collada.schema.trianglesType;
import com.jmex.model.collada.schema.vertex_weightsType;
import com.jmex.model.collada.schema.visual_sceneType;

/**
 * <code>ColladaNode</code> provides a mechanism to parse and load a COLLADA
 * (COLLAborative Design Activity) model. Making use of a DOM parse, the XML
 * formatted COLLADA file is parsed into Java Type classes and then processed by
 * jME. This processing is currently aimed at the 1.4.1 release of the COLLADA
 * Specification, and will, in most likelihood, require updating with a new
 * release of COLLADA.
 * 
 * @author Mark Powell, Rikard Herlitz, and others
 */
public class ColladaImporter {
    private static final Logger logger = Logger.getLogger(ColladaImporter.class
            .getName());
    // asset information
    private String modelAuthor;
    private String tool;
    private String revision;
    private String unitName;
    private float unitMeter;
    private String upAxis;
    private static ColladaImporter instance;
    private String name;
    private String[] boneIds;
    private static boolean squelch;

    // If true, models loaded by ColladaImporter will automatically have
    // geometry optimization applied. default: true.
    public static boolean OPTIMIZE_GEOMETRY = true;

    public static OptimizeCallback optimizeCallBack = null;

    private Map<String, Object> resourceLibrary;
    private ArrayList<String> controllerNames;
    private ArrayList<String> uvControllerNames;
    private ArrayList<String> skinNodeNames;
    private ArrayList<String> cameraNodeNames;
    private ArrayList<String> lightNodeNames;
    private ArrayList<String> geometryNames;
    private ArrayList<String> skeletonNames;
    private Map<String, Object> userInformation;
    private Map<TriMesh, String> subMaterialLibrary;
    private Node model;

    /**
     * Unique Serial ID for ColladaNode
     */
    private static final long serialVersionUID = -4024091270314000507L;

    /**
     * Default constructor instantiates a ColladaImporter object. A basic Node
     * structure is built and no data is loaded until the <code>load</code>
     * method is called.
     * 
     * @param name
     *            the name of the node.
     */
    private ColladaImporter(String name) {
        this.name = name;
    }

    public static boolean hasUserInformation(String key) {
        if (instance.userInformation == null) {
            return false;
        } else {
            return instance.userInformation.containsKey(key);
        }
    }

    public static void addUserInformation(String key, Object value) {
        if (instance.userInformation == null) {
            instance.userInformation = new HashMap<String, Object>();
        }
        instance.userInformation.put(key, value);
    }

    public static Object getUserInformation(String key) {
        if (instance.userInformation == null) {
            return null;
        } else {
            return instance.userInformation.get(key);
        }
    }

    /**
     * load takes the model path as a string object and uses the
     * COLLADASchemaDoc object to load it. This is then stored as a hierarchy of
     * data objects. This hierarchy is passed to the processCollada method to
     * build the jME data structures necessary to view the model.
     * 
     * @param source
     *            the source to import.
     * @param textureDirectory
     *            the location of the textures.
     * @param name
     *            the name of the node.
     */
    public static void load(InputStream source, String name) {
        if (instance == null) {
            instance = new ColladaImporter(name);
        }
        instance.load(source);
    }

    /**
     * load is called by the static load method, creating an instance of the
     * model to be returned.
     * 
     * @param source
     *            the source to import.
     * @param textureDirectory
     *            the location of the textures.
     */
    private void load(InputStream source) {
        model = new Node(name);
        resourceLibrary = new HashMap<String, Object>();
        subMaterialLibrary = new HashMap<TriMesh, String>();
        collada_schema_1_4_1Doc doc = new collada_schema_1_4_1Doc();
        try {
            COLLADAType root = new COLLADAType(doc.load(source));
            logger.info("Version: " + root.getversion().getValue());
            processCollada(root);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Unable to load Collada file. ", ex);
            return;
        }
    }

    /**
     * returns the names of the controllers that affect this imported model.
     * 
     * @return the list of string values for each controller name.
     */
    public static ArrayList<String> getControllerNames() {
        if (instance == null) {
            return null;
        }
        return instance.controllerNames;
    }

    /**
     * @return
     */
    public static ArrayList<String> getUVControllerNames() {
        if (instance == null) {
            return null;
        }
        return instance.uvControllerNames;
    }

    public static void addUVControllerName(String name) {
        if (instance.uvControllerNames == null) {
            instance.uvControllerNames = new ArrayList<String>();
        }
        instance.uvControllerNames.add(name);
    }

    /**
     * returns the names of the skin nodes that are associated with this
     * imported model.
     * 
     * @return the names of the skin nodes associated with this model.
     */
    public static ArrayList<String> getSkinNodeNames() {
        if (instance == null) {
            return null;
        }
        return instance.skinNodeNames;
    }

    /**
     * Returns the camera node names associated with this model.
     * 
     * @return the list of camera names that are referenced in this file.
     */
    public static ArrayList<String> getCameraNodeNames() {
        if (instance == null) {
            return null;
        }
        return instance.cameraNodeNames;
    }

    public static ArrayList<String> getLightNodeNames() {
        if (instance == null) {
            return null;
        }
        return instance.lightNodeNames;
    }

    public static ArrayList<String> getSkeletonNames() {
        if (instance == null) {
            return null;
        }
        return instance.skeletonNames;
    }

    public static ArrayList<String> getGeometryNames() {
        if (instance == null) {
            return null;
        }
        return instance.geometryNames;
    }

    public static Node getModel() {
        if (instance == null) {
            return null;
        }
        return instance.model;
    }

    public static SkinNode getSkinNode(String id) {
        if (instance == null) {
            return null;
        }
        return (SkinNode) instance.resourceLibrary.get(id);
    }

    public static CameraNode getCameraNode(String id) {
        if (instance == null) {
            return null;
        }
        return (CameraNode) instance.resourceLibrary.get(id);
    }

    public static LightNode getLightNode(String id) {
        if (instance == null) {
            return null;
        }
        return (LightNode) instance.resourceLibrary.get(id);
    }

    public static Object get(Object id) {
        return instance.resourceLibrary.get(id);
    }

    /**
     * places an object into the resource library with a given key. If there is
     * an object referenced by this key and it is not the same object that is to
     * be added to the library, a warning is issued. If this object already
     * exists in the library we do not read it.
     * 
     * @param key
     *            the key to obtain the object from the library.
     * @param value
     *            the object to store in the library.
     */
    public static void put(String key, Object value) {
        Object data = instance.resourceLibrary.get(key);
        if (data != value) {
            if (data != null) {
                if (!squelch) {
                    logger
                            .warning("Key: "
                                    + key
                                    + " already in use. Overriding previous data. This is probably not"
                                    + " desired.");
                }
            }
            instance.resourceLibrary.put(key, value);
        }
    }

    public static BoneAnimation getAnimationController(String id) {
        if (instance == null) {
            return null;
        }
        return (BoneAnimation) instance.resourceLibrary.get(id);
    }

    public static TextureKeyframeController getUVAnimationController(String id) {
        if (instance == null) {
            return null;
        }
        return (TextureKeyframeController) instance.resourceLibrary.get(id);
    }

    public static Bone getSkeleton(String id) {
        if (instance == null) {
            return null;
        }
        return (Bone) instance.resourceLibrary.get(id);
    }

    public static Geometry getGeometry(String id) {
        if (instance == null) {
            return null;
        }
        return (Geometry) instance.resourceLibrary.get(id);
    }

    public static void cleanUp() {
        if (instance != null) {
            instance.shutdown();
        }
    }

    public void shutdown() {
        instance = null;
    }

    /**
     * Author of the last loaded collada model.
     * 
     * @return the modelAuthor the author of the last loaded model.
     */
    public String getModelAuthor() {
        return modelAuthor;
    }

    /**
     * Revision number of the last loaded collada model.
     * 
     * @return the revision revision number of the last loaded collada model.
     */
    public String getRevision() {
        return revision;
    }

    /**
     * the tool used to build the last collada model.
     * 
     * @return the tool
     */
    public String getTool() {
        return tool;
    }

    /**
     * the unit scale of the last collada model.
     * 
     * @return the unitMeter
     */
    public float getUnitMeter() {
        return unitMeter;
    }

    /**
     * the unit name of the last collada model.
     * 
     * @return the unitName
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * getAssetInformation returns a string of the collected asset information
     * of this COLLADA model. The format is such: <br>
     * AUTHOR REVISION<br>
     * TOOL<br>
     * UNITNAME UNITMETER<br>
     * UPAXIS<br>
     * 
     * @return the string representation of the asset information of this file.
     */
    public String getAssetInformation() {
        return modelAuthor + " " + revision + "\n" + tool + "\n" + unitName
                + " " + unitMeter + "\n" + upAxis;
    }

    /**
     * processCollada takes a COLLADAType object that contains the hierarchical
     * information obtained from the XML structure of a COLLADA model. This root
     * object is processed and sets the data structures for jME to render the
     * model to *this* object.
     * 
     * @param root
     *            the COLLADAType data structure that contains the COLLADA model
     *            information.
     */
    public void processCollada(COLLADAType root) {
        // build the asset information about this model. This can be used
        // for debugging information. Only a single asset tag is allowed per
        // model.
        if (root.hasasset()) {
            try {
                processAssetInformation(root.getasset());
            } catch (Exception e) {
                if (!squelch) {
                    logger.log(Level.WARNING,
                            "Error processing asset information", e);
                }
            }
        }
        // user defined libraries may exist (for example, UV animations)
        if (root.hasextra()) {
            try {
                ExtraPluginManager.processExtra(root, root.getextra());
            } catch (Exception e) {
                if (!squelch) {
                    logger.log(Level.WARNING,
                            "Error processing extra information", e);
                }
            }
        }
        // builds the animation keyframes and places the controllers into a
        // node.
        if (root.haslibrary_animations()) {
            try {
                processAnimationLibrary(root.getlibrary_animations());
            } catch (Exception e) {
                if (!squelch) {
                    logger.log(Level.WARNING,
                            "Error processing animation information", e);
                }
            }
        }
        if (root.haslibrary_animation_clips()) {
            if (!squelch) {
                logger.warning("Animation Clips not currently supported");
            }
        }
        if (root.haslibrary_cameras()) {
            try {
                processCameraLibrary(root.getlibrary_cameras());
            } catch (Exception e) {
                if (!squelch) {
                    logger.log(Level.WARNING,
                            "Error processing camera information", e);
                }
            }
        }
        if (root.haslibrary_force_fields()) {
            if (!squelch) {
                logger.warning("Forcefields not currently supported");
            }
        }
        if (root.haslibrary_lights()) {
            try {
                processLightLibrary(root.getlibrary_lights());
            } catch (Exception e) {
                if (!squelch) {
                    logger.log(Level.WARNING,
                            "Error processing light information", e);
                }
            }
        }
        // build a map of images that the materials can use in the future.
        if (root.haslibrary_images()) {
            try {
                processImageLibrary(root.getlibrary_images());
            } catch (Exception e) {
                if (!squelch) {
                    logger.log(Level.WARNING,
                            "Error processing image library information", e);
                }
            }
        }
        // build all the material states that can be used later
        if (root.haslibrary_materials()) {
            try {
                processMaterialLibrary(root.getlibrary_materials());
            } catch (Exception e) {
                if (!squelch) {
                    logger.log(Level.WARNING,
                            "Error processing material library information", e);
                }
            }
        }
        // process the library of effects, filling in the appropriate
        // states.
        if (root.haslibrary_effects()) {
            try {
                processEffects(root.getlibrary_effects());
            } catch (Exception e) {
                if (!squelch) {
                    logger.log(Level.WARNING,
                            "Error processing effects library information", e);
                }
            }
        }
        // process the geometry information, creating the appropriate Geometry
        // object from jME (TriMesh, lines or point).
        if (root.haslibrary_geometries()) {
            try {
                processGeometry(root.getlibrary_geometries());
            } catch (Exception e) {
                if (!squelch) {
                    logger.log(Level.WARNING,
                            "Error processing geometry library information", e);
                }
            }
        }
        // controllers will define the action of another object. For example,
        // there may be a controller with a skin tag, defining how a mesh
        // is skinning a skeleton.
        if (root.haslibrary_controllers()) {
            try {
                processControllerLibrary(root.getlibrary_controllers());
            } catch (Exception e) {
                if (!squelch) {
                    logger.log(Level.WARNING,
                            "Error processing controller library information",
                            e);
                }
            }
        }
        if (root.haslibrary_nodes()) {
            try {
                processNodes(root.getlibrary_nodes());
            } catch (Exception e) {
                if (!squelch) {
                    logger.log(Level.WARNING,
                            "Error processing nodes library information", e);
                }
            }
        }
        // process the visual scene. This scene will define how the geometries
        // are structured in the world.
        if (root.haslibrary_visual_scenes()) {
            try {
                processVisualSceneLibrary(root.getlibrary_visual_scenes());
            } catch (Exception e) {
                if (!squelch) {
                    logger
                            .log(
                                    Level.WARNING,
                                    "Error processing visual scene library information",
                                    e);
                }
            }
        }
        if (root.haslibrary_physics_scenes()) {
            try {
                library_physics_scenesType library = root
                        .getlibrary_physics_scenes();
                for (int i = 0; i < library.getphysics_sceneCount(); i++) {
                    physics_sceneType scene = library.getphysics_sceneAt(i);
                    put(scene.getid().toString(), scene);
                }
            } catch (Exception e) {
                if (!squelch) {
                    logger
                            .log(
                                    Level.WARNING,
                                    "Error processing physics scene library information",
                                    e);
                }
            }
        }
        if (root.haslibrary_physics_models()) {
            try {
                library_physics_modelsType library = root
                        .getlibrary_physics_models();
                for (int i = 0; i < library.getphysics_modelCount(); i++) {
                    physics_modelType model = library.getphysics_modelAt(i);
                    put(model.getid().toString(), model);
                }
            } catch (Exception e) {
                if (!squelch) {
                    logger
                            .log(
                                    Level.WARNING,
                                    "Error processing physics model library information",
                                    e);
                }
            }
        }
        // the scene tag actually takes instances of the visual scene defined
        // above and attaches them to the model that is returned.
        if (root.hasscene()) {
            try {
                processScene(root.getscene());
            } catch (Exception e) {
                if (!squelch) {
                    logger.log(Level.WARNING,
                            "Error processing scene information", e);
                }
            }
        }
        try {
            optimizeGeometry();
        } catch (Exception e) {
            if (!squelch) {
                logger.log(Level.WARNING, "Error optimizing geometry", e);
            }
        }
    }

    /**
     * optimizeGeometry
     */
    private void optimizeGeometry() {
        for (String key : resourceLibrary.keySet()) {
            Object val = resourceLibrary.get(key);

            if (val instanceof Spatial) {
                Spatial spatial = (Spatial) val;
                int options = GeometryTool.MV_SAME_COLORS
                        | GeometryTool.MV_SAME_NORMALS
                        | GeometryTool.MV_SAME_TEXS;
                if (spatial.getParent() instanceof SkinNode) {
                    SkinNode pNode = ((SkinNode) spatial.getParent());
                    pNode.assignSkeletonBoneInfluences();

                    if (spatial instanceof Node) {
                        Node skins = (Node) spatial;
                        for (int i = 0; i < skins.getQuantity(); i++) {
                            TriMesh mesh = (TriMesh) skins.getChild(i);

                            if (OPTIMIZE_GEOMETRY) {
                                VertMap map = GeometryTool.minimizeVerts(mesh,
                                        options);
                                if (optimizeCallBack != null) {
                                    optimizeCallBack.remapInfluences(mesh, map);
                                }

                                int geomIndex = pNode.getSkins().getChildIndex(
                                        mesh);
                                pNode.remapInfluences(map, geomIndex);
                            }
                        }
                    } else if (spatial instanceof TriMesh) {
                        TriMesh mesh = (TriMesh) spatial;
                        if (OPTIMIZE_GEOMETRY) {
                            VertMap map = GeometryTool.minimizeVerts(mesh,
                                    options);
                            if (optimizeCallBack != null) {
                                optimizeCallBack.remapInfluences(mesh, map);
                            }

                            int geomIndex = pNode.getSkins()
                                    .getChildIndex(mesh);
                            pNode.remapInfluences(map, geomIndex);
                        }
                    }

                    if (OPTIMIZE_GEOMETRY) {
                        pNode.regenInfluenceOffsets();
                    }

                    pNode.revertToBind();
                } else if (spatial instanceof TriMesh) {
                    TriMesh mesh = (TriMesh) spatial;
                    if (OPTIMIZE_GEOMETRY) {
                        VertMap map = GeometryTool.minimizeVerts(mesh, options);
                        if (optimizeCallBack != null) {
                            optimizeCallBack.remapInfluences(mesh, map);
                        }
                    }
                }
            }
        }
    }

    /**
     * processLightLibrary
     * 
     * @param libraryLights
     * @throws Exception
     */
    private void processLightLibrary(library_lightsType libraryLights)
            throws Exception {
        if (libraryLights.haslight()) {
            for (int i = 0; i < libraryLights.getlightCount(); i++) {
                processLight(libraryLights.getlightAt(i));
            }
        }
    }

    /**
     * @param light
     * @throws Exception
     */
    private void processLight(lightType light) throws Exception {
        technique_commonType4 common = light.gettechnique_common();
        Light l = null;
        if (common.hasdirectional()) {
            l = new DirectionalLight();
            l.setDiffuse(getLightColor(common.getdirectional().getcolor()));
        } else if (common.haspoint()) {
            l = new PointLight();
            l.setDiffuse(getLightColor(common.getpoint().getcolor()));
            l.setAttenuate(true);
            l.setConstant(Float.parseFloat(common.getpoint()
                    .getconstant_attenuation().getValue().toString()));
            l.setLinear(Float.parseFloat(common.getpoint()
                    .getlinear_attenuation().getValue().toString()));
            l.setQuadratic(Float.parseFloat(common.getpoint()
                    .getquadratic_attenuation().getValue().toString()));
        } else if (common.hasspot()) {
            l = new SpotLight();
            l.setDiffuse(getLightColor(common.getspot().getcolor()));
            l.setAttenuate(true);
            l.setConstant(Float.parseFloat(common.getspot()
                    .getconstant_attenuation().getValue().toString()));
            l.setLinear(Float.parseFloat(common.getspot()
                    .getlinear_attenuation().getValue().toString()));
            l.setQuadratic(Float.parseFloat(common.getspot()
                    .getquadratic_attenuation().getValue().toString()));
            ((SpotLight) l).setAngle(Float.parseFloat(common.getspot()
                    .getfalloff_angle().getValue().toString()));
            ((SpotLight) l).setExponent(Float.parseFloat(common.getspot()
                    .getfalloff_exponent().getValue().toString()));
        }
        if (l != null) {
            l.getSpecular().set(0, 0, 0, 1);
            if (common.hasambient()) {
                l.setAmbient(getLightColor(common.getambient().getcolor()));
            } else {
                l.getAmbient().set(0, 0, 0, 1);
            }
            l.setEnabled(true);
            LightNode lightNode = new LightNode(light.getid().toString());
            lightNode.setLight(l);
            if (lightNodeNames == null) {
                lightNodeNames = new ArrayList<String>();
            }
            lightNodeNames.add(lightNode.getName());
            put(lightNode.getName(), lightNode);
        }
    }

    /**
     * getLightColor
     * 
     * @param color
     * @return c
     */
    private ColorRGBA getLightColor(TargetableFloat3 color) {
        StringTokenizer st = new StringTokenizer(color.getValue().toString());
        return new ColorRGBA(Float.parseFloat(st.nextToken()), Float
                .parseFloat(st.nextToken()), Float.parseFloat(st.nextToken()),
                1);
    }

    /**
     * processScene finalizes the model node to be returned as the COLLADA
     * model. This looks up visual scene instances that were placed in the
     * resource library previously.
     * 
     * @param scene
     *            the scene to process
     * @throws Exception
     *             thrown if there is an error processing the XML.
     */
    public void processScene(sceneType scene) throws Exception {
        if (scene.hasinstance_visual_scene()) {
            for (int i = 0; i < scene.getinstance_visual_sceneCount(); i++) {
                String key = scene.getinstance_visual_sceneAt(i).geturl()
                        .toString().substring(1);
                Node n = (Node) resourceLibrary.get(key);
                if (n != null) {
                    model.attachChild(n);
                }
            }
        }
        if (scene.hasinstance_physics_scene()) {
            for (int i = 0; i < scene.getinstance_physics_sceneCount(); i++) {
                String key = scene.getinstance_physics_sceneAt(i).geturl()
                        .toString().substring(1);
                physics_sceneType physScene = (physics_sceneType) resourceLibrary
                        .get(key);
                if (physScene != null) {
                    processPhysicsScene(physScene);
                }
            }
        }
    }

    private void processPhysicsScene(physics_sceneType physScene)
            throws Exception {
        if (physScene.hasinstance_physics_model()) {
            for (int i = 0; i < physScene.getinstance_physics_modelCount(); i++) {
                instance_physics_modelType instPhysModel = physScene
                        .getinstance_physics_modelAt(i);
                String key = instPhysModel.geturl().toString().substring(1);
                physics_modelType physModel = (physics_modelType) resourceLibrary
                        .get(key);
                if (physModel != null) {
                    processPhysicsModel(physModel);
                }
                if (instPhysModel.hasinstance_rigid_body()) {
                    // get the Spatial that is the collision mesh
                    String rigidBodyKey = instPhysModel
                            .getinstance_rigid_body().getbody().toString();
                    Spatial collisionMesh = (Spatial) resourceLibrary
                            .get(rigidBodyKey);
                    if (collisionMesh != null) {
                        // get the target
                        String targetKey = instPhysModel
                                .getinstance_rigid_body().gettarget()
                                .toString().substring(1);
                        Node n = (Node) resourceLibrary.get(targetKey);
                        if (n != null) {
                            n.setUserData("COLLISION", collisionMesh);
                        }
                    }
                }
            }
        }
    }

    private void processPhysicsModel(physics_modelType physModel)
            throws Exception {
        // we only care about the shape (which for now will only reference a
        // geometry), so simply store this geometry with the name of the rigid
        // body as the key. Initially, this only supports a single shape per
        // physics model. Will be enhanced first available chance.
        if (physModel.hasrigid_body()) {
            for (int i = 0; i < physModel.getrigid_bodyCount(); i++) {
                rigid_bodyType rigidBody = physModel.getrigid_bodyAt(i);
                String id = rigidBody.getsid().toString();
                if (rigidBody.hastechnique_common()) {
                    if (rigidBody.gettechnique_common().hasshape()) {
                        for (int j = 0; j < rigidBody.gettechnique_common()
                                .getshapeCount(); j++) {
                            shapeType2 shape = rigidBody.gettechnique_common()
                                    .getshapeAt(j);
                            if (shape.hasinstance_geometry()) {
                                String key = shape.getinstance_geometry()
                                        .geturl().toString().substring(1);
                                Spatial s = (Spatial) resourceLibrary.get(key);
                                if (s != null) {
                                    put(id, s);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * processSource builds resource objects TIME, TRANSFORM and Name array for
     * the interpolation type.
     * 
     * @param source
     *            the source to process
     * @throws Exception
     *             exception thrown if there is a problem with
     */
    private void processSource(sourceType source) throws Exception {
        if (source.hasfloat_array()) {
            if (source.hastechnique_common()) {
                float[] floatArray = processFloatArray(source.getfloat_array());
                paramType3 p = source.gettechnique_common().getaccessor()
                        .getparam();
                if ("TIME".equals(p.getname().toString())) {
                    put(source.getid().toString(), floatArray);
                } else if ("float4x4".equals(p.gettype().toString())) {
                    Matrix4f[] transforms = new Matrix4f[floatArray.length / 16];
                    for (int i = 0; i < transforms.length; i++) {
                        transforms[i] = new Matrix4f();
                        float[] data = new float[16];
                        for (int x = 0; x < 16; x++) {
                            data[x] = floatArray[(16 * i) + x];
                        }
                        transforms[i].set(data, true); // collada matrices are
                        // in row order.
                    }
                    put(source.getid().toString(), transforms);
                } else if ("ROTX.ANGLE".equals(p.getname().toString())) {
                    if ("float".equals(p.gettype().toString())) {
                        float[] xRot = new float[floatArray.length];
                        System.arraycopy(floatArray, 0, xRot, 0, xRot.length);
                        put(source.getid().toString(), xRot);
                    } else {
                        if (!squelch) {
                            logger.warning(p.gettype() + " not yet supported "
                                    + "for animation transforms.");
                        }
                    }
                } else if ("ROTY.ANGLE".equals(p.getname().toString())) {
                    if ("float".equals(p.gettype().toString())) {
                        float[] yRot = new float[floatArray.length];
                        System.arraycopy(floatArray, 0, yRot, 0, yRot.length);
                        put(source.getid().toString(), yRot);
                    } else {
                        if (!squelch) {
                            logger.warning(p.gettype() + " not yet supported "
                                    + "for animation transforms.");
                        }
                    }
                } else if ("ROTZ.ANGLE".equals(p.getname().toString())) {
                    if ("float".equals(p.gettype().toString())) {
                        float[] zRot = new float[floatArray.length];
                        System.arraycopy(floatArray, 0, zRot, 0, zRot.length);
                        put(source.getid().toString(), zRot);
                    } else {
                        if (!squelch) {
                            logger.warning(p.gettype() + " not yet supported "
                                    + "for animation transforms.");
                        }
                    }
                } else if ("TRANS.X".equals(p.getname().toString())) {
                    if ("float".equals(p.gettype().toString())) {
                        float[] xTrans = new float[floatArray.length];
                        System.arraycopy(floatArray, 0, xTrans, 0,
                                xTrans.length);
                        put(source.getid().toString(), xTrans);
                    } else {
                        if (!squelch) {
                            logger.warning(p.gettype() + " not yet supported "
                                    + "for animation transforms.");
                        }
                    }
                } else if ("TRANS.Y".equals(p.getname().toString())) {
                    if ("float".equals(p.gettype().toString())) {
                        float[] yTrans = new float[floatArray.length];
                        System.arraycopy(floatArray, 0, yTrans, 0,
                                yTrans.length);
                        put(source.getid().toString(), yTrans);
                    } else {
                        if (!squelch) {
                            logger.warning(p.gettype() + " not yet supported "
                                    + "for animation transforms.");
                        }
                    }
                } else if ("TRANS.Z".equals(p.getname().toString())) {
                    if ("float".equals(p.gettype().toString())) {
                        float[] zTrans = new float[floatArray.length];
                        System.arraycopy(floatArray, 0, zTrans, 0,
                                zTrans.length);
                        put(source.getid().toString(), zTrans);
                    } else {
                        if (!squelch) {
                            logger.warning(p.gettype() + " not yet supported "
                                    + "for animation transforms.");
                        }
                    }
                } else {
                    if (!squelch) {
                        logger.warning(p.getname() + " not yet supported "
                                + "for animation source.");
                    }
                }
            }
        } else if (source.hasName_array()) {
            int[] interpolation = processInterpolationArray(source
                    .getName_array());
            put(source.getid().toString(), interpolation);
        }
    }

    /**
     * processInterpolationArray builds a int array that corresponds to the
     * interpolation types defined in BoneAnimationController.
     * 
     * @param array
     *            the array to process.
     * @return the int array.
     * @throws Exception
     *             thrown if there is a problem processing this XML document.
     */
    private int[] processInterpolationArray(Name_arrayType array)
            throws Exception {
        StringTokenizer st = new StringTokenizer(array.getValue().toString());
        int[] out = new int[array.getcount().intValue()];
        String token = null;
        for (int i = 0; i < out.length; i++) {
            token = st.nextToken();
            if ("LINEAR".equals(token)) {
                out[i] = BoneAnimation.LINEAR;
            } else if ("BEZIER".equals(token)) {
                out[i] = BoneAnimation.BEZIER;
            }
        }
        return out;
    }

    /**
     * processes a float array object. The floats are represented as a String
     * with the values delimited by a space.
     * 
     * @param array
     *            the array to parse.
     * @return the float array to return.
     * @throws Exception
     *             thrown if there is a problem processing the XML.
     */
    private float[] processFloatArray(float_arrayType array) throws Exception {
        StringTokenizer st = new StringTokenizer(array.getValue().toString());
        float[] out = new float[array.getcount().intValue()];
        for (int i = 0; i < out.length; i++) {
            out[i] = Float.parseFloat(st.nextToken());
        }
        return out;
    }

    /**
     * processAssetInformation will store the information about the collada file
     * for future reference. This will include the author, the tool used, the
     * revision, the unit information, and the defined up axis.
     * 
     * @param asset
     *            the assetType for the root of the model.
     */
    private void processAssetInformation(assetType asset) throws Exception {
        if (asset.hascontributor()) {
            if (asset.getcontributor().hasauthor()) {
                modelAuthor = asset.getcontributor().getauthor().toString();
            }
            if (asset.getcontributor().hasauthoring_tool()) {
                tool = asset.getcontributor().getauthoring_tool().toString();
            }
        }
        if (asset.hasrevision()) {
            revision = asset.getrevision().toString();
        }
        if (asset.hasunit()) {
            unitName = asset.getunit().getname().toString();
            unitMeter = asset.getunit().getmeter().floatValue();
        }
        if (asset.hasup_axis()) {
            upAxis = asset.getup_axis().getValue();
        }
    }

    /**
     * processAnimationLibrary will store the individual
     * BoneAnimationControllers in the resource library for future use.
     * Animations at this level can be considered top level animations that
     * should be called from this level. These animations may contain children
     * animations the top level animation is responsible for calling.
     * 
     * @param animLib
     *            the library of animations to parse.
     */
    private void processAnimationLibrary(library_animationsType animLib)
            throws Exception {
        if (animLib.hasanimation()) {
            if (controllerNames == null) {
                controllerNames = new ArrayList<String>();
            }
            for (int i = 0; i < animLib.getanimationCount(); i++) {
                BoneAnimation bac = processAnimation(animLib.getanimationAt(i));
                bac.setInterpolate(false);
                bac.optimize(true);
                put(bac.getName(), bac);
                controllerNames.add(bac.getName());
                if (animLib.getanimationAt(i).hasextra()) {
                    for (int j = 0; j < animLib.getanimationAt(i)
                            .getextraCount(); j++) {
                        logger.info("Processing extra in animation library.");
                        ExtraPluginManager.processExtra(bac, animLib
                                .getanimationAt(i).getextraAt(j));
                    }
                }
            }
        }
    }

    /**
     * the animation element categorizes an animation hierarchy with each
     * controller defining the animation's keyframe and sampler functions. These
     * interact on single bones, where a collection of controllers will build up
     * a complete animation.
     * 
     * @param animation
     *            the animation to parse.
     * @throws Exception
     *             thrown if there is a problem processing the XML.
     */
    private BoneAnimation processAnimation(animationType animation)
            throws Exception {
        BoneAnimation out = new BoneAnimation(animation.getid().toString());
        BoneTransform bt = new BoneTransform();
        /*
        out.setInterpolate(true);
        TODO:  Build old baselines and test this example to find out,
        first:  did this example ever interpolate; and if so, then
        when did the interpolation break.
        */
        if (animation.hassource()) {
            for (int i = 0; i < animation.getsourceCount(); i++) {
                processSource(animation.getsourceAt(i));
            }
        }
        float[] rotx = null;
        float[] roty = null;
        float[] rotz = null;
        float[] transx = null;
        float[] transy = null;
        float[] transz = null;
        boolean transformsSet = false;
        if (animation.hassampler()) {
            for (int j = 0; j < animation.getsamplerCount(); j++) {
                for (int i = 0; i < animation.getsamplerAt(j).getinputCount(); i++) {
                    if ("INPUT".equals(animation.getsamplerAt(j).getinputAt(i)
                            .getsemantic().toString())) {
                        String key = animation.getsamplerAt(j).getinputAt(i)
                                .getsource().toString().substring(1);
                        float[] times = (float[]) resourceLibrary.get(key);
                        if (times == null) {
                            logger.warning("Animation source invalid: " + key);
                            continue;
                        }
                        out.setTimes(times);
                        out.setStartFrame(0);
                        out.setEndFrame(times.length - 1);
                    } else if ("OUTPUT".equals(animation.getsamplerAt(j)
                            .getinputAt(i).getsemantic().toString())) {
                        String key = animation.getsamplerAt(j).getinputAt(i)
                                .getsource().toString().substring(1);
                        Object object = resourceLibrary.get(key);
                        if (object == null) {
                            logger.warning("Animation source invalid: " + key);
                            continue;
                        }
                        if (object instanceof Matrix4f[]) {
                            Matrix4f[] transforms = (Matrix4f[]) object;
                            bt.setTransforms(transforms);
                            transformsSet = true;
                        } else if (object instanceof float[]) {
                            // Another bit of a hack that should be improved:
                            // to put the float arrays into the BoneTransform,
                            // we need to know what angle it is changing,
                            // I see know way to determine other than looking
                            // at the source name.
                            if (animation.getsamplerAt(j).getinputAt(i)
                                    .getsource().toString().contains(
                                            "Rotate-X-")) {
                                rotx = (float[]) object;
                            } else if (animation.getsamplerAt(j).getinputAt(i)
                                    .getsource().toString().contains(
                                            "Rotate-Y-")) {
                                roty = (float[]) object;
                            } else if (animation.getsamplerAt(j).getinputAt(i)
                                    .getsource().toString().contains(
                                            "Rotate-Z-")) {
                                rotz = (float[]) object;
                            } else if (animation.getsamplerAt(j).getinputAt(i)
                                    .getsource().toString().contains(
                                            "Translate-X-")) {
                                transx = (float[]) object;
                            } else if (animation.getsamplerAt(j).getinputAt(i)
                                    .getsource().toString().contains(
                                            "Translate-Y-")) {
                                transy = (float[]) object;
                            } else if (animation.getsamplerAt(j).getinputAt(i)
                                    .getsource().toString().contains(
                                            "Translate-Z-")) {
                                transz = (float[]) object;
                            } else {
                                if (!squelch) {
                                    logger
                                            .warning("Not sure what this sampler is.");
                                }
                            }
                        }
                    } else if ("INTERPOLATION".equals(animation.getsamplerAt(j)
                            .getinputAt(i).getsemantic().toString())) {
                        String key = animation.getsamplerAt(j).getinputAt(i)
                                .getsource().toString().substring(1);
                        int[] interpolation = (int[]) resourceLibrary.get(key);
                        if (interpolation == null) {
                            logger.warning("Animation source invalid: " + key);
                            continue;
                        }
                        out.setInterpolationTypes(interpolation);
                    }
                }
            }
            if (!transformsSet) {
                Matrix4f[] transforms = generateTransforms(rotx, roty, rotz,
                        transx, transy, transz);
                if (transforms != null) {
                    bt.setTransforms(transforms);
                }
            }
        }
        if (animation.haschannel()) {
            String target = animation.getchannel().gettarget().toString();
            if (target.contains("/")) {
                String key = target.substring(0, animation.getchannel()
                        .gettarget().toString().indexOf('/'));
                bt.setBoneId(key);
                Bone b = (Bone) resourceLibrary.get(key);
                if (b != null) {
                    bt.setBone(b);
                }
                out.addBoneTransforms(bt);
            }
        }
        // if the animation has children attach them
        if (animation.hasanimation()) {
            for (int i = 0; i < animation.getanimationCount(); i++) {
                out.addBoneAnimation(processAnimation(animation
                        .getanimationAt(i)));
            }
        }
        return out;
    }

    private Matrix4f[] generateTransforms(float[] rotx, float[] roty,
            float[] rotz, float[] transx, float[] transy, float[] transz) {
        Quaternion rot = new Quaternion();
        int index = 0;
        if (rotx != null) {
            index = rotx.length;
        } else if (transx != null) {
            index = transx.length;
        }
        Matrix4f[] transforms = new Matrix4f[index];
        float[] angles = new float[3];
        for (int i = 0; i < transforms.length; i++) {
            angles[0] = angles[1] = angles[2] = 0;
            if (rotx != null) {
                angles[0] = rotx[i];
            }
            if (roty != null) {
                angles[1] = roty[i];
            }
            if (rotz != null) {
                angles[2] = rotz[i];
            }
            rot.fromAngles(angles);
            transforms[i] = rot.toRotationMatrix(new Matrix4f());
            if (transx != null) {
                transforms[i].m03 = transx[i];
            }
            if (transy != null) {
                transforms[i].m13 = transy[i];
            }
            if (transz != null) {
                transforms[i].m23 = transz[i];
            }
        }
        return transforms;
    }

    private void processCameraLibrary(library_camerasType libraryCam)
            throws Exception {
        if (libraryCam.hascamera()) {
            for (int i = 0; i < libraryCam.getcameraCount(); i++) {
                // processCamera(libraryCam.getcameraAt(i));
            }
        }
    }

    private void processCamera(cameraType camera) throws Exception {
        opticsType optics = camera.getoptics();
        technique_commonType2 common = optics.gettechnique_common();
        Renderer r = DisplaySystem.getDisplaySystem().getRenderer();
        int width = r.getWidth();
        int height = r.getHeight();
        // FIXME: THIS LINE IS SUPPOSED TO ONLY BE DONE IN A GL THREAD.
        Camera c = r.createCamera(width, height);
        float near = c.getFrustumNear();
        float far = c.getFrustumFar();
        float aspect = (float) width / (float) height;
        if (common.hasorthographic()) {
            orthographicType ortho = common.getorthographic();
            float xmag = 1.0f;
            float ymag = 1.0f;
            if (ortho.hasznear()) {
                near = Float.parseFloat(ortho.getznear().getValue().toString());
            }
            if (ortho.haszfar()) {
                far = Float.parseFloat(ortho.getzfar().getValue().toString());
            }
            if (ortho.hasxmag() && ortho.hasymag()) {
                xmag = Float.parseFloat(ortho.getxmag().getValue().toString());
                ymag = Float.parseFloat(ortho.getymag().getValue().toString());
            } else {
                if (ortho.hasaspect_ratio()) {
                    aspect = Float.parseFloat(ortho.getaspect_ratio()
                            .getValue().toString());
                }
                if (ortho.hasxmag()) {
                    assert (!ortho.hasymag());
                    xmag = Float.parseFloat(ortho.getxmag().getValue()
                            .toString());
                    ymag = xmag / aspect;
                } else {
                    assert (ortho.hasymag());
                    ymag = Float.parseFloat(ortho.getymag().getValue()
                            .toString());
                    xmag = ymag * aspect;
                }
            }
            c.setParallelProjection(true);
            c.setFrustum(near, far, -xmag, xmag, -ymag, ymag);
        } else {
            assert (common.hasperspective());
            perspectiveType persp = common.getperspective();
            float xfov = 1.0f;
            float yfov = 1.0f;
            if (persp.hasznear()) {
                near = Float.parseFloat(persp.getznear().getValue().toString());
            }
            if (persp.haszfar()) {
                far = Float.parseFloat(persp.getzfar().getValue().toString());
            }
            if (persp.hasxfov() && persp.hasyfov()) {
                xfov = Float.parseFloat(persp.getxfov().getValue().toString());
                yfov = Float.parseFloat(persp.getyfov().getValue().toString());
            } else {
                if (persp.hasaspect_ratio()) {
                    aspect = Float.parseFloat(persp.getaspect_ratio()
                            .getValue().toString());
                }
                if (persp.hasxfov()) {
                    assert (!persp.hasyfov());
                    xfov = Float.parseFloat(persp.getxfov().getValue()
                            .toString());
                    yfov = xfov / aspect;
                } else {
                    assert (persp.hasyfov());
                    yfov = Float.parseFloat(persp.getyfov().getValue()
                            .toString());
                    xfov = yfov * aspect;
                }
            }
            c.setParallelProjection(false);
            c.setFrustumPerspective(yfov, aspect, near, far);
        }
        if (cameraNodeNames == null) {
            cameraNodeNames = new ArrayList<String>();
        }
        CameraNode nodeCamera = new CameraNode(camera.getid().toString(), c);
        // cameras are odd in that their rotation is typically exported
        // backwards from the direction that they're looking in the scene
        if ("X_UP".equals(upAxis))
            nodeCamera.setLocalRotation(new Quaternion(1, 0, 0, 0));
        else if ("Y_UP".equals(upAxis))
            nodeCamera.setLocalRotation(new Quaternion(0, 1, 0, 0));
        else if ("Z_UP".equals(upAxis))
            nodeCamera.setLocalRotation(new Quaternion(0, 0, 1, 0));
        cameraNodeNames.add(nodeCamera.getName());
        put(nodeCamera.getName(), nodeCamera);
    }

    /**
     * processImageLibrary will build a collection of image filenames. The image
     * tag contains the full directory path of the image from the artists
     * working directory. Therefore, the directory will be stripped off leaving
     * only the filename. This filename will be associated with a id key that
     * can be obtained by the material that wishes to make use of it.
     * 
     * @param libraryImg
     *            the library of images (name/image pair).
     */
    private void processImageLibrary(library_imagesType libraryImg)
            throws Exception {
        if (libraryImg.hasimage()) {
            for (int i = 0; i < libraryImg.getimageCount(); i++) {
                processImage(libraryImg.getimageAt(i));
            }
        }
    }

    /**
     * processImage takes an image type and places the necessary information in
     * the resource library.
     * 
     * @param image
     *            the image to process.
     * @throws Exception
     *             thrown if there is a problem with the image type.
     */
    private void processImage(imageType image) throws Exception {
        if (image.hasdata()) {
            if (!squelch) {
                logger.warning("Raw data images not supported.");
            }
        }
        if (image.hasinit_from()) {
            put(image.getid().toString(), image.getinit_from().toString());
        }
    }

    /**
     * processMaterialLibrary will build a collection (Map) of MaterialStates,
     * with the defined material id as the key in the Map. This map and
     * corresponding key will then be used to apply materials to the appropriate
     * node. The library only defines the id of the material and the URL of the
     * instance effect that defines its qualities, it won't be until the
     * library_effects tag is processed that the material state information is
     * filled in.
     * 
     * @param libraryMat
     *            the material library type.
     * @throws Exception
     *             thrown if there is a problem processing the XML.
     */
    private void processMaterialLibrary(library_materialsType libraryMat)
            throws Exception {
        if (libraryMat.hasmaterial()) {
            for (int i = 0; i < libraryMat.getmaterialCount(); i++) {
                processMaterial(libraryMat.getmaterialAt(i));
            }
        }
    }

    /**
     * process Material which typically contains an id and a reference URL to an
     * effect.
     * 
     * @param mat
     * @throws Exception
     *             thrown if there is a problem processing the XML.
     */
    private void processMaterial(materialType mat) throws Exception {
        ColladaMaterial material = new ColladaMaterial();
        String url = null;
        if (mat.hasinstance_effect()) {
            url = mat.getinstance_effect().geturl().toString();
            if (url.startsWith("#")) {
                url = url.substring(1);
            }
            put(url, material);
            put(mat.getid().toString(), url);
        }
        if (mat.hasextra()) {
            ExtraPluginManager.processExtra(material, mat.getextra());
        }
    }

    /**
     * processEffects will build effects as defined by the technique. The
     * appropriate render state will be obtained from the materialMap hashmap
     * based on the the name of the effect. Currently, the id of the effect is
     * ignored as it is directly tied to the material id. However, in the future
     * this may require support.
     * 
     * @param libraryEffects
     *            the library of effects to build.
     * @throws Exception
     *             thrown if there is a problem processing the XML.
     */
    private void processEffects(library_effectsType libraryEffects)
            throws Exception {
        if (libraryEffects.haseffect()) {
            for (int i = 0; i < libraryEffects.geteffectCount(); i++) {
                String key = libraryEffects.geteffectAt(i).getid().toString();
                ColladaMaterial mat = (ColladaMaterial) resourceLibrary
                        .get(key);
                if (mat != null) {
                    fillMaterial(libraryEffects.geteffectAt(i), mat);
                }
            }
        }
    }

    /**
     * fillMaterial will use the provided effectType to generate the material
     * setting for the collada model. The effect can handle both programmable
     * pipelines and fixed pipelines. This is defined by what sort of profile it
     * is using (profile_COMMON, profile_GLSL, profile_CG). Currently,
     * profile_CG is ignored. There may be multiple profiles, describing a path
     * of fallbacks. Currently, only one profile will be supported at a time.<br>
     * <br>
     * There is a possibility that each profile may have multiple techniques,
     * defining different materials for different situations, i.e. LOD. This
     * version of the loader will assume a single technique.
     * 
     * @param effect
     *            the collada effect to process.
     * @param mat
     *            the ColladaMaterial that will hold the RenderStates needed to
     *            express this material.
     * @throws Exception
     *             thrown if there is a problem processing the file.
     */
    private void fillMaterial(effectType effect, ColladaMaterial mat)
            throws Exception {
        // process the fixed pipeline information
        if (effect.hasprofile_COMMON()) {
            for (int i = 0; i < effect.getprofile_COMMON().getnewparamCount(); i++) {
                processNewParam(effect.getprofile_COMMON().getnewparamAt(i),
                        mat);
            }
            for (int i = 0; i < effect.getprofile_COMMON().gettechniqueCount(); i++) {
                processTechniqueCOMMON(effect.getprofile_COMMON()
                        .gettechniqueAt(i), mat);
            }
            if (effect.getprofile_COMMON().hasextra()) {
                for (int i = 0; i < effect.getprofile_COMMON().getextraCount(); i++) {
                    ExtraPluginManager.processExtra(mat, effect
                            .getprofile_COMMON().getextraAt(i));
                }
            }
        }
        // process the programmable pipeline
        // profile_GLSL defines all of OpenGL states as well as GLSL shaders.
        if (effect.hasprofile_GLSL()) {
            for (int i = 0; i < effect.getprofile_GLSL().gettechniqueCount(); i++) {
                processTechniqueGLSL(
                        effect.getprofile_GLSL().gettechniqueAt(i), mat);
            }
        }
    }

    /**
     * processNewParam sets specific properties of a material (surface
     * properties, sampler properties, etc).
     * 
     * @param param
     *            the XML element of the new parameter.
     * @param mat
     *            the material to store the parameters in.
     * @throws Exception
     *             thrown if there is a problem reading the XML.
     */
    private void processNewParam(common_newparam_type param, ColladaMaterial mat)
            throws Exception {
        if (param.hassampler2D()) {
            processSampler2D(param.getsid().toString(), param.getsampler2D(),
                    mat);
        }
        if (param.hassurface()) {
            processSurface(param.getsid().toString(), param.getsurface());
        }
    }

    /**
     * processes images information, defining the min and mag filter for
     * mipmapping.
     * 
     * @param id
     *            the id on the sampler
     * @param sampler
     *            the sampler XML element.
     * @param mat
     *            the material to store the values in.
     * @throws Exception
     *             thrown if there is a problem reading the file.
     */
    private void processSampler2D(String id, fx_sampler2D_common sampler,
            ColladaMaterial mat) throws Exception {
        if (sampler.hasmagfilter()) {
            mat.magFilter = sampler.getmagfilter().getValue();
        }
        if (sampler.hasminfilter()) {
            mat.minFilter = sampler.getminfilter().getValue();
        }
        mat.wrapS = "WRAP";
        mat.wrapT = "WRAP";
        
        put(id, sampler.getsource().getValue());
    }

    private void processSurface(String id, fx_surface_common surface)
            throws Exception {
        put(id, surface.getinit_from().getValue().toString());
    }

    /**
     * processes rendering information defined to be GLSL standard, which
     * includes all OpenGL state information and GLSL shader information.
     * 
     * @param technique
     * @param mat
     * @throws Exception
     */
    private void processTechniqueGLSL(techniqueType4 technique,
            ColladaMaterial mat) throws Exception {
        if (technique.haspass()) {
            for (int i = 0; i < technique.getpassCount(); i++) {
                processPassGLSL(technique.getpassAt(i), mat);
            }
        }
    }

    private void processPassGLSL(passType3 pass, ColladaMaterial mat)
            throws Exception {
        // XXX only a single pass supported currently. If multiple passes
        // XXX are defined under a profile_GLSL the states will be combined
        // XXX to a single pass. If the same render state is defined in
        // XXX different passes, the last pass will override the previous.
        if (pass.hasclip_plane()) {
            ClipState cs = (ClipState) mat.getState(RenderState.StateType.Clip);
            if (cs == null) {
                cs = DisplaySystem.getDisplaySystem().getRenderer()
                        .createClipState();
                mat.setState(cs);
            }
            if (pass.getclip_plane().hasindex()
                    && pass.getclip_plane().hasvalue2()) {
                int index = pass.getclip_plane().getindex().intValue();
                StringTokenizer st = new StringTokenizer(pass.getclip_plane()
                        .getvalue2().toString());
                float[] clip = new float[4];
                for (int i = 0; i < 4; i++) {
                    clip[i] = Float.parseFloat(st.nextToken());
                }
                cs.setClipPlaneEquation(index, clip[0], clip[1], clip[2],
                        clip[3]);
            }
        }
        if (pass.hasclip_plane_enable()) {
            ClipState cs = (ClipState) mat.getState(RenderState.StateType.Clip);
            if (cs == null) {
                cs = DisplaySystem.getDisplaySystem().getRenderer()
                        .createClipState();
                mat.setState(cs);
            }
            if (pass.getclip_plane_enable().hasindex()
                    && pass.getclip_plane_enable().hasvalue2()) {
                int index = pass.getclip_plane().getindex().intValue();
                cs.setEnableClipPlane(index, pass.getclip_plane_enable()
                        .getvalue2().booleanValue());
            }
        }
        if (pass.hascolor_mask()) {
            ColorMaskState cms = (ColorMaskState) mat
                    .getState(RenderState.StateType.ColorMask);
            if (cms == null) {
                cms = DisplaySystem.getDisplaySystem().getRenderer()
                        .createColorMaskState();
                mat.setState(cms);
            }
            if (pass.getcolor_mask().hasvalue2()) {
                StringTokenizer st = new StringTokenizer(pass.getcolor_mask()
                        .getvalue2().toString());
                boolean[] color = new boolean[4];
                for (int i = 0; i < 4; i++) {
                    color[i] = Boolean.parseBoolean(st.nextToken());
                }
                cms.setRed(color[0]);
                cms.setGreen(color[1]);
                cms.setBlue(color[2]);
                cms.setAlpha(color[3]);
            }
        }
        if (pass.hasdepth_func()) {
            ZBufferState zbs = (ZBufferState) mat
                    .getState(RenderState.StateType.ZBuffer);
            if (zbs == null) {
                zbs = DisplaySystem.getDisplaySystem().getRenderer()
                        .createZBufferState();
                mat.setState(zbs);
            }
            if (pass.getdepth_func().hasvalue2()) {
                String depth = pass.getdepth_func().getvalue2().toString();
                if ("NEVER".equals(depth)) {
                    zbs.setFunction(ZBufferState.TestFunction.Never);
                } else if ("LESS".equals(depth)) {
                    zbs.setFunction(ZBufferState.TestFunction.LessThan);
                } else if ("LEQUAL".equals(depth)) {
                    zbs.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
                } else if ("EQUAL".equals(depth)) {
                    zbs.setFunction(ZBufferState.TestFunction.EqualTo);
                } else if ("GREATER".equals(depth)) {
                    zbs.setFunction(ZBufferState.TestFunction.GreaterThan);
                } else if ("NOTEQUAL".equals(depth)) {
                    zbs.setFunction(ZBufferState.TestFunction.NotEqualTo);
                } else if ("GEQUAL".equals(depth)) {
                    zbs.setFunction(ZBufferState.TestFunction.GreaterThanOrEqualTo);
                } else if ("ALWAYS".equals(depth)) {
                    zbs.setFunction(ZBufferState.TestFunction.Always);
                }
            }
        }
        if (pass.hasdepth_mask()) {
            ZBufferState zbs = (ZBufferState) mat
                    .getState(RenderState.StateType.ZBuffer);
            if (zbs == null) {
                zbs = DisplaySystem.getDisplaySystem().getRenderer()
                        .createZBufferState();
                mat.setState(zbs);
            }
            if (pass.getdepth_mask().hasvalue2()) {
                zbs
                        .setWritable(pass.getdepth_mask().getvalue2()
                                .booleanValue());
            }
        }
        if (pass.hasdepth_test_enable()) {
            ZBufferState zbs = (ZBufferState) mat
                    .getState(RenderState.StateType.ZBuffer);
            if (zbs == null) {
                zbs = DisplaySystem.getDisplaySystem().getRenderer()
                        .createZBufferState();
                mat.setState(zbs);
            }
            if (pass.getdepth_test_enable().hasvalue2()) {
                zbs.setEnabled(pass.getdepth_test_enable().getvalue2()
                        .booleanValue());
            }
        }
        if (pass.hascolor_material()) {
            MaterialState ms = (MaterialState) mat
                    .getState(RenderState.StateType.Material);
            if (ms == null) {
                ms = DisplaySystem.getDisplaySystem().getRenderer()
                        .createMaterialState();
                mat.setState(ms);
            }
            if (pass.getcolor_material().hasface()) {
                String face = pass.getcolor_material().getface().getvalue2()
                        .toString();
                if ("FRONT".equals(face)) {
                    ms.setMaterialFace(MaterialState.MaterialFace.Front);
                } else if ("BACK".equals(face)) {
                    ms.setMaterialFace(MaterialState.MaterialFace.Back);
                } else if ("FRONT_AND_BACK".equals(face)) {
                    ms.setMaterialFace(MaterialState.MaterialFace.FrontAndBack);
                }
            }
            if (pass.getcolor_material().hasmode()) {
                String mode = pass.getcolor_material().getmode().getvalue2()
                        .toString();
                if ("AMBIENT".equals(mode)) {
                    ms.setColorMaterial(MaterialState.ColorMaterial.Ambient);
                } else if ("EMISSION".equals(mode)) {
                    ms.setColorMaterial(MaterialState.ColorMaterial.Emissive);
                } else if ("DIFFUSE".equals(mode)) {
                    ms.setColorMaterial(MaterialState.ColorMaterial.Diffuse);
                } else if ("SPECULAR".equals(mode)) {
                    ms.setColorMaterial(MaterialState.ColorMaterial.Specular);
                } else if ("AMBIENT_AND_DIFFUSE".equals(mode)) {
                    ms.setColorMaterial(MaterialState.ColorMaterial.AmbientAndDiffuse);
                }
            }
        }
        if (pass.hasfog_color()) {
            FogState fs = (FogState) mat.getState(RenderState.StateType.Fog);
            if (fs == null) {
                fs = DisplaySystem.getDisplaySystem().getRenderer()
                        .createFogState();
                mat.setState(fs);
            }
            if (pass.getfog_color().hasvalue2()) {
                StringTokenizer st = new StringTokenizer(pass.getfog_color()
                        .getvalue2().toString());
                float[] color = new float[4];
                for (int i = 0; i < 4; i++) {
                    color[i] = Float.parseFloat(st.nextToken());
                }
                fs.setColor(new ColorRGBA(color[0], color[1], color[2],
                        color[3]));
            }
        }
        if (pass.hasfog_density()) {
            FogState fs = (FogState) mat.getState(RenderState.StateType.Fog);
            if (fs == null) {
                fs = DisplaySystem.getDisplaySystem().getRenderer()
                        .createFogState();
                mat.setState(fs);
            }
            if (pass.getfog_density().hasvalue2()) {
                fs.setDensity(pass.getfog_density().getvalue2().floatValue());
            }
        }
        if (pass.hasfog_enable()) {
            FogState fs = (FogState) mat.getState(RenderState.StateType.Fog);
            if (fs == null) {
                fs = DisplaySystem.getDisplaySystem().getRenderer()
                        .createFogState();
                mat.setState(fs);
            }
            if (pass.getfog_enable().hasvalue2()) {
                fs.setEnabled(pass.getfog_enable().getvalue2().booleanValue());
            }
        }
        if (pass.hasfog_end()) {
            FogState fs = (FogState) mat.getState(RenderState.StateType.Fog);
            if (fs == null) {
                fs = DisplaySystem.getDisplaySystem().getRenderer()
                        .createFogState();
                mat.setState(fs);
            }
            if (pass.getfog_end().hasvalue2()) {
                fs.setEnd(pass.getfog_end().getvalue2().floatValue());
            }
        }
        if (pass.hasfog_mode()) {
            FogState fs = (FogState) mat.getState(RenderState.StateType.Fog);
            if (fs == null) {
                fs = DisplaySystem.getDisplaySystem().getRenderer()
                        .createFogState();
                mat.setState(fs);
            }
            if (pass.getfog_mode().hasvalue2()) {
                String mode = pass.getfog_mode().getvalue2().toString();
                if ("LINEAR".equals(mode)) {
                    fs.setDensityFunction(FogState.DensityFunction.Linear);
                } else if ("EXP".equals(mode)) {
                    fs.setDensityFunction(FogState.DensityFunction.Exponential);
                } else if ("EXP2".equals(mode)) {
                    fs.setDensityFunction(FogState.DensityFunction.ExponentialSquared);
                }
            }
        }
        if (pass.hasfog_start()) {
            FogState fs = (FogState) mat.getState(RenderState.StateType.Fog);
            if (fs == null) {
                fs = DisplaySystem.getDisplaySystem().getRenderer()
                        .createFogState();
                mat.setState(fs);
            }
            if (pass.getfog_start().hasvalue2()) {
                fs.setStart(pass.getfog_start().getvalue2().floatValue());
            }
        }
        if (pass.hasalpha_test_enable()) {
            BlendState as = (BlendState) mat.getState(RenderState.StateType.Blend);
            if (as == null) {
                as = DisplaySystem.getDisplaySystem().getRenderer()
                        .createBlendState();
                mat.setState(as);
            }
            as.setTestEnabled(pass.getalpha_test_enable().getvalue2()
                    .booleanValue());
        }
        if (pass.hasalpha_func()) {
            BlendState as = (BlendState) mat.getState(RenderState.StateType.Blend);
            if (as == null) {
                as = DisplaySystem.getDisplaySystem().getRenderer()
                        .createBlendState();
                mat.setState(as);
            }
            if (pass.getalpha_func().hasfunc()) {
                String func = pass.getalpha_func().getfunc().getvalue2()
                        .toString();
                if ("NEVER".equals(func)) {
                    as.setTestFunction(BlendState.TestFunction.Never);
                } else if ("LESS".equals(func)) {
                    as.setTestFunction(BlendState.TestFunction.LessThan);
                } else if ("LEQUAL".equals(func)) {
                    as.setTestFunction(BlendState.TestFunction.LessThanOrEqualTo);
                } else if ("EQUAL".equals(func)) {
                    as.setTestFunction(BlendState.TestFunction.EqualTo);
                } else if ("GREATER".equals(func)) {
                    as.setTestFunction(BlendState.TestFunction.GreaterThan);
                } else if ("NOTEQUAL".equals(func)) {
                    as.setTestFunction(BlendState.TestFunction.NotEqualTo);
                } else if ("GEQUAL".equals(func)) {
                    as.setTestFunction(BlendState.TestFunction.GreaterThanOrEqualTo);
                } else if ("ALWAYS".equals(func)) {
                    as.setTestFunction(BlendState.TestFunction.Always);
                }
            }
            if (pass.getalpha_func().hasvalue2()) {
                as.setReference(pass.getalpha_func().getvalue2().getvalue2()
                        .floatValue());
            }
        }
        if (pass.hasblend_enable()) {
            BlendState as = (BlendState) mat.getState(RenderState.StateType.Blend);
            if (as == null) {
                as = DisplaySystem.getDisplaySystem().getRenderer()
                        .createBlendState();
                mat.setState(as);
            }
            as.setBlendEnabled(pass.getblend_enable().getvalue2()
                    .booleanValue());
        }
        if (pass.hasblend_func()) {
            BlendState as = (BlendState) mat.getState(RenderState.StateType.Blend);
            if (as == null) {
                as = DisplaySystem.getDisplaySystem().getRenderer()
                        .createBlendState();
                mat.setState(as);
            }
            if (pass.getblend_func().hasdest()) {
                String dest = pass.getblend_func().getdest().getvalue2()
                        .toString();
                if ("ZERO".equals(dest)) {
                    as.setDestinationFunction(BlendState.DestinationFunction.Zero);
                } else if ("ONE".equals(dest)) {
                    as.setDestinationFunction(BlendState.DestinationFunction.One);
                } else if ("SRC_COLOR".equals(dest)) {
                    as.setDestinationFunction(BlendState.DestinationFunction.SourceColor);
                } else if ("ONE_MINUS_SRC_COLOR".equals(dest)) {
                    as.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceColor);
                } else if ("SRC_ALPHA".equals(dest)) {
                    as.setDestinationFunction(BlendState.DestinationFunction.SourceAlpha);
                } else if ("ONE_MINUS_SRC_ALPHA".equals(dest)) {
                    as.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
                } else if ("DST_ALPHA".equals(dest)) {
                    as.setDestinationFunction(BlendState.DestinationFunction.DestinationAlpha);
                } else if ("ONE_MINUS_DST_ALPHA".equals(dest)) {
                    as.setDestinationFunction(BlendState.DestinationFunction.OneMinusDestinationAlpha);
                } else if ("CONSTANT_COLOR".equals(dest)) {
                    as.setDestinationFunction(BlendState.DestinationFunction.ConstantColor);
                } else if ("ONE_MINUS_CONSTANT_COLOR".equals(dest)) {
                    as.setDestinationFunction(BlendState.DestinationFunction.OneMinusConstantColor);
                } else if ("CONSTANT_ALPHA".equals(dest)) {
                    as.setDestinationFunction(BlendState.DestinationFunction.ConstantAlpha);
                } else if ("ONE_MINUS_CONSTANT_ALPHA".equals(dest)) {
                    as.setDestinationFunction(BlendState.DestinationFunction.OneMinusConstantAlpha);
                }
            }
            if (pass.getblend_func().hassrc()) {
                String src = pass.getblend_func().getsrc().getvalue2()
                        .toString();
                if ("ZERO".equals(src)) {
                    as.setSourceFunction(BlendState.SourceFunction.Zero);
                } else if ("ONE".equals(src)) {
                    as.setSourceFunction(BlendState.SourceFunction.One);
                } else if ("DEST_COLOR".equals(src)) {
                    as.setSourceFunction(BlendState.SourceFunction.DestinationColor);
                } else if ("ONE_MINUS_DEST_COLOR".equals(src)) {
                    as.setSourceFunction(BlendState.SourceFunction.OneMinusDestinationColor);
                } else if ("SRC_ALPHA".equals(src)) {
                    as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
                } else if ("ONE_MINUS_SRC_ALPHA".equals(src)) {
                    as.setSourceFunction(BlendState.SourceFunction.OneMinusDestinationAlpha);
                } else if ("DST_ALPHA".equals(src)) {
                    as.setSourceFunction(BlendState.SourceFunction.DestinationAlpha);
                } else if ("ONE_MINUS_DST_ALPHA".equals(src)) {
                    as.setSourceFunction(BlendState.SourceFunction.OneMinusDestinationAlpha);
                } else if ("CONSTANT_COLOR".equals(src)) {
                    as.setSourceFunction(BlendState.SourceFunction.ConstantColor);
                } else if ("ONE_MINUS_CONSTANT_COLOR".equals(src)) {
                    as.setSourceFunction(BlendState.SourceFunction.OneMinusConstantColor);
                } else if ("CONSTANT_ALPHA".equals(src)) {
                    as.setSourceFunction(BlendState.SourceFunction.ConstantAlpha);
                } else if ("ONE_MINUS_CONSTANT_ALPHA".equals(src)) {
                    as.setSourceFunction(BlendState.SourceFunction.OneMinusConstantAlpha);
                } else if ("SRC_ALPHA_SATURATE".equals(src)) {
                    as.setSourceFunction(BlendState.SourceFunction.SourceAlphaSaturate);
                }
            }
        }
        if (pass.hascull_face_enable()) {
            CullState cs = (CullState) mat.getState(RenderState.StateType.Cull);
            if (cs == null) {
                cs = DisplaySystem.getDisplaySystem().getRenderer()
                        .createCullState();
                mat.setState(cs);
            }
            cs
                    .setEnabled(pass.getcull_face_enable().getvalue2()
                            .booleanValue());
        }
        if (pass.hascull_face()) {
            CullState cs = (CullState) mat.getState(RenderState.StateType.Cull);
            if (cs == null) {
                cs = DisplaySystem.getDisplaySystem().getRenderer()
                        .createCullState();
                mat.setState(cs);
            }
            if (pass.getcull_face().hasvalue2()) {
                String face = pass.getcull_face().getvalue2().toString();
                if ("FRONT".equals(face)) {
                    cs.setCullFace(CullState.Face.Front);
                } else if ("BACK".equals(face)) {
                    cs.setCullFace(CullState.Face.Back);
                } else if ("FRONT_AND_BACK".equals(face)) {
                    cs.setCullFace(CullState.Face.FrontAndBack);
                }
            }
        }
        // Define the ShadeState (FLAT OR SMOOTH);
        if (pass.hasshade_model()) {
            ShadeState ss = (ShadeState) mat.getState(RenderState.StateType.Shade);
            if (ss == null) {
                ss = DisplaySystem.getDisplaySystem().getRenderer()
                        .createShadeState();
                mat.setState(ss);
            }
            if (pass.getshade_model().hasvalue2()) {
                String shade = pass.getshade_model().getvalue2().toString();
                if ("FLAT".equals(shade)) {
                    ss.setShadeMode(ShadeState.ShadeMode.Flat);
                } else if ("SMOOTH".equals(shade)) {
                    ss.setShadeMode(ShadeState.ShadeMode.Smooth);
                }
            }
        }
        if (pass.hasmaterial_ambient()) {
            MaterialState ms = (MaterialState) mat
                    .getState(RenderState.StateType.Material);
            if (ms == null) {
                ms = DisplaySystem.getDisplaySystem().getRenderer()
                        .createMaterialState();
                mat.setState(ms);
            }
            if (pass.getmaterial_ambient().hasvalue2()) {
                StringTokenizer st = new StringTokenizer(pass
                        .getmaterial_ambient().getvalue2().toString());
                float[] color = new float[4];
                for (int i = 0; i < 4; i++) {
                    color[i] = Float.parseFloat(st.nextToken());
                }
                ms.setAmbient(new ColorRGBA(color[0], color[1], color[2],
                        color[3]));
            }
        }
        if (pass.hasmaterial_diffuse()) {
            MaterialState ms = (MaterialState) mat
                    .getState(RenderState.StateType.Material);
            if (ms == null) {
                ms = DisplaySystem.getDisplaySystem().getRenderer()
                        .createMaterialState();
                mat.setState(ms);
            }
            if (pass.getmaterial_diffuse().hasvalue2()) {
                StringTokenizer st = new StringTokenizer(pass
                        .getmaterial_diffuse().getvalue2().toString());
                float[] color = new float[4];
                for (int i = 0; i < 4; i++) {
                    color[i] = Float.parseFloat(st.nextToken());
                }
                ms.setDiffuse(new ColorRGBA(color[0], color[1], color[2],
                        color[3]));
            }
        }
        if (pass.hasmaterial_emission()) {
            MaterialState ms = (MaterialState) mat
                    .getState(RenderState.StateType.Material);
            if (ms == null) {
                ms = DisplaySystem.getDisplaySystem().getRenderer()
                        .createMaterialState();
                mat.setState(ms);
            }
            if (pass.getmaterial_emission().hasvalue2()) {
                StringTokenizer st = new StringTokenizer(pass
                        .getmaterial_emission().getvalue2().toString());
                float[] color = new float[4];
                for (int i = 0; i < 4; i++) {
                    color[i] = Float.parseFloat(st.nextToken());
                }
                ms.setEmissive(new ColorRGBA(color[0], color[1], color[2],
                        color[3]));
            }
        }
        if (pass.hasmaterial_shininess()) {
            MaterialState ms = (MaterialState) mat
                    .getState(RenderState.StateType.Material);
            if (ms == null) {
                ms = DisplaySystem.getDisplaySystem().getRenderer()
                        .createMaterialState();
                mat.setState(ms);
            }
            if (pass.getmaterial_shininess().hasvalue2()) {
                ms.setShininess(pass.getmaterial_shininess().getvalue2()
                        .floatValue());
            }
        }
        if (pass.hasmaterial_specular()) {
            MaterialState ms = (MaterialState) mat
                    .getState(RenderState.StateType.Material);
            if (ms == null) {
                ms = DisplaySystem.getDisplaySystem().getRenderer()
                        .createMaterialState();
                mat.setState(ms);
            }
            if (pass.getmaterial_specular().hasvalue2()) {
                StringTokenizer st = new StringTokenizer(pass
                        .getmaterial_specular().getvalue2().toString());
                float[] color = new float[4];
                for (int i = 0; i < 4; i++) {
                    color[i] = Float.parseFloat(st.nextToken());
                }
                ms.setSpecular(new ColorRGBA(color[0], color[1], color[2],
                        color[3]));
            }
        }
        if (pass.hasstencil_func()) {
            StencilState ss = (StencilState) mat
                    .getState(RenderState.StateType.Stencil);
            if (ss == null) {
                ss = DisplaySystem.getDisplaySystem().getRenderer()
                        .createStencilState();
                // FIXME: This, and other if == null sections do not set new state back into mat.
            }
            if (pass.getstencil_func().hasfunc()) {
                String func = pass.getstencil_func().getfunc().toString();
                if ("NEVER".equals(func)) {
                    ss.setStencilFunction(StencilState.StencilFunction.Never);
                } else if ("LESS".equals(func)) {
                    ss.setStencilFunction(StencilState.StencilFunction.LessThan);
                } else if ("LEQUAL".equals(func)) {
                    ss.setStencilFunction(StencilState.StencilFunction.LessThanOrEqualTo);
                } else if ("EQUAL".equals(func)) {
                    ss.setStencilFunction(StencilState.StencilFunction.EqualTo);
                } else if ("GREATER".equals(func)) {
                    ss.setStencilFunction(StencilState.StencilFunction.GreaterThan);
                } else if ("NOTEQUAL".equals(func)) {
                    ss.setStencilFunction(StencilState.StencilFunction.NotEqualTo);
                } else if ("GEQUAL".equals(func)) {
                    ss.setStencilFunction(StencilState.StencilFunction.GreaterThanOrEqualTo);
                } else if ("ALWAYS".equals(func)) {
                    ss.setStencilFunction(StencilState.StencilFunction.Always);
                }
            }
            if (pass.getstencil_func().hasref()) {
                ss.setStencilReference(pass.getstencil_func().getref().getvalue2()
                        .intValue());
            }
            if (pass.getstencil_func().hasmask()) {
                ss.setStencilReference(pass.getstencil_func().getmask().getvalue2()
                        .intValue());
            }
        }
        if (pass.hasstencil_op()) {
            StencilState ss = (StencilState) mat
                    .getState(RenderState.StateType.Stencil);
            if (ss == null) {
                ss = DisplaySystem.getDisplaySystem().getRenderer()
                        .createStencilState();
            }
            if (pass.getstencil_op().hasfail()) {
                ss.setStencilOpFail(evaluateStencilOp(pass.getstencil_op()
                        .getfail().toString()));
            }
            if (pass.getstencil_op().haszfail()) {
                ss.setStencilOpZFail(evaluateStencilOp(pass.getstencil_op()
                        .getzfail().toString()));
            }
            if (pass.getstencil_op().haszpass()) {
                ss.setStencilOpZPass(evaluateStencilOp(pass.getstencil_op()
                        .getzpass().toString()));
            }
        }
        if (pass.hasstencil_test_enable()) {
            StencilState ss = (StencilState) mat
                    .getState(RenderState.StateType.Stencil);
            if (ss == null) {
                ss = DisplaySystem.getDisplaySystem().getRenderer()
                        .createStencilState();
            }
            ss.setEnabled(pass.getstencil_test_enable().getvalue2()
                    .booleanValue());
        }
    }

    public StencilState.StencilOperation evaluateStencilOp(String value) {
        if ("KEEP".equals(value)) {
            return StencilState.StencilOperation.Keep;
        } else if ("ZERO".equals(value)) {
            return StencilState.StencilOperation.Zero;
        } else if ("REPLACE".equals(value)) {
            return StencilState.StencilOperation.Replace;
        } else if ("INCR".equals(value)) {
            return StencilState.StencilOperation.Increment;
        } else if ("DECR".equals(value)) {
            return StencilState.StencilOperation.Decrement;
        } else if ("INVERT".equals(value)) {
            return StencilState.StencilOperation.Invert;
        } else if ("INCR_WRAP".equals(value)) {
            return StencilState.StencilOperation.IncrementWrap;
        } else if ("DECT_WRAP".equals(value)) {
            return StencilState.StencilOperation.DecrementWrap;
        } else {
            return StencilState.StencilOperation.Keep;
        }
    }

    /**
     * processTechniqueCOMMON process a technique of techniqueType2 which are
     * defined to be returned from a profile_COMMON object. This technique
     * contains images, lambert shading, phong shading and blinn shading.
     * 
     * @param technique
     *            the fixed pipeline technique.
     * @param mat
     *            the material to store the technique in.
     * @throws Exception
     *             thrown if there is a problem processing the xml.
     */
    private void processTechniqueCOMMON(techniqueType2 technique,
            ColladaMaterial mat) throws Exception {
        if (technique.haslambert()) {
            processLambert(technique.getlambert(), mat);
        }
        // blinn shading and phong shading are virtually the same, and OpenGL
        // only has a single "smooth" attribute for this.
        if (technique.hasphong()) {
            processPhong(technique.getphong(), mat);
        }
        if (technique.hasextra()) {
            for (int i = 0; i < technique.getextraCount(); i++) {
                ExtraPluginManager.processExtra(mat, technique.getextraAt(i));
            }
        }
    }

    private void processPhong(phongType pt, ColladaMaterial mat)
            throws Exception {
        // obtain the colors for the material
        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
                .createMaterialState();
        // set the ambient color value of the material
        if (pt.hasambient()) {
            ms.setAmbient(getColor(pt.getambient().getcolor()));
        }
        // set the diffuse color value of the material
        if (pt.hasdiffuse()) {
            if (pt.getdiffuse().hascolor()) {
                ms.setDiffuse(getColor(pt.getdiffuse().getcolor()));
            }
            if (pt.getdiffuse().hastexture()) {
                // create a texturestate, and we will need to make use of
                // texcoord to put this texture in the correct "unit"
                for (int i = 0; i < pt.getdiffuse().gettextureCount(); i++) {
                	
                    mat.setState(processTexture(
                            pt.getdiffuse().gettextureAt(i), mat));
                }
            }
        }
        // set the emission color value of the material
        if (pt.hasemission()) {
            ms.setEmissive(getColor(pt.getemission().getcolor()));
        }

        // set the specular color value of the material
        if (pt.hasspecular()) {
          ms.setSpecular(getColor(pt.getspecular().getcolor()));
        }

        // set the shininess value of the material
        if (pt.hasshininess()) {
          ms.setShininess(pt.getshininess().getfloat2().getValue().floatValue());
        }
        
        /*
         * if (pt.hastransparent()) { if (pt.gettransparent().hascolor() &&
         * !pt.gettransparency().getfloat2().getValue() .toString().equals("0")) {
         * BlendState as = DisplaySystem.getDisplaySystem()
         * .getRenderer().createBlendState();
         * as.setSrcFunction(BlendState.SourceFunction.One_MINUS_DST_COLOR);
         * as.setDstFunction(BlendState.DestinationFunction.One); as.setBlendEnabled(true);
         * mat.setState(as); } else if (pt.gettransparent().hastexture()) {
         * BlendState as = DisplaySystem.getDisplaySystem()
         * .getRenderer().createBlendState();
         * as.setSrcFunction(BlendState.SourceFunction.SourceAlpha);
         * as.setDstFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
         * as.setBlendEnabled(true); as.setReference(0.14f);
         * as.setTestEnabled(true); as.setTestFunction(BlendState.TF_GEQUAL);
         * mat.setState(as); } }
         */
        mat.setState(ms);
    }

    private void processLambert(lambertType lt, ColladaMaterial mat)
            throws Exception {
        // lambert shading, create a FLAT shade state and material state
        // with
        // defined colors.
        ShadeState ss = DisplaySystem.getDisplaySystem().getRenderer()
                .createShadeState();
        ss.setShadeMode(ShadeState.ShadeMode.Flat);
        mat.setState(ss);
        // obtain the colors for the material
        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
                .createMaterialState();
        // set the ambient color value of the material
        if (lt.hasambient()) {
            ms.setAmbient(getColor(lt.getambient().getcolor()));
        }
        // set the diffuse color value of the material
        if (lt.hasdiffuse()) {
            if (lt.getdiffuse().hascolor()) {
                ms.setDiffuse(getColor(lt.getdiffuse().getcolor()));
            }
            if (lt.getdiffuse().hastexture()) {
                // create a texturestate, and we will need to make use of
                // texcoord to put this texture in the correct "unit"
            	for (int i = 0; i < lt.getdiffuse().gettextureCount(); i++) {
            		mat.setState(processTexture(
                            lt.getdiffuse().gettextureAt(i), mat));
                }
            }
        }
        // set the emission color value of the material
        if (lt.hasemission()) {
            ms.setEmissive(getColor(lt.getemission().getcolor()));
        }
        mat.setState(ms);
        /*
         * if (lt.hastransparent()) { if (lt.gettransparent().hascolor() &&
         * !lt.gettransparency().getfloat2().getValue() .toString().equals("0")) {
         * BlendState as = DisplaySystem.getDisplaySystem()
         * .getRenderer().createBlendState();
         * as.setSrcFunction(BlendState.SourceFunction.One_MINUS_DST_COLOR);
         * as.setDstFunction(BlendState.DestinationFunction.One); as.setBlendEnabled(true);
         * mat.setState(as); } else if (lt.gettransparent().hastexture()) {
         * BlendState as = DisplaySystem.getDisplaySystem()
         * .getRenderer().createBlendState();
         * as.setSrcFunction(BlendState.SourceFunction.SourceAlpha);
         * as.setDstFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
         * as.setBlendEnabled(true); as.setReference(0.14f);
         * as.setTestEnabled(true); as.setTestFunction(BlendState.TF_GEQUAL);
         * mat.setState(as); } }
         */
        // Ignored: reflective attributes, transparent attributes
    }

    /**
     * processTexture generates a texture state that contains the image and
     * texture coordinate unit information. This texture state is returned to be
     * placed in the Collada material.
     * 
     * @param texture
     *            the texture type to process.
     * @return the generated TextureState that handles this texture tag.
     * @throws Exception
     *             thrown if there is a problem processing the XML.
     */
    public TextureState processTexture(textureType texture,
            ColladaMaterial mat) throws Exception {
        String key = texture.gettexture().toString();
        String channel = texture.gettexcoord().toString();
        if(channel.contains("CHANNEL")) {
        	channel = channel.substring(channel.indexOf("CHANNEL")+7);
        }
        int index = 0;
        try {
        	index = Integer.parseInt(channel) - 1;
        } catch (NumberFormatException e) {
        	
        }
        return processTexture(key, mat, index);
    }
    
    public TextureState processTexture(String key, ColladaMaterial mat,
            int index) throws Exception {
        TextureState ts = (TextureState) mat.getState(RenderState.StateType.Texture);
        if (ts == null) {
            ts = DisplaySystem.getDisplaySystem().getRenderer()
                    .createTextureState();
        }
        String surfaceName = (String) resourceLibrary.get(key);
        if (surfaceName == null) {
            return null;
        }
        String imageName = (String) resourceLibrary.get(surfaceName);
        if (imageName == null) {
            return null;
        }
        String filename = (String) resourceLibrary.get(imageName);
        loadTexture(ts, filename, mat, index);
        return ts;
    }

    /**
     * @param ts
     * @param textureURL
     * @param filename
     */
    private void loadTexture(TextureState ts, String filename,
            ColladaMaterial mat, int index) {
        URL textureURL = ResourceLocatorTool.locateResource(
                ResourceLocatorTool.TYPE_TEXTURE, filename);
        
        if (textureURL != null) {
            Texture t0 = TextureManager.loadTexture(textureURL, mat
                    .getMinFilterConstant(), mat.getMagFilterConstant(),
                    Image.Format.GuessNoCompression, 0, true);

            t0.setWrap(WrapAxis.S, mat.getWrapSConstant());
            t0.setWrap(WrapAxis.T, mat.getWrapTConstant());
            
            ts.setTexture(t0, index);
        } else {
            if (!squelch) {
                logger.warning("Invalid or missing texture: \"" + filename
                        + "\"");
            }
        }
    }

    /**
     * Process Geometry will build a number of Geometry objects attaching them
     * to the supplied parent.
     * 
     * @param geometryLibrary
     *            the geometries to process individually.
     * @throws Exception
     *             thrown if there is a problem processing the XML.
     */
    private void processGeometry(library_geometriesType geometryLibrary)
            throws Exception {
        // go through each geometry one at a time
        for (int i = 0; i < geometryLibrary.getgeometryCount(); i++) {
            geometryType geom = geometryLibrary.getgeometryAt(i);
            if (geom.hasmesh()) {
                for (int j = 0; j < geom.getmeshCount(); j++) {
                    Spatial s = processMesh(geom.getmeshAt(j), geom);
                    put(geom.getid().toString(), s);
                    if (geometryNames == null) {
                        geometryNames = new ArrayList<String>();
                    }
                    geometryNames.add(geom.getid().toString());
                }
            }
            // splines are not currently supported.
            if (geom.hasspline()) {
                if (!squelch) {
                    logger.warning("splines not yet supported.");
                }
            }
        }
    }

    /**
     * processControllerLibrary builds a controller for each controller tag in
     * the file.
     * 
     * @param controllerLibrary
     *            the controller library object to parse.
     * @throws Exception
     *             thrown if there is a problem with the loader.
     */
    private void processControllerLibrary(
            library_controllersType controllerLibrary) throws Exception {
        if (controllerLibrary.hascontroller()) {
            for (int i = 0; i < controllerLibrary.getcontrollerCount(); i++) {
                processController(controllerLibrary.getcontrollerAt(i));
            }
        }
    }

    /**
     * controllers define how one object interacts with another. Typically, this
     * is skinning and morph targets.
     * 
     * @param controller
     *            the controller to process
     */
    private void processController(controllerType controller) throws Exception {
        // skin and morph are mutually exclusive.
        if (controller.hasskin()) {
            // there can only be one skin per controller
            processSkin(controller.getid().toString(), controller.getskin());
        } else if (controller.hasmorph()) {
            // more not currently supported.
        }
    }

    /**
     * processSkin builds a SkinnedMesh object that defines the vertex
     * information of a model and the skeletal system that supports it.
     * 
     * @param skin
     *            the skin to process
     * @throws Exception
     *             thrown if there is a problem parsing the skin.
     */
    private void processSkin(String id, skinType skin) throws Exception {
        // Add this skin's associated mesh to the resource library
        // put(id, skin.getsource().toString());
        SkinNode skinNode = new SkinNode(id + "_node");
        if (skinNodeNames == null) {
            skinNodeNames = new ArrayList<String>();
        }
        skinNodeNames.add(id);
        put(id, skinNode);
        // create a new SkinnedMesh object that will act on a given geometry.
        // SkinnedMesh skinnedMesh = new
        // SkinnedMesh(source.getName()+"skinned",source);
        // the bind shape matrix defines the overall orientation of the mesh
        // before any skinning occurs.
        if (skin.hasbind_shape_matrix()) {
            String key = skin.getsource().toString();
            if (key.startsWith("#")) {
                key = key.substring(1);
            }
            Spatial mesh = (Spatial) resourceLibrary.get(key);
            if (mesh == null) {
                if (!squelch) {
                    logger.warning(key
                            + " mesh does NOT exist in COLLADA file.");
                }
                return;
            }

            Node skins = null;
            if (mesh instanceof TriMesh) {
                skins = new Node(mesh.getName());
                skins.attachChild(mesh);
                resourceLibrary.put(key, skins);
            } else if (mesh instanceof Node) {
                skins = (Node) mesh;
            } else {
                if (!squelch) {
                    logger.warning(key + " mesh is of unsupported skin type: "
                            + mesh);
                }
                return;
            }

            processBindShapeMatrix(skinNode, skin.getbind_shape_matrix());

            skinNode.setSkins(skins);
        }
        // There are a couple types of sources, those setting the joints,
        // the binding table, and the weights. The Collada exporter
        // automatically
        // names them something like skin-joint-*, skin-binding-table-*, etc.
        // we are going to check for the string to determine what it is.
        if (skin.hassource2()) {
            for (int i = 0; i < skin.getsource2Count(); i++) {
                processControllerSource(skin.getsource2At(i));
            }
        }
        // the vertex weights will be assigned to the appropriate bones
        if (skin.hasvertex_weights()) {
            processVertexWeights(skin.getvertex_weights(), skinNode);
        }
        if (skin.hasjoints()) {
            String[] boneIds = null;
            Matrix4f[] bindMatrices = null;
            // define the inverse bind matrix to the joint
            if (skin.getjoints().hasinput()) {
                for (int i = 0; i < skin.getjoints().getinputCount(); i++) {
                    if ("JOINT".equals(skin.getjoints().getinputAt(i)
                            .getsemantic().toString())) {
                        boneIds = (String[]) resourceLibrary.get(skin
                                .getjoints().getinputAt(i).getsource()
                                .toString().substring(1));
                    } else if ("INV_BIND_MATRIX".equals(skin.getjoints()
                            .getinputAt(i).getsemantic().toString())) {
                        bindMatrices = (Matrix4f[]) resourceLibrary.get(skin
                                .getjoints().getinputAt(i).getsource()
                                .toString().substring(1));
                    }
                }
            }
            if (boneIds != null) {
                for (int i = 0; i < boneIds.length; i++) {
                    Bone b = (Bone) resourceLibrary.get(boneIds[i]);
                    b.setBindMatrix(bindMatrices[i].invert());
                }
            }
        }
    }

    /**
     * processVertexWeights defines a list of vertices and weights for a given
     * bone. These bones are defined by <v> as the first element to a group. The
     * bones were prebuilt in the priocessControllerSource method.
     * 
     * @param weights
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void processVertexWeights(vertex_weightsType weights,
            SkinNode skinNode) throws Exception {
        int[] boneCount = new int[weights.getcount().intValue()];
        StringTokenizer st = new StringTokenizer(weights.getvcount().getValue());
        for (int i = 0; i < boneCount.length; i++) {
            boneCount[i] = Integer.parseInt(st.nextToken());
        }
        st = new StringTokenizer(weights.getv().getValue());
        int count = 0;
        String[] boneIdArray = null;
        float[] weightArray = null;
        for (int i = 0; i < weights.getinputCount(); i++) {
            if ("JOINT".equals(weights.getinputAt(i).getsemantic().toString())) {
                String key = weights.getinputAt(i).getsource().toString();
                key = key.substring(1);
                boneIdArray = (String[]) resourceLibrary.get(key);
            } else if ("WEIGHT".equals(weights.getinputAt(i).getsemantic()
                    .toString())) {
                String key = weights.getinputAt(i).getsource().toString();
                key = key.substring(1);
                weightArray = (float[]) resourceLibrary.get(key);
            }
        }
        if (boneIdArray == null || weightArray == null) {
            if (!squelch) {
                logger.warning("Missing resource values for either bone "
                        + "weights or bone vertex ids.");
            }
            return;
        }

        Map<Integer, ArrayList<MeshVertPair>> vertMap = (Map) resourceLibrary
                .get(skinNode.getSkins().getName() + "VertMap");

        while (st.hasMoreTokens()) {
            // Get bone index
            for (int i = 0; i < boneCount[count]; i++) {
                int idIndex = Integer.parseInt(st.nextToken());
                int key = Integer.parseInt(st.nextToken());
                float weight = weightArray[key];
                ArrayList<MeshVertPair> target = vertMap.get(count);
                if (target != null) {
                    for (int j = 0, max = target.size(); j < max; j++) {
                        MeshVertPair bvp = target.get(j);
                        // Bone b =
                        // (Bone)resourceLibrary.get(boneIds[idIndex]);
                        skinNode.addBoneInfluence(bvp.mesh, bvp.index,
                                boneIds[idIndex], weight);
                    }
                }
            }
            count++;
        }
    }

    /**
     * processControllerSource will process the source types that define how a
     * controller is built. This includes support for skin joints, bindings and
     * weights.
     * 
     * @param source
     *            the source to process.
     * @throws Exception
     *             thrown if there is a problem processing the XML.
     */
    private void processControllerSource(sourceType source) throws Exception {
        // check for the joint id list
        String key = source.gettechnique_common().getaccessor().getparam()
                .gettype().getValue();
        if (key.equalsIgnoreCase("IDREF")) {
            if (source.hasIDREF_array()) {
                IDREF_arrayType idrefs = source.getIDREF_array();
                Bone[] bones = new Bone[idrefs.getcount().intValue()];
                boneIds = new String[bones.length];
                StringTokenizer st = new StringTokenizer(idrefs.getValue()
                        .toString());
                for (int i = 0; i < bones.length; i++) {
                    // this skin has a number of bones assigned to it.
                    // Create a Bone for each entry.
                    bones[i] = new Bone(st.nextToken());
                    boneIds[i] = bones[i].getName();
                    put(boneIds[i], bones[i]);
                }
                put(source.getid().toString(), boneIds);
            }
        } else if (key.equalsIgnoreCase("Name")) {
            if (source.hasName_array()) {
                Name_arrayType names = source.getName_array();
                Bone[] bones = new Bone[names.getcount().intValue()];
                boneIds = new String[bones.length];
                StringTokenizer st = new StringTokenizer(names.getValue()
                        .toString());
                for (int i = 0; i < bones.length; i++) {
                    // this skin has a number of bones assigned to it.
                    // Create a Bone for each entry.
                    bones[i] = new Bone(st.nextToken());
                    boneIds[i] = bones[i].getName();
                    put(boneIds[i], bones[i]);
                    put(source.getid().toString(), boneIds);
                }
            }
        } else if (key.equalsIgnoreCase("float4x4")) {
            StringTokenizer st = new StringTokenizer(source.getfloat_array()
                    .getValue().toString());
            int numOfTransforms = st.countTokens() / 16;
            // this creates a 4x4 matrix
            Matrix4f[] tm = new Matrix4f[numOfTransforms];
            for (int i = 0; i < tm.length; i++) {
                tm[i] = new Matrix4f();
                float[] data = new float[16];
                for (int x = 0; x < 16; x++) {
                    data[x] = Float.parseFloat(st.nextToken());
                }
                tm[i].set(data, true); // collada matrices are in row order.
            }
            put(source.getid().toString(), tm);
        } else if (key.equalsIgnoreCase("float")) {
            float_arrayType floats = source.getfloat_array();
            float[] weights = new float[floats.getcount().intValue()];
            StringTokenizer st = new StringTokenizer(floats.getValue()
                    .toString());
            for (int i = 0; i < weights.length; i++) {
                weights[i] = Float.parseFloat(st.nextToken());
            }
            put(source.getid().toString(), weights);
        }
    }

    /**
     * processBindShapeMatrix sets the initial transform of the skinned mesh.
     * The 4x4 matrix is converted to a 3x3 matrix and a vector, then passed to
     * the skinned mesh for use.
     * 
     * @param skin
     *            the skin to apply the bind to.
     * @param matrix
     *            the matrix to parse.
     */
    private void processBindShapeMatrix(SkinNode skin, float4x4 matrix) {
        Matrix4f mat = new Matrix4f();
        StringTokenizer st = new StringTokenizer(matrix.getValue());
        float[] data = new float[16];
        for (int x = 0; x < 16; x++) {
            data[x] = Float.parseFloat(st.nextToken());
        }
        mat.set(data, true); // collada matrices are in row order.
        skin.setBindMatrix(mat);
    }

    /**
     * processBindMaterial
     * 
     * @param material
     * @param spatial
     * @throws Exception
     *             the matrix to parse.
     */
    private void processBindMaterial(bind_materialType material,
            Spatial geomBindTo) throws Exception {
        technique_commonType common = material.gettechnique_common();
        for (int i = 0; i < common.getinstance_materialCount(); i++) {
            processInstanceMaterial(common.getinstance_materialAt(i),
                    geomBindTo);
        }
    }

    /**
     * processMesh will create either lines or a TriMesh. This means that the
     * only supported child elements are: triangles and lines or linestrips.
     * Polygons, trifans and tristrips are ignored.
     * 
     * @param mesh
     *            the mesh to parse.
     * @param geom
     *            the geometryType of the Geometry to build.
     * @return the created Geometry built from the mesh data.
     * @throws Exception
     *             thrown if there is a problem processing the xml.
     */
    private Spatial processMesh(meshType mesh, geometryType geom)
            throws Exception {
        // we need to build all the source data objects.
        for (int i = 0; i < mesh.getsourceCount(); i++) {
            sourceType source = mesh.getsourceAt(i);
            if (source.hasfloat_array()) {
                float_arrayType floatArray = source.getfloat_array();
                StringTokenizer st = new StringTokenizer(floatArray.getValue()
                        .toString());
                // build an array of data to use for the final vector list.
                float[] floats = new float[floatArray.getcount().intValue()];
                for (int j = 0; j < floats.length; j++) {
                    floats[j] = Float.parseFloat(st.nextToken());
                }
                // technique_common should have the accessor type
                if (source.hastechnique_common()) {
                    accessorType accessor = source.gettechnique_common()
                            .getaccessor();
                    // create an array of Vector3fs, using zero for the last
                    // element
                    // if the stride is 2 (the UV map case)
                    Vector3f[] vecs = new Vector3f[accessor.getcount()
                            .intValue()];
                    int stride = accessor.getstride().intValue();
                    if (2 == stride) {
                        for (int k = 0; k < vecs.length; k++) {
                            vecs[k] = new Vector3f(floats[(k * stride)],
                                    floats[(k * stride) + 1], 0.0f);
                        }
                    } else {
                        assert (3 == stride);
                        for (int k = 0; k < vecs.length; k++) {
                            vecs[k] = new Vector3f(floats[(k * stride)],
                                    floats[(k * stride) + 1],
                                    floats[(k * stride) + 2]);
                        }
                    }
                    put(source.getid().toString(), vecs);
                }
            }
        }
        // next we have to define what source defines the vertices positional
        // information
        if (mesh.hasvertices()) {
            if (mesh.getvertices().hasinput()) {
                put(mesh.getvertices().getid().toString(), mesh.getvertices()
                        .getinput().getsource().toString());
            }
        }
        // determine what type of geometry this is, and use the
        // lists to build the object.
        if (mesh.hastriangles()) {
            return processTriMesh(mesh, geom);
        } else if (mesh.haspolygons()) {
            return processPolygonMesh(mesh, geom);
        } else if (mesh.haslines()) {
            return processLines(mesh, geom);
        } else {
            return null;
        }
    }

    /**
     * processTriMesh will process the triangles tag from the mesh section of
     * the COLLADA file. A jME TriMesh is returned that defines the vertices,
     * indices, normals, texture coordinates and colors.
     * 
     * @param mesh
     *            the meshType to process for the trimesh.
     * @param geom
     *            the geometryType of the TriMesh to build.
     * @return the jME trimesh representing the COLLADA mesh.
     * @throws Exception
     *             thrown if there is a problem processing the XML.
     */
    private Spatial processTriMesh(meshType mesh, geometryType geom)
            throws Exception {

        HashMap<Integer, ArrayList<MeshVertPair>> vertMap = new HashMap<Integer, ArrayList<MeshVertPair>>();
        put(geom.getid().toString() + "VertMap", vertMap);

        Node parentNode = new Node(geom.getid().toString());

        for (int triangleIndex = 0; triangleIndex < mesh.gettrianglesCount(); triangleIndex++) {
            trianglesType tri = mesh.gettrianglesAt(triangleIndex);

            TriMesh triMesh = new TriMesh(geom.getid().toString());

            if (tri.hasmaterial()) {
                // first set the appropriate materials to this mesh.
                String matKey = (String) resourceLibrary.get(tri.getmaterial()
                        .toString());
                triMesh.setName(triMesh.getName()+"-"+tri.getmaterial().toString());
                ColladaMaterial cm = (ColladaMaterial) resourceLibrary
                    .get(matKey);

                if (cm != null) {
                	for (RenderState.StateType type : RenderState.StateType.values()) {
                        if (cm.getState(type) != null) {
                            if (type == RenderState.StateType.Blend) {
                                triMesh.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
                            }
                            // clone the state as different mesh's may have
                            // different
                            // attributes
                            try {
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                BinaryExporter.getInstance().save(
                                        cm.getState(type), out);
                                ByteArrayInputStream in = new ByteArrayInputStream(
                                        out.toByteArray());
                                RenderState rs = (RenderState) BinaryImporter
                                        .getInstance().load(in);
                                triMesh.setRenderState(rs);
                            } catch (IOException e) {
                                logger
                                        .throwing(
                                                this.getClass().toString(),
                                                "processTriMesh(meshType mesh, geometryType geom)",
                                                e);
                            }
                        }
                    }
                    ArrayList<Controller> cList = cm.getControllerList();
                    if (cList != null) {
                        for (int c = 0; c < cList.size(); c++) {
                            if (cList.get(c) instanceof TextureKeyframeController) {
                                TextureState ts = (TextureState) triMesh
                                        .getRenderState(RenderState.StateType.Texture);
                                if (ts != null) {
                                    // allow wrapping, as animated textures will
                                    // almost always need it.
                                    ts.getTexture().setWrap(Texture.WrapAxis.S, Texture.WrapMode.Repeat);
                                    ts.getTexture().setWrap(Texture.WrapAxis.T, Texture.WrapMode.Repeat);
                                    ((TextureKeyframeController) cList.get(c))
                                            .setTexture(ts.getTexture());
                                }
                            }
                        }
                    }
                    if (mesh.hasextra()) {
                        for (int i = 0; i < mesh.getextraCount(); i++) {
                            try {
                                ExtraPluginManager.processExtra(triMesh, mesh
                                        .getextraAt(i));
                            } catch (Exception e) {
                                if (!squelch) {
                                    logger
                                            .log(
                                                    Level.INFO,
                                                    "Error processing extra information for mesh",
                                                    e);
                                }
                            }
                        }
                    }
                }

                subMaterialLibrary.put(triMesh, tri.getmaterial().toString());
            }
            // build the index buffer, this is going to be easy as it's only
            // 0...N where N is the number of vertices in the model.
            IntBuffer indexBuffer = BufferUtils.createIntBuffer(tri.getcount()
                    .intValue() * 3);
            for (int i = 0; i < indexBuffer.capacity(); i++) {
                indexBuffer.put(i);
            }
            triMesh.setIndexBuffer(indexBuffer);
            // find the maximum offset to understand the stride
            int maxOffset = -1;
            for (int i = 0; i < tri.getinputCount(); i++) {
                int temp = tri.getinputAt(i).getoffset().intValue();
                if (maxOffset < temp) {
                    maxOffset = temp;
                }
            }
            // next build the other buffers, based on the input semantic
            for (int i = 0; i < tri.getinputCount(); i++) {
                if ("VERTEX".equals(tri.getinputAt(i).getsemantic().toString())) {
                    // build the vertex buffer
                    String key = tri.getinputAt(i).getsource().getValue();
                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }
                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }
                    if (data == null) {
                        logger.warning("Invalid source: " + key);
                        continue;
                    }
                    Vector3f[] v = (Vector3f[]) data;
                    StringTokenizer st = new StringTokenizer(tri.getp()
                            .getValue());
                    int vertCount = tri.getcount().intValue() * 3;
                    FloatBuffer vertBuffer = BufferUtils
                            .createVector3Buffer(vertCount);
                    triMesh.setVertexCount(vertCount);
                    for (int j = 0; j < vertCount; j++) {
                        // need to store the index in p to what j is for later
                        // processing the index to the vert for bones
                        int vertKey = Integer.parseInt(st.nextToken());
                        ArrayList<MeshVertPair> storage = vertMap.get(Integer
                                .valueOf(vertKey));
                        if (storage == null) {
                            storage = new ArrayList<MeshVertPair>();
                            storage.add(new MeshVertPair(triangleIndex, j));
                            vertMap.put(Integer.valueOf(vertKey), storage);
                        } else {
                            storage.add(new MeshVertPair(triangleIndex, j));
                        }
                        BufferUtils.setInBuffer(v[vertKey], vertBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            st.nextToken();
                        }
                    }
                    triMesh.setVertexBuffer(vertBuffer);
                } else if ("NORMAL".equals(tri.getinputAt(i).getsemantic()
                        .toString())) {
                    // build the normal buffer
                    String key = tri.getinputAt(i).getsource().getValue();
                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }
                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }
                    if (data == null) {
                        logger.warning("Invalid source: " + key);
                        continue;
                    }
                    Vector3f[] v = (Vector3f[]) data;
                    StringTokenizer st = new StringTokenizer(tri.getp()
                            .getValue());
                    int normCount = tri.getcount().intValue() * 3;
                    FloatBuffer normBuffer = BufferUtils
                            .createVector3Buffer(normCount);
                    int offset = tri.getinputAt(i).getoffset().intValue();
                    for (int j = 0; j < offset; j++) {
                        st.nextToken();
                    }
                    for (int j = 0; j < normCount; j++) {
                        int index = Integer.parseInt(st.nextToken());
                        if (index < v.length)
                            BufferUtils.setInBuffer(v[index], normBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            if (st.hasMoreTokens()) {
                                st.nextToken();
                            }
                        }
                    }
                    triMesh.setNormalBuffer(normBuffer);
                } else if ("TANGENT".equals(tri.getinputAt(i).getsemantic()
                        .toString())) {
                    // build the tangent buffer
                    String key = tri.getinputAt(i).getsource().getValue();
                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }
                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }
                    if (data == null) {
                        logger.warning("Invalid source: " + key);
                        continue;
                    }
                    Vector3f[] v = (Vector3f[]) data;
                    StringTokenizer st = new StringTokenizer(tri.getp()
                            .getValue());
                    int normCount = tri.getcount().intValue() * 3;
                    FloatBuffer colorBuffer = BufferUtils
                            .createColorBuffer(normCount);
                    int offset = tri.getinputAt(i).getoffset().intValue();
                    for (int j = 0; j < offset; j++) {
                        st.nextToken();
                    }
                    for (int j = 0; j < normCount; j++) {
                        int index = Integer.parseInt(st.nextToken());
                        if (index < v.length) {
                            colorBuffer.put((-v[index].x) / 2.0f + 0.5f);
                            colorBuffer.put((-v[index].y) / 2.0f + 0.5f);
                            colorBuffer.put((-v[index].z) / 2.0f + 0.5f);
                            colorBuffer.put(0.0f);
                        }
                        for (int k = 0; k < maxOffset; k++) {
                            if (st.hasMoreTokens()) {
                                st.nextToken();
                            }
                        }
                    }
                    triMesh.setColorBuffer(colorBuffer);
                } else if ("TEXCOORD".equals(tri.getinputAt(i).getsemantic()
                        .toString())) {
                    // build the texture buffer
                    String key = tri.getinputAt(i).getsource().getValue();
                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }
                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }
                    if (data == null) {
                        logger.warning("Invalid source: " + key);
                        continue;
                    }
                    Vector3f[] v = (Vector3f[]) data;
                    StringTokenizer st = new StringTokenizer(tri.getp()
                            .getValue());
                    int texCount = tri.getcount().intValue() * 3;
                    FloatBuffer texBuffer = BufferUtils
                            .createVector2Buffer(texCount);
                    int offset = tri.getinputAt(i).getoffset().intValue();
                    int set = tri.getinputAt(i).getset().intValue();
                    for (int j = 0; j < offset; j++) {
                        st.nextToken();
                    }
                    // Keep a max to set the wrap mode (if it's 1, clamp, if
                    // it's > 1 || < 0 wrap it)
                    float maxX = -10;
                    float maxY = -10;
                    float minX = 10;
                    float minY = 10;
                    Vector2f tempTexCoord = new Vector2f();
                    for (int j = 0; j < texCount; j++) {
                        int index = Integer.parseInt(st.nextToken());
                        Vector3f value = v[index];
                        if (value.x > maxX) {
                            maxX = value.x;
                        }
                        if (value.x < minX) {
                            minX = value.x;
                        }
                        if (value.y > maxY) {
                            maxY = value.y;
                        }
                        if (value.y < minY) {
                            minY = value.y;
                        }
                        tempTexCoord.set(value.x, value.y);
                        BufferUtils.setInBuffer(tempTexCoord, texBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            if (st.hasMoreTokens()) {
                                st.nextToken();
                            }
                        }
                    }
                    int unit;
                    if (set == 0) {
                        unit = 0;
                    } else {
                        unit = set - 1;
                    }
                    triMesh.setTextureCoords(new TexCoords(texBuffer,2), unit);
                    // Set the wrap mode, check if the mesh has a texture
                    // first, if not check the geometry.
                    // Then, based on the texture coordinates, we may need to
                    // change it from the default.

                    //XXX: not a good way of doing it                    
//                    TextureState ts = (TextureState) triMesh
//                            .getRenderState(RenderState.RS_TEXTURE);
//                    if (ts == null) {
//                        ts = (TextureState) triMesh
//                                .getRenderState(RenderState.RS_TEXTURE);
//                    }
//                    if (ts != null) {
//                        Texture t = ts.getTexture(unit);
//                        if (t != null) {
//                            if (maxX > 1 || minX < 0) {
//                                t.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Repeat);
//                            } else {
//                                t.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Clamp);
//                            }
//
//                            if (maxY > 1 || minY < 0) {
//                                t.setWrap(Texture.WrapAxis.T, Texture.WrapMode.Repeat);
//                            } else {
//                                t.setWrap(Texture.WrapAxis.T, Texture.WrapMode.Clamp);
//                            }
//                        }
//                    }
                } else if ("COLOR".equals(tri.getinputAt(i).getsemantic()
                        .toString())) {
                    // build the texture buffer
                    String key = tri.getinputAt(i).getsource().getValue();
                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }
                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }
                    Vector3f[] v = (Vector3f[]) data;
                    StringTokenizer st = new StringTokenizer(tri.getp()
                            .getValue());
                    int colorCount = tri.getcount().intValue() * 3;
                    FloatBuffer colorBuffer = BufferUtils
                            .createColorBuffer(colorCount);
                    int offset = tri.getinputAt(i).getoffset().intValue();
                    for (int j = 0; j < offset; j++) {
                        st.nextToken();
                    }
                    ColorRGBA tempColor = new ColorRGBA();
                    for (int j = 0; j < colorCount; j++) {
                        int index = Integer.parseInt(st.nextToken());
                        Vector3f value = v[index];
                        tempColor.set(value.x, value.y, value.z, 1);
                        BufferUtils.setInBuffer(tempColor, colorBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            if (st.hasMoreTokens()) {
                                st.nextToken();
                            }
                        }
                    }
                    triMesh.setColorBuffer(colorBuffer);
                }
            }

            triMesh.setModelBound(new BoundingBox());
            triMesh.updateModelBound();

//XXX: not parenting under a node when only one mesh needs to be fixed!! /rherlitz            
//            if (mesh.gettrianglesCount() == 1) {
//                return triMesh;
//            }

            parentNode.attachChild(triMesh);
        }

        return parentNode;
    }

    /**
     * TODO: this implementation is a quick hack to import triangles supplied in
     * polygon form... processPolygonMesh will process the polygons tag from the
     * mesh section of the COLLADA file. A jME TriMesh is returned that defines
     * the vertices, indices, normals, texture coordinates and colors.
     * 
     * @param mesh
     *            the meshType to process for the trimesh.
     * @param geom
     *            the geometryType of the TriMesh to build.
     * @return the jME trimesh representing the COLLADA mesh.
     * @throws Exception
     *             thrown if there is a problem processing the XML.
     */
    private Spatial processPolygonMesh(meshType mesh, geometryType geom)
            throws Exception {
        HashMap<Integer, ArrayList<MeshVertPair>> vertMap = new HashMap<Integer, ArrayList<MeshVertPair>>();
        put(geom.getid().toString() + "VertMap", vertMap);

        Node parentNode = new Node(geom.getid().toString());

        for (int triangleIndex = 0; triangleIndex < mesh.getpolygonsCount(); triangleIndex++) {
            polygonsType poly = mesh.getpolygonsAt(triangleIndex);

            TriMesh triMesh = new TriMesh(geom.getid().toString());

            if (poly.hasmaterial()) {
                // first set the appropriate materials to this mesh.
                String matKey = (String) resourceLibrary.get(poly.getmaterial()
                        .toString());
                ColladaMaterial cm = (ColladaMaterial) resourceLibrary
                        .get(matKey);
                if (cm != null) {
                    for (RenderState.StateType type : RenderState.StateType.values()) {
                        if (cm.getState(type) != null) {
                            if (type == RenderState.StateType.Blend) {
                                triMesh.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
                            }
                            // clone the state as different mesh's may have
                            // different
                            // attributes
                            try {
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                BinaryExporter.getInstance().save(
                                        cm.getState(type), out);
                                ByteArrayInputStream in = new ByteArrayInputStream(
                                        out.toByteArray());
                                RenderState rs = (RenderState) BinaryImporter
                                        .getInstance().load(in);
                                triMesh.setRenderState(rs);
                            } catch (IOException e) {
                                logger
                                        .throwing(
                                                this.getClass().toString(),
                                                "processTriMesh(meshType mesh, geometryType geom)",
                                                e);
                            }
                        }
                    }
                    ArrayList<Controller> cList = cm.getControllerList();
                    if (cList != null) {
                        for (int c = 0; c < cList.size(); c++) {
                            if (cList.get(c) instanceof TextureKeyframeController) {
                                TextureState ts = (TextureState) triMesh
                                        .getRenderState(RenderState.StateType.Texture);
                                if (ts != null) {
                                    // allow wrapping, as animated textures will
                                    // almost always need it.
                                    ts.getTexture().setWrap(Texture.WrapAxis.S, Texture.WrapMode.Repeat);
                                    ts.getTexture().setWrap(Texture.WrapAxis.T, Texture.WrapMode.Repeat);
                                    ((TextureKeyframeController) cList.get(c))
                                            .setTexture(ts.getTexture());
                                }
                            }
                        }
                    }
                    if (mesh.hasextra()) {
                        for (int i = 0; i < mesh.getextraCount(); i++) {
                            try {
                                ExtraPluginManager.processExtra(triMesh, mesh
                                        .getextraAt(i));
                            } catch (Exception e) {
                                if (!squelch) {
                                    logger
                                            .log(
                                                    Level.INFO,
                                                    "Error processing extra information for mesh",
                                                    e);
                                }
                            }
                        }
                    }
                }

                subMaterialLibrary.put(triMesh, poly.getmaterial().toString());
            }

            // build the index buffer, this is going to be easy as it's only
            // 0...N where N is the number of vertices in the model.
            IntBuffer indexBuffer = BufferUtils.createIntBuffer(poly.getcount()
                    .intValue() * 3);
            for (int i = 0; i < indexBuffer.capacity(); i++) {
                indexBuffer.put(i);
            }
            triMesh.setIndexBuffer(indexBuffer);
            // find the maximum offset to understand the stride
            int maxOffset = -1;
            for (int i = 0; i < poly.getinputCount(); i++) {
                int temp = poly.getinputAt(i).getoffset().intValue();
                if (maxOffset < temp) {
                    maxOffset = temp;
                }
            }
            int stride = maxOffset + 1;
            // next build the other buffers, based on the input semantic
            for (int i = 0; i < poly.getinputCount(); i++) {
                if ("VERTEX"
                        .equals(poly.getinputAt(i).getsemantic().toString())) {
                    // build the vertex buffer
                    String key = poly.getinputAt(i).getsource().getValue();
                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }
                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }
                    if (data == null) {
                        logger.warning("Invalid source: " + key);
                        continue;
                    }
                    Vector3f[] v = (Vector3f[]) data;
                    StringTokenizer st = null;
                    int vertCount = poly.getcount().intValue() * stride;
                    FloatBuffer vertBuffer = BufferUtils
                            .createVector3Buffer(vertCount);
                    triMesh.setVertexCount(vertCount);
                    for (int j = 0; j < vertCount; j++) {
                        if (j % stride == 0) {
                            st = new StringTokenizer(poly.getpAt(j / stride)
                                    .getValue());
                        }
                        // need to store the index in p to what j is for later
                        // processing the index to the vert for bones
                        int vertKey = Integer.parseInt(st.nextToken());
                        ArrayList<MeshVertPair> storage = vertMap.get(Integer
                                .valueOf(vertKey));
                        if (storage == null) {
                            storage = new ArrayList<MeshVertPair>();
                            storage.add(new MeshVertPair(triangleIndex, j));
                            vertMap.put(Integer.valueOf(vertKey), storage);
                        } else {
                            storage.add(new MeshVertPair(triangleIndex, j));
                        }
                        BufferUtils.setInBuffer(v[vertKey], vertBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            st.nextToken();
                        }
                    }
                    triMesh.setVertexBuffer(vertBuffer);
                } else if ("NORMAL".equals(poly.getinputAt(i).getsemantic()
                        .toString())) {
                    // build the normal buffer
                    String key = poly.getinputAt(i).getsource().getValue();
                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }
                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }
                    if (data == null) {
                        logger.warning("Invalid source: " + key);
                        continue;
                    }
                    Vector3f[] v = (Vector3f[]) data;
                    StringTokenizer st = null;
                    int normCount = poly.getcount().intValue() * stride;
                    FloatBuffer normBuffer = BufferUtils
                            .createVector3Buffer(normCount);
                    int offset = poly.getinputAt(i).getoffset().intValue();
                    for (int j = 0; j < offset; j++) {
                        if (j % stride == 0) {
                            st = new StringTokenizer(poly.getpAt(j / stride)
                                    .getValue());
                        }
                        st.nextToken();
                    }
                    for (int j = 0; j < normCount; j++) {
                        if (j % stride == 0) {
                            st = new StringTokenizer(poly.getpAt(j / stride)
                                    .getValue());
                        }
                        int index = Integer.parseInt(st.nextToken());
                        if (index < v.length)
                            BufferUtils.setInBuffer(v[index], normBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            if (st.hasMoreTokens()) {
                                st.nextToken();
                            }
                        }
                    }
                    triMesh.setNormalBuffer(normBuffer);
                } else if ("TANGENT".equals(poly.getinputAt(i).getsemantic()
                        .toString())) {
                    // build the tangent buffer
                    String key = poly.getinputAt(i).getsource().getValue();
                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }
                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }
                    if (data == null) {
                        logger.warning("Invalid source: " + key);
                        continue;
                    }
                    Vector3f[] v = (Vector3f[]) data;
                    StringTokenizer st = new StringTokenizer(poly.getp()
                            .getValue());
                    int normCount = poly.getcount().intValue() * 3;
                    FloatBuffer normBuffer = BufferUtils
                            .createVector3Buffer(normCount);
                    int offset = poly.getinputAt(i).getoffset().intValue();
                    for (int j = 0; j < offset; j++) {
                        st.nextToken();
                    }
                    for (int j = 0; j < normCount; j++) {
                        int index = Integer.parseInt(st.nextToken());
                        if (index < v.length)
                            BufferUtils.setInBuffer(v[index], normBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            if (st.hasMoreTokens()) {
                                st.nextToken();
                            }
                        }
                    }
                    triMesh.setTangentBuffer(normBuffer);
                    logger.info("setting tangent buffer: " + normBuffer);
                } else if ("BINORMAL".equals(poly.getinputAt(i).getsemantic()
                        .toString())) {
                    // build the tangent buffer
                    String key = poly.getinputAt(i).getsource().getValue();
                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }
                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }
                    if (data == null) {
                        logger.warning("Invalid source: " + key);
                        continue;
                    }
                    Vector3f[] v = (Vector3f[]) data;
                    StringTokenizer st = new StringTokenizer(poly.getp()
                            .getValue());
                    int normCount = poly.getcount().intValue() * 3;
                    FloatBuffer normBuffer = BufferUtils
                            .createVector3Buffer(normCount);
                    int offset = poly.getinputAt(i).getoffset().intValue();
                    for (int j = 0; j < offset; j++) {
                        st.nextToken();
                    }
                    for (int j = 0; j < normCount; j++) {
                        int index = Integer.parseInt(st.nextToken());
                        if (index < v.length)
                            BufferUtils.setInBuffer(v[index], normBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            if (st.hasMoreTokens()) {
                                st.nextToken();
                            }
                        }
                    }
                    triMesh.setBinormalBuffer(normBuffer);
                } else if ("TEXCOORD".equals(poly.getinputAt(i).getsemantic()
                        .toString())) {
                    // build the texture buffer
                    String key = poly.getinputAt(i).getsource().getValue();
                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }
                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }
                    if (data == null) {
                        logger.warning("Invalid source: " + key);
                        continue;
                    }
                    Vector3f[] v = (Vector3f[]) data;
                    StringTokenizer st = new StringTokenizer(poly.getp()
                            .getValue());
                    int texCount = poly.getcount().intValue() * stride;
                    FloatBuffer texBuffer = BufferUtils
                            .createVector2Buffer(texCount);
                    int offset = poly.getinputAt(i).getoffset().intValue();
                    int set = poly.getinputAt(i).getset().intValue();
                    for (int j = 0; j < offset; j++) {
                        if (j % stride == 0) {
                            st = new StringTokenizer(poly.getpAt(j / stride)
                                    .getValue());
                        }
                        st.nextToken();
                    }
                    // Keep a max to set the wrap mode (if it's 1, clamp, if
                    // it's > 1 wrap it)
                    float maxX = -1, maxY = -1;
                    float minX = 1, minY = 1;
                    Vector2f tempTexCoord = new Vector2f();
                    for (int j = 0; j < texCount; j++) {
                        if (j % stride == 0) {
                            st = new StringTokenizer(poly.getpAt(j / stride)
                                    .getValue());
                        }
                        int index = Integer.parseInt(st.nextToken());
                        Vector3f value = v[index];
                        if (value.x > maxX) {
                            maxX = value.x;
                        }
                        if (value.x < minX) {
                            minX = value.x;
                        }
                        if (value.y > maxY) {
                            maxY = value.y;
                        }
                        if (value.y < minY) {
                            minY = value.y;
                        }
                        tempTexCoord.set(value.x, value.y);
                        BufferUtils.setInBuffer(tempTexCoord, texBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            if (st.hasMoreTokens()) {
                                st.nextToken();
                            }
                        }
                    }
                    int unit;
                    if (set == 0) {
                        unit = 0;
                    } else {
                        unit = set - 1;
                    }
                    triMesh.setTextureCoords(new TexCoords(texBuffer,2), unit);
                    // Set the wrap mode, check if the mesh has a texture
                    // first, if not
                    // check the geometry.
                    // Then, based on the texture coordinates, we may need to
                    // change it from the
                    // default.
                    
                    //XXX: not a good way of doing it
//                    TextureState ts = (TextureState) triMesh
//                            .getRenderState(RenderState.RS_TEXTURE);
//                    if (ts == null) {
//                        ts = (TextureState) triMesh
//                                .getRenderState(RenderState.RS_TEXTURE);
//                    }
//                    if (ts != null) {
//                        Texture t = ts.getTexture(unit);
//                        if (t != null) {
//                            if (maxX > 1 || minX < 0) {
//                                t.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Repeat);
//                            } else {
//                                t.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Clamp);
//                            }
//
//                            if (maxY > 1 || minY < 0) {
//                                t.setWrap(Texture.WrapAxis.T, Texture.WrapMode.Repeat);
//                            } else {
//                                t.setWrap(Texture.WrapAxis.T, Texture.WrapMode.Clamp);
//                            }
//                        }
//                    }
                } else if ("COLOR".equals(poly.getinputAt(i).getsemantic()
                        .toString())) {
                    // build the texture buffer
                    String key = poly.getinputAt(i).getsource().getValue();
                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }
                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }
                    Vector3f[] v = (Vector3f[]) data;
                    StringTokenizer st = new StringTokenizer(poly.getp()
                            .getValue());
                    int colorCount = poly.getcount().intValue() * 3;
                    FloatBuffer colorBuffer = BufferUtils
                            .createColorBuffer(colorCount);
                    int offset = poly.getinputAt(i).getoffset().intValue();
                    for (int j = 0; j < offset; j++) {
                        st.nextToken();
                    }
                    ColorRGBA tempColor = new ColorRGBA();
                    for (int j = 0; j < colorCount; j++) {
                        int index = Integer.parseInt(st.nextToken());
                        Vector3f value = v[index];
                        tempColor.set(value.x, value.y, value.z, 1);
                        BufferUtils.setInBuffer(tempColor, colorBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            if (st.hasMoreTokens()) {
                                st.nextToken();
                            }
                        }
                    }
                    triMesh.setColorBuffer(colorBuffer);
                }
            }

            triMesh.setModelBound(new BoundingBox());
            triMesh.updateModelBound();

            if (mesh.gettrianglesCount() == 1) {
                return triMesh;
            }

            parentNode.attachChild(triMesh);
        }

        return parentNode;
    }

    /**
     * processLines will process the lines tag from the mesh section of the
     * COLLADA file. A jME Line is returned that defines the vertices, normals,
     * texture coordinates and colors.
     * 
     * @param mesh
     *            the meshType to process for the lines.
     * @param geom
     *            the geomType for the lines
     * @return the jME trimesh representing the COLLADA mesh.
     */
    private Spatial processLines(meshType mesh, geometryType geom) {
        if (!squelch) {
            logger.warning("Line are not supported.");
        }
        return null;
    }

    /**
     * the nodes library is a collection of nodes that can be instanced later by
     * the visual scene.
     * 
     * @param type
     *            the nodes library to process.
     * @throws Exception
     *             thrown if there is a problem with the processing.
     */
    private void processNodes(library_nodesType type)
            throws Exception {
        Node tempParent = new Node("temp_parent");
        for (int i = 0; i < type.getnodeCount(); i++) {
            processNode(type.getnodeAt(i), tempParent);
        }
        // should all be in the resource library now.
    }

    /**
     * The library of visual scenes defines how the loaded geometry is stored in
     * the scene graph, including scaling, translation, rotation, etc.
     * 
     * @param libScene
     *            the library of scenes
     * @throws Exception
     *             thrown if there is a problem processing the XML.
     */
    private void processVisualSceneLibrary(library_visual_scenesType libScene)
            throws Exception {
        for (int i = 0; i < libScene.getvisual_sceneCount(); i++) {
            Node scene = new Node(libScene.getvisual_sceneAt(i).getid()
                    .toString());
            put(scene.getName(), scene);
            processVisualScene(libScene.getvisual_sceneAt(i), scene);
        }
    }

    /**
     * the visual scene will contain any number of nodes that define references
     * to geometry. These are then placed into the scene as needed.
     * 
     * @param scene
     *            the scene to process.
     * @param node
     *            the jME node to attach this scene to.
     * @throws Exception
     *             thrown if there is a problem with the processing.
     */
    private void processVisualScene(visual_sceneType scene, Node node)
            throws Exception {
        for (int i = 0; i < scene.getnodeCount(); i++) {
            processNode(scene.getnodeAt(i), node);
        }
        for (int i = 0; i < node.getQuantity(); i++) {
            Spatial s = node.getChild(i);
            if (s instanceof Bone) {
                s.updateGeometricState(0, true);
                s.removeFromParent();
                node.attachChild(s);
            }
        }
    }

    /**
     * a node tag
     * 
     * @param xmlNode
     * @param parent
     * @throws Exception
     */
    private void processNode(nodeType2 xmlNode, Node parent) throws Exception {
        String childName = null;
        if (xmlNode.hasid())
            childName = xmlNode.getid().toString();
        else if (xmlNode.hassid())
            childName = xmlNode.getsid().toString();
        else if (xmlNode.hasname())
            childName = xmlNode.getname().toString();
        Node child = null;
        if (xmlNode.hastype() && "JOINT".equals(xmlNode.gettype().toString())
                && (xmlNode.hassid() || xmlNode.hasid())) {
            String key = (xmlNode.hassid() ? xmlNode.getsid() : xmlNode.getid())
                    .toString();
            child = (Bone) resourceLibrary.get(key);
            if (child == null) {
                child = new Bone(key);
                put(key, child);
                if (!squelch) {
                    logger.warning("Bone " + key
                            + " is not attached to any vertices.");
                }
            }
            if (!(parent instanceof Bone)) {
                if (skeletonNames == null) {
                    skeletonNames = new ArrayList<String>();
                }
                skeletonNames.add(key);
            }
        }
        if (xmlNode.hasextra()) {
            for (int i = 0; i < xmlNode.getextraCount(); i++) {
                try {
                    Object o = ExtraPluginManager.processExtra(childName,
                            xmlNode.getextraAt(i));
                    if (o instanceof Node) {
                        child = (Node) o;
                    }
                } catch (Exception e) {
                    if (!squelch) {
                        logger.log(Level.WARNING,
                                "Error processing extra information", e);
                    }
                }
            }
        }
        if (child == null) {
            child = new Node(childName);
        }
        parent.attachChild(child);
        put(childName, child);
        if (xmlNode.hasinstance_camera()) {
            for (int i = 0; i < xmlNode.getinstance_cameraCount(); i++) {
                processInstanceCamera(xmlNode.getinstance_cameraAt(i), child);
            }
        }
        // this node has a skeleton and skin
        if (xmlNode.hasinstance_controller()) {
            for (int i = 0; i < xmlNode.getinstance_controllerCount(); i++) {
                processInstanceController(xmlNode.getinstance_controllerAt(i),
                        child);
            }
        }
        if (xmlNode.hasinstance_geometry()) {
            for (int i = 0; i < xmlNode.getinstance_geometryCount(); i++) {
                processInstanceGeom(xmlNode.getinstance_geometryAt(i), child);
            }
        }
        if (xmlNode.hasinstance_node()) {
            for (int i = 0; i < xmlNode.getinstance_nodeCount(); i++) {
                processInstanceNode(xmlNode.getinstance_nodeAt(i), child);
            }
        }
        if (xmlNode.hasinstance_light()) {
            for (int i = 0; i < xmlNode.getinstance_lightCount(); i++) {
                processInstanceLight(xmlNode.getinstance_lightAt(i), child);
            }
        }
        // parse translation
        if (xmlNode.hastranslate()) {
            Vector3f translate = new Vector3f();
            StringTokenizer st = new StringTokenizer(xmlNode.gettranslate()
                    .getValue().toString());
            translate.x = Float.parseFloat(st.nextToken());
            translate.y = Float.parseFloat(st.nextToken());
            translate.z = Float.parseFloat(st.nextToken());
            child.setLocalTranslation(translate);
        }
        if (xmlNode.hasrotate()) {
            Quaternion rotation = null;
            for (int i = 0; i < xmlNode.getrotateCount(); i++) {
                Quaternion temp = new Quaternion();
                Vector3f axis = new Vector3f();
                StringTokenizer st = new StringTokenizer(xmlNode.getrotateAt(i)
                        .getValue().toString());
                axis.x = Float.parseFloat(st.nextToken());
                axis.y = Float.parseFloat(st.nextToken());
                axis.z = Float.parseFloat(st.nextToken());
                axis.normalizeLocal();
                float angle = Float.parseFloat(st.nextToken());
                angle *= FastMath.DEG_TO_RAD;
                temp.fromAngleNormalAxis(angle, axis);
                if (rotation == null) {
                    rotation = new Quaternion();
                    rotation.set(temp);
                } else {
                    rotation.multLocal(temp);
                }
            }
            child.setLocalRotation(rotation);
        }
        if (xmlNode.hasmatrix()) {
            Matrix4f tm = new Matrix4f();
            StringTokenizer st = new StringTokenizer(xmlNode.getmatrix()
                    .getValue().toString());
            float[] data = new float[16];
            for (int x = 0; x < 16; x++) {
                data[x] = Float.parseFloat(st.nextToken());
            }
            tm.set(data, true); // collada matrices are in row order.
            child.setLocalTranslation(tm.toTranslationVector());
            // find scale
            Vector3f vCol1 = new Vector3f(tm.m00, tm.m10, tm.m20);
            Vector3f vCol2 = new Vector3f(tm.m01, tm.m11, tm.m21);
            Vector3f vCol3 = new Vector3f(tm.m02, tm.m12, tm.m22);
            float scaleX = vCol1.length();
            float scaleY = vCol2.length();
            float scaleZ = vCol3.length();
            child.setLocalScale(new Vector3f(scaleX, scaleY, scaleZ));
            Matrix3f rm = new Matrix3f();
            rm.m00 = tm.m00 / scaleX;
            rm.m10 = tm.m10 / scaleX;
            rm.m20 = tm.m20 / scaleX;
           
            rm.m01 = tm.m01 / scaleY;
            rm.m11 = tm.m11 / scaleY;
            rm.m21 = tm.m21 / scaleY;
           
            rm.m02 = tm.m02 / scaleZ;
            rm.m12 = tm.m12 / scaleZ;
            rm.m22 = tm.m22 / scaleZ;
            Quaternion q = new Quaternion().fromRotationMatrix(rm);
            //Quaternion q = tm.toRotationQuat();
            //float scale = FastMath.sqrt(q.norm());
            //System.out.println(scale);
            //q.normalize();
            child.setLocalRotation(q);
        }
        if (xmlNode.hasscale()) {
            Vector3f scale = new Vector3f();
            StringTokenizer st = new StringTokenizer(xmlNode.getscale()
                    .getValue().toString());
            scale.x = Float.parseFloat(st.nextToken());
            scale.y = Float.parseFloat(st.nextToken());
            scale.z = Float.parseFloat(st.nextToken());
            child.setLocalScale(scale);
        }
        // parse subnodes
        if (xmlNode.hasnode()) {
            for (int i = 0; i < xmlNode.getnodeCount(); i++) {
                processNode(xmlNode.getnodeAt(i), child);
            }
        }
    }

    /**
     * processInstanceCamera
     * 
     * @param camera
     * @param node
     * @throws Exception
     */
    private void processInstanceCamera(InstanceWithExtra camera, Node node)
            throws Exception {
        String key = camera.geturl().toString();
        if (key.startsWith("#")) {
            key = key.substring(1);
        }
        CameraNode cn = (CameraNode) resourceLibrary.get(key);
        if (cn != null) {
            node.attachChild(cn);
        }
    }

    /**
     * processInstanceLight
     * 
     * @param light
     * @param node
     * @throws Exception
     */
    private void processInstanceLight(InstanceWithExtra light, Node node)
            throws Exception {
        String key = light.geturl().toString();
        if (key.startsWith("#")) {
            key = key.substring(1);
        }
        LightNode ln = (LightNode) resourceLibrary.get(key);
        if (ln != null) {
            node.attachChild(ln);
        }
    }

    /**
     * processInstanceController
     * 
     * @param controller
     * @param node
     * @throws Exception
     */
    private void processInstanceController(instance_controllerType controller,
            Node node) throws Exception {
        String key = controller.geturl().toString();
        if (key.startsWith("#")) {
            key = key.substring(1);
        }
        SkinNode sNode = (SkinNode) resourceLibrary.get(key);
        if (sNode != null) {
            node.attachChild(sNode);
        } else {
            if (!squelch) {
                logger.warning("Instance "
                        + controller.geturl().toString().substring(1)
                        + " does not exist.");
            }
        }
        if (controller.hasskeleton()) {
            if (controller.getskeletonCount() > 1) {
                if (!squelch) {
                    logger.warning("Controller has more than one skeleton.");
                }
            }
            String url = controller.getskeleton().getValue();
            if (url.startsWith("#")) {
                url = url.substring(1);
            }
            Bone b = (Bone) resourceLibrary.get(url);
            if (b != null) {
                sNode.setSkeleton(b);
            }
        }
        if (controller.hasbind_material()) {
            processBindMaterial(controller.getbind_material(), sNode.getSkins());
        }
    }

    /**
     * processInstanceNode
     * 
     * @param instance
     * @param parent
     * @throws Exception
     */
    private void processInstanceNode(InstanceWithExtra instance, Node parent)
            throws Exception {
        String key = instance.geturl().toString();
        if (key.startsWith("#")) {
            key = key.substring(1);
        }
        Spatial spatial = (Spatial) resourceLibrary.get(key);
        if (spatial != null) {
            if (spatial instanceof Node) {
                spatial = new SharedNode(key, (Node) spatial);
            }

            parent.attachChild(spatial);
        }
    }

    /**
     * processInstanceGeom
     * 
     * @param geometry
     * @param node
     * @throws Exception
     */
    private void processInstanceGeom(instance_geometryType geometry, Node node)
            throws Exception {
        String key = geometry.geturl().toString();
        if (key.startsWith("#")) {
            key = key.substring(1);
        }
        Spatial spatial = (Spatial) resourceLibrary.get(key);
        if (spatial != null) {
            if (spatial instanceof TriMesh) {
                spatial = new SharedMesh(key, (TriMesh) spatial);
            } else if (spatial instanceof Node) {
                spatial = new SharedNode(key, (Node) spatial);
            }

            node.attachChild(spatial);
            if (geometry.hasbind_material()) {
                processBindMaterial(geometry.getbind_material(), spatial);
            }
        }
    }

    /**
     * processInstanceMaterial
     * 
     * @param material
     * @param node
     * @throws Exception
     */
    private void processInstanceMaterial(instance_materialType material,
            Spatial geomBindTo) throws Exception {
        String key = material.gettarget().toString();
        if (key.startsWith("#")) {
            key = key.substring(1);
        }
        ColladaMaterial cm = (ColladaMaterial) resourceLibrary
                .get(resourceLibrary.get(key));

        Spatial target = geomBindTo;

        String symbol = material.getsymbol().toString();

        if (target instanceof Node) {
            Node targetNode = (Node) target;
            for (int i = 0; i < targetNode.getQuantity(); ++i) {
                Spatial child = targetNode.getChild(i);
                if (child instanceof TriMesh
                        && symbol.equals(subMaterialLibrary.get(child))) {
                    target = child;
                    break;
                } else if (child instanceof SharedMesh
                        && symbol.equals(subMaterialLibrary
                                .get(((SharedMesh) child).getTarget()))) {
                    target = child;
                    break;
                }
            }
        }

        if (cm != null) {
            for (RenderState.StateType type : RenderState.StateType.values()) {
                if (cm.getState(type) != null) {
                    if (type == RenderState.StateType.Blend) {
                        target.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
                    }
                    // clone the state as different mesh's may have
                    // different
                    // attributes
                    try {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        BinaryExporter.getInstance().save(cm.getState(type), out);
                        ByteArrayInputStream in = new ByteArrayInputStream(out
                                .toByteArray());
                        RenderState rs = (RenderState) BinaryImporter
                                .getInstance().load(in);
                        target.setRenderState(rs);
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Error cloning state", e);
                    }
                }
            }
        }
    }

    /**
     * getColor uses a string tokenizer to parse the value of a colorType into a
     * ColorRGBA type used internally by jME.
     * 
     * @param color
     *            the colorType to parse (RGBA format).
     * @return the ColorRGBA object to be used by jME.
     */
    private ColorRGBA getColor(colorType color) {
        ColorRGBA out = new ColorRGBA();
        StringTokenizer st = new StringTokenizer(color.getValue().toString());
        out.r = Float.parseFloat(st.nextToken());
        out.g = Float.parseFloat(st.nextToken());
        out.b = Float.parseFloat(st.nextToken());
        out.a = Float.parseFloat(st.nextToken());
        return out;
    }

    /**
     * MeshVertPair simply contain a mesh index and a vertex index. This defines
     * where a specific vertex may be found.
     */
    private class MeshVertPair {
        public int mesh;
        public int index;

        /**
         * MeshVertPair
         * 
         * @param mesh
         * @param index
         */
        public MeshVertPair(int mesh, int index) {
            this.mesh = mesh;
            this.index = index;
        }
    }

    /**
     * squelchErrors sets if the ColladaImporter should spit out errors or not
     * 
     * @param b
     */
    public static void squelchErrors(boolean b) {
        squelch = b;
    }

    public static ColladaImporter getInstance() {
        return instance;
    }
}
