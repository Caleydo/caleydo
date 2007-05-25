package org.jgraph.pad.coreframework;

import java.applet.Applet;
import java.util.Hashtable;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.jgraph.pad.resources.ImageLoader;
import org.jgraph.pad.resources.Translator;
import org.jgraph.pad.util.Utilities;

/**
 * While JGraphpad gets most of its parameters from the properties file, some
 * other parameters can be given at the session time. Those parameters can only
 * be strings and they should be passed as argument for the main class, either
 * when invoking JGraphpad as a standalone application, as an applet or via
 * webstart.
 * 
 * You can even gain full control of JGraphpad during the session by providing a
 * new properties file as an argument!
 * 
 * @author rvalyi
 */
public class GPSessionParameters {

	/**
	 * This is where we store every sort of session parameters
	 */
	private Map sessionParameters;

	/**
	 * This is a static map providing the appropriate key for command line
	 * arguments
	 */
	public static Map paramCommands;

	private Applet applet;

	public static final String DEFAULT_PROPERTIES_FILE = "org.jgraph.pad.resources.Graphpad";

	public static final String DEFAULT_IMAGE_PATH = "/org/jgraph/pad/resources";

	// session parameters:

	public static final String HOSTNAME = "host";

	public static final String HOSTPORT = "port";

	public static final String PROTOCOL = "protocol";

	public static final String DOWNLOADPATH = "drawpath";

	public static final String UPLOADPATH = "savepath";

	public static final String UPLOADFILE = "basename";

	public static final String MAPFILE = "mapFile";

	public static final String VIEWPATH = "pngpath";

	public static final String CUSTOMCONFIG = "customconfig";

	public static final String NODEFPLUGIN = "nodefplugin";

	// command line for the session parameters:

	public static final String HOSTNAME_SHORT = "-h";

	public static final String HOSTPORT_SHORT = "-p";

	public static final String PROTOCOL_SHORT = "-t";

	public static final String DOWNLOADPATH_SHORT = "-d";

	public static final String UPLOADPATH_SHORT = "-u";

	public static final String UPLOADFILE_SHORT = "-f";

	public static final String MAPFILE_SHORT = "-m";

	public static final String VIEWPATH_SHORT = "-v";

	public static final String CUSTOMCONFIG_SHORT = "-c";

	public static final String NODEFPLUGIN_SHORT = "-n";

	static {
		paramCommands = new Hashtable(10);
		paramCommands.put(HOSTNAME_SHORT, HOSTNAME);
		paramCommands.put(HOSTPORT_SHORT, HOSTPORT);
		paramCommands.put(PROTOCOL_SHORT, PROTOCOL);
		paramCommands.put(DOWNLOADPATH_SHORT, DOWNLOADPATH);
		paramCommands.put(UPLOADPATH_SHORT, UPLOADPATH);
		paramCommands.put(UPLOADFILE_SHORT, UPLOADFILE);
		paramCommands.put(MAPFILE_SHORT, MAPFILE);
		paramCommands.put(VIEWPATH_SHORT, VIEWPATH);
		paramCommands.put(CUSTOMCONFIG_SHORT, CUSTOMCONFIG);
		paramCommands.put(NODEFPLUGIN_SHORT, NODEFPLUGIN);
	}

	public GPSessionParameters() {
		this(null, null, null);
	}
	
	public GPSessionParameters(String customProperties) {
		this(null, null, customProperties);
	}

	public GPSessionParameters(Map map) {
		this(null, null, null);
	}

	public GPSessionParameters(Applet applet) {
		this(null, applet, null);
	}
	
	public GPSessionParameters(Applet applet, String customProperties) {
		this(null, applet, customProperties);
	}

	/**
	 * push the default properties file and image search path, then push the plugins
	 * bundles and finally the custom properties file. Warning, we take care of taking
	 * the plugins paths from the custom properties file, but we push the custom
	 * properties file only at last so that it can potentially override everything.
	 * @param map
	 * @param applet
	 * @param customProperties
	 */
	public GPSessionParameters(Map map, Applet applet, String customProperties) {
		if (map == null)
			map = new Hashtable(10);
		sessionParameters = map;
		this.applet = applet;

		// the minimal configuration of JGraphpad:
		Translator.pushBundle(DEFAULT_PROPERTIES_FILE);
		ImageLoader.pushSearchPath(DEFAULT_IMAGE_PATH);
		
		// custom plugins properties if any:
		String pluginPaths = null;
		
		ResourceBundle resourcebundle = null;
		
		// custom properties:
		if (customProperties != null) {
			resourcebundle = PropertyResourceBundle.getBundle(customProperties);
			pluginPaths = resourcebundle.getString("PluginPropertiesPath");
		}

		// default plugins if allowed:
		if (getParam(NODEFPLUGIN, false) == null && (pluginPaths == null)) {
			pluginPaths = Translator.getString("PluginPropertiesPath");
		}

		String[] values;
		values = Utilities.tokenize(pluginPaths);
		for (int i = 0; i < values.length; i++) {
			try {
				Translator.pushBundle(values[i]);
			} catch (MissingResourceException ex) {
				System.out.print("\nCAN'T FIND PROPERTIES FILE :" + values[i]
						+ ".properties; THE PLUGIN MIGHT BE SIMPLY MISSING");
			}
		}
		
		// finally, the custom properties file overrides the plugins and the default properties:
		if (resourcebundle != null) {
		Translator.getBundles().push(resourcebundle);
		Translator.getBundleNames().push(customProperties);
		}

		// custom image search paths properties if any:
		values = Utilities.tokenize(Translator.getString("ImagePaths"));
		for (int i = 0; i < values.length; i++) {
			try {
				ImageLoader.pushSearchPath(values[i]);
			} catch (MissingResourceException ex) {
				System.out.print("\nCAN'T FIND PROPERTIES FILE :" + values[i]
						+ ".properties; THE PLUGIN MIGHT BE SIMPLY MISSING");
			}
		}

		// translations if any:
		values = Utilities.tokenize(Translator.getString("TranslationsPath"));
		for (int i = 0; i < values.length; i++) {
			try {
				Translator.pushBundle(values[i]);
			} catch (MissingResourceException ex) {
				System.out.print(ex);
			}
		}
	}

	public void setParamWithCommand(String command, String value) {
		String key = (String) paramCommands.get(command);
		setParam(key, value);
	}

	public void setParam(String key, String value) {
		sessionParameters.put(key, value);
		if (key.equals(CUSTOMCONFIG)) {
			Translator.pushBundle(value);
		}
	}
	
	public void putApplicationParameters(String[] args) {
		for (int i = 0; i < args.length; i = i + 2) {
			if (args[i].indexOf("-") < 0) {// simple precaution in case
											// arguments aren't well formed
				i = i - 1;
				continue;
			}
			setParamWithCommand(args[i], args[i + 1]);
		}
	}

	/**
	 * Try to get the parameter from the applet if any. If not, then it looks if
	 * there is one in the session parameters map. Finally, if it's not defined
	 * and if we allow it, then we ask the user to enter the parameter.
	 * 
	 * @param key
	 * @param askIfNone
	 * @return
	 */
	public String getParam(String key, boolean askIfNone) {
		String value;
		if (isApplet()) {
			value = applet.getParameter(key);
			if (value != null)
				return value;

			// default parameters if none is passed:
			if (key == HOSTPORT)
				return Integer.toString(applet.getCodeBase().getPort());
			if (key == HOSTNAME)
				return applet.getCodeBase().getHost();
			if (key == PROTOCOL)
				return applet.getCodeBase().getProtocol();
		}
		Object object = sessionParameters.get(key);
		if (object != null) {
			return (String) object;
		}
		if (askIfNone) {
			return JOptionPane.showInputDialog(null,

			"the parameter '" + key + "' is missing, please enter a value:",

			"parameter dialog box",

			JOptionPane.QUESTION_MESSAGE);
		}
		return null;
	}

	public Map getSessionParameters() {
		return sessionParameters;
	}

	public void setSessionParameters(Map sessionParameters) {
		this.sessionParameters = sessionParameters;
	}

	public Applet getApplet() {
		return applet;
	}

	public void setApplet(Applet applet) {
		this.applet = applet;
	}

	public boolean isApplet() {
		return (applet != null);
	}
}
