package org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.storagebased.SetPointSizeEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.GLScatterplot;

/**
 * Listener that reacts events for setting the max. displayed hierarchy depth in RadialHierarchy.
 * 
 * @author Christian
 */

public class SetPointSizeListener 
	extends AEventListener<GLScatterplot> {

		@Override
		public void handleEvent(AEvent event) {
			if (event instanceof SetPointSizeEvent) {
				handler.setPointSize(((SetPointSizeEvent) event)
					.getPointSize());

			
			}
		}
}


//	package org.caleydo.core.view.opengl.canvas.radial.listener;
//
//	import org.caleydo.core.manager.event.AEvent;
//	import org.caleydo.core.manager.event.AEventListener;
//	import org.caleydo.core.manager.event.view.radial.SetMaxDisplayedHierarchyDepthEvent;
//	import org.caleydo.core.view.opengl.canvas.radial.GLRadialHierarchy;
//
//	/**
//	 * Listener that reacts events for setting the max. displayed hierarchy depth in RadialHierarchy.
//	 * 
//	 * @author Christian
//	 */
//	public class SetMaxDisplayedHierarchyDepthListener
//		extends AEventListener<GLRadialHierarchy> {
//
//		@Override
//		public void handleEvent(AEvent event) {
//			if (event instanceof SetMaxDisplayedHierarchyDepthEvent) {
//				handler.setMaxDisplayedHierarchyDepth(((SetMaxDisplayedHierarchyDepthEvent) event)
//					.getMaxDisplayedHierarchyDepth());
//			}
//
//		}
//
//	}

