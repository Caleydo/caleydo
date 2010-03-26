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

public final class ApplicationAccessInfo implements java.lang.Cloneable, java.io.Serializable
{
    public UserWindowAccess access;

    public int applicationID;

    public ApplicationAccessInfo()
    {
    }

    public ApplicationAccessInfo(UserWindowAccess access, int applicationID)
    {
        this.access = access;
        this.applicationID = applicationID;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        ApplicationAccessInfo _r = null;
        try
        {
            _r = (ApplicationAccessInfo)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(access != _r.access && access != null && !access.equals(_r.access))
            {
                return false;
            }
            if(applicationID != _r.applicationID)
            {
                return false;
            }

            return true;
        }

        return false;
    }

    public int
    hashCode()
    {
        int __h = 0;
        if(access != null)
        {
            __h = 5 * __h + access.hashCode();
        }
        __h = 5 * __h + applicationID;
        return __h;
    }

    public java.lang.Object
    clone()
    {
        java.lang.Object o = null;
        try
        {
            o = super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return o;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        access.__write(__os);
        __os.writeInt(applicationID);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        access = UserWindowAccess.__read(__is);
        applicationID = __is.readInt();
    }
}
