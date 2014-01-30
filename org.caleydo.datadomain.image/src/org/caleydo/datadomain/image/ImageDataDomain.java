/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.image;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.id.IDCreator;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorManager;

/**
 * @author Thomas Geymayer
 *
 */
@XmlType
@XmlRootElement
public class ImageDataDomain extends ADataDomain {

	private ImageSet imageSet;

	private Color color;

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.image";

	public ImageDataDomain() {
		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE + DataDomainManager.DATA_DOMAIN_INSTANCE_DELIMITER
				+ IDCreator.createPersistentID(ImageDataDomain.class));
	}

	public void setImageSet(ImageSet imageSet) {
		this.imageSet = imageSet;
	}

	public ImageSet getImageSet() {
		return imageSet;
	}

	@Override
	public void setLabel(String label) {
		imageSet.setName(label);
	}

	@Override
	public String getLabel() {
		if (imageSet != null && imageSet.getName() != null)
			return imageSet.getName();
		else
			return dataDomainID;
	}

	public void setColor(Color color) {
		if (this.color != null)
			ColorManager.get().markColor(ColorManager.QUALITATIVE_COLORS, this.color, false);
		ColorManager.get().markColor(ColorManager.QUALITATIVE_COLORS, color, true);
		this.color = color;
	}

	@Override
	public Color getColor() {
		if (color == null)
			color = ColorManager.get().getAndMarkColor(ColorManager.QUALITATIVE_COLORS);
		return color;
	}

	@Override
	public int getDataAmount() {
		return 0;
	}

}
