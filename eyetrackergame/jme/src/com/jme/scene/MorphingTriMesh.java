package com.jme.scene;

import java.io.IOException;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.jme.scene.TriMesh;
import com.jme.animation.SkinNode;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.state.RenderState;
import com.jme.renderer.Renderer;
import com.jme.util.export.StringFloatMap;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;
import com.jme.util.export.ListenableStringFloatMap;

/**
 * A MorphingGeometry implementation for TriMesh component morph Geometries.
 * <P>
 * Only component TriMeshes of mode Triangles are supported.
 * <P> </P>
 * The base morph must have &gt;= vertexes as the other morphs.
 * </P>
 *
 * TODO:  Optimize the instantiation procedure.  It's pretty complicated to get
 * to reconstitute or instantiate local and delegated influences properly
 * without invoking expensive but unnecessary forceMorphs.
 *
 * @see MorphingGeometry
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 */
public class MorphingTriMesh extends TriMesh implements MorphingGeometry {
    static final long serialVersionUID = 90398457851313109L;
    private static final Logger logger =
            Logger.getLogger(MorphingTriMesh.class.getName());

    protected Map<TriMesh, FloatBuffer> extrapolatedVertBuf;
    protected Map<TriMesh, FloatBuffer> extrapolatedNormBuf;
    protected List<TriMesh> morphs = new ArrayList<TriMesh>();
    protected List<String> morphKeys = new ArrayList<String>();
    // Not using an ordered Map of some type for these paired lists, only
    // because of the atrocious J2SE support for Maps which "preserve" order
    // but do not "apply" an order (I.e. to just preserve the order in which
    // the items are added).
    protected TriMesh baseMorph;
    volatile private boolean dirty = true;
    protected boolean needExtrapolation;
    protected float morphInfluenceThreshold = .00001f;

    /**
     * @see MorphingGeometry#getMorphInfluenceThreshold()
     */
    public void setMorphInfluenceThreshold(float morphInfluenceThreshold) {
        this.morphInfluenceThreshold = morphInfluenceThreshold;
    }

    /**
     * @see MorphingGeometry#setMorphInfluenceThreshold(float)
     */
    public float getMorphInfluenceThreshold() {
        return morphInfluenceThreshold;
    }

    /**
     * @return the extrapolated vertexBuffer for the specified Morph, if one
     * was generated.  Otherwise, return the traditional vert buffer for it.
     */
    protected FloatBuffer getMorphVertBuffer(TriMesh m) {
        return (extrapolatedVertBuf == null
                || !extrapolatedVertBuf.containsKey(m))
                ? m.getVertexBuffer() : extrapolatedVertBuf.get(m);
    }

    /**
     * @return the extrapolated normBuffer for the specified Morph, if one
     * was generated.  Otherwise, return the traditional norm buffer for it.
     */
    protected FloatBuffer getMorphNormBuffer(TriMesh m) {
        return (extrapolatedNormBuf == null
                || !extrapolatedNormBuf.containsKey(m))
                ? m.getNormalBuffer() : extrapolatedNormBuf.get(m);
    }

    /** This one may be a reference to a remotely managed map */
    protected ListenableStringFloatMap morphInfluencesMap;

    /** This one is persisted with this MorphingTriMesh instance */
    protected ListenableStringFloatMap localMorphInfluencesMap;

    /**
     * @see MorphingGeometry#getMorphInfluencesMap()
     */
    public ListenableStringFloatMap getMorphInfluencesMap() {
        return morphInfluencesMap;
    }

    /**
     * @see MorphingGeometry#setMorphInfluencesMap(ListenableStringFloatMap)
     */
    public void setMorphInfluencesMap(ListenableStringFloatMap m) {
        if (localMorphInfluencesMap == m) return;
        if (localMorphInfluencesMap != null)
            localMorphInfluencesMap.removeListener(this);
        localMorphInfluencesMap = m;
        localMorphInfluencesMap.addListener(this, morphKeys);
        morphInfluencesMap = localMorphInfluencesMap;
    }

    /**
     * @see MorphingGeometry#setSingleMorphInfluence(String, float)
     */
    public void setSingleMorphInfluence(String morphKey, float influence) {
        if (morphInfluencesMap == null)
            throw new IllegalStateException("No morphInfluences set");
        morphInfluencesMap.put(morphKey, influence);
    }

    /**
     * @see MorphingGeometry#setMorphInfluences(
     *           Map<? extends String, ? extends Float>)
     */
    public void setMorphInfluences(Map<? extends String, ? extends Float> m) {
        if (morphInfluencesMap == null)
            throw new IllegalStateException("No morphInfluences set");
        morphInfluencesMap.putAll(m);
    }

    /**
     * @see MorphingGeometry#delegateInfluences()
     */
    public boolean delegateInfluences() {
        ListenableStringFloatMap newInfluencesMap = null;
        Node ancestor = null;
        for (ancestor = getParent(); ancestor != null;
                ancestor = ancestor.getParent()) {
            logger.log(
                    Level.FINEST, "Trying anc ''{0}''...", ancestor.getName());
            if (!(ancestor instanceof MorphInfluencesMapProvider)) continue;
            newInfluencesMap =
                ((MorphInfluencesMapProvider) ancestor).getMorphInfluencesMap();
            if (newInfluencesMap != null) break;
        }
        if (newInfluencesMap == null) {
            logger.info("Failed to find a parent node to delegate to");
            return false;
        }
        if (morphInfluencesMap == newInfluencesMap) {
            logger.fine("The first MorphInfluencesMapProvider found has the "
                    + "same MorphInfluencesMap that we are already using");
            return false;
        }
        morphInfluencesMap = newInfluencesMap;
        morphInfluencesMap.addListener(this, morphKeys);
        logger.log(Level.INFO, "Delegating influences to ''{0}''", ancestor);
        return true;
    }

    /**
     * Constructor for internal use only.
     */
    public MorphingTriMesh() { }

    /**
     * Normal constructor.
     *
     * For local influence control, you should run <PRE><CODE>
     *     morphingTriMesh.addMorph(...);... // anytime before morph()ing
     *     morphingTriMesh.setMorphInfluencesMap(
     *             new ListenableStringFloatMap());
     *     morphingTriMesh.setMorphInfluences(...);
     *     morphingTriMesh.morph();
     * </CODE></PRE>
     * before attaching to a live scene.
     * For remote influence control, if you know there is an available
     * ancestor Node MorphInfluencesMapProvider, run
     * <PRE><CODE>
     *     morphingTriMesh.addMorph(...);... // anytime before morph()ing
     *     morphingTriMesh.delegateInfluences();
     *     morphingTriMesh.morph();
     * </CODE></PRE>
     * between attaching to the scene and rendering (like in a single update()
     * run).
     * <P>
     * If you want to delegate but aren't certain a capable ancestor is
     * at-hand, then run <PRE><CODE>
     *     morphingTriMesh.addMorph(...);... // anytime before morph()ing
     *     morphingTriMesh.setMorphInfluencesMap(
     *             new ListenableStringFloatMap());
     *     morphingTriMesh.setMorphInfluences(...);
     *     morphingTriMesh.delegateInfluences();
     *     morphingTriMesh.morph();
     * </CODE></PRE>
     * In this case, the local morphingInfluencesMap that you set up will be
     * overridden if the following delegateInfluences() call finds a provider.
     * </P>
     */
    public MorphingTriMesh(String name, TriMesh baseMorph) {
        super(name);
        this.baseMorph = baseMorph;
        initBase();
        logger.log(Level.FINE,
                "Base morph set for MorphingTriMesh '{0}'", getName());
    }

    /**
     * @param morphGeo  Must be a TriMesh instance.
     * @see MorphingGeometry#addMorph(String, Geometry)
     */
    public void addMorph(String morphKey, Geometry morphGeo) {
        if (baseMorph == null)
            throw new IllegalStateException(
                    "Base morph must be set before adding any others");
        // Validate that compatible with the base morph.
        if (!(morphGeo instanceof TriMesh))
            throw new IllegalArgumentException(
                    "This class can only handle TriMeshes as morphs");
        TriMesh morph = (TriMesh) morphGeo;
        if (baseMorph.getMode() != morph.getMode())
            throw new IllegalArgumentException(
                    "Trimesh " + morph.getName()
                    + " has mode which does not match the base morph: "
                    + morph.getMode() + " vs. " + baseMorph.getMode());
        if (morph.getVertexCount() > baseMorph.getVertexCount())
            throw new IllegalArgumentException(
                    "Trimesh " + morph.getName()
                    + " incompatible with base Trimesh "
                    + baseMorph.getName() + ".  Vertex counts "
                    + morph.getVertexCount() + " vs. "
                    + baseMorph.getVertexCount());
        if (morph.getMaxIndex() > baseMorph.getMaxIndex())
            throw new IllegalArgumentException(
                    "Trimesh " + morph.getName()
                    + " incompatible with base Trimesh "
                    + baseMorph.getName() + ".  Max indexes "
                    + morph.getMaxIndex() + " vs. "
                    + baseMorph.getMaxIndex());
        if ((morph.getNormalBuffer() == null
                && baseMorph.getNormalBuffer() != null)
                || (morph.getNormalBuffer() != null
                && baseMorph.getNormalBuffer() == null))
            throw new IllegalArgumentException(
                "Normal buffer conflicts with Base morph");
        if (morph.getNormalBuffer() != null
                && (morph.getNormalBuffer().capacity() >
                baseMorph.getNormalBuffer().capacity()))
            throw new IllegalArgumentException(
                    "Normal buffer count conflicts with Base morph.  "
                    + morph.getNormalBuffer().capacity() + " vs. "
                    + baseMorph.getNormalBuffer().capacity());
        if (!needExtrapolation && (
            morph.getVertexCount() < baseMorph.getVertexCount()
            || (morph.getNormalBuffer() != null
                && (morph.getNormalBuffer().capacity() <
                baseMorph.getNormalBuffer().capacity()))
        )) needExtrapolation = true;

        enforceEquality("fog coords",
                baseMorph.getFogBuffer(), morph.getFogBuffer());
        enforceEquality("tangent",
                baseMorph.getTangentBuffer(), morph.getTangentBuffer());
        enforceEquality("binormal",
                baseMorph.getBinormalBuffer(), morph.getBinormalBuffer());

        dirty = true;
        morphs.add(morph);
        morphKeys.add(morphKey);
        if (morphInfluencesMap != null)
            morphInfluencesMap.addListener(this, Arrays.asList(morphKey));
        logger.log(Level.FINE, "Added morph #{0} to MorphingTriMesh '{1}':  {2}",
                new Object[] {
                morphs.size(), getName(), morph.getName()});
    }

    protected void enforceEquality(
            String label, FloatBuffer fb1, FloatBuffer fb2) {
        if (fb1 == fb2) return;
        if (fb1 == null || fb2 == null)
            throw new IllegalArgumentException(
                    "Incompatible " + label + " values (one is null)");
        logger.fine("fb1 pre.  Pos/Rem = " + fb1.position() + " / " + fb1.remaining());
        // TODO:  Remove log statement once verify .equals() does not modify
        // position or remaining.
        if (!fb1.equals(fb2))
            throw new IllegalArgumentException(
                    "Incompatible " + label + " values");
        logger.fine("fb1 post.  Pos/Rem = " + fb1.position() + " / " + fb1.remaining());
        // TODO:  Remove log statement once verify .equals() does not modify
        // position or remaining.
    }

    /**
     * Replaces data other than merge data, by copying from the virgin
     * base morph TriMesh.
     */
    public void initBase() {
        if (baseMorph == null)
            throw new IllegalStateException(
                    "Can't initBase when no Geometry has been assigned");
        if (baseMorph.getMode() != TriMesh.Mode.Triangles)
            throw new IllegalStateException(
                    MorphingTriMesh.class.getName()
                    + " only supports Triangles mode at this time, not "
                    + baseMorph.getMode());
        if (baseMorph.getVertexBuffer() == null)
            throw new IllegalStateException("Base morph is just a shell");
        if (baseMorph.getVertexBuffer().capacity()
                != baseMorph.getVertexCount() * 3)
            throw new AssertionError(
                    "Sanity check failed.  "
                    + "Triangle mode base morph has screwey vertex count");
        // By virtue of having mode Triangles, if the normals buf is non-null
        // it should match the vert buffer in size
        if (baseMorph.getNormalBuffer() != null
                && baseMorph.getNormalBuffer().capacity()
                != baseMorph.getVertexBuffer().capacity())
            throw new AssertionError(
                "Triangle mode base morph has normal/vertex count mismatch");
        logger.fine("Initializing base...");
        dirty = true;
        TriMesh base = baseMorph; // Just for brevity below
        RenderState renderState;

        // Set scalars according to base morph
        //setName(base.getName());  Depend on Superclass to set/save name
        setMode(base.getMode());
        setDefaultColor(base.getDefaultColor());
        setLightState(base.getLightState());
        setCastsShadows(base.isCastsShadows());
        for (Controller c : base.getControllers()) addController(c);
        setLocalTranslation(new Vector3f(base.getLocalTranslation()));
        setLocalScale(new Vector3f(base.getLocalScale()));
        setLocalRotation(new Quaternion(base.getLocalRotation()));
        setZOrder(base.getZOrder(), false);
        setCullHint(base.getLocalCullHint());
        setTextureCombineMode(base.getLocalTextureCombineMode());
        setLightCombineMode(base.getLocalLightCombineMode());
        setRenderQueueMode(base.getRenderQueueMode());
        setNormalsMode(base.getLocalNormalsMode());
        setCollisionMask(base.getCollisionMask());
        setRenderQueueMode(base.getLocalRenderQueueMode());
        for (RenderState.StateType rsType : RenderState.StateType.values()) {
            clearRenderState(rsType);
            renderState = base.getRenderState(rsType);
            if (renderState != null) setRenderState(renderState);
        }
        setIndexBuffer(baseMorph.getIndexBuffer());
        setTextureCoords(baseMorph.getTextureCoords());
        setColorBuffer(baseMorph.getColorBuffer());
        setVBOInfo(baseMorph.getVBOInfo());

        setLocks(base.getLocks());
        logger.info("Base initialized successfully");
    }

    /**
     * @see MorphingGeometry#morph()
     */
    public void morph() {
        if (dirty) forceMorph();
    }

    /**
     * @see MorphingGeometry#forceMorph()
     */
    public void forceMorph() {
        if (needExtrapolation) extrapolateMorphBuffers();
        try {
        if (morphs.size() != morphKeys.size())
            throw new AssertionError(
                    "Morph dimensions != morph keys:  "
                    + morphs.size() + " vs. " + morphKeys.size());
        List<FloatBuffer> vertBuffers = new ArrayList<FloatBuffer>();
        List<FloatBuffer> normBuffers = new ArrayList<FloatBuffer>();
        List<Float> infList = new ArrayList<Float>();
        Float tmpF;
        TriMesh morph;
        if (morphInfluencesMap == null)
            throw new IllegalStateException(
                    "morphInfluencesMap must be non-null");
        for (int i = 0; i < morphKeys.size(); i++) {
            tmpF = morphInfluencesMap.get(morphKeys.get(i));
            if (tmpF == null)
                throw new IllegalStateException(
                        "Morph influence not set for required key: "
                        + morphKeys.get(i));
            if (tmpF.floatValue() < morphInfluenceThreshold) continue;
            infList.add(tmpF);
            morph = morphs.get(i);
            vertBuffers.add(getMorphVertBuffer(morph));
            normBuffers.add(getMorphNormBuffer(morph));
            // If more buffers need to be merged, add them here
        }
        float[] infs = new float[infList.size()];
        for (int i = 0; i < infs.length; i++)
            infs[i] = infList.get(i).floatValue();
        logger.log(Level.INFO, "Morphing ''{0}'' with influences:  {1}",
                new String[] {getName(), Arrays.toString(infs)});

        setVertexBuffer(mergeBuffers(
                baseMorph.getVertexBuffer(), vertBuffers, infs));
        setNormalBuffer(mergeBuffers(
                baseMorph.getNormalBuffer(), normBuffers, infs));

        //setModelBound(base.getModelBound()); // Regenereate a BV
        setHasDirtyVertices(true);  // necessary?
        // Need to scaleTextureCoordinates()?
        dirty = false;
        } catch (RuntimeException re) {
            if (autoMorph) {
                autoMorph = false;
                logger.warning("autoMorphing disabled.  "
                        + "Turn it back on after you fix your problem");
                throw re;
            }
        }
        SkinNode skinNode = getSkinNode();
        if (getSkinNode() != null) skinNode.regenInfluenceOffsets(this);
    }

    /**
     * @see MorphingGeometry#getSkinNode()
     */
    public SkinNode getSkinNode() {
        return (getParent() != null && getParent().getParent() != null
                && (getParent().getParent() instanceof SkinNode)
                && ((SkinNode) getParent().getParent()).hasSkinGeometry(
                        getName(), null))
                ? ((SkinNode) getParent().getParent()) : null;
    }

    /**
     * Assumes that influence.length == morphBuffers.size() - 1
     *
     * @return null if all the input buffers are null.
     */
    protected FloatBuffer mergeBuffers(FloatBuffer baseBuffer,
            List<FloatBuffer> morphBuffers, float[] influences) {
        for (int i = 0; i < morphBuffers.size(); i++) {
            if (baseBuffer == null && morphBuffers.get(i) != null)
                throw new IllegalStateException("Buffer mismatch (A)");
            if (baseBuffer != null && morphBuffers.get(i) == null)
                throw new IllegalStateException("Buffer mismatch (B)");
        }
        if (baseBuffer == null) return null;
        FloatBuffer outBuffer =
                BufferUtils.createFloatBuffer(baseBuffer.capacity());
        float f0, f;
        while (outBuffer.hasRemaining()) {
            f = f0 = baseBuffer.get();
            for (int i = 0; i < morphBuffers.size(); i++)
                f += influences[i] * (morphBuffers.get(i).get() - f0);
            outBuffer.put(f);
        }
        baseBuffer.flip();
        for (FloatBuffer fb : morphBuffers) fb.flip();
        outBuffer.flip();
        return outBuffer;
    }

    @SuppressWarnings("unchecked")
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.writeSavableArrayList(
                new ArrayList(morphs), "morphs", null);
        capsule.write(morphKeys.toArray(new String[0]), "morphKeys", null);
        capsule.write(baseMorph, "baseMorph", null);
        capsule.write(localMorphInfluencesMap, "morphInfluences", null);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        needExtrapolation = true;
        // I think we always need to do this, since we do not store the
        // extrapolated buffers.
        morphs = capsule.readSavableArrayList("morphs", null);
        String[] morphKeysArray = capsule.readStringArray("morphKeys", null);
        if (morphKeysArray != null) morphKeys = Arrays.asList(morphKeysArray);
        baseMorph = (TriMesh) capsule.readSavable("baseMorph", null);
        if (getVertexBuffer() == null) initBase();
            // A good clue that the MorphingTriMesh needs to be initialized.
        setMorphInfluencesMap((ListenableStringFloatMap)
                capsule.readSavable("morphInfluences", null));
        if (morphInfluencesMap != null) forceMorph();
        // If want to override the local map with delegation, will have to
        // manually change the map then re-morph.
        autoMorph = true;
    }

    public void floatChanged(StringFloatMap sfm) {
        dirty = true;
    }

    public void draw(Renderer r) {
        //if (dirty) System.err.print('d'); // TODO: REMOVE BEFORE PRODUCTION
        if (!dirty) super.draw(r);
    }

    /**
     * @see MorphingGeometry#setAutoMorph(boolean)
     */
    public void setAutoMorph(boolean autoMorph) {
        this.autoMorph = autoMorph;
    }

    protected boolean autoMorph;

    public void updateGeometricState(float time, boolean initiator) {
        if (autoMorph && dirty) forceMorph();
        super.updateGeometricState(time, initiator);
    }

    /**
     * This behavior is pretty invasive.
     * <P>
     * Trying to settle on a behavior which is very automated in normal
     * circumstances, but allows developers to override everything in a
     * consistent and reliable way.
     * </P>
     *
     * <b>Behavior here is subject to change</b>.
    public void setParent(Node parent) {
        super.setParent(parent);
        // Make an attempt to set up an influences map if we lack one.
        if (morphInfluencesMap != null) return;
        delegateInfluences();
        if (morphInfluencesMap != null) autoMorph = true;
    }
     */

    static final private float USNET = -1f;

    /**
     * Extrapolate component morph FloatBuffers to match the size of the
     * base Morph geometry.
     *
     * This will be called automatically by forceMorph(), but in many cases it
     * would be better to call this explicitly ahead-of-time rather than pausing
     * a frame cycle mid-game.
     */
    protected void extrapolateMorphBuffers() {
        needExtrapolation = false;

        FloatBuffer baseVertBuffer = baseMorph.getVertexBuffer();
        // First check if we really need to do anything
        for (TriMesh tm : morphs) {
            if (getMorphVertBuffer(tm).capacity() !=
                    baseVertBuffer.capacity()) {
                needExtrapolation = true;
                break;
            }
            if (baseMorph.getNormalBuffer() != null
                    && getMorphNormBuffer(tm).capacity() !=
                    baseMorph.getNormalBuffer().capacity()) {
                needExtrapolation = true;
                break;
            }
        }
        if (!needExtrapolation) return;
        needExtrapolation = false;

        if (extrapolatedVertBuf == null)
                extrapolatedVertBuf = new HashMap<TriMesh, FloatBuffer>();
        if (baseMorph.getNormalBuffer() != null && extrapolatedNormBuf == null)
                extrapolatedNormBuf = new HashMap<TriMesh, FloatBuffer>();

        FloatBuffer newVertBuffer, newNormBuffer;
        FloatBuffer inVertBuf, inNormBuf;
        Vector3f v3fBase;
        int closestPos;
        float closestDist;
        float dist;
        // addMorph has already enforced that either all morphs (incl. base)
        // either have or do not have (null) a norm buffer.
        for (TriMesh tm : morphs) {
            if (getMorphVertBuffer(tm).capacity() ==
                    baseVertBuffer.capacity()) continue;
            logger.info("Extrapolating morph Buffers for '"
                    + tm.getName() + "' to match the base Morph");
            newVertBuffer =
                    BufferUtils.createFloatBuffer(baseVertBuffer.capacity());
            extrapolatedVertBuf.put(tm, newVertBuffer);
            newNormBuffer = (baseMorph.getNormalBuffer() == null)
                          ? null
                          : BufferUtils.createFloatBuffer(
                                  baseVertBuffer.capacity());
            if (newNormBuffer != null)
                extrapolatedNormBuf.put(tm, newNormBuffer);

            inVertBuf = tm.getVertexBuffer();
            inNormBuf = tm.getNormalBuffer();
            baseVertBuffer.clear();
            while (baseVertBuffer.hasRemaining()) {
                v3fBase = new Vector3f(baseVertBuffer.get(),
                        baseVertBuffer.get(), baseVertBuffer.get());
                inVertBuf.clear();
                closestPos = -1;
                closestDist = Float.MAX_VALUE;
                while (inVertBuf.hasRemaining()) {
                    dist = v3fBase.distance(new Vector3f(inVertBuf.get(),
                            inVertBuf.get(), inVertBuf.get()));
                    if (dist < closestDist) {
                        closestPos = inVertBuf.position() - 3;
                        closestDist = dist;
                    }
                }
                if (closestDist == Float.MAX_VALUE)
                    throw new AssertionError(
                            "No closest morph vert found for a base vertex");
                newVertBuffer.put(inVertBuf.get(closestPos))
                        .put(inVertBuf.get(closestPos + 1))
                        .put(inVertBuf.get(closestPos + 2));
                if (newNormBuffer != null)
                    newNormBuffer.put(inNormBuf.get(closestPos))
                            .put(inNormBuf.get(closestPos + 1))
                            .put(inNormBuf.get(closestPos + 2));
            }
            inVertBuf.clear();
            baseVertBuffer.clear();
            newVertBuffer.clear();
            if (newNormBuffer != null) newNormBuffer.clear();
        }
    }
}
