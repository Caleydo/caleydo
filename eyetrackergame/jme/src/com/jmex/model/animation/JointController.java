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

package com.jmex.model.animation;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.jme.math.Quaternion;
import com.jme.math.TransformMatrix;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.system.JmeException;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;
import com.jmex.model.JointMesh;

/**
 * Started Date: Jun 9, 2004 <br>
 * 
 * This controller animates a Node's JointMesh children acording to the joints
 * stored inside <code>movementInfo</code>.
 * 
 * @author Jack Lindamood
 */
public class JointController extends Controller {

    private static final long serialVersionUID = 1L;

    /**
     * It is JointController's responsibility to keep changePoints sorted by
     * <code>time</code> at all times.
     */
    public int numJoints;

    /**
     * movementInfo[i] contains a float value time and an array of
     * TransformMatrix. At time <code>time</code> the joint i is at movement
     * <code>jointChange[i]</code>
     */
    public ArrayList<PointInTime> movementInfo;

    /**
     * parentIndex contains a list of who's parent a joint is. -1 indicates a
     * root joint with no parent
     */
    public int[] parentIndex;

    /**
     * Local refrence matrix that can determine a joint's position in space
     * relative to its parent.
     */
    public TransformMatrix[] localRefMatrix;

    /** Currently unused. */
    public float FPS;

    /**
     * Array of all the meshes this controller should consider animating.
     */
    public ArrayList<JointMesh> movingMeshes;

    /**
     * This controller's internal current time.
     */
    private float curTime;

    /**
     * This controller's internal current PointInTime index.
     */
    private int curTimePoint;

    /**
     * Used internally, they are updated every update(float) call to tell points
     * how to change.
     */
    private TransformMatrix[] jointMovements;

    /**
     * The inverse chain matrix of every joint. Calculated once with
     * prosessController()
     */
    private TransformMatrix[] inverseChainMatrix;

    // Internal worker classes
    private final static Quaternion unSyncbeginAngle = new Quaternion();

    private final static Vector3f unSyncbeginPos = new Vector3f();

    private final static TransformMatrix tempUnSyncd = new TransformMatrix();

    /**
     * Tells update that it should be called every <code>skipRate</code>
     * seconds
     */
    public float skipRate;

    /**
     * Used with skipRate internally.
     */
    private float currentSkip;

    /** If true, the model's bounding volume will update every frame. */
    private boolean updatePerFrame = true;

    private boolean movingForward = true;

    public JointController() {
        curTime = 0;
        curTimePoint = 1;
    }
    
    /**
     * Constructs a new JointController that will hold the given number of
     * joints.
     * 
     * @param numJoints
     *            The number of joints this jointController will have
     */
    public JointController(int numJoints) {
        this.numJoints = numJoints;
        parentIndex = new int[numJoints];
        localRefMatrix = new TransformMatrix[numJoints];
        movingMeshes = new ArrayList<JointMesh>();
        jointMovements = new TransformMatrix[numJoints];
        inverseChainMatrix = new TransformMatrix[numJoints];
        for (int i = 0; i < numJoints; i++) {
            localRefMatrix[i] = new TransformMatrix();
            jointMovements[i] = new TransformMatrix();
            inverseChainMatrix[i] = new TransformMatrix();
        }
        movementInfo = new ArrayList<PointInTime>();
        curTime = 0;
        curTimePoint = 1;
        currentSkip = 0;
        skipRate = .01f;
    }
    
    public float getCurrentTime() {
        return curTime;
    }

    /**
     * Tells JointController that at time <code>time</code> the joint
     * <code>jointNumber</code> will translate to x,y,z relative to its parent
     * 
     * @param jointNumber
     *            Index of joint to affect
     * @param time
     *            Which time the joint will take these values
     * @param x
     *            Joint's x translation
     * @param y
     *            Joint's y translation
     * @param z
     *            Joint's z translation
     */
    public void setTranslation(int jointNumber, float time, float x, float y,
            float z) {
        findUpToTime(time).setTranslation(jointNumber, x, y, z);
    }

    /**
     * Tells JointController that at time <code>time</code> the joint
     * <code>jointNumber</code> will translate to x,y,z relative to its parent
     * 
     * @param jointNumber
     *            Index of joint to affect
     * @param time
     *            Which time the joint will take these values
     * @param trans
     *            Joint's translation
     *  
     */
    public void setTranslation(int jointNumber, float time, Vector3f trans) {
        findUpToTime(time).setTranslation(jointNumber, trans);
    }

    /**
     * Tells JointController that at time <code>time</code> the joint
     * <code>jointNumber</code> will rotate acording to the euler angles x,y,z
     * relative to its parent's rotation
     * 
     * @param jointNumber
     *            Index of joint to affect
     * @param time
     *            Which time the joint will take these values
     * @param x
     *            Joint's x rotation
     * @param y
     *            Joint's y rotation
     * @param z
     *            Joint's z rotation
     */
    public void setRotation(int jointNumber, float time, float x, float y,
            float z) {
        findUpToTime(time).setRotation(jointNumber, x, y, z);
    }

    /**
     * Tells JointController that at time <code>time</code> the joint
     * <code>jointNumber</code> will rotate acording to
     * <code>Quaternion</code>.
     * 
     * @param jointNumber
     *            Index of joint to affect
     * @param time
     *            Which time the joint will take these values
     * @param quaternion
     *            The joint's new rotation
     */
    public void setRotation(int jointNumber, float time, Quaternion quaternion) {
        findUpToTime(time).setRotation(jointNumber, quaternion);
    }

    /**
     * Used with setRotation and setTranslation. This function finds a point in
     * time for given time. If one doesn't exist then a new PointInTime is
     * created and returned.
     * 
     * @param time
     * @return The PointInTime at that given time, or a new one if none exist so
     *         far.
     */
    private PointInTime findUpToTime(float time) {
        int index = 0;
        for (PointInTime point : movementInfo) {
            float curTime = point.time;
            if (curTime >= time) break;
            index++;
        }
        PointInTime storedNext = null;
        if (index == movementInfo.size()) {
            storedNext = new PointInTime(numJoints);
            movementInfo.add(storedNext);
            storedNext.time = time;
        } else {
            if (movementInfo.get(index).time == time) {
                storedNext = movementInfo.get(index);
            } else {
                storedNext = new PointInTime(numJoints);
                movementInfo.add(index, storedNext);
                storedNext.time = time;
            }
        }
        return storedNext;
    }

    /**
     * Updates the <code>movingMeshes</code> by updating their joints +=time
     * 
     * @param time
     *            Time from last update
     */
    public void update(float time) {
        if (numJoints == 0) return;
        if (movingForward)
            curTime += time * this.getSpeed();
        else
            curTime -= time * this.getSpeed();
        currentSkip += time;
        if (currentSkip >= skipRate) {
            currentSkip = 0;
        } else {
            return;
        }

        setCurTimePoint();
        PointInTime now = movementInfo.get(curTimePoint);
        PointInTime then = movementInfo.get(curTimePoint - 1);

        float delta = (curTime - then.time) / (now.time - then.time);
        createJointTransforms(delta);
        combineWithInverse();
        updateData();
    }

    private void setCurTimePoint() {

        int repeatType = getRepeatType();
        // reset if too far
        if (curTime > getMaxTime()) {
            if (repeatType == Controller.RT_WRAP)
                curTime = getMinTime();
            else if (repeatType == Controller.RT_CYCLE) {
                curTime = getMaxTime();
                movingForward = false;
            } else if (repeatType == Controller.RT_CLAMP) {
                setActive(false);
                curTime = getMaxTime();
            }
        }

        if (curTime < getMinTime()) {
            if (repeatType == Controller.RT_WRAP)
                curTime = getMaxTime();
            else if (repeatType == Controller.RT_CYCLE) {
                curTime = getMinTime();
                movingForward = true;
            } else if (repeatType == Controller.RT_CLAMP) {
                setActive(false);
                curTime = getMinTime();
            }
        }

        // if curTimePoint works then return
        PointInTime p1 = movementInfo.get(curTimePoint);
        PointInTime p2 = movementInfo.get(curTimePoint - 1);
        if (curTime <= p1.time && curTime >= p2.time) return;
        for (curTimePoint = 1; curTimePoint < movementInfo.size(); curTimePoint++) {
            p1 = movementInfo.get(curTimePoint);
            if (p1.time >= curTime) return;
        }
    }

    /**
     * Sets the frames the joint controller will animate from and to. The frames
     * are dependant upon the FPS. Remember that the first frame starts at 1,
     * <b>NOT </b>0.
     * 
     * @param start
     *            The starting frame number.
     * @param end
     *            The ending frame number.
     */
    public void setTimes(int start, int end) {
        if (start < 0
                || start > end
                || movementInfo.get(movementInfo.size() - 1).time < end
                        / FPS) {
            String message = "Malformed times: start="
                    + start
                    + " end="
                    + end
                    + " start limit: 0 End limit: "
                    + movementInfo.get(movementInfo.size() - 1).time
                    * FPS;
            throw new JmeException(message);
        }
        setMinTime(start / FPS);
        setMaxTime(end / FPS);
        curTime = getMinTime();
        curTimePoint = 1;
        movingForward = true;
    }

    /**
     * Used with update(float). <code>updateData</code> moves every normal and
     * vertex acording to its jointIndex
     */
    private void updateData() {
        for (int currentGroup = 0; currentGroup < movingMeshes.size(); currentGroup++) {
            JointMesh updatingGroup = movingMeshes
                    .get(currentGroup);
            int currentBoneIndex;
            FloatBuffer verts = updatingGroup.getVertexBuffer();
            FloatBuffer normals = updatingGroup.getNormalBuffer();
            int j;
            for (j = 0; j < updatingGroup.jointIndex.length; j++) {
                currentBoneIndex = updatingGroup.jointIndex[j];
                if (currentBoneIndex == -1) continue;
                unSyncbeginPos.set(updatingGroup.originalVertex[j]);
                BufferUtils.setInBuffer(jointMovements[currentBoneIndex]
                                                       .multPoint(unSyncbeginPos), verts, j);
                unSyncbeginPos.set(updatingGroup.originalNormal[j]);
                BufferUtils.setInBuffer(jointMovements[currentBoneIndex]
                                                       .multNormal(unSyncbeginPos), normals, j);
            }
            if (j != 0) {
                if (updatePerFrame) updatingGroup.updateModelBound();
            }
        }
    }

    /**
     * Used with update(float) to combine joints with their inverse to properly
     * translate points.
     */
    private void combineWithInverse() {
        for (int i = 0; i < numJoints; i++)
            jointMovements[i].multLocal(inverseChainMatrix[i], unSyncbeginPos);
    }

    /**
     * Processes a JointController by filling holes and creating inverse
     * matrixes. Should only be called once per JointController object lifetime
     */
    public void processController() {
        if (movementInfo.size() == 1) { // IE no times were added or only time 0
            // was added
            movementInfo.add(0, new PointInTime(0));
        }
        setMinTime(movementInfo.get(0).time);
        setMaxTime(movementInfo.get(movementInfo.size() - 1).time);
        curTime = getMinTime();
        invertWithParents();
        fillHoles();
    }

    /**
     * If true, the model's bounding volume will be updated every frame. If
     * false, it will not.
     * 
     * @param update
     *            The new update model volume per frame value.
     */
    public void setModelUpdate(boolean update) {
        updatePerFrame = update;
    }

    /**
     * Returns true if the model's bounding volume is being updated every frame.
     * 
     * @return True if bounding volume is updating.
     */
    public boolean getModelUpdate() {
        return updatePerFrame;
    }

    /**
     * Inverts joints with their parents. Only called once per JointController
     * lifetime during processing.
     */
    private void invertWithParents() {
        for (int i = 0; i < numJoints; i++) {
            inverseChainMatrix[i] = new TransformMatrix(localRefMatrix[i]);
            inverseChainMatrix[i].inverse();
            if (parentIndex[i] != -1)
                    inverseChainMatrix[i].multLocal(
                            inverseChainMatrix[parentIndex[i]], unSyncbeginPos);
        }
    }

    /**
     * Called with update to create the needed joint transforms for that point
     * in time.
     * 
     * @param changeAmnt
     *            The % diffrence (from 0-1) between two points in time
     */
    private void createJointTransforms(float changeAmnt) {
        PointInTime now = movementInfo.get(curTimePoint);
        PointInTime then = movementInfo.get(curTimePoint - 1);
        for (int index = 0; index < numJoints; index++) {
            int theParentIndex = parentIndex[index];

            unSyncbeginAngle.set(then.jointRotation[index]);
            unSyncbeginPos.set(then.jointTranslation[index]);

            unSyncbeginAngle.slerp(now.jointRotation[index], changeAmnt);
            unSyncbeginPos.interpolate(now.jointTranslation[index], changeAmnt);

            tempUnSyncd.set(unSyncbeginAngle, unSyncbeginPos);
            jointMovements[index].set(localRefMatrix[index]);
            jointMovements[index].multLocal(tempUnSyncd, unSyncbeginPos);
            if (theParentIndex != -1) {
                tempUnSyncd.set(jointMovements[index]);
                jointMovements[index].set(jointMovements[theParentIndex]);
                jointMovements[index].multLocal(tempUnSyncd, unSyncbeginPos);
            }
        }
    }

    /**
     * Fills null rotations and translations for any joint at any point in time.
     */
    private void fillHoles() {
        fillRots();
        fillTrans();
    }

    /**
     * Gives every PointInTime for every joint a valid rotation.
     */
    private void fillRots() {
        for (int joint = 0; joint < numJoints; joint++) {
            // 1) Find first non-null rotation of joint <code>joint</code>
            int start;
            for (start = 0; start < movementInfo.size(); start++) {
                if (movementInfo.get(start).jointRotation[joint] != null)
                        break;
            }
            if (start == movementInfo.size()) { // if they are all null then
                // fill with identity
                for (int i = 0; i < movementInfo.size(); i++)
                    movementInfo.get(i).jointRotation[joint] = new Quaternion();
                continue; // we're done with this joint so lets continue
            }
            if (start != 0) { // if there -are- null elements at the begining,
                // then fill with first non-null
                unSyncbeginAngle
                        .set(movementInfo.get(start).jointRotation[joint]);
                for (int i = 0; i < start; i++)
                    movementInfo.get(i).jointRotation[joint] = new Quaternion(
                            unSyncbeginAngle);
            }
            int lastgood = start;
            boolean allGood = true;
            for (int i = start + 2; i < movementInfo.size(); i++) {
                if (movementInfo.get(i).jointRotation[joint] != null) {
                    fillQuats(joint, lastgood, i); // fills gaps
                    lastgood = i;
                    allGood = false;
                }
            }
            if (!allGood && lastgood != movementInfo.size() - 1) { // fills tail
                movementInfo.get(movementInfo.size() - 1).jointRotation[joint] = new Quaternion(
                        movementInfo.get(lastgood).jointRotation[joint]);
                fillQuats(joint, lastgood, movementInfo.size() - 1); // fills
                // tail
            }
        }
    }

    /**
     * Gives every PointInTime for every joint a valid translation.
     */
    private void fillTrans() {
        for (int joint = 0; joint < numJoints; joint++) {
            // 1) Find first non-null translation of joint <code>joint</code>
            int start;
            for (start = 0; start < movementInfo.size(); start++) {
                if (movementInfo.get(start).jointTranslation[joint] != null)
                        break;
            }
            if (start == movementInfo.size()) { // if they are all null then
                // fill with identity
                for (int i = 0; i < movementInfo.size(); i++)
                    movementInfo.get(i).jointTranslation[joint] = new Vector3f(
                            0, 0, 0);
                continue; // we're done with this joint so lets continue
            }
            if (start != 0) { // if there -are- null elements at the begining,
                // then fill with first non-null
                unSyncbeginPos
                        .set(movementInfo.get(start).jointTranslation[joint]);
                for (int i = 0; i < start; i++)
                    movementInfo.get(i).jointTranslation[joint] = new Vector3f(
                            unSyncbeginPos);
            }
            int lastgood = start;
            boolean allGood = true;
            for (int i = start + 2; i < movementInfo.size(); i++) {
                if (movementInfo.get(i).jointTranslation[joint] != null) {
                    fillPos(joint, lastgood, i); // fills gaps
                    lastgood = i;
                    allGood = false;
                }
            }
            if (!allGood && lastgood != movementInfo.size() - 1) { // fills tail
                movementInfo.get(movementInfo.size() - 1).jointTranslation[joint] = new Vector3f(
                        movementInfo.get(lastgood).jointTranslation[joint]);
                fillPos(joint, lastgood, movementInfo.size() - 1); // fills tail
            }
        }
    }

    /**
     * Interpolates missing quats that weren't specified to the JointController.
     * 
     * @param jointIndex
     *            Index of the joint that has missing quats
     * @param startRotIndex
     *            Begining index of a valid non-null quat
     * @param endRotIndex
     *            Ending index of a valid non-null quat
     */
    private void fillQuats(int jointIndex, int startRotIndex, int endRotIndex) {
        unSyncbeginAngle
                .set(movementInfo.get(startRotIndex).jointRotation[jointIndex]);
        for (int i = startRotIndex + 1; i < endRotIndex; i++) {
            movementInfo.get(i).jointRotation[jointIndex] = new Quaternion(
                    unSyncbeginAngle);
            movementInfo.get(i).jointRotation[jointIndex]
                    .slerp(
                            movementInfo.get(endRotIndex).jointRotation[jointIndex],
                            ((float) i - startRotIndex)
                                    / (endRotIndex - startRotIndex));
        }
    }

    /**
     * Interpolates missing vector that weren't specified to the
     * JointController.
     * 
     * @param jointIndex
     *            Index of the joint that has missing vector
     * @param startPosIndex
     *            Begining index of a valid non-null vector
     * @param endPosIndex
     *            Ending index of a valid non-null vector
     */
    private void fillPos(int jointIndex, int startPosIndex, int endPosIndex) {
        unSyncbeginPos
                .set(movementInfo.get(startPosIndex).jointTranslation[jointIndex]);
        for (int i = startPosIndex + 1; i < endPosIndex; i++) {
            movementInfo.get(i).jointTranslation[jointIndex] = new Vector3f(
                    unSyncbeginPos);
            movementInfo.get(i).jointTranslation[jointIndex]
                    .interpolate(
                            movementInfo.get(endPosIndex).jointTranslation[jointIndex],
                            ((float) i - startPosIndex)
                                    / (endPosIndex - startPosIndex));
        }
    }

    /**
     * Adds a jointmesh for this JointController to consider animating.
     * 
     * @param child
     *            Child JointMesh to consider
     */
    public void addJointMesh(JointMesh child) {
        movingMeshes.add(child);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(numJoints, "numJoints", 0);
        capsule.writeSavableArrayList(movementInfo, "movementInfo", new ArrayList<PointInTime>());
        capsule.write(parentIndex, "parentIndex", new int[numJoints]);
        capsule.write(localRefMatrix, "localRefMatrix", new TransformMatrix[numJoints]);
        capsule.write(FPS, "FPS", 0);
        capsule.writeSavableArrayList(movingMeshes, "movingMeshes", new ArrayList<JointMesh>());
        capsule.write(jointMovements, "jointMovements", new TransformMatrix[numJoints]);
        capsule.write(skipRate, "skipRate", 0);
        capsule.write(updatePerFrame, "updatePerFrame", false);
        capsule.write(movingForward, "movingForward", false);
    }
    
    @SuppressWarnings("unchecked")
	public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        numJoints = capsule.readInt("numJoints", 0);
        movementInfo = capsule.readSavableArrayList("movementInfo", new ArrayList<PointInTime>());
        parentIndex = capsule.readIntArray("parentIndex", new int[numJoints]);
        
        Savable[] savs = capsule.readSavableArray("localRefMatrix", new TransformMatrix[numJoints]);
        if (savs == null)
            localRefMatrix = null;
        else {
            localRefMatrix = new TransformMatrix[savs.length];
            for (int x = 0; x < savs.length; x++) {
                localRefMatrix[x] = (TransformMatrix)savs[x];
            }
        }
        
        savs = capsule.readSavableArray("jointMovements", new TransformMatrix[numJoints]);
        if (savs == null)
            jointMovements = null;
        else {
            jointMovements = new TransformMatrix[savs.length];
            for (int x = 0; x < savs.length; x++) {
                jointMovements[x] = (TransformMatrix)savs[x];
            }
        }
        
        FPS = capsule.readFloat("FPS", 0);
        movingMeshes = capsule.readSavableArrayList("movingMeshes", new ArrayList<JointMesh>());
        skipRate = capsule.readFloat("skipRate", 0);
        updatePerFrame = capsule.readBoolean("updatePerFrame", false);
        movingForward = capsule.readBoolean("movingForward", false);
        
        
        inverseChainMatrix = new TransformMatrix[numJoints];
        for (int i = 0; i < numJoints; i++) {
            inverseChainMatrix[i] = new TransformMatrix();
        }
        processController();
    }
}