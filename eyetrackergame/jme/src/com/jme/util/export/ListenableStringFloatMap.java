package com.jme.util.export;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A StringFloatMap which can be listened to in order to get notification when
 * specified values in the map are modified.
 */
public class ListenableStringFloatMap extends StringFloatMap {
    /*
     * If this didn't need to be Savable, it would be very easy to generify it
     * for use with any types instead of just Floats.
     */
    static final long serialVersionUID = 4906428202001959046L;
    private static final Logger logger =
            Logger.getLogger(ListenableStringFloatMap.class.getName());

    public interface FloatListener {
        public void floatChanged(StringFloatMap stringFloatMap);
        /* TODO: Learn how to make the passed stringFloatMap immutable.
         * Can I make a deep "copy" of the internal Map and make it final?
         */
    }

    public String listenerReport() {
        return changeListeners.toString();
    }

    public ListenableStringFloatMap() {
        super();
    }

    public ListenableStringFloatMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ListenableStringFloatMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ListenableStringFloatMap(Map<? extends String, ? extends Float> m) {
        super(m);
    }

    protected Map<String, Set<FloatListener>> changeListeners
            = new HashMap<String, Set<FloatListener>>();

    /**
     * IMPORTANT:  MAKE SURE TO CALL removeListener() WHEN YOU ARE FINISHED.
     * Otherwise you will cause a memory leak when your instance is not
     * garbage collected.
     */
    public void addListener(
            FloatListener changeListener, Collection<String> keys) {
        for (String key : keys) {
            if (!changeListeners.containsKey(key))
                changeListeners.put(key, new HashSet<FloatListener>());
            changeListeners.get(key).add(changeListener);
        }
    }

    public void removeListener(FloatListener changeListener) {
        for (Set<FloatListener> listenerBatch : changeListeners.values())
            listenerBatch.remove(changeListener);
        cullListenerSets();
    }

    protected void cullListenerSets() {
        Set<String> zapKeys = new HashSet<String>();
        for (Map.Entry<String, Set<FloatListener>> e
                : changeListeners.entrySet())
            if (e.getValue().size() < 1) zapKeys.add(e.getKey());
        for (String key : zapKeys) super.remove(key);
    }

    protected Set<FloatListener> getListeners() {
        Set<FloatListener> allListeners = new HashSet<FloatListener>();
        for (Set<FloatListener> listenerBatch : changeListeners.values())
            allListeners.addAll(listenerBatch);
        return allListeners;
    }

    public int getListenerCount() {
        return getListeners().size();
    }

    public void clear() {
        /* Consider what to do if there is a listener for a key that is not
         * mapped. */
        super.clear();
        for (FloatListener listener : getListeners()) notifyListener(listener);
    }

    protected void notifyListener(FloatListener listener) {
        /* TODO: Learn how to make the passed stringFloatMap immutable.
         * Can I make a deep "copy" of the internal Map and make it final?
         */
        listener.floatChanged(new StringFloatMap(this));
    }

    /**
     * IMPORTANT:  This method will notify the caller if the caller is
     * listening.  If you don't want that, then wrap your putAll() call inside
     * of removeListener and addListener calls.
     */
    public void putAll(Map<? extends String, ? extends Float> newMappings) {
        for (Map.Entry<? extends String, ? extends Float> e
                : newMappings.entrySet())
            super.put(e.getKey(), e.getValue());
        notifyAbout(newMappings.keySet());
    }

    protected void notifyAbout(Collection<? extends String> keys) {
        /* This is the principal notification method */
        // An enhancement would be to check put values and skip notifications
        // if the value is not changing.
        Set<FloatListener> notifyees = new HashSet<FloatListener>();
        for (String key : keys)
            if (changeListeners.containsKey(key))
                for (FloatListener listener : changeListeners.get(key))
                    notifyees.add(listener);
        for (FloatListener listener : notifyees)
            logger.log(Level.FINEST, "Notifying about {0}:  => {1}",
                    new String[] {keys.toString(), listener.toString()});
        for (FloatListener listener : notifyees) notifyListener(listener);
    }
    
    /*
     * Important:  DO NOT USE THIS METHOD REPEATEDLY TO ADD OR CHANGE MULTIPLE
     * VALUES.
     * Use putAll() for that, to avoid performance costs of unnecessary
     * listener callbacks.
     */
    public Float put(String key, Float f) {
        Float oldVal = get(key);
        putAll(Collections.singletonMap(key, f));
        return oldVal;
    }

    public Float remove(Object key) {
        /* TODO: Add a removeAll() method, for same reason we prefer addAll. */

        Float oldVal = super.remove(key);
        if (changeListeners.containsKey(key))
            for (FloatListener listener : changeListeners.get(key))
                notifyListener(listener);
        return oldVal;
    }
}
