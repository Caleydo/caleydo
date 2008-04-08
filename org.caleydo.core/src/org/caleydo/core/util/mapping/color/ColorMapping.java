package org.caleydo.core.util.mapping.color;

import gleem.linalg.Vec3f;

/**
 * 
 * Class returns a color for an incoming value.
 * The mapping uses three colors.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class ColorMapping 
extends AColorMapping {

	private float fMid;
	
	private static final int MAPPING_WIDTH = 100;
	
	private Vec3f color_1 = new Vec3f(0,1,0); // green
	private Vec3f color_2 = new Vec3f(0,0,0); // yellow
	private Vec3f color_3 = new Vec3f(1,0,0); // red

	private float fMappingWidth_div_Mid;
	
	float[][] fArColorLookupTable_LEFT;
	float[][] fArColorLookupTable_RIGHT;
	
	/**
	 * Constructor.
	 *
	 */
	public ColorMapping(final float fMin, final float fMax) {
		
		super(fMin, fMax);
		
		fArColorLookupTable_LEFT = new float[MAPPING_WIDTH][3];
		fArColorLookupTable_RIGHT = new float[MAPPING_WIDTH][3];
		
		createLookupTable();
		this.setValueMinMax(fMin, fMax);
	}
	
	public void setValueMinMax(final float fMin, final float fMax) {
	
		super.setValueMinMax(fMin, fMax);
		
		fMid = (fMax - fMin) / 2;
		fMappingWidth_div_Mid = 2.0f * (float) MAPPING_WIDTH / (float) (fMax - fMin);
		
	}
	
	private void createLookupTable() {
		
		for (int iLookupIndex = 0; iLookupIndex < MAPPING_WIDTH; iLookupIndex++)
		{
			// x = index
			// x0 = 0
			// x1 = MAPPING_WIDTH
			// y = ?
			// y0 = color_1.x
			// y1 = color_2.x
			
			// Create LUT for color 1 to color 2
			fArColorLookupTable_LEFT[iLookupIndex][0] = color_1.x() + 
				(float)iLookupIndex * ((color_2.x() - color_1.x()) / MAPPING_WIDTH);  
			fArColorLookupTable_LEFT[iLookupIndex][1] = color_1.y() + 
				(float)iLookupIndex * ((color_2.y() - color_1.y()) / MAPPING_WIDTH);  
			fArColorLookupTable_LEFT[iLookupIndex][2] = color_1.z() + 
				(float)iLookupIndex * ((color_2.z() - color_1.z()) / MAPPING_WIDTH);  
			
			// Create LUT for color 2 to color 3
			fArColorLookupTable_RIGHT[iLookupIndex][0] = color_2.x() + 
				(float)iLookupIndex * ((color_3.x() - color_2.x()) / MAPPING_WIDTH);  
			fArColorLookupTable_RIGHT[iLookupIndex][1] = color_2.y() + 
				(float)iLookupIndex * ((color_3.y() - color_2.y()) / MAPPING_WIDTH);  
			fArColorLookupTable_RIGHT[iLookupIndex][2] = color_2.z() + 
				(float)iLookupIndex * ((color_3.z() - color_2.z()) / MAPPING_WIDTH);  
		}
	}
	
	public Vec3f colorMappingLookup(final float fLookupValue) {
		
		if (( fLookupValue < fMin ) || (fLookupValue > fMax) || Float.isNaN(fLookupValue)) {
			return color_outOfRange;
		}
		
		if (fLookupValue < fMid)
		{
			int iIndex = (int) ((float) (fLookupValue - fMin) * fMappingWidth_div_Mid);

			return new Vec3f(fArColorLookupTable_LEFT[iIndex][0],
					fArColorLookupTable_LEFT[iIndex][1],
					fArColorLookupTable_LEFT[iIndex][2]);
		}
	
		int iIndex = (int) ((float) (fLookupValue - fMin - fMid) * fMappingWidth_div_Mid -1);		
		return new Vec3f(fArColorLookupTable_RIGHT[iIndex][0],
					fArColorLookupTable_RIGHT[iIndex][1],
					fArColorLookupTable_RIGHT[iIndex][2]);		
	}
}
