package com.andr.tool.file;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 文件相关操作接口
 * Created by zhangxiaoming on 2018/9/14.
 */

public interface FileInterface {

    /**
     * 判断否个文件是否存在
     *
     * @param filePath 文件路径
     * @return
     */
    boolean isFileExist(String filePath);

    /**
     * 拷贝
     * @param context
     * @param fileName
     * @param newPath
     * @return
     */
    boolean copyAssetsToPath(Context context, String fileName, String newPath);

    /**
     *
     * @param srcPath
     * @param dstPath
     * @return
     */
    boolean copyFile(String srcPath,String dstPath);

    /**
     * 获取sd 卡目录
     * @return
     */
    String getExStoragePath();

    /**
     * 读取某个文件,并返回字符串
     * @param filePath
     * @return
     */
    String readFile(String filePath);

    /**
     * 写入字符串到到文件
     * @param obj
     * @param filePath
     * @param isAdd 是否追加
     * @return
     */
    boolean writeStrToFile(String obj ,String filePath,boolean isAdd);

    /**
     * 读取assets下的某个文件,并返回字符串
     * @param fileName
     * @return
     */
    String getStringFromAssets(Context context, String fileName);

    /**
     * 删除某个目录下的所有内容,但不包含本身
     * @param path
     * @return
     */
    void deleteDirContent(String path);

    /**
     * 删除某个目录,包含本身
     * @param path
     * @return
     */
    boolean deleteDir(String path);

    /**
     * 删除某个文件
     * @param filepath
     * @return
     */
    boolean deleteFile(String filepath);

    /**
     * 创建目录,多级目录也会创建
     * @param path
     * @return
     */
    boolean makeDirs(String path);

    /**
     * 移动文件
     * @param scrPath
     * @param dstPath
     * @return
     */
    boolean moveFile(String scrPath,String dstPath);

    /**
     * 获取文件后缀
     * @return
     */
    String getFileExtension(String filePath);

    /**
     * 判断是否存在某中文件类型
     * @return
     */
    boolean isHasFileType(String path,String type);

    /**
     * 解压文件到某个路径
     * @param zipFilePath
     * @param descDir
     * @return
     */
    boolean unZipFiles(String zipFilePath, String descDir);

    /**
     * 创建文件,如果目录页不存在,则连目录一起创建
     * @param filePath
     * @return
     */
    boolean createFile(String filePath) throws IOException;

    /**
     * 获取一个目录中的所有文件
     *
     * @param
     */
    ArrayList<String> getAllFilesForPath(String filePath);


    /**
     * 修改文件权限
     * @param cmd
     * @param path
     * @return
     */
    boolean chomdFile(String cmd,String path);
}
