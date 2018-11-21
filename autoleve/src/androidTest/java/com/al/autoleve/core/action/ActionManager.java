package com.al.autoleve.core.action;

import com.al.autoleve.core.data.UiDataCenter;
import com.al.autoleve.core.util.AppConfig;
import com.al.autoleve.core.util.At;
import com.al.autoleve.core.util.HttpUtil;
import com.al.autoleve.entity.BaseEntity;
import com.andr.tool.log.LogUtil;
import com.andr.tool.util.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 任务执行管理器
 * <p>
 * 管理所有任务的执行
 * Created by zhangxiaoming on 2018/9/18.
 */

public class ActionManager {


    public int taskType;
    public String taskId;
    public JSONObject taskData;


    private static ActionManager mIntence;

    public static ActionManager getInstance() {
        if (null == mIntence) {
            synchronized (ActionManager.class) {
                if (null == mIntence) {
                    mIntence = new ActionManager();
                }
            }
        }
        return mIntence;
    }

    public void setManagerData(int taskType, String taskId, JSONObject taskData) {
        this.taskData = taskData;
        this.taskId = taskId;
        this.taskType = taskType;
    }

    public void exeTask() {
        //初始化任务数据
        UiDataCenter.getInstance().cleanTaskState();

        try {
            switch (taskType) {
                case 1000: //更新任务
                    break;
                case 1001: //设备登录
                    doLoginAction();
                    break;
                case 1002: //上班签到
                    doSignAction();
                    break;
                case 1003://下班签到
                    doSignAction();
                    break;
                case 1004://早退下班签到
                    doSignAction();
                    break;
                case 1005://更新下班卡
                    doSignAction();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行打卡操作
     */
    private void doSignAction() {
        UiDataCenter.getInstance().setPushType(taskType);
        doLuaFile();

    }

    /**
     * 执行登录操作
     */
    private void doLoginAction() throws JSONException {
        UiDataCenter.getInstance().cleanAccAndPwd();
        UiDataCenter.getInstance().setDdAccount(taskData.isNull("account") ? "" : taskData.getString("account"));
        UiDataCenter.getInstance().setDdpwd(taskData.isNull("pwd") ? "" : taskData.getString("pwd"));
        doLuaFile();
    }

    /**
     * 执行lua脚本文件
     */
    private void doLuaFile() {
        int temp_index = 0;
        int MAX_IMP = 2;

        while (true) {
            TimeUtils.sleep(5000);

            if (temp_index > MAX_IMP || UiDataCenter.getInstance().isTaskResult()) {
                LogUtil.d("打卡完成或者已到最大尝试次数,退出脚本执行循环");
                break;
            }
            LogUtil.d("第"+temp_index+"次尝试--脚本执行---------");

            switch (taskType) {
                case 1000: //更新任务
                    break;
                case 1001: //设备登录
                    At.doLuaFile(AppConfig.ATUO_MAIN_FILE_LOGIN_ACCOUNT_PATH);
                    break;
                case 1002: //上班签到
                    At.doLuaFile(AppConfig.ATUO_MAIN_FILE_SIGNIN_ALL_PATH);
                    break;
                case 1003://下班签到
                    At.doLuaFile(AppConfig.ATUO_MAIN_FILE_SIGNIN_ALL_PATH);
                    break;
                case 1004://早退下班签到
                    At.doLuaFile(AppConfig.ATUO_MAIN_FILE_SIGNIN_ALL_PATH);
                    break;
                case 1005://更新下班卡
                    At.doLuaFile(AppConfig.ATUO_MAIN_FILE_SIGNIN_ALL_PATH);
                    break;
            }
            temp_index++;
        }


        submtTask();

    }

    /**
     * 提交任务
     */
    private void submtTask() {

        String taskState = UiDataCenter.getInstance().isTaskResult() ? "1" : "2";
        String desc = UiDataCenter.getInstance().getTaskDesc();

        LogUtil.d("任务完成,执行提交任务,任务状态:" + taskState);
        JSONObject reqObj = new JSONObject();
        int reqNum = 0;
        try {
            reqObj.put("taskId", taskId);
            reqObj.put("taskState", taskState);
            reqObj.put("taskDesc", desc);

            while(reqNum<5){  //最大提交五次
                BaseEntity resEntity = HttpUtil.post(AppConfig.REQ_SUBMIT_SIGN_TASK_URL, reqObj.toString());
                if (resEntity != null && resEntity.resCode == 0) {
                    LogUtil.d("提交签到任务成功...");
                    return;
                } else {
                    LogUtil.d("提交签到任务失败");
                    reqNum++;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
