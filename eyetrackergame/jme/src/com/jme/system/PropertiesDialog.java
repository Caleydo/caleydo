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

package com.jme.system;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * <code>PropertiesDialog</code> provides an interface to make use of the
 * <code>GameSettings</code> class. It provides a simple clean method of
 * creating a properties file. The <code>GameSettings</code> is still created
 * by the client application, and passed during construction.
 *
 * @see com.jme.system.GameSettings
 * @author Mark Powell
 * @version $Id: PropertiesDialog.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class PropertiesDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(PropertiesDialog.class.getName());

    private static final long serialVersionUID = 1L;

    //connection to properties file.
    private GameSettings source = null;

    //Title Image
    URL imageFile = null;

    //UI components
    private JCheckBox fullscreenBox = null;
    private JComboBox displayResCombo = null;
    private JComboBox colorDepthCombo = null;
    private JComboBox displayFreqCombo = null;
    private JComboBox rendererCombo = null;
    private JLabel icon = null;

    //flag to denote if the dialog has finished being used.
    private boolean done = false;

    /**
     * Constructor builds the interface for the <code>PropertiesDialog</code>.
     *
     * @param source
     *            the <code>GameSettings</code> object to use for working
     *            with the properties file.
     * @param imageFile
     *            the file to use as the title of the dialog. Null will result
     *            in no picture being used.
     * @throws JmeException
     *             if the source is null.
     */
    public PropertiesDialog(GameSettings source, String imageFile) {
        if (null == source)
            throw new JmeException("PropertyIO source cannot be null");

        URL file = null;
        try {
            file = new URL("file:" + imageFile);
        } catch (MalformedURLException e) {}

        this.source = source;
        this.imageFile = file;

        init();
    }

    /**
     * Constructor builds the interface for the <code>PropertiesDialog</code>.
     *
     * @param source
     *            the <code>GameSettings</code> object to use for working
     *            with the properties file.
     * @param imageFile
     *            the file to use as the title of the dialog. Null will result
     *            in no picture being used.
     * @throws JmeException
     *             if the source is null.
     */
    public PropertiesDialog(GameSettings source, URL imageFile) {
        if (null == source)
            throw new JmeException("PropertyIO source cannot be null");

        this.source = source;
        this.imageFile = imageFile;

        init();
    }

    /**
     * <code>isDone</code> returns the status of the dialog. If true, the
     * application has either been used to change/set the properties file, or
     * has been closed.
     *
     * @return true if the dialog is closed, false if it is still up.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * <code>setImage</code> sets the background image of the dialog.
     *
     * @param image <code>String</code> representing the image file.
     */
    public void setImage(String image) {
        URL file = null;
        try {
            file = new URL("file:" + image);
        //We can safely ignore the exception - it just means that
        //the user gave us a bogus file
        } catch (MalformedURLException e) {}
        setImage(file);
    }

    /**
     * <code>setImage</code> sets the background image of the dialog.
     *
     * @param image <code>URL</code> pointing to the image file.
     */
    public void setImage(URL image) {
        icon.setIcon(new ImageIcon(image));
        pack(); //Resize to accomodate the new image
        center();
    }

    /**
     * <code>showDialog</code> sets the dialog as visble, and
     * brings it to the front.
     */
    private void showDialog() {
        setVisible(true);
        toFront();
    }


    /**
     * <code>center</code> places this <code>PropertiesDialog</code> in
     * the center of the screen.
     */
    private void center(){
        int x, y;
        x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getWidth()) / 2;
        y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getHeight()) / 2;
        this.setLocation(x, y);
    }

    /**
     * <code>init</code> creates the components to use the dialog.
     */
    private void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warning("Could not set native look and feel.");
        }

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        this.setTitle("Select Display Settings");

        //The panels...
        JPanel mainPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel optionsPanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        //The buttons...
        JButton ok = new JButton("Ok");
        JButton cancel = new JButton("Cancel");

        mainPanel.setLayout(new BorderLayout());
        centerPanel.setLayout(new BorderLayout());

        icon = new JLabel(new ImageIcon(imageFile));
        centerPanel.add(icon, BorderLayout.NORTH);

        displayResCombo = setUpResolutionChooser();
        colorDepthCombo = setUpColorDepthChooser();
        displayFreqCombo = setUpFreqChooser();
        optionsPanel.add(displayResCombo);
        optionsPanel.add(colorDepthCombo);
        optionsPanel.add(displayFreqCombo);

        fullscreenBox = new JCheckBox("Fullscreen?");
        fullscreenBox.setSelected(source.isFullscreen());
        rendererCombo = setUpRendererChooser();
        optionsPanel.add(fullscreenBox);
        optionsPanel.add(rendererCombo);

        centerPanel.add(optionsPanel, BorderLayout.SOUTH);

        //Set the button action listeners. Cancel disposes without saving,
        //ok saves.
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (verifyAndSaveCurrentSelection()) {
                    dispose();
                    done = true;
                }
            }
        });

        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });

        buttonPanel.add(ok);
        buttonPanel.add(cancel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);

        pack();
        center();
        showDialog();
    }

    /**
     * <code>verifyAndSaveCurrentSelection</code> first verifies that the
     * display mode is valid for this system, and then saves the current
     * selection as a properties.cfg file.
     *
     * @return if the selection is valid
     */
    private boolean verifyAndSaveCurrentSelection() {
        String display = (String)displayResCombo.getSelectedItem();

        int width = Integer.parseInt(display.substring(0, display.indexOf('x')));
        display = display.substring(display.indexOf('x') + 1);

        int height = Integer.parseInt(display);

        String depthString = (String)colorDepthCombo.getSelectedItem();
        int depth = Integer.parseInt(depthString.substring(0, depthString.indexOf(' ')));

        String freqString = (String)displayFreqCombo.getSelectedItem();
        int freq = Integer.parseInt(freqString.substring(0, freqString.indexOf(' ')));

        boolean fullscreen = fullscreenBox.isSelected();
        String renderer = (String)rendererCombo.getSelectedItem();

        //test valid display mode
        DisplaySystem disp = DisplaySystem.getDisplaySystem(renderer);
        boolean valid = (disp != null) ? disp.isValidDisplayMode(width, height, depth, freq) : false;

        if (valid) {
            // use the PropertiesIO class to save it.
            source.setWidth(width);
            source.setHeight(height);
            source.setDepth(depth);
            source.setFrequency(freq);
            source.setFullscreen(fullscreen);
            source.setRenderer(renderer);
            try {
                source.save();
            } catch (IOException ioe) {
                logger.log(Level.WARNING,
                        "Failed to save setting changes", ioe);
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "The selected display mode is not valid!",
                "Invalid Mode",
                JOptionPane.ERROR_MESSAGE);
        }

        return valid;
    }

    /**
     * <code>setUpChooser</code> retrieves all available display modes and
     * places them in a <code>JComboBox</code>.
     *
     * @return the combo box of display modes.
     */
    private JComboBox setUpResolutionChooser() {
        String[] modes = { "640x480", "800x600", "1024x768", "1280x1024", "1600x1200" };
        JComboBox resolutionBox = new JComboBox(modes);

        resolutionBox.setSelectedItem(source.getWidth() + "x" + source.getHeight());

        return resolutionBox;
    }

    /**
     * @return a combo box of possible bit depths
     */
    private JComboBox setUpColorDepthChooser() {
        String[] depths = { "16 bpp", "24 bpp", "32 bpp" };
        JComboBox depthBox = new JComboBox(depths);

        depthBox.setSelectedItem(source.getDepth() + " bpp");

        return depthBox;
    }

    /**
     *
     * <code>setUpFreqChooser</code> sets available display frequencys.
     *
     * @return the combo box that contains the display frequencys.
     */
    private JComboBox setUpFreqChooser() {
        String modes[] = { "0 Hz (Linux)", "60 Hz", "70 Hz", "75 Hz", "80 Hz", "85 Hz" };
        JComboBox freqBox = new JComboBox(modes);
        freqBox.setSelectedItem(source.getFrequency() + " Hz");
        return freqBox;
    }

    /**
     *
     * <code>setUpRendererChooser</code> sets the list of available
     * renderers. This is obtained from the <code>DisplaySystem</code> class.
     *
     * @return the list of renderers.
     */
    private JComboBox setUpRendererChooser() {
        String modes[] = DisplaySystem.getSystemProviderIdentifiers();
        JComboBox nameBox = new JComboBox(modes);
        nameBox.setSelectedItem(source.getRenderer());
        return nameBox;
    }
}
