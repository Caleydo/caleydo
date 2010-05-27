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

package com.jme.scene.lod;

import java.io.IOException;

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.SwitchModel;
import com.jme.scene.SwitchNode;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>DiscreteLodNode</code>
 * @author Mark Powell
 * @version $Id: DiscreteLodNode.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class DiscreteLodNode extends SwitchNode implements Savable {
	private static final long serialVersionUID = 1L;
	private Vector3f modelCenter;
	private Vector3f worldCenter=new Vector3f();

    private static Vector3f tmpVs = new Vector3f();

	private SwitchModel model;

    public DiscreteLodNode() {}
    
	public DiscreteLodNode(String name, SwitchModel model) {
		super(name);
		this.model = model;

		modelCenter = new Vector3f();

	}
	
	/**
	 * Gets the switch model associated with this node.
	 * @return
	 */
	public	SwitchModel		getSwitchModel() {
		return model;
	}

	public void selectLevelOfDetail (Camera camera) {
        if(model == null) {
            return;
        }
		// compute world LOD center
        worldCenter = worldRotation.multLocal(worldCenter.set(modelCenter)).multLocal(worldScale).addLocal(worldTranslation);

		// compute world squared distance intervals
                
		float worldSqrScale = tmpVs.set(worldScale).multLocal(worldScale).length();
		model.set(worldCenter.subtractLocal(camera.getLocation()));
		model.set(new Float(worldSqrScale));
		setActiveChild(model.getSwitchChild());

	}

	public void draw (Renderer r) {
		selectLevelOfDetail(r.getCamera());
		super.draw(r);
	}
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(modelCenter, "modelCenter", Vector3f.ZERO);
        capsule.write(worldCenter, "worldCenter", Vector3f.ZERO);
        capsule.write(model, "model", null);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        modelCenter = (Vector3f)capsule.readSavable("modelCenter", Vector3f.ZERO.clone());
        worldCenter = (Vector3f)capsule.readSavable("worldCenter", Vector3f.ZERO.clone());
        model = (SwitchModel)capsule.readSavable("model", null);
    }
}
