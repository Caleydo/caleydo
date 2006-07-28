package cerberus.view.gui.swt.pathway.jgraph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.geom.Rectangle2D;

import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.swt.pathway.PathwayViewInter;
import cerberus.view.gui.swt.SWTEmbeddedGraphWidget;

/**
 * In this class the real drawing of the Pathway happens.
 * For the drawing the JGraph package is used.
 * We can decide here if we want to draw in a new widget
 * or if we want to draw in an existing one.
 * 
 * @author Marc Streit
 *
 */
public class PathwayViewRep implements PathwayViewInter
{
	protected final int iNewId;
	protected GeneralManager refGeneralManager;
	protected Frame refEmbeddedFrame;
	
	protected GraphModel refGraphModel;
	protected GraphLayoutCache refGraphLayoutCache;
	protected JGraph refPathwayGraph;
	protected DefaultGraphCell refGraphCell;
	
	public PathwayViewRep(int iNewId, GeneralManager refGeneralManager)
	{
		this.iNewId = iNewId;
		this.refGeneralManager = refGeneralManager;
		
		//FIXME: do the following code in a method
		initView();
		retrieveNewWidget();
		drawView();
	}

	public void initView()
	{
		refGraphModel = new DefaultGraphModel();
		refGraphLayoutCache = 
			new GraphLayoutCache(refGraphModel, new DefaultCellViewFactory());

		refPathwayGraph = new JGraph(refGraphModel, refGraphLayoutCache);
	}
	
	public void drawView()
	{
		//create node
		refGraphCell = new DefaultGraphCell("TEST");
		GraphConstants.setBounds(refGraphCell.getAttributes(), 
				new Rectangle2D.Double(200.0, 200.0, 400.0, 400.0));
		GraphConstants.setOpaque(refGraphCell.getAttributes(), true);
		GraphConstants.setGradientColor(refGraphCell.getAttributes(), Color.orange);		

		refPathwayGraph.getGraphLayoutCache().insert(refGraphCell);
		refPathwayGraph.setBackground(Color.cyan);

		refEmbeddedFrame.add(new JScrollPane(refPathwayGraph), BorderLayout.CENTER);
	}
	
	public void retrieveNewWidget()
	{
		SWTEmbeddedGraphWidget refSWTEmbeddedGraphWidget = 
			(SWTEmbeddedGraphWidget)refGeneralManager.getSingelton()
		.getSWTGUIManager().createWidget(ManagerObjectType.GUI_SWT_EMBEDDED_JGRAPH_WIDGET);

		refEmbeddedFrame = refSWTEmbeddedGraphWidget.getEmbeddedFrame();
	}
	
	public void retrieveExistingWidget()
	{
		
	}
	
}
