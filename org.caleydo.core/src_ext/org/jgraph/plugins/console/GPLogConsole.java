/*
 * @(#)GPLogConsole.java	1.0 29.01.2003
 *
 * Copyright (C) 2003 luzar
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jgraph.plugins.console;

import java.awt.AWTEvent;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.net.URLEncoder;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jgraph.pad.coreframework.GPGraphpad;
import org.jgraph.pad.coreframework.GPPluginInvoker;
import org.jgraph.pad.coreframework.GPPluginInvoker.PadAwarePlugin;
import org.jgraph.pad.resources.ImageLoader;
import org.jgraph.pad.resources.Translator;
import org.jgraph.pad.util.NamedOutputStream;

/**
 * Shows the System.in and System.out in a nice JFrame.
 * 
 * The Frame looks like this:<br>
 * <img src="doc-files/GPLogConsole.jpg">
 * 
 * @author Thomas Genssler (FZI)
 * @author Sven Luzar
 */
public class GPLogConsole extends JFrame implements PadAwarePlugin {

	/**
	 * Title of the Frame
	 * 
	 */
	private String frameTitle = "";

	/**
	 * Card Layout for the Window
	 * 
	 */
	CardLayout cardLayout = new CardLayout();

	/**
	 * Text area for the System.err output
	 */
	// Note: Use the commented out line for JGraphAddons <= 1.0.4
	JTextArea stderrText = JGraphConsole.createErrConsole();

	// JTextArea stderrText = new JGraphConsole(System.err);
	/**
	 * ScrollPane for the System.out text area
	 */
	JScrollPane stdoutScrollPane = new JScrollPane();

	/**
	 * Text area for the System.out output
	 */
	// Note: Use the commented out line for JGraphAddons > 1.0.4
	JTextArea stdoutText = JGraphConsole.createOutConsole();

	// JTextArea stdoutText = new JGraphConsole(System.out);
	/**
	 * ScrollPane for the System.err text area
	 */
	JScrollPane stderrScrollPane = new JScrollPane();

	/**
	 * Tabbed pane for the System.out and System.err text area
	 */
	JTabbedPane jTabbedPane1 = new JTabbedPane();

	/**
	 * Icon for the Window
	 */
	Image myIcon = null;

	/**
	 * If <code>true</code>, the console will become visible when any system
	 * output occurs.
	 */
	boolean makeVisibleOnError = false;

	/** PopUpMenu for save and clear the output textareas */
	InternalPopupMenu popup = new InternalPopupMenu();
    
    public void setGraphpad(GPGraphpad pad) {
        pad.setLogConsole(this);
    }

	public GPLogConsole() {
		super();
		frameTitle = Translator.getString("Title");
		myIcon = ImageLoader.getImageIcon(Translator.getString("Icon"))
				.getImage();
		this.makeVisibleOnError = new Boolean(Translator
				.getString("Error.makeLogDlgVisibleOnError")).booleanValue();

		if ((frameTitle == null) || (frameTitle.equals(""))) {
			frameTitle = "Test drive";
		}

		this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Overriden, in order to be able to deal with window events */
	protected void processWindowEvent(WindowEvent e) {
		//
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			// only close the window when we are not in embedded mode
			// release resources and exit if we are not running embedded,
			// buttonImage.buttonEdge., as
			// part of another application
			// super.processWindowEvent(buttonEdge);
			this.dispose();
		}
	}

	/**
	 * Initialises the Swing components
	 * 
	 */
	private void jbInit() throws Exception {
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				this_windowClosing(e);
			}
		});
		this.setTitle(frameTitle);
		this.getContentPane().setLayout(cardLayout);
		if (myIcon != null)
			this.setIconImage(myIcon);

		// re-direct stderr and stdout
		redirect();

		stderrText.setForeground(Color.red);
		stderrText.setBackground(Color.lightGray);
		stderrText.setEditable(false);
		stderrText.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				stderrText_mouseClicked(e);
			}
		});
		stdoutText.setForeground(Color.black);
		stdoutText.setEditable(false);
		stdoutText.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				stdoutText_mouseClicked(e);
			}
		});
		jTabbedPane1.setTabPlacement(JTabbedPane.BOTTOM);
		jTabbedPane1.setMinimumSize(new Dimension(400, 400));
		jTabbedPane1.setPreferredSize(new Dimension(400, 400));
		this.getContentPane().add(jTabbedPane1, "jTabbedPane1"/* #Frozen */);
		jTabbedPane1
				.add(
						stdoutScrollPane,
						Translator
								.getString("StandardOut"/*
														 * #Finished:Original="Standard
														 * out"
														 */));
		jTabbedPane1
				.add(
						stderrScrollPane,
						Translator
								.getString("StandardError"/*
															 * #Finished:Original="Standard
															 * error"
															 */));
		stderrScrollPane.getViewport().add(stderrText, null);
		stdoutScrollPane.getViewport().add(stdoutText, null);

		// make sure the last updated log is always in front
		stdoutText.getDocument().addDocumentListener(
				new MyDocumentListener(this, jTabbedPane1, stdoutScrollPane));

		stderrText.getDocument().addDocumentListener(
				new MyDocumentListener(this, jTabbedPane1, stderrScrollPane));

		this.pack();

	}

	/*
	 * Sets the new OutputStream for System.out and System.err
	 * 
	 */
	private void redirect() {

		try {
			// NOte: Use the commented out line for JGraphAddons > 1.0.4
			JGraphConsole.createOutConsole();
			// new JGraphConsole(System.out);
			// System.out.println("Standard out has been re-directed");
			// Note: Use the commented out line for JGraphAddons > 1.0.4
			JGraphConsole.createErrConsole();
			// new JGraphConsole(System.err);
			// System.err.println("Standard error has been re-directed");
		} catch (Exception ex) {
			System.err
					.println("Error while re-directing the output. Ignoring...");
		}
	}

	/**
	 * disposes this window
	 */
	void this_windowDispose(WindowEvent e) {
		this.dispose();
	}

	/**
	 * closes this window
	 */
	void this_windowClosing(WindowEvent e) {
		this_windowDispose(e);
		System.exit(0);
	}

	/**
	 * Shows the popup menu for the System.out textarea
	 */
	void stdoutText_mouseClicked(MouseEvent e) {
		if (e.getModifiers() == InputEvent.META_MASK) {
			popup.setTextArea(stdoutText);
			popup.show(this.stdoutText, e.getX(), e.getY());
		}

	}

	/**
	 * Shows the popup menu for the System.err textarea
	 */
	void stderrText_mouseClicked(MouseEvent e) {
		if (e.getModifiers() == InputEvent.META_MASK) {
			popup.setTextArea(stderrText);
			popup.show(this.stderrText, e.getX(), e.getY());
		}

	}
}

/**
 * Document listener to detect changes at the text areas and switches the right
 * one text area to front.
 */

class MyDocumentListener implements DocumentListener {
	/**
	 * The Tabbed pane to switch the right one text area to front
	 */
	private JTabbedPane paneToSwitch = null;

	/**
	 * The component which is in front
	 */
	private Component componentInFront = null;

	/**
	 * The parent container
	 */
	private GPLogConsole lc = null;

	/**
	 * creats an instance of this listener
	 * 
	 */
	public MyDocumentListener(GPLogConsole l, JTabbedPane paneToSwitch,
			Component inFront) {
		this.paneToSwitch = paneToSwitch;
		this.componentInFront = inFront;
		this.lc = l;
	}

	/**
	 * Calls getInFront()
	 * 
	 * @see #getInFront
	 * 
	 */
	public void changedUpdate(DocumentEvent e) {
		getInFront();
	}

	/**
	 * Calls getInFront()
	 * 
	 * @see #getInFront
	 * 
	 */
	public void insertUpdate(DocumentEvent e) {
		getInFront();
		if (lc.makeVisibleOnError) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					lc.setVisible(true);
					lc.toFront();
				}
			});
		}
	}

	/**
	 * Calls getInFront()
	 * 
	 * @see #getInFront
	 * 
	 */
	public void removeUpdate(DocumentEvent e) {
		getInFront();
	}

	/**
	 * Switches the rights one text area to front
	 */
	void getInFront() {
		// bring the attached component in front
		paneToSwitch.setSelectedComponent(this.componentInFront);
	}
}

/**
 * Internal Popup Menu with a clear and a save button to clear or save the text
 * areas.
 */
class InternalPopupMenu extends JPopupMenu {
	/**
	 * Menu item for clearing the text area
	 */
	JMenuItem jMenuItemClearWindow = new JMenuItem(Translator
			.getString("ClearOutput"/* #Finished:Original="Clear output" */));

	/**
	 * Menu item for saving the text area
	 */
	JMenuItem jMenuItemSaveToFile = new JMenuItem(Translator
			.getString("SaveToFile"/* #Finished:Original="Save to file..." */));

	/**
	 * Menu item for emailing the text area contents to tech support
	 */
	JMenuItem jMenuItemSendEmail = new JMenuItem(Translator
			.getString("Error.EmailTechSupport"));

	/**
	 * The current textarea
	 */
	private JTextArea currentWindow = null;

	/**
	 * creates an instance
	 */
	public InternalPopupMenu() {
		super();
		this.add(jMenuItemClearWindow);
		this.addSeparator();
		this.add(jMenuItemSaveToFile);
		this.add(jMenuItemSendEmail);

		jMenuItemClearWindow
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						clearWindow();
					}
				});
		jMenuItemSaveToFile
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveWindowToFile();
					}
				});
		jMenuItemSendEmail
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String email = Translator
								.getString("Error.EmailTechSupportAddress");
						String subj = "?subject=runtime error message&body=";
						String msg = null;
						try {
							// UTF8 is not exactly to spec for mailto: encoding
							// but it will work. only alternative is attaching
							// the javamail package at ~1.5Mb
							msg = URLEncoder.encode(currentWindow.getText(),
									"UTF-8");
						} catch (java.io.UnsupportedEncodingException ue) {
							System.out.println(ue);
						}

							GPPluginInvoker.openURL("mailto:" + email + subj
									+ msg);
					}
				});

		// jMenuItemSaveToFile.setEnabled(false);//.disable();
	}

	/**
	 * Sets the current text area
	 * 
	 */
	public void setTextArea(JTextArea ta) {
		currentWindow = ta;
	}

	/**
	 * clears the window
	 */
	private void clearWindow() {
		currentWindow.setText("");
	}

	/**
	 * Shows a file chooser and saves the file to the selected name
	 */
	private void saveWindowToFile() {
		String log = "log";
		NamedOutputStream out = GPPluginInvoker.provideOutputStream(".jgx", log,
				false);
		PrintStream os = new PrintStream(out.getOutputStream());
		os.println(currentWindow.getText());
		os.close();
		clearWindow();
	}
}
