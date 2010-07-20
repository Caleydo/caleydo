package org.caleydo.view.heatmap;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.view.heatmap.creator.ViewCreatorDendrogramHorizontal;
import org.caleydo.view.heatmap.creator.ViewCreatorDendrogramVertical;
import org.caleydo.view.heatmap.creator.ViewCreatorHeatMap;
import org.caleydo.view.heatmap.creator.ViewCreatorHierarchicalHeatMap;
import org.caleydo.view.heatmap.dendrogram.GLDendrogram;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.hierarchical.GLHierarchicalHeatMap;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.caleydo.view.heatmap";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		registerDataDomains();
		GeneralManager.get().getViewGLCanvasManager()
				.addViewCreator(new ViewCreatorHeatMap(GLHeatMap.VIEW_ID));

		GeneralManager
				.get()
				.getViewGLCanvasManager()
				.addViewCreator(
						new ViewCreatorHierarchicalHeatMap(GLHierarchicalHeatMap.VIEW_ID));

		GeneralManager
				.get()
				.getViewGLCanvasManager()
				.addViewCreator(
						new ViewCreatorDendrogramHorizontal(GLDendrogram.VIEW_ID
								+ ".horizontal"));

		GeneralManager
				.get()
				.getViewGLCanvasManager()
				.addViewCreator(
						new ViewCreatorDendrogramVertical(GLDendrogram.VIEW_ID
								+ ".vertical"));

		// Force bundle view plugin bookmarking to be loaded because it is not
		// created via RCP
		Platform.getBundle("org.caleydo.view.bookmarking").start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	private void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();
		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		dataDomainTypes.add("org.caleydo.datadomain.generic");
		dataDomainTypes.add("org.caleydo.datadomain.clinical");

		DataDomainManager.getInstance().getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes, PLUGIN_ID);
		DataDomainManager.getInstance().getAssociationManager()
		.registerDatadomainTypeViewTypeAssociation(dataDomainTypes, GLHierarchicalHeatMap.VIEW_ID);
		
	}

}
