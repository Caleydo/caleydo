/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventListenerManagers.QueuedEventListenerManager;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.ClusterProgressEvent;
import org.caleydo.core.event.data.ClustererCanceledEvent;
import org.caleydo.core.event.data.RenameProgressBarEvent;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Abstract base class for clusterers that handles external events
 *
 * @author Alexander Lex
 */
public abstract class AClusterer implements SafeCallable<PerspectiveInitializationData> {

	protected final QueuedEventListenerManager eventListeners = EventListenerManagers.createQueued();

	protected volatile boolean isClusteringCanceled = false;

	// variables needed for correct visualization of cluster progress bar
	private final int progressMultiplier;
	private final int progressOffset;

	protected final Table table;
	protected final ATableBasedDataDomain dataDomain;
	protected final VirtualArray va;
	protected final VirtualArray oppositeVA;

	protected final ClusterConfiguration config;

	private final EDistanceMeasure distance;

	public AClusterer(ClusterConfiguration config, int progressMultiplier, int progressOffset) {
		this.config = config;
		Perspective p;
		Perspective opposite;
		switch (config.getClusterTarget()) {
		case RECORD_CLUSTERING:
			p = config.getSourceRecordPerspective();
			opposite = config.getSourceDimensionPerspective();
			break;
		case DIMENSION_CLUSTERING:
			p = config.getSourceDimensionPerspective();
			opposite = config.getSourceRecordPerspective();
			break;
		default:
			throw new IllegalStateException();
		}
		this.va = p.getVirtualArray();
		this.oppositeVA = opposite.getVirtualArray();
		this.table = ((ATableBasedDataDomain) p.getDataDomain()).getTable();
		this.dataDomain = table.getDataDomain();

		this.progressMultiplier = progressMultiplier;
		this.progressOffset = progressOffset;
		this.distance = config.getDistanceMeasure();
	}

	protected final float distance(float[] a, float[] b) {
		return this.distance.apply(a, b);
	}

	protected final float get(Integer vaID, Integer oppositeVaID) {
		return dataDomain.getNormalizedValue(va.getIdType(), vaID, oppositeVA.getIdType(), oppositeVaID);
	}

	protected final String getPerspectiveLabel() {
		return config.getSourcePerspective().getIdType().getTypeName();
	}



	/**
	 * Function sorts clusters depending on their average value
	 *
	 * @return an lookup clustersample -&gt; clusterIndex
	 */
	protected final Map<Integer, Integer> sortClusters(List<Integer> clusterSamples) {
		SortHelper[] list = new SortHelper[clusterSamples.size()];
		int index = 0;
		for (Integer vaId : clusterSamples) {
			SortHelper s = new SortHelper();
			s.index = index++;
			for (Integer opId : oppositeVA) {
				float temp = get(vaId, opId);
				if (!Float.isNaN(temp))
					s.value += temp;
			}
			list[s.index] = s;
		}
		Arrays.sort(list);

		Map<Integer, Integer> lookup = new HashMap<>(clusterSamples.size());
		for (int i = 0; i < list.length; ++i) {
			lookup.put(clusterSamples.get(list[i].index), i);
		}
		return lookup;
	}

	private static class SortHelper implements Comparable<SortHelper> {
		private int index;
		private float value;

		@Override
		public int compareTo(SortHelper o) {
			return Float.compare(value, o.value);
		}
	}

	@Override
	public final PerspectiveInitializationData call() {
		eventListeners.register(this);
		try {
			return cluster();
		} finally {
			progress(100, true);
			eventListeners.unregisterAll();
		}
	}

	protected abstract PerspectiveInitializationData cluster();

	@ListenTo(sendToMe = true)
	private void onCancel(ClustererCanceledEvent event) {
		isClusteringCanceled = true;
	}

	protected final PerspectiveInitializationData error(Exception e1) {
		Logger.log(new Status(IStatus.ERROR, this.toString(), "Clustering fails: " + e1));
		progress(100, true);
		return null;
	}

	protected final PerspectiveInitializationData canceled() {
		progress(100, true);
		return null;
	}

	protected final void progressScaled(int factor) {
		progress(factor * progressMultiplier + progressOffset, true);
	}

	protected final boolean progressAndCancel(int percentCompleted, boolean forSimilaritiesBar) {
		eventListeners.processEvents();
		if (isClusteringCanceled) {
			return true;
		}
		progress(percentCompleted, forSimilaritiesBar);
		return false;
	}

	protected static void progress(int percentCompleted) {
		progress(percentCompleted, true);
	}
	protected static void progress(int percentCompleted, boolean forSimilaritiesBar) {
		EventPublisher.trigger(new ClusterProgressEvent(Math.min(percentCompleted, 100), forSimilaritiesBar));
	}

	protected static void rename(String text) {
		EventPublisher.trigger(new RenameProgressBarEvent(text));
	}
}
