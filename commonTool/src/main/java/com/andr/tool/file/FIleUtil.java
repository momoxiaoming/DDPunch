package com.andr.tool.file;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.andr.tool.cmd.CmdUtils;
import com.andr.tool.log.LogUtil;
import com.andr.tool.util.StringUtil;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by zhangxiaoming on 2018/9/18.
 */

public class FileUtil implements FileInterface
{


    private static FileUtil mIntence;


    public static FileUtil getInstance()
    {
        if (null == mIntence)
        {
            synchronized (FileUtil.class)
            {
                if (null == mIntence)
                {
                    mIntence = new FileUtil();
                }
            }
        }
        return mIntence;
    }


    @Override
    public boolean isFileExist(String filePath)
    {
        File file = new File(filePath);
        return file != null && file.exists() && (file.isFile() || file.isDirectory()) ? true : false;
    }

    @Override
    public boolean copyAssetsToPath(Context context, String fileName, String newPath)
    {
        boolean ret = false;


        InputStream inputStream = null;
        FileOutputStream fileStream = null;
        try
        {
            if (!isFileExist(newPath))
            {
                //目录不存在就创建
                createFile(newPath);
            }

            inputStream = context.getAssets().open(fileName);
            fileStream = new FileOutputStream(new File(newPath));
            writeStream(inputStream, fileStream);

            return true;
        } catch (IOException e)
        {
            LogUtil.e("assets文件拷贝异常:" + e.getMessage());

            e.printStackTrace();
        } finally
        {
            close(fileStream);
            close(inputStream);
        }

        return ret;
    }

    @Override
    public boolean copyFile(String srcPath, String dstPath)
    {
        boolean rlt = false;
        FileInputStream is = null;
        FileOutputStream fos = null;
        try
        {
            if (!TextUtils.isEmpty(srcPath) && !TextUtils.isEmpty(dstPath))
            {
                File dstFl = new File(dstPath);
                if (dstFl.exists())
                {
                    dstFl.delete();
                }

                is = new FileInputStream(new File(srcPath));
                fos = new FileOutputStream(new File(dstPath));
                byte buffer[] = new byte[4096];
                int read = 0;
                while ((read = is.read(buffer, 0, buffer.length)) != -1)
                {
                    fos.write(buffer, 0, read);
                }
                fos.flush();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
        } finally
        {
            close(fos);
            close(is);

        }

        return rlt;
    }

    @Override
    public String getExStoragePath()
    {
        try
        {
            Class<?> environmentcls = Class.forName("android.os.Environment");
            Method setUserRequiredM = environmentcls.getMethod("setUserRequired", boolean.class);
            setUserRequiredM.invoke(null, false);
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public String readFile(String filePath)
    {
        StringBuffer sb = new StringBuffer();
        File file = new File(filePath);
        if (file == null || !file.isFile() || !file.exists())
        {
            LogUtil.e("文件不存在:" + filePath);
            return null;
        }

        BufferedReader reader = null;
        InputStreamReader is = null;
        try
        {
            is = new InputStreamReader(new FileInputStream(file));
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
            }

            return sb.toString();
        } catch (IOException e)
        {
            e.printStackTrace();

        } finally
        {
            close(is);
            close(reader);
        }
        return null;
    }


    @Override
    public boolean writeStrToFile(String obj, String filePath, boolean isAdd)
    {
        if (TextUtils.isEmpty(obj) || TextUtils.isEmpty(filePath))
        {
            return false;
        }
        FileWriter fileWriter = null;
        try
        {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, isAdd);
            fileWriter.write(obj);
            fileWriter.flush();
            return true;
        } catch (Exception e)
        {
            return false;
        } finally
        {
            close(fileWriter);
        }
    }

    @Override
    public String getStringFromAssets(Context context, String fileName)
    {
        byte[] ret = null;
        InputStream in = null;
        try
        {
            in = context.getAssets().open(fileName);
            int length = in.available();
            ret = new byte[length];
            in.read(ret);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            close(in);
        }

        return new String(ret);
    }


    @Override
    public void deleteDirContent(String path)
    {
        File dir = new File(path);
        if (null != dir && dir.isDirectory())
        {
            File[] files = dir.listFiles();
            if (null != files && files.length > 0)
            {
                for (int i = 0; i < files.length; i++)
                {
                    LogUtil.d(files[i].getName());
                    if (files[i].exists())
                    {
                        if (files[i].isDirectory())
                        {
                            deleteDir(files[i].getAbsolutePath());
                        } else
                        {
                            files[i].delete();
                            files[i].deleteOnExit();

                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean deleteDir(String path)
    {
        File dir = new File(path);
        if (null != dir && dir.isDirectory())
        {
            File[] files = dir.listFiles();
            if (null != files && files.length > 0)
            {
                for (int i = 0; i < files.length; i++)
                {
                    LogUtil.d(files[i].getName());
                    if (files[i].exists())
                    {
                        if (files[i].isDirectory())
                        {
                            deleteDir(files[i].getAbsolutePath());
                        } else
                        {
                            files[i].delete();
                            files[i].deleteOnExit();

                        }
                    }
                }
            } else
            {
                //删除空目录
                dir.delete();
            }
        }
        return true;

    }

    @Override
    public boolean deleteFile(String filepath)
    {
        boolean rlt = false;

        if (!TextUtils.isEmpty(filepath))
        {
            try
            {
                File file = new File(filepath);
                if (file.exists())
                {
                    file.delete();
                    file.deleteOnExit();

                    rlt = true;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return rlt;
    }


    @Override
    public boolean makeDirs(String path)
    {

        File folder = new File(path);
        if (!folder.exists())
        {
            return folder.mkdirs();
        }
        return true;
    }


    @Override
    public boolean moveFile(String scrPath, String dstPath)
    {
        File srcFile = new File(scrPath);
        File destFile = new File(dstPath);


        if (!srcFile.exists() || !destFile.exists())
        {
            LogUtil.e("文件不存在");
            return false;
        }
        boolean rename = srcFile.renameTo(destFile);
        if (!rename)
        {
            copyFile(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
            deleteFile(srcFile.getAbsolutePath());

            return true;
        }
        return false;

    }

    private long writeStream(InputStream input, OutputStream output)
    {
        byte[] bytes = new byte[1024];
        long size = 0;
        int rlen = -1;
        try
        {
            for (size = 0; true; size += rlen)
            {
                rlen = input.read(bytes);
                if (rlen == -1)
                {
                    return size;
                }
                output.write(bytes, 0, rlen);
            }
        } catch (IOException e)
        {
        }
        return size;
    }

    private void close(Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
                closeable = null;
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getFileExtension(String filePath)
    {
        if (StringUtil.isStringEmpty(filePath))
        {
            return filePath;
        }
        int extenPosi = filePath.lastIndexOf(".");
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1)
        {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

    @Override
    public boolean isHasFileType(String path, String type)
    {
        if (StringUtil.isStringEmpty(path) || StringUtil.isStringEmpty(type))
        {
            return false;
        }
        File file = new File(path);
        if (!file.exists())
        {
            return false;
        }
        if (file.isDirectory())
        {
            File[] listFile = file.listFiles();
            int size = listFile.length;
            for (int i = 0; i < size; i++)
            {
                if (isHasFileType(listFile[i].getAbsolutePath(), type))
                {
                    return true;
                }
            }
            return false;
        } else
        {
            return type.equals(getFileExtension(path));
        }
    }

    @Override
    public boolean unZipFiles(String zipFilePath, String descDir)
    {
        File zipFile = new File(zipFilePath);

        if (!zipFile.exists())
        {
            LogUtil.e("压缩文件不存在");
            return false;
        }

        InputStream in = null;
        FileOutputStream out = null;
        try
        {
            @SuppressWarnings("resource")
            ZipFile zip = new ZipFile(zipFile);
            for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); )
            {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();


                in = zip.getInputStream(entry);
                String outPath = (descDir + File.separator + zipEntryName).replaceAll("\\*", "/");

                // 判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                if (!file.exists())
                {
                    file.mkdirs();
                }
                // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory())
                {
                    continue;
                }
                out = new FileOutputStream(outPath);
                byte[] buffer = new byte[1024 * 10];
                int len;
                while ((len = in.read(buffer)) > 0)
                {
                    out.write(buffer, 0, len);
                }

            }
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
            LogUtil.e("解压异常:" + e.getMessage());
            return false;
        } finally
        {

            close(in);
            close(out);
        }
    }

    @Override
    public boolean createFile(String filePath) throws IOException
    {
        File file = new File(filePath);


        if (null != file)
        {
            if (!file.exists())
            {
                File parentFile = file.getParentFile();
                if (!parentFile.exists())
                {
                    if (!parentFile.mkdirs())
                    {
                        return false;
                    } else
                    {
                        if (!file.createNewFile())
                        {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public ArrayList<String> getAllFilesForPath(String filePath)
    {
        File root = new File(filePath);
        ArrayList<String> arr = new ArrayList<>();
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files)
            {
                if (f.isDirectory())
                { // 判断是否为文件夹
                    getAllFilesForPath(f.getPath());
                } else
                {
                    if (f.exists())
                    { // 判断是否存在
                        arr.add(f.getAbsolutePath());
                    }
                }
            }
        return arr;
    }

    @Override
    public boolean chomdFile(String cmd, String path)
    {
        String command = cmd + " " + path;
        try
        {
            int res = CmdUtils.execRootCmdSilent(command);

            if (res != -1)
            {
                return true;
            }
        } catch (Exception e)
        {
            LogUtil.e("权限修改失败");
        }
        return false;
    }
}
