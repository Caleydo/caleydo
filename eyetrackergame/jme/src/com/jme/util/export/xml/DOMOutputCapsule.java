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

package com.jme.util.export.xml;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.jme.util.export.JMEExporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * Part of the jME XML IO system as introduced in the google code jmexml project.
 * 
 * @author Kai Rabien (hevee) - original author of the code.google.com jmexml project
 * @author Doug Daniels (dougnukem) - adjustments for jME 2.0 and Java 1.5
 */
public class DOMOutputCapsule implements OutputCapsule {

    private static final String dataAttributeName = "data";
    private Document doc;
    private Element currentElement;
    private JMEExporter exporter;
    private Map<Savable, Element> writtenSavables = new IdentityHashMap<Savable, Element>();

    public DOMOutputCapsule(Document doc, JMEExporter exporter) {
        this.doc = doc;
        this.exporter = exporter;
        currentElement = null;
    }

    public Document getDoc() {
        return doc;
    }

    /**
     * appends a new Element with the given name to currentElement, sets 
     * currentElement to be new Element, and returns the new Element as well
     */
    private Element appendElement(String name) {
        Element ret = null;
            ret = doc.createElement(name);
        if (currentElement == null) {
            doc.appendChild(ret);
        } else {
            currentElement.appendChild(ret);
        }
        currentElement = ret;
        return ret;
    }

    private static String encodeString(String s) {
        if (s == null) {
            return null;
        }
        s =     s.replaceAll("\\&", "&amp;")
                 .replaceAll("\\\"", "&quot;")
                 .replaceAll("\\<", "&lt;");
        return s;
    }

    public void write(byte value, String name, byte defVal) throws IOException {
        if (value == defVal) {
            return;
        }
        currentElement.setAttribute(name, String.valueOf(value));
    }

    public void write(byte[] value, String name, byte[] defVal) throws IOException {
        StringBuilder buf = new StringBuilder();
        if (value == null) {
            value = defVal;
        }
        for (byte b : value) {
            buf.append(b);
            buf.append(" ");
        }
        //remove last space
        buf.setLength(buf.length() - 1);

        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.length));
        el.setAttribute(dataAttributeName, buf.toString());
        currentElement = (Element) currentElement.getParentNode();
    }

    public void write(byte[][] value, String name, byte[][] defVal) throws IOException {
        StringBuilder buf = new StringBuilder();
        if (value == null) {
            value = defVal;
        }
        for (byte[] bs : value) {
            for (byte b : bs) {
                buf.append(b);
                buf.append(" ");
            }
            buf.append(" ");
        }
        //remove last spaces
        buf.setLength(buf.length() - 2);

        Element el = appendElement(name);
        el.setAttribute("size_outer", String.valueOf(value.length));
        el.setAttribute("size_inner", String.valueOf(value[0].length));
        el.setAttribute(dataAttributeName, buf.toString());
        currentElement = (Element) currentElement.getParentNode();
    }

    public void write(int value, String name, int defVal) throws IOException {
        if (value == defVal) {
            return;
        }
        currentElement.setAttribute(name, String.valueOf(value));
    }

    public void write(int[] value, String name, int[] defVal) throws IOException {
        StringBuilder buf = new StringBuilder();
        if (value == null) { return; }
        if (Arrays.equals(value, defVal)) { return; }
        
        for (int b : value) {
            buf.append(b);
            buf.append(" ");
        }
        //remove last space
        buf.setLength(Math.max(0, buf.length() - 1));

        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.length));
        el.setAttribute(dataAttributeName, buf.toString());
        currentElement = (Element) currentElement.getParentNode();
    }

    public void write(int[][] value, String name, int[][] defVal) throws IOException {        
        if (value == null) return;
        if(Arrays.deepEquals(value, defVal)) return;
        
        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.length));        
        
        for (int i=0; i<value.length; i++) {
        	int[] array = value[i];        	
        	write(array, "array_"+i, defVal==null?null:defVal[i]);
        }
        currentElement = (Element) el.getParentNode();
    }

    public void write(float value, String name, float defVal) throws IOException {
        if (value == defVal) {
            return;
        }
        currentElement.setAttribute(name, String.valueOf(value));
    }

    public void write(float[] value, String name, float[] defVal) throws IOException {
        StringBuilder buf = new StringBuilder();
        if (value == null) {
            value = defVal;
        }
        if (value != null) {
            for (float b : value) {
                buf.append(b);
                buf.append(" ");
            }
            //remove last space
            buf.setLength(buf.length() - 1);
        }

        Element el = appendElement(name);
        el.setAttribute("size", value == null ? "0" : String.valueOf(value.length));
        el.setAttribute(dataAttributeName, buf.toString());
        currentElement = (Element) currentElement.getParentNode();
    }

    public void write(float[][] value, String name, float[][] defVal) throws IOException {
        StringBuilder buf = new StringBuilder();
        if (value == null) return;
        if(Arrays.deepEquals(value, defVal)) return;
        
        for (float[] bs : value) {
            for(float b : bs){
                buf.append(b);
                buf.append(" ");
            }
        }
        //remove last space
        buf.setLength(buf.length() - 1);

        Element el = appendElement(name);
        el.setAttribute("size_outer", String.valueOf(value.length));
        el.setAttribute("size_inner", String.valueOf(value[0].length));
        el.setAttribute(dataAttributeName, buf.toString());
        currentElement = (Element) currentElement.getParentNode();
    }

    public void write(double value, String name, double defVal) throws IOException {
        if (value == defVal) {
            return;
        }
        currentElement.setAttribute(name, String.valueOf(value));
    }

    public void write(double[] value, String name, double[] defVal) throws IOException {
        StringBuilder buf = new StringBuilder();
        if (value == null) {
            value = defVal;
        }
        for (double b : value) {
            buf.append(b);
            buf.append(" ");
        }
        //remove last space
        buf.setLength(buf.length() - 1);

        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.length));
        el.setAttribute(dataAttributeName, buf.toString());
        currentElement = (Element) currentElement.getParentNode();
    }

    public void write(double[][] value, String name, double[][] defVal) throws IOException {    
            if (value == null) return;
            if(Arrays.deepEquals(value, defVal)) return;
            
            Element el = appendElement(name);
            el.setAttribute("size", String.valueOf(value.length));        
            
            for (int i=0; i<value.length; i++) {
            	double[] array = value[i];        	
            	write(array, "array_"+i, defVal==null?null:defVal[i]);
            }
            currentElement = (Element) el.getParentNode();
    }

    public void write(long value, String name, long defVal) throws IOException {
        if (value == defVal) {
            return;
        }
        currentElement.setAttribute(name, String.valueOf(value));
    }

    public void write(long[] value, String name, long[] defVal) throws IOException {
        StringBuilder buf = new StringBuilder();
        if (value == null) {
            value = defVal;
        }
        for (long b : value) {
            buf.append(b);
            buf.append(" ");
        }
        //remove last space
        buf.setLength(buf.length() - 1);

        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.length));
        el.setAttribute(dataAttributeName, buf.toString());
        currentElement = (Element) currentElement.getParentNode();
    }

    public void write(long[][] value, String name, long[][] defVal) throws IOException {
        if (value == null) return;
        if(Arrays.deepEquals(value, defVal)) return;
        
        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.length));        
        
        for (int i=0; i<value.length; i++) {
        	long[] array = value[i];        	
        	write(array, "array_"+i, defVal==null?null:defVal[i]);
        }
        currentElement = (Element) el.getParentNode();
    }

    public void write(short value, String name, short defVal) throws IOException {
        if (value == defVal) {
            return;
        }
        currentElement.setAttribute(name, String.valueOf(value));
    }

    public void write(short[] value, String name, short[] defVal) throws IOException {
        StringBuilder buf = new StringBuilder();
        if (value == null) {
            value = defVal;
        }
        for (short b : value) {
            buf.append(b);
            buf.append(" ");
        }
        //remove last space
        buf.setLength(buf.length() - 1);

        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.length));
        el.setAttribute(dataAttributeName, buf.toString());
        currentElement = (Element) currentElement.getParentNode();
    }

    public void write(short[][] value, String name, short[][] defVal) throws IOException {
        if (value == null) return;
        if(Arrays.deepEquals(value, defVal)) return;
        
        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.length));        
        
        for (int i=0; i<value.length; i++) {
        	short[] array = value[i];        	
        	write(array, "array_"+i, defVal==null?null:defVal[i]);
        }
        currentElement = (Element) el.getParentNode();
    }

    public void write(boolean value, String name, boolean defVal) throws IOException {
        if (value == defVal) {
            return;
        }
        currentElement.setAttribute(name, String.valueOf(value));
    }

    public void write(boolean[] value, String name, boolean[] defVal) throws IOException {
        StringBuilder buf = new StringBuilder();
        if (value == null) {
            value = defVal;
        }
        for (boolean b : value) {
            buf.append(b);
            buf.append(" ");
        }
        //remove last space
        buf.setLength(Math.max(0, buf.length() - 1));

        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.length));
        el.setAttribute(dataAttributeName, buf.toString());
        currentElement = (Element) currentElement.getParentNode();
    }

    public void write(boolean[][] value, String name, boolean[][] defVal) throws IOException {
        if (value == null) return;
        if(Arrays.deepEquals(value, defVal)) return;
        
        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.length));        
        
        for (int i=0; i<value.length; i++) {
        	boolean[] array = value[i];        	
        	write(array, "array_"+i, defVal==null?null:defVal[i]);
        }
        currentElement = (Element) el.getParentNode();
    }

    public void write(String value, String name, String defVal) throws IOException {
        if (value == null || value.equals(defVal)) {
            return;
        }
        currentElement.setAttribute(name, encodeString(value));
    }

    public void write(String[] value, String name, String[] defVal) throws IOException {
        Element el = appendElement(name);
        
        if (value == null) {
            value = defVal;
        }

        el.setAttribute("size", String.valueOf(value.length));
        
        for (int i=0; i<value.length; i++) {
        	String b = value[i];
        	appendElement("String_"+i);
            String val = encodeString(b);
            currentElement.setAttribute("value", val);
            currentElement = el;
        }
        currentElement = (Element) currentElement.getParentNode();
    }

    public void write(String[][] value, String name, String[][] defVal) throws IOException {
        if (value == null) return;
        if(Arrays.deepEquals(value, defVal)) return;
        
        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.length));        
        
        for (int i=0; i<value.length; i++) {
        	String[] array = value[i];        	
        	write(array, "array_"+i, defVal==null?null:defVal[i]);
        }
        currentElement = (Element) el.getParentNode();
    }

    public void write(BitSet value, String name, BitSet defVal) throws IOException {
        if (value == null || value.equals(defVal)) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        for (int i = value.nextSetBit(0); i >= 0; i = value.nextSetBit(i + 1)) {
            buf.append(i);
            buf.append(" ");
        }
        buf.setLength(Math.max(0, buf.length() - 1));
        currentElement.setAttribute(name, buf.toString());

    }

    public void write(Savable object, String name, Savable defVal) throws IOException {
        if (object == null) {
            return;
        }
        if (object.equals(defVal)) {
            return;
        }

        Element old = currentElement;
        Element el = writtenSavables.get(object);
        
        String className = null;
        if(!object.getClassTag().getName().equals(name)){
            className = object.getClassTag().getName();
        }
        try {
            doc.createElement(name);
        } catch (DOMException e) {
            // Ridiculous fallback behavior.
            // Would be far better to throw than to totally disregard the
            // specified "name" and write a class name instead!
            // (Besides the fact we are clobbering the managed .getClassTag()).
            name = "Object";
            className = object.getClass().getName();
        }

        if (el != null) {
            String refID = el.getAttribute("reference_ID");
            if (refID.length() == 0) {
                refID = object.getClassTag().getName() + "@" + object.hashCode();
                el.setAttribute("reference_ID", refID);
            }
            el = appendElement(name);
            el.setAttribute("ref", refID);
        } else {
            el = appendElement(name);
            writtenSavables.put(object, el);
            object.write(exporter);
        }
        if(className != null){
            el.setAttribute("class", className);
        }

        currentElement = old;
    }

    public void write(Savable[] objects, String name, Savable[] defVal) throws IOException {
        if (objects == null) {
            return;
        }
        if (Arrays.equals(objects, defVal)) {
            return;
        }

        Element old = currentElement;
        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(objects.length));
        for (int i = 0; i < objects.length; i++) {
            Savable o = objects[i];
            if(o == null){
                //renderStateList has special loading code, so we can leave out the null values
                if(!name.equals("renderStateList")){
                Element before = currentElement;
                appendElement("null");
                currentElement = before;
                }
            }else{
                write(o, o.getClassTag().getName(), null);
            }
        }
        currentElement = old;
    }

    public void write(Savable[][] value, String name, Savable[][] defVal) throws IOException {
        if (value == null) return;
        if(Arrays.deepEquals(value, defVal)) return;
        
        Element el = appendElement(name);
        el.setAttribute("size_outer", String.valueOf(value.length));
        el.setAttribute("size_inner", String.valueOf(value[0].length));
        for (Savable[] bs : value) {
            for(Savable b : bs){
                write(b, b.getClassTag().getSimpleName(), null);
            }
        }
        currentElement = (Element) currentElement.getParentNode();
    }

    public void writeSavableArrayList(ArrayList array, String name, ArrayList defVal) throws IOException {
        if (array == null) {
            return;
        }
        if (array.equals(defVal)) {
            return;
        }
        Element old = currentElement;
        Element el = appendElement(name);
        currentElement = el;
        el.setAttribute(XMLExporter.ATTRIBUTE_SIZE, String.valueOf(array.size()));
        for (Object o : array) {
        	if(o == null) {
        		continue;
        	}
        	else if (o instanceof Savable) {
                Savable s = (Savable) o;
                write(s, s.getClassTag().getName(), null);
            } else {
                throw new ClassCastException("Not a Savable instance: " + o);
            }
        }
        currentElement = old;
    }

    public void writeSavableArrayListArray(ArrayList[] objects, String name, ArrayList[] defVal) throws IOException {
        if (objects == null) {return;}
        if (Arrays.equals(objects, defVal)) {return;}

        Element old = currentElement;
        Element el = appendElement(name);
        el.setAttribute(XMLExporter.ATTRIBUTE_SIZE, String.valueOf(objects.length));
        for (int i = 0; i < objects.length; i++) {
            ArrayList o = objects[i];
            if(o == null){
                Element before = currentElement;
                appendElement("null");
                currentElement = before;
            }else{
                StringBuilder buf = new StringBuilder("SavableArrayList_");
                buf.append(i);
                writeSavableArrayList(o, buf.toString(), null);
            }
        }
        currentElement = old;
    }

    public void writeSavableArrayListArray2D(ArrayList[][] value, String name, ArrayList[][] defVal) throws IOException {
        if (value == null) return;
        if(Arrays.deepEquals(value, defVal)) return;
        
        Element el = appendElement(name);
        int size = value.length;
        el.setAttribute(XMLExporter.ATTRIBUTE_SIZE, String.valueOf(size));
        
        for (int i=0; i< size; i++) {
            ArrayList[] vi = value[i];
            writeSavableArrayListArray(vi, "SavableArrayListArray_"+i, null);
        }
        currentElement = (Element) el.getParentNode();
    }

    public void writeFloatBufferArrayList(ArrayList<FloatBuffer> array, String name, ArrayList<FloatBuffer> defVal) throws IOException {
        if (array == null) {
            return;
        }
        if (array.equals(defVal)) {
            return;
        }
        Element el = appendElement(name);
        el.setAttribute(XMLExporter.ATTRIBUTE_SIZE, String.valueOf(array.size()));
        for (FloatBuffer o : array) {
            write(o, XMLExporter.ELEMENT_FLOATBUFFER, null);
        }
        currentElement = (Element) el.getParentNode();
    }

    public void writeSavableMap(Map<? extends Savable, ? extends Savable> map, String name, Map<? extends Savable, ? extends Savable> defVal) throws IOException {
    	if (map == null) {
            return;
        }
        if (map.equals(defVal)) {
            return;
        }
		Element stringMap = appendElement(name);				
		
		Iterator<? extends Savable> keyIterator = map.keySet().iterator();
		while(keyIterator.hasNext()) {
			Savable key = keyIterator.next();
			Element mapEntry = appendElement(XMLExporter.ELEMENT_MAPENTRY);	
			write(key, XMLExporter.ELEMENT_KEY, null);						
			Savable value = map.get(key);
			write(value, XMLExporter.ELEMENT_VALUE, null);
			currentElement = stringMap;
		}		
		
		currentElement = (Element) stringMap.getParentNode();
    }

    public void writeStringSavableMap(Map<String, ? extends Savable> map, String name, Map<String, ? extends Savable> defVal) throws IOException {
        if (map == null) {
            return;
        }
        if (map.equals(defVal)) {
            return;
        }
		Element stringMap = appendElement(name);				
		
		Iterator<String> keyIterator = map.keySet().iterator();
		while(keyIterator.hasNext()) {
			String key = keyIterator.next();
			Element mapEntry = appendElement("MapEntry");	
			mapEntry.setAttribute("key", key);
			Savable s = map.get(key);
			write(s, "Savable", null);
			currentElement = stringMap;
		}		
		
		currentElement = (Element) stringMap.getParentNode();
    }

    public void write(FloatBuffer value, String name, FloatBuffer defVal) throws IOException {
        if (value == null) {
            return;
        }

        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.limit()));
        StringBuilder buf = new StringBuilder();
        int pos = value.position();
        value.rewind();
        int ctr = 0;
        while (value.hasRemaining()) {
            ctr++;
            buf.append(value.get());
            buf.append(" ");
        }
        if (ctr != value.limit())
            throw new IOException("'" + name
                + "' buffer contention resulted in write data consistency.  "
                + ctr + " values written when should have written "
                + value.limit());
        buf.setLength(Math.max(0, buf.length() - 1));
        value.position(pos);
        el.setAttribute(dataAttributeName, buf.toString());
        currentElement = (Element) el.getParentNode();
    }

    public void write(IntBuffer value, String name, IntBuffer defVal) throws IOException {
        if (value == null) {
            return;
        }
        if (value.equals(defVal)) {
            return;
        }

        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.limit()));
        StringBuilder buf = new StringBuilder();
        int pos = value.position();
        value.rewind();
        int ctr = 0;
        while (value.hasRemaining()) {
            ctr++;
            buf.append(value.get());
            buf.append(" ");
        }
        if (ctr != value.limit())
            throw new IOException("'" + name
                + "' buffer contention resulted in write data consistency.  "
                + ctr + " values written when should have written "
                + value.limit());
        buf.setLength(buf.length() - 1);
        value.position(pos);
        el.setAttribute(dataAttributeName, buf.toString());
        currentElement = (Element) el.getParentNode();
    }

    public void write(ByteBuffer value, String name, ByteBuffer defVal) throws IOException {
        if (value == null) return;
        if (value.equals(defVal)) return;

        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.limit()));
        StringBuilder buf = new StringBuilder();
        int pos = value.position();
        value.rewind();
        int ctr = 0;
        while (value.hasRemaining()) {
            ctr++;
            buf.append(value.get());
            buf.append(" ");
        }
        if (ctr != value.limit())
            throw new IOException("'" + name
                + "' buffer contention resulted in write data consistency.  "
                + ctr + " values written when should have written "
                + value.limit());
        buf.setLength(buf.length() - 1);
        value.position(pos);
        el.setAttribute(dataAttributeName, buf.toString());
        currentElement = (Element) el.getParentNode();
    }

    public void write(ShortBuffer value, String name, ShortBuffer defVal) throws IOException {
        if (value == null) {
            return;
        }
        if (value.equals(defVal)) {
            return;
        }

        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(value.limit()));
        StringBuilder buf = new StringBuilder();
        int pos = value.position();
        value.rewind();
        int ctr = 0;
        while (value.hasRemaining()) {
            ctr++;
            buf.append(value.get());
            buf.append(" ");
        }
        if (ctr != value.limit())
            throw new IOException("'" + name
                + "' buffer contention resulted in write data consistency.  "
                + ctr + " values written when should have written "
                + value.limit());
        buf.setLength(buf.length() - 1);
        value.position(pos);
        el.setAttribute(dataAttributeName, buf.toString());
        currentElement = (Element) el.getParentNode();
    }

	public void write(Enum value, String name, Enum defVal) throws IOException {
        if (value == defVal) {
            return;
        }
        currentElement.setAttribute(name, String.valueOf(value));
		
	}

	public void writeByteBufferArrayList(ArrayList<ByteBuffer> array,
			String name, ArrayList<ByteBuffer> defVal) throws IOException {
        if (array == null) {
            return;
        }
        if (array.equals(defVal)) {
            return;
        }
        Element el = appendElement(name);
        el.setAttribute("size", String.valueOf(array.size()));
        for (ByteBuffer o : array) {
            write(o, "ByteBuffer", null);
        }
        currentElement = (Element) el.getParentNode();
		
	}
}
