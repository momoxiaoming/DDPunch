package com.al.ddpunch;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.al.ddpunch.email.EmaiUtil;
import com.al.ddpunch.util.CMDUtil;
import com.al.ddpunch.util.LogUtil;
import com.al.ddpunch.util.SharpData;
import com.al.ddpunch.util.SystemUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    private CheckBox upBox, downBox;

    private TextView timeText, timeText2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        upBox = findViewById(R.id.upJob_btn);
        downBox = findViewById(R.id.downJob_btn);
        timeText = findViewById(R.id.time_text);
        timeText2 = findViewById(R.id.time_text2);






        TextView text = findViewById(R.id.version_text);
        text.setText("版本号:" + getLocalVersionName(getApplicationContext()));

        findViewById(R.id.start_work).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CMDUtil.execRootCmdSilent("echo test")!=-1){
                    Toast.makeText(MainActivity.this,"手机已root",Toast.LENGTH_SHORT).show();
                    startService(new Intent(getApplicationContext(), DDService.class));
                    startService(new Intent(getApplicationContext(), MainAccessService.class));
                    SharpData.setOpenApp(getApplicationContext(), 0);
                    finish();
                }else{
                    Toast.makeText(MainActivity.this,"请开启root权限再重试!",Toast.LENGTH_SHORT).show();

                }



            }
        });

        findViewById(R.id.stop_work).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharpData.setOpenApp(getApplicationContext(), 1);

            }
        });
        findViewById(R.id.email_work).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharpData.getEmailData(getApplicationContext()).equals("")) {
                    Toast.makeText(getApplicationContext(), "请设置邮箱", Toast.LENGTH_SHORT).show();

                    return;
                }
                EmaiUtil.sendMsg("邮件测试", SharpData.getEmailData(getApplicationContext()));

//                CMDUtil.doBoot();
            }
        });


        final EditText editText = findViewById(R.id.edt_email);

        Button email_btn = findViewById(R.id.email_setting);

        email_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String em = editText.getText().toString().trim();

                if (!"".equals(em)) {
                    if (isEmail(em)) {
                        boolean flsg = SharpData.setEmailData(getApplicationContext(), em);
                        Toast.makeText(getApplicationContext(), flsg ? "设置成功" : "设置失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "邮箱格式错误", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "邮箱不能为空", Toast.LENGTH_SHORT).show();
                }


            }
        });


        editText.setText(SharpData.getEmailData(getApplicationContext()) + "");

        setCheck();


        upBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int open = SharpData.getOpenJob(getApplicationContext());


                if (open == 0) {
                    SharpData.setOpenJob(getApplicationContext(), isChecked ? 1 : 0);
                } else if (open == 1) {
                    SharpData.setOpenJob(getApplicationContext(), isChecked ? 1 : 0);
                } else if (open == 2) {
                    SharpData.setOpenJob(getApplicationContext(), isChecked ? 3 : 2);
                } else if (open == 3) {
                    SharpData.setOpenJob(getApplicationContext(), isChecked ? 3 : 2);
                }

            }
        });

        downBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                int open = SharpData.getOpenJob(getApplicationContext());

                if (open == 0) {
                    SharpData.setOpenJob(getApplicationContext(), isChecked ? 2 : 0);
                } else if (open == 1) {
                    SharpData.setOpenJob(getApplicationContext(), isChecked ? 3 : 1);
                } else if (open == 2) {
                    SharpData.setOpenJob(getApplicationContext(), isChecked ? 2 : 0);
                } else if (open == 3) {
                    SharpData.setOpenJob(getApplicationContext(), isChecked ? 3 : 1);
                }


            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog(1);
            }
        });

        timeText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog(2);
            }
        });

        setinitData();



    }


    public void setDialog(final int flg) {


        Dialog dialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {


            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String min = "";
                String hour = "";
                if (minute < 10) {
                    min = "0" + minute;
                } else {
                    min = "" + minute;
                }
                if (hourOfDay < 10) {
                    hour = "0" + hourOfDay;
                } else {
                    hour = "" + hourOfDay;
                }


                String str = hour + ":" + min;
                if (flg == 1) {
                    SharpData.setDDupTime(getApplicationContext(), str);
                } else {
                    SharpData.setDDdownTime(getApplicationContext(), str);
                }

                setinitData();

            }
        }, 0, 0, true);
        dialog.show();


    }


    private void setinitData() {
        String up = SharpData.getDDupTime(getApplicationContext());
        String down = SharpData.getDDdownTime(getApplicationContext());

        timeText.setText("上班打卡时间:" + up);
        timeText2.setText("下班打卡时间:" + down);


        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels=metrics.widthPixels;
        int heightPixels=metrics.heightPixels;

        SharpData.setHeightmetrics(getApplicationContext(),heightPixels);
        SharpData.setWidthmetrics(getApplicationContext(),widthPixels);



        LogUtil.D("heightPixels:"+heightPixels+"----widthPixels:"+widthPixels);


        LogUtil.D("手机型号-->"+SystemUtil.getSystemModel());

    }

    private void setCheck() {
        int open = SharpData.getOpenJob(getApplicationContext());
        LogUtil.D("open---" + open);

        switch (open) {
            case 0:
                upBox.setChecked(false);
                downBox.setChecked(false);

                break;
            case 1:
                upBox.setChecked(true);
                downBox.setChecked(false);

                break;
            case 2:
                upBox.setChecked(false);
                downBox.setChecked(true);
                break;
            case 3:
                upBox.setChecked(true);
                downBox.setChecked(true);
                break;


        }
    }


    //判断email格式是否正确
    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))" +
                "([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;



         } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }




    private void openAccessSettingOn() {
        if (!isAccessibilitySettingsOn(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "请开启辅助服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        // TestService为对应的服务
        final String service = getPackageName() + "/" + MainAccessService.class.getCanonicalName();
        // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {

            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }
        return false;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

}
