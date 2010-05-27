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
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.image.Texture.WrapMode;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.scene.shape.Tube;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * @author Ahmed Abdelkader
 */
public class TubeAngleTest extends SimpleGame {

	private Tube tube;
	private float outerRadius = 10;
	private float innerRadius = 6;
	private float height = 30;
	private int axisSamples = 1;
	private int radialSamples = 100;
	private float centralAngle = 270;
	private int angleSign = 1;
	private int radiusSign = 1;
	
	protected void simpleInitGame() {
		display.setTitle("Tube-Angle Test");
		
		tube = new Tube("", outerRadius, innerRadius, height, axisSamples, radialSamples, centralAngle);
		tube.setLocalTranslation(0, -5, -40);
		tube.setLocalRotation(new Quaternion().fromAngles(0, -FastMath.HALF_PI, -FastMath.QUARTER_PI));
		rootNode.attachChild(tube);

		TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
		ts.setTexture(TextureManager.loadTexture(
				TubeAngleTest.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
				MinificationFilter.Trilinear, MagnificationFilter.Bilinear));
		ts.getTexture().setWrap(WrapMode.Repeat);
		rootNode.setRenderState(ts);
	}
	
	@Override
	protected void simpleUpdate() {
		if(tpf < 1) {
			centralAngle = centralAngle + angleSign * tpf * 360;
			if(FastMath.abs(centralAngle) > 360) { 
				angleSign = -angleSign;
				if(innerRadius == outerRadius || innerRadius == 0)
					radiusSign = -radiusSign;
				innerRadius += radiusSign * 1;
			}
		}
		tube.updateGeometry(outerRadius, innerRadius, height, axisSamples, radialSamples, centralAngle * FastMath.PI/180);
	}
	
	public static void main(String[] args) {
		new TubeAngleTest().start();
	}
}
