package org.jgraph.pad.coreframework;

import javax.swing.Action;
import javax.swing.ActionMap;

import org.jgraph.pad.resources.Translator;
import org.jgraph.pad.util.ICommandRegistery;
import org.jgraph.pad.util.NamedInputStream;
import org.jgraph.pad.util.NamedOutputStream;
import org.jgraph.pad.util.Utilities;

/**
 * Utilities for JGraphpad to load plugin classes in a loose coupled manner so
 * that they can be included or not dynamically and without generating
 * compilation error. The various kind of classes you try to instanciate via
 * those utilities can be replaced by an appropriate subclasser by any
 * registered plugin if it change the key association in the the properties
 * file.
 * 
 * @author rvalyi
 */
public final class GPPluginInvoker {

    public static interface PadAwarePlugin {
        public void setGraphpad(GPGraphpad pad);
    }

    public static interface DocAwarePlugin {
        public void setDocument(GPDocument doc);
    }

    public static Action getCommand(final String key, final ActionMap map,
            final ICommandRegistery registery) {
        Action action;
        action = map.get(key);
        if (action == null) {
            String path = "org.jgraph.pad.coreframework.actions." + key;
            try {
                String overrider = Translator.getString(key + ".class");
                if (overrider != null) {
                	if (overrider.equals(""))
                		return null;
               		action = (Action) getClassForName(overrider).newInstance();
                } else 
                	action = (Action) getClassForName(path).newInstance();
            } catch (Exception ex) {
                System.out.print("\nCANT'T FIND LOAD ACTION " + key
                        + ". I'M CONTINUING...");
                return null;
            }

            map.put(key, action);// we register this
            // new command
            registery.initCommand(action);// we eventually initialize
            // something dealing with this new
            // command
        }
        return action;
    }

    public static Object instanciateObjectForKey(final String key) {
        Class clazz = null;
        try {
            clazz = getClassForKey(key);
            return clazz.newInstance();
        } catch (Exception ex) {
            System.err.print("CAN'T INSTANCIATE CLASS: " + clazz.toString()
                    + "/nMAY BE THIS CLASS HAS NO PUBLIC EMPTY CONSTRUCTOR\n");
            ex.printStackTrace();
            return null;
        }
    }

    public static Object instanciateObjectForName(final String name)
            throws ClassNotFoundException {
        Class clazz = null;
        try {
            clazz = getClassForName(name);
            return clazz.newInstance();
        } catch (Exception ex) {
            System.err.print("CAN'T INSTANCIATE CLASS: " + clazz.toString()
                    + "/nMAY BE THIS CLASS HAS NO PUBLIC EMPTY CONSTRUCTOR\n");
            ex.printStackTrace();
            return null;
        }
    }

    public static Class getClassForName(final String name)
            throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(name);
    }

    public static Class getClassForKey(final String key) {
        try {
            return getClassForName(Translator.getString(key));
        } catch (ClassNotFoundException ex) {
            System.out.print("\nCAN'T LOAD CLASS: " + Translator.getString(key)
                    + "\nMAY BE THE REQUIRED PLUGIN IS SIMPLY MISSING");
            return null;
        }
    }

    /**
     * Decorate the document with registered plugins if any. Use the decorator
     * pattern to allow loose coupling with plugins
     * 
     * @param doc
     * @return
     */
    public static GPDocument decorateDocument(GPDocument doc) {
        String values[] = Utilities.tokenize(Translator
                .getString("DocumentDecorators"));
        for (int i = 0; i < values.length; i++) {
            try {
                Object object = getClassForName(
                        values[i]).newInstance();
                if (object instanceof DocAwarePlugin)
                    ((DocAwarePlugin) object).setDocument(doc);
            } catch (Exception ex) {
                System.out.print(ex);
            }
        }
        return doc;
    }
    
    
    /**
     * Decorate the Graphpad with registered plugins if any. Use the decorator
     * pattern to allow loose coupling with plugins
     * 
     * @param doc
     * @return
     */
    public static GPGraphpad decorateGraphpad(GPGraphpad pad) {
        String values[] = Utilities.tokenize(Translator
                .getString("GraphpadDecorators"));
        for (int i = 0; i < values.length; i++) {
            try {
                Object object = getClassForName(
                        values[i]).newInstance();
                if (object instanceof PadAwarePlugin)
                    ((PadAwarePlugin) object).setGraphpad(pad);
            } catch (Exception ex) {
                System.out.print(ex);
            }
        }
        return pad;
    }
    
    public static Object instanciateDocAwarePluginForKey(
            final String key, final GPDocument document) {
        Object object = instanciateObjectForKey(key);
        if (object instanceof DocAwarePlugin)
            ((DocAwarePlugin) object).setDocument(document);
        return object;
    }

    public static Object instanciatePadAwarePluginForKey(
            final String key, final GPGraphpad pad) {
        Object object = instanciateObjectForKey(key);
        if (object instanceof PadAwarePlugin)
            ((PadAwarePlugin) object).setGraphpad(pad);
        return object;
    }

    /**
     * Opens the given URL using the browser launcher plugin if one is found
     * @param url
     */
    public static void openURL(String url) {
        try {
            Class clazz = getClassForKey("BrowserLauncher.class");
            Class classes[] = { String.class };
            Object params[] = { new String(url) };
            clazz.getMethod("openURL", classes).invoke(null, params);
        } catch (Exception ex) {
            System.err.print(Translator.getString("Error.invokation"));
            ex.printStackTrace();
        }
    }

    public static NamedInputStream provideInputStream(String fileExtension) {
        try {
            Object object = instanciateObjectForKey("InputStreamProvider.class");
            Class classes[] = { String.class };
            Object params[] = { new String(fileExtension) };
            return (NamedInputStream) object.getClass().getMethod(
                    "provideInput", classes).invoke(object, params);
        } catch (Exception ex) {
            System.err.print(Translator.getString("Error.invokation"));
            ex.printStackTrace();
            return null;
        }
    }

    public static NamedOutputStream provideOutputStream(String fileExtension,
            String nameToBeConfirmed, boolean isZipped) {
        if (nameToBeConfirmed == null)
            nameToBeConfirmed = "undef";
        try {
            Object object = instanciateObjectForKey("OutputStreamProvider.class");
            Class classes[] = { String.class, String.class, Boolean.class };
            Object params[] = { new String(fileExtension),
                    new String(nameToBeConfirmed), new Boolean(isZipped) };
            return (NamedOutputStream) object.getClass().getMethod(
                    "provideOutput", classes).invoke(object, params);
        } catch (Exception ex) {
            System.err.print(Translator.getString("Error.invokation"));
            ex.printStackTrace();
            return null;
        }
    }
}
