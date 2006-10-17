/*
 * @(#)Graphpad.java	1.2 11/11/02
 *
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * GPGraphpad is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * GPGraphpad is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPGraphpad; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.jgraph.pad;

import java.applet.Applet;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.jgraph.pad.coreframework.GPGraphpad;
import org.jgraph.pad.coreframework.GPGraphpadFile;
import org.jgraph.pad.coreframework.GPSessionParameters;
import org.jgraph.pad.resources.ImageLoader;
import org.jgraph.pad.resources.Translator;
import org.jgraph.pad.util.SmartFrame;
import org.jgraph.pad.util.Utilities;

/**
 * A class with some static methods (including main and init) to properly
 * instanciate a GPGraphpad (actually a GPGraphpad JPanel) which is the main
 * JPanel where mutli JGraph document and GUI are displayed. You can do it
 * either as an application, either as an applet.
 * 
 * @see org.jgraph.pad.coreframework.GPGraphpad
 */
public class JGraphpad extends Applet {

	/**
	 * is properly set by the ant buildfile to ensure the source version match
	 * the binary version
	 */
	public static final String VERSION = "JGraphpad (v5.8.1.1.0)";

	public static GPSessionParameters sessionParameters;

	/*
	 * Main method for creating a JGraphpad in an application deployed either
	 * offline, either via webstart
	 */
	public static void main(String[] args) {
		sessionParameters = new GPSessionParameters();
		sessionParameters.putApplicationParameters(args);
		JWindow splashFrame = new JWindow();
		JLabel info = new JLabel("Initializing....", JLabel.CENTER);
		showSplash(splashFrame, info);
		createPad();
		splashFrame.dispose();
	}

	/**
	 * Automatic entry point when deploying JGraphpad as an applet
	 */
	public void init() {
		sessionParameters = new GPSessionParameters(this);
		setLayout(new BorderLayout());
		setBackground(Color.white);
		JButton button = new JButton("Start");
		button.setIcon(ImageLoader.getImageIcon(Translator.getString("Icon")));
		add(button, BorderLayout.CENTER);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createPad();
			}
		});
		button.setPreferredSize(getSize());
		createPad();
		button.revalidate();
	}

	/**
	 * This method is called when the user press the applet button and also at
	 * the applet start up. The button is especially handy to refresh the applet
	 * without having to reload the page.
	 */
	public static void createPad() {
		GPGraphpad pad = new GPGraphpad(sessionParameters);
		pad.init();
		JFrame gpframe = createFrame();
		gpframe.getContentPane().add(pad);
		gpframe.addWindowListener(pad.getAppCloser());
		gpframe.setVisible(true);
		String plaf = Translator.getString("LookAndFell.class");
		if (plaf != null && !plaf.equals(""))
			try {
				UIManager.setLookAndFeel(plaf);
			} catch (Exception e) {
			}
		;
		tryToLoadFile(pad);
	}

	/**
	 * By default we put the GPGraphpad in a JFrame we create here
	 * 
	 * @return
	 */
	public static JFrame createFrame() {
		JFrame frame = new SmartFrame();
		frame.setName("MainGraphpad");
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setLocation(0, 0);
		frame.setIconImage(ImageLoader.getImageIcon(
				Translator.getString("Icon")).getImage());
		frame.setTitle(Translator.getString("Title"));
		return frame;
	}

	/**
	 * If a file has been specified by command line, applet or webstart
	 * argument, then we try to open it
	 * 
	 * @param pad
	 */
	public static void tryToLoadFile(GPGraphpad pad) {
		String dowloadpath = pad.getSessionParameters().getParam(
				GPSessionParameters.DOWNLOADPATH, false);
		if (dowloadpath == null)// no file to upload!
			return;

		try {
			URL url = new URL(pad.getSessionParameters().getParam(
					GPSessionParameters.PROTOCOL, true), pad
					.getSessionParameters().getParam(
							GPSessionParameters.HOSTNAME, true), Integer
					.parseInt(pad.getSessionParameters().getParam(
							GPSessionParameters.HOSTPORT, true)), dowloadpath);
			InputStream input = url.openStream();
			if (dowloadpath.endsWith("gz"))//TODO: || dowloadpath.endsWith("draw"))//compressed
				input = new GZIPInputStream(input);
			System.out.print("before reading");
			GPGraphpadFile file = GPGraphpadFile.read(input);
			if (file == null)
				return;
			// add the new document with the new graph and the new model
			pad.addDocument(null, file);// TODO URL!!
			pad.update();
		} catch (Exception ex) {
		}
		;
	}

	/**
	 * Display the splash picture at startup
	 * 
	 * @param frame
	 * @param info
	 */
	public static void showSplash(JWindow frame, JLabel info) {
		ImageIcon logoIcon = ImageLoader.getImageIcon(Translator
				.getString("Splash"));
		info.setForeground(Color.black);
		JLabel lab = new JLabel(logoIcon) {
			public void paint(Graphics g) {
				super.paint(g);

				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setFont(new Font("Arial", Font.BOLD, 27));
				g2.setColor(Color.DARK_GRAY.darker());
				Composite originalComposite = g2.getComposite();
				g2.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 0.5f));

				g2.setFont(new Font("Arial", Font.PLAIN, 10));

				g2.drawString(VERSION, 18, 172);

				g2.setColor(Color.DARK_GRAY);
				g2.setFont(new Font("Arial", Font.BOLD, 8));
				String copyright = Translator.getString("Copyright");
				if (copyright != null)
					g2.drawString(copyright, 10, 202);
				g2.setComposite(originalComposite);
			}
		};

		frame.getContentPane().add(lab, BorderLayout.CENTER);
		lab.setLayout(new BorderLayout());
		lab.add(info, BorderLayout.SOUTH);
		lab.setBorder(BorderFactory.createRaisedBevelBorder());
		info.setVerticalAlignment(SwingConstants.CENTER);
		info.setHorizontalAlignment(SwingConstants.CENTER);
		info.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		info.setPreferredSize(new Dimension(lab.getWidth(), 24));
		info.setForeground(Color.WHITE);
		frame.pack();
		Utilities.center(frame);
		frame.setVisible(true);
		info.setText("Starting...");
	}
}
