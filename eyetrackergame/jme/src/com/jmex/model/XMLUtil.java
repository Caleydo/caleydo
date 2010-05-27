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

package com.jmex.model;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.jmex.model.ModelFormatException;

/**
 * XML parsing utility methods
 */
public class XMLUtil {
    public static Pattern float3CommaPattern = Pattern.compile(
            "\\s*([-+]?[0-9.]+[fF]?)"
            + "\\s*,\\s*([-+]?[0-9.]+[fF]?)"
            + "\\s*,\\s*([-+]?[0-9.]+[fF]?)\\s*");
    public static Pattern float4CommaPattern = Pattern.compile(
            "\\s*([-+]?[0-9.]+[fF]?)"
            + "\\s*,\\s*([-+]?[0-9.]+[fF]?)"
            + "\\s*,\\s*([-+]?[0-9.]+[fF]?)"
            + "\\s*,\\s*([-+]?[0-9.]+[fF]?)\\s*");
    public static Pattern float3Pattern = Pattern.compile(
            "\\s*([-+]?[0-9.]+[fF]?)"
            + "\\s+([-+]?[0-9.]+[fF]?)"
            + "\\s+([-+]?[0-9.]+[fF]?)\\s*");
    public static Pattern float4Pattern = Pattern.compile(
            "\\s*([-+]?[0-9.]+[fF]?)"
            + "\\s+([-+]?[0-9.]+[fF]?)"
            + "\\s+([-+]?[0-9.]+[fF]?)"
            + "\\s+([-+]?[0-9.]+[fF]?)\\s*");

    /**
     * Returns the first XML child tag with the specified name.
     *
     * @param node The node to search children of
     * @param name The name of the node to search for, case-sensitive.
     * @return The child with the specified name, or null if none exists.
     */
    public static Node getChildNode(Node node, String name) {
        Node child = node.getFirstChild();
        while (child != null) {
            if (child.getNodeName().equals(name)) {
                return child;
            }
            child = child.getNextSibling();
        }
        return null;
    }

    /**
     * @throws ModelFormatException for high-level Ogre Format violations.
     * @throws IOException for any other parsing or I/O problems.
     */
    public static Node loadDocument(InputStream in, String rootElementName)
            throws IOException, ModelFormatException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);
            Element rootEl = doc.getDocumentElement();
            if (rootEl == null) {
                throw new ModelFormatException(
                        "No root node in XML document, when trying to read '"
                        + rootElementName + "'");
            }
            if (rootEl.getTagName().equals(rootElementName)) {
                return rootEl;
            }
            throw new ModelFormatException(
                    "Input XML file does not have required root element '"
                    + rootElementName + "'");
        } catch (ParserConfigurationException ex) {
            throw new IOException("Error occured while reading XML document: "+ex.getLocalizedMessage());
        } catch (SAXException ex) {
            throw new IOException("Error occured while reading XML document: "+ex.getLocalizedMessage());
        }
    }

    /**
     * Returns an attribute of the specified tag with the name provided.
     *
     * @param node
     * @param name
     * @return The attribute if its defined, or null.
     */
    public static String getAttribute(Node node, String name, String defVal){
        Node att = node.getAttributes().getNamedItem(name);
        return att == null ? defVal : att.getNodeValue();
    }

    public static String getAttribute(Node node, String name){
        return getAttribute(node,name,null);
    }

    public static boolean getBoolAttribute(Node node, String name){
        return Boolean.parseBoolean(getAttribute(node,name));
    }

    public static boolean getBoolAttribute(Node node, String name, boolean defVal){
        String att = getAttribute(node, name);
        if (att == null) return defVal;
        return Boolean.parseBoolean(att);
    }

    public static float getFloatAttribute(Node node, String name){
        return Float.parseFloat(getAttribute(node,name,"0"));
    }

    public static float getFloatAttribute(Node node, String name, float defVal){
        String att = getAttribute(node, name);
        if (att == null) return defVal;
        return Float.parseFloat(att);
    }

    public static int getIntAttribute(Node node, String name, int defVal){
        String att = getAttribute(node, name);
        if (att == null) return defVal;
        return Integer.parseInt(att);
    }

    public static int getIntAttribute(Node node, String name){
        return Integer.parseInt(getAttribute(node,name));
    }

    public static float str2float(String str){
        return Float.parseFloat(str.trim());
    }

    /**
     * @throws ModelFormatException if the value String is not a properly
     *                                formatted float tuple of the right size.
     */
    public static Vector3f getVec3Attribute(Node node, String name)
            throws ModelFormatException {
        return getVec3Attribute(node, name, null);
    }

    /**
     * @throws ModelFormatException if the value String is not a properly
     *                                formatted float tuple of the right size.
     */
    public static Vector3f getVec3Attribute(Node node, String name,
            Vector3f defVal) throws ModelFormatException {
        String att = getAttribute(node, name);
        if (att == null)
            return defVal;

        Matcher floatMatcher = float3CommaPattern.matcher(att);
        if (!floatMatcher.matches())
            throw new ModelFormatException(
                    "Malformatted Vector value: " + att);
        return new Vector3f(str2float(floatMatcher.group(1)),
                            str2float(floatMatcher.group(2)),
                            str2float(floatMatcher.group(3)));
    }

    /**
     * @throws ModelFormatException if the value String is not a properly
     *                                formatted float tuple of the right size.
     */
    public static Quaternion getQuatAttribute(Node node, String name)
            throws ModelFormatException {
        return getQuatAttribute(node, name, null);
    }

    /**
     * @throws ModelFormatException if the value String is not a properly
     *                                formatted float tuple of the right size.
     */
    public static Quaternion getQuatAttribute(Node node, String name,
            Quaternion defVal) throws ModelFormatException {
        String att = getAttribute(node, name);
        if (att == null)
            return defVal;

        Matcher floatMatcher = float4CommaPattern.matcher(att);
        if (!floatMatcher.matches())
            throw new ModelFormatException(
                    "Malformatted Quaternion value: " + att);
        return new Quaternion(str2float(floatMatcher.group(1)),
                            str2float(floatMatcher.group(2)),
                            str2float(floatMatcher.group(3)),
                            str2float(floatMatcher.group(4)));
    }

    /**
     * @throws ModelFormatException if the value String is not a properly
     *                                formatted float tuple of the right size.
     */
    public static ColorRGBA getRGBAAttribute(Node node, String name)
            throws ModelFormatException {
        return getRGBAAttribute(node, name, null);
    }

    /**
     * @throws ModelFormatException if the value String is not a properly
     *                                formatted float tuple of the right size.
     */
    public static ColorRGBA getRGBAAttribute(Node node, String name,
            ColorRGBA defVal) throws ModelFormatException {
        String att = getAttribute(node, name);
        if (att == null)
            return defVal;

        if (att.startsWith("#")){
            // parse HEX color
            att = att.substring(1);

            int rgb = Integer.parseInt(att, 16);
            if (att.length() == 6)
                rgb = (rgb << 8) | 0xFF;

            ColorRGBA color = new ColorRGBA();
            color.fromIntRGBA(rgb);
            return color;
        }else{
            Matcher floatMatcher = float3CommaPattern.matcher(att);
            if (!floatMatcher.matches())
                floatMatcher = float4CommaPattern.matcher(att);
            if (!floatMatcher.matches())
                throw new ModelFormatException(
                        "Malformatted RGBA value: " + att);
            return new ColorRGBA(str2float(floatMatcher.group(1)),
                    str2float(floatMatcher.group(2)),
                    str2float(floatMatcher.group(3)),
                    (floatMatcher.groupCount() == 4)
                        ?  str2float(floatMatcher.group(4)) : 1.0f);
        }
    }

}
