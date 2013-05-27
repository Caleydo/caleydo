/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.clusterer;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.clusterer.gui.AClusterTab;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Stopwatch;

/**
 * Cluster manager handels {@link ClusterConfiguration} and calls corresponding
 * clusterer.
 *
 * @author Bernhard Schlegl
 * @author Alexander Lex
 */
public final class Clusterers {
	private static final String EXTENSION_POINT = "org.caleydo.core.util.Clusterer";

	private static final Collection<IClustererFactory> clusterers = ExtensionUtils.findImplementation(EXTENSION_POINT,
			"class", IClustererFactory.class);

	private static final Logger log = Logger.create(Clusterers.class);

	private Clusterers() {

	}

	public static Collection<AClusterTab> createClusterTabs(TabFolder folder) {
		Collection<AClusterTab> tabs = new ArrayList<>(clusterers.size());
		for (IClustererFactory clusterer : clusterers) {
			tabs.add(clusterer.createClusterTab(folder));
		}
		return tabs;
	}

	public static ClusterResult cluster(ClusterConfiguration config) {
		log.info("Started clustering with clusterConfiguration: " + config);
		Stopwatch w = new Stopwatch().start();
		try {
			SafeCallable<PerspectiveInitializationData> clusterer = createClusterer(config);
			if (clusterer == null) {
				log.error("unknown cluster configuration: " + config);
				throw new IllegalStateException("Unknown ClusterConfiguration: " + config);
			}

			PerspectiveInitializationData data = clusterer.call();

			ClusterResult result = new ClusterResult();
			switch (config.getClusterTarget()) {
			case DIMENSION_CLUSTERING:
				result.setDimensionResult(data);
				break;
			case RECORD_CLUSTERING:
				result.setRecordResult(data);
				break;
			}
			return result;
		} catch (final Exception e) {
			log.error("Clustering failed", e);

			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageBox messageBox = new MessageBox(new Shell(), SWT.ERROR);
							messageBox.setText("Error");
							messageBox.setMessage("A problem occured during clustering!");
							messageBox.open();
						}
					});
				}
			});
		} finally {
			log.debug("took: " + w);
		}
		return null;
	}

	/**
	 * @param config
	 * @return
	 */
	private static SafeCallable<PerspectiveInitializationData> createClusterer(ClusterConfiguration config) {
		SafeCallable<PerspectiveInitializationData> r = null;
		for(IClustererFactory clusterer: clusterers) {
			r = clusterer.create(config, 2, 0);
			if (r != null) // found the correct one
				return r;
		}
		return null;
	}
}
