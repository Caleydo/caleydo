package org.jgraph.plugins.gpgraph;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexView;
import org.jgraph.pad.coreframework.GPGraphpad;
import org.jgraph.pad.coreframework.GPPluginInvoker;

/**
 * @author Gaudenz Alder
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GPExplorer extends JPanel 
	implements GraphSelectionListener, ChangeListener {

	protected transient Object lastSelectionCell;
	protected transient GPGraph graph;
	protected transient JCheckBox exploreMode = new JCheckBox("Explore", true);
	protected transient JCheckBox autoArrange = new JCheckBox("Arrange", true);
	protected transient SpinnerNumberModel model = new SpinnerNumberModel(2, 1, 50, 1);
	protected transient JSpinner levelSpinner = new JSpinner(model);
	protected transient JLabel statusBar = new JLabel("Ready");
	
	public GPExplorer(GPGraphpad graphpad) {
		JGraph graph = graphpad.getCurrentGraph();
		setLayout(new BorderLayout());
		this.graph = (GPGraph) GPPluginInvoker.instanciateObjectForKey("JGraph.class");
        this.graph.setModel(graph.getModel());
		this.graph.setGraphLayoutCache(
			new StatefulGraphLayoutCache(graph.getModel(),
										 graph.getGraphLayoutCache().getFactory(),
										 true));
		JPanel toolBar = new JPanel();
		JButton showAll = new JButton("Reset");
		showAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		toolBar.add(showAll);
		toolBar.add(exploreMode);
		toolBar.add(autoArrange);
		toolBar.add(levelSpinner);
		add(toolBar, BorderLayout.NORTH);
		add(new JScrollPane(this.graph), BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);
		this.graph.addGraphSelectionListener(this);
		this.levelSpinner.addChangeListener(this);
		reset();
		executeLayout();
	}
	
	public void reset() {
		graph.getGraphLayoutCache().setVisible(graph.getAll(), true);
		graph.clearSelection();
		executeLayout();
	}
	
	public void execute() {
		if (exploreMode.isSelected()
			&& graph.getSelectionCount() == 1
			&& !graph.getModel().isEdge(
				graph.getSelectionCell())) {
			graph.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			// Hide all
			GraphLayoutCache cache = graph.getGraphLayoutCache();
			Object[] all =
				cache.getCells(
					cache.getAllDescendants(cache.getRoots()));
			cache.setVisible(all, false);
			int levels = model.getNumber().intValue();
			Object[] cells = graph.getSelectionCells();
			Set edges = new HashSet();
			for (int i = 0; i < levels; i++) {
				edges.addAll(
					DefaultGraphModel.getEdges(
						graph.getModel(),
						cells));
				cells =
					getVerticesForEdges(graph, edges)
						.toArray();
			}
			graph.getGraphLayoutCache().setVisible(
				edges.toArray(),
				true);
			graph.getGraphLayoutCache().toBack(edges.toArray());
			if (autoArrange.isSelected())
				executeLayout();
			graph.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			CellView[] views =
				cache.getAllDescendants(cache.getRoots());
			int edgeCount = 0, ports = 0, vertices = 0, groups = 0;
			for (int i = 0; i < views.length; i++) {
				if (!views[i].isLeaf())
					groups++;
				else if (views[i] instanceof EdgeView)
					edgeCount++;
				else if (views[i] instanceof PortView)
					ports++;
				else
					vertices++;
			}
			cells = graph.getDescendants(graph.getRoots());
			statusBar.setText(
				vertices
					+ " vertice(s) "
					+ edgeCount
					+ " edges(s) "
					+ ports
					+ " port(s) in "
					+ groups
					+ " group(s) visible out of "
					+ cells.length
					+ " cell(s)");
	}
	}

	public static Set getVerticesForEdges(GPGraph graph, Set edges) {
		HashSet set = new HashSet();
		Iterator it = edges.iterator();
		while (it.hasNext()) {
			Object edge = it.next();
			set.add(graph.getModel().getSource(edge));
			set.add(graph.getModel().getTarget(edge));
		}
		return set;
	}

	public void stateChanged(ChangeEvent e) {
		execute();
	}

	public void valueChanged(GraphSelectionEvent e) {
		//awps.resetTouchLayout();
		if ((graph.getSelectionCount() == 1)
			&& graph.getSelectionCell() == lastSelectionCell
			&& !graph.isEditing()) {
			graph.startEditingAtCell(lastSelectionCell);
		} else {
			execute();
		}
		lastSelectionCell = graph.getSelectionCell();
	}

	public class StatefulGraphLayoutCache extends GraphLayoutCache {
	
		/* Boolean indicating whether new groups should be automatically shown.
		 * This setting has no effect in non-partial views.
		 */
		public static final boolean showNewGroups = false;
	
		protected boolean askLocalAttribute = true;
		
		protected Set localAttributes = new HashSet();
	
		public StatefulGraphLayoutCache(GraphModel model,
										 CellViewFactory factory,
										 boolean partial) {
			super(model, factory, partial);
			localAttributes.add(GraphConstants.BOUNDS);
			localAttributes.add(GraphConstants.POINTS);
			localAttributes.add(GraphConstants.LABELPOSITION);
			localAttributes.add(GraphConstants.ROUTING);
		}
	
		public Set getLocalAttributes() {
			return new HashSet(localAttributes);
		}
	
		public void setLocalAttributes(Set attributes) {
			localAttributes = attributes;
		}
	
		// Nested is in-out
		// TODO: No longer needed
		protected GraphLayoutCacheEdit createLocalEdit(
			Object[] inserted,
			Map nested,
			Object[] visible,
			Object[] invisible) {
			if ((nested != null && !nested.isEmpty()) && askLocalAttribute) {
				// Move or Copy Local Attributes to Local View
				Map globalMap = new Hashtable();
				Map localMap = new Hashtable();
				Map localAttr;
				Iterator it = nested.entrySet().iterator();
				while (it.hasNext()) {
					localAttr = new Hashtable();
					Map.Entry entry = (Map.Entry) it.next();
					// (cell, Hashtable)
					Object cell = entry.getKey();
					Map attr = (Map) entry.getValue();
					// Create Difference of Existing and New Attributes
					CellView tmpView = getMapping(cell, false);
					if (tmpView != null)
						attr = tmpView.getAllAttributes().diff(attr);
					// End of Diff
					Iterator it2 = attr.entrySet().iterator();
					while (it2.hasNext()) {
						Map.Entry entry2 = (Map.Entry) it2.next();
						// (key, value)
						Object key = entry2.getKey();
						Object value = entry2.getValue();
						if (isLocalAttribute(cell, key, value)
							|| isControlAttribute(cell, key, value)) {
							localAttr.put(key, value);
							if (!isControlAttribute(cell, key, value))
								it2.remove();
						}
					}
					if (!localAttr.isEmpty())
						localMap.put(cell, localAttr);
					if (!attr.isEmpty())
						globalMap.put(cell, attr);
				}
				nested.clear();
				nested.putAll(globalMap);
				GraphLayoutCacheEdit edit =
					new GraphLayoutCacheEdit(inserted, new Hashtable(localMap), visible, invisible);
				edit.end();
				return edit;
			}
			return super.createLocalEdit(inserted, nested, visible, invisible);
		}

	}
	
	public void executeLayout() {
		executeCircleLayout();
		executeCircleLayout();
	}

	protected void executeCircleLayout() {
		Dimension min = graph.getPreferredSize();
		// Fetch All Views
		CellView[] views = graph.getGraphLayoutCache().getRoots();

		// Create list to hold vertices
		List vertices = new ArrayList();
		// Maximum width or height
		double max = 0;
		// Loop through all views
		for (int i = 0; i < views.length; i++) {
			// Add vertex to list
			if (views[i] instanceof VertexView
				&& !graph.isCellSelected(views[i].getCell())) {
				vertices.add(views[i]);
				// Fetch Bounds
				Rectangle2D bounds = views[i].getBounds();
				// Update Maximum
				if (bounds != null)
					max = Math.max(Math.max(bounds.getWidth(), bounds.getHeight()), max);
			}
		}
		// Compute Radius
		int r = (int) Math.max(vertices.size() * max / Math.PI / 2.5, 200);

		int offx = Math.max(r, min.width / 2);
		int offy = Math.max(r, min.height / 2);

		Hashtable nested = new Hashtable();

		// Compute angle step
		double phi = 2 * Math.PI / vertices.size();
		// Arrange vertices in a circle
		for (int i = 0; i < vertices.size(); i++) {
			CellView view = (CellView) vertices.get(i);
			Rectangle bounds = new Rectangle(view.getBounds().getBounds());
			// Update Location
			if (bounds != null) {
				bounds.setLocation(
					offx + (int) (r * Math.sin(i * phi)),
					offy + (int) (r * Math.cos(i * phi)));
				AttributeMap attr = new AttributeMap();
				GraphConstants.setBounds(attr, bounds);
				nested.put(view.getCell(), attr);
			}
		}
		// Move selected cell(s) to center
		Object[] cells = graph.getSelectionCells();
		for (int i = 0; i < cells.length; i++) {
			if (graph.getGraphLayoutCache().isVisible(cells[i])) {
				Rectangle2D bounds2D = graph.getCellBounds(cells[i]);
				Rectangle bounds = bounds2D.getBounds();
				// Update Location
				if (bounds != null) {
					bounds.setLocation(offx, offy);
					AttributeMap attr = new AttributeMap();
					GraphConstants.setBounds(attr, bounds);
					nested.put(cells[i], attr);
				}
			}
		}
		graph.getGraphLayoutCache().edit(nested, null, null, null);
	}

}
