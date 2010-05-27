package com.jme.scene;

import java.util.Map;

import com.jme.util.export.ListenableStringFloatMap;

/**
 * An item that shares a ListenableStringFloatMap.
 */
public interface MorphInfluencesMapProvider {
    /**
     * @return The morph influences map controlled by this Provider.
     */
    public ListenableStringFloatMap getMorphInfluencesMap();
}
