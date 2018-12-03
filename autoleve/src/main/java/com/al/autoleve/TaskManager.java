package com.al.autoleve;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.andr.tool.apk.ApkUtil;
import com.andr.tool.cmd.CmdUtils;
import com.andr.tool.file.FileUtil;
import com.andr.tool.log.LogUtil;
import com.andr.tool.net.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zhangxiaoming on 2018/11/20.
 */

public class TaskManager
{

    public static void updateApk(final Activity activity)
    {

        try
        {
            JSONObject reqJosn = new JSONObject();
            reqJosn.put("dev_token", FileUtil.getInstance().readFile(AppConfig.TOKEN_FILE_PATH));
            reqJosn.put("app_ver", ApkUtil.getInstance().getAppVersionCode(activity.getApplicationContext(),
                    activity.getPackageName()));

            final ProgressDialog dialog = new ProgressDialog(activity);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            OkHttpUtil.getInstence().postForAsy(AppConfig.UPDATE_APK_URL, reqJosn.toString(), new OkHttpUtil
                    .ResultCallBack()
            {
                @Override
                public void sucess(String res)
                {

                    dialog.dismiss();
                    LogUtil.d("检查更新->" + res);
                    if (res == null || "".equals(res))
                    {
                        LogUtil.e("网络出错");
                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(activity, "服务器返回为空", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else
                    {

                        JSONObject rltJson = null;
                        try
                        {
                            rltJson = new JSONObject(res);
                            int resCode = Integer.valueOf(rltJson.getString("resCode"));
                            final String resMsg = rltJson.getString("resMsg");

                            if (resCode == 0)
                            {
                                //保存code
                                LogUtil.i(resMsg);
                                String resData = rltJson.getString("resData");

                                JSONObject resDataJson = new JSONObject(resData);
                                String apkVer = resDataJson.getString("apkVer");
                                final String apkUrl = resDataJson.getString("apkUrl");

                                activity.runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                        builder.setTitle("提示");
                                        builder.setMessage("检测到新版本,是否下载安装");
                                        builder.setPositiveButton("是", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {

                                                downApk(apkUrl, activity);
                                            }
                                        });
                                        builder.setNegativeButton("否", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {

                                            }
                                        });
                                        builder.create().show();

                                    }
                                });

                            } else
                            {
                                activity.runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        Toast.makeText(activity, resMsg, Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }


                }

                @Override
                public void faild(String res)
                {
                    dialog.dismiss();
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            });
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * 杀死进程
     */
    public static void killGrep(Activity context)
    {
        try
        {
            String rlt = CmdUtils.executeShellCommand("ps |grep " + context.getPackageName());
            LogUtil.d("获取的进程信息");
            //获取进程ID
            String[] split = rlt.split(" ");
            ArrayList<String> infos = new ArrayList<String>();
            for (String string : split)
            {
                if (string != null && !"".endsWith(string))
                {
                    infos.add(string);
                }
            }
            if (infos.size() > 2)
            {
                LogUtil.d("进程id-" + infos.get(1));
                Toast.makeText(context, "已停止", Toast.LENGTH_SHORT).show();

                CmdUtils.executeShellCommand("kill " + infos.get(1));
            } else
            {
                Toast.makeText(context, "懒人打卡进程信息：" + rlt, Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @SuppressLint("StaticFieldLeak")
    public static void startTestApp(final Activity content)
    {
        final ProgressDialog dialog = new ProgressDialog(content);
        dialog.setMessage("启动中");
        new AsyncTask<Integer, Integer, Integer>()
        {

            @Override
            protected Integer doInBackground(Integer... integers)
            {
                toggleNotificationListenerService(content);

                if (CmdUtils.execRootCmdSilent(AppConfig.RESTART_MAIN_LOGICAL_APK_CMD) != -1)
                {
                    LogUtil.d("启动测试程序成功");
                    SharpData.setActive(content, System.currentTimeMillis() / 1000);

                    return 1;
                }
                LogUtil.d("启动测试程序失败");
                return -1;
            }

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                dialog.show();

            }

            @Override
            protected void onPostExecute(Integer integer)
            {
                super.onPostExecute(integer);
                dialog.dismiss();
                if (integer != 1)
                {
                    content.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(content, "测试程序无法完成安装,请重试", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        }.execute();
    }


    public static void downApk(String downUrl, final Activity activity)
    {

        final ProgressDialog dialog = new ProgressDialog(activity);

        dialog.setMax(100);
        dialog.show();
        dialog.setMessage("下载中..");

        OkHttpUtil.getInstence().downFileForAsy(downUrl, AppConfig.AUTO_OTHER_MK_NAME, new OkHttpUtil
                .DownProgressListener
                ()
        {


            @Override
            public void downStart(String fileName)
            {
                LogUtil.d("开始下载");
            }

            @Override
            public void downProgress(final int progress)
            {
                progressText(activity, dialog, " 下载中..." + progress + "%");
            }

            @Override
            public void downError(String fileName, final String msg)
            {
                LogUtil.d("现在失败.." + msg);
                dissDialog(activity, dialog);
                showTextDialog(activity, "下载失败");

            }

            @Override
            public void downFinsh(String filePath)
            {

                progressText(activity, dialog, "正在安装...");
                if (ApkUtil.getInstance().unInstallApk(AppConfig.TEST_APP_PAKENAME))
                {
                    LogUtil.d("卸载测试apk成功");
                    if (ApkUtil.getInstance().installApk(filePath, 0))
                    {
                        LogUtil.d("安装成功");
                        dissDialog(activity, dialog);
                        showTextAndBtnDialog(activity, "安装成功,是否立即启动");
                    } else
                    {
                        LogUtil.d("安装失败");

                    }

                } else
                {
                    dissDialog(activity, dialog);
                    showTextDialog(activity, "卸载失败");
                }

                FileUtil.getInstance().deleteFile(filePath);

            }
        });
    }

    private static void showTextDialog(final Activity activity, final String msg)
    {

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setMessage(msg);
                alertDialog.show();
            }
        });
    }

    private static void showTextAndBtnDialog(final Activity activity, final String msg)
    {

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setMessage(msg);
                alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (CmdUtils.execRootCmdSilent(AppConfig.RESTART_MAIN_LOGICAL_APK_CMD) != -1)
                        {
                            LogUtil.d("启动测试程序成功");
                            SharpData.setActive(activity, System.currentTimeMillis() / 1000);
                            Toast.makeText(activity, "启动成功", Toast.LENGTH_SHORT).show();
                        } else
                        {
                            Toast.makeText(activity, "启动失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                alertDialog.show();

            }
        });
    }

    private static void dissDialog(Activity activity, final ProgressDialog dialog)
    {
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                dialog.dismiss();
            }
        });
    }

    private static void progressText(Activity activity, final ProgressDialog dialog, final String msg)
    {
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                dialog.setMessage(msg);
            }
        });
    }

    private static void toggleNotificationListenerService(Context context)
    {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, DDNotService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(context, DDNotService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }
}
