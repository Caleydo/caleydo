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

package com.jme.util.export.binary;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.export.binary.modules.BinaryAbstractCameraModule;
import com.jme.util.export.binary.modules.BinaryBlendStateModule;
import com.jme.util.export.binary.modules.BinaryClipStateModule;
import com.jme.util.export.binary.modules.BinaryColorMaskStateModule;
import com.jme.util.export.binary.modules.BinaryCullStateModule;
import com.jme.util.export.binary.modules.BinaryFogStateModule;
import com.jme.util.export.binary.modules.BinaryFragmentProgramStateModule;
import com.jme.util.export.binary.modules.BinaryGLSLShaderObjectsStateModule;
import com.jme.util.export.binary.modules.BinaryLightStateModule;
import com.jme.util.export.binary.modules.BinaryMaterialStateModule;
import com.jme.util.export.binary.modules.BinaryShadeStateModule;
import com.jme.util.export.binary.modules.BinaryStencilStateModule;
import com.jme.util.export.binary.modules.BinaryTextureStateModule;
import com.jme.util.export.binary.modules.BinaryVertexProgramStateModule;
import com.jme.util.export.binary.modules.BinaryWireframeStateModule;
import com.jme.util.export.binary.modules.BinaryZBufferStateModule;

/**
 * This class is mis-named and is located in an inappropriate package:
 * It is not binary-specific (it is in fact used for XML format too), and it
 * is not a java.lang.ClassLoader, which is what "class loader" is for Java
 * developers.
 *
 * @author mpowell
 */
public class BinaryClassLoader {

    //list of modules maintained in the loader
    private static HashMap<String, BinaryLoaderModule> modules = new HashMap<String, BinaryLoaderModule>();
    
    //use a core module to handle render states.
    static {
        BinaryClassLoader.registerModule(new BinaryAbstractCameraModule());
        BinaryClassLoader.registerModule(new BinaryBlendStateModule());
        BinaryClassLoader.registerModule(new BinaryClipStateModule());
        BinaryClassLoader.registerModule(new BinaryColorMaskStateModule());
        BinaryClassLoader.registerModule(new BinaryCullStateModule());
        BinaryClassLoader.registerModule(new BinaryFogStateModule());
        BinaryClassLoader.registerModule(new BinaryFragmentProgramStateModule());
        BinaryClassLoader.registerModule(new BinaryGLSLShaderObjectsStateModule());
        BinaryClassLoader.registerModule(new BinaryLightStateModule());
        BinaryClassLoader.registerModule(new BinaryMaterialStateModule());
        BinaryClassLoader.registerModule(new BinaryShadeStateModule());
        BinaryClassLoader.registerModule(new BinaryStencilStateModule());
        BinaryClassLoader.registerModule(new BinaryTextureStateModule());
        BinaryClassLoader.registerModule(new BinaryVertexProgramStateModule());
        BinaryClassLoader.registerModule(new BinaryWireframeStateModule());
        BinaryClassLoader.registerModule(new BinaryZBufferStateModule());
    }
    
    /**
     * registrrModule adds a module to the loader for handling special case class names.
     * @param m the module to register with this loader.
     */
    public static void registerModule(BinaryLoaderModule m) {
       modules.put(m.getKey(), m);
    }
    
    /**
     * unregisterModule removes a module from the loader, no longer using it to handle
     * special case class names.
     * @param m the module to remove from the loader.
     */
    public static void unregisterModule(BinaryLoaderModule m) {
        modules.remove(m.getKey());
    }
    
    /**
     * fromName creates a new Savable from the provided class name. First registered modules
     * are checked to handle special cases, if the modules do not handle the class name, the
     * class is instantiated directly. 
     * @param className the class name to create.
     * @param inputCapsule the InputCapsule that will be used for loading the Savable (to look up ctor parameters)
     * @return the Savable instance of the class.
     * @throws InstantiationException thrown if the class does not have an empty constructor.
     * @throws IllegalAccessException thrown if the class is not accessable.
     * @throws ClassNotFoundException thrown if the class name is not in the classpath.
     * @throws IOException when loading ctor parameters fails
     */
    public static Savable fromName(String className, InputCapsule inputCapsule) throws InstantiationException, 
        IllegalAccessException, ClassNotFoundException, IOException {
        
        BinaryLoaderModule m = modules.get(className);
        if(m != null) {
            return m.load(inputCapsule);
        }
            
        try {
            return (Savable)Class.forName(className).newInstance();
        }
        catch (InstantiationException e) {
        	Logger.getLogger(BinaryClassLoader.class.getName()).severe(
        			"Could not access constructor of class '" + className + "'! \n" +
        			"Some types need to have the BinaryImporter set up in a special way. Please doublecheck the setup.");
        	throw e;
        }
        catch (IllegalAccessException e) {
        	Logger.getLogger(BinaryClassLoader.class.getName()).severe(
        			e.getMessage() + " \n" +
                    "Some types need to have the BinaryImporter set up in a special way. Please doublecheck the setup.");
        	throw e;
        }
    }

}
