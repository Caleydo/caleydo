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

package com.jme.input.action;

import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

/**
 * <code>KeyNodeStrafeLeftAction</code> defines an action that moves a node
 * along the positive left vector. The speed at which it moves is set and of the
 * form units per second.
 * 
 * @author Mark Powell
 * @version $Id: KeyNodeStrafeLeftAction.java,v 1.12 2004/08/22 02:00:34 cep21
 *          Exp $
 */
public class KeyNodeStrafeLeftAction extends KeyInputAction {
    //the node to manipulate
    private Spatial node;
    //temporary vector for translation
    private static final Vector3f tempVa = new Vector3f();

    /**
     * Constructor instantiates a new <code>KeyNodeStrafeLeftAction</code>
     * object.
     * 
     * @param node
     *            the node to move along the left vector.
     * @param speed
     *            the speed at which to move the node.
     */
    public KeyNodeStrafeLeftAction(Spatial node, float speed) {
        this.node = node;
        this.speed = speed;
    }

    /**
     * <code>performAction</code> moves the node along the left vector for a
     * given distance of speed * time.
     * 
     * @see com.jme.input.action.KeyInputAction#performAction(InputActionEvent)
     */
    public void performAction(InputActionEvent evt) {
        Vector3f loc = node.getLocalTranslation();
        loc.addLocal(node.getLocalRotation().getRotationColumn(0, tempVa)
                .multLocal(speed * evt.getTime()));
        node.setLocalTranslation(loc);
    }
}