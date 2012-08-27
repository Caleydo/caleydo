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

public final class AccessInformation implements java.lang.Cloneable, java.io.Serializable
{
    public String pointerId;

    public ApplicationAccessInfo[] applications;

    public AccessInformation()
    {
    }

    public AccessInformation(String pointerId, ApplicationAccessInfo[] applications)
    {
        this.pointerId = pointerId;
        this.applications = applications;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        AccessInformation _r = null;
        try
        {
            _r = (AccessInformation)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(pointerId != _r.pointerId && pointerId != null && !pointerId.equals(_r.pointerId))
            {
                return false;
            }
            if(!java.util.Arrays.equals(applications, _r.applications))
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
        if(pointerId != null)
        {
            __h = 5 * __h + pointerId.hashCode();
        }
        if(applications != null)
        {
            for(int __i0 = 0; __i0 < applications.length; __i0++)
            {
                if(applications[__i0] != null)
                {
                    __h = 5 * __h + applications[__i0].hashCode();
                }
            }
        }
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
        __os.writeString(pointerId);
        ApplicationAccessListHelper.write(__os, applications);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        pointerId = __is.readString();
        applications = ApplicationAccessListHelper.read(__is);
    }
}