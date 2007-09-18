package org.jgraph.plugins.library;

/*
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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jgraph.pad.coreframework.GPDocument;
import org.jgraph.pad.coreframework.GPGraphpad;
import org.jgraph.pad.coreframework.GPPluginInvoker.DocAwarePlugin;
import org.jgraph.pad.resources.Translator;
import org.jgraph.pad.util.Utilities;

public class LibraryDecorator implements DocAwarePlugin {

    /**
     * Splitpane between the libraries and the graph
     */
    protected GPSplitPane splitPane;

    /**
     * The left site of this document Shows the libraries
     * 
     */
    protected GPLibraryPanel libraryPanel;

    /**
     * true if the library expand is expanded
     */
    protected static boolean libraryExpanded;

    protected GPOverviewPanel gpOverviewPanel;

    public static final String LIBRARY_PLUGIN = "libraryPlugin";

    /*
     * (non-Javadoc)
     * 
     * @see org.jgraph.plugins.library.GPDocumentDecoratorInterface#decorateDocument(org.jgraph.pad.coreframework.GPDocument)
     */
    public void setDocument(GPDocument document) {
        libraryExpanded = new Boolean(Translator.getString("LibraryExpanded"))
                .booleanValue();
        libraryPanel = createLibrary(document.getGraphpad());
        gpOverviewPanel = new GPOverviewPanel(document.getGraphpad(), document
                .getGraph(), document);
        JPanel overviewPane = GPOverviewPanel.createOverviewPanel(document
                .getGraphpad(), document.getGraph(), document, gpOverviewPanel);
        JSplitPane librarySplit = new GPSplitPane(JSplitPane.VERTICAL_SPLIT,
                overviewPane, libraryPanel);
        librarySplit.setName("DocumentLibrary");

        splitPane = new GPSplitPane(GPSplitPane.HORIZONTAL_SPLIT, librarySplit,
                document.getScrollPane());
        splitPane.setName("DocumentMain");
        splitPane.setOneTouchExpandable(true);
        document.getDocComponent().add(BorderLayout.CENTER, splitPane);
        document.getPluginsMap().put(LIBRARY_PLUGIN, this);
    }

    private GPLibraryPanel createLibrary(GPGraphpad graphpad) {
        return new GPLibraryPanel(Translator.getString("LibraryName"),
                Utilities.tokenize(Translator.getString("LoadLibraries")),
                new Integer(Translator.getString("EntrySize")).intValue(),
                graphpad);
    }

    public GPLibraryPanel getLibraryPanel() {
        return libraryPanel;
    }

    public GPSplitPane getSplitPane() {
        return splitPane;
    }

    public void setSplitPane(GPSplitPane splitPane) {
        this.splitPane = splitPane;
    }
}
