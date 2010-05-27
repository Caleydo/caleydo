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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.renderer.ColorRGBA;
import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.editors.swing.widget.ValueSpinner;
import com.jmex.effects.particles.RampEntry;

public class RampEntryEditDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private JColorChooser colorChooser = new JColorChooser();
    private JDialog colorChooserDialog = new JDialog((JFrame)null, "Choose a color:");
    private ValueSpinner alphaSpinner = new ValueSpinner(0, 255, 1);
    private JLabel colorHex = new JLabel();
    private JPanel sColorPanel = new JPanel();

    public RampEntryEditDialog(final RampEntry entry) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        setLayout(new GridBagLayout());
        
        if (entry.getOffset() != -1) {
            final ValuePanel offsetPanel = new ValuePanel("Offset: ", "%", 1, 100, 1);
            offsetPanel.setValue((int)(entry.getOffset()*100));
            offsetPanel.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    entry.setOffset(offsetPanel.getIntValue()/100f);
                }
            });
    
            JPanel off = new JPanel(new GridBagLayout());
            off.setBorder(createTitledBorder("OFFSET"));
            off.add(offsetPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
                    0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            add(off, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(10, 5, 5, 5), 0, 0));
        }
        
        final ValuePanel sizePanel = new ValuePanel("Size: ", "", 0f, Float.MAX_VALUE, 1f);
        sizePanel.setValue(0f);
        sizePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                entry.setSize(sizePanel.getFloatValue());
            }
        });

        if (entry.getOffset() != -1) {
            final JCheckBox sizeCheck = new JCheckBox("");
            sizeCheck.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sizePanel.setEnabled(sizeCheck.isSelected());
                    entry.setSize(sizeCheck.isSelected() ? sizePanel.getFloatValue() : RampEntry.DEFAULT_SIZE);
                }
            });
            if (entry.hasSizeSet()) {
                sizeCheck.setSelected(true);
            } else {
                sizeCheck.doClick();
                sizeCheck.doClick();
            }
            add(sizeCheck, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                    new Insets(10, 5, 5, 5), 0, 0));
        }
        if (entry.hasSizeSet()) {
            sizePanel.setValue(entry.getSize());
        }

        
        JPanel size = new JPanel(new GridBagLayout());
        size.setBorder(createTitledBorder("PARTICLE SIZE"));
        size.add(sizePanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(size, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 5, 5), 0, 0));
        
        
        final ValuePanel spinPanel = new ValuePanel("Spin: ", "", -Float.MAX_VALUE, Float.MAX_VALUE, 0.01f);
        spinPanel.setValue(0f);
        spinPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                entry.setSpin(spinPanel.getFloatValue());
            }
        });


        if (entry.getOffset() != -1) {
            final JCheckBox spinCheck = new JCheckBox("");
            spinCheck.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    spinPanel.setEnabled(spinCheck.isSelected());
                    entry.setSpin(spinCheck.isSelected() ? spinPanel.getFloatValue() : RampEntry.DEFAULT_SPIN);
                }
            });
            if (entry.hasSpinSet()) {
                spinCheck.setSelected(true);
            } else {
                spinCheck.doClick();
                spinCheck.doClick();
            }
            add(spinCheck, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                    new Insets(10, 5, 5, 5), 0, 0));
        }
        if (entry.hasSpinSet()) {
            spinPanel.setValue(entry.getSpin());
        }
        
        JPanel spin = new JPanel(new GridBagLayout());
        spin.setBorder(createTitledBorder("PARTICLE SPIN"));
        spin.add(spinPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(spin, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 5, 5), 0, 0));
        
        final ValuePanel massPanel = new ValuePanel("Mass: ", "", -Float.MAX_VALUE, Float.MAX_VALUE, 0.01f);
        massPanel.setValue(0f);
        massPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                entry.setMass(massPanel.getFloatValue());
            }
        });

        if (entry.getOffset() != -1) {
            final JCheckBox massCheck = new JCheckBox("");
            massCheck.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    massPanel.setEnabled(massCheck.isSelected());
                    entry.setMass(massCheck.isSelected() ? massPanel.getFloatValue() : RampEntry.DEFAULT_MASS);
                }
            });
            if (entry.hasMassSet()) {
                massCheck.setSelected(true);
            } else {
                massCheck.doClick();
                massCheck.doClick();
            }
            add(massCheck, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                    new Insets(10, 5, 5, 5), 0, 0));
        }
        if (entry.hasMassSet()) {
            massPanel.setValue(entry.getMass());
        }

        JPanel mass = new JPanel(new GridBagLayout());
        mass.setBorder(createTitledBorder("PARTICLE MASS"));
        mass.add(massPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(mass, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 5, 5), 0, 0));

        final JLabel colorLabel = createBoldLabel("Color:"), alphaLabel = new JLabel("A:");
        colorHex.setFont(new Font("Arial", Font.PLAIN, 10));
        colorHex.setText("#FFFFFF");

        sColorPanel.setBackground(Color.white);
        sColorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        sColorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (sColorPanel.isEnabled())
                    colorPanel_mouseClicked(e);
            }
        });

        alphaSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                entry.getColor().a = ((Number) alphaSpinner
                        .getValue()).intValue() / 255f;
            }
        });
        
        final JPanel colorPanel = new JPanel(new GridBagLayout());
        colorPanel.setBorder(createTitledBorder("PARTICLE COLOR"));
        colorPanel.add(colorLabel, new GridBagConstraints(0, 0, 2, 1, 0.0,
                0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 0, 10), 0, 0));
        colorPanel.add(sColorPanel, new GridBagConstraints(0, 1, 2, 1, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 25, 25));
        colorPanel.add(colorHex, new GridBagConstraints(0, 2, 2, 1, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 4, 0), 0, 0));
        colorPanel.add(alphaSpinner, new GridBagConstraints(1, 3, 1, 1,
                0.25, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 20, 0));
        colorPanel.add(alphaLabel, new GridBagConstraints(0, 3, 1, 1,
                0.25, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        if (entry.getOffset() != -1) {
            final JCheckBox colorCheck = new JCheckBox("");
            colorCheck.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sColorPanel.setEnabled(colorCheck.isSelected());
                    alphaLabel.setEnabled(colorCheck.isSelected());
                    colorLabel.setEnabled(colorCheck.isSelected());
                    alphaSpinner.setEnabled(colorCheck.isSelected());
                    
                    ColorRGBA color = makeColorRGBA(sColorPanel.getBackground());
                    color.a = ((Number) alphaSpinner.getValue()).intValue() / 255f;
                    entry.setColor(colorCheck.isSelected() ? color : RampEntry.DEFAULT_COLOR);
                }
            });
            if (entry.hasColorSet()) {
                colorCheck.setSelected(true);
            } else {
                colorCheck.doClick();
                colorCheck.doClick();
            }
            add(colorCheck, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                    GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                    new Insets(10, 5, 5, 5), 0, 0));
        }
        if (entry.hasColorSet()) {
            sColorPanel.setBackground(makeColor(entry
                    .getColor(), false));
            colorHex.setText(convColorToHex(sColorPanel.getBackground()));
            alphaSpinner.setValue(new Integer(makeColor(
                    entry.getColor(), true).getAlpha()));
        }
        add(colorPanel, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 5, 5), 0, 0));

        setColorChooserDialogOwner(this, entry);
        pack();
    }

    public void setColorChooserDialogOwner(JDialog owner, final RampEntry entry) {
        colorChooserDialog = new JDialog(owner, "Choose a color:");
        initColorChooser(entry);
    }
    
    private void initColorChooser(final RampEntry entry) {
        colorChooser.setColor(sColorPanel.getBackground());
        colorChooserDialog.setLayout(new BorderLayout());
        colorChooserDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        colorChooserDialog.add(colorChooser, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));

        JButton okButton = new JButton("Ok");
        okButton.setOpaque(true);
        okButton.setMnemonic('O');
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color color = colorChooser.getColor();
                if (color == null) {
                    return;
                }
                ColorRGBA rgba = makeColorRGBA(color);
                rgba.a = (Integer.parseInt(alphaSpinner.getValue()
                        .toString()) / 255f);
                entry.setColor(rgba);
                sColorPanel.setBackground(color);
                colorHex.setText(convColorToHex(sColorPanel.getBackground()));
                colorChooserDialog.setVisible(false);
            }
         });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setOpaque(true);
        cancelButton.setMnemonic('C');
        cancelButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               colorChooserDialog.setVisible(false);
           }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        colorChooserDialog.add(buttonPanel, BorderLayout.SOUTH);
        colorChooserDialog.setSize(colorChooserDialog.getPreferredSize());
        colorChooserDialog.setLocationRelativeTo(null);
    }

    protected TitledBorder createTitledBorder(String title) {
        TitledBorder border = new TitledBorder(" " + title + " ");
        border.setTitleFont(new Font("Arial", Font.PLAIN, 10));
        return border;
    }

    private Color makeColor(ColorRGBA rgba, boolean useAlpha) {
        return new Color(rgba.r, rgba.g, rgba.b, (useAlpha ? rgba.a : 1f));
    }

    private ColorRGBA makeColorRGBA(Color color) {
        return new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);
    }
    
    private String convColorToHex(Color c) {
        if (c == null)
            return null;
        String sRed = Integer.toHexString(c.getRed());
        if (sRed.length() == 1)
            sRed = "0" + sRed;
        String sGreen = Integer.toHexString(c.getGreen());
        if (sGreen.length() == 1)
            sGreen = "0" + sGreen;
        String sBlue = Integer.toHexString(c.getBlue());
        if (sBlue.length() == 1)
            sBlue = "0" + sBlue;
        return "#" + sRed + sGreen + sBlue;
    }

    private void colorPanel_mouseClicked(MouseEvent e) {
        colorChooser.setColor(sColorPanel.getBackground());
        if (!colorChooserDialog.isVisible()) {
            colorChooserDialog.setVisible(true);
        }
    }

    protected JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        return label;
    }

}
