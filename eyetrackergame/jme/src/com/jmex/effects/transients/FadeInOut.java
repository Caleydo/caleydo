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

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.state.BlendState;
import com.jme.system.DisplaySystem;

/**
 * A <code>FadeInOut</code> object is made to be controlled by a <code>
 * FadeInOutController</code>.  It has 3 parts:<br>
 * 1) A Geometry who's per vertex color is to be changed by a <code>FadeInOutController</code>.
 * 2) A begining (fadeOutNode) node that represents the <code>FadeInOut</code> at the begining.
 * 3) An ending (fadeInNode) node that represents the <code>FadeInOut</code> when the <code>
 * FadeInOutController</code> has reached the next stage.<br>
 * When the next stage is reached by the controller, the begining node is detached and
 * the ending node is attached.  It is assumed the (1) geometry can fade to cover
 * up the change.
 *
 * @see FadeInOutController
 * @author Ahmed
 * @author Jack Lindamood (javadoc only)
 */
public class FadeInOut extends Transient {

    private static final long serialVersionUID = 1L;
	private Geometry fadeQ;
    private Node fadeInNode, fadeOutNode;
    private ColorRGBA fadeColor;
    private float speed;

    /**
     * Creates a new FadeInOut node. The speed is by default .01f
     * @param name The name of the node.
     * @param fade The geometry whos per vertex color will fade over time.
     * @param out The begining node that will fade out.
     * @param in The ending node that will fade in.
     * @param c The begining color of the fade Geometry.
     */
    public FadeInOut(String name, Geometry fade, Node out, Node in, ColorRGBA c) {
        super(name);
        initialise(fade, out, in, c, 0.01f);
    }

    /**
     * Creates a new FadeInOut node.
     * @param name The name of the node.
     * @param fade The geometry whos per vertex color will fade over time.
     * @param out The begining node that will fade out.
     * @param in The ending node that will fade in.
     * @param c The begining color of the fade geometry.
     * @param s The speed at which the fade will take place.
     */
    public FadeInOut(String name, Geometry fade, Node out, Node in, ColorRGBA c, float s) {
        super(name);
        initialise(fade, out, in, c, s);
    }

    private void initialise(Geometry fade, Node out, Node in, ColorRGBA c, float speed) {
        setMaxNumOfStages(2);
        setCurrentStage(0);
        setSpeed(speed);

        fadeColor = (ColorRGBA)c.clone();
        fadeColor.a = 0;
        
        fadeInNode = in;
        fadeOutNode = out;
        fadeQ = fade;

        BlendState fadeAS = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
		fadeAS.setBlendEnabled(true);
		fadeAS.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		fadeAS.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
		fadeAS.setTestEnabled(false);
		fadeAS.setTestFunction(BlendState.TestFunction.GreaterThanOrEqualTo);
		fadeAS.setEnabled(true);
		
		fadeQ.setRenderState(fadeAS);
        
        this.attachChild(fadeOutNode);
    }
    
    /**
     * Returns the Geometry that is fading.
     * @return The currently fading geometry.
     */
    public Geometry getFadeQuad() {
        return fadeQ;
    }

    /**
     * Sets the geometry that will fade.
     * @param f The new geometry that will fade.
     */
    public void setFadeQuad(Geometry f) {
        fadeQ = f;
    }
    
    /**
     * Returns the node this object is fading into.
     * @return The current fade in node.
     */
    public Node getFadeInNode() {
    	return fadeInNode;
    }

    /**
     * Sets the node that this object will fade into.
     * @param fade The node to fade into.
     */
    public void setFadeInNode(Node fade) {
    	fadeInNode = fade;
    }
    
    /**
     * Returns the node this object is fading from.
     * @return The current fade out node.
     */
    public Node getFadeOutNode() {
    	return fadeOutNode;
    }

    /**
     * Sets the node this object will fade from.
     * @param fade The new fade out node.
     */
    public void setFadeOutNode(Node fade) {
    	fadeOutNode = fade;
    }

    /**
     * Returns the current color being applied to the fade quad.
     * @return The current fade color.
     */
    public ColorRGBA getFadeColor() {
        return fadeColor;
    }

    /**
     * Sets the current per vertex color of the fade quad, and updates the
     * current fade color to c.
     * @param c The new color to set the fade quad too.
     */
    public void setFadeColor(ColorRGBA c) {
    	fadeColor = (ColorRGBA)c.clone();
    	fadeQ.setDefaultColor(fadeColor);
    }

    /**
     * Returns the speed that this object should fade at.
     * @return The current speed.
     */ 
    public float getSpeed() {
        return speed;
    }

    /**
     * Sets the speed this object should fade at.
     * @param s The new speed.
     */
    public void setSpeed(float s) {
        speed = s;
    }

    /**
     * Ignoring children, this only updates all the controllers of this FadeInOut
     * @param time the time to pass to update.
     */
    public void updateWorldData(float time) {
        if (getControllers().size() != 0) {
        	for (int i = 0; i < getControllers().size(); i++) {
        		(getController(i)).update(time);
        	}
        }
    }

}
