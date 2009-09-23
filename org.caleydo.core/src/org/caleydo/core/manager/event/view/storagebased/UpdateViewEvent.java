package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHierarchicalHeatMap;

/**
 * Event that signals a major update a view has to react to. In contrast to {@link RedrawViewEvent} a simple
 * update of the display list is not sufficient. An examplary case is the change of the color mapping which
 * requires the (expensive) re-calculation of the textures in the {@link GLHierarchicalHeatMap}
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class UpdateViewEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
