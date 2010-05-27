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

package com.jme.bounding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jme.bounding.CollisionTree;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;

/**
 * CollisionTreeManager is an automated system for handling the creation and
 * deletion of CollisionTrees. The manager maintains a cache map of currently
 * generated collision trees. The collision system itself requests a collision
 * tree from the manager via the <code>getCollisionTree</code> method. The
 * cache is checked for the tree, and if it is available, sent to the caller. If
 * the tree is not in the cache, and generateTrees is true, a new CollisionTree
 * is generated on the fly and sent to the caller. When a new tree is created,
 * the cache size is compared to the maxElements value. If the cache is larger
 * than maxElements, the cache is sent to the CollisionTreeController for
 * cleaning. There are a number of settings that can be used to control how
 * trees are generated. First, generateTrees denotes whether the manager should
 * be creating trees at all. This is set to true by default. doSort defines if
 * the CollisionTree triangle array should be sorted as it is built. This is
 * false by default. Sorting is beneficial for model data that is not well
 * ordered spatially. This occurrence is rare, and sorting slows creation time.
 * It is, therefore, only to be used when model data requires it. maxTrisPerLeaf
 * defines the number of triangles a leaf node in the collision tree should
 * maintain. The larger number of triangles maintained in a leaf node, the
 * smaller the tree, but the larger the number of triangle checks during a
 * collision. By default, this value is set to 16. maxElements defines the
 * maximum number of trees that will be maintained before clean-up is required.
 * A collision tree is defined for each mesh that is being collided with. The
 * user should determine the optimal number of trees to maintain (a
 * memory/performance tradeoff), based on the number of meshes, their
 * population density and their triangle size. By default, this value is set to
 * 25. The type of trees that will be generated is defined by the treeType
 * value, where valid options are define in CollisionTree as AABB_TREE, OBB_TREE
 * and SPHERE_TREE. You can set the functionality of how trees are removed from
 * the cache by providing the manager with a CollisionTreeController
 * implementation. By default, the manager will use the UsageTreeController for
 * removing trees, but any other CollisionTreeController is acceptable. You can
 * create protected tree manually. These are collision trees that you request
 * the manager to create and not allow them to be removed by the
 * CollisionTreeController.
 * 
 * @author Mark Powell
 * @see com.jme.bounding.CollisionTree
 * @see com.jme.bounding.CollisionTreeController
 */
public class CollisionTreeManager {
    /**
     * defines the default maximum number of trees to maintain.
     */
    public static final int DEFAULT_MAX_ELEMENTS = 25;
    /**
     * defines the default maximum number of triangles in a tree leaf.
     */
    public static final int DEFAULT_MAX_TRIS_PER_LEAF = 16;

    // the singleton instance of the manager
    private static CollisionTreeManager instance = new CollisionTreeManager();

    // the cache and protected list for storing trees.
    private Map<TriMesh, CollisionTree> cache;
    private List<TriMesh> protectedList;

    private boolean generateTrees = true;
    private boolean doSort;

    private CollisionTree.Type treeType = CollisionTree.Type.AABB;

    private int maxTrisPerLeaf = DEFAULT_MAX_TRIS_PER_LEAF;
    private int maxElements = DEFAULT_MAX_ELEMENTS;

    private CollisionTreeController treeRemover;

    /**
     * private constructor for the Singleton. Initializes the cache.
     */
    private CollisionTreeManager() {
        cache = Collections.synchronizedMap(new LinkedHashMap<TriMesh, CollisionTree>(1));
        setCollisionTreeController(new UsageTreeController());
    }

    /**
     * retrieves the singleton instance of the CollisionTreeManager.
     * 
     * @return the singleton instance of the manager.
     */
    public static CollisionTreeManager getInstance() {
        return instance;
    }

    /**
     * sets the CollisionTreeController used for cleaning the cache when the
     * maximum number of elements is reached.
     * 
     * @param treeRemover
     *            the controller used to clean the cache.
     */
    public void setCollisionTreeController(CollisionTreeController treeRemover) {
        this.treeRemover = treeRemover;
    }

    /**
     * getCollisionTree obtains a collision tree that is assigned to a supplied
     * TriMesh. The cache is checked for a pre-existing tree, if none is
     * available and generateTrees is true, a new tree is created and returned.
     * 
     * @param mesh
     *            the mesh to use as the key for the tree to obtain.
     * @return the tree associated with a triangle mesh
     */
    public synchronized CollisionTree getCollisionTree(TriMesh mesh) {
        CollisionTree toReturn = null;

        // If we have a shared mesh, we want to use the tree of the target.
        // However, the tree requires world transform information, therefore,
        // set the parent of the tree to that of the shared mesh's parent
        // and return it.
        if (mesh instanceof SharedMesh) {
            toReturn = cache.get(((SharedMesh) mesh).getTarget());
            if (toReturn != null) {
                toReturn.mesh = mesh;
            }
        } else {
            // check cache
            toReturn = cache.get(mesh);
        }

        // we didn't have it in the cache, create it if possible.
        if (toReturn == null) {
            if (generateTrees) {
                return generateCollisionTree(treeType, mesh, false);
            } else {
                return null;
            }
        } else {
            // we had it in the cache, to keep the keyset in order, reinsert
            // this element
            cache.remove(mesh);
            cache.put(mesh, toReturn);
            return toReturn;
        }
    }

    /**
     * creates a new collision tree for the provided spatial. If the spatial is
     * a node, it recursively calls generateCollisionTree for each child. If it
     * is a TriMesh, a call to generateCollisionTree is made for each mesh. If
     * this tree(s) is to be protected, i.e. not deleted by the
     * CollisionTreeController, set protect to true.
     * 
     * @param type
     *            the type of collision tree to generate.
     * @param object
     *            the Spatial to generate tree(s) for.
     * @param protect
     *            true to keep these trees from being removed, false otherwise.
     */
	public void generateCollisionTree(CollisionTree.Type type,
            Spatial object, boolean protect) {
		if (object instanceof TriMesh)
			generateCollisionTree(type, (TriMesh)object, protect);
		if (object instanceof Node) {
			if (((Node)object).getQuantity() > 0)
				for (Spatial sp : ((Node)object).getChildren())
					generateCollisionTree(type, sp, protect);
		}
	}

    /**
     * generates a new tree for the associated mesh. The type is provided and a
     * new tree is constructed of this type. The tree is placed in the cache. If
     * the cache's size then becomes too large, the cache is sent to the
     * CollisionTreeController for clean-up. If this tree is to be protected,
     * i.e. protected from the CollisionTreeController, set protect to true.
     * 
     * @param type
     *            the type of collision tree to generate.
     * @param mesh
     *            the mesh to generate the tree for.
     * @param protect
     *            true if this tree is to be protected, false otherwise.
     * @return the new collision tree.
     */
    public CollisionTree generateCollisionTree(CollisionTree.Type type,
            TriMesh mesh, boolean protect) {
        if (mesh == null) {
            return null;
        }

        CollisionTree tree = new CollisionTree(type);

        return generateCollisionTree(tree, mesh, protect);
    }

    /**
     * generates a new tree for the associated mesh. It is provided with a
     * pre-existing, non-null tree. The tree is placed in the cache. If the
     * cache's size then becomes too large, the cache is sent to the
     * CollisionTreeController for clean-up. If this tree is to be protected,
     * i.e. protected from the CollisionTreeController, set protect to true.
     * 
     * @param tree
     *            the tree to use for generation
     * @param mesh
     *            the mesh to generate the tree for.
     * @param protect
     *            true if this tree is to be protected, false otherwise.
     * @return the new collision tree.
     */
    public CollisionTree generateCollisionTree(CollisionTree tree,
            TriMesh mesh, boolean protect) {
        if (tree != null) {
            if (mesh instanceof SharedMesh) {
                // we might already have the appropriate tree
                if (!cache.containsKey(((SharedMesh) mesh).getTarget())) {
                    tree.construct(((SharedMesh) mesh).getTarget(), doSort);
                    cache.put(((SharedMesh) mesh).getTarget(), tree);
                    // This mesh has been added by outside sources and labeled
                    // as protected. Therefore, put it in the protected list
                    // so it is not removed by a controller.
                    if (protect) {
                        if (protectedList == null) {
                            protectedList = Collections.synchronizedList(new ArrayList<TriMesh>(1));
                        }
                        protectedList.add(((SharedMesh) mesh).getTarget());
                    }
                }
            } else {
                tree.construct(mesh, doSort);
                cache.put(mesh, tree);
                // This mesh has been added by outside sources and labeled
                // as protected. Therefore, put it in the protected list
                // so it is not removed by a controller.
                if (protect) {
                    if (protectedList == null) {
                        protectedList = Collections.synchronizedList(new ArrayList<TriMesh>(1));
                    }
                    protectedList.add(mesh);
                }
            }

            // Are we over our max? Test
            if (cache.size() > maxElements && treeRemover != null) {
                treeRemover.clean(cache, protectedList, maxElements);
            }
        }
        return tree;
    }

    /**
     * removes a collision tree from the manager based on the mesh supplied.
     * 
     * @param mesh
     *            the mesh to remove the corresponding collision tree.
     */
    public void removeCollisionTree(TriMesh mesh) {
        cache.remove(mesh);
    }

    /**
     * removes all collision trees associated with a Spatial object.
     * 
     * @param object
     *            the spatial to remove all collision trees from.
     */
    public void removeCollisionTree(Spatial object) {
        if (object instanceof Node) {
            Node n = (Node) object;
            for (int i = n.getQuantity() - 1; i >= 0; i--) {
                removeCollisionTree(n.getChild(i));
            }
        } else if (object instanceof TriMesh) {
            removeCollisionTree((TriMesh) object);
        }
    }

    /**
     * updates the existing tree for a supplied mesh. If this tree does not
     * exist, the tree is not updated. If the tree is not in the cache, no
     * further operations are handled.
     * 
     * @param mesh
     *            the mesh key for the tree to update.
     */
    public void updateCollisionTree(TriMesh mesh) {
        CollisionTree ct = cache.get(mesh);
        if (ct != null) {
            generateCollisionTree(ct, mesh, protectedList != null
                    && protectedList.contains(mesh));
        }
    }

    /**
     * updates the existing tree(s) for a supplied spatial. If this tree does
     * not exist, the tree is not updated. If the tree is not in the cache, no
     * further operations are handled.
     * 
     * @param object
     *            the object on which to update the tree.
     */
    public void updateCollisionTree(Spatial object) {
        if (object instanceof Node) {
            Node n = (Node) object;
            for (int i = n.getQuantity() - 1; i >= 0; i--) {
                updateCollisionTree(n.getChild(i));
            }
        } else if (object instanceof TriMesh) {
            updateCollisionTree((TriMesh) object);
        }
    }

    /**
     * returns true if the manager is set to sort new generated trees. False
     * otherwise.
     * 
     * @return true to sort tree, false otherwise.
     */
    public boolean isDoSort() {
        return doSort;
    }

    /**
     * set if this manager should have newly generated trees sort triangles.
     * 
     * @param doSort
     *            true to sort trees, false otherwise.
     */
    public void setDoSort(boolean doSort) {
        this.doSort = doSort;
    }

    /**
     * returns true if the manager will automatically generate new trees as
     * needed, false otherwise.
     * 
     * @return true if this manager is generating trees, false otherwise.
     */
    public boolean isGenerateTrees() {
        return generateTrees;
    }

    /**
     * set if this manager should generate new trees as needed.
     * 
     * @param generateTrees
     *            true to generate trees, false otherwise.
     */
    public void setGenerateTrees(boolean generateTrees) {
        this.generateTrees = generateTrees;
    }

    /**
     * @return the type of tree the manager will create.
     * @see CollisionTree.Type
     */
    public CollisionTree.Type getTreeType() {
        return treeType;
    }

    /**
     * @param treeType
     *            the type of tree to create.
     * @see CollisionTree.Type
     */
    public void setTreeType(CollisionTree.Type treeType) {
        this.treeType = treeType;
    }

    /**
     * returns the maximum number of triangles a leaf of the collision tree will
     * contain.
     * 
     * @return the maximum number of triangles a leaf will contain.
     */
    public int getMaxTrisPerLeaf() {
        return maxTrisPerLeaf;
    }

    /**
     * set the maximum number of triangles a leaf of the collision tree will
     * contain.
     * 
     * @param maxTrisPerLeaf
     *            the maximum number of triangles a leaf will contain.
     */
    public void setMaxTrisPerLeaf(int maxTrisPerLeaf) {
    	this.maxTrisPerLeaf = maxTrisPerLeaf;
    }
    
    /**
     * Returns the maximum number of trees to maintain.
     */
    public int getMaxElements() {
    	return maxElements;
    }

    /**
     * Sets the maximum number of trees to maintain.
     * @param maxElements
     *            the maximum number of trees to maintain.
     */
    public void setMaxElements(int maxElements) {
    	this.maxElements = maxElements;
    }
}
