/**
 * 
 */
package gleem.linalg.open;

import gleem.linalg.Veci;


/**
 * @author Michael Kalkusch
 *
 */
public class Vec2i extends Veci {

	protected static final int X = 0;
	protected static final int Y = 1;
	
	/**
	 * @param n
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
	 * @param n
	 */
	protected Vec2i(int size) {

		super(size);
	}
	
	public final int x() {
		return get(X);
	}
	
	public final int y() {
		return get(Y);
	}
	
	public final void setX(int x) {
		set(X,x);
	}
	
	public final void setY(int y) {
		set(Y,y);
	}

	public final void setXY( int x, int y) {
		set(X, x);
		set(Y, y);
	}
}
