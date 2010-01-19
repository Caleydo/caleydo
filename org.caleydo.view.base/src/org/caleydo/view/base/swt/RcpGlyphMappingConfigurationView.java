package org.caleydo.view.base.swt;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.swt.glyph.GlyphMappingConfigurationViewRep;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class RcpGlyphMappingConfigurationView extends ViewPart {
	public static final String ID = "org.caleydo.rcp.views.swt.SWTGlyphMappingConfigurationView";

	// private HTMLBrowserViewRep browserView;
	private GlyphMappingConfigurationViewRep GMCview;

	@Override
	public void createPartControl(Composite parent) {
		GMCview = (GlyphMappingConfigurationViewRep) GeneralManager.get()
				.getViewGLCanvasManager().createView(
						"org.caleydo.view.glyph.mappingconfiguration", -1,
						"Glyph Mapping Configuration");

		GMCview.initViewRCP(parent);
		GMCview.drawView();

		GeneralManager.get().getViewGLCanvasManager().registerItem(GMCview);
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();

		GeneralManager.get().getViewGLCanvasManager().unregisterItem(
				GMCview.getID());
	}
}
