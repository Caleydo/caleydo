/**
 * 
 */
package org.geneview.core.util.mapping;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.awt.Color;
import java.util.Iterator;


/**
 * Tuned color mapping for (float) ==> Vec4f as color; 
 * Vec4f(x,y,z,x) ==> Color(R [0..1],G[0..1],B[0..1], A[0..1])
 * 
 * @author Michael Kalkusch
 *
 */
public class ColorMapping4f extends AColorMappingVecf <Vec4f> {
	
	/**
	 * 
	 */
	public ColorMapping4f() {

		super();
		
		supportingPoints_ColorInc = new Vec4f[0];
		supportingPoints_ColorOffset = new Vec4f[0];
		
		belowLowerBound_ColorVecf = new Vec4f(0,0,0,1);
		aboveUpperBound_ColorVecf = new Vec4f(1,1,1,1);
	}

	public final void addSamplingPoint_Vecf(final Vec4f color, final float value) {
		
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.util.mapping.IColorMapping#addColorPoint(java.awt.Color, float)
	 */
	@Override
	public final void addSamplingPoint_Color(final Color color, final float value) {

		supportingPoints_Color.add( 
				new Vec4f(color.getRed() / 255, 
						color.getGreen() / 255, 
						color.getBlue() / 255,
						color.getAlpha() / 255) );
		supportingPoints_Float.add(value);
		
		supportingPoints_Values = new float[supportingPoints_Float.size()];
		supportingPoints_ValuesReciprocalRange = new float[supportingPoints_Float.size()];
		supportingPoints_ColorInc = new Vec4f[supportingPoints_Float.size()-1];
		supportingPoints_ColorOffset = new Vec4f[supportingPoints_Float.size()-1];
		
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
			Vec4f low = supportingPoints_Color.get(i);
			Vec4f high = supportingPoints_Color.get(i+1);
			
			/**
			 * this defines the color mapping!
			 */
			supportingPoints_ColorInc[i] = new Vec4f( 
					low.x() - high.x(),
					low.y() - high.y(),
					low.z() - high.z(),
					low.x() - high.w());
		}
		
		supportingPoints_Values[supportingPoints_Values.length] = iterFloat.next().floatValue();
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.util.mapping.IColorMapping#colorMapping(int)
	 */
	public Vec4f colorMapping4f(final float lookupValue) {

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

	public Vec3f colorMapping3f(float lookupValue) {

		return new Vec3f( colorMapping4f(lookupValue) );
	}

}
