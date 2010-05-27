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
package jmetest.shape;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Dome;
import com.jme.scene.state.CullState;
import com.jme.scene.state.CullState.PolygonWind;

public class TestInsideOutsideDomes extends SimpleGame {
	
	public static void main(String[] args) {
		TestInsideOutsideDomes test = new TestInsideOutsideDomes();
		test.setConfigShowMode(ConfigShowMode.AlwaysShow);
		test.start();
	}

	@Override
	protected void simpleInitGame() {
		this.buildInsideDome();
		this.buildOutsideDome();
		this.buildLight();
		this.rootNode.updateRenderState();
	}
	
	private void buildInsideDome() {
		Dome dome = new Dome("InsideDome", new Vector3f(), 32, 32, 20, false);
		dome.setModelBound(new BoundingBox());
		dome.updateModelBound();
		CullState cs = display.getRenderer().createCullState();
		cs.setCullFace(com.jme.scene.state.CullState.Face.Back);
		cs.setPolygonWind(PolygonWind.ClockWise);
		dome.setRenderState(cs);
		this.rootNode.attachChild(dome);
	}
	
	private void buildOutsideDome() {
		Dome dome = new Dome("OutsideDome", new Vector3f(50, 0, 0), 32, 32, 20, true);
		dome.setModelBound(new BoundingBox());
		dome.updateModelBound();
		CullState cs = display.getRenderer().createCullState();
		cs.setCullFace(com.jme.scene.state.CullState.Face.Back);
		cs.setPolygonWind(PolygonWind.CounterClockWise);
		dome.setRenderState(cs);
		this.rootNode.attachChild(dome);
	}
	
	private void buildLight() {
		this.lightState.detachAll();
		DirectionalLight light = new DirectionalLight();
		light.setDiffuse(ColorRGBA.white);
		light.setDirection(new Vector3f(.5f, -.5f, 0).normalizeLocal());
		light.setEnabled(true);
		this.lightState.attach(light);
	}
}
