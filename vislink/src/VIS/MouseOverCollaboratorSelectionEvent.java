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

public class MouseOverCollaboratorSelectionEvent extends InteractionEvent
{
    public MouseOverCollaboratorSelectionEvent()
    {
        super();
    }

    public MouseOverCollaboratorSelectionEvent(InteractionEventType eventType, String pointerId, int pointerX, int pointerY, String ownerPointerId, AccessInformation pointerAccessInformation, int srcApp)
    {
        super(eventType, pointerId, pointerX, pointerY);
        this.ownerPointerId = ownerPointerId;
        this.pointerAccessInformation = pointerAccessInformation;
        this.srcApp = srcApp;
    }

    private static class __F implements Ice.ObjectFactory
    {
        public Ice.Object
        create(String type)
        {
            assert(type.equals(ice_staticId()));
            return new MouseOverCollaboratorSelectionEvent();
        }

        public void
        destroy()
        {
        }
    }
    private static Ice.ObjectFactory _factory = new __F();

    public static Ice.ObjectFactory
    ice_factory()
    {
        return _factory;
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::VIS::InteractionEvent",
        "::VIS::MouseOverCollaboratorSelectionEvent"
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
        return __ids[2];
    }

    public String
    ice_id(Ice.Current __current)
    {
        return __ids[2];
    }

    public static String
    ice_staticId()
    {
        return __ids[2];
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeTypeId(ice_staticId());
        __os.startWriteSlice();
        __os.writeString(ownerPointerId);
        pointerAccessInformation.__write(__os);
        __os.writeInt(srcApp);
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
        ownerPointerId = __is.readString();
        pointerAccessInformation = new AccessInformation();
        pointerAccessInformation.__read(__is);
        srcApp = __is.readInt();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void
    __write(Ice.OutputStream __outS)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type VIS::MouseOverCollaboratorSelectionEvent was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type VIS::MouseOverCollaboratorSelectionEvent was not generated with stream support";
        throw ex;
    }

    public String ownerPointerId;

    public AccessInformation pointerAccessInformation;

    public int srcApp;
}
