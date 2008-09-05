package org.caleydo.core.util.mapping.color;

import java.util.ArrayList;
import java.util.EnumMap;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * Manage color mappings for different situations system-wide. There can only be
 * one color mapping for each of the values specified in
 * {@link EColorMappingType}. The color mapping can be initialized, otherwise a
 * default is provided. The class follows the Singleton pattern.
 * 
 * @author Alexander Lex
 * 
 */

public class ColorMappingManager
{
	private static ColorMappingManager colorMappingManager = null;
	private EnumMap<EColorMappingType, ColorMapping> hashColorMapping = null;

	/**
	 * Constructor, only called internally
	 */
	private ColorMappingManager()
	{
		hashColorMapping = new EnumMap<EColorMappingType, ColorMapping>(
				EColorMappingType.class);
	}

	/**
	 * Get the instance of the colorMappingManager
	 * 
	 * @return the manager
	 */
	public static ColorMappingManager get()
	{
		if (colorMappingManager == null)
		{
			colorMappingManager = new ColorMappingManager();
		}
		return colorMappingManager;
	}

	/**
	 * Set a color mapping for a mapping type. If there is already a mapping
	 * present it is replaced.
	 * 
	 * @param colorMappingType
	 * @param alMarkerPoints a list of marker points based on which the color
	 *            mapping is created
	 */
	public void initColorMapping(EColorMappingType colorMappingType,
			ArrayList<ColorMarkerPoint> alMarkerPoints)
	{
		if(hashColorMapping.containsKey(colorMappingType))
		{
			hashColorMapping.get(colorMappingType).resetColorMapping(alMarkerPoints);
			return;
		}
		hashColorMapping.put(colorMappingType, new ColorMapping(alMarkerPoints));
	}
	
	public void initiFromPreferenceStore()
	{
		PreferenceStore store = GeneralManager.get().getPreferenceStore();
		
	}

	/**
	 * Returns the color mapping for a particular mapping type. Creates a
	 * default mapping if no custom mapping was set beforehand.
	 * 
	 * @param colorMappingType the type
	 * @return the color mapping
	 */
	public ColorMapping getColorMapping(EColorMappingType colorMappingType)
	{
		ColorMapping colorMapping = hashColorMapping.get(colorMappingType);
		if (colorMapping == null)
		{
			colorMapping = getDefaultColorMapping(colorMappingType);
			hashColorMapping.put(colorMappingType, colorMapping);
		}
		return colorMapping;
	}

	/**
	 * Create default color mapping
	 * 
	 * @param colorMappingType the type
	 * @return the color mapping
	 */
	private ColorMapping getDefaultColorMapping(EColorMappingType colorMappingType)
	{

		ColorMapping colorMapping;
		ArrayList<ColorMarkerPoint> alColorMarkerPoints = new ArrayList<ColorMarkerPoint>();
		switch (colorMappingType)
		{
			case GENE_EXPRESSION:

				alColorMarkerPoints.add(new ColorMarkerPoint(0, 0, 1, 0));
				alColorMarkerPoints.add(new ColorMarkerPoint(0.2f, 0, 0, 0));
				alColorMarkerPoints.add(new ColorMarkerPoint(1, 1, 0, 0));
				colorMapping = new ColorMapping(alColorMarkerPoints);
				break;
			default:
				alColorMarkerPoints.add(new ColorMarkerPoint(0, 0, 1, 0));
				alColorMarkerPoints.add(new ColorMarkerPoint(1, 0, 0, 0));
				colorMapping = new ColorMapping(alColorMarkerPoints);
		}

		return colorMapping;
	}

}
