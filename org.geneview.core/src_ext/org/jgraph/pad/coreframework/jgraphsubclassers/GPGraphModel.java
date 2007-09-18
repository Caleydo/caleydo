/*
 * Created on 05.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.jgraph.pad.coreframework.jgraphsubclassers;

import java.util.List;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.pad.util.ICellBuisnessObject;

/**
 * The base class JGraph graph model subclassers for GPGraphpad. To use a
 * custom GraphModel subcalsser register your subclass in the graph model
 * factory. As part of the non copyrihted online documentation of JGraph (FAQ
 * section), this file is released here under the LGPL license as stated by the
 * Free Software Fundation. We use that sunclasser specially because it will deep clone the graph
 * cell user object.
 */
public class GPGraphModel extends DefaultGraphModel {

	public GPGraphModel() {
		super();
	}

	// required for serialization
	public GPGraphModel(List roots, AttributeMap attributes) {
		super(roots, attributes);
	}
    
    // required for serialization
    public GPGraphModel(List roots, AttributeMap attributes,
            ConnectionSet cs) {
        super(roots, attributes, cs);
    }

	protected Object cloneUserObject(Object userObject) {
		if (userObject instanceof ICellBuisnessObject)
			return ((ICellBuisnessObject) userObject).clone();
		return super.cloneUserObject(userObject);
	}

    //TODO: useful?
	public Object valueForCellChanged(Object cell, Object newValue) {
		Object userObject = getValue(cell);
		if (userObject instanceof ICellBuisnessObject && newValue instanceof String) {
			ICellBuisnessObject user = (ICellBuisnessObject) userObject;
			Object oldLabel = String.valueOf(user);
			user.setValue(newValue);
			return oldLabel;
		}

		return super.valueForCellChanged(cell, newValue);
	}

}

