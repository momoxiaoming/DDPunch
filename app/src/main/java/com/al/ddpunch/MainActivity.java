package com.al.ddpunch;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.al.ddpunch.email.EmaiUtil;
import com.al.ddpunch.util.SharpData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView text = findViewById(R.id.version_text);
        text.setText("版本号:" + getLocalVersionName(getApplicationContext()));

        findViewById(R.id.start_work).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), DDService.class));
                startService(new Intent(getApplicationContext(), MainAccessService.class));
                SharpData.setOpenApp(getApplicationContext(), 0);
                finish();


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


//        if (!isAccessibilitySettingsOn(getApplicationContext())) {
//            Toast.makeText(getApplicationContext(), "请开启辅助服务", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//            startActivity(intent);
//        }

        editText.setText(SharpData.getEmailData(getApplicationContext()) + "");

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
