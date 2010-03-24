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

public final class InteractionEventPrxHelper extends Ice.ObjectPrxHelperBase implements InteractionEventPrx
{
    public static InteractionEventPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        InteractionEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (InteractionEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::InteractionEvent"))
                {
                    InteractionEventPrxHelper __h = new InteractionEventPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static InteractionEventPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        InteractionEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (InteractionEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::InteractionEvent", __ctx))
                {
                    InteractionEventPrxHelper __h = new InteractionEventPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static InteractionEventPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        InteractionEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::InteractionEvent"))
                {
                    InteractionEventPrxHelper __h = new InteractionEventPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static InteractionEventPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        InteractionEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::InteractionEvent", __ctx))
                {
                    InteractionEventPrxHelper __h = new InteractionEventPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static InteractionEventPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        InteractionEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (InteractionEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                InteractionEventPrxHelper __h = new InteractionEventPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static InteractionEventPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        InteractionEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            InteractionEventPrxHelper __h = new InteractionEventPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _InteractionEventDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _InteractionEventDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, InteractionEventPrx v)
    {
        __os.writeProxy(v);
    }

    public static InteractionEventPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            InteractionEventPrxHelper result = new InteractionEventPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
