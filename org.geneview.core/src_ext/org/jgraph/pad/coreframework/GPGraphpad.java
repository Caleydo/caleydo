/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * JGraphpad is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
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
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jgraph.JGraph;
import org.jgraph.pad.resources.Translator;
import org.jgraph.pad.util.ICommandRegistery;
import org.jgraph.pad.util.StatusBar;

/**
 * This is the UI delegate of the JGraphpad multi JGraph document interface. To
 * gain the control of this class, you could subclass this class but since this
 * class delegates to its model its main behaviours, you would better set
 * (register in the XML configuration file) your custom implementations.
 * 
 * 
 * @author Gaudenz Alder
 * @author Sven Luzar
 * @author Raphael Valyi (major refactoring since Jan 2006, contributed as LGPL)
 * @version 1.3.2 Actions moved to an own package
 * @version 1.0 1/1/02
 */
public class GPGraphpad extends JComponent implements ICommandRegistery {
	
	/**
	 * parameters that can change from one sessions to another
	 */
	protected GPSessionParameters sessionParameters;

	/**
	 * Boolean for the visible state of the toolbars
	 */
	protected boolean toolBarsVisible = true;

	/**
	 * Log console for the System in and out messages
	 */
	protected static Component logger;

	/**
	 * Desktoppane for the internal frames
	 */
	protected JDesktopPane desktop = new JDesktopPane();

	/**
	 * Contains the mapping between GPDocument objects and GPInternalFrames.
	 */
	protected Hashtable doc2InternalFrame = new Hashtable();

	/** 
	 * The toolbar for this graphpad
	 */
	protected JPanel toolBarMainPanel = new JPanel(new BorderLayout());

	/**
	 * The toolbar for this graphpad
	 */
	protected JPanel toolBarInnerPanel;

	/**
	 * The menubar for this graphpad
	 */
	protected JMenuBar menubar;

	/**
	 * The statusbar for this Graphpad instance
	 */
	protected StatusBar statusbar;

	/**
	 * The main Panel with the status bar and the desktop pane
	 */
	protected JPanel mainPanel = new JPanel(new BorderLayout());

	/**
	 * A configuration specific to the Graphpad instance. Remark: we would
	 * hardly need it, the static configuration should be sufficient.
	 */

	public GPGraphpad(GPSessionParameters sessionParameters) {
        setDoubleBuffered(true);
		this.sessionParameters = sessionParameters;
	}
		
		
	public void init() {        
        setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BorderLayout());

		// create the statusbar
		createStatusBar();

		// build the menu and the toolbar
		toolBarInnerPanel = GPBarFactory.getInstance().createToolBars(
				toolBarMainPanel, this, GPBarFactory.PADTOOLBARS);
		setToolBarsVisible(true);

		menubar = GPBarFactory.getInstance().createMenubar(this);

		add(BorderLayout.NORTH, menubar);
		add(BorderLayout.CENTER, mainPanel);
		add(BorderLayout.SOUTH, statusbar);
        
        GPPluginInvoker.decorateGraphpad(this);//room for plugins

		// Don't show internals of pane being dragged
		// for performance
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		update();
	}

	// others

	public Action getCommand(String key) {
		return getCommand(key, getActionMap());
	}
	
	public final Action getCommand(final String key, ActionMap map) {
		return GPPluginInvoker.getCommand(key, map, this);
	}
    
    public final void initCommand(Action action) {
        if (action instanceof GPAbstractActionDefault)
            ((GPAbstractActionDefault) action).setGraphpad(this);
    }

	/**
	 * return a shutdown routine.
	 */
	public WindowAdapter getAppCloser() {
		return new AppCloser();
	}

	/**
	 * To shutdown when run as an application. This is a fairly lame
	 * implementation. A more self-respecting implementation would at least
	 * check to see if a save was needed.
	 */
	protected final class AppCloser extends WindowAdapter {

		public void windowClosing(WindowEvent e) {
			getCommand("FileExit").actionPerformed(null);
		}
	}

	/**
	 * Find the hosting frame, for the file-chooser dialog.
	 */
	public Frame getFrame() {
		for (Container p = getParent(); p != null; p = p.getParent()) {
			if (p instanceof Frame) {
				return (Frame) p;
			}
		}
		return null;
	}

	public JMenuBar getMenubar() {
		return menubar;
	}

	/**
	 * Create a status bar
	 */
	protected StatusBar createStatusBar() {
		statusbar = new StatusBar();
		return statusbar;
	}

	public StatusBar getStatusBar() {
		return statusbar;
	}

	/**
	 * Show a dialog with the given error message.
	 */
	public void error(String message) {
		JOptionPane.showMessageDialog(this, message, Translator.getString("Title"),
				JOptionPane.ERROR_MESSAGE);
	}

	// --- actions -----------------------------------

	public JGraph getCurrentGraph() {
		GPDocument doc = getCurrentDocument();
		if (doc == null)
			return null;
		return doc.getGraph();
	}

	public JInternalFrame getCurrentInternalFrame() {
		GPDocFrame internalFrame = (GPDocFrame) desktop
				.getSelectedFrame();
		if (internalFrame == null) {
			JInternalFrame[] frames = desktop.getAllFrames();
			if (frames.length > 0) {
				try {
					frames[0].setSelected(true);
					internalFrame = (GPDocFrame) frames[0];
				} catch (PropertyVetoException e) {
					return null;
				}
			}
		}
		if (internalFrame == null)
			return null;
		return internalFrame;
	}

	public GPDocument getCurrentDocument() {
		GPDocFrame internalFrame = (GPDocFrame) desktop
				.getSelectedFrame();
		if (internalFrame == null) {
			JInternalFrame[] frames = desktop.getAllFrames();
			if (frames.length > 0) {
				try {
					frames[0].setSelected(true);
					internalFrame = (GPDocFrame) frames[0];
				} catch (PropertyVetoException e) {
					return null;
				}
			}
		}
		if (internalFrame == null)
			return null;
		return internalFrame.getDocument();
	}

	public GPDocument[] getAllDocuments() {
		JInternalFrame[] frames = desktop.getAllFrames();

		if (frames != null && frames.length > 0) {
			ArrayList docs = new ArrayList();
			for (int i = 0; i < frames.length; i++) {
				// make sure to only pick up GPDocFrame instances
				if (frames[i] instanceof GPDocFrame) {
					docs.add(((GPDocFrame) frames[i]).getDocument());
				}
			}
			return (GPDocument[]) docs.toArray(new GPDocument[docs.size()]);
		}
		return null;
	}

	/**
	 * Returns the undoAction.
	 * 
	 * @return UndoAction
	 */
	public GPAbstractActionDefault getEditUndoAction() {
		return (GPAbstractActionDefault) this.getCommand("EditUndo");
	}

	/**
	 * Returns the redoAction.
	 * 
	 * @return RedoAction
	 */
	public GPAbstractActionDefault getEditRedoAction() {
		return (GPAbstractActionDefault) this.getCommand("EditRedo");
	}

	public Component getLogConsole() {
		return logger;
	}

	public void setLogConsole(Component console) {
		logger = console;
	}

	public boolean isToolBarsVisible() {
		return this.toolBarsVisible;
	}

	public void setToolBarsVisible(boolean state) {
		this.toolBarsVisible = state;

		if (state == true) {
			mainPanel.remove(desktop);
			toolBarInnerPanel.add(BorderLayout.CENTER, desktop);
			mainPanel.add(BorderLayout.CENTER, toolBarMainPanel);
		} else {
			mainPanel.remove(toolBarMainPanel);
			toolBarInnerPanel.remove(desktop);
			mainPanel.add(BorderLayout.CENTER, desktop);
		}
		desktop.repaint();
	}

	public void addGPInternalFrame(GPDocFrame f) {
		desktop.add(f);
		try {
			f.setMaximum(true);
			f.setSelected(true);
		} catch (Exception ex) {
		}
		doc2InternalFrame.put(f.getDocument(), f);
	}

	/**
	 * removes the specified Internal Frame from the Graphpad
	 */
	public void removeGPInternalFrame(GPDocFrame f) {
		if (f == null)
			return;
		f.setVisible(false);
		desktop.remove(f);
		doc2InternalFrame.remove(f.getDocument());
		f.cleanUp();
		JInternalFrame[] frames = desktop.getAllFrames();
		if (frames.length > 0) {
			try {
				frames[0].setSelected(true);
			} catch (PropertyVetoException e) {
			}
		}
	}

	public void exit() {
		if (!sessionParameters.isApplet()) {
			System.exit(0);
		} else {
			getFrame().dispose();
			String viewPath = sessionParameters.getParam(GPSessionParameters.VIEWPATH, false);
			if (viewPath != null) {
				try {
					URL codeBase = sessionParameters.getApplet().getCodeBase();
					URL url = new URL(codeBase, viewPath);
					sessionParameters.getApplet().getAppletContext().showDocument(url, "_self");//NOT SURE IT'S USEFUL
				} catch (MalformedURLException mue) {
					System.out.println(mue);
				}
			}
		}
	}

	/**
	 * ask every action to update itself
	 */
	public void update() {
		GPDocument currentDoc = getCurrentDocument();
		Object[] keys = getActionMap().keys();
		for (int i = 0; i < keys.length; i++) {
			Action a = getActionMap().get(keys[i]);

			if (a instanceof GPAbstractActionDefault) {
				((GPAbstractActionDefault) a).update();
			} else {
				if (currentDoc == null) {
					a.setEnabled(false);
				} else {
					a.setEnabled(true);
				}
			}
		}
	}

	public JInternalFrame[] getAllFrames() {
		return desktop.getAllFrames();
	}

	public void addDesktopContainerListener(ContainerListener listener) {
		desktop.addContainerListener(listener);
	}

	public void removeDesktopContainerListener(ContainerListener listener) {
		desktop.removeContainerListener(listener);
	}

	public Hashtable getDoc2InternalFrame() {
		return doc2InternalFrame;
	}

	public void setDoc2InternalFrame(Hashtable doc2InternalFrame) {
		this.doc2InternalFrame = doc2InternalFrame;
	}

	public void addDocument(String name, GPGraphpadFile jgraphpadCEFile) {	
		GPDocument doc = new GPDocument(this, name, jgraphpadCEFile);//concrete minimal document
		doc = GPPluginInvoker.decorateDocument(doc);//addition of the registered decorators
		GPDocFrame iframe = new GPDocFrame(doc);
		addGPInternalFrame(iframe);
		iframe.show();
		iframe.grabFocus();
	}

	public void removeDocument(GPDocument doc) {
		GPDocFrame iFrame = (GPDocFrame) getDoc2InternalFrame().get(doc);
		removeGPInternalFrame(iFrame);
	}

	public GPSessionParameters getSessionParameters() {
		return sessionParameters;
	}

	public void setSessionParameters(GPSessionParameters sessionParameters) {
		this.sessionParameters = sessionParameters;
	}
}
