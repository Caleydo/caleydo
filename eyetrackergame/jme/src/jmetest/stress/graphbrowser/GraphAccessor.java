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

package jmetest.stress.graphbrowser;

/**
 * Interface for accessing data of a graph.
 */
public interface GraphAccessor {
    /**
     * Specifies number of nodes such that 0 &lt;= index &lt; number of nodes.
     * @return number of nodes in the graph.
     * @see #getNode(int)
     */
    int getNodeCount();

    /**
     * Query the object representing the nth node in the graph.
     * @param index index of the node (n).
     * @return node at index
     */
    Object getNode( int index );

    /**
     * Specifies number of edges such that 0 &lt;= index &lt; number of edges.
     * @return number of edges in the graph.
     * @see #getEdge(int)
     */
    int getEdgeCount();

    /**
     * Query the object representing the nth edge in the graph.
     * @param index index of the edge (n).
     * @return edge at index
     */
    Object getEdge( int index );

    /**
     * Query source node of an edge.
     * @param edge edge of interest
     * @return object representing a node in the graph where the edge starts
     */
    Object getEdgeSource( Object edge );

    /**
     * Query target node of an edge.
     * @param edge edge of interest
     * @return object representing a node in the graph where the edge ends
     */
    Object getEdgeTarget( Object edge );

    /**
     * @param node any node object
     * @return true if the node is a terminal node (should be highlighted)
     */
    boolean isNodeTerminal( Object node );

    /**
     * @param edge any edge object
     * @return true if the edge is part of the selected path in the graph (should be highlighted)
     */
    boolean isEdgePath( Object edge );
}
