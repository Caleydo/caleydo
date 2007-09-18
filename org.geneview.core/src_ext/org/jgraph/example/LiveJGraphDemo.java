package org.jgraph.example;

import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;
import org.jgraph.graph.GraphConstants;
import org.jgraph.JGraph;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import javax.swing.event.InternalFrameEvent;
import java.beans.PropertyVetoException;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;

public class LiveJGraphDemo extends JFrame {
	private JDesktopPane _desktopPane = null;

	private FrameSelectionListener _fsl = null;

	private FrameComponentListener _fcl = null;

	private AddParentInternalFrameAction _apifa = null;

	private AddChildInternalFrameAction _acifa = null;

	private DefaultGraphModel _graph = null;

	private JPanel _canvas = null;

	private ComponentListener _cl = null;

	public LiveJGraphDemo() {
		super("Live JGraph Demo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_fsl = new FrameSelectionListener();
		_fcl = new FrameComponentListener();
		_cl = new CompListener();
		_graph = new DefaultGraphModel();
		AddRootInternalFrameAction arifa = new AddRootInternalFrameAction();
		_apifa = new AddParentInternalFrameAction();
		_apifa.setEnabled(false);
		_acifa = new AddChildInternalFrameAction();
		_acifa.setEnabled(false);
		JPanel mainPanel = new JPanel(new BorderLayout());
		_desktopPane = new JDesktopPane();
		_desktopPane.addComponentListener(_cl);
		_canvas = new JPanel(new BorderLayout());
		JGraph graphComp = new JGraph(_graph);
		_canvas.add(graphComp, BorderLayout.CENTER);
		_desktopPane.add(_canvas, JLayeredPane.FRAME_CONTENT_LAYER);
		mainPanel.add(_desktopPane, BorderLayout.CENTER);
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(arifa);
		toolBar.add(_apifa);
		toolBar.add(_acifa);
		mainPanel.add(toolBar, BorderLayout.NORTH);
		getContentPane().add(mainPanel);
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem menuItem = new JMenuItem("Exit");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu.add(menuItem);
		menuBar.add(menu);
		menu = new JMenu("Components");
		menu.add(arifa);
		menu.add(_apifa);
		menu.add(_acifa);
		menuBar.add(menu);
		setJMenuBar(menuBar);
		pack();
		setSize(1000, 1000);
		setVisible(true);
	}

	public static void main(String[] args) {
		new LiveJGraphDemo();
	}

	private class AddRootInternalFrameAction extends AbstractAction {
		public AddRootInternalFrameAction() {
			super("Add Root Component", new ImageIcon("org/jgraph/example/resources/plus.gif"));
			putValue(Action.SHORT_DESCRIPTION, "Add Root Component");
		}

		public void actionPerformed(ActionEvent ae) {
			//Create initial internal frame
			LiveJGraphInternalFrame internalFrame = new LiveJGraphInternalFrame();
			internalFrame.create();
			//Add to graph model
			DefaultGraphCell insertCell = new DefaultGraphCell(internalFrame);
			internalFrame.setGraphCell(insertCell);
			Object insertCells[] = new Object[] { insertCell };
			_graph.insert(insertCells, null, null, null, null);
			internalFrame.addInternalFrameListener(_fsl);
			internalFrame.addComponentListener(_fcl);
			_desktopPane.add(internalFrame);
			try {
				internalFrame.setSelected(true);
			} catch (PropertyVetoException pve) {
			}
		}
	}

	private class AddChildInternalFrameAction extends AbstractAction {
		public AddChildInternalFrameAction() {
			super("Add Child Component", new ImageIcon("org/jgraph/example/resources/add_down.gif"));
			putValue(Action.SHORT_DESCRIPTION, "Add Child Component");
		}

		public void actionPerformed(ActionEvent ae) {
			//Get currently selected internal frame
			LiveJGraphInternalFrame currentSelectedFrame = (LiveJGraphInternalFrame) _desktopPane
					.getSelectedFrame();
			//Create initial internal frame
			LiveJGraphInternalFrame internalFrame = new LiveJGraphInternalFrame();
			internalFrame.create();
			//Add to graph model
			DefaultGraphCell insertCell = new DefaultGraphCell(internalFrame);
			internalFrame.setGraphCell(insertCell);
			Object insertCells[] = new Object[] { insertCell };
			_graph.insert(insertCells, null, null, null, null);
			DefaultGraphCell parentCell = currentSelectedFrame.getGraphCell();
			DefaultPort parentPort = new DefaultPort();
			parentCell.add(parentPort);
			DefaultPort childPort = new DefaultPort();
			insertCell.add(childPort);
			DefaultEdge edge = new DefaultEdge();
			HashMap map = new HashMap();
			Map atts = new Hashtable();
			GraphConstants.setLineEnd(atts, GraphConstants.ARROW_CLASSIC);
			GraphConstants.setEndFill(atts, true);
			map.put(edge, atts);
			ConnectionSet cs = new ConnectionSet(edge, parentPort, childPort);
			Object insertEdges[] = new Object[] { edge };
			_graph.insert(insertEdges, map, cs, null, null);
			internalFrame.addInternalFrameListener(_fsl);
			internalFrame.addComponentListener(_fcl);
			_desktopPane.add(internalFrame);
			try {
				internalFrame.setSelected(true);
			} catch (PropertyVetoException pve) {
			}
		}
	}

	private class AddParentInternalFrameAction extends AbstractAction {
		public AddParentInternalFrameAction() {
			super("Add Parent Component", new ImageIcon("org/jgraph/example/resources/add_up.gif"));
			putValue(Action.SHORT_DESCRIPTION, "Add Parent Component");
		}

		public void actionPerformed(ActionEvent ae) {
			//Get currently selected internal frame
			LiveJGraphInternalFrame currentSelectedFrame = (LiveJGraphInternalFrame) _desktopPane
					.getSelectedFrame();
			//Create initial internal frame
			LiveJGraphInternalFrame internalFrame = new LiveJGraphInternalFrame();
			internalFrame.create();
			//Add to graph model
			DefaultGraphCell insertCell = new DefaultGraphCell(internalFrame);
			internalFrame.setGraphCell(insertCell);
			Object insertCells[] = new Object[] { insertCell };
			_graph.insert(insertCells, null, null, null, null);
			DefaultGraphCell childCell = currentSelectedFrame.getGraphCell();
			DefaultPort childPort = new DefaultPort();
			childCell.add(childPort);
			DefaultPort parentPort = new DefaultPort();
			insertCell.add(parentPort);
			DefaultEdge edge = new DefaultEdge();
			HashMap map = new HashMap();
			Map atts = new Hashtable();
			GraphConstants.setLineEnd(atts, GraphConstants.ARROW_CLASSIC);
			GraphConstants.setEndFill(atts, true);
			map.put(edge, atts);
			ConnectionSet cs = new ConnectionSet(edge, parentPort, childPort);
			Object insertEdges[] = new Object[] { edge };
			_graph.insert(insertEdges, map, cs, null, null);
			internalFrame.addInternalFrameListener(_fsl);
			internalFrame.addComponentListener(_fcl);
			_desktopPane.add(internalFrame);
			try {
				internalFrame.setSelected(true);
			} catch (PropertyVetoException pve) {
			}
		}
	}

	private class FrameSelectionListener extends InternalFrameAdapter {
		public void internalFrameActivated(InternalFrameEvent ife) {
			_apifa.setEnabled(true);
			_acifa.setEnabled(true);
		}

		public void internalFrameDeactivated(InternalFrameEvent ife) {
			_apifa.setEnabled(false);
			_acifa.setEnabled(false);
		}
	}

	private class CompListener extends ComponentAdapter {
		public void componentResized(ComponentEvent ce) {
			_canvas.setSize(_desktopPane.getSize());
			_canvas.updateUI();
		}
	}

	private class FrameComponentListener extends ComponentAdapter {
		public void componentResized(ComponentEvent ce) {
			HashMap map = new HashMap();
			Map atts = new Hashtable();
			LiveJGraphInternalFrame frame = (LiveJGraphInternalFrame) ce
					.getComponent();
			GraphConstants.setBounds(atts, frame.getBounds());
			map.put(frame.getGraphCell(), atts);
			_graph.edit(map, null, null, null);
		}

		public void componentMoved(ComponentEvent ce) {
			HashMap map = new HashMap();
			Map atts = new Hashtable();
			LiveJGraphInternalFrame frame = (LiveJGraphInternalFrame) ce
					.getComponent();
			GraphConstants.setBounds(atts, frame.getBounds());
			map.put(frame.getGraphCell(), atts);
			_graph.edit(map, null, null, null);
		}
	}
}