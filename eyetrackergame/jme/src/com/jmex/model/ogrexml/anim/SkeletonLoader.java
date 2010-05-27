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

package com.jmex.model.ogrexml.anim;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.w3c.dom.Node;
import static com.jmex.model.XMLUtil.*;

public class SkeletonLoader {

    private static final Logger logger = Logger.getLogger(SkeletonLoader.class.getName());

    /**
     * Loads a skeleton object given XML node.
     * All BONEs bind and inverseBind matrices will contain valid values.
     *
     * @param skeletonNode
     * @return
     */
    public static final Skeleton loadSkeleton(Node skeletonNode){
        // maps the name of a bone to itself
        Map<String, Bone> boneMap = new HashMap<String, Bone>();

        // maps the index of a bone to itself
        Map<Integer, Bone> indexedBoneMap = new HashMap<Integer, Bone>();

        // some work variables
        Vector3f vpos   = new Vector3f(0, 0, 0);
        Quaternion vrot = new Quaternion();
        //Vector3f vscale = new Vector3f(1, 1, 1);
        Vector3f vaxis  = new Vector3f(1, 0, 0);

        // skeleton -> bones -> bone
        Node bonesNode = getChildNode(skeletonNode, "bones");
        Node boneNode = bonesNode.getFirstChild();
        while (boneNode != null){
            if (!boneNode.getNodeName().equals("bone")){
                boneNode = boneNode.getNextSibling();
                continue;
            }

            int id = getIntAttribute(boneNode, "id");
            Bone bone = new Bone(getAttribute(boneNode, "name"));

            boneMap.put(bone.name, bone);
            //indexedBoneMap.put(id+1, bone);
            indexedBoneMap.put(id, bone);

            Node rot = getChildNode(boneNode, "rotation");
            if (rot != null){
                Node axis = getChildNode(rot, "axis");
                vaxis.set(getFloatAttribute(axis, "x"),
                          getFloatAttribute(axis, "y"),
                          getFloatAttribute(axis, "z"));

                if (vaxis.length() != 1.0){
                    logger.warning("Rotation axis not normalized");
                    vaxis.normalizeLocal();
                }

                vrot.fromAngleNormalAxis(getFloatAttribute(rot, "angle"), vaxis);
            }else{
                vrot.loadIdentity();
            }

            Node pos = getChildNode(boneNode, "position");
            if (pos != null){
                vpos.set(getFloatAttribute(pos, "x"),
                         getFloatAttribute(pos, "y"),
                         getFloatAttribute(pos, "z"));
            }else{
                vpos.zero();
            }

//            Node scale = getChildNode(boneNode, "scale");
//            if (scale != null){
//                vscale.set(getFloatAttribute(scale, "x"),
//                           getFloatAttribute(scale, "y"),
//                           getFloatAttribute(scale, "z"));
//            }else{
//                vscale.set(Vector3f.UNIT_XYZ);
//            }

            // compile individual transformations into bind matrix
            bone.setBindTransforms(vpos, vrot);

            boneNode = boneNode.getNextSibling();
        }

        // skeleton -> bonehierarchy -> boneparent
        Node bonehierarchy = getChildNode(skeletonNode, "bonehierarchy");
        Node boneparent = bonehierarchy.getFirstChild();
        while (boneparent != null){
            if (!boneparent.getNodeName().equals("boneparent")){
                boneparent = boneparent.getNextSibling();
                continue;
            }

            Bone bone = boneMap.get(getAttribute(boneparent, "bone"));
            Bone parent = boneMap.get(getAttribute(boneparent, "parent"));

            parent.addChild(bone);

            boneparent = boneparent.getNextSibling();
        }

        Bone[] bones = new Bone[indexedBoneMap.size()];

        // find bones without a parent and attach them to the skeleton
        // also assign the bones to the bonelist
        for (Map.Entry<Integer, Bone> entry: indexedBoneMap.entrySet()){
            Bone bone = entry.getValue();
            bones[entry.getKey()] = bone;
        }

        return new Skeleton(bones);
    }

}
