package com.al.autoleve;

import android.app.UiAutomation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.InstrumentationUiAutomatorBridge;
import android.support.test.uiautomator.UiDevice;

import com.al.autoleve.core.automator.api.AutomatorApi;
import com.al.autoleve.core.data.UiDataCenter;
import com.al.autoleve.core.task.AutoTaskManager;
import com.al.autoleve.core.util.At;
import com.andr.tool.log.LogUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by zhangxiaoming on 2018/9/14.
 * <p>
 * 相关注解执行顺序
 *
 * @BeforeClass >>@Before>>@Test>>@After>>….>>@Before>>@Tess>>@After>>@Before>>@After>>@AfterClass
 */
@RunWith(AndroidJUnit4.class)
public class StartApp {

    private static UiDevice uiDevice;
    private static Context mContext;
    private static UiAutomation uiAutomation;


    @Before
    public void init() {
        LogUtil.d("init");

        try {
            mContext = InstrumentationRegistry.getContext();
            uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

            //通过反射得到 UiAutomation 对象
            Method m1 = uiDevice.getClass().getDeclaredMethod("getAutomatorBridge");
            m1.setAccessible(true);
            InstrumentationUiAutomatorBridge ins = (InstrumentationUiAutomatorBridge) m1.invoke(uiDevice);
            Field field = ins.getClass().getSuperclass().getDeclaredField("mUiAutomation");
            field.setAccessible(true);
            uiAutomation = (UiAutomation) field.get(ins);

            //注入api中
            AutomatorApi.setContext(mContext);
            AutomatorApi.setUiDevice(uiDevice);
            AutomatorApi.setUiAutomation(uiAutomation);

            //注入数据中心
            UiDataCenter.getInstance().setTestApkContext(mContext);
            UiDataCenter.getInstance().setMainApkContext(InstrumentationRegistry.getTargetContext());
            UiDataCenter.getInstance().setUiDevice(uiDevice);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置操作的超时时间
        Configurator.getInstance().setActionAcknowledgmentTimeout(1);

    }


    @Test
    public void runTestApp() {
        //开始工作,执行一些基本的初始化

        //创建数据库
        if(At.getToken()!=null&& At.isRoot()){
            LogUtil.d("开始执行测试程序");

            try {
                AutoTaskManager.getInstence().initData(UiDataCenter.getInstance().getTestApkContext());
                AutoTaskManager.getInstence().startWork();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            LogUtil.d("测试程序执行失败,程序未注册或未root");

        }


    }


}
