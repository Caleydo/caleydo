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
package org.caleydo.core.util.clusterer.algorithm;

import java.util.Arrays;
import java.util.List;

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

import com.jogamp.common.util.IntIntHashMap;

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
			opposite = config.getSourceRecordPerspective();
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

		this.progressMultiplier = progressMultiplier;
		this.progressOffset = progressOffset;
		this.distance = config.getDistanceMeasure();
	}

	protected final float distance(float[] a, float[] b) {
		return this.distance.apply(a, b);
	}

	protected final String getPerspectiveLabel() {
		return config.getSourcePerspective().getIdType().getTypeName();
	}

	protected final Float getNormalizedValue(Integer vaID, Integer oppositeVaID) {
		switch (config.getClusterTarget()) {
		case DIMENSION_CLUSTERING:
			return table.getNormalizedValue(vaID, oppositeVaID);
		case RECORD_CLUSTERING:
			return table.getNormalizedValue(oppositeVaID, vaID);
		}
		return null;
	}

	/**
	 * Function sorts clusters depending on their average value
	 *
	 * @return an lookup clustersample -&gt; clusterIndex
	 */
	protected final IntIntHashMap sortClusters(List<Integer> clusterSamples) {
		SortHelper[] list = new SortHelper[clusterSamples.size()];
		int index = 0;
		for (Integer vaId : clusterSamples) {
			SortHelper s = new SortHelper();
			s.index = index++;
			for (Integer opId : oppositeVA) {
				float temp = getNormalizedValue(vaId, opId);
				if (!Float.isNaN(temp))
					s.value += temp;
			}
			list[s.index] = s;
		}
		Arrays.sort(list);

		IntIntHashMap lookup = new IntIntHashMap(clusterSamples.size());
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
		EventPublisher.trigger(new ClusterProgressEvent(percentCompleted, forSimilaritiesBar));
	}

	protected static void rename(String text) {
		EventPublisher.trigger(new RenameProgressBarEvent(text));
	}
}
