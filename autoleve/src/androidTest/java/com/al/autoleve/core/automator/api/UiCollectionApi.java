package com.al.autoleve.core.automator.api;

import android.support.test.uiautomator.UiCollection;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

/**
 * UiCollection是UiObject的子类。用来表示一个父控件，该控件下包含了子元素的集合。
 */
public class UiCollectionApi {
    public static UiCollection getUiCollection(UiSelector selector)
    {
        return new UiCollection(selector);
    }

    /**
     * 通过两个条件：selector和description属性，定位到其下符合条件的子控件
     *
     * @param collection
     * @param childPattern
     * @param desc
     * @return
     * @throws UiObjectNotFoundException
     */
    public static UiObject getChildByDescription(UiCollection collection, UiSelector childPattern, String desc) throws UiObjectNotFoundException {
        return collection.getChildByDescription(childPattern, desc);
    }

    /**
     * 通过两个条件：selector和text属性，定位到其下符合条件的子控件
     *
     * @param collection
     * @param childPattern
     * @param text
     * @return
     * @throws UiObjectNotFoundException
     */
    public static UiObject getChildByText (UiCollection collection, UiSelector childPattern, String text) throws UiObjectNotFoundException {
        return collection.getChildByText(childPattern, text);
    }

    /**
     * 通过两个条件：selector和instance属性，定位到其下符合条件的子控件
     *
     * @param collection
     * @param childPattern
     * @param index
     * @return
     * @throws UiObjectNotFoundException
     */
    public static UiObject getChildByInstance  (UiCollection collection, UiSelector childPattern, int index) throws UiObjectNotFoundException {
        return collection.getChildByInstance (childPattern, index);
    }

    /**
     * 按照UiSelector查找条件递归查找所有符合条件的子子孙孙集合的的数量
     *
     * @param collection
     * @param childPattern
     * @return
     */
    public static int getChildCount (UiCollection collection, UiSelector childPattern)
    {
        return collection.getChildCount(childPattern);
    }

}