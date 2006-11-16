/**
 * 
 */
package cerberus.view.swing.loader;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.BorderLayout;
//import java.awt.event.InputEvent;
//import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import cerberus.manager.IGeneralManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader;
//import javax.swing.JMenu;
//import javax.swing.JMenuBar;
//import javax.swing.JMenuItem;
//import javax.swing.KeyStroke;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.set.SetFlatSimple;
import cerberus.data.collection.storage.FlatThreadStorageSimple;
import cerberus.data.collection.virtualarray.VirtualArraySingleBlock;


/**
 * @author kalkusch
 *
 */
public class FileLoader {

	protected ISet refSet;
	
	protected IVirtualArray refSelection;
	
	protected IStorage refStorage;
	
	protected IGeneralManager regGeneralManager;
	
	private MicroArrayLoader loader;
	
	private String sText = "";
	
	private File curDir;
	
	public String sFileName = "D:\\src\\java\\cerberus\\data\\MicroArray\\gpr_format";
	//public String sFileName = "D:\\src\\Java\\JOGL\\Prometheus\\data\\MicroArray\\gpr_format\\GPR-Files_Unigene3 Hbg1-2";
	//public String sFileName = "D:\\src\\Java\\JOGL\\Prometheus\\data\\MicroArray\\gpr_format\\tests";
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		FileLoader myFileLoader = new FileLoader();

	}
	
	public FileLoader() {
		init();
	}
	
	private void init() {
		regGeneralManager = new OneForAllManager(null);		
		((OneForAllManager) regGeneralManager).initAll();
		
		refSet = new SetFlatSimple(1,regGeneralManager);
		
		refSelection = new VirtualArraySingleBlock(2,regGeneralManager,null);
		
		refStorage = new FlatThreadStorageSimple(3,regGeneralManager,null);
		
		loader = new MicroArrayLoader(regGeneralManager);
	}
	
	public void setText( String sUseText) {
		sText = sUseText;
	}
	
	public void load() {
			
		final File currentDir_gpr = 
			new File( sFileName );
		

          final JFrame jf_loaderSkipPattern = new JFrame("GRP Loader import pattern" + sText);
          final JTextField jtf_skipPattern = 
        	  new JTextField("SKIP;SKIP;SKIP;SKIP;SKIP;SKIP;SKIP;INT");
          JButton jb_skipPatternIsSet = new JButton("load..");
          
          jb_skipPatternIsSet.addActionListener( new ActionListener() {
        	  
        	  /* nested class for handling button event */
        	  
        	  public void actionPerformed( ActionEvent ae) {
 
        		  JFileChooser chooser = new JFileChooser(curDir);
                  FileFilter filter = new FileFilterGRP();
                  chooser.setFileFilter( filter );
                  chooser.setCurrentDirectory( currentDir_gpr );
                  
                  //jf_loaderSkipPattern.setVisible( false );
                  jf_loaderSkipPattern.setEnabled( false );
                  
        		  int res = chooser.showOpenDialog(null);
                  if (res == JFileChooser.APPROVE_OPTION) {
                    File chosen = chooser.getSelectedFile();
                    if (chosen != null) {
                      curDir = chosen.getParentFile();
                      
                      //hide skip pattern frame...
                      jf_loaderSkipPattern.setVisible( false );
                      
                      String sfileName = chosen.getAbsolutePath();
                      System.out.println("filename: " + sfileName );
                      loader.setFileName(chosen.getAbsolutePath());
                      loader.setTokenPattern( jtf_skipPattern.getText() );
                      //loader.setFileDataStorage( 
                    //		  refSet.getStorageByDimAndIndex(0,0) );
                      loader.setTargetSet( refSet );
                      loader.loadData();
                      System.out.println("filename: " + sfileName + "  ... has been loaded." );
                      
                      // remove skip pattern frame...
                      jf_loaderSkipPattern.dispose();
                      
                    } //end: if (chosen != null) {
                  } //end: if (res == JFileChooser.APPROVE_OPTION) {
                  
                  
        	  } // end: actionPerformed()..
          });
          
          jf_loaderSkipPattern.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
          jf_loaderSkipPattern.setLayout( new FlowLayout() );
          jf_loaderSkipPattern.add( new JLabel("import pattern"));
          jf_loaderSkipPattern.add( jtf_skipPattern );
          jf_loaderSkipPattern.add( jb_skipPatternIsSet );
          jf_loaderSkipPattern.setResizable(false);
          jf_loaderSkipPattern.pack();          
          jf_loaderSkipPattern.setVisible(true);
          
	}
	
	public ISet getSet() {
		return this.refSet;
	}
	
	public IVirtualArray getSelection() {
		return this.refSelection;
	}
	
	public IStorage getStorage() {
		return this.refStorage;
	}

	public void setSet( ISet setRefSet) {
		this.refSet = setRefSet;
	}
	
	public void setSelection( IVirtualArray setRefSelection ) {
		this.refSelection = setRefSelection;
	}
	
	public void setStorage( IStorage setRefStorage ) {
		this.refStorage = setRefStorage;
		loader.setFileDataStorage( setRefStorage );
	}
	
	public class FileFilterGRP extends FileFilter {
		
		public final static String FILEEXTENSION_GPR = "gpr";
		
		 //Accept all directories and all gif, jpg, tiff, or png files.
	    public boolean accept(File f) {
	        if (f.isDirectory()) {
	            return true;
	        }

	        String extension = getExtension(f);
	        if (extension != null) {
	            if (extension.equalsIgnoreCase(FILEEXTENSION_GPR)) {
	                    return true;
	            } else {
	                return false;
	            }
	        }

	        return false;
	    }

	    //The description of this filter
	    public String getDescription() {
	        return "MicroArray dataset (*.gpr)";
	    }
	    
	    /**
	     * get extension if is does exist.
	     * 
	     * @param f File
	     * @return extension or null if no extension was available
	     */
	    private String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
	            ext = s.substring(i+1).toLowerCase();
	        }
	        return ext;
	    }
	}
}
