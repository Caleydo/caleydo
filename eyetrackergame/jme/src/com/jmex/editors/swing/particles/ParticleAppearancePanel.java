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

package com.jmex.editors.swing.particles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.effects.particles.AnimationEntry;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleSystem;
import com.jmex.effects.particles.ParticleInfluence;
import com.jmex.effects.particles.ParticlePoints;
import com.jmex.effects.particles.RampEntry;
import com.jmex.effects.particles.ParticleSystem.ParticleType;

public abstract class ParticleAppearancePanel extends ParticleEditPanel {
    private static final Logger logger = Logger
            .getLogger(ParticleAppearancePanel.class.getName());
    
    private static final long serialVersionUID = 1L;
    private static File newTexture = null;

    private JCheckBox additiveBlendingBox;
    private JComboBox geomTypeBox;
    private JCheckBox velocityAlignedBox;
    private JLabel imageLabel = new JLabel();

    private JList rampList = null;
    private DefaultListModel rampModel = new DefaultListModel();
    private JButton rampAddButton = makeListButton("Add");
    private JButton rampRemoveButton = makeListButton("Remove");
    private JButton rampEditButton = makeListButton("Edit");
    private JButton rampMoveUpButton = makeListButton("/\\");
    private JButton rampMoveDownButton = makeListButton("\\/");

    private JList animList = null;
    private DefaultListModel animModel = new DefaultListModel();
    private JButton animAddButton = makeListButton("Add");
    private JButton animRemoveButton = makeListButton("Remove");
    private JButton animEditButton = makeListButton("Edit");
    private JButton animMoveUpButton = makeListButton("/\\");
    private JButton animMoveDownButton = makeListButton("\\/");

    private Preferences prefs;
    private JFileChooser textureChooser = new JFileChooser();
    private JPanel texturePanel;

    private ValuePanel texPanel, startTexPanel;

    public ParticleAppearancePanel(Preferences prefs) {
        super();
        this.prefs = prefs;
        setLayout(new GridBagLayout());
        initPanel();
        initTextureChooser();
    }

    private JButton makeListButton(String text) {
        JButton button = new JButton(text);
        button.setMargin(new Insets(2, 2, 2, 2));
        return button;
    }

    private void initPanel() {

        geomTypeBox = new JComboBox(ParticleType.values());
        geomTypeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeParticleType((ParticleType) geomTypeBox.getSelectedItem());
            }
        });

        velocityAlignedBox = new JCheckBox(new AbstractAction(
                "Align with Velocity") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                getEdittedParticles().setVelocityAligned(
                        velocityAlignedBox.isSelected());
            }
        });
        velocityAlignedBox.setFont(new Font("Arial", Font.BOLD, 13));

        rampList = new JList(rampModel);
        rampList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rampList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int selected = rampList.getSelectedIndex();
                rampRemoveButton.setEnabled(selected > 0 && selected < rampModel.getSize()-1);
                rampEditButton.setEnabled(selected != -1);
                rampMoveUpButton.setEnabled(selected > 1 && selected < rampModel.getSize()-1);
                rampMoveDownButton.setEnabled(selected < rampModel.getSize()-2 && selected > 0);
            }
        });
        rampList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    rampEditButton.doClick();
                    e.consume();
                }
            }
        });

        rampAddButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        RampEntry entry = new RampEntry();
                        getEdittedParticles().getRamp().addEntry(entry);
                        showEditWindow(entry);
                        updateRampModel();
                        rampList.setSelectedValue(entry, true);
                    }
                }.start();
            }
        });

        rampEditButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        int index = rampList.getSelectedIndex();
                        RampEntry entry = (RampEntry) rampList
                                .getSelectedValue();
                        showEditWindow(entry);
                        updateRampModel();
                        rampList.setSelectedIndex(index);
                    };
                }.start();
            }
        });

        rampRemoveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RampEntry entry = (RampEntry)rampList.getSelectedValue();
                getEdittedParticles().getRamp().removeEntry(entry);
                updateRampModel();
            }
        });

        rampMoveUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = rampList.getSelectedIndex();
                RampEntry entry = (RampEntry)rampList.getSelectedValue();
                getEdittedParticles().getRamp().removeEntry(entry);
                getEdittedParticles().getRamp().addEntry(index-2, entry);
                updateRampModel();
                rampList.setSelectedValue(entry, true);
            }
        });

        rampMoveDownButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = rampList.getSelectedIndex();
                RampEntry entry = (RampEntry)rampList.getSelectedValue();
                getEdittedParticles().getRamp().removeEntry(entry);
                getEdittedParticles().getRamp().addEntry(index, entry);
                updateRampModel();
                rampList.setSelectedValue(entry, true);
            }
        });
        
        rampRemoveButton.setEnabled(false);
        rampEditButton.setEnabled(false);
        rampMoveUpButton.setEnabled(false);
        rampMoveDownButton.setEnabled(false);
        
        
        JPanel geomPanel = new JPanel(new GridBagLayout());
        geomPanel.setBorder(createTitledBorder("PARTICLE GEOMETRY"));
        geomPanel.add(createBoldLabel("Type:"), new GridBagConstraints(0, 0, 1,
                1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        geomPanel.add(geomTypeBox, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        geomPanel.add(velocityAlignedBox, new GridBagConstraints(0, 1, 2, 1,
                1.0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));

        JPanel rampPanel = new JPanel(new GridBagLayout());
        rampPanel.setBorder(createTitledBorder("APPEARANCE TIMELINE"));
        rampPanel.add(new JScrollPane(rampList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                new GridBagConstraints(1, 0, 1, 6, 1.0, 1.0,
                        GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5), 0, 0));
        rampPanel.add(rampAddButton, new GridBagConstraints(0, 0, 1,
                1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        rampPanel.add(rampRemoveButton, new GridBagConstraints(0, 1, 1,
                1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        rampPanel.add(rampEditButton, new GridBagConstraints(0, 2, 1,
                1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        rampPanel.add(rampMoveUpButton, new GridBagConstraints(0, 3, 1,
                1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        rampPanel.add(rampMoveDownButton, new GridBagConstraints(0, 4, 1,
                1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
    
        animList = new JList(animModel);
        animList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        animList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int selected = animList.getSelectedIndex();
                animRemoveButton.setEnabled(selected != -1);
                animEditButton.setEnabled(selected != -1);
                animMoveUpButton.setEnabled(selected > 0);
                animMoveDownButton.setEnabled(selected != -1 && selected < animModel.getSize()-1);
            }
        });
        
        animList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    animEditButton.doClick();
                    e.consume();
                }
            }
        });

        animAddButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        AnimationEntry entry = new AnimationEntry();
                        getEdittedParticles().getTexAnimation().addEntry(entry);
                        showEditWindow(entry);
                        updateAnimModel();
                        animList.setSelectedValue(entry, true);
                    }
                }.start();
            }
        });

        animEditButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        int index = animList.getSelectedIndex();
                        AnimationEntry entry = (AnimationEntry) animList
                                .getSelectedValue();
                        showEditWindow(entry);
                        updateAnimModel();
                        animList.setSelectedIndex(index);
                    };
                }.start();
            }
        });

        animRemoveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AnimationEntry entry = (AnimationEntry)animList.getSelectedValue();
                getEdittedParticles().getTexAnimation().removeEntry(entry);
                updateAnimModel();
            }
        });

        animMoveUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = animList.getSelectedIndex();
                AnimationEntry entry = (AnimationEntry)animList.getSelectedValue();
                getEdittedParticles().getTexAnimation().removeEntry(entry);
                getEdittedParticles().getTexAnimation().addEntry(index-1, entry);
                updateAnimModel();
                animList.setSelectedValue(entry, true);
            }
        });

        animMoveDownButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = animList.getSelectedIndex();
                AnimationEntry entry = (AnimationEntry)animList.getSelectedValue();
                getEdittedParticles().getTexAnimation().removeEntry(entry);
                getEdittedParticles().getTexAnimation().addEntry(index+1, entry);
                updateAnimModel();
                animList.setSelectedValue(entry, true);
            }
        });
        
        animRemoveButton.setEnabled(false);
        animEditButton.setEnabled(false);
        animMoveUpButton.setEnabled(false);
        animMoveDownButton.setEnabled(false);
        
        JPanel animPanel = new JPanel(new GridBagLayout());
        animPanel.setBorder(createTitledBorder("ANIMATION TIMELINE"));
        animPanel.add(new JScrollPane(animList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                new GridBagConstraints(1, 0, 1, 6, 1.0, 1.0,
                        GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5), 0, 0));
        animPanel.add(animAddButton, new GridBagConstraints(0, 0, 1,
                1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        animPanel.add(animRemoveButton, new GridBagConstraints(0, 1, 1,
                1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        animPanel.add(animEditButton, new GridBagConstraints(0, 2, 1,
                1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        animPanel.add(animMoveUpButton, new GridBagConstraints(0, 3, 1,
                1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        animPanel.add(animMoveDownButton, new GridBagConstraints(0, 4, 1,
                1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));


        additiveBlendingBox = new JCheckBox(new AbstractAction(
                "Additive Blending") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                updateBlendState(additiveBlendingBox.isSelected());
            }
        });
        additiveBlendingBox.setFont(new Font("Arial", Font.BOLD, 13));

        JPanel blendPanel = new JPanel(new GridBagLayout());
        blendPanel.setBorder(createTitledBorder("PARTICLE BLENDING"));
        blendPanel.add(additiveBlendingBox, new GridBagConstraints(0, 0, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));

        JLabel textureLabel = createBoldLabel("Texture Image:");
        JButton changeTextureButton = new JButton(new AbstractAction(
                "Browse...") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                changeTexture();
            }
        });
        changeTextureButton.setFont(new Font("Arial", Font.BOLD, 12));
        changeTextureButton.setMargin(new Insets(2, 2, 2, 2));

        JButton clearTextureButton = new JButton(new AbstractAction("Clear") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                ((TextureState) getEdittedParticles().getRenderState(RenderState.StateType.Texture)).setTexture(null);
                imageLabel.setIcon(null);
            }
        });
        clearTextureButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearTextureButton.setMargin(new Insets(2, 2, 2, 2));

        imageLabel.setBackground(Color.lightGray);
        imageLabel.setMaximumSize(new Dimension(128, 128));
        imageLabel.setMinimumSize(new Dimension(0, 0));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(false);

        texturePanel = new JPanel(new GridBagLayout());
        texturePanel.setBorder(createTitledBorder("PARTICLE TEXTURE"));
        texturePanel.add(textureLabel, new GridBagConstraints(0, 0, 1, 1, 0.0,
                0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        texturePanel.add(changeTextureButton, new GridBagConstraints(0, 1, 1,
                1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));
        texturePanel.add(clearTextureButton, new GridBagConstraints(0, 2, 1, 1,
                0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));
        texturePanel.add(imageLabel, new GridBagConstraints(1, 0, 1, 3, 1.0,
                1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));

        texPanel = new ValuePanel("Sub Images: ", "", 1, Integer.MAX_VALUE, 1);
        texPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setTexQuantity(texPanel.getIntValue());
            }
        });

        texturePanel.add(texPanel, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        

        startTexPanel = new ValuePanel("Start Index: ", "", 0, Integer.MAX_VALUE, 1);
        startTexPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setStartTexIndex(startTexPanel.getIntValue());
            }
        });

        texturePanel.add(startTexPanel, new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        
        

        add(geomPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 10), 0, 0));
        add(texturePanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 5, 10), 0, 0));
        add(blendPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 5, 10), 0, 0));
        add(rampPanel, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 5, 10), 0, 0));
        add(animPanel, new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 5, 10), 0, 0));
    }

    protected void showEditWindow(final RampEntry entry) {
        RampEntryEditDialog dialog = new RampEntryEditDialog(entry);
        dialog.setLocationRelativeTo(ParticleAppearancePanel.this);
        dialog.setModal(true);
        dialog.setVisible(true);
        dialog.toFront();
    }

    protected void showEditWindow(final AnimationEntry entry) {
        AnimationEntryEditDialog dialog = new AnimationEntryEditDialog(entry);
        dialog.setLocationRelativeTo(ParticleAppearancePanel.this);
        dialog.setModal(true);
        dialog.setVisible(true);
        dialog.toFront();
    }

    protected void updateRampModel() {
        rampModel.clear();
        rampModel.addElement(new StartRamp(getEdittedParticles()));
        Iterator<RampEntry> it = getEdittedParticles().getRamp().getEntries();
        while (it.hasNext()) {
            RampEntry e = it.next();
            rampModel.addElement(e);
        }
        rampModel.addElement(new EndRamp(getEdittedParticles()));
    }

    protected void updateAnimModel() {
        animModel.clear();
        Iterator<AnimationEntry> it = getEdittedParticles().getTexAnimation().getEntries();
        while (it.hasNext()) {
            AnimationEntry e = it.next();
            animModel.addElement(e);
        }
    }

    private void changeParticleType(ParticleType newType) {
        ParticleType oldType = getEdittedParticles().getParticleType();
        if (newType == oldType) {
            return;
        }
        ParticleSystem oldGeom = getEdittedParticles(), newGeom;
        if (newType == ParticleSystem.ParticleType.Point) {
            ParticlePoints pPoints = ParticleFactory.buildPointParticles(oldGeom.getName(),
                    oldGeom.getNumParticles());
            newGeom = pPoints;
            pPoints.setPointSize(5);
            pPoints.setAntialiased(true);
        } else if (newType == ParticleSystem.ParticleType.Line) {
            newGeom = ParticleFactory.buildLineParticles(oldGeom.getName(),
                oldGeom.getNumParticles());
        } else {
            newGeom = ParticleFactory.buildParticles(oldGeom.getName(),
                oldGeom.getNumParticles(), newType);
        }
        // copy appearance parameters
        newGeom.setVelocityAligned(oldGeom.isVelocityAligned());
        newGeom.setStartColor(oldGeom.getStartColor().clone());
        newGeom.setEndColor(oldGeom.getEndColor().clone());
        newGeom.setStartTexIndex(oldGeom.getStartTexIndex());
        newGeom.setStartSize(oldGeom.getStartSize());
        newGeom.setEndSize(oldGeom.getEndSize());
        newGeom.setStartMass(oldGeom.getStartMass());
        newGeom.setEndMass(oldGeom.getEndMass());
        newGeom.setStartSpin(oldGeom.getStartSpin());
        newGeom.setEndSpin(oldGeom.getEndSpin());
        newGeom.setRamp(oldGeom.getRamp());
        newGeom.setTexQuantity(oldGeom.getTexQuantity());
        
        // copy origin parameters
        newGeom.setLocalTranslation(oldGeom.getLocalTranslation());
        newGeom.setLocalRotation(oldGeom.getLocalRotation());
        newGeom.setLocalScale(oldGeom.getLocalScale());
        newGeom.setOriginOffset(oldGeom.getOriginOffset());
        newGeom.setGeometry(oldGeom.getLine());
        newGeom.setGeometry(oldGeom.getRectangle());
        newGeom.setGeometry(oldGeom.getRing());
        newGeom.setEmitType(oldGeom.getEmitType());
        
        // copy emission parameters
        newGeom.setRotateWithScene(oldGeom.isRotateWithScene());
        newGeom.setEmissionDirection(oldGeom.getEmissionDirection());
        newGeom.setMinimumAngle(oldGeom.getMinimumAngle());
        newGeom.setMaximumAngle(oldGeom.getMaximumAngle());
        newGeom.setInitialVelocity(oldGeom.getInitialVelocity());
        
        // copy flow parameters
        newGeom.setControlFlow(oldGeom.getParticleController().isControlFlow());
        newGeom.setReleaseRate(oldGeom.getReleaseRate());
        newGeom.setReleaseVariance(oldGeom.getReleaseVariance());
        newGeom.setRepeatType(oldGeom.getParticleController().getRepeatType());
        
        // copy world parameters
        newGeom.setSpeed(oldGeom.getParticleController().getSpeed());
        newGeom.setMinimumLifeTime(oldGeom.getMinimumLifeTime());
        newGeom.setMaximumLifeTime(oldGeom.getMaximumLifeTime());
        newGeom.getParticleController().setPrecision(
                oldGeom.getParticleController().getPrecision());
        
        // copy influence parameters
        ArrayList<ParticleInfluence> infs = oldGeom.getInfluences();
        if (infs != null) {
            for (ParticleInfluence inf : infs) {
                newGeom.addInfluence(inf);
            }
        }
        
        // copy render states
        for (RenderState.StateType type : RenderState.StateType.values()) {
            RenderState rs = oldGeom.getRenderState(type);
            if (rs != null) {
                newGeom.setRenderState(rs);
            }
        }
        
        requestParticleSystemOverwrite(newGeom);
    }
    
    protected abstract void requestParticleSystemOverwrite(ParticleSystem newParticles);

    private void changeTexture() {
        try {
            int result = textureChooser.showOpenDialog(this);
            if (result == JFileChooser.CANCEL_OPTION) {
                return;
            }
            File textFile = textureChooser.getSelectedFile();
            prefs.put("texture_dir", textFile.getParent());

            newTexture = textFile;
            
            GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).enqueue(new Callable<Object>() {
                public Object call() throws Exception{
                    loadApplyTexture();
                    return null;
                }
            });

            ImageIcon icon = new ImageIcon(
                getToolkit().createImage(textFile.getAbsolutePath()));
            imageLabel.setIcon(icon);
            validate();
        } catch (Exception ex) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "changeTexture()", "Exception",
                    ex);
        }
    }

    private void initTextureChooser() {
        String tdir = prefs.get("texture_dir", null);
        if (tdir != null) {
            textureChooser.setCurrentDirectory(new File(tdir));
        }
    }

    private void loadApplyTexture() throws MalformedURLException {
        TextureState ts = (TextureState)getEdittedParticles().getRenderState(RenderState.StateType.Texture);
        TextureManager.clearCache();
        ts.setTexture(
                TextureManager.loadTexture(
                        newTexture.toURI().toURL(),
                        Texture.MinificationFilter.BilinearNearestMipMap,
                        Texture.MagnificationFilter.Bilinear));
        ts.setEnabled(true);
        getEdittedParticles().setRenderState(ts);
        getEdittedParticles().updateRenderState();
        newTexture = null;
    }

    private void updateBlendState(boolean additive) {
        BlendState as = (BlendState)getEdittedParticles().getRenderState(RenderState.StateType.Blend);
        if (as == null) {
            as = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
            as.setBlendEnabled(true);
            as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
            as.setTestEnabled(true);
            as.setTestFunction(BlendState.TestFunction.GreaterThan);
            getEdittedParticles().setRenderState(as);
            getEdittedParticles().updateRenderState();
        }
        as.setDestinationFunction(additive ?
            BlendState.DestinationFunction.One : BlendState.DestinationFunction.OneMinusSourceAlpha);
    }

    @Override
    public void updateWidgets() {
        updateRampModel();
        
        ParticleSystem system = getEdittedParticles();
        geomTypeBox.setSelectedItem(system.getParticleType());
        velocityAlignedBox.setSelected(system.isVelocityAligned());
        texPanel.setValue(system.getTexQuantity());
        startTexPanel.setValue(system.getStartTexIndex());

        BlendState as = (BlendState)system.getRenderState(RenderState.StateType.Blend);
        additiveBlendingBox.setSelected(as == null ||
            as.getDestinationFunctionRGB() == BlendState.DestinationFunction.One);
        if (getTexturePanel().isVisible()) {
            Texture tex = null;
            try {
                tex = ((TextureState)system.getRenderState(RenderState.StateType.Texture)).getTexture();
                if (tex != null) {
                    if (tex.getTextureKey() != null && tex.getTextureKey().getLocation() != null)
                        imageLabel.setIcon(
                                new ImageIcon(tex.getTextureKey().getLocation()));
                    else
                        imageLabel.setIcon(
                            new ImageIcon(new URL(tex.getImageLocation())));
                } else {
                    imageLabel.setIcon(null);
                }
            } catch (Exception e) {
                logger.warning("image: "+tex+" : "+ tex != null ? tex.getImageLocation() : "");
            }
        }
    }

    public JCheckBox getAdditiveBlendingBox() {
        return additiveBlendingBox;
    }

    public JPanel getTexturePanel() {
        return texturePanel;
    }
    
    public class StartRamp extends RampEntry {

        private ParticleSystem particles;

        public StartRamp(ParticleSystem particles) {
            super(-1);
            this.particles = particles;
            setColor(particles.getStartColor());
            setSize(particles.getStartSize());
            setMass(particles.getStartMass());
            setSpin(particles.getStartSpin());
        }
        
        @Override
        public String toString() {
            return "START: "+super.toString();
        }
        
        @Override
        public void setSize(float size) {
            super.setSize(size);
            particles.setStartSize(size);
        }
        
        @Override
        public void setMass(float mass) {
            super.setMass(mass);
            particles.setStartMass(mass);
        }
        
        @Override
        public void setSpin(float spin) {
            super.setSpin(spin);
            particles.setStartSpin(spin);
        }
        
        @Override
        public void setColor(ColorRGBA color) {
            super.setColor(color);
            particles.setStartColor(color);
        }
    }
    
    public class EndRamp extends RampEntry {

        private ParticleSystem particles;

        public EndRamp(ParticleSystem particles) {
            super(-1);
            this.particles = particles;
            setColor(particles.getEndColor());
            setSize(particles.getEndSize());
            setMass(particles.getEndMass());
            setSpin(particles.getEndSpin());
        }
        
        @Override
        public String toString() {
            return "END: "+super.toString();
        }
        
        @Override
        public void setSize(float size) {
            super.setSize(size);
            particles.setEndSize(size);
        }
        
        @Override
        public void setMass(float mass) {
            super.setMass(mass);
            particles.setEndMass(mass);
        }
        
        @Override
        public void setSpin(float spin) {
            super.setSpin(spin);
            particles.setEndSpin(spin);
        }
        
        @Override
        public void setColor(ColorRGBA color) {
            super.setColor(color);
            particles.setEndColor(color);
        }
    }
}
