package gleem.linalg.open;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;
import javax.media.opengl.GL2;

/**
 * @author Michael Kalkusch
 */
public final class Vec3fGL extends Vec3f {

	public Vec3fGL() {
		super();
	}

	public Vec3fGL(Vec3fGL arg) {
		super(arg);
	}

	public Vec3fGL(float x, float y, float z) {
		super(x, y, z);
	}

	/**
	 * Copy x,y,z to Vec3f and ignores arg.w().
	 * 
	 * @param arg
	 */
	public Vec3fGL(Vec4f arg) {
		super(arg.x(), arg.y(), arg.z());
	}

	public Vec3fGL(Vec3f arg) {
		super(arg);
	}

	public static final void setGlVertex(final GL2 gl, final Vec3f vertex) {
		gl.glVertex3f(vertex.x(), vertex.y(), vertex.z());
	}

	public static final void setGlColor(final GL2 gl, final Vec3f vertex) {
		gl.glColor3f(vertex.x(), vertex.y(), vertex.z());
	}

	public final void glColor3f(final GL2 gl) {
		gl.glColor3f(x(), y(), z());
	}

	public final void glVertex3f(final GL2 gl) {
		gl.glVertex3f(x(), y(), z());
	}

}
