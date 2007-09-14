package cerberus.util.mapping;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.awt.Color;
import java.util.Collection;

import javax.media.opengl.GL;

/**
 * Interface for color mapper
 * 
 * @author Michael Kalkusch
 *
 * @param <T>
 */
public interface IColorMapping <T> {
	
	public abstract Vec3f colorMapping3f(float iLookupValue);
	
	public abstract Vec4f colorMapping4f(float iLookupValue);
	
	public abstract Vec3f colorMapping3i(int iLookupValue);
	
	public abstract Vec4f colorMapping4i(int iLookupValue);
	
	public void colorMapping_glColor3f(GL gl, int iLookupValue);
	
	public void colorMapping_glColor4f(GL gl, int iLookupValue);

	/* Setting sampling points.. */
	
	public void addSamplingPoint_Color(Color color, float value);
	
	public void addSamplingPoint_Vecf(T color, float value);
	
	public Collection <Float> getValues();
	
	public Collection <T> getColors();
	
	public boolean removeSamplingPoint(float value);
	
}