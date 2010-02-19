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

public abstract class _VisRendererIDisp extends Ice.ObjectImpl implements VisRendererI
{
    protected void
    ice_copyStateFrom(Ice.Object __obj)
        throws java.lang.CloneNotSupportedException
    {
        throw new java.lang.CloneNotSupportedException();
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::VIS::VisRendererI"
    };

    public boolean
    ice_isA(String s)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean
    ice_isA(String s, Ice.Current __current)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[]
    ice_ids()
    {
        return __ids;
    }

    public String[]
    ice_ids(Ice.Current __current)
    {
        return __ids;
    }

    public String
    ice_id()
    {
        return __ids[1];
    }

    public String
    ice_id(Ice.Current __current)
    {
        return __ids[1];
    }

    public static String
    ice_staticId()
    {
        return __ids[1];
    }

    public final void
    clearAll()
    {
        clearAll(null);
    }

    public final void
    clearSelections()
    {
        clearSelections(null);
    }

    public final AccessInformation
    getAccessInformation(int sourceApplicationId)
    {
        return getAccessInformation(sourceApplicationId, null);
    }

    public final boolean
    registerSelectionContainer(SelectionContainer container)
    {
        return registerSelectionContainer(container, null);
    }

    public final void
    renderAllLinks(SelectionGroup[] selections)
    {
        renderAllLinks(selections, null);
    }

    public final void
    renderLinks(SelectionGroup selections)
    {
        renderLinks(selections, null);
    }

    public final void
    unregisterSelectionContainer(int id)
    {
        unregisterSelectionContainer(id, null);
    }

    public final boolean
    updateSelectionContainer(SelectionContainer container)
    {
        return updateSelectionContainer(container, null);
    }

    public static Ice.DispatchStatus
    ___registerSelectionContainer(VisRendererI __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        SelectionContainer container;
        container = new SelectionContainer();
        container.__read(__is);
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.registerSelectionContainer(container, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___updateSelectionContainer(VisRendererI __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        SelectionContainer container;
        container = new SelectionContainer();
        container.__read(__is);
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.updateSelectionContainer(container, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___unregisterSelectionContainer(VisRendererI __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int id;
        id = __is.readInt();
        __is.endReadEncaps();
        __obj.unregisterSelectionContainer(id, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getAccessInformation(VisRendererI __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int sourceApplicationId;
        sourceApplicationId = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        AccessInformation __ret = __obj.getAccessInformation(sourceApplicationId, __current);
        __ret.__write(__os);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___renderLinks(VisRendererI __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        SelectionGroup selections;
        selections = new SelectionGroup();
        selections.__read(__is);
        __is.endReadEncaps();
        __obj.renderLinks(selections, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___renderAllLinks(VisRendererI __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        SelectionGroup[] selections;
        selections = SelectionGroupListHelper.read(__is);
        __is.endReadEncaps();
        __obj.renderAllLinks(selections, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___clearSelections(VisRendererI __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.clearSelections(__current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___clearAll(VisRendererI __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.clearAll(__current);
        return Ice.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "clearAll",
        "clearSelections",
        "getAccessInformation",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "registerSelectionContainer",
        "renderAllLinks",
        "renderLinks",
        "unregisterSelectionContainer",
        "updateSelectionContainer"
    };

    public Ice.DispatchStatus
    __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
        }

        switch(pos)
        {
            case 0:
            {
                return ___clearAll(this, in, __current);
            }
            case 1:
            {
                return ___clearSelections(this, in, __current);
            }
            case 2:
            {
                return ___getAccessInformation(this, in, __current);
            }
            case 3:
            {
                return ___ice_id(this, in, __current);
            }
            case 4:
            {
                return ___ice_ids(this, in, __current);
            }
            case 5:
            {
                return ___ice_isA(this, in, __current);
            }
            case 6:
            {
                return ___ice_ping(this, in, __current);
            }
            case 7:
            {
                return ___registerSelectionContainer(this, in, __current);
            }
            case 8:
            {
                return ___renderAllLinks(this, in, __current);
            }
            case 9:
            {
                return ___renderLinks(this, in, __current);
            }
            case 10:
            {
                return ___unregisterSelectionContainer(this, in, __current);
            }
            case 11:
            {
                return ___updateSelectionContainer(this, in, __current);
            }
        }

        assert(false);
        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeTypeId(ice_staticId());
        __os.startWriteSlice();
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void
    __read(IceInternal.BasicStream __is, boolean __rid)
    {
        if(__rid)
        {
            __is.readTypeId();
        }
        __is.startReadSlice();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void
    __write(Ice.OutputStream __outS)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type VIS::VisRendererI was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type VIS::VisRendererI was not generated with stream support";
        throw ex;
    }
}
