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

package jmetest.util;

import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;

public class Sizeof {
    private static final Logger logger = Logger.getLogger(Sizeof.class
            .getName());
    
   public static void main(String[] args) throws Exception {
       // Warm up all classes/methods we will use
       runGC();
       usedMemory();

       // Array to keep strong references to allocated objects
       final int count = 1000;
       Object[] objects = new Object[count];

       long heap1 = 0;
       // Allocate count+1 objects, discard the first five
       Vector3f max = new Vector3f(5, 5, 5);
       Vector3f min = new Vector3f(-5, -5, -5);
       for (int i = -1; i < count; ++i) {
           Object object = null;

           // ### Instantiate your data here and assign it to object
           object = new Box("Box", min, max);

           ((Box)object).setModelBound(new BoundingBox());
           ((Box)object).updateModelBound();

           ((Box)object).setRandomColors();
           // ###

           if (i >= 0)
               objects[i] = object;
           else {
               object = null; // Discard the warm up object
               runGC();
               heap1 = usedMemory(); // Take a before heap snapshot
           }
       }

       runGC();
       long heap2 = usedMemory(); // Take an after heap snapshot:

       final int size = Math.round(((float)(heap2 - heap1)) / count);
       logger.info("'before' heap: " + heap1 + ", 'after' heap: " + heap2);
       logger.info("heap delta: " + (heap2 - heap1) + ", {" + objects[0].getClass()
               + "} size = " + size + " bytes");

       for (int i = 0; i < count; ++i)
           objects[i] = null;
       objects = null;
       System.exit(0);
   }

   private static void runGC() throws Exception {
       // It helps to call Runtime.gc()
       // using several method calls:
       for (int r = 0; r < 4; ++r)
           _runGC();
   }

   private static void _runGC() throws Exception {
       long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
       for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++i) {
           s_runtime.runFinalization();
           s_runtime.gc();
           Thread.yield();

           usedMem2 = usedMem1;
           usedMem1 = usedMemory();
       }
   }

   private static long usedMemory() {
       return s_runtime.totalMemory() - s_runtime.freeMemory();
   }

   private static final Runtime s_runtime = Runtime.getRuntime();

}

