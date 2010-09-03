package org.caleydo.core.serialize;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.manager.event.AEvent;
import org.eclipse.core.internal.runtime.InternalPlatform;
import org.osgi.framework.Bundle;

/**
 * Central access point for xml-serialization related tasks.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
public class SerializationManager {

	private static volatile SerializationManager instance = null;

	/** {@link JAXBContext} for event (de-)serialization */
	private JAXBContext eventContext;

	/** {link JAXBContext} for project (de-)serialization */
	private JAXBContext projectContext;

	private ArrayList<Class<?>> projectTypes;
	


	private SerializationManager() {
		try {
			Collection<Class<? extends AEvent>> eventTypes = getSerializeableEventTypes();
			projectTypes = new ArrayList<Class<?>>();
			projectTypes.add(DataInitializationData.class);
			projectTypes.add(ViewList.class);
			Class<?>[] classes = new Class<?>[eventTypes.size()];
			classes = eventTypes.toArray(classes);
			eventContext = JAXBContext.newInstance(classes);
			createNewProjectContext();
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}


	private void createNewProjectContext() {
		try {
			Class<?>[] projectClasses = new Class<?>[projectTypes.size()];
			projectTypes.toArray(projectClasses);
			projectContext = JAXBContext.newInstance(projectClasses);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}

	public synchronized static SerializationManager get() {
		if (instance == null) {
			synchronized (SerializationManager.class) {
				if (instance == null)
					instance = new SerializationManager();
			}
		}
		return instance;
	}

	/**
	 * Gets the {@link JAXBContext} used to serialize events.
	 * 
	 * @return events-serialization {@link JAXBContext}.
	 */
	public JAXBContext getEventContext() {
		return eventContext;
	}

	/**
	 * Gets the {@link JAXBContext} used during load/save caleydo projects.
	 * 
	 * @return caleydo-project serialization {@link JAXBContext}.
	 */
	public JAXBContext getProjectContext() {
		return projectContext;
	}

	/**
	 * Generates and returns a {@link Collection} of all views that may be serialized. This list can e.g. be
	 * used to get the list of views to save in a caledyo-project file.
	 * 
	 * @return {@link Collection} of serialized-view-classes that may be serialized.
	 */
	public Collection<Class<? extends ASerializedView>> getSerializeableViewTypes() {
		Collection<Class<? extends ASerializedView>> viewTypes =
			new ArrayList<Class<? extends ASerializedView>>();

		// the list of views is maintained in the {@link ASerilializedView}'s {@link XmlSeeAlso} annotation.
		viewTypes.add(ASerializedView.class);

		return viewTypes;
	}

	/**
	 * Generates and returns a {@link Collection} of all events to serialize
	 * 
	 * @return {@link Collection} of event-classes to transmit over the network
	 */
	public static Collection<Class<? extends AEvent>> getSerializeableEventTypes() {
		Collection<Class<? extends AEvent>> eventTypes = new ArrayList<Class<? extends AEvent>>();

		// FIXME: check if the list of individual events needs to be provided here
		eventTypes.add(AEvent.class);
		// eventTypes.add(LoadPathwayEvent.class);
		// eventTypes.add(SelectionCommandEvent.class);
		// eventTypes.add(SelectionUpdateEvent.class);
		// eventTypes.add(UpdateColorMappingEvent.class);
		// eventTypes.add(CreateGUIViewEvent.class);
		// eventTypes.add(EnableConnectionLinesEvent.class);
		// eventTypes.add(DisableConnectionLinesEvent.class);
		// eventTypes.add(LoadPathwaysByGeneEvent.class);
		// eventTypes.add(ResetRemoteRendererEvent.class);
		// eventTypes.add(ToggleNavigationModeEvent.class);
		// eventTypes.add(ToggleZoomEvent.class);
		// eventTypes.add(UseRandomSamplingEvent.class);
		// eventTypes.add(AngularBrushingEvent.class);
		// eventTypes.add(ApplyCurrentSelectionToVirtualArrayEvent.class);
		// eventTypes.add(BookmarkButtonEvent.class);
		// eventTypes.add(ChangeColorModeEvent.class);
		// eventTypes.add(GoBackInHistoryEvent.class);
		// eventTypes.add(GoForthInHistoryEvent.class);
		// eventTypes.add(SetMaxDisplayedHierarchyDepthEvent.class);
		// eventTypes.add(UpdateDepthSliderPositionEvent.class);
		// eventTypes.add(RedrawViewEvent.class);
		// eventTypes.add(ResetAxisSpacingEvent.class);
		// eventTypes.add(ResetParallelCoordinatesEvent.class);
		// eventTypes.add(UpdateViewEvent.class);
		// eventTypes.add(VirtualArrayUpdateEvent.class);
		// eventTypes.add(ClearSelectionsEvent.class);
		// eventTypes.add(RemoveViewSpecificItemsEvent.class);
		// eventTypes.add(ResetAllViewsEvent.class);
		// eventTypes.add(ViewActivationEvent.class);
		// eventTypes.add(TriggerPropagationCommandEvent.class);
		// eventTypes.add(DisableGeneMappingEvent.class);
		// eventTypes.add(DisableNeighborhoodEvent.class);
		// eventTypes.add(DisableTexturesEvent.class);
		// eventTypes.add(EnableGeneMappingEvent.class);
		// eventTypes.add(EnableNeighborhoodEvent.class);
		// eventTypes.add(EnableTexturesEvent.class);
		// eventTypes.add(InfoAreaUpdateEvent.class);
		// eventTypes.add(InterchangeContentGroupsEvent.class);
		// eventTypes.add(MergeStorageGroupsEvent.class);
		// eventTypes.add(ChangeQueryTypeEvent.class);
		// eventTypes.add(ChangeURLEvent.class);
		// eventTypes.add(ClustererCanceledEvent.class);
		// eventTypes.add(ClusterProgressEvent.class);
		// eventTypes.add(RenameProgressBarEvent.class);
		// eventTypes.add(ReplaceVAEvent.class);
		// eventTypes.add(ReplaceContentVAInUseCaseEvent.class);
		// eventTypes.add(StartClusteringEvent.class);
		// eventTypes.add(ClusterNodeSelectionEvent.class);
		// eventTypes.add(ClientListEvent.class);
		// eventTypes.add(AddConnectionLineVerticesEvent.class);
		// eventTypes.add(ClearConnectionsEvent.class);

		// eventTypes.add(NewSetEvent.class);

		return eventTypes;
	}

	public void registerSerializableType(Class<?> serializableClass) {
		projectTypes.add(serializableClass);
		createNewProjectContext();
	}
}
