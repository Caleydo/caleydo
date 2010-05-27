/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.entity;

import java.util.Properties;

import com.jme.scene.Spatial;

/**
 * <code>Entity</code> defines a core game element. An entity defines any object
 * within the game world. The <code>Entity</code> will contain all relevant
 * game information allowing for easy data reference and control.
 * @author Mark Powell
 * @version $Id: Entity.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class Entity {
	private Spatial spatial;
	private String id;
	private Properties properties;

	/**
	 * Constructor creates a new <code>Entity</code> object. During creation
	 * a string id is used to denote a unique entity.
	 * @param id the entity id.
	 */
	public Entity(String id) {
		this.id= id;
		properties=new Properties();
	}

	/**
		 * Constructor creates a new <code>Entity</code> object. During creation
		 * a string id is used to denote a unique entity, with predefined properties.
		 *
		 * @param id the entity id.
		 * @param props the entity properties.
		 */
	public Entity(String id, Properties props) {
		this(id);
		this.properties= props;
	}

	/**
	 *
	 * <code>setSpatial</code> sets the spatial object used to define the
	 * entitie's graphical representation.
	 * @param spatial the spatial object used to describe the geometry of the
	 * entity.
	 */
	public void setSpatial(Spatial spatial) {
		this.spatial= spatial;
	}

	/**
	 *
	 * <code>getSpatial</code> retrieves the spatial object of the entity.
	 * @return the spatial object of the entity.
	 */
	public Spatial getSpatial() {
		return spatial;
	}

	/**
	 *
	 * <code>getId</code> returns this entity's id.
	 * @return the id of the entity.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get a property of this entity.
	 * @param propertyName the property name to retrieve.
	 * @return The entity's property linked to propertyName.
	 */
	public Object getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	/**
	 * Binds a property name of the entity with it's property object.
	 * @param propertyName the property name.
	 * @param property the propery to bind with the name.
	 */
	public void setProperty(String propertyName, Object property) {
		properties.put(propertyName, property);
	}

}
