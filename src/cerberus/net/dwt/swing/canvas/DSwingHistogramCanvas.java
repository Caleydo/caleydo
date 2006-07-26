/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.canvas;

import java.awt.LayoutManager;
import java.awt.Color;
import java.util.Vector;
import java.util.Iterator;
import java.awt.Rectangle;
import java.io.IOException;

import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.border.LineBorder;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import cerberus.manager.DComponentManager;
import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.Set;
import cerberus.data.collection.view.ViewCanvas;
import cerberus.command.CommandListener;
import cerberus.net.dwt.DNetEvent;
import cerberus.net.dwt.DNetEventComponentInterface;
import cerberus.net.dwt.DNetEventListener;

import cerberus.net.dwt.swing.parser.DSwingHistogramCanvasHandler;
import cerberus.net.dwt.swing.component.DSwingJPanel;
import cerberus.net.protocol.interaction.SuperMouseEvent;
//import cerberus.util.exception.PrometheusRuntimeException;

import cerberus.math.statistics.histogram_old.HistogramCreator;
import cerberus.math.statistics.histogram_old.HistogramCreatorSimple;

import cerberus.xml.parser.DParseSaxHandler;

/**
 * @author Michael Kalkusch
 *
 */
public class DSwingHistogramCanvas 
extends DSwingJPanel 
implements DNetEventComponentInterface, ViewCanvas
{

	static final long serialVersionUID = 80008030;
	
	static final private int iTabOffsetXML = 2;
	
	protected int iDNetEventComponentId;
	
	protected HistogramCreator refHistogramCreator;
	
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
		
	private boolean bHistogramIsValid = false;
	
	protected GeneralManager refGeneralManager;
	
	protected Set refCurrentSet = null;
	
//	/**
//	 * @param arg0
//	 * @param arg1
//	 */
//	public DSwingHistogramCanvas(LayoutManager arg0, boolean arg1) {
//		super(arg0, arg1);
//		initDPanel();
//	}
//
//	/**
//	 * @param arg0
//	 */
//	public DSwingHistogramCanvas(LayoutManager arg0) {
//		super(arg0);
//		initDPanel();
//	}
//
//	/**
//	 * @param arg0
//	 */
//	public DSwingHistogramCanvas(boolean arg0) {
//		super(arg0);
//		initDPanel();
//	}


	/**
	 * 
	 */
	public DSwingHistogramCanvas( GeneralManager refGeneralManager, Set setRefCurrentSet ) {
		super();
		
		this.refGeneralManager= refGeneralManager;
		refCurrentSet = setRefCurrentSet;
		
		initDPanel();
	}
	
	
	private void initDPanel() {
		vecRefComponentCildren = new Vector<DNetEventComponentInterface>();
		
		verRefDNetEventListener = new  Vector<DNetEventListener>();
		
		vecRefCommandListener = new Vector<CommandListener>(); 
		
		this.add( new JLabel("HISTOGRAM") );
		
		refHistogramCreator = new HistogramCreatorSimple( refCurrentSet );
		
		refHistogramCreator.setRowWidth( 100 );
	}
	
	/**
	 * Set the current object, that creates the histogram.
	 * 
	 * @param set the current creator of histogram
	 */
	public void setHistogramCreator( final HistogramCreator setHistogramCreator ) {
		this.refHistogramCreator = setHistogramCreator;
	}
	
	/**
	 * Get the current object, that creates the histogram.
	 * 
	 * @return current histogram handler
	 */
	public HistogramCreator getHistogramCreator() {
		return refHistogramCreator;
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
	
	public final void setParentCreator( final DComponentManager creator) {
		refParentCreator = creator; 
	}
	
	public final void setParentComponent( final DNetEventComponentInterface parentComponent) {
		setParentComponent = parentComponent; 
	}

	public void setSet( Set setRefCurrentSet ) {
		refCurrentSet = setRefCurrentSet;
		refHistogramCreator.setSet( setRefCurrentSet );	
		refHistogramCreator.updateState();
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
			final DSwingHistogramCanvasHandler refHistogramSaxHandler = 
				(DSwingHistogramCanvasHandler) refSaxHandler;
			
			/**
			 * Test if GUI component does already exist...
			 */			
			if ( iDNetEventComponentId != refHistogramSaxHandler.getXML_dNetEvent_Id() ) {
				getManager().unregisterItem( iDNetEventComponentId, 
						ManagerObjectType.VIEW_HISTOGRAM2D );
				
				setId( refHistogramSaxHandler.getXML_dNetEvent_Id() );
			}
			
			getManager().registerItem( this, 
					iDNetEventComponentId, 
					ManagerObjectType.VIEW_HISTOGRAM2D );
			
			this.setVisible( refHistogramSaxHandler.getXML_state_visible() );
			this.setEnabled( refHistogramSaxHandler.getXML_state_enabled() );
			this.setToolTipText( refHistogramSaxHandler.getXML_state_tooltip() );
			this.setName( refHistogramSaxHandler.getXML_state_label() );
			this.setBounds( refHistogramSaxHandler.getXML_position_x(),
					refHistogramSaxHandler.getXML_position_y(),
					refHistogramSaxHandler.getXML_position_width(),
					refHistogramSaxHandler.getXML_position_height() );
			
			this.setBorder( new LineBorder( Color.RED ) );
			
			try {
				setSet( (Set) refGeneralManager.getItem( 
						refHistogramSaxHandler.getXML_link2Target_SetId() ) );	
			}
			catch (NullPointerException npe) {
				assert false:"can not cast obejct from Id";
			}
			
			/**
			 * Register Buttons and subcomponents...
			 */
//			Iterator<Integer> iter = refHistogramSaxHandler.getXML_Iterator_NetEventCildrenComponentId();
//			
//			while ( iter.hasNext() ) {
//				Integer itemId = iter.next();
//				DNetEventComponentInterface itemRef = refParentCreator.getItemSet( itemId );
//				
//				if ( itemRef == null ) {
//					throw new CerberusRuntimeException("DPanel.setMementoXML_usingHandler() ERROR during iterator due to not existing itemID= [" +
//							itemId + "]");
//				}
//				this.add( (JComponent) itemRef );
//			}
			
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
		
		//FIXME test type...
		
		System.out.println(" DPanel.callbackForParser() ");
	
		Graphics g = ((JComponent) refParentCreator).getGraphics();
		this.paint( g );
		
		g.setColor( Color.RED );
		g.fillRect( this.getX(), this.getY(), this.getWidth(), this.getWidth() );
	}
	
	/**
	 * Get the type of this object.
	 * 
	 * @return type of this object
	 */
	public ManagerObjectType getBaseType() {
		return ManagerObjectType.VIEW_HISTOGRAM2D;
	}

	public void paintComponent( Graphics g ) {
		paintDComponent( g );
	}
	
	public void paintDComponent( Graphics g ) {
		
		super.paintComponent(g);
		
		if ( ! bHistogramIsValid ) {
			
			refHistogramCreator.updateState();
			
			bHistogramIsValid = true;
		}
		
		//final int iCounterRows = refHistogramCreator.getRowWidth();
		
		final int[] iCounterPerRow = refHistogramCreator.getCounterPerRow();
		
		boolean bAlternateColor = true;
		
		final int iHistogramCell_height_Y = 1;
		final int iHistogramCell_width_X = 3;
		
		final  int iHistogramCell_offsetX = 10;
		final  int iHistogramCell_offsetY = 10
			+ refHistogramCreator.getMaxCountPerRow()*iHistogramCell_height_Y;
		
		final  int iHistogramCell_incX = 5;			
		
		int iHistogramCell_PosX = iHistogramCell_offsetX;
		int iHistogramCell_PosY = iHistogramCell_offsetY;
		
		for ( int i=0; i<iCounterPerRow.length; i++ ) {
			
			if ( bAlternateColor ) {
				g.setColor( Color.GREEN );
				bAlternateColor = false;
			}
			else {
				g.setColor( Color.YELLOW );
				 bAlternateColor = true;
			}
			
			int iX1 = iHistogramCell_PosX;
			int iX2 = iHistogramCell_width_X;			
			int iY2= iHistogramCell_height_Y * (iCounterPerRow[i]);
			int iY1= iHistogramCell_PosY - iY2;
			
			
			//iHistogramCell_PosY += iHistogramCell_incY;
			
			g.fillRect( iX1,iY1,iX2,iY2 );
			
			g.setColor( Color.BLACK );
			g.drawRect( iX1,iY1,iX2,iY2 );
			
		
			
			iHistogramCell_PosX += iHistogramCell_incX;
			
		} // end for...
		
		g.drawLine( iHistogramCell_offsetX,
				iHistogramCell_offsetY,
				iHistogramCell_offsetX + 
					((int) (iCounterPerRow.length+1)*iHistogramCell_incX ) + 
					iHistogramCell_width_X,
				iHistogramCell_offsetY );
		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.ViewCanvas#updateState()
	 */
	public void updateState() {
		refHistogramCreator.updateState();
	}
}
