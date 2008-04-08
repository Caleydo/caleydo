package org.jgraph.pad.graphcellsbase.cellviews;

import java.util.ArrayList;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.VertexView;
import org.jgraph.pad.graphcellsbase.cells.DiamondCell;
import org.jgraph.pad.graphcellsbase.cells.EllipseCell;
import org.jgraph.pad.graphcellsbase.cells.ImageCell;
import org.jgraph.pad.graphcellsbase.cells.RoundRectangleCell;
import org.jgraph.pad.graphcellsbase.cells.SwimlaneCell;
import org.jgraph.pad.graphcellsbase.cells.TextCell;

/**
 * A default view factory for a JGraph. This simple factory associate a given
 * cell class to a cell view. This is a javabean, just parameter it correctly in
 * order it meets your requirements (else subclass it or subclass
 * DefaultCellViewFactory). You can also recover the gpConfiguration of that
 * javabean via an XML file via XMLEncoder/XMLDecoder.
 * 
 * @author rvalyi, license of this file: LGPL as stated by the Free Software
 *         Foundation
 */
public class DefaultCellViewFactoryBean extends DefaultCellViewFactory {

    private ArrayList viewIndirections;
    
    public DefaultCellViewFactoryBean() {
    	//default initialization for JGraphpad, of course you can change that set before setting that view factory in the graph
		ArrayList vect = new ArrayList(15);
		vect.add(new ViewIndirection(EllipseCell.class, JGraphEllipseView.class));
		vect.add(new ViewIndirection(DiamondCell.class, JGraphDiamondView.class));
		vect.add(new ViewIndirection(RoundRectangleCell.class, JGraphRoundRectView.class));
		vect.add(new ViewIndirection(SwimlaneCell.class, JGraphSwimlaneView.class));
		vect.add(new ViewIndirection(ImageCell.class, ScaledVertexView.class));
		vect.add(new ViewIndirection(TextCell.class, JGraphMultilineView.class));
		setViewIndirections(vect);
    }

    public static class ViewIndirection {
        private Class cellClass;

        private Class viewClass;
        
        public ViewIndirection(Class cellClass, Class viewClass) {
        	this.cellClass = cellClass;
        	this.viewClass = viewClass;
        }
        
        public ViewIndirection() {
        	
        }

        public Class getCellClass() {
            return cellClass;
        }

        public void setCellClass(Class cellClass) {
            this.cellClass = cellClass;
        }

        public Class getViewClass() {
            return viewClass;
        }

        public void setViewClass(Class viewClass) {
            this.viewClass = viewClass;
        }
    }

    protected VertexView createVertexView(Object v) {
        VertexView view = null;
        try {
            for (int i = 0; i < viewIndirections.size(); i++) {
                ViewIndirection indirection = (ViewIndirection) viewIndirections
                        .get(i);
                if (v.getClass() == indirection.cellClass) {
                    view = (VertexView) indirection.viewClass.newInstance();
                    view.setCell(v);
                    return view;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.createVertexView(v);
    }

    public ArrayList getViewIndirections() {
        return viewIndirections;
    }

    public void setViewIndirections(ArrayList viewIndirections) {
        this.viewIndirections = viewIndirections;
    }
}
