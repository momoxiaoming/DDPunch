package com.al.autoleve.core.automator.api;

import android.app.UiAutomation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.Tracer;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.UiWatcher;
import android.support.test.uiautomator.Until;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.al.autoleve.core.data.UiDataCenter;
import com.andr.tool.apk.ApkUtil;
import com.andr.tool.log.LogUtil;
import com.andr.tool.phone.PhoneUtil;
import com.andr.tool.thread.ThreadUtil;
import com.andr.tool.util.ReflectUtil;
import com.andr.tool.util.StringUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/7/24/024.
 */
public class AutomatorApi {
    /**
     * UiDevice provides access to state information about the device.
     * You can also use this class to simulate user actions on the device,
     * such as pressing the d-pad or pressing the Home and Menu buttons.
     */
    public static UiDevice uiDevice = null;
    public static Context context = null;
    public static UiAutomation uiAutomation = null;


    //--------------------------
    public static void backToDeskTop() throws IOException {
        int sysVer = PhoneUtil.getInstance().getSysSdkVer();
        if (sysVer > 19) {
            backToDeskTopMoreThanSdk19(getLauncherPackageName());
        } else {
            backToDeskTopLessThanSdk19();
        }
    }

    //获取手机桌面启动页包名
    public String getLauncherPackageName(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res.activityInfo == null) {
            return "";
        }
        //如果是不同桌面主题，可能会出现某些问题，这部分暂未处理
        if (res.activityInfo.packageName.equals("android")) {
            return "";
        } else {
            return res.activityInfo.packageName;
        }
    }

    public static void backToDeskTopMoreThanSdk19(String deskTopActivityName) throws IOException {
        boolean isBackSuc = false;
        for (int i = 0; i < 15; i++) {
            String activityTaskInfo = AutomatorApi.executeShellCommand("dumpsys activity top");
            if (StringUtil.isValidate(activityTaskInfo)) {
                String[] activityInfos = activityTaskInfo.split("\n");
                if (null != activityInfos && StringUtil.isValidate(activityInfos[1])) {
                    boolean rlt = activityInfos[1].contains(deskTopActivityName);
                    if (!rlt) {
                        LogUtil.d("还未返回桌面执行回退...");
                        AutomatorApi.pressBack();
                        continue;
                    } else {
                        LogUtil.d("已经回退到桌面...");
                        isBackSuc = true;
                        break;
                    }
                }
            }
        }
        //如果没有回退成功，按home键退回到桌面
        if (!isBackSuc) {
            AutomatorApi.pressHome();
        }
    }
    //-------------------------

    public static void backToDeskTopLessThanSdk19() throws IOException {
        boolean isBackSuc = false;
        for (int i = 0; i < 15; i++) {
            boolean rlt = PhoneUtil.getInstance().isHome(UiDataCenter.getInstance().getMainApkContext());
            if (!rlt) {
                LogUtil.d("还未返回桌面执行回退...");
                AutomatorApi.pressBack();
                continue;
            } else {
                LogUtil.d("已经回退到桌面...");
                isBackSuc = true;
                break;
            }
        }
        //如果没有回退成功，按home键退回到桌面
        if (!isBackSuc) {
            AutomatorApi.pressHome();
        }
    }

    public static boolean startApp(String pkgName) {
        return ApkUtil.getInstance().startApk(UiDataCenter.getInstance().getMainApkContext(), pkgName);
    }

    public static void startApp(String pkgName, String activityName) throws IOException {
        AutomatorApi.executeShellCommand("am start -n " + pkgName + "/" + activityName);
    }

    public static void stopApp(String pkgName) throws IOException {
        AutomatorApi.executeShellCommand("am force-stop " + pkgName);
    }

    /**
     * Set init UiDevice
     *
     * @param dev
     */
    public static void setUiDevice(UiDevice dev) {
        uiDevice = dev;
    }

    /**
     * @param con
     */
    public static void setContext(Context con) {
        context = con;
    }

    /**
     * @param uia
     */
    public static void setUiAutomation(UiAutomation uia) {
        uiAutomation = uia;
    }

    //=================================== UiDevice ============================================

    /**
     * Returns a UiObject which represents a view that matches the specified selector criteria.
     *
     * @param selector
     * @return
     */
    public static UiObject findObject(UiSelector selector) {
        if (uiDevice == null)
            return null;
        return uiDevice.findObject(selector);
    }

    public static UiObject findObject(UiSelector selector, int index) {
        if (uiDevice == null)
            return null;
        return uiDevice.findObject(selector.instance(index));
    }

    /**
     * Returns the first object to match the {@code selector} criteria.
     */
    public static UiObject2 findObject(BySelector selector) {
        if (uiDevice == null)
            return null;
        return uiDevice.findObject(selector);
    }

    /**
     * Returns all objects that match the {@code selector} criteria.
     */
    public static List<UiObject2> findObjects(BySelector selector) {
        if (uiDevice == null)
            return null;
        return uiDevice.findObjects(selector);
    }

    /**
     * Simulates a short press on the MENU button.
     *
     * @return true if successful, else return false
     * @since API Level 16
     */
    public static Boolean pressMenu() {
        if (uiDevice == null)
            return false;
        return uiDevice.pressMenu();
    }

    /**
     * Simulates a short press on the BACK button.
     *
     * @return true if successful, else return false
     * @since API Level 16
     */
    public static Boolean pressBack() {
        if (uiDevice == null)
            return false;
        return uiDevice.pressBack();
    }

    /**
     * Simulates a short press on the HOME button.
     *
     * @return true if successful, else return false
     * @since API Level 16
     */
    public static Boolean pressHome() {
        if (uiDevice == null)
            return false;
        return uiDevice.pressHome();
    }

    /**
     * Simulates a short press on the ENTER key.
     *
     * @return true if successful, else return false
     * @since API Level 16
     */
    public static Boolean pressEnter() {
        if (uiDevice == null)
            return false;
        return uiDevice.pressEnter();
    }

    /**
     * 短按删除delete按键
     *
     * @return
     */
    public static Boolean pressDelete() {
        if (uiDevice == null)
            return false;
        return uiDevice.pressDelete();
    }

    /**
     * 短按键盘代码keycode
     *
     * @param keyCode   the key code of the event.
     * @param metaState an integer in which each bit set to 1 represents a pressed meta key
     * @return
     */
    public static Boolean pressKeyCode(int keyCode, int metaState) {
        if (uiDevice == null)
            return false;
        return uiDevice.pressKeyCode(keyCode, metaState);
    }

    /**
     * Simulates a short press using a key code.
     * <p/>
     * See {@link KeyEvent}
     *
     * @return true if successful, else return false
     * @since API Level 16
     */
    public static Boolean pressKeyCode(int keyCode) {
        if (uiDevice == null)
            return false;
        return uiDevice.pressKeyCode(keyCode);
    }

    /**
     * 短按最近使用程序
     *
     * @return
     * @throws RemoteException
     */
    public static Boolean pressRecentApps() throws RemoteException {
        if (uiDevice == null)
            return false;
        return uiDevice.pressRecentApps();
    }

    /**
     * 短按搜索键
     *
     * @return
     */
    public static Boolean pressSearch() {
        if (uiDevice == null)
            return false;
        return uiDevice.pressSearch();
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getDisplayHeight() {
        if (uiDevice == null)
            return -1;
        return uiDevice.getDisplayHeight();
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getDisplayWidth() {
        if (uiDevice == null)
            return -1;
        return uiDevice.getDisplayWidth();
    }

    /**
     * Returns the display size in dp (device-independent pixel)
     *
     * @return
     */
    public static Point getDisplaySizeDp() {
        if (uiDevice == null)
            return null;
        return uiDevice.getDisplaySizeDp();
    }

    /**
     * Opens the notification shade.
     *
     * @return true if successful, else return false
     * @since API Level 18
     */
    public static Boolean openNotification() {
        if (uiDevice == null)
            return false;
        return uiDevice.openNotification();
    }

    /**
     * Opens the Quick Settings shade.
     *
     * @return true if successful, else return false
     * @since API Level 18
     */
    public static Boolean openQuickSettings() {
        if (uiDevice == null)
            return false;
        return uiDevice.openQuickSettings();
    }

    /**
     * Perform a click at arbitrary coordinates specified by the user
     *
     * @param x coordinate
     * @param y coordinate
     * @return true if the click succeeded else false
     * @since API Level 16
     */
    public static Boolean click(int x, int y) {
        if (uiDevice == null)
            return false;
        return uiDevice.click(x, y);
    }

    /**
     * Performs a swipe from one coordinate to another using the number of steps
     * to determine smoothness and speed. Each step execution is throttled to 5ms
     * per step. So for a 100 steps, the swipe will take about 1/2 second to complete.
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param steps  is the number of move steps sent to the system
     * @return false if the operation fails or the coordinates are invalid
     * @since API Level 16
     */
    public static Boolean swipe(int startX, int startY, int endX, int endY, int steps) {
        if (uiDevice == null)
            return false;
        return uiDevice.swipe(startX, startY, endX, endY, steps);
    }

    /**
     * 在点阵列中滑动，5ms一步
     *
     * @param segments
     * @param segmentSteps
     * @return
     */
    public static Boolean swipe(Point[] segments, int segmentSteps) {
        if (uiDevice == null)
            return false;
        return uiDevice.swipe(segments, segmentSteps);
    }

    /**
     * Performs a swipe from one coordinate to another coordinate. You can control
     * the smoothness and speed of the swipe by specifying the number of steps.
     * Each step execution is throttled to 5 milliseconds per step, so for a 100
     * steps, the swipe will take around 0.5 seconds to complete.
     *
     * @param startX X-axis value for the starting coordinate
     * @param startY Y-axis value for the starting coordinate
     * @param endX   X-axis value for the ending coordinate
     * @param endY   Y-axis value for the ending coordinate
     * @param steps  is the number of steps for the swipe action
     * @return true if swipe is performed, false if the operation fails
     * or the coordinates are invalid
     * @since API Level 18
     */
    public static Boolean drag(int startX, int startY, int endX, int endY, int steps) {
        if (uiDevice == null)
            return false;
        return uiDevice.drag(startX, startY, endX, endY, steps);
    }

    /**
     * Retrieves the last activity to report accessibility events.
     *
     * @return String name of activity
     * @since API Level 16
     */
    public static String getCurrentActivityName() {
        if (uiDevice == null)
            return null;
        return uiDevice.getCurrentActivityName();
    }

    /**
     * Retrieves the name of the last package to report accessibility events.
     *
     * @return String name of package
     * @since API Level 16
     */
    public static String getCurrentPackageName() {
        if (uiDevice == null)
            return null;
        return uiDevice.getCurrentPackageName();
    }

    /**
     * Helper method used for debugging to dump the current window's layout hierarchy.
     * Relative file paths are stored the application's internal private storage location.
     *
     * @param fileName
     * @since API Level 16
     */
    public static Boolean dumpWindowHierarchy(String fileName) {
        if (uiDevice == null)
            return false;
        uiDevice.dumpWindowHierarchy(fileName);
        return true;
    }

    /**
     * Take a screenshot of current window and store it as PNG
     * <p/>
     * The screenshot is adjusted per screen rotation
     *
     * @param storePath where the PNG should be written to
     * @return true if screen shot is created successfully, false otherwise
     * @since API Level 17
     */
    public static Boolean takeScreenshot(String storePath) {
        if (uiDevice == null)
            return false;
        return uiDevice.takeScreenshot(new File(storePath));
    }

    /**
     * 把当前窗口截图为png格式图片，可以自定义缩放比例与图片质量。
     *
     * @param storePath 保存路径
     * @param scale
     * @param quality
     * @return
     */
    public static boolean takeScreenshot(String storePath, float scale, int quality) {
        if (uiDevice == null)
            return false;
        return uiDevice.takeScreenshot(new File(storePath), scale, quality);
    }

    /**
     * Retrieves default launcher package name
     *
     * @return package name of the default launcher
     */
    public static String getLauncherPackageName() {
        if (uiDevice == null)
            return null;
        return uiDevice.getLauncherPackageName();
    }

    /**
     * 按电源键，如果屏幕是唤醒的没有任何作用
     *
     * @throws RemoteException
     * @since API Level 16
     */
    public static Boolean wakeUp() throws RemoteException {
        if (uiDevice == null)
            return false;
        uiDevice.wakeUp();
        return true;
    }

    /**
     * 按电源键，如果屏幕已经是关闭的则没有任何作用
     *
     * @return true if successful, else return false
     * @throws RemoteException
     */
    public static Boolean sleep() throws RemoteException {
        if (uiDevice == null)
            return false;
        uiDevice.sleep();
        return true;
    }

    /**
     * 检查屏幕是否亮屏
     *
     * @return
     * @throws RemoteException
     */
    public static boolean isScreenOn() throws RemoteException {
        if (uiDevice == null)
            return false;
        return uiDevice.isScreenOn();
    }

    /**
     * 自定义超时等待当前应用处于空闲状态
     *
     * @param timeout
     * @return
     */
    public static Boolean waitForIdle(long timeout) {
        if (uiDevice == null)
            return false;
        uiDevice.waitForIdle(timeout);
        return true;
    }

    /**
     * 等待当前应用处于空闲状态，默认等待10s；
     * 即10s后还不处于空闲状态则报错，程序在该句代码处中断；
     * 10s内程序处于空闲状态，则该句代码执行完
     *
     * @return
     */
    public static Boolean waitForIdle() {
        if (uiDevice == null)
            return false;
        uiDevice.waitForIdle();
        return true;
    }

    /**
     * 等待窗口内容更新事件的发生
     *
     * @param packageName
     * @param timeout
     * @return
     */
    public static Boolean waitForWindowUpdate(String packageName, long timeout) {
        if (uiDevice == null)
            return false;
        uiDevice.waitForWindowUpdate(packageName, timeout);
        return true;
    }

    //============================== UiObject 常用方法 =============================================

    public static boolean clickFirstByTextEqual(String text) {
        List<UiObject2> list = findObjects(By.text(text));
        if (null != list && list.size() > 0) {
            UiObject2 obj2 = list.get(0);
            obj2.click();
            return true;
        }
        return false;
    }

    public static boolean clickLastByTextEqual(String text) {
        List<UiObject2> list = findObjects(By.text(text));
        if (null != list && list.size() > 0) {
            UiObject2 obj2 = list.get(list.size() - 1);
            obj2.click();
            return true;
        }
        return false;
    }

    /**
     * 通过包含文本内容找到组件并点击
     *
     * @param text  包含文本内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByTextContain(String text, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = findObject(UiSelectorApi.textContains(text).instance(index));
        object.click();
        return true;
    }

    /**
     * 通过等值文本内容找到组件并点击
     *
     * @param text  等值文本内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByTextEqual(String text, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = findObject(UiSelectorApi.text(text).instance(index));
        object.click();
        return true;
    }

    /**
     * 通过包含描述内容找到组件并点击
     *
     * @param dec   包含描述内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByDescContain(String dec, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = findObject(UiSelectorApi.descriptionContains(dec).instance(index));
        object.click();
        return true;
    }

    /**
     * 通过等值描述内容找到组件并点击
     *
     * @param dec   等值描述内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByDescEqual(String dec, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = findObject(UiSelectorApi.description(dec).instance(index));
        object.click();
        return true;
    }

    /**
     * 通过类名找到组件找到组件并点击， 类名可通过DDMS获取
     *
     * @param cls   类名
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByClass(String cls, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = findObject(UiSelectorApi.className(cls).instance(index));
        object.click();
        return true;
    }

    /**
     * 通过资源ID找到组件找到组件并点击， 资源ID可通过DDMS获取
     *
     * @param resId 资源ID
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByResId(String resId, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = findObject(UiSelectorApi.resourceId(resId).instance(index));
        object.click();
        return true;
    }


    /**
     * 通过资源ID找到组件找到组件并获取文本内容， 资源ID可通过DDMS获取
     *
     * @param resId 资源ID
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return 获取到的文本内容
     * @throws UiObjectNotFoundException
     */
    public static String getTextByResId(String resId, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return "";
        UiObject object = findObject(UiSelectorApi.resourceId(resId).instance(index));
        return object.getText();
    }

    /**
     * 通过类名找到组件并获取文本内容，类名可通过DDMS获取
     *
     * @param cls   类名
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return 获取到的文本内容
     * @throws UiObjectNotFoundException
     */
    public static String getTextByClass(String cls, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return "";
        UiObject object = findObject(UiSelectorApi.className(cls).instance(index));
        return object.getText();
    }

    /**
     * 通过包含文本内容匹配找到组件并获取文本内容
     *
     * @param str   包含的文本内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return 获取到的文本内容
     * @throws UiObjectNotFoundException
     */
    public static String getTextByTextContain(String str, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return "";
        UiObject object = findObject(UiSelectorApi.textContains(str).instance(index));
        return object.getText();
    }

    /**
     * 通过包含文本内容匹配找到组件并获取文本内容
     *
     * @param str   包含的文本内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return 获取到的文本内容F
     * @throws UiObjectNotFoundException
     */
    public static String getTextByTextEqual(String str, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return "";
        UiObject object = findObject(UiSelectorApi.text(str).instance(index));
        return object.getText();
    }

    /**
     * 通过包含描述内容匹配找到组件并获取文本内容
     *
     * @param dec   包含的描述内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return 获取到的文本内容
     * @throws UiObjectNotFoundException
     */
    public static String getTextByDescContain(String dec, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return "";
        UiObject object = findObject(UiSelectorApi.descriptionContains(dec).instance(index));
        return object.getText();
    }

    /**
     * 通过描述内容匹配找到组件并获取文本内容
     *
     * @param dec   描述内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return 获取到的文本内容
     * @throws UiObjectNotFoundException
     */
    public static String getTextByDescEqual(String dec, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return "";
        UiObject object = findObject(UiSelectorApi.description(dec).instance(index));
        return object.getText();
    }

    /**
     * 通过包含文本内容找到组件并长按
     *
     * @param text  包含匹配文本
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean longClickByTextContain(String text, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = new UiObject(new UiSelector().textContains(text).instance(index));
        object.longClick();
        return true;
    }

    /**
     * 通过等值描述内容找到组件并长按
     *
     * @param desc  等值匹配内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean longClickByDescEqual(String desc, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;

        UiObject object = findObject(new UiSelector().description(desc).instance(index));

        object.longClick();
        return true;
    }

    /**
     * 通过包含描述内容找到组件并长按
     *
     * @param desc  包含匹配描述
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean longClickByDescContain(String desc, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = findObject(new UiSelector().descriptionContains(desc).instance(index));
        object.longClick();
        return true;
    }

    /**
     * 通过等值文本内容找到组件并长按
     *
     * @param text  等值匹配内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean longClickByTextEqual(String text, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().text(text).instance(index));

        object.longClick();
        return true;
    }


    /**
     * 通过资源ID找到组件并长按，资源ID可通过DDMS获取
     *
     * @param resId 资源ID
     * @param index 如果找到多个相同项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean longClickByResId(String resId, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().resourceId(resId).instance(index));
        object.longClick();
        return true;
    }

    /**
     * 通过资源ID找到组件并长按，资源ID可通过DDMS获取
     *
     * @param cls   类名
     * @param index 如果找到多个相同项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean longClickByClass(String cls, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().className(cls).instance(index));
        object.longClick();
        return true;
    }

    /**
     * 通过包名匹配等待窗口出现，包名可通过DDMS获取
     *
     * @param pkg     包名
     * @param timeout 超时时间
     * @return
     */
    public static Boolean waitNewWindowByPkg(String pkg, int timeout) {
        if (uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.pkg(pkg).depth(0)), timeout);
    }

    /**
     * 通过包含匹配文本内容等待窗口出现，文本内容即界面组件显示的内容或通过DDMS获取
     *
     * @param text    包含匹配文本内容
     * @param timeout 超时时间
     * @return
     */
    public static Boolean waitNewWindowByTextContain(String text, int timeout) {
        if (uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.textContains(text)), timeout);
    }

    /**
     * 通过等值匹配文本内容等待窗口出现，文本内容即界面组件显示的内容或通过DDMS获取
     *
     * @param text    等值匹配文本内容
     * @param timeout 超时时间
     * @return
     */
    public static Boolean waitNewWindowByTextEqual(String text, int timeout) {
        if (uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.text(text)), timeout);
    }

    /**
     * 通过包含匹配描述字段等待窗口出现，描述字段内容可通过DDMS获取
     *
     * @param desc    包含匹配描述字段
     * @param timeout 超时时间
     * @return
     */
    public static Boolean waitNewWindowByDescContain(String desc, int timeout) {
        if (uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.descContains(desc)), timeout);
    }

    /**
     * 通过等值匹配描述字段等待窗口出现，描述字段内容可通过DDMS获取
     *
     * @param desc    等值匹配描述字段
     * @param timeout 超时时间
     * @return
     */
    public static Boolean waitNewWindowByDescEqual(String desc, int timeout) {
        if (uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.desc(desc)), timeout);
    }

    /**
     * @param resId
     * @param timeout
     * @return
     */
    public static Boolean waitNewWindowByResId(String resId, int timeout) {
        if (uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.res(resId)), timeout);
    }

    /**
     * 通过包含匹配文本内容来设置文本框内容，文本即该组件上显示的内容，或者通过DDMS获取.
     *
     * @param text        包含匹配的文本内容
     * @param textContent 需设置的文本内容
     * @param index       如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean setTextByTextContain(String text, String textContent, int index) throws
            UiObjectNotFoundException {
        if (uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().textContains(text).instance(index));
        object.click();
        object.setText(textContent);
        return true;
    }

    /**
     * 通过等值匹配文本内容来设置文本框内容，文本即该组件上显示的内容，或者通过DDMS获取.
     *
     * @param text        等值匹配的内容
     * @param textContent 需要设置的内容
     * @param index       如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean setTextByTextEqual(String text, String textContent, int index) throws
            UiObjectNotFoundException {
        if (uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().text(text).instance(index));
        object.click();
        object.setText(textContent);
        return true;
    }

    /**
     * 通过类设置文本框内容，类通过DDMS可以获取.
     *
     * @param cls   类名
     * @param text  需要设置的内容
     * @param index 如果找到多个相同类的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean setTextByClass(String cls, String text, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().className(cls).instance(index));
        object.click();
        object.setText(text);

        return true;
    }

    /**
     * 通过资源ID设置文本框内容，资源ID通过DDMS可以获取.
     *
     * @param resId 资源ID
     * @param text  需要设置的内容
     * @param index 如果找到多个相同资源ID的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean setTextByResId(String resId, String text, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().resourceId(resId).instance(index));
        object.click();
        object.setText(text);
        return true;
    }

    /**
     * 通过资源ID设置文本框内容，资源ID通过DDMS可以获取.
     *
     * @param desc  描述
     * @param text  需要设置的内容
     * @param index 如果找到多个相同资源ID的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean setTextByDescEqual(String desc, String text, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;

        UiObject object = findObject(new UiSelector().description(desc).instance(index));
        object.click();
        object.setText(text);
        return true;
    }

    /**
     * 通过资源ID设置文本框内容，资源ID通过DDMS可以获取.
     *
     * @param desc  描述
     * @param text  需要设置的内容
     * @param index 如果找到多个相同资源ID的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean setTextByDescContains(String desc, String text, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;

        UiObject object = findObject(new UiSelector().descriptionContains(desc).instance(index));
        object.click();
        object.setText(text);
        return true;
    }

    /**
     * 通过等值匹配文本内容来获取对象边界值，文本即该组件上显示的内容，或者通过DDMS获取.
     *
     * @param text  等值匹配的内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Rect getBoundByTextEqual(String text, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return null;
        UiObject object = findObject(UiSelectorApi.text(text).index(index));
        return object.getBounds();
    }

    /**
     * 通过包含匹配文本内容来获取对象边界值，文本即该组件上显示的内容，或者通过DDMS获取.
     *
     * @param text  包含匹配的文本内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Rect getBoundByTextContain(String text, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return null;

        UiObject object = findObject(new UiSelector().textContains(text).instance(index));
        return object.getBounds();
    }

    /**
     * 通过类来获取对象边界值，类通过DDMS可以获取.
     *
     * @param cls   类名
     * @param index 如果找到多个相同类的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Rect getBoundByClass(String cls, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return null;

        UiObject object = findObject(new UiSelector().className(cls).instance(index));
        return object.getBounds();
    }

    /**
     * 通过资源ID来获取对象边界值，资源ID通过DDMS可以获取.
     *
     * @param resId 资源ID
     * @param index 如果找到多个相同资源ID的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Rect getBoundByResId(String resId, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return null;

        UiObject object = findObject(new UiSelector().resourceId(resId).instance(index));
        return object.getBounds();
    }

    /**
     * 通过资源ID来获取对象边界值，资源ID通过DDMS可以获取.
     *
     * @param desc  描述
     * @param index 如果找到多个相同资源ID的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Rect getBoundByDescEqual(String desc, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return null;

        UiObject object = findObject(new UiSelector().description(desc).instance(index));
        return object.getBounds();
    }

    /**
     * 通过资源ID来获取对象边界值，资源ID通过DDMS可以获取.
     *
     * @param desc  描述
     * @param index 如果找到多个相同资源ID的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Rect getBoundByDescContains(String desc, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return null;

        UiObject object = findObject(new UiSelector().descriptionContains(desc).instance(index));
        return object.getBounds();
    }

    /**
     * 通过文本正则匹配找到组件并点击
     *
     * @param regex 文本正则匹配
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickBytextMatches(String regex, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = new UiObject(new UiSelector().textMatches(regex).instance(index));
        object.click();
        return true;
    }

    /**
     * 通过文本起始匹配找到组件并点击
     *
     * @param text  文本起始匹配
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickBytextStartsWith(String text, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = new UiObject(new UiSelector().textStartsWith(text).instance(index));
        object.click();
        return true;
    }

    /**
     * 通过等值描述内容找到组件并点击
     *
     * @param desc  等值描述内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByDescriptionEqual(String desc, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = new UiObject(new UiSelector().description(desc).instance(index));
        object.click();
        return true;
    }

    /**
     * 通过描述包含内容找到组件并点击
     *
     * @param desc  描述包含内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByDescriptionContain(String desc, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = new UiObject(new UiSelector().descriptionContains(desc).instance(index));
        object.click();
        return true;
    }

    /**
     * 通过描述正则匹配找到组件并点击
     *
     * @param regex 描述正则匹配
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByDescriptionMatche(String regex, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = new UiObject(new UiSelector().descriptionMatches(regex).instance(index));
        object.click();
        return true;
    }

    /**
     * 通过描述开始字符匹配找到组件并点击
     *
     * @param desc  描述开始字符匹配
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByDescriptionStartsWith(String desc, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = new UiObject(new UiSelector().descriptionStartsWith(desc).instance(index));
        object.click();
        return true;
    }

    /**
     * 通过等值包名找到组件并点击
     *
     * @param name 等值包名
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByPackageNameEqual(String name) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = new UiObject(new UiSelector().packageName(name));
        object.click();
        return true;
    }

    /**
     * 通过资源ID正则匹配找到组件并点击
     *
     * @param regex 资源ID正则匹配
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByResourceIdMatche(String regex, int index) throws UiObjectNotFoundException {
        if (uiDevice == null)
            return false;
        UiObject object = new UiObject(new UiSelector().resourceIdMatches(regex).instance(index));
        object.click();
        return true;
    }

    /**
     * Executes a shell command using shell user identity, and return the standard output in string.
     * <p/>
     * Calling function with large amount of output will have memory impacts, and the function call
     * will block if the command executed is blocking.
     * <p>Note: calling this function requires API level 21 or above
     *
     * @param cmd the command to run
     * @return the standard output of the command
     * @throws IOException
     * @hide
     * @since API Level 21
     */
    public static String executeShellCommand(String cmd) throws IOException {
        if (uiDevice == null)
            return null;

        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if (sdkVersion >= 21) {
            return uiDevice.executeShellCommand(cmd);
        }

        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Sleep.
     *
     * @param ms Millisecond.
     */
    public static void mSleep(int ms) {
        ThreadUtil.sleepTime(ms);
    }

    //===============================================控件滑动API===========================================
    //========================================  fling =========================================
    public static void commonFlingToBeginning(final int maxSwipes) throws UiObjectNotFoundException {
        try {
            flingToBeginningTencent(maxSwipes);
        } catch (UiObjectNotFoundException e) {
            flingToBeginningAndroid(maxSwipes);
        } catch (Exception e) {
            LogUtil.d("向上滑动出错..");
        }
    }

    public static void flingToBeginningAndroid(int maxSwipes) throws UiObjectNotFoundException {
        flingToBeginning("android.webkit.WebView", maxSwipes);
    }

    public static void flingToBeginningTencent(int maxSwipes) throws UiObjectNotFoundException {
        flingToBeginning("com.tencent.smtt.webkit.WebView", maxSwipes);
    }

    public static void flingToBeginning(String className, int maxSwipes) throws UiObjectNotFoundException {
        UiScrollableApi.flingToBeginning(new UiScrollable(new UiSelector().className(className)), maxSwipes);
    }

    //========================================  scroll =========================================

    public static void scrollForwardAndroid(int maxSwipes, int maxTime) throws UiObjectNotFoundException {
        scrollForward("android.webkit.WebView", maxSwipes, maxTime);
    }

    public static void scrollForwardTencent(int maxSwipes, int maxTime) throws UiObjectNotFoundException {
        scrollForward("com.tencent.smtt.webkit.WebView", maxSwipes, maxTime);
    }

    public static void scrollForward(String className, int maxSwipes, int maxTime) throws UiObjectNotFoundException {
        int i = 0;
        while (i < maxTime) {
            UiScrollableApi.scrollForward(new UiScrollable(new UiSelector().className(className)), maxSwipes);
            i++;
        }
    }

    public static void scrollBackward(String className, int maxSwipes, int maxTime) throws UiObjectNotFoundException {
        int i = 0;
        while (i < maxTime) {
            UiScrollableApi.scrollBackward(new UiScrollable(new UiSelector().className(className)), maxSwipes);
            i++;
        }
    }

    public static void scrollToTheBeginingAndroid(final int maxSwipes, int step) throws UiObjectNotFoundException {
        scrollToTheBegining("android.webkit.WebView", maxSwipes, step);
    }

    public static void scrollToTheBeginingTencent(final int maxSwipes, int step) throws UiObjectNotFoundException {
        scrollToTheBegining("com.tencent.smtt.webkit.WebView", maxSwipes, step);
    }

    public static void scrollToTheBegining(String className, int maxSwipes, int steps) throws
            UiObjectNotFoundException {
        UiScrollableApi.scrollToBeginning(new UiScrollable(new UiSelector().className(className)), maxSwipes, steps);
    }

    public static void scrollToEnd(String className, int maxSwipesTimes, int steps) throws UiObjectNotFoundException {
        UiScrollableApi.scrollToEnd(new UiScrollable(new UiSelector().className(className)), maxSwipesTimes, steps);
    }

    /**
     * 滑动到对象位置（适用于WebView中的对象）
     *
     * @param scrollable
     * @param obj
     * @throws UiObjectNotFoundException
     */
    public static void scrollIntoViewForWebview(UiScrollable scrollable, UiObject obj) throws
            UiObjectNotFoundException {
        for (int i = 0; i < 30; i++) {
            if (obj.getBounds().bottom - AutomatorApi.getDisplayHeight() <= 0) {
                LogUtil.d("点击");
                obj.click();
                mSleep(1000);
                break;
            }
            scrollable.scrollForward();
        }
    }


    /**
     * 滑动到对象位置（适用于非WebView中的对象）
     *
     * @param scrollable
     * @param obj
     * @throws UiObjectNotFoundException
     */
    public static void scrollIntoView(UiScrollable scrollable, UiObject obj) throws UiObjectNotFoundException {
        for (int i = 0; i < 30; i++) {
            if (obj != null && obj.exists()) {
                break;
            }
            scrollable.scrollForward();
        }
    }

    public static void scrollIntoViewForFrameLayout(UiObject obj, int times) throws UiObjectNotFoundException {
        UiScrollable scrollable = new UiScrollable(UiSelectorApi.className("android.widget.FrameLayout"));
        for (int i = 0; i < 30; i++) {
            if (obj.getBounds().bottom - AutomatorApi.getDisplayHeight() <= 0) {
                mSleep(times);
                obj.click();
                LogUtil.d("点击");
                mSleep(times);
                break;
            }
            scrollable.scrollForward();
        }
    }

    /**
     * 长按对象
     *
     * @param object
     */
    public static boolean longClickUiObect(UiObject object) throws UiObjectNotFoundException {
        return longClickUiObect(object, 70);
    }

    /**
     * 长按对象
     *
     * @param object
     */
    public static boolean longClickUiObect(UiObject object, int steps) throws UiObjectNotFoundException {
        Rect bounds = object.getBounds();
        int x = bounds.centerX();
        int y = bounds.centerY();
        return AutomatorApi.swipe(x, y, x, y, steps);//300,最后一个参数单位是5ms
    }

    public static void registerWatcher(String watcherName, final UiSelector checkSel, final UiSelector optSel) {
        registerWatcher(watcherName, checkSel, optSel, null);
    }

    public static void registerWatcher(String watcherName, final UiSelector checkSel, final UiSelector optSel, final
    UiSelector nextOptSel) {
        if (checkSel == null || optSel == null) {
            return;
        }

        uiDevice.registerWatcher(watcherName, new UiWatcher() {
            UiObject object = AutomatorApi.findObject(checkSel);

            @Override
            public boolean checkForCondition() {
                if (object.exists()) {
                    try {
                        UiObject object = AutomatorApi.findObject(optSel);
                        if (object != null) {
                            object.click();
                            if (nextOptSel != null) {
                                UiObject nextObj = AutomatorApi.findObject(nextOptSel);
                                if (nextObj != null) {
                                    nextObj.click();
                                }
                            }
                        } else {
                            LogUtil.e("根据optSel获取到的对象为Null...");
                        }
                    } catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public static boolean setTextByPaste(UiObject object, String text) throws UiObjectNotFoundException,
            ClassNotFoundException {
        if (text == null) {
            text = "";
        }

        mSleep(200);
        Tracer.trace(text);
        long selectorTimeout = Configurator.getInstance().getWaitForSelectorTimeout();
        AccessibilityNodeInfo node = (AccessibilityNodeInfo) ReflectUtil.invokeMethod(object, "android.support.test" +
                ".uiautomator.UiObject", "findAccessibilityNodeInfo", new Class[]{long.class}, selectorTimeout);

        if (node == null) {
            Object getSelector = ReflectUtil.invokeMethod(object, "android.support.test.uiautomator.UiObject",
                    "getSelector", null);
            throw new UiObjectNotFoundException(getSelector.toString());
        }
        Bundle args = new Bundle();
        args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
        return node.performAction(AccessibilityNodeInfo.ACTION_PASTE, args);
    }
}





