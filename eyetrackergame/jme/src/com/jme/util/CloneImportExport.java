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

package com.jme.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

/**
 * A JMEImporter and JMEExporter that stores the capsule information locally in
 * a hash from Savable to info. This gives a local copy without any overhead of
 * streams, zipping or setup. Found to be slightly more performant. Note: Not
 * thread safe. Note: Ignored fields take precedence over shallow copy
 * specification Intended usage looks something like:
 *
 * <pre><code>
 *		CloneImportExport ie = new CloneImportExport();
 *		ie.saveClone(node);
 *		Node copy1 = (Node) ie.loadClone();
 *		Node copy2 = (Node) ie.loadClone();
 *		Node copy3 = (Node) ie.loadClone();
 * </code></pre>
 * 
 * @author kevin
 * @version $Id: CloneImportExport.java 4651 2009-09-01 21:51:36Z blaine.dev $
 */
public class CloneImportExport implements JMEExporter, JMEImporter {
    private static final Logger logger = Logger
            .getLogger(CloneImportExport.class.getName());

    /** The map of all the savables to the capsules they've popualted */
    private HashMap<Object, CloneCapsule> all = new HashMap<Object, CloneCapsule>();
    /**
     * The mapping from new savable copy to the old savable - used to look up
     * the old savable's capsule
     */
    private IdentityHashMap<Savable, Savable> newToOld = new IdentityHashMap<Savable, Savable>();
    /**
     * The mapping from the old savable to the new copy - used when looking up
     * references
     */
    private IdentityHashMap<Savable, Savable> oldToNew = new IdentityHashMap<Savable, Savable>();
    /** The class name of the root savable */
    private String className;
    /** True if we're reading at the moment */
    private boolean reading;
    /** The root savable that was requested to be cloned */
    private Savable root;
    /**
     * A list of fields that shouldn't be copied - the default value will be
     * returned
     */
    private ArrayList<String> ignoredFields = new ArrayList<String>();
    /**
     * A list of fields that are shallow copied, i.e they just get a reference
     * to the original
     */
    private ArrayList<String> shallowFields = new ArrayList<String>();

    /**
     * @see com.jme.util.export.JMEExporter#getCapsule(com.jme.util.export.Savable)
     */
    public CloneCapsule getCapsule(Savable object) {
        Savable key = object;
        if (reading) {
            key = newToOld.get(key);
        }
        CloneCapsule copy = all.get(key);

        if (copy == null) {
            if (reading) {
                return new CloneCapsule(object.getClass().getName());
                // throw new RuntimeException("No capsule stored for: "+key);
            } else {
                copy = new CloneCapsule(object.getClass().getName());
                all.put(object, copy);
            }
        }

        return copy;
    }

    /**
     * Reset the local loading state to allow another clone to be produced
     * unaffected by previous loads
     */
    private void resetLoadingState() {
        newToOld.clear();
        oldToNew.clear();
    }

    /**
     * Reset the local saving state to allow another clone to be made
     */
    private void resetSavingState() {
        resetLoadingState();
        all.clear();
        ignoredFields.clear();
        className = null;
        reading = false;
        root = null;
    }

    /**
     * Apply a complete configuration to the cloner process
     * 
     * @param config
     *            The configuration to apply
     */
    public void applyConfiguration(CloneConfiguration config) {
        addIgnoredFields(config.getIgnored());
        addShallowCopyFields(config.getShallow());
    }

    /**
     * Add a field to be copied by reference only, this may be useful to allow
     * copies to shared references to certain buffers within the copies
     * 
     * @param field
     *            The name of the field to shallow copy
     */
    public void addShadowCopyField(String field) {
        shallowFields.add(field);
    }

    /**
     * Add a list of fields to be copied by reference only, this may be useful
     * to allow copies to shared references to certain buffers within the copies
     * 
     * @param fields
     *            The name of the fields to shallow copy
     */
    public void addShallowCopyFields(ArrayList<String> fields) {
        shallowFields.addAll(fields);
    }

    /**
     * Add a field to be ignored during the cloning process. The default values
     * for these fields will be returned
     * 
     * @param field
     *            The name of field to be ignored
     */
    public void addIgnoredField(String field) {
        ignoredFields.add(field);
    }

    /**
     * Add a list of fields to be ignored during the clong process. The default
     * values will be returned when these fields are copied
     * 
     * @param fields
     *            The names of the fields to be ignored
     */
    public void addIgnoredFields(ArrayList<String> fields) {
        ignoredFields.addAll(fields);
    }

    /**
     * Save a single savable's state to the local store
     * 
     * @param object
     *            The saveable to be written to the local store
     * @return True if we managed to save it
     * @throws IOException
     *             Indicates a failure to write - note this shouldn't happen
     *             with local saves
     */
    private boolean save(Savable object) throws IOException {
        reading = false;
        if (object != null && all.get(object) == null) {
            object.write(this);
        }
        return true;
    }

    /**
     * Load a single savable (and it's children) from the local state
     * 
     * @return The loaded savable
     * @throws IOException
     *             Indicates a failure to read from a stream - note this
     *             shouldn't happen with local reads.
     */
    private Savable load() throws IOException {
        if (root == null) {
            throw new RuntimeException(
                    "You need to save something to the CloneImportExport before loading from it");
        }

        reading = true;
        try {
            Savable newp = (Savable) Class.forName(className).newInstance();
            newToOld.put(newp, root);
            oldToNew.put(root, newp);

            newp.read(this);

            resetLoadingState();
            return newp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save the specified savable into the local state to allow it to be cloned
     * later using <code>loadClone</code>
     * 
     * @param object
     *            The savable which we're cloning
     * @return True if the saveable was synced ok
     */
    public boolean saveClone(Savable object) {
        resetSavingState();

        try {
            className = object.getClass().getName();
            root = object;

            return save(object);
        } catch (IOException e) {
            // this becomes a runtime failure since there is no actual
            // IO going on and any failure indicates a coding mistake
            // not a file failure
            throw new RuntimeException(e);
        }
    }

    /**
     * @see com.jme.util.export.JMEExporter#save(com.jme.util.export.Savable,
     *      java.io.OutputStream)
     */
    public boolean save(Savable object, OutputStream f) throws IOException {
        return saveClone(object);
    }

    /**
     * @see com.jme.util.export.JMEExporter#save(com.jme.util.export.Savable,
     *      java.io.File)
     */
    public boolean save(Savable object, File f) throws IOException {
        return saveClone(object);
    }

    /**
     * Load the savable that is currently stored in this import/export utility.
     * This will create a clone of the savable previously saved using
     * <code>saveClone</code>
     * 
     * @return The cloned savable
     */
    public Savable loadClone() {
        try {
            return load();
        } catch (IOException e) {
            // this becomes a runtime failure since there is no actual
            // IO going on and any failure indicates a coding mistake
            // not a file failure
            throw new RuntimeException(e);
        }
    }

    /**
     * @see com.jme.util.export.JMEImporter#load(java.io.InputStream)
     */
    public Savable load(InputStream f) throws IOException {
        return load();
    }

    /**
     * @see com.jme.util.export.JMEImporter#load(java.net.URL)
     */
    public Savable load(URL f) throws IOException {
        return load();
    }

    /**
     * @see com.jme.util.export.JMEImporter#load(java.io.File)
     */
    public Savable load(File f) throws IOException {
        return load();
    }

    /**
     * Create a copy of the specified savable and store it away in the local
     * mapping tables. Note this does not populate the copy
     * 
     * @param original
     *            The original savable to be copied
     * @return The newly created savable
     */
    private Savable create(Savable original) {
        if (original == null) {
            return null;
        }
        try {
            Savable newp = (Savable) Class.forName(
                    original.getClass().getName()).newInstance();
            newToOld.put(newp, original);
            oldToNew.put(original, newp);

            return newp;
        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "create(Savable)", "Exception", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if we should ignore the specified field name
     * 
     * @param field
     *            The field name to check
     * @return True if the field value should be ignored
     */
    private boolean ignoreField(String field) {
        return ignoredFields.contains(field);
    }

    /**
     * Check whether a particular field should be shallow copied
     * 
     * @param field
     *            The field name to check
     * @return True if the field value should be shallow copied
     */
    private boolean shallowCopyField(String field) {
        return shallowFields.contains(field);
    }

    /**
     * A capsule storing the data written by the clone target. Note that all the
     * cloning is done on the reading - allowing multiple copies of the same
     * model to be created rather than having to re-save. Most values are stored
     * by reference to the original savable and then duplicated at read time.
     * 
     * @author kevin
     */
    private class CloneCapsule implements OutputCapsule, InputCapsule {
        /** The name of the class writing to this capsule */
        public String classname;
        /** The values that have been written by the savable */
        public HashMap<String, Object> values = new HashMap<String, Object>();

        /**
         * Create a new capsule based on a particular savable
         * 
         * @param classname
         *            The name of the class which is being saved - used later to
         *            recreate a copy.
         */
        public CloneCapsule(String classname) {
            this.classname = classname;
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(byte, java.lang.String,
         *      byte)
         */
        public void write(byte value, String name, byte defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(byte[],
         *      java.lang.String, byte[])
         */
        public void write(byte[] value, String name, byte[] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(byte[][],
         *      java.lang.String, byte[][])
         */
        public void write(byte[][] value, String name, byte[][] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(int, java.lang.String,
         *      int)
         */
        public void write(int value, String name, int defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(int[], java.lang.String,
         *      int[])
         */
        public void write(int[] value, String name, int[] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(int[][],
         *      java.lang.String, int[][])
         */
        public void write(int[][] value, String name, int[][] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(float, java.lang.String,
         *      float)
         */
        public void write(float value, String name, float defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(float[],
         *      java.lang.String, float[])
         */
        public void write(float[] value, String name, float[] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(float[][],
         *      java.lang.String, float[][])
         */
        public void write(float[][] value, String name, float[][] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(double,
         *      java.lang.String, double)
         */
        public void write(double value, String name, double defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(double[],
         *      java.lang.String, double[])
         */
        public void write(double[] value, String name, double[] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(double[][],
         *      java.lang.String, double[][])
         */
        public void write(double[][] value, String name, double[][] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(long, java.lang.String,
         *      long)
         */
        public void write(long value, String name, long defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(long[],
         *      java.lang.String, long[])
         */
        public void write(long[] value, String name, long[] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(long[][],
         *      java.lang.String, long[][])
         */
        public void write(long[][] value, String name, long[][] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(short, java.lang.String,
         *      short)
         */
        public void write(short value, String name, short defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(short[],
         *      java.lang.String, short[])
         */
        public void write(short[] value, String name, short[] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(short[][],
         *      java.lang.String, short[][])
         */
        public void write(short[][] value, String name, short[][] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(boolean,
         *      java.lang.String, boolean)
         */
        public void write(boolean value, String name, boolean defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(boolean[],
         *      java.lang.String, boolean[])
         */
        public void write(boolean[] value, String name, boolean[] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(boolean[][],
         *      java.lang.String, boolean[][])
         */
        public void write(boolean[][] value, String name, boolean[][] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(java.lang.String,
         *      java.lang.String, java.lang.String)
         */
        public void write(String value, String name, String defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(java.lang.String[],
         *      java.lang.String, java.lang.String[])
         */
        public void write(String[] value, String name, String[] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(java.lang.String[][],
         *      java.lang.String, java.lang.String[][])
         */
        public void write(String[][] value, String name, String[][] defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(java.util.BitSet,
         *      java.lang.String, java.util.BitSet)
         */
        public void write(BitSet value, String name, BitSet defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(com.jme.util.export.Savable,
         *      java.lang.String, com.jme.util.export.Savable)
         */
        public void write(Savable object, String name, Savable defVal)
                throws IOException {
            if (object == null) {
                return;
            }

            save(object);
            values.put(name, object);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(com.jme.util.export.Savable[],
         *      java.lang.String, com.jme.util.export.Savable[])
         */
        public void write(Savable[] objects, String name, Savable[] defVal)
                throws IOException {
            if (objects == null) {
                return;
            }

            for (int i = 0; i < objects.length; i++) {
                save(objects[i]);
            }
            values.put(name, objects);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(com.jme.util.export.Savable[][],
         *      java.lang.String, com.jme.util.export.Savable[][])
         */
        public void write(Savable[][] objects, String name, Savable[][] defVal)
                throws IOException {
            if (objects == null) {
                return;
            }

            for (int j = 0; j < objects[0].length; j++) {
                for (int i = 0; i < objects.length; i++) {
                    save(objects[i][j]);
                }
            }
            values.put(name, objects);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(java.nio.FloatBuffer,
         *      java.lang.String, java.nio.FloatBuffer)
         */
        public void write(FloatBuffer value, String name, FloatBuffer defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(java.nio.IntBuffer,
         *      java.lang.String, java.nio.IntBuffer)
         */
        public void write(IntBuffer value, String name, IntBuffer defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(java.nio.ByteBuffer,
         *      java.lang.String, java.nio.ByteBuffer)
         */
        public void write(ByteBuffer value, String name, ByteBuffer defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(java.nio.ShortBuffer,
         *      java.lang.String, java.nio.ShortBuffer)
         */
        public void write(ShortBuffer value, String name, ShortBuffer defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#writeFloatBufferArrayList(ArrayList,
         *      String, ArrayList)
         */
        public void writeFloatBufferArrayList(ArrayList<FloatBuffer> array,
                String name, ArrayList<FloatBuffer> defVal) throws IOException {
            values.put(name, array);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#writeByteBufferArrayList(ArrayList,
         *      String, ArrayList)
         */
        public void writeByteBufferArrayList(ArrayList<ByteBuffer> array,
                String name, ArrayList<ByteBuffer> defVal) throws IOException {
            values.put(name, array);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#writeSavableArrayList(ArrayList,
         *      String, ArrayList)
         */
        public void writeSavableArrayList(ArrayList array, String name,
                ArrayList defVal) throws IOException {
            if (array == null) {
                return;
            }

            for (int i = 0; i < array.size(); i++) {
                save((Savable) array.get(i));
            }
            values.put(name, array);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#writeSavableArrayListArray(ArrayList[],
         *      String, ArrayList[])
         */
        public void writeSavableArrayListArray(ArrayList[] array, String name,
                ArrayList[] defVal) throws IOException {
            if (array == null) {
                return;
            }

            for (int j = 0; j < array.length; j++) {
                for (int i = 0; i < array[j].size(); i++) {
                    save((Savable) array[j].get(i));
                }
            }
            values.put(name, array);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#writeSavableArrayListArray2D(ArrayList[][],
         *      String, ArrayList[][])
         */
        public void writeSavableArrayListArray2D(ArrayList[][] array,
                String name, ArrayList[][] defVal) throws IOException {
            if (array == null) {
                return;
            }

            for (int k = 0; k < array[0].length; k++) {
                for (int j = 0; j < array.length; j++) {
                    for (int i = 0; i < array[j][k].size(); i++) {
                        save((Savable) array[j][k].get(i));
                    }
                }
            }
            values.put(name, array);
        }

        /**
         * @see com.jme.util.export.OutputCapsule#write(Enum, String, Enum)
         */
        public void write(Enum value, String name, Enum defVal)
                throws IOException {
            values.put(name, value);
        }

        /**
         * @see com.jme.util.export.InputCapsule#readBitSet(String, BitSet)
         */
        public BitSet readBitSet(String name, BitSet defVal) throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }
            BitSet bitSetObj = (BitSet) values.get(name);
            if (bitSetObj == null) return defVal;
            return shallowCopyField(name)
                   ? bitSetObj
                   : (BitSet) bitSetObj.clone();
        }

        /**
         * @see com.jme.util.export.InputCapsule#readBoolean(String, boolean)
         */
        public boolean readBoolean(String name, boolean defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            Boolean boolObj = (Boolean) values.get(name);
            return (boolObj == null) ? defVal : boolObj.booleanValue();
        }

        /**
         * @see com.jme.util.export.InputCapsule#readBooleanArray(java.lang.String,
         *      boolean[])
         */
        public boolean[] readBooleanArray(String name, boolean[] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            boolean[] original = (boolean[]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            boolean[] copy = new boolean[original.length];

            for (int i = 0; i < copy.length; i++) {
                copy[i] = original[i];
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readBooleanArray2D(java.lang.String,
         *      boolean[][])
         */
        public boolean[][] readBooleanArray2D(String name, boolean[][] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            boolean[][] original = (boolean[][]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            boolean[][] copy = new boolean[original.length][original[0].length];

            for (int j = 0; j < copy[0].length; j++) {
                for (int i = 0; i < copy.length; i++) {
                    copy[i][j] = original[i][j];
                }
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readByte(java.lang.String,
         *      byte)
         */
        public byte readByte(String name, byte defVal) throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            Byte byteObj = (Byte) values.get(name);
            return (byteObj == null) ? defVal : byteObj.byteValue();
        }

        /**
         * @see com.jme.util.export.InputCapsule#readByteArray(java.lang.String,
         *      byte[])
         */
        public byte[] readByteArray(String name, byte[] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            byte[] original = (byte[]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            byte[] copy = new byte[original.length];

            for (int i = 0; i < copy.length; i++) {
                copy[i] = original[i];
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readByteArray2D(java.lang.String,
         *      byte[][])
         */
        public byte[][] readByteArray2D(String name, byte[][] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            byte[][] original = (byte[][]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            byte[][] copy = new byte[original.length][original[0].length];

            for (int j = 0; j < copy[0].length; j++) {
                for (int i = 0; i < copy.length; i++) {
                    copy[i][j] = original[i][j];
                }
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readByteBuffer(java.lang.String,
         *      java.nio.ByteBuffer)
         */
        public ByteBuffer readByteBuffer(String name, ByteBuffer defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            ByteBuffer buffer = (ByteBuffer) values.get(name);
            if (buffer == null) return defVal;
            if (shallowCopyField(name)) {
                return buffer;
            }

            return BufferUtils.clone(buffer);
        }

        /**
         * @see com.jme.util.export.InputCapsule#readDouble(java.lang.String,
         *      double)
         */
        public double readDouble(String name, double defVal) throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            Double doubleObj = (Double) values.get(name);
            return (doubleObj == null) ? defVal : doubleObj.doubleValue();
        }

        /**
         * @see com.jme.util.export.InputCapsule#readDoubleArray(java.lang.String,
         *      double[])
         */
        public double[] readDoubleArray(String name, double[] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            double[] original = (double[]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            double[] copy = new double[original.length];

            for (int i = 0; i < copy.length; i++) {
                copy[i] = original[i];
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readDoubleArray2D(java.lang.String,
         *      double[][])
         */
        public double[][] readDoubleArray2D(String name, double[][] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            double[][] original = (double[][]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            double[][] copy = new double[original.length][original[0].length];

            for (int j = 0; j < copy[0].length; j++) {
                for (int i = 0; i < copy.length; i++) {
                    copy[i][j] = original[i][j];
                }
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readFloat(java.lang.String,
         *      float)
         */
        public float readFloat(String name, float defVal) throws IOException {
            if (ignoreField(name) || values.get(name) == null) {
                return defVal;
            }

            return (Float) values.get(name);
        }

        /**
         * @see com.jme.util.export.InputCapsule#readFloatArray(java.lang.String,
         *      float[])
         */
        public float[] readFloatArray(String name, float[] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            float[] original = (float[]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            float[] copy = new float[original.length];

            for (int i = 0; i < copy.length; i++) {
                copy[i] = original[i];
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readFloatArray2D(java.lang.String,
         *      float[][])
         */
        public float[][] readFloatArray2D(String name, float[][] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            float[][] original = (float[][]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            float[][] copy = new float[original.length][original[0].length];

            for (int j = 0; j < copy[0].length; j++) {
                for (int i = 0; i < copy.length; i++) {
                    copy[i][j] = original[i][j];
                }
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readFloatBuffer(java.lang.String,
         *      java.nio.FloatBuffer)
         */
        public FloatBuffer readFloatBuffer(String name, FloatBuffer defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            FloatBuffer buffer = (FloatBuffer) values.get(name);
            if (buffer == null) return defVal;
            if (shallowCopyField(name)) {
                return buffer;
            }

            return BufferUtils.clone(buffer);
        }

        /**
         * @see com.jme.util.export.InputCapsule#readFloatBufferArrayList(String, ArrayList)
         */
        @SuppressWarnings("unchecked")
        public ArrayList<FloatBuffer> readFloatBufferArrayList(String name,
                ArrayList<FloatBuffer> defVal) throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            ArrayList original = (ArrayList) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            ArrayList copy = new ArrayList();

            for (int i = 0; i < original.size(); i++) {
                FloatBuffer clone = BufferUtils.clone((FloatBuffer) original
                        .get(i));
                copy.add(clone);
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readByteBufferArrayList(String, ArrayList)
         */
        @SuppressWarnings("unchecked")
        public ArrayList<ByteBuffer> readByteBufferArrayList(String name,
                ArrayList<ByteBuffer> defVal) throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            ArrayList original = (ArrayList) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            ArrayList copy = new ArrayList();

            for (int i = 0; i < original.size(); i++) {
                ByteBuffer clone = BufferUtils.clone((ByteBuffer) original
                        .get(i));
                copy.add(clone);
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readInt(java.lang.String, int)
         */
        public int readInt(String name, int defVal) throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            Integer intObj = (Integer) values.get(name);
            return (intObj == null) ? defVal : intObj.intValue();
        }

        /**
         * @see com.jme.util.export.InputCapsule#readIntArray(java.lang.String,
         *      int[])
         */
        public int[] readIntArray(String name, int[] defVal) throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            int[] original = (int[]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            int[] copy = new int[original.length];

            for (int i = 0; i < copy.length; i++) {
                copy[i] = original[i];
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readIntArray2D(java.lang.String,
         *      int[][])
         */
        public int[][] readIntArray2D(String name, int[][] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            int[][] original = (int[][]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            int[][] copy = new int[original.length][original[0].length];

            for (int j = 0; j < copy[0].length; j++) {
                for (int i = 0; i < copy.length; i++) {
                    copy[i][j] = original[i][j];
                }
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readIntBuffer(java.lang.String,
         *      java.nio.IntBuffer)
         */
        public IntBuffer readIntBuffer(String name, IntBuffer defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            IntBuffer buffer = (IntBuffer) values.get(name);
            if (buffer == null) return defVal;
            if (shallowCopyField(name)) {
                return buffer;
            }

            return BufferUtils.clone(buffer);
        }

        /**
         * @see com.jme.util.export.InputCapsule#readLong(java.lang.String,
         *      long)
         */
        public long readLong(String name, long defVal) throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            Long longObj = (Long) values.get(name);
            return (longObj == null) ? defVal : longObj.longValue();
        }

        /**
         * @see com.jme.util.export.InputCapsule#readLongArray(java.lang.String,
         *      long[])
         */
        public long[] readLongArray(String name, long[] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            long[] original = (long[]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            long[] copy = new long[original.length];

            for (int i = 0; i < copy.length; i++) {
                copy[i] = original[i];
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readLongArray2D(java.lang.String,
         *      long[][])
         */
        public long[][] readLongArray2D(String name, long[][] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            long[][] original = (long[][]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            long[][] copy = new long[original.length][original[0].length];

            for (int j = 0; j < copy[0].length; j++) {
                for (int i = 0; i < copy.length; i++) {
                    copy[i][j] = original[i][j];
                }
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readSavable(java.lang.String,
         *      com.jme.util.export.Savable)
         */
        public Savable readSavable(String name, Savable defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            Savable original = (Savable) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            Savable copy = oldToNew.get(original);
            if (copy == null) {
                copy = create(original);
                copy.read(CloneImportExport.this);
            }
            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readSavableArray(java.lang.String,
         *      com.jme.util.export.Savable[])
         */
        public Savable[] readSavableArray(String name, Savable[] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            Savable[] original = (Savable[]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            Savable[] copy = new Savable[original.length];

            for (int i = 0; i < copy.length; i++) {
                copy[i] = oldToNew.get(original[i]);
                if (copy[i] == null) {
                    copy[i] = create(original[i]);
                    if (copy[i] == null) {
                        continue;
                    }
                    copy[i].read(CloneImportExport.this);
                }
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readSavableArray2D(java.lang.String,
         *      com.jme.util.export.Savable[][])
         */
        public Savable[][] readSavableArray2D(String name, Savable[][] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            Savable[][] original = (Savable[][]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            Savable[][] copy = new Savable[original.length][original[0].length];

            for (int j = 0; j < copy[0].length; j++) {
                for (int i = 0; i < copy.length; i++) {
                    copy[i][j] = oldToNew.get(original[i][j]);
                    if (copy[i][j] == null) {
                        copy[i][j] = create(original[i][j]);
                        if (copy[i][j] == null) {
                            continue;
                        }
                        copy[i][j].read(CloneImportExport.this);
                    }
                }
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readSavableArrayList(java.lang.String,
         *      java.util.ArrayList)
         */
        @SuppressWarnings("unchecked")
        public ArrayList readSavableArrayList(String name, ArrayList defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            ArrayList original = (ArrayList) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            ArrayList copy = new ArrayList();

            for (int i = 0; i < original.size(); i++) {
                Savable c = oldToNew.get(original.get(i));
                if (c == null) {
                    c = create(((Savable) original.get(i)));
                    if (c == null) {
                        continue;
                    }
                    c.read(CloneImportExport.this);
                }
                copy.add(c);
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readSavableArrayListArray(java.lang.String,
         *      java.util.ArrayList[])
         */
        @SuppressWarnings("unchecked")
        public ArrayList[] readSavableArrayListArray(String name,
                ArrayList[] defVal) throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            ArrayList[] original = (ArrayList[]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            ArrayList[] copy = new ArrayList[original.length];

            ;
            for (int j = 0; j < original.length; j++) {
                copy[j] = new ArrayList();
                for (int i = 0; i < original[j].size(); i++) {
                    Savable c = oldToNew.get(original[j].get(i));
                    if (c == null) {
                        c = create(((Savable) original[j].get(i)));
                        if (c == null) {
                            continue;
                        }
                        c.read(CloneImportExport.this);
                    }
                    copy[j].add(c);
                }
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readSavableArrayListArray2D(java.lang.String,
         *      java.util.ArrayList[][])
         */
        @SuppressWarnings("unchecked")
        public ArrayList[][] readSavableArrayListArray2D(String name,
                ArrayList[][] defVal) throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            ArrayList[][] original = (ArrayList[][]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            ArrayList[][] copy = new ArrayList[original.length][original[0].length];

            for (int k = 0; k < original[0].length; k++) {
                for (int j = 0; j < original.length; j++) {
                    copy[j][k] = new ArrayList();
                    for (int i = 0; i < original[j][k].size(); i++) {
                        Savable c = oldToNew.get(original[j][k].get(i));
                        if (c == null) {
                            c = create(((Savable) original[j][k].get(i)));
                            if (c == null) {
                                continue;
                            }
                            c.read(CloneImportExport.this);
                        }
                        copy[j][k].add(c);
                    }
                }
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readShort(java.lang.String,
         *      short)
         */
        public short readShort(String name, short defVal) throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            Short shortObj = (Short) values.get(name);
            return (shortObj == null) ? defVal : shortObj.shortValue();
        }

        /**
         * @see com.jme.util.export.InputCapsule#readShortArray(java.lang.String,
         *      short[])
         */
        public short[] readShortArray(String name, short[] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            short[] original = (short[]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            short[] copy = new short[original.length];

            for (int i = 0; i < copy.length; i++) {
                copy[i] = original[i];
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readShortArray2D(java.lang.String,
         *      short[][])
         */
        public short[][] readShortArray2D(String name, short[][] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            short[][] original = (short[][]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            short[][] copy = new short[original.length][original[0].length];

            for (int j = 0; j < copy[0].length; j++) {
                for (int i = 0; i < copy.length; i++) {
                    copy[i][j] = original[i][j];
                }
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readShortBuffer(java.lang.String,
         *      java.nio.ShortBuffer)
         */
        public ShortBuffer readShortBuffer(String name, ShortBuffer defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            ShortBuffer buffer = (ShortBuffer) values.get(name);
            if (buffer == null) return defVal;
            if (shallowCopyField(name)) {
                return buffer;
            }

            return BufferUtils.clone(buffer);
        }

        /**
         * @see com.jme.util.export.InputCapsule#readString(java.lang.String,
         *      java.lang.String)
         */
        public String readString(String name, String defVal) throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            String stringObj = (String) values.get(name);
            return (stringObj == null) ? defVal : stringObj;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readStringArray(java.lang.String,
         *      java.lang.String[])
         */
        public String[] readStringArray(String name, String[] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            String[] original = (String[]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            String[] copy = new String[original.length];

            for (int i = 0; i < copy.length; i++) {
                copy[i] = original[i];
            }

            return copy;
        }

        /**
         * @see com.jme.util.export.InputCapsule#readStringArray2D(java.lang.String,
         *      java.lang.String[][])
         */
        public String[][] readStringArray2D(String name, String[][] defVal)
                throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            String[][] original = (String[][]) values.get(name);
            if (original == null) {
                return defVal;
            }
            if (shallowCopyField(name)) {
                return original;
            }

            String[][] copy = new String[original.length][original[0].length];

            for (int j = 0; j < copy[0].length; j++) {
                for (int i = 0; i < copy.length; i++) {
                    copy[i][j] = original[i][j];
                }
            }

            return copy;
        }

        @SuppressWarnings("unchecked")
        public <T extends Enum<T>> T readEnum(String name, Class<T> enumType,
                T defVal) throws IOException {
            if (ignoreField(name)) {
                return defVal;
            }

            T enumObj = (T) values.get(name);
            return (enumObj == null) ? defVal : enumObj;
        }

        public void writeSavableMap(
                Map<? extends Savable, ? extends Savable> map, String name,
                Map<? extends Savable, ? extends Savable> defVal)
                throws IOException {
            // TODO Auto-generated method stub

        }

        public Map<? extends Savable, ? extends Savable> readSavableMap(
                String name, Map<? extends Savable, ? extends Savable> defVal)
                throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        public void writeStringSavableMap(Map<String, ? extends Savable> map,
                String name, Map<String, ? extends Savable> defVal)
                throws IOException {
            // TODO Auto-generated method stub

        }

        public Map<String, ? extends Savable> readStringSavableMap(String name,
                Map<String, ? extends Savable> defVal) throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
