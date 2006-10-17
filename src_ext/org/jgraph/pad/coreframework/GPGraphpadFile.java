package org.jgraph.pad.coreframework;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.beans.BeanInfo;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PersistenceDelegate;
import java.beans.PropertyDescriptor;
import java.beans.Statement;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jgraph.JGraph;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexView;
import org.jgraph.pad.coreframework.jgraphsubclassers.GPGraphModel;

/**
 * A Javabean whose properties are meant to be persisted or retrieved from an
 * XML file. See the JGraph manual for more details about how to use XMLEncoder
 * with JGraph
 * 
 * @author rvalyi (after the JGraph manual) - LGPL
 */
public class GPGraphpadFile extends DefaultMutableTreeNode {
	protected Map map;

	public static final String GRAPH_LAYOUT_CACHE = "GraphLayoutCache";

	public GPGraphpadFile() {
		this((GraphLayoutCache) null);
	}

	public GPGraphpadFile(Map map) {
		this(null, map);
	}

	public GPGraphpadFile(GraphLayoutCache graphlayoutcache) {
		this(graphlayoutcache, new Hashtable());
	}

	public GPGraphpadFile(GraphLayoutCache graphlayoutcache, Map map) {
		super();
		this.map = map;
		if (graphlayoutcache != null)
			map.put(GRAPH_LAYOUT_CACHE, graphlayoutcache);
	}

	public void setGraphLayoutCache(GraphLayoutCache graphlayoutcache) {
		map.put(GRAPH_LAYOUT_CACHE, graphlayoutcache);
	}

	public GraphLayoutCache getGraphLayoutCache() {
		GraphLayoutCache cache = (GraphLayoutCache) map.get(GRAPH_LAYOUT_CACHE);
		if (cache == null) {
			cache = new GraphLayoutCache();
			cache.setModel((GraphModel) GPPluginInvoker
					.instanciateObjectForKey("GraphModel.class"));
			cache.setFactory((CellViewFactory) GPPluginInvoker
					.instanciateObjectForKey("ViewFactory.class"));
			map.put(GRAPH_LAYOUT_CACHE, cache);
		}
		return cache;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	/**
	 * Reads a GPGraphpadFile file
	 * 
	 * @param in
	 *            the input stream to read
	 * @return the graph that is read in
	 */
	public static GPGraphpadFile read(InputStream in) throws IOException {
		XMLDecoder reader = new XMLDecoder(in);
		Object decoded = reader.readObject();
		in.close();
		if (decoded instanceof GPGraphpadFile) {
			return (GPGraphpadFile) decoded;
		}
		if (decoded instanceof GraphLayoutCache) {
			return new GPGraphpadFile((GraphLayoutCache) decoded);
		}
		if (decoded instanceof JGraph) {
			return null;
		}
		if (decoded instanceof GraphModel) {
			GraphLayoutCache cache = new GraphLayoutCache();
			cache.setModel((GraphModel) decoded);
			cache.setFactory((CellViewFactory) GPPluginInvoker
					.instanciateObjectForKey("ViewFactory.class"));// TODO: fix
			return new GPGraphpadFile(cache);
		}
		System.err
				.print("Unrecognized file format! May you should use import...");
		return null;
	}

	/**
	 * Saves the current graph in a file. We use long-term bean persistence to
	 * save the program data. See
	 * http://java.sun.com/products/jfc/tsc/articles/persistence4/index.html for
	 * an overview.
	 * 
	 * @param out
	 *            the stream for saving
	 */
	public void saveFile(OutputStream out) {
		XMLEncoder encoder = new XMLEncoder(out);

		// Better debugging output, in case you need it
		encoder.setExceptionListener(new ExceptionListener() {
			public void exceptionThrown(Exception e) {
				if (!(e instanceof AccessControlException))// happens in
															// unsigned
															// conditions,
															// benign
					e.printStackTrace();
			}
		});

		configureEncoder(encoder);

		makeCellViewFieldsTransient(PortView.class);
		makeCellViewFieldsTransient(VertexView.class);
		makeCellViewFieldsTransient(EdgeView.class);
		encoder.writeObject(this);
		encoder.close();
	}

	public static void makeCellViewFieldsTransient(Class clazz) {
		try {
			BeanInfo info = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] propertyDescriptors = info
					.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; ++i) {
				PropertyDescriptor pd = propertyDescriptors[i];
				if (!pd.getName().equals("cell")
						&& !pd.getName().equals("attributes")) {
					pd.setValue("transient", Boolean.TRUE);
				}
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * We set persistence delegates where the no argument constructor can't be
	 * used Warning, the java bug #4741757 is really annoying there when we want
	 * to deploy as unsigned applet or web start app because it breaks the
	 * security sandbox. We work arround that bug by taking inspiration from the
	 * great Violet UML code form Cay Horstmann TODO: use a simpler solution
	 * (new DefaultPersistenceDelegate(new String[]...)) when Java mustang will
	 * be the norm or when the fix is retrofited to most of clients.
	 * 
	 * @param encoder
	 */
	protected void configureEncoder(XMLEncoder encoder) {

		encoder.setPersistenceDelegate(GPGraphpadFile.class,
				new DefaultPersistenceDelegate() {
					protected Expression instantiate(Object oldInstance,
							Encoder out) {
						GPGraphpadFile p = (GPGraphpadFile) oldInstance;
						Object[] constructorArgs = new Object[] { p.getMap() };
						return new Expression(oldInstance, oldInstance
								.getClass(), "new", constructorArgs);
					}
				});

		encoder.setPersistenceDelegate(GraphLayoutCache.class,
				new DefaultPersistenceDelegate() {
					protected Expression instantiate(Object oldInstance,
							Encoder out) {
						GraphLayoutCache p = (GraphLayoutCache) oldInstance;
						Object[] constructorArgs = new Object[] { p.getModel(),
								p.getFactory(), p.getCellViews(),
								p.getHiddenCellViews(),
								new Boolean(p.isPartial()) };
						return new Expression(oldInstance, oldInstance
								.getClass(), "new", constructorArgs);
					}
				});

		encoder.setPersistenceDelegate(GPGraphModel.class,
				new DefaultPersistenceDelegate() {
					protected Expression instantiate(Object oldInstance,
							Encoder out) {
						GPGraphModel p = (GPGraphModel) oldInstance;
						Object[] constructorArgs = new Object[] { p.getRoots(),
								p.getAttributes(), p.getConnectionSet() };
						return new Expression(oldInstance, oldInstance
								.getClass(), "new", constructorArgs);
					}
				});

		encoder.setPersistenceDelegate(DefaultGraphModel.class,
				new DefaultPersistenceDelegate() {
					protected Expression instantiate(Object oldInstance,
							Encoder out) {
						DefaultGraphModel p = (DefaultGraphModel) oldInstance;
						Object[] constructorArgs = new Object[] { p.getRoots(),
								p.getAttributes(), p.getConnectionSet() };
						return new Expression(oldInstance, oldInstance
								.getClass(), "new", constructorArgs);
					}
				});

		encoder.setPersistenceDelegate(DefaultGraphCell.class,
				new DefaultPersistenceDelegate() {
					protected void initialize(Class type, Object oldInstance,
							Object newInstance, Encoder out) {
						super.initialize(type, oldInstance, newInstance, out);
						DefaultGraphCell p = (DefaultGraphCell) oldInstance;
						out.writeStatement(new Statement(oldInstance,
								"setUserObject", new Object[] { p
										.getUserObject() }));
					}
				});

		encoder.setPersistenceDelegate(DefaultEdge.class,
				new DefaultPersistenceDelegate() {
					protected void initialize(Class type, Object oldInstance,
							Object newInstance, Encoder out) {
						super.initialize(type, oldInstance, newInstance, out);
						DefaultEdge p = (DefaultEdge) oldInstance;
						out.writeStatement(new Statement(oldInstance,
								"setUserObject", new Object[] { p
										.getUserObject() }));
					}
				});

		encoder.setPersistenceDelegate(DefaultPort.class,
				new DefaultPersistenceDelegate() {
					protected void initialize(Class type, Object oldInstance,
							Object newInstance, Encoder out) {
						super.initialize(type, oldInstance, newInstance, out);
						DefaultPort p = (DefaultPort) oldInstance;
						out.writeStatement(new Statement(oldInstance,
								"setUserObject", new Object[] { p
										.getUserObject() }));
					}
				});

		encoder.setPersistenceDelegate(AbstractCellView.class,
				new DefaultPersistenceDelegate() {
					protected void initialize(Class type, Object oldInstance,
							Object newInstance, Encoder out) {
						super.initialize(type, oldInstance, newInstance, out);
						AbstractCellView p = (AbstractCellView) oldInstance;
						out.writeStatement(new Statement(oldInstance,
								"setCell", new Object[] { p.getCell() }));
						out.writeStatement(new Statement(oldInstance,
								"setAttributes", new Object[] { p
										.getAttributes() }));
					}
				});

		encoder.setPersistenceDelegate(DefaultEdge.DefaultRouting.class,
				new PersistenceDelegate() {
					protected Expression instantiate(Object oldInstance,
							Encoder out) {
						return new Expression(oldInstance,
								GraphConstants.class, "getROUTING_SIMPLE", null);
					}
				});
		encoder.setPersistenceDelegate(DefaultEdge.LoopRouting.class,
				new PersistenceDelegate() {
					protected Expression instantiate(Object oldInstance,
							Encoder out) {
						return new Expression(oldInstance,
								GraphConstants.class, "getROUTING_DEFAULT",
								null);
					}
				});

		encoder.setPersistenceDelegate(ArrayList.class, encoder
				.getPersistenceDelegate(List.class));

		encoder.setPersistenceDelegate(Point2D.Double.class,
				new DefaultPersistenceDelegate() {
					protected void initialize(Class type, Object oldInstance,
							Object newInstance, Encoder out) {
						super.initialize(type, oldInstance, newInstance, out);
						Point2D p = (Point2D) oldInstance;
						out.writeStatement(new Statement(oldInstance,
								"setLocation", new Object[] {
										new Double(p.getX()),
										new Double(p.getY()) }));
					}
				});

		encoder.setPersistenceDelegate(Color.class,
				new DefaultPersistenceDelegate() {
					protected Expression instantiate(Object oldInstance,
							Encoder out) {
						Color p = (Color) oldInstance;
						Object[] constructorArgs = new Object[] {
								new Integer(p.getRed()),
								new Integer(p.getGreen()),
								new Integer(p.getBlue()),
								new Integer(p.getAlpha()) };
						return new Expression(oldInstance, oldInstance
								.getClass(), "new", constructorArgs);
					}
				});

	}
}
