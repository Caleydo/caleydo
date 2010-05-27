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
package com.jmex.physics.util;

import java.util.AbstractList;
import java.util.List;

/**
 * A utility class that allow to use a list as association role implementation.
 * @author Irrisor
 */
public class AssocList<O> extends AbstractList<O> {

    /**
     * Callback interface for {@link AssocList}.
     */
    public interface ModificationHandler<O> {

        /**
         * @param element element that was added to the list
         */
        void added( O element );

        /**
         * @param element element that was removed from the list
         */
        void removed( O element );

        /**
         * @param element what could be added
         * @return true if element can be added, false to ignore add
         */
        boolean canAdd( O element );
    }

    private final List<O> delegate;
    private final ModificationHandler<O> handler;

    public AssocList( List<O> delegate, ModificationHandler<O> handler ) {
        if ( delegate == null ) {
            throw new NullPointerException();
        }
        if ( handler == null ) {
            throw new NullPointerException();
        }
        this.delegate = delegate;
        this.handler = handler;
    }

    @Override
    public O get( int index ) {
        return delegate.get( index );
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public void add( int index, O element ) {
        if ( element == null ) {
            throw new NullPointerException();
        }
        if ( handler.canAdd( element ) ) {
            delegate.add( index, element );
            handler.added( element );
        }
    }

    @Override
    public O remove( int index ) {
        O oldValue = delegate.remove( index );
        if ( oldValue != null )
        {
            handler.removed( oldValue );
        }
        return oldValue;
    }

    @Override
    public O set( int index, O element ) {
        if ( element == null ) {
            throw new NullPointerException();
        }
        O oldValue = delegate.set( index, element );
        if ( oldValue != element ) {
            handler.removed( oldValue );
            handler.added( element );
        }
        return oldValue;
    }
}

/*
 * $log$
 */

