/**
 *
 */
package gleem.linalg.open;

import gleem.linalg.Veci;

/**
 * @author Michael Kalkusch
 */
public class Vec4i extends Vec2i {

	protected static final int Z = 2;
	protected static final int W = 3;

	/**
	 *
	 */
	public Vec4i() {

		super(4);
	}

	public Vec4i(int x, int y, int z, int w) {

		super(4);

		this.setXYZW(x, y, z, w);
	}

	/**
	 *
	 */
	public Vec4i(final Veci arg) {

		super(4);
		for (int i = 0; i < arg.length(); i++) {
			setComponent(i, arg.get(i));
		}
	}

	public final int z() {
		return get(Z);
	}

	public final int w() {
		return get(W);
	}

	public final void setZ(int z) {
		setComponent(Z, z);
	}

	public final void setW(int w) {
		setComponent(W, w);
	}

	public final void setXYZW(final int x, final int y, final int z, final int w) {
		setComponent(X, x);
		setComponent(Y, y);
		setComponent(Z, z);
		setComponent(W, w);
	}

	public final void set(final Vec4i copy) {
		setComponent(X, copy.x());
		setComponent(Y, copy.y());
		setComponent(Z, copy.z());
		setComponent(W, copy.w());
	}
}
