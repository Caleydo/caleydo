/*
 * gleem -- OpenGL2 Extremely Easy-To-Use Manipulators. Copyright (C) 1998-2003 Kenneth B. Russell
 * (kbrussel@alum.mit.edu) Copying, distribution and use of this software in source and binary forms, with or
 * without modification, is permitted provided that the following conditions are met: Distributions of source
 * code must reproduce the copyright notice, this list of conditions and the following disclaimer in the
 * source code header files; and Distributions of binary code must reproduce the copyright notice, this list
 * of conditions and the following disclaimer in the documentation, Read me file, license file and/or other
 * materials provided with the software distribution. The names of Sun Microsystems, Inc. ("Sun") and/or the
 * copyright holder may not be used to endorse or promote products derived from this software without specific
 * prior written permission. THIS SOFTWARE IS PROVIDED "AS IS," WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR
 * IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, NON-INTERFERENCE, ACCURACY OF INFORMATIONAL CONTENT OR NON-INFRINGEMENT,
 * ARE HEREBY EXCLUDED. THE COPYRIGHT HOLDER, SUN AND SUN'S LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN
 * NO EVENT WILL THE COPYRIGHT HOLDER, SUN OR SUN'S LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA,
 * OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. YOU ACKNOWLEDGE THAT THIS SOFTWARE IS NOT DESIGNED, LICENSED OR
 * INTENDED FOR USE IN THE DESIGN, CONSTRUCTION, OPERATION OR MAINTENANCE OF ANY NUCLEAR FACILITY. THE
 * COPYRIGHT HOLDER, SUN AND SUN'S LICENSORS DISCLAIM ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR SUCH
 * USES.
 */

package gleem.linalg;

/**
 * 3-element single-precision vector
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */

public class Vec3f {
	/**
	 * PI stored in a float. Note: from (double) Math.PI
	 */
	public static final float fPI = (float) Math.PI;

	private static final float fConverteGrad2Rad = (float) (Math.PI / 180.0);

	private static final float fConverteRad2Grad = (float) (180.0 / Math.PI);

	private float x;
	private float y;
	private float z;

	public Vec3f() {
	}

	public Vec3f(Vec3f arg) {
		set(arg);
	}

	/**
	 * Copy x,y,z to Vec3f and ignores arg.w().
	 * 
	 * @param arg
	 */
	public Vec3f(Vec4f arg) {
		set(arg.x(), arg.y(), arg.z());
	}

	/**
	 * Convert grad to radiant (eg: [-360� .. +360�] ==> [-2PI .. 2PI]).
	 * 
	 * @param fGrad
	 *            in grad
	 * @return grad converted to radiant
	 */
	public static final float convertGrad2Radiant(final float fGrad) {
		return fGrad * fConverteGrad2Rad;
	}

	/**
	 * Convert radiant to grad (eg:[-2PI .. 2PI] ==> [-360� .. +360�]).
	 * 
	 * @param fRadiant
	 *            angle in radiant
	 * @return radiant converted to grad
	 */
	public static final float convertRadiant2Grad(final float fRadiant) {
		return fRadiant * fConverteRad2Grad;
	}

	public Vec3f(float x, float y, float z) {
		set(x, y, z);
	}

	/**
	 * all values are set to 0. x=y=z=w=0
	 */
	public void setZeroVector() {
		x = 0;
		y = 0;
		z = 0;
	}

	/**
	 * all values are set to 1. x=y=z=w=1
	 */
	public void setAllOneVector() {
		x = 1;
		y = 1;
		z = 1;
	}

	public Vec3f copy() {
		return new Vec3f(this);
	}

	/** Convert to double-precision */
	public Vec3d toDouble() {
		return new Vec3d(x, y, z);
	}

	public void set(Vec3f arg) {
		set(arg.x, arg.y, arg.z);
	}

	/**
	 * Copy x,y,z and ignores arg.w().
	 * 
	 * @param arg
	 */
	public void set(Vec4f arg) {
		set(arg.x(), arg.y(), arg.z());
	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/** Sets the ith component, 0 <= i < 3 */
	public void set(int i, float val) {
		switch (i) {
		case 0:
			x = val;
			break;
		case 1:
			y = val;
			break;
		case 2:
			z = val;
			break;
		default:
			throw new IndexOutOfBoundsException();
		}
	}

	/** Gets the ith component, 0 <= i < 3 */
	public float get(int i) {
		switch (i) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			throw new IndexOutOfBoundsException();
		}
	}

	public float x() {
		return x;
	}

	public float y() {
		return y;
	}

	public float z() {
		return z;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float dot(Vec3f arg) {
		return x * arg.x + y * arg.y + z * arg.z;
	}

	public float length() {
		return (float) Math.sqrt(lengthSquared());
	}

	public float lengthSquared() {
		return this.dot(this);
	}

	public void normalize() {
		float len = length();
		if (len == 0.0f)
			return;
		scale(1.0f / len);
	}

	/** Returns this * val; creates new vector */
	public Vec3f times(float val) {
		Vec3f tmp = new Vec3f(this);
		tmp.scale(val);
		return tmp;
	}

	/** this = this * val */
	public void scale(float val) {
		x *= val;
		y *= val;
		z *= val;
	}

	/** Returns this + arg; creates new vector */
	public Vec3f plus(Vec3f arg) {
		Vec3f tmp = new Vec3f();
		tmp.add(this, arg);
		return tmp;
	}

	/** this = this + b */
	public void add(Vec3f b) {
		add(this, b);
	}

	/** this = a + b */
	public void add(Vec3f a, Vec3f b) {
		x = a.x + b.x;
		y = a.y + b.y;
		z = a.z + b.z;
	}

	/** Returns this + s * arg; creates new vector */
	public Vec3f addScaled(float s, Vec3f arg) {
		Vec3f tmp = new Vec3f();
		tmp.addScaled(this, s, arg);
		return tmp;
	}

	/** this = a + s * b */
	public void addScaled(Vec3f a, float s, Vec3f b) {
		x = a.x + s * b.x;
		y = a.y + s * b.y;
		z = a.z + s * b.z;
	}

	/** Returns this - arg; creates new vector */
	public Vec3f minus(Vec3f arg) {
		Vec3f tmp = new Vec3f();
		tmp.sub(this, arg);
		return tmp;
	}

	/** this = this - b */
	public void sub(Vec3f b) {
		sub(this, b);
	}

	/** this = a - b */
	public void sub(Vec3f a, Vec3f b) {
		x = a.x - b.x;
		y = a.y - b.y;
		z = a.z - b.z;
	}

	/** Returns this cross arg; creates new vector */
	public Vec3f cross(Vec3f arg) {
		Vec3f tmp = new Vec3f();
		tmp.cross(this, arg);
		return tmp;
	}

	/**
	 * this = a cross b. NOTE: "this" must be a different vector than both a and
	 * b.
	 */
	public void cross(Vec3f a, Vec3f b) {
		x = a.y * b.z - a.z * b.y;
		y = a.z * b.x - a.x * b.z;
		z = a.x * b.y - a.y * b.x;
	}

	/**
	 * Sets each component of this vector to the product of the component with
	 * the corresponding component of the argument vector.
	 */
	public void componentMul(Vec3f arg) {
		x *= arg.x;
		y *= arg.y;
		z *= arg.z;
	}

	public Vecf toVecf() {
		Vecf out = new Vecf(3);
		for (int i = 0; i < 3; i++) {
			out.set(i, get(i));
		}
		return out;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
