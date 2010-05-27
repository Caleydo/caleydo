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
package com.jmex.terrain;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.jme.math.Vector3f;
import com.jme.system.DisplaySystem;
import com.jme.system.dummy.DummySystemProvider;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.export.xml.XMLExporter;
import com.jme.util.export.xml.XMLImporter;

/**
 * Unit tests for {@link TerrainPage}.
 *
 * @version $Revision$, $Date$
 */
public class TestTerrainPage {

    @Test
    public void checkThatSavingAsBinaryWorks() throws Exception {
        DisplaySystem.getDisplaySystem(DummySystemProvider.DUMMY_SYSTEM_IDENTIFIER);
        checkThatSavingWorks(BinaryExporter.getInstance(), BinaryImporter.getInstance());
    }

    @Test
    public void checkThatSavingAsXMLWorks() throws Exception {
        checkThatSavingWorks(XMLExporter.getInstance(), XMLImporter.getInstance());
    }
    
    public void checkThatSavingWorks(JMEExporter exporter, JMEImporter importer) throws Exception {
        int blockSize = 16;
        int size = 65;
        Vector3f stepScale = new Vector3f();
        float[] heightMap = new float[size];

        TerrainPage page = new TerrainPage("testTerrainPage", blockSize, size, stepScale, heightMap);

        File file = File.createTempFile("testTerrainPage", ".tmp");

        // check the the page writes without throwing an exception
        OutputStream output = new FileOutputStream(file);
        exporter.save(page, output);
        output.flush();
        output.close();

        // now see if we can load it back in correctly
        InputStream input = new FileInputStream(file);
        TerrainPage loadedPage = (TerrainPage) importer.load(input);
        
        file.deleteOnExit();

        // and some trivial sanity checks
        assertTrue(loadedPage.getSize() == size);
        assertTrue(loadedPage.getStepScale().equals(stepScale));
        assertTrue(loadedPage.getTotalSize() == size);
        
        // should probably also probe the height map here as well
    }
    
}
