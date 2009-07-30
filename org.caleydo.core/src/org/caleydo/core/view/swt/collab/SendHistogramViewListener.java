package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.event.view.CreateGUIViewEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Listener for the "start server" button that triggers the creation of a Server-Thread within the network
 * framework.
 * 
 * @author Werner Puff
 */
public class SendHistogramViewListener
	implements Listener {

	private int[] viewIds;
	
	private List viewList;
	
	private Text target;
	
	@Override
	public void handleEvent(Event event) {
		IGeneralManager gm = GeneralManager.get();

		int selectedView = viewList.getSelectionIndex();
		if (selectedView != -1) {
			IViewManager vm = gm.getViewGLCanvasManager();
			AGLEventListener view = vm.getGLEventListener(viewIds[selectedView]);
			ASerializedView sv = view.getSerializableRepresentation();
	
			CreateGUIViewEvent e = new CreateGUIViewEvent();
			e.setTargetApplicationID(target.getText());
			e.setSerializedView(sv);
			e.setSender(this);
			IEventPublisher ep = GeneralManager.get().getEventPublisher();
			ep.triggerEvent(e);
		}
	}

	public Text getTarget() {
		return target;
	}

	public void setTarget(Text target) {
		this.target = target;
	}

	public int[] getViewIds() {
		return viewIds;
	}

	public void setViewIds(int[] viewIds) {
		this.viewIds = viewIds;
	}

	public List getViewList() {
		return viewList;
	}

	public void setViewList(List viewList) {
		this.viewList = viewList;
	}

}
