package org.caleydo.rcp.views;

import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.glyph.GlyphMappingConfigurationViewRep;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class SWTGlyphMappingConfigurationView
	extends ViewPart
{
	public static final String ID = "org.caleydo.rcp.views.SWTGlyphMappingConfigurationView";

	// private HTMLBrowserViewRep browserView;
	private GlyphMappingConfigurationViewRep GMCview;

	@Override
	public void createPartControl(Composite parent)
	{
		GMCview = (GlyphMappingConfigurationViewRep) GeneralManager.get()
				.getViewGLCanvasManager().createView(
						EManagedObjectType.VIEW_SWT_GLYPH_MAPPINGCONFIGURATION, -1,
						"Glyph Mapping Configuration");

		GMCview.initViewRCP(parent);
		GMCview.drawView();

		GeneralManager.get().getViewGLCanvasManager().registerItem(GMCview);
	}

	@Override
	public void setFocus()
	{

	}

	@Override
	public void dispose()
	{
		super.dispose();

		GeneralManager.get().getEventPublisher().removeReceiver(
				EMediatorType.SELECTION_MEDIATOR, (IMediatorReceiver) GMCview);

		GeneralManager.get().getViewGLCanvasManager().unregisterItem(GMCview.getID());
	}
}
