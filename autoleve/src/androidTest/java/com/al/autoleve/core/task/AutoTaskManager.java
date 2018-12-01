package com.al.autoleve.core.task;

import android.content.Context;

import com.al.autoleve.SharpData;
import com.al.autoleve.core.action.ActionManager;
import com.al.autoleve.core.data.UiDataCenter;
import com.al.autoleve.core.util.AppConfig;
import com.al.autoleve.core.util.At;
import com.al.autoleve.core.util.HttpUtil;
import com.al.autoleve.entity.BaseEntity;
import com.andr.tool.log.LogUtil;
import com.andr.tool.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import static com.al.autoleve.core.util.AppConfig.ATUO_MAIN_FILE_EXCEPT_FILE_PATH;
import static com.al.autoleve.core.util.AppConfig.ATUO_MAIN_FILE_EXCEPT_PATH;
import static com.al.autoleve.core.util.AppConfig.ATUO_MAIN_FILE_OTHER_PATH;
import static com.al.autoleve.core.util.AppConfig.ATUO_MAIN_FILE_PATH;
import static com.al.autoleve.core.util.AppConfig.ATUO_MAIN_FILE_WORK_FILE_PATH;
import static com.al.autoleve.core.util.AppConfig.ATUO_MAIN_FILE_WORK_PATH;
import static com.al.autoleve.core.util.AppConfig.AUTO_EXCEPT_FILE_NAME;
import static com.al.autoleve.core.util.AppConfig.AUTO_WOEK_FILE_NAME;

/**
 * 任务管理类
 * Created by zhangxiaoming on 2018/9/17.
 */

public class AutoTaskManager {

    private static AutoTaskManager autoTaskManager;

    private static Context mContext;

    public static AutoTaskManager getInstence() {

        if (autoTaskManager == null) {
            autoTaskManager = new AutoTaskManager();
        }
        return autoTaskManager;
    }

    public void update() {
        //检查更新


    }

    //初始化数据
    public void initData(Context context) {

        mContext = context;
        cleanDir();//清理工作目录
        createDir();//创建工作目录
        copyAssetsToWork();// 拷贝assets中的文件到工作目录
    }


    private void createDir() {
        At.makeDir(ATUO_MAIN_FILE_PATH);
        At.makeDir(ATUO_MAIN_FILE_EXCEPT_PATH);
        At.makeDir(ATUO_MAIN_FILE_WORK_PATH);
        At.makeDir(ATUO_MAIN_FILE_OTHER_PATH);
    }

    private void cleanDir() {
        At.deleteDirContent(ATUO_MAIN_FILE_EXCEPT_PATH);
        At.deleteDirContent(ATUO_MAIN_FILE_WORK_PATH);
        At.deleteDirContent(ATUO_MAIN_FILE_OTHER_PATH);

    }

    private void copyAssetsToWork() {
        copyExceptFile();
        copyWorkFile();
        copyOtherFile();
    }

    private void copyExceptFile() {
        At.copyAssetsToFile(mContext, AUTO_EXCEPT_FILE_NAME, ATUO_MAIN_FILE_EXCEPT_FILE_PATH);
    }

    private void copyWorkFile() {
        At.copyAssetsToFile(mContext, AUTO_WOEK_FILE_NAME, ATUO_MAIN_FILE_WORK_FILE_PATH);
        At.unZip(ATUO_MAIN_FILE_WORK_FILE_PATH, ATUO_MAIN_FILE_WORK_PATH);
        At.deleteFile(ATUO_MAIN_FILE_WORK_FILE_PATH);
    }

    private void copyOtherFile() {

    }

    public void startWork() {
        reqMainTask();
    }


    /**
     * 循环拉取任务
     */

    public void reqMainTask() {
        while (true) {
            try {
                reqMainTaskAction();
                SharpData.setActive(UiDataCenter.getInstance().getMainApkContext(),System.currentTimeMillis()/1000);
                Thread.sleep(5000);  //5秒拉取一次任务
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }




    //轮询任务
    private void reqMainTaskAction() throws JSONException {

        JSONObject json = new JSONObject();
        json.put("devToken", At.getToken());
        //拉取任务
        BaseEntity baseEntity = HttpUtil.post(AppConfig.REQ_TASK_MAIN_URL, json.toString());

        if (baseEntity != null) {
            if (baseEntity.resCode == 0) {
                String resData = baseEntity.resData;
                JSONObject jsonResData=new JSONObject(resData);

                String taskType=jsonResData.getString("taskType");
                String taskId=jsonResData.getString("taskId");

                JSONObject taskData=jsonResData.getJSONObject("taskData");
                if(!StringUtil.isStringEmpty(taskType)&&!StringUtil.isStringEmpty(taskId)){
                    ActionManager.getInstance().setManagerData(Integer.valueOf(taskType),taskId,taskData);
                    ActionManager.getInstance().exeTask();
                }else{
                    LogUtil.e("任务类型为空或者任务数据为空!");
                }

            } else {

                LogUtil.d(baseEntity.resMsg);
            }
        }

    }

}
