package org.caleydo.core.view.opengl.canvas.storagebased.listener;

import java.util.List;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.IEventListener;
import org.caleydo.core.manager.event.view.TriggerPropagationCommandEvent;
import org.caleydo.core.manager.event.view.TriggerSelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.PropagationEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;

/**
 * Listener for {@link TriggerPropagationCommandEvent}s related to {@link GLHeatMap} views
 * @author Werner Puff
 */
public class TriggerPropagationCommandListener
	implements IEventListener {

	/** heatmap view this propagation listener is related to */
	GLHeatMap heatMapView = null;

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof TriggerPropagationCommandEvent) {
			TriggerPropagationCommandEvent triggerSelectionCommandEvent = (TriggerPropagationCommandEvent) event; 
			EIDType type = triggerSelectionCommandEvent.getType();
			List<SelectionCommand> selectionCommands= triggerSelectionCommandEvent.getSelectionCommands();
			switch (type) {
				case DAVID:
				case REFSEQ_MRNA_INT:
				case EXPRESSION_INDEX:
					// nothing to do
					break;
				case EXPERIMENT_INDEX:
					heatMapView.handleStorageTriggerSelectionCommand(type, selectionCommands);
					break;
			}
		}
	}
	
	public GLHeatMap getHeatMapView() {
		return heatMapView;
	}

	public void setHeatMapView(GLHeatMap heatMapView) {
		this.heatMapView = heatMapView;
	}

}
