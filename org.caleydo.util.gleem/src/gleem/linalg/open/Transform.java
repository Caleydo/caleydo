package gleem.linalg.open;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

/**
 * Class that holds rotation, translation and scaling.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class Transform {

	private Rotf rotation = new Rotf();
	private Vec3f translation = new Vec3f();
	private Vec3f scale = new Vec3f();

	public void setRotation(Rotf rotation) {
		this.rotation = rotation;
	}

	public void setTranslation(Vec3f translation) {
		this.translation = translation;
	}

	public void setScale(Vec3f scale) {
		this.scale = scale;
	}

	public Rotf getRotation() {
		return rotation;
	}

	public Vec3f getTranslation() {
		return translation;
	}

	public Vec3f getScale() {
		return scale;
	}

	/**
	 * combines rotation, translation and scale into one matrix.
	 * 
	 * @return matrix with rotation, translation and scale
	 */
	public synchronized Mat4f getMatrix() {
		Mat4f resultMatrix = new Mat4f();
		rotation.toMatrix(resultMatrix);
		resultMatrix.setTranslation(translation);
		resultMatrix.setScale(scale);
		return resultMatrix;
	}
}
