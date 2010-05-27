package com.jmex.model.animation;

import java.io.IOException;
import java.util.BitSet;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * At a point in time is defined by <b>time </b>. JointController will
 * change joint <b>i </b> to the rotation <code>jointRotation[i]</code>
 * and translation <code>jointTranslation[i]</code> at the point in time
 * <code>time</code>
 */
public class PointInTime implements Savable {


    /** The time represented by this PointInTime. */
    public float time;

    /**
     * Array of translations for this PointInTime. Each value represents a
     * translation.
     */
    public Vector3f[] jointTranslation;

    /**
     * Array of rotations for this PointInTime. Each value represents a
     * joint.
     */
    public Quaternion[] jointRotation;

    /**
     * The bitsets specify if the translation/rotation was specified
     * externally, or if it was interpolated. This is useful to cut down on
     * stored file size.
     */
    public BitSet usedTrans;

    /**
     * The bitsets specify if the translation/rotation was specified
     * externally, or if it was interpolated. This is useful to cut down on
     * stored file size.
     */
    public BitSet usedRot;

    public PointInTime(){}
    
    /**
     * Creates a new PointInTime with everything false or null to start
     * with.
     */
    public PointInTime(int numJoints) {
        jointTranslation = new Vector3f[numJoints];
        usedRot = new BitSet(numJoints);
        usedTrans = new BitSet(numJoints);
        jointRotation = new Quaternion[numJoints];
    }

    /**
     * Constructs a new PointInTime at the given time.
     * 
     * @param time
     *            The time for the new PointInTime.
     * @param controller TODO
     */
    public PointInTime(int numJoints, int time) {
        this(numJoints);
        this.time = time;
    }

    void setRotation(int jointIndex, float x, float y, float z) {
        if (jointRotation[jointIndex] == null)
                jointRotation[jointIndex] = new Quaternion();
        jointRotation[jointIndex].fromAngles(new float[] { x, y, z });
        usedRot.set(jointIndex);
    }

    void setTranslation(int jointIndex, float x, float y, float z) {
        if (jointTranslation[jointIndex] == null)
                jointTranslation[jointIndex] = new Vector3f();
        jointTranslation[jointIndex].set(x, y, z);
        usedTrans.set(jointIndex);
    }

    void setTranslation(int jointIndex, Vector3f v) {
        if (jointTranslation[jointIndex] == null)
                jointTranslation[jointIndex] = new Vector3f();
        jointTranslation[jointIndex].set(v);
        usedTrans.set(jointIndex);
    }

    /**
     * Sets for the given joint to have the given rotation.
     * 
     * @param jointIndex
     *            The joint index.
     * @param quaternion
     *            The rotation for this point in time.
     */
    public void setRotation(int jointIndex, Quaternion quaternion) {
        if (jointRotation[jointIndex] == null)
                jointRotation[jointIndex] = new Quaternion();
        jointRotation[jointIndex].set(quaternion);
        usedRot.set(jointIndex);
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(time, "time", 0);
        capsule.write(jointTranslation, "jointTranslation", new Vector3f[0]);
        capsule.write(jointRotation, "jointRotation", new Quaternion[0]);
        capsule.write(usedTrans, "usedTrans", new BitSet(0));
        capsule.write(usedRot, "usedRot", new BitSet(0));
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        time = capsule.readFloat("time", 0);
        Savable[] savs = capsule.readSavableArray("jointTranslation", new Vector3f[0]);
        if (savs == null)
            jointTranslation = null;
        else {
            jointTranslation = new Vector3f[savs.length];
            for (int x = 0; x < savs.length; x++) {
                jointTranslation[x] = (Vector3f)savs[x];
            }
        }
        savs = capsule.readSavableArray("jointRotation", new Quaternion[0]);
        if (savs == null)
            jointRotation = null;
        else {
            jointRotation = new Quaternion[savs.length];
            for (int x = 0; x < savs.length; x++) {
                jointRotation[x] = (Quaternion)savs[x];
            }
        }
        
        usedTrans = capsule.readBitSet("usedTrans", new BitSet(0));
        usedRot = capsule.readBitSet("usedRot", new BitSet(0));
        
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}