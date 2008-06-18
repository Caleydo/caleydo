/**
 * 
 */
package org.caleydo.core.util.mapping.color;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import java.awt.Color;


/**
 * Tuned color mapping for (float) ==> Vec3f as color; 
 * Fixed number of 3 sample points.
 * Vec3f(x,y,z) ==> Color(R [0..1],G[0..1],B[0..1])
 * 
 * @author Michael Kalkusch
 *
 */
public class ColorMapping3f_3SamplePoints extends AColorMappingVecf <Vec3f> {
	
	private static final int iSamplePoints = 3;
	
	/**
	 * Define, how many sample points have been set
	 */
	protected int iSamplePointsSet = 0;
	
	/**
	 * 
	 */
	public ColorMapping3f_3SamplePoints() {

		super();
		
		/**
		 * do not need the two ArrayLists, use arrays only!
		 */
		supportingPoints_Color = null;
		supportingPoints_Float = null;		
		
		belowLowerBound_ColorVecf = new Vec3f(0,0,0);
		aboveUpperBound_ColorVecf = new Vec3f(1,1,1);
		
		/**
		 * allocate arrays of super class
		 */
		supportingPoints_ColorInc = new Vec3f[iSamplePoints-1];
		supportingPoints_ColorOffset = new Vec3f[iSamplePoints];		
		supportingPoints_Values = new float[iSamplePoints];
		supportingPoints_ValuesReciprocalRange = new float[iSamplePoints];
	}

	public final void addSamplingPoint_Vecf(final Vec3f color, final float value) {	
		
		if ( iSamplePointsSet < iSamplePoints) 
		{
			supportingPoints_Values[iSamplePointsSet] = value;
			supportingPoints_ColorOffset[iSamplePointsSet] = color;
			
			if ( iSamplePointsSet != 0 )
			{
				float divisor = supportingPoints_Values[iSamplePointsSet] - supportingPoints_Values[iSamplePointsSet-1];
				if  (divisor != 0.0f ) 
				{
					supportingPoints_ValuesReciprocalRange[iSamplePointsSet-1] = 1.0f / divisor;
				}
				else
				{
					throw new RuntimeException("Division by Zero; during creation of color mapping");
				}
				
				/* buffer low and high values.. */
				Vec3f low = supportingPoints_ColorOffset[iSamplePointsSet-1];
				
				/**
				 * this defines the color mapping!
				 */
				supportingPoints_ColorInc[iSamplePointsSet-1] = new Vec3f( 
						low.x() - color.x(),
						low.y() - color.y(),
						low.z() - color.z());
			}
			
			iSamplePointsSet++;
		}
		else
		{
			throw new RuntimeException("Division by Zero; during creation of color mapping");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.util.mapping.IColorMapping#addColorPoint(java.awt.Color, float)
	 */
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
		
		for ( int i=0; i<(supportingPoints_Values.length-1); i++)
		{
			/* test with NEXT supporting value.. */
			if ( lookupValue < supportingPoints_Values[i+1] ) {
				
				/* use lower bound of current interval; diff_percentage is in the range of [0.0f .. 1.0f] */
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
