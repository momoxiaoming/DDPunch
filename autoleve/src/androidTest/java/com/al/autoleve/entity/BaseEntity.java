package com.al.autoleve.entity;

/**
 * Created by zhangxiaoming on 2018/9/19.
 */

public class BaseEntity {
    public int resCode;  //0成功,非0失败
    public String resMsg; //返回描述
    public String resData;  //返回数据,只有resCode为0是才返回

}
