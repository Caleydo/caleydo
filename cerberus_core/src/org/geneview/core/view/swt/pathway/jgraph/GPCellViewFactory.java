package org.geneview.core.view.swt.pathway.jgraph;

import java.util.Map;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.VertexView;

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
public class GPCellViewFactory extends DefaultCellViewFactory {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9075157924693207495L;
	
	public static final String VIEW_CLASS_KEY = "viewClassKey";
	
	public GPCellViewFactory() {
		super();		
	}
	
	public static final void setViewClass(Map <String,String> map, String viewClass) {
		map.put(VIEW_CLASS_KEY, viewClass);
	}

	protected VertexView createVertexView(Object v) {
		try {
			DefaultGraphCell cell = (DefaultGraphCell) v;
			String viewClass = (String) cell.getAttributes().get(VIEW_CLASS_KEY);

			VertexView view = (VertexView) Thread.currentThread()
					.getContextClassLoader().loadClass(viewClass).newInstance();
			view.setCell(v);
			return view;
		} catch (Exception ex) {
		}
		return super.createVertexView(v);
	}
}
