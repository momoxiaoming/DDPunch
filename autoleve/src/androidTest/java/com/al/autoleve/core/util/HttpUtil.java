package com.al.autoleve.core.util;

import com.al.autoleve.entity.BaseEntity;
import com.andr.tool.log.LogUtil;
import com.andr.tool.net.OkHttpUtil;
import com.google.gson.Gson;

/**
 * Created by zhangxiaoming on 2018/9/19.
 */

public class HttpUtil {

    public static BaseEntity post(String reqUri, String parm) {

        String res = OkHttpUtil.getInstence().post(reqUri, parm);

        if (res != null) {

            BaseEntity entity = new Gson().fromJson(res, BaseEntity.class);
            return entity;
        }
        LogUtil.d("服务器返回错误");
        return null;

    }

}
