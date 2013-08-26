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
public class Mat4f_GeneraRotfScale extends Mat4f_GeneralRotf {

	protected Vec3f scale;

	/**
	 *
	 */
	public Mat4f_GeneraRotfScale() {

		super();
		scale = new Vec3f(1, 1, 1);
	}

	public Mat4f_GeneraRotfScale(final Mat4f_GeneraRotfScale copy) {
		this();

		setAllAndUpdate(copy.getCenterOfRotation(), copy.getScale(), copy.getRotation());
	}

	/**
	 * @param centerOfRotation
	 * @param scale
	 * @param rotation
	 */
	public Mat4f_GeneraRotfScale(Vec3f centerOfRotation, Vec3f scale, Rotf rotation) {
		this();

		setAllAndUpdate(centerOfRotation, scale, rotation);
	}

	/**
	 * @return the scale
	 */
	public final Vec3f getScale() {

		return new Vec3f(scale);
	}

	/**
	 * @param scale
	 *            the scale to set
	 */
	public final void setScale(final Vec3f scale) {

		this.scale.set(scale);
	}

	@Override
	public void debugTestRun() {

		Vec3f axis = new Vec3f(1, 0, 0);
		float angle = grad_2_rad(45);

		this.setCenterOfRotation(new Vec3f(3, 3, 5));
		this.setScale(new Vec3f(1, 1, 1));
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

	@Override
	public void reset() {
		centerOfRotation.set(0, 0, 0);
		scale.set(1, 1, 1);
		rotation.set(new Vec3f(-1, 0, 0), 0.0f);
	}

	@Override
	public void update() {

		Mat4f T = new Mat4f(Mat4f.MAT4F_UNITY);
		Mat4f T_inv = new Mat4f(Mat4f.MAT4F_UNITY);
		Mat4f R = new Mat4f();
		Mat4f S = new Mat4f(Mat4f.MAT4F_UNITY);

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

		S.setScale(scale);

		/* build matrix .. */
		/* start with T_inv .. */
		Mat4f Tinv_R = T_inv.mul(R);
		Mat4f Tinv_R_S = Tinv_R.mul(S);
		matrix = Tinv_R_S.mul(T);

		/* debug output.. */
		debugInfo(T_inv);
		debugInfo(Tinv_R);
		debugInfo(Tinv_R_S);
		debugInfo(matrix);
	}

	/**
	 * @param rotation
	 *            the rotation to set
	 */
	public final void setAllAndUpdate(final Vec3f centerOfRotation, final Vec3f scale,
			final Rotf rotation) {

		this.rotation.set(rotation);
		this.centerOfRotation.set(centerOfRotation);
		this.setScale(scale);

		update();
	}

}
