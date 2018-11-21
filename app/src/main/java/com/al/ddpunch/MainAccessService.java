package com.al.ddpunch;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.al.ddpunch.email.EmaiUtil;
import com.al.ddpunch.util.CMDUtil;
import com.al.ddpunch.util.LogUtil;
import com.al.ddpunch.util.SharpData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangxiaoming on 2018/7/25.
 * 模拟点击主服务
 */

public class MainAccessService extends AccessibilityService {


    //考勤页面判定
    public static String webview_page_ResId = "com.alibaba.android.rimet:id/webview_frame";

    //主页
    public static String main_page_ResId = "com.alibaba.android.rimet:id/home_bottom_tab_root";

    public static String work_page_ResId = "com.alibaba.android.rimet:id/home_bottom_tab_button_work";

    //工作页判定
    public static String kaoqin_page_ResId = "com.alibaba.android.rimet:id/oa_fragment_gridview";


    public static boolean isSyn = true;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startCSer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        jumpNot(event);


    }

    private void startCSer() {
        synchronized (MainAccessService.class) {
            if (SharpData.getOpenApp(getApplicationContext()) == 1) {
                LogUtil.E("工作停止,请手动开启工作");
                return;
            }
            int order = SharpData.getOrderType(getApplicationContext());
            if (order == 0) {
                LogUtil.E("当前无任务!");
                return;
            }

            new_work(order);
        }


    }

    public void jumpNot(AccessibilityEvent event) {
//        LogUtil.D("event->"+event);
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED&&event.getPackageName().equals(Comm.dingding_PakeName)) {

            //获取Parcelable对象
            Parcelable data = event.getParcelableData();
            if (data == null) {
                return;
            }
            //判断是否是Notification对象
            if (data instanceof Notification) {

                Notification notification = (Notification) data;

                String tikeText = notification.tickerText == null ? "" : notification.tickerText.toString();
                String notTitle = notification.extras.getString("android.title") == null ? "" : notification.extras
                        .getString("android.title");//标题
                String subText = notification.extras.getString("android.subText") == null ? "" : notification.extras
                        .getString("android.subText");//摘要
                String text = notification.extras.getString("android.text") == null ? "" : notification.extras
                        .getString("android.text");  //正文
                String postTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(notification.when));   //通知时间

                LogUtil.D("通知时间-->" + postTime);
                LogUtil.D("通知-->tikeText:" + tikeText);
                LogUtil.D("通知-->标题:" + notTitle + "--摘要--" + subText + "--正文--" + text);

                SharpData.setNotData(getApplicationContext(), "通知时间-->" + postTime + "-通知-->tikeText:" + tikeText +
                        "通知-->标题:" + notTitle + "--摘要--" + subText + "--正文--" + text);

                String emmail = SharpData.getEmailData(getApplicationContext()).equals("") ? Comm.EmailInfo :
                        SharpData.getEmailData(getApplicationContext());

                //首先判断通知时间是不是当前时间
                String nowTime = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());

                //如果是当天
                if (nowTime.equals(postTime)) {
                    if (text.contains("上班打卡成功")) {
                        SharpData.setIsCompent(getApplicationContext(), 1);
                        EmaiUtil.sendMsg("服务通知1:"+text, emmail);
                    }
                    if (text.contains("下班打卡成功")) {
                        SharpData.setIsCompent(getApplicationContext(), 2);
                        EmaiUtil.sendMsg("服务通知1:"+text, emmail);

                    }
                }


            }
        }
    }

    private void new_work(int order) {

        isSyn = true;
        try {
            //脚本初始化,判断是否在主页
            AccessibilityNodeInfo node = refshPage();
            if (node == null || !isStartActivity(node.getPackageName().toString())) {

                throw new Exception("程序不在初始化启动器页面,抛出异常");
            }
            int m = 10;
            while (m > 0) {
                LogUtil.D("循环--" + node);
                if (node != null && Comm.dingding_PakeName.equals(node.getPackageName().toString())) {
                    node = refshPage();
                    LogUtil.D("已进入app" + node);

                    //进入主页
                    break;
                } else {
                    startApplication(getApplicationContext(), Comm.dingding_PakeName);
                }
                sleepT(1000);  //1秒钟启动一次
                if (node != null) {
                    node = refshPage();
                }
                m--;
            }
            if (m <= 0) {
                throw new Exception("启动钉钉异常");
            }
            sleepT(1000);

            int k = 5;
            while (k > 0) {
                //确认进入钉钉主页
                if (findResIdById(node, main_page_ResId)) {
                    LogUtil.D("已进入app主页");
                    break;
                }
                sleepT(1000);
                if (node != null)
                    node = refshPage();
                k--;
            }
            if (k <= 0) {
                throw new Exception("已进入app,未找到主页节点");
            }


            //进入工作页,点击工作按钮
            if (!findResIdById(node, work_page_ResId)) {
                throw new Exception("已进入主页,未找到工作页按钮节点");
            } else {
                LogUtil.D("已进入工作页");

                List<AccessibilityNodeInfo> list = node.findAccessibilityNodeInfosByViewId(work_page_ResId);
                list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            sleepT(1000);
            node = refshPage();

            //确认进入工作页,点击考勤打卡节点,进入考勤页面

            List<AccessibilityNodeInfo> list = node.findAccessibilityNodeInfosByViewId(kaoqin_page_ResId);
            if (list != null || list.size() != 0) {
                node = list.get(0);
                if (node != null && node.getChildCount() >= 8) {
                    node = node.getChild(7);
                    if (node != null) {  //已找到考勤打卡所在节点,进行点击操作
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    } else {
                        throw new Exception("已进入工作页,但未找到考勤打卡节点");
                    }
                } else {
                    throw new Exception("已进入工作页,但未找到考勤打卡节点");
                }
            } else {
                throw new Exception("已进入工作页,但未找到相关节点");
            }


            node = refshPage();

            int l = 10;
            while (l > 0) {

                List<AccessibilityNodeInfo> list2 = node.findAccessibilityNodeInfosByViewId(webview_page_ResId);
                if (list2 != null && list2.size() != 0) {
                    node = list2.get(0);
                    LogUtil.D("确认已进入考勤打卡页面" + list2);
                    break;
                }
                l--;
                node = refshPage();
                sleepT(1000);
            }

            if (l <= 0) {
                throw new Exception("进入考勤打卡页面异常");
            }
            sleepT(4000);

            //尝试打卡操作
            int j = 3;
            while (j >= 0) {
                LogUtil.D("尝试打卡操作->" + j);
                DoDaKa(order);
                //执行完操作之后,判断是否打卡成功
                sleepT(2000);
                j--;
            }

            AppCallBack();

            if (node != null) node.recycle();
        } catch (Exception e)

        {
            if (e != null) {
                String msg = e.getMessage() == null ? "" : e.getMessage();
                LogUtil.E(msg + "");
                if (msg.equals("进入考勤打卡页面异常")) {
                    //估计卡住了,杀死进程

                    CMDUtil.stopProcess(getRootInActiveWindow().getPackageName().toString());
                }
            }

            //执行回退操作
            AppCallBack();

        }

    }


    private void DoDaKa(int order) {

        int heightPixels = SharpData.getHeightmetrics(getApplicationContext());
        int widthPixels = SharpData.getWidthmetrics(getApplicationContext());
        LogUtil.D("heightPixels:"+heightPixels+"----widthPixels:"+widthPixels);

        double t_x ;
        double t_y ;

        double b_x ;
        double b_y ;

        if (widthPixels == Comm.widthmetrics_defult) {
            t_x = 240;
            b_x = 262;
        } else {
            t_x = ((float)240 / Comm.widthmetrics_defult) * widthPixels;
            b_x = ((float)262 / Comm.widthmetrics_defult)* widthPixels;
        }

        if (heightPixels == Comm.heightmetrics_defult) {
            t_y = 314;
            b_y = 556;
        } else {
            t_y = ((float)314 / Comm.heightmetrics_defult) * heightPixels;
            b_y = ((float)556 / Comm.heightmetrics_defult) * heightPixels;
        }


        int t_x_i= (int) t_x;
        int t_y_i= (int) t_y;
        int b_x_i= (int) b_x;
        int b_y_i= (int) b_y;

        LogUtil.D("t_x:"+t_x_i+"----t_y:"+t_y_i);
        LogUtil.D("b_x:"+b_x_i+"----b_y:"+b_y_i);


        if (order == 1) {
            //上班打卡
            CMDUtil.ClickXy(t_x_i+"", t_y_i+"");
        } else if (order == 2) {
            //下班卡
            CMDUtil.ClickXy(b_x_i+"", b_y_i+"");
        }


        //检查是否打卡成功
//       ;
//        LogUtil.D("根节点");
//        //查询所有的根节点,假如有弹窗,说明打卡成功
//        List<AccessibilityNodeInfo> list = getAllNode(node, null);
//        LogUtil.D("所有节点个数-->" + list.size());
//        if (list != null) {
//            for (AccessibilityNodeInfo info : list) {
//                String className = info.getClassName().toString();
//                if ("android.app.Dialog".equals(className)) {
//                    //说明可能是打卡导致的成功弹窗
//                    AccessibilityNodeInfo nodeInfo = info.getChild(0);
//                    if (nodeInfo != null) {
//                        nodeInfo = nodeInfo.getChild(1);
//                        if (nodeInfo != null) {
//                            String des = nodeInfo.getContentDescription().toString();
//                            if (des.contains("打卡成功")) {
//                                return true;
//                            }
//                        }
//                    }
//
//                }
//            }
//        }
//        return false;
    }

    //程序异常时的操作方法
    private void AppCallBack() {
        int i = 10;
        AccessibilityNodeInfo node = getRootInActiveWindow();

        while (true) {

            if (i == 0) {
                //有毒,直接重启手机
                CMDUtil.stopProcess(Comm.dingding_PakeName);
                break;
            }

            LogUtil.D("执行回退操作" + i);
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            if (node != null) {
                LogUtil.E("当前包名-" + node.getPackageName());
            }
            if (node != null && isStartActivity(node.getPackageName().toString())) {
                //已回退到启动页,退出循环
                LogUtil.D("已回到初始页");
                break;
            }
            i--;

            sleepT(1000); //睡眠一秒
            node = refshPage();
        }

        if (node != null)
            node.recycle();

    }


    public boolean isStartActivity(String page) {
        if (Comm.launcher_PakeName.equals(page) || Comm
                .launcher_PakeName2.equals(page)) {
            return true;
        }
        return false;
    }

    //刷新节点
    private AccessibilityNodeInfo refshPage() {
        return getRootInActiveWindow();
    }

    //递归获取所有节点
    private List<AccessibilityNodeInfo> getAllNode(AccessibilityNodeInfo node, List<AccessibilityNodeInfo> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        LogUtil.D("递归节点-->" + node + "---" + node.getChildCount());
        if (node != null && node.getChildCount() != 0) {
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo info = node.getChild(i);
                if (info != null) {
                    LogUtil.D("打卡节点数-" + info);
                    list.add(info);
                    node = info;

                }
            }

        } else {
            return list;
        }

        return getAllNode(node, list);

    }


    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

    }


    public static boolean startApplication(Context context, String packageName) {
        boolean rlt = false;

        PackageManager pkgMgr = context.getPackageManager();
        if (null != pkgMgr) {
            Intent intent = pkgMgr.getLaunchIntentForPackage(packageName);
            if (null != intent) {
                context.startActivity(intent);
                rlt = true;
            }
        }
        return rlt;
    }


    public boolean findResIdById(AccessibilityNodeInfo info, String resId) {
        if (info == null) {
            return false;
        }
        List<AccessibilityNodeInfo> list = info.findAccessibilityNodeInfosByViewId(resId);

        if (list == null || list.size() == 0) {
            return false;
        }
        return true;
    }


    public void sleepT(long t) {

        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
