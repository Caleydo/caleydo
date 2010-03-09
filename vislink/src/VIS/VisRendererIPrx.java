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

public interface VisRendererIPrx extends Ice.ObjectPrx
{
    public boolean registerSelectionContainer(SelectionContainer container);
    public boolean registerSelectionContainer(SelectionContainer container, java.util.Map<String, String> __ctx);

    public boolean updateSelectionContainer(SelectionContainer container);
    public boolean updateSelectionContainer(SelectionContainer container, java.util.Map<String, String> __ctx);

    public void unregisterSelectionContainer(int id);
    public void unregisterSelectionContainer(int id, java.util.Map<String, String> __ctx);

    public AccessInformation getAccessInformation(int sourceApplicationId);
    public AccessInformation getAccessInformation(int sourceApplicationId, java.util.Map<String, String> __ctx);

    public void renderLinks(SelectionGroup selections);
    public void renderLinks(SelectionGroup selections, java.util.Map<String, String> __ctx);

    public void renderAllLinks(SelectionReport selections);
    public void renderAllLinks(SelectionReport selections, java.util.Map<String, String> __ctx);

    public void clearSelections();
    public void clearSelections(java.util.Map<String, String> __ctx);

    public void clearAll();
    public void clearAll(java.util.Map<String, String> __ctx);
}
