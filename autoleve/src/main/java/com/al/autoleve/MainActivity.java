package com.al.autoleve;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andr.tool.apk.ApkUtil;
import com.andr.tool.file.FileUtil;
import com.andr.tool.log.LogUtil;
import com.andr.tool.net.OkHttpUtil;
import com.andr.tool.phone.PhoneUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private Button start_btn, stop_btn, regiest, update;

    public TextView token_text, root_text, version_text, runText;

    private boolean isPermisstion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityPermissionsDispatcher.needsPermissionWithPermissionCheck(this);
        PhoneUtil.getInstance().OpenNotificationReadPermission(this);

        initView();
        initData();


    }

    private void initView()
    {
        start_btn = findViewById(R.id.start_btn);
        stop_btn = findViewById(R.id.stop_btn);
        token_text = findViewById(R.id.token_text);
        root_text = findViewById(R.id.root_text);
        regiest = findViewById(R.id.regiest_btn);
        update = findViewById(R.id.update_btn);
        runText = findViewById(R.id.run_text);

        version_text = findViewById(R.id.version_text);

        start_btn.setOnClickListener(this);
        stop_btn.setOnClickListener(this);
        regiest.setOnClickListener(this);
        update.setOnClickListener(this);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setIsRunState();
    }

    public void initData()
    {


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        SharpData.setHeightmetrics(getApplicationContext(), heightPixels);
        SharpData.setWidthmetrics(getApplicationContext(), widthPixels);
        File file = new File(AppConfig.AUTO_OTHER_MK_NAME);
        if (!file.exists())
        {
            file.mkdirs();
        }

        //创建目录必须一级级创建
        FileUtil.getInstance().makeDirs(AppConfig.AUTO_MAIN_MK_NAME);
//        FileUtil.getInstance().makeDirs(AppConfig.AUTO_OTHER_MK_NAME);

        setIsRunState();
        setToken();
    }


    @Override
    public void onClick(View v)
    {

        if (!PhoneUtil.getInstance().isRoot())
        {
            Toast.makeText(this, "手机root后方可使用本程序!", Toast.LENGTH_SHORT);
            return;
        }

        if (v == regiest)
        {
            if (isPermisstion)
            {

                regiest();

            } else
            {
                Toast.makeText(this, "请打开相应权限", Toast.LENGTH_SHORT).show();
            }
        } else if (v == start_btn)
        {
            TaskManager.startTestApp(this);

        } else if (v == stop_btn)
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("停止程序后,程序将无法执行打卡,可通过启动按钮启动,是否确定?");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    TaskManager.killGrep(MainActivity.this);
                    runText.setText("已停止");
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                }
            });
            builder.create().show();
        } else if (v == update)
        {

            TaskManager.updateApk(this);
        }
    }


    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest
            .permission.READ_PHONE_STATE})
    void needsPermission()
    {
        isPermisstion = true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void setIsRunState()
    {
        if (SharpData.getActive(this))
        {
            runText.setText("正常运行");
        } else
        {
            runText.setText("未运行");
        }
    }

    private void setToken()
    {
        //检查手机是否root,未root则不执行任何操作
        String token = FileUtil.getInstance().readFile(AppConfig.TOKEN_FILE_PATH);

        if (token != null)
        {
            token_text.setText(token);
            regiest.setText("程序已注册");
            regiest.setEnabled(false);
        }
        version_text.setText("版本号:" + ApkUtil.getInstance().getAppVersionName(this));
        root_text.setText(PhoneUtil.getInstance().isRoot() ? "已root" : "未root");

    }

    private void regiest()
    {
        try
        {
            JSONObject reqJosn = new JSONObject();
            reqJosn.put("dev_andId", PhoneUtil.getInstance().getAndId(getApplicationContext()));
            reqJosn.put("dev_imei", PhoneUtil.getInstance().getImei(getApplicationContext()));
            reqJosn.put("dev_isRt", PhoneUtil.getInstance().isRoot() ? "0" : "1");
            reqJosn.put("dev_name", PhoneUtil.getInstance().getDeviceName());
            reqJosn.put("dev_sdk", PhoneUtil.getInstance().getSysSdkVer());
            reqJosn.put("app_ver", ApkUtil.getInstance().getAppVersionCode(getApplicationContext(),
                    getPackageName()));

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            OkHttpUtil.getInstence().postForAsy(AppConfig.REQ_REGIEST_URL, reqJosn.toString(), new OkHttpUtil
                    .ResultCallBack()
            {
                @Override
                public void sucess(String res)
                {

                    dialog.dismiss();
                    LogUtil.d("注册返回->" + res);
                    if (res == null || "".equals(res))
                    {
                        LogUtil.e("网络出错");
                        Toast.makeText(MainActivity.this, "服务器返回为空", Toast.LENGTH_SHORT).show();
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
                                resData = resDataJson.getString("devToken");
                                FileUtil.getInstance().writeStrToFile(resData, AppConfig.TOKEN_FILE_PATH, false);
                                final String finalResData = resData;
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        token_text.setText(finalResData);
                                        regiest.setText("程序已注册");
                                        regiest.setEnabled(false);
                                    }
                                });
                            } else
                            {
                                LogUtil.e(resMsg);
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        Toast.makeText(MainActivity.this, resMsg, Toast.LENGTH_SHORT).show();

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
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            });
        } catch (JSONException e)
        {
            e.printStackTrace();
        }


    }


}
