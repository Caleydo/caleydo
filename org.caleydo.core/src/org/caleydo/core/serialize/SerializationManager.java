package org.caleydo.core.serialize;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.ClustererCanceledEvent;
import org.caleydo.core.manager.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayEvent;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayInUseCaseEvent;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.CreateGUIViewEvent;
import org.caleydo.core.manager.event.view.RemoveViewSpecificItemsEvent;
import org.caleydo.core.manager.event.view.ResetAllViewsEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.TriggerPropagationCommandEvent;
import org.caleydo.core.manager.event.view.ViewActivationEvent;
import org.caleydo.core.manager.event.view.browser.ChangeQueryTypeEvent;
import org.caleydo.core.manager.event.view.browser.ChangeURLEvent;
import org.caleydo.core.manager.event.view.glyph.GlyphChangePersonalNameEvent;
import org.caleydo.core.manager.event.view.glyph.GlyphSelectionBrushEvent;
import org.caleydo.core.manager.event.view.glyph.GlyphUpdatePositionModelEvent;
import org.caleydo.core.manager.event.view.glyph.RemoveUnselectedGlyphsEvent;
import org.caleydo.core.manager.event.view.glyph.SetPositionModelEvent;
import org.caleydo.core.manager.event.view.group.InterchangeGroupsEvent;
import org.caleydo.core.manager.event.view.group.MergeGroupsEvent;
import org.caleydo.core.manager.event.view.histogram.UpdateColorMappingEvent;
import org.caleydo.core.manager.event.view.infoarea.InfoAreaUpdateEvent;
import org.caleydo.core.manager.event.view.pathway.DisableGeneMappingEvent;
import org.caleydo.core.manager.event.view.pathway.DisableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.DisableTexturesEvent;
import org.caleydo.core.manager.event.view.pathway.EnableGeneMappingEvent;
import org.caleydo.core.manager.event.view.pathway.EnableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.EnableTexturesEvent;
import org.caleydo.core.manager.event.view.radial.ChangeColorModeEvent;
import org.caleydo.core.manager.event.view.radial.GoBackInHistoryEvent;
import org.caleydo.core.manager.event.view.radial.GoForthInHistoryEvent;
import org.caleydo.core.manager.event.view.radial.SetMaxDisplayedHierarchyDepthEvent;
import org.caleydo.core.manager.event.view.radial.UpdateDepthSliderPositionEvent;
import org.caleydo.core.manager.event.view.remote.DisableConnectionLinesEvent;
import org.caleydo.core.manager.event.view.remote.EnableConnectionLinesEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.event.view.remote.ResetRemoteRendererEvent;
import org.caleydo.core.manager.event.view.remote.ToggleNavigationModeEvent;
import org.caleydo.core.manager.event.view.remote.ToggleZoomEvent;
import org.caleydo.core.manager.event.view.storagebased.AngularBrushingEvent;
import org.caleydo.core.manager.event.view.storagebased.ApplyCurrentSelectionToVirtualArrayEvent;
import org.caleydo.core.manager.event.view.storagebased.BookmarkButtonEvent;
import org.caleydo.core.manager.event.view.storagebased.ChangeOrientationParallelCoordinatesEvent;
import org.caleydo.core.manager.event.view.storagebased.PreventOcclusionEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.ResetAxisSpacingEvent;
import org.caleydo.core.manager.event.view.storagebased.ResetParallelCoordinatesEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.manager.event.view.storagebased.UseRandomSamplingEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.view.opengl.canvas.radial.event.ClusterNodeSelectionEvent;

/**
 * Central access point for xml-serialization related tasks.
 * @author Werner Puff
 */
public class SerializationManager {

	/** {@link JAXBContext} for view (de-)serialization */
	private JAXBContext viewContext;
	
	/** {@link JAXBContext} for event (de-)serialization */
	private JAXBContext eventContext;
	
	/** {link JAXBContext} for project (de-)serialization */
	private JAXBContext projectContext;
	
	public SerializationManager() {
		try {
			viewContext = JAXBContext.newInstance(ASerializedView.class);
			
			Collection<Class<? extends AEvent>>eventTypes = getSerializeableEventTypes();
			Class<?>[] classes = new Class<?>[eventTypes.size()];
			classes = eventTypes.toArray(classes);
			eventContext = JAXBContext.newInstance(classes);
			
			projectContext = JAXBContext.newInstance(ApplicationInitData.class, ASerializedView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}
	
	public JAXBContext getViewContext() {
		return viewContext;
	}

	public JAXBContext getEventContext() {
		return eventContext;
	}

	public JAXBContext getProjectContext() {
		return projectContext;
	}

	/**
	 * Generates and returns a {@link Collection} of all events to serialize
	 * 
	 * @return {@link Collection} of event-classes to transmit over the network
	 */
	public static Collection<Class<? extends AEvent>> getSerializeableEventTypes() {
		Collection<Class<? extends AEvent>> eventTypes = new ArrayList<Class<? extends AEvent>>();

		eventTypes.add(LoadPathwayEvent.class);
		eventTypes.add(SelectionCommandEvent.class);
		eventTypes.add(SelectionUpdateEvent.class);
		eventTypes.add(UpdateColorMappingEvent.class);
		eventTypes.add(CreateGUIViewEvent.class);
		eventTypes.add(EnableConnectionLinesEvent.class);
		eventTypes.add(DisableConnectionLinesEvent.class);
		eventTypes.add(LoadPathwaysByGeneEvent.class);
		eventTypes.add(ResetRemoteRendererEvent.class);
		eventTypes.add(ToggleNavigationModeEvent.class);
		eventTypes.add(ToggleZoomEvent.class);
		eventTypes.add(ChangeOrientationParallelCoordinatesEvent.class);
		eventTypes.add(PreventOcclusionEvent.class);
		eventTypes.add(UseRandomSamplingEvent.class);
		eventTypes.add(AngularBrushingEvent.class);
		eventTypes.add(ApplyCurrentSelectionToVirtualArrayEvent.class);
		eventTypes.add(BookmarkButtonEvent.class);
		eventTypes.add(ChangeColorModeEvent.class);
		eventTypes.add(GoBackInHistoryEvent.class);
		eventTypes.add(GoForthInHistoryEvent.class);
		eventTypes.add(SetMaxDisplayedHierarchyDepthEvent.class);
		eventTypes.add(UpdateDepthSliderPositionEvent.class);
		eventTypes.add(RedrawViewEvent.class);
		eventTypes.add(ResetAxisSpacingEvent.class);
		eventTypes.add(ResetParallelCoordinatesEvent.class);
		eventTypes.add(UpdateViewEvent.class);
		eventTypes.add(VirtualArrayUpdateEvent.class);
		eventTypes.add(ClearSelectionsEvent.class);
		eventTypes.add(RemoveViewSpecificItemsEvent.class);
		eventTypes.add(ResetAllViewsEvent.class);
		eventTypes.add(ViewActivationEvent.class);
		eventTypes.add(TriggerPropagationCommandEvent.class);
		eventTypes.add(DisableGeneMappingEvent.class);
		eventTypes.add(DisableNeighborhoodEvent.class);
		eventTypes.add(DisableTexturesEvent.class);
		eventTypes.add(EnableGeneMappingEvent.class);
		eventTypes.add(EnableNeighborhoodEvent.class);
		eventTypes.add(EnableTexturesEvent.class);
		eventTypes.add(InfoAreaUpdateEvent.class);
		eventTypes.add(InterchangeGroupsEvent.class);
		eventTypes.add(MergeGroupsEvent.class);
		eventTypes.add(GlyphChangePersonalNameEvent.class);
		eventTypes.add(GlyphSelectionBrushEvent.class);
		eventTypes.add(GlyphUpdatePositionModelEvent.class);
		eventTypes.add(RemoveUnselectedGlyphsEvent.class);
		eventTypes.add(SetPositionModelEvent.class);
		eventTypes.add(ChangeQueryTypeEvent.class);
		eventTypes.add(ChangeURLEvent.class);
		eventTypes.add(ClustererCanceledEvent.class);
		eventTypes.add(ClusterProgressEvent.class);
		eventTypes.add(RenameProgressBarEvent.class);
		eventTypes.add(ReplaceVirtualArrayEvent.class);
		eventTypes.add(ReplaceVirtualArrayInUseCaseEvent.class);
		eventTypes.add(StartClusteringEvent.class);
		eventTypes.add(ClusterNodeSelectionEvent.class);

//		eventTypes.add(NewSetEvent.class);

		return eventTypes;
	}

}
