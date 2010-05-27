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

package jmetest.stress.swarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

/**
 * The CollisionTreeManager creates a tree of Nodes in it's {@link #reorganize()} method to speed up collision
 * detection (and other tree dependent operations) for scene graph that would otherwise be flat.
 * <br>
 * See {@link TestSwarm} for a usage example.
 * <br>
 * In detail this manager does the following: On each call of {@link #reorganize()} it iterates all Spatials that
 * have been added (via the {@link #add method}) before. For each Spatial it computes an index in space. This index
 * has 4 ordinates x, y, z and level. Where level starts with 0 and describes the granularity of the index; x, y and z
 * are coimputed from the actual position of a Spatial divided by the levelDistance (see ctor) then rounded to an int.
 * For each index of an added Spatial a Sector (subclass of {@link Node}) is created which receives all Spatials with
 * that index. All Sectors of level 0 are then subsumed in Sectors of level 1, and so on (up to the number of levels).
 * The final level of Sectors is then added to the parent node (see {@link #CollisionTreeManager(Node, float[])}).
 *
 * @author Irrisor
 * @created 17.07.2005, 17:20:58
 */
public class CollisionTreeManager {

    /**
     * node that receives the top level Sectors.
     */
    private Node parent;

    /**
     * array with multiplicators for level indices, 1/levelDistance.
     */
    private final float[] levelMults;

    /**
     * Creates a new CollisionTreeManager. The level distances specify the approximate size (diameter) of a sector.
     * Calling <code>new CollisionTreeManager( rootNode, new float[]{0.2f, 1.2f} );</code> results in level 0
     * Sectors of size 0.2 and level 1 Sectors of size 1.2. The minimum level distance should reflect the average
     * distance of Spatials in a scene times the desired number of Spatials per Sector (to the power 1/3).
     *
     * @param parent         the Node that receives the top level Sectors
     * @param levelDistances array of level distances, length must be greater 0
     */
    public CollisionTreeManager( Node parent, float[] levelDistances ) {
        this.parent = parent;
        if ( levelDistances.length == 0 ) {
            throw new IllegalArgumentException( "Number of levels must be greater than zero." );
        }
        this.levelMults = new float[levelDistances.length];
        float last = 0;
        for ( int i = 0; i < levelDistances.length; i++ ) {
            final float dist = levelDistances[i];
            if ( dist > last ) {
                last = dist;
            }
            else {
                throw new IllegalArgumentException( "levelDistances must be ascending in size" );
            }
            levelMults[i] = 1.0f / dist;
        }
    }

    /**
     * List of Spatials, added via {@link #add}.
     */
    private ArrayList<Spatial> organizationLeafs = new ArrayList<Spatial>();

    /**
     * Add a spatial to the manager. This is first added to the parent Node. On call of {@link #reorganize()} it is then
     * put into a Sector.
     *
     * @param spatial what to add
     */
    public void add( Spatial spatial ) {
        if ( spatial.getParent() != null ) {
            throw new IllegalArgumentException( "added spatial must not be attached to a node!" );
        }
        parent.attachChild( spatial );
        organizationLeafs.add( spatial );
    }

    /**
     * Remove a Spatial from the list of managed Spatials.
     *
     * @param spatial what to remove
     */
    public void remove( Spatial spatial ) {
        int index = organizationLeafs.indexOf( spatial );
        remove( index );
    }

    /**
     * Remove Spatial at specified index from the list of managed Spatials.
     *
     * @param index where to remove
     */
    private void remove( int index ) {
        if ( index != -1 ) {
            Spatial spatial = organizationLeafs.remove( index );
            if ( spatial.getParent() != null ) {
                spatial.getParent().detachChild( spatial );
            }
        }
    }

    /**
     * Class to represent the index of a sector.
     */
    protected class SectorIndex {
        /**
         * coordinates of the sector.
         */
        private int x, y, z;
        private int level;
        private int hash;

        /**
         * don't allow creation in subclasses.
         */
        private SectorIndex() {
        }

        /**
         * Hashcode depends on coordinates and level.
         *
         * @return a hash code value for this object.
         */
        public int hashCode() {
            return hash;
        }

        /**
         * Equals to an index if coordinates and level are the same.
         *
         * @return <code>true</code> if this object is the same as the obj
         *         argument; <code>false</code> otherwise.
         */
        public boolean equals( Object obj ) {
            if ( obj instanceof SectorIndex ) {
                final SectorIndex other = ( (SectorIndex) obj );
                return x == other.x && y == other.y && z == other.z && level == other.level;
            }
            
            return false;            
        }

        /**
         * Set data in this index to the values in the given index.
         *
         * @param index where to read from
         */
        protected void set( SectorIndex index ) {
            this.x = index.x;
            this.y = index.y;
            this.z = index.z;
            this.level = index.level;
            this.hash = doHash();
        }

        /**
         * Set data in this index.
         *
         * @param x     x-coordinate
         * @param y     y-coordinate
         * @param z     z-coordinate
         * @param level level of this index
         */
        protected void set( int x, int y, int z, int level ) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.level = level;
            this.hash = doHash();
        }

        /**
         * Compute a hash value from coordinates and level.
         */
        private int doHash() {
            return x ^ ( ( ( ( ( level << 10 ) ^ z ) << 10 ) ^ y ) << 10 );
        }

        /**
         * @return level of this index (0 = near leaves)
         */
        public int getLevel() {
            return level;
        }
    }

    /**
     * A Sector structures the managed Spatials (see {@link CollisionTreeManager#add}) into partitions to form a Tree
     * instead of a flat graph.
     * @see CollisionTreeManager
     * @see SectorIndex
     */
    public class Sector extends Node {
        private static final long serialVersionUID = 1L;
        /**
         * index of this sector.
         */
        private SectorIndex index = new SectorIndex();

        /**
         * Create a new sector.
         * @param name name of this sector
         * @param index data for the index of this sector
         */
        public Sector( String name, SectorIndex index ) {
            super( name );
            this.index.set( index );
        }

        /**
         * @see CollisionTreeManager
         * @return index of this sector
         */
        public SectorIndex getIndex() {
            return index;
        }
    }

    /**
     * Map from SectorIndex to Sector
     */
    private Map<SectorIndex, Sector> sectorMap = new HashMap<SectorIndex, Sector>();

    /**
     * Sectors that have been created and can be recycled.
     */
    private ArrayList<Sector> unusedSectors = new ArrayList<Sector>();

    /**
     * to flatline memory usage.
     */
    private SectorIndex tmp_index = new SectorIndex();


    /**
     * Attach all added Spatials directly to the parent node. Remove all Sectors.
     * After calling this method the manager can be safely omitted.
     */
    public void disable() {
        for ( int i = organizationLeafs.size() - 1; i >= 0; i-- ) {
            Spatial spatial = organizationLeafs.get( i );
            parent.attachChild( spatial );
        }
        removeSectors( parent );
    }

    /**
     * remove all empty sectors contained in container (if Node or Sector).
     * @param container where to remove Sectors from.
     */
    private void removeSectors( Spatial container ) {
        if ( container instanceof Node ) {
            final Node node = ( (Node) container );
            for ( int i = node.getQuantity() - 1; i >= 0; i-- ) {
                Spatial spatial = node.getChild( i );
                removeSectors( spatial );
            }
            if ( node instanceof Sector ) {
                removeIfEmpty( (Sector) node );
            }
        }
    }

    /**
     * Reorganizes Sectors as described in the class description.
     * @see CollisionTreeManager
     */
    public void reorganize() {
        for ( int i = organizationLeafs.size() - 1; i >= 0; i-- ) {
            Spatial spatial = organizationLeafs.get( i );
            final SectorIndex index = getIndex( spatial, 0 );
            Sector sector;
            if ( spatial.getParent() == null ) {
                remove( i );
            }
            else {
                if ( spatial.getParent() instanceof Sector ) {
                    sector = (Sector) spatial.getParent();
                    if ( !sector.getIndex().equals( index ) ) {
                        sector.detachChild( spatial );
                        removeIfEmpty( sector );
                        sector = null;
                    }
                }
                else {
                    sector = null;
                }

                if ( sector == null ) {
                    sector = getSector( index );
                    sector.attachChild( spatial );
                }
            }
        }
    }

    /**
     * Flag a Sector for reuse if it is empty (has no children).
     * @param sector which Sector to be checked
     */
    private void removeIfEmpty( Sector sector ) {
        if ( sector.getQuantity() == 0 ) {
            sector.getParent().detachChild( sector );
            sectorMap.remove( sector.index );
            unusedSectors.add( sector );
        }
    }

    /**
     * Find or create a sector for given index.
     * @param index position for sector
     * @return found/created sector
     */
    private Sector getSector( SectorIndex index ) {
        Sector sector = sectorMap.get( index );
        if ( sector == null ) {
            final int unusedSectorsSize = unusedSectors.size();
            if ( unusedSectorsSize > 0 ) {
                sector = unusedSectors.remove( unusedSectorsSize - 1 );
                sector.index.set( index );
            }
            else {
                sector = new Sector( "", index );
            }
            sectorMap.put( sector.getIndex(), sector );   //todo: this creates a bucket :(
            final int level = index.getLevel();
            if ( level < levelMults.length - 1 ) {
                index = getIndex( index, level + 1 );
                getSector( index ).attachChild( sector );
            }
            else {
                parent.attachChild( sector );
            }
        }
        return sector;
    }

    /**
     * Compute an index for a different level.
     * @param index former index
     * @param level level for returned index
     * @return index that was computed
     */
    private SectorIndex getIndex( SectorIndex index, int level ) {
        float oldLevelSize = levelMults[index.getLevel()];
        final float x = index.x / oldLevelSize;
        final float y = index.y / oldLevelSize;
        final float z = index.z / oldLevelSize;
        return getIndexFromPos( x, y, z, level );
    }

    /**
     * Compute an index from the position of a Spatial and a level.
     * @param spatial where to read world translation
     * @param level level of the index
     * @return index for the Spatial
     */
    private SectorIndex getIndex( Spatial spatial, int level ) {
        final Vector3f pos = spatial.getWorldTranslation();
        final float x = pos.x;
        final float y = pos.y;
        final float z = pos.z;
        return getIndexFromPos( x, y, z, level );
    }

    /**
     * Actual computation for world coordinate to index coordinate.
     * @param x world-x
     * @param y world-y
     * @param z world-z
     * @param level level of the index
     * @return the index for the world-coordinates
     */
    private SectorIndex getIndexFromPos( final float x, final float y, final float z, int level ) {
        float levelSize = levelMults[level];
        tmp_index.set( (int) ( x * levelSize ), (int) ( y * levelSize ), (int) ( z * levelSize ), level );
        return tmp_index;
    }
}
