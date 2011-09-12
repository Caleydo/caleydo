package org.caleydo.core.util.color;

import javax.xml.bind.annotation.XmlType;

/**
 * Class representing a color using RGBA values.
 * 
 * @author Partl
 *
 */
@XmlType
public class Color {
	public float r;
	public float g;
	public float b;
	public float a;
	
	public Color() {
		
	}

	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1;
	}

	public float[] getRGB() {
		return new float[] { r, g, b };
	}

	public float[] getRGBA() {
		return new float[] { r, g, b, a };
	}
}
