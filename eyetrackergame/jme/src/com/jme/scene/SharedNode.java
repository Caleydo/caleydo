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

import com.jme.renderer.Renderer;
import com.jme.scene.state.RenderState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * SharedNode allows the sharing of data
 * @author Mark Powell
 *
 */
public class SharedNode extends Node {

	private static final long serialVersionUID = 1L;
	
	private boolean updatesCollisionTree;
    
    public SharedNode() {
        
    }

    /**
     * Constructor creates a new <code>SharedNode</code> object.
     * 
     * @param name
     *            the name of this shared mesh.
     * @param target
     *            the Node to share the data.
     */
    public SharedNode(String name, Node target) {
        super(name);
        setTarget(target);
        // reset here because otherwise it is set from target.
        setName(name);
    }

    /**
     * Constructor creates a new <code>SharedNode</code> object.
     * 
     * @param target
     *            the Node to share the data.
     */
    public SharedNode(Node target) {
        super(target.getName());
        setTarget(target);
    }

    @Override
    public void draw(Renderer r) {
        super.draw(r);
    }

	/**
	 * <code>setTarget</code> sets the shared data.
	 * 
	 * @param target
	 *            the Node to share the data.
	 */
	protected void setTarget(Node target) {
        if (target.getChildren() != null) {
            for(int i = 0; i < target.getChildren().size(); i++) {
                processTarget(this, target.getChild(i));
            }
        }
        copyNode(target, this);
        UserDataManager.getInstance().bind(this, target);
	}
	
	private void processTarget(Node parent, Spatial target) {
		if(target instanceof Node) {
			Node ntarget = (Node)target;
			Node node = new Node();

	        UserDataManager.getInstance().bind(node, target);
            copyNode(ntarget, node);			
			parent.attachChild(node);
			
            if (ntarget.getChildren() != null)
    			for(int i = 0; i < ntarget.getChildren().size(); i++) {
    				processTarget(node, ntarget.getChild(i));
    			}
			
		} else if(target instanceof TriMesh) {
			if(target instanceof SharedMesh) {
				SharedMesh copy = new SharedMesh((SharedMesh)target);
				parent.attachChild(copy);
			} else {
				SharedMesh copy = new SharedMesh((TriMesh)target);
				parent.attachChild(copy);
			}
		}
	}

    private void copyNode(Node original, Node copy) {
        copy.setName(original.getName());
        copy.setCullHint(original.getLocalCullHint());
        copy.setLightCombineMode(original.getLocalLightCombineMode());
        copy.getLocalRotation().set(original.getLocalRotation());
        copy.getLocalScale().set(original.getLocalScale());
        copy.getLocalTranslation().set(original.getLocalTranslation());
        copy.setRenderQueueMode(original.getLocalRenderQueueMode());
        copy.setTextureCombineMode(original.getLocalTextureCombineMode());
        copy.setZOrder(original.getZOrder());
        
        
        for (RenderState.StateType type : RenderState.StateType.values()) {
            RenderState state = original.getRenderState( type );
            if (state != null) {
                copy.setRenderState(state );
            }
        }
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(updatesCollisionTree, "updatesCollisionTree", false);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        updatesCollisionTree = capsule.readBoolean("updatesCollisionTree", false);
    }
}
