package org.caleydo.core.manager.event.view.histogram;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.util.mapping.color.ColorMapping;

/**
 * Event to signal that a {@link ColorMapping} has been updated.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class UpdateColorMappingEvent
	extends AEvent {

	/** The new or changed {@link ColorMapping} object */
	private ColorMapping colorMapping;

	@Override
	public boolean checkIntegrity() {
		if (colorMapping == null) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the new or changed {@link ColorMapping}. Required.
	 * 
	 * @return new or changed {@link ColorMapping}
	 */
	public ColorMapping getColorMapping() {
		return colorMapping;
	}

	/**
	 * Sets the new or changed {@link ColorMapping}. Required.
	 * 
	 * @param colorMapping
	 *            new or changed {@link ColorMapping}
	 */
	public void setColorMapping(ColorMapping colorMapping) {
		this.colorMapping = colorMapping;
	}
}
