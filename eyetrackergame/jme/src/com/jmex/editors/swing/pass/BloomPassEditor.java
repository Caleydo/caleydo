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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jmex.editors.swing.widget.ValueSpinner;
import com.jmex.effects.glsl.BloomRenderPass;

public class BloomPassEditor extends JPanel {
    private static final Logger logger = Logger.getLogger(BloomPassEditor.class
            .getName());
    
    private JCheckBox enabledCheckBox;
    private JRadioButton rerenderToPbufferRadioButton;
    private JRadioButton reuseFramebufferRadioButton;
    private static final long serialVersionUID = 1L;
    
    private ButtonGroup modeBG = new ButtonGroup();
    private ValueSpinner throttleField;
    private ValueSpinner sizeField;
    private ValueSpinner passesField;
    private ValueSpinner intensityField;
    private ValueSpinner powerField;
    private ValueSpinner cutoffField;
    
    private float origSize, origIntens, origPower, origCutoff;
    private int origPasses, origThrottleMS;
    private boolean origUseCurrent;

    protected File lastDir;
    
    public BloomPassEditor(final BloomRenderPass pass) {
        super();
        
        origCutoff = pass.getExposureCutoff();
        origIntens = pass.getBlurIntensityMultiplier();
        origPasses = pass.getNrBlurPasses();
        origPower = pass.getExposurePow();
        origSize = pass.getBlurSize();
        origUseCurrent = pass.useCurrentScene();
        origThrottleMS = (int)(pass.getThrottle()*1000);
        
        setLayout(new GridBagLayout());

        enabledCheckBox = new JCheckBox();
        enabledCheckBox.setSelected(pass.isEnabled());
        enabledCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                pass.setEnabled(enabledCheckBox.isSelected());
            }
        });
        enabledCheckBox.setText("Bloom Enabled");
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        add(enabledCheckBox, gridBagConstraints);

        final JPanel throttlePanel = new JPanel();
        final GridBagConstraints throttleConstraints = new GridBagConstraints();
        throttleConstraints.gridy = 1;
        throttleConstraints.gridx = 0;
        add(throttlePanel, throttleConstraints);

        final JLabel throttleLabel = new JLabel();
        throttleLabel.setText("Update Freq. (ms):");
        throttlePanel.add(throttleLabel);

        throttleField = new ValueSpinner(0, 1000, 1);
        throttleField.setValue((int)(pass.getThrottle()*1000));
        throttleField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pass.setThrottle(((Number) throttleField.getValue()).intValue()/1000f);
            }
        });
        throttlePanel.add(throttleField);

        final JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.setBorder(new TitledBorder(null, "Render Mode", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_1.weightx = 1;
        gridBagConstraints_1.gridy = 2;
        gridBagConstraints_1.gridx = 0;
        add(panel, gridBagConstraints_1);

        reuseFramebufferRadioButton = new JRadioButton();
        if (pass.useCurrentScene())
            reuseFramebufferRadioButton.setSelected(true);
        reuseFramebufferRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                pass.setUseCurrentScene(true);
            }
        });
        modeBG.add(reuseFramebufferRadioButton);
        reuseFramebufferRadioButton.setText("Reuse FrameBuffer");
        panel.add(reuseFramebufferRadioButton);

        rerenderToPbufferRadioButton = new JRadioButton();
        if (!pass.useCurrentScene())
            rerenderToPbufferRadioButton.setSelected(true);
        rerenderToPbufferRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                pass.setUseCurrentScene(false);
            }
        });
        modeBG.add(rerenderToPbufferRadioButton);
        rerenderToPbufferRadioButton.setText("Rerender to PBuffer");
        panel.add(rerenderToPbufferRadioButton);

        final JPanel exposurePanel = new JPanel();
        exposurePanel.setLayout(new GridLayout(0, 2));
        exposurePanel.setBorder(new TitledBorder(null, "Exposure Settings", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_2.weightx = 1;
        gridBagConstraints_2.gridy = 3;
        gridBagConstraints_2.gridx = 0;
        add(exposurePanel, gridBagConstraints_2);

        final JLabel powerLabel = new JLabel();
        powerLabel.setText("Power:");
        exposurePanel.add(powerLabel);

        powerField = new ValueSpinner(0, 32, .01f);
        powerField.setValue(pass.getExposurePow());
        powerField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pass.setExposurePow(((Number) powerField.getValue()).floatValue());
            }
        });
        exposurePanel.add(powerField);

        final JLabel cutoffLabel = new JLabel();
        cutoffLabel.setText("Cutoff:");
        exposurePanel.add(cutoffLabel);

        cutoffField = new ValueSpinner(0, 10, .01f);
        cutoffField.setValue(pass.getExposureCutoff());
        cutoffField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pass.setExposureCutoff(((Number) cutoffField.getValue()).floatValue());
            }
        });
        exposurePanel.add(cutoffField);

        final JPanel blurPanel = new JPanel();
        blurPanel.setBorder(new TitledBorder(null, "Blur Settings", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        blurPanel.setLayout(new GridLayout(0, 2));
        final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_3.weightx = 1;
        gridBagConstraints_3.gridy = 4;
        gridBagConstraints_3.gridx = 0;
        add(blurPanel, gridBagConstraints_3);

        final JLabel blurPassesLabel = new JLabel();
        blurPassesLabel.setText("Passes:");
        blurPanel.add(blurPassesLabel);

        passesField = new ValueSpinner(0, 16, 1);
        passesField.setValue(pass.getNrBlurPasses());
        passesField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pass.setNrBlurPasses(((Number) passesField.getValue()).intValue());
            }
        });
        blurPanel.add(passesField);

        final JLabel intensityLabel = new JLabel();
        intensityLabel.setText("Intensity:");
        blurPanel.add(intensityLabel);

        intensityField = new ValueSpinner(0, 16, .01f);
        intensityField.setValue(pass.getBlurIntensityMultiplier());
        intensityField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pass.setBlurIntensityMultiplier(((Number) intensityField.getValue()).floatValue());
            }
        });
        blurPanel.add(intensityField);

        final JLabel sizeLabel = new JLabel();
        sizeLabel.setText("Size:");
        blurPanel.add(sizeLabel);

        sizeField = new ValueSpinner(0, 16, .001f);
        sizeField.setValue(pass.getBlurSize());
        sizeField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pass.setBlurSize(((Number) sizeField.getValue()).floatValue());
            }
        });
        
        blurPanel.add(sizeField);
        setSize(218, 359);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
        gridBagConstraints_4.gridy = 5;
        gridBagConstraints_4.gridx = 0;
        add(buttonPanel, gridBagConstraints_4);

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
                int result = chooser.showSaveDialog(BloomPassEditor.this);
                if (result == JFileChooser.CANCEL_OPTION)
                    return;

                // save this dir as last opened
                lastDir = chooser.getCurrentDirectory();

                // store current as our new "go back" values.
                origCutoff = pass.getExposureCutoff();
                origIntens = pass.getBlurIntensityMultiplier();
                origPasses = pass.getNrBlurPasses();
                origThrottleMS = (int)(pass.getThrottle()*1000);
                origPower = pass.getExposurePow();
                origSize = pass.getBlurSize();
                origUseCurrent = pass.useCurrentScene();

                File file = chooser.getSelectedFile();
                Properties p = new Properties();
                p.put("cutoff", ""+origCutoff);
                p.put("intensity", ""+origIntens);
                p.put("passes", ""+origPasses);
                p.put("throttleMS", ""+origThrottleMS);
                p.put("power", ""+origPower);
                p.put("size", ""+origSize);
                p.put("useCurrent", ""+origUseCurrent);
                try {
                    p.storeToXML(new FileOutputStream(file), "Bloom properties.");
                } catch (Exception e1) {
                    logger.logp(Level.SEVERE, this.getClass().toString(),
                            "BloomPassEditor(pass)", "Exception", e1);
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
                int result = chooser.showOpenDialog(BloomPassEditor.this);
                if (result == JFileChooser.CANCEL_OPTION)
                    return;

                // save this dir as last opened
                lastDir = chooser.getCurrentDirectory();

                File file = chooser.getSelectedFile();
                Properties p = new Properties();
                try {
                    p.loadFromXML(new BufferedInputStream(new FileInputStream(file)));
                } catch (Exception ex) {
                    logger.logp(Level.SEVERE, this.getClass().toString(),
                            "actionPerformed(e)", "Exception", ex);
                    return;
                }
                
                // read in our new values
                origCutoff = Float.parseFloat(p.getProperty("cutoff", "0.0"));
                origIntens = Float.parseFloat(p.getProperty("intensity", "1.3"));
                origPasses = Integer.parseInt(p.getProperty("passes", "2"));
                origThrottleMS = Integer.parseInt(p.getProperty("throttleMS", "20"));
                origPower = Float.parseFloat(p.getProperty("power", "3.0"));
                origSize = Float.parseFloat(p.getProperty("size", "0.02"));
                origUseCurrent = Boolean.parseBoolean(p.getProperty("useCurrent", "true"));
                
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

    private void revert(final BloomRenderPass pass) {
        intensityField.setValue(origIntens);
        sizeField.setValue(origSize);
        cutoffField.setValue(origCutoff);
        powerField.setValue(origPower);
        throttleField.setValue(origThrottleMS);
        passesField.setValue(origPasses);
        pass.setUseCurrentScene(origUseCurrent);
        if (origUseCurrent) {
            reuseFramebufferRadioButton.setSelected(true);
        } else {
            rerenderToPbufferRadioButton.setSelected(true);
        }
    }

    public static JFrame makeFrame(BloomRenderPass pass) {
        JFrame rVal = new JFrame("Bloom Settings");
        BloomPassEditor edit = new BloomPassEditor(pass);
        rVal.getContentPane().setLayout(new BorderLayout());
        rVal.getContentPane().add(edit, BorderLayout.CENTER);
        rVal.pack();
        return rVal;
    }
}
