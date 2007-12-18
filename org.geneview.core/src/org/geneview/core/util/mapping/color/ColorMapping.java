package org.geneview.core.util.mapping.color;

import java.awt.Color;

/**
 * 
 * Class returns a color for an incoming value.
 * The mapping uses three colors.
 * From Red to Yellow to Green
 * 
 * TODO: Don't use color - use Vec3f instead with 0-1 range.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class ColorMapping extends AColorMappingInt {

	private int iMid;
	
	protected static final int MAPPING_WIDTH = 255;
	
	protected Color color_1 = Color.RED;
	protected Color color_2 = Color.YELLOW;
	protected Color color_3 = Color.GREEN;

	private float fMappingWidth_div_Mid;

	protected Color resultColor;
	
	float[][] fArColorLookupTable_LEFT;
	float[][] fArColorLookupTable_RIGHT;
	
	/**
	 * Constructor.
	 *
	 */
	public ColorMapping(int min, int max) {
		
		super(min,max);
		
		fArColorLookupTable_LEFT = new float[MAPPING_WIDTH][3];
		fArColorLookupTable_RIGHT = new float[MAPPING_WIDTH][3];
		
		createLookupTable();
		this.setValueMinMax(min, max);
	}
	
	public void setValueMinMax(final int min, final int max) {
		super.setValueMinMax(min, max);
		
		iMid = (iMax - iMin) / 2;
		fMappingWidth_div_Mid = 2.0f * (float) MAPPING_WIDTH / (float) (iMax - iMin);
		
	}
	
	protected void createLookupTable() {
		
		for (int iLookupIndex = 0; iLookupIndex < MAPPING_WIDTH; iLookupIndex++)
		{
			// Create LUT for color 1 to color 2
			fArColorLookupTable_LEFT[iLookupIndex][0] = 1;		
			fArColorLookupTable_LEFT[iLookupIndex][1] = iLookupIndex / 255.0f; 
			fArColorLookupTable_LEFT[iLookupIndex][2] = 0;
			
			// Create LUT for color 2 to color 3
			fArColorLookupTable_RIGHT[iLookupIndex][0] = 1 - iLookupIndex / 255.0f;			
			fArColorLookupTable_RIGHT[iLookupIndex][1] = 1;		
			fArColorLookupTable_RIGHT[iLookupIndex][2] = 0;
		}
	}
	
	public Color colorMappingLookup(int iLookupValue) {
		
		if (( iLookupValue < iMin )||(iLookupValue > iMax)) {
			return color_outOfRange;
		}
		
		if (iLookupValue < iMid)
		{
			int iIndex = (int) ((float) (iLookupValue - iMin) * fMappingWidth_div_Mid -1);
			//int iIndexB = (iLookupValue - iMin) * MAPPING_WIDTH  / iMid;
			return new Color(fArColorLookupTable_LEFT[iIndex][0],
					fArColorLookupTable_LEFT[iIndex][1],
					fArColorLookupTable_LEFT[iIndex][2]);
		}
	
		int iIndex = (int) ((float) (iLookupValue - iMin - iMid) * fMappingWidth_div_Mid -1);		
		return new Color(				
					fArColorLookupTable_RIGHT[iIndex][0],
					fArColorLookupTable_RIGHT[iIndex][1],
					fArColorLookupTable_RIGHT[iIndex][2]);		
	}
}
