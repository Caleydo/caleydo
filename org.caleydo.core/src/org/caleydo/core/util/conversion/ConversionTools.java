package org.caleydo.core.util.conversion;

import java.util.StringTokenizer;

public class ConversionTools
{

	public static float[] getColorFromString(String sColor)
	{
		float[] fArColor = new float[3];
		if (sColor.isEmpty())
		{
			fArColor[0] = 0;
			fArColor[1] = 0;
			fArColor[2] = 0;
		}
		else
		{
			StringTokenizer tokenizer = new StringTokenizer(sColor, ",", false);
			int iInnerCount = 0;
			while (tokenizer.hasMoreTokens())
			{
				try
				{
					String token = tokenizer.nextToken();
					int iTemp = Integer.parseInt(token);
					fArColor[iInnerCount] = (float) iTemp / 255;
				}
				catch (Exception e)
				{

				}
				iInnerCount++;
			}
		}
		return fArColor;
	}

}
