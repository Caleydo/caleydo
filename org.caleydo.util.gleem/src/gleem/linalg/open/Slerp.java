package gleem.linalg.open;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import javax.media.opengl.GL2;

/**
 * Slerp implementation that can handle rotation, scaling and transformation.
 * 
 * @author Marc Streit
 */
public class Slerp {

	private Rotf quatResult;

	private Vec3f translationResult;

	private Vec3f scaleResult;

	public Slerp() {

		quatResult = new Rotf();
	}

	public Transform interpolate(Transform transformOrigin,
			Transform transformDestination, float delta) {

		translationResult = interpolate(transformOrigin.getTranslation(),
				transformDestination.getTranslation(), delta);
		scaleResult = interpolate(transformOrigin.getScale(),
				transformDestination.getScale(), delta);

		// Return the interpolated quaternion
		quatResult = slerp(transformOrigin.getRotation(),
				transformDestination.getRotation(), delta);

		Transform resultTransform = new Transform();
		resultTransform.setTranslation(translationResult);
		resultTransform.setScale(scaleResult);
		resultTransform.setRotation(quatResult);

		return resultTransform;
	}

	public void applySlerp(final GL2 gl, final Transform transform,
			boolean bIgnoreZRotation, boolean bCenterPivotalPoint) {

		Vec3f translation = transform.getTranslation();
		Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = transform.getRotation().get(axis);

		if (bCenterPivotalPoint) {
			// Subtract 1.5 for slerping around the views center point
			gl.glTranslatef(translation.x() - 1.5f, translation.y() - 1.5f,
					translation.z());
		} else {
			gl.glTranslatef(translation.x(), translation.y(), translation.z());
		}

		gl.glScalef(scale.x(), scale.y(), scale.z());

		float fZRot = 0;
		if (!bIgnoreZRotation)
			fZRot = axis.z();

		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), fZRot);
	}

	// /**
	// * <code>slerp</code> sets this quaternion's value as an interpolation
	// * between two other quaternions.
	// *
	// * @param q1
	// * the first quaternion.
	// * @param q2
	// * the second quaternion.
	// * @param t
	// * the amount to interpolate between the two quaternions.
	// */
	// protected Rotf slerp(Rotf q1, Rotf q2, float t) {
	//
	// Rotf quatResult = new Rotf();
	//
	// // Create a local quaternion to store the interpolated quaternion
	// if (q1.getX() == q2.getX() && q1.getY() == q2.getY() && q1.getZ() ==
	// q2.getZ() && q1.getAngle() == q2.getAngle()) {
	// quatResult.set(q1);
	// return quatResult;
	// }
	//
	// float result = (q1.getX() * q2.getX()) + (q1.getY() * q2.getY()) +
	// (q1.getZ() * q2.getZ())
	// + (q1.getAngle() * q2.getAngle());
	//
	// if (result < 0.0f) {
	// // Negate the second quaternion and the result of the dot product
	// q2.setX(-q2.getX());
	// q2.setY(-q2.getY());
	// q2.setZ(-q2.getZ());
	// q2.setAngle(-q2.getAngle());
	// result = -result;
	// }
	//
	// // Set the first and second scale for the interpolation
	// float scale0 = 1 - t;
	// float scale1 = t;
	//
	// // Check if the angle between the 2 quaternions was big enough to
	// // warrant such calculations
	// if ((1 - result) > 0.1f) {// Get the angle between the 2 quaternions,
	// // and then store the sin() of that angle
	// float theta = (float) Math.acos(result);
	// float invSinTheta = (float) (1f / Math.sin(theta));
	//
	// // Calculate the scale for q1 and q2, according to the angle and
	// // it's sine value
	// scale0 = (float) (Math.sin((1 - t) * theta) * invSinTheta);
	// scale1 = (float) (Math.sin((t * theta)) * invSinTheta);
	// }
	//
	// // Calculate the x, y, z and w values for the quaternion by using a
	// // special
	// // form of linear interpolation for quaternions.
	// quatResult.setX((scale0 * q1.getX()) + (scale1 * q2.getX()));
	// quatResult.setY((scale0 * q1.getY()) + (scale1 * q2.getY()));
	// quatResult.setZ((scale0 * q1.getZ()) + (scale1 * q2.getZ()));
	// quatResult.setAngle((scale0 * q1.getAngle()) + (scale1 * q2.getAngle()));
	//
	// // Return the interpolated quaternion
	// return quatResult;
	// }

	/**
	 * <code>slerp</code> sets this quaternion's value as an interpolation
	 * between two other quaternions.
	 * 
	 * @param q1
	 *            the first quaternion.
	 * @param q2
	 *            the second quaternion.
	 * @param t
	 *            the amount to interpolate between the two quaternions.
	 */
	protected Rotf slerp(Rotf q1, Rotf q2, float t) {

		Rotf quatResult = new Rotf();

		Vec3f vecQ1Axis = new Vec3f();
		float fQ1Angle = q1.get(vecQ1Axis);
		Vec3f vecQ2Axis = new Vec3f();
		float fQ2Angle = q2.get(vecQ2Axis);

		// Create a local quaternion to store the interpolated quaternion
		if (vecQ1Axis.x() == vecQ2Axis.x() && vecQ1Axis.y() == vecQ2Axis.y()
				&& vecQ1Axis.z() == vecQ2Axis.z() && fQ1Angle == fQ2Angle) {
			quatResult.set(q1);
			return quatResult;
		}

		float result = vecQ1Axis.x() * vecQ2Axis.x() + vecQ1Axis.y() * vecQ2Axis.y()
				+ vecQ1Axis.z() * vecQ2Axis.z() + fQ1Angle * fQ2Angle;

		if (result < 0.0f) {
			// Negate the second quaternion and the result of the dot product
			q2.set(new Vec3f(-vecQ2Axis.x(), -vecQ2Axis.y(), -vecQ2Axis.z()), -fQ2Angle);
			fQ2Angle = q2.get(vecQ2Axis);
			result = -result;
		}

		// Set the first and second scale for the interpolation
		float scale0 = 1 - t;
		float scale1 = t;

		// Check if the angle between the 2 quaternions was big enough to
		// warrant such calculations
		if (1 - result > 0.1f) {// Get the angle between the 2 quaternions,
			// and then store the sin() of that angle
			float theta = (float) Math.acos(result);
			float invSinTheta = (float) (1f / Math.sin(theta));

			// Calculate the scale for q1 and q2, according to the angle and
			// it's sine value
			scale0 = (float) (Math.sin((1 - t) * theta) * invSinTheta);
			scale1 = (float) (Math.sin((t * theta)) * invSinTheta);
		}

		// Calculate the x, y, z and w values for the quaternion by using a
		// special
		// form of linear interpolation for quaternions.
		quatResult.set(new Vec3f(scale0 * vecQ1Axis.x() + scale1 * vecQ2Axis.x(), scale0
				* vecQ1Axis.y() + scale1 * vecQ2Axis.y(), scale0 * vecQ1Axis.z() + scale1
				* vecQ2Axis.z()), scale0 * fQ1Angle + scale1 * fQ2Angle);

		// Return the interpolated quaternion
		return quatResult;
	}

	/**
	 * Sets this vector to the interpolation by changeAmnt from beginVec to
	 * finalVec this=(1-changeAmnt)*beginVec + changeAmnt * finalVec
	 * 
	 * @param beginVec
	 *            the begin vector (changeAmnt=0)
	 * @param finalVec
	 *            The final vector to interpolate towards
	 * @param changeAmnt
	 *            An amount between 0.0 - 1.0 representing a percentage change
	 *            from beginVec towards finalVec
	 */
	public Vec3f interpolate(Vec3f beginVec, Vec3f finalVec, float changeAmnt) {

		Vec3f result = new Vec3f();
		result.setX((1 - changeAmnt) * beginVec.x() + changeAmnt * finalVec.x());
		result.setY((1 - changeAmnt) * beginVec.y() + changeAmnt * finalVec.y());
		result.setZ((1 - changeAmnt) * beginVec.z() + changeAmnt * finalVec.z());

		return result;
	}

	public Vec3f getTranslationResult() {

		return translationResult;
	}

	public Vec3f getScaleResult() {

		return scaleResult;
	}
}
