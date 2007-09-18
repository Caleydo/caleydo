/*
 * @(#)LayoutDialog.java        1.0 12-JUL-2004
 *
 * Copyright (C) 2003 Gaudenz Alder
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jgraph.plugins.layouts;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.pad.resources.Translator;

/**
 * This dialog is shown when the layout function is requested.<br>
 *<br>
 * It offers a list with available layouts to choose from.<br>
 *<br>
 *<br>
 * @author <a href="mailto:Sven.Luzar@web.de">Sven Luzar</a>
 * @since 1.2.2
 * @version 1.0 init
 */
public class LayoutDialog extends javax.swing.JDialog {

        protected JGraph graph;
        
        // A cache for the layout settings
        protected Hashtable layoutSettings = new Hashtable();
        
        /**
         * Creates new form LayoutDialog
         */
        public LayoutDialog(Dialog parent, JGraph graph) {
                super(parent, true);
                this.graph = graph;
                init();
        }

        /**
         * Creates new form LayoutDialog
         */
        public LayoutDialog(Frame parent, JGraph graph) {
                super(parent);
                this.graph = graph;
                init();
        }

        /** initializes the dialog
         */
        protected void init() {
                // netbeans
                initComponents();

                // fill the list
                fillList();

                // select the first one
                try {
                        lstLayoutControllers.setSelectedIndex(0);
                } catch (Exception e) {
                }
        }

        /** Fills the List with the LayoutControllers
         */
        protected void fillList() {
                try {
                        DefaultListModel model = new DefaultListModel();
                        Iterator all = JGraphLayoutRegistry.getSharedJGraphLayoutRegistry().getLayouts().iterator();
                        //LayoutRegistry.registeredLayoutControllers();

                        while (all.hasNext()) {
                                JGraphLayoutAlgorithm controller = (JGraphLayoutAlgorithm) all.next();
                                //LayoutController controller = (LayoutController) all.next();
                                model.addElement(controller);
                        }
                        
                        
                        lstLayoutControllers.setModel(model);
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        /** 
         * Execute the algorithm
         */
        protected void execute() {
        	cmdFinished.setEnabled(false);
    		final JGraphLayoutAlgorithm controller = getSelectedLayoutController();
    		if (controller == null)
    			return;
    		final ProgressMonitor progressMonitor = new ProgressMonitor(this,
                    "Performing Layout...",
                    "", 0, controller.getMaximumProgress());
    		progressMonitor.setProgress(0);
    		progressMonitor.setMillisToDecideToPopup(1000);
    		// Decouple the updating of the progress meter and
    		// the running of the layout algorithm.
    		final Timer updater = new Timer(1000, new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				progressMonitor.setProgress(controller.getProgress());
   					controller.setAllowedToRun(!progressMonitor.isCanceled());
    	 		}
    		});
    		// Fork the layout algorithm and leave UI dispatcher thread
    		Thread t = new Thread("Layout Algorithm " + controller.toString()) {
    			public void run() {
    				try {
	    				Object[] cells = (isApplyLayoutToAll()) ?
	    						DefaultGraphModel.getAll(graph.getModel()) : graph.getSelectionCells();
	    				if (cells != null && cells.length > 0) {
	    		    		updater.start();
	    					JGraphLayoutAlgorithm.applyLayout(	graph,
	    														controller,
	    														cells,
	    														null );
	    				}
    				} finally {
    					progressMonitor.close();
    					updater.stop();
    					cmdFinished.setEnabled(true);
    				}
    	 		}
    		};
    		t.start();
        }

        public synchronized JDialog getLayoutSettingsDialog(JGraphLayoutAlgorithm layout) {
        	JDialog dlg = (JDialog) layoutSettings.get(layout);
        	if (dlg == null) {
        		final JGraphLayoutSettings settings = layout.createSettings();
        		if (settings != null) {
        			final JDialog dialog = JGraphLayoutAlgorithm.
						createDialog(settings, this, "Configure", "Close", "Apply");
        			dialog.pack();
        	        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        	        int x = (int)((screen.width  - this.getWidth() ) / 2.0);
        	        int y = (int)((screen.height - this.getHeight()) / 2.0);
        	        dialog.setLocation(x, y);
        			layoutSettings.put(layout, dialog);
            		dlg = dialog;
        		}
        	}
        	return dlg;
        }
        
        /** Will call if the user clicks on the gpConfiguration button.
         *  if the layout controller is configurable the method
         *  calls the configure method at the controller.
         */
        protected void configure() {
                try {
                        //LayoutController controller =
                		JGraphLayoutAlgorithm controller =
                                (JGraphLayoutAlgorithm) lstLayoutControllers.getSelectedValue();
            			JDialog dialog = getLayoutSettingsDialog(controller);
                		dialog.setVisible(true);
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        /** Returns true 
         *      if the user 
         *  wants to apply the Algorithm for 
         *  all nodes.
         */
        public boolean isApplyLayoutToAll() {
                return cmdAllNodes.isSelected();
        }

        /** Returns the selected 
         *  LayoutController of null if
         *  no LayoutController was selected
         *
         */
        public JGraphLayoutAlgorithm getSelectedLayoutController() {
                try {
                        return (JGraphLayoutAlgorithm) lstLayoutControllers.getSelectedValue();
                } catch (Exception e) {
                        return null;
                }
        }

        /** initializes the GUI
         *
         */
        protected void initComponents() {//GEN-BEGIN:initComponents
                cmdGrpApplyTo = new javax.swing.ButtonGroup();
                pnlMain = new javax.swing.JPanel();
                pnlApplyTo = new javax.swing.JPanel();
                lblApplyTo = new javax.swing.JLabel();
                cmdAllNodes = new javax.swing.JRadioButton();
                cmdSelectedNodes = new javax.swing.JRadioButton();
                pnlButtons = new javax.swing.JPanel();
                layoutHint = new JLabel("Hint:");
                cmdConfigure = new javax.swing.JButton();
                cmdCancel = new javax.swing.JButton();
                cmdFinished = new javax.swing.JButton();
                pnlLayoutControllers = new javax.swing.JPanel();
                scrollLayoutControllers = new javax.swing.JScrollPane();
                lstLayoutControllers = new javax.swing.JList();
                cmdConfigure.setEnabled(false);
                lstLayoutControllers.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent arg0) {
						JGraphLayoutAlgorithm layout = getSelectedLayoutController();
						if (layout != null) {
							String hint = layout.getHint();
							if (hint != null && hint.length() > 0)
								layoutHint.setText("Hint: "+hint);
							else
								layoutHint.setText("Hint:");
	            			JDialog dialog = getLayoutSettingsDialog(layout);
							cmdConfigure.setEnabled(dialog != null);
						} else {
							layoutHint.setText("Hint:");
							cmdConfigure.setEnabled(false);
						}
					}
                });

                setTitle(Translator.getString("Layout"));       //#Changed
                setName("Layout");      //#Frozen
                addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent evt) {
                               dispose();
                        }
                });

                pnlMain.setLayout(new java.awt.BorderLayout());

                lblApplyTo.setText(Translator.getString("Apply to"));   //#Changed
                lblApplyTo.setName("ApplyTo");  //#Frozen
                pnlApplyTo.add(lblApplyTo);

                cmdAllNodes.setFont(new java.awt.Font("Dialog", 0, 12));
                cmdAllNodes.setText(Translator.getString("AllNodes"));  //#Changed
                cmdAllNodes.setName("AllNodes");        //#Frozen
                cmdGrpApplyTo.add(cmdAllNodes);
                pnlApplyTo.add(cmdAllNodes);

                cmdSelectedNodes.setFont(new java.awt.Font("Dialog", 0, 12));
                cmdSelectedNodes.setSelected(true);
                cmdSelectedNodes.setText(Translator.getString("SelectedNodes"));        //#Changed
                cmdSelectedNodes.setName("SelectedNodes");      //#Frozen
                cmdGrpApplyTo.add(cmdSelectedNodes);
                pnlApplyTo.add(cmdSelectedNodes);

                pnlMain.add(pnlApplyTo, java.awt.BorderLayout.NORTH);

                cmdConfigure.setText(Translator.getString("Configure"));        //#Changed
                cmdConfigure.setName("Configure");      //#Frozen
                cmdConfigure.setFocusPainted(false);
                cmdConfigure.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                configure();
                        }
                });

                pnlButtons.add(cmdConfigure);

                cmdFinished.setText("Execute");        //#Changed
                cmdFinished.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                        	execute();
                        }
                });
                pnlButtons.add(cmdFinished);
                pnlButtons.add(cmdCancel);

                getRootPane().setDefaultButton(cmdFinished);

                cmdCancel.setText(Translator.getString("Component.FileClose.Text"));      //#Changed
                cmdCancel.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                        	dispose();
                        }
                });

                JPanel panelHintAndButtons = new JPanel(new BorderLayout());
                panelHintAndButtons.add(layoutHint, java.awt.BorderLayout.NORTH);
                panelHintAndButtons.add(pnlMain, java.awt.BorderLayout.CENTER);
                
                pnlMain.add(pnlButtons, java.awt.BorderLayout.SOUTH);

                getContentPane().add(panelHintAndButtons, java.awt.BorderLayout.SOUTH);

                pnlLayoutControllers.setLayout(new java.awt.BorderLayout());

                scrollLayoutControllers.setViewportView(lstLayoutControllers);

                pnlLayoutControllers.add(scrollLayoutControllers, java.awt.BorderLayout.CENTER);

                getContentPane().add(pnlLayoutControllers, java.awt.BorderLayout.CENTER);

                pack();
                java.awt.Dimension screenSize =
                        java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                //setSize(new java.awt.Dimension(302, 312));
                pack();
                setLocation(
                        (screenSize.width - this.getWidth()) / 2,
                        (screenSize.height - this.getHeight()) / 2);
        }//GEN-END:initComponents

        // Variables declaration - do not modify//GEN-BEGIN:variables
        /** GUI object */
        private javax.swing.JPanel pnlApplyTo;
        /** GUI object */
        private javax.swing.JButton cmdConfigure;
        /** GUI object */
        private javax.swing.JPanel pnlLayoutControllers;
        /** GUI object */
        private javax.swing.JScrollPane scrollLayoutControllers;
        /** GUI object */
        private javax.swing.JRadioButton cmdSelectedNodes;
        /** GUI object */
        private javax.swing.ButtonGroup cmdGrpApplyTo;
        /** GUI object */
        private javax.swing.JRadioButton cmdAllNodes;
        /** GUI object */
        private javax.swing.JList lstLayoutControllers;
        /** GUI object */
        private javax.swing.JPanel pnlButtons;
        /** GUI object */
        private javax.swing.JButton cmdCancel;
        /** GUI object */
        private javax.swing.JPanel pnlMain;
        /** GUI object */
        private javax.swing.JLabel lblApplyTo, layoutHint;
        /** GUI object */
        private javax.swing.JButton cmdFinished;
        // End of variables declaration//GEN-END:variables

}
