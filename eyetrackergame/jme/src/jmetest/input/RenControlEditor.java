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

package jmetest.input;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jme.bounding.BoundingBox;
import com.jme.input.ChaseCamera;
import com.jme.input.InputSystem;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.ThirdPersonHandler;
import com.jme.input.thirdperson.ThirdPersonMouseLook;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jmex.awt.input.AWTKeyInput;
import com.jmex.awt.input.AWTMouseInput;
import com.jmex.awt.lwjgl.LWJGLAWTCanvasConstructor;

public class RenControlEditor extends JFrame {
    private static final Logger logger = Logger
            .getLogger(RenControlEditor.class.getName());
    
    private JCheckBox strafeAlignTargetBox;
    private static final long serialVersionUID = 1L;
    private static final Dimension MIN_DIMENSION = new Dimension(400, 300);

    private JTextField scaleZField;
    private JTextField scaleYField;
    private JTextField minDistanceField;
    private JTextField moveSpeedField;
    private JTextField camSpeedField;
    private JTextField scaleXField;
    private JComboBox targetTypeCB;
    private JComboBox landTypeCB;
    private JTextArea codeArea;
    private ButtonGroup offsetGroup = new ButtonGroup();
    private JTextField offsetRatioField;
    private JTextField offsetYField;
    private JTextField offsetZField;
    private JTextField offsetXField;
    private JTextField maxDistanceField;
    private JTextField polarField;
    private JTextField radiusField;
    private JCheckBox maintainSpringRatioBox;
    private JCheckBox gradualTurnsCheckBox;
    private JCheckBox lockBackwardsCheckBox;
    private JCheckBox stayBehindTargetBox;
    private JCheckBox turnWithCameraBox;
    private JCheckBox rotateOnlyBox;
    private JCheckBox enableMouseLookBox;
    private JList examplesList;
    private JRadioButton alignCameraRadio;
    private JRadioButton alignTargetRadio;
    private ButtonGroup alignmentGroup = new ButtonGroup();
    private JTextField turnSpeedField;
    private JTextField springKField;
    private JTextField dampingKField;
    private JTextField maxZoomField;
    private JTextField minZoomField;
    private JTextField turnWithCamSpeedField;
    private JTextField accelHorizontalField;
    private JTextField accelVerticalField;
    private JTextField accelZoomField;
    private JCheckBox enableSpringsCheckBox;
    private JRadioButton offsetRelativeRadio;
    private JRadioButton offsetAbsRadio;
    private JCheckBox invertControlCheckBox;
    private JCheckBox lockPolarBox;
    private JSlider maxAscentSlider;
    private JSlider minAscentSlider;
    private JButton applyExampleButton;
    
    private Canvas glCanvas;
    private int width = 640, height = 480;
    private ControlImplementor impl;
    private HashMap<String, Object> keys = new HashMap<String, Object>();

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Toolkit.getDefaultToolkit().setDynamicLayout(true);
            JFrame.setDefaultLookAndFeelDecorated(true);
            new RenControlEditor();
        } catch (Exception e) {
            logger.logp(Level.SEVERE, RenControlEditor.class.toString(), "main(args)", "Exception", e);
        }
    }

    public RenControlEditor() {
        setTitle("RenControlEditor - 3rd Person  v. 1.0 rc1");
        setBounds(100, 100, 760, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String vers = System.getProperty("os.name").toLowerCase();
        boolean isMac = false;
        if (vers.indexOf("mac") != -1) {
            isMac = true;
        }

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        final JPanel testPanel = new JPanel();
        testPanel.setPreferredSize(new Dimension(50, 50));
        testPanel.setMinimumSize(new Dimension(100, 100));
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_2.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_2.fill = GridBagConstraints.BOTH;
        gridBagConstraints_2.weighty = 1.0;
        gridBagConstraints_2.weightx = .7;
        gridBagConstraints_2.gridx = 0;
        gridBagConstraints_2.gridy = 0;
        mainPanel.add(testPanel, gridBagConstraints_2);
        testPanel.setLayout(new BorderLayout());
        testPanel.add(getGlCanvas(), BorderLayout.CENTER);
        testPanel.setBorder(new TitledBorder(null, "Quick Test Here",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));

        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.LIGHT_GRAY);
        tabbedPane.setForeground(Color.BLACK);
        tabbedPane.setTabPlacement(SwingConstants.RIGHT);
        final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_3.fill = GridBagConstraints.BOTH;
        gridBagConstraints_3.weightx = .3;
        gridBagConstraints_3.weighty = 1.0;
        gridBagConstraints_3.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_3.gridx = 1;
        gridBagConstraints_3.gridy = 0;
        mainPanel.add(tabbedPane, gridBagConstraints_3);
        tabbedPane.setMinimumSize(new Dimension(200, 100));
        tabbedPane.setPreferredSize(new Dimension(200, 100));
        tabbedPane.setBorder(new TitledBorder(null, "Settings",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));

        final JPanel camPanel = new JPanel();
        camPanel.setOpaque(false);
        camPanel.setLayout(new GridBagLayout());
        if (!isMac) { // we need to use vertical label
            VTextIcon icon = new VTextIcon(tabbedPane, "Camera");
            tabbedPane.addTab(null, icon, camPanel, null);
        } else
            tabbedPane.addTab("Camera", null, camPanel, null);
        
        final JPanel mousePanel = new JPanel();
        mousePanel.setOpaque(false);
        mousePanel.setLayout(new GridBagLayout());
        if (!isMac) { // we need to use vertical label
            VTextIcon icon = new VTextIcon(tabbedPane, "Mouse");
            tabbedPane.addTab(null, icon, mousePanel, null);
        } else
            tabbedPane.addTab("Mouse", null, mousePanel, null);

        final JPanel mouseSubPanel2 = new JPanel();
        mouseSubPanel2.setOpaque(false);
        mouseSubPanel2.setBorder(new TitledBorder(null, "Vertical Control",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        mouseSubPanel2.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
        gridBagConstraints_14.weightx = 1.0;
        gridBagConstraints_14.fill = GridBagConstraints.BOTH;
        gridBagConstraints_14.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_14.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_14.gridx = 0;
        gridBagConstraints_14.gridy = 1;
        mousePanel.add(mouseSubPanel2, gridBagConstraints_14);

        final JLabel maxAscentLabel = new JLabel();
        maxAscentLabel.setText("Max Ascent:");
        final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
        gridBagConstraints_4.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_4.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_4.gridx = 0;
        gridBagConstraints_4.gridy = 0;
        mouseSubPanel2.add(maxAscentLabel, gridBagConstraints_4);

        final JLabel ascentValueLabel = new JLabel();
        ascentValueLabel.setText("30 deg");
        final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
        gridBagConstraints_8.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_8.insets = new Insets(4, 2, 0, 0);
        gridBagConstraints_8.gridy = 0;
        gridBagConstraints_8.gridx = 2;
        mouseSubPanel2.add(ascentValueLabel, gridBagConstraints_8);

        maxAscentSlider = new JSlider();
        maxAscentSlider
                .setToolTipText("Maximum angle from the ground that the camera can rise.");
        maxAscentSlider.setOpaque(false);
        maxAscentSlider.setMinorTickSpacing(15);
        maxAscentSlider.setValue(30);
        maxAscentSlider.setMajorTickSpacing(30);
        maxAscentSlider.setMinimum(-90);
        maxAscentSlider.setMaximum(90);
        maxAscentSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int val = maxAscentSlider.getValue();
                ascentValueLabel.setText(val + " deg");
                impl.chaser.getMouseLook().setMaxAscent(
                        FastMath.DEG_TO_RAD * val);
                updateCode();
            }
        });
        final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
        gridBagConstraints_5.weightx = 1;
        gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_5.insets = new Insets(0, 2, 0, 0);
        gridBagConstraints_5.gridy = 0;
        gridBagConstraints_5.gridx = 1;
        mouseSubPanel2.add(maxAscentSlider, gridBagConstraints_5);


        final JLabel minAscentLabel = new JLabel();
        minAscentLabel.setText("Min Ascent:");
        final GridBagConstraints gridBagConstraints_4b = new GridBagConstraints();
        gridBagConstraints_4b.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_4b.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_4b.gridx = 0;
        gridBagConstraints_4b.gridy = 1;
        mouseSubPanel2.add(minAscentLabel, gridBagConstraints_4b);

        final JLabel ascentValueLabel2 = new JLabel();
        ascentValueLabel2.setText("-15 deg");
        final GridBagConstraints gridBagConstraints_8b = new GridBagConstraints();
        gridBagConstraints_8b.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_8b.insets = new Insets(4, 2, 0, 0);
        gridBagConstraints_8b.gridy = 1;
        gridBagConstraints_8b.gridx = 2;
        mouseSubPanel2.add(ascentValueLabel2, gridBagConstraints_8b);


        minAscentSlider = new JSlider();
        minAscentSlider
                .setToolTipText("Maximum angle below level that the camera can fall.");
        minAscentSlider.setOpaque(false);
        minAscentSlider.setMinorTickSpacing(15);
        minAscentSlider.setValue(-15);
        minAscentSlider.setMajorTickSpacing(30);
        minAscentSlider.setMinimum(-90);
        minAscentSlider.setMaximum(90);
        minAscentSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int val = minAscentSlider.getValue();
                ascentValueLabel2.setText(val + " deg");
                impl.chaser.getMouseLook().setMinAscent(
                        FastMath.DEG_TO_RAD * val);
                updateCode();
            }
        });

        final GridBagConstraints gridBagConstraints_5b = new GridBagConstraints();
        gridBagConstraints_5b.weightx = 1;
        gridBagConstraints_5b.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_5b.insets = new Insets(0, 2, 0, 0);
        gridBagConstraints_5b.gridy = 1;
        gridBagConstraints_5b.gridx = 1;
        mouseSubPanel2.add(minAscentSlider, gridBagConstraints_5b);

        final JLabel invertedControlLabel = new JLabel();
        invertedControlLabel.setText("Inverted Control:");
        final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
        gridBagConstraints_10.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_10.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_10.gridy = 2;
        gridBagConstraints_10.gridx = 0;
        mouseSubPanel2.add(invertedControlLabel, gridBagConstraints_10);

        invertControlCheckBox = new JCheckBox();
        invertControlCheckBox
                .setToolTipText("Invert the direction the camera moves when the mouse goes up/down.");
        invertControlCheckBox
                .setSelected(ThirdPersonMouseLook.DEFAULT_INVERTEDY);
        invertControlCheckBox.setMargin(new Insets(0, 0, 0, 0));
        invertControlCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                impl.chaser.getMouseLook().setInvertedY(
                        invertControlCheckBox.isSelected());
                updateCode();
            }
        });
        invertControlCheckBox.setOpaque(false);
        final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
        gridBagConstraints_9.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_9.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_9.gridy = 2;
        gridBagConstraints_9.gridx = 1;
        mouseSubPanel2.add(invertControlCheckBox, gridBagConstraints_9);

        final JPanel mouseSubPanel3 = new JPanel();
        mouseSubPanel3.setOpaque(false);
        mouseSubPanel3.setBorder(new TitledBorder(null,
                "Zoom Control (scrollwheel)",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        mouseSubPanel3.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
        gridBagConstraints_15.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_15.fill = GridBagConstraints.BOTH;
        gridBagConstraints_15.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_15.weightx = 1.0;
        gridBagConstraints_15.gridy = 2;
        gridBagConstraints_15.gridx = 0;
        mousePanel.add(mouseSubPanel3, gridBagConstraints_15);

        final JLabel minRolloutLabel = new JLabel();
        minRolloutLabel.setText("Min:");
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        mouseSubPanel3.add(minRolloutLabel, gridBagConstraints);

        minZoomField = new JTextField();
        minZoomField
                .setToolTipText("Minimum radius between camera and target (controlled by mousewheel)");
        minZoomField.setHorizontalAlignment(SwingConstants.RIGHT);
        minZoomField.setText("" + ThirdPersonMouseLook.DEFAULT_MINROLLOUT);
        addExpandedNumericVerifier(minZoomField);
        minZoomField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                String val = minZoomField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val);
                } catch (NumberFormatException nfe) {
                    return;
                }

                impl.chaser.getMouseLook().setMinRollOut(fval);
                updateCode();
            }
        });
        minZoomField.setColumns(5);
        final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
        gridBagConstraints_19.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_19.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_19.gridy = 0;
        gridBagConstraints_19.gridx = 1;
        mouseSubPanel3.add(minZoomField, gridBagConstraints_19);

        final JLabel maxRolloutLabel = new JLabel();
        maxRolloutLabel.setText("Max:");
        final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.insets = new Insets(4, 8, 0, 0);
        gridBagConstraints_1.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_1.gridy = 0;
        gridBagConstraints_1.gridx = 2;
        mouseSubPanel3.add(maxRolloutLabel, gridBagConstraints_1);

        maxZoomField = new JTextField();
        maxZoomField
                .setToolTipText("Maximum radius between camera and target (controlled by mousewheel)");
        maxZoomField.setHorizontalAlignment(SwingConstants.RIGHT);
        maxZoomField.setText("" + ThirdPersonMouseLook.DEFAULT_MAXROLLOUT);
        addExpandedNumericVerifier(maxZoomField);
        maxZoomField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                String val = maxZoomField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val);
                } catch (NumberFormatException nfe) {
                    return;
                }
                impl.chaser.getMouseLook().setMaxRollOut(fval);
                updateCode();
            }
        });
        maxZoomField.setColumns(5);
        final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
        gridBagConstraints_20.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_20.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_20.gridy = 0;
        gridBagConstraints_20.gridx = 3;
        mouseSubPanel3.add(maxZoomField, gridBagConstraints_20);

        final JPanel mouseSubPanel4 = new JPanel();
        mouseSubPanel4.setBorder(new TitledBorder(null, "Acceleration",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        mouseSubPanel4.setOpaque(false);
        mouseSubPanel4.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
        gridBagConstraints_12.weightx = 1.0;
        gridBagConstraints_12.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_12.fill = GridBagConstraints.BOTH;
        gridBagConstraints_12.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_12.gridy = 3;
        gridBagConstraints_12.gridx = 0;
        mousePanel.add(mouseSubPanel4, gridBagConstraints_12);

        final JLabel baseSpeedLabel = new JLabel();
        baseSpeedLabel.setText("Base Speed:");
        final GridBagConstraints gridBagConstraints_79 = new GridBagConstraints();
        gridBagConstraints_79.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_79.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_79.gridx = 0;
        gridBagConstraints_79.gridy = 0;
        mouseSubPanel4.add(baseSpeedLabel, gridBagConstraints_79);

        camSpeedField = new JTextField();
        camSpeedField.setToolTipText("Base mouse motion speed");
        camSpeedField.setHorizontalAlignment(SwingConstants.RIGHT);
        camSpeedField.setText("1.0");
        addExpandedNumericVerifier(camSpeedField);
        camSpeedField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                String val = camSpeedField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val);
                } catch (NumberFormatException nfe) {
                    return;
                }
                impl.chaser.setActionSpeed(fval);
                updateCode();
            }
        });
        camSpeedField.setColumns(5);
        final GridBagConstraints gridBagConstraints_80 = new GridBagConstraints();
        gridBagConstraints_80.insets = new Insets(2, 4, 10, 0);
        gridBagConstraints_80.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_80.gridy = 0;
        gridBagConstraints_80.gridx = 1;
        mouseSubPanel4.add(camSpeedField, gridBagConstraints_80);

        final JLabel accelHorizontalLabel = new JLabel();
        accelHorizontalLabel.setText("Horizontal:");
        final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
        gridBagConstraints_13.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_13.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_13.gridy = 1;
        gridBagConstraints_13.gridx = 0;
        mouseSubPanel4.add(accelHorizontalLabel, gridBagConstraints_13);

        accelHorizontalField = new JTextField();
        accelHorizontalField
                .setToolTipText("acceleration to apply to speed in the horizontal direction");
        accelHorizontalField.setHorizontalAlignment(SwingConstants.RIGHT);
        accelHorizontalField.setText(""
                + ThirdPersonMouseLook.DEFAULT_MOUSEXMULT);
        addExpandedNumericVerifier(accelHorizontalField);
        accelHorizontalField.getDocument().addDocumentListener(
                new DocumentAdapter() {
                    public void update() {
                        String val = accelHorizontalField.getText();
                        float fval = 0;
                        try {
                            fval = Float.parseFloat(val);
                        } catch (NumberFormatException nfe) {
                            return;
                        }
                        impl.chaser.getMouseLook().setMouseXMultiplier(fval);
                        updateCode();
                    }
                });
        accelHorizontalField.setColumns(5);
        final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
        gridBagConstraints_18.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_18.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_18.gridy = 1;
        gridBagConstraints_18.gridx = 1;
        mouseSubPanel4.add(accelHorizontalField, gridBagConstraints_18);

        final JLabel accelVerticalLabel = new JLabel();
        accelVerticalLabel.setText("Vertical:");
        final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
        gridBagConstraints_16.insets = new Insets(4, 8, 0, 0);
        gridBagConstraints_16.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_16.gridy = 1;
        gridBagConstraints_16.gridx = 2;
        mouseSubPanel4.add(accelVerticalLabel, gridBagConstraints_16);

        accelVerticalField = new JTextField();
        accelVerticalField
                .setToolTipText("acceleration to apply to speed in the vertical direction");
        accelVerticalField.setHorizontalAlignment(SwingConstants.RIGHT);
        accelVerticalField
                .setText("" + ThirdPersonMouseLook.DEFAULT_MOUSEYMULT);
        addExpandedNumericVerifier(accelVerticalField);
        accelVerticalField.getDocument().addDocumentListener(
                new DocumentAdapter() {
                    public void update() {
                        String val = accelVerticalField.getText();
                        float fval = 0;
                        try {
                            fval = Float.parseFloat(val);
                        } catch (NumberFormatException nfe) {
                            return;
                        }

                        impl.chaser.getMouseLook().setMouseYMultiplier(fval);
                        updateCode();
                    }
                });
        accelVerticalField.setColumns(5);
        final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
        gridBagConstraints_7.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_7.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_7.gridy = 1;
        gridBagConstraints_7.gridx = 3;
        mouseSubPanel4.add(accelVerticalField, gridBagConstraints_7);

        final JLabel accelZoomLabel = new JLabel();
        accelZoomLabel.setText("Zoom:");
        final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
        gridBagConstraints_17.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_17.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_17.gridy = 2;
        gridBagConstraints_17.gridx = 0;
        mouseSubPanel4.add(accelZoomLabel, gridBagConstraints_17);

        accelZoomField = new JTextField();
        accelZoomField
                .setToolTipText("acceleration to apply to speed during zooming");
        accelZoomField.setHorizontalAlignment(SwingConstants.RIGHT);
        accelZoomField.setText("" + ThirdPersonMouseLook.DEFAULT_MOUSEROLLMULT);
        addExpandedNumericVerifier(accelZoomField);
        accelZoomField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                String val = accelZoomField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val);
                } catch (NumberFormatException nfe) {
                    return;
                }

                impl.chaser.getMouseLook().setMouseRollMultiplier(fval);
                updateCode();
            }
        });
        accelZoomField.setColumns(5);
        final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
        gridBagConstraints_6.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_6.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_6.gridy = 2;
        gridBagConstraints_6.gridx = 1;
        mouseSubPanel4.add(accelZoomField, gridBagConstraints_6);

        final JPanel camSubPanel1 = new JPanel();
        camSubPanel1.setBorder(new TitledBorder(null, "Initial Positioning",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        camSubPanel1.setOpaque(false);
        camSubPanel1.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_27 = new GridBagConstraints();
        gridBagConstraints_27.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_27.fill = GridBagConstraints.BOTH;
        gridBagConstraints_27.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_27.weightx = 1;
        gridBagConstraints_27.gridy = 0;
        gridBagConstraints_27.gridx = 0;
        camPanel.add(camSubPanel1, gridBagConstraints_27);

        final JLabel radiusLabel = new JLabel();
        radiusLabel.setText("Radius:");
        final GridBagConstraints gridBagConstraints_46 = new GridBagConstraints();
        gridBagConstraints_46.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_46.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_46.gridy = 0;
        gridBagConstraints_46.gridx = 0;
        camSubPanel1.add(radiusLabel, gridBagConstraints_46);

        radiusField = new JTextField();
        radiusField
                .setToolTipText("Initial \"ideal\" distance to start camera at");
        radiusField.setHorizontalAlignment(SwingConstants.RIGHT);
        radiusField.setText("100");
        addExpandedNumericVerifier(radiusField);
        radiusField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                String val = radiusField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val);
                } catch (NumberFormatException nfe) {
                    return;
                }

                impl.chaser.getIdealSphereCoords().x = fval;
                updateCode();
            }
        });
        radiusField.setColumns(5);
        final GridBagConstraints gridBagConstraints_47 = new GridBagConstraints();
        gridBagConstraints_47.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_47.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_47.gridy = 0;
        gridBagConstraints_47.gridx = 1;
        camSubPanel1.add(radiusField, gridBagConstraints_47);

        final JLabel polarLabel = new JLabel();
        polarLabel.setText("Polar Angle:");
        final GridBagConstraints gridBagConstraints_53 = new GridBagConstraints();
        gridBagConstraints_53.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_53.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_53.gridy = 2;
        gridBagConstraints_53.gridx = 0;
        camSubPanel1.add(polarLabel, gridBagConstraints_53);

        polarField = new JTextField();
        polarField
                .setToolTipText("Initial \"ideal\" angle from ground to start camera at");
        polarField.setHorizontalAlignment(SwingConstants.RIGHT);
        polarField.setText("30");
        addExpandedNumericVerifier(polarField);
        polarField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                String val = polarField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val);
                } catch (NumberFormatException nfe) {
                    return;
                }

                impl.chaser.getIdealSphereCoords().z = FastMath.DEG_TO_RAD
                        * fval;
                updateCode();
            }
        });
        polarField.setColumns(5);
        final GridBagConstraints gridBagConstraints_52 = new GridBagConstraints();
        gridBagConstraints_52.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_52.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_52.gridy = 2;
        gridBagConstraints_52.gridx = 1;
        camSubPanel1.add(polarField, gridBagConstraints_52);

        final JLabel degreesLabel2 = new JLabel();
        final GridBagConstraints gridBagConstraints_50 = new GridBagConstraints();
        gridBagConstraints_50.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_50.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_50.gridy = 2;
        gridBagConstraints_50.gridx = 2;
        camSubPanel1.add(degreesLabel2, gridBagConstraints_50);
        degreesLabel2.setText("degrees");

        lockPolarBox = new JCheckBox();
        lockPolarBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                impl.chaser.getMouseLook().setLockAscent(
                        lockPolarBox.isSelected());
                updateCode();
            }
        });
        lockPolarBox
                .setToolTipText("Lock the Camera's height to the initial Polar Angle.");
        lockPolarBox.setMargin(new Insets(0, 0, 0, 0));
        lockPolarBox.setOpaque(false);
        lockPolarBox.setText("Lock Polar Height");
        final GridBagConstraints gridBagConstraints_54 = new GridBagConstraints();
        gridBagConstraints_54.insets = new Insets(2, 4, 0, 4);
        gridBagConstraints_54.gridwidth = 3;
        gridBagConstraints_54.gridy = 3;
        gridBagConstraints_54.gridx = 0;
        camSubPanel1.add(lockPolarBox, gridBagConstraints_54);

        final JPanel camSubPanel2 = new JPanel();
        camSubPanel2.setBorder(new TitledBorder(null, "Target",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        camSubPanel2.setLayout(new GridBagLayout());
        camSubPanel2.setOpaque(false);
        final GridBagConstraints gridBagConstraints_92 = new GridBagConstraints();
        gridBagConstraints_92.insets = new Insets(4, 4, 4, 0);
        gridBagConstraints_92.fill = GridBagConstraints.BOTH;
        gridBagConstraints_92.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_92.gridy = 1;
        gridBagConstraints_92.gridx = 0;
        camPanel.add(camSubPanel2, gridBagConstraints_92);

        stayBehindTargetBox = new JCheckBox();
        stayBehindTargetBox
                .setToolTipText("Keep camera's ideal position behind target facing direction.");
        stayBehindTargetBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                impl.chaser.setStayBehindTarget(stayBehindTargetBox
                        .isSelected());
                updateCode();
            }
        });
        stayBehindTargetBox.setOpaque(false);
        stayBehindTargetBox.setMargin(new Insets(0, 0, 0, 0));
        stayBehindTargetBox.setText("Stay Behind Target");
        final GridBagConstraints gridBagConstraints_93 = new GridBagConstraints();
        gridBagConstraints_93.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_93.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_93.gridy = 0;
        gridBagConstraints_93.gridx = 0;
        gridBagConstraints_93.gridwidth = 2;
        camSubPanel2.add(stayBehindTargetBox, gridBagConstraints_93);

        turnWithCameraBox = new JCheckBox();
        turnWithCameraBox
                .setToolTipText("Keep camera's ideal position behind target facing direction.");
        turnWithCameraBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                impl.chaser.getMouseLook().setRotateTarget(turnWithCameraBox
                        .isSelected());
                turnWithCamSpeedField.setEnabled(turnWithCameraBox
                        .isSelected());
                updateCode();
            }
        });
        turnWithCameraBox.setOpaque(false);
        turnWithCameraBox.setMargin(new Insets(0, 0, 0, 0));
        turnWithCameraBox.setText("Turn Target With Camera");
        final GridBagConstraints gridBagConstraints_93b = new GridBagConstraints();
        gridBagConstraints_93b.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_93b.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_93b.gridy = 1;
        gridBagConstraints_93b.gridx = 0;
        gridBagConstraints_93b.gridwidth = 2;
        camSubPanel2.add(turnWithCameraBox, gridBagConstraints_93b);

        final JLabel targetTurnLabel = new JLabel();
        targetTurnLabel.setText("Target Turn Speed:");
        final GridBagConstraints gridBagConstraintsb = new GridBagConstraints();
        gridBagConstraintsb.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraintsb.insets = new Insets(4, 4, 0, 0);
        gridBagConstraintsb.gridy = 2;
        gridBagConstraintsb.gridx = 0;
        camSubPanel2.add(targetTurnLabel, gridBagConstraintsb);

        turnWithCamSpeedField = new JTextField();
        turnWithCamSpeedField.setEnabled(false);
        turnWithCamSpeedField
                .setToolTipText("Speed (in degrees) that target turns towards camera facing direction.");
        turnWithCamSpeedField.setHorizontalAlignment(SwingConstants.RIGHT);
        turnWithCamSpeedField.setText("" + ThirdPersonMouseLook.DEFAULT_TARGETTURNSPEED * FastMath.RAD_TO_DEG);
        addExpandedNumericVerifier(turnWithCamSpeedField);
        turnWithCamSpeedField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                String val = turnWithCamSpeedField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val) * FastMath.DEG_TO_RAD;
                } catch (NumberFormatException nfe) {
                    return;
                }

                impl.chaser.getMouseLook().setMinRollOut(fval);
                updateCode();
            }
        });
        turnWithCamSpeedField.setColumns(5);
        final GridBagConstraints gridBagConstraints_93c = new GridBagConstraints();
        gridBagConstraints_93c.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_93c.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_93c.gridy = 2;
        gridBagConstraints_93c.gridx = 1;
        camSubPanel2.add(turnWithCamSpeedField, gridBagConstraints_93c);

        final JLabel camSpacerLabel = new JLabel();
        final GridBagConstraints gridBagConstraints_94 = new GridBagConstraints();
        gridBagConstraints_94.weighty = 1;
        gridBagConstraints_94.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_94.fill = GridBagConstraints.BOTH;
        gridBagConstraints_94.gridy = 6;
        gridBagConstraints_94.gridx = 0;
        camPanel.add(camSpacerLabel, gridBagConstraints_94);

        final JLabel mouseSpacerLabel = new JLabel();
        final GridBagConstraints gridBagConstraints_38 = new GridBagConstraints();
        gridBagConstraints_38.weighty = 1;
        gridBagConstraints_38.fill = GridBagConstraints.BOTH;
        gridBagConstraints_38.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_38.gridy = 5;
        gridBagConstraints_38.gridx = 0;
        mousePanel.add(mouseSpacerLabel, gridBagConstraints_38);

        final JPanel mouseSubPanel1 = new JPanel();
        mouseSubPanel1.setBorder(new TitledBorder(null, "Enabled",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        mouseSubPanel1.setOpaque(false);
        mouseSubPanel1.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_89 = new GridBagConstraints();
        gridBagConstraints_89.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_89.fill = GridBagConstraints.BOTH;
        gridBagConstraints_89.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_89.gridx = 0;
        gridBagConstraints_89.gridy = 0;
        mousePanel.add(mouseSubPanel1, gridBagConstraints_89);

        enableMouseLookBox = new JCheckBox();
        enableMouseLookBox
                .setToolTipText("Enable mouse interaction with the camera");
        enableMouseLookBox.setSelected(true);
        enableMouseLookBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean enabled = enableMouseLookBox.isSelected();
                impl.chaser.getMouseLook().setEnabled(enabled);

                maxAscentSlider.setEnabled(enabled);
                minAscentSlider.setEnabled(enabled);
                invertControlCheckBox.setEnabled(enabled);
                minZoomField.setEnabled(enabled);
                maxZoomField.setEnabled(enabled);
                camSpeedField.setEnabled(enabled);
                accelHorizontalField.setEnabled(enabled);
                accelVerticalField.setEnabled(enabled);
                accelZoomField.setEnabled(enabled);

                updateCode();
            }
        });
        enableMouseLookBox.setMargin(new Insets(0, 0, 0, 0));
        enableMouseLookBox.setOpaque(false);
        enableMouseLookBox.setText("Enable MouseLook");
        final GridBagConstraints gridBagConstraints_90 = new GridBagConstraints();
        gridBagConstraints_90.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_90.gridy = 0;
        gridBagConstraints_90.gridx = 0;
        mouseSubPanel1.add(enableMouseLookBox, gridBagConstraints_90);

        final JPanel springPanel = new JPanel();
        springPanel.setLayout(new GridBagLayout());
        springPanel.setOpaque(false);
        if (!isMac) { // we need to use vertical label
            VTextIcon icon = new VTextIcon(tabbedPane, "Springs");
            tabbedPane.addTab(null, icon, springPanel, null);
        } else
            tabbedPane.addTab("Springs", null, springPanel, null);

        final JPanel sprintSubPanel1 = new JPanel();
        sprintSubPanel1.setBorder(new TitledBorder(null, "Enabled",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        sprintSubPanel1.setOpaque(false);
        sprintSubPanel1.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_35 = new GridBagConstraints();
        gridBagConstraints_35.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_35.fill = GridBagConstraints.BOTH;
        gridBagConstraints_35.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_35.weightx = 1;
        gridBagConstraints_35.gridx = 0;
        gridBagConstraints_35.gridy = 0;
        springPanel.add(sprintSubPanel1, gridBagConstraints_35);

        enableSpringsCheckBox = new JCheckBox();
        enableSpringsCheckBox
                .setToolTipText("Enable spring controlled movement of the camera (more fluid and lifelike)");
        enableSpringsCheckBox.setMargin(new Insets(0, 0, 0, 0));
        enableSpringsCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean enable = enableSpringsCheckBox.isSelected();
                springKField.setEnabled(enable);
                dampingKField.setEnabled(enable);
                maintainSpringRatioBox.setEnabled(enable);
                maxDistanceField.setEnabled(enable);
                minDistanceField.setEnabled(enable);
                impl.chaser.setEnableSpring(enable);
                updateCode();
            }
        });
        enableSpringsCheckBox.setSelected(true);
        enableSpringsCheckBox.setOpaque(false);
        enableSpringsCheckBox.setText("Enable Springs");
        final GridBagConstraints gridBagConstraints_36 = new GridBagConstraints();
        gridBagConstraints_36.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_36.gridy = 0;
        gridBagConstraints_36.gridx = 0;
        sprintSubPanel1.add(enableSpringsCheckBox, gridBagConstraints_36);

        final JPanel springSubPanel2 = new JPanel();
        springSubPanel2.setOpaque(false);
        springSubPanel2.setLayout(new GridBagLayout());
        springSubPanel2.setBorder(new TitledBorder(null, "Spring Coefficients",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        final GridBagConstraints gridBagConstraints_21 = new GridBagConstraints();
        gridBagConstraints_21.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_21.fill = GridBagConstraints.BOTH;
        gridBagConstraints_21.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_21.weightx = 1;
        gridBagConstraints_21.gridy = 1;
        gridBagConstraints_21.gridx = 0;
        springPanel.add(springSubPanel2, gridBagConstraints_21);

        maintainSpringRatioBox = new JCheckBox();
        maintainSpringRatioBox
                .setToolTipText("Keep constant values at a ratio where the camera will stop asap without ocillating.");
        maintainSpringRatioBox.setMargin(new Insets(0, 0, 0, 0));
        maintainSpringRatioBox.setOpaque(false);
        maintainSpringRatioBox.setSelected(true);
        maintainSpringRatioBox.setText("Maintain Critically Damped Ratio");
        final GridBagConstraints gridBagConstraints_26 = new GridBagConstraints();
        gridBagConstraints_26.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_26.gridwidth = 3;
        gridBagConstraints_26.gridx = 0;
        gridBagConstraints_26.gridy = 0;
        springSubPanel2.add(maintainSpringRatioBox, gridBagConstraints_26);

        final JLabel dampingKLabel = new JLabel();
        dampingKLabel.setText("Damping K:");
        final GridBagConstraints gridBagConstraints_22 = new GridBagConstraints();
        gridBagConstraints_22.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_22.gridx = 0;
        gridBagConstraints_22.gridy = 1;
        gridBagConstraints_22.insets = new Insets(4, 4, 0, 0);
        springSubPanel2.add(dampingKLabel, gridBagConstraints_22);

        dampingKField = new JTextField();
        dampingKField
                .setToolTipText("constant affecting the damping of the spring's power");
        dampingKField.setText("" + ChaseCamera.DEFAULT_DAMPINGK);
        dampingKField.setHorizontalAlignment(SwingConstants.RIGHT);
        addExpandedNumericVerifier(dampingKField);
        dampingKField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                if (!dampingKField.hasFocus())
                    return;
                String val = dampingKField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val);
                } catch (NumberFormatException nfe) {
                    return;
                }

                if (maintainSpringRatioBox.isSelected()) {
                    float sK = (fval * fval) * .25f;
                    springKField.setText("" + sK);
                    impl.chaser.setSpringK(sK);
                }
                impl.chaser.setDampingK(fval);
                updateCode();
            }
        });
        dampingKField.setColumns(6);
        final GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
        gridBagConstraints_23.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_23.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_23.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_23.gridy = 1;
        gridBagConstraints_23.gridx = 1;
        springSubPanel2.add(dampingKField, gridBagConstraints_23);

        final JLabel springKLabel = new JLabel();
        springKLabel.setText("Spring K:");
        final GridBagConstraints gridBagConstraints_24 = new GridBagConstraints();
        gridBagConstraints_24.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_24.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_24.gridy = 2;
        gridBagConstraints_24.gridx = 0;
        springSubPanel2.add(springKLabel, gridBagConstraints_24);

        springKField = new JTextField();
        springKField
                .setToolTipText("constant affecting the power of the spring");
        springKField.setText("" + ChaseCamera.DEFAULT_SPRINGK);
        springKField.setHorizontalAlignment(SwingConstants.RIGHT);
        addExpandedNumericVerifier(springKField);
        springKField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                if (!springKField.hasFocus())
                    return;
                String val = springKField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val);
                } catch (NumberFormatException nfe) {
                    return;
                }
                if (maintainSpringRatioBox.isSelected()) {
                    float dK = 2 * FastMath.sqrt(fval);
                    dampingKField.setText("" + dK);
                    impl.chaser.setDampingK(dK);
                }
                impl.chaser.setSpringK(fval);
                updateCode();
            }
        });
        springKField.setColumns(6);
        final GridBagConstraints gridBagConstraints_25 = new GridBagConstraints();
        gridBagConstraints_25.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_25.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_25.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_25.gridy = 2;
        gridBagConstraints_25.gridx = 1;
        springSubPanel2.add(springKField, gridBagConstraints_25);

        final JPanel springSubPanel3 = new JPanel();
        springSubPanel3.setBorder(new TitledBorder(null, "Limits",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        springSubPanel3.setOpaque(false);
        springSubPanel3.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_55 = new GridBagConstraints();
        gridBagConstraints_55.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_55.fill = GridBagConstraints.BOTH;
        gridBagConstraints_55.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_55.gridy = 2;
        gridBagConstraints_55.gridx = 0;
        springPanel.add(springSubPanel3, gridBagConstraints_55);

        final JLabel maxDistanceLabel = new JLabel();
        maxDistanceLabel.setText("Max Spring Length:");
        final GridBagConstraints gridBagConstraints_56 = new GridBagConstraints();
        gridBagConstraints_56.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_56.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_56.gridy = 0;
        gridBagConstraints_56.gridx = 0;
        springSubPanel3.add(maxDistanceLabel, gridBagConstraints_56);

        maxDistanceField = new JTextField();
        maxDistanceField
                .setToolTipText("at what distance to force camera to maintain distance regardless of spring");
        maxDistanceField.setText("0");
        maxDistanceField.setHorizontalAlignment(SwingConstants.RIGHT);
        addExpandedNumericVerifier(maxDistanceField);
        maxDistanceField.getDocument().addDocumentListener(
                new DocumentAdapter() {
                    public void update() {
                        String val = maxDistanceField.getText();
                        float fval = 0;
                        try {
                            fval = Float.parseFloat(val);
                        } catch (NumberFormatException nfe) {
                            return;
                        }
                        impl.chaser.setMaxDistance(fval);
                        updateCode();
                    }
                });
        maxDistanceField.setColumns(5);
        final GridBagConstraints gridBagConstraints_57 = new GridBagConstraints();
        gridBagConstraints_57.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_57.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_57.gridy = 0;
        gridBagConstraints_57.gridx = 1;
        springSubPanel3.add(maxDistanceField, gridBagConstraints_57);

        final JLabel limitLabel1 = new JLabel();
        limitLabel1.setFont(new Font("", Font.PLAIN, 10));
        limitLabel1.setText("(0 - no limit)");
        final GridBagConstraints gridBagConstraints_58 = new GridBagConstraints();
        gridBagConstraints_58.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_58.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_58.gridy = 0;
        gridBagConstraints_58.gridx = 2;
        springSubPanel3.add(limitLabel1, gridBagConstraints_58);

        final JLabel minSpringLengthLabel = new JLabel();
        minSpringLengthLabel.setText("Min Spring Length:");
        final GridBagConstraints gridBagConstraints_48 = new GridBagConstraints();
        gridBagConstraints_48.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_48.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_48.gridy = 1;
        gridBagConstraints_48.gridx = 0;
        springSubPanel3.add(minSpringLengthLabel, gridBagConstraints_48);

        minDistanceField = new JTextField();
        minDistanceField
                .setToolTipText("at what distance to push away camera to maintain distance regardless of spring");
        minDistanceField.setHorizontalAlignment(SwingConstants.RIGHT);
        minDistanceField.setText("0");
        addExpandedNumericVerifier(minDistanceField);
        minDistanceField.getDocument().addDocumentListener(
                new DocumentAdapter() {
                    public void update() {
                        String val = minDistanceField.getText();
                        float fval = 0;
                        try {
                            fval = Float.parseFloat(val);
                        } catch (NumberFormatException nfe) {
                            return;
                        }
                        impl.chaser.setMinDistance(fval);
                        updateCode();
                    }
                });
        minDistanceField.setColumns(5);
        final GridBagConstraints gridBagConstraints_49 = new GridBagConstraints();
        gridBagConstraints_49.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_49.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_49.gridy = 1;
        gridBagConstraints_49.gridx = 1;
        springSubPanel3.add(minDistanceField, gridBagConstraints_49);

        final JLabel limitLabel2 = new JLabel();
        limitLabel2.setFont(new Font("", Font.PLAIN, 10));
        limitLabel2.setText("(0 - no limit)");
        final GridBagConstraints gridBagConstraints_51 = new GridBagConstraints();
        gridBagConstraints_51.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_51.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_51.gridy = 1;
        gridBagConstraints_51.gridx = 2;
        springSubPanel3.add(limitLabel2, gridBagConstraints_51);

        final JLabel springSpacerLabel = new JLabel();
        final GridBagConstraints gridBagConstraints_37 = new GridBagConstraints();
        gridBagConstraints_37.fill = GridBagConstraints.BOTH;
        gridBagConstraints_37.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_37.weighty = 1;
        gridBagConstraints_37.gridy = 3;
        gridBagConstraints_37.gridx = 0;
        springPanel.add(springSpacerLabel, gridBagConstraints_37);

        final JPanel movePanel = new JPanel();
        movePanel.setLayout(new GridBagLayout());
        movePanel.setOpaque(false);
        if (!isMac) { // we need to use vertical label
            VTextIcon icon = new VTextIcon(tabbedPane, "Movement");
            tabbedPane.addTab(null, icon, movePanel, null);
        } else
            tabbedPane.addTab("Movement", null, movePanel, null);

        final JPanel moveSubPanel2 = new JPanel();
        moveSubPanel2.setBorder(new TitledBorder(null, "Target Turning",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        moveSubPanel2.setOpaque(false);
        moveSubPanel2.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_29 = new GridBagConstraints();
        gridBagConstraints_29.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_29.fill = GridBagConstraints.BOTH;
        gridBagConstraints_29.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_29.weightx = 1;
        gridBagConstraints_29.gridy = 1;
        gridBagConstraints_29.gridx = 0;
        movePanel.add(moveSubPanel2, gridBagConstraints_29);

        final JLabel turnSpeedLabel = new JLabel();
        turnSpeedLabel.setText("Turn Speed:");
        final GridBagConstraints gridBagConstraints_30 = new GridBagConstraints();
        gridBagConstraints_30.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_30.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_30.gridy = 2;
        gridBagConstraints_30.gridx = 0;
        moveSubPanel2.add(turnSpeedLabel, gridBagConstraints_30);

        turnSpeedField = new JTextField();
        turnSpeedField.setHorizontalAlignment(SwingConstants.RIGHT);
        turnSpeedField.setToolTipText("radians per second to turn");
        turnSpeedField.setText("3.1415");
        addExpandedNumericVerifier(turnSpeedField);
        turnSpeedField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                String val = turnSpeedField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val);
                } catch (NumberFormatException nfe) {
                    return;
                }

                impl.input.setTurnSpeed(fval);
                updateCode();
            }
        });
        turnSpeedField.setColumns(5);
        final GridBagConstraints gridBagConstraints_31 = new GridBagConstraints();
        gridBagConstraints_31.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_31.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_31.gridy = 2;
        gridBagConstraints_31.gridx = 1;
        moveSubPanel2.add(turnSpeedField, gridBagConstraints_31);

        rotateOnlyBox = new JCheckBox();
        rotateOnlyBox
                .setToolTipText("Rotate in place if only left/right keys are held. (otherwise, moves forward while turning)");
        rotateOnlyBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                impl.input.setRotateOnly(rotateOnlyBox.isSelected());
                updateCode();
            }
        });
        rotateOnlyBox.setMargin(new Insets(0, 0, 0, 0));
        rotateOnlyBox.setOpaque(false);
        rotateOnlyBox.setAutoscrolls(true);
        rotateOnlyBox.setText("Rotate In Place");
        final GridBagConstraints gridBagConstraints_91 = new GridBagConstraints();
        gridBagConstraints_91.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_91.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_91.gridwidth = 2;
        gridBagConstraints_91.gridx = 0;
        gridBagConstraints_91.gridy = 0;
        moveSubPanel2.add(rotateOnlyBox, gridBagConstraints_91);

        gradualTurnsCheckBox = new JCheckBox();
        gradualTurnsCheckBox
                .setToolTipText("turn gradually to face indicated direction (else turn is immediate)");
        gradualTurnsCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                impl.input.setDoGradualRotation(gradualTurnsCheckBox
                        .isSelected());
                turnSpeedField.setEnabled(gradualTurnsCheckBox.isSelected());
                updateCode();
            }
        });
        gradualTurnsCheckBox.setSelected(true);
        gradualTurnsCheckBox.setMargin(new Insets(0, 0, 0, 0));
        gradualTurnsCheckBox.setOpaque(false);
        gradualTurnsCheckBox.setText("Gradual Turns");
        final GridBagConstraints gridBagConstraints_32 = new GridBagConstraints();
        gridBagConstraints_32.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_32.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_32.gridwidth = 2;
        gridBagConstraints_32.gridy = 1;
        gridBagConstraints_32.gridx = 0;
        moveSubPanel2.add(gradualTurnsCheckBox, gridBagConstraints_32);

        lockBackwardsCheckBox = new JCheckBox();
        lockBackwardsCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                impl.input.setLockBackwards(lockBackwardsCheckBox.isSelected());
                updateCode();
            }
        });
        lockBackwardsCheckBox.setMargin(new Insets(0, 0, 0, 0));
        lockBackwardsCheckBox
                .setToolTipText("Don't turn around target to face backwards when backing up.");
        lockBackwardsCheckBox.setOpaque(false);
        lockBackwardsCheckBox.setText("Lock Backwards Motion");
        final GridBagConstraints gridBagConstraints_33 = new GridBagConstraints();
        gridBagConstraints_33.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_33.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_33.gridwidth = 2;
        gridBagConstraints_33.gridy = 3;
        gridBagConstraints_33.gridx = 0;
        moveSubPanel2.add(lockBackwardsCheckBox, gridBagConstraints_33);

        final JPanel moveSubPanel3 = new JPanel();
        moveSubPanel3.setOpaque(false);
        moveSubPanel3.setBorder(new TitledBorder(null, "Movement Alignment",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        moveSubPanel3.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_34 = new GridBagConstraints();
        gridBagConstraints_34.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_34.fill = GridBagConstraints.BOTH;
        gridBagConstraints_34.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_34.weightx = 1;
        gridBagConstraints_34.gridy = 2;
        gridBagConstraints_34.gridx = 0;
        movePanel.add(moveSubPanel3, gridBagConstraints_34);

        alignCameraRadio = new JRadioButton();
        alignCameraRadio
                .setToolTipText("movements are in relation to the direction the camera is facing");
        alignCameraRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                impl.input.setCameraAlignedMovement(true);
                lockBackwardsCheckBox.setEnabled(true);
                updateCode();
            }
        });
        alignCameraRadio.setMargin(new Insets(0, 0, 0, 0));
        alignmentGroup.add(alignCameraRadio);
        alignCameraRadio.setOpaque(false);
        alignCameraRadio.setSelected(true);
        alignCameraRadio.setText("Camera Aligned");
        final GridBagConstraints gridBagConstraints_40 = new GridBagConstraints();
        gridBagConstraints_40.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_40.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_40.gridy = 0;
        gridBagConstraints_40.gridx = 0;
        moveSubPanel3.add(alignCameraRadio, gridBagConstraints_40);

        alignTargetRadio = new JRadioButton();
        alignTargetRadio
                .setToolTipText("movements are in relation to the direction the target is currently facing");
        alignTargetRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                impl.input.setCameraAlignedMovement(false);
                impl.input.setLockBackwards(true);
                lockBackwardsCheckBox.setSelected(true);
                lockBackwardsCheckBox.setEnabled(false);
                updateCode();
            }
        });
        alignTargetRadio.setMargin(new Insets(0, 0, 0, 0));
        alignmentGroup.add(alignTargetRadio);
        alignTargetRadio.setOpaque(false);
        alignTargetRadio.setText("Target Aligned");
        final GridBagConstraints gridBagConstraints_41 = new GridBagConstraints();
        gridBagConstraints_41.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_41.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_41.gridy = 0;
        gridBagConstraints_41.gridx = 1;
        moveSubPanel3.add(alignTargetRadio, gridBagConstraints_41);

        strafeAlignTargetBox = new JCheckBox();
        strafeAlignTargetBox.setToolTipText("force strafe movements to be target aligned");
        strafeAlignTargetBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                impl.input.setStrafeAlignTarget(strafeAlignTargetBox
                        .isSelected());
                updateCode();
            }
        });
        strafeAlignTargetBox.setOpaque(false);
        strafeAlignTargetBox.setText("Strafe is always Target Aligned");
        final GridBagConstraints gridBagConstraints_104 = new GridBagConstraints();
        gridBagConstraints_104.gridwidth = 2;
        gridBagConstraints_104.gridy = 1;
        gridBagConstraints_104.gridx = 0;
        moveSubPanel3.add(strafeAlignTargetBox, gridBagConstraints_104);

        final JPanel moveSubPanel1 = new JPanel();
        moveSubPanel1.setBorder(new TitledBorder(null, "Speed",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        moveSubPanel1.setOpaque(false);
        moveSubPanel1.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_81 = new GridBagConstraints();
        gridBagConstraints_81.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_81.weightx = 1;
        gridBagConstraints_81.fill = GridBagConstraints.BOTH;
        gridBagConstraints_81.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_81.gridy = 0;
        gridBagConstraints_81.gridx = 0;
        movePanel.add(moveSubPanel1, gridBagConstraints_81);

        final JLabel movementSpeedLabel = new JLabel();
        movementSpeedLabel.setText("Movement Speed:");
        final GridBagConstraints gridBagConstraints_82 = new GridBagConstraints();
        gridBagConstraints_82.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_82.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_82.gridy = 0;
        gridBagConstraints_82.gridx = 0;
        moveSubPanel1.add(movementSpeedLabel, gridBagConstraints_82);

        moveSpeedField = new JTextField();
        moveSpeedField.setToolTipText("base acceleration for target movement");
        moveSpeedField.setHorizontalAlignment(SwingConstants.RIGHT);
        moveSpeedField.setText("100");
        addExpandedNumericVerifier(moveSpeedField);
        moveSpeedField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                String val = moveSpeedField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val);
                } catch (NumberFormatException nfe) {
                    return;
                }

                impl.input.setActionSpeed(fval);
                updateCode();
            }
        });
        moveSpeedField.setColumns(5);
        final GridBagConstraints gridBagConstraints_83 = new GridBagConstraints();
        gridBagConstraints_83.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_83.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_83.gridy = 0;
        gridBagConstraints_83.gridx = 1;
        moveSubPanel1.add(moveSpeedField, gridBagConstraints_83);

        final JPanel moveSubPanel4 = new JPanel();
        moveSubPanel4.setLayout(new GridBagLayout());
        moveSubPanel4.setBorder(new TitledBorder(null, "Keys",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        moveSubPanel4.setOpaque(false);
        final GridBagConstraints gridBagConstraints_95 = new GridBagConstraints();
        gridBagConstraints_95.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_95.fill = GridBagConstraints.BOTH;
        gridBagConstraints_95.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_95.gridy = 3;
        gridBagConstraints_95.gridx = 0;
        movePanel.add(moveSubPanel4, gridBagConstraints_95);

        final JLabel movementKeysLabel = new JLabel();
        movementKeysLabel.setText("Movement Keys:");
        final GridBagConstraints gridBagConstraints_103 = new GridBagConstraints();
        gridBagConstraints_103.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_103.gridwidth = 3;
        gridBagConstraints_103.gridx = 0;
        gridBagConstraints_103.gridy = 0;
        moveSubPanel4.add(movementKeysLabel, gridBagConstraints_103);

        final JButton forwardKeyButton = new JButton();
        addKeyDialog(forwardKeyButton, ThirdPersonHandler.PROP_KEY_FORWARD);
        forwardKeyButton.setMargin(new Insets(2, 10, 2, 10));
        forwardKeyButton.setText("W");
        forwardKeyButton.setToolTipText("forward key");
        keys.put(ThirdPersonHandler.PROP_KEY_FORWARD, "KEY_W");
        final GridBagConstraints gridBagConstraints_96 = new GridBagConstraints();
        gridBagConstraints_96.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_96.fill = GridBagConstraints.BOTH;
        gridBagConstraints_96.gridy = 1;
        gridBagConstraints_96.gridx = 1;
        moveSubPanel4.add(forwardKeyButton, gridBagConstraints_96);

        final JButton leftKeyButton = new JButton();
        addKeyDialog(leftKeyButton, ThirdPersonHandler.PROP_KEY_LEFT);
        leftKeyButton.setMargin(new Insets(2, 10, 2, 10));
        leftKeyButton.setText("A");
        leftKeyButton.setToolTipText("left key");
        keys.put(ThirdPersonHandler.PROP_KEY_LEFT, "KEY_A");
        final GridBagConstraints gridBagConstraints_97 = new GridBagConstraints();
        gridBagConstraints_97.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_97.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_97.fill = GridBagConstraints.BOTH;
        gridBagConstraints_97.gridy = 2;
        gridBagConstraints_97.gridx = 0;
        moveSubPanel4.add(leftKeyButton, gridBagConstraints_97);

        final JButton backKeyButton = new JButton();
        addKeyDialog(backKeyButton, ThirdPersonHandler.PROP_KEY_BACKWARD);
        keys.put(ThirdPersonHandler.PROP_KEY_BACKWARD, "KEY_S");
        backKeyButton.setMargin(new Insets(2, 10, 2, 10));
        backKeyButton.setText("S");
        backKeyButton.setToolTipText("backward key");
        final GridBagConstraints gridBagConstraints_99 = new GridBagConstraints();
        gridBagConstraints_99.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_99.fill = GridBagConstraints.BOTH;
        gridBagConstraints_99.gridy = 2;
        gridBagConstraints_99.gridx = 1;
        moveSubPanel4.add(backKeyButton, gridBagConstraints_99);

        final JButton rightKeyButton = new JButton();
        addKeyDialog(rightKeyButton, ThirdPersonHandler.PROP_KEY_RIGHT);
        keys.put(ThirdPersonHandler.PROP_KEY_RIGHT, "KEY_D");
        rightKeyButton.setText("D");
        rightKeyButton.setToolTipText("right key");
        rightKeyButton.setMargin(new Insets(2, 10, 2, 10));
        final GridBagConstraints gridBagConstraints_98 = new GridBagConstraints();
        gridBagConstraints_98.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_98.fill = GridBagConstraints.BOTH;
        gridBagConstraints_98.gridy = 2;
        gridBagConstraints_98.gridx = 2;
        moveSubPanel4.add(rightKeyButton, gridBagConstraints_98);

        final JLabel strafeKeysLabel = new JLabel();
        strafeKeysLabel.setText("Strafe Keys:");
        final GridBagConstraints gridBagConstraints_102 = new GridBagConstraints();
        gridBagConstraints_102.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_102.gridwidth = 3;
        gridBagConstraints_102.gridy = 3;
        gridBagConstraints_102.gridx = 0;
        moveSubPanel4.add(strafeKeysLabel, gridBagConstraints_102);

        final JButton leftStrafeButton = new JButton();
        addKeyDialog(leftStrafeButton, ThirdPersonHandler.PROP_KEY_STRAFELEFT);
        keys.put(ThirdPersonHandler.PROP_KEY_STRAFELEFT, "KEY_Q");
        leftStrafeButton.setText("Q");
        leftStrafeButton.setToolTipText("strafe left key");
        leftStrafeButton.setMargin(new Insets(2, 10, 2, 10));
        final GridBagConstraints gridBagConstraints_100 = new GridBagConstraints();
        gridBagConstraints_100.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_100.fill = GridBagConstraints.BOTH;
        gridBagConstraints_100.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_100.gridy = 4;
        gridBagConstraints_100.gridx = 0;
        moveSubPanel4.add(leftStrafeButton, gridBagConstraints_100);

        final JButton rightStrafeButton = new JButton();
        addKeyDialog(rightStrafeButton, ThirdPersonHandler.PROP_KEY_STRAFERIGHT);
        keys.put(ThirdPersonHandler.PROP_KEY_STRAFERIGHT, "KEY_E");
        rightStrafeButton.setText("E");
        rightStrafeButton.setToolTipText("strafe right key");
        rightStrafeButton.setMargin(new Insets(2, 10, 2, 10));
        final GridBagConstraints gridBagConstraints_101 = new GridBagConstraints();
        gridBagConstraints_101.fill = GridBagConstraints.BOTH;
        gridBagConstraints_101.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_101.gridy = 4;
        gridBagConstraints_101.gridx = 2;
        moveSubPanel4.add(rightStrafeButton, gridBagConstraints_101);

        final JLabel moveSpacerLabel = new JLabel();
        final GridBagConstraints gridBagConstraints_39 = new GridBagConstraints();
        gridBagConstraints_39.fill = GridBagConstraints.BOTH;
        gridBagConstraints_39.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_39.weighty = 1;
        gridBagConstraints_39.gridy = 4;
        gridBagConstraints_39.gridx = 0;
        movePanel.add(moveSpacerLabel, gridBagConstraints_39);

        final JPanel trackingPanel = new JPanel();
        trackingPanel.setLayout(new GridBagLayout());
        trackingPanel.setOpaque(false);
        if (!isMac) { // we need to use vertical label
            VTextIcon icon = new VTextIcon(tabbedPane, "Tracking");
            tabbedPane.addTab(null, icon, trackingPanel, null);
        } else
            tabbedPane.addTab("Tracking", null, trackingPanel, null);

        final JPanel trackSubPanel1 = new JPanel();
        trackSubPanel1.setBorder(new TitledBorder(null, "View Offset",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        trackSubPanel1.setOpaque(false);
        trackSubPanel1.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_59 = new GridBagConstraints();
        gridBagConstraints_59.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_59.fill = GridBagConstraints.BOTH;
        gridBagConstraints_59.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_59.weightx = 1;
        gridBagConstraints_59.gridy = 0;
        gridBagConstraints_59.gridx = 0;
        trackingPanel.add(trackSubPanel1, gridBagConstraints_59);

        offsetRelativeRadio = new JRadioButton();
        offsetRelativeRadio
                .setToolTipText("lock view onto a point defined by a ratio of target height");
        offsetRelativeRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                offsetRatioField.setEnabled(true);
                offsetXField.setEnabled(false);
                offsetYField.setEnabled(false);
                offsetZField.setEnabled(false);
                updateCode();
            }
        });
        offsetRelativeRadio.setSelected(true);
        offsetGroup.add(offsetRelativeRadio);
        offsetRelativeRadio.setMargin(new Insets(0, 0, 0, 0));
        offsetRelativeRadio.setOpaque(false);
        offsetRelativeRadio.setText("By Height Ratio (of bounds)");
        final GridBagConstraints gridBagConstraints_65 = new GridBagConstraints();
        gridBagConstraints_65.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_65.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_65.gridwidth = 3;
        gridBagConstraints_65.gridy = 0;
        gridBagConstraints_65.gridx = 0;
        trackSubPanel1.add(offsetRelativeRadio, gridBagConstraints_65);

        DocumentAdapter offsetAdapter = new DocumentAdapter() {
            public void update() {
                updateOffset();
                updateCode();
            }
        };

        offsetRatioField = new JTextField();
        offsetRatioField.setToolTipText("height ratio to use");
        offsetRatioField.setText("1.5");
        addExpandedNumericVerifier(offsetRatioField);
        offsetRatioField.getDocument().addDocumentListener(offsetAdapter);
        offsetRatioField.setColumns(5);
        final GridBagConstraints gridBagConstraints_61 = new GridBagConstraints();
        gridBagConstraints_61.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_61.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_61.gridy = 1;
        gridBagConstraints_61.gridx = 0;
        trackSubPanel1.add(offsetRatioField, gridBagConstraints_61);

        offsetAbsRadio = new JRadioButton();
        offsetAbsRadio
                .setToolTipText("lock view onto a point defined as an offset of the current target location");
        offsetGroup.add(offsetAbsRadio);
        offsetAbsRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                offsetRatioField.setEnabled(false);
                offsetXField.setEnabled(true);
                offsetYField.setEnabled(true);
                offsetZField.setEnabled(true);
                updateCode();
            }
        });
        offsetAbsRadio.setMargin(new Insets(0, 0, 0, 0));
        final GridBagConstraints gridBagConstraints_66 = new GridBagConstraints();
        gridBagConstraints_66.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_66.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_66.gridwidth = 3;
        gridBagConstraints_66.gridy = 2;
        gridBagConstraints_66.gridx = 0;
        trackSubPanel1.add(offsetAbsRadio, gridBagConstraints_66);
        offsetAbsRadio.setOpaque(false);
        offsetAbsRadio.setText("Absolute Offset (x, y, z)");

        offsetXField = new JTextField();
        offsetXField.setToolTipText("X value of offset");
        offsetXField.setText("0.0");
        offsetXField.setEnabled(false);
        addExpandedNumericVerifier(offsetXField);
        offsetXField.getDocument().addDocumentListener(offsetAdapter);
        offsetXField.setColumns(5);
        final GridBagConstraints gridBagConstraints_62 = new GridBagConstraints();
        gridBagConstraints_62.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_62.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_62.gridy = 3;
        gridBagConstraints_62.gridx = 0;
        trackSubPanel1.add(offsetXField, gridBagConstraints_62);

        offsetYField = new JTextField();
        offsetYField.setToolTipText("Y value of offset");
        offsetYField.setText("0.0");
        offsetYField.setEnabled(false);
        addExpandedNumericVerifier(offsetYField);
        offsetYField.getDocument().addDocumentListener(offsetAdapter);
        offsetYField.setColumns(5);
        final GridBagConstraints gridBagConstraints_64 = new GridBagConstraints();
        gridBagConstraints_64.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_64.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_64.gridy = 3;
        gridBagConstraints_64.gridx = 1;
        trackSubPanel1.add(offsetYField, gridBagConstraints_64);

        offsetZField = new JTextField();
        offsetZField.setToolTipText("Z value of offset");
        offsetZField.setText("0.0");
        offsetZField.setEnabled(false);
        addExpandedNumericVerifier(offsetZField);
        offsetZField.getDocument().addDocumentListener(offsetAdapter);
        offsetZField.setColumns(5);
        final GridBagConstraints gridBagConstraints_63 = new GridBagConstraints();
        gridBagConstraints_63.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_63.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_63.gridy = 3;
        gridBagConstraints_63.gridx = 2;
        trackSubPanel1.add(offsetZField, gridBagConstraints_63);

        final JLabel trackSpacerLabel = new JLabel();
        final GridBagConstraints gridBagConstraints_60 = new GridBagConstraints();
        gridBagConstraints_60.fill = GridBagConstraints.BOTH;
        gridBagConstraints_60.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_60.weighty = 1;
        gridBagConstraints_60.gridy = 1;
        gridBagConstraints_60.gridx = 0;
        trackingPanel.add(trackSpacerLabel, gridBagConstraints_60);

        final JPanel scenePanel = new JPanel();
        scenePanel.setLayout(new GridBagLayout());
        scenePanel.setOpaque(false);
        if (!isMac) { // we need to use vertical label
            VTextIcon icon = new VTextIcon(tabbedPane, "Test Scene");
            tabbedPane.addTab(null, icon, scenePanel, null);
        } else
            tabbedPane.addTab("Test Scene", null, scenePanel, null);

        final JPanel sceneSubPanel1 = new JPanel();
        sceneSubPanel1.setBorder(new TitledBorder(null, "World",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        sceneSubPanel1.setLayout(new GridBagLayout());
        sceneSubPanel1.setOpaque(false);
        final GridBagConstraints gridBagConstraints_28 = new GridBagConstraints();
        gridBagConstraints_28.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_28.fill = GridBagConstraints.BOTH;
        gridBagConstraints_28.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_28.weightx = 1;
        gridBagConstraints_28.gridy = 0;
        gridBagConstraints_28.gridx = 0;
        scenePanel.add(sceneSubPanel1, gridBagConstraints_28);

        final JLabel landscapeLabel = new JLabel();
        landscapeLabel.setText("Landscape:");
        final GridBagConstraints gridBagConstraints_73 = new GridBagConstraints();
        gridBagConstraints_73.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_73.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_73.gridy = 0;
        gridBagConstraints_73.gridx = 0;
        sceneSubPanel1.add(landscapeLabel, gridBagConstraints_73);

        landTypeCB = new JComboBox(new String[] { "Random Terrain" });
        landTypeCB.setToolTipText("Landscape of world to use in demo");
        final GridBagConstraints gridBagConstraints_74 = new GridBagConstraints();
        gridBagConstraints_74.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_74.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_74.gridy = 0;
        gridBagConstraints_74.gridx = 1;
        sceneSubPanel1.add(landTypeCB, gridBagConstraints_74);

        final JPanel sceneSubPanel2 = new JPanel();
        sceneSubPanel2.setBorder(new TitledBorder(null, "Target",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        sceneSubPanel2.setOpaque(false);
        sceneSubPanel2.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_67 = new GridBagConstraints();
        gridBagConstraints_67.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_67.fill = GridBagConstraints.BOTH;
        gridBagConstraints_67.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_67.weightx = 1;
        gridBagConstraints_67.gridy = 1;
        gridBagConstraints_67.gridx = 0;
        scenePanel.add(sceneSubPanel2, gridBagConstraints_67);

        final JLabel modelLabel = new JLabel();
        modelLabel.setText("Model:");
        final GridBagConstraints gridBagConstraints_75 = new GridBagConstraints();
        gridBagConstraints_75.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_75.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_75.gridy = 0;
        gridBagConstraints_75.gridx = 0;
        sceneSubPanel2.add(modelLabel, gridBagConstraints_75);

        targetTypeCB = new JComboBox(new String[] { "1 unit Box (.5,.5,.5)" });
        targetTypeCB.setToolTipText("Model to use as target in demo");
        final GridBagConstraints gridBagConstraints_76 = new GridBagConstraints();
        gridBagConstraints_76.gridwidth = 3;
        gridBagConstraints_76.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_76.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_76.gridy = 0;
        gridBagConstraints_76.gridx = 1;
        sceneSubPanel2.add(targetTypeCB, gridBagConstraints_76);

        final JLabel scaleLabel = new JLabel();
        scaleLabel.setText("Scale (x,y,z):");
        final GridBagConstraints gridBagConstraints_77 = new GridBagConstraints();
        gridBagConstraints_77.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_77.gridwidth = 6;
        gridBagConstraints_77.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_77.gridy = 1;
        gridBagConstraints_77.gridx = 0;
        sceneSubPanel2.add(scaleLabel, gridBagConstraints_77);

        scaleXField = new JTextField();
        scaleXField.setToolTipText("X scale of target model in demo");
        scaleXField.setHorizontalAlignment(SwingConstants.RIGHT);
        scaleXField.setText("10");
        addExpandedNumericVerifier(scaleXField);
        scaleXField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                String val = scaleXField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val);
                } catch (NumberFormatException nfe) {
                    return;
                }
                impl.target.getLocalScale().x = fval;
                updateOffset();
                updateCode();
            }
        });
        scaleXField.setColumns(5);
        final GridBagConstraints gridBagConstraints_78 = new GridBagConstraints();
        gridBagConstraints_78.gridwidth = 2;
        gridBagConstraints_78.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_78.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints_78.gridy = 2;
        gridBagConstraints_78.gridx = 0;
        sceneSubPanel2.add(scaleXField, gridBagConstraints_78);

        scaleYField = new JTextField();
        scaleYField.setToolTipText("Y scale of target model in demo");
        scaleYField.setHorizontalAlignment(SwingConstants.RIGHT);
        scaleYField.setText("10");
        addExpandedNumericVerifier(scaleYField);
        scaleYField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                String val = scaleYField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val);
                } catch (NumberFormatException nfe) {
                    return;
                }
                impl.target.getLocalScale().y = fval;
                impl.m_character.updateGeometricState(0, true);
                updateOffset();
                updateCode();
            }
        });
        scaleYField.setColumns(5);
        final GridBagConstraints gridBagConstraints_86 = new GridBagConstraints();
        gridBagConstraints_86.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_86.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_86.gridy = 2;
        gridBagConstraints_86.gridx = 2;
        sceneSubPanel2.add(scaleYField, gridBagConstraints_86);

        scaleZField = new JTextField();
        scaleZField.setToolTipText("Z scale of target model in demo");
        scaleZField.setHorizontalAlignment(SwingConstants.RIGHT);
        scaleZField.setText("10");
        addExpandedNumericVerifier(scaleZField);
        scaleZField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void update() {
                String val = scaleZField.getText();
                float fval = 0;
                try {
                    fval = Float.parseFloat(val);
                } catch (NumberFormatException nfe) {
                    return;
                }
                impl.target.getLocalScale().z = fval;
                updateOffset();
                updateCode();
            }
        });
        scaleZField.setColumns(5);
        final GridBagConstraints gridBagConstraints_87 = new GridBagConstraints();
        gridBagConstraints_87.insets = new Insets(2, 4, 0, 0);
        gridBagConstraints_87.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_87.gridy = 2;
        gridBagConstraints_87.gridx = 3;
        sceneSubPanel2.add(scaleZField, gridBagConstraints_87);

        final JLabel sceneSpacerLabel = new JLabel();
        final GridBagConstraints gridBagConstraints_68 = new GridBagConstraints();
        gridBagConstraints_68.fill = GridBagConstraints.BOTH;
        gridBagConstraints_68.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_68.weighty = 1;
        gridBagConstraints_68.gridy = 2;
        gridBagConstraints_68.gridx = 0;
        scenePanel.add(sceneSpacerLabel, gridBagConstraints_68);

        final JPanel examplesPanel = new JPanel();
        examplesPanel.setOpaque(false);
        examplesPanel.setLayout(new GridBagLayout());
        if (!isMac) { // we need to use vertical label
            VTextIcon icon = new VTextIcon(tabbedPane, "Examples");
            tabbedPane.addTab(null, icon, examplesPanel, null);
        } else
            tabbedPane.addTab("Examples", null, examplesPanel, null);

        final JLabel exampleLabel = new JLabel();
        exampleLabel.setText("Example Control Settings:");
        final GridBagConstraints gridBagConstraints_43 = new GridBagConstraints();
        gridBagConstraints_43.insets = new Insets(10, 4, 0, 4);
        gridBagConstraints_43.gridx = 0;
        gridBagConstraints_43.gridy = 0;
        examplesPanel.add(exampleLabel, gridBagConstraints_43);

        final JScrollPane examplesSP = new JScrollPane();
        final GridBagConstraints gridBagConstraints_42 = new GridBagConstraints();
        gridBagConstraints_42.weighty = .8;
        gridBagConstraints_42.weightx = 1;
        gridBagConstraints_42.fill = GridBagConstraints.BOTH;
        gridBagConstraints_42.insets = new Insets(4, 10, 0, 10);
        gridBagConstraints_42.gridy = 1;
        gridBagConstraints_42.gridx = 0;
        examplesPanel.add(examplesSP, gridBagConstraints_42);

        examplesList = new JList(new String[] {"Plumber 64", "Max Payne-ish"});
        examplesList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                applyExampleButton
                        .setEnabled(examplesList.getSelectedIndex() > -1);
            }
        });
        examplesSP.setViewportView(examplesList);

        applyExampleButton = new JButton();
        applyExampleButton.setEnabled(false);
        applyExampleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyExample(examplesList.getSelectedIndex());
            }
        });
        applyExampleButton.setText("Apply");
        final GridBagConstraints gridBagConstraints_45 = new GridBagConstraints();
        gridBagConstraints_45.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_45.gridy = 2;
        gridBagConstraints_45.gridx = 0;
        examplesPanel.add(applyExampleButton, gridBagConstraints_45);

        final JLabel exampleSpacerLabel = new JLabel();
        final GridBagConstraints gridBagConstraints_44 = new GridBagConstraints();
        gridBagConstraints_44.weightx = 1;
        gridBagConstraints_44.weighty = .2;
        gridBagConstraints_44.fill = GridBagConstraints.BOTH;
        gridBagConstraints_44.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_44.gridy = 3;
        gridBagConstraints_44.gridx = 0;
        examplesPanel.add(exampleSpacerLabel, gridBagConstraints_44);

        final JPanel codePanel = new JPanel();
        codePanel.setOpaque(false);
        codePanel.setLayout(new GridBagLayout());
        if (!isMac) { // we need to use vertical label
            VTextIcon icon = new VTextIcon(tabbedPane, "Code");
            tabbedPane.addTab(null, icon, codePanel, null);
        } else
            tabbedPane.addTab("Code", null, codePanel, null);

        final JButton copyButton = new JButton();
        copyButton.setToolTipText("Copy code to system clipboard.");
        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                codeArea.selectAll();
                codeArea.copy();
                codeArea.setCaretPosition(0);
            }
        });
        copyButton.setText("Copy");
        final GridBagConstraints gridBagConstraints_70 = new GridBagConstraints();
        gridBagConstraints_70.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_70.gridx = 0;
        gridBagConstraints_70.gridy = 2;
        codePanel.add(copyButton, gridBagConstraints_70);

        final JScrollPane codeSP = new JScrollPane();
        codeSP
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        final GridBagConstraints gridBagConstraints_69 = new GridBagConstraints();
        gridBagConstraints_69.insets = new Insets(4, 10, 0, 10);
        gridBagConstraints_69.fill = GridBagConstraints.BOTH;
        gridBagConstraints_69.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_69.weighty = .8;
        gridBagConstraints_69.weightx = 1;
        gridBagConstraints_69.gridy = 1;
        gridBagConstraints_69.gridx = 0;
        codePanel.add(codeSP, gridBagConstraints_69);

        codeArea = new JTextArea();
        codeArea.setFont(new java.awt.Font("Monospaced", 0, 10));
        codeArea.setText("");
        codeArea.setEditable(false);
        codeArea.setAutoscrolls(true);
        codeSP.setViewportView(codeArea);

        final JLabel codeSpacerLabel = new JLabel();
        final GridBagConstraints gridBagConstraints_71 = new GridBagConstraints();
        gridBagConstraints_71.fill = GridBagConstraints.BOTH;
        gridBagConstraints_71.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_71.weighty = .2;
        gridBagConstraints_71.gridy = 3;
        gridBagConstraints_71.gridx = 0;
        codePanel.add(codeSpacerLabel, gridBagConstraints_71);

        final JLabel codeLabel = new JLabel();
        codeLabel.setText("Current Handler Code:");
        final GridBagConstraints gridBagConstraints_72 = new GridBagConstraints();
        gridBagConstraints_72.insets = new Insets(10, 4, 0, 4);
        gridBagConstraints_72.gridx = 0;
        gridBagConstraints_72.gridy = 0;
        codePanel.add(codeLabel, gridBagConstraints_72);

        final JPanel statPanel = new JPanel();
        statPanel.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
        gridBagConstraints_11.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_11.gridwidth = 2;
        gridBagConstraints_11.weightx = 1.0;
        gridBagConstraints_11.fill = GridBagConstraints.BOTH;
        gridBagConstraints_11.gridx = 0;
        gridBagConstraints_11.gridy = 1;
        mainPanel.add(statPanel, gridBagConstraints_11);

        final JLabel infoLabel = new JLabel();
        infoLabel
                .setText("Click and drag in the Test area to control.  WASDQE controls target.");
        final GridBagConstraints gridBagConstraints_84 = new GridBagConstraints();
        gridBagConstraints_84.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_84.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_84.gridx = 0;
        gridBagConstraints_84.gridy = 0;
        statPanel.add(infoLabel, gridBagConstraints_84);

        final JLabel statSpacerLabel = new JLabel();
        final GridBagConstraints gridBagConstraints_85 = new GridBagConstraints();
        gridBagConstraints_85.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_85.fill = GridBagConstraints.BOTH;
        gridBagConstraints_85.weightx = 1;
        gridBagConstraints_85.gridy = 0;
        gridBagConstraints_85.gridx = 1;
        statPanel.add(statSpacerLabel, gridBagConstraints_85);

        final JScrollPane scrollPane = new JScrollPane(mainPanel);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // center the frame
        setLocationRelativeTo(null);
        // show frame
        setVisible(true);

        while (glCanvas == null || impl.startTime == 0)
            ;

        // force a resize to ensure proper canvas size.
        glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight() + 1);
        glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight() - 1);

        updateCode();
    }

    /**
     * <code>addKeyDialog</code>
     * 
     * @param button
     */
    private void addKeyDialog(final JButton button, final String keyProp) {
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                KeyInputDialog dialog = new KeyInputDialog(
                        RenControlEditor.this);
                dialog.setLocationRelativeTo(button);
                dialog.setVisible(true);
                int code = dialog.event.getKeyCode();
                button.setText(KeyEvent.getKeyText(code));
                KeyBindingManager keyboard = KeyBindingManager
                        .getKeyBindingManager();
                keyboard.set(keyProp, AWTKeyInput.toInputCode(code));
                keys.put(keyProp, AWTKeyInput.getKeyParam(code));
                updateCode();
            }
        });
    }

    private void updateFromImpl() {
        rotateOnlyBox.setSelected(impl.input.isRotateOnly());
        gradualTurnsCheckBox.setSelected(impl.input.isDoGradualRotation());
        turnSpeedField.setText(""+impl.input.getTurnSpeed());
        lockBackwardsCheckBox.setSelected(impl.input.isLockBackwards());
        if (impl.input.isCameraAlignedMovement())
            alignCameraRadio.setSelected(true);
        else
            alignTargetRadio.setSelected(true);
        strafeAlignTargetBox.setSelected(impl.input.isStrafeAlignTarget());

        moveSpeedField.setText(""+impl.input.getSpeed());

        enableSpringsCheckBox.setSelected(impl.chaser.isEnableSpring());
        dampingKField.setText(""+impl.chaser.getDampingK());
        springKField.setText(""+impl.chaser.getSpringK());
        maxDistanceField.setText(""+impl.chaser.getMaxDistance());
        minDistanceField.setText(""+impl.chaser.getMinDistance());

        radiusField.setText(""+impl.chaser.getIdealSphereCoords().x);
        polarField.setText(""+(impl.chaser.getIdealSphereCoords().z / FastMath.DEG_TO_RAD));

        stayBehindTargetBox.setSelected(impl.chaser.isStayBehindTarget());

        offsetXField.setText(""+impl.chaser.getTargetOffset().x);
        offsetYField.setText(""+impl.chaser.getTargetOffset().y);
        offsetZField.setText(""+impl.chaser.getTargetOffset().z);

        rotateOnlyBox.setSelected(impl.chaser.getMouseLook().isEnabled());
        maxAscentSlider.setValue((int)(impl.chaser.getMouseLook().getMaxAscent() / FastMath.DEG_TO_RAD));
        minAscentSlider.setValue((int)(impl.chaser.getMouseLook().getMinAscent() / FastMath.DEG_TO_RAD));
        invertControlCheckBox.setSelected(impl.chaser.getMouseLook().isInvertedY());
        turnWithCameraBox.setSelected(impl.chaser.getMouseLook().isRotateTarget());
        turnWithCamSpeedField.setText(""+impl.chaser.getMouseLook().getTargetTurnSpeed() / FastMath.DEG_TO_RAD);
        minZoomField.setText(""+impl.chaser.getMouseLook().getMinRollOut());
        maxZoomField.setText(""+impl.chaser.getMouseLook().getMaxRollOut());
        accelHorizontalField.setText(""+impl.chaser.getMouseLook().getMouseXMultiplier());
        accelVerticalField.setText(""+impl.chaser.getMouseLook().getMouseYMultiplier());
        accelZoomField.setText(""+impl.chaser.getMouseLook().getMouseRollMultiplier());
        lockPolarBox.setSelected(impl.chaser.getMouseLook().isLockAscent());
        
        camSpeedField.setText(""+impl.chaser.getSpeed());
        updateCode();
    }

    private void updateCode() {
        StringBuffer code = new StringBuffer();

        code.append("HashMap handlerProps = new HashMap();\n");
        code.append("handlerProps.put(ThirdPersonHandler.PROP_ROTATEONLY, \""
                + rotateOnlyBox.isSelected() + "\");\n");
        code.append("handlerProps.put(ThirdPersonHandler.PROP_DOGRADUAL, \""
                + gradualTurnsCheckBox.isSelected() + "\");\n");
        if (gradualTurnsCheckBox.isSelected())
            code.append("handlerProps.put(ThirdPersonHandler.PROP_TURNSPEED, \""
                            + turnSpeedField.getText() + "\");\n");
        code.append("handlerProps.put(ThirdPersonHandler.PROP_LOCKBACKWARDS, \""
                        + lockBackwardsCheckBox.isSelected() + "\");\n");
        code.append("handlerProps.put(ThirdPersonHandler.PROP_STRAFETARGETALIGN, \""
                + strafeAlignTargetBox.isSelected() + "\");\n");
        code.append("handlerProps.put(ThirdPersonHandler.PROP_CAMERAALIGNEDMOVE, \""
                        + alignCameraRadio.isSelected() + "\");\n\n");

        code.append("handlerProps.put(ThirdPersonHandler.PROP_KEY_FORWARD, \"\"+KeyInput."
                        + keys.get(ThirdPersonHandler.PROP_KEY_FORWARD)
                        + ");\n");
        code.append("handlerProps.put(ThirdPersonHandler.PROP_KEY_LEFT, \"\"+KeyInput."
                        + keys.get(ThirdPersonHandler.PROP_KEY_LEFT) + ");\n");
        code.append("handlerProps.put(ThirdPersonHandler.PROP_KEY_BACKWARD, \"\"+KeyInput."
                        + keys.get(ThirdPersonHandler.PROP_KEY_BACKWARD)
                        + ");\n");
        code.append("handlerProps.put(ThirdPersonHandler.PROP_KEY_RIGHT, \"\"+KeyInput."
                        + keys.get(ThirdPersonHandler.PROP_KEY_RIGHT) + ");\n");
        code.append("handlerProps.put(ThirdPersonHandler.PROP_KEY_STRAFELEFT, \"\"+KeyInput."
                        + keys.get(ThirdPersonHandler.PROP_KEY_STRAFELEFT)
                        + ");\n");
        code.append("handlerProps.put(ThirdPersonHandler.PROP_KEY_STRAFERIGHT, \"\"+KeyInput."
                        + keys.get(ThirdPersonHandler.PROP_KEY_STRAFERIGHT)
                        + ");\n");

        code.append("input = new ThirdPersonHandler(myTarget, cam, handlerProps);\n");
        code.append("input.setActionSpeed(" + moveSpeedField.getText()
                        + "f);\n");

        code.append("\nHashMap chaserProps = new HashMap();\n");
        code.append("chaserProps.put(ChaseCamera.PROP_ENABLESPRING, \""
                + enableSpringsCheckBox.isSelected() + "\");\n");
        if (enableSpringsCheckBox.isSelected()) {
            code.append("chaserProps.put(ChaseCamera.PROP_DAMPINGK, \""
                    + dampingKField.getText() + "\");\n");
            code.append("chaserProps.put(ChaseCamera.PROP_SPRINGK, \""
                    + springKField.getText() + "\");\n");
            code.append("chaserProps.put(ChaseCamera.PROP_MAXDISTANCE, \""
                    + maxDistanceField.getText() + "\");\n");
            code.append("chaserProps.put(ChaseCamera.PROP_MINDISTANCE, \""
                    + minDistanceField.getText() + "\");\n");
        }

        code.append("chaserProps.put(ChaseCamera.PROP_INITIALSPHERECOORDS, new Vector3f("
                        + radiusField.getText()
                        + "f, 0f, FastMath.DEG_TO_RAD * "
                        + polarField.getText() + "f));\n");
        code.append("chaserProps.put(ChaseCamera.PROP_STAYBEHINDTARGET, \""
                + stayBehindTargetBox.isSelected() + "\");\n");
        if (offsetRelativeRadio.isSelected())
            code.append("chaserProps.put(ChaseCamera.PROP_TARGETOFFSET, new Vector3f(0f, ((BoundingBox) myTarget.getWorldBound()).yExtent * "
                            + offsetRatioField.getText() + "f, 0f));\n");
        else
            code.append("chaserProps.put(ChaseCamera.PROP_TARGETOFFSET, new Vector3f("
                            + offsetXField.getText()
                            + "f, "
                            + offsetYField.getText()
                            + "f, "
                            + offsetZField.getText() + "f));\n");

        code.append("chaserProps.put(ThirdPersonMouseLook.PROP_ENABLED, \""
                + enableMouseLookBox.isSelected() + "\");\n");
        if (enableMouseLookBox.isSelected()) {
            code.append("chaserProps.put(ThirdPersonMouseLook.PROP_MAXASCENT, \"\" + FastMath.DEG_TO_RAD * "
                            + maxAscentSlider.getValue() + ");\n");
            code.append("chaserProps.put(ThirdPersonMouseLook.PROP_MINASCENT, \"\" + FastMath.DEG_TO_RAD * "
                            + minAscentSlider.getValue() + ");\n");
            code.append("chaserProps.put(ThirdPersonMouseLook.PROP_INVERTEDY, \""
                            + invertControlCheckBox.isSelected() + "\");\n");
            code.append("chaserProps.put(ThirdPersonMouseLook.PROP_ROTATETARGET, \""
                            + turnWithCameraBox.isSelected() + "\");\n");
            if (turnWithCameraBox.isSelected())
                code.append("chaserProps.put(ThirdPersonMouseLook.PROP_TARGETTURNSPEED, \"\" + FastMath.DEG_TO_RAD * "
                                + turnWithCamSpeedField.getText() + ");\n");
            code.append("chaserProps.put(ThirdPersonMouseLook.PROP_MINROLLOUT, \""
                            + minZoomField.getText() + "\");\n");
            code.append("chaserProps.put(ThirdPersonMouseLook.PROP_MAXROLLOUT, \""
                            + maxZoomField.getText() + "\");\n");
            code.append("chaserProps.put(ThirdPersonMouseLook.PROP_MOUSEXMULT, \""
                            + accelHorizontalField.getText() + "\");\n");
            code.append("chaserProps.put(ThirdPersonMouseLook.PROP_MOUSEYMULT, \""
                            + accelVerticalField.getText() + "\");\n");
            code.append("chaserProps.put(ThirdPersonMouseLook.PROP_MOUSEROLLMULT, \""
                            + accelZoomField.getText() + "\");\n");
            code.append("chaserProps.put(ThirdPersonMouseLook.PROP_LOCKASCENT, \""
                            + lockPolarBox.isSelected() + "\");\n");
        }

        code.append("chaser = new ChaseCamera(cam, m_character, chaserProps);\n");
        code.append("chaser.setActionSpeed(" + camSpeedField.getText() + "f);");

        codeArea.setText(code.toString());
        codeArea.setCaretPosition(0);
    }

    private void updateOffset() {
        Vector3f offset = impl.chaser.getTargetOffset();
        if (offsetRelativeRadio.isSelected()) {
            String val = offsetRatioField.getText();
            float fval = 0;
            try {
                fval = Float.parseFloat(val);
            } catch (NumberFormatException nfe) {
                return;
            }
            offset.set(0,
                    ((BoundingBox) impl.m_character.getWorldBound()).yExtent
                            * fval, 0);
        } else {
            String val = offsetXField.getText();
            float fval = 0;
            try {
                fval = Float.parseFloat(val);
            } catch (NumberFormatException nfe) {
                return;
            }
            offset.x = fval;

            val = offsetYField.getText();
            try {
                fval = Float.parseFloat(val);
            } catch (NumberFormatException nfe) {
                return;
            }
            offset.y = fval;

            val = offsetZField.getText();
            try {
                fval = Float.parseFloat(val);
            } catch (NumberFormatException nfe) {
                return;
            }
            offset.z = fval;
        }
    }

    public Dimension getMinimumSize() {
        return MIN_DIMENSION;
    }

    protected Canvas getGlCanvas() {
        if (glCanvas == null) {

            // -------------GL STUFF------------------

            // make the canvas:
        	DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
        	display.registerCanvasConstructor("AWT", LWJGLAWTCanvasConstructor.class);
            glCanvas = (Canvas)display.createCanvas(width, height);

            // add a listener... if window is resized, we can do something about it.
            glCanvas.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent ce) {
                    impl.resizeCanvas(glCanvas.getSize().width, glCanvas
                            .getSize().height);
                }
            });
            glCanvas.addFocusListener(new FocusListener() {

                public void focusGained(FocusEvent arg0) {
                    ((AWTKeyInput) KeyInput.get()).setEnabled(true);
                    ((AWTMouseInput) MouseInput.get()).setEnabled(true);
                }

                public void focusLost(FocusEvent arg0) {
                    ((AWTKeyInput) KeyInput.get()).setEnabled(false);
                    ((AWTMouseInput) MouseInput.get()).setEnabled(false);
                }

            });

            // We are going to use jme's Input systems, so enable updating.
            ((JMECanvas) glCanvas).setUpdateInput(true);

            KeyInput.setProvider( InputSystem.INPUT_SYSTEM_AWT );
            ((AWTKeyInput) KeyInput.get()).setEnabled(false);
            KeyListener kl = (KeyListener) KeyInput.get();

            glCanvas.addKeyListener(kl);

            AWTMouseInput.setup( glCanvas, true );

            // Important!  Here is where we add the guts to the canvas:
            impl = new ControlImplementor(width, height);
            ((JMECanvas) glCanvas).setImplementor(impl);

            // -----------END OF GL STUFF-------------
        }
        return glCanvas;
    }

    /**
     * provides a numeric verifier that beeps and doesn't allow nothing but a number,
     * backspace,  delete, decimal, minus sign and enter
     * @param field the field that needs a verifier
     */
    public static void addExpandedNumericVerifier(final JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))) {
                    String text = field.getText();
                    if (field.getSelectedText() != null
                            && field.getSelectedText().length() > 0) {
                        if (field.getSelectionStart() == 0) {
                            if (field.getSelectionEnd() == text.length())
                                text = "";
                            else
                                text = text
                                        .substring(field.getSelectionEnd() + 1);
                        } else {
                            if (field.getSelectionEnd() == text.length())
                                text = text.substring(0, field
                                        .getSelectionStart());
                            else
                                text = text.substring(0, field
                                        .getSelectionStart())
                                        + text.substring(field
                                                .getSelectionEnd() + 1);
                        }
                    }
                    boolean skip = false;
                    if (c == '.' || c == '-') {
                        if (c == '-' && field.getCaretPosition() == 0)
                            skip = true;
                        if (c == '.'
                                && (text.length() == 0 || text.substring(0,
                                        text.length()).indexOf('.') == -1))
                            skip = true;
                    } else if (Character.isDigit(c)
                            && !(field.getCaretPosition() <= text.indexOf("-")))
                        skip = true;

                    if (!skip) {
                        field.getToolkit().beep();
                        e.consume();
                    }
                }
            }
        });
    }

    private void applyExample(int selectedIndex) {
        HashMap<String, Object> chaserProps = new HashMap<String, Object>();
        HashMap<String, Object> handlerProps = new HashMap<String, Object>();
        switch (selectedIndex) {
        case 0:
            handlerProps.put(ThirdPersonHandler.PROP_ROTATEONLY, "false");
            handlerProps.put(ThirdPersonHandler.PROP_DOGRADUAL, "true");
            handlerProps.put(ThirdPersonHandler.PROP_TURNSPEED, "3.1415");
            handlerProps.put(ThirdPersonHandler.PROP_LOCKBACKWARDS, "false");
            handlerProps.put(ThirdPersonHandler.PROP_STRAFETARGETALIGN, "true");
            handlerProps.put(ThirdPersonHandler.PROP_CAMERAALIGNEDMOVE, "true");

            handlerProps.put(ThirdPersonHandler.PROP_KEY_FORWARD, ""+KeyInput.KEY_W);
            handlerProps.put(ThirdPersonHandler.PROP_KEY_LEFT, ""+KeyInput.KEY_A);
            handlerProps.put(ThirdPersonHandler.PROP_KEY_BACKWARD, ""+KeyInput.KEY_S);
            handlerProps.put(ThirdPersonHandler.PROP_KEY_RIGHT, ""+KeyInput.KEY_D);
            handlerProps.put(ThirdPersonHandler.PROP_KEY_STRAFELEFT, ""+KeyInput.KEY_Q);
            handlerProps.put(ThirdPersonHandler.PROP_KEY_STRAFERIGHT, ""+KeyInput.KEY_E);
            impl.input.updateProperties(handlerProps);
            impl.input.setActionSpeed(180f);

            chaserProps.put(ChaseCamera.PROP_ENABLESPRING, "true");
            chaserProps.put(ChaseCamera.PROP_DAMPINGK, "10");
            chaserProps.put(ChaseCamera.PROP_SPRINGK, "25.0");
            chaserProps.put(ChaseCamera.PROP_MAXDISTANCE, "200");
            chaserProps.put(ChaseCamera.PROP_MINDISTANCE, "0");
            chaserProps.put(ChaseCamera.PROP_INITIALSPHERECOORDS, new Vector3f(100f, 0f, FastMath.DEG_TO_RAD * 30f));
            chaserProps.put(ChaseCamera.PROP_STAYBEHINDTARGET, "false");
            chaserProps.put(ChaseCamera.PROP_TARGETOFFSET, new Vector3f(0f, ((BoundingBox) impl.m_character.getWorldBound()).yExtent * 1.5f, 0f));
            chaserProps.put(ThirdPersonMouseLook.PROP_ENABLED, "true");
            chaserProps.put(ThirdPersonMouseLook.PROP_MAXASCENT, "" + FastMath.DEG_TO_RAD * 45);
            chaserProps.put(ThirdPersonMouseLook.PROP_INVERTEDY, "false");
            chaserProps.put(ThirdPersonMouseLook.PROP_MINROLLOUT, "20.0");
            chaserProps.put(ThirdPersonMouseLook.PROP_MAXROLLOUT, "200.0");
            chaserProps.put(ThirdPersonMouseLook.PROP_MOUSEXMULT, "2.0");
            chaserProps.put(ThirdPersonMouseLook.PROP_MOUSEYMULT, "30.0");
            chaserProps.put(ThirdPersonMouseLook.PROP_MOUSEROLLMULT, "80.0");
            chaserProps.put(ThirdPersonMouseLook.PROP_LOCKASCENT, "false");
            impl.chaser.updateProperties(chaserProps);
            impl.chaser.setActionSpeed(1.0f);
            break;
        case 1:
            handlerProps.put(ThirdPersonHandler.PROP_ROTATEONLY, "true");
            handlerProps.put(ThirdPersonHandler.PROP_DOGRADUAL, "true");
            handlerProps.put(ThirdPersonHandler.PROP_TURNSPEED, "3.1415");
            handlerProps.put(ThirdPersonHandler.PROP_LOCKBACKWARDS, "true");
            handlerProps.put(ThirdPersonHandler.PROP_CAMERAALIGNEDMOVE, "false");

            handlerProps.put(ThirdPersonHandler.PROP_STRAFETARGETALIGN, "true");
            handlerProps.put(ThirdPersonHandler.PROP_KEY_FORWARD, ""+KeyInput.KEY_W);
            handlerProps.put(ThirdPersonHandler.PROP_KEY_LEFT, ""+KeyInput.KEY_A);
            handlerProps.put(ThirdPersonHandler.PROP_KEY_BACKWARD, ""+KeyInput.KEY_S);
            handlerProps.put(ThirdPersonHandler.PROP_KEY_RIGHT, ""+KeyInput.KEY_D);
            handlerProps.put(ThirdPersonHandler.PROP_KEY_STRAFELEFT, ""+KeyInput.KEY_Q);
            handlerProps.put(ThirdPersonHandler.PROP_KEY_STRAFERIGHT, ""+KeyInput.KEY_E);
            impl.input.updateProperties(handlerProps);
            impl.input.setActionSpeed(250);

            chaserProps.put(ChaseCamera.PROP_ENABLESPRING, "true");
            chaserProps.put(ChaseCamera.PROP_DAMPINGK, "55");
            chaserProps.put(ChaseCamera.PROP_SPRINGK, "756.25");
            chaserProps.put(ChaseCamera.PROP_MAXDISTANCE, "0");
            chaserProps.put(ChaseCamera.PROP_MINDISTANCE, "0");
            chaserProps.put(ChaseCamera.PROP_INITIALSPHERECOORDS, new Vector3f(65.0f, 0f, FastMath.DEG_TO_RAD * 12f));
            chaserProps.put(ChaseCamera.PROP_STAYBEHINDTARGET, "true");
            chaserProps.put(ChaseCamera.PROP_TARGETOFFSET, new Vector3f(0f, ((BoundingBox) impl.m_character.getWorldBound()).yExtent * 1.6f, 0f));
            chaserProps.put(ThirdPersonMouseLook.PROP_MAXASCENT, "" + FastMath.DEG_TO_RAD * 85);
            chaserProps.put(ThirdPersonMouseLook.PROP_INVERTEDY, "false");
            chaserProps.put(ThirdPersonMouseLook.PROP_MINROLLOUT, "30");
            chaserProps.put(ThirdPersonMouseLook.PROP_MAXROLLOUT, "240.0");
            chaserProps.put(ThirdPersonMouseLook.PROP_MOUSEXMULT, "2.0");
            chaserProps.put(ThirdPersonMouseLook.PROP_MOUSEYMULT, "30.0");
            chaserProps.put(ThirdPersonMouseLook.PROP_MOUSEROLLMULT, "50.0");
            chaserProps.put(ThirdPersonMouseLook.PROP_LOCKASCENT, "true");
            impl.chaser.updateProperties(chaserProps);
            impl.chaser.setActionSpeed(1.0f);
            break;
        default:
            break;
        }
        updateFromImpl();
    }

    class DocumentAdapter implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            update();
        }

        public void removeUpdate(DocumentEvent e) {
            update();
        }

        public void changedUpdate(DocumentEvent e) {
        }

        public void update() {
        }
    }
}
