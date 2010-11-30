package org.caleydo.view.filterpipeline;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.caleydo.core.data.filter.ContentFilter;
import org.caleydo.core.data.filter.ContentMetaFilter;
import org.caleydo.core.data.filter.StorageFilter;
import org.caleydo.core.data.filter.StorageMetaFilter;
import org.caleydo.core.data.filter.event.FilterUpdatedEvent;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.caleydo.view.filterpipeline.listener.FilterUpdateListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * TODO: DOCUMENT ME!
 * 
 * @author <INSERT_YOUR_NAME>
 */
public class RcpGLFilterPipelineView
	extends ARcpGLViewPart
	implements IListenerOwner
{
	public static final String VIEW_ID = "org.caleydo.view.filterpipeline";

	private ASetBasedDataDomain dataDomain;

	private EventPublisher eventPublisher;

	private FilterUpdateListener filterUpdateListener;

	/**
	 * Constructor.
	 */
	public RcpGLFilterPipelineView()
	{
		super();

		try
		{
			viewContext = JAXBContext.newInstance(SerializedFilterPipelineView.class);
		}
		catch (JAXBException ex)
		{
			throw new RuntimeException("Could not create JAXBContext", ex);
		}

		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		
		dataDomain =
			(ASetBasedDataDomain) DataDomainManager.get().getDataDomain
			(
				"org.caleydo.datadomain.genetic"
			);

		
		createGLCanvas();
		view = new GLFilterPipeline(glCanvas, serializedView.getViewFrustum());
		view.initFromSerializableRepresentation(serializedView);
		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView()
	{
		serializedView = new SerializedFilterPipelineView();
		determineDataDomain(serializedView);
	}

	@Override
	public String getViewGUIID()
	{
		return GLFilterPipeline.VIEW_ID;
	}

	@Override
	public void queueEvent(final AEventListener<? extends IListenerOwner> listener,
			final AEvent event)
	{
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				listener.handleEvent(event);
			}
		});
	}

	@Override
	public void dispose()
	{
		super.dispose();
		unregisterEventListeners();
	}

	@Override
	public void registerEventListeners()
	{
		filterUpdateListener = new FilterUpdateListener();
		filterUpdateListener.setHandler(this);
		eventPublisher.addListener(FilterUpdatedEvent.class, filterUpdateListener);
	}

	@Override
	public void unregisterEventListeners()
	{
		if (filterUpdateListener != null)
		{
			eventPublisher.removeListener(filterUpdateListener);
			filterUpdateListener = null;
		}
	}

	public void handleFilterUpdatedEvent()
	{
		Logger.log(new Status(IStatus.INFO, this.toString(), "Filterupdate..."));
		
		List<Filter> filterList = new LinkedList<Filter>();
		int filterID = 0;

		System.out.println("StorageFilter:");
		
		for (StorageFilter filter : dataDomain.getStorageFilterManager().getFilterPipe())
		{
			int num_removed_items = 0;
			
			if (filter instanceof StorageMetaFilter)
			{
				System.out.println("MetaFilter");

//				for (StorageFilter subFilter : ((StorageMetaFilter) filter).getFilterList())
//				{
//					// TODO
//				}
			}
			else
			{
				for (VADeltaItem deltaItem : filter.getVADelta().getAllItems())
				{
					if( deltaItem.getType() == EVAOperation.REMOVE_ELEMENT )
					{
						++num_removed_items;
					}
					else
						System.out.println(deltaItem);
					
					//deltaItem.getIndex()
				}
			}
			
			System.out.println(filter.getLabel()+" (filtered "+num_removed_items+" items)");
			
			filterList.add(new Filter(filterID++, filter.getLabel(), num_removed_items, filter.getFilterRep()));
		}

		System.out.println("ContentFilter:");

		for (ContentFilter filter : dataDomain.getContentFilterManager().getFilterPipe())
		{
			int num_removed_items = 0;

			if (filter instanceof ContentMetaFilter)
			{
				System.out.println("MetaFilter");

//				for (ContentFilter subFilter : ((ContentMetaFilter) filter).getFilterList())
//				{
//					// TODO
//				}
			}
			else
			{
				for (VADeltaItem deltaItem : filter.getVADelta().getAllItems())
				{
					if( deltaItem.getType() == EVAOperation.REMOVE_ELEMENT )
						++num_removed_items;
					else
						System.out.println(deltaItem);
				}
			}
			
			System.out.println(filter.getLabel()+" (filtered "+num_removed_items+" items)");
			filterList.add(new Filter(filterID++, filter.getLabel(), num_removed_items, filter.getFilterRep()));
		}
		
		// TODO what shall we do with genes vs. experiments?
		((GLFilterPipeline)view).handleFilterUpdated(filterList, dataDomain.getSet().depth());
	}

}