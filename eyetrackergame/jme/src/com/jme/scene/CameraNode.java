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

package com.jme.scene;

import java.io.IOException;

import com.jme.renderer.Camera;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>CameraNode</code> defines a node that contains a camera object. This
 * allows a camera to be controlled by any other node, and allows the camera to
 * be attached to any node. A call to <code>updateWorldData</code> will adjust
 * the camera's frame by the world translation and the world rotation. The
 * column 0 of the world rotation matrix is used for the camera left vector,
 * column 1 is used for the camera up vector, column 2 is used for the camera
 * direction vector.
 * 
 * @author Mark Powell
 * @version $Id: CameraNode.java 4636 2009-08-28 14:52:25Z skye.book $
 */
public class CameraNode extends Node {
	private static final long serialVersionUID = 1L;

	private Camera camera;

    
    public CameraNode() {}
	/**
	 * Constructor instantiates a new <code>CameraNode</code> object setting
	 * the camera to use for the frame reference.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparison purposes.
	 * @param camera
	 *            the camera this node controls.
	 */
	public CameraNode(String name, Camera camera) {
		super(name);
		this.camera = camera;
	}

    /**
     * Forces rotation and translation of this node to be consistent with the
     * attached camera. (Assumes the node is in world space.)
     */
    public void updateFromCamera() {
        getLocalRotation().fromAxes(camera.getLeft(), camera.getUp(),
                camera.getDirection());
        getLocalTranslation().set(camera.getLocation());
    }
    
	/**
     * <code>setCamera</code> sets the camera that this node controls.
     * 
     * @param camera
     *            the camera that this node controls.
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

	/**
     * <code>getCamera</code> retrieves the camera object that this node
     * controls.
     * 
     * @return the camera this node controls.
     */
	public Camera getCamera() {
		return camera;
	}

	/**
	 * <code>updateWorldData</code> updates the rotation and translation of
	 * this node, and sets the camera's frame buffer to reflect the current
	 * view.
	 * 
	 * @param time
	 *            the time between frames.
	 */
	public void updateWorldData(float time) {
		super.updateWorldData(time);
		if (camera != null) {
			camera.setFrame(worldTranslation, worldRotation);
		}
	}
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(camera, "camera", null);
        
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        camera = (Camera)capsule.readSavable("camera", null);
        
    }
}