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

package com.jme.animation;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.ConnectionPoint;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.export.StringStringMap;
import com.jme.util.geom.VertMap;

/**
 * SkinNode defines a scene node that contains skinned mesh data. A skinned mesh
 * is defined by a Geometry object representing the "skin" that is attached to a
 * skeleton (or a tree of Bones). The orientation, translation of these bones
 * define the position of the skin vertices. These bones can then be driven by
 * an animation system to provide the animation of the skin. SkinNode defines
 * for each vertex of the skin the bone that affects it and the weight
 * (BoneInfluence) of that affect. This allows multiple bones to share a single
 * vertex (although the total weight must add up to 1).
 * <P>
 * One of the removeSkinGeometry methods should be used to remove skins.
 * Simply detaching a skin from the scene may result in a memory leak, since
 * the bone influences for that skin will be retained.
 * </P> <P>
 * The 'skins' Node of a SkinNode should only parent Geometries.
 * Do not parent it to anything else.
 * </P>
 *
 * @author Joshua Slack
 * @author Mark Powell
 */
public class SkinNode extends Node implements Savable, BoneChangeListener {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(
            SkinNode.class.getName());

    protected Vector3f vertex = new Vector3f();
    protected Vector3f normal = new Vector3f();

    protected boolean needsRefresh = true;

    protected Node skins = null;

    protected Bone skeleton = null;
    protected ArrayList<BoneInfluence>[][] cache = null;

    protected ArrayList<ConnectionPoint> connectionPoints;

    protected transient boolean newSkeletonAssigned = false;
    protected transient Matrix4f bindMatrix;
    // Is this bindMatrix EVER useful?  It has never been persisted or
    // restored by this class.  ??

    private final Vector3f tmpTranslation = new Vector3f();
    private final Quaternion tmpRotation = new Quaternion();
    private final Vector3f tmpScale = new Vector3f();

    private boolean externalControl = false;

    /**
     * Empty Constructor to be used internally only.
     */
    public SkinNode() {
        setLastFrustumIntersection(Camera.FrustumIntersect.Inside);
    }

    /**
     * Constructor creates a new SkinNode object with the supplied name.
     *
     * @param name
     *            the name of this SkinNode
     */
    public SkinNode(String name) {
        super(name);
    }

    /**
     * getSkin returns the skins (Geometry objects) that the SkinNode is currently controlling.
     *
     * @return the skins contained in this SkinNode
     */
    public Node getSkins() {
        return skins;
    }

    /**
     * Returns a typed skin Geometry.
     *
     * @throws RuntimeException or Error subclass if 'skins' is null, if child
     *                          not present, or is not a Geometry.
     */
    public Geometry getSkin(int i) {
        return (Geometry) skins.getChild(i);
    }

    /**
     * setSkin sets the skin that the SkinNode will affect.
     *
     * @param skins
     *            the skins that this SkinNode will affect.
     */
    public void setSkins(Node skins) {
        this.skins = skins;
        validateSkins();
        attachChild(skins);
    }

    /**
     * Validates that the 'skins' node is either null or contains only
     * Geometry children.
     *
     * @throws IllegalStateException if 'skins' contains a non-Geometry child.
     */
    protected void validateSkins() {
        if (skins == null || skins.getQuantity() < 1) return;
        for (Spatial child : skins.getChildren())
            if (!(child instanceof Geometry))
                throw new IllegalStateException(
                        "'skins' contains non-Geometry child: "
                        + child.getName()
                        + " of type " + child.getClass().getName());
    }

    /**
     * addSkins sets the skin that the SkinNode will affect.
     *
     * @param skin
     *            an additional skin that this SkinNode will affect.
     */
    public void addSkin(Geometry skin) {
        if (skins == null) {
            skins = new Node("Skins");
            attachChild(skins);
        }
        this.skins.attachChild(skin);
    }

    /**
     * addBoneInfluence defines how a vertex will be affected by a bone. This is
     * given with four values, the geometry child the vertex is found, the index
     * to the vertex in the geometry, the index of the bone that has been or
     * will be set via setBones or addBone and the weight that this indexed bone
     * affects the vertex.
     *
     * @param geomIndex
     *            the geometry child that contains the vertex to be affected.
     * @param vert
     *            the index to the vertex.
     * @param bone
     *            the bone that affects the vertex.
     * @param weight
     *            the weight that the bone will affect the vertex.
     */
    public void addBoneInfluence(int geomIndex, int vert, Bone bone,
            float weight) {
    	if (weight == 0) return;
        if (cache == null)
            recreateCache();

        ArrayList<BoneInfluence> infs = cache[geomIndex][vert];
        if (infs == null) {
            infs = new ArrayList<BoneInfluence>(1);
            cache[geomIndex][vert] = infs;
        }
        BoneInfluence i = new BoneInfluence(bone, weight);
        i.boneId = bone.getName();
        if (!infs.contains(i))
        	infs.add(i);
    }

    public void setAnimation(BoneAnimation anim) {
        if(skeleton != null && skeleton.getAnimationController() != null) {
        	skeleton.getAnimationController().setActiveAnimation(anim);
        }
    }

    public void setAnimation(int index) {
        if(skeleton != null && skeleton.getAnimationController() != null) {
            skeleton.getAnimationController().setActiveAnimation(index);
        }
    }

    public void setAnimation(String name) {
        if(skeleton != null && skeleton.getAnimationController() != null) {
            skeleton.getAnimationController().setActiveAnimation(name);
        }
    }

    public String getAnimationString() {
    	if (skeleton != null && skeleton.getAnimationController() != null
        	&& skeleton.getAnimationController().getActiveAnimation() != null) {
        	return skeleton.getAnimationController().getActiveAnimation().getName();
    	}
    	return null;

   	}

    public void addBoneInfluence(
            int geomIndex, int vert, String boneId, float weight) {
    	if (weight == 0) return;
        if (cache == null) recreateCache();

        ArrayList<BoneInfluence> infs = cache[geomIndex][vert];
        if (infs == null) {
            infs = new ArrayList<BoneInfluence>(1);
            cache[geomIndex][vert] = infs;
        }
        BoneInfluence i = new BoneInfluence(null, weight);
        i.boneId = boneId;
        if (!infs.contains(i)) infs.add(i);
    }

    public ConnectionPoint addConnectionPoint(String name, Bone b) {
        ConnectionPoint cp = new ConnectionPoint(name, b);
        if(connectionPoints == null) {
            connectionPoints = new ArrayList<ConnectionPoint>();
        }
        connectionPoints.add(cp);
        this.attachChild(cp);
        return cp;
    }

    public ArrayList<ConnectionPoint> getConnectionPoints() {
        return connectionPoints;
    }

    /**
     * recreateCache initializes the cache of BoneInfluences for use by the skin
     * node.
     */
    @SuppressWarnings("unchecked")
    public void recreateCache() {
        validateSkins();
        cache = new ArrayList[skins.getQuantity()][];
        for (int x = 0; x < cache.length; x++) {
        	cache[x] = new ArrayList[getSkin(x).getVertexCount()];
        }
    }

    /**
     * updateGeometricState overrides Spatials updateGeometric state to update
     * the assigned skeleton bone influences, if changed.
     *
     * @param time
     *            the time that has passed between calls.
     * @param initiator
     *            true if this is the top level being called.
     */
    public void updateGeometricState(float time, boolean initiator) {
        if (newSkeletonAssigned) {
            assignSkeletonBoneInfluences();
        }

        if (!externalControl && skins != null && needsRefresh) {
        	updateSkin();
        	skins.updateModelBound();

        	needsRefresh = false;
        }

        super.updateGeometricState(time, initiator);
    }

    /**
     * normalizeWeights insures that all vertex BoneInfluences equal 1. The total
     * BoneInfluence on a single vertex should be 1 otherwise the position of the
     * vertex will be multiplied.
     */
    public void normalizeWeights() {
        if (cache == null)
            return;
        for (int geomIndex = cache.length; --geomIndex >= 0;) {
            normalizeWeights(geomIndex);
        }
    }

    public int getInfluenceCount(int geomIndex) {
        if (cache == null)
            return 0;
        int rVal = 0;
        for (int vert = cache[geomIndex].length; --vert >= 0;) {
            ArrayList<BoneInfluence> infs = cache[geomIndex][vert];
            if (infs != null)
                rVal+=infs.size();
        }
        return rVal;
    }

    public void normalizeWeights(int geomIndex) {
        if (cache == null)
            return;
        for (int vert = cache[geomIndex].length; --vert >= 0;) {
            ArrayList<BoneInfluence> infs = cache[geomIndex][vert];
            if (infs == null)
                continue;
            float total = 0;
            for (int x = infs.size() - 1; x >= 0; --x) {
                BoneInfluence influence = infs.get(x);
                total += influence.weight;
            }
            for (int x = infs.size() - 1; x >= 0; --x) {
                BoneInfluence influence = infs.get(x);
                influence.weight /= total;
            }
        }
    }

    public void setSkeleton(Bone b) {
        skeleton = b;
        if (skeleton != null) {
            skeleton.removeBoneListener(this);
            skeleton.addBoneListener(this);
        }
        newSkeletonAssigned = true;
    }

    public Bone getSkeleton() {
        return skeleton;
    }

    /**
     * Assigns Bone instance references to BoneInfluences, by looking up
     * the boneId names.
     */
    public void assignSkeletonBoneInfluences() {
        if (skeleton == null) return;
        if (cache == null) return;
        validateSkins();
        for (int index = cache.length - 1; index >= 0; --index)
            assignSkeletonBoneInfluences(index);
        newSkeletonAssigned = false;
    }

    /**
     Assigns Bone instance references for one specific skin Geometry.
     *
     * @see #assignSkeletonBoneInfluences()
     */
    public void assignSkeletonBoneInfluences(Geometry skinGeo) {
        if (skeleton == null)
            throw new IllegalStateException("No skeleton assigned yet");
        if (cache == null)
            throw new IllegalStateException("No skins initialized yet");

        validateSkins();
        for (int index = skins.getQuantity() - 1; index >= 0; --index)
            if (getSkin(index) == skinGeo) {
                assignSkeletonBoneInfluences(index);
                return;
            }
        throw new IllegalArgumentException(
                "Geometry is not one of our skins: " + skinGeo.getName());
    }

    protected void assignSkeletonBoneInfluences(int i) {
        for (ArrayList<BoneInfluence> biList : cache[i])
            if (biList != null)
                for (int j = 0; j < biList.size(); j++)
                    biList.get(j).assignBone(skeleton);
    }

    /**
     * regenInfluenceOffsets calculate the offset of a particular vertex from a
     * bone. This allows the bone's rotation to position the vertex in world
     * space. This nees to be called only be called a single time during
     * initialization (or when a Geometry's local vertex locations change).
     */
    public void regenInfluenceOffsets() {
        if (cache == null) return;

        validateSkins();
        for (int index = cache.length - 1; index >= 0; --index)
            regenInfluenceOffsets(index);
    }

    /**
     * Regenerates the offsets for one specific skin Geometry.
     *
     * @see #regenInfluenceOffsets()
     */
    public void regenInfluenceOffsets(Geometry skinGeo) {
        if (cache == null)
            throw new IllegalStateException("No skins initialized yet");

        validateSkins();
        for (int index = skins.getQuantity() - 1; index >= 0; --index)
            if (getSkin(index) == skinGeo) {
                regenInfluenceOffsets(index);
                return;
            }
        throw new IllegalArgumentException(
                "Geometry is not one of our skins: " + skinGeo.getName());
    }

    protected void regenInfluenceOffsets(int index) {
        FloatBuffer verts, norms;
        Vector3f vertex = new Vector3f();
        Vector3f normal = new Vector3f();

        Geometry geom = getSkin(index);
        verts = geom.getVertexBuffer();
        if (verts == null) {
            logger.log(Level.FINE,
                    "Skipping skin ''{0}'' because verts uninitialized",
                    geom.getName());
            return;
        }
        norms = geom.getNormalBuffer();
        verts.clear();
        norms.clear();
        for (ArrayList<BoneInfluence> infs : cache[index]) {
            vertex.set(verts.get(), verts.get(), verts.get());
            normal.set(norms.get(), norms.get(), norms.get());

            if (infs == null) continue;

            if (bindMatrix != null) {
                bindMatrix.mult(vertex, vertex);
                bindMatrix.rotateVect(normal);
            }

            for (int x = infs.size() - 1; x >= 0; --x) {
                BoneInfluence infl = infs.get(x);
                if (infl.bone == null) continue;
                infl.vOffset = new Vector3f(vertex);
                infl.bone.bindMatrix.inverseTranslateVect(infl.vOffset);
                infl.bone.bindMatrix.inverseRotateVect(infl.vOffset);

                infl.nOffset = new Vector3f(normal);
                infl.bone.bindMatrix.inverseRotateVect(infl.nOffset);
            }
        }
    }

    /**
     * updateSkin positions the vertices of the skin based on the bones and the
     * BoneInfluences those bones have on the vertices. Each vertex is placed
     * into world space for rendering.
     */
    public synchronized void updateSkin() {
        if (cache == null || skins == null)
            return;

        if (skeleton != null) {
        	if (skeleton.getParent() != null) {
        		tmpTranslation.set(skeleton.getParent().getWorldTranslation());
        		tmpRotation.set(skeleton.getParent().getWorldRotation());
        		tmpScale.set(skeleton.getParent().getWorldScale());

        		skeleton.getParent().getWorldTranslation().set(0,0,0);
        		skeleton.getParent().getWorldRotation().set(0,0,0,1);
        		skeleton.getParent().getWorldScale().set(1,1,1);
        		skeleton.updateWorldVectors(true);
        	}

        	skeleton.update();
        }

        FloatBuffer verts, norms;

        // Note that it would be simpler to .clear() then .flip() the
        // Buffers, but since we want to support the potential case of
        // leaving vertexes at the end of the buffer unmodified, we must
        // just set the write buffer and leave the limit setting alone.
        for (int index = cache.length - 1; index >= 0; --index) {
            Geometry geom = getSkin(index);
            verts = geom.getVertexBuffer();
            if (verts == null) {
                logger.log(Level.FINE,
                        "Skipping skin ''{0}'' because verts uninitialized",
                        geom.getName());
                continue;
            }
            verts.rewind();
            norms = geom.getNormalBuffer();
            norms.rewind();
            geom.setHasDirtyVertices(true);
            if (cache[index].length * 3 > verts.limit())
                throw new IllegalStateException( "Skin " + getName() + ':'
                        + geom.getName() + " has more influences than "
                        + " vertexes.  " + cache[index].length + " vs. "
                        + verts.limit() + "/3");
            if (cache[index].length * 3 > norms.limit())
                throw new IllegalStateException( "Skin " + getName() + ':'
                        + geom.getName() + " has more influences than "
                        + " normals.  " + cache[index].length + " vs. "
                        + norms.limit() + "/3");
            if (cache[index].length * 3 < verts.limit())
                logger.log(Level.WARNING, "Skin ''{0}:{1}'' has fewer "
                        + "influences than vertexes.  {2} vs {3}/3",
                        new Object[] { getName(), geom.getName(),
                        cache[index].length, verts.limit() });
            if (cache[index].length * 3 < norms.limit())
                logger.log(Level.WARNING, "Skin ''{0}:{1}'' has fewer "
                        + "influences than normals.  {2} vs {3}/3",
                        new Object[] { getName(), geom.getName(),
                        cache[index].length, norms.limit() });

            for (int vert = 0, max = cache[index].length; vert < max; vert++) {
                ArrayList<BoneInfluence> infs = cache[index][vert];
                if (infs == null || infs.size() < 1) {
                    verts.position(verts.position() + 3);
                    norms.position(norms.position() + 3);
                    continue;
                }
                vertex.zero();
                normal.zero();

                for (int x = infs.size() - 1; x >= 0; --x) {
                    BoneInfluence inf = infs.get(x);
                    if (inf.bone != null)
                        inf.bone.applyBone(inf, vertex, normal);
                }

                verts.put(vertex.x).put(vertex.y).put(vertex.z);
                norms.put(normal.x).put(normal.y).put(normal.z);
            }
            verts.rewind();
            norms.rewind();
        }

        if (skeleton != null && skeleton.getParent() != null) {
    		skeleton.getParent().getWorldTranslation().set(tmpTranslation);
    		skeleton.getParent().getWorldRotation().set(tmpRotation);
    		skeleton.getParent().getWorldScale().set(tmpScale);
    		skeleton.updateWorldVectors(true);
        }
    }

    public ArrayList<BoneInfluence>[][] getCache() {
        return cache;
    }

    public void setCache(ArrayList<BoneInfluence>[][] cache) {
        this.cache = cache;
    }

    public void setBindMatrix(Matrix4f mat) {
        bindMatrix = (mat != null && mat.isIdentity()) ? null : mat;
    }

    public void childChange(Geometry geometry, int index1, int index2) {
        if(skins != null && skins.hasChild(geometry)) {
            ArrayList<BoneInfluence>[] temp1 = cache[index1];
            ArrayList<BoneInfluence>[] temp2 = cache[index2];
            cache[index1] = temp2;
            cache[index2] = temp1;
        }
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule cap = e.getCapsule(this);

        cap.write(skins, "skins", null);
        cap.write(skeleton, "skeleton", null);
        cap.writeSavableArrayListArray2D(cache, "cache", null);
        cap.writeSavableArrayList(connectionPoints, "connectionPoints", null);

        cullRegionMappings();
        if (geometryRegions.size() > 0)
            cap.write(geometryRegions, "geometryRegions", null);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule cap = e.getCapsule(this);

        skins = (Node)cap.readSavable("skins", null);
        Bone readSkeleton = (Bone)cap.readSavable("skeleton", null);
        connectionPoints = cap.readSavableArrayList("connectionPoints", null);
        cache = cap.readSavableArrayListArray2D("cache", null);
        Savable sav = cap.readSavable("geometryRegions", null);
        if (sav != null) geometryRegions = (StringStringMap) sav;

        if (readSkeleton != null) {
            setSkeleton(readSkeleton);
            regenInfluenceOffsets();
            skeleton.addBoneListener(this);
        }

        updateWorldBound();
    }

    public void revertToBind() {
        bindMatrix = null;
        updateSkin();
    }

    public void boneChanged(BoneChangeEvent e) {
        needsRefresh = true;
    }

    public void remapInfluences(VertMap[] mappings) {
        for (int x = 0; x < mappings.length; x++) {
            remapInfluences(mappings[x], x);
        }
    }

    @SuppressWarnings("unchecked")
    public void remapInfluences(VertMap mappings, int geomIndex) {
        validateSkins();
    	ArrayList<BoneInfluence>[] infls = cache[geomIndex];
        ArrayList<BoneInfluence>[] newInfls =
                new ArrayList[getSkin(geomIndex).getVertexCount()];
        cache[geomIndex] = newInfls;
        for (int x = 0; x < infls.length; x++) {
        	for (int y = 0; y < infls[x].size(); y++) {
                BoneInfluence bi = infls[x].get(y);
                if (bi.bone != null)
                    addBoneInfluence(geomIndex, mappings.getNewIndex(x), bi.bone, bi.weight);
                else
                    addBoneInfluence(geomIndex, mappings.getNewIndex(x), bi.boneId, bi.weight);
            }
        }
        normalizeWeights(geomIndex);
    }

    /**
     * This method DOES NOT REMOVE THE Geometry.
     * It just removes the Influences of the indicated Geometry.
     */
    @SuppressWarnings("unchecked")
    public void removeGeometry(int geomIndex) {
    	if (geomIndex >= cache.length) return;
        ArrayList<BoneInfluence>[][] newCache =
                new ArrayList[skins.getQuantity()-1][];
        for (int x = 0; x < cache.length-1; x++)
            newCache[x] = cache[(x < geomIndex) ? x : (x+1)];
        cache = newCache;
    }

	public void setExternalControl(boolean externalControl) {
		this.externalControl = externalControl;
	}

	public boolean isExternalControl() {
		return externalControl;
	}

    /**
     * Assimilate all skin Geometries from the specified 'otherSkinNode',
     * leaving this other SkinNode castrated.
     *
     * Convenience wrapper for ALL skin geos.
     * After running this method, you should remove 'otherSkinNode' from any
     * scene graph it's attached to, unless you plan to add Geometry skin nodes
     * to it again.
     *
     * @see #assimilate(SkinNode, String)
     */
    public void assimilate(SkinNode otherSkinNode) {
        assimilate(otherSkinNode, null);
    }

    /**
     * Assimilates the specified skin mesh Geometries from the specified
     * 'otherSkinNode' into this one, removing them from 'otherSkinNode'.
     * If otherSkinNode is a non-null-skinRegion SkinTransferNode and no
     * narrowing regex is supplied, we will REPLACE all current Geometries
     * with that same skinRegion.
     * </P> <P>
     * For this first implementation, we have some rather stringent
     * requirements.  If use cases justify accommodating other states, these
     * requirements can be relaxed with further development work.
     * </P> <P>
     * This method will not succeed if the rest animation and rest pose frame
     * have not been set in the AnimationController (unless no
     * AnimationController has been assigned for this SkinNode yet).
     * </P>
     *
     * <b>IMPORTANT:  The calling signature is tentative.
     *    It's likely that checked exceptions will be added, so expect to
     *    need to update your exception-handling until this method has
     *    stabilized.</b>
     *
     * @param otherSkinNode Node from which the Geometries and BoneInfluences
     *                      will be taken.
     * @param geoNameRegex To specify which Geometries to assimilate.
     *                     Null to assimilate all skin Geometries from
     *                     'otherSkinNode'.
     * @throws RuntimeException for various state validation failures.
     *         This is easier to use, but not as robust.
     *         This is likely to change soon.
     */
    public void assimilate(SkinNode otherSkinNode, String geoNameRegex) {
        /* URGENT TODOs:
         *     Find out if need to stop, clear, reset animations or the
         *      AnimationController before doing this stuff.
         *     Consider how to valiate inported SkinNodes'
         *      BI.nOffset, BI.vOffset, which should always be derived, as they
         *      should never be persisted for hot skins.
         */
        Node otherSkins = otherSkinNode.getSkins();
        if (otherSkins == null || otherSkins.getQuantity() < 1) {
            logger.log(Level.WARNING,
                    "Not merging SkinNode ''{0}'' into ''{1}'' "
                    + "since no skin meshes for former", new String[] {
                    otherSkinNode.getName(), getName()});
            return;
        }
        BoneAnimation origAnim = null;
        int origFrame = -1;
        boolean origActive = false;
        AnimationController ac =
                (skeleton == null) ? null : skeleton.getAnimationController();
        if (ac == null) {
            logger.fine("No Animation Controller in place");
        } else {
            origActive = ac.isActive();
            origAnim = ac.getActiveAnimation();
            if (origAnim != null) origFrame = origAnim.getCurrentFrame();
            ac.rest();
            updateSkin();
        }
        ArrayList<BoneInfluence>[][] otherCache = otherSkinNode.getCache();
        if (otherCache == null)
            throw new IllegalArgumentException(
                    "Other skin has skin mesh(es), but no influence cache: "
                    + otherSkinNode.getName());
        if (otherSkins.getQuantity() != otherSkinNode.getCache().length)
            throw new IllegalArgumentException(
                    "SkinNode '" + otherSkinNode.getName()
                    + "' has skin geo vs. cache count mismatch: "
                    + otherSkins.getQuantity() + " vs. "
                    + otherSkinNode.getCache().length);
        logger.log(Level.FINE, "Merging SkinNode ''{0}'' into ''{1}''",
                new String[] { otherSkinNode.getName(), getName()});
        if (otherSkinNode.bindMatrix != null)
            throw new IllegalArgumentException(
                    "Skin bindMatrixes are not supported for assimilation");

        ArrayList<BoneInfluence>[] transferredInfluences;

        String skinRegion = (otherSkinNode instanceof SkinTransferNode)
            ?  ((SkinTransferNode) otherSkinNode).getSkinRegion() : null;

        Geometry g;
        validateSkins();
        otherSkinNode.validateSkins();
        if (geoNameRegex == null && skinRegion != null
                && skins != null && skins.getQuantity() > 0) {
            // This block culls old Geometries with the incoming skinRegion.
            int rmCount = 0;
            cullRegionMappings();
            int childCount = skins.getQuantity();
            for (int i = childCount -1; i >= 0; i--) {
                g = getSkin(i);
                if (!geometryRegions.containsKey(g.getName())) continue;
                if (!geometryRegions.get(g.getName()).equals(skinRegion))
                    continue;
                if (otherSkinNode.hasSkinGeometry(g.getName(), skinRegion)) {
                    logger.log(Level.INFO,
                            "Retaining geometry ''{0}''", g.getName());
                    continue;
                }
                removeSkinGeometry(i);
                rmCount++;
            }
            logger.log(Level.INFO, "Purged {0} Geos to be replaced", rmCount);
        }

        for (int i = otherSkins.getQuantity() - 1; i >= 0; i--) {
            g = otherSkinNode.getSkin(i);
            if (geoNameRegex == null) {
                if (skinRegion != null
                        && hasSkinGeometry(g.getName(), skinRegion)) continue;
                // We retained the old skin of same name and region above
            } else {
                if (!g.getName().matches(geoNameRegex)) continue;
            }
            transferredInfluences = otherCache[i];
            assimilate(otherSkinNode.removeSkinGeometry(g.getName()),
                    transferredInfluences, skinRegion);
        }

        if (ac != null) {
            ac.setActive(origActive);
            if (origAnim == null) {
                ac.clearActiveAnimation();
            } else {
                ac.setActiveAnimation(origAnim);
                if (origFrame > -1) {
                    origAnim.setCurrentFrame(origFrame);
                    updateSkin();
                    /* There are cases where this updateSkin is unnecessary,
                     * but it is very easy to miss cases where it is
                     * necessary, so just do it!
                     * The only case where this is undesirable is if an
                     * animation is the activeAnimation but the Controller
                     * itself has never been active while the animation has
                     * been active.  In this case, we will switch the active
                     * pose.  Unfortunately, it's impossible to detect this
                     * border case.
                     */
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void assimilate(Geometry newSkinGeo,
            ArrayList<BoneInfluence>[] newInfluences, String skinRegion) {
        addSkin(newSkinGeo);
        if (skinRegion != null)
            geometryRegions.put(newSkinGeo.getName(), skinRegion);
        // Node.attachChild will automatically remove from previous parent.
        // This will NOT remove the corresponding cache (BoneInfluences) if
        // the previous parent was a SkinNode.
        ArrayList<BoneInfluence>[][] newCache = (ArrayList<BoneInfluence>[][])
                new ArrayList[skins.getQuantity()][];
        for (int i = 0; i < cache.length; i++) newCache[i] = cache[i];
        newCache[newCache.length - 1] = (ArrayList<BoneInfluence>[])
                new ArrayList[newInfluences.length];
        cache = newCache;
        for (int i = 0; i < newInfluences.length; i++)
            for (BoneInfluence bi : newInfluences[i])
                addBoneInfluence(newCache.length - 1, i, bi.boneId, bi.weight);
        assignSkeletonBoneInfluences(newSkinGeo);
        regenInfluenceOffsets(newSkinGeo);
        logger.log(Level.INFO,
                "Assimilating Geometry ''{0}''", newSkinGeo.getName());
    }

     /**
      * Detaches the named skin mesh from the SkinNode, removing the associated
      * BoneInfluences along with it.
      *
      * Returns null if there is no such named skin Geometry attached.
      *
      * @see #removeSkinGeometry(int)
      */
    public Geometry removeSkinGeometry(String geoName) {
        if (skins == null) return null;
        int childCount = skins.getQuantity();
        Geometry g;
        validateSkins();
        for (int i = 0; i < childCount; i++)
            if (getSkin(i).getName().equals(geoName))
                return removeSkinGeometry(i);
        return null;
    }

    /**
     * @param skinRegion  null means match any (including no) skin region
     */
    public boolean hasSkinGeometry(String geoName, String skinRegion) {
        if (skins == null) return false;
        validateSkins();
        for (Spatial child : skins.getChildren())
            if (child.getName().equals(geoName)
                    && (skinRegion == null
                    || (geometryRegions.containsKey(geoName)
                    && geometryRegions.get(geoName).equals(skinRegion))))
                return true;
        return false;
    }

    /**
     * Detaches a skin mesh from the SkinNode, removing the associated
     * BoneInfluences along with it.
     *
     * Unlike the removeGeometry method, this one really does remove the
     * specified Geometry.
     *
     * @see #removeGeometry(int)
     */
    public Geometry removeSkinGeometry(int i) {
        if (skins == null) return null;
        validateSkins();
        if (i >= skins.getQuantity())
           throw new IllegalArgumentException(
                    "Can't remove child index " + i
                    + " when there are only " + skins.getQuantity()
                    + " children");
        Geometry g = getSkin(i);
        removeGeometry(i);
        g.removeFromParent();
        logger.log(Level.FINE, "Removed skin ''{0}''", g.getName());
        return g;
    }

    /**
     * Remove all skin Geometries, including associated BoneInfluences.
     *
     * @return Number of Geometries removed.  May be zero.
     */
    public int removeSkinGeometries() {
        return deassimilate(null);
    }

    /**
     * Remove specified skin Geometries, including associated BoneInfluences.
     *
     * @param skinRegion All skin Geometries associated with this skinRegion
     *                   name will be removed.
     *                   <b>IMPORTANT:</b> null means to remove <b>all</b>
     *                   skin Geometries, not just those with null skinRegion,
     *                   nor just those added by assimilation.
     *                   There is no method to remove just null skinRegion
     *                   skins, since null means they should be managed
     *                   obliviously to skinRegions.
     * @return Number of Geometries removed.  May be zero.
     */
    public int deassimilate(String skinRegion) {
        int rmCount = 0;
        if (skins == null) return rmCount;
        Geometry g;
        cullRegionMappings();
        validateSkins();
        int childCount = skins.getQuantity();
        for (int i = childCount -1; i >= 0; i--) {
            g = getSkin(i);
            if (skinRegion != null) {
                if (!geometryRegions.containsKey(g.getName())) continue;
                if (!geometryRegions.get(g.getName()).equals(skinRegion))
                    continue;
            }
            removeSkinGeometry(i);
            rmCount++;
        }
        return rmCount;
    }

    /**
     * We use a String key so that this structure won't delay garbage
     * collection.
     * Do not make this map public, since we update it lazily (only before we
     * need to use it).  See cullRegionMappings() about that.
     */
    protected StringStringMap geometryRegions = new StringStringMap();

    /**
     * We can't control how skin Geometries are removed, so we must cull
     * unused region mappings before we use it.
     *
     * Note that we never cull an entry unless the named Geometry is missing.
     * We will not cull because the skin region has not been loaded, or if
     * the indicated Geometry is not a SkinTransferNode Geometry (in both
     * cases, we have no way of knowing).
     */
    protected void cullRegionMappings() {
        if (skins == null || skins.getQuantity() < 1) {
            geometryRegions.clear();
            return;
        }
        Set<String> zapKeys = new HashSet<String>();

        // We can't use getChild(), descendantMatches(), etc., since we only
        // want to check direct children, not grandchildren, etc.
        EACH_KEY:
        for (String key : geometryRegions.keySet()) {
            for (Spatial child : skins.getChildren())
                if (child.getName().equals(key)) continue EACH_KEY;
            // There is no active skin with name of this key
            zapKeys.add(key);
        }
        for (String key : zapKeys) {
            geometryRegions.remove(key);
            logger.log(Level.FINE, "Culled region mapping for ''{0}''", key);
        }
    }

    /**
     * Use this to assign a skin region for a specific skin geometry.
     * This is very useful both to change skin regions of geometries loaded
     * from SkinTransferNodes, and also to assign skin regions to
     * non-SkinTransferNode skin geometries (so that traditionally loaded
     * Geometries can be automatically replaced by assimilations).
     * <P>
     * If the skinRegion for skinGeometry is already set to skin region, no
     * harm done.
     * </P>
     *
     * @param skinGeometry Should already be a skin geometry of this SkinNode.
     *   If it isn't, it will have no effect and no indication will be given.
     *   (The entry will get lazily culled in the future).
     */
    public void setSkinRegion(Geometry skinGeometry, String skinRegion) {
        geometryRegions.put(skinGeometry.getName(), skinRegion);
    }

    /**
     * Stops the current animation and poses the skins to the static pose of
     * the specified and frame.
     */
    public void pose(int frameNum) {
        AnimationController ac =
                (skeleton == null) ? null : skeleton.getAnimationController();
        if (ac == null)
            throw new IllegalStateException(
                    "Can't pose without an AnimationController set");
        BoneAnimation animation = ac.getActiveAnimation();
        if (animation == null)
            throw new IllegalStateException(
                "Controller has no active animation");
        ac.setActive(false);
        animation.setCurrentFrame(frameNum);
        //updateSkin();
        needsRefresh = true;
    }

    /**
     * Stops the current animation and poses the skins to the static pose of
     * the specified and frame.
     */
    public void pose(String frameName) {
        AnimationController ac =
                (skeleton == null) ? null : skeleton.getAnimationController();
        if (ac == null)
            throw new IllegalStateException(
                    "Can't pose without an AnimationController set");
        BoneAnimation animation = ac.getActiveAnimation();
        if (animation == null)
            throw new IllegalStateException(
                "Controller has no active animation");
        ac.setActive(false);
        animation.setCurrentFrame(frameName);
        //updateSkin();
        needsRefresh = true;
    }

    /**
     * Stops any running animation and poses the skins to the static pose of
     * the specified animation and frame.
     */
    public void pose(String animationName, int frameNum) {
        if (animationName == null)
            throw new IllegalArgumentException(
                    "Target animation must be specified");
        String origAnimationName = getAnimationString();
        AnimationController ac =
                (skeleton == null) ? null : skeleton.getAnimationController();
        if (ac == null)
            throw new IllegalStateException(
                    "Can't pose without an AnimationController set");
        if (ac.getActiveAnimation() == null)
            throw new IllegalStateException(
                "Controller has no such animation as '" + animationName + "'");
        pose(frameNum);
    }

    /**
     * Stops any running animation and poses the skins to the static pose of
     * the specified animation and frame.
     */
    public void pose(String animationName, String frameName) {
        if (animationName == null)
            throw new IllegalArgumentException(
                    "Target animation must be specified");
        String origAnimationName = getAnimationString();
        AnimationController ac =
                (skeleton == null) ? null : skeleton.getAnimationController();
        if (ac == null)
            throw new IllegalStateException(
                    "Can't pose without an AnimationController set");
        if (ac.getActiveAnimation() == null)
            throw new IllegalStateException(
                "Controller has no such animation as '" + animationName + "'");
        pose(frameName);
    }
}
