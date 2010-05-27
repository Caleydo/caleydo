/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.effects;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.canvas.SimpleCanvasImpl;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.Debug;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jme.util.stat.StatCollector;
import com.jme.util.stat.StatType;
import com.jme.util.stat.graph.GraphFactory;
import com.jme.util.stat.graph.LineGrapher;
import com.jme.util.stat.graph.TabledLabelGrapher;
import com.jmex.awt.lwjgl.LWJGLAWTCanvasConstructor;
import com.jmex.editors.swing.particles.ParticleAppearancePanel;
import com.jmex.editors.swing.particles.ParticleEmissionPanel;
import com.jmex.editors.swing.particles.ParticleFlowPanel;
import com.jmex.editors.swing.particles.ParticleInfluencePanel;
import com.jmex.editors.swing.particles.ParticleOriginPanel;
import com.jmex.editors.swing.particles.ParticleWorldPanel;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleSystem;
import com.jmex.effects.particles.SimpleParticleInfluenceFactory;
import com.jmex.effects.particles.SwarmInfluence;

/**
 * <code>RenParticleControlFrame</code>
 * 
 * @author Joshua Slack
 * @author Andrzej Kapolka - additions for multiple layers, save/load from jme
 *         format
 * @version $Id: RenParticleEditor.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */

public class RenParticleEditor extends JFrame {
    private static final Logger logger = Logger
            .getLogger(RenParticleEditor.class.getName());

    public static Node particleNode;
    public static ParticleSystem particleGeom;

    private static final long serialVersionUID = 1L;
    private static final String[] EXAMPLE_NAMES = { "Fire", "Fountain", "Lava",
            "Smoke", "Jet", "Snow", "Rain", "Explosion", "Ground Fog",
            "Fireflies" };
    int width = 640, height = 480;

    MyImplementor impl;
    private CameraHandler camhand;
    private Canvas glCanvas;
    private Node root;
    private Geometry grid;
    private Action spawnAction;

    // edit panels
    private ParticleAppearancePanel appearancePanel;
    private ParticleFlowPanel flowPanel;
    private ParticleOriginPanel originPanel;
    private ParticleEmissionPanel emissionPanel;
    private ParticleWorldPanel scenePanel;
    private ParticleInfluencePanel influencePanel;

    // layer panel components
    private LayerTableModel layerModel = new LayerTableModel();
    private JTable layerTable = new JTable(layerModel);
    private JButton deleteLayerButton;

    // examples panel components
    private JList exampleList;
    private JButton exampleButton;

    private JFileChooser fileChooser = new JFileChooser();
    private File openFile;

    private Preferences prefs = Preferences
            .userNodeForPackage(RenParticleEditor.class);

    private JCheckBoxMenuItem yUp;

    private JCheckBoxMenuItem zUp;

    /**
     * Main Entry point...
     * 
     * @param args
     *            String[]
     */
    public static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {

    		public void run() {
    			try {
    				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    			} catch (Exception e) {
    				logger.logp(Level.SEVERE, RenParticleEditor.class.toString(), "main(args)", "Exception", e);
    			}
    			new RenParticleEditor();
    		}});
    }

    public RenParticleEditor() {
        try {
            init();
            // center the frame
            setLocationRelativeTo(null);

            // show frame
            setVisible(true);

            // init some location dependent sub frames
            initFileChooser();

            while (glCanvas == null) {
            	try { Thread.sleep(100); } catch (InterruptedException e) {}
            }

        } catch (Exception ex) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "RenParticleEditor()", "Exception", ex);
        }
    }

    private void init() throws Exception {
        updateTitle();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFont(new Font("Arial", 0, 12));

        setJMenuBar(createMenuBar());

        appearancePanel = new ParticleAppearancePanel(prefs) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void requestParticleSystemOverwrite(
                    ParticleSystem newParticles) {
                particleNode.getChildren().set(
                        particleNode.getChildren().indexOf(particleGeom),
                        newParticles);
                particleGeom = newParticles;
                particleGeom.updateRenderState();
                updateFromManager();
            }
        };
        flowPanel = new ParticleFlowPanel();
        originPanel = new ParticleOriginPanel();
        emissionPanel = new ParticleEmissionPanel();
        scenePanel = new ParticleWorldPanel();
        influencePanel = new ParticleInfluencePanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add(new JScrollPane(appearancePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "Appearance");
        tabbedPane.add(new JScrollPane(originPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "Origin");
        tabbedPane.add(new JScrollPane(emissionPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "Emission");
        tabbedPane.add(new JScrollPane(flowPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "Flow");
        tabbedPane.add(new JScrollPane(scenePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "Scene");
        tabbedPane.add(new JScrollPane(influencePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "Influences");
        tabbedPane.add(new JScrollPane(createExamplesPanel(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), "Examples");
        tabbedPane.setPreferredSize(new Dimension(300, 150));

        JPanel canvasPanel = new JPanel();
        canvasPanel.setLayout(new BorderLayout());
        canvasPanel.add(getGlCanvas(), BorderLayout.CENTER);
        
        Dimension minimumSize = new Dimension(150, 150);
        tabbedPane.setMinimumSize(minimumSize);
        canvasPanel.setMinimumSize(minimumSize);

        JSplitPane sideSplit = new JSplitPane();
        sideSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
        sideSplit.setTopComponent(createLayerPanel());
        sideSplit.setBottomComponent(tabbedPane);
        sideSplit.setDividerLocation(150);

        JSplitPane mainSplit = new JSplitPane();
        mainSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setLeftComponent(sideSplit);
        mainSplit.setRightComponent(canvasPanel);
        mainSplit.setDividerLocation(300);
        getContentPane().add(mainSplit, BorderLayout.CENTER);

        grid = createGrid();
        
        yUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Callable<Void> exe = new Callable<Void>() {
                    public Void call() {
                        camhand.worldUpVector.set(Vector3f.UNIT_Y);
                        Camera cam = impl.getRenderer().getCamera();
                        cam.getLocation().set(0, 850, -850);
                        camhand.recenterCamera();
                        grid.unlock();
                        grid.getLocalRotation().fromAngleAxis(0, Vector3f.UNIT_X);
                        grid.lock();
                        prefs.putBoolean("yUp", true);
                        return null;
                    }
                };
                GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
                        .enqueue(exe);
            }
        });
        zUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Callable<Void> exe = new Callable<Void>() {
                    public Void call() {
                        camhand.worldUpVector.set(Vector3f.UNIT_Z);
                        Camera cam = impl.getRenderer().getCamera();
                        cam.getLocation().set(0, -850, 850);
                        camhand.recenterCamera();
                        grid.unlock();
                        grid.getLocalRotation().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
                        grid.lock();
                        prefs.putBoolean("yUp", false);
                        return null;
                    }
                };
                GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
                        .enqueue(exe);
            }
        });
        
        Callable<Void> exe = new Callable<Void>() {
            public Void call() {
                if (prefs.getBoolean("yUp", true)) {
                    yUp.doClick();
                } else {
                    zUp.doClick();
                }
                return null;
            }
        };
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
                .enqueue(exe);

        setSize(new Dimension(1024, 768));
    }

    private void updateTitle() {
        setTitle("Particle System Editor"
                + (openFile == null ? "" : (" - " + openFile)));
    }

    private JMenuBar createMenuBar() {
        Action newAction = new AbstractAction("New") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                createNewSystem();
            }
        };
        newAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);

        Action open = new AbstractAction("Open...") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                showOpenDialog();
            }
        };
        open.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);

        Action importAction = new AbstractAction("Merge Layers...") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                showMergeDialog();
            }
        };
        importAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);

        AbstractAction save = new AbstractAction("Save") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                saveAs(openFile);
            }
        };
        save.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);

        Action saveAs = new AbstractAction("Save As...") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                saveAs(null);
            }
        };
        saveAs.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);

        Action quit = new AbstractAction("Quit") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        quit.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Q);

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        file.add(newAction);
        file.add(open);
        file.add(importAction);
        file.add(save);
        file.add(saveAs);
        file.addSeparator();
        file.add(quit);

        spawnAction = new AbstractAction("Force Spawn") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                for (Spatial child : particleNode.getChildren()) {
                    if (child instanceof ParticleSystem) {
                        ((ParticleSystem) child).forceRespawn();
                    }
                }
            }
        };
        spawnAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F);
        spawnAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_F, 0));

        JMenu edit = new JMenu("Edit");
        edit.setMnemonic(KeyEvent.VK_E);
        edit.add(spawnAction);

        Action showGrid = new AbstractAction("Show Grid") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                grid
                        .setCullHint(grid.getCullHint() == Spatial.CullHint.Always ? Spatial.CullHint.Dynamic
                                : Spatial.CullHint.Always);
                prefs.putBoolean("showgrid", grid.getCullHint() != Spatial.CullHint.Always);
            }
        };
        showGrid.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);

        Action changeBackground = new AbstractAction(
                "Change Background Color...") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                showBackgroundDialog();
            }
        };
        changeBackground.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_B);

        Action recenter = new AbstractAction("Recenter Camera") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                camhand.recenterCamera();
            }
        };
        recenter.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);

        yUp = new JCheckBoxMenuItem("Y-Up Camera");
        yUp.setMnemonic(KeyEvent.VK_Y);
        zUp = new JCheckBoxMenuItem("Z-Up Camera");
        zUp.setMnemonic(KeyEvent.VK_Y);
        ButtonGroup upGroup = new ButtonGroup();
        upGroup.add(yUp);
        upGroup.add(zUp);

        JMenu view = new JMenu("View");
        view.setMnemonic(KeyEvent.VK_V);
        JCheckBoxMenuItem sgitem = new JCheckBoxMenuItem(showGrid);
        sgitem.setSelected(prefs.getBoolean("showgrid", true));
        view.add(sgitem);
        view.add(changeBackground);
        view.addSeparator();
        view.add(recenter);
        view.add(yUp);
        view.add(zUp);

        JMenuBar mbar = new JMenuBar();
        mbar.add(file);
        mbar.add(edit);
        mbar.add(view);
        return mbar;
    }

    private JPanel createLayerPanel() {
        JLabel layerLabel = createBoldLabel("Particle Layers:");

        layerTable.setColumnSelectionAllowed(false);
        layerTable.setRowSelectionAllowed(true);
        layerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int vwidth = layerTable.getTableHeader().getDefaultRenderer()
                .getTableCellRendererComponent(layerTable, "Visible", false,
                        false, -1, 1).getMinimumSize().width;
        TableColumn vcol = layerTable.getColumnModel().getColumn(1);
        vcol.setMinWidth(vwidth);
        vcol.setPreferredWidth(vwidth);
        vcol.setMaxWidth(vwidth);
        layerTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        if (layerTable.getSelectedRow() != -1) {
                            particleGeom = (ParticleSystem) particleNode
                                    .getChild(layerTable.getSelectedRow());
                            updateFromManager();
                        }
                    }
                });

        JButton newLayerButton = new JButton(new AbstractAction("New") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                int idx = particleNode.getQuantity();
                createNewLayer();
                layerModel.fireTableRowsInserted(idx, idx);
                layerTable.setRowSelectionInterval(idx, idx);
                deleteLayerButton.setEnabled(true);
            }
        });
        newLayerButton.setMargin(new Insets(2, 14, 2, 14));

        deleteLayerButton = new JButton(new AbstractAction("Delete") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                deleteLayer();
            }
        });
        deleteLayerButton.setMargin(new Insets(2, 14, 2, 14));
        deleteLayerButton.setEnabled(false);

        JPanel layerPanel = new JPanel(new GridBagLayout());
        layerPanel.add(layerLabel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
                        10, 10, 5, 10), 0, 0));
        layerPanel.add(new JScrollPane(layerTable), new GridBagConstraints(0,
                1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
        layerPanel.add(newLayerButton, new GridBagConstraints(0, 2, 1, 1, 0.5,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        layerPanel.add(deleteLayerButton, new GridBagConstraints(1, 2, 1, 1,
                0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        return layerPanel;
    }

    private JPanel createExamplesPanel() {
        JLabel examplesLabel = createBoldLabel("Prebuilt Examples:");

        exampleList = new JList(EXAMPLE_NAMES);
        exampleList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (exampleList.getSelectedValue() instanceof String)
                    exampleButton.setEnabled(true);
                else
                    exampleButton.setEnabled(false);
            }
        });

        exampleButton = new JButton(new AbstractAction("Apply") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                applyExample();
            }
        });
        exampleButton.setMargin(new Insets(2, 14, 2, 14));
        exampleButton.setEnabled(false);

        JPanel examplesPanel = new JPanel(new GridBagLayout());
        examplesPanel.add(examplesLabel, new GridBagConstraints(0, 0, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 10, 5, 10), 0, 0));
        examplesPanel.add(new JScrollPane(exampleList), new GridBagConstraints(
                0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
        examplesPanel.add(exampleButton, new GridBagConstraints(0, 2, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        return examplesPanel;
    }

    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        return label;
    }

    private void createNewSystem() {
        layerTable.clearSelection();
        particleNode.detachAllChildren();
        createNewLayer();
        layerModel.fireTableDataChanged();
        layerTable.setRowSelectionInterval(0, 0);
        deleteLayerButton.setEnabled(false);
        openFile = null;
        updateTitle();
    }

    private void showOpenDialog() {
        fileChooser.setSelectedFile(new File(""));
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fileChooser.getSelectedFile();
        prefs.put("particle_dir", file.getParent().toString());
        SimpleResourceLocator locator = new SimpleResourceLocator(file.getParentFile().toURI());
        ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, locator);
        try {
            Spatial obj = (Spatial) BinaryImporter.getInstance().load(file);
            if (obj instanceof Node && !(obj instanceof ParticleSystem)) {
                Node node = (Node) obj;
                for (int ii = node.getQuantity() - 1; ii >= 0; ii--) {
                    if (!(node.getChild(ii) instanceof ParticleSystem)) {
                        node.detachChildAt(ii);
                    }
                }
                if (node.getQuantity() == 0) {
                    throw new Exception("Node contains no particle meshes");
                }
                layerTable.clearSelection();
                root.detachChild(particleNode);
                particleNode = node;
                root.attachChild(particleNode);
                deleteLayerButton.setEnabled(true);

            } else { // obj instanceof ParticleSystem
                particleGeom = (ParticleSystem) obj;
                layerTable.clearSelection();
                particleNode.detachAllChildren();
                particleNode.attachChild(particleGeom);
                deleteLayerButton.setEnabled(false);
            }
            particleNode.updateRenderState();
            layerModel.fireTableDataChanged();
            layerTable.setRowSelectionInterval(0, 0);
            openFile = file;
            updateTitle();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Couldn't open '" + file
                    + "': " + e, "File Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.WARNING, "Couldn't open '" + file, e);
        } finally {
            ResourceLocatorTool.removeResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, locator);
        }
    }

    private void showMergeDialog() {
        fileChooser.setSelectedFile(new File(""));
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fileChooser.getSelectedFile();
        prefs.put("particle_dir", file.getParent());
        SimpleResourceLocator locator = new SimpleResourceLocator(file.getParentFile().toURI());
        ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, locator);
        try {
            Spatial obj = (Spatial) BinaryImporter.getInstance().load(file);
            int lidx = particleNode.getQuantity();
            if (obj instanceof Node) {
                Node node = (Node) obj;
                ArrayList<Spatial> meshes = new ArrayList<Spatial>();
                for (int ii = 0, nn = node.getQuantity(); ii < nn; ii++) {
                    if (node.getChild(ii) instanceof ParticleSystem) {
                        meshes.add(node.getChild(ii));
                    }
                }
                if (meshes.size() == 0) {
                    throw new Exception("Node contains no particle meshes");
                }
                layerTable.clearSelection();
                for (Spatial mesh : meshes) {
                    particleNode.attachChild(mesh);
                }

            } else { // obj instanceof ParticleSystem
                particleGeom = (ParticleSystem) obj;
                layerTable.clearSelection();
                particleNode.attachChild(particleGeom);
            }
            particleNode.updateRenderState();
            layerModel.fireTableRowsInserted(lidx,
                    particleNode.getQuantity() - 1);
            layerTable.setRowSelectionInterval(lidx, lidx);
            deleteLayerButton.setEnabled(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Couldn't open '" + file
                    + "': " + e, "File Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.WARNING, "Couldn't open '" + file, e);
        } finally {
            ResourceLocatorTool.removeResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, locator);
        }
    }

    private void saveAs(File file) {
        if (file == null) {
            fileChooser.setSelectedFile(openFile == null ? new File("")
                    : openFile);
            if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            file = fileChooser.getSelectedFile();
            prefs.put("particle_dir", file.getParent().toString());
        }
        setTexturePathsRelative(particleNode, file.getParentFile(), true);
        try {
            BinaryExporter.getInstance().save(
                    particleNode.getQuantity() > 1 ? particleNode
                            : particleGeom, file);
            openFile = file;
            updateTitle();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Couldn't save '" + file
                    + "': " + e, "File Error", JOptionPane.ERROR_MESSAGE);
        }
        setTexturePathsRelative(particleNode, file.getParentFile(), false);
    }

    private void setTexturePathsRelative(Spatial spatial, File parent,
            boolean relative) {
        TextureState tstate = (TextureState) spatial.getRenderState(RenderState.StateType.Texture);
        if (tstate != null) {
            Texture tex = tstate.getTexture();
            if (tex != null
                    && tex.getTextureKey() != null
                    && "file".equals(tex.getTextureKey().getLocation()
                            .getProtocol())) {
                String tfile = tex.getTextureKey().getLocation().getFile();
                try {
                    if (relative) {
                        String path = relativize(new File(tfile), parent)
                                .replace(File.separatorChar, '/');
                        tex.getTextureKey()
                                .setLocation(new URL("file:" + path));
                    } else {
                        tex.getTextureKey().setLocation(
                                new File(parent, tfile).getCanonicalFile()
                                        .toURI().toURL());
                    }
                } catch (Exception e) {
                    logger.logp(Level.SEVERE, 
                                    this.getClass().toString(),
                                    "setTexturePathsRelative(Spatial spatial, " +
                                    "File parent, boolean relative)", "Exception",
                                    e);
                }
            }
        }
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (int ii = 0, nn = node.getQuantity(); ii < nn; ii++) {
                setTexturePathsRelative(node.getChild(ii), parent, relative);
            }
        }
    }

    private String relativize(File absolute, File parent) {
        String abspath = absolute.toString();
        StringBuffer path = new StringBuffer();
        while (!abspath.startsWith(parent.toString())) {
            path.append("..").append(File.separatorChar);
            if ((parent = parent.getParentFile()) == null) {
                return abspath; // different roots
            }
        }
        String pstr = parent.toString();
        path.append(abspath.substring(pstr.length()
                + (pstr.endsWith(File.separator) ? 0 : 1)));
        return path.toString();
    }

    private void showBackgroundDialog() {
        final Color bg = JColorChooser.showDialog(this,
                "Choose Background Color", makeColor(impl.getRenderer()
                        .getBackgroundColor(), false));
        if (bg != null) {
            prefs.putInt("bg_color", bg.getRGB());
            Callable<Void> exe = new Callable<Void>() {
                public Void call() {
                    impl.getRenderer().setBackgroundColor(makeColorRGBA(bg));
                    return null;
                }
            };
            GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
                    .enqueue(exe);
        }
    }

    private void createNewLayer() {
        particleGeom = ParticleFactory.buildParticles(createLayerName(), 300);
        particleGeom.addInfluence(SimpleParticleInfluenceFactory
                .createBasicGravity(new Vector3f(0, -3f, 0), true));
        particleGeom.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
        particleGeom.setMaximumAngle(0.2268928f);
        particleGeom.getParticleController().setSpeed(1.0f);
        particleGeom.setMinimumLifeTime(2000.0f);
        particleGeom.setStartSize(10.0f);
        particleGeom.setEndSize(10.0f);
        particleGeom.setStartColor(new ColorRGBA(0.0f, 0.0625f, 1.0f, 1.0f));
        particleGeom.setEndColor(new ColorRGBA(0.0f, 0.0625f, 1.0f, 0.0f));
        particleGeom.warmUp(120);

        BlendState as = (BlendState) particleGeom.getRenderState(RenderState.StateType.Blend);
        if (as == null) {
            as = DisplaySystem.getDisplaySystem().getRenderer()
                    .createBlendState();
            as.setBlendEnabled(true);
            as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
            as.setTestEnabled(true);
            as.setTestFunction(BlendState.TestFunction.GreaterThan);
            particleGeom.setRenderState(as);
            particleGeom.updateRenderState();
        }
        as.setDestinationFunction(BlendState.DestinationFunction.One);
        TextureState ts = impl.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture(RenParticleEditor.class
                .getClassLoader().getResource(
                        "jmetest/data/texture/flaresmall.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear));
        particleGeom.setRenderState(ts);

        particleNode.attachChild(particleGeom);
        particleGeom.updateRenderState();
    }

    private String createLayerName() {
        int max = -1;
        for (int ii = 0, nn = particleNode.getQuantity(); ii < nn; ii++) {
            String name = particleNode.getChild(ii).getName();
            if (name.startsWith("Layer #")) {
                try {
                    max = Math.max(max, Integer.parseInt(name.substring(7)));
                } catch (NumberFormatException e) {
                }
            }
        }
        return "Layer #" + (max + 1);
    }

    private void deleteLayer() {
        int idx = layerTable.getSelectedRow(), sidx = (idx == particleNode
                .getQuantity() - 1) ? idx - 1 : idx;
        layerTable.clearSelection();
        particleNode.detachChildAt(idx);
        layerModel.fireTableRowsDeleted(idx, idx);
        layerTable.setRowSelectionInterval(sidx, sidx);

        if (particleNode.getQuantity() == 1) {
            deleteLayerButton.setEnabled(false);
        }
    }

    /**
     * applyExample
     */
    private void applyExample() {
        if (exampleList == null || exampleList.getSelectedValue() == null)
            return;
        String examType = exampleList.getSelectedValue().toString();
        particleGeom.clearInfluences();
        if ("FIRE".equalsIgnoreCase(examType)) {
            particleGeom.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
            particleGeom.setMaximumAngle(0.20943952f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(1.0f);
            particleGeom.setMinimumLifeTime(1000.0f);
            particleGeom.setMaximumLifeTime(1500.0f);
            particleGeom.setStartSize(40.0f);
            particleGeom.setEndSize(40.0f);
            particleGeom
                    .setStartColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 0.0f));
            particleGeom.getParticleController().setControlFlow(true);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(0.3f);
            particleGeom.getParticleController().setRepeatType(
                    Controller.RT_WRAP);
        } else if ("FOUNTAIN".equalsIgnoreCase(examType)) {
            particleGeom.addInfluence(SimpleParticleInfluenceFactory
                    .createBasicGravity(new Vector3f(0, -3f, 0), true));
            particleGeom.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
            particleGeom.setMaximumAngle(0.2268928f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(1.0f);
            particleGeom.setMinimumLifeTime(1300.0f);
            particleGeom.setMaximumLifeTime(1950.0f);
            particleGeom.setStartSize(10.0f);
            particleGeom.setEndSize(10.0f);
            particleGeom
                    .setStartColor(new ColorRGBA(0.0f, 0.0625f, 1.0f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(0.0f, 0.0625f, 1.0f, 0.0f));
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(1.1f);
            particleGeom.getParticleController().setRepeatType(
                    Controller.RT_WRAP);
        } else if ("LAVA".equalsIgnoreCase(examType)) {
            particleGeom.addInfluence(SimpleParticleInfluenceFactory
                    .createBasicGravity(new Vector3f(0, -3f, 0), true));
            particleGeom.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
            particleGeom.setMaximumAngle(0.418f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(1.0f);
            particleGeom.setMinimumLifeTime(1057.0f);
            particleGeom.setMaximumLifeTime(1500.0f);
            particleGeom.setStartSize(40.0f);
            particleGeom.setEndSize(40.0f);
            particleGeom
                    .setStartColor(new ColorRGBA(1.0f, 0.18f, 0.125f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(1.0f, 0.18f, 0.125f, 0.0f));
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(1.1f);
            particleGeom.getParticleController().setRepeatType(
                    Controller.RT_WRAP);
        } else if ("SMOKE".equalsIgnoreCase(examType)) {
            particleGeom.setEmissionDirection(new Vector3f(0.0f, 0.6f, 0.0f));
            particleGeom.setMaximumAngle(0.36651915f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(0.2f);
            particleGeom.setMinimumLifeTime(1000.0f);
            particleGeom.setMaximumLifeTime(1500.0f);
            particleGeom.setStartSize(32.5f);
            particleGeom.setEndSize(40.0f);
            particleGeom.setStartColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 0.0f));
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(0.58f);
            particleGeom.setParticleSpinSpeed(0.08f);
        } else if ("RAIN".equalsIgnoreCase(examType)) {
            particleGeom.addInfluence(SimpleParticleInfluenceFactory
                    .createBasicGravity(new Vector3f(0, -3f, 0), true));
            particleGeom.setEmissionDirection(new Vector3f(0.0f, -1.0f, 0.0f));
            particleGeom.setMaximumAngle(3.1415927f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(0.5f);
            particleGeom.setMinimumLifeTime(1626.0f);
            particleGeom.setMaximumLifeTime(2400.0f);
            particleGeom.setStartSize(9.1f);
            particleGeom.setEndSize(13.6f);
            particleGeom.setStartColor(new ColorRGBA(0.16078432f, 0.16078432f,
                    1.0f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(0.16078432f, 0.16078432f,
                    1.0f, 0.15686275f));
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(0.58f);
            particleGeom.getParticleController().setRepeatType(
                    Controller.RT_WRAP);
        } else if ("SNOW".equalsIgnoreCase(examType)) {
            particleGeom.addInfluence(SimpleParticleInfluenceFactory
                    .createBasicGravity(new Vector3f(0, -3f, 0), true));
            particleGeom.setEmissionDirection(new Vector3f(0.0f, -1.0f, 0.0f));
            particleGeom.setMaximumAngle(1.5707964f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(0.2f);
            particleGeom.setMinimumLifeTime(1057.0f);
            particleGeom.setMaximumLifeTime(1500.0f);
            particleGeom.setStartSize(30.0f);
            particleGeom.setEndSize(30.0f);
            particleGeom.setStartColor(new ColorRGBA(0.3764706f, 0.3764706f,
                    0.3764706f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(0.3764706f, 0.3764706f,
                    0.3764706f, 0.1882353f));
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(0.59999996f);
            particleGeom.getParticleController().setRepeatType(
                    Controller.RT_WRAP);
        } else if ("JET".equalsIgnoreCase(examType)) {
            particleGeom.setEmissionDirection(new Vector3f(-1.0f, 0.0f, 0.0f));
            particleGeom.setMaximumAngle(0.034906585f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(1.0f);
            particleGeom.setMinimumLifeTime(100.0f);
            particleGeom.setMaximumLifeTime(150.0f);
            particleGeom.setStartSize(6.6f);
            particleGeom.setEndSize(30.0f);
            particleGeom.setStartColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(0.6f, 0.2f, 0.0f, 0.0f));
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(1.4599999f);
            particleGeom.getParticleController().setRepeatType(
                    Controller.RT_WRAP);
        } else if ("EXPLOSION".equalsIgnoreCase(examType)) {
            particleGeom.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
            particleGeom.setMaximumAngle(3.1415927f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(1.4f);
            particleGeom.setMinimumLifeTime(1000.0f);
            particleGeom.setMaximumLifeTime(1500.0f);
            particleGeom.setStartSize(40.0f);
            particleGeom.setEndSize(40.0f);
            particleGeom
                    .setStartColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(1.0f, 0.24313726f,
                    0.03137255f, 0.0f));
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.getParticleController().setRepeatType(
                    Controller.RT_CLAMP);
        } else if ("GROUND FOG".equalsIgnoreCase(examType)) {
            particleGeom.setEmissionDirection(new Vector3f(0.0f, 0.3f, 0.0f));
            particleGeom.setMaximumAngle(1.5707964f);
            particleGeom.setMinimumAngle(1.5707964f);
            particleGeom.getParticleController().setSpeed(0.5f);
            particleGeom.setMinimumLifeTime(1774.0f);
            particleGeom.setMaximumLifeTime(2800.0f);
            particleGeom.setStartSize(35.4f);
            particleGeom.setEndSize(40.0f);
            particleGeom.setStartColor(new ColorRGBA(0.87058824f, 0.87058824f,
                    0.87058824f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(0.0f, 0.8f, 0.8f, 0.0f));
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(1.0f);
            particleGeom.setParticleSpinSpeed(0.0f);
        } else if ("FIREFLIES".equalsIgnoreCase(examType)) {
            particleGeom.setEmissionDirection(new Vector3f(0, 1, 0));
            particleGeom.setStartSize(3f);
            particleGeom.setEndSize(1.5f);
            particleGeom.setOriginOffset(new Vector3f(0, 0, 0));
            particleGeom.setInitialVelocity(.05f);
            particleGeom.setMinimumLifeTime(5000f);
            particleGeom.setMaximumLifeTime(15000f);
            particleGeom.setStartColor(new ColorRGBA(1, 0, 0, 1));
            particleGeom.setEndColor(new ColorRGBA(0, 1, 0, 1));
            particleGeom.setMaximumAngle(FastMath.PI);
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.getParticleController().setSpeed(0.75f);
            SwarmInfluence swarm = new SwarmInfluence(new Vector3f(0, 0, 0),
                    .001f);
            swarm.setMaxSpeed(.2f);
            swarm.setSpeedBump(0.025f);
            swarm.setTurnSpeed(FastMath.DEG_TO_RAD * 360);
            particleGeom.addInfluence(swarm);
        }

        particleGeom.warmUp(120);
        updateFromManager();
    }

    /**
     * updateFromManager
     */
    public void updateFromManager() {
        // update appearance controls
        appearancePanel.setEdittedParticles(particleGeom);
        appearancePanel.updateWidgets();

        // update flow controls
        flowPanel.setEdittedParticles(particleGeom);
        flowPanel.updateWidgets();

        // update origin controls
        originPanel.setEdittedParticles(particleGeom);
        originPanel.updateWidgets();

        // update emission controls
        emissionPanel.setEdittedParticles(particleGeom);
        emissionPanel.updateWidgets();

        // update world controls
        scenePanel.setEdittedParticles(particleGeom);
        scenePanel.updateWidgets();

        // update influence controls
        influencePanel.setEdittedParticles(particleGeom);
        influencePanel.updateWidgets();

        validate();
    }

    /**
     * updateManager
     * 
     * @param particles
     *            number of particles to reset manager with.
     */
    public void resetManager(int particles) {
        particleGeom.recreate(particles);
        validate();
    }

    private Color makeColor(ColorRGBA rgba, boolean useAlpha) {
        return new Color(rgba.r, rgba.g, rgba.b, (useAlpha ? rgba.a : 1f));
    }

    private ColorRGBA makeColorRGBA(Color color) {
        return new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    private void initFileChooser() {
        String pdir = prefs.get("particle_dir", null);
        if (pdir != null) {
            fileChooser.setCurrentDirectory(new File(pdir));
        }
        fileChooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory()
                        || f.toString().toLowerCase().endsWith(".jme");
            }

            public String getDescription() {
                return "JME Files (*.jme)";
            }
        });
    }

    protected Canvas getGlCanvas() {
        if (glCanvas == null) {

            // -------------GL STUFF------------------

            // make the canvas:
        	DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
        	display.registerCanvasConstructor("AWT", LWJGLAWTCanvasConstructor.class);
            glCanvas = (Canvas)display.createCanvas(width, height);
            glCanvas.setMinimumSize(new Dimension(100, 100));

            // add a listener... if window is resized, we can do something about
            // it.
            glCanvas.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent ce) {
                    doResize();
                }
            });

            camhand = new CameraHandler();

            glCanvas.addMouseWheelListener(camhand);
            glCanvas.addMouseListener(camhand);
            glCanvas.addMouseMotionListener(camhand);

            // Important! Here is where we add the guts to the canvas:
            impl = new MyImplementor(width, height);

            ((JMECanvas) glCanvas).setImplementor(impl);

            // -----------END OF GL STUFF-------------

            Callable<Void> exe = new Callable<Void>() {
                public Void call() {
                    forceUpdateToSize();
                    ((JMECanvas) glCanvas).setTargetRate(60);
                    return null;
                }
            };
            GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).enqueue(exe);
        }
        return glCanvas;
    }

    public void forceUpdateToSize() {
        // force a resize to ensure proper canvas size.
        glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight() + 1);
        glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight() - 1);
    }

    class CameraHandler extends MouseAdapter implements MouseMotionListener,
            MouseWheelListener {
        Point last = new Point(0, 0);
        Vector3f focus = new Vector3f();
        private Vector3f vector = new Vector3f();
        private Quaternion rot = new Quaternion();
        public Vector3f worldUpVector = Vector3f.UNIT_Y.clone();

        public void mouseDragged(final MouseEvent arg0) {
            Callable<Void> exe = new Callable<Void>() {
                public Void call() {
                    int difX = last.x - arg0.getX();
                    int difY = last.y - arg0.getY();
                    int mult = arg0.isShiftDown() ? 10 : 1;
                    last.x = arg0.getX();
                    last.y = arg0.getY();

                    int mods = arg0.getModifiers();
                    if ((mods & InputEvent.BUTTON1_MASK) != 0) {
                        rotateCamera(worldUpVector, difX * 0.0025f);
                        rotateCamera(impl.getRenderer().getCamera().getLeft(),
                                -difY * 0.0025f);
                    }
                    if ((mods & InputEvent.BUTTON2_MASK) != 0 && difY != 0) {
                        zoomCamera(difY * mult);
                    }
                    if ((mods & InputEvent.BUTTON3_MASK) != 0) {
                        panCamera(-difX, -difY);
                    }
                    return null;
                }
            };
            GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
                    .enqueue(exe);
        }

        public void mouseMoved(MouseEvent arg0) {
        }

        public void mousePressed(MouseEvent arg0) {
            last.x = arg0.getX();
            last.y = arg0.getY();
        }

        public void mouseWheelMoved(final MouseWheelEvent arg0) {
            Callable<Void> exe = new Callable<Void>() {
                public Void call() {
                    zoomCamera(arg0.getWheelRotation()
                            * (arg0.isShiftDown() ? -100 : -20));
                    return null;
                }
            };
            GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
                    .enqueue(exe);
        }

        public void recenterCamera() {
            Callable<Void> exe = new Callable<Void>() {
                public Void call() {
                    Camera cam = impl.getRenderer().getCamera();
                    Vector3f.ZERO.subtract(focus, vector);
                    cam.getLocation().addLocal(vector);
                    focus.addLocal(vector);
                    cam.lookAt(focus, worldUpVector );
                    cam.onFrameChange();
                    return null;
                }
            };
            GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
                    .enqueue(exe);
        }

        private void rotateCamera(Vector3f axis, float amount) {
            Camera cam = impl.getRenderer().getCamera();
            if (axis.equals(cam.getLeft())) {
                float elevation = -FastMath.asin(cam.getDirection().z);
                // keep the camera constrained to -89 -> 89 degrees elevation
                amount = Math.min(Math.max(elevation + amount,
                        -(FastMath.DEG_TO_RAD * 89)), (FastMath.DEG_TO_RAD * 89))
                        - elevation;
            }
            rot.fromAngleAxis(amount, axis);
            cam.getLocation().subtract(focus, vector);
            rot.mult(vector, vector);
            focus.add(vector, cam.getLocation());
            cam.lookAt(focus, worldUpVector );
        }

        private void panCamera(float left, float up) {
            Camera cam = impl.getRenderer().getCamera();
            cam.getLeft().mult(left, vector);
            vector.scaleAdd(up, cam.getUp(), vector);
            cam.getLocation().addLocal(vector);
            focus.addLocal(vector);
            cam.onFrameChange();
        }

        private void zoomCamera(float amount) {
            Camera cam = impl.getRenderer().getCamera();
            float dist = cam.getLocation().distance(focus);
            amount = dist - Math.max(0f, dist - amount);
            cam.getLocation().scaleAdd(amount, cam.getDirection(),
                    cam.getLocation());
            cam.onFrameChange();
        }
    }

    protected void doResize() {
        if (impl != null) {
            impl.resizeCanvas(glCanvas.getWidth(), glCanvas.getHeight());
            if (impl.getCamera() != null) {
                Callable<Void> exe = new Callable<Void>() {
                    public Void call() {
                        impl.getCamera().setFrustumPerspective(
                                45.0f,
                                (float) glCanvas.getWidth()
                                        / (float) glCanvas.getHeight(), 1,
                                10000);
                        return null;
                    }
                };
                GameTaskQueueManager.getManager()
                        .getQueue(GameTaskQueue.RENDER).enqueue(exe);
            }
        }
    }

    class LayerTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        public int getRowCount() {
            return particleNode == null ? 0 : particleNode.getQuantity();
        }

        public int getColumnCount() {
            return 2;
        }

        public String getColumnName(int columnIndex) {
            return columnIndex == 0 ? "Name" : "Visible";
        }

        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? String.class : Boolean.class;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            ParticleSystem pmesh = (ParticleSystem) particleNode
                    .getChild(rowIndex);
            return (columnIndex == 0) ? pmesh.getName() : Boolean.valueOf(pmesh
                    .getCullHint() != Spatial.CullHint.Always);
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            ParticleSystem pmesh = (ParticleSystem) particleNode
                    .getChild(rowIndex);
            if (columnIndex == 0) {
                pmesh.setName((String) aValue);
            } else {
                pmesh
                        .setCullHint(((Boolean) aValue).booleanValue() ? Spatial.CullHint.Dynamic
                                : Spatial.CullHint.Always);
            }
        }
    }

    // IMPLEMENTING THE SCENE:

    class MyImplementor extends SimpleCanvasImpl {

        /**
         * The root node of our stat graphs.
         */
        protected Node statNode;

        private TabledLabelGrapher tgrapher;

        private Quad labGraph;
        public MyImplementor(int width, int height) {
            super(width, height);
        }

        public void simpleSetup() {
            Color bg = new Color(prefs.getInt("bg_color", 0));
            renderer.setBackgroundColor(makeColorRGBA(bg));
            cam.setFrustumPerspective(45.0f, (float) glCanvas.getWidth()
                    / (float) glCanvas.getHeight(), 1, 10000);

            root = rootNode;

            // Finally, a stand alone node (not attached to root on purpose)
            statNode = new Node("stat node");
            statNode.setCullHint(Spatial.CullHint.Never);

            if (Debug.stats) {
                setupStatGraphs();
                setupStats();
            }

            root.attachChild(grid);
            grid.updateRenderState();

            particleNode = new Node("particles");
            root.attachChild(particleNode);

            ZBufferState zbuf = renderer.createZBufferState();
            zbuf.setWritable(false);
            zbuf.setEnabled(true);
            zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);

            particleNode.setRenderState(zbuf);
            particleNode.updateRenderState();

            statNode.updateGeometricState(0, true);
            statNode.updateRenderState();

            createNewSystem();
            
            logger.info("Running on: "
                    + DisplaySystem.getDisplaySystem().getAdapter()
                    + "\nDriver version: "
                    + DisplaySystem.getDisplaySystem().getDriverVersion()
                    + "\n"
                    + DisplaySystem.getDisplaySystem().getDisplayVendor()
                    + " - "
                    + DisplaySystem.getDisplaySystem().getDisplayRenderer()
                    + " - "
                    + DisplaySystem.getDisplaySystem().getDisplayAPIVersion());

        };

        public void simpleUpdate() {
            if (Debug.stats) {
                StatCollector.update();
                labGraph.setLocalTranslation((renderer.getWidth()-.5f*labGraph.getWidth()), (renderer.getHeight()-.5f*labGraph.getHeight()), 0);
            }
        }

        @Override
        public void simpleRender() {
            statNode.draw(renderer);
        }

        /**
         * Set up which stats to graph
         *
         */
        protected void setupStats() {
        	tgrapher.addConfig(StatType.STAT_FRAMES, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.green);
        	tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.cyan);
        	tgrapher.addConfig(StatType.STAT_QUAD_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.lightGray);
        	tgrapher.addConfig(StatType.STAT_LINE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.red);
        	tgrapher.addConfig(StatType.STAT_POINT_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.yellow);
        	tgrapher.addConfig(StatType.STAT_GEOM_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.gray);
        	tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.orange);

            tgrapher.addConfig(StatType.STAT_FRAMES, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_FRAMES, TabledLabelGrapher.ConfigKeys.Name.name(), "Frames/s:");
            tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Tris:");
            tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
            tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Quads:");
            tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
            tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Lines:");
            tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
            tgrapher.addConfig(StatType.STAT_POINT_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_POINT_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Points:");
            tgrapher.addConfig(StatType.STAT_POINT_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
            tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Objs:");
            tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
            tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
            tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Tex binds:");
            tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        }
        
        /**
         * Set up the graphers we will use and the quads we'll show the stats on.
         *
         */
        protected void setupStatGraphs() {
            StatCollector.setSampleRate(1000L);
            StatCollector.setMaxSamples(30);
            labGraph = new Quad("labelGraph", renderer.getWidth()/3, renderer.getHeight()/3) {
                private static final long serialVersionUID = 1L;
                @Override
                public void draw(Renderer r) {
                    StatCollector.pause();
                    super.draw(r);
                    StatCollector.resume();
                }
            };
            tgrapher = GraphFactory.makeTabledLabelGraph(renderer.getWidth()/3, renderer.getHeight()/3, labGraph);
            tgrapher.setColumns(1);
            labGraph.setLocalTranslation((renderer.getWidth()*5/6), (renderer.getHeight()*5/6), 0);
            statNode.attachChild(labGraph);
            
        }
    }

    private static final int GRID_LINES = 51;
    private static final float GRID_SPACING = 100f;

    private Geometry createGrid() {
        Vector3f[] vertices = new Vector3f[GRID_LINES * 2 * 2];
        float edge = GRID_LINES / 2 * GRID_SPACING;
        for (int ii = 0, idx = 0; ii < GRID_LINES; ii++) {
            float coord = (ii - GRID_LINES / 2) * GRID_SPACING;
            vertices[idx++] = new Vector3f(-edge, 0f, coord);
            vertices[idx++] = new Vector3f(+edge, 0f, coord);
            vertices[idx++] = new Vector3f(coord, 0f, -edge);
            vertices[idx++] = new Vector3f(coord, 0f, +edge);
        }
        Geometry grid = new com.jme.scene.Line("grid", vertices, null,
                null, null) {
            private static final long serialVersionUID = 1L;
            @Override
            public void draw(Renderer r) {
                StatCollector.pause();
                super.draw(r);
                StatCollector.resume();
            }
        };
        grid.getDefaultColor().set(ColorRGBA.darkGray.clone());
        grid.setCullHint(prefs.getBoolean("showgrid", true) ? Spatial.CullHint.Dynamic
                : Spatial.CullHint.Always);
        return grid;
    }
}
