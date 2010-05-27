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

package com.jme.input;

import com.jme.input.action.KeyNodeBackwardAction;
import com.jme.input.action.KeyNodeForwardAction;
import com.jme.input.action.KeyNodeLookDownAction;
import com.jme.input.action.KeyNodeLookUpAction;
import com.jme.input.action.KeyNodeRotateLeftAction;
import com.jme.input.action.KeyNodeRotateRightAction;
import com.jme.input.action.KeyNodeStrafeLeftAction;
import com.jme.input.action.KeyNodeStrafeRightAction;
import com.jme.input.action.NodeMouseLook;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

/**
 * <code>NodeHandler</code> defines an InputHandler that sets
 * a node that can be controlled via keyboard and mouse inputs. By default the
 * commands are, WSAD moves the node forward, backward and strafes. The
 * arrow keys rotate and tilt the node and the mouse also rotates and tilts
 * the node.
 * @author Mark Powell
 * @version $Id: NodeHandler.java 4664 2009-09-04 17:09:19Z skye.book $
 */
public class NodeHandler extends InputHandler {

    /**
     * Constructor instantiates a new <code>NodeHandler</code> object. The
     * application is set for the use of the exit action. The node is set to
     * control.
     * @param node the node to control.
     */
    public NodeHandler(Spatial node) {
        setKeyBindings();
        setUpMouse(node, 1 );
        setActions(node, 0.5f, 0.01f );
    }

    /**
     * Constructor instantiates a new <code>NodeHandler</code> object. The
     * application is set for the use of the exit action. The node is set to
     * control.
     * @param node the node to control.
     * @param keySpeed action speed for key actions (move)
     * @param mouseSpeed action speed for mouse actions (rotate)
     */
    public NodeHandler(Spatial node, float keySpeed, float mouseSpeed) {

        setKeyBindings();
        setUpMouse(node, mouseSpeed );
        setActions(node, keySpeed, mouseSpeed );
    }

    /**
     *
     * <code>setKeyBindings</code> binds the keys to use for the actions.
     */
    private void setKeyBindings() {
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();

        keyboard.set("forward", KeyInput.KEY_W);
        keyboard.set("backward", KeyInput.KEY_S);
        keyboard.set("strafeLeft", KeyInput.KEY_A);
        keyboard.set("strafeRight", KeyInput.KEY_D);
        keyboard.set("lookUp", KeyInput.KEY_UP);
        keyboard.set("lookDown", KeyInput.KEY_DOWN);
        keyboard.set("turnRight", KeyInput.KEY_RIGHT);
        keyboard.set("turnLeft", KeyInput.KEY_LEFT);
    }

    /**
     *
     * <code>setUpMouse</code> sets the mouse look object.
     * @param node the node to use for rotations.
     * @param mouseSpeed
     */
    private void setUpMouse( Spatial node, float mouseSpeed ) {
        RelativeMouse mouse = new RelativeMouse("Mouse Input");
        mouse.registerWithInputHandler( this );

        NodeMouseLook mouseLook = new NodeMouseLook(mouse, node, 0.1f);
        mouseLook.setSpeed( mouseSpeed );
        mouseLook.setLockAxis(new Vector3f(node.getLocalRotation().getRotationColumn(1).x,
                node.getLocalRotation().getRotationColumn(1).y,
                node.getLocalRotation().getRotationColumn(1).z));
        addAction(mouseLook);
    }

    /**
     *
     * <code>setActions</code> sets the keyboard actions with the corresponding
     * key command.
     * @param node the node to control.
     * @param moveSpeed
     * @param turnSpeed
     */
    private void setActions( Spatial node, float moveSpeed, float turnSpeed ) {
        addAction( new KeyNodeForwardAction( node, moveSpeed ), "forward", true );
        addAction( new KeyNodeBackwardAction( node, moveSpeed ), "backward", true );
        addAction( new KeyNodeStrafeLeftAction( node, moveSpeed ), "strafeLeft", true );
        addAction( new KeyNodeStrafeRightAction( node, moveSpeed ), "strafeRight", true );
        addAction( new KeyNodeLookUpAction( node, turnSpeed ), "lookUp", true );
        addAction( new KeyNodeLookDownAction( node, turnSpeed ), "lookDown", true );
        KeyNodeRotateRightAction rotateRight = new KeyNodeRotateRightAction( node, turnSpeed );
        rotateRight.setLockAxis( node.getLocalRotation().getRotationColumn( 1 ) );
        addAction( rotateRight, "turnRight", true );
        KeyNodeRotateLeftAction rotateLeft = new KeyNodeRotateLeftAction( node, turnSpeed );
        rotateLeft.setLockAxis( node.getLocalRotation().getRotationColumn( 1 ) );
        addAction( rotateLeft, "turnLeft", true );
    }
}
