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

package com.jme.scene.shape;

import java.io.IOException;

import com.jme.math.FastMath;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

/**
 * A hollow cylindrical shape (the cylinder wall can have width).
 * 
 * @author Landei
 * @author Ahmed Abdelkader (added the centralAngle parameter)
 */
public class Tube extends TriMesh implements Savable {

	private static final long serialVersionUID = 1L;

	@Deprecated
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	private int axisSamples;
	private int radialSamples;

	private float outerRadius;
	private float innerRadius;
	private float height;
	private float centralAngle;

	/**
	 * Constructor meant for Savable use only.
	 */
	public Tube() {
	}

	public Tube(String name, float outerRadius, float innerRadius, float height) {
		this(name, outerRadius, innerRadius, height, 2, 20, FastMath.TWO_PI);
	}

	public Tube(String name, float outerRadius, float innerRadius,
			float height, float centralAngle) {
		this(name, outerRadius, innerRadius, height, 2, (int) (20 * FastMath
				.ceil(centralAngle * FastMath.INV_TWO_PI)), centralAngle);
	}

	public Tube(String name, float outerRadius, float innerRadius,
			float height, int axisSamples, int radialSamples, float centralAngle) {
		super(name);
		updateGeometry(outerRadius, innerRadius, height, axisSamples, radialSamples, centralAngle);
	}

	public float getCentralAngle() {
		return centralAngle;
	}

	public int getAxisSamples() {
		return axisSamples;
	}

	public float getHeight() {
		return height;
	}

	public float getInnerRadius() {
		return innerRadius;
	}

	public float getOuterRadius() {
		return outerRadius;
	}

	public int getRadialSamples() {
		return radialSamples;
	}

	@Override
	public void read(JMEImporter e) throws IOException {
		super.read(e);
		InputCapsule capsule = e.getCapsule(this);
		int axisSamples = capsule.readInt("axisSamples", 0);
		int radialSamples = capsule.readInt("radialSamples", 0);
		float outerRadius = capsule.readFloat("outerRadius", 0);
		float innerRadius = capsule.readFloat("innerRadius", 0);
		float height = capsule.readFloat("height", 0);
		float centralAngle = capsule.readFloat("centralAngle", FastMath.TWO_PI);
		updateGeometry(outerRadius, innerRadius, height, axisSamples, radialSamples, centralAngle);
	}

	/**
	 * @deprecated Use {@link #updateGeometry(float, float, float, int, int, float)} instead.
	 */
	public void setAxisSamples(int axisSamples) {
		updateGeometry(outerRadius, innerRadius, height, axisSamples, radialSamples, centralAngle);
	}

	/**
	 * @deprecated Use {@link #updateGeometry(float, float, float, int, int, float)} instead.
	 */
	public void setCentralAngle(float centralAngle) {
		updateGeometry(outerRadius, innerRadius, height, axisSamples, radialSamples, centralAngle);
	}

	private void setGeometryData() {
		float inverseRadial = 1.0f / radialSamples;
		float axisStep = height / axisSamples;
		float axisTextureStep = 1.0f / axisSamples;
		float halfHeight = 0.5f * height;
		float innerOuterRatio = innerRadius / outerRadius;
		float[] sin = new float[radialSamples + 1];
		float[] cos = new float[radialSamples + 1];

		for (int radialCount = 0; radialCount <= radialSamples; radialCount++) {
			float angle = centralAngle * inverseRadial * radialCount;
			cos[radialCount] = FastMath.cos(angle);
			sin[radialCount] = FastMath.sin(angle);
		}

		// outer cylinder
		for (int radialCount = 0; radialCount <= radialSamples; radialCount++) {
			for (int axisCount = 0; axisCount <= axisSamples; axisCount++) {
				getVertexBuffer()
						.put(cos[radialCount] * outerRadius)
						.put(axisStep * axisCount - halfHeight)
						.put(sin[radialCount] * outerRadius);
				getNormalBuffer()
						.put(cos[radialCount])
						.put(0).put(sin[radialCount]);
				getTextureCoords(0).coords
						.put((radialSamples - radialCount) * inverseRadial)
						.put(axisTextureStep * axisCount);
			}
		}
		// inner cylinder
		for (int radialCount = 0; radialCount <= radialSamples; radialCount++) {
			for (int axisCount = 0; axisCount <= axisSamples; axisCount++) {
				getVertexBuffer()
						.put(cos[radialCount] * innerRadius)
						.put(axisStep * axisCount - halfHeight)
						.put(sin[radialCount] * innerRadius);
				getNormalBuffer()
						.put(-cos[radialCount])
						.put(0).put(-sin[radialCount]);
				getTextureCoords(0).coords
				        .put(radialCount * inverseRadial)
				        .put(axisTextureStep * axisCount);
			}
		}
		// bottom edge
		for (int radialCount = 0; radialCount <= radialSamples; radialCount++) {
			getVertexBuffer()
			        .put(cos[radialCount] * outerRadius)
			        .put(-halfHeight).put(sin[radialCount] * outerRadius);
			getVertexBuffer()
			        .put(cos[radialCount] * innerRadius)
			        .put(-halfHeight).put(sin[radialCount] * innerRadius);
			getNormalBuffer().put(0).put(-1).put(0);
			getNormalBuffer().put(0).put(-1).put(0);
			getTextureCoords(0).coords
			        .put(0.5f + 0.5f * cos[radialCount])
			        .put(0.5f + 0.5f * sin[radialCount]);
			getTextureCoords(0).coords
			        .put(0.5f + innerOuterRatio * 0.5f * cos[radialCount])
			        .put(0.5f + innerOuterRatio * 0.5f * sin[radialCount]);
		}
		// top edge
		for (int radialCount = 0; radialCount <= radialSamples; radialCount++) {
			getVertexBuffer()
			        .put(cos[radialCount] * outerRadius)
			        .put(halfHeight).put(sin[radialCount] * outerRadius);
			getVertexBuffer()
			        .put(cos[radialCount] * innerRadius)
			        .put(halfHeight).put(sin[radialCount] * innerRadius);
			getNormalBuffer().put(0).put(1).put(0);
			getNormalBuffer().put(0).put(1).put(0);
			getTextureCoords(0).coords
			        .put(0.5f + 0.5f * cos[radialCount])
			        .put(0.5f + 0.5f * sin[radialCount]);
			getTextureCoords(0).coords
			        .put(0.5f + innerOuterRatio * 0.5f * cos[radialCount])
			        .put(0.5f + innerOuterRatio * 0.5f * sin[radialCount]);
		}
		// vertical edges
		for (int radialCount = 0; radialCount <= radialSamples; radialCount+=radialSamples) {
			for (int axisCount = 0; axisCount <= axisSamples; axisCount++) {
				getVertexBuffer()
						.put(cos[radialCount] * outerRadius)
						.put(axisStep * axisCount - halfHeight)
						.put(sin[radialCount] * outerRadius);
				getVertexBuffer()
						.put(cos[radialCount] * innerRadius)
						.put(axisStep * axisCount - halfHeight)
						.put(sin[radialCount] * innerRadius);
				getNormalBuffer()
						.put(cos[radialCount])
						.put(0).put(sin[radialCount]);
				getNormalBuffer()
						.put(-cos[radialCount])
						.put(0).put(-sin[radialCount]);
				getTextureCoords(0).coords
						.put(radialCount * inverseRadial)
						.put(axisTextureStep * axisCount);
				getTextureCoords(0).coords
						.put((radialSamples - radialCount) * inverseRadial)
						.put(axisTextureStep * axisCount);
			}
		}

	}

	/**
	 * @deprecated Use {@link #updateGeometry(float, float, float, int, int, float)} instead.
	 */
	public void setHeight(float height) {
		updateGeometry(outerRadius, innerRadius, height, axisSamples, radialSamples, centralAngle);
	}

	private void setIndexData() {
		int axisSamplesPlusOne = axisSamples + 1;
		int innerCylinder = axisSamplesPlusOne * (radialSamples + 1);
		int bottomEdge = 2 * innerCylinder;
		int topEdge = bottomEdge + 2 * (radialSamples + 1);
		int verEdge1 = topEdge + 2 * (radialSamples + 1);
		int verEdge2 = verEdge1 + 2 * axisSamplesPlusOne;
		// outer cylinder
		for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
			for (int axisCount = 0; axisCount < axisSamples; axisCount++) {
				int index0 = axisCount + axisSamplesPlusOne * radialCount;
				int index1 = index0 + 1;
				int index2 = index0 + axisSamplesPlusOne;
				int index3 = index2 + 1;
				getIndexBuffer().put(index0).put(index1).put(index2);
				getIndexBuffer().put(index1).put(index3).put(index2);
			}
		}

		// inner cylinder
		for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
			for (int axisCount = 0; axisCount < axisSamples; axisCount++) {
				int index0 = innerCylinder + axisCount + axisSamplesPlusOne
						* radialCount;
				int index1 = index0 + 1;
				int index2 = index0 + axisSamplesPlusOne;
				int index3 = index2 + 1;
				getIndexBuffer().put(index0).put(index2).put(index1);
				getIndexBuffer().put(index1).put(index2).put(index3);
			}
		}

		// bottom edge
		for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
			int index0 = bottomEdge + 2 * radialCount;
			int index1 = index0 + 1;
			int index2 = index1 + 1;
			int index3 = index2 + 1;
			getIndexBuffer().put(index0).put(index2).put(index1);
			getIndexBuffer().put(index1).put(index2).put(index3);
		}

		// top edge
		for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
			int index0 = topEdge + 2 * radialCount;
			int index1 = index0 + 1;
			int index2 = index1 + 1;
			int index3 = index2 + 1;
			getIndexBuffer().put(index0).put(index1).put(index2);
			getIndexBuffer().put(index1).put(index3).put(index2);
		}

		// vertical edge0
		for (int axisCount = 0; axisCount < axisSamples; axisCount++) {
			int index0 = verEdge1 + 2 * axisCount;
			int index1 = index0 + 1;
			int index2 = index1 + 1;
			int index3 = index2 + 1;
			getIndexBuffer().put(index0).put(index2).put(index1);
			getIndexBuffer().put(index1).put(index2).put(index3);
		}

		// vertical edge1
		for (int axisCount = 0; axisCount < axisSamples; axisCount++) {
			int index0 = verEdge2 + 2 * axisCount;
			int index1 = index0 + 1;
			int index2 = index1 + 1;
			int index3 = index2 + 1;
			getIndexBuffer().put(index0).put(index2).put(index1);
			getIndexBuffer().put(index1).put(index2).put(index3);
		}
	}

	/**
	 * @deprecated Use {@link #updateGeometry(float, float, float, int, int, float)} instead.
	 */
	public void setInnerRadius(float innerRadius) {
		updateGeometry(outerRadius, innerRadius, height, axisSamples, radialSamples, centralAngle);
	}

	/**
	 * @deprecated Use {@link #updateGeometry(float, float, float, int, int, float)} instead.
	 */
	public void setOuterRadius(float outerRadius) {
		updateGeometry(outerRadius, innerRadius, height, axisSamples, radialSamples, centralAngle);
	}

	/**
	 * @deprecated Use {@link #updateGeometry(float, float, float, int, int, float)} instead.
	 */
	public void setRadialSamples(int radialSamples) {
		updateGeometry(outerRadius, innerRadius, height, axisSamples, radialSamples, centralAngle);
	}

	public void updateGeometry(float outerRadius, float innerRadius,
			float height, int axisSamples, int radialSamples, float centralAngle) {
		this.outerRadius = outerRadius;
		this.innerRadius = innerRadius;
		this.height = height;
		this.axisSamples = axisSamples;
		this.radialSamples = radialSamples;
		this.centralAngle = FastMath.normalize(centralAngle, -FastMath.TWO_PI, FastMath.TWO_PI);
		setVertexCount(2 * (axisSamples + 1) * (radialSamples + 1)
				+ 4 * (radialSamples + 1) + 4 * (axisSamples + 1));
		setVertexBuffer(BufferUtils.createVector3Buffer(
				getVertexBuffer(), getVertexCount()));
		setNormalBuffer(BufferUtils.createVector3Buffer(
				getNormalBuffer(), getVertexCount()));
        getTextureCoords().set(0,
                new TexCoords(BufferUtils.createVector2Buffer(getVertexCount())));
		setTriangleQuantity(4 * (radialSamples + 1) * (axisSamples + 1));
		setIndexBuffer(BufferUtils.createIntBuffer(
				getIndexBuffer(), 3 * getTriangleCount()));

		setGeometryData();
		setIndexData();
	}

	@Override
	public void write(JMEExporter e) throws IOException {
		super.write(e);
		OutputCapsule capsule = e.getCapsule(this);
		capsule.write(getAxisSamples(), "axisSamples", 0);
		capsule.write(getRadialSamples(), "radialSamples", 0);
		capsule.write(getOuterRadius(), "outerRadius", 0);
		capsule.write(getInnerRadius(), "innerRadius", 0);
		capsule.write(getHeight(), "height", 0);
		capsule.write(getCentralAngle(), "centralAngle", FastMath.TWO_PI);
	}
}