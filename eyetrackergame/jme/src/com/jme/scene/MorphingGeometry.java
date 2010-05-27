package com.jme.scene;

import java.util.Map;

import com.jme.util.export.ListenableStringFloatMap;
import com.jme.scene.Geometry;
import com.jme.animation.SkinNode;

/**
 * A derived Geometry that generates its data from a set of component
 * Geometries (morphs) which are not attached to any scene.
 * <P>
 * <b>Each morph Geometry must have the same quantity of vertexes (and normals
 *  and vertex colors).</b>  Most other data, including the vertex indexes,
 *  are taken from the Base Morph, ignoring these data from the other morphs.
 * <P> </P>
 * Data which is not morphed comes directly from the first (base) morph
 * Geometry.
 * <P> </P>
 * The base skin is modified by each additional skin morph in order.
 * </P>
 *
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 */
public interface MorphingGeometry
        extends ListenableStringFloatMap.FloatListener {
    /**
     * Add a component Geometry morph with same quantity of vertexes as the
     * base morph.
     * Do not use this method to set the base morph.
     *
     * @param morphKey  Key in the influences map for the influence for this
     *                  morph.
     */
    public void addMorph(String morphKey, Geometry mesh);

    /**
     * Use setMorphInfluences if you want to change multiple floats, to avoid
     * unnecessary listener callbacks.
     *
     * @see #setMorphInfluences(Map<? extends String, ? extends Float)
     */
    public void setSingleMorphInfluence(String morphKey, float influence);

    /**
     * Assign morph influence values.
     */
    public void setMorphInfluences(Map<? extends String, ? extends Float> m);

    /**
     * If we have an influences map before you invoke this method (either local
     * or remote), it will be retained unless we successfully find delegate to
     * another (in which case 'yes' will be returned).
     *
     * @return true if we successfully fetched a new influences map from a
     * provider.
     */
    public boolean delegateInfluences();

    /**
     * Set a local morph influences map.
     * If there were an influences map in use before (local or remote), it
     * will be removed or disassociated-from first.
     */
    public void setMorphInfluencesMap(ListenableStringFloatMap m);

    /**
     * @return The morph influences map controlling the MorphingGeometry.
     *         It may be a non-local (delegated) influences map.
     */
    public ListenableStringFloatMap getMorphInfluencesMap();

    /**
     * Morphs (verb) if any of the morphs (noun) or any of the morph
     * influence values have changed.
     *
     * This method should only be called from an update thread.
     */
    public void morph();

    /**
     * Unconditionally merges the relevant floats from FloabBuffers of the
     * component morphs.
     *
     * This method should only be called from an update thread.
     */
    public void forceMorph();

    /**
     * Causes morph merges to automatically occur as needed during update loops.
     */
    public void setAutoMorph(boolean autoMorph);

    /**
     * @return Grandparent SkinNode IFF this MorphingGeometry is a currently-
     *         attached Skin geometry.
     */
    public SkinNode getSkinNode(); 

    /**
     * Morphs with corresponding influence values below this threshold will
     * have no effect on the MorphingGeometry.
     */
    public void setMorphInfluenceThreshold(float morphInfluenceThreshold);

    /**
     * @see #setMorphInfluenceThreshold(float)
     */
    public float getMorphInfluenceThreshold();
}
