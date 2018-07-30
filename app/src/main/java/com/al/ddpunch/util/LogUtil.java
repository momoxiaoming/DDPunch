package com.al.ddpunch.util;

import android.util.Log;

/**
 * Created by zhangxiaoming on 2018/7/25.
 */

public class LogUtil {

    private static String TAG="allen";

    private static boolean isLog=true;


    public  static  void D(String str){

        if(isLog){

            Log.d(TAG,str);

        }
    }
    public  static  void E(String str){

        if(isLog){

            Log.e(TAG,str);

        }
    }
}
