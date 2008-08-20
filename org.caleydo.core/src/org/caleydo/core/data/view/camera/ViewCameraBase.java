package org.caleydo.core.data.view.camera;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import org.caleydo.core.data.AUniqueObject;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class ViewCameraBase
	extends AUniqueObject
	implements IViewCamera
{

	/**
	 * Zoom is equal to scaling in a 4x4 matrix
	 */
	protected Vec3f v3fCameraScale = new Vec3f(1, 1, 1);

	/**
	 * Pan used to translate.
	 */
	protected Vec3f v3fCameraPosition = new Vec3f(0, 0, 0);

	/**
	 * Rotation in euler angles
	 */
	protected Vec3f v3fCameraRotationEuler = new Vec3f(0, 0, 0);

	/**
	 * Rotation stored as quaternion.
	 */
	protected Rotf rotfCameraRotation;

	/**
	 * Flag indicates update state.
	 */
	protected boolean bHasChanged = true;

	/**
	 * Matrix created by pan, zoom and rotation. Is updated by setter methods.
	 */
	protected Mat4f mat4fCameraViewMatrix;

	/**
	 * Constructor. Sets pan to (0,0,0) zoom to (1,1,1) and rotation to
	 * (1,0,0,0) using null-vector (0,0,0)
	 */
	public ViewCameraBase(int iId)
	{

		super(iId);

		rotfCameraRotation = new Rotf();

		mat4fCameraViewMatrix = Mat4f.MAT4F_UNITY;
	}

	/**
	 * Updates the matrix using rotation, pan and zoom. Note: Does not influence
	 * homogeneous coordinates.
	 */
	protected void updateMatrix()
	{

		mat4fCameraViewMatrix.setRotation(rotfCameraRotation);
		mat4fCameraViewMatrix.setScale(v3fCameraScale);
		mat4fCameraViewMatrix.setTranslation(v3fCameraPosition);
	}

	public boolean hasViewCameraChanged()
	{

		return bHasChanged;
	}

	@Override
	public void setCameraPosition(final Vec3f setPan)
	{

		v3fCameraPosition = new Vec3f(setPan);
		mat4fCameraViewMatrix.setTranslation(v3fCameraPosition);
	}

	public void addCameraPosition(final Vec3f setPos)
	{

		v3fCameraPosition = new Vec3f(v3fCameraPosition);
		v3fCameraPosition.add(setPos);
		mat4fCameraViewMatrix.setTranslation(v3fCameraPosition);
	}

	@Override
	public void setCameraRotation(final Rotf setRot)
	{

		rotfCameraRotation = setRot;
		mat4fCameraViewMatrix.setRotation(rotfCameraRotation);
		mat4fCameraViewMatrix.setScale(v3fCameraScale);
	}

	@Override
	public void setCameraScale(final Vec3f setZoom)
	{

		v3fCameraScale = new Vec3f(setZoom);
		// v3fCameraScale.add(setZoom);
		mat4fCameraViewMatrix.setScale(v3fCameraScale);
	}

	@Override
	public void setCameraAll(final Vec3f setPan, final Vec3f setZoom, final Rotf setRot)
	{

		v3fCameraPosition = new Vec3f(setPan);
		v3fCameraScale = new Vec3f(setZoom);
		rotfCameraRotation = new Rotf(setRot);
		updateMatrix();
	}

	public void setHasChanged(final boolean bSetHasChanged)
	{

		this.bHasChanged = bSetHasChanged;
	}

	@Override
	public final Vec3f getCameraScale()
	{

		return new Vec3f(v3fCameraScale);
	}

	@Override
	public final Vec3f getCameraPosition()
	{

		// return this.v3fCameraPosition;

		// System.err.println("ViewCameraBase.getCameraPosition  [" +
		// Integer.toString(getId()) + "]");

		return new Vec3f(v3fCameraPosition);
	}

	@Override
	public final Rotf getCameraRotation()
	{

		return new Rotf(this.rotfCameraRotation);
	}

	public final float getCameraRotationGrad(Vec3f axis)
	{

		return (float) Math.toDegrees(rotfCameraRotation.get(axis));
	}

	public final float getCameraRotationRadiant(Vec3f axis)
	{

		return rotfCameraRotation.get(axis);
	}

	@Override
	public final Mat4f getCameraMatrix()
	{

		updateMatrix();

		return new Mat4f(this.mat4fCameraViewMatrix);
	}

	public String toString()
	{

		return "p:" + this.v3fCameraPosition.toString() + " z:"
				+ this.v3fCameraScale.toString() + " r:" + this.rotfCameraRotation.toString();
	}

	public final void setCameraRotationVec3f(final Vec3f setRotVec3f)
	{

		/**
		 * compute Quaternion from input vector assuming vector Vec3f describs 3
		 * rotations alpha, betha, gamma
		 */
		Vec3f helpRot_cos = new Vec3f((float) Math.cos((setRotVec3f.x() * 0.5f)), (float) Math
				.cos((setRotVec3f.y() * 0.5f)), (float) Math.cos((setRotVec3f.z() * 0.5f)));
		Vec3f helpRot_sin = new Vec3f((float) Math.sin((setRotVec3f.x() * 0.5f)), (float) Math
				.sin((setRotVec3f.y() * 0.5f)), (float) Math.sin((setRotVec3f.z() * 0.5f)));

		float w = helpRot_cos.x() * helpRot_cos.y() * helpRot_cos.z() - helpRot_sin.x()
				* helpRot_sin.y() * helpRot_sin.z();

		rotfCameraRotation.set(new Vec3f(helpRot_cos.x() * helpRot_sin.y() * helpRot_cos.z()
				+ helpRot_sin.x() * helpRot_sin.y() * helpRot_sin.z(),

		helpRot_sin.x() * helpRot_sin.y() * helpRot_cos.z() - helpRot_cos.x()
				* helpRot_sin.y() * helpRot_sin.z(),

		helpRot_sin.x() * helpRot_cos.y() * helpRot_cos.z() + helpRot_cos.x()
				* helpRot_cos.y() * helpRot_sin.z()),

		w);
	}

	public final void addCameraRotationVec3f(final Vec3f setRotVec3f)
	{

		assert false : "Not teste yet!";

		/**
		 * compute Quaternion from input vector assuming vector Vec3f describs 3
		 * rotations alpha, betha, gamma
		 */
		Vec3f helpRot_cos = new Vec3f((float) Math.cos((setRotVec3f.x() * 0.5f)), (float) Math
				.cos((setRotVec3f.y() * 0.5f)), (float) Math.cos((setRotVec3f.z() * 0.5f)));
		Vec3f helpRot_sin = new Vec3f((float) Math.sin((setRotVec3f.x() * 0.5f)), (float) Math
				.sin((setRotVec3f.y() * 0.5f)), (float) Math.sin((setRotVec3f.z() * 0.5f)));

		float w = helpRot_cos.x() * helpRot_cos.y() * helpRot_cos.z() - helpRot_sin.x()
				* helpRot_sin.y() * helpRot_sin.z();

		Rotf temp = new Rotf();
		temp.set(new Vec3f(helpRot_cos.x() * helpRot_sin.y() * helpRot_cos.z()
				+ helpRot_sin.x() * helpRot_sin.y() * helpRot_sin.z(),

		helpRot_sin.x() * helpRot_sin.y() * helpRot_cos.z() - helpRot_cos.x()
				* helpRot_sin.y() * helpRot_sin.z(),

		helpRot_sin.x() * helpRot_cos.y() * helpRot_cos.z() + helpRot_cos.x()
				* helpRot_cos.y() * helpRot_sin.z()),

		w);

		rotfCameraRotation.times(temp);
	}

	public final Vec3f getCameraRotationEuler()
	{

		return v3fCameraRotationEuler;
	}

	public void setCameraRotationEuler(final Vec3f setRotEuler)
	{

		v3fCameraRotationEuler = setRotEuler;

	}

	public void addCameraRotationEuler(final Vec3f addRotEuler)
	{

		v3fCameraRotationEuler.add(addRotEuler);
	}

	public void addCameraRotation(final Rotf setRot)
	{

		Rotf buffer = rotfCameraRotation.times(setRot);
		rotfCameraRotation = buffer;
	}

	public void addCameraScale(final Vec3f setScale)
	{

		v3fCameraScale.add(setScale);
		v3fCameraPosition.add(setScale);
	}

	public final Vec3f addCameraScaleAndGet(final Vec3f setScale)
	{

		addCameraScale(setScale);

		return new Vec3f(v3fCameraScale);
	}

	public void clone(IViewCamera cloneFromCamera)
	{

		this.v3fCameraPosition = new Vec3f(cloneFromCamera.getCameraPosition());
		this.v3fCameraScale = new Vec3f(cloneFromCamera.getCameraScale());
		this.rotfCameraRotation = new Rotf(cloneFromCamera.getCameraRotation());

		bHasChanged = true;
	}
}
