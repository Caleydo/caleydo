/**
 *
 */
package gleem.linalg.open;

import gleem.linalg.Veci;

/**
 * @author Michael Kalkusch
 */
public class Vec2i extends Veci {

	protected static final int X = 0;
	protected static final int Y = 1;

	/**
	 *
	 */
	public Vec2i() {

		super(2);
	}

	/**
	 * @param arg
	 */
	public Vec2i(Veci arg) {

		super(2);
		this.setXY(arg.get(X), arg.get(Y));
	}

	/**
	 * @param arg
	 *            copy values
	 */
	public Vec2i(Vec2i arg) {

		super(2);
		this.set(arg);
	}

	/**
	 * @param size
	 *            specify size
	 */
	protected Vec2i(final int size) {

		super(size);
	}

	public final int x() {
		return get(X);
	}

	public final int y() {
		return get(Y);
	}

	public final void set(Vec2i arg) {
		setComponent(X, arg.x());
		setComponent(Y, arg.y());
	}

	public final void setX(int x) {
		setComponent(X, x);
	}

	public final void setY(int y) {
		setComponent(Y, y);
	}

	public final void setXY(int x, int y) {
		setComponent(X, x);
		setComponent(Y, y);
	}
}
