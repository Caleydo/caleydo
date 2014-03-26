/**
 *
 */
package gleem.linalg.open;

import gleem.linalg.Veci;

/**
 * Container for 3 integer values.
 *
 * @author Michael Kalkusch
 */
public class Vec3i extends Veci {

	// protected int[] data;

	/**
	 *
	 */
	public Vec3i() {

		super(3);
	}

	/**
	 *
	 */
	public Vec3i(Veci source) {

		super(3);

		this.cloneFrom(source);
	}

	public int[] getData() {
		int[] dataBuffer = new int[3];
		for (int i = 0; i < this.length(); i++) {
			dataBuffer[i] = this.get(i);
		}
		return dataBuffer;
	}

	public void cloneFrom(Veci source) {
		for (int i = 0; i < source.length(); i++) {
			if (i > 2) {
				assert false : "access size of internal array; ignore other values";
				return;
			}
			this.setComponent(i, source.get(i));
		}
	}

	public void setData(int[] data) {
		for (int i = 0; i < data.length; i++) {
			if (i > 2) {
				assert false : "access size of internal array; ignore other values";
				return;
			}
			this.setComponent(i, data[i]);
		}
	}

}
