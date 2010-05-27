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

/*
 * Created on Apr 6, 2004
 */

package com.jmex.effects.transients;

import com.jme.scene.Node;

/**
 * A Transient is a node that has "stages" in its life.  The stages are set by
 * the extended classes, as well as what going from one stage to another means.
 * @author Ahmed
 * @author Jack Lindamood (javadoc only)
 */
public abstract class Transient extends Node {
    private static final long serialVersionUID = 1L;
    private int maxNumOfStages, currentStage;
    /**
     * Creates a Transient Node.  Nothing is intialized by default.
     * @param name The name of the node.
     */
    public Transient(String name) {
        super(name);
    }
    /**
     * Returns the maximum number of stages for this node.
     * @return The maximum number of stages for this node.
     */
    public int getMaxNumOfStages() {
        return maxNumOfStages;
    }

    /**
     * Sets the maximum number of stages for this node.
     * @param s The new maximum number of stages.
     */
    public void setMaxNumOfStages(int s) {
        maxNumOfStages = s;
    }

    /**
     * Returns the current stage of this node.  Should be between 0 maximum
     * number of stages.
     * @return The current stage.
     */
    public int getCurrentStage() {
        return currentStage;
    }

    /**
     * Sets the current stage to the integer given.  If the current stage value
     * is greater than the maximum number of stages allowed, then it is trimmed
     * to simply be the maximum number of stages.
     * @param s The new current stage.
     */
    public void setCurrentStage(int s) {
        if (s < getMaxNumOfStages()) {
            currentStage = s;
        } else {
            currentStage = getMaxNumOfStages();
        }
    }
}