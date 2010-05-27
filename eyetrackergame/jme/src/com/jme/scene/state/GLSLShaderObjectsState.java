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

package com.jme.scene.state;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.math.Matrix3f;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;
import com.jme.util.shader.ShaderVariable;
import com.jme.util.shader.uniformtypes.ShaderVariableFloat;
import com.jme.util.shader.uniformtypes.ShaderVariableFloat2;
import com.jme.util.shader.uniformtypes.ShaderVariableFloat3;
import com.jme.util.shader.uniformtypes.ShaderVariableFloat4;
import com.jme.util.shader.uniformtypes.ShaderVariableInt;
import com.jme.util.shader.uniformtypes.ShaderVariableInt2;
import com.jme.util.shader.uniformtypes.ShaderVariableInt3;
import com.jme.util.shader.uniformtypes.ShaderVariableInt4;
import com.jme.util.shader.uniformtypes.ShaderVariableMatrix2;
import com.jme.util.shader.uniformtypes.ShaderVariableMatrix3;
import com.jme.util.shader.uniformtypes.ShaderVariableMatrix4;
import com.jme.util.shader.uniformtypes.ShaderVariableMatrix4Array;
import com.jme.util.shader.uniformtypes.ShaderVariablePointerByte;
import com.jme.util.shader.uniformtypes.ShaderVariablePointerFloat;
import com.jme.util.shader.uniformtypes.ShaderVariablePointerInt;
import com.jme.util.shader.uniformtypes.ShaderVariablePointerShort;

/**
 * Implementation of the GL_ARB_shader_objects extension.
 *
 * @author Thomas Hourdel
 * @author Rikard Herlitz (MrCoder)
 */
public abstract class GLSLShaderObjectsState extends RenderState {
    private static final Logger logger = Logger
            .getLogger(GLSLShaderObjectsState.class.getName());

    /** Storage for shader uniform values */
    protected HashMap<String, ShaderVariable> shaderUniforms =
            new HashMap<String, ShaderVariable>();
    /** Storage for shader attribute values */
    protected HashMap<String, ShaderVariable> shaderAttributes =
            new HashMap<String, ShaderVariable>();
    
    /** Optional logic for setting shadervariables based on the current geom */
    protected GLSLShaderDataLogic shaderDataLogic;

    /** The Geometry this shader currently operates on during rendering */
    protected Geometry geom;
    
    protected static boolean glslSupported = false;
    protected static boolean glslSupportedDetected = false;
    
    protected boolean needSendShader = false;
    
    protected String vertShader, fragShader;
    
    /**
     * Gets the currently loaded vertex shader.
     * @return
     */
    public	String	getVertexShader() {
    	return vertShader;
    }
    
    /**
     * Gets the currently loaded fragment shader.
     * @return
     */
    public	String	getFragmentShader() {
    	return fragShader;
    }
    
    /**
     * Gets all shader uniforms variables.
     * @return
     */
    public	Collection<ShaderVariable>	getShaderUniforms() {
    	return shaderUniforms.values();
    }
    
    /**
     * Retrieves a shader uniform by name.
     * @param uniformName
     * @return
     */
    public	ShaderVariable		getUniformByName(String uniformName) {
    	return shaderUniforms.get(uniformName);
    }
    
    /**
     * Gets all shader attribute variables.
     * @return
     */
    public	Collection<ShaderVariable>	getShaderAttributes() {
    	return shaderAttributes.values();
    }
    
    /**
     * Retrieves a shader attribute by name.
     * @param uniformName
     * @return
     */
    public	ShaderVariable		getAttributeByName(String attributeName) {
        return shaderAttributes.get(attributeName);
    }
    
    /**
     * 
     * @param geom
     */
    public void setGeometry(Geometry geom) {
        this.geom = geom;
    }
    
    /**
     * Logic to handle setting geom-specific data to a shader before rendering 
     * @param shaderDataLogic
     */
    public void setShaderDataLogic(GLSLShaderDataLogic shaderDataLogic) {
        this.shaderDataLogic = shaderDataLogic;
    }    

    /**
     * <code>isSupported</code> determines if the ARB_shader_objects extension
     * is supported by current graphics configuration. This will only be valid
     * if a renderer has been created.
     * 
     * @return if ARB shader objects are supported
     */
    public static boolean isSupported() {
        return glslSupported;
    }

    /**
     * Overide setting of glsl support.
     * 
     * @param use
     */
    public static void overrideSupport(boolean use) {
        glslSupported = use;
    }

    /**
     * Reset glsl support to driver-detected setting.
     */
    public static void resetSupport() {
        glslSupported = glslSupportedDetected;
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value the new value
     */
    public void setUniform(String name, boolean value) {
        ShaderVariableInt shaderUniform =
                getShaderUniform(name, ShaderVariableInt.class);
        shaderUniform.value1 = value ? 1 : 0;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value the new value
     */
    public void setUniform(String name, int value) {
        ShaderVariableInt shaderUniform =
                getShaderUniform(name, ShaderVariableInt.class);
        shaderUniform.value1 = value;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value the new value
     */
    public void setUniform(String name, float value) {
        ShaderVariableFloat shaderUniform =
                getShaderUniform(name, ShaderVariableFloat.class);
        shaderUniform.value1 = value;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value1 the new value
     * @param value2 the new value
     */
    public void setUniform(String name, boolean value1, boolean value2) {
        ShaderVariableInt2 shaderUniform =
                getShaderUniform(name, ShaderVariableInt2.class);
        shaderUniform.value1 = value1 ? 1 : 0;
        shaderUniform.value2 = value2 ? 1 : 0;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value1 the new value
     * @param value2 the new value
     */
    public void setUniform(String name, int value1, int value2) {
        ShaderVariableInt2 shaderUniform =
                getShaderUniform(name, ShaderVariableInt2.class);
        shaderUniform.value1 = value1;
        shaderUniform.value2 = value2;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value1 the new value
     * @param value2 the new value
     */
    public void setUniform(String name, float value1, float value2) {
        ShaderVariableFloat2 shaderUniform =
                getShaderUniform(name, ShaderVariableFloat2.class);
        shaderUniform.value1 = value1;
        shaderUniform.value2 = value2;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value1 the new value
     * @param value2 the new value
     * @param value3 the new value
     */
    public void setUniform(String name, boolean value1, boolean value2,
            boolean value3) {
        ShaderVariableInt3 shaderUniform =
                getShaderUniform(name, ShaderVariableInt3.class);
        shaderUniform.value1 = value1 ? 1 : 0;
        shaderUniform.value2 = value2 ? 1 : 0;
        shaderUniform.value3 = value3 ? 1 : 0;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value1 the new value
     * @param value2 the new value
     * @param value3 the new value
     */
    public void setUniform(String name, int value1, int value2, int value3) {
        ShaderVariableInt3 shaderUniform =
                getShaderUniform(name, ShaderVariableInt3.class);
        shaderUniform.value1 = value1;
        shaderUniform.value2 = value2;
        shaderUniform.value3 = value3;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value1 the new value
     * @param value2 the new value
     * @param value3 the new value
     */
    public void setUniform(String name, float value1, float value2,
            float value3) {
        ShaderVariableFloat3 shaderUniform =
                getShaderUniform(name, ShaderVariableFloat3.class);
        shaderUniform.value1 = value1;
        shaderUniform.value2 = value2;
        shaderUniform.value3 = value3;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value1 the new value
     * @param value2 the new value
     * @param value3 the new value
     * @param value4 the new value
     */
    public void setUniform(String name, boolean value1, boolean value2,
            boolean value3, boolean value4) {
        ShaderVariableInt4 shaderUniform =
                getShaderUniform(name, ShaderVariableInt4.class);
        shaderUniform.value1 = value1 ? 1 : 0;
        shaderUniform.value2 = value2 ? 1 : 0;
        shaderUniform.value3 = value3 ? 1 : 0;
        shaderUniform.value4 = value4 ? 1 : 0;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value1 the new value
     * @param value2 the new value
     * @param value3 the new value
     * @param value4 the new value
     */
    public void setUniform(String name, int value1, int value2, int value3,
            int value4) {
        ShaderVariableInt4 shaderUniform =
                getShaderUniform(name, ShaderVariableInt4.class);
        shaderUniform.value1 = value1;
        shaderUniform.value2 = value2;
        shaderUniform.value3 = value3;
        shaderUniform.value4 = value4;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value1 the new value
     * @param value2 the new value
     * @param value3 the new value
     * @param value4 the new value
     */
    public void setUniform(String name, float value1, float value2,
            float value3, float value4) {
        ShaderVariableFloat4 shaderUniform =
                getShaderUniform(name, ShaderVariableFloat4.class);
        shaderUniform.value1 = value1;
        shaderUniform.value2 = value2;
        shaderUniform.value3 = value3;
        shaderUniform.value4 = value4;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value the new value
     */
    public void setUniform(String name, Vector2f value) {
        ShaderVariableFloat2 shaderUniform =
                getShaderUniform(name, ShaderVariableFloat2.class);
        shaderUniform.value1 = value.x;
        shaderUniform.value2 = value.y;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value the new value
     */
    public void setUniform(String name, Vector3f value) {
        ShaderVariableFloat3 shaderUniform =
                getShaderUniform(name, ShaderVariableFloat3.class);
        shaderUniform.value1 = value.x;
        shaderUniform.value2 = value.y;
        shaderUniform.value3 = value.z;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value the new value
     */
    public void setUniform(String name, ColorRGBA value) {
        ShaderVariableFloat4 shaderUniform =
                getShaderUniform(name, ShaderVariableFloat4.class);
        shaderUniform.value1 = value.r;
        shaderUniform.value2 = value.g;
        shaderUniform.value3 = value.b;
        shaderUniform.value4 = value.a;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value the new value
     */
    public void setUniform(String name, Quaternion value) {
        ShaderVariableFloat4 shaderUniform =
                getShaderUniform(name, ShaderVariableFloat4.class);
        shaderUniform.value1 = value.x;
        shaderUniform.value2 = value.y;
        shaderUniform.value3 = value.z;
        shaderUniform.value4 = value.w;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value the new value (a float buffer of size 4)
     * @param rowMajor true if is this in row major order
     */
    public void setUniform(String name, float value[], boolean rowMajor) {
        if (value.length != 4)
            return;

        ShaderVariableMatrix2 shaderUniform =
                getShaderUniform(name, ShaderVariableMatrix2.class);
        shaderUniform.matrixBuffer.clear();
        shaderUniform.matrixBuffer.put(value[0]);
        shaderUniform.matrixBuffer.put(value[1]);
        shaderUniform.matrixBuffer.put(value[2]);
        shaderUniform.matrixBuffer.put(value[3]);
        shaderUniform.rowMajor = rowMajor;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value the new value
     * @param rowMajor true if is this in row major order
     */
    public void setUniform(String name, Matrix3f value, boolean rowMajor) {
        ShaderVariableMatrix3 shaderUniform =
                getShaderUniform(name, ShaderVariableMatrix3.class);
        // prepare buffer for writing
        shaderUniform.matrixBuffer.rewind();
        value.fillFloatBuffer(shaderUniform.matrixBuffer);
        // prepare buffer for reading
        shaderUniform.matrixBuffer.rewind();
        shaderUniform.rowMajor = rowMajor;

        setNeedsRefresh(true);
    }

    /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value the new value
     * @param rowMajor true if is this in row major order
     */
    public void setUniform(String name, Matrix4f value, boolean rowMajor) {
        ShaderVariableMatrix4 shaderUniform =
                getShaderUniform(name, ShaderVariableMatrix4.class);
        // prepare buffer for writing
        shaderUniform.matrixBuffer.rewind();
        value.fillFloatBuffer(shaderUniform.matrixBuffer);
        // prepare buffer for reading
        shaderUniform.matrixBuffer.rewind();
        shaderUniform.rowMajor = rowMajor;

        setNeedsRefresh(true);
    }

     /**
     * Set an uniform value for this shader object.
     *
     * @param name uniform variable to change
     * @param value the new value
     * @param rowMajor true if is this in row major order
     */
    public void setUniform(String name, Matrix4f[] values, boolean rowMajor) {
        ShaderVariableMatrix4Array shaderUniform =
                getShaderUniform(name, ShaderVariableMatrix4Array.class);
        // prepare buffer for writing
        FloatBuffer matrixBuffer = shaderUniform.matrixBuffer;
        if (matrixBuffer == null || matrixBuffer.capacity() < values.length * 16){
            matrixBuffer = BufferUtils.createFloatBuffer(values.length * 16);
            shaderUniform.matrixBuffer = matrixBuffer;
        }
        matrixBuffer.limit(values.length * 16);
        
        matrixBuffer.rewind();
        for (Matrix4f value : values){
            value.fillFloatBuffer(matrixBuffer);
        }
        matrixBuffer.flip();
        
        // prepare buffer for reading
        shaderUniform.rowMajor = rowMajor;

        setNeedsRefresh(true);
    }

    /** <code>clearUniforms</code> clears all uniform values from this state. */
    public void clearUniforms() {
        shaderUniforms.clear();
    }

    /**
     * Set an attribute pointer value for this shader object.
     *
     * @param name attribute variable to change
     * @param size Specifies the number of values for each element of the
     * generic vertex attribute array. Must be 1, 2, 3, or 4.
     * @param normalized Specifies whether fixed-point data values should be
     * normalized or converted directly as fixed-point values when they are
     * accessed.
     * @param stride Specifies the byte offset between consecutive attribute
     * values. If stride is 0 (the initial value), the attribute values are
     * understood to be tightly packed in the array.
     * @param data The actual data to use as attribute pointer
     */
    public void setAttributePointer(String name, int size, boolean normalized,
            int stride, FloatBuffer data) {
        ShaderVariablePointerFloat shaderUniform =
                getShaderAttribute(name, ShaderVariablePointerFloat.class);
        shaderUniform.size = size;
        shaderUniform.normalized = normalized;
        shaderUniform.stride = stride;
        shaderUniform.data = data;

        setNeedsRefresh(true);
    }

    /**
     * Set an attribute pointer value for this shader object.
     *
     * @param name attribute variable to change
     * @param size Specifies the number of values for each element of the
     * generic vertex attribute array. Must be 1, 2, 3, or 4.
     * @param normalized Specifies whether fixed-point data values should be
     * normalized or converted directly as fixed-point values when they are
     * accessed.
     * @param unsigned Specifies wheter the data is signed or unsigned
     * @param stride Specifies the byte offset between consecutive attribute
     * values. If stride is 0 (the initial value), the attribute values are
     * understood to be tightly packed in the array.
     * @param data The actual data to use as attribute pointer
     */
    public void setAttributePointer(String name, int size, boolean normalized,
            boolean unsigned, int stride, ByteBuffer data) {
        ShaderVariablePointerByte shaderUniform =
                getShaderAttribute(name, ShaderVariablePointerByte.class);
        shaderUniform.size = size;
        shaderUniform.normalized = normalized;
        shaderUniform.unsigned = unsigned;
        shaderUniform.stride = stride;
        shaderUniform.data = data;

        setNeedsRefresh(true);
    }

    /**
     * Set an attribute pointer value for this shader object.
     *
     * @param name attribute variable to change
     * @param size Specifies the number of values for each element of the
     * generic vertex attribute array. Must be 1, 2, 3, or 4.
     * @param normalized Specifies whether fixed-point data values should be
     * normalized or converted directly as fixed-point values when they are
     * accessed.
     * @param unsigned Specifies wheter the data is signed or unsigned
     * @param stride Specifies the byte offset between consecutive attribute
     * values. If stride is 0 (the initial value), the attribute values are
     * understood to be tightly packed in the array.
     * @param data The actual data to use as attribute pointer
     */
    public void setAttributePointer(String name, int size, boolean normalized,
            boolean unsigned, int stride, IntBuffer data) {
        ShaderVariablePointerInt shaderUniform =
                getShaderAttribute(name, ShaderVariablePointerInt.class);
        shaderUniform.size = size;
        shaderUniform.normalized = normalized;
        shaderUniform.unsigned = unsigned;
        shaderUniform.stride = stride;
        shaderUniform.data = data;

        setNeedsRefresh(true);
    }

    /**
     * Set an attribute pointer value for this shader object.
     *
     * @param name attribute variable to change
     * @param size Specifies the number of values for each element of the
     * generic vertex attribute array. Must be 1, 2, 3, or 4.
     * @param normalized Specifies whether fixed-point data values should be
     * normalized or converted directly as fixed-point values when they are
     * accessed.
     * @param unsigned Specifies wheter the data is signed or unsigned
     * @param stride Specifies the byte offset between consecutive attribute
     * values. If stride is 0 (the initial value), the attribute values are
     * understood to be tightly packed in the array.
     * @param data The actual data to use as attribute pointer
     */
    public void setAttributePointer(String name, int size, boolean normalized,
            boolean unsigned, int stride, ShortBuffer data) {
        ShaderVariablePointerShort shaderUniform =
                getShaderAttribute(name, ShaderVariablePointerShort.class);
        shaderUniform.size = size;
        shaderUniform.normalized = normalized;
        shaderUniform.unsigned = unsigned;
        shaderUniform.stride = stride;
        shaderUniform.data = data;

        setNeedsRefresh(true);
    }

    /**
     * <code>clearAttributes</code> clears all attribute values from this
     * state.
     */
    public void clearAttributes() {
        shaderAttributes.clear();
    }


    /**
     * @return RS_SHADER_OBJECTS
     * @see com.jme.scene.state.RenderState#getType()
     * @deprecated As of 2.0, use {@link RenderState#getStateType()} instead.
     */
    public int getType() {
        return RS_GLSL_SHADER_OBJECTS;
    }

    /**
     * <code>getStateType</code> returns the type {@link RenderState.StateType#GLSLShaderObjects}
     * 
     * @return {@link RenderState.StateType#GLSLShaderObjects}
     * @see com.jme.scene.state.RenderState#getStateType()
     */
    public StateType getStateType() {
    	
        return StateType.GLSLShaderObjects;
    }

    /**
     * Creates or retrieves a uniform shadervariable.
     *
     * @param name Name of the uniform shadervariable to retrieve or create
     * @param classz Class type of the shadervariable
     * @return
     */
    private <T extends ShaderVariable> T getShaderUniform(String name,
            Class<T> classz) {
        T shaderVariable = getShaderVariable(name, classz, shaderUniforms);
        checkUniformSizeLimits();
        return shaderVariable;
    }

    /**
     * Creates or retrieves a attribute shadervariable.
     *
     * @param name Name of the attribute shadervariable to retrieve or create
     * @param classz Class type of the shadervariable
     * @return
     */
    private <T extends ShaderVariable> T getShaderAttribute(String name,
            Class<T> classz) {
        T shaderVariable = getShaderVariable(name, classz, shaderAttributes);
        checkAttributeSizeLimits();
        return shaderVariable;
    }

    /**
     * @param name Name of the shadervariable to retrieve or create
     * @param classz Class type of the shadervariable
     * @param shaderVariableList List retrieve shadervariable from
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T extends ShaderVariable> T getShaderVariable(String name,
            Class<T> classz, HashMap<String, ShaderVariable> shaderVariableList) {
    	
    	ShaderVariable temp = shaderVariableList.get(name);
        if (temp != null) {
            temp.needsRefresh = true;
            return (T) temp;
        }

        try {
            T shaderUniform = classz.newInstance();
            shaderUniform.name = name;
            shaderVariableList.put(name, shaderUniform);

            return shaderUniform;
        } catch (InstantiationException e) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "getShaderVariable(name, classz, shaderVariableList)", "Exception", e);
        } catch (IllegalAccessException e) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "getShaderVariable(name, classz, shaderVariableList)", "Exception", e);
        }

        return null;
    }

    /**
     * Check if we are keeping the size limits in terms of uniform locations
     * on the card.
     */
    public void checkUniformSizeLimits() {
        // Implement in provider
    }

    /**
     * Check if we are keeping the size limits in terms of attribute locations
     * on the card.
     */
    public void checkAttributeSizeLimits() {
        // Implement in provider
    }

    /**
     * Loads shader from URL
     * 
     * @param url
     * @return
     */
    private String load(URL url) {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buf = new StringBuffer();
            while (r.ready()) {
                buf.append(r.readLine()).append('\n');
            }
            r.close();
            return buf.toString();
        } catch (IOException e) {
            logger.severe("Could not load shader program: " + e);
            logger.logp(Level.SEVERE, getClass().getName(), "load(URL)", "Exception", e);
            return null;
        }
    }
    
    /**
     * <code>load</code> loads the shader object from the specified file. The
     * program must be in ASCII format. The implementation must
     * convert the String into data compatible with the graphics library.
     *
     * @param vert text file containing the vertex shader object
     * @param frag text file containing the fragment shader object
     */
    public void load(URL vert, URL frag){
        vertShader = vert != null ? load(vert) : null;
        fragShader = frag != null ? load(frag) : null;
        needSendShader = true;
    }

    /**
     * <code>load</code> loads the shader object from the specified file. The
     * program must be in ASCII format. The implementation must
     * convert the String into data compatible with the graphics library.
     *
     * @param vert The input stream from which the vertex shader can be read
     * @param frag The input stream from which the fragment shader can be read
     */
    public void load(InputStream vert, InputStream frag) {
        ByteBuffer vertexByteBuffer = vert != null ? load(vert) : null;
        ByteBuffer fragmentByteBuffer = frag != null ? load(frag) : null;
        sendToGL(vertexByteBuffer, fragmentByteBuffer);
	}
    
    /**
     * <code>load</code> loads the shader object from the specified string. The
     * program must be in ASCII format. We delegate the loading to each
     * implementation because we do not know in what format the underlying API
     * wants the data.
     *
     * @param vert string containing the vertex shader object
     * @param frag string containing the fragment shader object
     */
    public void load(String vert, String frag){
        vertShader = vert;
        fragShader = frag;
        needSendShader = true;
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.writeStringSavableMap(shaderUniforms, "shaderUniforms", null);
        capsule.writeStringSavableMap(shaderAttributes, "shaderAttributes", null);
    }

    @SuppressWarnings ("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        shaderUniforms = (HashMap<String, ShaderVariable>) capsule.readStringSavableMap("shaderUniforms",
                new HashMap<String, ShaderVariable>());
        shaderAttributes = (HashMap<String, ShaderVariable>) capsule.readStringSavableMap("shaderAttributes",
                new HashMap<String, ShaderVariable>());
    }

    public Class<? extends GLSLShaderObjectsState> getClassTag() {
        return GLSLShaderObjectsState.class;
    }
    
    /**
     * Loads the shader object. Use null for an empty vertex or empty fragment
     * shader.
     *
     * @param vertexByteBuffer vertex shader
     * @param fragmentByteBuffer fragment shader
     * @see com.jme.scene.state.GLSLShaderObjectsState#load(java.net.URL,
     *java.net.URL)
     */
    protected abstract void sendToGL(ByteBuffer vertexByteBuffer,
            						 ByteBuffer fragmentByteBuffer); 
    
    /**
     * Load an URL and grab content into a ByteBuffer.
     *
     * @param in The input stream to read
     * @return the loaded url
     */
    protected ByteBuffer load(InputStream in) {
        DataInputStream dataStream = null;
        try {
            BufferedInputStream bufferedInputStream =
                    new BufferedInputStream(in);
            dataStream =
                    new DataInputStream(bufferedInputStream);
            byte shaderCode[] = new byte[bufferedInputStream.available()];
            dataStream.readFully(shaderCode);
            bufferedInputStream.close();
            dataStream.close();
            ByteBuffer shaderByteBuffer =
                    BufferUtils.createByteBuffer(shaderCode.length);
            shaderByteBuffer.put(shaderCode);
            shaderByteBuffer.rewind();

            return shaderByteBuffer;
        } catch (Exception e) {
            logger.severe("Could not load shader object: " + e);
            logger.logp(Level.SEVERE, getClass().getName(), "load(URL)", "Exception", e);
            return null;
        }
        finally {
            // Ensure that the stream is closed, even if there is an exception.
            if (dataStream != null) {
                try {
                    dataStream.close();
                } catch (IOException closeFailure) {
                    logger.log(Level.WARNING,
                            "Failed to close the shader object",
                            closeFailure);
                }
            }
        }
    }
    
    /**
     * Frees the memory and invalidates the shader handle
     */
    public abstract void cleanup();
}
