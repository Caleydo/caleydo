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

public interface VisManagerIPrx extends Ice.ObjectPrx
{
    public void reportEvent(InteractionEvent event);
    public void reportEvent(InteractionEvent event, java.util.Map<String, String> __ctx);
}
