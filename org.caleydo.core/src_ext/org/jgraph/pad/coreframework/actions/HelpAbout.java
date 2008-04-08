/*
 * @(#)HelpAbout.java	1.2 01.02.2003
 *
 * Copyright (C) 2001-2004 Gaudenz Alder
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
package org.jgraph.pad.coreframework.actions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Calendar;
import java.util.MissingResourceException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jgraph.pad.JGraphpad;
import org.jgraph.pad.coreframework.GPAbstractActionDefault;
import org.jgraph.pad.coreframework.GPPluginInvoker;
import org.jgraph.pad.resources.ImageLoader;
import org.jgraph.pad.resources.Translator;

public class HelpAbout extends GPAbstractActionDefault {

	/** The about dialog for GPGraphpad
	 */
	protected JDialog aboutDlg;

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (aboutDlg == null) {
			// Create a frame containing an instance of
			// LibraryPanel.

			String iconName = Translator.getString("Logo");
			ImageIcon logoIcon = ImageLoader.getImageIcon(iconName);

			try {
				String title = Translator.getString("AboutFrameTitle");

				aboutDlg = new GPAboutDialog(graphpad.getFrame(), title, logoIcon);
			} catch (MissingResourceException mre) {
				aboutDlg =
					new GPAboutDialog(
						graphpad.getFrame(),
						"About GPGraphpad",
						logoIcon);
			}
		}
		aboutDlg.setVisible(true);
	}
	/** Empty implementation.
	 *  This Action should be available
	 *  each time.
	 */
	public void update(){};
    
	// TODO makes it works with parameters!
    public static class GPAboutDialog extends JDialog {

        public GPAboutDialog(Frame owner, String title, ImageIcon logo) {
            super(owner, title, true);
            setSize(new Dimension(450, 320));

            JTabbedPane mainTabs = new JTabbedPane();
            JPanel aboutPanel = new JPanel();
            JPanel creditsPanel = new JPanel();
            mainTabs.addTab(Translator.getString("About"), aboutPanel);
            mainTabs.addTab(Translator.getString("Credits"), creditsPanel);
            getContentPane().add(mainTabs);
            setLocationRelativeTo(owner);
            setResizable(false);

            // Construct About Panel
            JLabel lab1 = new JLabel(logo);
            JLabel lab2 = new JLabel(JGraphpad.VERSION);
            lab2.setFont(lab1.getFont().deriveFont(Font.PLAIN, 24));
            JLabel lab3 = new JLabel("Based on "+org.jgraph.JGraph.VERSION);
            lab3.setFont(lab3.getFont().deriveFont(Font.PLAIN, 12));
            JLabel lab4 =
                new JLabel("(C) 2001-"
                        + Calendar.getInstance().get(Calendar.YEAR)
                        + " JGraph.com. All rights reserved.");
            lab4.setFont(lab4.getFont().deriveFont(Font.PLAIN, 12));
            JLabel lab5 =
                new JLabel("Java:"+System.getProperty("java.version")+" OS: "+System.getProperty("os.name"));
            lab5.setFont(lab5.getFont().deriveFont(Font.PLAIN, 12));
            lab1.setBounds(10, 9, 20, 24);
            lab2.setBounds(40, 5, 360, 30);
            lab3.setBounds(40, 33, 360, 25);
            lab4.setBounds(40, 200, 360, 25);
            HTMLPane text = new HTMLPane();
            text.setOpaque(false);
            text.setText(
                Translator.getString("AboutText"));
            text.setBounds(40, 65, 400, 140);
            text.setFont(lab4.getFont());
            text.setEditable(false);
            aboutPanel.setLayout(null);
            aboutPanel.add(lab1);
            aboutPanel.add(lab2);
            aboutPanel.add(lab3);
            aboutPanel.add(text);
            aboutPanel.add(lab4);

            // Construct Credits Panel
            JTextArea credits = new JTextArea();
            creditsPanel.setLayout(new BorderLayout());
            creditsPanel.add(new JScrollPane(credits), BorderLayout.CENTER);
            credits.setOpaque(false);
            credits.setText(
                    "The following people and groups have made the JGraph\n"
                + "Project possible:\n"
                + "Thanks to Prof. Moira Norrie, Prof. Bernhard Plattner\n"
                + "and Prof. Gerhard Tr�ster at the Federal Institute of \n"
                + "Technology (www.ethz.ch) for their support!\n\n"
                + "Beat Signer from the Global Information Systems Group\n"
                + "was instrumental in helping to get this project off\n"
                + "the ground. He arranged that JGraph could be handed-\n"
                + "in as a semester work.\n\n"
                + "The new design was strongly influenced by Men Muheim's\n"
                + "experiments with JGraph in another project. Thanks to\n"
                + "Men for the redesign, and willingness to accept this\n"
                + "work as a diploma thesis.\n\n"
                + "Christophe Avare translated GPGraphpad to French,\n"
                + "Shinji Nakamatsu provided the Japenese, Indosian and\n"
                + "Thai versions. Thomas Suter, Lars Gersmann, Markus\n"
                + "Schmidt, Antonio Caliano, Martina Huber and Andri\n"
                + "Kr�mer suggested new features or read the drafts of\n"
                + "the documentation. Farrukh Najmi helped to put up the\n"
                + "CVS repository, and Alex Shapiro implemented a cool\n"
                + "automatic layout for GPGraphpad (www.touchgraph.com).\n"
                + "Claudio Rosati and David Larsson submitted bug fixes\n"
                + "and new features, Francesco Candeliere translated the\n"
                + "paper to Italian, and Michael Lawley has ported the\n"
                + "documentation to the Docbook format.\n\n"
                + "Special thanks to Van Woods, Dennis Daniels, Hallvard\n"
                + "Tr�tteberg, Jenya Burstein, Sven Luzar, Rapha�l Valyi\n"
                + "and all others who are helping to improve JGraph and/or\n"
                + "JGraphpad up to this day!\n\n"
                + "The website and CVS repository are kindly hosted by\n"
                + "sourceforge.net.\n");
            credits.setCaretPosition(0);
            credits.setEditable(false);
        }

        // Close on escape
        protected JRootPane createRootPane() {
            KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
            JRootPane rootPane = new JRootPane();
            rootPane.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    setVisible(false);
                }
            }, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
            return rootPane;
        }

    }
    
    
    

    /**
     * This pane extends JEditorPane's functionality to add support for
     * HTML related display and behavior. Specifically, it handles the necessary
     * implementation to support URL tooltip support and launching of a platform
     * specific HTML/Internet browser.
     *
     * @author Van Woods
     */
    public static class HTMLPane extends JEditorPane {
        protected CustomLinkHandler linkHandler = new CustomLinkHandler(this);
        protected boolean toolTipOriginalEnabledStatus = true;

        /**
         * Convenience constructor with no required parameters which automatically
         * creates an uneditable editor instance.
         */
        public HTMLPane() {
            this(false);
        }

        /**
         * Constructor for the HTMLPane object
         *
         * @param editable  If <code>true</code>, this component supports HTML editing.
         * This class only manages the tooltip and URL mouse events, not any
         * graphical layout and editing capability. This class could be used, however,
         * in building such a tool as it does have support for disabling URL tracking
         * during editing (ie no launch of browser or tooltip display).
         */
        public HTMLPane(boolean editable) {
            this.setEditable(editable);

            // make sure type is html
            this.setContentType("text/html");

            // register listener responsible for launching browser
            this.addHyperlinkListener(linkHandler);

            // initiliaze tooltip
            toolTipOriginalEnabledStatus = ToolTipManager.sharedInstance().isEnabled();
            ToolTipManager.sharedInstance().registerComponent(this);
        }

        /**
         * Override tool tip method to display URL
         *
         * @param event  event passed
         * @return       tooltip as URL
         */
        public String getToolTipText(MouseEvent event) {
            if (linkHandler.isHoveringOverHyperlink() && (linkHandler.getHoveredURL() != null)) {
                // have to manually toggle tooltip enabled status to prevent empty
                // tooltip from appearing when not hovering over url
                ToolTipManager.sharedInstance().setEnabled(true);
                return linkHandler.getHoveredURL();
            }
            ToolTipManager.sharedInstance().setEnabled(false);
            return null;
        }

        /**
         * Override Swing's poor label position choice. The new behaviour
         * shows the label relative to the current location of the mouse.
         *
         * @param event  tool tip location event
         * @return       tool tip location
         */
        public Point getToolTipLocation(MouseEvent event) {
            return new Point(event.getX() + 10, event.getY() + 25);
        }

        /**
         * Determines if current mouse location is hovering over a hyperlink.
         * Remember, <code>CustomLinkHandler</code> is NOT notified of hyperlink
         * events if editing is enabled by defintion in JEditorPane. In otherwords,
         * when HTML code is being displayed, then hyperlink tracking is not occuring.
         *
         * @return   <code>true</code> if mouse if hovering over hyperlink and pane
         * is not editable
         */
        public boolean isHoveringOverHyperlink() {
            return linkHandler.isHoveringOverHyperlink();
        }

        /**
         * Gets the URL being hovered over.
         *
         * @return   The URL value if mouse is currently hovering over a URL, or
         * <code>null</code> if not currently hovering over a URL
         */
        public String getHoveredURL() {
            return linkHandler.getHoveredURL();
        }


//     *****************************************************************************

        /**
         * Handles URL hyperlink events and provides status information.
         *
         * @author    Van Woods
         */
        protected class CustomLinkHandler implements HyperlinkListener {
            protected JEditorPane pane = null;
            protected boolean isHovering = false;
            protected String hoveredURLString = null;

            /**
             * Constructor for the CustomLinkHandler object
             *
             * @param inpane  Description of Parameter
             */
            public CustomLinkHandler(JEditorPane inpane) {
                this.pane = inpane;
            }

            /**
             * Prevent class from being instantiated without required parameter.
             */
            private CustomLinkHandler() {
            }

            /**
             * Determines if current mouse location is hovering over a hyperlink.
             * Remember, <code>CustomLinkHandler</code> is NOT notified of hyperlink
             * events if editing is enabled by defintion in JEditorPane. In otherwords,
             * when HTML code is being displayed, then hyperlink tracking is not occuring.
             *
             * @return   true if mouse if hovering over hyperlink and pane is not editable
             */
            public boolean isHoveringOverHyperlink() {
                // check if pane is editable as caller could have changed editability after
                // hyperlinkUpdate was fired causing indeterminability in hovering status
                if (pane.isEditable()) {
                    return false;
                }
                return isHovering;
            }

            /**
             * Gets the URL being hovered over.
             *
             * @return   The URL value if mouse is currently hovering over a URL, or
             * <code>null</code> if not currently hovering over a URL
             */
            public String getHoveredURL() {
                return hoveredURLString;
            }

            /**
             * Launch browser if hyperlink is clicked by user. Will go to existing opened
             * browser if one exists. If not clicked, then store url pointed to.
             *
             * @param e  event passed by source
             */
            public void hyperlinkUpdate(HyperlinkEvent e) {
                // track mouse enters and exits
                if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                    isHovering = true;
                    URL url = e.getURL();
                    if (url != null) {
                        hoveredURLString = url.toExternalForm();
                    }
                    else {
                        // error case
                        hoveredURLString = null;
                    }
                    //System.out.println("hyperlinkUpdate fired");
                    //System.out.println("     entered->");
                }
                else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
                    isHovering = false;
                    hoveredURLString = null;
                    //System.out.println("     <-exited");
                }

                // launch native browser if URL is clicked
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        GPPluginInvoker.openURL(e.getURL().toString());
                    }
                    catch (Exception ex) {
                        //Utilities.errorMessage(Translator.getString("Error.DealingWithBrowser"/*#Finished:Original="Error dealing with browser."*/), ex);
                        System.out.println("Error dealing with browser."/*#Frozen*/);
                        ex.printStackTrace();
                    }
                }
            }
        }
    }


}
