/**
 * 
 */
package org.caleydo.core.util.mapping.color;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.awt.Color;
import java.util.Iterator;


/**
 * Tuned color mapping for (float) ==> Vec3f as color; 
 * Vec3f(x,y,z) ==> Color(R [0..1],G[0..1],B[0..1])
 * 
 * @author Michael Kalkusch
 *
 */
public class ColorMapping3f extends AColorMappingVecf <Vec3f> {
	
	/**
	 * 
	 */
	public ColorMapping3f() {

		super();
		
		supportingPoints_ColorInc = new Vec3f[0];
		supportingPoints_ColorOffset = new Vec3f[0];
		
		belowLowerBound_ColorVecf = new Vec3f(0,0,0);
		aboveUpperBound_ColorVecf = new Vec3f(1,1,1);
	}

	public final void addSamplingPoint_Vecf(final Vec3f color, final float value) {
		
		supportingPoints_Color.add(color);
		supportingPoints_Float.add(value);
		
		supportingPoints_Values = new float[supportingPoints_Float.size()];
		supportingPoints_ValuesReciprocalRange = new float[supportingPoints_Float.size()];
		supportingPoints_ColorInc = new Vec3f[supportingPoints_Float.size()-1];
		supportingPoints_ColorOffset = new Vec3f[supportingPoints_Float.size()-1];
		
		Iterator<Float> iterFloat = supportingPoints_Float.iterator();
		
		for ( int i=0; i<supportingPoints_Values.length-1; i++) 
		{
			supportingPoints_Values[i] = iterFloat.next().floatValue();
			
			if ( i > 0 )
			{
				float divisor = supportingPoints_Values[i-1] - supportingPoints_Values[i];
				if  (divisor != 0.0f ) 
				{
					supportingPoints_ValuesReciprocalRange[i-1] = 1.0f / divisor;
				}
				else
				{
					throw new RuntimeException("Division by Zero; during creation of color mapping");
				}
			}
			
			/* buffer low and high values.. */
			Vec3f low = supportingPoints_Color.get(i);
			Vec3f high = supportingPoints_Color.get(i+1);
			
			/**
			 * this defines the color mapping!
			 */
			supportingPoints_ColorInc[i] = new Vec3f( 
					low.x() - high.x(),
					low.y() - high.y(),
					low.z() - high.z());
		}
		
		supportingPoints_Values[supportingPoints_Values.length] = iterFloat.next().floatValue();
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.util.mapping.IColorMapping#addColorPoint(java.awt.Color, float)
	 */
	@Override
	public final void addSamplingPoint_Color(final Color color, final float value) {

		addSamplingPoint_Vecf(
				 new Vec3f(color.getRed() / 255, 
							color.getGreen() / 255, 
							color.getBlue() / 255 )
				 , value);	
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.util.mapping.IColorMapping#colorMapping(int)
	 */
	public Vec4f colorMapping4f(final float lookupValue) {

		return new Vec4f( colorMapping3f(lookupValue), 1.0f);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.util.mapping.IColorMapping#colorMapping(int)
	 */
	public Vec3f colorMapping3f(final float lookupValue) {
		
		if ( lookupValue < supportingPoints_Values[0] ) {
			/* below lower bound */
			return belowLowerBound_ColorVecf;
		}
		
		for ( int i=0; i<supportingPoints_Values.length; i++)
		{
			/* test with NEXT supporting value.. */
			if ( lookupValue < supportingPoints_Values[i+1] ) {
				
				/* use lower bound of current interval */
				float diff_percentage = 
					(lookupValue - supportingPoints_Values[i]) * supportingPoints_ValuesReciprocalRange[i];
				return supportingPoints_ColorOffset[i].addScaled(diff_percentage, supportingPoints_ColorInc[i] );
			}
		}
		
		/* above upper bound */
		return this.aboveUpperBound_ColorVecf;
	}

	@Override
	protected boolean testColorIsValid(Vec3f color) {

		for (int i=0; i<color.length(); i++ )
		{
			float value = color.get(i);
			if ( value < 0.0f || value>1.0f ) {
				return false;
			}
		}
		return false;
	}

}
