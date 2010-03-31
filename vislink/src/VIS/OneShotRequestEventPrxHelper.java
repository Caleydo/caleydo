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

public final class OneShotRequestEventPrxHelper extends Ice.ObjectPrxHelperBase implements OneShotRequestEventPrx
{
    public static OneShotRequestEventPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        OneShotRequestEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (OneShotRequestEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::OneShotRequestEvent"))
                {
                    OneShotRequestEventPrxHelper __h = new OneShotRequestEventPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static OneShotRequestEventPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        OneShotRequestEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (OneShotRequestEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::OneShotRequestEvent", __ctx))
                {
                    OneShotRequestEventPrxHelper __h = new OneShotRequestEventPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static OneShotRequestEventPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        OneShotRequestEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::OneShotRequestEvent"))
                {
                    OneShotRequestEventPrxHelper __h = new OneShotRequestEventPrxHelper();
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

    public static OneShotRequestEventPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        OneShotRequestEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::OneShotRequestEvent", __ctx))
                {
                    OneShotRequestEventPrxHelper __h = new OneShotRequestEventPrxHelper();
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

    public static OneShotRequestEventPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        OneShotRequestEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (OneShotRequestEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                OneShotRequestEventPrxHelper __h = new OneShotRequestEventPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static OneShotRequestEventPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        OneShotRequestEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            OneShotRequestEventPrxHelper __h = new OneShotRequestEventPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _OneShotRequestEventDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _OneShotRequestEventDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, OneShotRequestEventPrx v)
    {
        __os.writeProxy(v);
    }

    public static OneShotRequestEventPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            OneShotRequestEventPrxHelper result = new OneShotRequestEventPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
