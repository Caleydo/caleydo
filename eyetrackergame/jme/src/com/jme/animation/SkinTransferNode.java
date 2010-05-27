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

import java.util.ArrayList;
import java.io.IOException;

import com.jme.math.Matrix4f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.util.geom.VertMap;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * A non-parented SkinNode which is enforced to be used only for holding
 * SkinNode Geometry skins.
 * </P> <P>
 * Specifically, this SkinNode will throw if you attempt to parent it, or if
 * you attempt to set skeleton, controller, animation, or other
 * non-skin-geometry properties.
 * </P> <P>
 * This class is intended to be used for classes dedicated for usage as a
 * SkinNode.assimilate() targets.
 * </P> <P>
 * Especially useful is the skinRegion attribute.
 * Unlike normal SkinNodes, this class does not allow different skinRegions
 * per Geometry, but the skinRegion of this SkinTransferNode applies to all its
 * Geometries.
 * When a SkinTransferNode is assimilated which has a non-null skinRegion, it
 * will <i>replace</i> all currently loaded skin Geometries of that same
 * skinRegion.
 * (Except for same-named Geometries, which are retained instead of replaced).
 * </P>
 *
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 * @see SkinNode.assimilate(SkinNode)
 */
public class SkinTransferNode extends SkinNode {
    private static final long serialVersionUID = 1L;
    /**
     * Empty Constructor to be used internally only.
     */
    public SkinTransferNode() {
    }

    public SkinTransferNode(String name) {
        super(name);
    }

    protected String skinRegion;

    public String getSkinRegion() {
        return skinRegion;
    }

    /**
     * Sets the target skin region.
     * If set to a non-null value, then when this SkinTransferNode is
     * assimilated, these Geometries will <i>replace</i> all previously loaded
     * Geometries with the same skinRegion.
     */
    public void setSkinRegion(String skinRegion) {
        this.skinRegion = skinRegion;
    }

    /**
     * Do not use thos method with SkinTransferNodes.
     */
    protected void setParent(Node parent) {
        throw new IllegalStateException(
            SkinTransferNode.class.getName() + "s may not be parented");
    }

    /**
     * Do not use thos method with SkinTransferNodes.
     */
    public void addBoneInfluence(int geomIndex, int vert, Bone bone,
            float weight) {
        throw new IllegalStateException(
                "Only the 'boneId' variant of 'addBoneInfluence' may be used "
                + "for " + SkinTransferNode.class.getName());
    }

    /**
     * Do not use thos method with SkinTransferNodes.
     */
    public void setAnimation(BoneAnimation anim) {
        throw new IllegalStateException(
            "Animation may not be set for " + SkinTransferNode.class.getName());
    }

    /**
     * Do not use thos method with SkinTransferNodes.
     */
    public void setAnimation(int index) {
        throw new IllegalStateException(
            "Animation may not be set for " + SkinTransferNode.class.getName());
    }

    /**
     * Do not use thos method with SkinTransferNodes.
     */
    public void setAnimation(String name) {
        throw new IllegalStateException(
            "Animation may not be set for " + SkinTransferNode.class.getName());
    }

    public String getAnimationString() {
    	return null;

   	}

    /**
     * Do not use thos method with SkinTransferNodes.
     */
    public void setSkeleton(Bone b) {
        throw new IllegalStateException(
            "Skeleton may not be set for " + SkinTransferNode.class.getName());
    }

    public Bone getSkeleton() {
        return null;
    }

    /**
     * Do not use thos method with SkinTransferNodes.
     */
    public void assignSkeletonBoneInfluences() {
        throw new IllegalStateException(
            SkinTransferNode.class.getName() + "s do not have Skeletons");
    }

    /**
     * Do not use thos method with SkinTransferNodes.
     */
    public void regenInfluenceOffsets() {
        throw new IllegalStateException(
            SkinTransferNode.class.getName() + "s do not have Skeletons");
    }

    /**
     * Do not use thos method with SkinTransferNodes.
     */
    public synchronized void updateSkin() {
        throw new IllegalStateException(
            SkinTransferNode.class.getName() + "s do not have Skeletons");
    }

    /**
     * Do not use thos method with SkinTransferNodes.
     */
    public void setBindMatrix(Matrix4f mat) {
        throw new IllegalStateException(
            SkinTransferNode.class.getName() + "s do not have bind matrixes");
    }

    /**
     * Do not use thos method with SkinTransferNodes.
     */
    public void remapInfluences(VertMap mappings, int geomIndex) {
        throw new IllegalStateException(
            SkinTransferNode.class.getName() + "s do not have Skeletons");
    }

    /**
     * Do not use thos method with SkinTransferNodes.
     */
    protected void assimilate(
            Geometry newSkinGeo, ArrayList<BoneInfluence>[] newInfluences) {
        throw new IllegalStateException(
            "Can only assimilate FROM a " + SkinTransferNode.class.getName());
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        if (skinRegion != null)
            e.getCapsule(this).write(skinRegion, "region", null);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        if (bindMatrix != null)
            throw new IOException(
            SkinTransferNode.class.getName() + "s may not have bind matrixes");
        skinRegion = e.getCapsule(this).readString("region", null);
    }

    /**
     * Do not use thos method with SkinTransferNodes.
     */
    public void setSkinRegion(Geometry skinGeometry, String skinRegion) {
        throw new IllegalStateException(
                "setSkinRegion is not available for SkinTransferNode, since "
                + "all Geometries in a SkinTransferNode share the single "
                + "skinRegion of the SkinTransferNode itself");
    }

    public boolean hasSkinGeometry(String geoName, String matchSkinRegion) {
        if (skins == null) return false;
        if (matchSkinRegion != null &&
            (skinRegion == null || !matchSkinRegion.equals(skinRegion)))
                return false;
        validateSkins();
        for (Spatial child : skins.getChildren())
            if (child.getName().equals(geoName)) return true;
        return false;
    }
}
