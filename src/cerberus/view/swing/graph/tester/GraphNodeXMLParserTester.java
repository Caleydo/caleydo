/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.view.swing.graph.tester;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.BorderLayout;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import cerberus.manager.CommandManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.view.swing.graph.DualNode;
import cerberus.view.swing.graph.parser.NodeSaxDefaultHandler;
import cerberus.view.swing.graph.visitor.NodeVisitorRenderer;

import cerberus.command.CommandType;
import cerberus.command.window.CmdWindowNewIFrameHistogram2D;
import cerberus.command.window.CmdWindowNewIFrameHeatmap2D;

//import cerberus.data.manager.BaseManagerType;
import cerberus.data.collection.Set;
import cerberus.data.collection.parser.CollectionFlatStorageParseSaxHandler;
import cerberus.data.collection.parser.CollectionSelectionParseSaxHandler;
import cerberus.data.collection.parser.CollectionSetParseSaxHandler;
import cerberus.net.dwt.swing.collection.DSwingSelectionCanvas;
import cerberus.net.dwt.swing.collection.DSwingStorageCanvas;
import cerberus.net.dwt.swing.canvas.DSwingHistogramCanvas;
import cerberus.net.dwt.swing.canvas.DSwingHeatMap2DCanvas;
import cerberus.xml.parser.DParseBaseSaxHandler;
import cerberus.net.dwt.swing.parser.DSwingHistogramCanvasHandler;
import cerberus.net.dwt.swing.menu.DMenuBootStraper;
import cerberus.net.dwt.swing.mdi.DDesktopPane;
import cerberus.net.dwt.swing.mdi.DInternalFrame;

//import cerberus.data.xml.MementoNetEventXML;
import cerberus.data.xml.MementoCallbackXML;


/**
 * Creates the hole application state from a XML file.
 * 
 * @author Michael Kalkusch
 *
 */
public class GraphNodeXMLParserTester extends JFrame {
//implements MementoCallbackXML {


	private GraphNodeAWTCanvasTester canvas;
	
	//protected FlatStorageSimple storage;
	
	/**
	 * 
	 */
	public GraphNodeXMLParserTester() {
		super("XML BootStrapper");
		
		canvas = new GraphNodeAWTCanvasTester();
		
//		this.getContentPane().setLayout( new BorderLayout() );
//		this.getContentPane().add( canvas, BorderLayout.CENTER );				
		
		setLayout( new BorderLayout() );
		add( canvas, BorderLayout.CENTER );				
		
		
		setSize( 800, 500 );
		//XmlBootStrapper.setLocation( 2000, 30);
		setLocation( 300, 30);
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setVisible( true );
	}

	
//	public void callbackForParser( final String tag_causes_callback,
//			final DParseSaxHandler refSaxHandler) {
//		
//	}
	
	public boolean parseOnce( InputSource inStream, 
			NodeSaxDefaultHandler handler) {
		
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();			
			reader.setContentHandler( handler );
			
			try {						
				reader.parse( inStream );
			} 
			catch ( IOException e) {
				System.out.println(" EX " + e.toString() );
			}
			
			/**
			 * Use data from parser to restore state...
			 */ 
			
			System.out.println("PARSE DONE\n  INFO:" +
					handler.getErrorMessage() );
			
			/**
			 * Restore state...
			 */
		
			inStream.getCharacterStream().close();
		}
		catch (SAXException se) {
			System.out.println("ERROR SE " + se.toString() );
		} // end try-catch SAXException
		catch (IOException ioe) {
			System.out.println("ERROR IO " + ioe.toString() );
		} // end try-catch SAXException, IOException
		
		
		return true;
	}
	
	public boolean setMementoXMLusingFileName( final String filename ) {
		
		NodeSaxDefaultHandler currentXMLContentHandler
			= new NodeSaxDefaultHandler( );
		
		parseOnce( openInputStreamFromFile(filename),
				currentXMLContentHandler);
		
		
		System.out.println("PARSE done.");
	
		
		/**
		 * SELECTION is done...
		 */
		DualNode rootNode = currentXMLContentHandler.getRootNode();

		System.out.println("Tree:\n" +
				rootNode.toStringRecursively(" ") );
		
		NodeVisitorRenderer visitor = new NodeVisitorRenderer();				
		visitor.calculatePoints( rootNode );
		
		canvas.setGraph( visitor, rootNode );
		
		canvas.repaint();
		
		this.repaint();
		
		return true;
	}
	
	/**
	 * Opens a file and returns an input stream to that file.
	 * 
	 * @param sXMLFileName name abd path of the file
	 * @return input stream of the file, or null
	 */
	protected InputSource openInputStreamFromFile( final String sXMLFileName ) {
		
		try {
			File inputFile = new File( sXMLFileName );		
			FileReader inReader = new FileReader( inputFile );
			
			InputSource inStream = new InputSource( inReader );
			
			return inStream;
		}
		catch ( FileNotFoundException fnfe) {
			System.out.println("File not found " + fnfe.toString() );
		}
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		GraphNodeXMLParserTester XmlBootStrapper =
			new GraphNodeXMLParserTester();
		
		String sFileXML = "..\\data\\XML\\graph\\cerberus_graph_sample.xml";
		
		/**
		 * use arguments...
		 */
		if ( args.length > 0 ) {
			sFileXML = args[0];
		}
		
//		XmlBootStrapper.setSize( 800, 500 );
//		//XmlBootStrapper.setLocation( 2000, 30);
//		XmlBootStrapper.setLocation( 300, 30);
//		XmlBootStrapper.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//		XmlBootStrapper.setVisible( true );
		
		try {
			
			XmlBootStrapper.setMementoXMLusingFileName( sFileXML );		
			
		}
		catch ( Exception e) {
			System.out.println("ERROR: " + e.toString());
		}
		
		XmlBootStrapper.repaint();
	
		
		
		
	}
	
//	public void callbackForParser( final ManagerObjectType type,
//			final String tag_causes_callback,
//			final DParseSaxHandler refSaxHandler) {
//			
//	}

}
