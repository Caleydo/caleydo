/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.component;

import java.awt.LayoutManager;
import java.awt.Color;
import java.util.Vector;
import java.util.Iterator;
import java.awt.Rectangle;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.border.LineBorder;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import cerberus.manager.DComponentManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.command.CommandListener;
import cerberus.net.dwt.DNetEvent;
import cerberus.net.dwt.DNetEventComponentInterface;
import cerberus.net.dwt.DNetEventListener;
import cerberus.net.dwt.swing.parser.DPanelSaxHandler;
import cerberus.xml.parser.DParseSaxHandler;
import cerberus.net.protocol.interaction.SuperMouseEvent;
import cerberus.util.exception.PrometheusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public class DPanel 
extends JPanel 
implements DNetEventComponentInterface {

	static final long serialVersionUID = 80008030;
	
	static final private int iTabOffsetXML = 2;
	
	protected int iDNetEventComponentId;
	
	/**
	 * Reference to parent and/or creator of this class.
	 * Used to check, if id was changed by creator.
	 * 
	 * TODO: remove this from stable code!
	 */
	private DComponentManager refParentCreator;
	
	/**
	 * reference to parent object.
	 */
	private DNetEventComponentInterface setParentComponent = this;
	
	
	protected SuperMouseEvent refMouseNetEvent;
	
	/**
	 * stores references to Command listener objects.
	 */
	private Vector<CommandListener> vecRefCommandListener;
	
	private Vector<DNetEventComponentInterface> vecRefComponentCildren;
	
	private Vector<DNetEventListener> verRefDNetEventListener;
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public DPanel(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
		initDPanel();
	}

	/**
	 * @param arg0
	 */
	public DPanel(LayoutManager arg0) {
		super(arg0);
		initDPanel();
	}

	/**
	 * @param arg0
	 */
	public DPanel(boolean arg0) {
		super(arg0);
		initDPanel();
	}

	/**
	 * 
	 */
	public DPanel() {
		super();
		initDPanel();
	}

	private void initDPanel() {
		vecRefComponentCildren = new Vector<DNetEventComponentInterface>();
		
		verRefDNetEventListener = new  Vector<DNetEventListener>();
		
		vecRefCommandListener = new Vector<CommandListener>(); 
	}
	
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#addNetActionListener(cerberus.net.dwt.DNetEventListener)
	 */
	public void addNetActionListener(DNetEventListener addListener) {
		
		//TODO remove call in release version
		if ( verRefDNetEventListener.contains(addListener)) {
			assert false: "addNetActionListener() try to add existing listener";
			return;
		}
		
		verRefDNetEventListener.add( addListener );
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#handleNetEvent(cerberus.net.dwt.DNetEvent)
	 */
	public void handleNetEvent(DNetEvent event) {
		// TODO Auto-generated method stub
		
		/**
		 * promote event to children...
		 */
		Iterator<DNetEventListener> iter = verRefDNetEventListener.iterator();
		
		while ( iter.hasNext() ) {
			iter.next().netActionPerformed( event );
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#addCommandListener(cerberus.command.CommandListener)
	 */
	synchronized public boolean addCommandListener(CommandListener setCommandListener) {
		
		if ( vecRefCommandListener.contains(setCommandListener)) {
			return false;
		}
		
		vecRefCommandListener.add( setCommandListener );
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#containsNetEvent(cerberus.net.dwt.DNetEvent)
	 */
	public boolean containsNetEvent(DNetEvent event) {
		return this.getBounds().contains( 
				event.getSuperMouseEvent().getX(),
				event.getSuperMouseEvent().getY() );
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventComponentInterface#getNetEventComponent(cerberus.net.dwt.DNetEvent)
	 */
	public DNetEventComponentInterface getNetEventComponent(DNetEvent event) {
		if ( this.containsNetEvent( event ) ) {
			
			Iterator<DNetEventComponentInterface> iter = vecRefComponentCildren.iterator();
			
			for (int i=0; iter.hasNext(); i++ ) {
				DNetEventComponentInterface child = iter.next();
				if ( child.containsNetEvent( event ) ) {
					return child.getNetEventComponent( event );
				}
				
			} // end for
			return this;
			
		} // end if
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventMementoXML#getDNetEventId()
	 */
	public int getId() {
		return iDNetEventComponentId;
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventMementoXML#setDNetEventId(java.lang.Object, int)
	 */
	public void setId( final int iSetDNetEventId) {
		iDNetEventComponentId = iSetDNetEventId;
	}
	
	public final void setParentCreator( final DComponentManager creator) {
		refParentCreator = creator; 
	}
	
	public final void setParentComponent( final DNetEventComponentInterface parentComponent) {
		setParentComponent = parentComponent; 
	}


	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.xml.MementoNetEventXML#setMementoXML_usingHandler(cerberus.net.dwt.swing.parser.DParseSaxHandler)
	 */
	public synchronized boolean setMementoXML_usingHandler( final DParseSaxHandler refSaxHandler ) {
		
		try {
			/**
			 * TRy to cast refSaxHandler ...
			 */
			final DPanelSaxHandler refDButtonSaxHandler = (DPanelSaxHandler) refSaxHandler;
			
			/**
			 * Test if GUI component does already exist...
			 */			
			if ( iDNetEventComponentId != refDButtonSaxHandler.getXML_dNetEvent_Id() ) {
				return false;
			}
			
			this.setVisible( refDButtonSaxHandler.getXML_state_visible() );
			this.setEnabled( refDButtonSaxHandler.getXML_state_enabled() );
			this.setToolTipText( refDButtonSaxHandler.getXML_state_tooltip() );
			this.setName( refDButtonSaxHandler.getXML_state_label() );
			this.setBounds( refDButtonSaxHandler.getXML_position_x(),
					refDButtonSaxHandler.getXML_position_y(),
					refDButtonSaxHandler.getXML_position_width(),
					refDButtonSaxHandler.getXML_position_height() );
			
			this.setBorder( new LineBorder( Color.RED ) );
			
			/**
			 * Register Buttons and subcomponents...
			 */
			Iterator<Integer> iter = refDButtonSaxHandler.getXML_Iterator_NetEventCildrenComponentId();
			
			while ( iter.hasNext() ) {
				Integer itemId = iter.next();
				DNetEventComponentInterface itemRef = refParentCreator.getItemSet( itemId );
				
				if ( itemRef == null ) {
					throw new PrometheusRuntimeException("DPanel.setMementoXML_usingHandler() ERROR during iterator due to not existing itemID= [" +
							itemId + "]");
				}
				this.add( (JComponent) itemRef );
				
				
			}
			
			/**
			 * memento is applied now!
			 */
			return true;
						
		}
		catch (ClassCastException ce) {
			System.out.println("ERROR: DPanel.setMementoXML_usingHandler() wrong cast! " + ce.toString() );
			return false;
		}
		catch (NullPointerException ne) {
			System.out.println("ERROR: DPanel.setMementoXML_usingHandler() " + ne.toString() );
			return false;
		}
		
	}
	
	/**
	 * formating XML output.
	 * Talking locla tab offset into account. Thus  getTab(0) also is an important call.
	 * 
	 * @param iCountTabs number of tabs to be set starting with 0 tabs
	 * @return number of tabs created
	 */
	static private String getTab( final int iCountTabs ) {
		final String sTab ="  ";
		String tabResult = "";
		
		for ( int i=0; i<iCountTabs+iTabOffsetXML ; i++) {
			tabResult += sTab;
		}
		return tabResult;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventMementoXML#createMementoXML()
	 */
	public String createMementoXMLperObject() {
		/**
		 * XML Header
		 */
		String XML_MementoString = getTab(0) + "<DNetEventComponent dNetEvent_Id=\"" +
			this.iDNetEventComponentId + "\" label=\"DPanel Swing\">\n";
		XML_MementoString += getTab(1) + "<DNetEvent_type type=\"DPanel\"/>\n";
		XML_MementoString += getTab(1) + "<DNetEvent_details>\n";
		
		/**
		 * position of component
		 */
		final Rectangle rec = this.getBounds();
		XML_MementoString += getTab(2) + "<position x=\"" + rec.x + 
			"\" y=\"" + rec.y +
			"\" width=\"" + rec.width + 
			"\" height=\"" + rec.height + "\" />\n";
		
		/**
		 * State of component
		 */
		XML_MementoString += getTab(2) + "<state enabled=\"" + this.isEnabled() +
			"\" visible=\"" + this.isVisible() + 
			"\" label=\"" + this.getName() + 
			"\" tooltip=\"" + this.getToolTipText() + "\" />\n";	
		
		/**
		 * Layout manager
		 */
		XML_MementoString += getTab(2) + "<PanelLayout style=\"" +
			this.getLayout().getClass() + "\" />\n";
		
		/**
		 * XML footer
		 */
		XML_MementoString += getTab(1) + "</DNetEvent_details>\n<";	
		
		/**
		 * cildren...
		 */
		XML_MementoString += getTab(1) + "\n<SubComponents>\n";
		
		Iterator<DNetEventComponentInterface> iter = vecRefComponentCildren.iterator();
		
		for (int i=0; iter.hasNext(); i++ ) {
			DNetEventComponentInterface child = iter.next();
			XML_MementoString += getTab(2) + "<item  dNetEvent_Id=\"" +
				child.getId() + "\" >\n";
			XML_MementoString += getTab(3) + "<item_details></item_details>\n";
			XML_MementoString += getTab(2) + "</item>\n";
		}			
		
		XML_MementoString += getTab(1) + "</SubComponents>\n";	
		
		/**
		 * Link to NetEventListener ...
		 */
		XML_MementoString += getTab(1) + "<SubNetEventListener>\n";	
		
		Iterator<DNetEventListener> iterNetEvent = verRefDNetEventListener.iterator();
		
		while ( iterNetEvent.hasNext() ) {
			XML_MementoString += getTab(2) + "<NetListener  Id=\"" +			
				iterNetEvent.next().getId() + "\"></NetListener>\n";
		}
		XML_MementoString += getTab(1) + "</SubNetEventListener>\n";
		
		/**
		 * Link to CommandListener ...
		 */
		XML_MementoString += getTab(1) + "<SubCommandListener>\n";	
		
//		Iterator<CommandListener> iterCommand = vecRefCommandListener.iterator();
//		
//		while ( iterCommand.hasNext() ) {
//			XML_MementoString += getTab(2) + "<CmdListener  Id=\"" +			
//			iterCommand.next().getDNetEventId() + "\"></CmdListener>\n";
//		}
		XML_MementoString += getTab(1) + "</SubCommandListener>\n";
		
		
		XML_MementoString += getTab(0) + "</DNetEventComponent>\n\n";
		
		return XML_MementoString;		
	}

	/* (non-Javadoc)
	 * @see cerberus.net.dwt.DNetEventMementoXML#createMementoXML()
	 */
	public String createMementoXML() {
		String XML_MementoString = createMementoXMLperObject();
		
		Iterator<DNetEventComponentInterface> iter = vecRefComponentCildren.iterator();
				
		for (;iter.hasNext();) {
			XML_MementoString +=
				((DNetEventComponentInterface)iter.next()).createMementoXML();
		}
		
		return XML_MementoString;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.xml.MementoNetEventXML#callbackForParser(java.lang.String)
	 */
	public void callbackForParser(  final ManagerObjectType type,
			final String tag_causes_callback,
			final String details,
			final DParseSaxHandler refSaxHandler ) {
		
		System.out.println(" DPanel.callbackForParser() ");
	}

}
