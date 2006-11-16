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

import cerberus.manager.IDistComponentManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;

import gleem.linalg.Vec3f;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.view.IViewCanvas;
import cerberus.data.collection.virtualarray.iterator.VirtualArrayProxyIterator;
import cerberus.data.collection.IStorage;
import cerberus.command.ICommandListener;
import cerberus.net.dwt.DNetEvent;
import cerberus.net.dwt.DNetEventComponentInterface;
import cerberus.net.dwt.DNetEventListener;
import cerberus.net.dwt.base.ViewingAreaComponent;
import cerberus.net.dwt.swing.component.DSwingJPanel;
import cerberus.xml.parser.ISaxParserHandler;
import cerberus.net.dwt.swing.parser.DSwingHistogramCanvasHandler;
import cerberus.net.protocol.interaction.SuperMouseEvent;
import cerberus.util.exception.CerberusRuntimeException;


/**
 * @author Michael Kalkusch
 *
 */
public class DSwingHeatMap2DCanvas 
extends DSwingJPanel 
implements DNetEventComponentInterface, IViewCanvas, ViewingAreaComponent
{

	static final long serialVersionUID = 80008030;
	
	static final private int iTabOffsetXML = 2;


	protected Vec3f v3fPanPercent;
	
	protected Vec3f v3fWindowPercent;
	
	/**
	 * Reference to parent and/or creator of this class.
	 * Used to check, if id was changed by creator.
	 * 
	 * TODO: remove this from stable code!
	 */
	private IDistComponentManager refParentCreator;
	
	/**
	 * reference to parent object.
	 */
	private DNetEventComponentInterface setParentComponent = this;
	
	
	protected SuperMouseEvent refMouseNetEvent;
	
	/**
	 * stores references to Command listener objects.
	 */
	private Vector<ICommandListener> vecRefCommandListener;
	
	private Vector<DNetEventComponentInterface> vecRefComponentCildren;
	
	private Vector<DNetEventListener> verRefDNetEventListener;
		
	private boolean bHistogramIsValid = false;

	
	protected ISet refCurrentSet = null;
	
	
	private int iGui_RowsX = 0;
	
	private int iGui_ColumnsY = 0;
	
	/**
	 * product of iGui_RowsX tiems iGui_ColumnsY.
	 * ISet in setter only!
	 * Used to test if data array is large enough.
	 */
	private int iGui_RowsX_times_ColumnsY =0 ;
	
	private float[] fGui_ColorRangeRGBA;
	
	private float[] fGui_ColorLowRGBA;
	
	private int iGui_Color_lowValue;
	
	private int iGui_Color_highValue;
	
	private int iGui_HeatMapCell_height_Y = 4;
	private int iGui_HeatMapCell_width_X = 4;
	
	private int iGui_HeatMapCell_width_X_inc = 5;
	private int iGui_HeatMapCell_height_Y_inc = 5;
	
	/**
	 * Difference between iGui_Color_highValue - iGui_Color_lowValue;
	 */
	private float fGui_Color_rangeValue;
	
	private Color cGui_lowColor = Color.RED;
	
	private Color cGui_highColor = Color.GREEN;
	
	private IStorage refStorageFromSet;
	
	private IVirtualArray refSelectionFromSet;

	

	/**
	 * 
	 */
	public DSwingHeatMap2DCanvas( IGeneralManager refGeneralManager ) {
		super();
		initDPanel();
		refGeneralManager = refGeneralManager;
	}

	
	/**
	 * Sets 
	 * @param iRowsX
	 * @param iColumnsY
	 */
	public void setSetRationXY( final ISet refSet, 
			final int iRowsX, 
			final int iColumnsY ) {
		
		refCurrentSet = refSet;
		iGui_RowsX = iRowsX;
		iGui_ColumnsY = iColumnsY;
		
		iGui_RowsX_times_ColumnsY = iGui_RowsX * iGui_ColumnsY;
	}
	
	private void initDPanel() {
		vecRefComponentCildren = new Vector<DNetEventComponentInterface>();
		
		verRefDNetEventListener = new  Vector<DNetEventListener>();
		
		vecRefCommandListener = new Vector<ICommandListener>(); 
		
		this.add( new JLabel("HEATMAP") );

		// RGBA
		fGui_ColorRangeRGBA = new float[4];
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
	 * @see cerberus.net.dwt.DNetEventComponentInterface#addCommandListener(cerberus.command.ICommandListener)
	 */
	synchronized public boolean addCommandListener(ICommandListener setCommandListener) {
		
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


	

	
	public final void setParentCreator( final IDistComponentManager creator) {
		refParentCreator = creator; 
	}
	
	public final void setParentComponent( final DNetEventComponentInterface parentComponent) {
		setParentComponent = parentComponent; 
	}

	/**
	 * ISet the pixel ratio of the heatmap.
	 * Note: iGui_HeatMapCell_height_Y shall be smaller or equal than iGui_HeatMapCell_height_Y_inc 
	 * to avoid overlapping. Same goes for iGui_HeatMapCell_width_X and iGui_HeatMapCell_width_X_inc
	 * 
	 * @param iGui_HeatMapCell_width_X width per value in X 
	 * @param iGui_HeatMapCell_height_Y height per value in Y
	 * @param iGui_HeatMapCell_width_X_inc bounding box in X per value
	 * @param iGui_HeatMapCell_height_Y_inc bounding box in Y per value
	 */
	public final void setHeatmapPixelRatio( final int iGui_HeatMapCell_width_X,
		final int iGui_HeatMapCell_height_Y,
		final int iGui_HeatMapCell_width_X_inc,
		final int iGui_HeatMapCell_height_Y_inc) {
		
		this.iGui_HeatMapCell_height_Y 		= iGui_HeatMapCell_height_Y;
		this.iGui_HeatMapCell_width_X 		= iGui_HeatMapCell_width_X;
		this.iGui_HeatMapCell_width_X_inc 	= iGui_HeatMapCell_width_X_inc;
		this.iGui_HeatMapCell_height_Y_inc 	= iGui_HeatMapCell_height_Y_inc; 
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.xml.IMementoNetEventXML#setMementoXML_usingHandler(cerberus.net.dwt.swing.parser.DParseSaxHandler)
	 */
	public synchronized boolean setMementoXML_usingHandler( final ISaxParserHandler refSaxHandler ) {
		
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
				refCurrentSet = (ISet) refGeneralManager.getItem( 
						refHistogramSaxHandler.getXML_link2Target_SetId() );
					
				updateState();
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
		 * Link to ICommandListener ...
		 */
		XML_MementoString += getTab(1) + "<SubCommandListener>\n";	
		
//		Iterator<ICommandListener> iterCommand = vecRefCommandListener.iterator();
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
	 * @see cerberus.data.xml.IMementoNetEventXML#callbackForParser(java.lang.String)
	 */
	public void callbackForParser(  final ManagerObjectType type,
			final String tag_causes_callback,
			final String details,
			final ISaxParserHandler refSaxHandler ) {
		
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

	/**
	 * Define a linear color interpolation sceme.
	 * If lower value is largen than higher value the colors are swapped two.
	 * 
	 * @param setLowColor Color for lower value
	 * @param setHighColor Color for higher vale
	 * @param iSetLowValue lower value
	 * @param iSetHighValue higher value
	 */
	public void setColorInterpolation(
			final Color setLowColor,
			final Color setHighColor,
			final int iSetLowValue,
			final int iSetHighValue ) {
		
		if ( iSetLowValue <= iSetHighValue) {
			iGui_Color_lowValue = iSetLowValue;
			iGui_Color_highValue = iSetHighValue;
			
			/*
			 * calculate range..
			 */
			fGui_Color_rangeValue = (float)(iGui_Color_highValue - iGui_Color_lowValue);
			
			this.cGui_lowColor = setLowColor;
			this.cGui_highColor = setHighColor;
			
			float[] fArrayLow = setLowColor.getComponents( null );
			float[] fArrayHigh = setHighColor.getComponents( null );
			
			// precalculate color range between high and low color...
			for ( int i=0; i<4; i++ ){
				fGui_ColorRangeRGBA[i] = fArrayHigh[i] - fArrayLow[i];
			}
			
			fGui_ColorLowRGBA = setLowColor.getComponents( null );
		} 
		else {
			/**
			 * recursion, automatic swap of values!
			 */ 
			assert false:"setColorInterpolation() with swapped high and low values!";
		
			setColorInterpolation(setHighColor,setLowColor,iSetHighValue,iSetLowValue);
		}
		
	}
	
	private void setColorByValue( Graphics g, final int iValue) {
		
//		private int iGui_Color_lowValue;		
//		private int iGui_Color_highValue;
//		private Color cGui_lowColor = Color.RED;		
//		private Color cGui_highColor = Color.GREEN;
		
		if ( iValue <= iGui_Color_lowValue ) {
			//g.setColor( cGui_lowColor );
			g.setColor( Color.WHITE );
			return;
		}
		else if ( iValue >= iGui_Color_highValue ) {
			//g.setColor( cGui_highColor );
			g.setColor( Color.LIGHT_GRAY );
			return;
		}
		
		final float fPercentage = 
			((float)(iValue - iGui_Color_lowValue) ) / fGui_Color_rangeValue;
		
		
		final Color setNewColor = new Color( 
				fGui_ColorLowRGBA[0] + fPercentage * fGui_ColorRangeRGBA[0],
				fGui_ColorLowRGBA[1] + fPercentage * fGui_ColorRangeRGBA[1],
				fGui_ColorLowRGBA[1] + fPercentage * fGui_ColorRangeRGBA[1]
				);
		
		g.setColor( setNewColor );
	}
	
	public void paintComponent( Graphics g ) {
		paintDComponent( g );
	}
	
	public void paintDComponent( Graphics g ) {
		
		super.paintComponent(g);
		
		if ( ! bHistogramIsValid ) {
			
			updateState();
			
			bHistogramIsValid = true;
		}
		
		//final int iCounterRows = refHistogramCreator.getRowWidth();
		
		if (( refSelectionFromSet == null)||( this.refStorageFromSet == null)) {
			throw new CerberusRuntimeException("DSwingHEatMap2DCanvas.paintComponent() due to selction or storage references are null.");						
		}
		
		VirtualArrayProxyIterator iterSelection = new VirtualArrayProxyIterator(refSelectionFromSet);
		
		int[] iData_IntArray_FromStorage = this.refStorageFromSet.getArrayInt();
		
		/**
		 * Test if enough values are available...
		 */
		
		if ( iData_IntArray_FromStorage.length < iGui_RowsX_times_ColumnsY ) {
			throw new CerberusRuntimeException("DSwingHEatMap2DCanvas.paintComponent() storage provide to few values!");									
		}
		
		/**
		 * end heatmap algorithm
		 */

		final int iHeatMapCell_height_Y = iGui_HeatMapCell_height_Y;
		final int iHeatMapCell_width_X = iGui_HeatMapCell_width_X;
		
		final int iHistogramCell_offsetX = 10;
		final int iHistogramCell_offsetY = 10;
		
		final  int iHistogramCell_incX = iGui_HeatMapCell_width_X_inc;
		final  int iHistogramCell_incY = iGui_HeatMapCell_height_Y_inc;
		
		int iHistogramCell_PosX = iHistogramCell_offsetX;
		int iHistogramCell_PosY = iHistogramCell_offsetY;
		
		int iIndexInArray = 0;
		for ( int i=0; i< iGui_RowsX; i++ ) {
			
			for ( int j=0; j < this.iGui_ColumnsY; j++ ) {		
				
				//iHistogramCell_PosY += iHistogramCell_incY;
				
				setColorByValue( g, iData_IntArray_FromStorage[iIndexInArray] );
				g.fillRect( iHistogramCell_PosX,
						iHistogramCell_PosY,
						iHeatMapCell_width_X,
						iHeatMapCell_height_Y );
				
//				g.setColor( Color.BLACK );
//				g.drawRect( iHistogramCell_PosX,
//						iHistogramCell_PosY,
//						iHeatMapCell_width_X,
//						iHeatMapCell_height_Y );
											
				iHistogramCell_PosX += iHistogramCell_incX;
				iIndexInArray++;
				
			} // end for j ... iGui_ColumnsY...
			
			iHistogramCell_PosX = iHistogramCell_offsetX;
			iHistogramCell_PosY += iHistogramCell_incY;
			
		} // end for i ... iGui_RowsX...
		
//		g.drawLine();
		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.ViewCanvas#updateState()
	 */
	public void updateState() {
		
		if ( refCurrentSet != null ) {
			IStorage[] refStorageArray = refCurrentSet.getStorageByDim(0);
			IVirtualArray[] refSelectionArray = refCurrentSet.getSelectionByDim(0);
			
			if ( refStorageArray.length > 0 ) {
				refStorageFromSet = refStorageArray[0]; 
			}
			else {
				refStorageFromSet = null;
				refSelectionFromSet = null;
				throw new CerberusRuntimeException("DSwingHEatMap2DCanvas.updateState() error while try to get storage.");			
			}
			
			if ( refSelectionArray.length > 0 ){
				refSelectionFromSet = refSelectionArray[0];
			}
			else {
				refStorageFromSet = null;
				refSelectionFromSet = null;
				throw new CerberusRuntimeException("DSwingHEatMap2DCanvas.updateState() error while try to get selection.");				
			}
		}
				
	}
}
