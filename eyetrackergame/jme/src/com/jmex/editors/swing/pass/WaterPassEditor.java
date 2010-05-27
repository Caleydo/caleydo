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
package com.jmex.editors.swing.pass;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.renderer.ColorRGBA;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jmex.editors.swing.widget.RGBAChooserPanel;
import com.jmex.editors.swing.widget.ValueSpinner;
import com.jmex.effects.water.WaterRenderPass;

public class WaterPassEditor extends JPanel {
    private static final Logger logger = Logger.getLogger(WaterPassEditor.class
            .getName());
    
    private RGBAChooserPanel endColorButton;
    private RGBAChooserPanel startColorButton;
    private JCheckBox enabledCheckBox;
    private JCheckBox waterFoggingBox;
    private JCheckBox enableReflectionBox;
    private JCheckBox enableRefractionBox;
    private JCheckBox enableProjectionCheckBox;
    private ValueSpinner falloffSpeedField;
    private ValueSpinner clipBiasField;
    private ValueSpinner maxAmplitudeField;
    private ValueSpinner falloffStartField;
    private ValueSpinner baseHeightField;
    private ValueSpinner refractField;
    private ValueSpinner reflectField;
    private static final long serialVersionUID = 1L;

    private float origClipBias, origHeightFalloffSpeed, origHeightFalloffStart,
            origSpeedReflection;
    private float origSpeedRefraction, origWaterHeight, origWaterMaxAmplitude;
    private ColorRGBA origWaterColorEnd = new ColorRGBA();
    private ColorRGBA origWaterColorStart = new ColorRGBA();
    private boolean origUseReflection, origUseRefraction, origUseProjection, origWaterFog;

    protected File lastDir;

    public WaterPassEditor(final WaterRenderPass pass) {
        super();

        origClipBias = pass.getClipBias();
        origHeightFalloffSpeed = pass.getHeightFalloffSpeed();
        origHeightFalloffStart = pass.getHeightFalloffStart();
        origSpeedReflection = pass.getSpeedReflection();
        origSpeedRefraction = pass.getSpeedRefraction();
        origWaterColorEnd.set(pass.getWaterColorEnd());
        origWaterColorStart.set(pass.getWaterColorStart());
        origWaterHeight = pass.getWaterHeight();
        origWaterMaxAmplitude = pass.getWaterMaxAmplitude();
        origUseReflection = pass.isUseReflection();
        origUseRefraction = pass.isUseRefraction();
        origUseProjection = pass.isUseRefraction();
        origWaterFog = pass.isUseFadeToFogColor();

        setLayout(new GridBagLayout());

        enabledCheckBox = new JCheckBox("Water Enabled");
        enabledCheckBox.setSelected(pass.isEnabled());
        enabledCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                pass.setEnabled(enabledCheckBox.isSelected());
            }
        });
        final GridBagConstraints enabledBagConstraints = new GridBagConstraints();
        enabledBagConstraints.gridwidth = 2;
        enabledBagConstraints.gridy = 0;
        enabledBagConstraints.gridx = 0;
        add(enabledCheckBox, enabledBagConstraints);

        final JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new GridBagLayout());
        colorPanel.setBorder(new TitledBorder(null, "Water Color",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 0;
        add(colorPanel, gridBagConstraints);

        final JLabel startLabel = new JLabel();
        startLabel.setText("Start");
        final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.insets = new Insets(0, 2, 0, 4);
        gridBagConstraints_1.gridy = 0;
        gridBagConstraints_1.gridx = 0;
        colorPanel.add(startLabel, gridBagConstraints_1);

        final JLabel endLabel = new JLabel();
        endLabel.setText("End");
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.insets = new Insets(0, 4, 0, 2);
        gridBagConstraints_2.gridy = 0;
        gridBagConstraints_2.gridx = 1;
        colorPanel.add(endLabel, gridBagConstraints_2);

        startColorButton = new RGBAChooserPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected ColorRGBA getColor() {
                return origWaterColorStart.clone();
            }

            @Override
            protected void setColor(ColorRGBA color) {
                pass.getWaterColorStart().set(color);
            }
        };
        final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.insets = new Insets(2, 2, 0, 4);
        gridBagConstraints_3.gridy = 1;
        gridBagConstraints_3.gridx = 0;
        colorPanel.add(startColorButton, gridBagConstraints_3);

        endColorButton = new RGBAChooserPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected ColorRGBA getColor() {
                return origWaterColorEnd.clone();
            }

            @Override
            protected void setColor(ColorRGBA color) {
                pass.getWaterColorEnd().set(color);
            }
        };
        final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
        gridBagConstraints_4.insets = new Insets(2, 4, 0, 2);
        gridBagConstraints_4.gridy = 1;
        gridBagConstraints_4.gridx = 1;
        colorPanel.add(endColorButton, gridBagConstraints_4);

        waterFoggingBox = new JCheckBox();
        waterFoggingBox.setSelected(pass.isUseFadeToFogColor());
        waterFoggingBox.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                pass.useFadeToFogColor(waterFoggingBox.isSelected());
            }
        });
        waterFoggingBox.setText("Water Fogging");
        final GridBagConstraints gridBagConstraints_27 = new GridBagConstraints();
        gridBagConstraints_27.gridwidth = 2;
        gridBagConstraints_27.gridy = 2;
        gridBagConstraints_27.gridx = 0;
        colorPanel.add(waterFoggingBox, gridBagConstraints_27);

        final JPanel reflectPanel = new JPanel();
        reflectPanel.setLayout(new GridBagLayout());
        reflectPanel.setBorder(new TitledBorder(null, "Reflection",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
        gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_8.gridy = 2;
        gridBagConstraints_8.gridx = 0;
        add(reflectPanel, gridBagConstraints_8);

        enableReflectionBox = new JCheckBox();
        enableReflectionBox.setSelected(pass.isUseReflection());
        enableReflectionBox.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Callable<?> exe = new Callable() {
                    public Object call() {
                        try {
                            pass.setUseReflection(enableReflectionBox.isSelected());
                        } catch (Exception ex) {
                            logger.log(Level.SEVERE,
                                    "Water Editor Error Caught", ex);
                        }
                        return null;
                    }
                };
                GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).enqueue(exe);
            }
        });
        enableReflectionBox.setBorder(new EmptyBorder(0, 0, 0, 0));
        enableReflectionBox
                .setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        enableReflectionBox.setText("Enable:");
        final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
        gridBagConstraints_11.gridwidth = 2;
        gridBagConstraints_11.weightx = 1;
        gridBagConstraints_11.anchor = GridBagConstraints.WEST;
        gridBagConstraints_11.gridy = 0;
        gridBagConstraints_11.gridx = 0;
        reflectPanel.add(enableReflectionBox, gridBagConstraints_11);

        final JLabel reflectionSpeedLabel = new JLabel();
        reflectionSpeedLabel.setText("Reflection Speed:");
        final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
        gridBagConstraints_13.gridy = 1;
        gridBagConstraints_13.gridx = 0;
        reflectPanel.add(reflectionSpeedLabel, gridBagConstraints_13);

        reflectField = new ValueSpinner(-1000, 1000, .01f);
        reflectField.setValue(pass.getSpeedReflection());
        reflectField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pass.setSpeedReflection(((Number) reflectField.getValue())
                        .floatValue());
            }
        });
        final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
        gridBagConstraints_15.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_15.gridy = 1;
        gridBagConstraints_15.gridx = 1;
        reflectPanel.add(reflectField, gridBagConstraints_15);

        final JPanel refractPanel = new JPanel();
        refractPanel.setBorder(new TitledBorder(null, "Refraction",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        refractPanel.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
        gridBagConstraints_9.weightx = 0;
        gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_9.gridy = 2;
        gridBagConstraints_9.gridx = 1;
        add(refractPanel, gridBagConstraints_9);

        enableRefractionBox = new JCheckBox();
        enableRefractionBox.setSelected(pass.isUseRefraction());
        enableRefractionBox.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Callable<?> exe = new Callable() {
                    public Object call() {
                        try {
                            pass.setUseRefraction(enableRefractionBox.isSelected());
                        } catch (Exception ex) {
                            logger.log(Level.SEVERE,
                                    "Water Editor Error Caught", ex);
                        }
                        return null;
                    }
                };
                GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).enqueue(exe);
            }
        });
        enableRefractionBox.setBorder(new EmptyBorder(0, 0, 0, 0));
        enableRefractionBox
                .setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        enableRefractionBox.setText("Enable:");
        final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
        gridBagConstraints_12.gridwidth = 2;
        gridBagConstraints_12.weightx = 1;
        gridBagConstraints_12.anchor = GridBagConstraints.WEST;
        gridBagConstraints_12.gridy = 0;
        gridBagConstraints_12.gridx = 0;
        refractPanel.add(enableRefractionBox, gridBagConstraints_12);

        final JLabel refractionSpeedLabel = new JLabel();
        refractionSpeedLabel.setText("Refraction Speed:");
        final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
        gridBagConstraints_14.gridy = 1;
        gridBagConstraints_14.gridx = 0;
        refractPanel.add(refractionSpeedLabel, gridBagConstraints_14);

        refractField = new ValueSpinner(-1000, 1000, .1f);
        refractField.setValue(pass.getSpeedRefraction());
        refractField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pass.setSpeedRefraction(((Number) refractField.getValue())
                        .floatValue());
            }
        });
        final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
        gridBagConstraints_16.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_16.gridy = 1;
        gridBagConstraints_16.gridx = 1;
        refractPanel.add(refractField, gridBagConstraints_16);

        final JPanel wavesPanel = new JPanel();
        wavesPanel.setBorder(new TitledBorder(null, "Waves",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        wavesPanel.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
        gridBagConstraints_10.gridwidth = 2;
        gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_10.gridy = 4;
        gridBagConstraints_10.gridx = 0;
        add(wavesPanel, gridBagConstraints_10);

        enableProjectionCheckBox = new JCheckBox();
        enableProjectionCheckBox.setSelected(pass.isUseProjectedShader());
        enableProjectionCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Callable<?> exe = new Callable() {
                    public Object call() {
                        try {
                            pass.setUseProjectedShader(enableProjectionCheckBox.isSelected());
                        } catch (Exception ex) {
                            logger.log(Level.SEVERE,
                                    "Water Editor Error Caught", ex);
                        }
                        return null;
                    }
                };
                GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).enqueue(exe);
            }
        });
        enableProjectionCheckBox.setText("Use Projected");
        final GridBagConstraints gridBagConstraints_28 = new GridBagConstraints();
        gridBagConstraints_28.gridwidth = 2;
        gridBagConstraints_28.gridx = 2;
        gridBagConstraints_28.gridy = 0;
        wavesPanel.add(enableProjectionCheckBox, gridBagConstraints_28);

        final JLabel baseHeightLabel = new JLabel();
        baseHeightLabel.setText("Base Height:");
        final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
        gridBagConstraints_17.insets = new Insets(0, 0, 2, 0);
        gridBagConstraints_17.anchor = GridBagConstraints.EAST;
        gridBagConstraints_17.gridy = 1;
        gridBagConstraints_17.gridx = 0;
        wavesPanel.add(baseHeightLabel, gridBagConstraints_17);

        baseHeightField = new ValueSpinner(-10000, 10000, .1f);
        baseHeightField.setValue(pass.getWaterHeight());
        baseHeightField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pass.setWaterHeight(((Number) baseHeightField.getValue())
                        .floatValue());
            }
        });
        final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
        gridBagConstraints_18.insets = new Insets(0, 0, 2, 0);
        gridBagConstraints_18.gridy = 1;
        gridBagConstraints_18.gridx = 1;
        wavesPanel.add(baseHeightField, gridBagConstraints_18);

        final JLabel falloffStartLabel = new JLabel();
        falloffStartLabel.setText("Falloff Start:");
        final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
        gridBagConstraints_19.insets = new Insets(0, 8, 2, 2);
        gridBagConstraints_19.anchor = GridBagConstraints.EAST;
        gridBagConstraints_19.gridy = 1;
        gridBagConstraints_19.gridx = 2;
        wavesPanel.add(falloffStartLabel, gridBagConstraints_19);

        falloffStartField = new ValueSpinner(-1000, 1000, .1f);
        falloffStartField.setValue(pass.getHeightFalloffStart());
        falloffStartField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pass.setHeightFalloffStart(((Number) falloffStartField
                        .getValue()).floatValue());
            }
        });
        final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
        gridBagConstraints_20.insets = new Insets(0, 0, 2, 0);
        gridBagConstraints_20.gridy = 1;
        gridBagConstraints_20.gridx = 3;
        wavesPanel.add(falloffStartField, gridBagConstraints_20);

        final JLabel maxAmplitudeLabel = new JLabel();
        maxAmplitudeLabel.setText("Max Amplitude:");
        final GridBagConstraints gridBagConstraints_21 = new GridBagConstraints();
        gridBagConstraints_21.insets = new Insets(0, 0, 2, 0);
        gridBagConstraints_21.anchor = GridBagConstraints.EAST;
        gridBagConstraints_21.gridy = 2;
        gridBagConstraints_21.gridx = 0;
        wavesPanel.add(maxAmplitudeLabel, gridBagConstraints_21);

        maxAmplitudeField = new ValueSpinner(-1000, 1000, .1f);
        maxAmplitudeField.setValue(pass.getWaterMaxAmplitude());
        maxAmplitudeField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pass.setWaterMaxAmplitude(((Number) maxAmplitudeField
                        .getValue()).floatValue());
            }
        });
        final GridBagConstraints gridBagConstraints_24 = new GridBagConstraints();
        gridBagConstraints_24.insets = new Insets(0, 0, 2, 0);
        gridBagConstraints_24.gridy = 2;
        gridBagConstraints_24.gridx = 1;
        wavesPanel.add(maxAmplitudeField, gridBagConstraints_24);

        final JLabel falloffSpeedLabel = new JLabel();
        falloffSpeedLabel.setText("Falloff Speed:");
        final GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
        gridBagConstraints_23.insets = new Insets(0, 8, 2, 2);
        gridBagConstraints_23.anchor = GridBagConstraints.EAST;
        gridBagConstraints_23.gridy = 2;
        gridBagConstraints_23.gridx = 2;
        wavesPanel.add(falloffSpeedLabel, gridBagConstraints_23);

        falloffSpeedField = new ValueSpinner(-1000, 1000, .1f);
        falloffSpeedField.setValue(pass.getHeightFalloffSpeed());
        falloffSpeedField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pass.setHeightFalloffSpeed(((Number) falloffSpeedField
                        .getValue()).floatValue());
            }
        });
        final GridBagConstraints gridBagConstraints_26 = new GridBagConstraints();
        gridBagConstraints_26.insets = new Insets(0, 0, 2, 0);
        gridBagConstraints_26.gridy = 2;
        gridBagConstraints_26.gridx = 3;
        wavesPanel.add(falloffSpeedField, gridBagConstraints_26);

        final JLabel clipBiasLabel = new JLabel();
        clipBiasLabel.setText("Clip Bias:");
        final GridBagConstraints gridBagConstraints_22 = new GridBagConstraints();
        gridBagConstraints_22.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints_22.anchor = GridBagConstraints.EAST;
        gridBagConstraints_22.gridy = 3;
        gridBagConstraints_22.gridx = 0;
        wavesPanel.add(clipBiasLabel, gridBagConstraints_22);

        clipBiasField = new ValueSpinner(-1000, 1000, .1f);
        clipBiasField.setValue(pass.getClipBias());
        clipBiasField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pass.setClipBias(((Number) clipBiasField.getValue())
                        .floatValue());
            }
        });
        final GridBagConstraints gridBagConstraints_25 = new GridBagConstraints();
        gridBagConstraints_25.gridy = 3;
        gridBagConstraints_25.gridx = 1;
        wavesPanel.add(clipBiasField, gridBagConstraints_25);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        final GridBagConstraints buttonBagConstraints = new GridBagConstraints();
        buttonBagConstraints.gridwidth = 2;
        buttonBagConstraints.gridy = 5;
        buttonBagConstraints.gridx = 0;
        add(buttonPanel, buttonBagConstraints);

        final JButton saveButton = new JButton();
        saveButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                // find out what file to save to:
                JFileChooser chooser;
                if (lastDir != null)
                    chooser = new JFileChooser(lastDir);
                else
                    chooser = new JFileChooser();

                chooser.setMultiSelectionEnabled(false);
                int result = chooser.showSaveDialog(WaterPassEditor.this);
                if (result == JFileChooser.CANCEL_OPTION)
                    return;

                // save this dir as last opened
                lastDir = chooser.getCurrentDirectory();

                // store current as our new "go back" values.
                origClipBias = pass.getClipBias();
                origHeightFalloffSpeed = pass.getHeightFalloffSpeed();
                origHeightFalloffStart = pass.getHeightFalloffStart();
                origSpeedReflection = pass.getSpeedReflection();
                origSpeedRefraction = pass.getSpeedRefraction();
                origWaterColorEnd.set(pass.getWaterColorEnd());
                origWaterColorStart.set(pass.getWaterColorStart());
                origWaterHeight = pass.getWaterHeight();
                origWaterMaxAmplitude = pass.getWaterMaxAmplitude();
                origUseReflection = pass.isUseReflection();
                origUseRefraction = pass.isUseRefraction();
                origUseProjection = pass.isUseProjectedShader();
                origWaterFog = pass.isUseFadeToFogColor();

                File file = chooser.getSelectedFile();
                Properties p = new Properties();
                p.put("clipBias", "" + origClipBias);
                p.put("heightFalloffSpeed", "" + origHeightFalloffSpeed);
                p.put("heightFalloffStart", "" + origHeightFalloffStart);
                p.put("speedReflection", "" + origSpeedReflection);
                p.put("speedRefraction", "" + origSpeedRefraction);
                p.put("waterColorEnd", "" + origWaterColorEnd.asIntARGB());
                p.put("waterColorStart", "" + origWaterColorStart.asIntARGB());
                p.put("waterHeight", "" + origWaterHeight);
                p.put("waterMaxAmplitude", "" + origWaterMaxAmplitude);
                p.put("useReflection", "" + origUseReflection);
                p.put("useRefraction", "" + origUseRefraction);
                p.put("useProjection", "" + origUseProjection);
                p.put("useWaterFog", "" + origWaterFog);
                try {
                    p.storeToXML(new FileOutputStream(file),
                            "Water properties.");
                } catch (Exception e1) {
                    logger.logp(Level.SEVERE, this.getClass().toString(),
                            "WaterPassEditor(pass)", "Exception", e1);
                }
            }
        });
        saveButton.setText("Export...");
        final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
        gridBagConstraints_5.insets = new Insets(2, 2, 0, 4);
        gridBagConstraints_5.gridx = 0;
        gridBagConstraints_5.gridy = 0;
        buttonPanel.add(saveButton, gridBagConstraints_5);

        final JButton importButton = new JButton();
        importButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                // find out what file to save to:
                JFileChooser chooser;
                if (lastDir != null)
                    chooser = new JFileChooser(lastDir);
                else
                    chooser = new JFileChooser();

                chooser.setMultiSelectionEnabled(false);
                int result = chooser.showOpenDialog(WaterPassEditor.this);
                if (result == JFileChooser.CANCEL_OPTION)
                    return;

                // save this dir as last opened
                lastDir = chooser.getCurrentDirectory();

                File file = chooser.getSelectedFile();
                Properties p = new Properties();
                try {
                    p.loadFromXML(new BufferedInputStream(new FileInputStream(
                            file)));
                } catch (Exception ex) {
                    logger.logp(Level.SEVERE, this.getClass().toString(),
                            "actionPerformed(ActionEvent e)", "Exception", ex);
                    return;
                }

                // read in our new values
                origClipBias = Float.parseFloat(p
                        .getProperty("clipBias", "0.0"));
                origHeightFalloffSpeed = Float.parseFloat(p.getProperty(
                        "heightFalloffSpeed", "0.0"));
                origHeightFalloffStart = Float.parseFloat(p.getProperty(
                        "heightFalloffStart", "0.0"));
                origSpeedReflection = Float.parseFloat(p.getProperty(
                        "speedReflection", "0.0"));
                origSpeedRefraction = Float.parseFloat(p.getProperty(
                        "speedRefraction", "0.0"));
                origWaterColorEnd.fromIntARGB(Integer.parseInt(p.getProperty(
                        "waterColorEnd", "0.0")));
                origWaterColorStart.fromIntARGB(Integer.parseInt(p.getProperty(
                        "waterColorStart", "0.0")));
                origWaterHeight = Float.parseFloat(p.getProperty("waterHeight",
                        "0.0"));
                origWaterMaxAmplitude = Float.parseFloat(p.getProperty(
                        "waterMaxAmplitude", "0.0"));
                origUseReflection = Boolean.parseBoolean(p.getProperty(
                        "useReflection", "true"));
                origUseRefraction = Boolean.parseBoolean(p.getProperty(
                        "useRefraction", "true"));
                origUseProjection = Boolean.parseBoolean(p.getProperty(
                        "useProjection", "true"));
                origWaterFog = Boolean.parseBoolean(p.getProperty(
                        "useWaterFog", "true"));

                // reapply to editor and in turn, to our pass
                revert(pass);
            }
        });
        importButton.setText("Import...");
        final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
        gridBagConstraints_7.insets = new Insets(2, 4, 0, 2);
        gridBagConstraints_7.gridx = 1;
        gridBagConstraints_7.gridy = 0;
        buttonPanel.add(importButton, gridBagConstraints_7);

        final JButton resetButton = new JButton();
        final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
        gridBagConstraints_6.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints_6.gridwidth = 2;
        gridBagConstraints_6.gridx = 0;
        gridBagConstraints_6.gridy = 1;
        buttonPanel.add(resetButton, gridBagConstraints_6);
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                revert(pass);
            }
        });
        resetButton.setText("Reset");
    }

    private void revert(final WaterRenderPass pass) {
        clipBiasField.setValue(origClipBias);
        falloffSpeedField.setValue(origHeightFalloffSpeed);
        falloffStartField.setValue(origHeightFalloffStart);
        reflectField.setValue(origSpeedReflection);
        refractField.setValue(origSpeedRefraction);
        baseHeightField.setValue(origWaterHeight);
        maxAmplitudeField.setValue(origWaterMaxAmplitude);
        enableReflectionBox.setSelected(origUseReflection);
        enableRefractionBox.setSelected(origUseRefraction);
        enableProjectionCheckBox.setSelected(origUseProjection);
        waterFoggingBox.setSelected(origWaterFog);
        startColorButton.updateColor();
        endColorButton.updateColor();
    }

    public static JFrame makeFrame(WaterRenderPass pass) {
        JFrame rVal = new JFrame("Water Settings");
        WaterPassEditor edit = new WaterPassEditor(pass);
        rVal.getContentPane().setLayout(new BorderLayout());
        rVal.getContentPane().add(edit, BorderLayout.CENTER);
        rVal.pack();
        return rVal;
    }
}
