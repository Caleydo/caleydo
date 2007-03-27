package cerberus.util.colormapping;

import java.awt.Color;

public class ColorMapping {

	protected int iMin = 0;
	protected int iMax = 0;
	
	protected static final int MAPPING_WIDTH = 256;
	
	protected Color color_1 = Color.RED;
	protected Color color_2 = Color.GREEN;
	protected Color resultColor;
	
	float[][] fArColorLookupTable;
	
	/**
	 * Constructor.
	 *
	 */
	public ColorMapping(int iMin, int iMax) {
		
		this.iMin = iMin;
		this.iMax = iMax;
		
		fArColorLookupTable = new float[MAPPING_WIDTH][3];
	}
	
	public void createLookupTable() {
		
		int iMin = this.iMin / MAPPING_WIDTH;
		int iMax = this.iMax / MAPPING_WIDTH;
		
		for (int iLookupIndex = 0; iLookupIndex < MAPPING_WIDTH; iLookupIndex++)
		{
			fArColorLookupTable[iLookupIndex][0] = (color_1.getRed() + 
				((color_2.getRed() - color_1.getRed()) / ((float)iMax - (float)iMin)) * ((float)iLookupIndex - (float)iMin)) / 255.0f; 
			
			fArColorLookupTable[iLookupIndex][1] = (color_1.getGreen() + 
				((color_2.getGreen() - color_1.getGreen()) / ((float)iMax - (float)iMin)) * ((float)iLookupIndex - (float)iMin)) / 255.0f; 
	
			fArColorLookupTable[iLookupIndex][2] = (color_1.getBlue() + 
				((color_2.getBlue() - color_1.getBlue()) / ((float)iMax - (float)iMin)) * ((float)iLookupIndex - (float)iMin)) / 255.0f; 
			
//			System.out.println(fArColorLookupTable[iLookupIndex][0] + "," +
//					fArColorLookupTable[iLookupIndex][1] + "," +
//					fArColorLookupTable[iLookupIndex][2]);
		}
	}
	
	public Color colorMappingLookup(int iLookupValue) {
		
		if (iLookupValue < iMin || iLookupValue > iMax)
			return Color.BLACK;

		return new Color(fArColorLookupTable[(iLookupValue - iMin) / MAPPING_WIDTH][0],
				fArColorLookupTable[(int)((iLookupValue - iMin) / MAPPING_WIDTH)][1],
				fArColorLookupTable[(int)((iLookupValue - iMin) / MAPPING_WIDTH)][2]);
	}
}
