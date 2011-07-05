package org.caleydo.core.manager.event.view.histogram;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.util.mapping.color.ColorMapper;

/**
 * Event to signal that a {@link ColorMapper} has been updated.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class UpdateColorMappingEvent
	extends AEvent {

	/** The new or changed {@link ColorMapper} object */
	private ColorMapper colorMapping;

	@Override
	public boolean checkIntegrity() {
		if (colorMapping == null) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the new or changed {@link ColorMapper}. Required.
	 * 
	 * @return new or changed {@link ColorMapper}
	 */
	public ColorMapper getColorMapping() {
		return colorMapping;
	}

	/**
	 * Sets the new or changed {@link ColorMapper}. Required.
	 * 
	 * @param colorMapping
	 *            new or changed {@link ColorMapper}
	 */
	public void setColorMapping(ColorMapper colorMapping) {
		this.colorMapping = colorMapping;
	}
}
