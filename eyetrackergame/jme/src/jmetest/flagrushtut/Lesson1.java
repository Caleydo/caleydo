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

package jmetest.flagrushtut;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * First example class shows how to create a window/application using 
 * SimpleGame. This will do nothing but display a Sphere in the center.
 * For Flag Rush Tutorial Series.
 * @author mark powell
 *
 */
public class Lesson1 extends SimpleGame {
	/**
	 * Main method is the entry point for this lesson. It creates a 
	 * SimpleGame and tells the dialog to always appear. It then 
	 * starts the main loop.
	 * @param args
	 */
	public static void main(String[] args) {
		Lesson1 app = new Lesson1();
	    app.setConfigShowMode(ConfigShowMode.AlwaysShow);
	    app.start();
	}

	/**
	   * sets the title of the window, creates a sphere and textures it
	   * with the monkey.
	   * @see com.jme.app.SimpleGame#initGame()
	   */
	  protected void simpleInitGame() {
		  display.setTitle("Tutorial 1");
		  
		  Sphere s = new Sphere("Sphere", 30, 30, 25);
		  s.setLocalTranslation(new Vector3f(0,0,-40));
		  s.setModelBound(new BoundingBox());
		  s.updateModelBound();
		    
		  TextureState ts = display.getRenderer().createTextureState();
		  ts.setEnabled(true);
		  ts.setTexture(
		            TextureManager.loadTexture(
		                Lesson1.class.getClassLoader().getResource(
		                "jmetest/data/images/Monkey.jpg"),
		                Texture.MinificationFilter.Trilinear,
		                Texture.MagnificationFilter.Bilinear));
		 
		  s.setRenderState(ts);
		 
		  rootNode.attachChild(s);
	  }

}
