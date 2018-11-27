package com.al.autoleve.core.automator.api;

import android.graphics.Rect;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

/**
 * UiObject用来代表一个组件对象，它提供一系列方法和属性来模拟在手机上的实际操作。
 */
public class UiObjectApi
{
    /**
     *获得对象的子类对象，可以递归获取子孙当中某个对象
     *
     * @param object 对象
     * @param selector 子类对象的selector
     * @return
     */
    public static UiObject getChild(UiObject object, UiSelector selector) throws UiObjectNotFoundException {
        return object.getChild(selector);
    }

    /**
     * 从父类获取子类，按照UiSeletor获取兄弟类（递归）
     *
     * @param object 对象
     * @param selector for a sibling view or children of the sibling view
     * @return
     */
    public static UiObject getFromParent(UiObject object, UiSelector selector) throws UiObjectNotFoundException {
        return object.getFromParent(selector);
    }

    /**
     * 拖拽对象到另一个对象位置上，步长可设置拖动的速度
     *
     * @param object 对象
     * @param destObj 目标对象
     * @param steps
     * @return
     */
    public static boolean dragTo(UiObject object, UiObject destObj, int steps) throws UiObjectNotFoundException {
        return object.dragTo(destObj,steps);
    }

    /**
     * 拖拽对象到屏幕某个坐标位置上，步长可设置拖动速度
     *
     * @param object 对象
     * @param destX 目标x轴位置
     * @param destY 目标y轴位置
     * @param steps
     * @return
     */
    public static boolean dragTo(UiObject object, int destX, int destY, int steps) throws UiObjectNotFoundException {
        return object.dragTo(destX, destY, steps);
    }

    /**
     * 拖动对象往上滑动
     *
     * @param object 对象
     * @param steps
     * @return
     */
    public static boolean swipeUp(UiObject object, int steps) throws UiObjectNotFoundException {
        return object.swipeUp(steps);
    }

    /**
     * 拖动对象往下滑动
     *
     * @param object 对象
     * @param steps
     * @return
     */
    public static boolean swipeDown(UiObject object, int steps) throws UiObjectNotFoundException {
        return object.swipeDown(steps);
    }

    /**
     * 拖动对象往左滑动
     *
     * @param object 对象
     * @param steps
     * @return
     */
    public static boolean swipeLeft(UiObject object, int steps) throws UiObjectNotFoundException {
        return object.swipeLeft(steps);
    }

    /**
     * 拖动对象往右滑动
     *
     * @param object 对象
     * @param steps
     * @return
     */
    public static boolean swipeRight(UiObject object, int steps) throws UiObjectNotFoundException {
        return object.swipeRight(steps);
    }

    /**
     * 点击对象
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean click(UiObject object) throws UiObjectNotFoundException
    {
        return object.click();
    }

    /**
     * 点击对象，等待新窗口出现
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean clickAndWaitForNewWindow(UiObject object) throws UiObjectNotFoundException
    {
        return object.clickAndWaitForNewWindow();
    }

    /**
     * 点击对象，等待新窗口出现
     *
     * @param object
     * @param timeout 等待超时时长
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean clickAndWaitForNewWindow(UiObject object, long timeout) throws UiObjectNotFoundException
    {
        return object.clickAndWaitForNewWindow(timeout);
    }

    /**
     * 点击对象的左上角
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean clickTopLeft(UiObject object) throws UiObjectNotFoundException {
        return object.clickBottomRight();
    }

    /**
     * 点击对象的右下角
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean clickBottomRight(UiObject object) throws UiObjectNotFoundException {
        return object.clickBottomRight();
    }

    /**
     * 长按对象，对对象执行长按操作
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean longClick(UiObject object) throws UiObjectNotFoundException {
        return object.longClick();
    }

    /**
     * 长按对象的左上角
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean longClickTopLeft(UiObject object) throws UiObjectNotFoundException {
        return object.longClickTopLeft();
    }

    /**
     * 长按对象的右下角
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean longClickBottomRight(UiObject object) throws UiObjectNotFoundException {
        return object.longClickBottomRight();
    }

    /**
     * 在对象中输入文本（实现方式：自动先清除文本再输入）
     *
     * @param object
     * @param text
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean setText(UiObject object, String text) throws UiObjectNotFoundException {
        return object.setText(text);
    }

    /**
     * 在对象中输入文本（实现方式：自动先清除文本再输入）
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static void clearTextField(UiObject object) throws UiObjectNotFoundException {
        object.clearTextField();
    }

    /**
     * 等待对象出现
     *
     * @param object
     * @param timeout
     * @return
     */
    public static boolean waitForExists(UiObject object, long timeout)
    {
        return object.waitForExists(timeout);
    }

    /**
     * 等待对象消失
     *
     * @param object
     * @param timeout
     * @return
     */
    public static boolean waitUntilGone(UiObject object, long timeout)
    {
        return object.waitForExists(timeout);
    }

    /**
     * 检查对象是否存在
     *
     * @param object
     * @return
     */
    public static boolean exists(UiObject object)
    {
        return object.exists();
    }

    /**
     * 获得对象矩形坐标，矩形左上角坐标与右下角坐标
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Rect getBounds(UiObject object) throws UiObjectNotFoundException
    {
        return object.getBounds();
    }

    /**
     * 获得下一级子类数量
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static int getChildCount(UiObject object) throws UiObjectNotFoundException {
        return object.getChildCount();
    }

    /**
     * 获得对象类名属性的类名文本
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static String getClassName(UiObject object) throws UiObjectNotFoundException {
        return object.getClassName();
    }

    /**
     * 获得对象的描述属性的描述文本
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static String getContentDescription(UiObject object) throws UiObjectNotFoundException {
        return object.getContentDescription();
    }

    /**
     * 获得对象包名属性的包名文本
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static String getPackageName(UiObject object) throws UiObjectNotFoundException {
        return object.getPackageName();
    }

    /**
     * 获得对象的文本属性中的文本
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static String getText(UiObject object) throws UiObjectNotFoundException {
        return object.getText();
    }

    /**
     * 返回可见视图的范围，如果视图的部分是可见的，只有可见部分报告的范围
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Rect getVisibleBounds(UiObject object) throws UiObjectNotFoundException {
        return object.getVisibleBounds();
    }

    /**
     * 检查对象的checkable属性是否为true
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean isCheckable(UiObject object) throws UiObjectNotFoundException {
        return object.isCheckable();
    }

    /**
     * 检查对象的checked属性是否为true
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean isChecked(UiObject object) throws UiObjectNotFoundException {
        return object.isChecked();
    }

    /**
     * 检查对象的Clickable属性是否为true
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean isClickable(UiObject object) throws UiObjectNotFoundException {
        return object.isClickable();
    }

    /**
     * 检查对象的Enabled属性是否为true
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean isEnabled(UiObject object) throws UiObjectNotFoundException {
        return object.isEnabled();
    }

    /**
     * 检查对象的Focusable属性是否为true
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean isFocusable(UiObject object) throws UiObjectNotFoundException {
        return object.isFocusable();
    }

    /**
     * 检查对象的Focused属性是否为true
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean isFocused(UiObject object) throws UiObjectNotFoundException {
        return object.isFocused();
    }

    /**
     * 检查对象的LongClickable属性是否为true
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean isLongClickable(UiObject object) throws UiObjectNotFoundException {
        return object.isLongClickable();
    }

    /**
     * 检查对象的Scrollable属性是否为true
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean isScrollable(UiObject object) throws UiObjectNotFoundException {
        return object.isScrollable();
    }

    /**
     * 检查对象的Selected属性是否为true
     *
     * @param object
     * @return
     * @throws UiObjectNotFoundException
     */
    public static boolean isSelected(UiObject object) throws UiObjectNotFoundException {
        return object.isSelected();
    }


}