/*
 * Copyright (c) 2005-2007 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
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
package com.jmex.physics.material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jmex.physics.contact.ContactHandlingDetails;
import com.jmex.physics.contact.MutableContactInfo;

/**
 * A material specifies contact and mass details about a collision geometry or physics node.
 *
 * @author Irrisor
 * @see com.jmex.physics.PhysicsCollisionGeometry#setMaterial(Material)
 * @see com.jmex.physics.PhysicsNode#setMaterial(Material)
 */
public class Material implements Savable {
    public static final Material IRON = new Material( "iron" );
    public static final Material WOOD = new Material( "wood" );
    public static final Material CONCRETE = new Material( "concrete" );
    public static final Material GRANITE = new Material( "granite" );
    public static final Material GLASS = new Material( "glass" );
    public static final Material PLASTIC = new Material( "plastic" );
    public static final Material RUBBER = new Material( "rubber" );
    public static final Material ICE = new Material( "ice" );
    public static final Material DEFAULT = new Material( "default" );
    public static final Material GHOST = new Material( "ghost" );
    public static final Material OSMIUM = new Material( "osmium" );
    public static final Material SPONGE = new Material( "sponge" );
    
    private static final ArrayList<Material> commonMaterials = new ArrayList<Material>();

    static {
        // most values were found on the internet (wikipedia etc.), some are guessed
        MutableContactInfo info = new MutableContactInfo();
        {
            info.setMu( 1 );
            info.setBounce( 0.4f );
            info.setMinimumBounceVelocity( 1f );
            DEFAULT.putContactHandlingDetails( null, info );
        }

        {
            IRON.setDebugColor( new ColorRGBA( 0.6f, 0.6f, 0.6f, 1 ) );
            IRON.setDensity( 7.8f );

            info.setMu( 0.15f );
            info.setBounce( 0.9f );
            IRON.putContactHandlingDetails( IRON, info );
            info.setBounce( 0.5f );
            IRON.putContactHandlingDetails( null, info );
            IRON.putContactHandlingDetails( DEFAULT, info );

            info.setMu( 0.5f );
            IRON.putContactHandlingDetails( WOOD, info );
            info.setMu( 0.6f );
            IRON.putContactHandlingDetails( GLASS, info );
            info.setMu( 0.8f );
            IRON.putContactHandlingDetails( PLASTIC, info );
            info.setMu( 0.45f );
            IRON.putContactHandlingDetails( CONCRETE, info );
        }
        {
            OSMIUM.setDebugColor( new ColorRGBA( 0.3f, 0.3f, 0.5f, 1 ) );
            OSMIUM.setDensity( 22.6f );

            // following values are guessed (based on iron)

            info.setMu( 0.12f );
            info.setBounce( 0.9f );
            OSMIUM.putContactHandlingDetails( OSMIUM, info );
            OSMIUM.putContactHandlingDetails( IRON, info );
            info.setBounce( 0.6f );
            OSMIUM.putContactHandlingDetails( null, info );
            OSMIUM.putContactHandlingDetails( DEFAULT, info );

            info.setMu( 0.3f );
            OSMIUM.putContactHandlingDetails( WOOD, info );
            info.setMu( 0.4f );
            OSMIUM.putContactHandlingDetails( GLASS, info );
            info.setMu( 0.6f );
            OSMIUM.putContactHandlingDetails( PLASTIC, info );
            info.setMu( 0.45f );
            OSMIUM.putContactHandlingDetails( CONCRETE, info );
        }
        {
            WOOD.setDebugColor( new ColorRGBA( 0.4f, 0.2f, 0.1f, 1 ) );
            WOOD.setDensity( 0.5f );

            info.setMu( 0.65f );
            info.setBounce( 0.5f );
            WOOD.putContactHandlingDetails( WOOD, info );
            WOOD.putContactHandlingDetails( null, info );
            WOOD.putContactHandlingDetails( DEFAULT, info );

            info.setMu( 1.5f );
            info.setBounce( 0.1f );
            WOOD.putContactHandlingDetails( CONCRETE, info );
        }
        {
            CONCRETE.setDebugColor( new ColorRGBA( 0.4f, 0.4f, 0.4f, 1 ) );
            CONCRETE.setDensity( 2 );

            info.setMu( 1f );
            info.setBounce( 0.2f );
            CONCRETE.putContactHandlingDetails( null, info );
            CONCRETE.putContactHandlingDetails( DEFAULT, info );
        }
        {
            GRANITE.setDebugColor( new ColorRGBA( 0.3f, 0.3f, 0.3f, 1 ) );
            GRANITE.setDensity( 2.8f );

            info.setMu( 0.3f );
            info.setBounce( 0.4f );
            GRANITE.putContactHandlingDetails( null, info );
            GRANITE.putContactHandlingDetails( DEFAULT, info );
        }
        {
            GLASS.setDebugColor( new ColorRGBA( 0.8f, 0.8f, 0.8f, 0.5f ) );
            GLASS.setDensity( 2.5f );

            info.setMu( 0.1f );
            info.setBounce( 0.4f );
            GLASS.putContactHandlingDetails( null, info );
            GLASS.putContactHandlingDetails( DEFAULT, info );
        }
        {
            PLASTIC.setDebugColor( new ColorRGBA( 0.8f, 0.8f, 0.8f, 0.5f ) );
            PLASTIC.setDensity( 2.5f );

            info.setMu( 0.2f );
            info.setBounce( 0.7f );
            PLASTIC.putContactHandlingDetails( null, info );
            PLASTIC.putContactHandlingDetails( DEFAULT, info );
        }
        {
            RUBBER.setDebugColor( new ColorRGBA( 0.1f, 0.5f, 0.1f, 1 ) );
            RUBBER.setDensity( 0.95f );

            info.setMu( 5f );
            info.setBounce( 1.0f );
            RUBBER.putContactHandlingDetails( RUBBER, info );
            info.setBounce( 0.9f );
            RUBBER.putContactHandlingDetails( null, info );
            RUBBER.putContactHandlingDetails( DEFAULT, info );

            info.setMu( 0.9f );
            info.setBounce( 0.85f );
            RUBBER.putContactHandlingDetails( CONCRETE, info );
        }
        {
            ICE.setDebugColor( new ColorRGBA( 0.8f, 0.8f, 0.8f, 0.5f ) );
            ICE.setDensity( 0.9f );

            info.setMu( 0.002f );
            info.setBounce( 0.2f );
            ICE.putContactHandlingDetails( null, info );
            ICE.putContactHandlingDetails( DEFAULT, info );
            ICE.putContactHandlingDetails( CONCRETE, info );
            ICE.putContactHandlingDetails( GLASS, info );
            ICE.putContactHandlingDetails( PLASTIC, info );
            ICE.putContactHandlingDetails( GRANITE, info );
            ICE.putContactHandlingDetails( IRON, info );
            ICE.putContactHandlingDetails( RUBBER, info );
            ICE.putContactHandlingDetails( WOOD, info );
        }

        {
            SPONGE.setDebugColor( new ColorRGBA( 0.8f, 0.7f, 0.0f, 1 ) );
            SPONGE.setDensity( 0.1f );
            SPONGE.setSpringPenetrationDepth( 1 );

            info.setMu( 6 );
            info.setDampingCoefficient( 20 );
            info.setSpringConstant( 2 );
            info.setBounce( 0.1f );
            SPONGE.putContactHandlingDetails( null, info );
        }

        info.setApplied( false );
        GHOST.putContactHandlingDetails( null, info );
        info.setApplied( true );
        
        commonMaterials.add(IRON);
        commonMaterials.add(WOOD);
        commonMaterials.add(CONCRETE);
        commonMaterials.add(GRANITE);
        commonMaterials.add(GLASS);
        commonMaterials.add(PLASTIC);
        commonMaterials.add(RUBBER);
        commonMaterials.add(ICE);
        commonMaterials.add(DEFAULT);
        commonMaterials.add(GHOST);
        commonMaterials.add(OSMIUM);
        commonMaterials.add(SPONGE);
    }

    private String name;

    /**
     * @return name for this material (might be null)
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the material, can be null
     * 
     * @param name name of the material
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Create a new material.
     *
     * @param name name for this material, can be null
     */
    public Material( String name ) {
        this.name = name;
    }

    /**
     * Create a new material without name.
     */
    public Material() {
        this( null );
    }

    /**
     * Density of this material. Default is 1.
     *
     * @return density
     */
    public float getDensity() {
        return this.density;
    }

    /**
     * store the value for field density
     */
    private float density = 1;

    /**
     * Change the density of this material.
     *
     * @param value new value
     */
    public void setDensity( final float value ) {
        this.density = value;
    }


    /**
     * getter for field debugColor
     *
     * @param color where to put the debug color (null to create a new ColorRGBA)
     * @return color set to debugcolor
     */
    public ColorRGBA getDebugColor( ColorRGBA color ) {
        if ( color != null ) {
            color.set( this.debugColor );
        } else {
            color = new ColorRGBA( this.debugColor );
        }
        return color;
    }

    /**
     * store the value for field debugColor
     */
    private ColorRGBA debugColor = new ColorRGBA( 0, 0, 1, 1 );

    /**
     * setter for field debugColor
     *
     * @param value new value
     */
    public void setDebugColor( final ColorRGBA value ) {
        if ( value == null ) {
            throw new IllegalArgumentException( "null color not allowed" );
        } else {
            this.debugColor.set( value );
        }
    }

    /**
     * Map from Material to ContactHandlingDetails
     */
    private Map<Material, ContactHandlingDetails> contactDetails;

    private static final Vector2f tmpV2 = new Vector2f();
    private static final Vector2f tmpV22 = new Vector2f();

    /**
     * Query contact details of this material with another material.
     *
     * @param contactMaterial the second material
     * @return contact details if
     */
    public ContactHandlingDetails getContactHandlingDetails( Material contactMaterial ) {
        ContactHandlingDetails details = contactDetails != null ? contactDetails.get( contactMaterial ) : null;
        if ( details == null && contactMaterial != null ) {
            // use default contact details
            details = contactDetails != null ? contactDetails.get( null ) : null;
            if ( details != null && ( details.isIgnored() ||
                    !details.isApplied() ) ) {
                return details;
            }
            if ( contactMaterial.contactDetails != null )
            {
                ContactHandlingDetails otherDefault = contactMaterial.contactDetails.get( null );
                if ( otherDefault != details )
                {
                    if ( otherDefault != null && ( otherDefault.isIgnored() ||
                            !otherDefault.isApplied() || details == null ) ) {
                        return otherDefault;
                    }
                    if ( otherDefault == null ) {
                        return null;
                    }
                    // merge the contact info - to avoid garbage all material pairs should be defined explicitly
                    MutableContactInfo merged = new MutableContactInfo();
                    merged.setBounce( interpolate( details.getBounce(), otherDefault.getBounce() ) );
                    merged.setMinimumBounceVelocity( interpolate( details.getMinimumBounceVelocity(), otherDefault.getMinimumBounceVelocity() ) );
                    merged.setMu( interpolate( details.getMu(), otherDefault.getMu() ) );
                    merged.setMuOrthogonal( interpolate( details.getMuOrthogonal(), otherDefault.getMuOrthogonal() ) );
                    details.getSlip( tmpV2 ).interpolate( otherDefault.getSlip( tmpV22 ), 0.5f );
                    merged.setSlip( tmpV2 );
                    details.getSurfaceMotion( tmpV2 ).addLocal( otherDefault.getSurfaceMotion( tmpV22 ) );
                    merged.setSurfaceMotion( tmpV2 );
                    merged.setDampingCoefficient( interpolate( details.getDampingCoefficient(), otherDefault.getDampingCoefficient() ) );
                    merged.setSpringConstant( interpolate( details.getSpringConstant(), otherDefault.getSpringConstant() ) );
                    details = merged;
                }
            }
        }
        return details;
    }

    private float interpolate( float a, float b ) {
        if ( !Float.isNaN( a ) )
        {
            if ( !Float.isNaN( b ) )
            {
                return ( a + b ) / 2;
            }
            else
            {
                return a;
            }
        }
        else
        {
            return b;
        }
    }

    /**
     * Specify contact details like friction (mu), bouciness etc. for a material pair. You can specify default contact
     * details for any unspecified contact partner by passing null as first parameter.
     * <br>
     * Not that specifying default contact details can lead to undesired behaviour as it depends on the implementation
     * which material is queried upon contact of two different materials.
     * <p/><code>material1.getContactHandlingDetails( material2 )</code><br>
     * could lead to different results than
     * <br><code>material2.getContactHandlingDetails( material1 )</code><br>
     * if only default contact details are set.</p>
     * <p/>
     * This can avoided by specifying contact details for a pair of materials. If contactMaterial parameter is not null
     * the contact details are set for material1->material2 and material2->material1.
     *
     * @param contactMaterial contact material, null for setting default details
     * @param details         contact details for specified contact pair / default, null to clear
     */
    public void putContactHandlingDetails( Material contactMaterial, ContactHandlingDetails details ) {
        if ( details != null ) {
            details = new MutableContactInfo( details );
        }
        putInternal( contactMaterial, details );
        if ( contactMaterial != null ) {
            contactMaterial.putInternal( this, details );
        }
    }

    private void putInternal( Material contactMaterial, ContactHandlingDetails details ) {
        if ( contactDetails == null ) {
            contactDetails = new HashMap<Material, ContactHandlingDetails>();
        }
        contactDetails.put( contactMaterial, details );
    }

    private Vector3f surfaceMotion;


    /**
     * Surface motion - if set, the surface is assumed to be moving
     * independently of the motion of the nodes. This is kind of like a conveyor belt running over the surface.
     *
     * @param motion this vector specifies the motion of the surface in geometry coordinate space. This results in a
     *               motion relative to the world rotation of the geometry but independent from the direction of the surface itself.
     */
    public void setSurfaceMotion( Vector3f motion ) {
        if ( surfaceMotion == null ) {
            surfaceMotion = new Vector3f();
        }
        surfaceMotion.set( motion );
    }

    /**
     * @param store where to store the retrieved value (null to create a new vector)
     * @return surface motion
     * @see #setSurfaceMotion(com.jme.math.Vector3f)
     */
    public Vector3f getSurfaceMotion( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        if ( surfaceMotion != null ) {
            store.set( surfaceMotion );
        } else {
            store.set( 0, 0, 0 );
        }
        return store;
    }

    @Override
    public String toString() {
        return String.valueOf( name );
    }

    private float springPenetrationDepth = 0;

    public float getSpringPenetrationDepth() {
        return springPenetrationDepth;
    }

    public void setSpringPenetrationDepth( float springPenetrationDepth ) {
        this.springPenetrationDepth = springPenetrationDepth;
    }
    
    /**
     * Returns whether the given material is one of the common materials
     * (GLASS, WOOD, etc.). This is used when loading materials to reduce
     * the number of material instances.
     * 
     * @param material The material to check
     * @return The common material instance, or the original material if no common one was found.
     */
    public static Material checkForCommonMaterial(Material material) {
    	if (material == null)
    		return null;
    	for (Material commonMaterial : commonMaterials) {
    		// contact handling details are not compared here, because it would
    		// make the comparison with a loaded material very tricky
        	boolean equal = commonMaterial.name.equals(material.name) &&
    			Float.compare(commonMaterial.density, material.density) == 0 &&
    			Float.compare(commonMaterial.springPenetrationDepth, material.springPenetrationDepth) == 0 &&
    			(commonMaterial.surfaceMotion != null ? commonMaterial.surfaceMotion.equals(material.surfaceMotion) : material.surfaceMotion == null);

        	if (equal)
				return commonMaterial;
		}
    	return material;
    }
    
	private static final String NAME_PROPERTY = "name";
    private static final String DENSITY_PROPERTY = "density";
    private static final String CONTACT_HANDLING_DETAILS_PROPERTY = "contactHandlingDetails";
    private static final String SURFACE_MOTION_PROPERTY = "sufaceMotion";
    private static final String SPRING_PENETRATION_DEPTH_PROPERTY = "springPenetrationDepth";

	public Class getClassTag() {
		return Material.class;
	}

	@SuppressWarnings({"unchecked"})
    public void read(JMEImporter im) throws IOException {

        InputCapsule capsule = im.getCapsule( this );
        
        setName(capsule.readString(NAME_PROPERTY, null));
        setDensity(capsule.readFloat(DENSITY_PROPERTY, 1.0f));
        
        Vector3f surfaceMotion = (Vector3f) capsule.readSavable(SURFACE_MOTION_PROPERTY, Vector3f.ZERO );
        if (!Vector3f.ZERO.equals(surfaceMotion)) 
        	setSurfaceMotion(surfaceMotion);
        
        setSpringPenetrationDepth(capsule.readFloat(SPRING_PENETRATION_DEPTH_PROPERTY, 0.0f));

        contactDetails = null;
        Map<Material, ContactHandlingDetails> loadedContactDetails =
                (Map<Material, ContactHandlingDetails>)capsule.readSavableMap(CONTACT_HANDLING_DETAILS_PROPERTY, null);
        for (Entry<Material, ContactHandlingDetails> entry : loadedContactDetails.entrySet()) {
			Material material = checkForCommonMaterial(entry.getKey());
			putInternal(material, entry.getValue());
		}
	}

	public void write(JMEExporter ex) throws IOException {
		
		OutputCapsule capsule = ex.getCapsule( this );
		
        capsule.write(getName(), NAME_PROPERTY, null);
        capsule.write(getDensity(), DENSITY_PROPERTY, 1.0f);
        capsule.writeSavableMap(contactDetails, CONTACT_HANDLING_DETAILS_PROPERTY, null);
        capsule.write(getSurfaceMotion( null ), SURFACE_MOTION_PROPERTY, Vector3f.ZERO );
        capsule.write(getSpringPenetrationDepth(), SPRING_PENETRATION_DEPTH_PROPERTY, 0.0f);
	}
}

/*
 * $log$
 */

