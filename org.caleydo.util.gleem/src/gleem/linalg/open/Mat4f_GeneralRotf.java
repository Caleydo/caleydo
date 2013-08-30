/**
 *
 */
package gleem.linalg.open;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

/**
 * @author Michael Kalkusch
 */
public class Mat4f_GeneralRotf {

	private static final float EPSILON = 0.00000001f;

	protected boolean bNoRotation = true;

	boolean showMatrix = true;

	protected Vec3f centerOfRotation;

	protected Rotf rotation;

	protected Mat4f matrix;

	/**
	 *
	 */
	public Mat4f_GeneralRotf() {

		centerOfRotation = new Vec3f(0, 0, 0);
		rotation = new Rotf();
		matrix = new Mat4f();
	}

	public Mat4f_GeneralRotf(final Mat4f_GeneralRotf copy) {
		this();

		setAllAndUpdate(copy.getCenterOfRotation(), copy.getRotation());
	}

	/**
	 *
	 */
	public Mat4f_GeneralRotf(final Vec3f centerOfRotation, final Rotf rotation) {

		this();

		setAllAndUpdate(centerOfRotation, rotation);
	}

	protected static final float grad_2_rad(float rad) {
		return (float) Math.PI / 180.0f * rad;
	}

	/**
	 * debug message
	 */
	protected void debugInfo(Mat4f matrix) {
		if (!showMatrix)
			return;

		System.out.println(matrix.toString());
		System.out.println("-------------------------");
	}

	/**
	 * @return the rotation
	 */
	public final Rotf getRotation() {

		return new Rotf(rotation);
	}

	/**
	 * @param rotation
	 *            the rotation to set
	 */
	public final void setRotation(final Rotf rotation) {

		this.rotation.set(rotation);

		if (Math.abs(rotation.get(new Vec3f())) < EPSILON) {
			bNoRotation = true;
		} else {
			bNoRotation = false;
		}
	}

	/**
	 * @param rotation
	 *            the rotation to set
	 */
	public final void setAllAndUpdate(final Vec3f centerOfRotation, final Rotf rotation) {

		this.rotation.set(rotation);
		this.centerOfRotation.set(centerOfRotation);

		update();
	}

	/**
	 * @return the centerOfRotation
	 */
	public final Vec3f getCenterOfRotation() {

		return new Vec3f(centerOfRotation);
	}

	/**
	 * @param centerOfRoation
	 *            the centerOfRotation to set
	 */
	public final void setCenterOfRotation(final Vec3f centerOfRoation) {

		this.centerOfRotation.set(centerOfRoation);
	}

	/**
	 * Get a deep copy of the internal matrix.
	 *
	 * @return the matrix
	 */
	public final Mat4f getMatrix() {

		Mat4f result = new Mat4f(matrix);
		return result;
	}

	// /**
	// * Overwrite internal matrix with a deep copy of new matrix.
	// *
	// * @param matrix the matrix to set
	// */
	// public final void setMatrix(Mat4f matrix) {
	//
	// this.matrix = new Mat4f(matrix);
	// }

	public void debugTestRun() {

		Vec3f axis = new Vec3f(1, 0, 0);
		float angle = grad_2_rad(45);

		this.setCenterOfRotation(new Vec3f(3, 3, 5));
		this.setRotation(new Rotf(axis, angle));
		this.update();

		System.out.println("CENTER: " + centerOfRotation.toString() + "\nROT: "
				+ rotation.toString() + "\n ---\n");

		Vec3f inPoint = new Vec3f(7, 3, 5);
		float xStart = (int) inPoint.x();
		int iLength = 5;

		for (int i = 0; i < iLength; i++) {
			inPoint.setX(i + xStart);
			Vec3f outPoint = this.xformPt(inPoint);

			System.out.println("IN: " + inPoint.toString() + "\nOUT: "
					+ outPoint.toString());
		}

		System.out.println(" ==================\n ");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Mat4f_GeneralRotf test = new Mat4f_GeneralRotf();
		test.debugTestRun();
	}

	/**
	 * transforms the input point
	 *
	 * @see gleem.linalg.Mat4f#xformPt(Vec3f, Vec3f)
	 * @see gleem.linalg.open.Mat4f_GeneralRotf#xformPt(Vec3f, Vec3f)
	 * @param src
	 *            point to be transformed
	 * @return point transformed by internal Mat4f; creates a new Vec3f() object
	 */
	public final Vec3f xformPt(final Vec3f src) {
		Vec3f result = new Vec3f();
		matrix.xformPt(src, result);
		return result;
	}

	/**
	 * @see gleem.linalg.Mat4f#xformPt(Vec3f, Vec3f)
	 * @param src
	 * @param dest
	 *            result; note this Vec3f must not be null!
	 */
	public final void xformPt(final Vec3f src, Vec3f dest) {
		matrix.xformPt(src, dest);
	}

	public void reset() {
		centerOfRotation.set(0, 0, 0);
		rotation.set(new Vec3f(-1, 0, 0), 0.0f);
	}

	public void update() {

		Mat4f T = new Mat4f(Mat4f.MAT4F_UNITY);
		Mat4f T_inv = new Mat4f(Mat4f.MAT4F_UNITY);
		Mat4f R = new Mat4f();

		T.setTranslation(centerOfRotation);
		T_inv.setTranslation(new Vec3f(-centerOfRotation.x(), -centerOfRotation.y(),
				-centerOfRotation.z()));

		if (bNoRotation) {
			R.makeIdent();
		} else {
			rotation.toMatrix(R);
			/* set missing homogeneous coordinate */
			R.set(3, 3, 1.0f);
		}

		/* build matrix .. */
		/* start with T_inv .. */
		Mat4f Tinv_R = T_inv.mul(R);
		matrix = Tinv_R.mul(T);

		/* debug output.. */
		debugInfo(T_inv);
		debugInfo(Tinv_R);
		debugInfo(matrix);
	}

}
