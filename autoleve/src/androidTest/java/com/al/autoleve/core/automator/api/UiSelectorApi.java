package com.al.autoleve.core.automator.api;

import android.support.test.uiautomator.UiSelector;

import com.andr.tool.log.LogUtil;

/**
 * UiSelector可通过控件的各种属性与节点关系定位组件。
 */
public class UiSelectorApi
{
    /**
     * 查找文本为text的所有组件
     *
     * @param text 查找文本
     * @return
     */
    public static UiSelector text(String text)
    {
        return new UiSelector().text(text);
    }

    /**
     * 查找文本包含text的所有组件
     *
     * @param text 文本包含
     * @return
     */
    public static UiSelector textContains(String text)
    {
        return new UiSelector().textContains(text);
    }

    /**
     * 查找文本正则regex的所有组件
     *
     * @param regex 文本正则
     * @return
     */
    public static UiSelector textMatches(String regex)
    {
        return new UiSelector().textMatches(regex);
    }

    /**
     * 查找文本起始匹配text的所有组件
     *
     * @param text 文本起始匹配
     * @return
     */
    public static UiSelector textStartsWith(String text)
    {
        return new UiSelector().textStartsWith(text);
    }

    /**
     * 查找类名为className的所有组件
     *
     * @param className 类名
     * @return
     */
    public static UiSelector className(String className)
    {
        return new UiSelector().className(className);
    }

    /**
     * 查找正则类名为className的所有组件
     *
     * @param regex 正则类名
     * @return
     */
    public static UiSelector classNameMatches(String regex)
    {
        return new UiSelector().classNameMatches(regex);
    }

    /**
     * Set the search criteria to match the class property
     * for a widget (for example, "android.widget.Button").
     *
     * @param type type
     * @return UiSelector with the specified search criteria
     * @since API Level 17
     */
    public static  <T> UiSelector className(Class<T> type)
    {
        return new UiSelector().className(type);
    }

    /**
     * 查找描述为desc的所有组件
     *
     * @param desc 查找描述
     * @return
     */
    public static UiSelector description(String desc)
    {
        return new UiSelector().description(desc);
    }

    /**
     * 查找描述包含desc的所有组件
     *
     * @param desc 描述包含
     * @return
     */
    public static UiSelector descriptionContains(String desc)
    {
        return new UiSelector().descriptionContains(desc);
    }

    /**
     * 查找描述正则regex的所有组件
     *
     * @param regex 描述正则
     * @return
     */
    public static UiSelector descriptionMatches(String regex)
    {
        return new UiSelector().descriptionMatches(regex);
    }

    /**
     * 查找描述开始字符匹配desc的所有组件
     *
     * @param desc 文描述开始字符匹配
     * @return
     */
    public static UiSelector descriptionStartsWith(String desc)
    {
        return new UiSelector().textStartsWith(desc);
    }

    /**
     * 查找资源ID为id的所有组件
     *
     * @param id 资源ID
     * @return
     */
    public static UiSelector resourceId(String id)
    {
        return new UiSelector().resourceId(id);
    }

    /**
     * 查找资源ID正则id的所有组件
     *
     * @param regex 资源ID正则
     * @return
     */
    public static UiSelector resourceIdMatches(String regex)
    {
        return new UiSelector().resourceIdMatches(regex);
    }

    /**
     * 查找在同级中下标为index的组件
     *
     * @param index 索引：同级组件的下标；从0开始计
     * @return
     */
    public static UiSelector index(int index)
    {
        return new UiSelector().index(index);
    }

    /**
     * 查找界面中第index个组件
     *
     * @param instance 实例：界面中同一类View的所有实例的下标；从0开始计
     * @return
     */
    public static UiSelector instance(int instance)
    {
        return new UiSelector().instance(instance);
    }

    /**
     * 查找属性为val的组件
     *
     * @param val 可操作属性
     * @return
     */
    public static UiSelector enabled(boolean val)
    {
        return new UiSelector().enabled(val);
    }

    /**
     * 查找属性为val的组件
     *
     * @param val 当前焦点属性
     * @return
     */
    public static UiSelector focused(boolean val)
    {
        return new UiSelector().focused(val);
    }


    /**
     * 查找属性为val的组件
     *
     * @param val 焦点属性
     * @return
     */
    public static UiSelector focusable(boolean val)
    {
        return new UiSelector().focusable(val);
    }

    /**
     * 查找属性为val的组件
     *
     * @param val 滚动属性
     * @return
     */
    public static UiSelector scrollable(boolean val)
    {
        return new UiSelector().scrollable(val);
    }

    /**
     * 查找属性为val的组件
     *
     * @param val 背景选择属性
     * @return
     */
    public static UiSelector selected(boolean val)
    {
        return new UiSelector().selected(val);
    }

    /**
     * 查找属性为val的组件
     *
     * @param val 选择属性
     * @return
     */
    public static UiSelector checked(boolean val)
    {
        return new UiSelector().checked(val);
    }
    /**
     * Set the search criteria to match widgets that are checkable.
     *
     * @param val Value to match
     * @return
     */
    public static UiSelector checkable(boolean val)
    {
        return new UiSelector().checkable(val);
    }

    /**
     * 查找属性为val的组件
     *
     * @param val 可点击属性
     * @return
     */
    public static UiSelector clickable(boolean val)
    {
        return new UiSelector().clickable(val);
    }

    /**
     * 查找属性为val的组件
     *
     * @param val 长按属性
     * @return
     */
    public static UiSelector longClickable(boolean val)
    {
        return new UiSelector().longClickable(val);
    }

    /**
     * 从当前组件往下查找其子子孙孙中符合条件的所有组件
     *
     * @param selector
     * @return
     */
    public static UiSelector childSelector(UiSelector selector)
    {
        return new UiSelector().childSelector(selector);
    }

    /**
     * 从当前组件往上查找其长辈中符合条件的所有组件
     *
     * @param selector
     * @return
     */
    public static UiSelector fromParent(UiSelector selector)
    {
        return new UiSelector().fromParent(selector);
    }

    /**
     * 查找包名为name的所有组件
     *
     * @param name 包名
     * @return
     */
    public static UiSelector packageName(String name)
    {
        return new UiSelector().packageName(name);
    }

    /**
     * 查找正则包名为name的所有组件
     *
     * @param regex 正则包名
     * @return
     */
    public static UiSelector packageNameMatches(String regex)
    {
        return new UiSelector().packageNameMatches(regex);
    }

}