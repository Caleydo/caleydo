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

public interface _VisRendererIOperations
{
    boolean registerSelectionContainer(SelectionContainer container, Ice.Current __current);

    boolean updateSelectionContainer(SelectionContainer container, Ice.Current __current);

    void unregisterSelectionContainer(int id, Ice.Current __current);

    AccessInformation getAccessInformation(int sourceApplicationId, Ice.Current __current);

    void renderLinks(SelectionGroup selections, Ice.Current __current);

    void renderAllLinks(SelectionReport selections, Ice.Current __current);

    void clearSelections(Ice.Current __current);

    void clearAll(Ice.Current __current);
}
