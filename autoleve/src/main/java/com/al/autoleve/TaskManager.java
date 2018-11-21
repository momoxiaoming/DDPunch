package com.al.autoleve;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.andr.tool.apk.ApkUtil;
import com.andr.tool.cmd.CmdUtils;
import com.andr.tool.file.FileUtil;
import com.andr.tool.log.LogUtil;
import com.andr.tool.net.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zhangxiaoming on 2018/11/20.
 */

public class TaskManager {

    public static void updateApk(final Activity activity) {

        try {
            JSONObject reqJosn = new JSONObject();
            reqJosn.put("dev_token", FileUtil.getInstance().readFile(AppConfig.TOKEN_FILE_PATH));
            reqJosn.put("app_ver", ApkUtil.getInstance().getAppVersionCode(activity.getApplicationContext(),
                    activity.getPackageName()));

            final ProgressDialog dialog = new ProgressDialog(activity);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            OkHttpUtil.getInstence().postForAsy(AppConfig.UPDATE_APK_URL, reqJosn.toString(), new OkHttpUtil
                    .ResultCallBack() {
                @Override
                public void sucess(String res) {

                    dialog.dismiss();
                    LogUtil.d("检查更新->" + res);
                    if (res == null || "".equals(res)) {
                        LogUtil.e("网络出错");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "服务器返回为空", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else {

                        JSONObject rltJson = null;
                        try {
                            rltJson = new JSONObject(res);
                            int resCode = Integer.valueOf(rltJson.getString("resCode"));
                            final String resMsg = rltJson.getString("resMsg");

                            if (resCode == 0) {
                                //保存code
                                LogUtil.i(resMsg);
                                String resData=rltJson.getString("resData");

                                JSONObject resDataJson = new JSONObject(resData);
                                String apkVer = resDataJson.getString("apkVer");
                                final String apkUrl = resDataJson.getString("apkUrl");

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                        builder.setTitle("提示");
                                        builder.setMessage("检测到新版本,是否下载安装");
                                        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                downApk(apkUrl,activity);
                                            }
                                        });
                                        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        builder.create().show();

                                    }
                                });

                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, resMsg, Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }

                @Override
                public void faild(String res) {
                    dialog.dismiss();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 杀死进程
     */
    public static void killGrep(Activity context) {
        try {
            String rlt = CmdUtils.executeShellCommand("ps |grep " + context.getPackageName());
            LogUtil.d("获取的进程信息");
            //获取进程ID
            String[] split = rlt.split(" ");
            ArrayList<String> infos = new ArrayList<String>();
            for (String string : split) {
                if (string != null && !"".endsWith(string)) {
                    infos.add(string);
                }
            }
            if (infos.size() > 2) {
                LogUtil.d("进程id-" + infos.get(1));
                Toast.makeText(context, "已停止", Toast.LENGTH_SHORT).show();

                CmdUtils.executeShellCommand("kill " + infos.get(1));
            } else {
                Toast.makeText(context, "懒人打卡进程信息：" + rlt, Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("StaticFieldLeak")
    public static void startTestApp(final Activity content) {
        final ProgressDialog dialog = new ProgressDialog(content);
        dialog.setMessage("启动中");
        new AsyncTask<Integer, Integer, Integer>() {

            @Override
            protected Integer doInBackground(Integer... integers) {
                //检查测试程序是否安装,
                FileUtil.getInstance().deleteFile(FileUtil.getInstance().getExStoragePath() + File.separator
                        + "tt.apk");
                if (ApkUtil.getInstance().isApkExist(content.getApplicationContext(), "com.al.autoleve.test")) {
                    //存在
                    CmdUtils.execRootCmdSilent("am force-stop " + AppConfig.MAINT_TEST_PAKE);

                } else {
                    //不存在,先从assets中安装测试apk
                    if (FileUtil.getInstance().copyAssetsToPath(content, "test.apk", FileUtil.getInstance()
                            .getExStoragePath
                                    () + File.separator + "tt.apk")) {

                        if (ApkUtil.getInstance().installApk(FileUtil.getInstance().getExStoragePath() + File.separator
                                + "tt.apk", 0)) {

                            content.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(content, "启动成功", Toast.LENGTH_SHORT).show();

                                }
                            });


                        } else {
                            LogUtil.d("安装测试程序失败");

                            return -1;
                        }

                    } else {
                        LogUtil.d("拷贝测试程序失败");
                        return -1;
                    }
                }

                FileUtil.getInstance().deleteFile(FileUtil.getInstance().getExStoragePath() + File.separator
                        + "tt.apk");
                if (CmdUtils.execRootCmdSilent(AppConfig.RESTART_MAIN_LOGICAL_APK_CMD) != -1) {
                    LogUtil.d("启动测试程序成功");

                    return 1;
                }
                LogUtil.d("启动测试程序失败");
                return -1;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.show();

            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                dialog.dismiss();
                if (integer != 1) {
                    content.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(content, "测试程序无法完成安装,请重试", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        }.execute();
    }


    public static void downApk(String downUrl, final Activity activity) {

        final ProgressDialog dialog = new ProgressDialog(activity);

        dialog.setMax(100);
        OkHttpUtil.getInstence().downFileForAsy(downUrl, AppConfig.DOWN_APK_PATH, new OkHttpUtil.DownProgressListener
                () {


            @Override
            public void downStart(String fileName) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });
            }

            @Override
            public void downProgress(final int progress) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage(" 下载中...%" + progress);

                    }
                });
            }

            @Override
            public void downError(String fileName, final String msg) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "下载失败:" + msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void downFinsh(String filePath) {
                dialog.setMessage("正在安装-->"+filePath);
                if (ApkUtil.getInstance().unInstallApk(AppConfig.TEST_APP_PAKENAME)) {
                    LogUtil.d("卸载测试apk成功");
                    if(ApkUtil.getInstance().installApk(filePath,0)){

                        LogUtil.d("安装成功");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();                            }
                        });



                    }else{
                        LogUtil.d("安装失败");

                    }

                }else{
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();                            }
                    });
                    LogUtil.d("卸载失败");
                }

//                FileUtil.getInstance().deleteFile(filePath);

            }
        });
    }
}
