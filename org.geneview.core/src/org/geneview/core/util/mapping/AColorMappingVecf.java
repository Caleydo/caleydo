/**
 * 
 */
package org.geneview.core.util.mapping;

import org.geneview.core.math.MathUtil;

import gleem.linalg.Vecf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.media.opengl.GL;

/**
 * @author Michael Kalkusch
 *
 * @see gleem.linalg.Vec3f
 * @see gleem.linalg.Vec4f
 * 
 * @param T could be gleem.linalg.Vec3f or gleem.linalg.Vec4f
 */
public abstract class AColorMappingVecf <T>
implements IColorMapping <T> {

	protected ArrayList <T> supportingPoints_Color;
	protected ArrayList <Float> supportingPoints_Float;
	
	protected T belowLowerBound_ColorVecf;
	protected T aboveUpperBound_ColorVecf;
	
	/**
	 * incremental values..
	 * maybe other color mapper do not need these arrays.
	 */
	protected float [] supportingPoints_Values;
	protected float [] supportingPoints_ValuesReciprocalRange;	
	
	protected T [] supportingPoints_ColorInc;
	protected T [] supportingPoints_ColorOffset;
	
	
	/**
	 * 
	 */
	protected AColorMappingVecf() {

		supportingPoints_Color = new ArrayList <T> (3);
		supportingPoints_Float = new ArrayList <Float> (3);
		
		supportingPoints_Values = new float[0];
		supportingPoints_ValuesReciprocalRange = new float[0];
		
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.util.mapping.IColorMapping#getColors()
	 */
	@Override
	public final Collection<T> getColors() {

		return supportingPoints_Color;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.util.mapping.IColorMapping#getValues()
	 */
	@Override
	public final Collection<Float> getValues() {

		return supportingPoints_Float;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.util.mapping.IColorMapping#colorMapping4i(int)
	 */
	public final Vec4f colorMapping4i(int lookupValue) {

		return colorMapping4f( (float) lookupValue);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.util.mapping.IColorMapping#colorMapping3i(int)
	 */
	public final Vec3f colorMapping3i(int lookupValue) {

		return colorMapping3f( (float) lookupValue);
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.util.mapping.IColorMapping#colorMapping_glColor3f(javax.media.opengl.GL, int)
	 */
	public final void colorMapping_glColor3f(GL gl, int lookupValue) {

		Vec3f color = colorMapping3f(lookupValue);
		gl.glColor3f(color.x(), color.y(), color.z());
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.util.mapping.IColorMapping#colorMapping_glColor3f(javax.media.opengl.GL, int)
	 */
	public final void colorMapping_glColor4f(GL gl, int lookupValue) {

		Vec4f color = colorMapping4f(lookupValue);
		gl.glColor4f(color.x(), color.y(), color.z(),color.w());
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.util.mapping.IColorMapping#removeColorPoint(float)
	 */
	public final boolean removeSamplingPoint(float value) {

		Iterator<Float> iterValue = supportingPoints_Float.iterator();
		Iterator<T> iterColor = supportingPoints_Color.iterator();
		
		while ( iterValue.hasNext() )
		{
			iterColor.next();
			if ( Math.abs(iterValue.next().floatValue() - value) < MathUtil.EPSILON ) {
				iterValue.remove();
				iterColor.remove();				
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Test if a color is valid; each of the values inside the Vecf must be in the range of [0.0f .. 1.0f]
	 * 
	 * @param color
	 * @return TRUE if color is valid
	 */
	protected abstract boolean testColorIsValid( final T color);
	
	/**
	 * Set upper bound and lower bound color if values are out of bounds.
	 * Color values must be in the range of [0.0f .. 1.0f]
	 * 
	 * @param belowLowerBound_Color color value uses if value is below lower bound
	 * @param aboveUpperBound_Color color value used if value is above upper bound
	 */
	public final void setOutOfBoundsColors( final T belowLowerBound_Color,
			final T aboveUpperBound_Color) {
		
		if ( testColorIsValid(belowLowerBound_Color) && testColorIsValid(aboveUpperBound_Color))
		{
			this.aboveUpperBound_ColorVecf = aboveUpperBound_Color;
			this.belowLowerBound_ColorVecf = belowLowerBound_Color;
		}
		else
		{
			
		}
	}
	
	/**
	 * Get color value used for values that are above the upper bound.
	 * @return shallow copy of color value
	 */
	public final T getOutOfBoundsColor_aboveUpperBound() {
		return this.aboveUpperBound_ColorVecf;
	}
	
	/**
	 *  Get color value used for values that are below the lower bound.
	 * @return shallow copy of color value
	 */
	public final T getOutOfBoundsColor_belowLowerBound() {
		return this.belowLowerBound_ColorVecf;
	}
}
