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

import com.jme.input.Mouse;
import com.jme.input.MouseInput;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

/**
 * <code>NodeMouseLook</code> defines a mouse action that detects mouse
 * movement and converts it into node rotations and node tilts.
 * 
 * @author Mark Powell
 * @version $Id: NodeMouseLook.java 4570 2009-08-09 04:37:47Z skye.book $
 */
public class NodeMouseLook extends MouseInputAction {

    //the actions that handle looking up, down, left and right.
    private KeyNodeLookDownAction lookDown;

    private KeyNodeLookUpAction lookUp;

    private KeyNodeRotateLeftAction rotateLeft;

    private KeyNodeRotateRightAction rotateRight;

    //the axis to lock
    private Vector3f lockAxis;

    //the node to control
    private Spatial node;

    //the event to distribute to the look actions.
    private static InputActionEvent event;
    
    private boolean buttonPressRequired = false;

	private int mouseButtonForRequired = 0;

    /**
     * Constructor creates a new <code>NodeMouseLook</code> object. It takes
     * the mouse, node and speed of the looking.
     * 
     * @param mouse
     *            the mouse to calculate view changes.
     * @param node
     *            the node to move.
     * @param speed
     *            the speed at which to alter the camera.
     */
    public NodeMouseLook(Mouse mouse, Spatial node, float speed) {
        this.mouse = mouse;
        this.speed = speed;
        this.node = node;

        lookDown = new KeyNodeLookDownAction(this.node, speed);
        lookUp = new KeyNodeLookUpAction(this.node, speed);
        rotateLeft = new KeyNodeRotateLeftAction(this.node, speed);
        rotateRight = new KeyNodeRotateRightAction(this.node, speed);
        
        event = new InputActionEvent();
    }

    /**
     * 
     * <code>setLockAxis</code> sets the axis that should be locked down. This
     * prevents "rolling" about a particular axis. Typically, this is set to the
     * mouse's up vector.
     * 
     * @param lockAxis
     *            the axis that should be locked down to prevent rolling.
     */
    public void setLockAxis(Vector3f lockAxis) {
        this.lockAxis = lockAxis;
        rotateLeft.setLockAxis(lockAxis);
        rotateRight.setLockAxis(lockAxis);
    }

    /**
     * Returns the axis that is currently locked.
     * 
     * @return The currently locked axis
     * @see #setLockAxis(com.jme.math.Vector3f)
     */
    public Vector3f getLockAxis() {
        return lockAxis;
    }

    /**
     * 
     * <code>setSpeed</code> sets the speed of the mouse look.
     * 
     * @param speed
     *            the speed of the mouse look.
     */
    public void setSpeed(float speed) {
        super.setSpeed( speed );
        lookDown.setSpeed(speed);
        lookUp.setSpeed(speed);
        rotateRight.setSpeed(speed);
        rotateLeft.setSpeed(speed);

    }
    
    /**
     * Sets the option for requiring the user to click the mouse
     * button specified by <code>mouseButtonForRequired</code> in
     * order to rotate the node.
	 * @param buttonPressRequired the buttonPressRequired to set
	 */
	public void setButtonPressRequired(boolean buttonPressRequired) {
		this.buttonPressRequired = buttonPressRequired;
	}

	/**
	 * Sets which mouse button needs to be pressed in order to rotate
	 * the node (that is, assuming <code>buttonPressRequired</code> is
	 * set to true).
	 * @param mouseButtonForRequired the mouseButtonForRequired to set
	 */
	public void setMouseButtonForRequired(int mouseButtonForRequired) {
		this.mouseButtonForRequired = mouseButtonForRequired;
	}

    /**
     * <code>performAction</code> checks for any movement of the mouse, and
     * calls the appropriate method to alter the node's orientation when
     * applicable.
     * 
     * @see com.jme.input.action.MouseInputAction#performAction(InputActionEvent) 
     */
    public void performAction(InputActionEvent evt) {
        float time = 0.01f * speed;

        if (buttonPressRequired && MouseInput.get().isButtonDown(mouseButtonForRequired) || !buttonPressRequired)
        {
        	if (mouse.getLocalTranslation().x > 0) {
        		event.setTime(time * mouse.getLocalTranslation().x);
        		rotateRight.performAction(event);
        	} else if (mouse.getLocalTranslation().x < 0) {
        		event.setTime(time * mouse.getLocalTranslation().x * -1);
        		rotateLeft.performAction(event);
        	}
        	if (mouse.getLocalTranslation().y > 0) {
        		event.setTime(time * mouse.getLocalTranslation().y);
        		lookUp.performAction(event);
        	} else if (mouse.getLocalTranslation().y < 0) {
        		event.setTime(time * mouse.getLocalTranslation().y * -1);
        		lookDown.performAction(event);
        	}
        }
    }

}