/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * GPGraphpad is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * GPGraphpad is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPGraphpad; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.jgraph.pad.coreframework;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.MissingResourceException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ToolTipManager;

import org.jgraph.JGraph;
import org.jgraph.event.GraphLayoutCacheEvent;
import org.jgraph.event.GraphLayoutCacheListener;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphUndoManager;
import org.jgraph.graph.Port;
import org.jgraph.pad.coreframework.jgraphsubclassers.GPMarqueeHandler;
import org.jgraph.pad.resources.Translator;
import org.jgraph.pad.util.ICommandRegistery;
import org.jgraph.pad.util.IEdgeFactory;
import org.jgraph.pad.util.IVertexFactory;
import org.jgraph.pad.util.Rule;
import org.jgraph.plaf.GraphUI;

/**
 * A Document represents a single instance of a graph view with associated
 * library and overview panes. The document deals with a lot of the listening
 * required on the graph, prompting for save if modified, undo handling and top
 * level UI issues relating to pane positioning.
 * 
 * You could subclass GPDocument, but often subclassing the objects GPDocument
 * deals with should be sufficient. In order to ensure subclassers will be used
 * by GPDocuments, critical objects are instanciated by a factory where you can
 * register custom subclassers.
 * 
 * There is also a hook for custom plugins to ba able to redefine/decorate the
 * rootPane of the document and also to register themself in the pluginsMap.
 */
public class GPDocument implements GraphSelectionListener,
        ComponentListener, Printable, GraphModelListener,
        PropertyChangeListener, GraphLayoutCacheListener, ICommandRegistery {
	
	/**
	 * use to put associate aribtrary plugins to the document
	 */
	protected Map pluginsMap;
	
	protected JComponent docComponent;

    protected boolean enableTooltips;

    /**
     * A reference to the top level component
     */
    protected GPGraphpad graphpad;

    /**
     * Container for the graph so that you can scroll over the graph
     */
    protected JScrollPane scrollPane;

    /**
     * The joint graph for this document
     */
    protected JGraph graph;

    /**
     * The column rule for the graph
     */
    protected Rule columnRule;

    /**
     * The row rule for the graph
     */
    protected Rule rowRule;

    /**
     * The graphUndoManager manager for the joint graph.
     * 
     * @see #graph
     */
    protected GraphUndoManager graphUndoManager;

    /**
     * The graphUndoManager handler for the current document. Each document has
     * his own handler. So you can make an graphUndoManager seperate for each
     * document.
     * 
     */
    protected GPUndoHandler undoHandler;

    /**
     * True if this documents graph model was modified since last save.
     */
    protected boolean modified = false;

    /**
     * true if the current graph is Metric. default is true.
     */
    protected static boolean isMetric = true;

    /**
     * true if the ruler show is activated
     */
    protected static boolean showRuler = true;

    /**
     * Action used for fitting the size
     */
    protected Action fitAction;

    /**
     * contains the find pattern for this document
     */
    protected String findPattern;

    /**
     * contains the last found object
     */
    protected Object lastFound;

    /**
     * a reference to the internal Frame
     */
    protected GPDocFrame internalFrame;

    /**
     * The data of the document which is meant to be saved
     */
    protected GPGraphpadFile jGraphpadCEFile;
    
    protected ArrayList edgeCreators = new ArrayList();

    protected ArrayList vertexnPortsCreators = new ArrayList();
    
    protected Image backgroundImage;
    
    protected boolean pagevisible = false;
    
    protected transient PageFormat pageFormat = new PageFormat();

    public GPDocument(GPGraphpad gp, String name, GPGraphpadFile file) {
    	pluginsMap = new Hashtable();//empty by default
        isMetric = new Boolean(Translator.getString("IsMetric")).booleanValue();
        showRuler = new Boolean(Translator.getString("ShowRuler"))
                .booleanValue();
        enableTooltips = new Boolean(Translator.getString("IsEnableTooltips")).booleanValue();
        docComponent = new JPanel();
        docComponent.setDoubleBuffered(true);
        docComponent.updateUI();
        docComponent.setName(name);//we take care of setting a proper name later if it's null
        graphpad = gp;
        undoHandler = new GPUndoHandler(this);
        graphUndoManager = createGraphUndoManager();
        
        jGraphpadCEFile = file;
        if (jGraphpadCEFile == null)
        	jGraphpadCEFile = new GPGraphpadFile();
        
        graph = createGraph(jGraphpadCEFile);
        
        createComponents();
        
        graph.setMarqueeHandler((GPMarqueeHandler) GPPluginInvoker
                .instanciateDocAwarePluginForKey("MarqueeHandler.class",
                        this));
        
        registerListeners(graph);
    }
    
    public void createComponents() {
    	docComponent.setBorder(BorderFactory.createEtchedBorder());
    	docComponent.setLayout(new BorderLayout());

        JPanel toolBarMainPanel = new JPanel(new BorderLayout());
        GPBarFactory.getInstance().createToolBars(
                toolBarMainPanel, this, GPBarFactory.DOCTOOLBARS);

        docComponent.add(BorderLayout.NORTH, toolBarMainPanel);
        docComponent.add(BorderLayout.CENTER, createScrollPane());
        update();
    }
    
    /*
     * Create a JGraph with JGraph primitives or custom subclassers registered in the properties file
     */
    protected JGraph createGraph(GPGraphpadFile file) {
        graph = (JGraph) GPPluginInvoker
                .instanciateObjectForKey("JGraph.class");
        graph.setUI((GraphUI) GPPluginInvoker
                .instanciateDocAwarePluginForKey("GraphUI.class", this));
        graph.setDragEnabled(false);
        graph.setJumpToDefaultPort(true);
        graph.setInvokesStopCellEditing(true);
        graph.setCloneable(true);
        graph.setGraphLayoutCache(jGraphpadCEFile.getGraphLayoutCache());//we transfer the data to the JGraph
        return graph;
    }

    protected Component createScrollPane() {
        scrollPane = new JScrollPane(graph);
        JViewport port = scrollPane.getViewport();
        try {
            String vpFlag = Translator.getString("ViewportBackingStore");
            Boolean bs = new Boolean(vpFlag);
            if (bs.booleanValue()) {
                port.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
            } else {
                port.setScrollMode(JViewport.BLIT_SCROLL_MODE);
            }
        } catch (MissingResourceException mre) {
            // just use the viewport default
        }
        columnRule = new Rule(Rule.HORIZONTAL, isMetric, graph);
        rowRule = new Rule(Rule.VERTICAL, isMetric, graph);
        if (showRuler) {
            scrollPane.setColumnHeaderView(columnRule);
            scrollPane.setRowHeaderView(rowRule);
        }
        return scrollPane;
    }

    protected GraphUndoManager createGraphUndoManager() {
        return new GraphUndoManager();
    }

    /**
     * Returns the model of the graph
     */
    public GraphModel getModel() {
        return graph.getModel();
    }

    /**
     * Returns the view from the current graph
     * 
     */
    public GraphLayoutCache getGraphLayoutCache() {
        return graph.getGraphLayoutCache();
    }

    /* Add this documents listeners to the specified graph. */
    private void registerListeners(JGraph graph) {
        graph.getModel().addUndoableEditListener(undoHandler);
        docComponent.addComponentListener(this);
        graph.getSelectionModel().addGraphSelectionListener(this);
        graph.getModel().addGraphModelListener(this);
        graph.getGraphLayoutCache().addGraphLayoutCacheListener(this);
        graph.addPropertyChangeListener(this);
        graph.getGraphLayoutCache().addGraphLayoutCacheListener(this);
    }

    public void setModified(boolean modified) {
        this.modified = modified;
        updateFrameTitle();
        graphpad.update();
    }

    /* Return the scale of this document as a string. */
    protected String getDocumentScale() {
        return Integer.toString((int) (graph.getScale() * 100)) + "%";
    }

    /* Sets the attributes of the selected cells. */
    public void setSelectionAttributes(Map map) {
        map = new Hashtable(map);
        map.remove(GraphConstants.BOUNDS);
        map.remove(GraphConstants.POINTS);
        graph.getGraphLayoutCache().edit(graph.getSelectionCells(), map);
    }

    /* Sets the attributes of the selected cells. */
    public void setFontSizeForSelection(float size) {
        Object[] cells = DefaultGraphModel.getDescendants(graph.getModel(),
                graph.getSelectionCells()).toArray();
        // Filter ports out
        java.util.List list = new ArrayList();
        for (int i = 0; i < cells.length; i++)
            if (!(cells[i] instanceof Port))
                list.add(cells[i]);
        cells = list.toArray();

        Map nested = new Hashtable();
        for (int i = 0; i < cells.length; i++) {
            CellView view = graph.getGraphLayoutCache().getMapping(cells[i],
                    false);
            if (view != null) {
                Font font = GraphConstants.getFont(view.getAllAttributes());
                AttributeMap attr = new AttributeMap();
                GraphConstants.setFont(attr, font.deriveFont(size));
                nested.put(cells[i], attr);
            }
        }
        graph.getGraphLayoutCache().edit(nested, null, null, null);
    }

    /* Sets the attributes of the selected cells. */
    public void setFontStyleForSelection(int style) {
        Object[] cells = DefaultGraphModel.getDescendants(graph.getModel(),
                graph.getSelectionCells()).toArray();
        // Filter ports out
        java.util.List list = new ArrayList();
        for (int i = 0; i < cells.length; i++)
            if (!(cells[i] instanceof Port))
                list.add(cells[i]);
        cells = list.toArray();

        Map nested = new Hashtable();
        for (int i = 0; i < cells.length; i++) {
            CellView view = graph.getGraphLayoutCache().getMapping(cells[i],
                    false);
            if (view != null) {
                Font font = GraphConstants.getFont(view.getAllAttributes());
                AttributeMap attr = new AttributeMap();
                GraphConstants.setFont(attr, font.deriveFont(style));
                nested.put(cells[i], attr);
            }
        }
        graph.getGraphLayoutCache().edit(nested, null, null, null);
    }

    /* Sets the attributes of the selected cells. */
    public void setFontNameForSelection(String name) {
        Object[] cells = DefaultGraphModel.getDescendants(graph.getModel(),
                graph.getSelectionCells()).toArray();
        // Filter ports out
        java.util.List list = new ArrayList();
        for (int i = 0; i < cells.length; i++)
            if (!(cells[i] instanceof Port))
                list.add(cells[i]);
        cells = list.toArray();

        Map nested = new Hashtable();
        for (int i = 0; i < cells.length; i++) {
            CellView view = graph.getGraphLayoutCache().getMapping(cells[i],
                    false);
            if (view != null) {
                Font font = GraphConstants.getFont(view.getAllAttributes());
                AttributeMap attr = new AttributeMap();
                GraphConstants.setFont(attr, new Font(name, font.getStyle(),
                        font.getSize()));
                nested.put(cells[i], attr);
            }
        }
        graph.getGraphLayoutCache().edit(nested, null, null, null);
    }

    // -----------------------------------------------------------------
    // Component Listener
    // -----------------------------------------------------------------
    public void setResizeAction(AbstractAction e) {
        fitAction = e;
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        if (fitAction != null)
            fitAction.actionPerformed(null);
    }

    public void componentShown(ComponentEvent e) {
        componentResized(e);
    }

    public void setScale(double scale) {
        scale = Math.max(Math.min(scale, 16), .01);
        graph.setScale(scale);
        componentResized(null);
    }

    // ----------------------------------------------------------------------
    // Printable
    // ----------------------------------------------------------------------

    /**
     * not from Printable interface, but related
     */
    public void updatePageFormat() {
        PageFormat f = getPageFormat();
        columnRule.setActiveOffset((int) (f.getImageableX()));
        rowRule.setActiveOffset((int) (f.getImageableY()));
        columnRule.setActiveLength((int) (f.getImageableWidth()));
        rowRule.setActiveLength((int) (f.getImageableHeight()));
        if (isPageVisible()) {
            int w = (int) (f.getWidth());
            int h = (int) (f.getHeight());
            graph.setMinimumSize(new Dimension(w + 5, h + 5));
        } else
            graph.setMinimumSize(null);
        docComponent.invalidate();
        // Execute fitAction...
        componentResized(null);
        graph.repaint();
    }

    public int print(Graphics g, PageFormat pF, int page) {
        int pw = (int) pF.getImageableWidth();
        int ph = (int) pF.getImageableHeight();
        int cols = (graph.getWidth() / pw) + 1;
        int rows = (graph.getHeight() / ph) + 1;
        int pageCount = cols * rows;
        if (page >= pageCount)
            return NO_SUCH_PAGE;
        int col = page % cols;
        int row = page % rows;
        g.translate(-col * pw, -row * ph);
        g.setClip(col * pw, row * ph, pw, ph);
        graph.paint(g);
        g.translate(col * pw, row * ph);
        return PAGE_EXISTS;
    }

    //
    // Listeners
    //

    // PropertyChangeListener
    public void propertyChange(PropertyChangeEvent evt) {
        if (graphpad != null)
            update();
    }

    // GraphSelectionListener
    public void valueChanged(GraphSelectionEvent e) {
        update();
    }

    // View Observer
    public void graphLayoutCacheChanged(GraphLayoutCacheEvent e) {
        modified = true;
        update();
    }

    // GraphModelListener
    public void graphChanged(GraphModelEvent e) {
        modified = true;
        update();
        // System.out.println("Change:\n"+buttonEdge.getChange().getStoredAttributeMap());
    }

    protected void update() {
        updateFrameTitle();
        graphpad.update();
        graphpad.getStatusBar().setMessage(getDocumentStatus());
        graphpad.getStatusBar().setScale(this.getDocumentScale());
    }
    
    /* Return the status of this document as a string. */
    protected String getDocumentStatus() {
        String s = null;
        int n = graph.getSelectionCount();
        if (n > 0)
            s = n + " " + Translator.getString("Selected");
        else {
            int c = graph.getModel().getRootCount();
            if (c == 0) {
                s = Translator.getString("Empty");
            } else {
                s = c + " ";
                if (c > 1)
                    s += Translator.getString("Cells");
                else
                    s += Translator.getString("Cell");
                c = graph.getSelectionCount();
                s = s + " / " + c + " ";
                if (c > 1)
                    s += Translator.getString("Components");
                else
                    s += Translator.getString("Component");
            }
        }
        return s;
    }

    /**
     * Returns the graphUndoManager.
     * 
     * @return GraphUndoManager
     */
    public GraphUndoManager getGraphUndoManager() {
        return graphUndoManager;
    }

    /**
     * Sets the graphUndoManager.
     * 
     * @param graphUndoManager
     *            The graphUndoManager to set
     */
    public void setGraphUndoManager(GraphUndoManager graphUndoManager) {
        this.graphUndoManager = graphUndoManager;
    }

    /**
     * Resets the Graph undo manager
     */
    public void resetGraphUndoManager() {
        graphUndoManager.discardAllEdits();
    }

    /**
     * Returns the graphpad.
     * 
     * @return GPGraphpad
     */
    public GPGraphpad getGraphpad() {
        return graphpad;
    }

    /**
     * Sets the graphpad.
     * 
     * @param graphpad
     *            The graphpad to set
     */
    public void setGraphpad(GPGraphpad graphpad) {
        this.graphpad = graphpad;
    }

    /**
     * Returns true if the user really wants to close. Gives chance to save
     * work.
     */
    public boolean close(boolean showConfirmDialog) {
        // set default to save on close
        int r = JOptionPane.YES_OPTION;

        if (modified) {
            if (showConfirmDialog)
                r = JOptionPane.showConfirmDialog(graphpad.getFrame(),
                        Translator.getString("SaveChangesDialog"), Translator
                                .getString("Title"),
                        JOptionPane.YES_NO_CANCEL_OPTION);

            // if yes, then save and close
            if (r == JOptionPane.YES_OPTION) {
            	graphpad.getCommand("FileSave").actionPerformed(null);
                return true;
            }
            // if no, then don't save and just close
            else if (r == JOptionPane.NO_OPTION) {
                return true;
            }
            // all other conditions (cancel and dialog's 'X' button)
            // don't save and don't close
            else
                return false;

        }
        return true;
    }

    /**
     * Returns the findPattern.
     * 
     * @return String
     */
    public String getFindPattern() {
        return findPattern;
    }

    /**
     * Sets the findPattern.
     * 
     * @param findPattern
     *            The findPattern to set
     */
    public void setFindPattern(String findPattern) {
        this.findPattern = findPattern;
    }

    /**
     * Returns the lastFound.
     * 
     * @return Object
     */
    public Object getLastFound() {
        return lastFound;
    }

    /**
     * Sets the lastFound.
     * 
     * @param lastFound
     *            The lastFound to set
     */
    public void setLastFound(Object lastFound) {
        this.lastFound = lastFound;
    }

    /**
     * Returns the scrollPane.
     * 
     * @return JScrollPane
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    /**
     * Sets the scrollPane.
     * 
     * @param scrollPane
     *            The scrollPane to set
     */
    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    /**
     * Returns the columnRule.
     * 
     * @return Rule
     */
    public Rule getColumnRule() {
        return columnRule;
    }

    /**
     * Returns the rowRule.
     * 
     * @return Rule
     */
    public Rule getRowRule() {
        return rowRule;
    }

    /**
     * Sets the columnRule.
     * 
     * @param columnRule
     *            The columnRule to set
     */
    public void setColumnRule(Rule columnRule) {
        this.columnRule = columnRule;
    }

    /**
     * Sets the rowRule.
     * 
     * @param rowRule
     *            The rowRule to set
     */
    public void setRowRule(Rule rowRule) {
        this.rowRule = rowRule;
    }

    /**
     * Returns the enableTooltips.
     * 
     * @return boolean
     */
    public boolean isEnableTooltips() {
        return enableTooltips;
    }

    /**
     * Sets the enableTooltips.
     * 
     * @param enableTooltips
     *            The enableTooltips to set
     */
    public void setEnableTooltips(boolean enableTooltips) {
        this.enableTooltips = enableTooltips;

        if (this.enableTooltips)
            ToolTipManager.sharedInstance().registerComponent(graph);
        else
            ToolTipManager.sharedInstance().unregisterComponent(graph);
    }

    /**
     * Returns the internalFrame.
     * 
     * @return GPDocFrame
     */
    public GPDocFrame getInternalFrame() {
        return internalFrame;
    }

    /**
     * Sets the internalFrame.
     * 
     * @param internalFrame
     *            The internalFrame to set
     */
    protected void setInternalFrame(GPDocFrame internalFrame) {
        this.internalFrame = internalFrame;
    }

    protected void updateFrameTitle() {
        if (this.internalFrame != null) {
            this.internalFrame.setTitle(getFrameTitle());
        }

    }

    public String getFrameTitle() {
        if (docComponent.getName() == null)
        	docComponent.setName(Translator.getString("NewGraph") + graphpad.getAllFrames().length);
        return docComponent.getName() + (modified ? "*" : "");
    }

    public GPMarqueeHandler getMarqueeHandler() {
        return (GPMarqueeHandler) graph.getMarqueeHandler();
    }

    public void setMarqueeHandler(GPMarqueeHandler marqueeHandler) {
        graph.setMarqueeHandler(marqueeHandler);
    }

    public GPGraphpadFile getJGraphpadCEFile() {
        return jGraphpadCEFile;
    }

    public void setJGraphpadCEFile(GPGraphpadFile jGraphpadCEFile) {
        this.jGraphpadCEFile = jGraphpadCEFile;
        graph.setGraphLayoutCache(jGraphpadCEFile.getGraphLayoutCache());
    }

    public ArrayList getEdgeCreators() {
        return edgeCreators;
    }

    public void setEdgeCreators(ArrayList edgeCreators) {
        this.edgeCreators = edgeCreators;
    }

    public ArrayList getVertexnPortsCreators() {
        return vertexnPortsCreators;
    }

    public void setVertexnPortsCreators(ArrayList vertexnPortsCreators) {
        this.vertexnPortsCreators = vertexnPortsCreators;
    }

    public Action getCommand(String key) {
        return GPPluginInvoker.getCommand(key, docComponent.getActionMap(), this);
    }
    
    public void initCommand(Action action) {
        graphpad.initCommand(action);
        if (action instanceof IVertexFactory) {
            vertexnPortsCreators.add(action);
        } else if (action instanceof IEdgeFactory) {
            edgeCreators.add(action);
        }
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Image backGroundImage) {
        this.backgroundImage = backGroundImage;
    }

	public boolean isPageVisible() {
		return pagevisible;
	}

	public void setPageVisible(boolean pagevisible) {
		this.pagevisible = pagevisible;
	}

	public PageFormat getPageFormat() {
		return pageFormat;
	}

	public void setPageFormat(PageFormat pageFormat) {
		this.pageFormat = pageFormat;
	}

	public Action getFitAction() {
		return fitAction;
	}

	public Map getPluginsMap() {
		return pluginsMap;
	}

	public void setPluginsMap(Map pluginsMap) {
		this.pluginsMap = pluginsMap;
	}

	public JGraph getGraph() {
		return graph;
	}

	public void setGraph(JGraph graph) {
		this.graph = graph;
	}

	public JComponent getDocComponent() {
		return docComponent;
	}

	public void setDocComponent(JComponent docComponent) {
		this.docComponent = docComponent;
	}
}
