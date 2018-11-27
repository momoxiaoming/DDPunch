package com.al.autoleve.core.automator.api;

import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

/**
 * 1、UiScrollable是UiCollection的子类。
 * 2、UiScrollable专门处理滚动时间，提供各种滚动方法。
 * 常用功能有：向前滚动、向后滚动、快速滚动、滚动到某个对象、设置滚动方向、设置滚动次数等。
 */
public class UiScrollableApi {

    public static UiScrollable getUiScrollable(UiSelector selector) {
        return new UiScrollable(selector);
    }

    /**
     * 以步长为5快速向后滑动
     *
     * @param scrollable
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean flingBackward(UiScrollable scrollable) throws UiObjectNotFoundException {
        return scrollable.flingBackward();
    }

    /**
     * 以步长为5快速向前滑动、
     *
     * @param scrollable
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean flingForward(UiScrollable scrollable) throws UiObjectNotFoundException {
        return scrollable.flingForward();
    }

    /**
     * 自定义扫动次数以步长为5快速滑动到开启
     * 扫动次数：触发滚动的次数。
     *
     * @param scrollable
     * @param maxSwipes
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean flingToBeginning(UiScrollable scrollable, int maxSwipes) throws UiObjectNotFoundException {
        return scrollable.flingToBeginning(maxSwipes);
    }

    /**
     * 自定义扫动次数以步长为5快速滑动到结束
     * 扫动次数：触发滚动的次数。
     *
     * @param scrollable
     * @param maxSwipes
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean flingToEnd(UiScrollable scrollable, int maxSwipes) throws UiObjectNotFoundException {
        return scrollable.flingToEnd(maxSwipes);
    }

    /**
     * 是否允许滚动查找获取具备ＵiＳelector条件元素集合后再以文本描述条件查找对象
     *
     * @param scrollable
     * @param childPattern
     * @param desc
     * @param allowScrollSearch
     * @return
     * @throws UiObjectNotFoundException
     */
    public static UiObject getChildByDescription(UiScrollable scrollable, UiSelector childPattern, String desc, boolean allowScrollSearch) throws UiObjectNotFoundException {
        return scrollable.getChildByDescription(childPattern, desc, allowScrollSearch);
    }

    /**
     * 默认滚动获取具备ＵiＳelector条件的元素集合后再以文本描述条件查找对象
     *
     * @param scrollable
     * @param childPattern
     * @param desc
     * @return
     * @throws UiObjectNotFoundException
     */
    public static UiObject getChildByDescription(UiScrollable scrollable, UiSelector childPattern, String desc) throws UiObjectNotFoundException {
        return scrollable.getChildByDescription(childPattern, desc);
    }

    /**
     * 获取具备ＵiＳelector条件的子集，再从子集中按照实例筛选想要的元素（不滚动）
     *
     * @param scrollable
     * @param childPattern
     * @param index
     * @return
     * @throws UiObjectNotFoundException
     */
    public static UiObject getChildByInstance(UiScrollable scrollable, UiSelector childPattern, int index)
            throws UiObjectNotFoundException {
        return scrollable.getChildByInstance(childPattern, index);
    }

    /**
     * 是否允许滚动获取具备ＵiＳelector条件的元素集合后再以文本条件查找对象
     *
     * @param scrollable
     * @param childPattern
     * @param text
     * @param allowScrollSearch
     * @return
     * @throws UiObjectNotFoundException
     */
    public static UiObject getChildByText(UiScrollable scrollable, UiSelector childPattern, String text, boolean allowScrollSearch) throws UiObjectNotFoundException {
        return scrollable.getChildByText(childPattern, text, allowScrollSearch);
    }

    /**
     * 默认滚动获取具备ＵiＳelector条件元素集合后再以文本条件的查找对象
     *
     * @param scrollable
     * @param childPattern
     * @param text
     * @return
     * @throws UiObjectNotFoundException
     */
    public static UiObject getChildByText(UiScrollable scrollable, UiSelector childPattern, String text) throws UiObjectNotFoundException {
        return scrollable.getChildByText(childPattern, text);
    }

    /**
     * 获取执行搜索滑动过程中的最大滑动次数，默认常量为30
     *
     * @param scrollable
     * @return
     */
    public static int getMaxSearchSwipes(UiScrollable scrollable) {
        return scrollable.getMaxSearchSwipes();
    }

    /**
     * 为UiScrollable设置最大可扫动次数
     *
     * @param scrollable
     * @param swipes
     * @return
     */
    public static UiScrollable setMaxSearchSwipes(UiScrollable scrollable, int swipes) {
        return scrollable.setMaxSearchSwipes(swipes);
    }

    /**
     * 自定义步长向后滑动
     *
     * @param scrollable
     * @param steps
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean scrollBackward(UiScrollable scrollable, int steps) throws UiObjectNotFoundException {
        return scrollable.scrollBackward(steps);
    }

    /**
     * 以默认步长55向后滑动
     *
     * @param scrollable
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean scrollBackward(UiScrollable scrollable) throws UiObjectNotFoundException {
        return scrollable.scrollBackward();
    }

    /**
     * 自定义步长向前滑动
     *
     * @param scrollable
     * @param steps
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean scrollForward(UiScrollable scrollable, int steps) throws UiObjectNotFoundException {
        return scrollable.scrollForward(steps);
    }

    /**
     * 以默认步长55向前滑动
     *
     * @param scrollable
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean scrollForward(UiScrollable scrollable) throws UiObjectNotFoundException {
        return scrollable.scrollForward();
    }

    /**
     * 滚动到描述所在位置，并且尽量让它居于屏幕中央
     *
     * @param scrollable
     * @param text
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean scrollDescriptionIntoView(UiScrollable scrollable, String text) throws UiObjectNotFoundException {
        return scrollable.scrollDescriptionIntoView(text);
    }

    /**
     * 滚动到条件元素所在位置，并且尽量让其居于屏幕中央
     *
     * @param scrollable
     * @param selector
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean scrollIntoView(UiScrollable scrollable, UiSelector selector) throws UiObjectNotFoundException {
        return scrollable.scrollIntoView(selector);
    }

    /**
     * 滚动到对象所在位置，并且尽量让其居于屏幕中央
     *
     * @param scrollable
     * @param obj
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean scrollIntoView(UiScrollable scrollable, UiObject obj) throws UiObjectNotFoundException {
        return scrollable.scrollIntoView(obj);
    }

    /**
     * 滚动到文本对象所在位置，并且尽量让其居于屏幕中央
     *
     * @param scrollable
     * @param text
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean scrollTextIntoView(UiScrollable scrollable, String text) throws UiObjectNotFoundException {
        return scrollable.scrollTextIntoView(text);
    }

    /**
     * 自定义最大滚动次数，滚动到开始位置
     *
     * @param scrollable
     * @param maxSwipes
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean scrollToBeginning(UiScrollable scrollable, int maxSwipes) throws UiObjectNotFoundException {
        return scrollable.scrollToBeginning(maxSwipes);
    }

    /**
     * 自定义最大滚动次数与步长，滚动到开始位置
     *
     * @param scrollable
     * @param maxSwipes
     * @param steps
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean scrollToBeginning(UiScrollable scrollable, int maxSwipes, int steps) throws UiObjectNotFoundException {
        return scrollable.scrollToBeginning(maxSwipes, steps);
    }

    /**
     * 自定义最大滚动次数，滚动到结束位置
     *
     * @param scrollable
     * @param maxSwipes
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean scrollToEnd(UiScrollable scrollable, int maxSwipes) throws UiObjectNotFoundException {
        return scrollable.scrollToEnd(maxSwipes);
    }

    /**
     * 自定义最大滚动次数与步长，滚动到结束位置
     *
     * @param scrollable
     * @param maxSwipes
     * @param steps
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean scrollToEnd(UiScrollable scrollable, int maxSwipes, int steps) throws UiObjectNotFoundException {
        return scrollable.scrollToEnd(maxSwipes, steps);
    }

    /**
     * 设置滚动方向设置为水平滚动
     *
     * @param scrollable
     * @return
     */
    public static UiScrollable setAsHorizontalList(UiScrollable scrollable)
    {
        return scrollable.setAsHorizontalList();
    }

    /**
     * 设置滚动方向设置为纵向滚动
     *
     * @param scrollable
     * @return
     */
    public static UiScrollable setAsVerticalList(UiScrollable scrollable)
    {
        return scrollable.setAsVerticalList();
    }

}