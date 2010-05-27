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
package com.jmex.editors.swing.settings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.jme.system.GameSettings;
import com.jme.system.jogl.JOGLSystemProvider;
import com.jme.system.lwjgl.LWJGLSystemProvider;

/**
 * @author Matthew D. Hicks
 */
public class GameSettingsPanel extends JPanel {
    private static final Logger logger = Logger
            .getLogger(GameSettingsPanel.class.getName());
    
	private static final long serialVersionUID = 1L;

	public static final int[][] RESOLUTIONS = {
			{640, 480},
			{800, 600},
			{1024, 768},
			{1280, 1024},
			{1440, 900},
			{1600, 1024},
			{1600, 1200},
			{1920, 1200}
		};
	public static final int[] DEPTHS = {
			16,
			24,
			32
		};

	private GameSettings settings;
	private DisplayMode[] allModes;

	private GridBagLayout layout;
	private GridBagConstraints constraints;

	private JComboBox renderer;
	private JComboBox resolution;
	private JComboBox depth;
	private JComboBox frequency;
	private JComboBox verticalSync;
	private JComboBox fullscreen;
	private JComboBox music;
	private JComboBox sfx;
	private JComboBox depthBits;
	private JComboBox alphaBits;
	private JComboBox stencilBits;
	private JComboBox samples;

	private HashMap<String, JComboBox> map;
	private HashMap<String, Object> defaults;

	public GameSettingsPanel(GameSettings settings) {
		this.settings = settings;

		try {
			allModes = Display.getAvailableDisplayModes();
		} catch (Exception e) {
		}

		map = new HashMap<String, JComboBox>();
		defaults = new HashMap<String, Object>();
		init();
	}

	private void init() {
		layout = new GridBagLayout();
		setLayout(layout);
		constraints = new GridBagConstraints();

		List<Component> list = getSettingsComponents();
		revert();
		JLabel label = null;
		for (int i = 0; i < list.size(); i++) {
			Component c = list.get(i);
			label = new JLabel(" " + c.getName() + ": ");
			label.setHorizontalAlignment(SwingConstants.RIGHT);

			constraints.gridwidth = 1;
			constraints.anchor = GridBagConstraints.EAST;
			constraints.insets = new Insets(5, 5, 5, 5);
			layout.setConstraints(label, constraints);
			add(label);

			constraints.anchor = GridBagConstraints.WEST;
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			layout.setConstraints(c, constraints);
			add(c);
		}
	}

	public void addSetting(String name, Object[] choices, Object defaultChoice) {
		defaultChoice = settings.getObject(name, defaultChoice);
		logger.info("Default Choice for " + name + " = " + defaultChoice);
		
		JComboBox c = new JComboBox(choices);
		c.setName(name);
		c.setSelectedItem(defaultChoice);

		JLabel label = new JLabel(" " + c.getName() + ": ");
		label.setHorizontalAlignment(SwingConstants.RIGHT);

		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(label, constraints);
		add(label);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(c, constraints);
		add(c);

		map.put(name, c);
		defaults.put(name, defaultChoice);
	}

	protected List<Component> getSettingsComponents() {
		List<Component> components = new ArrayList<Component>();
		components.add(createRenderer());
		components.add(createResolution());
		components.add(createDepth());
		components.add(createFrequency());
		components.add(createVerticalSync());
		components.add(createFullscreen());
		components.add(createMusic());
		components.add(createSFX());
		components.add(createDepthBits());
		components.add(createAlphaBits());
		components.add(createStencilBits());
		components.add(createSamples());
		return components;
	}

	protected Component createRenderer() {
		renderer = new JComboBox(new Object[] { LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER,
		                                        JOGLSystemProvider.SYSTEM_IDENTIFIER});
		renderer.setName("Renderer");
		return renderer;
	}

	protected Component createResolution() {
		resolution = new JComboBox(getResolutionArray());
		resolution.setName("Resolution");
		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				// The resolution combobox is all we care about
				String[] parser = ((String) resolution.getSelectedItem())
						.split("x");
				setMenuOptions(Integer.parseInt(parser[0]), Integer
						.parseInt(parser[1]));
			}
		};
		resolution.addItemListener(itemListener);
		return resolution;
	}

	protected Component createDepth() {
		depth = new JComboBox(getDepthArray());
		depth.setName("Depth");
		return depth;
	}

	/**
	 * Sets the other menu options based on the given width and height
	 * parameters.
	 * 
	 * @param width
	 * @param height
	 */
	public void setMenuOptions(int width, int height) {
		Vector<DisplayMode> availableModes = getAvailableModesRes(allModes, width, height);
		depth.removeAllItems();
		frequency.removeAllItems();
		HashSet<String> depths = new HashSet<String>();
		HashSet<String> frequencies = new HashSet<String>();

		for (DisplayMode aMode : availableModes) {
			depths.add(String.valueOf(aMode.getBitsPerPixel()));
			frequencies.add(String.valueOf(aMode.getFrequency()));
		}
		for (String oneDepth : depths) {
			depth.addItem(oneDepth);
		}
		for (String oneFreq : frequencies) {
			frequency.addItem(oneFreq);
		}
		depth.updateUI();
		frequency.updateUI();
	}

	/**
	 * Gets the available modes based on the set of modes for the system and a
	 * resolution.
	 * 
	 * @param theModes
	 * @param width
	 * @param height
	 * @return
	 */
	public static Vector<DisplayMode> getAvailableModesRes(DisplayMode[] theModes, int width, int height) {
		Vector<DisplayMode> modes = new Vector<DisplayMode>();

		for (int[] res : RESOLUTIONS) {
			if (res[0] == width && res[1] == height) {
				for (DisplayMode aMode : theModes) {
					if (aMode.getHeight() == height
							&& aMode.getWidth() == width) {
						modes.add(aMode);
					}
				}
			}
		}
		return modes;
	}

	public static Object[] getResolutionArray() {
		Object[] resolutions = new Object[RESOLUTIONS.length];
		for (int i = 0; i < resolutions.length; i++) {
			resolutions[i] = RESOLUTIONS[i][0] + "x" + RESOLUTIONS[i][1];
		}
		return resolutions;
	}

	public static Object[] getDepthArray() {
		Object[] depths = new Object[DEPTHS.length];
		for (int i = 0; i < depths.length; i++) {
			depths[i] = String.valueOf(DEPTHS[i]);
		}
		return depths;
	}

	protected Component createFrequency() {
		frequency = new JComboBox(new Object[] { "60", "70", "72", "75", "85",
				"100", "120", "140" });
		frequency.setName("Frequency");
		return frequency;
	}

	protected Component createVerticalSync() {
		verticalSync = new JComboBox(new Object[] { "Yes", "No" });
		verticalSync.setName("Vertical Sync");
		return verticalSync;
	}

	protected Component createFullscreen() {
		fullscreen = new JComboBox(new Object[] { "Yes", "No" });
		fullscreen.setName("Fullscreen");
		return fullscreen;
	}

	protected Component createMusic() {
		music = new JComboBox(new Object[] { "Yes", "No" });
		music.setName("Music");
		return music;
	}

	protected Component createSFX() {
		sfx = new JComboBox(new Object[] { "Yes", "No" });
		sfx.setName("Sound Effects");
		return sfx;
	}

	protected Component createDepthBits() {
		depthBits = new JComboBox(new Object[] { "8" });
		depthBits.setName("Depth Bits");
		return depthBits;
	}

	protected Component createAlphaBits() {
		alphaBits = new JComboBox(new Object[] { "0" });
		alphaBits.setName("Alpha Bits");
		return alphaBits;
	}

	protected Component createStencilBits() {
		stencilBits = new JComboBox(new Object[] { "0" });
		stencilBits.setName("Stencil Bits");
		return stencilBits;
	}

	protected Component createSamples() {
		samples = new JComboBox(new Object[] { "0" });
		samples.setName("Samples");
		return samples;
	}

	public void defaults() {
		try {
			settings.clear();
			revert();
		} catch(Exception exc) {
			logger.logp(Level.SEVERE, this.getClass().toString(), "defaults()", "Exception", exc);
		}
	}

	public void revert() {
		renderer.setSelectedItem(settings.getRenderer());
		resolution.setSelectedItem(settings.getWidth() + "x" + settings.getHeight());
		depth.setSelectedItem(String.valueOf(settings.getDepth()));
		frequency.setSelectedItem(String.valueOf(settings.getFrequency()));
		verticalSync.setSelectedItem(settings.isVerticalSync() ? "Yes" : "No");
		fullscreen.setSelectedItem(settings.isFullscreen() ? "Yes" : "No");
		music.setSelectedItem(settings.isMusic() ? "Yes" : "No");
		sfx.setSelectedItem(settings.isSFX() ? "Yes" : "No");
		depthBits.setSelectedItem(String.valueOf(settings.getDepthBits()));
		alphaBits.setSelectedItem(String.valueOf(settings.getAlphaBits()));
		stencilBits.setSelectedItem(String.valueOf(settings.getStencilBits()));
		samples.setSelectedItem(String.valueOf(settings.getSamples()));
		for (String name : map.keySet()) {
			JComboBox combo = map.get(name);
			combo.setSelectedItem(settings.getObject(name, defaults.get(name)));
		}
	}

	public void apply() {
		settings.setRenderer((String) renderer.getSelectedItem());
		String[] parser = ((String) resolution.getSelectedItem()).split("x");
		settings.setWidth(Integer.parseInt(parser[0]));
		settings.setHeight(Integer.parseInt(parser[1]));
		settings.setDepth(Integer.parseInt((String) depth.getSelectedItem()));
		settings.setFrequency(Integer.parseInt((String) frequency.getSelectedItem()));
		settings.setVerticalSync(verticalSync.getSelectedItem().equals("Yes"));
		settings.setFullscreen(fullscreen.getSelectedItem().equals("Yes"));
		settings.setMusic(music.getSelectedItem().equals("Yes"));
		settings.setSFX(sfx.getSelectedItem().equals("Yes"));
		settings.setDepthBits(Integer.parseInt((String) depthBits.getSelectedItem()));
		settings.setAlphaBits(Integer.parseInt((String) alphaBits.getSelectedItem()));
		settings.setStencilBits(Integer.parseInt((String) stencilBits.getSelectedItem()));
		settings.setSamples(Integer.parseInt((String) samples.getSelectedItem()));
		for (String name : map.keySet()) {
			settings.setObject(name, map.get(name).getSelectedItem());
		}
	}
	
	public boolean validateDisplay() {
		if (depth.getSelectedItem() == null) {
			return false;
		} else if (frequency.getSelectedItem() == null) {
			return false;
		}
		return true;
	}

	private static boolean ok;
	
	public static final boolean prompt(GameSettings settings) throws InterruptedException {
		return prompt(settings, "Game Settings");
	}
	
	public static final boolean prompt(GameSettings settings, String title) throws InterruptedException {
		final JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setAlwaysOnTop(true);

		final GameSettingsPanel panel = new GameSettingsPanel(settings);

		ok = false;
		
		ActionListener buttonListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JButton b = (JButton) evt.getSource();
				if ("Defaults".equals(b.getText())) {
					panel.defaults();
				} else if ("Revert".equals(b.getText())) {
					panel.revert();
				} else if ("OK".equals(b.getText())) {
					if (panel.validateDisplay()) {
						ok = true;
						panel.apply();
						frame.dispose();
					} else {
						JOptionPane.showMessageDialog(frame, "Invalid display configuration combination", "Invalid Settings", JOptionPane.ERROR_MESSAGE);
					}
				} else if ("Cancel".equals(b.getText())) {
					frame.dispose();
				}
			}
		};

		JPanel bottom = new JPanel();
		bottom.setLayout(new FlowLayout());
		JButton b = new JButton("Defaults");
		b.addActionListener(buttonListener);
		bottom.add(b);
		b = new JButton("Revert");
		b.addActionListener(buttonListener);
		bottom.add(b);
		b = new JButton("OK");
		b.addActionListener(buttonListener);
		bottom.add(b);
        frame.getRootPane().setDefaultButton(b);
        b = new JButton("Cancel");
		b.addActionListener(buttonListener);
		bottom.add(b);

		Container c = frame.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(BorderLayout.CENTER, panel);
		c.add(BorderLayout.SOUTH, bottom);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// Wait for finish before returning
		while (frame.isVisible()) {
			Thread.sleep(50);
		}
		return ok;
	}
}