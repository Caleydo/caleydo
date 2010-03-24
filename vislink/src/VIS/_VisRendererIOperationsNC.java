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

public interface _VisRendererIOperationsNC
{
    void registerManager(VisManagerIPrx manager);

    boolean registerSelectionContainer(SelectionContainer container);

    boolean updateSelectionContainer(SelectionContainer container);

    void unregisterSelectionContainer(int id);

    AccessInformation getAccessInformation(int sourceApplicationId);

    void renderLinks(SelectionGroup selections);

    void renderAllLinks(SelectionReport selections);

    void clearSelections();

    void clearAll();

    InteractionEvent[] getInteractionEventQueue();
}
