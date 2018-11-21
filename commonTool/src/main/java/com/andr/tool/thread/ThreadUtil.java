package com.andr.tool.thread;

/**
 * Created by zhangxiaoming on 2018/9/14.
 */

public class ThreadUtil {

    public static void sleepTime(long mis){

        try {
            Thread.sleep(mis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
