package com.al.ddpunch.util;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by zhangxiaoming on 2018/7/27.
 */

public class CMDUtil {
    public static void stopProcess( String pakeName) {
        String cmd = "am force-stop " + pakeName;

        try {

            execRootCmdSilent( cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //模拟点击坐标
    public static void ClickXy(String x,String y){
        String cmd = "input tap "+x+" "+y ;

        try {

            execRootCmdSilent( cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行命令但不关注结果输出
     */
    private  static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());

            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
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
        }
        return result;
    }

}
