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

package jmetest.renderer;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;

public class ShadowTweaker extends JFrame {
    private JCheckBox enableTextureCheckBox;
    private JComboBox lPassSrcBlend;
    private JComboBox lPassDstBlend;
    private JComboBox tPassSrcBlend;
    private JComboBox tPassDstBlend;
    private static final long serialVersionUID = 1L;

    private ButtonGroup lmethodGroup = new ButtonGroup();

    private JRadioButton additiveRadioButton;

    private JRadioButton modulativeRadioButton;
    private static ShadowedRenderPass spass;

    public ShadowTweaker(ShadowedRenderPass pass) {
        super();
        spass = pass;
        getContentPane().setLayout(new GridBagLayout());
        setTitle("ShadowTweaker");
        setBounds(100, 100, 388, 443);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JLabel blendForLightLabel = new JLabel();
        blendForLightLabel.setText("Blend for Light Passes (S/D):");
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        getContentPane().add(blendForLightLabel, gridBagConstraints);

        SourceFunction[] srcBlendOptions = BlendState.SourceFunction.values();
        DestinationFunction[] dstBlendOptions = BlendState.DestinationFunction.values();

        lPassSrcBlend = new JComboBox(srcBlendOptions);
        lPassSrcBlend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (additiveRadioButton != null && additiveRadioButton.isSelected() && ShadowedRenderPass.blended != null)
                    ShadowedRenderPass.blended.setSourceFunction((SourceFunction) lPassSrcBlend.getSelectedItem());
                else if (modulativeRadioButton != null && modulativeRadioButton.isSelected() && ShadowedRenderPass.modblended != null)
                    ShadowedRenderPass.modblended.setSourceFunction((SourceFunction) lPassSrcBlend.getSelectedItem());
            }
        });
        lPassSrcBlend.setFont(new Font("Arial", Font.PLAIN, 8));
        final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_1.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints_1.weightx = 1;
        gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_1.gridwidth = 2;
        gridBagConstraints_1.gridy = 1;
        gridBagConstraints_1.gridx = 0;
        getContentPane().add(lPassSrcBlend, gridBagConstraints_1);

        lPassDstBlend = new JComboBox(dstBlendOptions);
        lPassDstBlend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (additiveRadioButton != null && additiveRadioButton.isSelected() && ShadowedRenderPass.blended != null)
                    ShadowedRenderPass.blended.setDestinationFunction((DestinationFunction) lPassDstBlend.getSelectedItem());
                else if (modulativeRadioButton != null && modulativeRadioButton.isSelected() && ShadowedRenderPass.modblended != null)
                    ShadowedRenderPass.modblended.setDestinationFunction((DestinationFunction) lPassDstBlend.getSelectedItem());
            }
        });
        lPassDstBlend.setFont(new Font("Arial", Font.PLAIN, 8));
        final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_3.gridwidth = 2;
        gridBagConstraints_3.gridy = 3;
        gridBagConstraints_3.gridx = 0;
        getContentPane().add(lPassDstBlend, gridBagConstraints_3);

        final JLabel blendForTextureLabel = new JLabel();
        blendForTextureLabel.setText("Blend for Texture Pass (S/D):");
        final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
        gridBagConstraints_8.gridwidth = 2;
        gridBagConstraints_8.insets = new Insets(10, 10, 0, 10);
        gridBagConstraints_8.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_8.gridy = 4;
        gridBagConstraints_8.gridx = 0;
        getContentPane().add(blendForTextureLabel, gridBagConstraints_8);

        tPassSrcBlend = new JComboBox(srcBlendOptions);
        tPassSrcBlend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ShadowedRenderPass.blendTex != null)
                    ShadowedRenderPass.blendTex.setSourceFunction((SourceFunction) tPassSrcBlend.getSelectedItem());
            }
        });
        tPassSrcBlend.setFont(new Font("Arial", Font.PLAIN, 8));
        final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
        gridBagConstraints_9.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_9.gridwidth = 2;
        gridBagConstraints_9.gridy = 5;
        gridBagConstraints_9.gridx = 0;
        getContentPane().add(tPassSrcBlend, gridBagConstraints_9);

        tPassDstBlend = new JComboBox(dstBlendOptions);
        tPassDstBlend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ShadowedRenderPass.blendTex != null)
                    ShadowedRenderPass.blendTex.setDestinationFunction((DestinationFunction) tPassDstBlend.getSelectedItem());
            }
        });
        tPassDstBlend.setFont(new Font("Arial", Font.PLAIN, 8));
        final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
        gridBagConstraints_10.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_10.gridwidth = 2;
        gridBagConstraints_10.gridy = 6;
        gridBagConstraints_10.gridx = 0;
        getContentPane().add(tPassDstBlend, gridBagConstraints_10);

        final JCheckBox enableShadowsCheckBox = new JCheckBox();
        enableShadowsCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spass.setRenderShadows(enableShadowsCheckBox.isSelected());
            }
        });
        enableShadowsCheckBox.setSelected(true);
        enableShadowsCheckBox.setText("Enable Shadows");
        final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
        gridBagConstraints_4.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints_4.insets = new Insets(10, 10, 0, 10);
        gridBagConstraints_4.gridy = 7;
        gridBagConstraints_4.gridx = 0;
        getContentPane().add(enableShadowsCheckBox, gridBagConstraints_4);

        enableTextureCheckBox = new JCheckBox();
        enableTextureCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ShadowedRenderPass.rTexture = enableTextureCheckBox.isSelected();
            }
        });
        enableTextureCheckBox.setSelected(true);
        enableTextureCheckBox.setText("Enable Texture");
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints_2.gridy = 7;
        gridBagConstraints_2.gridx = 1;
        getContentPane().add(enableTextureCheckBox, gridBagConstraints_2);

        final JLabel methodLabel = new JLabel();
        methodLabel.setText("Lighting Method:");
        final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
        gridBagConstraints_7.insets = new Insets(4, 10, 0, 10);
        gridBagConstraints_7.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_7.gridy = 8;
        gridBagConstraints_7.gridx = 0;
        getContentPane().add(methodLabel, gridBagConstraints_7);

        additiveRadioButton = new JRadioButton();
        additiveRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLMode();
            }
        });
        lmethodGroup.add(additiveRadioButton);
        additiveRadioButton.setSelected(true);
        additiveRadioButton.setText("ADDITIVE");
        final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
        gridBagConstraints_5.weightx = .5;
        gridBagConstraints_5.gridy = 9;
        gridBagConstraints_5.gridx = 0;
        getContentPane().add(additiveRadioButton, gridBagConstraints_5);

        modulativeRadioButton = new JRadioButton();
        modulativeRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLMode();
            }
        });
        lmethodGroup.add(modulativeRadioButton);
        modulativeRadioButton.setText("MODULATIVE");
        final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
        gridBagConstraints_6.weightx = .5;
        gridBagConstraints_6.gridy = 9;
        gridBagConstraints_6.gridx = 1;
        getContentPane().add(modulativeRadioButton, gridBagConstraints_6);
        setLMode();
    }

    public void setLMode() {
        if (additiveRadioButton.isSelected()) {
            spass.setLightingMethod(ShadowedRenderPass.LightingMethod.Additive);
            lPassDstBlend.setSelectedItem(BlendState.DestinationFunction.One);
            lPassSrcBlend.setSelectedItem(BlendState.SourceFunction.DestinationColor);
            tPassDstBlend.setSelectedItem(BlendState.DestinationFunction.Zero);
            tPassSrcBlend.setSelectedItem(BlendState.SourceFunction.DestinationColor);
            enableTextureCheckBox.setText("Enable Texture Pass");
        }
        else {
            spass.setLightingMethod(ShadowedRenderPass.LightingMethod.Modulative);
            lPassDstBlend.setSelectedItem(BlendState.DestinationFunction.OneMinusSourceAlpha);
            lPassSrcBlend.setSelectedItem(BlendState.SourceFunction.DestinationColor);
            enableTextureCheckBox.setText("Enable Dark Pass");
        }
    }
    
}
