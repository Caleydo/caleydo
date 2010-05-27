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
package com.jmex.editors.swing.widget;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.renderer.ColorRGBA;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;

/**
 * <code>RGBAChooserPanel</code>
 * 
 * @author Joshua Slack
 */
public abstract class RGBAChooserPanel extends JPanel {
    private static final Logger logger = Logger
            .getLogger(RGBAChooserPanel.class.getName());
    
    private JSpinner alphaSpinner;
    private JPanel rgbPanel;
    private static final long serialVersionUID = 1L;

    public RGBAChooserPanel() {
        super();
        setLayout(new GridBagLayout());

        final JLabel rgbLabel = new JLabel();
        rgbLabel.setText("RGB");
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints_2.gridx = 0;
        gridBagConstraints_2.gridy = 0;
        add(rgbLabel, gridBagConstraints_2);

        rgbPanel = new JPanel();
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.ipady = 20;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 0;
        rgbPanel.setToolTipText("Click here to set RGB color.");
        ColorRGBA rgb = new ColorRGBA(getColor());
        rgb.a = 1;
        rgbPanel.setBackground(makeColor(rgb));
        rgbPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Color picked = JColorChooser.showDialog(RGBAChooserPanel.this,
                        "Pick a color", rgbPanel.getBackground());
                if (picked == null)
                    return;
                rgbPanel.setBackground(picked);
                ColorRGBA color = makeColorRGBA(picked);
                color.a = ((Integer) alphaSpinner.getValue()) / 255f;
                setColor(color);
            }
        });
        add(rgbPanel, gridBagConstraints);

        final JLabel alphaLabel = new JLabel();
        alphaLabel.setText("alpha");
        final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.insets = new Insets(2, 0, 0, 0);
        gridBagConstraints_3.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints_3.gridy = 2;
        gridBagConstraints_3.gridx = 0;
        add(alphaLabel, gridBagConstraints_3);

        final SpinnerNumberModel snm = new SpinnerNumberModel(
                (int) (getColor().a * 255), 0, 255, 1);
        alphaSpinner = new JSpinner(snm);
        alphaSpinner.setToolTipText("Alpha value for above color.");
        alphaSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Callable<?> exe = new Callable() {
                    public Object call() {
                        try {
                            ColorRGBA color = makeColorRGBA(rgbPanel.getBackground());
                            color.a = snm.getNumber().floatValue() / 255f;
                            setColor(color);
                        } catch (Exception ex) {
                            logger.log(Level.SEVERE,
                                    "Swing Change Error Caught!", ex);
                        }
                        return null;
                    }
                };
                GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).enqueue(exe);
            }
        });
        final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.insets = new Insets(2, 0, 0, 0);
        gridBagConstraints_1.ipadx = 15;
        gridBagConstraints_1.gridy = 3;
        gridBagConstraints_1.gridx = 0;
        add(alphaSpinner, gridBagConstraints_1);
    }

    public void updateColor() {
        ColorRGBA rgb = new ColorRGBA(getColor());
        rgbPanel.setBackground(makeColor(rgb));
        alphaSpinner.setValue((int) (getColor().a * 255));
    }

    protected abstract ColorRGBA getColor();

    protected abstract void setColor(ColorRGBA color);

    protected ColorRGBA makeColorRGBA(Color color) {
        if (color == null)
            return new ColorRGBA(0, 0, 0, 1);
        return new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    protected Color makeColor(ColorRGBA color) {
        if (color == null)
            return new Color(0, 0, 0, 1);
        return new Color((int) (color.r * 255), (int) (color.g * 255),
                (int) (color.b * 255), (int) (color.a * 255));
    }
}