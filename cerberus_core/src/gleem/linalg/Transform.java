package gleem.linalg;

/**
 * 
 * Class that holds rotation, translation and scaling.
 * 
 * @author Marc Streit
 *
 */
public class Transform {
	
	private Rotf rotation = new Rotf();
	private Vec3f translation = new Vec3f();
	private Vec3f scale = new Vec3f();
	
	public void setRotation (Rotf rotation) {
		this.rotation = rotation;
	}

	public void setTranslation (Vec3f translation) {
		this.translation = translation;
	}
	
	public void setScale (Vec3f scale) {
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
}
