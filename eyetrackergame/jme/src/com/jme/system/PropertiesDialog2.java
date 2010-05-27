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
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
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
 * @author Eric Woroshow
 * @version $Id: PropertiesDialog2.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public final class PropertiesDialog2 extends JDialog {
    private static final Logger logger = Logger.getLogger(PropertiesDialog2.class.getName());

    private static final long serialVersionUID = 1L;

    //connection to properties file.
    private final GameSettings source;

    //Title Image
    private URL imageFile = null;
    
    //Array of supported display modes
    private final DisplayMode[] modes;

    //UI components
    private JCheckBox fullscreenBox = null;
    private JComboBox displayResCombo = null;
    private JComboBox colorDepthCombo = null;
    private JComboBox displayFreqCombo = null;
    private JComboBox rendererCombo = null;
    private JLabel icon = null;
    
    /**
     * Constructor for the <code>PropertiesDialog</code>. Creates a
     * properties dialog initialized for the primary display.
     * @param source the <code>GameSettings</code> object to use for working with
     *               the properties file.
     * @param imageFile the image file to use as the title of the dialog;
     *                  <code>null</code> will result in to image being displayed
     * @throws JmeException if the source is <code>null</code>
     */
    public PropertiesDialog2(GameSettings source, String imageFile) {
        this(source, getURL(imageFile));
    }


    /**
     * Constructor for the <code>PropertiesDialog</code>. Creates a
     * properties dialog initialized for the primary display.
     * @param source the <code>GameSettings</code> object to use for working with
     *               the properties file.
     * @param imageFile the image file to use as the title of the dialog;
     *                  <code>null</code> will result in to image being displayed
     * @throws JmeException if the source is <code>null</code>
     */
    public PropertiesDialog2(GameSettings source, URL imageFile) {
        if (null == source)
                throw new JmeException("PropertyIO source cannot be null");

        this.source = source;
        this.imageFile = imageFile;
        this.modes = GraphicsEnvironment.getLocalGraphicsEnvironment()
                     .getDefaultScreenDevice().getDisplayModes();
        Arrays.sort(modes, new DisplayModeSorter());

        createUI();
    }

    /**
     * <code>setImage</code> sets the background image of the dialog.
     * @param image <code>String</code> representing the image file.
     */
    public void setImage(String image) {
        try {
            URL file = new URL("file:" + image);
            setImage(file);
            //We can safely ignore the exception - it just means that the user gave us a bogus file
        } catch (MalformedURLException e) {}
    }

    /**
     * <code>setImage</code> sets the background image of this dialog.
     * @param image <code>URL</code> pointing to the image file.
     */
    public void setImage(URL image) {
        icon.setIcon(new ImageIcon(image));
        pack(); //Resize to accomodate the new image
        center();
    }

    /**
     * <code>showDialog</code> sets this dialog as visble, and brings it to the
     * front.
     */
    private void showDialog() {
        setVisible(true);
        toFront();
    }

    /**
     * <code>center</code> places this <code>PropertiesDialog</code> in the
     * center of the screen.
     */
    private void center() {
        int x, y;
        x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getWidth()) / 2;
        y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getHeight()) / 2;
        this.setLocation(x, y);
    }

    /**
     * <code>init</code> creates the components to use the dialog.
     */
    private void createUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warning("Could not set native look and feel.");
        }

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        setTitle("Select Display Settings");

        //The panels...
        JPanel mainPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel optionsPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        //The buttons...
        JButton ok = new JButton("Ok");
        JButton cancel = new JButton("Cancel");
        
        icon = new JLabel(new ImageIcon(imageFile));

        mainPanel.setLayout(new BorderLayout());
        
        centerPanel.setLayout(new BorderLayout());
        
        displayResCombo = setUpResolutionChooser();
        colorDepthCombo = new JComboBox();
        displayFreqCombo = new JComboBox();
        fullscreenBox = new JCheckBox("Fullscreen?");
        fullscreenBox.setSelected(source.isFullscreen());
        rendererCombo = setUpRendererChooser();
        
        updateDisplayChoices();
        
        optionsPanel.add(displayResCombo);
        optionsPanel.add(colorDepthCombo);
        optionsPanel.add(displayFreqCombo);
        optionsPanel.add(fullscreenBox);
        optionsPanel.add(rendererCombo);

        //Set the button action listeners. Cancel disposes without saving, OK saves.
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (verifyAndSaveCurrentSelection())
                    dispose();
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
        
        centerPanel.add(icon, BorderLayout.NORTH);
        centerPanel.add(optionsPanel, BorderLayout.SOUTH);

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
        String display = (String) displayResCombo.getSelectedItem();

        int width = Integer.parseInt(display.substring(0, display.indexOf(" x ")));
        display = display.substring(display.indexOf(" x ") + 3);
        int height = Integer.parseInt(display);

        String depthString = (String) colorDepthCombo.getSelectedItem();
        int depth = Integer.parseInt(depthString.substring(0, depthString.indexOf(' ')));

        String freqString = (String) displayFreqCombo.getSelectedItem();
        int freq = Integer.parseInt(freqString.substring(0, freqString.indexOf(' ')));

        boolean fullscreen = fullscreenBox.isSelected();
        if (!fullscreen) {
            //query the current bit depth of the desktop
            int curDepth = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDisplayMode().getBitDepth();
            if (depth > curDepth) {
                showError(this,"Cannot choose a higher bit depth in windowed " +
                               "mode than your current desktop bit depth");
                return false;
            }            
        }
        
        String renderer = (String) rendererCombo.getSelectedItem();

        //test valid display mode
        DisplaySystem disp = DisplaySystem.getDisplaySystem(renderer);
        boolean valid = (disp != null) ? disp.isValidDisplayMode(width, height, depth, freq) : false;

        if (valid) {
            //use the GameSettings class to save it.
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
        } else
            showError(this, "Your monitor claims to not support the display mode you've selected.\n" +
                            "The combination of bit depth and refresh rate is not supported.");

        return valid;
    }

    /**
     * <code>setUpChooser</code> retrieves all available display modes and
     * places them in a <code>JComboBox</code>. The resolution specified
     * by GameSettings is used as the default value.
     * @return the combo box of display modes.
     */
    private JComboBox setUpResolutionChooser() {
        String[] res = getResolutions(modes);
        JComboBox resolutionBox = new JComboBox(res);

        resolutionBox.setSelectedItem(source.getWidth() + " x " + source.getHeight());
        resolutionBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateDisplayChoices();
            }
        });

        return resolutionBox;
    }

    /**
     * <code>setUpRendererChooser</code> sets the list of available renderers.
     * Data is obtained from the <code>DisplaySystem</code> class. The renderer
     * specified by GameSettings is used as the default value.
     * @return the list of renderers.
     */
    private JComboBox setUpRendererChooser() {
        String modes[] = DisplaySystem.getSystemProviderIdentifiers();
        JComboBox nameBox = new JComboBox(modes);
        nameBox.setSelectedItem(source.getRenderer());
        return nameBox;
    }
    
    private void updateDisplayChoices() {
        String resolution = (String)displayResCombo.getSelectedItem();
        //grab available depths
        String[] depths = getDepths(resolution, modes);
        colorDepthCombo.setModel(new DefaultComboBoxModel(depths));
        colorDepthCombo.setSelectedItem(source.getDepth() + " bpp");
        //grab available frequencies
        String[] freqs = getFrequencies(resolution, modes);
        displayFreqCombo.setModel(new DefaultComboBoxModel(freqs));
        displayFreqCombo.setSelectedItem(source.getFrequency() + " Hz");
    }
    
    //
    //Utility methods
    //
    
    /**
     * Utility method for converting a String denoting a file
     * into a URL.
     * @return a URL pointing to the file or null
     */
    private static URL getURL(String file) {
        URL url = null;
        try {
            url = new URL("file:" + file);
        } catch (MalformedURLException e) {}
        return url;
    }
    
    private static void showError(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Reutrns every unique resolution from an array of <code>DisplayMode</code>s.
     */
    private static String[] getResolutions(DisplayMode[] modes) {
        ArrayList<String> resolutions = new ArrayList<String>(16);
        for (int i = 0; i < modes.length; i++) {
            String res = modes[i].getWidth() + " x " + modes[i].getHeight();
            if (!resolutions.contains(res))
                resolutions.add(res);
        }
        
        String[] res = new String[resolutions.size()];
        resolutions.toArray(res);
        return res;
    }
    
    /**
     * Returns every possible bit depth for the given resolution.
     */
    private static String[] getDepths(String resolution, DisplayMode[] modes) {
        ArrayList<String> depths = new ArrayList<String>(4);
        for (int i = 0; i < modes.length; i++) {
            //Filter out all bit depths lower than 16 - Java incorrectly reports
            //them as valid depths though the monitor does not support them
            if (modes[i].getBitDepth() < 16) continue;
            
            String res = modes[i].getWidth() + " x " + modes[i].getHeight();
            String depth = modes[i].getBitDepth() + " bpp";
            if (res.equals(resolution) && !depths.contains(depth))
                depths.add(depth);
        }
        
        String[] res = new String[depths.size()];
        depths.toArray(res);
        return res;
    }
    
    /**
     * Returns every possible refresh rate for the given resolution.
     */
    private static String[] getFrequencies(String resolution, DisplayMode[] modes) {
        ArrayList<String> freqs = new ArrayList<String>(4);
        for (int i = 0; i < modes.length; i++) {
            String res = modes[i].getWidth() + " x " + modes[i].getHeight();
            String freq = modes[i].getRefreshRate() + " Hz";
            if (res.equals(resolution) && !freqs.contains(freq))
                freqs.add(freq);
        }
        
        String[] res = new String[freqs.size()];
        freqs.toArray(res);
        return res;
    }
    
    /**
     * Utility class for sorting <code>DisplayMode</code>s. Sorts by resolution,
     * then bit depth, and then finally refresh rate.
     */
    private class DisplayModeSorter implements Comparator<DisplayMode> {
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(DisplayMode a, DisplayMode b) {
            //Width
            if (a.getWidth() != b.getWidth())
                return (a.getWidth() > b.getWidth()) ?  1 : -1;
            //Height
            if (a.getHeight() != b.getHeight())
                return (a.getHeight() > b.getHeight()) ?  1 : -1;
            //Bit depth
            if (a.getBitDepth() != b.getBitDepth())
                return (a.getBitDepth() > b.getBitDepth()) ?  1 : -1;
            //Refresh rate
            if (a.getRefreshRate() != b.getRefreshRate())
                return (a.getRefreshRate() > b.getRefreshRate()) ?  1 : -1;
            //All fields are equal
            return 0;
        }
    }
}
