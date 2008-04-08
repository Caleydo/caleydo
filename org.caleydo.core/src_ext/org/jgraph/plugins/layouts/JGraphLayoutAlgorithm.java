/*
 * @(#)JGraphLayoutAlgorithm.java 1.0 18-MAY-2004
 * 
 * Copyright (c) 2001-2005, Gaudenz Alder
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jgraph.plugins.layouts;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.VertexView;

/**
 *
 */
public abstract class JGraphLayoutAlgorithm {

	protected static Set LAYOUT_ATTRIBUTES = new HashSet();
	
	static {
		LAYOUT_ATTRIBUTES.add(GraphConstants.BOUNDS);
		LAYOUT_ATTRIBUTES.add(GraphConstants.POINTS);
		LAYOUT_ATTRIBUTES.add(GraphConstants.LABELPOSITION);
		LAYOUT_ATTRIBUTES.add(GraphConstants.ROUTING);
	}
	
	/**
	 * Set to false if the algorithm should terminate immediately
	 */
	boolean isAllowedToRun = true;
	
	/**
	 * Set to non zero if you want to indicate progress
	 */
	int progress = 0, maximumProgress = 0;

	/**
	 * Subclassers may return a new JComponent that
	 * allows to configure the layout. The default
	 * implementation returns <code>null</code>.<br>
	 * Note: Settings creation may be expensive so
	 * the UI should cache the values returned by
	 * this method.
	 */
	public JGraphLayoutSettings createSettings() {
		return null;
	}
	
	/**
	 * Get a human readable hint for using this layout.
	 */
	public String getHint() {
		return "";
	}
	
	/**
	 * Executes the layout algorithm with the given cells to be moved
	 * @param graph JGraph to be altered by layout
	 * @param cells Array of cells to be moved by the layout
	 */
	public void run(JGraph graph, Object[] cells) {
		run(graph, cells, null);
	}
	
	/**
	 * Executes the layout algorithm specifying which cells are to remain
	 * in place after the layout is applied.
	 * @param jgraph JGraph to be altered by layout
	 * @param dynamic_cells Cells that are to be moved by the layout
	 * @param static_cells Cells that are not to be moved, but allowed for by the layout
	 */
	public abstract void run(	JGraph jgraph,
								Object[] dynamic_cells,
								Object[] static_cells);

	/**
	 * @return Returns the isAllowedToRun.
	 */
	public boolean isAllowedToRun() {
		return isAllowedToRun;
	}
	/**
	 * @param isAllowedToRun The isAllowedToRun to set.
	 */
	public void setAllowedToRun(boolean isAllowedToRun) {
		this.isAllowedToRun = isAllowedToRun;
	}
	
	/**
	 * Returns the maximum progress
	 */
	public int getMaximumProgress() {
		return maximumProgress;
	}

	/**
	 * Sets the maximum progress.
	 */
	public void setMaximumProgress(int maximumProgress) {
		this.maximumProgress = maximumProgress;
	}

	/**
	 * Returns the current progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress The progress complete amount
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * A utility method to create a simple dialog with
	 * close and apply button.
	 * @param settings Layout settings instance
	 * @param parent Parent JFrame
	 * @param title Title of dialog box
	 * @param close Text for cancel button
	 * @param apply Text for apply button
	 * @return JDialog dialog to be displayed
	 */
	public static JDialog createDialog(final JGraphLayoutSettings settings, 
				JFrame parent, String title, String close, String apply)
	{
		if (settings instanceof Component)
			return populateDialog(settings, new JDialog(parent, title, true), close, apply);
		return null;
	}
	
	/**
	 * A utility method to create a simple dialog with
	 * close and apply button.
	 * @param settings Layout settings instance
	 * @param parent Parent JDialog
	 * @param title Title of dialog box
	 * @param close Text for cancel button
	 * @param apply Text for apply button
	 * @return JDialog dialog to be displayed
	 */
	public static JDialog createDialog(final JGraphLayoutSettings settings, 
				JDialog parent, String title, String close, String apply)
	{
		if (settings instanceof Component)
			return populateDialog(settings, new JDialog(parent, title, true), close, apply);
		return null;
	}
	
	/**
	 * A utility method to create a simple dialog with
	 * close and apply button.
	 * @param settings Layout settings instance
	 * @param dialog JDialog to be used
	 * @param close Text for cancel button
	 * @param apply Text for apply button
	 * @return JDialog showing the settings for the layout
	 */
	public static JDialog populateDialog(final JGraphLayoutSettings settings, 
				final JDialog dialog, String close, String apply)
	{
		if (dialog != null && settings instanceof Component) {
			dialog.getContentPane().setLayout(new BorderLayout());
			dialog.getContentPane().add((Component) settings, BorderLayout.CENTER);
			JButton cancelButton = new JButton(close);
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dialog.dispose();
				}
			});
			JButton applyButton = new JButton(apply);
			applyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					settings.apply();
					dialog.dispose();
				}
			});
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			buttonPanel.add(applyButton);
			buttonPanel.add(cancelButton);
			dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
			dialog.getRootPane().setDefaultButton(applyButton);
			return dialog;
		}
		return dialog;
	}

	/**
	 * Calls applyLayout() with null static_cells
	 * @param sourceGraph The JGraph to have the layout applied
	 * @param layout The layout algorithm to be applied
	 * @param cells Cells that are to be moved by the layout
	 */
	public static void applyLayout(	JGraph sourceGraph,
			JGraphLayoutAlgorithm layout,
			Object[] cells ) {
		JGraphLayoutAlgorithm.applyLayout(sourceGraph,
											layout,
											cells,
											null);
	}
	
	/**
	 * Takes a local clone of the JGraph and calls the run() method on the
	 * specified layout algorithm on the local JGraph. After the layout has
	 * been applied, the changed attributes on the local JGraph are extracted
	 * and applied to the JGraph instance passed in, creating one undoable edit.
	 * @param sourceGraph The JGraph to have the layout applied
	 * @param layout The layout algorithm to be applied
	 * @param dynamic_cells Cells that are to be moved by the layout
	 * @param static_cells Cells that are not to be moved, but allowed for by the layout
	 */
	public static void applyLayout(	JGraph sourceGraph,
									JGraphLayoutAlgorithm layout,
									Object[] dynamic_cells,
									Object[] static_cells ) {
		JGraph localGraph = new JGraph(sourceGraph.getModel());
		localGraph.setBounds(sourceGraph.getBounds());
		GraphLayoutCache cache = localGraph.getGraphLayoutCache();
		cache.setLocalAttributes(LAYOUT_ATTRIBUTES);
		layout.run(localGraph, dynamic_cells, static_cells);
		if (layout.isAllowedToRun()) {
			// fetch attributes from cellview and write to source Graph
			Map nested = new Hashtable();
			CellView[] cellViews = cache.getAllDescendants(cache.getRoots());
			for (int i = 0; i < cellViews.length; i++) {
				Map attrs = cellViews[i].getAttributes();
				Rectangle2D bounds = GraphConstants.getBounds(attrs);
				if (cellViews[i] instanceof VertexView && bounds == null) {
					GraphConstants.setBounds(attrs, cellViews[i].getBounds());
				}
				if (!attrs.isEmpty())
					nested.put(cellViews[i].getCell(), attrs);
			}
			if (!nested.isEmpty())
				sourceGraph.getGraphLayoutCache().edit(nested, null, null, null);
		}
	}
	
	
}
