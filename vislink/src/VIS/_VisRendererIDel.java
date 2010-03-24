// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.1

package VIS;

public interface _VisRendererIDel extends Ice._ObjectDel
{
    void registerManager(VisManagerIPrx manager, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean registerSelectionContainer(SelectionContainer container, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean updateSelectionContainer(SelectionContainer container, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void unregisterSelectionContainer(int id, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    AccessInformation getAccessInformation(int sourceApplicationId, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void renderLinks(SelectionGroup selections, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void renderAllLinks(SelectionReport selections, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void clearSelections(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void clearAll(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    InteractionEvent[] getInteractionEventQueue(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;
}
