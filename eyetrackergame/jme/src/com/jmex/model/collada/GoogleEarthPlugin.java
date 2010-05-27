package com.jmex.model.collada;

import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.jme.scene.state.CullState;
import com.jme.system.DisplaySystem;
import com.jmex.model.collada.schema.extraType;

public class GoogleEarthPlugin implements ExtraPlugin {
    private static final Logger logger = Logger
            .getLogger(GoogleEarthPlugin.class.getName());

    public Object processExtra(String profile, Object target, extraType extra) {
        try {
            NodeList nodes = extra.gettechnique().getDomNode().getChildNodes();
            for (int j = 0; j < nodes.getLength(); j++) {
                if (nodes.item(j) instanceof Element) {
                    Element el = (Element) nodes.item(j);
                    if (el.getNodeName().equals("double_sided")) {
                        boolean dblSided = "1".equals(el.getTextContent());
                        if (dblSided) {
                            ColladaMaterial material = (ColladaMaterial)target;
                            CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
                            cs.setCullFace(CullState.Face.None);
                            cs.setEnabled(false);
                            material.setState(cs);
                            return material;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.throwing(this.getClass().toString(),
                    "processExtra(String profile, Object target, extraType extra)", e);
        }
        
        return null;
    }
}

