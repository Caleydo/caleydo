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

package com.jmex.model.collada;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleHeadlessApp;
import com.jme.scene.Spatial;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;

public class ColladaToJme extends SimpleHeadlessApp {
    private static final Logger logger = Logger.getLogger(ColladaToJme.class
            .getName());
    
   Spatial collada;
   static String in;
   static String outDir;
    public static void main(String[] args) {
        if (args.length < 3 || args.length > 3) {
            logger.info("USAGE: ColladaToJme <COLLADA File> <Texture Directory> <Jme File>");
            System.exit(1);
        }
        in = args[0];
        String texDir = args[1];
        outDir = args[2];
        
        ResourceLocatorTool.addResourceLocator(
                ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator(
                        new File(texDir).toURI()));
        
        //make sure outDir exists:
        File out = new File(outDir);
        
        if(!out.exists()) {
            out.mkdir();
        }
        
        ColladaToJme ctj = new ColladaToJme();
        ctj.start();
    }

    protected void simpleInitGame() {
        long start = System.nanoTime();
        writeFile(in);

        this.finished = true;
        long end = System.nanoTime();
        
        logger.info("Conversion took: " + ((end-start)/1000000000) + " seconds.");
    }
    
    protected void writeFile(String inputFile) {
        File inFile = new File(inputFile);
        if(inFile.isDirectory()) {
            if(!inputFile.endsWith(File.separator)) {
                inputFile += File.separator;
            }
            logger.info(inputFile + " is a Directory, getting subfiles: ");
            String[] files = inFile.list();
            for(int i = 0; i < files.length; i++) {
                logger.info("Sending: " + (inputFile+files[i]));
                writeFile(inputFile+files[i]);
            }
            
            return;
        }
        
        if(inFile.getName().toUpperCase().endsWith(".DAE")) {
            collada = null;
            System.gc();
            
            logger.info("Found Collada file, converting: " + inputFile);
            String out = outDir + inFile.getName().substring( 0, inFile.getName().toUpperCase().indexOf(".DAE") ) + ".jme";
            logger.info("Storing as: " + out);
            String modelName = inFile.getName().substring(0,
                    inFile.getName().indexOf("."));
            FileInputStream input = null;
            try {
                input = new FileInputStream(inFile);
            } catch (FileNotFoundException e1) {
                logger.log(Level.WARNING, "Error creating FileInputStream", e1);
            }
            if (input == null) {
                logger.info("Unable to find file");
                System.exit(0);
            }
            
            
            try {
                ColladaImporter.load(input, modelName);
                collada = ColladaImporter.getModel();
                ColladaImporter.cleanUp();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading Collada file", e);
            }
            
            collada.updateGeometricState(0, true);
            collada.updateRenderState();
            
            try {
                File f = new File(out);
                if(f.exists()) {
                    f.delete();
                }
                BinaryExporter.getInstance().save(collada, f);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error saving Collada file", e);
            }
        }
    }
}
